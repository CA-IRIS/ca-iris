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

