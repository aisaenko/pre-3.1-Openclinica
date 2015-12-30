--
-- Name: ruleset; Type: TABLE; Schema: public; Owner: clinica; Tablespace: 
--

CREATE TABLE ruleset (
    ruleset_id serial NOT NULL,
    enabled boolean,
    event_crf_id numeric,
    owner_id numeric,
    date_created date,
    date_updated date,
    update_id numeric,
    status_id numeric
);


ALTER TABLE public.ruleset OWNER TO clinica;

--
-- Name: ruleset_ruleset_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clinica
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('ruleset', 'ruleset_id'), 1, false);


--
-- Name: ruleset_id_pkey; Type: CONSTRAINT; Schema: public; Owner: clinica; Tablespace: 
--

ALTER TABLE ONLY ruleset
    ADD CONSTRAINT ruleset_id_pkey PRIMARY KEY (ruleset_id);


--
-- Name: rule; Type: TABLE; Schema: public; Owner: clinica; Tablespace: 
--

CREATE TABLE rule (
	rule_id serial NOT NULL,
	name character varying(255),
    ruleset_id numeric NOT NULL,
    event_crf_id numeric,
    item_data_source_name character varying(255),
    item_data_source_id numeric,
    item_data_source_value character varying(255),
    operator_source_code numeric,
    item_data_target_name character varying(255),
    item_data_target_id numeric,
    item_data_target_value character varying(255),
    operator_target_code numeric,
    owner_id numeric,
    date_created date,
    date_updated date,
    update_id numeric,
    status_id numeric
);


ALTER TABLE public.rule OWNER TO clinica;

--
-- Name: rule_rule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clinica
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('rule', 'rule_id'), 1, false);



--
-- Name: rule_id_pkey; Type: CONSTRAINT; Schema: public; Owner: clinica; Tablespace: 
--

ALTER TABLE ONLY rule ADD CONSTRAINT rule_id_pkey PRIMARY KEY (rule_id);


--
-- Update discrepancy_note_type with two new types
--

--INSERT INTO discrepancy_note_type (discrepancy_note_type_id, name, description) VALUES (6, 'Query', '');
--INSERT INTO discrepancy_note_type (discrepancy_note_type_id, name, description) VALUES (7, 'Reason for Change', '');

--
--	Esigs implementation
--

--alter table event_definition_crf add column electronic_signature boolean DEFAULT false;
--alter table event_crf add column electronic_signature_status boolean DEFAULT false;

INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (14, 'Event CRF signed complete');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (15, 'Event CRF IDE signed (DDE)');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (16, 'Event CRF validated and signed (DDE)');

INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (17, 'Study Event scheduled');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (18, 'Study Event data entry started');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (19, 'Study Event completed');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (20, 'Study Event stopped');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (21, 'Study Event skipped');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (22, 'Study Event locked');

INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (23, 'Study Event removed');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (24, 'Study Event start date changed');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (25, 'Study Event end date changed');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (26, 'Study Event location changed');
INSERT INTO audit_log_event_type(audit_log_event_type_id, name) values (27, 'Subject Site Assignment');

ALTER TABLE audit_log_event ADD COLUMN study_event_id INTEGER;
ALTER TABLE audit_log_event ADD COLUMN event_crf_version_id INTEGER;

CREATE OR REPLACE FUNCTION event_crf_trigger() RETURNS "trigger"
    AS $$DECLARE
	pk INTEGER;
	entity_name_value TEXT;
