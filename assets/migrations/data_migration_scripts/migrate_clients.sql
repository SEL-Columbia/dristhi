--clear data before performing the migration
TRUNCATE TABLE core.client CASCADE;

ALTER SEQUENCE core.client_id_seq RESTART WITH 1;

ALTER SEQUENCE core.client_metadata_id_seq RESTART WITH 1;

/* Insert into client */
insert into core.client(json,date_deleted)
select doc as json, (doc->>'dateVoided')::TIMESTAMP as date_deleted from couchdb
where doc->>'type'='Client';

/* Insert into client metadata */
INSERT INTO core.client_metadata (client_id, document_id, base_entity_id, relational_id, server_version,
openmrs_uuid, unique_id, first_name, middle_name, last_name, birth_date, date_deleted)
select id as client_id,json->>'_id' document_id,json->>'baseEntityId' as base_entity_id,
json->'relationships'->'mother'->>0 as relational_id,(json->>'serverVersion')::BIGINT as server_version,
json->'identifiers'->>'OPENMRS_UUID' as openmrs_uuid,
coalesce(json->'identifiers'->>'ZEIR_ID',json->'identifiers'->>'M_ZEIR_ID') unique_id,
json->>'firstName' as first_name, json->>'MiddleName' as middle_name, json->>'lastName' as last_name,
(json->>'birthdate')::date as birth_date, (json->>'dateVoided')::TIMESTAMP as date_deleted
from core.client;