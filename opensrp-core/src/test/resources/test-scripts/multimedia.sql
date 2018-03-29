--clear data
TRUNCATE TABLE core.multi_media;

ALTER SEQUENCE core.multi_media_id_seq RESTART WITH 6;

INSERT INTO core.multi_media (id, document_id, case_id, provider_id, content_type, file_path, file_category) VALUES 
(1, '05934ae338431f28bf6793b241f0c5ca', '040d4f18-8140-479c-aa21-725612073490', 'biddemo', 'image/jpeg', '/opt/patient_images/040d4f18-8140-479c-aa21-725612073490.jpg', 'profilepic'),
(2, '317f8db1bb6cc4b15ecc9993a282bc2f', '3c07c42c-6aee-4eb9-8db3-0aae3eac8f6d', 'biddemo', 'image/jpeg', '/opt/patient_images/3c07c42c-6aee-4eb9-8db3-0aae3eac8f6d.jpg', 'profilepic'),
(3, '091488163b6ecd589a915372a0ad3b0d', '87dc3230-84f7-4088-b257-e8b3130ab86b', 'biddemo', 'image/jpeg', '/opt/patient_images/87dc3230-84f7-4088-b257-e8b3130ab86b.jpg', 'profilepic'),
(4, '3157f9339bf0c948dd5d12aff82111e1', '01503901-5acb-45fb-affe-839ac39d200d', 'biddemo', 'image/jpeg', '/opt/patient_images/01503901-5acb-45fb-affe-839ac39d200d.jpg', 'profilepic'),
(5, '317f8db1bb6cc4b15ecc9993a2922f47', '24eec0d8-e0ee-4f22-9d6b-3cca84bdefcf', 'tester11', 'image/jpeg', '/opt/patient_images/24eec0d8-e0ee-4f22-9d6b-3cca84bdefcf.jpg', 'profilepic');
