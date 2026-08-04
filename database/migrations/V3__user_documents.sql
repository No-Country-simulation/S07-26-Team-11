-- V3__user_documents.sql
-- Documentos PDF institucionales generados por un usuario autenticado.
--
-- Se separa de pdf_documents a proposito: aquella tabla cuelga de
-- benchmark_responses (un informe por respuesta de benchmark, generado por el
-- worker asincrono). Esta guarda los documentos que el propio usuario genera
-- bajo demanda desde /api/v1/documents, y cuelga de users.
--
-- Los bytes viven en el object storage (bucket privado de OCI); aca solo van
-- los metadatos, para poder listar sin pedirle el inventario al bucket en cada
-- request.

-- El rol venia hardcodeado como 'USER' en UserAuthService: se persiste para
-- poder distinguir a un administrador (unico que ve los documentos de todos).
ALTER TABLE users ADD COLUMN role varchar(20) NOT NULL DEFAULT 'USER';

ALTER TABLE users ADD CONSTRAINT users_role_check
    CHECK (role IN ('USER', 'ADMIN'));

CREATE TABLE user_documents (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          uuid         NOT NULL REFERENCES users (id),
    -- Identificador del documento dentro del usuario, sin la extension .pdf.
    -- Regenerar con el mismo nombre reemplaza el documento, no lo duplica.
    name             varchar(64)  NOT NULL,
    title            varchar(300) NOT NULL,
    -- Clave del objeto dentro del bucket: documents/<user_id>/<name>.pdf
    storage_key      varchar(500) NOT NULL,
    size_bytes       bigint       NOT NULL,
    template_version varchar(20)  NOT NULL,
    created_at       timestamptz  NOT NULL DEFAULT now(),
    updated_at       timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT user_documents_uk UNIQUE (user_id, name)
);

CREATE UNIQUE INDEX user_documents_storage_key_uk ON user_documents (storage_key);
CREATE INDEX user_documents_user_idx ON user_documents (user_id, created_at DESC);
