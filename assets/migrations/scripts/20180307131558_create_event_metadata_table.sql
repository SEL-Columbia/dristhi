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
    base_entity_id character varying NOT NULL,
	form_submission_id  character varying NOT NULL,
    server_version timestamp without time zone,
    openmrs_uuid character varying,
    event_type character varying,
	event_date date,
    provider_id character varying,
    location_id character varying,
	team character varying,
	team_id character varying,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);

CREATE UNIQUE INDEX event_metadata_relational_id_unique_index ON core.event_metadata (form_submission_id);

CREATE INDEX event_metadata_base_entity_id_index ON core.event_metadata (base_entity_id);
CREATE INDEX event_metadata_server_version_index ON core.event_metadata (server_version);
CREATE INDEX event_metadata_openmrs_uuid_index ON core.event_metadata (openmrs_uuid);
CREATE INDEX event_metadata_event_type_index ON core.event_metadata (event_type);
CREATE INDEX event_metadata_event_date_index ON core.event_metadata (event_date);
CREATE INDEX event_metadata_provider_id_index ON core.event_metadata (provider_id);
CREATE INDEX event_metadata_location_id_index ON core.event_metadata (location_id);
CREATE INDEX event_metadata_team_index ON core.event_metadata (team);
CREATE INDEX event_metadata_team_id_index ON core.event_metadata (team_id);


-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE core.event_metadata;
