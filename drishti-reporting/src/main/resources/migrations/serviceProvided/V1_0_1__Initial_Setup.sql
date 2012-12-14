CREATE TABLE report.dim_phc (ID SERIAL, phcIdentifier VARCHAR NOT NULL, name VARCHAR NOT NULL,
  CONSTRAINT pk_dim_phc PRIMARY KEY (ID),
  CONSTRAINT U_DP_PI UNIQUE (phcIdentifier));

CREATE TABLE report.dim_anm (ID SERIAL, anmIdentifier VARCHAR NOT NULL, phc INTEGER NOT NULL,
  CONSTRAINT pk_dim_anm PRIMARY KEY (ID),
  CONSTRAINT FK_DA_DP_ID FOREIGN KEY (phc) REFERENCES report.dim_phc (ID),
  CONSTRAINT U_DA_AN UNIQUE (anmIdentifier));

CREATE TABLE report.dim_indicator (ID SERIAL, indicator VARCHAR NOT NULL,
  CONSTRAINT pk_dim_indicator PRIMARY KEY (ID),
  CONSTRAINT U_DI_IN UNIQUE (indicator));

CREATE TABLE report.dim_date (ID SERIAL, date_ DATE NOT NULL,
  CONSTRAINT pk_dim_date PRIMARY KEY (ID),
  CONSTRAINT U_DD_DT UNIQUE (date_));

CREATE TABLE report.dim_location (ID SERIAL, village VARCHAR NOT NULL, subCenter VARCHAR NOT NULL, phc INTEGER NOT NULL, taluka VARCHAR NOT NULL, district VARCHAR NOT NULL, state VARCHAR NOT NULL,
  CONSTRAINT pk_dim_location PRIMARY KEY (ID),
  CONSTRAINT FK_DL_DP_ID FOREIGN KEY (phc) REFERENCES report.dim_phc (ID),
  CONSTRAINT U_DL_VI_SU_PH UNIQUE (village, subCenter, phc),
  CONSTRAINT U_DL_VI_SU_PH_TK_DT_ST UNIQUE (village, subCenter, phc, taluka, district, state));

CREATE TABLE report.dim_service_provider_type (ID SERIAL, type VARCHAR NOT NULL,
  CONSTRAINT pk_dim_service_provider_type PRIMARY KEY (ID),
  CONSTRAINT U_DSPT_TY UNIQUE (type),
  CONSTRAINT CK_SPT CHECK (type = 'ANM' OR type = 'PHC'));

CREATE TABLE report.dim_service_provider (ID SERIAL, service_provider INTEGER NOT NULL, type INTEGER NOT NULL,
  CONSTRAINT pk_dim_service_provider PRIMARY KEY (ID),
  CONSTRAINT FK_DSP_DST_ID FOREIGN KEY (type) REFERENCES report.dim_service_provider_type (ID),
  CONSTRAINT U_SP_TY UNIQUE (service_provider, type));

CREATE TABLE report.annual_target (ID SERIAL, service_provider INTEGER NOT NULL, indicator INTEGER NOT NULL, target VARCHAR NOT NULL, start_date DATE NOT NULL, end_date DATE NOT NULL,
  CONSTRAINT pk_annual_target PRIMARY KEY (ID),
  CONSTRAINT FK_AT_DSP_ID FOREIGN KEY (service_provider) REFERENCES report.dim_service_provider (ID),
  CONSTRAINT FK_AT_DI_ID FOREIGN KEY (indicator) REFERENCES report.dim_indicator (ID),
  CONSTRAINT U_AT_SP_IN_TA_SD_ED UNIQUE (service_provider, indicator, start_date, end_date));

CREATE TABLE report.service_provided (ID SERIAL, service_provider INTEGER NOT NULL, externalId VARCHAR NOT NULL, indicator INTEGER NOT NULL, date_ INTEGER NOT NULL, location INTEGER NOT NULL,
  CONSTRAINT pk_service_provided PRIMARY KEY (ID),
  CONSTRAINT FK_SP_DSP_ID FOREIGN KEY (service_provider) REFERENCES report.dim_service_provider (ID),
  CONSTRAINT FK_SP_DI_ID FOREIGN KEY (indicator) REFERENCES report.dim_indicator (ID),
  CONSTRAINT FK_SP_DD_ID FOREIGN KEY (date_) REFERENCES report.dim_date (ID),
  CONSTRAINT FK_SP_DL_ID FOREIGN KEY (location) REFERENCES report.dim_location (ID));
