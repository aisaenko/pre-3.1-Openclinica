/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.subjectmgmt;

//import org.akaza.openclinica.bean.core.Role;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.PiiPrivilege;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.subject.Person;
import org.akaza.openclinica.bean.subject.SubjectLabelMapBean;
import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.subject.LdapServer;
import org.akaza.openclinica.dao.subject.SubjectEntryLabelDAO;
import org.akaza.openclinica.dao.subject.SubjectLabelMapDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

//import javax.servlet.http.*;

/**
 * Enroll a new subject into system
 * 
 * @author ssachs
 * @version CVS: $Id: AddNewSubjectServlet.java,v 1.15 2005/07/05 17:20:43 jxu
 *          Exp $
 */
public class AddNewPiiSubjectServlet extends SecureController {

    public static final String INPUT_UNIQUE_IDENTIFIER = "guid";

    public static final String INPUT_GIVENNAME = "givenname";

    public static final String INPUT_SURNAME = "surname";

    public static final String INPUT_GIVENNAME_AT_BIRTH = "givennameAtBirth";

    public static final String INPUT_MIDDLENAME_AT_BIRTH = "middlenameAtBirth";

    public static final String INPUT_SURNAME_AT_BIRTH = "surnameAtBirth";

    public static final String INPUT_GIVENNAME_OF_FATHER = "givennameOfFather";

    public static final String INPUT_SURNAME_OF_FATHER = "surnameOfFather";

    public static final String INPUT_GIVENNAME_OF_MOTHER = "givennameOfMother";

    public static final String INPUT_SURNAME_OF_MOTHER = "surnameOfMother";

    public static final String INPUT_ADDRESS = "streetAddress";

    public static final String INPUT_CITY = "localityName";

    public static final String INPUT_STATE = "stateOrProvinceName";

    public static final String INPUT_ZIP = "postalCode";

    public static final String INPUT_PHONE = "telephoneNumber";

    public static final String INPUT_EMAIL = "emailAddress";

    public static final String INPUT_DOB = "dob";
    public static final String INPUT_COB = "cob";

    public static final String INPUT_DOB_OF_FATHER = "dobOfFather";

    public static final String INPUT_DOB_OF_MOTHER = "dobOfMother";

    public static final String INPUT_YOB = "yob"; //year of birth

    public static final String INPUT_GENDER = "gender";

    public static final String INPUT_SUBJECT_ENTRY_LABEL = "subjectEntryLabelId";

    public static final String INPUT_FATHER = "father";

    public static final String INPUT_MOTHER = "mother";

    public static final String BEAN_FATHERS = "fathers";

    public static final String BEAN_MOTHERS = "mothers";

    public static final String SUBMIT_ENROLL_BUTTON = "submitEnroll";

    public static final String SUBMIT_DONE_BUTTON = "submitDone";

    public static final String EDIT_DOB = "editDob";

    public static final String EXISTING_SUB_SHOWN = "existingSubShown";

    public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

