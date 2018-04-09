--
--    Copyright 2010-2016 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

-- // create error trace table
-- Migration SQL that makes the change goes here.

CREATE TABLE error.error_trace
(
  	id bigserial NOT NULL,
  	document_id character varying UNIQUE NOT NULL,
  	date_occurred timestamp ,
    error_type character varying,
    occurred_at character varying,
    stack_trace character varying,
    status character varying,
    record_id character varying,
    date_closed timestamp,
    document_type character varying,
    retry_url character varying,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
) TABLESPACE error_space;


CREATE INDEX error_trace_status_index ON error.error_trace (status);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE error.error_trace;