INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'demo4', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Demo4', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'demo5', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Demo5', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'demo6', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Demo6', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'demo7', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Demo7', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'demo8', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Demo8', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'demo9', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Demo9', 'munjanahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'demo10', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'), 'Demo10', 'munjanahalli');

INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='demo4'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='demo5'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='demo6'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='demo7'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='demo8'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='demo9'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='demo10'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));