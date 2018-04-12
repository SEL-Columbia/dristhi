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
select id as alert_id,json->>'_id' document_id,
json->>'entityId' as base_entity_id,
(json->>'timeStamp')::BIGINT as server_version,json->>'providerId' as provider_id,json->>'locationId' as location_id,
json->>'team' as team, json->>'teamId' as team_id,
(json->>'isActive')::bool as is_active, json->>'triggerName' as trigger_name
from core.alert;