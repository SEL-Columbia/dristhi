-- trainer3
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('trainer3', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer3'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- trainer4
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('trainer4', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer4'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- trainer5
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('trainer5', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer5'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user11
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user11', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user11'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user12
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user12', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user12'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user13
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user13', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user13'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user14
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user14', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user14'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user15
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user15', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user15'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user16
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user16', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user16'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user17
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user17', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user17'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user18
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user18', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user18'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user19
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user19', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user19'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

-- user20
INSERT INTO report.dim_anm (anmIdentifier, phc, subcenter) VALUES ('user20', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='keelanapura'), 'keelanapura_a');

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user20'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));