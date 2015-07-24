ALTER TABLE report.dim_anm ADD COLUMN subcenter VARCHAR(100);

UPDATE report.dim_anm SET subcenter='bherya_a' WHERE anmidentifier='bhe1';

UPDATE report.dim_anm SET subcenter='bherya_b' WHERE anmidentifier='bhe2';

UPDATE report.dim_anm SET subcenter='g_a_guppe' WHERE anmidentifier='bhe3';

UPDATE report.dim_anm SET subcenter='hosa_agrahara' WHERE anmidentifier='bhe4';

UPDATE report.dim_anm SET subcenter='munjanahalli' WHERE anmidentifier='bhe5';

UPDATE report.dim_anm SET subcenter='keelanapura_a' WHERE anmidentifier='klp4';

UPDATE report.dim_anm SET subcenter='keelanapura_b' WHERE anmidentifier='klp2';

UPDATE report.dim_anm SET subcenter='puttegowdanahundi' WHERE anmidentifier='klp1';

UPDATE report.dim_anm SET subcenter='vajamangala' WHERE anmidentifier='klp3';

UPDATE report.dim_anm SET subcenter='munjanahalli' WHERE subcenter IS NULL;