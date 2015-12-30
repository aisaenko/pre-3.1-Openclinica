-- -----------------------------------------------------------------------------
-- OpenClinica 2.0 changes
-- -----------------------------------------------------------------------------
ALTER TABLE &clinica..study DROP COLUMN collect_dob
/

ALTER TABLE &clinica..study DROP COLUMN discrepancy_management
/

-- -----------------------------------------------------------------------------
-- TABLE: study_parameter
-- -----------------------------------------------------------------------------

-- DROP TABLE &clinica..study_parameter;

CREATE TABLE &clinica..study_parameter
(
  study_parameter_id NUMBER(10) NOT NULL,
  handle VARCHAR2(50),
  NAME VARCHAR2(50),
  description VARCHAR2(255),
  DEFAULT_VALUE VARCHAR2(50),
  inheritable VARCHAR2(1) DEFAULT 1,
  overridable VARCHAR2(1)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_parameter_seq
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_parameter_birtrg
  BEFORE INSERT
  ON &clinica..study_parameter
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_parameter_id IS NULL THEN
    SELECT study_parameter_seq.NEXTVAL
      INTO :NEW.study_parameter_id
      FROM DUAL;
  END IF;
END;
/

ALTER TABLE &clinica..study_parameter
ADD CONSTRAINT study_parameter_pk PRIMARY KEY (study_parameter_id)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..study_parameter
ADD CONSTRAINT study_parameter_uk UNIQUE (handle)
USING INDEX TABLESPACE &clinica_indx_ts
/

-- -----------------------------------------------------------------------------
-- Table: study_parameter_value
-- -----------------------------------------------------------------------------
-- DROP TABLE study_parameter_value;

CREATE TABLE &clinica..study_parameter_value
(
  study_parameter_value_id NUMBER(10) NOT NULL,
  study_id NUMBER(10),
  VALUE VARCHAR2(50),
  parameter VARCHAR2(50)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_parameter_value_seq
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_parameter_value_birtrg
  BEFORE INSERT
  ON &clinica..study_parameter_value
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_parameter_value_id IS NULL THEN
    SELECT study_parameter_value_seq.NEXTVAL
      INTO :NEW.study_parameter_value_id
      FROM DUAL;
  END IF;
END;
/

ALTER TABLE &clinica..study_parameter_value
ADD CONSTRAINT study_parameter_value_pk PRIMARY KEY (study_parameter_value_id)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..study_parameter_value
ADD CONSTRAINT study_par_val_study_par_fk FOREIGN KEY (parameter)
REFERENCES &clinica..study_parameter (handle)
/

ALTER TABLE &clinica..study_parameter_value
ADD CONSTRAINT study_par_val_study_fk FOREIGN KEY (study_id)
REFERENCES &clinica..study (study_id)
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle,
             NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'collectDob',
             'collect subject''s date of birth',
             'In study creation, Subject Birthdate can be set to require collect full birthdate, year of birth, or not used',
             'required', 1, 0
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle,
             NAME, description, DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'discrepancyManagement',
             '', '', 'true', 1, 0
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id,
             handle, NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL,
             'subjectPersonIdRequired', '',
             'In study creation, Person ID can be set to required, optional, or not used',
             'required', 1, 0
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle, NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'genderRequired', '',
             'In study creation, Subject Gender can be SET TO required OR NOT used',
             'required', 1, 0
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle,
             NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'subjectIdGeneration',
             '',
             'In study creation, Study Subject ID can be set to Manual Entry, Auto-generate (editable), Auto-generate (non-editable)',
             'manual', 1, 0
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle,
             NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'subjectIdPrefixSuffix',
             '',
             'In study and/or site creation, if Study Subject ID is set to Auto-generate, user can optionally specify a prefix and suffix for the format of the ID, using the format [PRETEXT][AUTO#][POSTTEXT]',
             'false', 1, 0
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id,
             handle, NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL,
             'interviewerNameRequired', '',
             'In study or site creation, CRF Interviewer Name can be set as optional or required fields',
             'required', 1, 1
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle,
             NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'interviewerNameDefault',
             '',
             'In study or site creation, CRF Interviewer Name can be set to default to blank or to be pre-populated with user''s name and the date of the study event',
             'blank', 1, 1
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id,
             handle, NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL,
             'interviewerNameEditable', '',
             'In study creation, CRF Interviewer Name can be set to editable or not editable',
             'editable', 1, 0
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle,
             NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'interviewDateRequired',
             '',
             'In study or site creation, CRF Interviewer Date can be set as optional or required fields',
             'required', 1, 1
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle,
             NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'interviewDateDefault',
             '',
             'In study or site creation, CRF Interviewer Date can be set to default to blank or to be pre-populated with user''s name and the date of the study event',
             'eventDate', 1, 1
            )
/

INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle,
             NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'interviewDateEditable',
             '',
             'In study creation, CRF Interview Name and Date can be set to editable or not editable',
             'editable', 1, 0
            )
