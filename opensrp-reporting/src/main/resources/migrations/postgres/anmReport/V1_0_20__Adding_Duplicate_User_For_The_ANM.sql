INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'srir1' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='srir1');

INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'budg1' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='budg1');

INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'karat1' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='karat1');
