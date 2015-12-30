-- -----------------------------------------------------------------------------
-- -----------------------------------------------------------------------------
-- OpenClinica 1.1 basecase data
-- -----------------------------------------------------------------------------
-- -----------------------------------------------------------------------------
INSERT INTO &clinica..status
            (status_id, NAME,
             description
            )
     VALUES (&clinica..status_seq.NEXTVAL, 'available',
             'this is the active status'
            );
INSERT INTO &clinica..status
            (status_id, NAME,
             description
            )
     VALUES (&clinica..status_seq.NEXTVAL, 'unavailable',
             'this is the inactive status'
            );
INSERT INTO &clinica..status
            (status_id, NAME, description
            )
     VALUES (&clinica..status_seq.NEXTVAL, 'private', NULL
            );
INSERT INTO &clinica..status
            (status_id, NAME, description
            )
     VALUES (&clinica..status_seq.NEXTVAL, 'pending', NULL
            );
INSERT INTO &clinica..status
            (status_id, NAME, description
            )
     VALUES (&clinica..status_seq.NEXTVAL, 'removed', NULL
            );
INSERT INTO &clinica..status
            (status_id, NAME, description
            )
     VALUES (&clinica..status_seq.NEXTVAL, 'locked', NULL
            );

INSERT INTO &clinica..completion_status
            (status_id, NAME,
             description
            )
     VALUES ((SELECT status_id
                FROM &clinica..status
               WHERE NAME = 'available'), 'completion status',
             'place filler for completion status'
            );

INSERT INTO &clinica..group_class_types
            (NAME, description
            )
     VALUES ('Arm', NULL
            );
INSERT INTO &clinica..group_class_types
            (NAME, description
            )
     VALUES ('Family/Pedigree', NULL
            );

INSERT INTO &clinica..group_class_types
            (NAME, description
            )
     VALUES ('Demographic', NULL
            );

INSERT INTO &clinica..group_class_types
            (NAME, description
            )
     VALUES ('Other', NULL
            );
            
INSERT INTO &clinica..item_data_type
            (code, NAME, item_data_type_definition, REFERENCE
            )
     VALUES ('BL', 'Boolean', NULL, NULL
            );
INSERT INTO &clinica..item_data_type
            (code, NAME, item_data_type_definition, REFERENCE
            )
     VALUES ('BN', 'BooleanNonNull', NULL, NULL
            );
INSERT INTO &clinica..item_data_type
            (code, NAME, item_data_type_definition, REFERENCE
            )
     VALUES ('ED', 'Encapsulated Data', NULL, NULL
            );
INSERT INTO &clinica..item_data_type
            (code, NAME, item_data_type_definition, REFERENCE
            )
     VALUES ('TEL', 'A telecommunication address', NULL, NULL
            );
INSERT INTO &clinica..item_data_type
            (code, NAME, item_data_type_definition, REFERENCE
            )
     VALUES ('ST', 'Character String', NULL, NULL
            );
INSERT INTO &clinica..item_data_type
            (code, NAME, item_data_type_definition, REFERENCE
            )
     VALUES ('INT', 'Integer', NULL, NULL
            );
INSERT INTO &clinica..item_data_type
            (code, NAME, item_data_type_definition, REFERENCE
            )
     VALUES ('REAL', 'Floating', NULL, NULL
            );
INSERT INTO &clinica..item_data_type
            (code, NAME, item_data_type_definition, REFERENCE
            )
     VALUES ('SET', NULL, 'a value that contains other distinct values', NULL
            );
INSERT INTO &clinica..item_data_type
            (code, NAME, item_data_type_definition, REFERENCE
            )
     VALUES ('DATE', 'date', 'date', NULL
            );

INSERT INTO &clinica..item_reference_type
            (NAME, description
            )
     VALUES ('literal', NULL
            );

INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('NI', 'NoInformation', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('NA', 'not applicable', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('UNK', 'unknown', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('NASK', 'not asked', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('ASKU', 'asked but unknown', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('NAV', 'temporarily unavailable', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('OTH', 'other', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('PINF', 'positive infinity', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('NINF', 'negative infinity', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('MSK', 'masked', NULL, NULL
            );
INSERT INTO &clinica..null_value_type
            (code, NAME, null_value_type_definition, REFERENCE
            )
     VALUES ('NP', 'not present', NULL, NULL
            );

INSERT INTO &clinica..response_type
            (NAME, description
            )
     VALUES ('text', 'free form text entry limited to one line'
            );
INSERT INTO &clinica..response_type
            (NAME, description
            )
     VALUES ('textarea', 'free form text area display'
            );
INSERT INTO &clinica..response_type
            (NAME, description
            )
     VALUES ('checkbox', 'selecting one from many options'
            );
INSERT INTO &clinica..response_type
            (NAME, description
            )
     VALUES ('file', 'for upload of files'
            );
INSERT INTO &clinica..response_type
            (NAME, description
            )
     VALUES ('radio', 'selecting one from many options'
            );
INSERT INTO &clinica..response_type
            (NAME, description
            )
     VALUES ('single-select', 'pick one from a list'
            );
INSERT INTO &clinica..response_type
            (NAME, description
            )
     VALUES ('multi-select', 'pick many from a list'
            );
INSERT INTO &clinica..response_type
            (NAME, description
            )
     VALUES ('calculation', 'value calculated automatically'
            );

INSERT INTO &clinica..study_type
            (study_type_id, NAME, description
            )
     VALUES (&clinica..study_type_seq.NEXTVAL, 'genetic', NULL
            );
INSERT INTO &clinica..study_type
            (study_type_id, NAME, description
            )
     VALUES (&clinica..study_type_seq.NEXTVAL, 'non-genetic', NULL
            );

INSERT INTO &clinica..user_type
            (user_type_id, user_type
            )
     VALUES (&clinica..user_type_seq.NEXTVAL, 'admin'
            );
INSERT INTO &clinica..user_type
            (user_type_id, user_type
            )
     VALUES (&clinica..user_type_seq.NEXTVAL, 'user'
            );
INSERT INTO &clinica..user_type
            (user_type_id, user_type
            )
     VALUES (&clinica..user_type_seq.NEXTVAL, 'tech-admin'
            );

INSERT INTO &clinica..user_role
            (role_id, role_name,
             parent_id
            )
     VALUES (&clinica..user_role_seq.NEXTVAL, 'admin',
             &clinica..user_role_seq.CURRVAL
            );
INSERT INTO &clinica..user_role
            (role_name, parent_id
            )
     VALUES ('coordinator', (SELECT role_id
                               FROM &clinica..user_role
                              WHERE role_name = 'admin')
            );
INSERT INTO &clinica..user_role
            (role_name, parent_id
            )
     VALUES ('director', (SELECT role_id
                            FROM &clinica..user_role
                           WHERE role_name = 'admin')
            );
INSERT INTO &clinica..user_role
            (role_name, parent_id
            )
     VALUES ('investigator', (SELECT role_id
                                FROM &clinica..user_role
                               WHERE role_name = 'admin')
            );
INSERT INTO &clinica..user_role
            (role_name, parent_id
            )
     VALUES ('ra', (SELECT role_id
                      FROM &clinica..user_role
                     WHERE role_name = 'admin')
            );
INSERT INTO &clinica..user_role
            (role_name, parent_id
            )
     VALUES ('guest', (SELECT role_id
                         FROM &clinica..user_role
                        WHERE role_name = 'admin')
            );


INSERT INTO &clinica..user_account
            (user_id, user_name,
             passwd, first_name, last_name,
             email, active_study, institutional_affiliation,
             status_id, owner_id,
             date_created, date_updated, date_lastvisit, passwd_timestamp,
             passwd_challenge_question, passwd_challenge_answer, phone,
             user_type_id,
             update_id
            )
     VALUES (&clinica..user_account_seq.NEXTVAL, 'root',
             '25d55ad283aa400af464c76d713c07ad', 'Root', 'User',
             'openclinica_admin@akazaresearch.com', NULL, 'Akaza Research',
             (SELECT status_id
                FROM &clinica..status
               WHERE NAME = 'available'), &clinica..user_account_seq.CURRVAL,
             NULL, SYSDATE, SYSDATE, SYSDATE,
             NULL, NULL, '515 444 2222',
             (SELECT user_type_id
                FROM &clinica..user_type
               WHERE user_type = 'tech-admin'),
             &clinica..user_account_seq.CURRVAL
            );
INSERT INTO &clinica..user_account
            (user_id, user_name,
             passwd, first_name, last_name,
             email, active_study, institutional_affiliation,
             status_id, owner_id,
             date_created, date_updated, date_lastvisit, passwd_timestamp,
             passwd_challenge_question, passwd_challenge_answer, phone,
             user_type_id, update_id
            )
     VALUES (&clinica..user_account_seq.NEXTVAL, 'demo_ra',
             '6e9bece1914809fb8493146417e722f6', 'demo', 'ra',
             'demo_ra@akazaresearch.com', NULL, 'Akaza Research',
             (SELECT status_id
                FROM &clinica..status
               WHERE NAME = 'available'), (SELECT user_id
                                             FROM &clinica..user_account
                                            WHERE user_name = 'root'),
             NULL, SYSDATE, SYSDATE, SYSDATE,
             NULL, NULL, '515 444 2222',
             (SELECT user_type_id
                FROM &clinica..user_type
               WHERE user_type = 'admin'), (SELECT user_id
                                              FROM &clinica..user_account
                                             WHERE user_name = 'root')
            );
INSERT INTO &clinica..user_account
            (user_id, user_name,
             passwd, first_name, last_name,
             email, active_study, institutional_affiliation, status_id,
             owner_id, date_created, date_updated, date_lastvisit,
             passwd_timestamp, passwd_challenge_question,
             passwd_challenge_answer, phone, user_type_id, update_id
            )
     VALUES (&clinica..user_account_seq.NEXTVAL, 'demo_director',
             '6e9bece1914809fb8493146417e722f6', 'demo', 'director',
             'demo_director@akazaresearch.com', NULL,  'Akaza Research', 
             (SELECT status_id
                FROM &clinica..status
               WHERE NAME = 'available'), (SELECT user_id
                                             FROM &clinica..user_account
                                            WHERE user_name = 'root'),
             NULL, SYSDATE, SYSDATE,
             SYSDATE, NULL,
             NULL, '515 444 2222', 
             (SELECT user_type_id
                FROM &clinica..user_type
               WHERE user_type = 'admin'), (SELECT user_id
                                              FROM &clinica..user_account
                                             WHERE user_name = 'root')
            );


INSERT INTO &clinica..study
            (study_id, parent_study_id, unique_identifier,
             secondary_identifier, NAME, SUMMARY, date_planned_start,
             date_planned_end, date_created, date_updated, owner_id,
             update_id, type_id, status_id, principal_investigator,
             facility_name, facility_city, facility_state, facility_zip,
             facility_country, facility_recruitment_status,
             facility_contact_name, facility_contact_degree,
             facility_contact_phone, facility_contact_email, protocol_type,
             protocol_description, protocol_date_verification, phase,
             expected_total_enrollment, sponsor, collaborators,
             medline_identifier, url, url_description, conditions, keywords,
             eligibility, gender, age_max, age_min,
             healthy_volunteer_accepted, purpose, allocation, masking,
             control, assignment, endpoint, interventions, DURATION,
             selection, timing, official_title, results_reference,
             collect_dob, discrepancy_management
            )
     VALUES (&clinica..study_seq.NEXTVAL, NULL, 'default-study',
             'default-study', 'Default Study', 
             'This is a default study used to pre-populate the system', SYSDATE,
             SYSDATE, SYSDATE, SYSDATE, 
             (SELECT user_id FROM &clinica..user_account WHERE user_name = 'root'),
             NULL, 
             (SELECT study_type_id FROM &clinica..study_type WHERE NAME = 'genetic'), 
             (SELECT status_id FROM &clinica..status WHERE NAME = 'available'), 
             'default',
             '', '', '', '',
             '', '',
             '', '',
             '', '', 'observational',
             '', SYSDATE, 'default',
             0, 'default', '',
             '', '', '', '', '',
             '', 'both', '', '',
             '0', 'Natural History', '', '',
             '', '', '', '', 'longitudinal',
             'Convenience Sample', 'Retrospective', '', '0',
             '0', '0'
            );


INSERT INTO &clinica..study_user_role
            (role_name, study_id, status_id, owner_id, date_created, date_updated,
             update_id, user_name)
     VALUES ('admin', 
             (SELECT study_id FROM &clinica..study WHERE unique_identifier = 'default-study'), 
             (SELECT status_id FROM &clinica..status WHERE NAME = 'available'), 
             (SELECT user_id FROM &clinica..user_account WHERE user_name = 'root'),
             SYSDATE, NULL, NULL, 'root');
INSERT INTO &clinica..study_user_role
            (role_name, study_id, status_id, owner_id, date_created, date_updated,
             update_id, user_name)
     VALUES ('director', 
             (SELECT study_id FROM &clinica..study WHERE unique_identifier = 'default-study'), 
             (SELECT status_id FROM &clinica..status WHERE NAME = 'available'), 
             (SELECT user_id FROM &clinica..user_account WHERE user_name = 'root'), 
             SYSDATE, NULL, NULL, 'root');
INSERT INTO &clinica..study_user_role
            (role_name, study_id, status_id, owner_id, date_created, date_updated,
             update_id, user_name)
     VALUES ('ra', 
             (SELECT study_id FROM &clinica..study WHERE unique_identifier = 'default-study'), 
             (SELECT status_id FROM &clinica..status WHERE NAME = 'available'), 
             (SELECT user_id FROM &clinica..user_account WHERE user_name = 'root'), 
             SYSDATE, NULL, NULL, 'demo_ra');
INSERT INTO &clinica..study_user_role
            (role_name, study_id, status_id, owner_id, date_created, date_updated,
             update_id, user_name)
     VALUES ('director', 
             (SELECT study_id FROM &clinica..study WHERE unique_identifier = 'default-study'), 
             (SELECT status_id FROM &clinica..status WHERE NAME = 'available'), 
             (SELECT user_id FROM &clinica..user_account WHERE user_name = 'root'), 
            SYSDATE, NULL, NULL, 'demo_director');

UPDATE &clinica..user_account
   SET active_study = 1
 WHERE user_id IN (1, 2, 3);
 
INSERT INTO &clinica..export_format
            (export_format_id, NAME, description, mime_type)
     VALUES (&clinica..export_format_seq.NEXTVAL, 'text/plain', 
             'Default export format for tab-delimited text',
             'text/plain');
INSERT INTO &clinica..export_format
            (export_format_id, NAME, description, mime_type)
     VALUES (&clinica..export_format_seq.NEXTVAL, 'text/plain',
             'Default export format for comma-delimited text', 'text/plain');
INSERT INTO &clinica..export_format
            (export_format_id, NAME, description, mime_type)
     VALUES (&clinica..export_format_seq.NEXTVAL, 'application/vnd.ms-excel',
             'Default export format for Excel files',
             'application/vnd.ms-excel');

INSERT INTO &clinica..discrepancy_note_type
            (discrepancy_note_type_id, NAME, description)
     VALUES (&clinica..discrepancy_note_type_seq.NEXTVAL, 'Failed Validation Check', '');
INSERT INTO &clinica..discrepancy_note_type
            (discrepancy_note_type_id, NAME, description)
     VALUES (&clinica..discrepancy_note_type_seq.NEXTVAL, 'Incomplete', '');
INSERT INTO &clinica..discrepancy_note_type
            (discrepancy_note_type_id, NAME, description)
     VALUES (&clinica..discrepancy_note_type_seq.NEXTVAL, 'Unclear/Unreadable', '');
INSERT INTO &clinica..discrepancy_note_type
            (discrepancy_note_type_id, NAME, description)
     VALUES (&clinica..discrepancy_note_type_seq.NEXTVAL, 'Annotation', '');
INSERT INTO &clinica..discrepancy_note_type
            (discrepancy_note_type_id, NAME, description)
     VALUES (&clinica..discrepancy_note_type_seq.NEXTVAL, 'Other', '');
     
     
INSERT INTO &clinica..resolution_status
            (resolution_status_id, NAME, description)
     VALUES (&clinica..resolution_status_seq.NEXTVAL, 'New/Open', '');
INSERT INTO &clinica..resolution_status
            (resolution_status_id, NAME, description)
     VALUES (&clinica..resolution_status_seq.NEXTVAL, 'Updated', '');
INSERT INTO &clinica..resolution_status
            (resolution_status_id, NAME, description)
     VALUES (&clinica..resolution_status_seq.NEXTVAL, 'Resolved/Closed', '');


COMMIT
/
