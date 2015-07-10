INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'srir1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'Asha Begum', 'sriramnagar_a'); 
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='srir1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'budg1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_budgumpa'), 'K.N.Bhairamatti', 'budgumpa');
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='budg1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'karat1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'Bhagyalakshmi', 'karatagi_b');
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='karat1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM')); 