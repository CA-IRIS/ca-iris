-- current as of MnDOT 4.35.4


-- updates required before rest of updates
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

-- updates CA-only protocols (that haven't been given to MnDOT)
INSERT INTO iris.comm_protocol (id, description) VALUES (35, 'CA RWIS');
INSERT INTO iris.comm_protocol (id, description) VALUES (36, 'TTIP DMS');
UPDATE iris.comm_link SET protocol = 36 WHERE protocol = 34;
UPDATE iris.comm_link SET protocol = 35 WHERE protocol = 33;
DELETE FROM iris.comm_protocol WHERE id IN (33, 34);

-- fix some table ownership issues found in D10
ALTER TABLE event.tag_type OWNER TO tms;
ALTER TABLE event.tag_read_event OWNER TO tms;
ALTER VIEW tag_read_event_view OWNER TO tms;

-- ADDITIONS for CA-IRIS v10.2 go at the bottom




-- ============================================================================
-- BEGIN: MnDOT updates
-- ============================================================================

-- migrate-4.27.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.27.0'
	WHERE name = 'database_version';

-- add tollway column to toll_zone
ALTER TABLE iris.toll_zone ADD COLUMN tollway VARCHAR(16);

-- add tollway to toll_zone_view
CREATE OR REPLACE VIEW toll_zone_view AS
	SELECT name, start_id, end_id, tollway
	FROM iris.toll_zone;
GRANT SELECT ON toll_zone_view TO PUBLIC;

-- rename dms_op_status_enable sys attr to device_op_status_enable
UPDATE iris.system_attribute SET name = 'device_op_status_enable'
	WHERE name = 'dms_op_status_enable';

-- add toll_zone column to tag_reader
DROP VIEW tag_reader_view;
DROP VIEW iris.tag_reader;
DROP FUNCTION iris.tag_reader_insert();
DROP FUNCTION iris.tag_reader_update();
DROP FUNCTION iris.tag_reader_delete();

ALTER TABLE iris._tag_reader
    ADD COLUMN toll_zone VARCHAR(20) REFERENCES iris.toll_zone(name);

CREATE VIEW iris.tag_reader AS SELECT
	t.name, geo_loc, controller, pin, notes, toll_zone
	FROM iris._tag_reader t JOIN iris._device_io d ON t.name = d.name;

CREATE FUNCTION iris.tag_reader_insert() RETURNS TRIGGER AS
	$tag_reader_insert$
BEGIN
	INSERT INTO iris._device_io (name, controller, pin)
	     VALUES (NEW.name, NEW.controller, NEW.pin);
	INSERT INTO iris._tag_reader (name, geo_loc, notes, toll_zone)
	     VALUES (NEW.name, NEW.geo_loc, NEW.notes, NEW.toll_zone);
	RETURN NEW;
END;
$tag_reader_insert$ LANGUAGE plpgsql;

CREATE TRIGGER tag_reader_insert_trig
    INSTEAD OF INSERT ON iris.tag_reader
    FOR EACH ROW EXECUTE PROCEDURE iris.tag_reader_insert();

CREATE FUNCTION iris.tag_reader_update() RETURNS TRIGGER AS
	$tag_reader_update$
BEGIN
	UPDATE iris._device_io
	   SET controller = NEW.controller,
	       pin = NEW.pin
	 WHERE name = OLD.name;
	UPDATE iris._tag_reader
	   SET geo_loc = NEW.geo_loc,
	       notes = NEW.notes,
	       toll_zone = NEW.toll_zone
	 WHERE name = OLD.name;
	RETURN NEW;
END;
$tag_reader_update$ LANGUAGE plpgsql;

CREATE TRIGGER tag_reader_update_trig
    INSTEAD OF UPDATE ON iris.tag_reader
    FOR EACH ROW EXECUTE PROCEDURE iris.tag_reader_update();

CREATE FUNCTION iris.tag_reader_delete() RETURNS TRIGGER AS
	$tag_reader_delete$
BEGIN
	DELETE FROM iris._device_io WHERE name = OLD.name;
	IF FOUND THEN
		RETURN OLD;
	ELSE
		RETURN NULL;
	END IF;
END;
$tag_reader_delete$ LANGUAGE plpgsql;

CREATE TRIGGER tag_reader_delete_trig
    INSTEAD OF DELETE ON iris.tag_reader
    FOR EACH ROW EXECUTE PROCEDURE iris.tag_reader_delete();

CREATE VIEW tag_reader_view AS
	SELECT t.name, t.notes, t.toll_zone, t.geo_loc, l.roadway, l.road_dir,
	       l.cross_mod, l.cross_street, l.cross_dir, l.lat, l.lon,
	       t.controller, t.pin, ctr.comm_link, ctr.drop_id, ctr.condition
	FROM iris.tag_reader t
	LEFT JOIN geo_loc_view l ON t.geo_loc = l.name
	LEFT JOIN controller_view ctr ON t.controller = ctr.name;
GRANT SELECT ON tag_reader_view TO PUBLIC;

-- drop old tag_read_event_view
DROP VIEW tag_read_event_view;

-- drop toll_zone and tollway from event.tag_read_event table
ALTER TABLE event.tag_read_event DROP COLUMN toll_zone;
ALTER TABLE event.tag_read_event DROP COLUMN tollway;

-- change tag_read_event_view to use tollway from toll_zone
CREATE VIEW tag_read_event_view AS
	SELECT event_id, event_date, event_description.description,
	       tag_type.description AS tag_type, tag_id, tag_reader,
	       toll_zone, tollway, hov, trip_id
	FROM event.tag_read_event
	JOIN event.event_description
	ON   tag_read_event.event_desc_id = event_description.event_desc_id
	JOIN event.tag_type
	ON   tag_read_event.tag_type = tag_type.id
	JOIN iris.tag_reader
	ON   tag_read_event.tag_reader = tag_reader.name
	LEFT JOIN iris.toll_zone
	ON        tag_reader.toll_zone = toll_zone.name;
GRANT SELECT ON tag_read_event_view TO PUBLIC;

-- create update trigger for tag_read_event_view
CREATE FUNCTION event.tag_read_event_view_update() RETURNS TRIGGER AS
	$tag_read_event_view_update$
BEGIN
	UPDATE event.tag_read_event
	   SET trip_id = NEW.trip_id
	 WHERE event_id = OLD.event_id;
	RETURN NEW;
END;
$tag_read_event_view_update$ LANGUAGE plpgsql;

CREATE TRIGGER tag_read_event_view_update_trig
    INSTEAD OF UPDATE ON tag_read_event_view
    FOR EACH ROW EXECUTE PROCEDURE event.tag_read_event_view_update();




-- migrate-4.28.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.28.0'
	WHERE name = 'database_version';

-- add toll price limit system attributes
INSERT INTO iris.system_attribute (name, value) VALUES ('toll_min_price', '0.25');
INSERT INTO iris.system_attribute (name, value) VALUES ('toll_max_price', '8');

-- replace sign_message scheduled with source
-- DELETE FROM iris.sign_message;
ALTER TABLE iris.sign_message ADD COLUMN source INTEGER;
UPDATE iris.sign_message SET source = 0;
UPDATE iris.sign_message SET source = 1 WHERE scheduled = true;
ALTER TABLE iris.sign_message ALTER COLUMN source SET NOT NULL;
ALTER TABLE iris.sign_message DROP COLUMN scheduled;

-- added price message event descriptions
INSERT INTO event.event_description (event_desc_id, description)
	VALUES (651, 'Price DEPLOYED');
INSERT INTO event.event_description (event_desc_id, description)
	VALUES (652, 'Price VERIFIED');

-- add price_message_event table
CREATE TABLE event.price_message_event (
	event_id SERIAL PRIMARY KEY,
	event_date timestamp WITH time zone NOT NULL,
	event_desc_id INTEGER NOT NULL
		REFERENCES event.event_description(event_desc_id),
	device_id VARCHAR(20) NOT NULL,
	toll_zone VARCHAR(20) NOT NULL,
	price NUMERIC(4,2) NOT NULL
);

-- add price_message_event_view
CREATE VIEW price_message_event_view AS
	SELECT event_id, event_date, event_description.description,
	       device_id, toll_zone, price
	FROM event.price_message_event
	JOIN event.event_description
	ON price_message_event.event_desc_id = event_description.event_desc_id;
GRANT SELECT ON price_message_event_view TO PUBLIC;

-- add tag_reader_dms relation
CREATE TABLE iris.tag_reader_dms (
	tag_reader VARCHAR(10) NOT NULL REFERENCES iris._tag_reader,
	dms VARCHAR(10) NOT NULL REFERENCES iris._dms
);

-- add agency field to tag_read_event
ALTER TABLE event.tag_read_event ADD COLUMN agency INTEGER;

-- recreate tag_read_event_view
DROP VIEW tag_read_event_view;
CREATE VIEW tag_read_event_view AS
	SELECT event_id, event_date, event_description.description,
	       tag_type.description AS tag_type, agency, tag_id, tag_reader,
	       toll_zone, tollway, hov, trip_id
	FROM event.tag_read_event
	JOIN event.event_description
	ON   tag_read_event.event_desc_id = event_description.event_desc_id
	JOIN event.tag_type
	ON   tag_read_event.tag_type = tag_type.id
	JOIN iris.tag_reader
	ON   tag_read_event.tag_reader = tag_reader.name
	LEFT JOIN iris.toll_zone
	ON        tag_reader.toll_zone = toll_zone.name;
GRANT SELECT ON tag_read_event_view TO PUBLIC;

CREATE TRIGGER tag_read_event_view_update_trig
    INSTEAD OF UPDATE ON tag_read_event_view
    FOR EACH ROW EXECUTE PROCEDURE event.tag_read_event_view_update();




-- migrate-4.29.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.29.0'
	WHERE name = 'database_version';

-- Add tag_reader_dms_view
CREATE VIEW tag_reader_dms_view AS
	SELECT tag_reader, dms
	FROM iris.tag_reader_dms;
GRANT SELECT ON tag_reader_dms_view TO PUBLIC;

-- Add dms_action_view
CREATE VIEW dms_action_view AS
	SELECT name, action_plan, sign_group, phase, quick_message,
	       beacon_enabled, a_priority, r_priority
	FROM iris.dms_action;
GRANT SELECT ON dms_action_view TO PUBLIC;




-- migrate-4.30.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.30.0'
	WHERE name = 'database_version';

-- Add index to tag_read_event
CREATE INDEX ON event.tag_read_event(tag_id);

-- Add indexes to price_message_event
CREATE INDEX ON event.price_message_event(event_date);
CREATE INDEX ON event.price_message_event(device_id);

-- add hidden field to sign_group
ALTER TABLE iris.sign_group ADD COLUMN hidden BOOLEAN;
UPDATE iris.sign_group SET hidden = false;
ALTER TABLE iris.sign_group ALTER COLUMN hidden SET NOT NULL;




-- migrate-4.31.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.31.0'
	WHERE name = 'database_version';

CREATE OR REPLACE VIEW controller_view AS
	SELECT c.name, drop_id, comm_link, cabinet,
	       cnd.description AS condition, notes, cab.geo_loc, fail_time
	FROM iris.controller c
	LEFT JOIN iris.cabinet cab ON c.cabinet = cab.name
	LEFT JOIN iris.condition cnd ON c.condition = cnd.id;
GRANT SELECT ON controller_view TO PUBLIC;




-- migrate-4.32.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.32.0'
	WHERE name = 'database_version';

-- Reserve Control By Web comm protocol value
INSERT INTO iris.comm_protocol (id, description) VALUES (33, 'Control By Web');

-- Expand length of dms_sign_group name column
ALTER TABLE iris.dms_sign_group ALTER COLUMN name TYPE VARCHAR(28);




-- migrate-4.33.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.33.0'
	WHERE name = 'database_version';

-- Reserve incident feed comm protocol value
INSERT INTO iris.comm_protocol (id, description) VALUES (34, 'Incident Feed');

-- add confirmed field to incident
ALTER TABLE event.incident ADD COLUMN confirmed BOOLEAN;
UPDATE event.incident SET confirmed = true;
ALTER TABLE event.incident ALTER COLUMN confirmed SET NOT NULL;

-- add confirmed field to incident_update
ALTER TABLE event.incident_update ADD COLUMN confirmed BOOLEAN;
UPDATE event.incident_update SET confirmed = true;
ALTER TABLE event.incident_update ALTER COLUMN confirmed SET NOT NULL;

-- replace incident_update_trig
CREATE OR REPLACE FUNCTION event.incident_update_trig() RETURNS TRIGGER AS
$incident_update_trig$
BEGIN
    INSERT INTO event.incident_update
               (incident, event_date, impact, cleared, confirmed)
        VALUES (NEW.name, now(), NEW.impact, NEW.cleared, NEW.confirmed);
    RETURN NEW;
END;
$incident_update_trig$ LANGUAGE plpgsql;

-- replace incident_view
DROP VIEW incident_view;
CREATE VIEW incident_view AS
    SELECT iu.event_id, name, iu.event_date, ed.description, road,
           d.direction, iu.impact, iu.cleared, iu.confirmed, camera,
           ln.description AS lane_type, detail, replaces, lat, lon
    FROM event.incident i
    JOIN event.incident_update iu ON i.name = iu.incident
    LEFT JOIN event.event_description ed ON i.event_desc_id = ed.event_desc_id
    LEFT JOIN iris.direction d ON i.dir = d.id
    LEFT JOIN iris.lane_type ln ON i.lane_type = ln.id;
GRANT SELECT ON incident_view TO PUBLIC;




-- migrate-4.34.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.34.0'
	WHERE name = 'database_version';

-- Add verify_pin to beacons
ALTER TABLE iris._beacon ADD COLUMN verify_pin INTEGER;

DROP VIEW beacon_view;
DROP VIEW iris.beacon;
DROP FUNCTION iris.beacon_insert();
DROP FUNCTION iris.beacon_update();
DROP FUNCTION iris.beacon_delete();

CREATE VIEW iris.beacon AS
	SELECT b.name, geo_loc, controller, pin, notes, message, verify_pin,
	       preset
	FROM iris._beacon b
	JOIN iris._device_io d ON b.name = d.name
	JOIN iris._device_preset p ON b.name = p.name;

CREATE FUNCTION iris.beacon_insert() RETURNS TRIGGER AS
	$beacon_insert$
BEGIN
	INSERT INTO iris._device_io (name, controller, pin)
	    VALUES (NEW.name, NEW.controller, NEW.pin);
	INSERT INTO iris._device_preset (name, preset)
	    VALUES (NEW.name, NEW.preset);
	INSERT INTO iris._beacon (name, geo_loc, notes, message, verify_pin)
	    VALUES (NEW.name, NEW.geo_loc, NEW.notes, NEW.message,
	            NEW.verify_pin);
	RETURN NEW;
END;
$beacon_insert$ LANGUAGE plpgsql;

CREATE TRIGGER beacon_insert_trig
    INSTEAD OF INSERT ON iris.beacon
    FOR EACH ROW EXECUTE PROCEDURE iris.beacon_insert();

CREATE FUNCTION iris.beacon_update() RETURNS TRIGGER AS
	$beacon_update$
BEGIN
	UPDATE iris._device_io
	   SET controller = NEW.controller,
	       pin = NEW.pin
	 WHERE name = OLD.name;
	UPDATE iris._device_preset
	   SET preset = NEW.preset
	 WHERE name = OLD.name;
	UPDATE iris._beacon
	   SET geo_loc = NEW.geo_loc,
	       notes = NEW.notes,
	       message = NEW.message,
	       verify_pin = NEW.verify_pin
	 WHERE name = OLD.name;
	RETURN NEW;
END;
$beacon_update$ LANGUAGE plpgsql;

CREATE TRIGGER beacon_update_trig
    INSTEAD OF UPDATE ON iris.beacon
    FOR EACH ROW EXECUTE PROCEDURE iris.beacon_update();

CREATE FUNCTION iris.beacon_delete() RETURNS TRIGGER AS
	$beacon_delete$
BEGIN
	DELETE FROM iris._device_preset WHERE name = OLD.name;
	DELETE FROM iris._device_io WHERE name = OLD.name;
	IF FOUND THEN
		RETURN OLD;
	ELSE
		RETURN NULL;
	END IF;
END;
$beacon_delete$ LANGUAGE plpgsql;

CREATE TRIGGER beacon_delete_trig
    INSTEAD OF DELETE ON iris.beacon
    FOR EACH ROW EXECUTE PROCEDURE iris.beacon_delete();

CREATE VIEW beacon_view AS
	SELECT b.name, b.notes, b.message, p.camera, p.preset_num, b.geo_loc,
	       l.roadway, l.road_dir, l.cross_mod, l.cross_street, l.cross_dir,
	       l.lat, l.lon,
	       b.controller, b.pin, b.verify_pin, ctr.comm_link, ctr.drop_id,
	       ctr.condition
	FROM iris.beacon b
	LEFT JOIN iris.camera_preset p ON b.preset = p.name
	LEFT JOIN geo_loc_view l ON b.geo_loc = l.name
	LEFT JOIN controller_view ctr ON b.controller = ctr.name;
GRANT SELECT ON beacon_view TO PUBLIC;




-- migrate-4.35.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.35.0'
	WHERE name = 'database_version';

-- Add dictionary system attributes
INSERT INTO iris.system_attribute (name, value)
	VALUES ('dict_allowed_scheme', '0');
INSERT INTO iris.system_attribute (name, value)
	VALUES ('dict_banned_scheme', '0');

-- Add dictionary privileges
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d) VALUES
	('prv_dic1', 'policy_admin', 'word(/.*)?', true, true, true, true);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d) VALUES
	('prv_dic2', 'dms_tab', 'word(/.*)?', true, false, false, false);

