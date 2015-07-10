CREATE TABLE anm_report.dim_anm (ID INT(11) NOT NULL AUTO_INCREMENT , anmIdentifier VARCHAR(255) NOT NULL,
  CONSTRAINT pk_dim_anm PRIMARY KEY (ID),
  CONSTRAINT U_DA_AN UNIQUE (anmIdentifier));

CREATE TABLE anm_report.dim_indicator (ID INT(11) NOT NULL AUTO_INCREMENT , indicator VARCHAR(255) NOT NULL,
  CONSTRAINT pk_dim_indicator PRIMARY KEY (ID),
  CONSTRAINT U_DI_IN UNIQUE (indicator));

CREATE TABLE anm_report.dim_date (ID INT(11) NOT NULL AUTO_INCREMENT , date_ DATE NOT NULL,
  CONSTRAINT pk_dim_date PRIMARY KEY (ID),
  CONSTRAINT U_DD_DT UNIQUE (date_));

CREATE TABLE anm_report.annual_target (ID INT(11) NOT NULL AUTO_INCREMENT , anmIdentifier INT(11) NOT NULL, indicator INT(11) NOT NULL, target VARCHAR(255) NOT NULL, start_date DATE NOT NULL, end_date DATE NOT NULL,
  CONSTRAINT pk_annual_target PRIMARY KEY (ID),
  CONSTRAINT FK_AT_DA_ID FOREIGN KEY (anmIdentifier) REFERENCES anm_report.dim_anm (ID),
  CONSTRAINT FK_AT_DI_ID FOREIGN KEY (indicator) REFERENCES anm_report.dim_indicator (ID),
  CONSTRAINT U_AT_AI_IN_TA_SD_ED UNIQUE (anmIdentifier, indicator, start_date, end_date));

CREATE TABLE anm_report.anm_report_data (ID INT(11) NOT NULL AUTO_INCREMENT , anmIdentifier INT(11) NOT NULL, externalId VARCHAR(255) NOT NULL, indicator INT(11) NOT NULL, date_ INT(11) NOT NULL,
  CONSTRAINT pk_anm_report_data PRIMARY KEY (ID),
  CONSTRAINT FK_ARD_DA_ID FOREIGN KEY (anmIdentifier) REFERENCES anm_report.dim_anm (ID),
  CONSTRAINT FK_ARD_DI_ID FOREIGN KEY (indicator) REFERENCES anm_report.dim_indicator (ID),
  CONSTRAINT FK_ARD_DD_ID FOREIGN KEY (date_) REFERENCES anm_report.dim_date (ID));
