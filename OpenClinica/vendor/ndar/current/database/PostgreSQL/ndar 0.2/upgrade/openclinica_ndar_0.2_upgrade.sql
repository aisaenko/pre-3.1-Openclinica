CREATE TABLE skip_rule (
    rule_id serial NOT NULL,
    name character varying(255),
    instruction_text character varying(4000),
    condition character varying(4000),
    assignments character varying(4000),
    crf_version_id integer,
    status_id integer,
    owner_id integer,
    date_created date,
    date_updated date,
    update_id numeric
);

ALTER TABLE public.skip_rule OWNER TO clinica;

ALTER TABLE ONLY skip_rule
  ADD CONSTRAINT pk_skip_rule PRIMARY KEY (rule_id);

ALTER INDEX public.pk_skip_rule OWNER TO clinica;

ALTER TABLE ONLY skip_rule
  ADD CONSTRAINT fk_skip_rule_crf_version FOREIGN KEY (crf_version_id) 
  REFERENCES crf_version(crf_version_id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY skip_rule
  ADD CONSTRAINT fk_skip_rule_status FOREIGN KEY (status_id) 
  REFERENCES status (status_id);

ALTER TABLE ONLY skip_rule
  ADD CONSTRAINT fk_skip_rule_user_account FOREIGN KEY (owner_id)
  REFERENCES user_account (user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;