-- Add word table
CREATE TABLE iris.word (
	name VARCHAR(24) PRIMARY KEY,
	abbr VARCHAR(12),
	allowed BOOLEAN DEFAULT false NOT NULL
);

-- Create incident descriptor table
CREATE TABLE iris.inc_descriptor (
	name VARCHAR(10) PRIMARY KEY,
	sign_group VARCHAR(16) NOT NULL REFERENCES iris.sign_group,
	event_desc_id INTEGER NOT NULL
		REFERENCES event.event_description(event_desc_id),
	lane_type SMALLINT NOT NULL REFERENCES iris.lane_type(id),
	detail VARCHAR(8) REFERENCES event.incident_detail(name),
	cleared BOOLEAN NOT NULL,
	multi VARCHAR(64) NOT NULL
);

CREATE FUNCTION iris.inc_descriptor_ck() RETURNS TRIGGER AS
	$inc_descriptor_ck$
BEGIN
	-- Only incident event IDs are allowed
	IF NEW.event_desc_id < 21 OR NEW.event_desc_id > 24 THEN
		RAISE EXCEPTION 'invalid incident event_desc_id';
	END IF;
	-- Only mainline, cd road, merge and exit lane types are allowed
	IF NEW.lane_type != 1 AND NEW.lane_type != 3 AND
	   NEW.lane_type != 5 AND NEW.lane_type != 7 THEN
		RAISE EXCEPTION 'invalid incident lane_type';
	END IF;
	RETURN NEW;
