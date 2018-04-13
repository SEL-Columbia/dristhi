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

-- // create event metadata table
-- Migration SQL that makes the change goes here.

CREATE TABLE core.event_metadata
(
    id bigserial NOT NULL,
    event_id bigint REFERENCES core.event (id),
    document_id character varying UNIQUE NOT NULL,
    base_entity_id character varying NOT NULL,
    form_submission_id  character varying UNIQUE NOT NULL,
    server_version bigint,
    openmrs_uuid character varying,
    event_type character varying,
	event_date timestamp,
	entity_type character varying,
    provider_id character varying,
    location_id character varying,
	team character varying,
	team_id character varying,
	date_created timestamp DEFAULT CURRENT_TIMESTAMP,
    date_edited timestamp,
    date_deleted timestamp,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
) TABLESPACE core_space;


CREATE INDEX event_metadata_sync_index ON core.event_metadata (provider_id,location_id,team,team_id,server_version);
CREATE INDEX event_metadata_base_entity_id_index ON core.event_metadata (base_entity_id);
CREATE INDEX event_metadata_openmrs_uuid_index ON core.event_metadata (openmrs_uuid);
CREATE INDEX event_metadata_date_deleted_index ON core.event_metadata (date_deleted);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE core.event_metadata;
