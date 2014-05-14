INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'srir1' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='srir1');
