INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'demo4' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='demo4');
INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'demo5' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='demo5');
INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'demo6' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='demo6');
INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'demo7' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='demo7');
INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'demo8' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='demo8');
INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'demo9' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='demo9');
INSERT INTO anm_report.dim_anm (anmIdentifier)  SELECT 'demo10' WHERE NOT EXISTS (SELECT dim_anm FROM anm_report.dim_anm WHERE anmIdentifier='demo10');