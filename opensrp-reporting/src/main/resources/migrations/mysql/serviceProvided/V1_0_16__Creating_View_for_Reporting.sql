DROP VIEW IF EXISTS report.service_provided_report_view;
CREATE VIEW report.service_provided_report_view
AS
(SELECT
  a.id AS "id",
  g.anmIdentifier AS "anmidentifier",
  f.type "service_provided_type",
  c.indicator AS "indicator", 
  a.date_ AS "service_date",
  e.village AS "village", 
  e.subcenter AS "subcenter", 
  h.phcidentifier AS "phc", 
  e.taluka AS "taluka", 
  e.district AS "district", 
  e.state AS "state" 
FROM 
  report.service_provided a, 
  report.dim_service_provider b, 
  report.dim_indicator c, 
  report.dim_location e,
  report.dim_service_provider_type f, 
  report.dim_anm g,
  report.dim_phc h
WHERE
  a.service_provider=b.id AND
  a.indicator=c.id AND 
  a.location=e.id AND
  b.type=f.id AND 
  b.service_provider=g.id AND 
  e.phc=h.id
);