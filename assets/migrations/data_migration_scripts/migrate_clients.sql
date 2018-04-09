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
select (select id from core.client where json->>'_id'=doc->>'_id') as client_id,doc->>'_id' document_id,
doc->>'baseEntityId' as base_entity_id,
doc->'relationships'->'mother'->>0 as relational_id,(doc->>'serverVersion')::BIGINT as server_version,
doc->'identifiers'->>'OPENMRS_UUID' as openmrs_uuid,doc->'identifiers'->>'ZEIR_ID' as unique_id,
doc->>'firstName' as first_name, doc->>'MiddleName' as middle_name, doc->>'lastName' as last_name,
(doc->>'birthdate')::date as birth_date, (doc->>'dateVoided')::TIMESTAMP as date_deleted
from couchdb
where doc->>'type'='Client';