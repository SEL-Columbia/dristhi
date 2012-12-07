INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('bherya', 'Bherya');

INSERT INTO report.dim_anm (anmIdentifier, phc) VALUES ('c', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='bherya'));

INSERT INTO report.dim_service_provider (service_provider, type) VALUES (
  (SELECT ID FROM report.dim_anm WHERE anmIdentifier='c'),
  (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));

INSERT INTO report.dim_indicator (indicator) VALUES ('DPT');
INSERT INTO report.dim_indicator (indicator) VALUES ('HEP');
INSERT INTO report.dim_indicator (indicator) VALUES ('OPV');
INSERT INTO report.dim_indicator (indicator) VALUES ('MEASLES');
INSERT INTO report.dim_indicator (indicator) VALUES ('BCG');
INSERT INTO report.dim_indicator (indicator) VALUES ('ANC');
INSERT INTO report.dim_indicator (indicator) VALUES ('ANC<12');
INSERT INTO report.dim_indicator (indicator) VALUES ('LIVE_BIRTH');
INSERT INTO report.dim_indicator (indicator) VALUES ('STILL_BIRTH');
INSERT INTO report.dim_indicator (indicator) VALUES ('OCP');
INSERT INTO report.dim_indicator (indicator) VALUES ('IUD');
INSERT INTO report.dim_indicator (indicator) VALUES ('MALE_STERILIZATION');
INSERT INTO report.dim_indicator (indicator) VALUES ('FEMALE_STERILIZATION');
INSERT INTO report.dim_indicator (indicator) VALUES ('CONDOM');
INSERT INTO report.dim_indicator (indicator) VALUES ('DMPA');
INSERT INTO report.dim_indicator (indicator) VALUES ('FP_TRADITIONAL');
INSERT INTO report.dim_indicator (indicator) VALUES ('LAM');
INSERT INTO report.dim_indicator (indicator) VALUES ('TT');

INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='DPT'), 40);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='HEP'), 50);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='OPV'), 60);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='MEASLES'), 45);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='BCG'), 55);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='ANC'), 50);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='ANC<12'), 60);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='OCP'), 40);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='IUD'), 50);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='MALE_STERILIZATION'), 60);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='FEMALE_STERILIZATION'), 400);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='CONDOM'), 55);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='DMPA'), 10);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='FP_TRADITIONAL'), 10);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='LAM'), 10);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='TT'), 40);
INSERT INTO report.annual_target (service_provider, indicator, target) VALUES ((SELECT sp.ID FROM report.dim_service_provider sp, report.dim_anm a WHERE sp.service_provider = a.id and anmIdentifier='c'), (SELECT ID FROM report.dim_indicator WHERE indicator='LIVE_BIRTH'), 200);