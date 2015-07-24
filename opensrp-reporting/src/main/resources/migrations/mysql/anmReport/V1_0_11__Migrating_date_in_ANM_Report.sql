--  Only thing required is to drop FK on date_ to dim_date and make is nullable
ALTER TABLE anm_report.anm_report_data DROP FOREIGN KEY FK_ARD_DD_ID;
ALTER TABLE anm_report.anm_report_data MODIFY COLUMN date_ DATE;
