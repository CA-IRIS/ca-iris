\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dms_composer_trim', true);
INSERT INTO iris.system_attribute (name, value) VALUES ('dms_preview_instant', false);

