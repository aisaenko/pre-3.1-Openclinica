package org.akaza.openclinica.control.subjectmgmt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.PiiPrivilege;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.subject.Person;
import org.akaza.openclinica.bean.subject.SubjectEntryLabelBean;
import org.akaza.openclinica.bean.subject.SubjectLabelMapBean;
import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.subject.LdapServer;
import org.akaza.openclinica.dao.subject.SubjectEntryLabelDAO;
import org.akaza.openclinica.dao.subject.SubjectLabelMapDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

public class EditSubjectServlet extends SecureController {

    public static final String ARG_UNIQUE_IDENTIFIER = "uniqueIdentifier";//global Id

    public static final String INPUT_GIVENNAME = "givenname";

    public static final String INPUT_SURNAME = "surname";

    public static final String INPUT_ADDRESS = "streetAddress";

    public static final String INPUT_CITY = "localityName";

    public static final String INPUT_STATE = "stateOrProvinceName";

    public static final String INPUT_ZIP = "postalCode";

    public static final String INPUT_PHONE = "telephoneNumber";

    public static final String INPUT_EMAIL = "emailAddress";

    public static final String INPUT_DOB = "dob";

    public static final String INPUT_YOB = "yob"; //year of birth

    public static final String INPUT_GENDER = "gender";

    public static final String INPUT_FATHER = "fatherId";

    public static final String INPUT_MOTHER = "motherId";

    public static final String BEAN_FATHERS = "fathers";

    public static final String BEAN_MOTHERS = "mothers";

    public static final String INPUT_CONFIRM_BUTTON = "submit";

    public static final String PATH = "EditSubject";

    public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

    public static final String INPUT_SUBJECT_ENTRY_LABEL_ID = "subjectEntryLabelId";

    public static final String ARG_STEPNUM = "stepNum";

    // possible values of ARG_STEPNUM
    public static final int EDIT_STEP = 1;

    public static final int CONFIRM_STEP = 2;

    // possible values of INPUT_CONFIRM_BUTTON
    public static final String BUTTON_CONFIRM_VALUE = "Confirm";

    public static final String BUTTON_BACK_VALUE = "Back";

    protected SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public static String getLink(int guid) {
	return PATH + '?' + ARG_UNIQUE_IDENTIFIER + '=' + guid;
    }

    protected void mayProceed() throws InsufficientPermissionException {
	if (ub.getPiiPrivilege().equals(PiiPrivilege.EDIT)) {
	   return; 
	}
    addPageMessage("You don't have correct privilege to edit a subject, please contact your system admin. ");
    throw new InsufficientPermissionException(Page.MENU,
        "You may not perform subject management functions", "1");
    }

