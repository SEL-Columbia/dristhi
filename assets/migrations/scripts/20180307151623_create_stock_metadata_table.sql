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

-- // create stock metadata table
-- Migration SQL that makes the change goes here.

CREATE TABLE core.stock_metadata
(
    id bigserial NOT NULL,
    stock_id bigint REFERENCES core.stock (id),
    document_id character varying UNIQUE NOT NULL,
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

CREATE INDEX stock_metadata_document_id_index ON core.stock_metadata (document_id);
CREATE INDEX stock_metadata_server_version_index ON core.stock_metadata (server_version);
CREATE INDEX stock_metadata_provider_id_index ON core.stock_metadata (provider_id);
CREATE INDEX stock_metadata_location_id_index ON core.stock_metadata (location_id);
CREATE INDEX stock_metadata_team_index ON core.stock_metadata (team);
CREATE INDEX stock_metadata_team_id_index ON core.stock_metadata (team_id);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE core.stock_metadata;