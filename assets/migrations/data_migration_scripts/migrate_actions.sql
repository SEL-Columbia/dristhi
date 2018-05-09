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
select id  action_id,json->>'_id' jsonument_id,
json->>'baseEntityId' as base_entity_id,
(json->>'timeStamp')::BIGINT as server_version,json->>'providerId' as provider_id,json->>'locationId' as location_id,
json->>'team' as team, json->>'teamId' as team_id
from  core.action;


/*Incase of very large dataset or in low memory conditions use cursor below to  insert into action_metadata
 * 
 * The replicate the cursor to insert into other other documents types metadata
 * 

DO $$
DECLARE
  DECLARE actions_cursor CURSOR FOR SELECT * FROM core.action;
  t_action   RECORD;
BEGIN
  OPEN actions_cursor;
  LOOP
      FETCH actions_cursor INTO t_action;
      EXIT WHEN NOT FOUND;

    INSERT INTO core.action_metadata(action_id, document_id, base_entity_id,server_version, provider_id,location_id,team,team_id)
      VALUES (t_action.id ,t_action.json->>'_id',t_action.json->>'baseEntityId',(t_action.json->>'timeStamp')::BIGINT ,
              t_action.json->>'providerId',t_action.json->>'locationId',t_action.json->>'team',t_action.json->>'teamId');
  END LOOP;
END$$;
*/ 
