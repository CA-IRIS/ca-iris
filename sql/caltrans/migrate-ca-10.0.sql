-- CA-IRIS v10.0 is a port of IRIS v4.22
-- TODO: document upgrade sequence from CA-IRIS v9.x -> v10.0

-- migrate-axisjpeg-encoder.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.encoder_type VALUES (7, 'Axis JPEG');


-- migrate-ca-rwis.patch
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.comm_protocol (id, description) VALUES (30, 'CA RWIS');

-- migrate-dms-reinit-detect.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dmsxml_reinit_detect', false);
INSERT INTO iris.system_attribute (name, value) VALUES ('email_recipient_dmsxml_reinit', '');


-- migrate-aws.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dms_aws_msg_file_url', 'http://iris/irisaws.txt');
INSERT INTO iris.system_attribute (name, value) VALUES ('dms_aws_user_name', 'IRISAWS');


-- migrate-axisptz.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('camera_ptz_axis_comport', 1);
INSERT INTO iris.system_attribute (name, value) VALUES ('camera_ptz_axis_reset', '');
INSERT INTO iris.system_attribute (name, value) VALUES ('camera_ptz_axis_wipe', '');

-- migrate-cctv-auto-stop.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('camera_stream_duration_secs', 0);


-- migrate-cctv-return-home.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('camera_ptz_return_home', false);

-- migrate-composer-trim-an-preview.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dms_composer_trim', true);
INSERT INTO iris.system_attribute (name, value) VALUES ('dms_preview_instant', false);

-- migrate-composer-uppercase.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dms_composer_uppercase', false);


-- migrate-geoloc-enhance.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

-- sysattrs
INSERT INTO iris.system_attribute (name, value) VALUES ('camera_manager_show_location', true);
INSERT INTO iris.system_attribute (name, value) VALUES ('dms_manager_show_location', true);
INSERT INTO iris.system_attribute (name, value) VALUES ('dms_manager_show_owner', true);
INSERT INTO iris.system_attribute (name, value) VALUES ('location_format', '');
INSERT INTO iris.system_attribute (name, value) VALUES ('dms_sort', 0);
INSERT INTO iris.system_attribute (name, value) VALUES ('camera_sort', 0);
INSERT INTO iris.system_attribute (name, value) VALUES ('rwis_sort', 0);

-- table containing extended site data for geo_loc entities
-- ideally, geo_loc would also have "REFERENCES iris.geo_loc(name)", but we're
-- not doing this right now to avoid needing a cascade delete rule or similar.
-- This means that entries in this table will persist even when geolocs are
-- deleted, but that's fine for now.  This whole feature will need to be
-- redesigned somewhat to merge with MnDOT anyway.
CREATE TABLE iris.site_data (
	name VARCHAR(8) PRIMARY KEY,
	geo_loc VARCHAR(20) UNIQUE NOT NULL,
	county VARCHAR(24),
	site_name VARCHAR(32) UNIQUE,
	format VARCHAR(128)
);

-- add privs
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c, priv_d)
	VALUES ('prv_sd1', 'login', 'site_data(/.*)?', true, false, false, false);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c, priv_d)
	VALUES ('prv_sd2', 'device_admin', 'site_data/.*', false, true,  true,  true);


-- migrate-preset-aliases.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

-- table containing allowed preset alias names
CREATE TABLE iris.camera_preset_alias_name (
	id integer PRIMARY KEY,
	alias VARCHAR(20) NOT NULL
);
COPY iris.camera_preset_alias_name (id, alias) FROM stdin;
0	Home
\.

-- table containing current alias:preset# mappings for each camera
CREATE TABLE iris.camera_preset_alias (
	name VARCHAR(10) PRIMARY KEY,
	camera VARCHAR(10) NOT NULL REFERENCES iris._camera,
	alias INTEGER NOT NULL REFERENCES iris.camera_preset_alias_name,
	preset_num INTEGER NOT NULL CHECK (preset_num > 0 AND preset_num <= 12),
	UNIQUE(camera, alias)
);

-- add privileges to camera_tab and device_admin capabilities
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c, priv_d)
	VALUES ('prv_cpa1', 'camera_tab',   'camera_preset_alias(/.*)?', true,  false, false, false);
INSERT INTO iris.privilege (name, capability, pattern, priv_r, priv_w, priv_c, priv_d)
	VALUES ('prv_cpa2', 'device_admin', 'camera_preset_alias/.*',    false, true,  true,  true);


-- migrate-quickmsg-uppercase-names.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dms_quickmsg_uppercase_names', false);

-- migrate-rtms.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('rtms_read_margin_sec', 5);

-- migrate-rwis-ui.patch (sql)
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.capability (name, enabled) VALUES ('weather_tab', true);

INSERT INTO iris.privilege
	(name, capability, pattern, priv_r, priv_w, priv_c, priv_d)
	VALUES
	('prv_ws1', 'weather_tab', 'weather_sensor(/.*)?',
	true, false, false, false);

INSERT INTO iris.role_capability (role, capability)
	VALUES ('administrator', 'weather_tab');

INSERT INTO iris.role_capability (role, capability)
	VALUES ('operator', 'weather_tab');

-- migrate-switchserver.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

CREATE SCHEMA video;
ALTER SCHEMA video OWNER TO tms;
CREATE TABLE video.decoder_map (
	did VARCHAR(64) NOT NULL,
	cid VARCHAR(64) NOT NULL
	);

-- migrate-urms.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('urms_read_margin_sec', 5);

-- migrate-wizard.sql
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('wizard_read_margin_sec', 5);

