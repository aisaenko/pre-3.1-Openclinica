-- -----------------------------------------------------------------------------
-- Rename subject tracking related tables and sequences
-- -----------------------------------------------------------------------------
CONNECT &clinica_account/&clinica_password@&db_name

RENAME group_class_types TO study_template_type
/

RENAME study_group_class TO study_template
/

RENAME group_class_types_seq TO study_template_type_seq
/

RENAME study_group_class_seq TO study_template_seq
/

CONNECT &dba_account/&dba_account_password@&db_name

-- -----------------------------------------------------------------------------
-- Rename subject tracking related column names
-- -----------------------------------------------------------------------------
ALTER TABLE &clinica..study_template_type
RENAME COLUMN group_class_type_id TO study_template_type_id
/

ALTER TABLE &clinica..study_template
RENAME COLUMN group_class_type_id TO study_template_type_id
/

ALTER TABLE &clinica..study_template
RENAME COLUMN study_group_class_id TO study_template_id
/

ALTER TABLE &clinica..study_group
RENAME COLUMN study_group_class_id TO study_template_id
/

ALTER TABLE &clinica..subject_group_map
RENAME COLUMN study_group_class_id TO study_template_id
/

-- -----------------------------------------------------------------------------
-- Recreate triggers after rename tables and columns
-- -----------------------------------------------------------------------------
DROP TRIGGER &clinica..group_class_types_birtrg
/

CREATE OR REPLACE TRIGGER &clinica..study_template_type_birtrg
  BEFORE INSERT
  ON &clinica..study_template_type
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_template_type_id IS NULL THEN
    SELECT study_template_type_seq.NEXTVAL
      INTO :NEW.study_template_type_id
      FROM dual;
  END IF;

END;
/

DROP TRIGGER &clinica..study_group_class_birtrg
/

CREATE OR REPLACE TRIGGER &clinica..study_template_birtrg
  BEFORE INSERT
  ON &clinica..study_template
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_template_id IS NULL THEN
    SELECT study_template_seq.NEXTVAL
      INTO :NEW.study_template_id
      FROM dual;
  END IF;

END;
/

-- -----------------------------------------------------------------------------
-- Rename subject tracking related constraint names
-- -----------------------------------------------------------------------------
ALTER TABLE &clinica..study_template_type
RENAME CONSTRAINT group_class_types_pk TO study_template_type_pk
/

ALTER TABLE &clinica..study_template
RENAME CONSTRAINT study_group_class_pk TO study_template_pk
/

ALTER TABLE &clinica..study_template
RENAME CONSTRAINT study_group_class_status_fk TO study_template_status_fk
/

ALTER TABLE &clinica..study_template
RENAME CONSTRAINT study_group_class_user_acct_fk 
TO study_template_user_account_fk
/

ALTER TABLE &clinica..study_template
RENAME CONSTRAINT study_grp_class_group_types_fk
TO stdy_tmplt_stdy_tmplt_types_fk
/

ALTER TABLE &clinica..study_group
RENAME CONSTRAINT stdy_group_stdy_group_class_fk 
TO study_group_study_template_fk
/

ALTER TABLE &clinica..subject_group_map
RENAME CONSTRAINT subj_grp_map_subj_grp_class_fk
TO subj_grp_map_stdy_template_fk
/


