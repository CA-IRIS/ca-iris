\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

INSERT INTO iris.system_attribute (name, value) VALUES ('dms_aws_msg_file_url', 'http://iris/irisaws.txt');
INSERT INTO iris.system_attribute (name, value) VALUES ('dms_aws_user_name', 'IRISAWS');

