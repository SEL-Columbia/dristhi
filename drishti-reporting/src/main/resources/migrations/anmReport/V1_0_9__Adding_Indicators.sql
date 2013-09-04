INSERT INTO anm_report.dim_indicator (indicator) VALUES ('CHILD_DIARRHEA');
INSERT INTO anm_report.dim_indicator (indicator) VALUES ('F_VIT_A_1');
INSERT INTO anm_report.dim_indicator (indicator) VALUES ('F_VIT_A_2');
INSERT INTO anm_report.dim_indicator (indicator) VALUES ('M_VIT_A_1');
INSERT INTO anm_report.dim_indicator (indicator) VALUES ('M_VIT_A_2');
INSERT INTO anm_report.dim_indicator (indicator) VALUES ('CMD');
UPDATE anm_report.dim_indicator SET indicator = 'PENTAVALENT3_OPV3' where indicator = 'DPT3_OPV3';