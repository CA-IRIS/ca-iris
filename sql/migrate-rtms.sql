\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('rtms_read_margin_sec', 5);

