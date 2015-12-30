drop table permissions_filter;

CREATE TABLE permissions_filter
(
  study_event_definition_id integer NOT NULL,
  crf_id integer NOT NULL,
  item_id integer NOT NULL,
  role_id integer NOT NULL,
  permit integer,
  status_id integer,
  date_created date,
  date_updated date,
  owner_id integer NOT NULL,
  update_id numeric,
  CONSTRAINT fk_filter_fk_query__status FOREIGN KEY (status_id)
      REFERENCES status (status_id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_filter_fk_query__study_ev FOREIGN KEY (study_event_definition_id)
      REFERENCES study_event_definition (study_event_definition_id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_filter_fk_query__crf FOREIGN KEY (crf_id)
      REFERENCES crf (crf_id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_filter_fk_query__item FOREIGN KEY (item_id)
      REFERENCES item (item_id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_filter_fk_query__role FOREIGN KEY (role_id)
      REFERENCES user_role (role_id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_filter_fk_query__user_acc FOREIGN KEY (owner_id)
      REFERENCES user_account (user_id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
) 
WITH OIDS;
ALTER TABLE permissions_filter OWNER TO clinica;