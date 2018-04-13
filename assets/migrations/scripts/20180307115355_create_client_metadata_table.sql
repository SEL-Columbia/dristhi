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

-- // create client metadata table
-- Migration SQL that makes the change goes here.

CREATE TABLE core.client_metadata
(
    id bigserial NOT NULL,
    client_id bigint REFERENCES core.client (id),
    document_id character varying UNIQUE NOT NULL,
    base_entity_id character varying UNIQUE NOT NULL,
    relational_id character varying,
    server_version bigint,
    openmrs_uuid character varying,
    unique_id character varying,
    first_name character varying,
    middle_name character varying,
    last_name character varying,
    birth_date date,
    date_deleted timestamp,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
) TABLESPACE core_space;


CREATE INDEX client_metadata_server_version_index ON core.client_metadata (server_version);
CREATE INDEX client_metadata_identifiers_index ON core.client_metadata (openmrs_uuid,unique_id);
CREATE INDEX client_metadata_date_deleted_index ON core.client_metadata (date_deleted);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE core.client_metadata;