-- -----------------------------------------------------------------------------
-- Create new tables to support subject tracking
-- -----------------------------------------------------------------------------
CREATE TABLE &clinica..study_template_event_def
( study_template_event_def_id NUMBER(10) NOT NULL,
  study_template_id NUMBER(10) NOT NULL,
  study_event_definition_id NUMBER(10) NOT NULL,
  event_duration NUMBER(10,4),
  ideal_time_to_next_event NUMBER(10,4),
  min_time_to_next_event NUMBER(10,4),
  max_time_to_next_event NUMBER(10,4),
  status_id NUMBER(10),
  owner_id NUMBER(10),
  date_created DATE,
  update_id NUMBER(10),
  date_updated DATE  
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_template_event_def_seq
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  NOMAXVALUE 
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_template_evnt_def_birtrg
  BEFORE INSERT
  ON &clinica..study_template_event_def
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_template_event_def_id IS NULL THEN
    SELECT study_template_event_def_seq.NEXTVAL
      INTO :NEW.study_template_event_def_id
      FROM dual;
  END IF;

END;
/

ALTER TABLE &clinica..study_template_event_def
ADD CONSTRAINT study_template_event_def_pk 
PRIMARY KEY (study_template_event_def_id)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..study_template_event_def
ADD CONSTRAINT study_template_event_def_uk 
UNIQUE (study_template_id, study_event_definition_id)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..study_template_event_def
ADD CONSTRAINT stdy_tmp_event_def_stdy_tmp_fk FOREIGN KEY (study_template_id)
REFERENCES &clinica..study_template (study_template_id)
/

ALTER TABLE &clinica..study_template_event_def
ADD CONSTRAINT stdy_tmp_vnt_df_stdy_vnt_df_fk 
FOREIGN KEY (study_event_definition_id)
REFERENCES &clinica..study_event_definition (study_event_definition_id)
/

ALTER TABLE &clinica..study_template_event_def
ADD CONSTRAINT stdy_tmp_evnt_def_usr_acct_fk1 FOREIGN KEY (owner_id)
REFERENCES &clinica..user_account (user_id)
/

ALTER TABLE &clinica..study_template_event_def
ADD CONSTRAINT stdy_tmp_event_def_status_fk FOREIGN KEY (status_id)
REFERENCES &clinica..status (status_id)
/

ALTER TABLE &clinica..study_template_event_def
ADD CONSTRAINT stdy_tmp_evnt_def_usr_acct_fk2 FOREIGN KEY (update_id)
REFERENCES &clinica..user_account (user_id)
/


CREATE TABLE &clinica..summary_data_entry_status
( summary_data_entry_status_id NUMBER(10) NOT NULL,
  NAME VARCHAR2(50) NOT NULL,
  description VARCHAR2(255)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..summary_data_entry_status_seq
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  NOMAXVALUE 
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..summary_data_entry_stat_birtrg
  BEFORE INSERT
  ON &clinica..summary_data_entry_status
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.summary_data_entry_status_id IS NULL THEN
    SELECT summary_data_entry_status_seq.NEXTVAL
      INTO :NEW.summary_data_entry_status_id
      FROM dual;
  END IF;

END;
/

ALTER TABLE &clinica..summary_data_entry_status
ADD CONSTRAINT summary_data_entry_status_pk 
PRIMARY KEY (summary_data_entry_status_id)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..summary_data_entry_status
ADD CONSTRAINT summary_data_entry_status_uk UNIQUE (NAME)
USING INDEX TABLESPACE &clinica_indx_ts
/

-- -----------------------------------------------------------------------------
-- Add new columns and add constraints to existing tables to support 
-- subject tracking
-- -----------------------------------------------------------------------------
ALTER TABLE &clinica..subject_group_map ADD
(study_template_start_dt DATE)
/


ALTER TABLE &clinica..study_event ADD
(study_template_event_def_id NUMBER(10),
 summary_data_entry_status_id NUMBER(10)
)
/

ALTER TABLE &clinica..study_event
ADD CONSTRAINT stdy_evnt_stdy_tmpl_evnt_df_fk 
FOREIGN KEY (study_template_event_def_id)
REFERENCES &clinica..study_template_event_def (study_template_event_def_id)
/

ALTER TABLE &clinica..study_event
ADD CONSTRAINT stdy_evnt_sum_dat_entr_stat_fk 
FOREIGN KEY (summary_data_entry_status_id)
REFERENCES &clinica..summary_data_entry_status (summary_data_entry_status_id) 
/


-- -----------------------------------------------------------------------------
-- Create openclinicameta to store version information
-- -----------------------------------------------------------------------------
CREATE TABLE &clinica..openclinicameta
(database_version VARCHAR2(10),
 application_version VARCHAR2(10)
)
TABLESPACE &clinica_data_ts
/

-- -----------------------------------------------------------------------------
-- Insert new records and migrate existing data
-- -----------------------------------------------------------------------------
INSERT INTO &clinica..summary_data_entry_status
(summary_data_entry_status_id, NAME, description)
VALUES
(&clinica..summary_data_entry_status_seq.NEXTVAL, 'not started', 
 'Data entry has not been started')
/

INSERT INTO &clinica..summary_data_entry_status
(summary_data_entry_status_id, NAME, description)
VALUES
(&clinica..summary_data_entry_status_seq.NEXTVAL, 'started', 
 'Data entry for at least one of the CRFs in this event has been started')
/

INSERT INTO &clinica..summary_data_entry_status
(summary_data_entry_status_id, NAME, description)
VALUES
(&clinica..summary_data_entry_status_seq.NEXTVAL, 'completed', 
 'Data entry for all the CRFs in this event has not been completed')
/

-- -----------------------------------------------------------------------------
-- Insert/update suject_event_status records and update study_event records
-- -----------------------------------------------------------------------------
-- Insert new 'no show/reschedule' subject_event_status
INSERT INTO &clinica..subject_event_status (subject_event_status_id, NAME)
VALUES (&clinica..subject_event_status_seq.NEXTVAL, 'no show/reschedule')
/

-- Change old subject_event_status values to new values
UPDATE &clinica..subject_event_status 
   SET NAME = 'no show/no reschedule'
 WHERE NAME = 'locked'
/

-- Update records in study_event for 'skipped' subject_event_status
-- 'Skipped' is changed to 'no show/no reschedule'
UPDATE &clinica..study_event
   SET subject_event_status_id = 
      (SELECT subject_event_status_id
         FROM &clinica..subject_event_status
        WHERE NAME = 'no show/no reschedule')
 WHERE subject_event_status_id = 
       (SELECT subject_event_status_id
         FROM &clinica..subject_event_status
        WHERE NAME = 'skipped')
/

UPDATE &clinica..subject_event_status
   SET NAME = 'cancelled/reschedule'
 WHERE NAME = 'skipped'
/

UPDATE &clinica..subject_event_status
   SET NAME = 'cancelled/no reschedule'
 WHERE NAME = 'stopped'
/

UPDATE &clinica..subject_event_status
   SET NAME = 'in progress'
 WHERE NAME = 'data entry started'
/

UPDATE &clinica..subject_event_status
   SET NAME = 'scheduled 1'
 WHERE NAME = 'not scheduled'
/

-- Update records in study_event for 'scheduled' subject_event_status
UPDATE &clinica..study_event
   SET subject_event_status_id = 
   (SELECT subject_event_status_id
         FROM &clinica..subject_event_status
        WHERE NAME = 'scheduled 1')
 WHERE subject_event_status_id = 
       (SELECT subject_event_status_id
         FROM &clinica..subject_event_status
        WHERE NAME = 'scheduled')
/

UPDATE &clinica..subject_event_status
   SET NAME = 'proposed'
 WHERE NAME = 'scheduled'
/

UPDATE &clinica..subject_event_status
   SET NAME = 'scheduled'
 WHERE NAME = 'scheduled 1'
/


-- -----------------------------------------------------------------------------
-- Add unique constraint to subject_event_status table
-- -----------------------------------------------------------------------------
ALTER TABLE &clinica..subject_event_status
ADD CONSTRAINT subject_event_status_uk UNIQUE (NAME)
USING INDEX TABLESPACE &clinica_indx_ts
/


-- -----------------------------------------------------------------------------
-- Add foreign key to study_event table
-- -----------------------------------------------------------------------------
ALTER TABLE &clinica..study_event
ADD CONSTRAINT stdy_evet_subj_evet_status_fk 
FOREIGN KEY (subject_event_status_id)
REFERENCES &clinica..subject_event_status (subject_event_status_id)
/


-- -----------------------------------------------------------------------------
-- Add new values to basecase data
-- -----------------------------------------------------------------------------
INSERT INTO &clinica..study_parameter
            (study_parameter_id, handle,
             NAME,
             description,
             DEFAULT_VALUE, inheritable, overridable
            )
     VALUES (&clinica..study_parameter_seq.NEXTVAL, 'reschedule',
             '',
             'It is used when any event reschedule causes events to exceed scheduling window',
             'reschedule based on the first event', 1, 0
            )
/


DECLARE 
  v_study_id NUMBER(10) := 0;
  v_study_parameter_value_id NUMBER(10) := 0;
  v_sql VARCHAR2(500) := NULL;
  v_insert VARCHAR2(500) := NULL;
  v_schema VARCHAR2(30) := '&clinica';
  
  TYPE v_cur_type IS REF CURSOR;
  v_cur v_cur_type;
  
BEGIN
  v_sql :=
    'SELECT study_id ' ||
    '  FROM ' || v_schema || '.study';
    

  v_insert :=
    'INSERT INTO ' || v_schema || '.study_parameter_value ' ||
    '(study_parameter_value_id, study_id, VALUE,parameter) ' ||
    'VALUES ' ||
    '(' || v_schema || '.study_parameter_value_seq.NEXTVAL, ' ||
    ' :1, ''reschedule based on the first event'', ''reschedule'')';
    
  OPEN v_cur FOR v_sql;
  LOOP
    FETCH v_cur INTO v_study_id;
    EXIT WHEN v_cur%NOTFOUND;
    
    EXECUTE IMMEDIATE v_insert USING v_study_id;
    
  END LOOP;
  CLOSE v_cur;
  COMMIT;
  
EXCEPTION
  WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE ( 'Error ' || TO_CHAR(SQLCODE) || ': ' || SQLERRM);

END;
/    


-- -----------------------------------------------------------------------------
-- Update openclinicameta to 0.6 release
-- -----------------------------------------------------------------------------
INSERT INTO &clinica..openclinicameta
(database_version, application_version)
VALUES
('0.6', '0.6')
/

COMMIT
/

-- ---------------------------------------------------------------------------------
-- ---------------------------------------------------------------------------------
-- Things above this line have been applied to orcl10g and ndardev
-- ---------------------------------------------------------------------------------
-- ---------------------------------------------------------------------------------

