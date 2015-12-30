-- -----------------------------------------------------------------------------
-- OpenClincia 2.0 changes
-- -----------------------------------------------------------------------------
ALTER TABLE study DROP COLUMN collect_dob;
ALTER TABLE study DROP COLUMN discrepancy_management;

-- Table: study_parameter

-- DROP TABLE study_parameter;

CREATE TABLE study_parameter
(
  study_parameter_id serial NOT NULL,
  handle varchar(50),
  name varchar(50),
  description varchar(255),
  default_value varchar(50),
  inheritable bool DEFAULT true,
  overridable bool,
  CONSTRAINT study_parameter_pkey PRIMARY KEY (study_parameter_id),
  CONSTRAINT study_parameter_handle_key UNIQUE (handle)
) 
WITH OIDS;
ALTER TABLE study_parameter OWNER TO clinica;

-- Table: study_parameter_value

-- DROP TABLE study_parameter_value;

CREATE TABLE study_parameter_value
(
  study_parameter_value_id serial NOT NULL,
  study_id int4,
  value varchar(50),
  parameter varchar(50),
  CONSTRAINT study_parameter_value_parameter_fkey FOREIGN KEY (parameter) REFERENCES study_parameter (handle) ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT study_parameter_value_study_id_fkey FOREIGN KEY (study_id) REFERENCES study (study_id) ON UPDATE RESTRICT ON DELETE RESTRICT
) 
WITH OIDS;
ALTER TABLE study_parameter_value OWNER TO clinica;

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES 
 (1,'collectDob','collect subject''s date of birth','In study creation, Subject Birthdate can be set to require collect full birthdate, year of birth, or not used','required',true,false);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES 
(2,'discrepancyManagement','','','true',true,false);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(3,'subjectPersonIdRequired','','In study creation, Person ID can be set to required, optional, or not used','required',true,false);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(4,'genderRequired','','In study creation, Subject Gender can be set to required or not used','required',true,false);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(5,'subjectIdGeneration','','In study creation, Study Subject ID can be set to Manual Entry, Auto-generate (editable), Auto-generate (non-editable)','manual',true,false);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(6,'subjectIdPrefixSuffix','','In study and/or site creation, if Study Subject ID is set to Auto-generate, user can optionally specify a prefix and suffix for the format of the ID, using the format [PRETEXT][AUTO#][POSTTEXT]','false',true,false);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(7,'interviewerNameRequired','','In study or site creation, CRF Interviewer Name can be set as optional or required fields','required',true,true);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(8,'interviewerNameDefault','','In study or site creation, CRF Interviewer Name can be set to default to blank or to be pre-populated with user''s name and the date of the study event','blank',true,true);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(9,'interviewerNameEditable','','In study creation, CRF Interviewer Name can be set to editable or not editable','editable',true,false);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(10,'interviewDateRequired','','In study or site creation, CRF Interviewer Date can be set as optional or required fields','required',true,true);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(11,'interviewDateDefault','','In study or site creation, CRF Interviewer Date can be set to default to blank or to be pre-populated with user''s name and the date of the study event','eventDate',true,true);

INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
 VALUES
(12,'interviewDateEditable','','In study creation, CRF Interview Name and Date can be set to editable or not editable','editable',true,false);


INSERT INTO EXPORT_FORMAT VALUES (4, 'text/plain', 'Default export format for CDISC ODM XML files', 'text/plain');
INSERT INTO EXPORT_FORMAT VALUES (5, 'text/plain', 'Default export format for SAS files', 'text/plain');


--do the following inserts if your DB for 1.1 doesn't have any data in subject_event_status
--otherwise, you may need to update the data manually, not insert, because the DB for 1.0 has 9 statuses
--in subject_event_status table, but the data is missing by mistake in 1.1
insert into subject_event_status values(1, 'scheduled', '');
insert into subject_event_status values(2, 'not scheduled', '');
insert into subject_event_status values(3, 'data entry started', '');
insert into subject_event_status values(4, 'completed', '');
insert into subject_event_status values(5, 'stopped', '');
insert into subject_event_status values(6, 'skipped', '');
insert into subject_event_status values(7, 'locked', '');

-- -----------------------------------------------------------------------------
--  Add a new record
-- -----------------------------------------------------------------------------
INSERT INTO study_parameter (study_parameter_id,handle,name,description,default_value,inheritable,overridable)
VALUES 
(13,'personIdShownOnCRF','','','false',true,false);

-- -----------------------------------------------------------------------------
-- Change the column names to match the modified columns in Oracle
-- The following change should not be applied to Oracle
-- -----------------------------------------------------------------------------
ALTER TABLE dc_event RENAME "type"  TO dc_event_type;
ALTER TABLE dc_event ALTER COLUMN dc_event_type SET STATISTICS -1;

ALTER TABLE decision_condition RENAME "type"  TO decision_condition_type;
ALTER TABLE decision_condition ALTER COLUMN decision_condition_type SET STATISTICS -1;

ALTER TABLE item_data_type RENAME definition  TO item_data_type_definition;
ALTER TABLE item_data_type ALTER COLUMN item_data_type_definition SET STATISTICS -1;

ALTER TABLE null_value_type RENAME definition  TO null_value_type_definition;
ALTER TABLE null_value_type ALTER COLUMN null_value_type_definition SET STATISTICS -1;

ALTER TABLE study_event_definition RENAME "type"  TO study_event_definition_type;
ALTER TABLE study_event_definition ALTER COLUMN study_event_definition_type SET STATISTICS -1;

ALTER TABLE test_table_three RENAME study_event_definition_repeating  TO study_event_def_repeating;
ALTER TABLE test_table_three ALTER COLUMN study_event_def_repeating SET STATISTICS -1;

