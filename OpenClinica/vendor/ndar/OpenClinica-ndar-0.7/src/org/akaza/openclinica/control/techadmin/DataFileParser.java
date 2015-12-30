/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.techadmin;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.SummaryDataEntryStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.subject.Person;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.admin.SpreadSheetTable;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.subject.LdapServer;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class DataFileParser {
  private POIFSFileSystem fs = null;

  private UserAccountBean ub = null;

  private CRFBean crf;

  private CRFVersionBean version;

  private StudyBean study;// may not be here if user wants to create study in
                          // parser

  private StudyEventDefinitionBean def;// may not be here if user wants to
                                        // create definition in parser

  private DataSource ds;

  protected final Logger logger = Logger.getLogger(getClass().getName());

  private ArrayList<String> queries = new ArrayList<String>();

  private ArrayList<String> errors = new ArrayList<String>();

  private ArrayList<String> sqlErrors = new ArrayList<String>();

  public DataFileParser(FileInputStream parseStream, UserAccountBean ub, CRFBean crf,
      CRFVersionBean version, DataSource ds, StudyBean study, StudyEventDefinitionBean def)
      throws IOException {

    this.fs = new POIFSFileSystem(parseStream);
    this.ub = ub;
    this.crf = crf;
    this.version = version;
    this.ds = ds;
    this.study = study;
    this.def = def;
  }

  /**
   * @return Returns the errors.
   */
  public ArrayList<String> getErrors() {
    return errors;
  }

  /**
   * @param errors
   *          The errors to set.
   */
  public void setErrors(ArrayList<String> errors) {
    this.errors = errors;
  }

  /**
   * @return Returns the sqlErrors.
   */
  public ArrayList<String> getSqlErrors() {
    return sqlErrors;
  }

  /**
   * @param sqlErrors
   *          The sqlErrors to set.
   */
  public void setSqlErrors(ArrayList<String> sqlErrors) {
    this.sqlErrors = sqlErrors;
  }

  public void parseData() throws IOException {
    StringBuffer buf = new StringBuffer();
    HSSFWorkbook wb = new HSSFWorkbook(fs);
    int numSheets = wb.getNumberOfSheets();

    for (int j = 0; j < numSheets; j++) {
      HSSFSheet sheet = wb.getSheetAt(j);// sheetIndex);
      String sheetName = wb.getSheetName(j);

      int numRows = sheet.getPhysicalNumberOfRows();
      int lastNumRow = sheet.getLastRowNum();
      System.out.println("PhysicalNumberOfRows" + sheet.getPhysicalNumberOfRows());
      // System.out.println("LastRowNum()" + sheet.getLastRowNum());

      HashMap htmlErrors = new HashMap();
      HashMap<String, SubjectBean> patients = new HashMap<String, SubjectBean>();
      HashMap<String, StudySubjectBean> studySubjects = new HashMap<String, StudySubjectBean>();
      HashMap<String, ArrayList> events = new HashMap<String, ArrayList>();
      int blankRowCount = 0;
      DAODigester digester = null;
      List<Object> params = new ArrayList<Object>();
      String sql = null;

      for (int k = 1; k < numRows; k++) {
        // System.out.println("hit row "+k);
        if (blankRowCount == 5) {
          System.out.println("hit end of the row ");
          break;
        }
        if (sheet.getRow(k) == null) {
          blankRowCount++;
          continue;
        }
        boolean isNewPatient = true;
        String patientNum = "";
        // ArrayList eventCrfs = new ArrayList();// patient's eventCRfs

        Person person = new Person();
        person.setStatus(Status.AVAILABLE);

        SubjectBean sub = new SubjectBean();
        sub.setStatus(Status.AVAILABLE);

        StudySubjectBean ssub = new StudySubjectBean();
        ssub.setStatus(Status.AVAILABLE);
        ssub.setEnrollmentDate(null);
        ssub.setStudyId(study.getId());

        HSSFCell cell = sheet.getRow(k).getCell((short) 0);
        String formId = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 1);
        String version = SpreadSheetTable.getValue(cell);

        if (StringUtil.isBlank(version)) {
          errors.add("The Version column was blank at row " + (k + 1) + ".");

        }

        cell = sheet.getRow(k).getCell((short) 2);
        patientNum = SpreadSheetTable.getValue(cell);
        // System.out.println("patientNum:" + patientNum);
        if (StringUtil.isBlank(patientNum)) {
          errors.add("The PatientNum column was blank at row " + (k + 1) + ", column 3.");
          // htmlErrors.put(j + "," + k + ",2",
          // SpreadSheetTable.ERROR_REQUIRED_FIELD);
        } else {
          if (!patients.containsKey(patientNum)) {
            // find a new patient
            patients.put(patientNum, sub);
            studySubjects.put(patientNum, ssub);
            // events.put(patientNum, eventCrfs);
          } else {
            isNewPatient = false;
            // find a new record for an existing patient
            sub = patients.get(patientNum);
            ssub = studySubjects.get(patientNum);
            System.out.println("sub.getUniqueIdentifier()" + sub.getUniqueIdentifier());
            // eventCrfs = events.get(patientNum);

          }

        }

        cell = sheet.getRow(k).getCell((short) 3);
        String projectNum = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 4);
        String projectName = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 5);
        String gender = SpreadSheetTable.getValue(cell);
        // System.out.println("gender:" + gender);
        if (!StringUtil.isBlank(gender)) {         
          if ("1".equalsIgnoreCase(gender)) {
            sub.setGender('m');
            person.setGender(Person.GENDER_MALE);
          } else if ("2".equalsIgnoreCase(gender)) {
            sub.setGender('f');
            person.setGender(Person.GENDER_FEMALE);
          } else {
            sub.setGender(' ');
            person.setGender(Person.GENDER_UNKNOWN);
          }
        }

        cell = sheet.getRow(k).getCell((short) 6);
        String individualId = SpreadSheetTable.getValue(cell);
        ssub.setLabel(individualId);

        cell = sheet.getRow(k).getCell((short) 7);
        String familyId = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 8);
        String RefId1 = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 9);
        String RefId2 = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 10);
        String RefId3 = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 11);
        String status = SpreadSheetTable.getValue(cell);

        StudyEventBean event = new StudyEventBean();
        event.setStatus(Status.AVAILABLE);
        event.setSubjectEventStatus(SubjectEventStatus.IN_PROGRESS);
        event.setSummaryDataEntryStatus(SummaryDataEntryStatus.DATA_ENTRY_STARTED);
        event.setStudyEventDefinitionId(def.getId());

        EventCRFBean eventCrf = new EventCRFBean();
        eventCrf.setStatus(Status.AVAILABLE);
        eventCrf.setCrf(this.crf);
        eventCrf.setCRFVersionId(this.version.getId());
        eventCrf.setCompletionStatusId(1);

        cell = sheet.getRow(k).getCell((short) 12);
        String interviewMonth = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 13);
        String interviewDay = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 14);
        String interviewYear = SpreadSheetTable.getValue(cell);

        Calendar cal = Calendar.getInstance();
        if (!StringUtil.isBlank(interviewMonth) && !StringUtil.isBlank(interviewDay)
            && !StringUtil.isBlank(interviewYear)) {

          int month = Integer.parseInt(interviewMonth);
          int day = Integer.parseInt(interviewDay);
          int year = Integer.parseInt(interviewYear);
          cal.set(year, month, day);
          Date interviewDate = cal.getTime();
          
          event.setDateStarted(interviewDate);
          cal.setTime(interviewDate);
          cal.add(Calendar.DATE,1);
          event.setDateEnded(cal.getTime());
          eventCrf.setDateInterviewed(interviewDate);

        } else {
          errors.add("The interviewDay/Month/Year columns are required at row " + (k + 1) + ".");
          event.setDateStarted(new Date());
          cal.setTime(event.getDateStarted());
          cal.add(Calendar.DATE,1);
          event.setDateEnded(cal.getTime());
          eventCrf.setDateInterviewed(new Date());
        }

        cell = sheet.getRow(k).getCell((short) 15);
        String age = SpreadSheetTable.getValue(cell);
        if (!StringUtil.isBlank(age)) {
          float ageNum = Float.parseFloat(age);
          int days = Math.round(ageNum * 365);
          
          cal.setTime(eventCrf.getDateInterviewed());
          
          cal.add(Calendar.DAY_OF_YEAR, -days);
          
          Date birthDate = cal.getTime();
          
          person.setDob(cal);
          sub.setDateOfBirth(birthDate);
        } else {
          errors.add("The age column was blank at row " + (k + 1) + ", column 16."); 
        }

        // create person and get GUID
        if (StringUtil.isBlank(sub.getUniqueIdentifier())) {
          LdapServer server = LdapServer.getInstance();
          if (!server.isConnected()) {
            errors
                .add("The connection with LDAP server was unable to be created, please contact your administrator.");
            return;
          }
          int ret = server.insertPerson(person);
          if (ret != LdapServer.SUCCESS) {
            errors
                .add("The PII information was not able to be created on your LDAP server, please contact your administrator.");
            return;
          }
          String uniqueIdentifier = person.getPersonId();
          sub.setUniqueIdentifier(uniqueIdentifier);
        }
        // create subject
        if (isNewPatient) {
          SubjectDAO sdao = new SubjectDAO(ds);
          digester = sdao.getDigester();
          params = new ArrayList<Object>();
          params.add(null);// father id
          params.add(null);// mother id
          params.add(new Integer(sub.getStatus().getId()));
          params.add(sub.getDateOfBirth());
          params.add(""+sub.getGender());         
          params.add(sub.getUniqueIdentifier());
          params.add(new Integer(ub.getId()));
          params.add(Boolean.FALSE);

          sql = EntityDAO.createStatement(digester.getQuery("create"), params);
          queries.add(sql);

          StudySubjectDAO ssdao = new StudySubjectDAO(ds);
          digester = ssdao.getDigester();
          params = new ArrayList<Object>();
          params.add(ssub.getLabel());
          params.add(new Integer(ssub.getStudyId()));// if user needs to create
                                                      // study?
          params.add(new Integer(ssub.getStatus().getId()));
          params.add(new Integer(ub.getId()));
          params.add(ssub.getEnrollmentDate());
          params.add(ssub.getSecondaryLabel());

          sql = EntityDAO.createStatement(digester.getQuery("createWithSubject"), params);
          //System.out.println("ssub:"+sql);
          queries.add(sql);

        }

        cell = sheet.getRow(k).getCell((short) 16);
        String interviewerNum = SpreadSheetTable.getValue(cell);
        if ("1".equalsIgnoreCase(interviewerNum)) {// from user feedback
          eventCrf.setInterviewerName("Stacy Shumway");
        } else {
          eventCrf.setInterviewerName("Audrey");
        }

        cell = sheet.getRow(k).getCell((short) 17);
        String InterviewerOrg = SpreadSheetTable.getValue(cell);

        cell = sheet.getRow(k).getCell((short) 18);
        String validated = SpreadSheetTable.getValue(cell);

        // creat event and event crf
        StudyEventDAO edao = new StudyEventDAO(ds);
        digester = edao.getDigester();
        params = new ArrayList<Object>();
        //if a user needs to create definition before importing data?
        params.add(new Integer(event.getStudyEventDefinitionId()));
        params.add(event.getLocation());
        params.add(new Integer(1));
        params.add(event.getDateStarted());// start date
        params.add(event.getDateEnded());// end date
        params.add(new Integer(ub.getId()));
        params.add(new Integer(event.getStatus().getId()));
        params.add(new Integer(event.getSubjectEventStatus().getId()));
        params.add(null);// StudyTemplateEventDefId

        params.add(new Integer(event.getSummaryDataEntryStatus().getId()));

        sql = EntityDAO.createStatement(digester.getQuery("createWithStudySubject"), params);// create
                                                                                              // event
                                                                                              // with
                                                                                              // PK
        queries.add(sql);

        // create event crf

        EventCRFDAO ecdao = new EventCRFDAO(ds);

        digester = ecdao.getDigester();
        params = new ArrayList<Object>();
        params.add(new Integer(eventCrf.getCRFVersionId()));
        params.add(eventCrf.getDateInterviewed());
        params.add(eventCrf.getInterviewerName());
        params.add(new Integer(eventCrf.getCompletionStatusId()));
        params.add(new Integer(eventCrf.getStatus().getId()));
        params.add(eventCrf.getAnnotations());
        params.add(new Integer(ub.getId()));
        params.add(eventCrf.getValidateString());
        params.add(eventCrf.getValidatorAnnotations());
        params.add(null);// how to set FORM_USAGE_COUNT_ID?

        sql = EntityDAO
            .createStatement(digester.getQuery("createWithEventAndStudySubject"), params);                                                                                       
                                                                        
                                                                
                                                                                         
        queries.add(sql);

        ItemDAO idao = new ItemDAO(ds);

        cell = sheet.getRow(k).getCell((short) 19);
        String a1 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupA_0001", a1);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 20);
        String a2 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupA_0002", a2);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 21);
        String a3 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupA_0003", a3);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 22);
        String a4 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupA_0004", a4);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 23);
        String a5 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupA_0005", a5);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 24);
        String a6 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupA_0006", a6);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 25);
        String a8 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupA_0007", a8);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 26);
        String a9 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupA_0008", a9);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 27);
        String b1 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0001", b1);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 28);
        String b2 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0002", b2);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 29);
        String b3 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0003", b3);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 30);
        String a7 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0004", a7);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 31);
        String b4 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0005", b4);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 32);
        String b5 = SpreadSheetTable.getValue(cell);

        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0006", b5);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 33);
        String b6 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0007", b6);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 34);
        String b7 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0008", b7);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 35);
        String b8 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0009", b8);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 36);
        String b9 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0010", b9);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 37);
        String b10 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupB_0011", b10);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 38);
        String c1 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupC_0001", c1);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 39);
        String c2 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupC_0002", c2);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 40);
        String d1 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupD_0001", d1);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 41);
        String d2 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupD_0002", d2);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 42);
        String d3 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupD_0003", d3);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 43);
        String d4 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupD_0004", d4);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 44);
        String e1 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupE_0001", e1);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 45);
        String e2 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupE_0002", e2);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 46);
        String e3 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CodingGroupE_0003", e3);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 47);
        String cSA2 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0001", cSA2);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 48);
        String cSA5 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0002", cSA5);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 49);
        String cSA6 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0003", cSA6);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 50);
        String cSA8 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0004", cSA8);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 51);
        String cSA9 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0005", cSA9);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 52);
        String cSSATotal = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0006", cSSATotal);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 53);
        String cSB1 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0008", cSB1);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 54);
        String cSB2 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0009", cSB2);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 55);
        String cSB5 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0010", cSB5);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 56);
        String cSB7 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0011", cSB7);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 57);
        String cSB8 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0012", cSB8);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 58);
        String cSB9 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0013", cSB9);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 59);
        String cSB10 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0014", cSB10);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 60);
        String cSSBTotal = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0015", cSSBTotal);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 61);
        String cSSABTotal = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0016", cSSABTotal);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 62);
        String cSC2 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0018", cSC2);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 63);
        String cSD1 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0020", cSD1);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 64);
        String cSD2 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0021", cSD2);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 65);
        String cSD4 = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0022", cSD4);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 66);
        String cSSDTotal = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0023", cSSDTotal);
        queries.add(sql);

        cell = sheet.getRow(k).getCell((short) 67);
        String cSadosgdiag = SpreadSheetTable.getValue(cell);
        sql = createItemSql(ds, idao, "ADOS_Mod002_CScoring_0024", cSadosgdiag);
        queries.add(sql);

      }

    }
  }

  /**
   * Constructs the sql of inserting a new item into DB
   * 
   * @param ds
   * @param idao
   * @param itemName
   * @param value
   * @return
   */
  public String createItemSql(javax.sql.DataSource ds, ItemDAO idao, String itemName, String value) {
    ItemBean item = (ItemBean) idao.findByName(itemName);

    ItemDataBean idb = new ItemDataBean();
    idb.setItemId(item.getId());
    idb.setOwner(ub);
    idb.setValue(value);
    idb.setStatus(Status.AVAILABLE);// not assume the data status is completed

    ItemDataDAO iddao = new ItemDataDAO(ds);
    DAODigester digester = iddao.getDigester();
    ArrayList<Object> params = new ArrayList<Object>();
    params.add(new Integer(idb.getItemId()));
    params.add(new Integer(idb.getStatus().getId()));
    params.add(idb.getValue());
    params.add(new Integer(idb.getOwner().getId()));

    String sql = EntityDAO.createStatement(digester.getQuery("createWithEventCRF"), params);

    return sql;

  }

  public void insertToDB() throws OpenClinicaException {

    Statement s = null;
    PreparedStatement ps = null;
    Connection con = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    ArrayList<String> errors = new ArrayList<String>();
    int count = 0;
    try {

      con = ds.getConnection();
      if (con.isClosed()) {
        String msg = "The connection to the database is not open.";
        errors.add(msg);
        throw new OpenClinicaException("DataFileTable, insertToDB, connection not open", "1");
      }

      con.setAutoCommit(false);
      logger.info("---start of query here---");

      int last = queries.size();
      for (int th = 0; th < last; th++) {
        String query = queries.get(th);
        count = th;
        s = con.createStatement();
        //logger.info("query" + th+ ":" + query);
        s.executeUpdate(query);
        s.close();
        errors.add("\nquery" + th+ ":" + query);
      }

      con.commit();
      logger.info("---end of query generation, all queries committed---");
      con.setAutoCommit(true);
      logger.info("---end of query generation, autocommit set to true---");

    } catch (SQLException se) {
      se.printStackTrace();
      try {
        con.rollback();
        logger.info("Error detected, rollback " + se.getMessage());
        String msg2 = "The following error was returned from the database: " + se.getMessage()
            + " using the following query: " + queries.get(count);
        errors.add(msg2);
        this.setSqlErrors(errors);
        con.setAutoCommit(true);
       
      } catch (SQLException seq) {
        seq.printStackTrace();
        logger.info("Error within rollback " + seq.getMessage());
        String msg2 = "The following error was returned from the database: " + seq.getMessage();
        errors.add(msg2);
        this.setSqlErrors(errors);
       
      }
    } catch (OpenClinicaException pe) {
      pe.printStackTrace();
      try {
        con.rollback();
        logger.info("OpenClinica Error detected, rollback " + pe.getMessage());
        String msg2 = "The following error was returned from the application: " + pe.getMessage();
        errors.add(msg2);
        this.setSqlErrors(errors);
        con.setAutoCommit(true);
        
      } catch (SQLException seq) {
        seq.printStackTrace();
        logger.info("OpenClinica Error within rollback " + seq.getMessage());
        String msg2 = "The following error was returned from the application: " + seq.getMessage();
        errors.add(msg2);
        this.setSqlErrors(errors);
      
        
      }

    } finally {
      try {
        if (con != null)
          con.close();
        if (s != null)
          s.close();
        if (ps != null)
          ps.close();
        if (rs != null)
          rs.close();
        if (rs2 != null)
          rs2.close();
      } catch (SQLException e) {
        e.printStackTrace();
        throw new OpenClinicaException(e.getMessage(), "1");
      }

    }
  }

}
