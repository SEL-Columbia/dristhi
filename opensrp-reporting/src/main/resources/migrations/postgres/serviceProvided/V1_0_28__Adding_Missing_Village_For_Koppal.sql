INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) SELECT 'Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'chikbommanhal', 'hiresulikeri_thanda' WHERE NOT EXISTS (SELECT village FROM report.dim_location where village = 'hiresulikeri_thanda');