BEGIN
	IF (TG_OP = 'UPDATE') THEN
		IF(OLD.status_id <> NEW.status_id) THEN
		---------------
		--Event CRF status changed
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value 'Status';
		IF(OLD.status_id = '1' AND NEW.status_id = '2') THEN
		    IF (NEW.electronic_signature_status) THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, '14', now(), NEW.update_id, 'event_crf', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            ELSE
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, '8', now(), NEW.update_id, 'event_crf', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            END IF;
		ELSIF (OLD.status_id = '1' AND NEW.status_id = '4') THEN
		    IF (NEW.electronic_signature_status) THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, '15', now(), NEW.update_id, 'event_crf', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            ELSE
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, '10', now(), NEW.update_id, 'event_crf', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            END IF;
		ELSIF (OLD.status_id = '4' AND NEW.status_id = '2') THEN
    		IF (NEW.electronic_signature_status) THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, '16', now(), NEW.update_id, 'event_crf', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
		    ELSE
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, '11', now(), NEW.update_id, 'event_crf', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
		    END IF;
		END IF;
		---------------
		END IF;

		IF(OLD.date_interviewed <> NEW.date_interviewed) THEN
		---------------
		--Event CRF date interviewed
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value 'Date interviewed';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
			VALUES (pk, '9', now(), NEW.update_id, 'event_crf', NEW.event_crf_id, entity_name_value, OLD.date_interviewed, NEW.date_interviewed, NEW.event_crf_id);
		---------------
		END IF;

		IF((OLD.interviewer_name <> NEW.interviewer_name) AND (OLD.interviewer_name <> '')) THEN		---------------
		--Event CRF interviewer name
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value 'Interviewer Name';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
			VALUES (pk, '9', now(), NEW.update_id, 'event_crf', NEW.event_crf_id, entity_name_value, OLD.interviewer_name, NEW.interviewer_name, NEW.event_crf_id);
		---------------
		END IF;
	RETURN NULL; --return values ignored for 'after' triggers
	END IF;
END;
$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.event_crf_trigger() OWNER TO clinica;

CREATE OR REPLACE FUNCTION study_event_trigger() RETURNS "trigger"
    AS $$DECLARE
	pk INTEGER;
BEGIN
	IF (TG_OP = 'INSERT') THEN
        SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
        IF(NEW.subject_event_status_id = '1') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, '17', now(), NEW.owner_id, 'study_event', NEW.study_event_id, 'Status','0', NEW.subject_event_status_id);
        END IF;                    
    END IF;

	IF (TG_OP = 'UPDATE') THEN
		IF(OLD.subject_event_status_id <> NEW.subject_event_status_id) THEN
            SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
            IF(NEW.subject_event_status_id = '1') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, '17', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'Status', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = '3') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, '18', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'Status', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = '4') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, '19', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'Status', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = '5') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, '20', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'Status', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = '6') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, '21', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'Status', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = '7') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, '22', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'Status', OLD.subject_event_status_id, NEW.subject_event_status_id);
		    END IF;
	    END IF;
        IF(OLD.status_id <> NEW.status_id) THEN
            IF(NEW.status_id = '5') THEN
                SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, '23', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'Status', OLD.status_id, NEW.status_id);
            END IF;
        END IF;
        IF(OLD.date_start <> NEW.date_start) THEN
            SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, '24', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'Start date', OLD.date_start, NEW.date_start);
        END IF;
        IF(OLD.date_end <> NEW.date_end) THEN
            SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, '25', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'End date', OLD.date_end, NEW.date_end);
        END IF;
        IF(OLD.location <> NEW.location) THEN
            SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, '26', now(), NEW.update_id, 'study_event', NEW.study_event_id, 'Location', OLD.location, NEW.location);
        END IF;
    	RETURN NULL; --return values ignored for 'after' triggers
	END IF;
	RETURN NULL;
END;$$
    LANGUAGE plpgsql;

ALTER FUNCTION public.study_event_trigger() OWNER TO clinica;

CREATE TRIGGER study_event_insert_update
    AFTER INSERT OR UPDATE ON study_event
    FOR EACH ROW
    EXECUTE PROCEDURE study_event_trigger();

CREATE OR REPLACE FUNCTION item_data_trigger() RETURNS "trigger"
    AS $$DECLARE
	pk INTEGER;
	entity_name_value TEXT;
	std_evnt_id INTEGER;
	crf_version_id INTEGER;
