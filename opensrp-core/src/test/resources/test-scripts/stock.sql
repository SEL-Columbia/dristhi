--clear data
TRUNCATE TABLE core.stock CASCADE;

ALTER SEQUENCE core.stock_id_seq RESTART WITH 16;

ALTER SEQUENCE core.stock_metadata_id_seq RESTART WITH 16;

--insert data
INSERT INTO core.stock (id, json) VALUES 
(1, '{"id": "05934ae338431f28bf6793b24181ea5e", "type": "Stock", "value": -3, "to_from": "C/C", "version": 1521003030510, "revision": "1-4f090916632e2ae10f1ef7f972aaf6d2", "identifier": 1, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.515+03:00", "date_created": 1520892000000, "date_updated": 1520939303991, "serverVersion": 1521003030510, "vaccine_type_id": "2", "transaction_type": "issued"}'),
(2, '{"id": "05934ae338431f28bf6793b241974356", "type": "Stock", "value": 10, "to_from": "DHO", "version": 1521007341010, "revision": "1-f24ec853ded69d25028985069960f71a", "identifier": 2, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.587+03:00", "date_created": 1518559200000, "date_updated": 1521007053945, "serverVersion": 1521007341010, "vaccine_type_id": "1", "transaction_type": "received"}'),
(3, '{"id": "05934ae338431f28bf6793b241974f0e", "type": "Stock", "value": -1, "to_from": "C/C", "version": 1521007341010, "revision": "1-6e7a4b9ee84403f5fa25c5d83269149b", "identifier": 3, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.633+03:00", "date_created": 1520892000000, "date_updated": 1521007153528, "serverVersion": 1521007341010, "vaccine_type_id": "1", "transaction_type": "issued"}'),
(4, '{"id": "05934ae338431f28bf6793b241975c6c", "type": "Stock", "value": 12, "to_from": "Physical_recount", "version": 1521007342323, "revision": "1-a57ae3bf0a9d652f00dc33c23e2c7a52", "identifier": 4, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.655+03:00", "date_created": 1518559200000, "date_updated": 1521007339163, "serverVersion": 1521007342323, "vaccine_type_id": "2", "transaction_type": "loss_adjustment"}'),
(5, '{"id": "05934ae338431f28bf6793b241978ad9", "type": "Stock", "value": 20, "to_from": "DHO", "version": 1521009418783, "revision": "1-1421e42faf02e9fe5a65279ee3b199c3", "identifier": 5, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.672+03:00", "date_created": 1521000000000, "date_updated": 1521009346585, "serverVersion": 1521009418783, "vaccine_type_id": "1", "transaction_type": "received"}'),
(6, '{"id": "05934ae338431f28bf6793b2419a4bd9", "type": "Stock", "value": 20, "to_from": "DHO", "version": 1521022620903, "revision": "1-aba3734a871fb2b88deab7bb7e7f3a60", "identifier": 6, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.683+03:00", "date_created": 1519855200000, "date_updated": 1521022504878, "serverVersion": 1521022620903, "vaccine_type_id": "1", "transaction_type": "received"}'),
(7, '{"id": "05934ae338431f28bf6793b2419a590b", "type": "Stock", "value": -27, "to_from": "C/C", "version": 1521022620903, "revision": "1-5f6fdd394f6c34143f07798dd7fe74b0", "identifier": 7, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.689+03:00", "date_created": 1520632800000, "date_updated": 1521022571660, "serverVersion": 1521022620903, "vaccine_type_id": "1", "transaction_type": "issued"}'),
(8, '{"id": "05934ae338431f28bf6793b2419a606f", "type": "Stock", "value": 1, "to_from": "Physical_recount", "version": 1521023046990, "revision": "1-872a0dd1e50ea9437786f69fad2d182a", "identifier": 8, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.693+03:00", "date_created": 1518559200000, "date_updated": 1521022962706, "serverVersion": 1521023046990, "vaccine_type_id": "1", "transaction_type": "loss_adjustment"}'),
(9, '{"id": "05934ae338431f28bf6793b2419a6dc7", "type": "Stock", "value": 10, "to_from": "DHO", "version": 1521030501655, "revision": "1-a3846a988777e252c6f9aba383300b78", "identifier": 9, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.705+03:00", "date_created": 1518559200000, "date_updated": 1521027411881, "serverVersion": 1521030501655, "vaccine_type_id": "2", "transaction_type": "received"}'),
(10, '{"id": "05934ae338431f28bf6793b2419a72da", "type": "Stock", "value": -19, "to_from": "C/C", "version": 1521030501655, "revision": "1-42168381f27525e3c9022fbc46358a4b", "identifier": 10, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.735+03:00", "date_created": 1519164000000, "date_updated": 1521027466540, "serverVersion": 1521030501655, "vaccine_type_id": "2", "transaction_type": "issued"}'),
(11, '{"id": "05934ae338431f28bf6793b241b2daa6", "type": "Stock", "value": -7, "to_from": "C/C", "version": 1521074546344, "revision": "1-65d5197a96e17a8febf61324fbe5d454", "identifier": 11, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.755+03:00", "date_created": 1521064800000, "date_updated": 1521074321594, "serverVersion": 1521074546344, "vaccine_type_id": "1", "transaction_type": "issued"}'),
(12, '{"id": "05934ae338431f28bf6793b241b2df09", "type": "Stock", "value": -2, "to_from": "Physical_recount", "version": 1521074546344, "revision": "1-4069d28441029c88717f71d622eab412", "identifier": 12, "providerid": "biddemo", "dateCreated": "2018-04-04T13:55:43.771+03:00", "date_created": 1521064800000, "date_updated": 1521074352741, "serverVersion": 1521074546344, "vaccine_type_id": "1", "transaction_type": "loss_adjustment"}'),
(13, '{"id": "05934ae338431f28bf6793b241b3c445", "type": "Stock", "value": -6, "to_from": "C/C", "version": 1521112141441, "revision": "1-509d0919039dc1b0f9180aa187a87b75", "identifier": 13, "providerid": "biddemo1", "dateCreated": "2018-04-04T13:55:43.787+03:00", "date_created": 1520632800000, "date_updated": 1521111867821, "serverVersion": 1521112141441, "vaccine_type_id": "1", "transaction_type": "issued"}'),
(14, '{"id": "05934ae338431f28bf6793b241b3c6f9", "type": "Stock", "value": 2, "to_from": "DHO", "version": 1521112141441, "revision": "1-7e45403108a172bd63486e35ec99ce79", "identifier": 14, "providerid": "biddemo1", "dateCreated": "2018-04-04T13:55:43.804+03:00", "date_created": 1518645600000, "date_updated": 1521111912719, "serverVersion": 1521112141441, "vaccine_type_id": "1", "transaction_type": "received"}'),
(15, '{"id": "05934ae338431f28bf6793b241b3cd6f", "type": "Stock", "value": -55, "to_from": "C/C", "version": 1521112141441, "revision": "1-83e0bc2abeda38e5aa282d1f8d75e6bc", "identifier": 15, "providerid": "biddemo1", "dateCreated": "2018-04-04T13:55:43.809+03:00", "date_created": 1520460000000, "date_updated": 1521111967331, "serverVersion": 1521112141441, "vaccine_type_id": "1", "transaction_type": "issued"}');


