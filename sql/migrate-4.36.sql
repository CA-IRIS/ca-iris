\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.36.0'
	WHERE name = 'database_version';

INSERT INTO iris.comm_protocol VALUES (37, 'ONVIF PTZ');

ALTER TABLE iris.controller ADD COLUMN username VARCHAR(16);