BEGIN
	IF (TG_OP = 'DELETE') THEN
		---------------
		--Item data deleted (by deleting an event crf)
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = OLD.item_id;
        SELECT INTO std_evnt_id ec.study_event_id FROM event_crf ec WHERE ec.event_crf_id = OLD.event_crf_id;
        SELECT INTO crf_version_id ec.crf_version_id FROM event_crf ec WHERE ec.event_crf_id = OLD.event_crf_id;
		IF (OLD.update_id < 1) THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, event_crf_id, study_event_id, event_crf_version_id)
                VALUES (pk, '13', now(), OLD.update_id, 'item_data', OLD.item_data_id, entity_name_value, OLD.value, OLD.event_crf_id, std_evnt_id, crf_version_id);
        ELSE
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, event_crf_id, study_event_id, event_crf_version_id)
                VALUES (pk, '13', now(), OLD.owner_id, 'item_data', OLD.item_data_id, entity_name_value, OLD.value, OLD.event_crf_id, std_evnt_id, crf_version_id);
        END IF;
		RETURN NULL; --return values ignored for 'after' triggers
	ELSIF (TG_OP = 'UPDATE') THEN
		IF(OLD.status_id <> NEW.status_id) THEN
		---------------
		--Item data status changed (by removing an event crf)
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = NEW.item_id;
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
			VALUES (pk, '12', now(), NEW.update_id, 'item_data', NEW.item_data_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
		---------------
		END IF;

		IF(OLD.value <> NEW.value) THEN
		---------------
		--Item data updated
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = NEW.item_id;
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
			VALUES (pk, '1', now(), NEW.update_id, 'item_data', NEW.item_data_id, entity_name_value, OLD.value, NEW.value, NEW.event_crf_id);
		---------------
		END IF;
		RETURN NULL; --return values ignored for 'after' triggers
	END IF;
RETURN NULL; --return values ignored for 'after' triggers
END;
$$
    LANGUAGE plpgsql;

ALTER FUNCTION public.item_data_trigger() OWNER TO clinica;

CREATE OR REPLACE FUNCTION study_subject_trigger() RETURNS "trigger"
    AS $$DECLARE
	pk INTEGER;
	entity_name_value TEXT;
    old_unique_identifier TEXT;
    new_unique_identifier TEXT;

BEGIN
	IF (TG_OP = 'INSERT') THEN
		---------------
		--Study subject created
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id)
			VALUES (pk, '2', now(), NEW.owner_id, 'study_subject', NEW.study_subject_id);
		RETURN NULL; --return values ignored for 'after' triggers
		---------------
	ELSIF (TG_OP = 'UPDATE') THEN
		IF(OLD.status_id <> NEW.status_id) THEN
		---------------
		--Study subject status changed
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value 'Status';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, '3', now(), NEW.update_id, 'study_subject', NEW.study_subject_id, entity_name_value, OLD.status_id, NEW.status_id);
		---------------
		END IF;

		IF(OLD.label <> NEW.label) THEN
		---------------
		--Study subject value changed
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value 'Study Subject ID';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, '4', now(), NEW.update_id, 'study_subject', NEW.study_subject_id, entity_name_value, OLD.label, NEW.label);
		---------------
		END IF;

		IF(OLD.secondary_label <> NEW.secondary_label) THEN
		---------------
		--Study subject value changed
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value 'Secondary Subject ID';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, '4', now(), NEW.update_id, 'study_subject', NEW.study_subject_id, entity_name_value, OLD.secondary_label, NEW.secondary_label);
		---------------
		END IF;

		IF(OLD.enrollment_date <> NEW.enrollment_date) THEN
		---------------
		--Study subject value changed
		SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
		SELECT INTO entity_name_value 'Enrollment Date';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, '4', now(), NEW.update_id, 'study_subject', NEW.study_subject_id, entity_name_value, OLD.enrollment_date, NEW.enrollment_date);
		---------------
		END IF;

        IF(OLD.study_id <> NEW.study_id) THEN
        ---------------
        --Subject reassigned
        SELECT INTO pk NEXTVAL('audit_log_event_audit_id_seq');
        SELECT INTO entity_name_value 'Study id';
        SELECT INTO old_unique_identifier study.unique_identifier FROM study study WHERE study.study_id = OLD.study_id;
        SELECT INTO new_unique_identifier study.unique_identifier FROM study study WHERE study.study_id = NEW.study_id;
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, '27', now(), NEW.update_id, 'study_subject', NEW.study_subject_id, entity_name_value, old_unique_identifier, new_unique_identifier);
        ---------------
        END IF;

	RETURN NULL; --return values ignored for 'after' triggers
	END IF;
END;
$$
    LANGUAGE plpgsql;

ALTER FUNCTION public.study_subject_trigger() OWNER TO clinica;


---
--- OID Implementation
---

ALTER TABLE crf ADD COLUMN oc_oid character varying(40);
ALTER TABLE study_event_definition ADD COLUMN oc_oid character varying(40);
ALTER TABLE crf_version ADD COLUMN oc_oid character varying(40);
ALTER TABLE item_group ADD COLUMN oc_oid character varying(40);
ALTER TABLE item ADD COLUMN oc_oid character varying(40);
ALTER TABLE study ADD COLUMN oc_oid character varying(40);
ALTER TABLE study_subject ADD COLUMN oc_oid character varying(40);

