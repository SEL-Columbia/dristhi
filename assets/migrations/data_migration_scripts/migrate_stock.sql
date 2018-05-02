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
select id as stock_id,json->>'_id' document_id,
(json->>'serverVersion')::BIGINT as server_version,json->>'providerid' as provider_id,json->>'locationId' as location_id,
json->>'team' as team, json->>'teamId' as team_id
from core.stock;