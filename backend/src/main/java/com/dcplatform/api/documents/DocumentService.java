package com.dcplatform.api.documents;

import com.dcplatform.api.auth.User;
import com.dcplatform.api.auth.UserRepository;
import com.dcplatform.api.pdf.storage.PdfStorage;
import com.dcplatform.api.shared.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Orquesta la cadena completa del documento institucional:
 * peticion -> modelo -> HTML (Thymeleaf) -> PDF (Open HTML to PDF) -> bucket privado,
 * mas los metadatos en {@code user_documents} para poder listarlos.
 *
 * <p>Todas las operaciones estan acotadas al usuario autenticado. El demo del que se adapto este
 * modulo recibia el {@code owner} por parametro y cualquiera podia leer o pisar los documentos de
 * cualquiera; aca el dueno sale del token y nunca del cuerpo de la peticion.
 */
@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final UserRepository userRepository;
    private final UserDocumentRepository documentRepository;
    private final InstitutionalPdfRenderer renderer;
    private final DocumentDateFormatter dateFormatter;
    private final DocumentAssets assets;
    private final PdfStorage storage;
    private final DocumentProperties properties;

    public DocumentService(UserRepository userRepository,
                           UserDocumentRepository documentRepository,
                           InstitutionalPdfRenderer renderer,
                           DocumentDateFormatter dateFormatter,
                           DocumentAssets assets,
                           PdfStorage storage,
                           DocumentProperties properties) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.renderer = renderer;
        this.dateFormatter = dateFormatter;
        this.assets = assets;
        this.storage = storage;
        this.properties = properties;
    }

    /**
     * Genera el PDF, lo sube al bucket y registra sus metadatos. Regenerar con el mismo
     * {@code metadata.name} reemplaza el documento anterior: misma clave de objeto, misma fila.
     */
    @Transactional
    public DocumentSummary create(String email, DocumentRequest request) {
        User user = requireUser(email);
        String name = request.metadata().name().trim();

        // Se comprueba antes de renderizar: el render cuesta segundos y sin bucket no hay
        // donde dejar el resultado. Sin esto el usuario esperaria para recibir igual un 503.
        if (!storage.isAvailable()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "storage-unavailable",
                    "El almacenamiento de documentos no esta configurado");
        }

        byte[] pdf = renderer.renderPdf(model(user, request));
        String storageKey = storageKey(user.getId(), name);
        storage.upload(storageKey, pdf);

        UserDocument document = documentRepository.findByUserIdAndName(user.getId(), name)
                .map(existing -> {
                    existing.replaceWith(request.title().trim(), pdf.length, properties.templateVersion());
                    return existing;
                })
                .orElseGet(() -> new UserDocument(user.getId(), name, request.title().trim(),
                        storageKey, pdf.length, properties.templateVersion()));

        UserDocument saved = documentRepository.save(document);
        log.info("Documento institucional generado: user={} name={} bytes={}", user.getId(), name, pdf.length);
        return DocumentSummary.of(saved, user.getEmail());
    }

    /**
     * Devuelve solo el HTML intermedio, sin generar el PDF ni tocar el bucket. Sirve para iterar
     * el diseno del template en el navegador, que da recarga inmediata; recordar que el navegador
     * es mas permisivo que el motor de PDF (por ejemplo, alli si funcionaria flexbox).
     */
    @Transactional(readOnly = true)
    public String previewHtml(String email, DocumentRequest request) {
        return renderer.renderHtml(model(requireUser(email), request));
    }

    /** Documentos del usuario autenticado, del mas reciente al mas antiguo. */
    @Transactional(readOnly = true)
    public OwnerDocuments listMine(String email) {
        User user = requireUser(email);
        List<DocumentSummary> documents = documentRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(doc -> DocumentSummary.of(doc, user.getEmail()))
                .toList();
        return OwnerDocuments.of(user.getEmail(), documents);
    }

    @Transactional(readOnly = true)
    public DocumentSummary getMine(String email, String name) {
        User user = requireUser(email);
        return DocumentSummary.of(requireDocument(user, name), user.getEmail());
    }

    /**
     * URL temporal y firmada del object storage a la que redirigir con 302. Vive pocos minutos
     * (app.storage.oci.signed-url-ttl): el enlace permanente es este endpoint, no el del storage.
     */
    @Transactional(readOnly = true)
    public URI downloadUrl(String email, String name) {
        User user = requireUser(email);
        UserDocument document = requireDocument(user, name);
        return storage.createSignedDownloadUrl(document.getStorageKey(), document.getName() + ".pdf");
    }

    /** Todos los documentos agrupados por usuario. Solo para ADMIN. */
    @Transactional(readOnly = true)
    public List<OwnerDocuments> listAll() {
        Map<UUID, String> emailsById = new HashMap<>();
        userRepository.findAll().forEach(user -> emailsById.put(user.getId(), user.getEmail()));

        Map<String, List<DocumentSummary>> byOwner = new HashMap<>();
        for (UserDocument document : documentRepository.findAllByOrderByCreatedAtDesc()) {
            String owner = emailsById.getOrDefault(document.getUserId(), "(usuario eliminado)");
            byOwner.computeIfAbsent(owner, key -> new ArrayList<>())
                    .add(DocumentSummary.of(document, owner));
        }

        return byOwner.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> OwnerDocuments.of(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Variables que consume el template. Es el contrato con quien disena el PDF: cada clave de
     * aca tiene que existir como {@code th:text}/{@code th:each} en
     * {@code pdf/institutional-document.html}, y viceversa.
     */
    private Map<String, Object> model(User user, DocumentRequest request) {
        DocumentProperties.Organization org = properties.organization();
        String dateText = dateFormatter.format(request.date());

        Map<String, Object> model = new HashMap<>();
        model.put("title", request.title().trim());
        model.put("owner", user.getEmail());
        model.put("assets", assets.images());
        model.put("org", org);

        model.put("executiveSummary", request.executiveSummary().trim());
        model.put("annualCost", request.annualCost().trim());
        model.put("maturityLevel", request.maturityLevel().trim());
        model.put("score", request.score().trim());
        model.put("kwUnderutilized", request.kwUnderutilized().trim());
        model.put("utilizationPercent", request.utilizationPercent().trim());
        model.put("costPerRack", request.costPerRack().trim());

        model.put("industryScores", request.industryScores().stream()
                .map(item -> Map.of(
                        "label", item.label().trim(),
                        "value", item.value(),
                        // Sin CSS para una clase que no sea alguna de estas dos: ver
                        // .bar-gold / .bar-dark-green en el template.
                        "barClass", item.own() ? "bar-gold" : "bar-dark-green"))
                .toList());
        model.put("recommendations", request.recommendations().stream()
                .map(String::trim)
                .toList());

        model.put("meetingLink", org.meetingLink());
        model.put("contactEmail", org.contactEmail());
        // "Capacia — Informe de capacidad | 29 de julio de 2026 · Confidencial"
        model.put("footerText", org.name() + " — Informe de capacidad | " + dateText + " · Confidencial");

        return model;
    }

    /**
     * Clave del objeto dentro del bucket. Se agrupa por id de usuario y no por email: el email es
     * un dato personal que no tiene por que quedar en los nombres de objeto, cambia con el tiempo
     * y admite caracteres que complican las rutas.
     */
    private String storageKey(UUID userId, String name) {
        return properties.storagePrefix() + userId + "/" + name + ".pdf";
    }

    private User requireUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .filter(User::isActive)
                .orElseThrow(() -> ApiException.unauthorized("La cuenta del token ya no existe o esta inactiva"));
    }

    private UserDocument requireDocument(User user, String name) {
        return documentRepository.findByUserIdAndName(user.getId(), name.trim())
                .orElseThrow(() -> ApiException.notFound("El documento solicitado no existe"));
    }
}
