--clear data before performing the migration
TRUNCATE TABLE core.action CASCADE;

ALTER SEQUENCE core.action_id_seq RESTART WITH 1;

ALTER SEQUENCE core.action_metadata_id_seq RESTART WITH 1;

/* Insert into action */
insert into core.action(json)
select doc as json from couchdb
where doc->>'type'='Action';

/* Insert into action metadata */
INSERT INTO core.action_metadata(action_id, document_id, base_entity_id,server_version, provider_id,location_id,team,team_id)
select (select id from core.action where json->>'_id'=doc->>'_id') action_id,doc->>'_id' document_id,
doc->>'baseEntityId' as base_entity_id,
(doc->>'timeStamp')::BIGINT as server_version,doc->>'providerId' as provider_id,doc->>'locationId' as location_id,
doc->>'team' as team, doc->>'teamId' as team_id
from couchdb
where doc->>'type'='Action';