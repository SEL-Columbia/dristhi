--clear data before performing the migration
TRUNCATE TABLE core.event CASCADE;

ALTER SEQUENCE core.event_id_seq RESTART WITH 1;

ALTER SEQUENCE core.event_metadata_id_seq RESTART WITH 1;

/*Query to show constriants
 SELECT CONSTRAINT_name FROM information_schema.CONSTRAINT_TABLE_usage WHERE TABLE_name = 'event_metadata';
*/

/*disable formsubmission unique CONSTRAINT*/
ALTER TABLE core.event_metadata DROP CONSTRAINT IF EXISTS  event_metadata_form_submission_id_key ;

/* Insert into event */
insert into core.event(json,date_deleted)
select doc as json, (doc->>'dateVoided')::TIMESTAMP as date_deleted from couchdb
where doc->>'type'='Event';

/* Insert into event metadata */
INSERT INTO core.event_metadata
( event_id, document_id, base_entity_id, form_submission_id, server_version,
  openmrs_uuid, event_type, event_date, entity_type, provider_id, location_id,
  team, team_id, date_created, date_edited, date_deleted)
select  id  as event_id,json->>'_id' document_id,
json->>'baseEntityId' as base_entity_id,json->>'formSubmissionId' as form_submission_id,
(json->>'serverVersion')::BIGINT as server_version,json->'identifiers'->>'OPENMRS_UUID' as openmrs_uuid,
json->>'eventType' as event_type,(json->>'eventDate')::TIMESTAMP as event_date,
json->>'entityType' as entity_type,json->>'providerId' as provider_id,json->>'locationId' as location_id,
json->>'team' as team, json->>'teamId' as team_id,(json->>'dateCreated')::TIMESTAMP as date_created,
(json->>'dateEdited')::TIMESTAMP as date_edited,(json->>'dateVoided')::TIMESTAMP as date_deleted
from core.event;

/* create and index to help identify duplicates faster*/
CREATE INDEX event_metadata_duplicate_index on core.event_metadata(form_submission_id,server_version);

/*delete duplicates in events and event_medatadata. 
 * Disable cascade(fk) triggers for better performance(faster delete) */
DO $$
  DECLARE duplicate_events_cursor CURSOR FOR
select e.event_id,e.id from core.event_metadata e
  JOIN (
    SELECT
      form_submission_id,
      max(server_version) server_version
    FROM core.event_metadata
    GROUP BY form_submission_id
    HAVING count(*) > 1
    ) d on e.form_submission_id=d.form_submission_id and e.server_version<d.server_version;
 rec RECORD;
BEGIN
  ALTER TABLE core.event_metadata DISABLE TRIGGER ALL;
  ALTER TABLE core.event DISABLE TRIGGER ALL;
  OPEN duplicate_events_cursor;
  LOOP
    FETCH duplicate_events_cursor into rec;
    EXIT WHEN NOT FOUND;
      delete from core.event_metadata where id =rec.id;
      delete from core.event where id =rec.event_id;
  END LOOP;
  CLOSE duplicate_events_cursor;
  ALTER TABLE core.event_metadata ENABLE TRIGGER ALL;
  ALTER TABLE core.event ENABLE TRIGGER ALL;
END;
$$

/*drop the duplicates index*/
DROP INDEX core.event_metadata_duplicate_index;

/*Enable formsubmission unique CONSTRAINT*/
ALTER TABLE core.event_metadata ADD  UNIQUE (form_submission_id);