/


INSERT INTO &clinica..export_format
            (export_format_id, NAME,
             description, mime_type
            )
     VALUES (&clinica..export_format_seq.NEXTVAL, 'text/plain',
             'Default export format for CDISC ODM XML files', 'text/plain'
            )
/

INSERT INTO &clinica..export_format
            (export_format_id, NAME,
             description, mime_type
            )
     VALUES (&clinica..export_format_seq.NEXTVAL, 'text/plain',
             'Default export format for SAS files', 'text/plain'
            )
/


--do the following inserts if your DB for 1.1 doesn't have any data in subject_event_status
--otherwise, you may need to update the data manually, not insert, because the DB for 1.0 has 9 statuses
--in subject_event_status table, but the data is missing by mistake in 1.1
INSERT INTO &clinica..subject_event_status
            (subject_event_status_id, NAME, description
            )
     VALUES (&clinica..subject_event_status_seq.NEXTVAL, 'scheduled', ''
            )
/

INSERT INTO &clinica..subject_event_status
            (subject_event_status_id, NAME, description
            )
     VALUES (&clinica..subject_event_status_seq.NEXTVAL, 'not scheduled', ''
            )
/

INSERT INTO &clinica..subject_event_status
            (subject_event_status_id, NAME, description
            )
     VALUES (&clinica..subject_event_status_seq.NEXTVAL, 'data entry started', ''
            )
/

INSERT INTO &clinica..subject_event_status
            (subject_event_status_id, NAME, description
            )
     VALUES (&clinica..subject_event_status_seq.NEXTVAL, 'completed', ''
            )
/

INSERT INTO &clinica..subject_event_status
            (subject_event_status_id, NAME, description
            )
     VALUES (&clinica..subject_event_status_seq.NEXTVAL, 'stopped', ''
            )
/

INSERT INTO &clinica..subject_event_status
            (subject_event_status_id, NAME, description
            )
     VALUES (&clinica..subject_event_status_seq.NEXTVAL, 'skipped', ''
            )
/

INSERT INTO &clinica..subject_event_status
            (subject_event_status_id, NAME, description
            )
     VALUES (&clinica..subject_event_status_seq.NEXTVAL, 'locked', ''
            )
/

-- -----------------------------------------------------------------------------
--  Add a new record to study_parameter table
-- -----------------------------------------------------------------------------
INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle, NAME, description, DEFAULT_VALUE,
             inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'personIdShownOnCRF', '', '', 'false',
             1, 0
            )
/

-- -----------------------------------------------------------------------------
-- Insert new records into study_parameter_value table
-- -----------------------------------------------------------------------------
INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE, parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), '1', 'collectDob'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'true',
             'discrepancyManagement'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'true',
             'genderRequired'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'required',
             'subjectPersonIdRequired'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'true',
             'interviewerNameRequired'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'blank',
             'interviewerNameDefault'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'true',
             'interviewerNameEditable'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'true',
             'interviewDateRequired'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'blank',
             'interviewDateDefault'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'true',
             'interviewDateEditable'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'manual',
             'subjectIdGeneration'
            )
/

-- not implemented for now, so the value is an empty string 
INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), '',
             'subjectIdPrefixSuffix'
            )
/

INSERT INTO &clinica..study_parameter_value
            (study_parameter_value_id,
             study_id, VALUE,
             parameter
            )
     VALUES (&clinica..study_parameter_value_seq.NEXTVAL,
             (SELECT study_id
                FROM &clinica..study
               WHERE unique_identifier = 'default-study'), 'true',
             'personIdShownOnCRF'
            )

/

COMMIT
/
