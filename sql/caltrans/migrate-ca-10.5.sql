-- current as of MnDOT 4.35.4


-- updates required before rest of updates
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

-- updates CA-only protocols (that haven't been given to MnDOT)

-- ADDITIONS for CA-IRIS v10.5 go at the bottom




-- ============================================================================
-- BEGIN: MnDOT updates
-- ============================================================================



-- ============================================================================
-- BEGIN 10.5 changes
-- ============================================================================

-- feature 631 dms plan control / allowed
ALTER TABLE iris._dms
ADD COLUMN plan_allowed BOOLEAN DEFAULT false,
ADD COLUMN plan_controlled BOOLEAN DEFAULT false;

