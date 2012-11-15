CREATE TABLE anm_report.dim_anm(ID SERIAL, anmIdentifier VARCHAR,
    CONSTRAINT pk_dim_anm PRIMARY KEY (ID),
    CONSTRAINT U_DA_AN UNIQUE (anmIdentifier));

CREATE TABLE anm_report.dim_indicator(ID SERIAL, indicator VARCHAR,
    CONSTRAINT pk_dim_indicator PRIMARY KEY (ID),
    CONSTRAINT U_DI_IN UNIQUE (indicator));

CREATE TABLE anm_report.dim_date(ID SERIAL, date_ DATE,
    CONSTRAINT pk_dim_date PRIMARY KEY (ID),
    CONSTRAINT U_DD_DT UNIQUE (date_));

CREATE TABLE anm_report.anm_report_data(ID SERIAL, anmIdentifier INTEGER ,externalId VARCHAR, indicator INTEGER, date_ INTEGER,
    CONSTRAINT pk_anm_report_data PRIMARY KEY (ID),
    CONSTRAINT FK_SP_DA_ID FOREIGN KEY (anmIdentifier) REFERENCES anm_report.dim_anm(ID),
    CONSTRAINT FK_SP_DI_ID FOREIGN KEY (indicator) REFERENCES anm_report.dim_indicator(ID),
    CONSTRAINT FK_SP_DD_ID FOREIGN KEY (date_) REFERENCES anm_report.dim_date(ID));
