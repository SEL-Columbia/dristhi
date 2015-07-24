-- trainer1
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('trainer1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer1'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- trainer2
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('trainer2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer2'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user1
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user1'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user2
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user2'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user3
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user3', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user3'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user4
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user4', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user4'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user5
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user5', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user5'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user6
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user6', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user6'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user7
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user7', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user7'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user8
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user8', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user8'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user9
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user9', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user9'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user10
INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('user10', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user10'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
