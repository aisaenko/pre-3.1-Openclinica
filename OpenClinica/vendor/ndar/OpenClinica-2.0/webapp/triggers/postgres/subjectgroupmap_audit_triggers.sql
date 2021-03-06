DROP TRIGGER subjectgroupmap_AUDIT_TRIGGER ON SUBJECT_GROUP_MAP;
DROP FUNCTION subjectgroupmap_AUDIT_SQL();


CREATE FUNCTION subjectgroupmap_AUDIT_SQL() RETURNS OPAQUE AS '
DECLARE
	pk INTEGER;
BEGIN 
SELECT INTO PK NEXTVAL(''audit_log_sequence'');

INSERT INTO AUDIT_EVENT (AUDIT_ID, AUDIT_DATE, AUDIT_TABLE, 
				USER_ID, ENTITY_ID, REASON_FOR_CHANGE)
	VALUES
	(pk, now(), 
	''SUBJECT_GROUP_MAP'', 
	NEW.UPDATE_ID,
	NEW.USER_ID,
	''UPDATE TRIGGER FIRED'');

IF OLD.STUDY_GROUP_ID IS NOT NULL AND (OLD.STUDY_GROUP_ID <> NEW.STUDY_GROUP_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''STUDY_GROUP_ID'', OLD.STUDY_GROUP_ID, NEW.STUDY_GROUP_ID);
	
END IF;

IF OLD.STUDY_SUBJECT_ID IS NOT NULL AND (OLD.STUDY_SUBJECT_ID <> NEW.STUDY_SUBJECT_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''STUDY_SUBJECT_ID'', OLD.STUDY_SUBJECT_ID, NEW.STUDY_SUBJECT_ID);
	
END IF;
IF OLD.GROUP_ROLE_ID IS NOT NULL AND (OLD.GROUP_ROLE_ID <> NEW.GROUP_ROLE_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''GROUP_ROLE_ID'', OLD.GROUP_ROLE_ID, NEW.GROUP_ROLE_ID);
	
END IF;
IF OLD.STATUS_ID IS NOT NULL AND (OLD.STATUS_ID <> NEW.STATUS_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''STATUS_ID'', OLD.STATUS_ID, NEW.STATUS_ID);
	
END IF;
IF OLD.DATE_CREATED IS NOT NULL AND (OLD.DATE_CREATED <> NEW.DATE_CREATED)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DATE_CREATED'', OLD.DATE_CREATED, NEW.DATE_CREATED);
	
END IF;
IF OLD.DATE_UPDATED IS NOT NULL AND (OLD.DATE_UPDATED <> NEW.DATE_UPDATED)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DATE_UPDATED'', OLD.DATE_UPDATED, NEW.DATE_UPDATED);
	
END IF;
IF OLD.OWNER_ID IS NOT NULL AND (OLD.OWNER_ID <> NEW.OWNER_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''OWNER_ID'', OLD.OWNER_ID, NEW.OWNER_ID);
	
END IF;
IF OLD.UPDATE_ID IS NOT NULL AND (OLD.UPDATE_ID <> NEW.UPDATE_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''UPDATE_ID'', OLD.UPDATE_ID, NEW.UPDATE_ID);
	
END IF;

return null;
END;
' LANGUAGE 'plpgsql';


CREATE TRIGGER subjectgroupmap_AUDIT_TRIGGER
AFTER UPDATE
ON SUBJECT_GROUP_MAP
FOR EACH ROW
EXECUTE PROCEDURE subjectgroupmap_audit_sql();