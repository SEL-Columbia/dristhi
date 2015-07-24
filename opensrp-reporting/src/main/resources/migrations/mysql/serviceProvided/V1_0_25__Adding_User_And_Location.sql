INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('chc_heresindogi', 'CHC Heresindogi');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('chc_kanakageri', 'CHC Kanakageri');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('chc_karatagi', 'CHC Karatagi');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('chc_mangalore', 'CHC Mangalore');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('chc_sriramnagar', 'CHC Sriramnagar');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('chc_tawargera', 'CHC Tawargera');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_alwandi', 'PHC Alwandi');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_anegundi', 'PHC Anegundi');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_bandihal', 'PHC Bandihal');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_benakal', 'PHC Benakal');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_betagera', 'PHC Betagera');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_bhagyanagar', 'PHC Bhagyanagar');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_budgumpa', 'PHC Budgumpa');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_chalgera', 'PHC Chalgera');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_dotihal', 'PHC Dotihal');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_gandhal', 'PHC Gandhal');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_hanamsagar', 'PHC Hanamsagar');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_hanmanhal', 'PHC Hanmanhal');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_hirebommanhal', 'PHC Hirebommanhal');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_hiregonnagar', 'PHC Hiregonnagar');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_hiremannapur', 'PHC Hiremannapur');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_hitnal', 'PHC Hitnal');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_hosakera', 'PHC Hosakera');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_hulgera', 'PHC Hulgera');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_irkalgada', 'PHC Irkalgada');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_mudenoor', 'PHC Mudenoor');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_mudhol', 'PHC Mudhol');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_mushtur', 'PHC Mushtur');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_muslimpur', 'PHC muslimpur');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_navali', 'PHC Navali');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_sanganhal', 'PHC Sanganhal');
INSERT INTO report.dim_phc(phcIdentifier, name) VALUES ('phc_vajra_bandi', 'PHC Vajra Bandi');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'hulihydar', 'hanmanhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'hulihydar', 'hosgudda');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'hulihydar', 'hulihyder');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'hulihydar', 'kanakapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'hulihydar', 'layadhunsi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'hulihydar', 'varankhed');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_a', 'anganwadi_1');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_a', 'anganwadi_15');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_a', 'anganwadi_17');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_a', 'anganwadi_2');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_a', 'anganwadi_3');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_a', 'anganwadi_7');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_a', 'anganwadi_9');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_b', 'anganwadi_11');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_b', 'anganwadi_12');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_b', 'anganwadi_14');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_b', 'anganwadi_16');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_b', 'anganwadi_18');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_b', 'anganwadi_19');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'kanakageri_b', 'anganwadi_5');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'karatagi_b', 'hale_juratgi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'karatagi_b', 'hosa_juratagi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'karatagi_b', 'karatagi_b');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'karatagi_b', 'ramnagar');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'salunchmara', 'hulkihal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'salunchmara', 'mari_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'salunchmara', 'marlanahalli');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'salunchmara', 'salunchimara');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'sriramnagar_a', 'guntakal_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'sriramnagar_a', 'modal_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'sriramnagar_a', 'sriramnagar_a');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'sriramnagar_b', 'anjuri_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'sriramnagar_b', 'dangeri_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'sriramnagar_b', 'renuka_coliny');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'sriramnagar_b', 'sriramnagar_b');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_anegundi'), 'basvandurg', 'basvandurg');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_anegundi'), 'basvandurg', 'krishnapuradiggi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_anegundi'), 'basvandurg', 'kurihatti');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_anegundi'), 'basvandurg', 'ramdurg');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_anegundi'), 'basvandurg', 'ramdurg_camp');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_budgumpa'), 'budgumpa', 'budgumpa');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_budgumpa'), 'budgumpa', 'devicamp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_budgumpa'), 'budgumpa', 'halasamudra');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_budgumpa'), 'budgumpa', 'lakshmi_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_budgumpa'), 'budgumpa', 'rajeev_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_budgumpa'), 'budgumpa', 'shesagiri_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_budgumpa'), 'budgumpa', 'timmapur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hosakera'), 'heroor', 'b_r_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hosakera'), 'heroor', 'batterhanchnal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hosakera'), 'heroor', 'galemmacamp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hosakera'), 'heroor', 'gonal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hosakera'), 'heroor', 'herur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mushtur'), 'chikka_jantkal', 'chikka_jantkal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mushtur'), 'chikka_jantkal', 'hosahalli');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mushtur'), 'chikka_jantkal', 'nagarhalli');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mushtur'), 'chikka_jantkal', 'vinoob_nagar');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mushtur'), 'danapur', 'danapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mushtur'), 'danapur', 'hebbal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mushtur'), 'danapur', 'majjigi_camp');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'basarihal', 'bailakampur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'basarihal', 'basrihal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'basarihal', 'chikka_wadderkal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'basarihal', 'devlapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'basarihal', 'gouripur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'obalbanda', 'hulejali');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'obalbanda', 'kannermadu');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'obalbanda', 'naglapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'obalbanda', 'obalbanda');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'obalbanda', 'ramdurg');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'obalbanda', 'rampur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'somsagara', 'a_b_chiktanda');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'somsagara', 'a_b_doddatanda');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'somsagara', 'advibhavi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'somsagara', 'somsagara');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_navali'), 'jeeral', 'chikka_dankankal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_navali'), 'jeeral', 'gandhi_nagar');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_navali'), 'jeeral', 'jeeral');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_navali'), 'jeeral', 'kalgudi_camp');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_navali'), 'jeeral', 'new_kalgudi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Gangavati', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_navali'), 'jeeral', 'old_kalgudi');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'bahaddurbandi', 'bahaddurbandi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'bahaddurbandi', 'belavinala');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'bahaddurbandi', 'chukankal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'bahaddurbandi', 'hosahalli');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'bahaddurbandi', 'huvinhal');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'gondabal', 'hyati');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'gondabal', 'muddaballi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'gondabal', 'new_gondabal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'gondabal', 'old_gondabal');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_alwandi'), 'mainahalli', 'bikanhalli');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_alwandi'), 'mainahalli', 'handral');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_alwandi'), 'mainahalli', 'hanwal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_alwandi'), 'mainahalli', 'mainahalli');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_betagera'), 'katarki', 'adaramaggi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_betagera'), 'katarki', 'belur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_betagera'), 'katarki', 'gudlanuru');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_betagera'), 'katarki', 'katarki');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_bhagyanagar'), 'bhagyanagar', 'bhagyanagar');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_bhagyanagar'), 'bhagyanagar', 'ojanhalli');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'chikbommanhal', 'achartimmapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'chikbommanhal', 'challari');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'chikbommanhal', 'chikbommanhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'chikbommanhal', 'chiksulikeri');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'chikbommanhal', 'ganganhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'chikbommanhal', 'hirebommanhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'chikbommanhal', 'hiresulikeri');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'chikbommanhal', 'uplapur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'arsinkera');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'arsinkera_thanda');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'chamalapura');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'chilkmukki');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'gosaldoddi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'hasgal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'hosur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'jinnapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'jinnapur_chickthanda');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'jinnapur_thanda');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'metgal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'sidaganhalli');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'hasgal', 'venktapur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'agalkera', 'agalkera');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'bandi_harlapur', 'hosabandiharlapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'bandi_harlapur', 'kouli');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'bandi_harlapur', 'mohammad_nagar');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'bandi_harlapur', 'shivapur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'hitnal', 'hitnal');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'shivpur', 'kampasagar');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'shivpur', 'old_shivapur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_irkalgada'), 'irkalgada', 'hanmanhatti');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_irkalgada'), 'irkalgada', 'irkalgada');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_irkalgada'), 'irkalgada', 'kodadhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_irkalgada'), 'irkalgada', 'taad_kanakapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_irkalgada'), 'irkalgada', 'wadderhatti');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Koppal', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_irkalgada'), 'irkalgada', 'yelamgera');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_10');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_13');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_14');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_15');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_2');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_4');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_5');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_6');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_7');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'anganwadi_8');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'tawargera_a', 'gadderatti');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'benkanhal', 'benkanhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'benkanhal', 'kalalbandi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'benkanhal', 'madikera');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'benkanhal', 'yelbunchi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'benkanhal', 'zhoolkatti');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'talugeri', 'kordakera');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'talugeri', 'talugeri');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'talugeri', 'topalkatti');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'talugeri', 'vanageri');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_dotihal'), 'nidsesi', 'benchamatti');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_dotihal'), 'nidsesi', 'byalihal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_dotihal'), 'nidsesi', 'madalgatti');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_dotihal'), 'nidsesi', 'nidsesi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_dotihal'), 'nidsesi', 'shakapur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanamsagar'), 'hanamsagar_a', 'beelagi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanamsagar'), 'hanamsagar_a', 'develapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanamsagar'), 'hanamsagar_a', 'hanamsagar_a');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanamsagar'), 'hanamsagar_a', 'mannerhal');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'nilogal', 'basapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'nilogal', 'bommanhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'nilogal', 'nilogal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'nilogal', 'rampur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'nilogal', 'vakkandurga');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'tuggaldoni', 'masbinkurbanhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'tuggaldoni', 'mittalkod');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'tuggaldoni', 'nirlakoppa');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'tuggaldoni', 'shadalgera');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'tuggaldoni', 'tuggaldoni');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiregonnagar'), 'hiregonnagar', 'chickgonnagar');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiregonnagar'), 'hiregonnagar', 'habalkatti');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiregonnagar'), 'hiregonnagar', 'hiregonnagar');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiregonnagar'), 'hiregonnagar', 'narsapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiregonnagar'), 'hiregonnagar', 'tumrikoppa');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiregonnagar'), 'hiregonnagar', 'varikal');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'gudadur', 'basapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'gudadur', 'gonhal_k');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'gudadur', 'gudadur_m');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'gudadur', 'hosur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'gudadur', 'madapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'gudadur', 'tengunti');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'hiremannapur', 'ganganhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'hiremannapur', 'gumgeri');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'hiremannapur', 'hiremannapur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hulgera'), 'katapur', 'kabbargi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hulgera'), 'katapur', 'kalgonhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hulgera'), 'katapur', 'katapur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudenoor'), 'muddalgundi', 'amarapur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudenoor'), 'muddalgundi', 'kalmalli');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudenoor'), 'muddalgundi', 'kidadur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudenoor'), 'muddalgundi', 'muddalgundi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Kustagi', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudenoor'), 'muddalgundi', 'muddalgundi_thanda');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'chendoor', 'arkeri');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'chendoor', 'chendoor');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'chendoor', 'muthalu');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'chendoor', 'sirur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'mangalore_a', 'anganwadi_10');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'mangalore_a', 'anganwadi_2');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'mangalore_a', 'anganwadi_4');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'mangalore_a', 'anganwadi_6');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_bandihal'), 'bandihal', 'bandihal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_bandihal'), 'bandihal', 'tondihal');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_bandihal'), 'karmudi', 'karmudi');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_benakal'), 'benakal', 'benakal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_benakal'), 'benakal', 'veerapur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_gandhal'), 'gandhal_a', 'bukkanhatti');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_gandhal'), 'gandhal_a', 'gandhal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_gandhal'), 'gandhal_a', 'hire_waddarkal');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_gandhal'), 'gandhal_a', 'katgihalli');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudhol'), 'hiremyageri', 'hiremyageri');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudhol'), 'hiremyageri', 'sompur');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudhol'), 'mudhol', 'mudhol');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_sanganhal'), 'sanganhal', 'kallur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_sanganhal'), 'sanganhal', 'sanganhal');

INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_vajra_bandi'), 'hulegudda', 'hulegudda');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_vajra_bandi'), 'hulegudda', 'makkahalli');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_vajra_bandi'), 'hulegudda', 'murdi_thanda');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_vajra_bandi'), 'hulegudda', 'salbhavi');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_vajra_bandi'), 'hulegudda', 'talur');
INSERT INTO report.dim_location(state, district, taluka, phc, subCenter, village) VALUES ('Karnataka', 'Koppal', 'Yalburga', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_vajra_bandi'), 'hulegudda', 'talur_thanda');

--  ANM DATA
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'kana1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'Manjula', 'hulihydar');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'kana2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'K.Santha', 'kanakageri_a');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'kana3', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_kanakageri'), 'Jayasree', 'kanakageri_b');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'kara1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'Bhagyalakshmi', 'karatagi_b');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'kara2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_karatagi'), 'Santarayanagoudar', 'salunchmara');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'sri1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'Asha Begum', 'sriramnagar_a');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'sri2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_sriramnagar'), 'Santhavva', 'sriramnagar_b');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'ane1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_anegundi'), 'Manjula', 'basvandurg');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'bud1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_budgumpa'), 'K.N.Bhairamatti', 'budgumpa');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hosa1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hosakera'), 'Mulimani Khadarbi', 'heroor');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'mush1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mushtur'), 'Gnaneswari', 'chikka_jantkal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'mush2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mushtur'), 'Kavita', 'danapur');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'musl1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'Kanaakava ( Kamalamma)', 'basarihal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'musl2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'Aswani C', 'obalbanda');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'musl3', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_muslimpur'), 'Mahadevakka', 'somsagara');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'nava1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_navali'), 'Mangalabai', 'jeeral');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'here1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'Rose Henna', 'bahaddurbandi');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'here2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_heresindogi'), 'Hasena Begum', 'gondabal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'alw1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_alwandi'), 'Saraswati', 'mainahalli');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'bet1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_betagera'), 'Saubhagyamma', 'katarki');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'bha1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_bhagyanagar'), 'S.V.Mahendrakar', 'bhagyanagar');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hir1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'Siddalingamma', 'chikbommanhal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hir2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hirebommanhal'), 'Manjula', 'hasgal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hit1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'Vijayalakshmi', 'agalkera');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hit2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'J. Ambika', 'bandi_harlapur');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hit3', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'K.Bhagyamma', 'hitnal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hit4', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hitnal'), 'Devakki Jaylaxmi', 'shivpur');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'irk1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_irkalgada'), 'Karibasamma', 'irkalgada');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'taw1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_tawargera'), 'Renuka', 'tawargera_a');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'cha1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'Sarvamangala', 'benkanhal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'cha2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_chalgera'), 'Renuka.R.', 'talugeri');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'dot1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_dotihal'), 'Renuka.Hubli', 'nidsesi');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'han1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanamsagar'), 'Sharanamma Malipatil', 'hanamsagar_a');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hanl1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'Rekha Immadi', 'nilogal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hanl2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hanmanhal'), 'Mangalagauri Pirangi', 'tuggaldoni');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hire1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiregonnagar'), 'Lalitavva', 'hiregonnagar');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hmp1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'Channabasavva', 'gudadur');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hmp2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hiremannapur'), 'Nurajahan Begum', 'hiremannapur');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'hul1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_hulgera'), 'Yashoda', 'katapur');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'mud1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudenoor'), 'Sis. Renuka', 'muddalgundi');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'man1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'Kumari Manjula', 'chendoor');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'man2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='chc_mangalore'), 'Geeta', 'mangalore_a');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'ban1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_bandihal'), 'Manjula.M', 'bandihal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'ban2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_bandihal'), 'Shobhabai', 'karmudi');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'ben1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_benakal'), 'Eramma Ryavanaki', 'benakal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'gan1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_gandhal'), 'Kalavati.S.M', 'gandhal_a');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'mul1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudhol'), 'Hemakshi', 'hiremyageri');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'mul2', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_mudhol'), 'Savitramma', 'mudhol');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'san1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_sanganhal'), 'Shantadevi', 'sanganhal');
INSERT INTO report.dim_anm (anmIdentifier, phc, name, subcenter)  (SELECT 'vaj1', (SELECT ID FROM report.dim_phc WHERE phcIdentifier='phc_vajra_bandi'), 'Rajeshwari', 'hulegudda');

INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='kana1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='kana2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='kana3'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='kara1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='kara2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='sri1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='sri2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='ane1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='bud1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hosa1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='mush1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='mush2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='musl1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='musl2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='musl3'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='nava1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='here1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='here2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='alw1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='bet1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='bha1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hir1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hir2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hit1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hit2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hit3'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hit4'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='irk1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='taw1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='cha1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='cha2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='dot1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='han1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hanl1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hanl2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hire1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hmp1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hmp2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='hul1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='mud1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='man1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='man2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='ban1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='ban2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='ben1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='gan1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='mul1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='mul2'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='san1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
INSERT INTO report.dim_service_provider (service_provider, type) (SELECT (SELECT ID FROM report.dim_anm WHERE anmIdentifier='vaj1'), (SELECT ID FROM report.dim_service_provider_type WHERE type='ANM'));
