CREATE OR REPLACE FUNCTION fixordinal() RETURNS integer AS $$
DECLARE
    s_events RECORD;    

    s_e_def RECORD;
    sed_id RECORD;
    ss_id RECORD;
    count INTEGER;
BEGIN
    FOR sed_id IN SELECT distinct study_event_definition_id FROM study_event LOOP
	SELECT INTO s_e_def * FROM study_event_definition sed WHERE sed.study_event_definition_id = sed_id.study_event_definition_id;
	
	IF s_e_def.repeating THEN
		
		FOR ss_id IN SELECT DISTINCT STUDY_SUBJECT_ID FROM STUDY_EVENT WHERE STUDY_EVENT_DEFINITION_ID = sed_id.study_event_definition_id LOOP
			count := 1;
			FOR s_events IN SELECT * FROM STUDY_EVENT WHERE STUDY_EVENT_DEFINITION_ID = sed_id.study_event_definition_id AND STUDY_SUBJECT_ID = ss_id.study_subject_id ORDER BY STUDY_EVENT_ID LOOP
				--s_events.sample_ordinal := count;
				UPDATE STUDY_EVENT SET SAMPLE_ORDINAL = count WHERE STUDY_EVENT_ID = s_events.study_event_id;
				count := count + 1;

			END LOOP;
		END LOOP;
			
	ELSE
		FOR s_events in SELECT * FROM STUDY_EVENT WHERE STUDY_EVENT_DEFINITION_ID = sed_id.study_event_definition_id LOOP
			--s_events.sample_ordinal := 1;
			UPDATE STUDY_EVENT SET SAMPLE_ORDINAL = 1 WHERE STUDY_EVENT_ID = s_events.study_event_id;
		END LOOP;
		--s_events.sample_ordinal := 1;
		--set all sample ordinals to 1 for that se definition
	END IF;
    END LOOP;

    RETURN 1;
END;
$$ LANGUAGE plpgsql;

select fixordinal();