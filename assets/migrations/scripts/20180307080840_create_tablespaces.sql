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

-- // create tablespaces
-- Migration SQL that makes the change goes here.

CREATE TABLESPACE core_space LOCATION ${core_tablespace_location};
CREATE TABLESPACE error_space LOCATION ${error_tablespace_location};
CREATE TABLESPACE schedule_space LOCATION ${schedule_tablespace_location};
CREATE TABLESPACE feed_space LOCATION ${feed_tablespace_location};
CREATE TABLESPACE form_space LOCATION ${form_tablespace_location};

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLESPACE core_space;;
DROP TABLESPACE error_space;
DROP TABLESPACE schedule_space;
DROP TABLESPACE feed_space;
DROP TABLESPACE form_space;
