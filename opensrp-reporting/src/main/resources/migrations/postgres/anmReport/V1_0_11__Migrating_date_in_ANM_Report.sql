ALTER TABLE anm_report.anm_report_data DROP COLUMN IF EXISTS new_date_;
ALTER TABLE anm_report.anm_report_data ADD COLUMN new_date_ DATE;

DROP FUNCTION IF EXISTS  merge_date_in_anm_report_data();

CREATE FUNCTION merge_date_in_anm_report_data()
RETURNS BOOLEAN AS $THIS$
DECLARE
  v_date DATE;
  anm_report_data_row anm_report.anm_report_data%ROWTYPE;
BEGIN
  FOR anm_report_data_row IN SELECT * FROM anm_report.anm_report_data LOOP
    SELECT date_ INTO v_date FROM anm_report.dim_date WHERE id = anm_report_data_row.date_;
    EXECUTE 'UPDATE anm_report.anm_report_data SET new_date_ = ''' || v_date || ''' where id = ' || anm_report_data_row.id;
    END LOOP;
  RETURN TRUE;
EXCEPTION
  WHEN OTHERS THEN
    RAISE NOTICE 'Exception: %', SQLERRM;
    RETURN FALSE;
END;
$THIS$LANGUAGE plpgsql;

SELECT * FROM merge_date_in_anm_report_data();

ALTER TABLE anm_report.anm_report_data DROP CONSTRAINT FK_ARD_DD_ID;
ALTER TABLE anm_report.anm_report_data RENAME COLUMN date_ TO old_date_;
ALTER TABLE anm_report.anm_report_data ALTER COLUMN old_date_ DROP NOT NULL;
ALTER TABLE anm_report.anm_report_data RENAME COLUMN new_date_ TO date_;
