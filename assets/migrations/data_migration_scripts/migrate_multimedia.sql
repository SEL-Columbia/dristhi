--clear data before performing the migration
TRUNCATE TABLE core.multi_media;

ALTER SEQUENCE core.multi_media_id_seq RESTART WITH 1;

/* Insert into multi_media */
INSERT INTO core.multi_media
(document_id, case_id, provider_id, content_type, file_path, file_category)
select doc->>'_id' document_id, doc->>'caseId' as base_entity_id,
doc->>'providerId' as provider_id,doc->>'contentType' as content_type,
doc->>'filePath' as file_path, doc->>'fileCategory' as file_category
from couchdb
where doc->>'type'='Multimedia';
