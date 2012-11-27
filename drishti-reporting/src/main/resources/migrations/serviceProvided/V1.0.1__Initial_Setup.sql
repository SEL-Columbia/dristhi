CREATE TABLE report.dim_anm(ID SERIAL, anmIdentifier VARCHAR,
    CONSTRAINT pk_dim_anm PRIMARY KEY (ID),
    CONSTRAINT U_DA_AN UNIQUE (anmIdentifier));

CREATE TABLE report.dim_indicator(ID SERIAL, indicator VARCHAR,
    CONSTRAINT pk_dim_indicator PRIMARY KEY (ID),
    CONSTRAINT U_DI_IN UNIQUE (indicator));

CREATE TABLE report.dim_date(ID SERIAL, date_ DATE,
    CONSTRAINT pk_dim_date PRIMARY KEY (ID),
    CONSTRAINT U_DD_DT UNIQUE (date_));

CREATE TABLE report.dim_location(ID SERIAL, village VARCHAR, subCenter VARCHAR, phc VARCHAR,
    CONSTRAINT pk_dim_location PRIMARY KEY (ID),
    CONSTRAINT U_DL_VI_SU_PH UNIQUE (village, subCenter, phc));

CREATE TABLE report.annual_target(ID SERIAL, anmIdentifier INTEGER, indicator INTEGER, target VARCHAR,
    CONSTRAINT pk_annual_target PRIMARY KEY (ID),
    CONSTRAINT FK_AT_DA_ID FOREIGN KEY (anmIdentifier) REFERENCES report.dim_anm(ID),
    CONSTRAINT FK_AT_DI_ID FOREIGN KEY (indicator) REFERENCES report.dim_indicator(ID));

CREATE TABLE report.service_provided(ID SERIAL, anmIdentifier INTEGER ,externalId VARCHAR, indicator INTEGER, date_ INTEGER, location INTEGER,
    CONSTRAINT pk_service_provided PRIMARY KEY (ID),
    CONSTRAINT FK_SP_DA_ID FOREIGN KEY (anmIdentifier) REFERENCES report.dim_anm(ID),
    CONSTRAINT FK_SP_DI_ID FOREIGN KEY (indicator) REFERENCES report.dim_indicator(ID),
    CONSTRAINT FK_SP_DD_ID FOREIGN KEY (date_) REFERENCES report.dim_date(ID),
    CONSTRAINT FK_SP_DL_ID FOREIGN KEY (location) REFERENCES report.dim_location(ID));
