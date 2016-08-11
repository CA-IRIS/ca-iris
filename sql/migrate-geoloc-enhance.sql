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

