-- Agrega la columna de versionado para el Optimistic Locking de JPA (@Version)
-- Se define como bigint para que coincida con el tipo Long de Java.
-- Se asigna DEFAULT 0 para que los registros ya existentes no queden en null y causen errores.

ALTER TABLE benchmark_responses
    ADD COLUMN version bigint NOT NULL DEFAULT 0;