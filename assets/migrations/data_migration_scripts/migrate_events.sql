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
select (select id from core.event where json->>'_id'=doc->>'_id') as event_id,doc->>'_id' document_id,
doc->>'baseEntityId' as base_entity_id,doc->>'formSubmissionId' as form_submission_id,
(doc->>'serverVersion')::BIGINT as server_version,doc->'identifiers'->>'OPENMRS_UUID' as openmrs_uuid,
doc->>'eventType' as event_type,(doc->>'eventDate')::TIMESTAMP as event_date,
doc->>'entityType' as entity_type,doc->>'providerId' as provider_id,doc->>'locationId' as location_id,
doc->>'team' as team, doc->>'teamId' as team_id,(doc->>'dateCreated')::TIMESTAMP as date_created,
(doc->>'dateEdited')::TIMESTAMP as date_edited,(doc->>'dateVoided')::TIMESTAMP as date_deleted
from couchdb
where doc->>'type'='Event';

/*set event_metadata foreign key to delete cascade*/
ALTER TABLE core.event_metadata DROP CONSTRAINT IF EXISTS event_metadata_event_id_fkey  ;

ALTER TABLE  core.event_metadata Add CONSTRAINT event_metadata_event_id_fkey FOREIGN KEY (event_id)
REFERENCES core.event(id) ON DELETE CASCADE ;

/*delete cascade duplicate events and event_medatadata*/
delete from core.event where id in (
select e.event_id from core.event_metadata e
  JOIN (
    SELECT
      form_submission_id,
      max(server_version) server_version
    FROM core.event_metadata
    GROUP BY form_submission_id
    HAVING count(*) > 1
    ) d on e.form_submission_id=d.form_submission_id and e.server_version<d.server_version
);


/*Return the foreign key without cascade delete*/
ALTER TABLE core.event_metadata DROP CONSTRAINT event_metadata_event_id_fkey;

ALTER TABLE  core.event_metadata Add CONSTRAINT event_metadata_event_id_fkey FOREIGN KEY (event_id)
REFERENCES core.event(id);



/*Enable formsubmission unique CONSTRAINT*/
ALTER TABLE core.event_metadata Add  UNIQUE (form_submission_id);