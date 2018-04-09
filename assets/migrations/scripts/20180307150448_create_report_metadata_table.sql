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

-- // create report metadata table
-- Migration SQL that makes the change goes here.

CREATE TABLE core.report_metadata
(
    id bigserial NOT NULL,
    report_id bigint REFERENCES core.report (id),
    document_id character varying  UNIQUE NOT NULL,
    form_submission_id  character varying UNIQUE NOT NULL,
    base_entity_id character varying,
	server_version bigint,
	report_type character varying,
	report_date date,
    provider_id character varying,
    location_id character varying,
	team character varying,
	team_id character varying,
	date_edited date,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
) TABLESPACE core_space;

CREATE INDEX report_metadata_form_submission_id_index ON core.report_metadata (form_submission_id);
CREATE INDEX report_metadata_base_entity_id_index ON core.report_metadata (base_entity_id);
CREATE INDEX report_metadata_server_version_index ON core.report_metadata (server_version);
CREATE INDEX report_metadata_report_type_index ON core.report_metadata (report_type);
CREATE INDEX report_metadata_provider_id_index ON core.report_metadata (provider_id);
CREATE INDEX report_metadata_location_id_index ON core.report_metadata (location_id);
CREATE INDEX report_metadata_team_index ON core.report_metadata (team);
CREATE INDEX report_metadata_team_id_index ON core.report_metadata (team_id);
CREATE INDEX report_metadata_report_date_index ON core.report_metadata (report_date);
CREATE INDEX report_metadata_date_edited_index ON core.report_metadata (date_edited);


-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE core.report_metadata;