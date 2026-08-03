-- V2__auth_users.sql
-- Cuentas de autenticacion basica (email + password), independientes de admin_users
-- (dashboard interno) y de leads (captura de marketing). Es la base minima sobre la
-- que se construira el flujo real de magic link: emite JWT reales para que el resto
-- del sistema (endpoints protegidos, revocacion) tenga algo con que probarse mientras
-- se integra el envio de correo y la validacion de tokens de magic link.

CREATE TABLE users (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email         varchar(320) NOT NULL,
    password_hash varchar(200) NOT NULL,
    is_active     boolean      NOT NULL DEFAULT true,
    created_at    timestamptz  NOT NULL DEFAULT now(),
    updated_at    timestamptz  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX users_email_uk ON users (lower(email));

-- Lista de revocacion para JWT (stateless): logout guarda el hash del token hasta
-- que expire por su cuenta. El mismo mecanismo sirve para revocar tokens de magic
-- link el dia que se integren.
CREATE TABLE revoked_tokens (
    token_hash varchar(64) PRIMARY KEY,
    expires_at timestamptz NOT NULL,
    revoked_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX revoked_tokens_expires_idx ON revoked_tokens (expires_at);
