-- current as of MnDOT 4.35.4


-- updates required before rest of updates
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

-- updates CA-only protocols (that haven't been given to MnDOT)

-- ADDITIONS for CA-IRIS v10.4 go at the bottom




-- ============================================================================
-- BEGIN: MnDOT updates
-- ============================================================================



-- ============================================================================
-- BEGIN 10.4 changes
-- ============================================================================

-- feature 599 onvif profile s
INSERT INTO iris.comm_protocol VALUES (37, 'ONVIF PTZ');

ALTER TABLE iris.controller ADD COLUMN username VARCHAR(16);

UPDATE iris.system_attribute
    SET value = regexp_replace(value, '10\.3\.\d+.*\s+\[', '10.4.0 [')
    WHERE NAME = 'window_title';
