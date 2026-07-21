-- V1__initial_schema.sql
-- Esquema inicial de referencia. ARQUETIPO: el Data Owner lo revisa y ajusta
-- antes de aplicarlo. Una vez mergeado, este archivo es INMUTABLE.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ---------------------------------------------------------------- leads

CREATE TABLE leads (
    id                      uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email                   varchar(320) NOT NULL,
    company_name            varchar(200),
    role                    varchar(120),
    source                  varchar(30)  NOT NULL,
    consent_at              timestamptz,
    consent_ip              inet,
    privacy_policy_version  varchar(20),
    created_at              timestamptz  NOT NULL DEFAULT now(),
    updated_at              timestamptz  NOT NULL DEFAULT now(),
    deleted_at              timestamptz,
    CONSTRAINT leads_source_check
        CHECK (source IN ('CALCULATOR', 'BENCHMARK', 'REPORT', 'OUTREACH'))
);

CREATE UNIQUE INDEX leads_email_uk ON leads (lower(email)) WHERE deleted_at IS NULL;
CREATE INDEX leads_created_at_idx ON leads (created_at DESC);

-- Magic links. Se guarda solo el HASH del token, nunca el token.
CREATE TABLE lead_access_tokens (
    id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id     uuid NOT NULL REFERENCES leads (id),
    token_hash  varchar(128) NOT NULL,
    expires_at  timestamptz  NOT NULL,
    used_at     timestamptz,
    created_at  timestamptz  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX lead_access_tokens_hash_uk ON lead_access_tokens (token_hash);
CREATE INDEX lead_access_tokens_lead_idx ON lead_access_tokens (lead_id);

-- ----------------------------------------------------------- calculadora

CREATE TABLE calculator_estimates (
    id                    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id               uuid REFERENCES leads (id),
    calculation_version   varchar(20) NOT NULL,
    inputs_json           jsonb       NOT NULL,
    outputs_json          jsonb       NOT NULL,
    idle_capacity_ratio   numeric(7,4),
    idle_capacity_cost    numeric(19,4),
    currency              char(3),
    created_at            timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX calculator_estimates_lead_idx ON calculator_estimates (lead_id);
CREATE INDEX calculator_estimates_created_idx ON calculator_estimates (created_at DESC);

-- ------------------------------------------------------------- benchmark

CREATE TABLE benchmark_instruments (
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    version      varchar(20) NOT NULL UNIQUE,
    is_active    boolean     NOT NULL DEFAULT false,
    published_at timestamptz,
    created_at   timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE benchmark_dimensions (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    instrument_id uuid NOT NULL REFERENCES benchmark_instruments (id),
    code          varchar(60)  NOT NULL,
    label         varchar(200) NOT NULL,
    weight        numeric(5,4) NOT NULL,
    display_order integer      NOT NULL,
    CONSTRAINT benchmark_dimensions_uk UNIQUE (instrument_id, code)
);

CREATE TABLE benchmark_questions (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    dimension_id  uuid NOT NULL REFERENCES benchmark_dimensions (id),
    text          text    NOT NULL,
    help_text     text,
    display_order integer NOT NULL
);

CREATE INDEX benchmark_questions_dimension_idx ON benchmark_questions (dimension_id);

-- El puntaje de cada opcion NUNCA se expone al cliente.
CREATE TABLE benchmark_options (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id   uuid NOT NULL REFERENCES benchmark_questions (id),
    label         varchar(300) NOT NULL,
    score         numeric(5,2) NOT NULL,
    display_order integer      NOT NULL
);

CREATE INDEX benchmark_options_question_idx ON benchmark_options (question_id);

CREATE TABLE benchmark_responses (
    id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id        uuid NOT NULL REFERENCES leads (id),
    instrument_id  uuid NOT NULL REFERENCES benchmark_instruments (id),
    status         varchar(20)  NOT NULL DEFAULT 'IN_PROGRESS',
    global_score   numeric(6,2),
    maturity_level integer,
    percentile     numeric(5,4),
    cohort_size    integer,
    started_at     timestamptz  NOT NULL DEFAULT now(),
    completed_at   timestamptz,
    CONSTRAINT benchmark_responses_status_check
        CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED'))
);

CREATE INDEX benchmark_responses_lead_idx ON benchmark_responses (lead_id);
CREATE INDEX benchmark_responses_completed_idx ON benchmark_responses (completed_at DESC);

CREATE TABLE benchmark_answers (
    id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    response_id uuid NOT NULL REFERENCES benchmark_responses (id),
    question_id uuid NOT NULL REFERENCES benchmark_questions (id),
    option_id   uuid NOT NULL REFERENCES benchmark_options (id),
    answered_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT benchmark_answers_uk UNIQUE (response_id, question_id)
);

CREATE INDEX benchmark_answers_response_idx ON benchmark_answers (response_id);

-- --------------------------------------------------------- cola y PDF

-- Cola de trabajos en PostgreSQL. Se consume con FOR UPDATE SKIP LOCKED.
-- Sin Redis: menos infraestructura y comportamiento correcto con varios workers.
CREATE TABLE pdf_jobs (
    id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    response_id    uuid NOT NULL REFERENCES benchmark_responses (id),
    status         varchar(20) NOT NULL DEFAULT 'PENDING',
    attempts       integer     NOT NULL DEFAULT 0,
    failure_reason text,
    created_at     timestamptz NOT NULL DEFAULT now(),
    started_at     timestamptz,
    finished_at    timestamptz,
    CONSTRAINT pdf_jobs_status_check
        CHECK (status IN ('PENDING', 'PROCESSING', 'DONE', 'FAILED')),
    CONSTRAINT pdf_jobs_response_uk UNIQUE (response_id)
);

CREATE INDEX pdf_jobs_pending_idx ON pdf_jobs (created_at) WHERE status = 'PENDING';

CREATE TABLE pdf_documents (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    response_id      uuid NOT NULL REFERENCES benchmark_responses (id),
    storage_key      varchar(500) NOT NULL,
    template_version varchar(20)  NOT NULL,
    size_bytes       bigint,
    page_count       integer,
    generated_at     timestamptz  NOT NULL DEFAULT now(),
    download_count   integer      NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX pdf_documents_storage_key_uk ON pdf_documents (storage_key);
CREATE INDEX pdf_documents_response_idx ON pdf_documents (response_id);

-- ------------------------------------------------------------- outreach

CREATE TABLE outreach_campaigns (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name          varchar(200) NOT NULL,
    subject       varchar(300),
    template_code varchar(60),
    status        varchar(20)  NOT NULL DEFAULT 'DRAFT',
    created_at    timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT outreach_campaigns_status_check
        CHECK (status IN ('DRAFT', 'SENDING', 'SENT', 'PAUSED'))
);

CREATE TABLE outreach_contacts (
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id  uuid NOT NULL REFERENCES outreach_campaigns (id),
    email        varchar(320) NOT NULL,
    name         varchar(200),
    company      varchar(200),
    job_title    varchar(150),
    status       varchar(20)  NOT NULL DEFAULT 'QUEUED',
    invite_hash  varchar(128),
    sent_at      timestamptz,
    delivered_at timestamptz,
    opened_at    timestamptz,
    clicked_at   timestamptz,
    completed_at timestamptz,
    created_at   timestamptz  NOT NULL DEFAULT now(),
    deleted_at   timestamptz,
    CONSTRAINT outreach_contacts_uk UNIQUE (campaign_id, email),
    CONSTRAINT outreach_contacts_status_check
        CHECK (status IN ('QUEUED', 'SENT', 'DELIVERED', 'OPENED', 'CLICKED',
                          'COMPLETED', 'BOUNCED', 'SUPPRESSED'))
);

CREATE INDEX outreach_contacts_campaign_idx ON outreach_contacts (campaign_id);
CREATE INDEX outreach_contacts_status_idx ON outreach_contacts (campaign_id, status);

-- Lista de supresion global. Se consulta ANTES de cada envio. Requisito legal.
CREATE TABLE email_suppressions (
    email      varchar(320) PRIMARY KEY,
    reason     varchar(30) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT email_suppressions_reason_check
        CHECK (reason IN ('UNSUBSCRIBED', 'HARD_BOUNCE', 'COMPLAINT', 'MANUAL'))
);

-- -------------------------------------------------- usuarios internos

CREATE TABLE admin_users (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email         varchar(320) NOT NULL UNIQUE,
    password_hash varchar(200) NOT NULL,
    role          varchar(20)  NOT NULL DEFAULT 'VIEWER',
    is_active     boolean      NOT NULL DEFAULT true,
    created_at    timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT admin_users_role_check CHECK (role IN ('ADMIN', 'VIEWER'))
);
