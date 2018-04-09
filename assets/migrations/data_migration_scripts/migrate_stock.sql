--clear data before performing the migration
TRUNCATE TABLE core.stock CASCADE;

ALTER SEQUENCE core.stock_id_seq RESTART WITH 1;

ALTER SEQUENCE core.stock_metadata_id_seq RESTART WITH 1;

/* Insert into stock */
insert into core.stock(json)
select doc as json from couchdb
where doc->>'type'='Stock';

/* Insert into stock metadata */
INSERT INTO core.stock_metadata(stock_id, document_id, server_version, provider_id,location_id,team,team_id)
select (select id from core.stock where json->>'_id'=doc->>'_id') stock_id,doc->>'_id' document_id,
(doc->>'serverVersion')::BIGINT as server_version,doc->>'providerid' as provider_id,doc->>'locationId' as location_id,
doc->>'team' as team, doc->>'teamId' as team_id
from couchdb
where doc->>'type'='Stock';