INSERT INTO core.stock_metadata (id, stock_id, document_id, server_version, provider_id, location_id, team, team_id) VALUES
(1, 1, '05934ae338431f28bf6793b24181ea5e', 1521003030510, 'biddemo', null, null, null),
(2, 2, '05934ae338431f28bf6793b241974356', 1521007341010, 'biddemo', null, null, null),
(3, 3, '05934ae338431f28bf6793b241974f0e', 1521007341010, 'biddemo', null, null, null),
(4, 4, '05934ae338431f28bf6793b241975c6c', 1521007342323, 'biddemo', null, null, null),
(5, 5, '05934ae338431f28bf6793b241978ad9', 1521009418783, 'biddemo', null, null, null),
(6, 6, '05934ae338431f28bf6793b2419a4bd9', 1521022620903, 'biddemo', null, null, null),
(7, 7, '05934ae338431f28bf6793b2419a590b', 1521022620903, 'biddemo', null, null, null),
(8, 8, '05934ae338431f28bf6793b2419a606f', 1521023046990, 'biddemo', null, null, null),
(9, 9, '05934ae338431f28bf6793b2419a6dc7', 1521030501655, 'biddemo', null, null, null),
(10, 10, '05934ae338431f28bf6793b2419a72da', 1521030501655, 'biddemo', null, null, null),
(11, 11, '05934ae338431f28bf6793b241b2daa6', 1521074546344, 'biddemo', null, null, null),
(12, 12, '05934ae338431f28bf6793b241b2df09', 1521074546344, 'biddemo', null, null, null),
(13, 13, '05934ae338431f28bf6793b241b3c445', 1521112141441, 'biddemo1', null, null, null),
(14, 14, '05934ae338431f28bf6793b241b3c6f9', 1521112141441, 'biddemo1', null, null, null),
(15, 15, '05934ae338431f28bf6793b241b3cd6f', 1521112141441, 'biddemo1', null, null, null);