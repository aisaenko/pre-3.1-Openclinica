-- -----------------------------------------------------------------------------
-- Title: openclinica_ndar_0.2_install_ora.sql
--
-- Purpose: Create OpenClinica objects in Oracle database.  This is based on
--          NDAR OpenClinica 0.2 release.  It has the changes made in NDAR 0.1
--          and NDAR 0.2 releases.  
--         (NDAR 0.1 release is based on Akaza OpenClinica 1.1 release)
--
-- Author: Wen Nie
--
-- Modification History:
--
--------------------------------------------------------------------------------
-- -----------------------------------------------------------------------------
-- NDAR 0.2 database objects
-- -----------------------------------------------------------------------------
-- -----------------------------------------------------------------------------

-- -----------------------------------------------------------------------------
-- Create tables
-- -----------------------------------------------------------------------------
--
-- Name: archived_dataset_file 
--
CREATE TABLE &clinica..archived_dataset_file (
    archived_dataset_file_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    dataset_id NUMBER(10),
    export_format_id NUMBER(10),
    file_reference VARCHAR2(1000),
    run_time NUMBER,
    file_size NUMBER,
    date_created DATE,
    owner_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..archived_dataset_file_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..archived_dataset_file_birtrg
  BEFORE INSERT
  ON &clinica..archived_dataset_file
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.archived_dataset_file_id IS NULL THEN
    SELECT archived_dataset_file_seq.NEXTVAL
      INTO :NEW.archived_dataset_file_id
      FROM dual;
  END IF;

END;
/


--
-- Name: audit_event 
--
CREATE TABLE &clinica..audit_event (
    audit_id NUMBER(10) NOT NULL,
    audit_date TIMESTAMP NOT NULL,
    audit_table VARCHAR2(500) NOT NULL,
    user_id NUMBER(10),
    entity_id NUMBER(10),
    reason_for_change VARCHAR2(1000),
    action_message VARCHAR2(4000)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..audit_event_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..audit_event_birtrg
  BEFORE INSERT
  ON &clinica..audit_event
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.audit_id IS NULL THEN
    SELECT audit_event_seq.NEXTVAL
      INTO :NEW.audit_id
      FROM dual;
  END IF;

END;
/


--
-- Name: audit_event_context 
--
CREATE TABLE &clinica..audit_event_context (
    audit_id NUMBER(10),
    study_id NUMBER(10),
    subject_id NUMBER(10),
    study_subject_id NUMBER(10),
    role_name VARCHAR2(200),
    event_crf_id NUMBER(10),
    study_event_id NUMBER(10),
    study_event_definition_id NUMBER(10),
    crf_id NUMBER(10),
    crf_version_id NUMBER(10),
    study_crf_id NUMBER(10),
    item_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/


--
-- Name: audit_event_values
--
CREATE TABLE &clinica..audit_event_values (
    audit_id NUMBER(10),
    column_name VARCHAR2(255),
    old_value VARCHAR2(2000),
    new_value VARCHAR2(2000)
)
TABLESPACE &clinica_data_ts
/


--
-- Name: completion_status;  
--
CREATE TABLE &clinica..completion_status (
    completion_status_id NUMBER(10) NOT NULL,
    status_id NUMBER(10),
    NAME VARCHAR2(255),
    description VARCHAR2(1000)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..completion_status_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..completion_status_birtrg
  BEFORE INSERT
  ON &clinica..completion_status
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.completion_status_id IS NULL THEN
    SELECT completion_status_seq.NEXTVAL
      INTO :NEW.completion_status_id
      FROM dual;
  END IF;

END;
/


--
-- Name: crf 
--
CREATE TABLE &clinica..crf (
    crf_id NUMBER(10) NOT NULL,
    status_id NUMBER(10),
    NAME VARCHAR2(255),
    description VARCHAR2(2048),
    owner_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    update_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..crf_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..crf_birtrg
  BEFORE INSERT
  ON &clinica..crf
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.crf_id IS NULL THEN
    SELECT crf_seq.NEXTVAL
      INTO :NEW.crf_id
      FROM dual;
  END IF;

END;
/


--
-- Name: crf_version
--
CREATE TABLE &clinica..crf_version (
    crf_version_id NUMBER(10) NOT NULL,
    crf_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(4000),
    revision_notes VARCHAR2(255),
    status_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    owner_id NUMBER(10),
    update_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..crf_version_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..crf_version_birtrg
  BEFORE INSERT
  ON &clinica..crf_version
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.crf_version_id IS NULL THEN
    SELECT crf_version_seq.NEXTVAL
      INTO :NEW.crf_version_id
      FROM dual;
  END IF;

END;
/


--
-- Name: dataset 
--
CREATE TABLE &clinica..dataset (
    dataset_id NUMBER(10) NOT NULL,
    study_id NUMBER(10),
    status_id NUMBER(10),
    NAME VARCHAR2(255),
    description VARCHAR2(2000),
    sql_statement CLOB,
    num_runs NUMBER,
    date_start DATE,
    date_end DATE,
    date_created DATE,
    date_updated DATE,
    date_last_run DATE,
    owner_id NUMBER(10),
    approver_id NUMBER(10),
    update_id NUMBER(10),
    show_event_location VARCHAR2(1) DEFAULT '0',
    show_event_start VARCHAR2(1) DEFAULT '0',
    show_event_end VARCHAR2(1) DEFAULT '0',
    show_subject_dob VARCHAR2(1) DEFAULT '0',
    show_subject_gender VARCHAR2(1) DEFAULT '0'
)
TABLESPACE &clinica_data_ts
LOB (sql_statement) STORE AS (TABLESPACE &clinica_lob_ts)
/

CREATE SEQUENCE &clinica..dataset_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..dataset_birtrg
  BEFORE INSERT
  ON &clinica..dataset
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.dataset_id IS NULL THEN
    SELECT dataset_seq.NEXTVAL
      INTO :NEW.dataset_id
      FROM dual;
  END IF;

END;
/

--
-- Name: dataset_crf_version_map 
--
CREATE TABLE &clinica..dataset_crf_version_map (
    dataset_id NUMBER(10),
    event_definition_crf_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/


--
-- Name: dataset_filter_map 
--
CREATE TABLE &clinica..dataset_filter_map (
    dataset_id NUMBER(10),
    filter_id NUMBER(10),
    ordinal NUMBER(10)
)
TABLESPACE &clinica_data_ts
/


--
-- Name: dc_computed_event 
--
CREATE TABLE &clinica..dc_computed_event (
    dc_summary_event_id NUMBER(10) NOT NULL,
    dc_event_id NUMBER(10) NOT NULL,
    item_target_id NUMBER(10),
    summary_type VARCHAR2(255)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..dc_computed_event_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..dc_computed_event_birtrg
  BEFORE INSERT
  ON &clinica..dc_computed_event
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.dc_summary_event_id IS NULL THEN
    SELECT dc_computed_event_seq.NEXTVAL
      INTO :NEW.dc_summary_event_id
      FROM dual;
  END IF;

END;
/


--
-- Name: dc_event 
--
CREATE TABLE &clinica..dc_event (
    dc_event_id NUMBER(10) NOT NULL,
    decision_condition_id NUMBER(10),
    ordinal NUMBER NOT NULL,
    dc_event_type VARCHAR2(256) NOT NULL
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..dc_event_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..dc_event_birtrg
  BEFORE INSERT
  ON &clinica..dc_event
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.dc_event_id IS NULL THEN
    SELECT dc_event_seq.NEXTVAL
      INTO :NEW.dc_event_id
      FROM dual;
  END IF;

END;
/


--
-- Name: dc_primitive 
--
CREATE TABLE &clinica..dc_primitive (
    dc_primitive_id NUMBER(10) NOT NULL,
    decision_condition_id NUMBER(10),
    item_id NUMBER(10),
    dynamic_value_item_id NUMBER(10),
    comparison VARCHAR2(3) NOT NULL,
    constant_value VARCHAR2(4000)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..dc_primitive_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..dc_primitive_birtrg
  BEFORE INSERT
  ON &clinica..dc_primitive
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.dc_primitive_id IS NULL THEN
    SELECT dc_primitive_seq.NEXTVAL
      INTO :NEW.dc_primitive_id
      FROM dual;
  END IF;

END;
/


--
-- Name: dc_section_event 
--
CREATE TABLE &clinica..dc_section_event (
    dc_event_id NUMBER(10) NOT NULL,
    section_id NUMBER(10) NOT NULL
)
TABLESPACE &clinica_data_ts
/

--
-- Name: dc_send_email_event 
--
CREATE TABLE &clinica..dc_send_email_event (
    dc_event_id NUMBER(10) NOT NULL,
    to_address VARCHAR2(1000) NOT NULL,
    subject VARCHAR2(1000),
    Body VARCHAR2(4000)
)
TABLESPACE &clinica_data_ts
/


--
-- Name: dc_substitution_event 
--
CREATE TABLE &clinica..dc_substitution_event (
    dc_event_id NUMBER(10) NOT NULL,
    item_id NUMBER(10),
    VALUE VARCHAR2(1000) NOT NULL
)
TABLESPACE &clinica_data_ts
/


--
-- Name: dc_summary_item_map 
--
CREATE TABLE &clinica..dc_summary_item_map (
    dc_summary_event_id NUMBER(10),
    item_id NUMBER(10),
    ordinal NUMBER 
)
TABLESPACE &clinica_data_ts
/


--
-- Name: decision_condition 
--
CREATE TABLE &clinica..decision_condition (
    decision_condition_id NUMBER(10) NOT NULL,
    crf_version_id NUMBER(10),
    status_id NUMBER(10),
    LABEL VARCHAR2(1000) NOT NULL,
    comments VARCHAR2(3000) NOT NULL,
    quantity NUMBER NOT NULL,
    decision_condition_type VARCHAR2(3) NOT NULL,
    owner_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    update_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..decision_condition_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..decision_condition_birtrg
  BEFORE INSERT
  ON &clinica..decision_condition
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.decision_condition_id IS NULL THEN
    SELECT decision_condition_seq.NEXTVAL
      INTO :NEW.decision_condition_id
      FROM dual;
  END IF;

END;
/


--
-- Name: discrepancy_note 
--
CREATE TABLE &clinica..discrepancy_note (
    discrepancy_note_id NUMBER(10) NOT NULL,
    description VARCHAR2(255),
    discrepancy_note_type_id NUMBER(10),
    resolution_status_id NUMBER(10),
    detailed_notes VARCHAR2(1000),
    date_created DATE,
    owner_id NUMBER(10),
    parent_dn_id NUMBER(10),
    entity_type VARCHAR2(30),
    study_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..discrepancy_note_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..discrepancy_note_birtrg
  BEFORE INSERT
  ON &clinica..discrepancy_note
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.discrepancy_note_id IS NULL THEN
    SELECT discrepancy_note_seq.NEXTVAL
      INTO :NEW.discrepancy_note_id
      FROM dual;
  END IF;

END;
/


--
-- Name: discrepancy_note_type 
--
CREATE TABLE &clinica..discrepancy_note_type (
    discrepancy_note_type_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(50),
    description VARCHAR2(255)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..discrepancy_note_type_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..discrepancy_note_type_birtrg
  BEFORE INSERT
  ON &clinica..discrepancy_note_type
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.discrepancy_note_type_id IS NULL THEN
    SELECT discrepancy_note_type_seq.NEXTVAL
      INTO :NEW.discrepancy_note_type_id
      FROM dual;
  END IF;

END;
/


--
-- Name: dn_event_crf_map 
--
CREATE TABLE &clinica..dn_event_crf_map (
    event_crf_id NUMBER(10),
    discrepancy_note_id NUMBER(10),
    column_name VARCHAR2(255)
)
TABLESPACE &clinica_data_ts
/


--
-- Name: dn_item_data_map 
--
CREATE TABLE &clinica..dn_item_data_map (
    item_data_id NUMBER(10),
    discrepancy_note_id NUMBER(10),
    column_name VARCHAR2(255)
)
TABLESPACE &clinica_data_ts
/


--
-- Name: dn_study_event_map 
--
CREATE TABLE &clinica..dn_study_event_map (
    study_event_id NUMBER(10),
    discrepancy_note_id NUMBER(10),
    column_name VARCHAR2(255)
) 
TABLESPACE &clinica_data_ts
/


--
-- Name: dn_study_subject_map 
--
CREATE TABLE &clinica..dn_study_subject_map (
    study_subject_id NUMBER(10),
    discrepancy_note_id NUMBER(10),
    column_name VARCHAR2(255)
) TABLESPACE &clinica_data_ts
/


--
-- Name: dn_subject_map 
--
CREATE TABLE &clinica..dn_subject_map (
    subject_id NUMBER(10),
    discrepancy_note_id NUMBER(10),
    column_name VARCHAR2(255)
) TABLESPACE &clinica_data_ts
/


--
-- Name: event_crf 
--
CREATE TABLE &clinica..event_crf (
    event_crf_id NUMBER(10) NOT NULL,
    study_event_id NUMBER(10),
    crf_version_id NUMBER(10),
    date_interviewed DATE,
    interviewer_name VARCHAR2(255),
    completion_status_id NUMBER(10),
    status_id NUMBER(10),
    annotations VARCHAR2(4000),
    date_completed TIMESTAMP,
    validator_id NUMBER(10),
    date_validate DATE,
    date_validate_completed TIMESTAMP,
    validator_annotations VARCHAR2(4000),
    validate_string VARCHAR2(256),
    owner_id NUMBER(10),
    date_created DATE,
    study_subject_id NUMBER(10),
    date_updated DATE,
    update_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..event_crf_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..event_crf_birtrg
  BEFORE INSERT
  ON &clinica..event_crf
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.event_crf_id IS NULL THEN
    SELECT event_crf_seq.NEXTVAL
      INTO :NEW.event_crf_id
      FROM dual;
  END IF;

END;
/


--
-- Name: event_definition_crf 
--
CREATE TABLE &clinica..event_definition_crf (
    event_definition_crf_id NUMBER(10) NOT NULL,
    study_event_definition_id NUMBER(10),
    study_id NUMBER(10),
    crf_id NUMBER(10),
    required_crf VARCHAR2(1),
    double_entry VARCHAR2(1),
    require_all_text_filled VARCHAR2(1),
    decision_conditions VARCHAR2(1),
    null_values VARCHAR2(255),
    default_version_id NUMBER(10),
    status_id NUMBER(10),
    owner_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    update_id NUMBER(10),
    ordinal NUMBER
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..event_definition_crf_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..event_definition_crf_birtrg
  BEFORE INSERT
  ON &clinica..event_definition_crf
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.event_definition_crf_id IS NULL THEN
    SELECT event_definition_crf_seq.NEXTVAL
      INTO :NEW.event_definition_crf_id
      FROM dual;
  END IF;

END;
/


--
-- Name: export_format 
--
CREATE TABLE &clinica..export_format (
    export_format_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(1000),
    mime_type VARCHAR2(255)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..export_format_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..export_format_birtrg
  BEFORE INSERT
  ON &clinica..export_format
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.export_format_id IS NULL THEN
    SELECT export_format_seq.NEXTVAL
      INTO :NEW.export_format_id
      FROM dual;
  END IF;

END;
/


--
-- Name: filter 
--
CREATE TABLE &clinica..FILTER (
    filter_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(2000),
    sql_statement CLOB,
    status_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    owner_id NUMBER(10) NOT NULL,
    update_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
LOB (sql_statement) STORE AS (TABLESPACE &clinica_lob_ts)
/

CREATE SEQUENCE &clinica..filter_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..filter_birtrg
  BEFORE INSERT
  ON &clinica..FILTER
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.filter_id IS NULL THEN
    SELECT filter_seq.NEXTVAL
      INTO :NEW.filter_id
      FROM dual;
  END IF;

END;
/


--
-- Name: filter_crf_version_map 
--
CREATE TABLE &clinica..filter_crf_version_map (
    filter_id NUMBER(10),
    crf_version_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/


--
-- Name: group_class_types 
--
CREATE TABLE &clinica..group_class_types (
    group_class_type_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..group_class_types_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..group_class_types_birtrg
  BEFORE INSERT
  ON &clinica..group_class_types
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.group_class_type_id IS NULL THEN
    SELECT group_class_types_seq.NEXTVAL
      INTO :NEW.group_class_type_id
      FROM dual;
  END IF;

END;
/


--
-- Name: item 
--
CREATE TABLE &clinica..item (
    item_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(4000),
    units VARCHAR2(64),
    phi_status VARCHAR2(1),
    item_data_type_id NUMBER(10),
    item_reference_type_id NUMBER(10),
    status_id NUMBER(10),
    owner_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    update_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..item_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..item_birtrg
  BEFORE INSERT
  ON &clinica..item
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.item_id IS NULL THEN
    SELECT item_seq.NEXTVAL
      INTO :NEW.item_id
      FROM dual;
  END IF;

END;
/


--
-- Name: item_data 
--
CREATE TABLE &clinica..item_data (
    item_data_id NUMBER(10) NOT NULL,
    item_id NUMBER(10) NOT NULL,
    event_crf_id NUMBER(10),
    status_id NUMBER(10),
    VALUE VARCHAR2(255),
    date_created DATE,
    date_updated DATE,
    owner_id NUMBER(10),
    update_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..item_data_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..item_data_birtrg
  BEFORE INSERT
  ON &clinica..item_data
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.item_data_id IS NULL THEN
    SELECT item_data_seq.NEXTVAL
      INTO :NEW.item_data_id
      FROM dual;
  END IF;

END;
/


--
-- Name: item_data_type 
--
CREATE TABLE &clinica..item_data_type (
    item_data_type_id NUMBER(10) NOT NULL,
    code VARCHAR2(20),
    NAME VARCHAR2(255),
    item_data_type_definition VARCHAR2(1000),
    reference VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..item_data_type_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..item_data_type_birtrg
  BEFORE INSERT
  ON &clinica..item_data_type
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.item_data_type_id IS NULL THEN
    SELECT item_data_type_seq.NEXTVAL
      INTO :NEW.item_data_type_id
      FROM dual;
  END IF;

END;
/


--
-- Name: item_form_metadata
--
CREATE TABLE &clinica..item_form_metadata (
    item_form_metadata_id NUMBER(10) NOT NULL,
    item_id NUMBER(10) NOT NULL,
    crf_version_id NUMBER(10),
    HEADER VARCHAR2(2000),
    subheader VARCHAR2(4000),
    parent_id NUMBER(10),
    parent_label VARCHAR2(120),
    column_number NUMBER(10),
    page_number_label VARCHAR2(5),
    question_number_label VARCHAR2(20),
    left_item_text VARCHAR2(2000),
    right_item_text VARCHAR2(2000),
    section_id NUMBER(10) NOT NULL,
    decision_condition_id NUMBER(10),
    response_set_id NUMBER(10) NOT NULL,
    regexp VARCHAR2(1000),
    regexp_error_msg VARCHAR2(255),
    ordinal NUMBER NOT NULL,
    required VARCHAR2(1)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..item_form_metadata_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..item_form_metadata_birtrg
  BEFORE INSERT
  ON &clinica..item_form_metadata
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.item_form_metadata_id IS NULL THEN
    SELECT item_form_metadata_seq.NEXTVAL
      INTO :NEW.item_form_metadata_id
      FROM dual;
  END IF;

END;
/


--
-- Name: item_group 
--
CREATE TABLE &clinica..item_group (
    item_group_id NUMBER(10) NOT NULL,
    status_id NUMBER(10),
    NAME VARCHAR2(255),
    date_created DATE,
    date_updated DATE,
    owner_id NUMBER(10),
    update_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..item_group_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..item_group_birtrg
  BEFORE INSERT
  ON &clinica..item_group
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.item_group_id IS NULL THEN
    SELECT item_group_seq.NEXTVAL
      INTO :NEW.item_group_id
      FROM dual;
  END IF;

END;
/


--
-- Name: item_group_map
--
CREATE TABLE &clinica..item_group_map (
    item_group_id NUMBER(10),
    item_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/


--
-- Name: item_reference_type 
--
CREATE TABLE &clinica..item_reference_type (
    item_reference_type_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..item_reference_type_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..item_reference_type_birtrg
  BEFORE INSERT
  ON &clinica..item_reference_type
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.item_reference_type_id IS NULL THEN
    SELECT item_reference_type_seq.NEXTVAL
      INTO :NEW.item_reference_type_id
      FROM dual;
  END IF;

END;
/


--
-- Name: null_value_type 
--
CREATE TABLE &clinica..null_value_type (
    null_value_type_id NUMBER(10) NOT NULL,
    code VARCHAR2(20),
    NAME VARCHAR2(255),
    null_value_type_definition VARCHAR2(1000),
    reference VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..null_value_type_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..null_value_type_birtrg
  BEFORE INSERT
  ON &clinica..null_value_type
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.null_value_type_id IS NULL THEN
    SELECT null_value_type_seq.NEXTVAL
      INTO :NEW.null_value_type_id
      FROM dual;
  END IF;

END;
/


--
-- Name: openclinica_version
--
CREATE TABLE &clinica..openclinica_version (
    NAME VARCHAR2(255),
    test_path VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/


--
-- Name: privilege 
--
CREATE TABLE &clinica..PRIVILEGE (
    priv_id NUMBER(10) NOT NULL,
    priv_name VARCHAR2(50),
    priv_desc VARCHAR2(2000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..privilege_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..privilege_birtrg
  BEFORE INSERT
  ON &clinica..PRIVILEGE
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.priv_id IS NULL THEN
    SELECT privilege_seq.NEXTVAL
      INTO :NEW.priv_id
      FROM dual;
  END IF;

END;
/


--
-- Name: resolution_status
--
CREATE TABLE &clinica..resolution_status (
    resolution_status_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(50),
    description VARCHAR2(255)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..resolution_status_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..resolution_status_birtrg
  BEFORE INSERT
  ON &clinica..resolution_status
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.resolution_status_id IS NULL THEN
    SELECT resolution_status_seq.NEXTVAL
      INTO :NEW.resolution_status_id
      FROM dual;
  END IF;

END;
/


--
-- Name: response_set 
--
CREATE TABLE &clinica..response_set (
    response_set_id NUMBER(10) NOT NULL,
    response_type_id NUMBER(10),
    LABEL VARCHAR2(80),
    options_text VARCHAR2(4000),
    options_values VARCHAR2(4000),
    version_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..response_set_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..response_set_birtrg
  BEFORE INSERT
  ON &clinica..response_set
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.response_set_id IS NULL THEN
    SELECT response_set_seq.NEXTVAL
      INTO :NEW.response_set_id
      FROM dual;
  END IF;

END;
/


--
-- Name: response_type 
--
CREATE TABLE &clinica..response_type (
    response_type_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..response_type_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..response_type_birtrg
  BEFORE INSERT
  ON &clinica..response_type
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.response_type_id IS NULL THEN
    SELECT response_type_seq.NEXTVAL
      INTO :NEW.response_type_id
      FROM dual;
  END IF;

END;
/


--
-- Name: role_privilege_map 
--
CREATE TABLE &clinica..role_privilege_map (
    role_id NUMBER(10) NOT NULL,
    priv_id NUMBER(10) NOT NULL,
    priv_value VARCHAR2(50)
) 
TABLESPACE &clinica_data_ts
/


--
-- Name: section 
--
CREATE TABLE &clinica..SECTION (
    section_id NUMBER(10) NOT NULL,
    crf_version_id NUMBER(10) NOT NULL,
    status_id NUMBER(10),
    LABEL VARCHAR2(2000),
    title VARCHAR2(2000),
    subtitle VARCHAR2(2000),
    instructions VARCHAR2(2000),
    page_number_label VARCHAR2(5),
    ordinal NUMBER(10),
    parent_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    owner_id NUMBER(10) NOT NULL,
    update_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..section_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..section_birtrg
  BEFORE INSERT
  ON &clinica..SECTION
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.section_id IS NULL THEN
    SELECT section_seq.NEXTVAL
      INTO :NEW.section_id
      FROM dual;
  END IF;

END;
/


--
-- Name: skip_rule 
--
CREATE TABLE &clinica..skip_rule (
    rule_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    instruction_text VARCHAR2(4000),
    condition VARCHAR2(4000),
    assignments VARCHAR2(4000),
    crf_version_id NUMBER(10),
    status_id NUMBER(10),
    owner_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    update_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..skip_rule_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..skip_rule_birtrg
  BEFORE INSERT
  ON &clinica..skip_rule
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.rule_id IS NULL THEN
    SELECT skip_rule_seq.NEXTVAL
      INTO :NEW.rule_id
      FROM dual;
  END IF;

END;
/


--
-- Name: status
--
CREATE TABLE &clinica..status (
    status_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..status_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..status_birtrg
  BEFORE INSERT
  ON &clinica..status
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.status_id IS NULL THEN
    SELECT status_seq.NEXTVAL
      INTO :NEW.status_id
      FROM dual;
  END IF;

END;
/


--
-- Name: study 
--
CREATE TABLE &clinica..study (
    study_id NUMBER(10) NOT NULL,
    parent_study_id NUMBER(10),
    unique_identifier VARCHAR2(30),
    secondary_identifier VARCHAR2(255),
    NAME VARCHAR2(60),
    SUMMARY VARCHAR2(255) NOT NULL,
    date_planned_start DATE,
    date_planned_end DATE,
    date_created DATE,
    date_updated DATE,
    owner_id NUMBER(10),
    update_id NUMBER(10),
    type_id NUMBER(10),
    status_id NUMBER(10),
    principal_investigator VARCHAR2(255),
    facility_name VARCHAR2(255),
    facility_city VARCHAR2(255),
    facility_state VARCHAR2(20),
    facility_zip VARCHAR2(64),
    facility_country VARCHAR2(64),
    facility_recruitment_status VARCHAR2(60),
    facility_contact_name VARCHAR2(255),
    facility_contact_degree VARCHAR2(255),
    facility_contact_phone VARCHAR2(255),
    facility_contact_email VARCHAR2(255),
    protocol_type VARCHAR2(30),
    protocol_description VARCHAR2(1000),
    protocol_date_verification DATE,
    phase VARCHAR2(30),
    expected_total_enrollment NUMBER(10),
    sponsor VARCHAR2(255),
    collaborators VARCHAR2(1000),
    medline_identifier VARCHAR2(255),
    url VARCHAR2(255),
    url_description VARCHAR2(255),
    conditions VARCHAR2(500),
    keywords VARCHAR2(255),
    eligibility VARCHAR2(500),
    gender VARCHAR2(30),
    age_max VARCHAR2(3),
    age_min VARCHAR2(3),
    healthy_volunteer_accepted VARCHAR2(1),
    purpose VARCHAR2(64),
    allocation VARCHAR2(64),
    masking VARCHAR2(30),
    control VARCHAR2(30),
    assignment VARCHAR2(30),
    endpoint VARCHAR2(64),
    interventions VARCHAR2(1000),
    duration VARCHAR2(30),
    selection VARCHAR2(30),
    timing VARCHAR2(30),
    official_title VARCHAR2(255),
    results_reference VARCHAR2(1),
    collect_dob VARCHAR2(1),
    discrepancy_management VARCHAR2(1)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_birtrg
  BEFORE INSERT
  ON &clinica..study
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_id IS NULL THEN
    SELECT study_seq.NEXTVAL
      INTO :NEW.study_id
      FROM dual;
  END IF;

END;
/


--
-- Name: study_event 
--
CREATE TABLE &clinica..study_event (
    study_event_id NUMBER(10) NOT NULL,
    study_event_definition_id NUMBER(10),
    study_subject_id NUMBER(10),
    LOCATION VARCHAR2(2000),
    sample_ordinal NUMBER(10),
    date_start DATE,
    date_end DATE,
    owner_id NUMBER(10),
    status_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    update_id NUMBER(10),
    subject_event_status_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_event_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_event_birtrg
  BEFORE INSERT
  ON &clinica..study_event
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_event_id IS NULL THEN
    SELECT study_event_seq.NEXTVAL
      INTO :NEW.study_event_id
      FROM dual;
  END IF;

END;
/


--
-- Name: study_event_definition 
--
CREATE TABLE &clinica..study_event_definition (
    study_event_definition_id NUMBER(10) NOT NULL,
    study_id NUMBER(10),
    NAME VARCHAR2(2000),
    description VARCHAR2(2000),
    repeating VARCHAR2(1),
    study_event_definition_type VARCHAR2(20),
    CATEGORY VARCHAR2(2000),
    owner_id NUMBER(10),
    status_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    update_id NUMBER(10),
    ordinal NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_event_definition_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_event_definition_birtrg
  BEFORE INSERT
  ON &clinica..study_event_definition
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_event_definition_id IS NULL THEN
    SELECT study_event_definition_seq.NEXTVAL
      INTO :NEW.study_event_definition_id
      FROM dual;
  END IF;

END;
/


--
-- Name: study_group 
--
CREATE TABLE &clinica..study_group (
    study_group_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(1000),
    study_group_class_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_group_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_group_birtrg
  BEFORE INSERT
  ON &clinica..study_group
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_group_id IS NULL THEN
    SELECT study_group_seq.NEXTVAL
      INTO :NEW.study_group_id
      FROM dual;
  END IF;

END;
/



--
-- Name: study_group_class 
--
CREATE TABLE &clinica..study_group_class (
    study_group_class_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(30),
    study_id NUMBER(10),
    owner_id NUMBER(10),
    date_created DATE,
    group_class_type_id NUMBER(10),
    status_id NUMBER(10),
    date_updated DATE,
    update_id NUMBER(10),
    subject_assignment VARCHAR2(30)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_group_class_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_group_class_birtrg
  BEFORE INSERT
  ON &clinica..study_group_class
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_group_class_id IS NULL THEN
    SELECT study_group_class_seq.NEXTVAL
      INTO :NEW.study_group_class_id
      FROM dual;
  END IF;

END;
/




--
-- Name: study_subject
--
CREATE TABLE &clinica..study_subject (
    study_subject_id NUMBER(10) NOT NULL,
    LABEL VARCHAR2(30),
    secondary_label VARCHAR2(30),
    subject_id NUMBER(10),
    study_id NUMBER(10),
    status_id NUMBER(10),
    enrollment_date DATE,
    date_created DATE,
    date_updated DATE,
    owner_id NUMBER(10),
    update_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_subject_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_subject_birtrg
  BEFORE INSERT
  ON &clinica..study_subject
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_subject_id IS NULL THEN
    SELECT study_subject_seq.NEXTVAL
      INTO :NEW.study_subject_id
      FROM dual;
  END IF;

END;
/



--
-- Name: study_type
--
CREATE TABLE &clinica..study_type (
    study_type_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..study_type_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..study_type_birtrg
  BEFORE INSERT
  ON &clinica..study_type
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.study_type_id IS NULL THEN
    SELECT study_type_seq.NEXTVAL
      INTO :NEW.study_type_id
      FROM dual;
  END IF;

END;
/


--
-- Name: study_user_role
--
CREATE TABLE &clinica..study_user_role (
    role_name VARCHAR2(40),
    study_id NUMBER(10),
    status_id NUMBER(10),
    owner_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    update_id NUMBER(10),
    user_name VARCHAR2(40)
) 
TABLESPACE &clinica_data_ts
/


--
-- Name: subject 
--
CREATE TABLE &clinica..subject (
    subject_id NUMBER(10) NOT NULL,
    father_id NUMBER(10),
    mother_id NUMBER(10),
    status_id NUMBER(10),
    date_of_birth DATE,
    gender VARCHAR2(1),
    unique_identifier VARCHAR2(255),
    date_created DATE,
    owner_id NUMBER(10),
    date_updated DATE,
    update_id NUMBER(10),
    dob_collected VARCHAR2(1)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..subject_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..subject_birtrg
  BEFORE INSERT
  ON &clinica..subject
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.subject_id IS NULL THEN
    SELECT subject_seq.NEXTVAL
      INTO :NEW.subject_id
      FROM dual;
  END IF;

END;
/


--
-- Name: subject_event_status 
--
CREATE TABLE &clinica..subject_event_status (
    subject_event_status_id NUMBER(10) NOT NULL,
    NAME VARCHAR2(255),
    description VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..subject_event_status_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..subject_event_status_birtrg
  BEFORE INSERT
  ON &clinica..subject_event_status
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.subject_event_status_id IS NULL THEN
    SELECT subject_event_status_seq.NEXTVAL
      INTO :NEW.subject_event_status_id
      FROM dual;
  END IF;

END;
/


--
-- Name: subject_group_map 
--
CREATE TABLE &clinica..subject_group_map (
    subject_group_map_id NUMBER(10) NOT NULL,
    study_group_class_id NUMBER(10),
    study_subject_id NUMBER(10),
    study_group_id NUMBER(10),
    status_id NUMBER(10),
    owner_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    update_id NUMBER(10),
    notes VARCHAR2(255)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..subject_group_map_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..subject_group_map_birtrg
  BEFORE INSERT
  ON &clinica..subject_group_map
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.subject_group_map_id IS NULL THEN
    SELECT subject_group_map_seq.NEXTVAL
      INTO :NEW.subject_group_map_id
      FROM dual;
  END IF;

END;
/


--
-- Name: test_table_three
--
CREATE TABLE &clinica..test_table_three (
    subject_id NUMBER(10),
    subject_identifier VARCHAR2(30),
    study_id NUMBER(10),
    study_identifier VARCHAR2(30),
    event_definition_crf_id NUMBER(10),
    crf_id NUMBER(10),
    crf_description VARCHAR2(2048),
    crf_name VARCHAR2(255),
    crf_version_id NUMBER(10),
    crf_version_revision_notes VARCHAR2(255),
    crf_version_name VARCHAR2(255),
    study_event_id NUMBER(10),
    event_crf_id NUMBER(10),
    item_data_id NUMBER(10),
    VALUE VARCHAR2(255),
    study_event_definition_name VARCHAR2(2000),
    study_event_def_repeating VARCHAR2(1),
    sample_ordinal NUMBER(10),
    item_id NUMBER(10),
    item_name VARCHAR2(255),
    item_description VARCHAR2(4000),
    item_units VARCHAR2(64),
    date_created DATE,
    study_event_definition_id NUMBER(10),
    options_text VARCHAR2(4000),
    options_values VARCHAR2(4000),
    response_type_id NUMBER(10),
    gender VARCHAR2(1),
    date_of_birth DATE,
    LOCATION VARCHAR2(2000),
    date_start DATE,
    date_end DATE
) 
TABLESPACE &clinica_data_ts
/


--
-- Name: user_account
--
CREATE TABLE &clinica..user_account (
    user_id NUMBER(10) NOT NULL,
    user_name VARCHAR2(64),
    passwd VARCHAR2(255),
    first_name VARCHAR2(50),
    last_name VARCHAR2(50),
    email VARCHAR2(120),
    active_study NUMBER(10),
    institutional_affiliation VARCHAR2(255),
    status_id NUMBER(10),
    owner_id NUMBER(10),
    date_created DATE,
    date_updated DATE,
    date_lastvisit TIMESTAMP,
    passwd_timestamp DATE,
    passwd_challenge_question VARCHAR2(64),
    passwd_challenge_answer VARCHAR2(255),
    phone VARCHAR2(64),
    user_type_id NUMBER(10),
    update_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..user_account_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..user_account_birtrg
  BEFORE INSERT
  ON &clinica..user_account
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.user_id IS NULL THEN
    SELECT user_account_seq.NEXTVAL
      INTO :NEW.user_id
      FROM dual;
  END IF;

END;
/


--
-- Name: user_role 
--
CREATE TABLE &clinica..user_role (
    role_id NUMBER(10) NOT NULL,
    role_name VARCHAR2(50) NOT NULL,
    parent_id NUMBER(10),
    role_desc VARCHAR2(2000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..user_role_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..user_role_birtrg
  BEFORE INSERT
  ON &clinica..user_role
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.role_id IS NULL THEN
    SELECT user_role_seq.NEXTVAL
      INTO :NEW.role_id
      FROM dual;
  END IF;

END;
/


--
-- Name: user_type 
--
CREATE TABLE &clinica..user_type (
    user_type_id NUMBER(10) NOT NULL,
    user_type VARCHAR2(50)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..user_type_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..user_type_birtrg
  BEFORE INSERT
  ON &clinica..user_type
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.user_type_id IS NULL THEN
    SELECT user_type_seq.NEXTVAL
      INTO :NEW.user_type_id
      FROM dual;
  END IF;

END;
/


--
-- Name: versioning_map
--
CREATE TABLE &clinica..versioning_map (
    crf_version_id NUMBER(10),
    item_id NUMBER(10)
) 
TABLESPACE &clinica_data_ts
/


-- -----------------------------------------------------------------------------
-- Create constraints
-- -----------------------------------------------------------------------------
--
-- Name: discrepancy_note_pk
--
ALTER TABLE &clinica..discrepancy_note
  ADD CONSTRAINT discrepancy_note_pk PRIMARY KEY (discrepancy_note_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: discrepancy_note_type_pk 
--
ALTER TABLE &clinica..discrepancy_note_type
  ADD CONSTRAINT discrepancy_note_type_pk PRIMARY KEY (discrepancy_note_type_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: item_data_pk
--
ALTER TABLE &clinica..item_data
  ADD CONSTRAINT item_data_pk PRIMARY KEY (item_data_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: archived_dataset_file_pk 
--
ALTER TABLE &clinica..archived_dataset_file
  ADD CONSTRAINT archived_dataset_file_pk PRIMARY KEY (archived_dataset_file_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: audit_event_pk 
--
ALTER TABLE &clinica..audit_event
  ADD CONSTRAINT audit_event_pk PRIMARY KEY (audit_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: completion_status_pk
--
ALTER TABLE &clinica..completion_status
  ADD CONSTRAINT completion_status_pk PRIMARY KEY (completion_status_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: dc_computed_event_pk 
--
ALTER TABLE &clinica..dc_computed_event
  ADD CONSTRAINT dc_computed_event_pk PRIMARY KEY (dc_summary_event_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: dc_event_pk 
--
ALTER TABLE &clinica..dc_event
  ADD CONSTRAINT dc_event_pk PRIMARY KEY (dc_event_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: dc_primitive_pk 
--
ALTER TABLE &clinica..dc_primitive
  ADD CONSTRAINT dc_primitive_pk PRIMARY KEY (dc_primitive_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: dc_section_event_uk 
--
ALTER TABLE &clinica..dc_section_event
  ADD CONSTRAINT dc_section_event_uk UNIQUE (dc_event_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: dc_send_email_event_uk 
--
ALTER TABLE &clinica..dc_send_email_event
  ADD CONSTRAINT dc_send_email_event_uk UNIQUE (dc_event_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: dc_substitution_event_uk 
--
ALTER TABLE &clinica..dc_substitution_event
  ADD CONSTRAINT dc_substitution_event_uk UNIQUE (dc_event_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: decision_condition_pk 
--
ALTER TABLE &clinica..decision_condition
  ADD CONSTRAINT decision_condition_pk PRIMARY KEY (decision_condition_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: event_crf_pk 
--
ALTER TABLE &clinica..event_crf
  ADD CONSTRAINT event_crf_pk PRIMARY KEY (event_crf_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: export_format_pk 
--
ALTER TABLE &clinica..export_format
  ADD CONSTRAINT export_format_pk PRIMARY KEY (export_format_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: group_role_pk 
--
ALTER TABLE &clinica..study_group
  ADD CONSTRAINT group_role_pk PRIMARY KEY (study_group_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: group_types_pk 
--
ALTER TABLE &clinica..group_class_types
  ADD CONSTRAINT group_class_types_pk PRIMARY KEY (group_class_type_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: subject_pk 
--
ALTER TABLE &clinica..subject
  ADD CONSTRAINT subject_pk PRIMARY KEY (subject_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: crf_pk 
--
ALTER TABLE &clinica..crf
  ADD CONSTRAINT crf_pk PRIMARY KEY (crf_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: item_pk 
--
ALTER TABLE &clinica..item
  ADD CONSTRAINT item_pk PRIMARY KEY (item_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: item_data_type_pk 
--
ALTER TABLE &clinica..item_data_type
  ADD CONSTRAINT item_data_type_pk PRIMARY KEY (item_data_type_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: item_reference_type_pk 
--
ALTER TABLE &clinica..item_reference_type
  ADD CONSTRAINT pk_item_reference_type PRIMARY KEY (item_reference_type_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: null_value_type_pk 
--
ALTER TABLE &clinica..null_value_type
  ADD CONSTRAINT null_value_type_pk PRIMARY KEY (null_value_type_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: person_user_pk 
--
ALTER TABLE &clinica..user_account
  ADD CONSTRAINT user_account_pk PRIMARY KEY (user_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: item_group_pk 
--
ALTER TABLE &clinica..item_group
  ADD CONSTRAINT item_group_pk PRIMARY KEY (item_group_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: item_form_metadata_pk 
--
ALTER TABLE &clinica..item_form_metadata
  ADD CONSTRAINT item_form_metadata_pk PRIMARY KEY (item_form_metadata_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: privilege_pk 
--
ALTER TABLE &clinica..PRIVILEGE
  ADD CONSTRAINT privilege_pk PRIMARY KEY (priv_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: study_pk 
--
ALTER TABLE &clinica..study
  ADD CONSTRAINT study_pk PRIMARY KEY (study_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: study_group_class_pk 
--
ALTER TABLE &clinica..study_group_class
  ADD CONSTRAINT study_group_class_pk PRIMARY KEY (study_group_class_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: study_subject_pk 
--
ALTER TABLE &clinica..study_subject
  ADD CONSTRAINT study_subject_pk PRIMARY KEY (study_subject_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: event_defintion_crf_pk 
--
ALTER TABLE &clinica..event_definition_crf
  ADD CONSTRAINT event_definition_crf_pk PRIMARY KEY (event_definition_crf_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: filter_pk 
--
ALTER TABLE &clinica..FILTER
  ADD CONSTRAINT filter_pk PRIMARY KEY (filter_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: dataset_pk 
--
ALTER TABLE &clinica..dataset
  ADD CONSTRAINT dataset_pk PRIMARY KEY (dataset_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: response_type_pk 
--
ALTER TABLE &clinica..response_type
  ADD CONSTRAINT response_type_pk PRIMARY KEY (response_type_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: user_role_pk 
--
ALTER TABLE &clinica..user_role
  ADD CONSTRAINT user_role_pk PRIMARY KEY (role_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: response_set_pk 
--
ALTER TABLE &clinica..response_set
  ADD CONSTRAINT response_set_pk PRIMARY KEY (response_set_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: section_pk
--
ALTER TABLE &clinica..SECTION
  ADD CONSTRAINT section_pk PRIMARY KEY (section_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: skip_rule_pk 
--
ALTER TABLE &clinica..skip_rule
  ADD CONSTRAINT skip_rule_pk PRIMARY KEY (rule_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- NAME: status_pk 
--
ALTER TABLE &clinica..status
  ADD CONSTRAINT status_pk PRIMARY KEY (status_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: study_event_pk 
--
ALTER TABLE &clinica..study_event
  ADD CONSTRAINT study_event_pk PRIMARY KEY (study_event_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: study_event_definition_pk 
--
ALTER TABLE &clinica..study_event_definition
  ADD CONSTRAINT study_event_definition_pk PRIMARY KEY (study_event_definition_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: study_type_pk 
--
ALTER TABLE &clinica..study_type
  ADD CONSTRAINT study_type_pk PRIMARY KEY (study_type_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: subject_event_status_pk 
--
ALTER TABLE &clinica..subject_event_status
  ADD CONSTRAINT subject_event_status_pk PRIMARY KEY (subject_event_status_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: subject_group_map_pk 
--
ALTER TABLE &clinica..subject_group_map
  ADD CONSTRAINT subject_group_map_pk PRIMARY KEY (subject_group_map_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: user_type_pk 
--
ALTER TABLE &clinica..user_type
  ADD CONSTRAINT user_type_pk PRIMARY KEY (user_type_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: crf_versioning_pk
--
ALTER TABLE &clinica..crf_version
  ADD CONSTRAINT crf_versioning_pk PRIMARY KEY (crf_version_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


--
-- Name: resolution_status_pk 
--
ALTER TABLE &clinica..resolution_status
  ADD CONSTRAINT resolution_status_pk PRIMARY KEY (resolution_status_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/


-- -----------------------------------------------------------------------------
-- Create foreign keys
-- -----------------------------------------------------------------------------
--
-- Name: study_event_def_study_fk
--
ALTER TABLE &clinica..study_event_definition
  ADD CONSTRAINT study_event_def_study_fk FOREIGN KEY (study_id) 
  REFERENCES &clinica..study(study_id)
/


--
-- Name: subject_subject_father_fk
--
ALTER TABLE &clinica..subject
  ADD CONSTRAINT subject_subject_father_fk FOREIGN KEY (father_id) 
  REFERENCES &clinica..subject(subject_id) ON DELETE SET NULL
/


--
-- Name: subject_subject_monther_fk
--
ALTER TABLE &clinica..subject
  ADD CONSTRAINT subject_subject_monther_fk FOREIGN KEY (mother_id) 
  REFERENCES &clinica..subject(subject_id) ON DELETE SET NULL
/


--
-- Name: study_study_parent_fk
--
ALTER TABLE &clinica..study
  ADD CONSTRAINT study_study_parent_fk FOREIGN KEY (parent_study_id) 
  REFERENCES &clinica..study(study_id) ON DELETE SET NULL
/


--
-- Name: dc_note_dc_note_type_fk
--
ALTER TABLE &clinica..discrepancy_note
  ADD CONSTRAINT dc_note_dc_note_type_fk FOREIGN KEY (discrepancy_note_type_id) 
  REFERENCES &clinica..discrepancy_note_type(discrepancy_note_type_id)
/


--
-- Name: dc_note_user_account_fk
--
ALTER TABLE &clinica..discrepancy_note
  ADD CONSTRAINT dc_note_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: dc_note_resolution_status_fk
--
ALTER TABLE &clinica..discrepancy_note
  ADD CONSTRAINT dc_note_resolution_status_fk FOREIGN KEY (resolution_status_id) 
  REFERENCES &clinica..resolution_status(resolution_status_id)
/


--
-- Name: discrepancy_note_study_fk
--
ALTER TABLE &clinica..discrepancy_note
  ADD CONSTRAINT discrepancy_note_study_id_fk FOREIGN KEY (study_id) 
  REFERENCES &clinica..study(study_id)
/


--
-- Name: dn_event_crf_map_dc_note_fk
--
ALTER TABLE &clinica..dn_event_crf_map
  ADD CONSTRAINT dn_event_crf_map_dc_note_fk FOREIGN KEY (discrepancy_note_id) 
  REFERENCES &clinica..discrepancy_note(discrepancy_note_id)
/


--
-- Name: dn_event_crf_map_event_crf_fk
--
ALTER TABLE &clinica..dn_event_crf_map
  ADD CONSTRAINT dn_event_crf_map_event_crf_fk FOREIGN KEY (event_crf_id) 
  REFERENCES &clinica..event_crf(event_crf_id)
/


--
-- Name: dn_item_data_map_dc_note_fk
--
ALTER TABLE &clinica..dn_item_data_map
  ADD CONSTRAINT dn_item_data_map_dc_note_fk FOREIGN KEY (discrepancy_note_id) 
  REFERENCES &clinica..discrepancy_note(discrepancy_note_id)
/


--
-- Name: dn_item_data_map_item_data_fk
--
ALTER TABLE &clinica..dn_item_data_map
  ADD CONSTRAINT dn_item_data_map_item_data_fk FOREIGN KEY (item_data_id) 
  REFERENCES &clinica..item_data(item_data_id)
/


--
-- Name: dn_study_event_map_dc_note_fk
--
ALTER TABLE &clinica..dn_study_event_map
  ADD CONSTRAINT dn_study_event_map_dc_note_fk FOREIGN KEY (discrepancy_note_id) 
  REFERENCES &clinica..discrepancy_note(discrepancy_note_id)
/


--
-- Name: dn_stdy_event_mp_stdy_event_fk
--
ALTER TABLE &clinica..dn_study_event_map
  ADD CONSTRAINT dn_stdy_event_mp_stdy_event_fk FOREIGN KEY (study_event_id) 
  REFERENCES &clinica..study_event(study_event_id)
/


--
-- Name: dn_stdy_subject_map_dc_note_fk
--
ALTER TABLE &clinica..dn_study_subject_map
  ADD CONSTRAINT dn_stdy_subject_map_dc_note_fk FOREIGN KEY (discrepancy_note_id) 
  REFERENCES &clinica..discrepancy_note(discrepancy_note_id)
/


--
-- Name: dn_stdy_subj_map_stdy_subj_fk
--
ALTER TABLE &clinica..dn_study_subject_map
  ADD CONSTRAINT dn_stdy_subj_map_stdy_subj_fk FOREIGN KEY (study_subject_id) 
  REFERENCES &clinica..study_subject(study_subject_id)
/


--
-- Name: dn_subject_map_dc_note_fk
--
ALTER TABLE &clinica..dn_subject_map
  ADD CONSTRAINT dn_subject_map_dc_note_fk FOREIGN KEY (discrepancy_note_id) 
  REFERENCES &clinica..discrepancy_note(discrepancy_note_id)
/


--
-- Name: dn_subject_map_subject_fk
--
ALTER TABLE &clinica..dn_subject_map
  ADD CONSTRAINT dn_subject_map_subject_fk FOREIGN KEY (subject_id) 
  REFERENCES &clinica..subject(subject_id)
/


--
-- Name: item_data_item_fk
--
ALTER TABLE &clinica..item_data
  ADD CONSTRAINT item_data_item_fk FOREIGN KEY (item_id) 
  REFERENCES &clinica..item(item_id)
/


--
-- Name: arch_dataset_file_dataset_fk
--
ALTER TABLE &clinica..archived_dataset_file
  ADD CONSTRAINT arch_dataset_file_dataset_fk FOREIGN KEY (dataset_id) 
  REFERENCES &clinica..dataset(dataset_id)
/


--
-- Name: arch_dataset_file_exp_frmt_fk
--
ALTER TABLE &clinica..archived_dataset_file
  ADD CONSTRAINT arch_dataset_file_exp_frmt_fk FOREIGN KEY (export_format_id) 
  REFERENCES &clinica..export_format(export_format_id)
/


--
-- Name: audit_event_ref_audit_event_fk
--
ALTER TABLE &clinica..audit_event_context
  ADD CONSTRAINT audit_event_ref_audit_event_fk FOREIGN KEY (audit_id) 
  REFERENCES &clinica..audit_event(audit_id)
/


--
-- Name: audit_event_val_audit_event_fk 
--
ALTER TABLE &clinica..audit_event_values
  ADD CONSTRAINT audit_event_val_audit_event FOREIGN KEY (audit_id) 
  REFERENCES &clinica..audit_event(audit_id)
/


--
-- Name: completion_status_status_fk
--
ALTER TABLE &clinica..completion_status
  ADD CONSTRAINT completion_status_status FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: crf_user_account_fk
--
ALTER TABLE &clinica..crf
  ADD CONSTRAINT crf_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: crf_status_fk
--
ALTER TABLE &clinica..crf
  ADD CONSTRAINT crf_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: crf_version_user_account_fk
--
ALTER TABLE &clinica..crf_version
  ADD CONSTRAINT crf_version_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: crf_version_status_fk
--
ALTER TABLE &clinica..crf_version
  ADD CONSTRAINT crf_version_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: ds_crf_ver_mp_event_def_crf_fk
--
ALTER TABLE &clinica..dataset_crf_version_map
  ADD CONSTRAINT ds_crf_ver_mp_event_def_crf_fk FOREIGN KEY (event_definition_crf_id) 
  REFERENCES &clinica..event_definition_crf(event_definition_crf_id)
/


--
-- Name: dataset_crf_ver_map_dataset_fk
--
ALTER TABLE &clinica..dataset_crf_version_map
  ADD CONSTRAINT dataset_crf_ver_map_dataset FOREIGN KEY (dataset_id) 
  REFERENCES &clinica..dataset(dataset_id)
/


--
-- Name: dataset_status_fk
--
ALTER TABLE &clinica..dataset
  ADD CONSTRAINT dataset_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: dataset_study_fk
--
ALTER TABLE &clinica..dataset
  ADD CONSTRAINT datase_study_fk FOREIGN KEY (study_id) 
  REFERENCES &clinica..study(study_id)
/


--
-- Name: dataset_user_account_fk
--
ALTER TABLE &clinica..dataset
  ADD CONSTRAINT dataset_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: dataset_filter_map_dataset_fk
--
ALTER TABLE &clinica..dataset_filter_map
  ADD CONSTRAINT dataset_filter_map_dataset_fk FOREIGN KEY (dataset_id) 
  REFERENCES &clinica..dataset(dataset_id)
/


--
-- Name: dataset_filter_map_filter_fk
--
ALTER TABLE &clinica..dataset_filter_map
  ADD CONSTRAINT dataset_filter_map_filter_fk FOREIGN KEY (filter_id) 
  REFERENCES &clinica..FILTER(filter_id)
/


--
-- Name: dc_computed_event_dc_event_fk
--
ALTER TABLE &clinica..dc_computed_event
  ADD CONSTRAINT dc_computed_event_dc_event_fk FOREIGN KEY (dc_event_id) 
  REFERENCES &clinica..dc_event(dc_event_id)
/


--
-- Name: dc_event_decision_condition_fk
--
ALTER TABLE &clinica..dc_event
  ADD CONSTRAINT dc_event_decision_condition_fk FOREIGN KEY (decision_condition_id) 
  REFERENCES &clinica..decision_condition(decision_condition_id)
/


--
-- Name: dc_primitive_decision_cond_fk
--
ALTER TABLE &clinica..dc_primitive
  ADD CONSTRAINT dc_primitive_decision_cond_fk FOREIGN KEY (decision_condition_id) 
  REFERENCES &clinica..decision_condition(decision_condition_id)
/


--
-- Name: dc_primitive_item_dyn_val_fk
--
ALTER TABLE &clinica..dc_primitive
  ADD CONSTRAINT dc_primitive_item_dyn_val_fk FOREIGN KEY (dynamic_value_item_id) 
  REFERENCES &clinica..item(item_id)
/


--
-- Name: dc_primitive_item_item_fk
--
ALTER TABLE &clinica..dc_primitive
  ADD CONSTRAINT dc_primitive_item_item_fk FOREIGN KEY (item_id) 
  REFERENCES &clinica..item(item_id)
/


--
-- Name: dc_section_event_dc_event_fk
--
ALTER TABLE &clinica..dc_section_event
  ADD CONSTRAINT fk_dc_secti_fk_dc_sec_dc_event FOREIGN KEY (dc_event_id) 
  REFERENCES &clinica..dc_event(dc_event_id)
/


--
-- Name: dc_send_eml_event_dc_event_fk
--
ALTER TABLE &clinica..dc_send_email_event
  ADD CONSTRAINT dc_send_eml_event_dc_event_fk FOREIGN KEY (dc_event_id) 
  REFERENCES &clinica..dc_event(dc_event_id) 
/


--
-- Name: dc_subst_event_dc_event_fk
--
ALTER TABLE &clinica..dc_substitution_event
  ADD CONSTRAINT dc_subst_event_dc_event_fk FOREIGN KEY (dc_event_id) 
  REFERENCES &clinica..dc_event(dc_event_id)
/


--
-- Name: dc_subst_event_item_fk
--
ALTER TABLE &clinica..dc_substitution_event
  ADD CONSTRAINT dc_subst_event_item_fk FOREIGN KEY (item_id) 
  REFERENCES &clinica..item(item_id)
/


--
-- Name: ddc_sum_item_map_dc_comp_ent_fk
--
ALTER TABLE &clinica..dc_summary_item_map
  ADD CONSTRAINT dc_sum_item_map_dc_comp_ent_fk FOREIGN KEY (dc_summary_event_id) 
  REFERENCES &clinica..dc_computed_event(dc_summary_event_id)
/


--
-- Name: dc_sum_item_map_item_fk
--
ALTER TABLE &clinica..dc_summary_item_map
  ADD CONSTRAINT dc_sum_item_map_item_fk FOREIGN KEY (item_id) 
  REFERENCES &clinica..item(item_id)
/


--
-- Name: decision_condition_crf_vrn_fk
--
ALTER TABLE &clinica..decision_condition
  ADD CONSTRAINT decision_condition_crf_vrn_fk FOREIGN KEY (crf_version_id) 
  REFERENCES &clinica..crf_version(crf_version_id)
/


-- 
-- Name: decision_condition_status_fk
--
ALTER TABLE &clinica..decision_condition
  ADD CONSTRAINT decision_condition_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: decision_condition_user_acc_fk
--
ALTER TABLE &clinica..decision_condition
  ADD CONSTRAINT decision_condition_user_acc_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: evnt_crf_evnt_complet_stat_fk
--
ALTER TABLE &clinica..event_crf
  ADD CONSTRAINT evnt_crf_evnt_complet_stat_fk FOREIGN KEY (completion_status_id) 
  REFERENCES &clinica..completion_status(completion_status_id)
/


--
-- Name: event_crf_status_fk
--
ALTER TABLE &clinica..event_crf
  ADD CONSTRAINT event_crf_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: event_crf_study_event_fk
--
ALTER TABLE &clinica..event_crf
  ADD CONSTRAINT event_crf_study_event_fk FOREIGN KEY (study_event_id)
  REFERENCES &clinica..study_event(study_event_id)
/


--
-- Name: event_crf_user_account_fk
--
ALTER TABLE &clinica..event_crf
  ADD CONSTRAINT event_crf_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: event_crf_study_subject_fk
--
ALTER TABLE &clinica..event_crf
  ADD CONSTRAINT event_crf_study_subject_fk FOREIGN KEY (study_subject_id) 
  REFERENCES &clinica..study_subject(study_subject_id)
/


--
-- Name: event_definition_crf_status_fk
--
ALTER TABLE &clinica..event_definition_crf
  ADD CONSTRAINT event_definition_crf_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: event_def_study_event_def_fk
--
ALTER TABLE &clinica..event_definition_crf
  ADD CONSTRAINT event_def_study_event_def_fk FOREIGN KEY (study_event_definition_id) 
  REFERENCES &clinica..study_event_definition(study_event_definition_id)
/


--
-- Name: event_def_crf_user_acct_fk
--
ALTER TABLE &clinica..event_definition_crf
  ADD CONSTRAINT event_def_crf_user_acct_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: filter_crf_ver_map_crf_ver_fk
--
ALTER TABLE &clinica..filter_crf_version_map
  ADD CONSTRAINT filter_crf_ver_map_crf_ver_fk FOREIGN KEY (crf_version_id) 
  REFERENCES &clinica..crf_version(crf_version_id)
/


--
-- Name: filter_crf_ver_map_filter_fk
--
ALTER TABLE &clinica..filter_crf_version_map
  ADD CONSTRAINT filter_crf_ver_map_filter_fk FOREIGN KEY (filter_id) 
  REFERENCES &clinica..FILTER(filter_id)
/


--
-- Name: filter_status_fk
--
ALTER TABLE &clinica..FILTER
  ADD CONSTRAINT filter_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: filter_user_account_fk
--
ALTER TABLE &clinica..FILTER
  ADD CONSTRAINT filter_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: stdy_group_stdy_group_class_fk
--
ALTER TABLE &clinica..study_group
  ADD CONSTRAINT stdy_group_stdy_group_class_fk FOREIGN KEY (study_group_class_id) 
  REFERENCES &clinica..study_group_class(study_group_class_id) 
  ON DELETE SET NULL
/
  

--
-- Name: item_data_status_fk
--
ALTER TABLE &clinica..item_data
  ADD CONSTRAINT item_data_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: item_data_user_account_fk
--
ALTER TABLE &clinica..item_data
  ADD CONSTRAINT item_data_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: item_item_reference_type_fk
--
ALTER TABLE &clinica..item
  ADD CONSTRAINT item_item_reference_type_fk FOREIGN KEY (item_reference_type_id) 
  REFERENCES &clinica..item_reference_type(item_reference_type_id) 
/


--
-- Name: item_item_data_type_fk
--
ALTER TABLE &clinica..item
  ADD CONSTRAINT item_item_data_type_fk FOREIGN KEY (item_data_type_id) 
  REFERENCES &clinica..item_data_type(item_data_type_id)
/


--
-- Name: item_status_fk
--
ALTER TABLE &clinica..item
  ADD CONSTRAINT item_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: item_user_account_fk
--
ALTER TABLE &clinica..item
  ADD CONSTRAINT item_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: item_group_map_item_fk
--
ALTER TABLE &clinica..item_group_map
  ADD CONSTRAINT item_group_map_item_fk FOREIGN KEY (item_id) 
  REFERENCES &clinica..item(item_id)
/


--
-- Name: item_group_map_item_group_fk
--
ALTER TABLE &clinica..item_group_map
  ADD CONSTRAINT item_group_map_item_group_fk FOREIGN KEY (item_group_id) 
  REFERENCES &clinica..item_group(item_group_id)
/


--
-- Name: item_group_status_fk
--
ALTER TABLE &clinica..item_group
  ADD CONSTRAINT item_group_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: item_group_user_account_fk
--
ALTER TABLE &clinica..item_group
  ADD CONSTRAINT item_group_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: item_form_metadata_item_fk
--
ALTER TABLE &clinica..item_form_metadata
  ADD CONSTRAINT item_form_metadata_item_fk FOREIGN KEY (item_id) 
  REFERENCES &clinica..item(item_id)
/


--
-- Name: item_data_event_crf_fk
--
ALTER TABLE &clinica..item_data
  ADD CONSTRAINT item_data_event_crf_fk FOREIGN KEY (event_crf_id) 
  REFERENCES &clinica..event_crf(event_crf_id)
/


--
-- Name: study_user_role_study_fk
--
ALTER TABLE &clinica..study_user_role
  ADD CONSTRAINT study_user_role_study_fk FOREIGN KEY (study_id) 
  REFERENCES &clinica..study(study_id)
/


--
-- Name: role_priv_map_privilege_fk
--
ALTER TABLE &clinica..role_privilege_map
  ADD CONSTRAINT role_priv_map_privilege_fk FOREIGN KEY (priv_id) 
  REFERENCES &clinica..PRIVILEGE(priv_id)
/


--
-- Name: study_subject_study_fk
--
ALTER TABLE &clinica..study_subject
  ADD CONSTRAINT study_subject_study_fk FOREIGN KEY (study_id) 
  REFERENCES &clinica..study(study_id)
/


--
-- Name: response_set_response_type_fk
--
ALTER TABLE &clinica..response_set
  ADD CONSTRAINT response_set_response_type_fk FOREIGN KEY (response_type_id) 
  REFERENCES &clinica..response_type(response_type_id)
/


--
-- Name: role_priv_map_user_role_fk
--
ALTER TABLE &clinica..role_privilege_map
  ADD CONSTRAINT role_priv_map_user_role_fk FOREIGN KEY (role_id)
  REFERENCES &clinica..user_role(role_id)
/


--
-- Name: item_form_metadata_resp_set_fk
--
ALTER TABLE &clinica..item_form_metadata
  ADD CONSTRAINT item_form_metadata_resp_set_fk FOREIGN KEY (response_set_id) 
  REFERENCES &clinica..response_set(response_set_id)
/


--
-- Name: item_form_metadata_section_fk
--
ALTER TABLE &clinica..item_form_metadata
  ADD CONSTRAINT item_form_metadata_section_fk FOREIGN KEY (section_id) 
  REFERENCES &clinica..SECTION(section_id)
/


--
-- Name: section_status_fk
--
ALTER TABLE &clinica..SECTION
  ADD CONSTRAINT section_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: section_user_account_fk
--
ALTER TABLE &clinica..SECTION
  ADD CONSTRAINT section_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: section_crf_version_fk
--
ALTER TABLE &clinica..SECTION
  ADD CONSTRAINT section_crf_version_fk FOREIGN KEY (crf_version_id) 
  REFERENCES &clinica..crf_version(crf_version_id)
/


--
-- Name: skip_rule_crf_version_fk
--
ALTER TABLE &clinica..skip_rule
  ADD CONSTRAINT skip_rule_crf_version_fk FOREIGN KEY (crf_version_id) 
  REFERENCES &clinica..crf_version(crf_version_id)
/


--
-- Name: skip_rule_status_fk
--
ALTER TABLE &clinica..skip_rule
  ADD CONSTRAINT skip_rule_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: skip_rule_user_account_fk
--
ALTER TABLE &clinica..skip_rule
  ADD CONSTRAINT skip_rule_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: study_event_status_fk
--
ALTER TABLE &clinica..study_event
  ADD CONSTRAINT study_event_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: study_event_study_event_def_fk
--
ALTER TABLE &clinica..study_event
  ADD CONSTRAINT study_event_study_event_def_fk FOREIGN KEY (study_event_definition_id) 
  REFERENCES &clinica..study_event_definition(study_event_definition_id)
/


--
-- Name: study_event_user_account_fk
--
ALTER TABLE &clinica..study_event
  ADD CONSTRAINT study_event_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: study_event_def_status_fk
--
ALTER TABLE &clinica..study_event_definition
  ADD CONSTRAINT study_event_def_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: study_event_def_usr_account_fk
--
ALTER TABLE &clinica..study_event_definition
  ADD CONSTRAINT study_event_def_usr_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: study_event_study_subject_fk
--
ALTER TABLE &clinica..study_event
  ADD CONSTRAINT study_event_study_subject_fk FOREIGN KEY (study_subject_id) 
  REFERENCES &clinica..study_subject(study_subject_id)
/


--
-- Name: study_status_fk
--
ALTER TABLE &clinica..study
  ADD CONSTRAINT study_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: study_user_account_fk
--
ALTER TABLE &clinica..study
  ADD CONSTRAINT study_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: study_grp_class_group_types_fk
--
ALTER TABLE &clinica..study_group_class
  ADD CONSTRAINT study_grp_class_group_types_fk FOREIGN KEY (group_class_type_id) 
  REFERENCES &clinica..group_class_types(group_class_type_id)
/


--
-- Name: study_group_class_status_fk
--
ALTER TABLE &clinica..study_group_class
  ADD CONSTRAINT study_group_class_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: study_group_class_user_acct_fk
--
ALTER TABLE &clinica..study_group_class
  ADD CONSTRAINT study_group_class_user_acct_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: event_definition_crf_crf_fk
--
ALTER TABLE &clinica..event_definition_crf
  ADD CONSTRAINT event_definition_crf_crf_fk FOREIGN KEY (crf_id) 
  REFERENCES &clinica..crf(crf_id)
/


--
-- Name: event_definition_crf_study_fk
--
ALTER TABLE &clinica..event_definition_crf
  ADD CONSTRAINT event_definition_crf_study_fk FOREIGN KEY (study_id) 
  REFERENCES &clinica..study(study_id)
/


--
-- Name: study_subject_subject_fk
--
ALTER TABLE &clinica..study_subject
  ADD CONSTRAINT study_subject_subject_fk FOREIGN KEY (subject_id) 
  REFERENCES &clinica..subject(subject_id)
/


--
-- Name: study_subject_status_fk
--
ALTER TABLE &clinica..study_subject
  ADD CONSTRAINT study_subject_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: study_subject_user_account_fk
--
ALTER TABLE &clinica..study_subject
  ADD CONSTRAINT study_subject_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: study_study_type_fk
--
ALTER TABLE &clinica..study
  ADD CONSTRAINT study_study_type_fk FOREIGN KEY (type_id) 
  REFERENCES &clinica..study_type(study_type_id)
/


--
-- Name: study_user_role_status_fk
--
ALTER TABLE &clinica..study_user_role
  ADD CONSTRAINT study_user_role_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: study_user_role_user_acct_fk
--
ALTER TABLE &clinica..study_user_role
  ADD CONSTRAINT study_user_role_user_acct_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: subject_group_map_user_acct_fk
--
ALTER TABLE &clinica..subject_group_map
  ADD CONSTRAINT subject_group_map_user_acct_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: subj_group_map_subj_group_fk
--
ALTER TABLE &clinica..subject_group_map
  ADD CONSTRAINT subj_group_map_subj_group_fk FOREIGN KEY (study_group_id) 
  REFERENCES &clinica..study_group(study_group_id)
/


--
-- Name: subject_group_map_status_fk
--
ALTER TABLE &clinica..subject_group_map
  ADD CONSTRAINT subject_group_map_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: subj_grp_map_subj_grp_class_fk
--
ALTER TABLE &clinica..subject_group_map
  ADD CONSTRAINT subj_grp_map_subj_grp_class_fk FOREIGN KEY (study_group_class_id) 
  REFERENCES &clinica..study_group_class(study_group_class_id)
/


--
-- Name: subj_group_map_study_subj_fk
--
ALTER TABLE &clinica..subject_group_map
  ADD CONSTRAINT subj_group_map_study_subj_fk FOREIGN KEY (study_subject_id) 
  REFERENCES &clinica..study_subject(study_subject_id)
/


--
-- Name: subject_status_fk
--
ALTER TABLE &clinica..subject
  ADD CONSTRAINT subject_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: subject_user_account_fk
--
ALTER TABLE &clinica..subject
  ADD CONSTRAINT subject_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: event_crf_crf_version_fk
--
ALTER TABLE &clinica..event_crf
  ADD CONSTRAINT event_crf_crf_version_fk FOREIGN KEY (crf_version_id) 
  REFERENCES &clinica..crf_version(crf_version_id)
/


--
-- Name: user_account_user_account_fk
--
ALTER TABLE &clinica..user_account
  ADD CONSTRAINT user_account_user_account_fk FOREIGN KEY (owner_id) 
  REFERENCES &clinica..user_account(user_id)
/


--
-- Name: user_account_user_type_fk
--
ALTER TABLE &clinica..user_account
  ADD CONSTRAINT user_account_user_type_fk FOREIGN KEY (user_type_id) 
  REFERENCES &clinica..user_type(user_type_id)
/


--
-- Name: user_account_status_fk
--
ALTER TABLE &clinica..user_account
  ADD CONSTRAINT user_account_status_fk FOREIGN KEY (status_id) 
  REFERENCES &clinica..status(status_id)
/


--
-- Name: versioning_map_crf_version_fk
--
ALTER TABLE &clinica..versioning_map
  ADD CONSTRAINT versioning_map_crf_version_fk FOREIGN KEY (crf_version_id) 
  REFERENCES &clinica..crf_version(crf_version_id)
/


--
-- Name: versioning_map_item_fk
--
ALTER TABLE &clinica..versioning_map
  ADD CONSTRAINT versioning_map_item_fk FOREIGN KEY (item_id) 
  REFERENCES &clinica..item(item_id)
/


--
-- Name: crf_version_crf_fk
--
ALTER TABLE &clinica..crf_version
  ADD CONSTRAINT crf_version_crf_fk FOREIGN KEY (crf_id) 
  REFERENCES &clinica..crf(crf_id)
/


--
-- Name: event_def_crf_crf_version_fk
--
ALTER TABLE &clinica..event_definition_crf
  ADD CONSTRAINT event_def_crf_crf_version_fk FOREIGN KEY (default_version_id) 
  REFERENCES &clinica..crf_version(crf_version_id)
/


-- ---------------------------------------------------------------------------------
-- The things above this line have been applied to both orcl10g and cimsdvdb
-- ---------------------------------------------------------------------------------
-- -----------------------------------------------------------------------------
-- Create auditing triggers
-- -----------------------------------------------------------------------------
--
-- Name: discrepancy_note_aidrtrg; 
--
CREATE OR REPLACE TRIGGER &clinica..discrepancy_note_aidrtrg
  AFTER INSERT OR DELETE 
  ON &clinica..discrepancy_note
  REFERENCING OLD AS OLD NEW AS NEW
  FOR EACH ROW
DECLARE
  v_audit_id                 audit_event.audit_id%TYPE;
  v_event_crf_id             event_crf.event_crf_id%TYPE;
  v_item_id                  item.item_id%TYPE;
  v_study_event_id           study_event.study_event_id%TYPE;
  v_study_subject_id         study_subject.study_subject_id%TYPE;
  v_subject_id               subject.subject_id%TYPE;
  v_dn_type_name             discrepancy_note_type.NAME%TYPE;
  v_resolution_status_name   resolution_status.NAME%TYPE;
  v_item_name                item.NAME%TYPE;
BEGIN
  SELECT audit_event_seq.NEXTVAL
    INTO v_audit_id
    FROM DUAL;

  --do we only do inserts and deletes?
  IF INSERTING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table,
                 user_id, entity_id,
                 reason_for_change, action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Discrepancy Note',
                 :NEW.owner_id, :NEW.discrepancy_note_id,
                 'Added a Discrepancy Note', 'Added a Discrepancy Note'
                );

    SELECT event_crf_id
      INTO v_event_crf_id
      FROM dn_event_crf_map
     WHERE discrepancy_note_id = :NEW.discrepancy_note_id;

    SELECT ID.item_id
      INTO v_item_id
      FROM dn_item_data_map idm, item_data ID
     WHERE idm.item_data_id = ID.item_data_id
       AND idm.discrepancy_note_id = :NEW.discrepancy_note_id;

    SELECT study_event_id
      INTO v_study_event_id
      FROM dn_study_event_map
     WHERE discrepancy_note_id = :NEW.discrepancy_note_id;

    SELECT study_subject_id
      INTO v_study_subject_id
      FROM dn_study_subject_map
     WHERE discrepancy_note_id = :NEW.discrepancy_note_id;

    SELECT subject_id
      INTO v_subject_id
      FROM dn_subject_map
     WHERE discrepancy_note_id = :NEW.discrepancy_note_id;

    INSERT INTO audit_event_context
                (audit_id, event_crf_id, item_id,
                 study_event_id, study_subject_id, subject_id,
                 role_name
                )
         VALUES (v_audit_id, v_event_crf_id, v_item_id,
                 v_study_event_id, v_study_subject_id, v_subject_id,
                 'Discrepancy note created in database'
                );

    --short description, type, resolution status, note
    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Short Description', NULL, :NEW.description
                );

    SELECT NAME
      INTO v_dn_type_name
      FROM discrepancy_note_type
     WHERE discrepancy_note_type_id = :NEW.discrepancy_note_type_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Type', NULL, v_dn_type_name
                );

    SELECT NAME
      INTO v_resolution_status_name
      FROM resolution_status
     WHERE resolution_status_id = :NEW.resolution_status_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Resolution Status', NULL,
                 v_resolution_status_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Note', NULL, :NEW.detailed_notes
                );

    SELECT NAME
      INTO v_item_name
      FROM item
     WHERE item_id = v_item_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Item', NULL, v_item_name
                );
  ELSIF DELETING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table,
                 user_id, entity_id,
                 reason_for_change, action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Discrepancy Note',
                 :OLD.owner_id, :OLD.discrepancy_note_id,
                 'Deleted a Discrepancy Note', 'Deleted a Discrepancy Note'
                );

    SELECT subject_id
      INTO v_subject_id
      FROM dn_subject_map
     WHERE discrepancy_note_id = :OLD.discrepancy_note_id;

    INSERT INTO audit_event_context
                (audit_id, subject_id,
                 role_name
                )
         VALUES (v_audit_id, v_subject_id,
                 'Discrepancy note deleted from database'
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Short Description', NULL, :NEW.description
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Note', NULL, :NEW.detailed_notes
                );
  END IF;
END;
/


--
-- Name: event_crf_aiudrtrg
--
CREATE OR REPLACE TRIGGER &clinica..event_crf_aiudrtrg
  AFTER INSERT OR UPDATE OR DELETE
  ON &clinica..event_crf
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
DECLARE
  v_audit_id                 audit_event.audit_id%TYPE;
  v_crf_name                 crf.NAME%TYPE;
  v_crf_version_name         crf_version.NAME%TYPE;
  v_study_event_def_name     study_event_definition.NAME%TYPE;
  v_study_event_date_start   study_event.date_start%TYPE;
BEGIN
  --CREATES a row in the audit table; at the same time,
  --creates a row in the context table and multiple rows
  --in the values table
  SELECT audit_event_seq.NEXTVAL
    INTO v_audit_id
    FROM dual;

  IF INSERTING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Event CRF', :NEW.owner_id,
                 :NEW.event_crf_id, 'Added an Event CRF',
                 'Started CRF Data Entry'
                );

    INSERT INTO audit_event_context
                (audit_id, event_crf_id, study_event_id,
                 study_subject_id, crf_version_id, role_name
                )
         VALUES (v_audit_id, :NEW.event_crf_id, :NEW.study_event_id,
                 :NEW.study_subject_id, :NEW.crf_version_id, 'created'
                );

    SELECT crf.NAME, crfv.NAME
      INTO v_crf_name, v_crf_version_name
      FROM crf_version crfv, crf
     WHERE crfv.crf_id = crf.crf_id
       AND crfv.crf_version_id = :NEW.crf_version_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'CRF', NULL, v_crf_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'CRF Version', NULL, v_crf_version_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Interviewer', NULL, v_crf_version_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Interview Date', NULL, v_crf_version_name
                );

    SELECT sed.NAME, se.date_start
      INTO v_study_event_def_name, v_study_event_date_start
      FROM study_event_definition sed, study_event se
     WHERE se.study_event_definition_id = sed.study_event_definition_id
       AND se.study_event_id = :NEW.study_event_id;

    INSERT INTO audit_event_values
                (audit_id, column_name,
                 old_value, new_value
                )
         VALUES (v_audit_id, 'Study Event Definition',
                 v_study_event_def_name, v_study_event_def_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name,
                 old_value, new_value
                )
         VALUES (v_audit_id, 'Study Event Start Date',
                 v_study_event_date_start, v_study_event_date_start
                );
                
  ELSIF UPDATING THEN
    --updated, removed, marked complete
    --inserted the basic data first, then other messages
    --errors if we try to insert values first, so we need to
    --set up the audit_event row and then update it later on
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id
                )
         VALUES (v_audit_id, SYSDATE, 'Event CRF', :NEW.update_id,
                 :NEW.event_crf_id
                );

    SELECT crf.NAME, crfv.NAME
      INTO v_crf_name, v_crf_version_name
      FROM crf_version crfv, crf
     WHERE crfv.crf_id = crf.crf_id
       AND crfv.crf_version_id = :NEW.crf_version_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'CRF', NULL, v_crf_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'CRF Version', NULL, v_crf_version_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Interviewer', NULL, v_crf_version_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Interview Date', NULL, v_crf_version_name
                );

    SELECT sed.NAME, se.date_start
      INTO v_study_event_def_name, v_study_event_date_start
      FROM study_event_definition sed, study_event se
     WHERE se.study_event_definition_id = sed.study_event_definition_id
       AND se.study_event_id = :NEW.study_event_id;

    INSERT INTO audit_event_values
                (audit_id, column_name,
                 old_value, new_value
                )
         VALUES (v_audit_id, 'Study Event Definition',
                 v_study_event_def_name, v_study_event_def_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name,
                 old_value, new_value
                )
         VALUES (v_audit_id, 'Study Event Start Date',
                 v_study_event_date_start, v_study_event_date_start
                );

    IF (:OLD.status_id != :NEW.status_id) THEN
      IF (:OLD.status_id = 1 AND :NEW.status_id = 5) THEN
        UPDATE audit_event
           SET reason_for_change = 'Removed Event CRF and all related data',
               action_message = 'Removed Event CRF'
         WHERE audit_id = v_audit_id;

        INSERT INTO audit_event_context
                    (audit_id, event_crf_id,
                     study_event_id, study_subject_id,
                     crf_version_id, role_name
                    )
             VALUES (v_audit_id, :NEW.event_crf_id,
                     :NEW.study_event_id, :NEW.study_subject_id,
                     :NEW.crf_version_id, 'removed'
                    );
                    
      ELSIF (:OLD.status_id = 5 AND :NEW.status_id = 1) THEN
        UPDATE audit_event
           SET reason_for_change = 'Restored Event CRF and all related data',
               action_message = 'Restored Event CRF'
         WHERE audit_id = v_audit_id;

        INSERT INTO audit_event_context
                    (audit_id, event_crf_id,
                     study_event_id, study_subject_id,
                     crf_version_id, role_name
                    )
             VALUES (v_audit_id, :NEW.event_crf_id,
                     :NEW.study_event_id, :NEW.study_subject_id,
                     :NEW.crf_version_id, 'restored'
                    );
                    
      ELSIF (:NEW.status_id = 2) THEN
        UPDATE audit_event
           SET reason_for_change = 'Marked CRF and all its items as complete',
               action_message = 'Marked CRF Complete'
         WHERE audit_id = v_audit_id;

        INSERT INTO audit_event_context
                    (audit_id, event_crf_id,
                     study_event_id, study_subject_id,
                     crf_version_id, role_name
                    )
             VALUES (v_audit_id, :NEW.event_crf_id,
                     :NEW.study_event_id, :NEW.study_subject_id,
                     :NEW.crf_version_id, 'completed'
                    );
      END IF;
      
    ELSE
      UPDATE audit_event
         SET reason_for_change = 'Updated Event CRF Properties',
             action_message = 'Updated Event CRF Properties'
       WHERE audit_id = v_audit_id;

      INSERT INTO audit_event_context
                  (audit_id, event_crf_id, study_event_id,
                   study_subject_id, crf_version_id, role_name
                  )
           VALUES (v_audit_id, :NEW.event_crf_id, :NEW.study_event_id,
                   :NEW.study_subject_id, :NEW.crf_version_id, 'updated'
                  );
    END IF;
    
  ELSIF DELETING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id,
                 reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Event CRF', :OLD.owner_id,
                 :OLD.event_crf_id,
                 'Permenantly deleted Event CRF record and all associated data from the database',
                 'Deleted Event CRF'
                );

    INSERT INTO audit_event_context
                (audit_id, event_crf_id, study_event_id,
                 study_subject_id, crf_version_id, role_name
                )
         VALUES (v_audit_id, :OLD.event_crf_id, :OLD.study_event_id,
                 :OLD.study_subject_id, :OLD.crf_version_id, 'deleted'
                );

    SELECT crf.NAME, crfv.NAME
      INTO v_crf_name, v_crf_version_name
      FROM crf_version crfv, crf
     WHERE crfv.crf_id = crf.crf_id
       AND crfv.crf_version_id = :OLD.crf_version_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'CRF', v_crf_name, v_crf_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'CRF Version', v_crf_version_name,
                 v_crf_version_name
                );

    SELECT sed.NAME, se.date_start
      INTO v_study_event_def_name, v_study_event_date_start
      FROM study_event_definition sed, study_event se
     WHERE se.study_event_definition_id = sed.study_event_definition_id
       AND se.study_event_id = :OLD.study_event_id;

    INSERT INTO audit_event_values
                (audit_id, column_name,
                 old_value, new_value
                )
         VALUES (v_audit_id, 'Study Event Definition',
                 v_study_event_def_name, v_study_event_def_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name,
                 old_value, new_value
                )
         VALUES (v_audit_id, 'Study Event Start Date',
                 v_study_event_date_start, v_study_event_date_start
                );
  END IF;
END;
/


CREATE OR REPLACE TRIGGER &clinica..item_data_aiudrtrg
  AFTER INSERT OR UPDATE OR DELETE
  ON &clinica..item_data
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
DECLARE
  v_audit_id                 audit_event.audit_id%TYPE;
  v_item_name                item.NAME%TYPE;
  v_study_event_def_name     study_event_definition.NAME%TYPE;
  v_study_event_date_start   study_event.date_start%TYPE;
  v_crf_name                 crf.NAME%TYPE;
  v_crf_version_name         crf_version.NAME%TYPE;
BEGIN
  SELECT audit_event_seq.NEXTVAL
    INTO v_audit_id
    FROM DUAL;

  --will be very plain, one for insert, one for update, one for delete
  -- maybe not so plain, as we have to aggregate
  IF INSERTING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Item Data', :NEW.owner_id,
                 :NEW.item_data_id, 'Inserted Data into a CRF',
                 'Saved Data to a CRF'
                );

    INSERT INTO audit_event_context
                (audit_id, item_id, event_crf_id,
                 role_name
                )
         VALUES (v_audit_id, :NEW.item_id, :NEW.event_crf_id,
                 'ITEM_DATA_created'
                );

    SELECT a.NAME crf_name, c.NAME crfv_name
      INTO v_crf_name, v_crf_version_name
      FROM crf a, event_crf b, crf_version c
     WHERE c.crf_id = a.crf_id
       AND b.crf_version_id = c.crf_version_id
       AND b.event_crf_id = :NEW.event_crf_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'CRF', NULL, v_crf_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'CRF Version', NULL, v_crf_version_name
                );

    SELECT item.NAME
      INTO v_item_name
      FROM item
     WHERE item.item_id = :NEW.item_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, v_item_name, NULL, :NEW.VALUE
                );

    SELECT sed.NAME, se.date_start
      INTO v_study_event_def_name, v_study_event_date_start
      FROM study_event_definition sed, event_crf, study_event se
     WHERE sed.study_event_definition_id = se.study_event_definition_id
       AND event_crf.study_event_id = se.study_event_id
       AND event_crf.event_crf_id = :NEW.event_crf_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Study Event Definition', NULL,
                 v_study_event_def_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Study Event Start Date', NULL,
                 v_study_event_date_start
                );
  ELSIF UPDATING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Item Data', :NEW.update_id,
                 :NEW.item_data_id, 'Updated Data on a CRF',
                 'Updated CRF Data'
                );

    INSERT INTO audit_event_context
                (audit_id, item_id, event_crf_id,
                 role_name
                )
         VALUES (v_audit_id, :NEW.item_id, :NEW.event_crf_id,
                 'ITEM_DATA_updated'
                );

    SELECT item.NAME
      INTO v_item_name
      FROM item
     WHERE item.item_id = :NEW.item_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, v_item_name, :OLD.VALUE, :NEW.VALUE
                );

    SELECT a.NAME, c.NAME
      INTO v_crf_name, v_crf_version_name
      FROM crf a, event_crf b, crf_version c
     WHERE c.crf_id = a.crf_id
       AND b.crf_version_id = c.crf_version_id
       AND b.event_crf_id = :NEW.event_crf_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'CRF', NULL, v_crf_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'CRF Version', NULL, v_crf_version_name
                );

    SELECT se.date_start
      INTO v_study_event_date_start
      FROM event_crf, study_event se
     WHERE event_crf.study_event_id = se.study_event_id
       AND event_crf.event_crf_id = :NEW.event_crf_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Study Event Start Date', NULL,
                 v_study_event_date_start
                );
  ELSIF DELETING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Item Data', :OLD.update_id,
                 :OLD.item_data_id, 'Deleted Data from a CRF',
                 'Deleted Data from a CRF'
                );

    INSERT INTO audit_event_context
                (audit_id, item_id, event_crf_id,
                 role_name
                )
         VALUES (v_audit_id, :OLD.item_id, :OLD.event_crf_id,
                 'ITEM_DATA_deleted'
                );

    SELECT item.NAME
      INTO v_item_name
      FROM item
     WHERE item.item_id = :OLD.item_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, v_item_name, :OLD.VALUE, :OLD.VALUE
                );
  END IF;
  
END;
/


--
-- Name: study_event_aiudrtrg
--
CREATE OR REPLACE TRIGGER &clinica..study_event_aiudrtrg
  BEFORE INSERT OR UPDATE OR DELETE
  ON &clinica...study_event
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
DECLARE
  v_audit_id                 audit_event.audit_id%TYPE;
  v_new_study_ent_def_name   study_event_definition.NAME%TYPE;
  v_old_study_ent_def_name   study_event_definition.NAME%TYPE;
BEGIN
  --CREATES a row in the audit table; at the same time,
  --creates a row in the context table and multiple rows
  SELECT audit_event_seq.NEXTVAL
    INTO v_audit_id
    FROM DUAL;

  IF INSERTING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Study Event', :NEW.owner_id,
                 :NEW.study_event_id, 'Added a Study Event:',
                 'Added a Study Event'
                );

    INSERT INTO audit_event_context
                (audit_id, study_event_id, study_subject_id,
                 study_event_definition_id, role_name
                )
         VALUES (v_audit_id, :NEW.study_event_id, :NEW.study_subject_id,
                 :NEW.study_event_definition_id, 'study event inserted'
                );

    --do we want to select a study_id into the context table?
    SELECT NAME
      INTO v_new_study_ent_def_name
      FROM study_event_definition
     WHERE study_event_definition_id = :NEW.study_event_definition_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Location', NULL, :NEW.LOCATION
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Study Event Definition', NULL,
                 v_new_study_ent_def_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Event Ordinal', NULL, :NEW.sample_ordinal
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Date Start', NULL, :NEW.date_start
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Date End', NULL, :NEW.date_end
                );
  --INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, --OLD_VALUE, NEW_VALUE)
  --VALUES
  --(v_audit_id,'study_event.owner_id', NULL, :NEW.owner_id);

  --not sure if this will work
  --INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, --OLD_VALUE, NEW_VALUE)
  --VALUES
  --(v_audit_id,'study_event.date_created', NULL, --:NEW.date_created);
  ELSIF UPDATING THEN
    --just updated and removed
    IF ((:OLD.status_id = 1) AND (:NEW.status_id = 5)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id,
                   reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Study Event', :NEW.update_id,
                   :OLD.study_event_id,
                   'Removed Study Event and all associated events and study data',
                   'Removed a Study Event'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_event_id, study_subject_id,
                   study_event_definition_id, role_name
                  )
           VALUES (v_audit_id, :NEW.study_event_id, :NEW.study_subject_id,
                   :NEW.study_event_definition_id, 'study event removed'
                  );

      SELECT NAME
        INTO v_new_study_ent_def_name
        FROM study_event_definition
       WHERE study_event_definition_id = :OLD.study_event_definition_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name,
                   old_value, new_value
                  )
           VALUES (v_audit_id, 'Study Event Definition',
                   v_new_study_ent_def_name, v_new_study_ent_def_name
                  );
    ELSE
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Study Event', :NEW.update_id,
                   :NEW.study_event_id, 'Updated a Study Event:',
                   'Updated a Study Event'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_event_id, study_subject_id,
                   study_event_definition_id, role_name
                  )
           VALUES (v_audit_id, :NEW.study_event_id, :NEW.study_subject_id,
                   :NEW.study_event_definition_id, 'study event updated'
                  );

      SELECT NAME
        INTO v_new_study_ent_def_name
        FROM study_event_definition
       WHERE study_event_definition_id = :NEW.study_event_definition_id;

      SELECT NAME
        INTO v_old_study_ent_def_name
        FROM study_event_definition
       WHERE study_event_definition_id = :OLD.study_event_definition_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name,
                   old_value, new_value
                  )
           VALUES (v_audit_id, 'Study Event Definition',
                   v_old_study_ent_def_name, v_new_study_ent_def_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Location', :OLD.LOCATION, :NEW.LOCATION
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Event Ordinal', :OLD.sample_ordinal,
                   :NEW.sample_ordinal
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Date Start', :OLD.date_start, :NEW.date_start
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Date End', :OLD.date_end, :NEW.date_end
                  );
    END IF;

    IF (:OLD.subject_event_status_id != :NEW.subject_event_status_id) THEN
      INSERT INTO audit_event_values
                  (audit_id, column_name,
                   old_value, new_value
                  )
           VALUES (v_audit_id, 'Subject Event Status ID',
                   :OLD.subject_event_status_id, :NEW.subject_event_status_id
                  );
    END IF;
  ELSIF DELETING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id,
                 reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Study Event', :OLD.owner_id,
                 :OLD.study_event_id,
                 'Permenantly deleted Study Event record from database',
                 'Deleted a Study Event'
                );

    INSERT INTO audit_event_context
                (audit_id, study_event_id, study_subject_id,
                 study_event_definition_id, role_name
                )
         VALUES (v_audit_id, :OLD.study_event_id, :OLD.study_subject_id,
                 :OLD.study_event_definition_id, 'deleted'
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Location', :OLD.LOCATION, :OLD.LOCATION
                );
  END IF;
END;
/


--
-- Name: study_subject_aiudrtrg
--
CREATE OR REPLACE TRIGGER &clinica..study_subject_aiudrtrg
  BEFORE INSERT OR UPDATE OR DELETE 
  ON &clinica..study_subject
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
DECLARE
  v_audit_id            audit_event.audit_id%TYPE;
  v_unique_identifier   subject.unique_identifier%TYPE;
  v_old_study_name      study.NAME%TYPE;
  v_new_study_name      study.NAME%TYPE;
BEGIN
  SELECT audit_event_seq.NEXTVAL
    INTO v_audit_id
    FROM DUAL;

  IF INSERTING THEN
    SELECT unique_identifier
      INTO v_unique_identifier
      FROM subject
     WHERE subject_id = :NEW.subject_id;

    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Study Subject', :NEW.owner_id,
                 :NEW.study_subject_id, 'Enrolled Subject in Study: ',
                 'Enrolled a Subject in a Study/Site'
                );

    INSERT INTO audit_event_context
                (audit_id, subject_id, study_id,
                 study_subject_id, role_name
                )
         VALUES (v_audit_id, :NEW.subject_id, :NEW.study_id,
                 :NEW.study_subject_id, 'Enrolled a Subject in a Study/Site'
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Date of Enrollment', NULL, :NEW.enrollment_date
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Unique ID', NULL, :NEW.label
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Secondary ID', NULL, :NEW.secondary_label
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Subject Global ID', NULL, v_unique_identifier
                );
  ELSIF UPDATING THEN
    --we cover four cases here: removed, restored, reassignments,
    --and other updates
    IF ((:OLD.status_id = 1) AND (:NEW.status_id = 5)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id,
                   reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Study Subject', :NEW.update_id,
                   :OLD.study_subject_id,
                   'Removed study record and all associated study data: '
                   || :OLD.label,
                   'Removed Subject from Study/Site'
                  );

      INSERT INTO audit_event_context
                  (audit_id, subject_id, study_id,
                   study_subject_id, role_name
                  )
           VALUES (v_audit_id, :OLD.subject_id, :OLD.study_id,
                   :OLD.study_subject_id, 'removed'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Unique ID', :OLD.label, :NEW.label
                  );
    ELSIF ((:OLD.status_id = 5) AND (:NEW.status_id = 1)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id,
                   reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Study Subject', :NEW.update_id,
                   :OLD.study_subject_id,
                   'Restored study subject record and all asscoiated ' ||
                   'study data: ' || :OLD.label,
                   'Restored Subject to Study/Site'
                  );

      INSERT INTO audit_event_context
                  (audit_id, subject_id, study_id,
                   study_subject_id, role_name
                  )
           VALUES (v_audit_id, :OLD.subject_id, :OLD.study_id,
                   :OLD.study_subject_id, 'restored'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Unique ID', :OLD.label, :NEW.label
                  );
    ELSIF (:OLD.study_id != :NEW.study_id) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id,
                   reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Study Subject', :NEW.update_id,
                   :OLD.study_subject_id,
                   'Reassigned study subject record and all associated ' ||
                   'study data to another site: ' || :NEW.label,
                   'Reassigned Subject to New Site'
                  );

      INSERT INTO audit_event_context
                  (audit_id, subject_id, study_id,
                   study_subject_id, role_name
                  )
           VALUES (v_audit_id, :OLD.subject_id, :OLD.study_id,
                   :OLD.study_subject_id, 'reassigned'
                  );

      SELECT NAME
        INTO v_old_study_name
        FROM study
       WHERE study_id = :OLD.study_id;

      SELECT NAME
        INTO v_new_study_name
        FROM study
       WHERE study_id = :NEW.study_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Study/Site', v_old_study_name,
                   v_new_study_name
                  );
    ELSE
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id,
                   reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Study Subject', :NEW.update_id,
                   :OLD.study_subject_id,
                   'Updated subject information for a Study/Site',
                   'Updated Study Subject record'
                  );

      INSERT INTO audit_event_context
                  (audit_id, subject_id, study_id,
                   study_subject_id, role_name
                  )
           VALUES (v_audit_id, :OLD.subject_id, :OLD.study_id,
                   :OLD.study_subject_id, 'updated'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Unique ID', :OLD.label, :NEW.label
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Secondary Label', :OLD.secondary_label,
                   :NEW.secondary_label
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Date of Enrollment', :OLD.enrollment_date,
                   :NEW.enrollment_date
                  );
    END IF;
  ELSIF DELETING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id,
                 reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Study Subject', :NEW.update_id,
                 :OLD.study_subject_id,
                 'Permenantly deleted study subject record and all ' ||
                 'associated study data: '|| :OLD.label,
                 'Deleted Study Subject from Study/Site'
                );

    INSERT INTO audit_event_context
                (audit_id, subject_id, study_id,
                 study_subject_id, role_name
                )
         VALUES (v_audit_id, :OLD.subject_id, :OLD.study_id,
                 :OLD.study_subject_id, 'deleted'
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Unique ID', :OLD.label, :OLD.label
                );
  END IF;
END;
/


--
-- Name: study_user_role_aiudrtrg
--
CREATE OR REPLACE TRIGGER &clinica..study_user_role_aiudrtrg
  BEFORE INSERT OR UPDATE OR DELETE
  ON &clinica..study_user_role
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
DECLARE
  v_audit_id         audit_event.audit_id%TYPE;
  v_user_id          user_account.user_id%TYPE;
  v_first_name       user_account.first_name%TYPE;
  v_last_name        user_account.last_name%TYPE;
  v_new_study_name   study.NAME%TYPE;
  v_old_study_name   study.NAME%TYPE;
BEGIN
  SELECT audit_event_seq.NEXTVAL
    INTO v_audit_id
    FROM DUAL;

  IF INSERTING THEN
    SELECT user_id, first_name, last_name
      INTO v_user_id, v_first_name, v_last_name
      FROM user_account
     WHERE user_name = :NEW.user_name;

    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.owner_id,
                 v_user_id, 'Assigned User to a Study/Site:',
                 'Assigned User to Study'
                );

    INSERT INTO audit_event_context
                (audit_id, study_id,
                 role_name
                )
         VALUES (v_audit_id, :NEW.study_id,
                 'STUDY_USER_ROLE created in the database'
                );

    --study site name and role name
    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Username', NULL, :NEW.user_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'User ID', NULL, v_user_id
                );

    SELECT NAME
      INTO v_new_study_name
      FROM study
     WHERE study_id = :NEW.study_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Study/Site', NULL, v_new_study_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Name', NULL, v_first_name || ' ' || v_last_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Role name', NULL, :NEW.role_name
                );
  --add longer desc?
  ELSIF UPDATING THEN
    --again, modified, removed, restored
    SELECT user_id, first_name, last_name
      INTO v_user_id, v_first_name, v_last_name
      FROM user_account
     WHERE user_name = :NEW.user_name;

    IF ((:OLD.status_id = 1) AND (:NEW.status_id = 5)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.owner_id,
                   v_user_id, 'Removed User from a Study/Site:',
                   'Removed User from Study'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id,
                   role_name
                  )
           VALUES (v_audit_id, :NEW.study_id,
                   'STUDY_USER_ROLE removed from study'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User ID', NULL, v_user_id
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Username', :OLD.user_name, :NEW.user_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Name', NULL,
                   v_first_name || ' ' || v_last_name
                  );
    ELSIF ((:OLD.status_id = 5) AND (:NEW.status_id = 1)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.owner_id,
                   v_user_id, 'Restored User to a Study/Site:',
                   'Restored User to Study'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id,
                   role_name
                  )
           VALUES (v_audit_id, :NEW.study_id,
                   'STUDY_USER_ROLE restored to study'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User ID', NULL, v_user_id
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Username', :OLD.user_name, :NEW.user_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Name', NULL,
                   v_first_name || ' ' || v_last_name
                  );
    ELSE
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.owner_id,
                   v_user_id, 'Updated User Role in a Study/Site:',
                   'Modified User Role in Study'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id,
                   role_name
                  )
           VALUES (v_audit_id, :NEW.study_id,
                   'STUDY_USER_ROLE updated in the database'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Username', :OLD.user_name, :NEW.user_name
                  );

      SELECT NAME
        INTO v_old_study_name
        FROM study
       WHERE study_id = :OLD.study_id;

      SELECT NAME
        INTO v_new_study_name
        FROM study
       WHERE study_id = :NEW.study_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User ID', NULL, v_user_id
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Study/Site', v_old_study_name,
                   v_new_study_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Role name', :OLD.role_name, :NEW.role_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Name', NULL,
                   v_first_name || ' ' || v_last_name
                  );
    END IF;
  ELSIF DELETING THEN
    SELECT user_id, first_name, last_name
      INTO v_user_id, v_first_name, v_last_name
      FROM user_account
     WHERE user_name = :OLD.user_name;

    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'User Account', :OLD.update_id,
                 v_user_id, 'Deleted User from a Study/Site:',
                 'Assigned User to Study'
                );

    INSERT INTO audit_event_context
                (audit_id, study_id,
                 role_name
                )
         VALUES (v_audit_id, :OLD.study_id,
                 'STUDY_USER_ROLE deleted from the database'
                );

    --study site name and role name
    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Username', :OLD.user_name, :OLD.user_name
                );

    SELECT NAME
      INTO v_old_study_name
      FROM study
     WHERE study_id = :OLD.study_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Study/Site', v_old_study_name, v_old_study_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Name', NULL, v_first_name || ' ' || v_last_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Role name', :OLD.role_name, :OLD.role_name
                );
  END IF;
END;
/


--
-- Name: subject_group_map_aiudrtrg
--
CREATE OR REPLACE TRIGGER &clinica..subject_group_map_aiudrtrg
  AFTER INSERT OR UPDATE OR DELETE
  ON &clinica..subject_group_map
  REFERENCING OLD AS OLD NEW AS NEW 
  FOR EACH ROW
DECLARE
		v_audit_id audit_event.audit_id%TYPE;
		v_new_study_id study_subject.study_id%TYPE;
		v_new_group_class_name study_group_class.name%TYPE;
		v_new_group_name study_group.name%TYPE;
		v_old_group_class_name study_group_class.name%TYPE;
		v_old_group_name study_group.name%TYPE;
		
BEGIN
  SELECT audit_event_seq.NEXTVAL
    INTO v_audit_id
    FROM DUAL;

  IF INSERTING THEN
    SELECT study_id
      INTO v_new_study_id
      FROM study_subject
     WHERE study_subject_id = :NEW.study_subject_id;

    SELECT NAME
      INTO v_new_group_class_name
      FROM study_group_class
     WHERE study_group_class_id = :NEW.study_group_class_id;

    SELECT NAME
      INTO v_new_group_name
      FROM study_group
     WHERE study_group_id = :NEW.study_group_id;

    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Subject Group', :NEW.owner_id,
                 :NEW.subject_group_map_id, 'Assigned Subject to Group:',
                 'Assigned Subject to Group'
                );

    INSERT INTO audit_event_context
                (audit_id, study_id, study_subject_id,
                 role_name
                )
         VALUES (v_audit_id, v_new_study_id, :NEW.study_subject_id,
                 'Assigned Subject to Group'
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Date of Enrollment', NULL, :NEW.date_created
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Group Class', NULL, v_new_group_class_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Group', NULL, v_new_group_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Notes', NULL, :NEW.notes
                );
  ELSIF UPDATING THEN
    --we cover three cases here: removed, restored, updated
    SELECT study_id
      INTO v_new_study_id
      FROM study_subject
     WHERE study_subject_id = :NEW.study_subject_id;

    SELECT NAME
      INTO v_new_group_class_name
      FROM study_group_class
     WHERE study_group_class_id = :NEW.study_group_class_id;

    SELECT NAME
      INTO v_old_group_class_name
      FROM study_group_class
     WHERE study_group_class_id = :OLD.study_group_class_id;

    SELECT NAME
      INTO v_new_group_name
      FROM study_group
     WHERE study_group_id = :NEW.study_group_id;

    SELECT NAME
      INTO v_old_group_name
      FROM study_group
     WHERE study_group_id = :OLD.study_group_id;

    IF ((:OLD.status_id = 1) AND (:NEW.status_id = 5)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Subject Group', :NEW.update_id,
                   :OLD.subject_group_map_id, 'Removed Subject from Group',
                   'Removed Subject from a group'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id, study_subject_id,
                   role_name
                  )
           VALUES (v_audit_id, v_new_study_id, :NEW.study_subject_id,
                   'Removed Subject from Group'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Date of Enrollment', :OLD.date_created,
                   :NEW.date_created
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Group Class', v_old_group_class_name,
                   v_new_group_class_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Group', v_old_group_name, v_new_group_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Notes', :OLD.notes, :NEW.notes
                  );
    ELSIF ((:OLD.status_id = 5) AND (:NEW.status_id = 1)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Subject Group', :NEW.update_id,
                   :OLD.subject_group_map_id, 'Restored Subject to Group',
                   'Restored Subject to a group'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id, study_subject_id,
                   role_name
                  )
           VALUES (v_audit_id, v_new_study_id, :NEW.study_subject_id,
                   'Removed Subject from Group'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Date of Enrollment', :OLD.date_created,
                   :NEW.date_created
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Group Class', v_old_group_class_name,
                   v_new_group_class_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Group', v_old_group_name, v_new_group_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Notes', :OLD.notes, :NEW.notes
                  );
    ELSE
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id,
                   reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Subject Group', :NEW.update_id,
                   :OLD.subject_group_map_id,
                   'Updated Subject information for a Group',
                   'Updated Subject in a Group'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id, study_subject_id,
                   role_name
                  )
           VALUES (v_audit_id, v_new_study_id, :NEW.study_subject_id,
                   'Removed Subject from Group'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Date of Enrollment', :OLD.date_created,
                   :NEW.date_created
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Group Class', v_old_group_class_name,
                   v_new_group_class_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Group', v_old_group_name, v_new_group_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Notes', :OLD.notes, :NEW.notes
                  );
    END IF;
  ELSIF DELETING THEN
    SELECT study_id
      INTO v_new_study_id
      FROM study_subject
     WHERE study_subject_id = :OLD.study_subject_id;

    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id,
                 reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Subject Group', :OLD.update_id,
                 :OLD.subject_group_map_id,
                 'Permenantly deleted Subject from Group',
                 'Deleted Subject from Group'
                );

    INSERT INTO audit_event_context
                (audit_id, study_id, study_subject_id, role_name
                )
         VALUES (v_audit_id, v_new_study_id, :OLD.study_subject_id, 'Deleted'
                );

    SELECT NAME
      INTO v_old_group_class_name
      FROM study_group_class
     WHERE study_group_class_id = :OLD.study_group_class_id;

    SELECT NAME
      INTO v_old_group_name
      FROM study_group
     WHERE study_group_id = :OLD.study_group_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Date of Enrollment', :OLD.date_created,
                 :OLD.date_created
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Group Class', v_old_group_class_name,
                 v_old_group_class_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Group', v_old_group_name, v_old_group_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Notes', :OLD.notes, :OLD.notes
                );
  END IF;
END;
/


--
-- Name: subject_aiudrtrg
--
CREATE OR REPLACE TRIGGER &clinica..subject_aiudrtrg
  AFTER INSERT OR UPDATE OR DELETE
  ON &clinica..subject
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
DECLARE
  v_audit_id    audit_event.audit_id%TYPE;
  v_mother_id   subject.unique_identifier%TYPE;
  v_father_id   subject.unique_identifier%TYPE;
BEGIN
  SELECT subject_seq.NEXTVAL
    INTO v_audit_id
    FROM DUAL;

  --the following block will fill in the 'added subject record to database'
  IF INSERTING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id,
                 reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Subject', :NEW.owner_id,
                 :NEW.subject_id,
                    'Added Subject record to the database: '
                 || :NEW.unique_identifier,
                 'Added Subject Record to database'
                );

    --we want to add study_id to the context line as well,
    --but that may only occur when the trigger fires on an
    --insert to study_subject
    INSERT INTO audit_event_context
                (audit_id, subject_id,
                 role_name
                )
         VALUES (v_audit_id, :NEW.subject_id,
                 'subject created in the database'
                );

    --NEXT: COLLECT ALL INFORMATION, with human-readable --column names
    IF :NEW.father_id IS NOT NULL THEN
      SELECT unique_identifier
        INTO v_father_id
        FROM subject
       WHERE subject_id = :NEW.father_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Father ID', NULL, v_father_id
                  );
    END IF;

    --INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, --OLD_VALUE, NEW_VALUE)
    --VALUES
    --(v_audit_id,'Subject ID', NULL, :NEW.SUBJECT_ID);
    IF :NEW.mother_id IS NOT NULL THEN
      SELECT unique_identifier
        INTO v_mother_id
        FROM subject
       WHERE subject_id = :NEW.mother_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Mother ID', NULL, v_mother_id
                  );
    END IF;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Date of Birth', NULL, :NEW.date_of_birth
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Gender', NULL, :NEW.gender
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Global ID', NULL, :NEW.unique_identifier
                );
  ELSIF UPDATING THEN
    --fufills the trigger for Updated global subject record,
    --but what about Removed global subject record
    --and restored global subject record?
    IF ((:OLD.status_id = 1) AND (:NEW.status_id = 5)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id,
                   reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Subject', :NEW.update_id,
                   :NEW.subject_id,
                   'Removed Subject from system: ' || :NEW.unique_identifier,
                   'Removed Subject from System'
                  );

      INSERT INTO audit_event_context
                  (audit_id, subject_id, role_name
                  )
           VALUES (v_audit_id, :OLD.subject_id, 'removed'
                  );
    ELSIF ((:OLD.status_id = 5) AND (:NEW.status_id = 1)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id,
                   reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Subject', :NEW.update_id,
                   :NEW.subject_id,
                   'Restored Subject to system: ' || :NEW.unique_identifier,
                   'Restored Subject to system'
                  );

      INSERT INTO audit_event_context
                  (audit_id, subject_id, role_name
                  )
           VALUES (v_audit_id, :OLD.subject_id, 'restored'
                  );
    ELSE
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id,
                   reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'Subject', :NEW.update_id,
                   :NEW.subject_id,
                   'Updated Global Subject record: ' || :NEW.unique_identifier,
                   'Updated Global Subject Record'
                  );

      INSERT INTO audit_event_context
                  (audit_id, subject_id, role_name
                  )
           VALUES (v_audit_id, :OLD.subject_id, 'updated'
                  );

      IF (:OLD.mother_id != :NEW.mother_id) THEN
        -- PROBLEM HERE: :NEW.MOTHER_ID CAN BE NULL  NEED TO MAKE SURE THIS WON'T
        -- THROGGH EXCEPTION
        SELECT unique_identifier
          INTO v_mother_id
          FROM subject
         WHERE subject_id = :NEW.mother_id;

        INSERT INTO audit_event_values
                    (audit_id, column_name, old_value, new_value
                    )
             VALUES (v_audit_id, 'Mother ID', :OLD.mother_id, v_mother_id
                    );
      END IF;

      IF (:OLD.father_id != :NEW.father_id) THEN
        -- PROBLEM HERE: :NEW.FATHER_ID CAN BE NULL  NEED TO MAKE SURE THIS WON'T
        -- THROGGH EXCEPTION
        SELECT unique_identifier
          INTO v_father_id
          FROM subject
         WHERE subject_id = :NEW.father_id;

        INSERT INTO audit_event_values
                    (audit_id, column_name, old_value, new_value
                    )
             VALUES (v_audit_id, 'Father ID', :OLD.father_id, v_father_id
                    );
      END IF;

      IF (:OLD.status_id != :NEW.status_id) THEN
        INSERT INTO audit_event_values
                    (audit_id, column_name, old_value, new_value
                    )
             VALUES (v_audit_id, 'Status ID', :OLD.status_id, :NEW.status_id
                    );
      END IF;

      IF (:OLD.unique_identifier != :NEW.unique_identifier) THEN
        INSERT INTO audit_event_values
                    (audit_id, column_name, old_value,
                     new_value
                    )
             VALUES (v_audit_id, 'Global ID', :OLD.unique_identifier,
                     :NEW.unique_identifier
                    );
      END IF;

      IF (:OLD.owner_id != :NEW.owner_id) THEN
        INSERT INTO audit_event_values
                    (audit_id, column_name, old_value, new_value
                    )
             VALUES (v_audit_id, 'Creator ID', :OLD.owner_id, :NEW.owner_id
                    );
      END IF;

      IF (:OLD.update_id != :NEW.update_id) THEN
        INSERT INTO audit_event_values
                    (audit_id, column_name, old_value, new_value
                    )
             VALUES (v_audit_id, 'Updater ID', :OLD.update_id, :NEW.update_id
                    );
      END IF;

      IF (:OLD.date_updated != :NEW.date_updated) THEN
        INSERT INTO audit_event_values
                    (audit_id, column_name, old_value,
                     new_value
                    )
             VALUES (v_audit_id, 'Date Updated', :OLD.date_updated,
                     :NEW.date_updated
                    );
      END IF;
    END IF;

    --we are inserting gender and date of birth to show something no matter what
    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Date of Birth', :OLD.date_of_birth,
                 :NEW.date_of_birth
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Gender', :OLD.gender, :NEW.gender
                );
  ELSIF DELETING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id,
                 reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'Subject', :OLD.owner_id,
                 :OLD.subject_id,
                    'Permenantly deleted global subject record and all '
                 || 'associated study data from database: '
                 || :OLD.unique_identifier,
                 'Deleted Subject from System'
                );

    INSERT INTO audit_event_context
                (audit_id, subject_id, role_name
                )
         VALUES (v_audit_id, :OLD.subject_id, 'deleted'
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Date of Birth', :OLD.date_of_birth,
                 :OLD.date_of_birth
                );
  END IF;
END;
/


--
-- Name: user_account_aiudrtrg
--
CREATE OR REPLACE TRIGGER &clinica..user_account_aiudrtrg
  BEFORE INSERT OR UPDATE OR DELETE
  ON &clinica..user_account
  REFERENCING OLD AS OLD NEW AS NEW 
  FOR EACH ROW
DECLARE
	v_audit_id INTEGER;
	v_active_study_name study.name%TYPE;
	v_user_type user_type.user_type%TYPE;
BEGIN
  SELECT audit_event_seq.NEXTVAL
    INTO v_audit_id
    FROM DUAL;

  IF INSERTING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change, action_message
                )
         VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.owner_id,
                 :NEW.user_id, 'Added User Account', 'Added User Account'
                );

    INSERT INTO audit_event_context
                (audit_id, study_id,
                 role_name
                )
         VALUES (v_audit_id, :NEW.active_study,
                 'USER_ACCOUNT created in the database'
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Username', NULL, :NEW.user_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'First name', NULL, :NEW.first_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Last name', NULL, :NEW.last_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Email', NULL, :NEW.email
                );

    SELECT NAME
      INTO v_active_study_name
      FROM study
     WHERE study_id = :NEW.active_study;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Default Study', NULL, v_active_study_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Institutional Affiliation', NULL,
                 :NEW.institutional_affiliation
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Phone', NULL, :NEW.phone
                );

    SELECT user_type
      INTO v_user_type
      FROM user_type
     WHERE user_type_id = :NEW.user_type_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'User Type', NULL, v_user_type
                );
  ELSIF UPDATING THEN
    --updated, removed, restored
    IF ((:OLD.status_id = 1) AND (:NEW.status_id = 5)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.update_id,
                   :NEW.user_id, 'Removed User Account from the system',
                   'Removed User Account'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id,
                   role_name
                  )
           VALUES (v_audit_id, :NEW.active_study,
                   'USER_ACCOUNT removed from the database'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User ID', :OLD.user_id, :NEW.user_id
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Username', :OLD.user_name, :NEW.user_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Name', NULL,
                   :NEW.first_name || ' ' || :NEW.last_name
                  );

      SELECT user_type
        INTO v_user_type
        FROM user_type
       WHERE user_type_id = :NEW.user_type_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User Type', NULL, v_user_type
                  );
    ELSIF ((:OLD.status_id = 5) AND (:NEW.status_id = 1)) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.update_id,
                   :NEW.user_id, 'Restored User Account to the system',
                   'Restored User Account'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id,
                   role_name
                  )
           VALUES (v_audit_id, :NEW.active_study,
                   'USER_ACCOUNT restored to the database'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User ID', :OLD.user_id, :NEW.user_id
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Username', :OLD.user_name, :NEW.user_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Name', NULL,
                   :NEW.first_name || ' ' || :NEW.last_name
                  );

      SELECT user_type
        INTO v_user_type
        FROM user_type
       WHERE user_type_id = :NEW.user_type_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User Type', NULL, v_user_type
                  );
    ELSIF (:OLD.date_lastvisit != :NEW.date_lastvisit) THEN
      IF (:OLD.active_study != :NEW.active_study) THEN
        INSERT INTO audit_event
                    (audit_id, audit_date, audit_table, user_id,
                     entity_id, reason_for_change, action_message
                    )
             VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.update_id,
                     :NEW.user_id, 'User Changed Study', 'User Changed Study'
                    );
      ELSE
        INSERT INTO audit_event
                    (audit_id, audit_date, audit_table, user_id,
                     entity_id, reason_for_change, action_message
                    )
             VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.update_id,
                     :NEW.user_id, 'User Login', 'User Login'
                    );
      END IF;

      INSERT INTO audit_event_context
                  (audit_id, study_id,
                   role_name
                  )
           VALUES (v_audit_id, :NEW.active_study,
                   'USER_ACCOUNT updated in the database'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Username', :OLD.user_name, :NEW.user_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'First name', :OLD.first_name, :NEW.first_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Last name', :OLD.last_name, :NEW.last_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Email', :OLD.email, :NEW.email
                  );

      SELECT NAME
        INTO v_active_study_name
        FROM study
       WHERE study_id = :NEW.active_study;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Default Study', NULL, v_active_study_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name,
                   old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Institutional Affiliation',
                   :OLD.institutional_affiliation,
                   :NEW.institutional_affiliation
                  );

      SELECT user_type
        INTO v_user_type
        FROM user_type
       WHERE user_type_id = :NEW.user_type_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User Type', NULL, v_user_type
                  );
    ELSIF (:OLD.active_study != :NEW.active_study) THEN
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change, action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.update_id,
                   :NEW.user_id, 'User Changed Study', 'User Changed Study'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id,
                   role_name
                  )
           VALUES (v_audit_id, :NEW.active_study,
                   'USER_ACCOUNT updated in the database'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Username', :OLD.user_name, :NEW.user_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'First name', :OLD.first_name, :NEW.first_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Last name', :OLD.last_name, :NEW.last_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Email', :OLD.email, :NEW.email
                  );

      SELECT NAME
        INTO v_active_study_name
        FROM study
       WHERE study_id = :NEW.active_study;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Default Study', NULL, v_active_study_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name,
                   old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Institutional Affiliation',
                   :OLD.institutional_affiliation,
                   :NEW.institutional_affiliation
                  );

      SELECT user_type
        INTO v_user_type
        FROM user_type
       WHERE user_type_id = :NEW.user_type_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User Type', NULL, v_user_type
                  );
    ELSE
      INSERT INTO audit_event
                  (audit_id, audit_date, audit_table, user_id,
                   entity_id, reason_for_change,
                   action_message
                  )
           VALUES (v_audit_id, SYSDATE, 'User Account', :NEW.update_id,
                   :NEW.user_id, 'Updated User Account',
                   'Updated User Account'
                  );

      INSERT INTO audit_event_context
                  (audit_id, study_id,
                   role_name
                  )
           VALUES (v_audit_id, :NEW.active_study,
                   'USER_ACCOUNT updated in the database'
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Username', :OLD.user_name, :NEW.user_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'First name', :OLD.first_name, :NEW.first_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Last name', :OLD.last_name, :NEW.last_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Email', :OLD.email, :NEW.email
                  );

      SELECT NAME
        INTO v_active_study_name
        FROM study
       WHERE study_id = :NEW.active_study;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Default Study', NULL, v_active_study_name
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name,
                   old_value,
                   new_value
                  )
           VALUES (v_audit_id, 'Institutional Affiliation',
                   :OLD.institutional_affiliation,
                   :NEW.institutional_affiliation
                  );

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'Phone', :OLD.phone, :NEW.phone
                  );

      SELECT user_type
        INTO v_user_type
        FROM user_type
       WHERE user_type_id = :NEW.user_type_id;

      INSERT INTO audit_event_values
                  (audit_id, column_name, old_value, new_value
                  )
           VALUES (v_audit_id, 'User Type', NULL, v_user_type
                  );

      IF (:OLD.passwd != :NEW.passwd) THEN
        INSERT INTO audit_event_values
                    (audit_id, column_name, old_value, new_value
                    )
             VALUES (v_audit_id, 'Password', NULL, 'Changed'
                    );
      ELSE
        INSERT INTO audit_event_values
                    (audit_id, column_name, old_value, new_value
                    )
             VALUES (v_audit_id, 'Password', NULL, 'Not Changed'
                    );
      END IF;
    END IF;
  ELSIF DELETING THEN
    INSERT INTO audit_event
                (audit_id, audit_date, audit_table, user_id,
                 entity_id, reason_for_change,
                 action_message
                )
         VALUES (v_audit_id, SYSDATE, 'User Account', :OLD.owner_id,
                 :OLD.user_id, 'Deleted User Account from the system',
                 'Deleted User Account'
                );

    INSERT INTO audit_event_context
                (audit_id, study_id, role_name
                )
         VALUES (v_audit_id, :OLD.active_study, 'USER_ACCOUNT deleted'
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'User ID', :OLD.user_id, :OLD.user_id
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'Username', :OLD.user_name, :OLD.user_name
                );

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value,
                 new_value
                )
         VALUES (v_audit_id, 'Name', NULL,
                 :OLD.first_name || ' ' || :OLD.last_name
                );

    SELECT user_type
      INTO v_user_type
      FROM user_type
     WHERE user_type_id = :OLD.user_type_id;

    INSERT INTO audit_event_values
                (audit_id, column_name, old_value, new_value
                )
         VALUES (v_audit_id, 'User Type', v_user_type, v_user_type
                );
  END IF;
END;
/

COMMIT
/
