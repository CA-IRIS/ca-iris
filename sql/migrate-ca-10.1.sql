\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.XX.X' -- change me
	WHERE name = 'database_version';

-- CA trac 318
INSERT INTO iris.system_attribute(name, value) VALUES('system_protected_user_role','administrator');

-- CA trac 446
ALTER TABLE iris.map_extent ADD COLUMN position INTEGER NOT NULL DEFAULT 0;
-- TODO: Need to update existing extents to have positions from 0..n-1
ALTER TABLE iris.map_extent ADD UNIQUE (position);

-- CA trac 504
INSERT INTO iris.system_attribute(name, value) VALUES('camera_direction_override','');