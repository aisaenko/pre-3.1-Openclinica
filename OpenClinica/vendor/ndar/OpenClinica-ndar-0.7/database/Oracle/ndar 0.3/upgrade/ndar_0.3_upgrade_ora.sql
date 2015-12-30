CREATE TABLE &clinica..subjectentrylabel (
    subjectentrylabel_id NUMBER(10) NOT NULL,
    LABEL VARCHAR2(255),
    description VARCHAR2(2000),
    owner_id NUMBER(10),
    date_created DATE,
    update_id NUMBER(10),
    date_updated DATE 
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..subjectentrylabel_seq
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..subjectentrylabel_birtrg
  BEFORE INSERT
  ON &clinica..subjectentrylabel
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.subjectentrylabel_id IS NULL THEN
    SELECT subjectentrylabel_seq.NEXTVAL
      INTO :NEW.subjectentrylabel_id
      FROM dual;
  END IF;

END;
/


CREATE TABLE &clinica..subject_subjectentrylabel (
    subject_subjectentrylabel_id NUMBER(10) NOT NULL,
    subject_id NUMBER(10),
    subjectentrylabel_id NUMBER(10)
)
TABLESPACE &clinica_data_ts
/

CREATE SEQUENCE &clinica..subject_subjectentrylabel_seq
  INCREMENT BY 1
  START WITH 1
  MINVALUE 1
  NOMAXVALUE
  NOCYCLE
  NOORDER
  NOCACHE
/

CREATE OR REPLACE TRIGGER &clinica..subj_subjectentrylabel_birtrg
  BEFORE INSERT
  ON &clinica..subject_subjectentrylabel
  REFERENCING NEW AS NEW OLD AS OLD
  FOR EACH ROW
BEGIN
  IF :NEW.subject_subjectentrylabel_id IS NULL THEN
    SELECT subject_subjectentrylabel_seq.NEXTVAL
      INTO :NEW.subject_subjectentrylabel_id
      FROM dual;
  END IF;

END;
/

ALTER TABLE &clincia..subjectentrylabel
  ADD CONSTRAINT subjectentrylabel_pk PRIMARY KEY (subjectentrylabel_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clincia..subjectentrylabel
  ADD CONSTRAINT subjectentrylabel_uk UNIQUE (LABEL)
  USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clincia..subject_subjectentrylabel
  ADD CONSTRAINT subject_subjectentrylabel_pk PRIMARY KEY (subject_subjectentrylabel_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clincia..subject_subjectentrylabel
  ADD CONSTRAINT subject_subjectentrylabel_uk UNIQUE (subject_id, subjectentrylabel_id)
  USING INDEX TABLESPACE &clinica_indx_ts
/

ALTER TABLE &clincia..subject_subjectentrylabel
  ADD CONSTRAINT sub_subentrylabel_subject_fk FOREIGN KEY (subject_id)
  REFERENCES &clinica..subject (subject_id)
/

ALTER TABLE &clincia..subject_subjectentrylabel
  ADD CONSTRAINT sub_subentlabel_subentlabel_fk FOREIGN KEY (subjectentrylabel_id)
  REFERENCES &clinica..subjectentrylabel (subjectentrylabel_id)
/

COMMIT
/
