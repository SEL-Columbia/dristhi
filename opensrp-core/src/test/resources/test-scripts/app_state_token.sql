--clear data
TRUNCATE TABLE core.app_state_token;

ALTER SEQUENCE core.app_state_token_id_seq RESTART WITH 6;

--insert data
INSERT INTO core.app_state_token (id, name, description, value, last_edited_date) VALUES
(1, 'sync_schedule_tracker_by_last_update_enrollment', 'ScheduleTracker token to keep track of enrollment', '34343', 1521017416),
(2, 'sync_client_by_date_updated', 'OpenMRS data pusher token to keep track of new / updated clients', '65765', 1521217416),
(3, 'sync_client_by_date_voided', 'OpenMRS data pusher token to keep track of voided clients synced ', '122001', 1521017416),
(4, 'sync_event_by_date_updated', 'OpenMRS data pusher token to keep track of new / updated events synced ', '343232', 1521017416),
(5, 'sync_event_by_date_voided', 'OpenMRS data pusher token to keep track of voided events synced ', '23432', 1521017416);
