\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.XX.X' -- change me
	WHERE name = 'database_version';

-- CA trac 318
INSERT INTO iris.system_attribute(name, value) VALUES('system_protected_user_role','administrator');
