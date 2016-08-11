\set ON_ERROR_STOP

-- This is an SQL migration script for updating CA-IRIS v10.0 to v10.1
-- Due to differences, and some additions that are not present in the
-- MnDOT IRIS database schema, these will be done here.
-- CA-IRIS v10.0 is a port of MnDOT IRIS v4.22, as such, the contents of
-- the following SQL scripts have been copied into this script for ease of
-- use and editing for future reference.

-- migrate-4.23.sql
-- migrate-4.24.sql
-- migrate-4.25.sql
-- migrate-4.26.sql (modified)

SET SESSION AUTHORIZATION 'tms';

-- ========================================================================= --
-- BEGIN: MnDOT 4.23 migration script
-- ========================================================================= --
UPDATE iris.system_attribute SET value = '4.23.0'
WHERE name = 'database_version';

-- add camera wiper system attribute
INSERT INTO iris.system_attribute (name, value)
VALUES ('camera_wiper_precip_mm_hr', 8);

-- delete kml file enable system attribute
DELETE FROM iris.system_attribute WHERE name = 'kml_file_enable';
-- ========================================================================= --
-- END: MnDOT 4.23 migration script
-- ========================================================================= --

-- ========================================================================= --
-- BEGIN: MnDOT 4.24 migration script
-- ========================================================================= --
UPDATE iris.system_attribute SET value = '4.24.0'
WHERE name = 'database_version';

-- add system attributes
INSERT INTO iris.system_attribute (name, value)
VALUES ('map_extent_name_initial', 'Home');
INSERT INTO iris.system_attribute (name, value)
VALUES ('speed_limit_min_mph', '45');
INSERT INTO iris.system_attribute (name, value)
VALUES ('speed_limit_default_mph', '55');
INSERT INTO iris.system_attribute (name, value)
VALUES ('speed_limit_max_mph', '75');

-- increase size of sign_message multi
ALTER TABLE iris.sign_message ALTER COLUMN multi TYPE VARCHAR(512);

-- add toll zone table
CREATE TABLE iris.toll_zone (
  name VARCHAR(20) PRIMARY KEY,
  start_id VARCHAR(10) REFERENCES iris.r_node(station_id),
  end_id VARCHAR(10) REFERENCES iris.r_node(station_id)
);

-- add toll zone view
CREATE VIEW toll_zone_view AS
  SELECT name, start_id, end_id
  FROM iris.toll_zone;
GRANT SELECT ON toll_zone_view TO PUBLIC;

-- add privileges for toll zones
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
                            priv_d)
VALUES ('prv_tz1', 'detection', 'toll_zone(/.*)?', true, false,
        false, false);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c,
                            priv_d)
VALUES ('prv_tz2', 'device_admin', 'toll_zone/.*', false, true,
        true, true);
-- ========================================================================= --
-- END: MnDOT 4.24 migration script
-- ========================================================================= --

-- ========================================================================= --
-- BEGIN: CALTRANS 4.24.1-ca migration script
-- sql to handle CA-only values that would conflict with next MnDOT script
-- ========================================================================= --
-- reassigned 'CA RWIS' to new id (to prevent conflict with MnDOT)
INSERT INTO iris.comm_protocol (id, description) VALUES (33, 'CA RWIS');
UPDATE iris.comm_link SET protocol = 33 WHERE protocol = 30;
DELETE FROM iris.comm_protocol WHERE id = 30;
-- ========================================================================= --
-- END: CALTRANS 4.24.1-ca migration script
-- ========================================================================= --


-- ========================================================================= --
-- BEGIN: MnDOT 4.25 migration script
-- ========================================================================= --
UPDATE iris.system_attribute SET value = '4.25.0'
WHERE name = 'database_version';

-- Reserve DR-500 comm protocol value
INSERT INTO iris.comm_protocol (id, description) VALUES (30, 'DR-500');

-- Reserve ADDCO comm protocol value
INSERT INTO iris.comm_protocol (id, description) VALUES (31, 'ADDCO');

-- Rename travel_time_max_legs to route_max_legs
UPDATE iris.system_attribute SET name = 'route_max_legs'
WHERE name = 'travel_time_max_legs';

-- Rename travel_time_max_miles to route_max_miles
UPDATE iris.system_attribute SET name = 'route_max_miles'
WHERE name = 'travel_time_max_miles';
-- ========================================================================= --
-- END: MnDOT 4.25 migration script
-- ========================================================================= --

