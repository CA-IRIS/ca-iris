-- current as of MnDOT 4.35.4


-- updates required before rest of updates
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

-- updates CA-only protocols (that haven't been given to MnDOT)

-- ADDITIONS for CA-IRIS v10.3 go at the bottom




-- ============================================================================
-- BEGIN: MnDOT updates
-- ============================================================================



-- ============================================================================
-- BEGIN 10.3 changes
-- ============================================================================

-- feature 587 travel time
INSERT INTO iris.system_attribute(name, value) VALUES ('route_max_link_miles', 0.6);

INSERT INTO iris.system_attribute (name, value) VALUES ('system_min_password_length', 8);
