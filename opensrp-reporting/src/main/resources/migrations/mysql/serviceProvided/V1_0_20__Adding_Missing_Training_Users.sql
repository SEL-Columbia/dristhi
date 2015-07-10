-- suresh
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter) VALUES ('suresh', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Suresh', 'munjanahalli');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='suresh'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- nirmala
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter) VALUES ('nirmala', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Nirmala', 'munjanahalli');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='nirmala'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
