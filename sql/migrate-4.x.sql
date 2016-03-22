\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE iris.system_attribute SET value = '4.XX.X' -- change me
	WHERE name = 'database_version';

-- CA trac 318
INSERT INTO iris.system_attribute(name, value) VALUES('system_protected_user_role','administrator');

-- CA trac 504
INSERT INTO iris.system_attribute(name, value) VALUES('camera_direction_override','');

-- CA trac 476
INSERT INTO iris.system_attributes(name, value) VALUES('rwis_color_high', 'FF0000')
INSERT INTO iris.system_attributes(name, value) VALUES('rwis_color_low', '00FFFF')
INSERT INTO iris.system_attributes(name, value) VALUES('rwis_color_mid', 'FFC800')
INSERT INTO iris.system_attributes(name, value) VALUES('rwis_high_air_temp_c')
INSERT INTO iris.system_attributes(name, value) VALUES('rwis_high_precip_rate_mmh')
INSERT INTO iris.system_attributes(name, value) VALUES('rwis_high_visibility_distance_m')
INSERT INTO iris.system_attributes(name, value) VALUES('rwis_low_air_temp_c')
INSERT INTO iris.system_attributes(name, value) VALUES('rwis_low_precip_rate_mmh')
INSERT INTO iris.system_attributes(name, value) VALUES('rwis_low_wind_speed_kph')-- CA trac 528
INSERT INTO iris.comm_protocol (id, description) VALUES(34, 'TTIP DMS');

