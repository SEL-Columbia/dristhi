INSERT INTO report.dim_location(village, subcenter, phc, taluka, district, state)
    VALUES ('munjanahalli', 'munjanahalli', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'holenarasipura', 'mysore', 'karnataka');
INSERT INTO report.dim_location(village, subcenter, phc, taluka, district, state)
  VALUES ('chikkabherya', 'munjanahalli', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'holenarasipura', 'mysore', 'karnataka');
INSERT INTO report.dim_location(village, subcenter, phc, taluka, district, state)
  VALUES ('kavalu_hosur', 'munjanahalli', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'holenarasipura', 'mysore', 'karnataka');