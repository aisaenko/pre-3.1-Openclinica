CREATE TABLE subjectentrylabel (
    subjectentrylabel_id serial NOT NULL,
    label character varying(255),
    description character varying(2000),
    owner_id integer,
    date_created date,
    update_id integer,
    date_updated date
);

ALTER TABLE subjectentrylabel OWNER TO clinica;

CREATE TABLE subject_subjectentrylabel (
    subject_subjectentrylabel_id serial NOT NULL,
    subject_id integer,
    subjectentrylabel_id integer
);

ALTER TABLE subject_subjectentrylabel OWNER TO clinica;

ALTER TABLE subjectentrylabel
  ADD CONSTRAINT subjectentrylabel_pk PRIMARY KEY (subjectentrylabel_id);

ALTER TABLE subjectentrylabel
  ADD CONSTRAINT subjectentrylabel_uk UNIQUE (label);

ALTER TABLE subject_subjectentrylabel
  ADD CONSTRAINT subject_subjectentrylabel_pk PRIMARY KEY (subject_subjectentrylabel_id);

ALTER TABLE subject_subjectentrylabel
  ADD CONSTRAINT subject_subjectentrylabel_uk UNIQUE (subject_id, subjectentrylabel_id);

ALTER TABLE subject_subjectentrylabel
  ADD CONSTRAINT sub_subentrylabel_subject_fk FOREIGN KEY (subject_id)
  REFERENCES subject (subject_id) ON UPDATE RESTRICT ON DELETE RESTRICT ;

ALTER TABLE subject_subjectentrylabel
  ADD CONSTRAINT sub_subentlabel_subentlabel_fk FOREIGN KEY (subjectentrylabel_id)
  REFERENCES subjectentrylabel (subjectentrylabel_id) ON UPDATE RESTRICT ON DELETE RESTRICT;