END;
$inc_descriptor_ck$ LANGUAGE plpgsql;

CREATE TRIGGER inc_descriptor_ck_trig
	BEFORE INSERT OR UPDATE ON iris.inc_descriptor
	FOR EACH ROW EXECUTE PROCEDURE iris.inc_descriptor_ck();

-- Create incident range lookup table
CREATE TABLE iris.inc_range (
	id INTEGER PRIMARY KEY,
	description VARCHAR(10) NOT NULL
);

-- Create incident locator table
CREATE TABLE iris.inc_locator (
	name VARCHAR(10) PRIMARY KEY,
	sign_group VARCHAR(16) NOT NULL REFERENCES iris.sign_group,
	range INTEGER NOT NULL REFERENCES iris.inc_range(id),
	branched BOOLEAN NOT NULL,
	pickable BOOLEAN NOT NULL,
	multi VARCHAR(64) NOT NULL
);

-- Create incident advice table
CREATE TABLE iris.inc_advice (
	name VARCHAR(10) PRIMARY KEY,
	sign_group VARCHAR(16) NOT NULL REFERENCES iris.sign_group,
	range INTEGER NOT NULL REFERENCES iris.inc_range(id),
	lane_type SMALLINT NOT NULL REFERENCES iris.lane_type(id),
	impact VARCHAR(20) NOT NULL,
	cleared BOOLEAN NOT NULL,
	multi VARCHAR(64) NOT NULL
);

