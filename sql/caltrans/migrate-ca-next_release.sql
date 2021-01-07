-- current as of MnDOT 4.35.4

\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

-- TODO Update version below.
UPDATE iris.system_attribute
    SET value = regexp_replace(value, '10\.5\.\d+.*\s+\[', '11.0 [')
    WHERE NAME = 'window_title';
