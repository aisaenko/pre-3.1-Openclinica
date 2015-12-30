CREATE TABLE &clinica..score_ref_table
(
  score_ref_table_id NUMBER(10) NOT NULL,
  crf_version_id NUMBER(10) NOT NULL,
  score_ref_table_name VARCHAR2(100) NOT NULL,
  description VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..score_ref_table_seq
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..score_ref_table_birtrg
  BEFORE INSERT
  ON &clinica..score_ref_table
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.score_ref_table_id IS NULL THEN
    SELECT score_ref_table_seq.NEXTVAL
      INTO :NEW.score_ref_table_id
      FROM dual;
  END IF;

END;
/

ALTER TABLE &clinica..score_ref_table
ADD CONSTRAINT score_ref_table_pk PRIMARY KEY (score_ref_table_id)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..score_ref_table
ADD CONSTRAINT score_ref_table_uk UNIQUE (crf_version_id, score_ref_table_name)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..score_ref_table
ADD CONSTRAINT crf_version_score_ref_table_fk FOREIGN KEY (crf_version_id)
REFERENCES &clinica..crf_version (crf_version_id)
/


CREATE TABLE &clinica..score_attribute
(
  score_attribute_id NUMBER(10) NOT NULL,
  score_ref_table_id NUMBER(10) NOT NULL,
  score_attribute_name VARCHAR2(50) NOT NULL,
  description VARCHAR2(1000)
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..score_attribute_seq
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..score_attribute_birtrg
  BEFORE INSERT
  ON &clinica..score_attribute
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.score_attribute_id IS NULL THEN
    SELECT score_attribute_seq.NEXTVAL
      INTO :NEW.score_attribute_id
      FROM dual;
  END IF;

END;
/

ALTER TABLE &clinica..score_attribute
ADD CONSTRAINT score_attribute_pk PRIMARY KEY (score_attribute_id)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..score_attribute
ADD CONSTRAINT score_attribute_uk UNIQUE (score_ref_table_id, score_attribute_name)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..score_attribute
ADD CONSTRAINT score_ref_tab_score_attr_fk FOREIGN KEY (score_ref_table_id)
REFERENCES &clinica..score_ref_table (score_ref_table_id) ON DELETE CASCADE 
/


CREATE TABLE &clinica..score_attribute_value
(
  score_attribute_value_id NUMBER(10) NOT NULL,
  score_attribute_id NUMBER(10) NOT NULL,
  score_attribute_charvalue VARCHAR2(1000),
  score_attribute_recnumber NUMBER(10) NOT NULL
) 
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..score_attribute_value_seq
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..score_attribute_value_birtrg
  BEFORE INSERT
  ON &clinica..score_attribute_value
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.score_attribute_value_id IS NULL THEN
    SELECT score_attribute_value_seq.NEXTVAL
      INTO :NEW.score_attribute_value_id
      FROM dual;
  END IF;

END;
/

ALTER TABLE &clinica..score_attribute_value
ADD CONSTRAINT score_attribute_value_pk PRIMARY KEY (score_attribute_value_id)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..score_attribute_value
ADD CONSTRAINT score_attribute_value_uk UNIQUE (score_attribute_id, 
  score_attribute_recnumber)
USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clinica..score_attribute_value
ADD CONSTRAINT score_attr_score_attr_val_fk FOREIGN KEY (score_attribute_id)
REFERENCES &clinica..score_attribute (score_attribute_id) ON DELETE CASCADE 
/

COMMIT
/