CREATE FUNCTION iris.inc_advice_ck() RETURNS TRIGGER AS
	$inc_advice_ck$
BEGIN
	-- Only mainline, cd road, merge and exit lane types are allowed
	IF NEW.lane_type != 1 AND NEW.lane_type != 3 AND
	   NEW.lane_type != 5 AND NEW.lane_type != 7 THEN
		RAISE EXCEPTION 'invalid incident lane_type';
	END IF;
	RETURN NEW;
END;
$inc_advice_ck$ LANGUAGE plpgsql;

CREATE TRIGGER inc_advice_ck_trig
	BEFORE INSERT OR UPDATE ON iris.inc_advice
	FOR EACH ROW EXECUTE PROCEDURE iris.inc_advice_ck();

-- Add incident control privileges
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d) VALUES ('prv_inc1', 'incident_control', 'inc_descriptor(/.*)?',
	true, false, false, false);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d) VALUES ('prv_inc2', 'incident_control', 'inc_locator(/.*)?',
	true, false, false, false);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d) VALUES ('prv_inc3', 'incident_control', 'inc_advice(/.*)?',
	true, false, false, false);

-- Add incident policy privileges
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d) VALUES ('prv_inc4', 'policy_admin', 'inc_descriptor(/.*)?',
	false, true, true, true);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d) VALUES ('prv_inc5', 'policy_admin', 'inc_locator(/.*)?',
	false, true, true, true);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
	priv_d) VALUES ('prv_inc6', 'policy_admin', 'inc_advice(/.*)?',
	false, true, true, true);

