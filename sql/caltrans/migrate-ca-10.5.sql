-- current as of MnDOT 4.35.4

-- see CAIRISINT-47/TT622
\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dms_querymsg_after_send_new_msg', false);

UPDATE iris.system_attribute
    SET value = regexp_replace(value, '10\.4\.\d+.*\s+\[', '10.4.1 [')
    WHERE NAME = 'window_title';
