INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('bhe1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'));
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('bhe2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'));
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('bhe3', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'));
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('bhe4', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'));
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('bhe5', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'));

INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('klp1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('klp2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('klp3', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('klp4', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES ((SELECT ID FROM report.dim_anm WHERE anmIdentifier='bhe1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) VALUES ((SELECT ID FROM report.dim_anm WHERE anmIdentifier='bhe2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) VALUES ((SELECT ID FROM report.dim_anm WHERE anmIdentifier='bhe3'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) VALUES ((SELECT ID FROM report.dim_anm WHERE anmIdentifier='bhe4'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) VALUES ((SELECT ID FROM report.dim_anm WHERE anmIdentifier='bhe5'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES ((SELECT ID FROM report.dim_anm WHERE anmIdentifier='klp1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) VALUES ((SELECT ID FROM report.dim_anm WHERE anmIdentifier='klp2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) VALUES ((SELECT ID FROM report.dim_anm WHERE anmIdentifier='klp3'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) VALUES ((SELECT ID FROM report.dim_anm WHERE anmIdentifier='klp4'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