    /*
     * (non-Javadoc)
     * 
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    protected void mayProceed() throws InsufficientPermissionException {
	String exceptionName = "no permission to add new subject";
	String noAccessMessage = "You may not add a new subject to this study.  Please change your active study or contact the System Administrator.";

    if (ub.getPiiPrivilege().equals(PiiPrivilege.EDIT)){
        return;
    }

	addPageMessage(noAccessMessage);
	throw new InsufficientPermissionException(Page.MENU_SERVLET, exceptionName, "1");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.akaza.openclinica.control.core.SecureController#processRequest()
     */
    protected void processRequest() throws Exception {
	panel.setStudyInfoShown(false);
	FormProcessor fp = new FormProcessor(request);
	FormDiscrepancyNotes discNotes;

	LdapServer server = LdapServer.getInstance();
	if (!server.isConnected()) {
	    addPageMessage("The connection with LDAP server was unable to be created, please contact your administrator.");
	    resetPanel();

	    panel.setStudyInfoShown(false);
	    panel.setOrderedData(true);
	    setToPanel("You currently have", "");
	    setToPanel("Subjects: ", new Integer(0).toString());
	    forwardPage(Page.PII_SUBJECT_LIST);
	    return;
	}

	List<Person> subjects = server.query(0, "");
	if (!fp.isSubmitted()) {
	    if (fp.getBoolean("instr")) {
		session.removeAttribute(FORM_DISCREPANCY_NOTES_NAME);
		forwardPage(Page.INSTRUCTIONS_ENROLL_PII_SUBJECT);
	    } else {

		setUpBeans();

		setPresetValues(fp.getPresetValues());
		discNotes = new FormDiscrepancyNotes();
		session.setAttribute(FORM_DISCREPANCY_NOTES_NAME, discNotes);
		SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm
			.getDataSource());
		request.setAttribute("entryLabels", seldao.findAll());
		resetPanel();

		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		setToPanel("You currently have", "");
		if (subjects == null || subjects.size() == 0) {
		    setToPanel("Subjects: ", new Integer(0).toString());
		} else {
		    setToPanel("Subjects: ", new Integer(subjects.size())
			    .toString());
		}
		if(LdapServer.GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())){
		    forwardPage(Page.ADD_NEW_PII_SUBJECT_USING_WS);
		}else{
		    forwardPage(Page.ADD_NEW_PII_SUBJECT);
		}
	    }
	} else {
	    discNotes = (FormDiscrepancyNotes) session
		    .getAttribute(FORM_DISCREPANCY_NOTES_NAME);
	    DiscrepancyValidator v = new DiscrepancyValidator(request,
		    discNotes);
	    //v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.NO_BLANKS);
	    //      v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.LENGTH_NUMERIC_COMPARISON,
	    //          NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

	    v.addValidation(INPUT_DOB, Validator.IS_A_DATE);
	    v.alwaysExecuteLastValidation(INPUT_DOB);
	    v.addValidation(INPUT_DOB, Validator.DATE_IN_PAST);

	    if (LdapServer.GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())) {
//		v.addValidation(INPUT_DOB_OF_MOTHER, Validator.IS_A_DATE);
//		v.alwaysExecuteLastValidation(INPUT_DOB_OF_MOTHER);
//		v.addValidation(INPUT_DOB_OF_MOTHER, Validator.DATE_IN_PAST);
//		v.addValidation(INPUT_DOB_OF_FATHER, Validator.IS_A_DATE);
//		v.alwaysExecuteLastValidation(INPUT_DOB_OF_FATHER);
//		v.addValidation(INPUT_DOB_OF_FATHER, Validator.DATE_IN_PAST);
	    }
	    ArrayList acceptableGenders = new ArrayList();
	    acceptableGenders.add("m");
	    acceptableGenders.add("f");
	    v.addValidation(INPUT_GENDER, Validator.IS_IN_SET,
		    acceptableGenders);

	    if (fp.getString(INPUT_PHONE) != null
		    && fp.getString(INPUT_PHONE).length() > 0) {
		v.addValidation(INPUT_PHONE, Validator.IS_A_PHONE_NUMBER);
	    }
	    if (fp.getString(INPUT_EMAIL) != null
		    && fp.getString(INPUT_EMAIL).length() > 0) {
		v.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);
	    }
	    if (fp.getString(INPUT_ZIP) != null
		    && fp.getString(INPUT_ZIP).length() > 0) {
		v.addValidation(INPUT_ZIP, Validator.IS_A_POSTAL_CODE);
	    }
	    errors = v.validate();

	    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
	    String guid = fp.getString(INPUT_UNIQUE_IDENTIFIER);//global Id
	    if (guid != null && !guid.equals("")) {
		SubjectBean subjectWithSameId = new SubjectBean();
		//checks whether there is a subject with same global id
		subjectWithSameId = sdao
			.findByUniqueIdentifier(guid);
		if (subjectWithSameId != null && subjectWithSameId.isActive()) {
		    Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER,
			    "A subject with the Person ID: " + guid
				    + " already exists.");
		}
	    }

	    boolean insertWithParents = ((fp.getInt(INPUT_MOTHER) > 0) || (fp
		    .getInt(INPUT_FATHER) > 0));

	    if (fp.getInt(INPUT_MOTHER) > 0) {
		SubjectBean mother = (SubjectBean) sdao.findByPK(fp
			.getInt(INPUT_MOTHER));
		if ((mother == null) || !mother.isActive()
			|| (mother.getGender() != 'f')) {
		    Validator
			    .addError(errors, INPUT_MOTHER,
				    "Please choose a valid female subject as the mother.");
		}
	    }

	    if (fp.getInt(INPUT_FATHER) > 0) {
		SubjectBean father = (SubjectBean) sdao.findByPK(fp
			.getInt(INPUT_FATHER));
		if ((father == null) || !father.isActive()
			|| (father.getGender() != 'm')) {
		    Validator
			    .addError(errors, INPUT_FATHER,
				    "Please choose a valid male subject as the father.");
		}
	    }

	    if (!errors.isEmpty()) {
		/*this doesnt work
		 * if (errors.containsKey(INPUT_GENDER)) {
		 errors.put(INPUT_GENDER, "Please select either Male or Female for gender.");
		 }
		 if (errors.containsKey(INPUT_YOB)) {
		 errors.put(INPUT_YOB, "Please enter the Year of Birth in YYYY format.");
		 }
		 if (errors.containsKey(INPUT_ENROLLMENT_DATE)) {
		 errors.put(INPUT_ENROLLMENT_DATE, "The enrollment date cannot be in the future.");
		 }*/

		addPageMessage("There were some errors in your submission.");
		setInputMessages(errors);
		for (Object error : errors.keySet()) {
		    System.out.println(error.toString() + ": "
			    + errors.get(error));
		}
		loadPresetValuesFromForm(fp);

		fp.addPresetValue(EDIT_DOB, fp.getString(EDIT_DOB));
		setPresetValues(fp.getPresetValues());

		setUpBeans();
		boolean existingSubShown = fp.getBoolean(EXISTING_SUB_SHOWN);

		SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm
			.getDataSource());
		request.setAttribute("entryLabels", seldao.findAll());

		if (!existingSubShown) {
		    if(LdapServer.GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())){
			forwardPage(Page.ADD_NEW_PII_SUBJECT_USING_WS);
		    }else{
			forwardPage(Page.ADD_NEW_PII_SUBJECT);
		    }
		} else {
		    forwardPage(Page.ADD_EXISTING_PII_SUBJECT);
		}
	    } else {
		// no errors

		Person person = new Person();

		SubjectBean subject = new SubjectBean();

		if (!StringUtil.isBlank(fp.getString(INPUT_GENDER))) {
		    subject.setGender(fp.getString(INPUT_GENDER).charAt(0));
		} else {
		    subject.setGender(' ');
		}

		subject.setDateOfBirth(fp.getDate(INPUT_DOB));
		subject.setDobCollected(true);

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(subject.getDateOfBirth());
		person.setDob(calendar);
		person.setGivenName(fp.getString(INPUT_GIVENNAME));
		person.setSurname(fp.getString(INPUT_SURNAME));
		person.setStreetAddress(fp.getString(INPUT_ADDRESS));
		person.setLocalityName(fp.getString(INPUT_CITY));
		person.setStateOrProvinceName(fp.getString(INPUT_STATE));
		person.setPostalCode(fp.getString(INPUT_ZIP));
		person.setTelephoneNumber(fp.getString(INPUT_PHONE));
		person.setEmailAddress(fp.getString(INPUT_EMAIL));
		if (LdapServer.GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())) {
		    person.setSurnameAtBirth(fp.getString(INPUT_SURNAME_AT_BIRTH));
		    person.setGivenNameAtBirth(fp.getString(INPUT_GIVENNAME_AT_BIRTH));
		    person.setMiddleNameAtBirth(fp.getString(INPUT_MIDDLENAME_AT_BIRTH));
		    person.setFsurname(fp.getString(INPUT_SURNAME_OF_FATHER));
		    person.setFgivenName(fp.getString(INPUT_GIVENNAME_OF_FATHER));
		    person.setMsurname(fp.getString(INPUT_SURNAME_OF_MOTHER));
		    person.setMgivenName(fp.getString(INPUT_GIVENNAME_OF_MOTHER));
		    Calendar fdob = Calendar.getInstance();
		    fdob.setTime(fp.getDate(INPUT_DOB_OF_FATHER));
		    person.setFdob(fdob);
		    Calendar mdob = Calendar.getInstance();
		    mdob.setTime(fp.getDate(INPUT_DOB_OF_MOTHER));
		    person.setMdob(mdob);
		    person.setCob(fp.getString(INPUT_COB));
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

		if (guid == null || guid.length() == 0) {
		    System.out.println("inserting person...");
		    int ret = server.insertPerson(person);
		    if (ret == LdapServer.ALREADY_EXISTING_SUBJECT_ERROR) {
			addPageMessage("The subject already existed in your PII database. If you want to update, please go to the editing page.");
			forwardPage(Page.LIST_PII_SUBJECT_SERVLET);
			return;
		    }else if (ret == LdapServer.CREATING_SUBCONTEXT_IN_LDAP_ERROR) {
			addPageMessage("The subject was not able to be created in your PII database, and none of your changes have been saved, please contact your administrator.");
			forwardPage(Page.LIST_PII_SUBJECT_SERVLET);
			return;
		    }else if (ret == LdapServer.EMPTY_PERSON_ERROR) {
			addPageMessage("The subject was empty, please double check your input.");
			forwardPage(Page.LIST_PII_SUBJECT_SERVLET);
			return;
		    }
		    guid = person.getPersonId();
		}
		subject.setUniqueIdentifier(guid);

		System.out.println("creating subject...");
		if (insertWithParents) {
		    System.out.println("...with parents...");
		    subject.setFatherId(fp.getInt(INPUT_FATHER));
		    subject.setMotherId(fp.getInt(INPUT_MOTHER));
		}
		
		// Check whether this subject already exists
		SubjectBean sb = sdao.findByUniqueIdentifier(subject.getUniqueIdentifier());
		if(sb != null){
		    addPageMessage("The subject is already existed in the system. If you want to update, please go to the editing page.");
		    forwardPage(Page.LIST_PII_SUBJECT_SERVLET);
		    return;
		}else{
		    subject = sdao.create(subject);
		    if (!sdao.isQuerySuccessful() || !subject.isActive()) {
			if (server.deletePerson(person)) {
			    addPageMessage("The subject was not able to be created in your database, and none of your changes have been saved, please contact your administrator.");
			} else {
			    addPageMessage("The subject was not able to be created in your database, and the changes saved in on your LDAP server was unable to be rolled back, please contact your administrator.");
			}
			forwardPage(Page.LIST_PII_SUBJECT_SERVLET);
			throw new OpenClinicaException(
				"Could not create subject.", "3");
		    }
		}

		int subjectLabelId = fp.getInt(INPUT_SUBJECT_ENTRY_LABEL);
		if (subjectLabelId != 0) {
		    SubjectLabelMapDAO slmdao = new SubjectLabelMapDAO(sm
			    .getDataSource());
		    SubjectLabelMapBean slmb = new SubjectLabelMapBean();
		    slmb.setSubjectEntryLabelId(subjectLabelId);
		    slmb.setSubjectId(subject.getId());
		    slmdao.create(slmb);

		    if (!slmdao.isQuerySuccessful()) {
			throw new OpenClinicaException(
				"Could not create subject label.", "3");
		    }
		}

		//save discrepancy notes into DB
		FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
			.getAttribute(FORM_DISCREPANCY_NOTES_NAME);
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm
			.getDataSource());

		String[] subjectFields = { INPUT_DOB, INPUT_YOB, INPUT_GENDER };
		for (int i = 0; i < subjectFields.length; i++) {
		    saveFieldNotes(subjectFields[i], fdn, dndao, subject
			    .getId(), "subject");
		}

		request.removeAttribute(FormProcessor.FIELD_SUBMITTED);
		request.setAttribute(FormProcessor.FIELD_SUBMITTED, "0");

		addPageMessage("The subject was created.");//AJF
		String submitEnroll = fp.getString(SUBMIT_ENROLL_BUTTON);

		session.removeAttribute(FORM_DISCREPANCY_NOTES_NAME);
		if (!StringUtil.isBlank(submitEnroll)) {
		    forwardPage(Page.ADD_PII_SUBJECT_SERVLET);
		} else {
		    forwardPage(Page.LIST_PII_SUBJECT_SERVLET);
		}
	    }
	}
    }

    private void loadPresetValuesFromForm(FormProcessor fp) {
	fp.clearPresetValues();

	String textFields[] = { INPUT_UNIQUE_IDENTIFIER, INPUT_GIVENNAME,
		INPUT_SURNAME, INPUT_ADDRESS, INPUT_CITY, INPUT_STATE,
		INPUT_ZIP, INPUT_PHONE, INPUT_EMAIL, INPUT_DOB, INPUT_GENDER };
	fp.setCurrentStringValuesAsPreset(textFields);

	String intFields[] = { INPUT_FATHER, INPUT_MOTHER,
		INPUT_SUBJECT_ENTRY_LABEL };
	fp.setCurrentIntValuesAsPreset(intFields);
    }

    protected void setUpBeans() throws Exception {
	//		addEntityList(BEAN_GROUPS, sgdao.findAllByStudy(currentStudy),
	//				"A group must be available in order to add new subjects to this study;
	// however, there are no groups in this Study. Please contact your Study
	// Director.",
	//				Page.SUBMIT_DATA);

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
    }

    /**
     * Save the discrepancy notes of each field into session in the form
     * @param field
     * @param notes
     * @param dndao
     * @param entityId
     * @param entityType
     * @param sb
     */
    public static void saveFieldNotes(String field, FormDiscrepancyNotes notes,
	    DiscrepancyNoteDAO dndao, int entityId, String entityType) {
	ArrayList fieldNotes = notes.getNotes(field);

	for (int i = 0; i < fieldNotes.size(); i++) {
	    DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) fieldNotes.get(i);
	    dnb.setEntityId(entityId);
	    dnb.setStudyId(0);
	    dnb.setEntityType(entityType);
	    dnb = (DiscrepancyNoteBean) dndao.create(dnb);
	    dndao.createMapping(dnb);
	}
    }

    /**
     * Find study subject id for each subject, and construct displaySubjectBean
     * @param displayArray
     * @param subjects
     * @param ds
     */
    public static void displaySubjects(ArrayList displayArray,
	    ArrayList subjects, StudySubjectDAO ssdao, StudyDAO stdao) {

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
		    protocolSubjectIds = protocolId + "-" + studySub.getLabel()
			    + ", ";
		}
	    }
	    DisplaySubjectBean dsb = new DisplaySubjectBean();
	    dsb.setSubject(subject);
	    dsb.setStudySubjectIds(protocolSubjectIds);
	    displayArray.add(dsb);

	}

    }
}