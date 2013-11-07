DROP VIEW IF EXISTS anm_report.anm_report_view;
CREATE VIEW anm_report.anm_report_view
AS
(SELECT 
  a.id AS "id", 
  g.anmIdentifier AS "anmidentifier",
  c.indicator AS "indicator", 
  d.date_ AS "service_date"
FROM 
  anm_report.anm_report_data a,
  anm_report.dim_indicator c,
  anm_report.dim_date d, 
  anm_report.dim_anm g
WHERE
  a.indicator=c.id AND 
  a.date_=d.id AND
  a.anmIdentifier=g.id
);