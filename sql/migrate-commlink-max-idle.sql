\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

-- add new idle_secs column and set default to 2^31 (max signed 32-bit int)
ALTER TABLE iris.comm_link ADD COLUMN idle_secs INTEGER NOT NULL DEFAULT 2147483647;
UPDATE iris.comm_link SET idle_secs = 2147483647;

-- NOTE: Most if not all of the following will be irrelevant to non-CA IRIS. Remove before pushing to MN.

-- all previously per-op commands being updated to per-op equivalent idle_secs
UPDATE iris.comm_link
SET idle_secs = 0
FROM iris.comm_protocol
WHERE protocol = id
AND iris.comm_protocol.description IN ('Axis PTZ', 'Sensys');


-- update comm links related to PelcoD protocol
UPDATE iris.comm_link
SET idle_secs = (SELECT CASE
    WHEN (SELECT value::INTEGER FROM iris.system_attribute WHERE name = 'camera_pelcod_conn_mode') = 1
      THEN (SELECT value::INTEGER FROM iris.system_attribute WHERE name = 'camera_pelcod_max_idle')
    WHEN (SELECT value::INTEGER FROM iris.system_attribute WHERE name = 'camera_pelcod_conn_mode') = 2
      THEN 0
    ELSE 2147483647
  END)
FROM iris.comm_protocol
WHERE iris.comm_protocol.description = 'Pelco D PTZ';


-- update comm links related to Cohu protocol
UPDATE iris.comm_link
SET idle_secs = (SELECT CASE
    WHEN (SELECT value::INTEGER FROM iris.system_attribute WHERE name = 'camera_cohu_conn_mode') = 1
      THEN (SELECT value::INTEGER FROM iris.system_attribute WHERE name = 'camera_cohu_max_idle')
    WHEN (SELECT value::INTEGER FROM iris.system_attribute WHERE name = 'camera_cohu_conn_mode') = 2
      THEN 0
    ELSE 2147483647
  END)
FROM iris.comm_protocol
WHERE iris.comm_protocol.description = 'Cohu PTZ';


-- cleanup unused system attributes
DELETE FROM iris.system_attribute
WHERE name IN ('camera_pelcod_conn_mode', 'camera_pelcod_max_idle', 'camera_cohu_conn_mode', 'camera_cohu_max_idle');

