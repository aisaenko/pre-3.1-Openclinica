DROP TABLE &clinica..test_table_three
/

CREATE TABLE &clinica..test_table_three AS
  SELECT ss.subject_id,
         ss.label AS subject_identifier,
         su.study_id,
         su.unique_identifier AS study_identifier,
         edc.event_definition_crf_id, crf.crf_id,
         crf.description AS crf_description,
         crf.NAME AS crf_name, crfv.crf_version_id,
         crfv.revision_notes AS crf_version_revision_notes,
         crfv.NAME AS crf_version_name,
         se.study_event_id,
         ec.event_crf_id, ID.item_data_id, ID.VALUE,
         sed.NAME AS study_event_definition_name,
         sed.repeating AS study_event_def_repeating,
         se.sample_ordinal,
         it.item_id,
         it.NAME AS item_name,
         it.description AS item_description,
         it.units AS item_units,
         ss.enrollment_date AS date_created,
         sed.study_event_definition_id,
         rs.options_text,
         rs.options_values,
         rs.response_type_id,
         s.gender,
         s.date_of_birth,
         se.LOCATION,
         se.date_start,
         se.date_end
    FROM &clinica..study su, &clinica..subject s, &clinica..event_definition_crf edc,
         &clinica..crf, &clinica..crf_version crfv, &clinica..study_event se,
         &clinica..event_crf ec, &clinica..item_data ID, &clinica..item it,
         &clinica..study_subject ss, &clinica..study_event_definition sed,
         &clinica..item_form_metadata ifm, &clinica..response_set rs
   WHERE it.item_id = ID.item_id
     AND ifm.item_id = ID.item_id
     AND ifm.response_set_id = rs.response_set_id
     AND ID.event_crf_id = ec.event_crf_id
     AND ec.crf_version_id = crfv.crf_version_id
     AND ec.study_event_id = se.study_event_id
     AND se.study_subject_id = ss.study_subject_id
     AND ss.subject_id = s.subject_id
     AND se.study_event_definition_id = sed.study_event_definition_id
     AND sed.study_id = su.study_id
     AND sed.study_event_definition_id = edc.study_event_definition_id
     AND edc.crf_id = crf.crf_id
     AND crf.crf_id = crfv.crf_id
     AND (ID.status_id = 2 OR ID.status_id = 6)
     AND (ec.status_id = 2 OR ec.status_id = 6)
/

