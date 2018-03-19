TRUNCATE TABLE core.app_state_token;

TRUNCATE TABLE core.event CASCADE;

ALTER SEQUENCE core.app_state_token_id_seq RESTART WITH 6;

ALTER SEQUENCE core.event_id_seq RESTART WITH 16;

ALTER SEQUENCE core.event_metadata_id_seq RESTART WITH 16;