-- generate OID's for crfs  
update crf set oc_oid  = 
'F_' ||
substring(upper(regexp_replace(crf.name, '\\s+|\\W+', '','g')),0,5) || '_' ||
crf_id ;

-- generate OID's for study event definitions  
update study_event_definition set oc_oid = 
'SE_' ||
substring(upper(regexp_replace(name, '\\s+|\\W+', '','g')),0,9) || study_event_definition_id;

-- generate OID's for crf versions  
update crf_version set oc_oid  = 
crf.oc_oid || '_' ||
substring(upper(regexp_replace(crf_version.name, '\\s+|\\W+', '','g')),0,5) ||
crf_version.crf_version_id 
from crf where crf_version.crf_id=crf.crf_id;

-- generate OID's for item groups 
update item_group set oc_oid  = 
'IG_' ||
substring(upper(regexp_replace(crf.name, '\\s+|\\W+', '','g')),0,5) || '_' ||
substring(upper(regexp_replace(item_group.name, '\\s+|\\W+', '','g')),0,28) ||
item_group.item_group_id 
from crf where item_group.crf_id=crf.crf_id;

-- generate OIDs for items
update item set oc_oid  = 
'I_' ||
substring(upper(regexp_replace(crf.name, '\\s+|\\W+', '','g')),0,5) || '_' ||
substring(upper(regexp_replace(item.name, '\\s+|\\W+', '','g')),0,29) ||
item.item_id 
from item_group_metadata, item_group,crf where 
item_group_metadata.item_id = item.item_id AND
item_group_metadata.item_group_id = item_group.item_group_id AND
item_group.crf_id=crf.crf_id;

-- generate OIDs for study
update study set oc_oid = substring(upper(regexp_replace(unique_identifier, '\\s+|\\W+', '','g')),0,9) || study_id;

-- generate OIDs for study_subject
update study_subject set oc_oid = substring(upper(regexp_replace(label, '\\s+|\\W+', '','g')),0,9) || study_subject_id;

-- make columns not null 
ALTER TABLE crf ALTER COLUMN oc_oid SET NOT NULL;
ALTER TABLE study_event_definition ALTER COLUMN oc_oid SET NOT NULL;
ALTER TABLE crf_version ALTER COLUMN oc_oid SET NOT NULL;
ALTER TABLE item_group ALTER COLUMN oc_oid SET NOT NULL;
ALTER TABLE item ALTER COLUMN oc_oid SET NOT NULL;
ALTER TABLE study ALTER COLUMN oc_oid SET NOT NULL;
ALTER TABLE study_subject ALTER COLUMN oc_oid SET NOT NULL;

-- add uniqueness constraints 
ALTER TABLE crf ADD CONSTRAINT uniq_crf_oc_oid UNIQUE (oc_oid);
ALTER TABLE study_event_definition ADD CONSTRAINT uniq_study_event_definition_oid UNIQUE (oc_oid);
ALTER TABLE crf_version ADD CONSTRAINT uniq_crf_version_oc_oid UNIQUE (oc_oid);
ALTER TABLE item_group ADD CONSTRAINT uniq_item_group_oc_oid UNIQUE (oc_oid);
ALTER TABLE item ADD CONSTRAINT uniq_item_oc_oid UNIQUE (oc_oid);
ALTER TABLE study ADD CONSTRAINT uniq_study_oid UNIQUE (oc_oid);
ALTER TABLE study_subject ADD CONSTRAINT uniq_study_subject_oid UNIQUE (oc_oid);

-- create indexes 
CREATE INDEX oc_oid_crf_table ON crf USING btree (oc_oid);
CREATE INDEX oc_oid_study_event_definition_table ON study_event_definition USING btree (oc_oid);
CREATE INDEX oc_oid_crf_version_table ON crf_version USING btree (oc_oid);
CREATE INDEX oc_oid_item_group_table ON item_group USING btree (oc_oid);
CREATE INDEX oc_oid_item_table ON item USING btree (oc_oid);
CREATE INDEX oc_oid_study_table ON study USING btree (oc_oid);
CREATE INDEX oc_oid_study_subject_table ON study_subject USING btree (oc_oid);

