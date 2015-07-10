INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'chc_mangalore'), 'mangalore_a', 'hirebindal');
INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'chc_mangalore'), 'mangalore_a', 'kadralli');

INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'phc_mudhol'), 'mudhol', 'anganwadi_1');
INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'phc_mudhol'), 'mudhol', 'anganwadi_2');
INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'phc_mudhol'), 'mudhol', 'anganwadi_4');
INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'phc_mudhol'), 'mudhol', 'anganwadi_5');
INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'phc_mudhol'), 'mudhol', 'anganwadi_6');
INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'phc_mudhol'), 'mudhol', 'anganwadi_7');
INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'phc_mudhol'), 'mudhol', 'anganwadi_8');
INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'phc_mudhol'), 'mudhol', 'anganwadi_9');
INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'phc_mudhol'), 'mudhol', 'anganwadi_10');

INSERT INTO report.dim_location (state, district, taluka, phc, subCenter, village) (SELECT 'Karnataka', 'Koppal', 'Kustagi' , (SELECT ID FROM report.dim_phc WHERE phcIdentifier = 'chc_tawargera'), 'tawargera_a',	'anganwadi_9');