-- Populate incident range lookup table
COPY iris.inc_range (id, description) FROM stdin;
0	near
1	middle
2	far
\.

-- Add incident to sign_message
ALTER TABLE iris.sign_message ADD COLUMN incident VARCHAR(16);




-- ============================================================================
-- BEGIN 10.2 changes
-- ============================================================================

-- feature 525
INSERT INTO iris.camera_preset_alias_name (id, alias) VALUES (1, 'Night-shift Home');

INSERT INTO iris.system_attribute(name, value) VALUES ('camera_shift_concur_move', 0);
INSERT INTO iris.system_attribute(name, value) VALUES ('camera_shift_move_pause', 5);
INSERT INTO iris.system_attribute(name, value) VALUES ('camera_shift_reinit', 'false');
INSERT INTO iris.system_attribute(name, value) VALUES ('camera_shift_sunrise_offset', -30);
INSERT INTO iris.system_attribute(name, value) VALUES ('camera_shift_sunset_offset', -30);

ALTER TABLE iris._camera ADD COLUMN shift_schedule INTEGER;

CREATE OR REPLACE VIEW iris.camera AS SELECT
	c.name, geo_loc, controller, pin, notes, encoder, encoder_channel,
		encoder_type, publish, shift_schedule
	FROM iris._camera c JOIN iris._device_io d ON c.name = d.name;

