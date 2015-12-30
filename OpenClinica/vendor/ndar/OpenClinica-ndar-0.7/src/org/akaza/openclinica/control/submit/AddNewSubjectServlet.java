/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

// import org.akaza.openclinica.bean.core.Role;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.SummaryDataEntryStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateEventDefBean;
import org.akaza.openclinica.bean.subject.Person;
import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.subjectmgmt.AddNewPiiSubjectServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.managestudy.StudyTemplateDAO;
import org.akaza.openclinica.dao.managestudy.StudyTemplateEventDefDAO;
import org.akaza.openclinica.dao.subject.LdapServer;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

// import javax.servlet.http.*;

/**
 * Enroll a new subject into system
 * 
 * @author ssachs
 * @version CVS: $Id: AddNewSubjectServlet.java,v 1.15 2005/07/05 17:20:43 jxu
 *          Exp $
 */
public class AddNewSubjectServlet extends SecureController {

  public static final String INPUT_UNIQUE_IDENTIFIER = "uniqueIdentifier";// global

  // Id

  public static final String INPUT_DOB = "dob";

  public static final String INPUT_YOB = "yob"; // year of birth

  public static final String INPUT_GENDER = "gender";

  public static final String INPUT_LABEL = "label";

  public static final String INPUT_SECONDARY_LABEL = "secondaryLabel";

  public static final String INPUT_ENROLLMENT_DATE = "enrollmentDate";

  public static final String INPUT_GROUP = "group";

  public static final String INPUT_FATHER = "father";

  public static final String INPUT_MOTHER = "mother";

  public static final String BEAN_GROUPS = "groups";

  public static final String BEAN_FATHERS = "fathers";

  public static final String BEAN_MOTHERS = "mothers";

  public static final String SUBMIT_EVENT_BUTTON = "submitEvent";

  public static final String SUBMIT_ENROLL_BUTTON = "submitEnroll";

  public static final String SUBMIT_DONE_BUTTON = "submitDone";

  public static final String EDIT_DOB = "editDob";

  public static final String EXISTING_SUB_SHOWN = "existingSubShown";

