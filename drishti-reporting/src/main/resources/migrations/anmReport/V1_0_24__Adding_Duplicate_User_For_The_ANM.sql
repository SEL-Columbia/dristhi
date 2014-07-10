INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'musla3' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='musla3');