CREATE OR REPLACE FUNCTION iris.camera_insert() RETURNS TRIGGER AS
	$camera_insert$
BEGIN
	INSERT INTO iris._device_io (name, controller, pin)
	     VALUES (NEW.name, NEW.controller, NEW.pin);
	INSERT INTO iris._camera (name, geo_loc, notes, encoder,
	                          encoder_channel, encoder_type, publish, shift_schedule)
	     VALUES (NEW.name, NEW.geo_loc, NEW.notes, NEW.encoder,
	             NEW.encoder_channel, NEW.encoder_type, NEW.publish, NEW.shift_schedule);
	RETURN NEW;
END;
$camera_insert$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION iris.camera_update() RETURNS TRIGGER AS
	$camera_update$
BEGIN
	UPDATE iris._device_io
	   SET controller = NEW.controller,
	       pin = NEW.pin
	 WHERE name = OLD.name;
	UPDATE iris._camera
	   SET geo_loc = NEW.geo_loc,
	       notes = NEW.notes,
	       encoder = NEW.encoder,
	       encoder_channel = NEW.encoder_channel,
	       encoder_type = NEW.encoder_type,
	       publish = NEW.publish,
	       shift_schedule = NEW.shift_schedule
	 WHERE name = OLD.name;
	RETURN NEW;
END;
$camera_update$ LANGUAGE plpgsql;

-- feature 571
INSERT INTO iris.system_attribute(name, value) VALUES ('dms_notify_needs_attention', 'false');

-- feature 592
-- detector_event history table
-- the detector_event table, at least in D10 gets quite large, really fast.
-- this table keeps values without referential integrity to speed up insertion
CREATE TABLE IF NOT EXISTS event.detector_event_hist (
-- event_id INTEGER DEFAULT nextval('event.event_id_seq') NOT NULL,
  event_id INTEGER NOT NULL,
	event_date TIMESTAMP with time zone NOT NULL,
--	event_desc_id INTEGER NOT NULL REFERENCES event.event_description(event_desc_id),
	event_desc_id INTEGER NOT NULL,
--	device_id VARCHAR(10) REFERENCES iris._detector(name) ON DELETE CASCADE
	device_id VARCHAR(10)
);
-- ALTER TABLE event.detector_event_hist OWNER TO tms;

-- add a single record to the history table, so that something is there for later
-- MAX calls
INSERT INTO event.detector_event_hist (event_id, event_date, event_desc_id, device_id)
  SELECT event_id, event_date, event_desc_id, device_id
  FROM event.detector_event
  WHERE event_id = (SELECT MIN(event_id) FROM event.detector_event);

INSERT INTO iris.system_attribute (name, value) VALUES ('detector_reduce_malf_logging', 'false');



