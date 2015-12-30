--
-- Update RuleSet table column names
--
ALTER TABLE rule RENAME rule_id  TO id;
ALTER TABLE rule_rule_id_seq RENAME TO rule_id_seq;
ALTER TABLE rule ADD COLUMN version integer;
UPDATE rule set version=0 where version is null;

ALTER TABLE rule_action RENAME rule_action_id  TO id;
ALTER TABLE rule_action_rule_action_id_seq RENAME TO rule_action_id_seq;
ALTER TABLE rule_action ADD COLUMN version integer;
UPDATE rule_action set version=0 where version is null;

ALTER TABLE rule_expression RENAME rule_expression_id  TO id;
ALTER TABLE rule_expression_rule_expression_id_seq RENAME TO rule_expression_id_seq;
ALTER TABLE rule_expression ADD COLUMN version integer;
UPDATE rule_expression set version=0 where version is null;

ALTER TABLE rule_set RENAME rule_set_id  TO id;
ALTER TABLE rule_set_rule_set_id_seq RENAME TO rule_set_id_seq;
ALTER TABLE rule_set ADD COLUMN version integer;
UPDATE rule_set set version=0 where version is null;

ALTER TABLE rule_set_audit RENAME rule_set_audit_id  TO id;
ALTER TABLE rule_set_audit_rule_set_audit_id_seq RENAME TO rule_set_audit_id_seq;
ALTER TABLE rule_set_audit ADD COLUMN version integer;
UPDATE rule_set_audit set version=0 where version is null;

ALTER TABLE rule_set_rule RENAME rule_set_rule_id  TO id;
ALTER TABLE rule_set_rule_rule_set_rule_id_seq RENAME TO rule_set_rule_id_seq;
ALTER TABLE rule_set_rule ADD COLUMN version integer;
UPDATE rule_set_rule set version=0 where version is null;

ALTER TABLE rule_set_rule_audit RENAME rule_set_rule_audit_id  TO id;
ALTER TABLE rule_set_rule_audit_rule_set_rule_audit_id_seq RENAME TO rule_set_rule_audit_id_seq;
ALTER TABLE rule_set_rule_audit ADD COLUMN version integer;
UPDATE rule_set_rule_audit set version=0 where version is null;

--
-- Added new data types to item_data_type
--
INSERT INTO item_data_type (item_data_type_id, code, name, definition, reference) VALUES (10,'PDATE','partial date', 'year only or year with month or date', NULL);
INSERT INTO item_data_type (item_data_type_id, code, name, definition, reference) VALUES (11, 'FILE', 'File', 'File name, extension and path', NULL);

--
-- Added Tables for Spring Security
--

CREATE TABLE authorities (
  id serial NOT NULL,
  username VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL,
  version INTEGER
  );
 ALTER TABLE authorities OWNER TO clinica;

INSERT INTO authorities select nextval('authorities_id_seq'),user_name,'ROLE_USER',1 from user_account ;

--
-- Scheduler Implementation
--

CREATE TABLE oc_qrtz_job_details
  (
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE BOOL NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    IS_STATEFUL BOOL NOT NULL,
    REQUESTS_RECOVERY BOOL NOT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
);
ALTER TABLE oc_qrtz_job_details OWNER TO clinica;

CREATE TABLE oc_qrtz_job_listeners
  (
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    JOB_LISTENER VARCHAR(200) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
    REFERENCES oc_qrtz_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);
ALTER TABLE oc_qrtz_job_listeners OWNER TO clinica;

CREATE TABLE oc_qrtz_triggers
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT NULL,
    PREV_FIRE_TIME BIGINT NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT NOT NULL,
    END_TIME BIGINT NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
    REFERENCES oc_qrtz_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);
ALTER TABLE oc_qrtz_triggers OWNER TO clinica;

CREATE TABLE oc_qrtz_simple_triggers
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT NOT NULL,
    REPEAT_INTERVAL BIGINT NOT NULL,
    TIMES_TRIGGERED BIGINT NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES oc_qrtz_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
ALTER TABLE oc_qrtz_simple_triggers OWNER TO clinica;

CREATE TABLE oc_qrtz_cron_triggers
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(120) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES oc_qrtz_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
ALTER TABLE oc_qrtz_cron_triggers OWNER TO clinica;

CREATE TABLE oc_qrtz_blob_triggers
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BYTEA NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES oc_qrtz_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
ALTER TABLE oc_qrtz_blob_triggers OWNER TO clinica;

CREATE TABLE oc_qrtz_trigger_listeners
  (
    TRIGGER_NAME  VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    TRIGGER_LISTENER VARCHAR(200) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES oc_qrtz_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
ALTER TABLE oc_qrtz_trigger_listeners OWNER TO clinica;


CREATE TABLE oc_qrtz_calendars
  (
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BYTEA NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);
ALTER TABLE oc_qrtz_calendars OWNER TO clinica;

CREATE TABLE oc_qrtz_paused_trigger_grps
  (
    TRIGGER_GROUP  VARCHAR(200) NOT NULL,
    PRIMARY KEY (TRIGGER_GROUP)
);
ALTER TABLE oc_qrtz_paused_trigger_grps OWNER TO clinica;

CREATE TABLE oc_qrtz_fired_triggers
  (
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_STATEFUL BOOL NULL,
    REQUESTS_RECOVERY BOOL NULL,
    PRIMARY KEY (ENTRY_ID)
);
ALTER TABLE oc_qrtz_fired_triggers OWNER TO clinica;

CREATE TABLE oc_qrtz_scheduler_state
  (
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT NOT NULL,
    CHECKIN_INTERVAL BIGINT NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
);
ALTER TABLE oc_qrtz_scheduler_state OWNER TO clinica;

CREATE TABLE oc_qrtz_locks
  (
    LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (LOCK_NAME)
);


INSERT INTO oc_qrtz_locks values('TRIGGER_ACCESS');
INSERT INTO oc_qrtz_locks values('JOB_ACCESS');
INSERT INTO oc_qrtz_locks values('CALENDAR_ACCESS');
INSERT INTO oc_qrtz_locks values('STATE_ACCESS');
INSERT INTO oc_qrtz_locks values('MISFIRE_ACCESS');
ALTER TABLE oc_qrtz_locks OWNER TO clinica;

ALTER TABLE event_definition_crf  ADD COLUMN hide_crf boolean DEFAULT false;

-- addition to manage Query workflow, tbh 02/2009
ALTER TABLE discrepancy_note ADD COLUMN assigned_user_id integer;
ALTER TABLE discrepancy_note ADD CONSTRAINT discrepancy_note_assigned_user_id_fkey
	FOREIGN KEY (assigned_user_id) REFERENCES user_account (user_id) MATCH SIMPLE ON UPDATE RESTRICT ON DELETE RESTRICT;



INSERT INTO study_parameter (study_parameter_id, handle, name, description, default_value, inheritable, overridable) VALUES (14, 'secondaryLabelViewable', '', '', 'not viewable', true, false);
INSERT INTO study_parameter_value(study_parameter_value_id, study_id, value, parameter) VALUES (14, 1, 'false', 'secondaryLabelViewable');

ALTER TABLE item_form_metadata ADD COLUMN width_decimal VARCHAR(10);