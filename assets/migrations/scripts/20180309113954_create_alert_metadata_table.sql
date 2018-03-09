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

-- // create alert metadata table
-- Migration SQL that makes the change goes here.

CREATE TABLE core.alert_metadata
(
    id bigserial NOT NULL,
    alert_id bigint REFERENCES core.alert (id),
    base_entity_id character varying NOT NULL,
	server_version timestamp without time zone,
    provider_id character varying,
    location_id character varying,
	team character varying,
	team_id character varying,
	is_active boolean,
	trigger_name character varying,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)TABLESPACE core_space;

CREATE INDEX alert_metadata_base_entity_id_index ON core.alert_metadata (base_entity_id);
CREATE INDEX alert_metadata_server_version_index ON core.alert_metadata (server_version);
CREATE INDEX alert_metadata_provider_id_index ON core.alert_metadata (provider_id);
CREATE INDEX alert_metadata_location_id_index ON core.alert_metadata (location_id);
CREATE INDEX alert_metadata_team_index ON core.alert_metadata (team);
CREATE INDEX alert_metadata_team_id_index ON core.alert_metadata (team_id);
CREATE INDEX alert_metadata_is_active_index ON core.alert_metadata (is_active);
CREATE INDEX alert_metadata_trigger_name_index ON core.alert_metadata (trigger_name);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE core.alert_metadata;
