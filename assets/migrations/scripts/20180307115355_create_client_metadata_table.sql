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
    base_entity_id character varying NOT NULL,
    relational_id character varying,
    openmrs_uuid character varying,
    unique_id character varying,
    first_name character varying,
    last_name character varying,
    birth_date date,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);

CREATE UNIQUE INDEX client_metadata_base_entity_id_unique_index ON core.client_metadata (base_entity_id);

CREATE INDEX client_metadata_relational_id_index ON core.client_metadata (relational_id);
CREATE INDEX client_metadata_openmrs_uuid_index ON core.client_metadata (openmrs_uuid);
CREATE INDEX client_metadata_unique_id_index ON core.client_metadata (unique_id);
CREATE INDEX client_metadata_first_name_index ON core.client_metadata (first_name NULLS LAST);
CREATE INDEX client_metadata_last_name_index ON core.client_metadata (last_name NULLS LAST);
CREATE INDEX client_metadata_birth_date_index ON core.client_metadata (birth_date);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE core.client_metadata;
