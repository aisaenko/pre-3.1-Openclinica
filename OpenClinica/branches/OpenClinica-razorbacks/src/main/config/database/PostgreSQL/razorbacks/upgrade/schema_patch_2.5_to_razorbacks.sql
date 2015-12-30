--
-- temporary location for subject registration
--
CREATE TABLE subject_transfer
(
  subject_transfer_id serial NOT NULL,
  grid_id character varying(50),
  person_id character varying(255),
  study_subject_id character varying(30),
  secondary_id character varying(30),
  enrollment_date date,
  date_of_birth date,
  gender character(1),
  study_oid character varying(40),
  owner_id integer,
  datetime_received timestamp
);
ALTER TABLE subject_transfer OWNER TO clinica;

CREATE INDEX i_subject_transfer_subject_transfer_id ON subject_transfer USING btree (subject_transfer_id);

ALTER TABLE user_account ALTER user_name TYPE character varying(128);
ALTER TABLE study_user_role ALTER user_name TYPE character varying(128);
