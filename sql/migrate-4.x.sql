\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.XX.X' -- change me
	WHERE name = 'database_version';

-- CA trac 318
INSERT INTO iris.system_attribute(name, value) VALUES('system_protected_user_role','administrator');

-- CA trac 504
INSERT INTO iris.system_attribute(name, value) VALUES('camera_direction_override','');

-- CA trac 528
INSERT INTO iris.comm_protocol (id, description) VALUES(34, 'TTIP DMS');

-- CA trac 578 PTZ issue
INSERT INTO iris.system_attribute(name, value) VALUES('camera_ptz_fixed_speed', false);
