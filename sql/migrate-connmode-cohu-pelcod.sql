\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('camera_cohu_conn_mode', 0);
INSERT INTO iris.system_attribute (name, value) VALUES ('camera_cohu_max_idle', 30);
INSERT INTO iris.system_attribute (name, value) VALUES ('camera_pelcod_conn_mode', 0);
INSERT INTO iris.system_attribute (name, value) VALUES ('camera_pelcod_max_idle', 30);

