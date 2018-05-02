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

-- // create action metadata table
-- Migration SQL that makes the change goes here.

CREATE TABLE core.action_metadata
(
    id bigserial NOT NULL,
    action_id bigint REFERENCES core.action (id),
    document_id character varying UNIQUE NOT NULL,
    base_entity_id character varying NOT NULL,
	server_version bigint,
    provider_id character varying,
    location_id character varying,
	team character varying,
	team_id character varying,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
) TABLESPACE core_space;

CREATE INDEX action_metadata_base_entity_id_index ON core.action_metadata (base_entity_id);
CREATE INDEX action_metadata_sync_index ON core.action_metadata (provider_id,server_version);


-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE core.action_metadata;
