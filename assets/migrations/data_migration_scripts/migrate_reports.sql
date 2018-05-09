--clear data before performing the migration
TRUNCATE TABLE core.report CASCADE;

ALTER SEQUENCE core.report_id_seq RESTART WITH 1;

ALTER SEQUENCE core.report_metadata_id_seq RESTART WITH 1;

/* Insert into report */
insert into core.report(json)
select doc as json from couchdb
where doc->>'type'='Report';

/* Insert into report metadata */

/* Insert into report metadata */
INSERT INTO core.report_metadata
(report_id, document_id,form_submission_id, base_entity_id,server_version,report_type,report_date, provider_id,
 location_id,team,team_id,date_edited)
select id as report_id,json->>'_id' document_id,
json->>'formSubmissionId' as form_submission_id,json->>'baseEntityId' as base_entity_id,
(json->>'serverVersion')::BIGINT as server_version,
json->>'reportType' as report_type,(json->>'reportDate')::date as report_date,
json->>'providerId' as provider_id,json->>'locationId' as location_id,
json->>'team' as team, json->>'teamId' as team_id,(json->>'dateEdited')::date as date_edited
from core.report;