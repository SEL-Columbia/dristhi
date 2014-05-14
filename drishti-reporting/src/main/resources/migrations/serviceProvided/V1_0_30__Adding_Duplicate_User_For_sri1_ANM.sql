INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  SELECT 'srir1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'Asha Begum', 'sriramnagar_a' WHERE NOT EXISTS (SELECT dim_anm FROM report.dim_anm WHERE anmIdentifier='srir1');

INSERT INTO report.dim_service_provider (service_provider, type) SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='srir1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM') WHERE NOT EXISTS (SELECT service_provider FROM report.dim_service_provider WHERE service_provider=(SELECT ID FROM report.dim_anm WHERE anmIdentifier='srir1'));

