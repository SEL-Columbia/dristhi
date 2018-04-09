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
select (select id from core.report where json->>'_id'=doc->>'_id') report_id,doc->>'_id' document_id,
doc->>'formSubmissionId' as form_submission_id,doc->>'baseEntityId' as base_entity_id,
(doc->>'serverVersion')::BIGINT as server_version,
doc->>'reportType' as report_type,(doc->>'reportDate')::date as report_date,
doc->>'providerId' as provider_id,doc->>'locationId' as location_id,
doc->>'team' as team, doc->>'teamId' as team_id,(doc->>'dateEdited')::date as date_edited
from couchdb
where doc->>'type'='Report';