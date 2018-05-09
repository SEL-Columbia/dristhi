--clear data before performing the migration
TRUNCATE TABLE error.error_trace;

ALTER SEQUENCE error.error_trace_id_seq RESTART WITH 1;

/* Insert into error_trace */
INSERT INTO error.error_trace
(document_id, date_occurred, error_type, occurred_at, stack_trace, status, record_id,
 date_closed, document_type, retry_url)
select doc->>'_id' document_id, (doc->>'dateOccurred')::timestamp as date_occurred,
  doc->>'errorType' as error_type,  doc->>'occurredAt' as occurred_at,doc->>'stackTrace' as stack_trace,
  doc->>'status' as status, doc->>'recordId' as record_id, (doc->>'dateClosed')::timestamp as date_closed,
  doc->>'documentType' as document_type, doc->>'retryUrl' as retry_url
from couchdb
where doc->>'type'='ErrorTrace';
