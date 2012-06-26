CREATE TABLE report.dim_anm(ID SERIAL PRIMARY KEY, anmIdentifier VARCHAR UNIQUE);
CREATE TABLE report.dim_indicator(ID SERIAL PRIMARY KEY, indicator VARCHAR UNIQUE);
CREATE TABLE report.dim_date(ID SERIAL PRIMARY KEY, date_ DATE UNIQUE);
CREATE TABLE report.dim_location(ID SERIAL PRIMARY KEY, village VARCHAR, subCenter VARCHAR, phc VARCHAR, UNIQUE (village, subCenter, phc));
CREATE TABLE report.services_provided(ID SERIAL PRIMARY KEY, anmIdentifier INTEGER REFERENCES report.dim_anm(ID), externalId INTEGER, indicator INTEGER REFERENCES report.dim_indicator(ID), date_ INTEGER REFERENCES report.dim_date(ID), location INTEGER REFERENCES report.dim_location(ID));