CREATE TABLE report.anm(ID SERIAL PRIMARY KEY, anmIdentifier VARCHAR);
CREATE TABLE report.indicator(ID SERIAL PRIMARY KEY, indicator VARCHAR);
CREATE TABLE report.date_(ID SERIAL PRIMARY KEY, date_ DATE);
CREATE TABLE report.location(ID SERIAL PRIMARY KEY, village VARCHAR, subCenter VARCHAR, phc VARCHAR);
CREATE TABLE report.services_provided(ID SERIAL PRIMARY KEY, anmIdentifier INTEGER REFERENCES report.anm(ID), externalId INTEGER, indicator INTEGER REFERENCES report.indicator(ID), date_ INTEGER REFERENCES report.date_(ID), location INTEGER REFERENCES report.location(ID));