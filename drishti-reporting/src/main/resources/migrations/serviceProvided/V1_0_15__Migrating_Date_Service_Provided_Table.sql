ALTER TABLE report.service_provided DROP COLUMN IF EXISTS new_date_;
ALTER TABLE report.service_provided ADD COLUMN new_date_ DATE;

DROP FUNCTION IF EXISTS  merge_date_in_service_provided();

CREATE FUNCTION merge_date_in_service_provided()
RETURNS BOOLEAN AS $THIS$
DECLARE
  v_date DATE;
  service_provided_row report.service_provided%ROWTYPE;
BEGIN
  FOR service_provided_row IN SELECT * FROM report.service_provided LOOP
    SELECT date_ INTO v_date FROM report.dim_date WHERE id = service_provided_row.date_;
    EXECUTE 'UPDATE report.service_provided SET new_date_ = ''' || v_date || ''' where id = ' || service_provided_row.id;
    END LOOP;
  RETURN TRUE;
EXCEPTION
  WHEN OTHERS THEN
    RAISE NOTICE 'Exception: %', SQLERRM;
    RETURN FALSE;
END;
$THIS$LANGUAGE plpgsql;

SELECT * FROM merge_date_in_service_provided();

ALTER TABLE report.service_provided DROP CONSTRAINT FK_SP_DD_ID;
ALTER TABLE report.service_provided RENAME COLUMN date_ TO old_date_;
ALTER TABLE report.service_provided ALTER COLUMN old_date_ DROP NOT NULL; ;
ALTER TABLE report.service_provided RENAME COLUMN new_date_ TO date_;
