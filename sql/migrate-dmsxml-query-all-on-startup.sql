\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dmsxml_query_all_on_startup', false);