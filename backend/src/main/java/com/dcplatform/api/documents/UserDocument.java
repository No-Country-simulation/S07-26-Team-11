package com.dcplatform.api.documents;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Metadatos de un PDF institucional generado por un usuario.
 *
 * <p>Los bytes viven en el bucket privado; aca solo va lo necesario para listar sin pedirle el
 * inventario al object storage en cada request. Refleja la tabla {@code user_documents} de
 * {@code database/migrations/V3__user_documents.sql}.
 */
@Entity
@Table(name = "user_documents")
public class UserDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** FK a users.id. El dueno del documento; nadie mas lo ve, salvo un ADMIN. */
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    /** Identificador dentro del usuario, sin la extension. Unico por usuario. */
    @Column(name = "name", nullable = false, updatable = false, length = 64)
    private String name;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "storage_key", nullable = false, updatable = false, length = 500)
    private String storageKey;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "template_version", nullable = false, length = 20)
    private String templateVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected UserDocument() {
        // requerido por JPA
    }

    public UserDocument(UUID userId, String name, String title, String storageKey,
                        long sizeBytes, String templateVersion) {
        this.userId = userId;
        this.name = name;
        this.title = title;
        this.storageKey = storageKey;
        this.sizeBytes = sizeBytes;
        this.templateVersion = templateVersion;
    }

    @PrePersist
    void onInsert() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Regenerar un documento con el mismo nombre lo reemplaza: mismo id, misma clave.
     *
     * <p>{@code updatedAt} se fija aqui y no solo en {@code @PreUpdate}: ese callback corre al
     * hacer flush, o sea despues de que el controller ya construyo la respuesta, y el cliente
     * recibiria la marca de tiempo anterior.
     */
    public void replaceWith(String title, long sizeBytes, String templateVersion) {
        this.title = title;
        this.sizeBytes = sizeBytes;
        this.templateVersion = templateVersion;
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public UUID getId() { return id; }

    public UUID getUserId() { return userId; }

    public String getName() { return name; }

    public String getTitle() { return title; }

    public String getStorageKey() { return storageKey; }

    public long getSizeBytes() { return sizeBytes; }

    public String getTemplateVersion() { return templateVersion; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