    protected void processRequest() throws Exception {
	FormProcessor fp = new FormProcessor(request);
	FormDiscrepancyNotes discNotes;

	String uniqueIdentifier = fp.getString(ARG_UNIQUE_IDENTIFIER);
	SubjectDAO sdao = new SubjectDAO(sm.getDataSource());

	int stepNum = fp.getInt(ARG_STEPNUM);
	//TODO: some checks?
	LdapServer server = LdapServer.getInstance();
	if (server == null) {
	    addPageMessage("The connection with LDAP server was unable to be created, please contact your administrator.");
	    forwardPage(Page.LIST_PII_SUBJECT_SERVLET);
	    return;
	}
	int countPersons = server.countPersons();
	List<Person> persons = server.query(LdapServer.QUERY_BY_UID,
		uniqueIdentifier);
	if (persons == null || persons.size() == 0) {
	    addPageMessage("There is unconsistent information between the LDAP server and the database, please contact your administrator to clean up the database or LDAP server.");
	    forwardPage(Page.LIST_PII_SUBJECT_SERVLET);
	    return;
	}
	Person person = persons.get(0);
	SubjectBean sb = sdao.findByUniqueIdentifier(uniqueIdentifier);
	if (sb == null) {
	    sb = new SubjectBean();
	    sb.setName(person.getPersonId());
	    sb.setActive(true);
	    sb.setDobCollected(true);
	    sb.setDateOfBirth(person.getDob() == null ? null : person.getDob()
		    .getTime());
	}

	request.setAttribute("person", person);
	request.setAttribute("subject", sb);

	resetPanel();
	panel.setStudyInfoShown(false);
	panel.setOrderedData(true);
	if (countPersons > 0) {
	    setToPanel("Subjects", new Integer(countPersons).toString());
	} else {
	    setToPanel("Subjects", "0");
	}
	if (!fp.isSubmitted()) {
	    setUpBeans();
	    loadPresetValuesFromBean(fp, sb, person);
	    discNotes = new FormDiscrepancyNotes();
	    fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
	    session.setAttribute(FORM_DISCREPANCY_NOTES_NAME, discNotes);
	    session.setAttribute("person", person);

	    SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm
		    .getDataSource());
	    request.setAttribute("entryLabels", seldao.findAll());
	    SubjectLabelMapDAO slmdao = new SubjectLabelMapDAO(sm
		    .getDataSource());
	    List<SubjectLabelMapBean> slbs = slmdao.findAllBySubjectId(sb
		    .getId());
	    if (slbs != null && slbs.size() > 0) {
		fp.addPresetValue("subjectEntryLabelId", slbs.get(0)
			.getSubjectEntryLabelId());
	    }
	    setPresetValues(fp.getPresetValues());
	    forwardPage(Page.EDIT_SUBJECT);
	} else if (stepNum == EDIT_STEP) {
	    discNotes = (FormDiscrepancyNotes) session
		    .getAttribute(FORM_DISCREPANCY_NOTES_NAME);
	    DiscrepancyValidator v = new DiscrepancyValidator(request,
		    discNotes);
	    //v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.NO_BLANKS);
	    //            v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.LENGTH_NUMERIC_COMPARISON,
	    //                NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

	    if (sb.isDobCollected()) {
		v.addValidation(INPUT_DOB, Validator.IS_A_DATE);
		v.alwaysExecuteLastValidation(INPUT_DOB);
		v.addValidation(INPUT_DOB, Validator.DATE_IN_PAST);
	    } else {
		v.addValidation(INPUT_YOB, Validator.IS_AN_INTEGER);
		v.alwaysExecuteLastValidation(INPUT_YOB);
		v.addValidation(INPUT_YOB, Validator.COMPARES_TO_STATIC_VALUE,
			NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO,
			1000);

		// get today's year
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		int currentYear = c.get(Calendar.YEAR);
		v.addValidation(INPUT_YOB, Validator.COMPARES_TO_STATIC_VALUE,
			NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
			currentYear);
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

	    HashMap errors = v.validate();

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

	    loadPresetValuesFromForm(fp);
	    if (errors.isEmpty()) {
		fp.addPresetValue(ARG_STEPNUM, CONFIRM_STEP);
		int entryLabelId = fp.getInt(INPUT_SUBJECT_ENTRY_LABEL_ID);
		SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm
			.getDataSource());
		SubjectEntryLabelBean selb = (SubjectEntryLabelBean) seldao
			.findByPK(entryLabelId);
		fp.addPresetValue("subjectEntryLabelName", selb.getName());
		setPresetValues(fp.getPresetValues());

		StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
		StudyDAO stdao = new StudyDAO(sm.getDataSource());

		if (fp.getInt(INPUT_MOTHER) != 0) {
		    SubjectBean mother = (SubjectBean) sdao.findByPK(fp
			    .getInt(INPUT_MOTHER));
		    if (mother != null) {
			ArrayList a = new ArrayList();
			ArrayList b = new ArrayList();
			b.add(mother);
			displaySubjects(a, b, ssdao, stdao);
			if (((DisplaySubjectBean) a.get(0))
				.getStudySubjectIds() != null
				&& ((DisplaySubjectBean) a.get(0))
					.getStudySubjectIds().length() > 0) {
			    fp.addPresetValue("motherName",
				    ((DisplaySubjectBean) a.get(0))
					    .getStudySubjectIds());
			} else {
			    fp.addPresetValue("motherName", mother
				    .getUniqueIdentifier());
			}
		    }
		}

		if (fp.getInt(INPUT_FATHER) != 0) {
		    SubjectBean father = (SubjectBean) sdao.findByPK(fp
			    .getInt(INPUT_FATHER));
		    if (father != null) {
			ArrayList a = new ArrayList();
			ArrayList b = new ArrayList();
			b.add(father);
			displaySubjects(a, b, ssdao, stdao);
			if (((DisplaySubjectBean) a.get(0))
				.getStudySubjectIds() != null
				&& ((DisplaySubjectBean) a.get(0))
					.getStudySubjectIds().length() > 0) {
			    fp.addPresetValue("fatherName",
				    ((DisplaySubjectBean) a.get(0))
					    .getStudySubjectIds());
			} else {
			    fp.addPresetValue("fatherName", father
				    .getUniqueIdentifier());
			}
		    }
		}
		forwardPage(Page.EDIT_SUBJECT_CONFIRM);

	    } else {
		loadPresetValuesFromForm(fp);
		fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
		setInputMessages(errors);

		setPresetValues(fp.getPresetValues());

		addPageMessage("There were some errors in your submission.  See below for details.");
		forwardPage(Page.EDIT_SUBJECT);
	    }
	} else if (stepNum == CONFIRM_STEP) {
	    String button = fp.getString(INPUT_CONFIRM_BUTTON);

	    if (button.equals(BUTTON_BACK_VALUE)) {
		setUpBeans();
		loadPresetValuesFromForm(fp);
		discNotes = new FormDiscrepancyNotes();
		fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
		session.setAttribute(FORM_DISCREPANCY_NOTES_NAME, discNotes);
		SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm
			.getDataSource());
		request.setAttribute("entryLabels", seldao.findAll());
		setPresetValues(fp.getPresetValues());
		forwardPage(Page.EDIT_SUBJECT);
	    } else if (button.equals(BUTTON_CONFIRM_VALUE)) {
		// no errors

		if (!StringUtil.isBlank(fp.getString(INPUT_GENDER))) {
		    sb.setGender(fp.getString(INPUT_GENDER).charAt(0));
		} else {
		    sb.setGender(' ');
		}

		if (sb.isDobCollected()) {
		    sb.setDateOfBirth(fp.getDate(INPUT_DOB));
		} else {
		    Calendar cal = Calendar.getInstance();
		    cal.clear();
		    cal.set(fp.getInt(INPUT_YOB), Calendar.JANUARY, 1);
		    sb.setDateOfBirth(cal.getTime());
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sb.getDateOfBirth());
		person.setPersonId(person.getPersonId());
		person.setDob(calendar);
		person.setGivenName(fp.getString(INPUT_GIVENNAME));
		person.setSurname(fp.getString(INPUT_SURNAME));
		person.setStreetAddress(fp.getString(INPUT_ADDRESS));
		person.setLocalityName(fp.getString(INPUT_CITY));
		person.setStateOrProvinceName(fp.getString(INPUT_STATE));
		person.setPostalCode(fp.getString(INPUT_ZIP));
		person.setTelephoneNumber(fp.getString(INPUT_PHONE));
		person.setEmailAddress(fp.getString(INPUT_EMAIL));

		Person oldPerson = (Person) session.getAttribute("person");
		if (!server.updatePerson(oldPerson, person)) {
		    throw new OpenClinicaException(
			    "Could not update subject in PII.",
			    uniqueIdentifier);
		}

		sb.setStatus(Status.AVAILABLE);

		System.out.println("updating subject...");
		sb.setFatherId(fp.getInt(INPUT_FATHER));
		sb.setMotherId(fp.getInt(INPUT_MOTHER));

		if (sb.getId() != 0) {
		    sb.setUpdater(ub);
		    sb = (SubjectBean) sdao.update(sb);
		} else {
		    sb.setOwner(ub);
		    sb = sdao.create(sb);
		}

		if (!sdao.isQuerySuccessful() || !sb.isActive()) {
		    throw new OpenClinicaException("Could not update subject.",
			    "3");
		}

		int subjectLabelId = fp.getInt(INPUT_SUBJECT_ENTRY_LABEL_ID);
		if (subjectLabelId != 0) {
		    SubjectLabelMapDAO slmdao = new SubjectLabelMapDAO(sm.getDataSource());
		    SubjectEntryLabelDAO seldao = new SubjectEntryLabelDAO(sm.getDataSource());

		    SubjectEntryLabelBean selb = (SubjectEntryLabelBean) seldao.findByPK(subjectLabelId);
		    List<SubjectLabelMapBean> slmbs = slmdao.findAllBySubjectId(sb.getId());

		    if (slmbs == null || slmbs.size() == 0) {
				SubjectLabelMapBean slmb = new SubjectLabelMapBean();
				slmb.setSubjectId(sb.getId());
				slmb.setSubjectEntryLabelId(subjectLabelId);
				slmdao.create(slmb);
	
				if (!slmdao.isQuerySuccessful()) {
				    throw new OpenClinicaException(
					    "Could not create subject label.", "3");
				}
		    } else {
				SubjectLabelMapBean slmb = slmbs.get(0);
				if (slmb.getSubjectEntryLabelId() != subjectLabelId) {
				    slmb.setSubjectEntryLabelId(subjectLabelId);
				    slmdao.update(slmb);
				}
	
				if (!slmdao.isQuerySuccessful()) {
				    throw new OpenClinicaException(
					    "Could not update subject label.", "3");
				}
		    }
		} else {
		    SubjectLabelMapDAO slmdao = new SubjectLabelMapDAO(sm.getDataSource());
		    List<SubjectLabelMapBean> slmbs = slmdao.findAllBySubjectId(sb.getId());

		    if (slmbs != null && slmbs.size() > 0) {
				SubjectLabelMapBean slmb = slmbs.get(0);
			    slmdao.delete(slmb);

				if (!slmdao.isQuerySuccessful()) {
				    throw new OpenClinicaException(
					    "Could not remove subject label.", "3");
				}
		    }
		}

		if (!sb.isActive()) {
		    throw new OpenClinicaException("Could not create subject.",
			    "3");
		}

		//save discrepancy notes into DB
		FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
			.getAttribute(FORM_DISCREPANCY_NOTES_NAME);
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm
			.getDataSource());

