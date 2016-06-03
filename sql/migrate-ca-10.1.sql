\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.XX.X' -- change me
	WHERE name = 'database_version';

-- CA trac 318
INSERT INTO iris.system_attribute(name, value) VALUES('system_protected_user_role','administrator');

-- CA trac 446
ALTER TABLE iris.map_extent ADD COLUMN position INTEGER NOT NULL DEFAULT 0;

-- Need block level to declare and update variables
-- So we create a temp function for this purpose then immediately drop the function
CREATE FUNCTION iris.migrate_extents() RETURNS VOID AS $$
DECLARE
	idx INTEGER DEFAULT 0;
	extent VARCHAR(20);
BEGIN
	FOR extent IN SELECT name FROM iris.map_extent ORDER BY name::bytea LOOP
		UPDATE iris.map_extent SET position = idx WHERE name = extent;
		idx := idx + 1;
	END LOOP;
	RETURN;
END;
$$ LANGUAGE plpgsql;
SELECT iris.migrate_extents();
DROP FUNCTION iris.migrate_extents();

-- now we can enforce uniqueness & drop default value
ALTER TABLE iris.map_extent ADD UNIQUE (position);
ALTER TABLE iris.map_extent ALTER COLUMN position DROP DEFAULT;

-- CA trac 504
INSERT INTO iris.system_attribute(name, value) VALUES('camera_direction_override','');

-- CA trac 528
INSERT INTO iris.comm_protocol (id, description) VALUES(34, 'TTIP DMS');

