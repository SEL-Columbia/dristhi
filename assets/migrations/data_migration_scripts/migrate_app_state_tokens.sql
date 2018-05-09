--clear data before performing the migration
TRUNCATE TABLE core.app_state_token;

ALTER SEQUENCE core.app_state_token_id_seq RESTART WITH 1;

/* Insert into app_state_token */
insert into core.app_state_token(name,description,value,last_edited_date)
select doc->>'name' as name, doc->>'description' as description,
doc->>'value' as value, (doc->>'lastEditDate')::BIGINT as last_edited_date
from couchdb
where doc->>'type'='AppStateToken';
