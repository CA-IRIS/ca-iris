-- current as of MnDOT 4.35.4

\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

--INSERT INTO iris.system_attribute (name, value) VALUES ('dms_querymsg_after_send_new_msg', false);

UPDATE iris.system_attribute
    SET value = regexp_replace(value, '10\.5\.\d+.*\s+\[', '10.5 [')
    WHERE NAME = 'window_title';

-- CAIRISINT48/TT623

-- update sign_message table
-- adjust SCHEDULED message priority to temp
UPDATE iris.sign_message
	SET a_priority = 15
	WHERE a_priority = 6;;

UPDATE iris.sign_message
	SET r_priority = 15
	WHERE r_priority = 6;

-- decrement previously higher priorities
UPDATE iris.sign_message
	SET a_priority = a_priority-1
	WHERE a_priority > 6 AND a_priority < 14;

UPDATE iris.sign_message
	SET r_priority = r_priority-1
	WHERE r_priority > 6 AND r_priority < 14;

-- adjust SCHEDULED from temp to 13
UPDATE iris.sign_message
	SET a_priority = 13
	WHERE a_priority = 15;

UPDATE iris.sign_message
	SET r_priority = 13
	WHERE r_priority = 15;

-- repeat for dms.action
-- adjust SCHEDULED message priority to temp
UPDATE iris.dms_action
	SET a_priority = 15
	WHERE a_priority = 6;;

UPDATE iris.dms_action
	SET r_priority = 15
	WHERE r_priority = 6;

-- decrement previously higher priorities
UPDATE iris.dms_action
	SET a_priority = a_priority-1
	WHERE a_priority > 6 AND a_priority < 14;

UPDATE iris.dms_action
	SET r_priority = r_priority-1
	WHERE r_priority > 6 AND r_priority < 14;

-- adjust SCHEDULED from temp to 13
UPDATE iris.dms_action
	SET a_priority = 13
	WHERE a_priority = 15;

UPDATE iris.dms_action
	SET r_priority = 13
	WHERE r_priority = 15;


-- 10.5: Add Trucks and Vehicles into Lane Use Indications
INSERT INTO iris.lane_use_indication ("id", description) VALUES (15, 'Trucks');
INSERT INTO iris.lane_use_indication ("id", description) VALUES (16, 'Vehicles');

-- 10.5: Make msg_num in Lane Use Multi NOT UNQIUE
DROP INDEX iris.lane_use_multi_msg_num_idx;


-- 10.5: Alter DMS Table to add plan_controlled
ALTER TABLE iris._dms ADD COLUMN plan_controlled BOOLEAN;


-- 10.5: Alter DMS View to include plan_controlled
CREATE OR REPLACE VIEW iris.dms AS
	SELECT dms.name, geo_loc, controller, pin, notes, beacon, preset,
	       aws_allowed, aws_controlled, default_font, plan_controlled
	FROM iris._dms dms
	JOIN iris._device_io d ON dms.name = d.name
	JOIN iris._device_preset p ON dms.name = p.name;


-- 10.5: DROP and CREATE FUNCTION insert() for DMS to include plan_controlled
CREATE OR REPLACE FUNCTION iris.dms_insert() RETURNS TRIGGER AS
	$dms_insert$
BEGIN
	INSERT INTO iris._device_io (name, controller, pin)
	     VALUES (NEW.name, NEW.controller, NEW.pin);
	INSERT INTO iris._device_preset (name, preset)
	     VALUES (NEW.name, NEW.preset);
	INSERT INTO iris._dms (name, geo_loc, notes, beacon, aws_allowed,
	                       aws_controlled, default_font, plan_controlled)
	     VALUES (NEW.name, NEW.geo_loc, NEW.notes, NEW.beacon,
	             NEW.aws_allowed, NEW.aws_controlled, NEW.default_font,
	             NEW.plan_controlled);
	RETURN NEW;
END;
$dms_insert$ LANGUAGE plpgsql;

DROP TRIGGER dms_insert_trig on iris.dms;

CREATE TRIGGER dms_insert_trig
    INSTEAD OF INSERT ON iris.dms
    FOR EACH ROW EXECUTE PROCEDURE iris.dms_insert();


-- 10.5: DROP and CREATE FUNCTION update() for DMS to include plan_controlled
CREATE OR REPLACE FUNCTION iris.dms_update() RETURNS TRIGGER AS
	$dms_update$
BEGIN
	UPDATE iris._device_io
	   SET controller = NEW.controller,
	       pin = NEW.pin
	 WHERE name = OLD.name;
	UPDATE iris._device_preset
	   SET preset = NEW.preset
	 WHERE name = OLD.name;
	UPDATE iris._dms
	   SET geo_loc = NEW.geo_loc,
	       notes = NEW.notes,
	       beacon = NEW.beacon,
	       aws_allowed = NEW.aws_allowed,
	       aws_controlled = NEW.aws_controlled,
	       default_font = NEW.default_font,
	       plan_controlled = NEW.plan_controlled
	 WHERE name = OLD.name;
	RETURN NEW;
END;
$dms_update$ LANGUAGE plpgsql;

DROP TRIGGER dms_update_trig on iris.dms;

CREATE TRIGGER dms_update_trig
    INSTEAD OF UPDATE ON iris.dms
    FOR EACH ROW EXECUTE PROCEDURE iris.dms_update();