-- ========================================================================= --
-- BEGIN: MnDOT 4.26 migration script (MODIFIED)
-- ========================================================================= --
UPDATE iris.system_attribute SET value = '4.26.0'
WHERE name = 'database_version';

-- Reserve Transcore E6 comm protocol value
INSERT INTO iris.comm_protocol (id, description) VALUES (32, 'TransCore E6');

-- add tag types
CREATE TABLE event.tag_type (
  id INTEGER PRIMARY KEY,
  description VARCHAR(16) NOT NULL
);

-- add tag read events
CREATE TABLE event.tag_read_event (
  event_id SERIAL PRIMARY KEY,
  event_date timestamp WITH time zone NOT NULL,
  event_desc_id INTEGER NOT NULL
    REFERENCES event.event_description(event_desc_id),
  tag_type INTEGER NOT NULL REFERENCES event.tag_type,
  tag_id INTEGER NOT NULL,
  tag_reader VARCHAR(10) NOT NULL,
  toll_zone VARCHAR(20) REFERENCES iris.toll_zone
  ON DELETE SET NULL,
  tollway VARCHAR(16) NOT NULL,
  hov BOOLEAN NOT NULL,
  trip_id INTEGER
);

-- add tag_read_event_view
CREATE VIEW tag_read_event_view AS
  SELECT event_id, event_date, event_description.description,
    tag_type.description AS tag_type, tag_id, tag_reader, toll_zone,
    tollway, hov, trip_id
  FROM event.tag_read_event
    JOIN event.event_description
      ON   tag_read_event.event_desc_id = event_description.event_desc_id
    JOIN event.tag_type
      ON   tag_read_event.tag_type = tag_type.id;
GRANT SELECT ON tag_read_event_view TO PUBLIC;

-- added tag read event descriptions
INSERT INTO event.event_description (event_desc_id, description)
VALUES (601, 'Tag Read');

-- populate tag type LUT
COPY event.tag_type (id, description) FROM stdin;
0	ASTMv6
1	SeGo
\.

-- add Axis JPEG encoder type
-- The following is already present in CA installations, so don't bother
-- INSERT INTO iris.encoder_type VALUES (7, 'Axis JPEG');

-- add dmsxml reinit system attributes
INSERT INTO iris.system_attribute (name, value)
VALUES ('dmsxml_reinit_detect', false);
INSERT INTO iris.system_attribute (name, value)
VALUES ('email_recipient_dmsxml_reinit', '');
-- ========================================================================= --
-- END: MnDOT 4.26 migration script
-- ========================================================================= --


-- ========================================================================= --
-- BEGIN: CA-IRIS v10.1
-- CA-IRIS v10.1 is a port of the MnDOT 4.26 database, so some changes are
-- required
-- ========================================================================= --
UPDATE iris.system_attribute SET value = '4.26.1'
WHERE name = 'database_version';

-- CA trac 574
UPDATE iris.encoder_type
SET description = 'Generic URL'
WHERE id = (SELECT id FROM iris.encoder_type WHERE description = 'Generic MMS');

-- CA trac 511
INSERT INTO iris.system_attribute (name, value)
VALUES ('dmsxml_query_all_on_startup', false);

-- CA trac 318
INSERT INTO iris.system_attribute(name, value)
VALUES('system_protected_user_role','administrator');

-- CA trac 446
ALTER TABLE iris.map_extent ADD COLUMN position INTEGER NOT NULL DEFAULT 0;

-- Need block level to declare and update variables
-- So we create a temp function for this purpose then immediately drop the function
CREATE FUNCTION iris.migrate_extents() RETURNS VOID AS $$
DECLARE
    idx INTEGER DEFAULT 0;
    extent VARCHAR(20);
    BEGIN
        FOR extent IN SELECT name FROM iris.map_extent
                      ORDER BY name::bytea LOOP
            UPDATE iris.map_extent
            SET position = idx
            WHERE name = extent;
            idx := idx + 1;
        END LOOP;
        RETURN;
    END;
$$ LANGUAGE plpgsql;
SELECT iris.migrate_extents();
DROP FUNCTION iris.migrate_extents();

-- now we can enforce uniqueness & drop default value
ALTER TABLE iris.map_extent
  ADD UNIQUE (position);