  public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#processRequest()
   */
  protected void processRequest() throws Exception {
    StudySubjectDAO ssd = new StudySubjectDAO(sm.getDataSource());
    StudyTemplateDAO sttdao = new StudyTemplateDAO(sm.getDataSource());
    ArrayList classes = (ArrayList) sttdao.findAllActiveByStudy(currentStudy);
    panel.setStudyInfoShown(false);
    FormProcessor fp = new FormProcessor(request);
    FormDiscrepancyNotes discNotes;

    if (!fp.isSubmitted()) {
      if (fp.getBoolean("instr")) {
        session.removeAttribute(FORM_DISCREPANCY_NOTES_NAME);
        forwardPage(Page.INSTRUCTIONS_ENROLL_SUBJECT);
      } else {

        setUpBeans(classes);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date(System.currentTimeMillis());
        String todayFormatted = sdf.format(today);
        fp.addPresetValue(INPUT_ENROLLMENT_DATE, todayFormatted);

        String idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
        logger.info("subject id setting :" + idSetting);
        // set up auto study subject id
        if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
          int nextLabel = ssd.findTheGreatestLabel() + 1;
          fp.addPresetValue(INPUT_LABEL, new Integer(nextLabel).toString());
        }

        setPresetValues(fp.getPresetValues());
        discNotes = new FormDiscrepancyNotes();
        session.setAttribute(FORM_DISCREPANCY_NOTES_NAME, discNotes);
        
        if(LdapServer.GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())){
            forwardPage(Page.ADD_NEW_SUBJECT_WITH_PII);
        } else{
          forwardPage(Page.ADD_NEW_SUBJECT);
        }
        
      }
    } else {
      discNotes = (FormDiscrepancyNotes) session.getAttribute(FORM_DISCREPANCY_NOTES_NAME);
      DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);

      v.addValidation(INPUT_LABEL, Validator.NO_BLANKS);

      v.addValidation(INPUT_LABEL, Validator.LENGTH_NUMERIC_COMPARISON,
          NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);

      // if
      // (currentStudy.getStudyParameterConfig().getSubjectPersonIdRequired().equals("required"))
      // {
      // v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.NO_BLANKS);
      // }
      v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.LENGTH_NUMERIC_COMPARISON,
          NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

      if (!StringUtil.isBlank(fp.getString(INPUT_SECONDARY_LABEL))) {
        v.addValidation(INPUT_SECONDARY_LABEL, Validator.LENGTH_NUMERIC_COMPARISON,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);
      }
      
      v.addValidation(INPUT_ENROLLMENT_DATE, Validator.IS_A_DATE);
      v.alwaysExecuteLastValidation(INPUT_ENROLLMENT_DATE);

      v.addValidation(INPUT_ENROLLMENT_DATE, Validator.DATE_IN_PAST);

      String dobSetting = currentStudy.getStudyParameterConfig().getCollectDob();
      if (dobSetting.equals("1")) {// date of birth
        v.addValidation(INPUT_DOB, Validator.IS_A_DATE);
        if (!StringUtil.isBlank(fp.getString("INPUT_DOB"))) {
          v.alwaysExecuteLastValidation(INPUT_DOB);
        }
        v.addValidation(INPUT_DOB, Validator.DATE_IN_PAST);
        
        Date enrollDate = fp.getDate(INPUT_ENROLLMENT_DATE);
        Date birthDate  = fp.getDate(INPUT_DOB);
        if (enrollDate != null && birthDate!= null ) {
          v.addValidation(INPUT_ENROLLMENT_DATE,Validator.DATE_IS_AFTER_OR_EQUAL,INPUT_DOB);
   
        }
      } else if (dobSetting.equals("2")) {// year of birth
        v.addValidation(INPUT_YOB, Validator.IS_AN_INTEGER);
        v.alwaysExecuteLastValidation(INPUT_YOB);
        v.addValidation(INPUT_YOB, Validator.COMPARES_TO_STATIC_VALUE,
            NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, 1000);

        // get today's year
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int currentYear = c.get(Calendar.YEAR);
        v.addValidation(INPUT_YOB, Validator.COMPARES_TO_STATIC_VALUE,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, currentYear);
        
        Date enrollDate = fp.getDate(INPUT_ENROLLMENT_DATE);
        if (enrollDate != null) {
          c.setTime(enrollDate);
          int enrollYear = c.get(Calendar.YEAR);
          v.addValidation(INPUT_YOB, Validator.COMPARES_TO_STATIC_VALUE,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, enrollYear);
        }
       
      }

      ArrayList acceptableGenders = new ArrayList();
      acceptableGenders.add("m");
      acceptableGenders.add("f");

      if (!currentStudy.getStudyParameterConfig().getGenderRequired().equals("false")) {
        v.addValidation(INPUT_GENDER, Validator.IS_IN_SET, acceptableGenders);
      }
    
      
      if (LdapServer.GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())) {
        if (!StringUtil.isBlank(fp.getString(AddNewPiiSubjectServlet.INPUT_DOB_OF_MOTHER))) {
          v.addValidation(AddNewPiiSubjectServlet.INPUT_DOB_OF_MOTHER, Validator.IS_A_DATE);
          v.alwaysExecuteLastValidation(AddNewPiiSubjectServlet.INPUT_DOB_OF_MOTHER);
          v.addValidation(AddNewPiiSubjectServlet.INPUT_DOB_OF_MOTHER, Validator.DATE_IN_PAST);
        }
        if (!StringUtil.isBlank(fp.getString(AddNewPiiSubjectServlet.INPUT_DOB_OF_FATHER))) {
          v.addValidation(AddNewPiiSubjectServlet.INPUT_DOB_OF_FATHER, Validator.IS_A_DATE);
          v.alwaysExecuteLastValidation(AddNewPiiSubjectServlet.INPUT_DOB_OF_FATHER);
          v.addValidation(AddNewPiiSubjectServlet.INPUT_DOB_OF_FATHER, Validator.DATE_IN_PAST);
        }
       }

      if (!classes.isEmpty()) {
        for (int i = 0; i < classes.size(); i++) {
          StudyTemplateBean stb = (StudyTemplateBean) classes.get(i);
          int groupId = fp.getInt("studyGroupId" + i);
          String notes = fp.getString("notes" + i);

          if ("Required".equalsIgnoreCase(stb.getSubjectAssignment())) {
            // Validator.addError(errors, "studyGroupId" + i,
            // "This study template is required.");
            v.addValidation("studyGroupId" + i, Validator.IS_REQUIRED);
            
          }
          if (notes.trim().length() > 255) {
            // Validator.addError(errors, "notes" + i,
            // "Notes cannot be longer than 255 characters.");
            v.addValidation("notes" + i, Validator.COMPARES_TO_STATIC_VALUE,
                NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
          }
          if (!StringUtil.isBlank(fp.getString("studyTemplateStartDate" + i))) {
            v.addValidation("studyTemplateStartDate" + i, Validator.IS_A_DATE);
            stb.setStudyTemplateStartDate(fp.getDate("studyTemplateStartDate" + i));

          }
          stb.setStudyGroupId(groupId);
          stb.setGroupNotes(notes);

        }
      }

      HashMap errors = v.validate();

      SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
      String uniqueIdentifier = fp.getString(INPUT_UNIQUE_IDENTIFIER);// global
                                                                      // Id
      SubjectBean subjectWithSameId = new SubjectBean();
      boolean showExistingRecord = false;
      if (!uniqueIdentifier.equals("")) {
        SubjectBean existedSubject = sdao.findByUniqueIdentifier(uniqueIdentifier);
        if (existedSubject == null) {
          Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER, "This subjet with Person ID:"
              + uniqueIdentifier + " doesn't exist in the system.");
        } else {
          boolean subjectWithSameIdInCurrentStudyTree = false;
          // checks whether there is a subject with same id inside current
          // study/site
          subjectWithSameId = sdao.findByUniqueIdentifierAndStudy(uniqueIdentifier, currentStudy
              .getId());
          if (subjectWithSameId.isActive()) {
            Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER, "A subject with the Person ID: "
                + uniqueIdentifier + " is already enrolled in this study.");

            subjectWithSameIdInCurrentStudyTree = true;
          } else {
            // checks whether there is a subject with same id inside sites of
            // current study
            subjectWithSameId = sdao.findByUniqueIdentifierAndParentStudy(uniqueIdentifier,
                currentStudy.getId());
            if (subjectWithSameId.isActive()) {
              StudyDAO stdao = new StudyDAO(sm.getDataSource());
              StudySubjectBean ssub = ssd.findBySubjectIdAndStudy(subjectWithSameId.getId(),
                  currentStudy);
              StudyBean site = (StudyBean) stdao.findByPK(ssub.getStudyId());
              Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER, "This subject (Person ID:"
                  + uniqueIdentifier + ") has already been enrolled in the site (" + site.getName()
                  + ") of current study. If you need to move this subject to a different site, "
                  + "please have a user with Manage Study privileges reassign the subject.");
              subjectWithSameIdInCurrentStudyTree = true;
            } else {
              // check whether there is a subject with same id in the parent
              // study
              subjectWithSameId = sdao.findByUniqueIdentifierAndStudy(uniqueIdentifier,
                  currentStudy.getParentStudyId());
              if (subjectWithSameId.isActive()) {
                Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER, "This subject with Person ID:"
                    + uniqueIdentifier + " has already been enrolled in the parent study.");

                subjectWithSameIdInCurrentStudyTree = true;
              }
            }
          }

          if (!subjectWithSameIdInCurrentStudyTree) {
            subjectWithSameId = sdao.findByUniqueIdentifier(uniqueIdentifier);
            // found subject with same id in other study
            if (subjectWithSameId != null && subjectWithSameId.isActive()) {
              showExistingRecord = true;
            }
          }
        }
      }

      boolean insertWithParents = ((fp.getInt(INPUT_MOTHER) > 0) || (fp.getInt(INPUT_FATHER) > 0));

      if (fp.getInt(INPUT_MOTHER) > 0) {
        SubjectBean mother = (SubjectBean) sdao.findByPK(fp.getInt(INPUT_MOTHER));
        if ((mother == null) || !mother.isActive() || (mother.getGender() != 'f')) {
          Validator.addError(errors, INPUT_MOTHER,
              "Please choose a valid female subject as the mother.");
        }
      }

      if (fp.getInt(INPUT_FATHER) > 0) {
        SubjectBean father = (SubjectBean) sdao.findByPK(fp.getInt(INPUT_FATHER));
        if ((father == null) || !father.isActive() || (father.getGender() != 'm')) {
          Validator.addError(errors, INPUT_FATHER,
              "Please choose a valid male subject as the father.");
        }
      }

      String label = fp.getString(INPUT_LABEL);
      StudySubjectBean subjectWithSameLabel = ssd.findByLabelAndStudy(label, currentStudy);

      if (subjectWithSameLabel.isActive()) {
        Validator
            .addError(
                errors,
                INPUT_LABEL,
                "Another subject has been enrolled in the active study with this identifier.  Please choose an identifier unique to this study.");
      }

      if (!errors.isEmpty()) {

        addPageMessage("There were some errors in your submission.");
        setInputMessages(errors);
        fp.addPresetValue(INPUT_DOB, fp.getString(INPUT_DOB));
        fp.addPresetValue(INPUT_YOB, fp.getString(INPUT_YOB));
        fp.addPresetValue(INPUT_GENDER, fp.getString(INPUT_GENDER));
        fp.addPresetValue(INPUT_UNIQUE_IDENTIFIER, uniqueIdentifier);
        fp.addPresetValue(INPUT_LABEL, label);
        fp.addPresetValue(INPUT_SECONDARY_LABEL, fp.getString(INPUT_SECONDARY_LABEL));
        fp.addPresetValue(INPUT_ENROLLMENT_DATE, fp.getString(INPUT_ENROLLMENT_DATE));
        
        if (LdapServer.GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())) {
          fp.addPresetValue(AddNewPiiSubjectServlet.INPUT_GIVENNAME_AT_BIRTH,fp.getString(AddNewPiiSubjectServlet.INPUT_GIVENNAME_AT_BIRTH));
          fp.addPresetValue(AddNewPiiSubjectServlet.INPUT_MIDDLENAME_AT_BIRTH,fp.getString(AddNewPiiSubjectServlet.INPUT_MIDDLENAME_AT_BIRTH));
          fp.addPresetValue(AddNewPiiSubjectServlet.INPUT_SURNAME_OF_FATHER,fp.getString(AddNewPiiSubjectServlet.INPUT_SURNAME_OF_FATHER));
          fp.addPresetValue(AddNewPiiSubjectServlet.INPUT_SURNAME_OF_MOTHER,fp.getString(AddNewPiiSubjectServlet.INPUT_GIVENNAME_OF_FATHER));
          fp.addPresetValue(AddNewPiiSubjectServlet.INPUT_SURNAME_OF_MOTHER,fp.getString(AddNewPiiSubjectServlet.INPUT_SURNAME_OF_MOTHER));
          fp.addPresetValue(AddNewPiiSubjectServlet.INPUT_GIVENNAME_OF_MOTHER,fp.getString(AddNewPiiSubjectServlet.INPUT_GIVENNAME_OF_MOTHER));
          fp.addPresetValue(AddNewPiiSubjectServlet.INPUT_DOB_OF_FATHER,fp.getString(AddNewPiiSubjectServlet.INPUT_DOB_OF_FATHER));
          fp.addPresetValue(AddNewPiiSubjectServlet.INPUT_DOB_OF_MOTHER,fp.getString(AddNewPiiSubjectServlet.INPUT_DOB_OF_MOTHER));
          fp.addPresetValue(AddNewPiiSubjectServlet.INPUT_COB,fp.getString(AddNewPiiSubjectServlet.INPUT_COB));
        }
        if (currentStudy.isGenetic()) {
          String intFields[] = { INPUT_GROUP, INPUT_FATHER, INPUT_MOTHER };
          fp.setCurrentIntValuesAsPreset(intFields);
        }
        fp.addPresetValue(EDIT_DOB, fp.getString(EDIT_DOB));
        setPresetValues(fp.getPresetValues());

        setUpBeans(classes);
        boolean existingSubShown = fp.getBoolean(EXISTING_SUB_SHOWN);

        if (!existingSubShown) {
          if(LdapServer.GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())){
            forwardPage(Page.ADD_NEW_SUBJECT_WITH_PII);
          }
          else{
            forwardPage(Page.ADD_NEW_SUBJECT);
          }
        } else {
          forwardPage(Page.ADD_EXISTING_SUBJECT);
        }
      } else {
        // no errors
        StudySubjectBean studySubject = new StudySubjectBean();

        Person person = new Person();

        SubjectBean subject = new SubjectBean();
        boolean existingSubShown = fp.getBoolean(EXISTING_SUB_SHOWN);
        if (showExistingRecord && !existingSubShown) {
          subject = subjectWithSameId;
          Calendar cal = Calendar.getInstance();
          int year = 0;
          if (subject.getDateOfBirth() != null) {
            cal.setTime(subject.getDateOfBirth());
            year = cal.get(Calendar.YEAR);
            fp.addPresetValue(INPUT_DOB, sdf.format(subject.getDateOfBirth()));
          } else {
            fp.addPresetValue(INPUT_DOB, "");
          }

          if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")
              && !subject.isDobCollected()) {
            fp.addPresetValue(EDIT_DOB, "yes");
          }

          fp.addPresetValue(INPUT_SECONDARY_LABEL, fp.getString(INPUT_SECONDARY_LABEL));
          fp.addPresetValue(INPUT_ENROLLMENT_DATE, fp.getString(INPUT_ENROLLMENT_DATE));
          fp.addPresetValue(INPUT_LABEL, fp.getString(INPUT_LABEL));
          fp.addPresetValue(INPUT_YOB, String.valueOf(year));

          if (!currentStudy.getStudyParameterConfig().getGenderRequired().equals("false")) {
            fp.addPresetValue(INPUT_GENDER, subject.getGender() + "");
          } else {
            fp.addPresetValue(INPUT_GENDER, "");
          }

          fp.addPresetValue(INPUT_UNIQUE_IDENTIFIER, subject.getUniqueIdentifier());

          fp.addPresetValue(INPUT_FATHER, subject.getFatherId());
          fp.addPresetValue(INPUT_MOTHER, subject.getMotherId());

          setPresetValues(fp.getPresetValues());
          setUpBeans(classes);
          forwardPage(Page.ADD_EXISTING_SUBJECT);
          return;

        } else {

          if (!StringUtil.isBlank(fp.getString(INPUT_GENDER))) {
            subject.setGender(fp.getString(INPUT_GENDER).charAt(0));
          } else {
            subject.setGender(' ');
          }

          if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
            if (!StringUtil.isBlank(fp.getString(INPUT_DOB))) {
              subject.setDateOfBirth(fp.getDate(INPUT_DOB));
              subject.setDobCollected(true);
            } else {
              subject.setDateOfBirth(null);
              subject.setDobCollected(false);
            }

          } else {
            // generate a fake birthday in 01/01/YYYY format, only the year is
            // valid
            subject.setDobCollected(false);
            int yob = fp.getInt(INPUT_YOB);
            String dobString = "01/01/" + yob;
            try {
              Date fakeDOB = sdf.parse(dobString);
              subject.setDateOfBirth(fakeDOB);
            } catch (ParseException pe) {
              subject.setDateOfBirth(new Date());
              addPageMessage("Problem happened on saving the Year of Birth.");
            }

          }

          Calendar calendar = Calendar.getInstance();
          calendar.setTime(subject.getDateOfBirth());
          person.setDob(calendar);
          
          if (LdapServer.GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())) {
            person.setSurnameAtBirth(fp.getString(AddNewPiiSubjectServlet.INPUT_SURNAME_AT_BIRTH));
            person.setGivenNameAtBirth(fp.getString(AddNewPiiSubjectServlet.INPUT_GIVENNAME_AT_BIRTH));
            person.setMiddleNameAtBirth(fp.getString(AddNewPiiSubjectServlet.INPUT_MIDDLENAME_AT_BIRTH));
            person.setFsurname(fp.getString(AddNewPiiSubjectServlet.INPUT_SURNAME_OF_FATHER));
            person.setFgivenName(fp.getString(AddNewPiiSubjectServlet.INPUT_GIVENNAME_OF_FATHER));
            person.setMsurname(fp.getString(AddNewPiiSubjectServlet.INPUT_SURNAME_OF_MOTHER));
            person.setMgivenName(fp.getString(AddNewPiiSubjectServlet.INPUT_GIVENNAME_OF_MOTHER));
            Calendar fdob = Calendar.getInstance();
            fdob.setTime(fp.getDate(AddNewPiiSubjectServlet.INPUT_DOB_OF_FATHER));
            person.setFdob(fdob);
            Calendar mdob = Calendar.getInstance();
            fdob.setTime(fp.getDate(AddNewPiiSubjectServlet.INPUT_DOB_OF_MOTHER));
            person.setMdob(mdob);
            person.setCob(fp.getString(AddNewPiiSubjectServlet.INPUT_COB));
            String gender = fp.getString(INPUT_GENDER);
            if("m".equalsIgnoreCase(gender)){
            person.setGender(Person.GENDER_MALE);
            }else if("f".equalsIgnoreCase(gender)){
            person.setGender(Person.GENDER_FEMALE);
            }else{
            person.setGender(Person.GENDER_UNKNOWN);
            }
        }

          subject.setStatus(Status.AVAILABLE);
          subject.setOwner(ub);

          if (uniqueIdentifier == null || uniqueIdentifier.length() == 0) {
            LdapServer server = LdapServer.getInstance();
            if (!server.isConnected()) {
              addPageMessage("The connection with LDAP server was unable to be created, please contact your administrator.");
              forwardPage(Page.SUBMIT_DATA_SERVLET);
              return;
            }
            int ret = server.insertPerson(person);
            if (ret != LdapServer.SUCCESS) {
              addPageMessage("The PII information was not able to be created on your LDAP server, please contact your administrator.");
              forwardPage(Page.SUBMIT_DATA_SERVLET);
              return;
            }
            uniqueIdentifier = person.getPersonId();
            subject.setUniqueIdentifier(uniqueIdentifier);

            if (insertWithParents) {
              subject.setFatherId(fp.getInt(INPUT_FATHER));
              subject.setMotherId(fp.getInt(INPUT_MOTHER));
            }
            subject = sdao.create(subject);

            if (!sdao.isQuerySuccessful() || !subject.isActive()) {
              if (server.deletePerson(person)) {
                addPageMessage("The subject was not able to be created in your database, and none of your changes have been saved, please contact your administrator.");
              } else {
                addPageMessage("The subject was not able to be created in your database, and the changes saved in on your LDAP server was unable to be rolled back, please contact your administrator.");
              }
              forwardPage(Page.SUBMIT_DATA_SERVLET);
              throw new OpenClinicaException("Could not create subject.", "3");
            }
          } else {
            subject = sdao.findByUniqueIdentifier(uniqueIdentifier);
            if (subject == null) {
              throw new OpenClinicaException("Could not create subject matching identifier: "
                  + uniqueIdentifier + ".", "3");
            }
          }

          // enroll the subject in the active study
          studySubject.setSubjectId(subject.getId());
          studySubject.setStudyId(currentStudy.getId());
          studySubject.setLabel(fp.getString(INPUT_LABEL));
          studySubject.setSecondaryLabel(fp.getString(INPUT_SECONDARY_LABEL));
          studySubject.setStatus(Status.AVAILABLE);
          studySubject.setEnrollmentDate(fp.getDate(INPUT_ENROLLMENT_DATE));
          studySubject.setOwner(ub);

          studySubject = (StudySubjectBean) ssd.createWithoutGroup(studySubject);
          if (!classes.isEmpty() && studySubject.isActive()) {
            SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
            for (int i = 0; i < classes.size(); i++) {
              StudyTemplateBean template = (StudyTemplateBean) classes.get(i);
              int studyGroupId = template.getStudyGroupId();
              String notes = template.getGroupNotes();
              SubjectGroupMapBean map = new SubjectGroupMapBean();
              map.setNotes(template.getGroupNotes());
              map.setStatus(Status.AVAILABLE);
              map.setStudyGroupId(template.getStudyGroupId());
              map.setStudySubjectId(studySubject.getId());
              map.setStudyGroupClassId(template.getId());
              map.setStudyTemplateStartDate(template.getStudyTemplateStartDate());
              map.setOwner(ub);
              if (map.getStudyGroupId() > 0) {
                sgmdao.create(map);
              }

              //if this subject is assigned into a group template and has a start date for this template
              //and if there is an event sequence in the template,
              // we should create study events for this subject
              // according to the event sequence-jxu
              if (map.getStudyGroupId() > 0 && template.getStudyTemplateStartDate() != null) {
                logger.info("template start date is not null");
                StudyTemplateEventDefDAO steddao = new StudyTemplateEventDefDAO(sm.getDataSource());
                ArrayList groupTemplates = steddao.findAllActiveByStudyTemplate(template.getId());
                StudyEventBean previous = new StudyEventBean();
                for (int k = 0; k < groupTemplates.size(); k++) {
                  StudyTemplateEventDefBean sted = (StudyTemplateEventDefBean) groupTemplates.get(k);
                  logger.info("will create proposed events for the new subject");
                 
                  StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
                  StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
                  StudyEventBean se = new StudyEventBean();
                  
                  se.setStudyTemplateEventDefId(sted.getId());
                  
                  //find the default location
                  se.setLocation(currentStudy.getFacilityCity());
                                                                  
                  // calculate the start date
                  Calendar cal = Calendar.getInstance();
                  if (k == 0) {// the very first event in the template
                    se.setDateStarted(template.getStudyTemplateStartDate());
                    se.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
                    
                  } else {
                    cal.setTime(previous.getDateStarted());
                    StudyTemplateEventDefBean prevSted = (StudyTemplateEventDefBean) groupTemplates
                    .get(k-1);
                    cal.add(Calendar.DATE, new Float(prevSted.getEventDuration()+ prevSted.getIdealTimeToNextEvent()).intValue());
                    se.setDateStarted(cal.getTime());
                    se.setSubjectEventStatus(SubjectEventStatus.PROPOSED);
                  }

                  // calculate the end date
                  cal.setTime(se.getDateStarted());
                  cal.add(Calendar.DATE, sted.getEventDuration().intValue());
                  se.setDateEnded(cal.getTime());

                  se.setOwner(ub);
                  se.setStudyEventDefinitionId(sted.getStudyEventDefinitionId());
                  se.setStatus(Status.AVAILABLE);
                  se.setStudySubjectId(studySubject.getId());                 
                  se.setSummaryDataEntryStatus(SummaryDataEntryStatus.NOT_STARTED);
                  StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(sted
                      .getStudyEventDefinitionId());
                  se.setSampleOrdinal(sedao.getMaxSampleOrdinal(sed, studySubject) + 1);
                  sedao.create(se);
                  previous = se;

                }
              }//end if template.getStudyTemplateStartDate() != null

            }
          }

          if (!studySubject.isActive()) {
            throw new OpenClinicaException("Could not create study subject.", "4");
          }
        }

        // save discrepancy notes into DB
        FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
            .getAttribute(FORM_DISCREPANCY_NOTES_NAME);
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

        String[] subjectFields = { INPUT_DOB, INPUT_YOB, INPUT_GENDER };
        for (int i = 0; i < subjectFields.length; i++) {
          saveFieldNotes(subjectFields[i], fdn, dndao, subject.getId(), "subject", currentStudy);
        }
        saveFieldNotes(INPUT_ENROLLMENT_DATE, fdn, dndao, studySubject.getId(), "studySub",
            currentStudy);

        request.removeAttribute(FormProcessor.FIELD_SUBMITTED);
        request.setAttribute(CreateNewStudyEventServlet.INPUT_STUDY_SUBJECT, studySubject);
        request.setAttribute(CreateNewStudyEventServlet.INPUT_REQUEST_STUDY_SUBJECT, "no");
        request.setAttribute(FormProcessor.FIELD_SUBMITTED, "0");

        addPageMessage("The subject with unique identifier '" + studySubject.getLabel()
            + "' was created.");

        //this is for creating subject from the manage subject page
        //the express way of creating subject with event
        if (fp.getBoolean("addWithEvent")) {
          createStudyEvent(fp, studySubject);
          forwardPage(Page.SUBMIT_DATA_SERVLET);
          return;
        }

        String submitEvent = fp.getString(SUBMIT_EVENT_BUTTON);
        String submitEnroll = fp.getString(SUBMIT_ENROLL_BUTTON);
        String submitDone = fp.getString(SUBMIT_DONE_BUTTON);

        session.removeAttribute(FORM_DISCREPANCY_NOTES_NAME);
        if (!StringUtil.isBlank(submitEvent)) {
          forwardPage(Page.CREATE_NEW_STUDY_EVENT_SERVLET);
        } else if (!StringUtil.isBlank(submitEnroll)) {
          forwardPage(Page.INSTRUCTIONS_ENROLL_SUBJECT_SERVLET);
        } else {
          forwardPage(Page.SUBMIT_DATA_SERVLET);
        }
      }
    }
  }

  protected void createStudyEvent(FormProcessor fp, StudySubjectBean s) {
    int studyEventDefinitionId = fp.getInt("studyEventDefinition");
    String location = fp.getString("location");
    Date startDate = s.getEnrollmentDate();

    if (studyEventDefinitionId > 0 && !StringUtil.isBlank(location)) {
      logger.info("will create single event with new subject");
      StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
      StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
      StudyEventBean se = new StudyEventBean();
      se.setLocation(location);
      se.setDateStarted(startDate);
      se.setDateEnded(startDate);
      se.setOwner(ub);
      se.setStudyEventDefinitionId(studyEventDefinitionId);
      se.setStatus(Status.AVAILABLE);
      se.setStudySubjectId(s.getId());
      se.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
      se.setSummaryDataEntryStatus(SummaryDataEntryStatus.NOT_STARTED);
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
          .findByPK(studyEventDefinitionId);
      se.setSampleOrdinal(sedao.getMaxSampleOrdinal(sed, s) + 1);
      sedao.create(se);

    } else {
      addPageMessage("But the event cannot be created because some errors happened."
          + "The fields for event are all required. Please choose 'Add New Study Event' from Menu and try to add events again. ");

    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
   */
  protected void mayProceed() throws InsufficientPermissionException {
    String exceptionName = "no permission to add new subject";
    String noAccessMessage = "You may not add a new subject to this study.  Please change your active study or contact the System Administrator.";

    if (SubmitDataServlet.maySubmitData(ub, currentRole)) {
      return;
    }

    addPageMessage(noAccessMessage);
    throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
  }

  protected void setUpBeans(ArrayList classes) throws Exception {
    StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
    // addEntityList(BEAN_GROUPS, sgdao.findAllByStudy(currentStudy),
    // "A group must be available in order to add new subjects to this study;
    // however, there are no groups in this Study. Please contact your Study
    // Director.",
    // Page.SUBMIT_DATA);

    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    ArrayList fathers = sdao.findAllByGender('m');
    ArrayList mothers = sdao.findAllByGender('f');

    ArrayList dsFathers = new ArrayList();
    ArrayList dsMothers = new ArrayList();

    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    StudyDAO stdao = new StudyDAO(sm.getDataSource());

    displaySubjects(dsFathers, fathers, ssdao, stdao);
    displaySubjects(dsMothers, mothers, ssdao, stdao);

    request.setAttribute(BEAN_FATHERS, dsFathers);
    request.setAttribute(BEAN_MOTHERS, dsMothers);

    for (int i = 0; i < classes.size(); i++) {
      StudyTemplateBean group = (StudyTemplateBean) classes.get(i);
      ArrayList studyGroups = sgdao.findAllByGroupClass(group);
      group.setStudyGroups(studyGroups);
    }

    request.setAttribute(BEAN_GROUPS, classes);
  }

  /**
   * Save the discrepancy notes of each field into session in the form
   * 
   * @param field
   * @param notes
   * @param dndao
   * @param entityId
   * @param entityType
   * @param sb
   */
  public static void saveFieldNotes(String field, FormDiscrepancyNotes notes,
      DiscrepancyNoteDAO dndao, int entityId, String entityType, StudyBean sb) {
    ArrayList fieldNotes = notes.getNotes(field);

    for (int i = 0; i < fieldNotes.size(); i++) {
      DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) fieldNotes.get(i);
      dnb.setEntityId(entityId);
      dnb.setStudyId(sb.getId());
      dnb.setEntityType(entityType);
      dnb = (DiscrepancyNoteBean) dndao.create(dnb);
      dndao.createMapping(dnb);
    }
  }

  /**
   * Find study subject id for each subject, and construct displaySubjectBean
   * 
   * @param displayArray
   * @param subjects
   * @param ds
   */
  public static void displaySubjects(ArrayList displayArray, ArrayList subjects,
      StudySubjectDAO ssdao, StudyDAO stdao) {

    for (int i = 0; i < subjects.size(); i++) {
      SubjectBean subject = (SubjectBean) subjects.get(i);
      ArrayList studySubs = ssdao.findAllBySubjectId(subject.getId());
      String protocolSubjectIds = "";
      for (int j = 0; j < studySubs.size(); j++) {
        StudySubjectBean studySub = (StudySubjectBean) studySubs.get(j);
        int studyId = studySub.getStudyId();
        StudyBean stu = (StudyBean) stdao.findByPK(studyId);
        String protocolId = stu.getIdentifier();
        if (j == (studySubs.size() - 1)) {
          protocolSubjectIds = protocolId + "-" + studySub.getLabel();
        } else {
          protocolSubjectIds = protocolId + "-" + studySub.getLabel() + ", ";
        }
      }
      DisplaySubjectBean dsb = new DisplaySubjectBean();
      dsb.setSubject(subject);
      dsb.setStudySubjectIds(protocolSubjectIds);
      displayArray.add(dsb);

    }

  }
}