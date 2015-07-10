INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user41', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User41', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user42', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User42', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user43', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User43', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user44', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User44', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user45', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User45', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user46', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User46', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user47', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User47', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user48', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User48', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user49', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User49', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'user50', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'User50', 'munjanahalli');

INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user41'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user42'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user43'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user44'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user45'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user46'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user47'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user48'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user49'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='user50'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));


-- Trainer data
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'trainer11', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Trainer11', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'trainer12', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Trainer12', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'trainer13', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Trainer13', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'trainer14', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Trainer14', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'trainer15', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Trainer15', 'munjanahalli');

INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer11'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer12'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer13'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer14'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='trainer15'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
