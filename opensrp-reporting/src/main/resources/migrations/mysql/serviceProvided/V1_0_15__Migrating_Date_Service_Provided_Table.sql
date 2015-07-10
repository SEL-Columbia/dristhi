--  Only thing required is to drop FK on date_ to dim_date and make is nullable
ALTER TABLE report.service_provided DROP FOREIGN KEY FK_SP_DD_ID;
ALTER TABLE report.service_provided MODIFY COLUMN date_ DATE;
