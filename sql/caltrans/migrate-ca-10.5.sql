-- current as of MnDOT 4.35.4
-- this script should be run with iris not running

\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute
    SET value = regexp_replace(value, '10\.4\.\d+.*\s+\[', '10.4.3 [')
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