---
--- Indexing for performance inhacing in datawarehouse script
---

-- create indexes on item_group_metadata
CREATE INDEX item_id_item_group_metadata_table ON item_group_metadata USING btree (item_id);
CREATE INDEX crf_version_id_item_group_metadata_table ON item_group_metadata USING btree (crf_version_id);

-- create indexes on item_data
CREATE INDEX status_id_item_data_table ON item_data USING btree (status_id);
CREATE INDEX item_id_item_data_table ON item_data USING btree (item_id);
CREATE INDEX event_crf_id_item_data_table ON item_data USING btree (event_crf_id);

-- create indexes on item_form_metadata, event_definition_crf, event_crf, item_group
CREATE INDEX response_set_id_item_form_metadata_table ON item_form_metadata USING btree (response_set_id);
CREATE INDEX crf_id_event_definition_crf_table ON event_definition_crf USING btree (crf_id);
CREATE INDEX status_id_event_crf_table ON event_crf USING btree (status_id);
CREATE INDEX item_group_id_item_group_table ON item_group USING btree (item_group_id);

--
-- Scoring Implementation
--

-- new response_types have been added. ywang, 1/8/2007
INSERT INTO response_type VALUES (8, 'calculation', 'value calculated automatically');
INSERT INTO response_type VALUES (9, 'group-calculation', 'value calculated automatically from an entire group of items');
-- add more columns in the table <dataset>.
ALTER TABLE dataset ADD odm_metadataversion_name varchar(255);
ALTER TABLE dataset ADD odm_metadataversion_oid varchar(255);
ALTER TABLE dataset ADD odm_prior_study_oid varchar(255);
ALTER TABLE dataset ADD odm_prior_metadataversion_oid varchar(255);

--
-- Rules Implementation
--

CREATE TABLE "rule"
(
   rule_id serial NOT NULL, 
   name character varying(255),
   description character varying(255),
   oc_oid character varying(40), 
   enabled boolean,
   rule_expression_id numeric NOT NULL,
   owner_id numeric,
   date_created date,
   date_updated date,
   update_id numeric,
   status_id numeric,
   CONSTRAINT rule_id_pkey PRIMARY KEY (rule_id)
) WITHOUT OIDS;
ALTER TABLE "rule" OWNER TO clinica;

CREATE TABLE "rule_set"
(
   rule_set_id serial NOT NULL,
   rule_expression_id numeric NOT NULL,
   owner_id numeric,
   date_created date,
   date_updated date,
   update_id numeric,
   status_id numeric,
   CONSTRAINT rule_set_id_pkey PRIMARY KEY (rule_set_id)
) WITHOUT OIDS;
ALTER TABLE "rule_set" OWNER TO clinica;


CREATE TABLE "rule_set_rule"
(
   rule_set_rule_id serial NOT NULL,
   rule_set_id numeric NOT NULL, 
   rule_id numeric NOT NULL, 
   owner_id numeric,
   date_created date,
   date_updated date,
   update_id numeric,
   status_id numeric,
   CONSTRAINT rule_set_rule_id_pkey PRIMARY KEY (rule_set_rule_id)
) WITHOUT OIDS;
ALTER TABLE "rule_set_rule" OWNER TO clinica;

CREATE TABLE "rule_action"
(
   rule_action_id serial NOT NULL,
   rule_set_rule_id numeric NOT NULL,
   action_type numeric NOT NULL,
   expression_evaluates_to boolean NOT NULL,
   message character varying(255), 
   owner_id numeric,
   date_created date,
   date_updated date,
   update_id numeric,
   status_id numeric,
   CONSTRAINT rule_action_id_pkey PRIMARY KEY (rule_action_id)
) WITHOUT OIDS;
ALTER TABLE "rule_action" OWNER TO clinica;

CREATE TABLE "rule_expression"
(
   rule_expression_id serial NOT NULL,
   value character varying(1025) NOT NULL, 
   context numeric NOT NULL,
   owner_id numeric,
   date_created date,
   date_updated date,
   update_id numeric,
   status_id numeric,
   CONSTRAINT expression_id_pkey PRIMARY KEY (rule_expression_id)
) WITHOUT OIDS;
ALTER TABLE "rule_expression" OWNER TO clinica;



