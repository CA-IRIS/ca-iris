\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dms_reinit_detect', false);
INSERT INTO iris.system_attribute (name, value) VALUES ('email_recipient_reinit', '');
