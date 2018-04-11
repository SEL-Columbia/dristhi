--clear data before performing the migration
TRUNCATE TABLE core.alert CASCADE;

ALTER SEQUENCE core.alert_id_seq RESTART WITH 1;

ALTER SEQUENCE core.alert_metadata_id_seq RESTART WITH 1;

/* Insert into alert */
insert into core.alert(json)
select doc as json from couchdb
where doc->>'type'='Alert';

/* Insert into alert metadata */
INSERT INTO core.alert_metadata
(alert_id, document_id, base_entity_id,server_version, provider_id,
 location_id,team,team_id,is_active,trigger_name)
select (select id from core.alert where json->>'_id'=doc->>'_id') alert_id,doc->>'_id' document_id,
doc->>'entityId' as base_entity_id,
(doc->>'timeStamp')::BIGINT as server_version,doc->>'providerId' as provider_id,doc->>'locationId' as location_id,
doc->>'team' as team, doc->>'teamId' as team_id,
(doc->>'isActive')::bool as is_active, doc->>'triggerName' as trigger_name
from couchdb
where doc->>'type'='Alert';