ALTER TABLE iris.map_extent
  ALTER COLUMN position DROP DEFAULT;

-- CA trac 504
INSERT INTO iris.system_attribute(name, value)
VALUES('camera_direction_override','');

-- CA trac 476
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_color_high', 'FF0000');
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_color_low', '00FFFF');
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_color_mid', 'FFC800');
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_high_air_temp_c', 32.0);
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_high_precip_rate_mmh', 50);
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_high_visibility_distance_m', 3000);
-- value already present
-- INSERT INTO iris.system_attribute(name, value)
-- VALUES('rwis_high_wind_speed_kph', 40);
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_low_air_temp_c', 0.0);
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_low_precip_rate_mmh', 5);
-- value already present
-- INSERT INTO iris.system_attribute(name, value)
-- VALUES('rwis_low_visibility_distance_m', 152);
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_low_wind_speed_kph', 5);
-- value already present
-- INSERT INTO iris.system_attribute(name, value)
-- VALUES('rwis_max_valid_wind_speed_kph', 282);

INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_opacity_percentage', 30);
INSERT INTO iris.system_attribute(name, value)
VALUES('rwis_measurement_radius', 16093.44);

-- CA trac 578
-- add new idle_secs column and set default to 2^31 (max signed 32-bit int)
ALTER TABLE iris.comm_link
  ADD COLUMN idle_secs INTEGER NOT NULL DEFAULT 2147483647;
UPDATE iris.comm_link SET idle_secs = 2147483647;

-- NOTE: Most if not all of the following will be irrelevant to non-CA IRIS.
-- Remove before pushing to MN.

-- all previously per-op commands being updated to per-op equivalent idle_secs
UPDATE iris.comm_link
SET idle_secs = 0
FROM iris.comm_protocol
WHERE protocol = id
      AND iris.comm_protocol.description IN ('Axis PTZ', 'Sensys');


-- update comm links related to PelcoD protocol
UPDATE iris.comm_link
SET idle_secs = (SELECT CASE
    WHEN (SELECT value::INTEGER
          FROM iris.system_attribute
          WHERE name = 'camera_pelcod_conn_mode') = 1
      THEN (SELECT value::INTEGER
            FROM iris.system_attribute
            WHERE name = 'camera_pelcod_max_idle')
    WHEN (SELECT value::INTEGER
          FROM iris.system_attribute
          WHERE name = 'camera_pelcod_conn_mode') = 2
      THEN 0
    ELSE 2147483647
  END)
FROM iris.comm_protocol
WHERE iris.comm_protocol.description = 'Pelco D PTZ';


-- update comm links related to Cohu protocol
UPDATE iris.comm_link
SET idle_secs = (SELECT CASE
    WHEN (SELECT value::INTEGER
          FROM iris.system_attribute
          WHERE name = 'camera_cohu_conn_mode') = 1
      THEN (SELECT value::INTEGER
            FROM iris.system_attribute
            WHERE name = 'camera_cohu_max_idle')
    WHEN (SELECT value::INTEGER
          FROM iris.system_attribute
          WHERE name = 'camera_cohu_conn_mode') = 2
      THEN 0
    ELSE 2147483647
  END)
FROM iris.comm_protocol
WHERE iris.comm_protocol.description = 'Cohu PTZ';


-- cleanup unused system attributes
DELETE FROM iris.system_attribute
WHERE name IN ('camera_pelcod_conn_mode', 'camera_pelcod_max_idle',
               'camera_cohu_conn_mode', 'camera_cohu_max_idle');

-- CA trac 528
INSERT INTO iris.comm_protocol (id, description) VALUES(34, 'TTIP DMS');

-- update window title / version
UPDATE iris.system_attribute SET value = 'CA-IRIS 10.1.0-rc1 [DX]:'
WHERE name = 'window_title';

-- CA trac 401 clean-up
DELETE FROM iris.system_attribute
WHERE name IN ('dms_reinit_detect', 'email_recipient_reinit');
-- ========================================================================= --
-- END: CA-IRIS v10.1
-- ========================================================================= --

-- ========================================================================= --
-- BEGIN: CA-IRIS v10.1.0-rc8
-- ========================================================================= --

-- Axis cameras need this field set to zero.
update iris.comm_link set idle_secs = 0 where protocol = 27;
-- ========================================================================= --
-- END: CA-IRIS v10.1.0-rc8
-- ========================================================================= --
