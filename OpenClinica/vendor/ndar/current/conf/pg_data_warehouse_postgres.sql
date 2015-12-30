
-- View: "test_view_three"


DROP TABLE test_table_three;

CREATE TABLE test_table_three AS

 SELECT ss.subject_id, 
	ss.label AS subject_identifier, 
	su.study_id,
	su.unique_identifier AS study_identifier, 
	edc.event_definition_crf_id, crf.crf_id,
	crf.description AS crf_description, 
	crf.name AS crf_name, crfv.crf_version_id,
	crfv.revision_notes AS crf_version_revision_notes, 
	crfv.name AS crf_version_name,
	se.study_event_id, 
	ec.event_crf_id, id.item_data_id, id.value, 
	sed.name AS study_event_definition_name, 
	sed.repeating AS study_event_def_repeating,
	se.sample_ordinal, 
	it.item_id, 
	it.name AS item_name, 
	it.description AS item_description, 
	it.units AS item_units, 
	ss.enrollment_date AS date_created,
	sed.study_event_definition_id,
	rs.options_text,
	rs.options_values,
	rs.response_type_id,
	s.gender,
	s.date_of_birth,
	se.location,
	se.date_start,
	se.date_end
FROM study su, subject s, event_definition_crf edc, crf, crf_version crfv,
study_event se, event_crf ec, item_data id, item it, study_subject ss,
study_event_definition sed, item_form_metadata ifm, response_set rs

WHERE it.item_id::numeric = id.item_id 
AND ifm.item_id = id.item_id
AND ifm.response_set_id = rs.response_set_id
AND id.event_crf_id = ec.event_crf_id
AND ec.crf_version_id = crfv.crf_version_id 
AND ec.study_event_id = se.study_event_id 
AND se.study_subject_id = ss.study_subject_id 
AND ss.subject_id = s.subject_id 
AND se.study_event_definition_id = sed.study_event_definition_id::numeric 
AND sed.study_id = su.study_id::numeric 
AND sed.study_event_definition_id::numeric = edc.study_event_definition_id 
AND edc.crf_id = crf.crf_id::numeric 
AND crf.crf_id::numeric = crfv.crf_id 
AND (id.status_id = 2::numeric OR id.status_id = 6::numeric) 
AND (ec.status_id = 2::numeric OR ec.status_id = 6::numeric);

ALTER TABLE test_table_three OWNER TO clinica;