		String[] subjectFields = { INPUT_DOB, INPUT_YOB, INPUT_GENDER };
		for (int i = 0; i < subjectFields.length; i++) {
		    AddNewPiiSubjectServlet.saveFieldNotes(subjectFields[i],
			    fdn, dndao, sb.getId(), "subject");
		}

		request.removeAttribute(FormProcessor.FIELD_SUBMITTED);
		request.setAttribute(FormProcessor.FIELD_SUBMITTED, "0");

		addPageMessage("The subject was updated.");//AJF

		session.removeAttribute(FORM_DISCREPANCY_NOTES_NAME);
		forwardPage(Page.LIST_PII_SUBJECT_SERVLET);
	    } else {
		throw new InconsistentStateException(Page.ADMIN_SYSTEM,
			"An invalid submit button was clicked while editing a user.");
	    }
	}
    }

    private void loadPresetValuesFromBean(FormProcessor fp, SubjectBean sb,
	    Person person) {
	fp.addPresetValue(ARG_UNIQUE_IDENTIFIER, person.getPersonId());
	fp.addPresetValue(INPUT_GIVENNAME, person.getGivenName());
	fp.addPresetValue(INPUT_SURNAME, person.getSurname());
	fp.addPresetValue(INPUT_ADDRESS, person.getStreetAddress());
	fp.addPresetValue(INPUT_CITY, person.getLocalityName());
	fp.addPresetValue(INPUT_STATE, person.getStateOrProvinceName());
	fp.addPresetValue(INPUT_ZIP, person.getPostalCode());
	fp.addPresetValue(INPUT_PHONE, person.getTelephoneNumber());
	fp.addPresetValue(INPUT_EMAIL, person.getEmailAddress());
	if (sb != null && sb.getDateOfBirth() != null) {
	    if (sb.isDobCollected()) {
		fp.addPresetValue(INPUT_DOB, sdf.format(sb.getDateOfBirth()));
	    } else {
		Calendar cal = Calendar.getInstance();
		cal.setTime(sb.getDateOfBirth());
		cal.getTime(); // needed since calendar sometimes doesn't update until a getTime is called
		fp.addPresetValue(INPUT_YOB,
			new Integer(cal.get(Calendar.YEAR)));
	    }
	    fp.addPresetValue(INPUT_GENDER, sb.getGender());
	    fp.addPresetValue(INPUT_FATHER, sb.getFatherId());
	    fp.addPresetValue(INPUT_MOTHER, sb.getMotherId());
	    fp
		    .addPresetValue(INPUT_GENDER, (sb.getGender() == 'm') ? "m"
			    : "f");
	}
	if (person.getSubjectEntryLabelBean() != null) {
	    fp.addPresetValue(INPUT_SUBJECT_ENTRY_LABEL_ID, person
		    .getSubjectEntryLabelBean().getId());
	}
    }

    private void loadPresetValuesFromForm(FormProcessor fp) {
	fp.clearPresetValues();

	String textFields[] = { ARG_UNIQUE_IDENTIFIER, INPUT_GIVENNAME,
		INPUT_SURNAME, INPUT_ADDRESS, INPUT_CITY, INPUT_STATE,
		INPUT_ZIP, INPUT_PHONE, INPUT_EMAIL, INPUT_DOB, INPUT_YOB,
		INPUT_GENDER, INPUT_FATHER, INPUT_MOTHER,
		INPUT_SUBJECT_ENTRY_LABEL_ID };
	fp.setCurrentStringValuesAsPreset(textFields);
    }

    protected void setUpBeans() throws Exception {
	//      addEntityList(BEAN_GROUPS, sgdao.findAllByStudy(currentStudy),
	//              "A group must be available in order to add new subjects to this study;
	// however, there are no groups in this Study. Please contact your Study
	// Director.",
	//              Page.SUBMIT_DATA);

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

    private ArrayList getUserTypes() {

	ArrayList types = UserType.toArrayList();
	types.remove(UserType.INVALID);
	if (!ub.isTechAdmin()) {
	    types.remove(UserType.TECHADMIN);
	}
	return types;
    }

    protected String getAdminServlet() {
	return SecureController.ADMIN_SERVLET_CODE;
    }
    
    public static String calendarToString(Calendar c, String pattern){
	if(c == null || pattern == null || pattern.length() == 0){
	    return null;
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat(pattern);
	return sdf.format(c.getTime());
    }
    
    public static Calendar stringToCalendar(String time, String pattern) throws ParseException {
	if(time == null || time.length() == 0 || pattern == null || pattern.length() == 0){
	    return null;
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat(pattern);
	Calendar c = Calendar.getInstance();
	c.setTime(sdf.parse(time));
	return c;
    }
}