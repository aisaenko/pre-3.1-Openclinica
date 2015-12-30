/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.FilterBean;
import org.akaza.openclinica.bean.extract.FilterRow;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.extract.FilterDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Creates a dataset by building a query based on study events, CRFs and items
 * 
 * @author jxu
 * @author thickerson 
 *  
 */
public class CreateDatasetServlet extends SecureController {
  public static final String BEAN_YEARS = "years";

  public static final String BEAN_MONTHS = "months";

  public static final String EVENTS_FOR_CREATE_DATASET = "eventsForCreateDataset";

  public static final String SAVE_BUTTON = "save";

  public static final String SAVE_CONTINUE_BUTTON = "saveContinue";

  public static final String DOB = "dob";

  public static final String GENDER = "gender";

  public static final String EVENT_LOCATION = "location";

  public static final String EVENT_START = "start";

  public static final String EVENT_END = "end";

  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)
        || currentRole.getRole().equals(Role.INVESTIGATOR)) {
      return;
    }

    addPageMessage("You don't have the correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU,
        "not allowed to access extract data servlet", "1");

  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    String action = fp.getString("action");
    if (StringUtil.isBlank(action)) {
      //step 1 -- instructions, and continue button
      session.setAttribute("newDataset", new DatasetBean());
      forwardPage(Page.CREATE_DATASET_1);

    } else {
      StudyBean studyWithEventDefs = currentStudy;
      if (currentStudy.getParentStudyId() > 0) {
        studyWithEventDefs = new StudyBean();
        studyWithEventDefs.setId(currentStudy.getParentStudyId());
      }

      if ("begin".equalsIgnoreCase(action)) {
        // step 2 -- select study events/crfs

        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        StudyBean studyWithEventDefinitions = currentStudy;
        if (currentStudy.getParentStudyId() > 0) {
          studyWithEventDefinitions = new StudyBean();
          studyWithEventDefinitions.setId(currentStudy.getParentStudyId());

        }
        ArrayList seds = seddao.findAllActiveByStudy(studyWithEventDefinitions);

        CRFDAO crfdao = new CRFDAO(sm.getDataSource());
        HashMap events = new LinkedHashMap();
        for (int i = 0; i < seds.size(); i++) {
          StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
          ArrayList crfs = (ArrayList) crfdao.findAllActiveByDefinition(sed);
          if (!crfs.isEmpty()) {
            events.put(sed, crfs);
          }
        }
        //System.out.println("how many events =" + events.keySet().size());
        //determine if events are empty or not, redirect to view datasets
        if (events.isEmpty()) {
          addPageMessage("You do not have any Study Event Definitions"
              + " assigned to your Study yet.  Please ask" + " your study director to add Study "
              + "Events so that you may continue.");
          forwardPage(Page.CREATE_DATASET_1);
        } else {
          request.setAttribute("eventlist", events);

          session.setAttribute(EVENTS_FOR_CREATE_DATASET, events);
          session.setAttribute("newDataset", new DatasetBean());
          session.setAttribute("allItems", new ArrayList());
          session.setAttribute("crf", new CRFBean());
          forwardPage(Page.CREATE_DATASET_2);
        }

      } else if ("beginsubmit".equalsIgnoreCase(action)) {
        String saveItems = fp.getString(SAVE_BUTTON);
        String saveContinue = fp.getString(SAVE_CONTINUE_BUTTON);

        DatasetBean db = (DatasetBean) session.getAttribute("newDataset");
        if (db == null) {
          db = new DatasetBean();
        }
        extractIdsFromForm(db);
        session.setAttribute("newDataset", db);
        //session.setAttribute("itemSelectedNum",db.getItemIds().size() +"");

        if (!StringUtil.isBlank(saveItems)) {
          request.setAttribute("eventlist", session.getAttribute(EVENTS_FOR_CREATE_DATASET));
          String summary = "You have selected " + db.getItemIds().size() + " item(s) so far.<br>";
          summary += genAttMsg(db);
          addPageMessage(summary);

          int crfId = fp.getInt("crfId");
          if (crfId > 0) {
            // user choose a crf and select items
            forwardPage(Page.CREATE_DATASET_2);
          } else {
            //form submitted from "view selected item ' or attribute page, so
            //forward back to "view selected item " page
            forwardPage(Page.CREATE_DATASET_VIEW_SELECTED);
          }
        } else {
          if (db.getItemIds().size() == 0) {
            request.setAttribute("eventlist", session.getAttribute(EVENTS_FOR_CREATE_DATASET));
            addPageMessage("You should at least select one item to create a dataset.");
            forwardPage(Page.CREATE_DATASET_2);
          } else {

            String summary = "You have selected " + db.getItemIds().size()
                + " item(s) totally for this dataset.<br>";

            summary += genAttMsg(db);
            addPageMessage(summary);

            fp.addPresetValue("firstmonth", 0);//0 means using default month
            fp.addPresetValue("firstyear", 1900);
            fp.addPresetValue("lastmonth", 0);
            fp.addPresetValue("lastyear", 2100);
            setPresetValues(fp.getPresetValues());
            logger.warning("found preset values while setting date: "
                + fp.getPresetValues().toString());
            request.setAttribute(BEAN_MONTHS, getMonths());
            request.setAttribute(BEAN_YEARS, getYears());

            forwardPage(Page.CREATE_DATASET_3);
          }
        }

      } else if ("scopesubmit".equalsIgnoreCase(action)) {
        ArrayList months = getMonths();
        ArrayList years = getYears();

        int firstMonth = fp.getInt("firstmonth");
        int firstYear = fp.getInt("firstyear");
        int lastMonth = fp.getInt("lastmonth");
        int lastYear = fp.getInt("lastyear");
        if (fp.getInt("firstmonth") == 0) {
          firstMonth = 1;//default value
        }
        if (fp.getInt("lastmonth") == 0) {
          lastMonth = 12;//default value
        }

        errors = new HashMap();
        if (fp.getInt("firstmonth") > 0 && firstYear == 1900) {
          Validator.addError(errors, "firstmonth",
              "If you specify a month, you must also specify the year.");

        }
        if (fp.getInt("lastmonth") > 0 && lastYear == 2100) {
          Validator.addError(errors, "lastmonth",
              "If you specify a month, you must also specify the year.");

        }
        Date dateStart = getFirstDayOfMonth(firstYear, firstMonth);
        Date dateEnd = getLastDayOfMonth(lastYear, lastMonth);

        if (dateEnd.compareTo(dateStart) < 0) {
          Validator.addError(errors, "firstmonth",
              "The 'from' date should come before the 'to' date.");
        }

        if (!errors.isEmpty()) {
          String fieldNames[] = { "firstmonth", "firstyear", "lastmonth", "lastyear" };
          fp.setCurrentIntValuesAsPreset(fieldNames);

          setInputMessages(errors);
          addPageMessage("There were some errors in your submission.  See below for details.");
          setPresetValues(fp.getPresetValues());

          request.setAttribute(BEAN_MONTHS, getMonths());
          request.setAttribute(BEAN_YEARS, getYears());

          forwardPage(Page.CREATE_DATASET_3);
        } else {
          DatasetBean dsb = (DatasetBean) session.getAttribute("newDataset");

          dsb.setDateStart(dateStart);
          dsb.setDateEnd(dateEnd);
          session.setAttribute("newDataset", dsb);

          if (fp.getString("submit").equals("Continue to Apply Filter")) {
            //FilterDAO fdao = new FilterDAO(sm.getDataSource());
            //Collection filters = fdao.findAll();
            //TODO make findAllByProject
            //request.setAttribute("filters",filters);
            EntityBeanTable table = getFilterTable();
            session.setAttribute("partOfCreateDataset", new Integer(1));
            //to be used in createFiltersThree servlet, tbh
            request.setAttribute("table", table);
            forwardPage(Page.APPLY_FILTER);
          } else {
            request.setAttribute("statuses", Status.toArrayList());
            forwardPage(Page.CREATE_DATASET_4);
          }
        }
      } else if ("specifysubmit".equalsIgnoreCase(action)) {   	  
    	  String mdvOID = fp.getString("mdvOID");
          session.setAttribute("mdvOID", mdvOID);
          String mdvName = (String)fp.getString("mdvName");
          session.setAttribute("mdvName", mdvName);
          session.setAttribute("mdvPrevStudy", fp.getString("mdvPrevStudy"));
          String mdvPrevOID = fp.getString("mdvPrevOID");
          session.setAttribute("mdvPrevOID", mdvPrevOID);
          logger.info("in createDatasetseverlet, mdvOID=" + session.getAttribute("mdvOID"));
    	  
        Validator v = new Validator(request);

        v.addValidation("dsName", Validator.NO_BLANKS);
        v.addValidation("dsDesc", Validator.NO_BLANKS);
        v.addValidation("dsStatus", Validator.IS_VALID_TERM, TermType.STATUS);

        v.addValidation("dsName", Validator.LENGTH_NUMERIC_COMPARISON,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
        v.addValidation("dsDesc", Validator.LENGTH_NUMERIC_COMPARISON,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);        

        errors = v.validate();
        
        if (!StringUtil.isBlank(fp.getString("dsName"))) {
          //logger.info("dsName" + fp.getString("dsName"));
          DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
          DatasetBean dsBean = (DatasetBean)dsdao.findByNameAndStudy(fp.getString("dsName").trim(),currentStudy);
          if (dsBean.getId()>0) {
            Validator.addError(errors, "dsName", "This Dataset name has been used by another Dataset, please choose a unique name.");
          }
        }

        if (!errors.isEmpty()) {
          String fieldNames[] = { "dsName", "dsDesc" };
          fp.setCurrentStringValuesAsPreset(fieldNames);
          fp.addPresetValue("dsStatusId", fp.getInt("dsStatus"));

          addPageMessage("There were some errors in your submission.  See below for details.");
          setInputMessages(errors);
          setPresetValues(fp.getPresetValues());

          request.setAttribute("statuses", Status.toArrayList());
          forwardPage(Page.CREATE_DATASET_4);
        } else {
          DatasetBean dsb = (DatasetBean) session.getAttribute("newDataset");
          dsb.setSQLStatement(dsb.generateQuery());
          String dbName = SQLInitServlet.getField("dataBase");
          if ("oracle".equals(dbName)) {
            dsb.setSQLStatement(dsb.generateOracleQuery());
          }
          //TODO set up oracle syntax for the query, grab the database
          //from the session manager and feed it to the dataset bean
          //possibly done, tbh 1/4/2005

          //TODO look for the filter here, re-create the sql statement
          //and put it in here
          //possibly done need to test, tbh 1/7/2005
          FilterBean fb = (FilterBean) session.getAttribute("newFilter");
          if (fb != null) {
            //FilterDAO fDAO = new FilterDAO(sm.getDataSource());
            dsb.setSQLStatement(dsb.getSQLStatement() + " " + fb.getSQLStatement());
          }
          dsb.setName(fp.getString("dsName"));
          dsb.setDescription(fp.getString("dsDesc"));
          dsb.setStatus(Status.get(fp.getInt("dsStatus")));
          session.removeAttribute("partOfCreateDataset");
          //done to remove the set up of going to get the filter, tbh
          //set up dataset here, grab primary key???!!!???
          //added by jxu
          request.setAttribute("defaultStart", sdf.parse("01/01/1900"));
          request.setAttribute("defaultEnd", getLastDayOfMonth(2100, 12));

          session.setAttribute("newDataset", dsb);
          forwardPage(Page.CONFIRM_DATASET);
        }

      } else if ("confirmall".equalsIgnoreCase(action)) {
        String submit = fp.getString("btnSubmit");
        logger.info("reached confirm all");
        if (!"Confirm and Save".equalsIgnoreCase(submit)) {
          //we're going back, so we should not destroy the
          //data we've created, tbh
          //session.removeAttribute("newDataset");
          //session.removeAttribute("newFilter");
          forwardPage(Page.CREATE_DATASET_4);
        } else {
          DatasetDAO ddao = new DatasetDAO(sm.getDataSource());
          DatasetBean dsb = (DatasetBean) session.getAttribute("newDataset");
          dsb.setStudyId(this.currentStudy.getId());

          dsb.setOwner(ub);
          //dsb.setOwnerId(ub.getId());
          if (dsb.getId() == 0) {
            //if the bean hasn't been created already that is...
            logger.info("*** about to create the dataset bean");
            dsb = (DatasetBean) ddao.create(dsb);
            logger.info("created dataset bean: " + dsb.getId() + ", name: " + dsb.getName());
          }
          if (!dsb.isActive()) {
            addPageMessage("There was a problem creating the dataset.  Please try again.");
            forwardPage(Page.EXTRACT_DATASETS_MAIN);
          }

          logger.info("setting data set id here");
          //may be easier to just set the dataset bean
          //back into the session?
          
          request.setAttribute("dataset", dsb);

          forwardPage(Page.EXPORT_DATASETS);
        }
      } else {
        //refine this bit to catch errors, hopefully
        addPageMessage("Creating a new Dataset is cancelled.");
        forwardPage(Page.CREATE_DATASET_1);
      }
    }
  }

  public void extractIdsFromForm(DatasetBean db) {
    FormProcessor fp = new FormProcessor(request);
    int crfId = fp.getInt("crfId");
    int defId = fp.getInt("defId");
    boolean eventAttr = fp.getBoolean("eventAttr");
    boolean subAttr = fp.getBoolean("subAttr");

    ArrayList allItems = (ArrayList) session.getAttribute("allItems");
    if (!db.getEventIds().contains(new Integer(defId))) {
      db.getEventIds().add(new Integer(defId));
    }
    
    StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
    String defName ="";
    if (defId>0) {
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean)seddao.findByPK(defId);
      defName = sed.getName();
    }
    
    if (crfId == -1) {
      //submit from 'view selected item' page
      allItems = (ArrayList) session.getAttribute("allSelectedItems");
      db.getItemIds().clear();
      db.getItemMap().clear();
      db.getItemDefCrf().clear();
    } else if (crfId > 0) {//user chose a CRF and submitted items
      //remove all old items first, since user submitted again
      //so user can de-select items
      for (int i = 0; i < allItems.size(); i++) {
        ItemBean item = (ItemBean) allItems.get(i);
        item.setSelected(false);
        if (db.getItemMap().containsKey(defId + "_" + item.getId())) {
          db.getItemIds().remove(new Integer(item.getId()));
          db.getItemMap().remove(defId + "_" + item.getId());
          db.getItemDefCrf().remove(item);
        }
      }
    }
    
    if (crfId != 0 && allItems != null) {
      CRFDAO cdao = new CRFDAO(sm.getDataSource());
      CRFBean crf = (CRFBean)cdao.findByPK(crfId);
      
      ArrayList newSelectItems = new ArrayList();
      for (int i = 0; i < allItems.size(); i++) {
        String checked = fp.getString("itemSelected" + i);
        String itemCrfName = fp.getString("itemCrfName" + i);
        String itemDefName = fp.getString("itemDefName" + i);
        ItemBean selectedItem = (ItemBean) allItems.get(i);

        if (!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim())) {
          selectedItem.setSelected(true); 
          if (!"".equals(crf.getName())) {
            selectedItem.setCrfName(crf.getName());
          } else {
            selectedItem.setCrfName(itemCrfName);
          }
          if (!"".equals(defName)){
            selectedItem.setDefName(defName);
          } else {
            selectedItem.setDefName(itemDefName);
          }
          newSelectItems.add(selectedItem);         
          
          if (!db.getItemMap().containsKey(defId + "_" + selectedItem.getId())) {
            //System.out.println("one item selected");
            db.getItemIds().add(new Integer(selectedItem.getId()));
            db.getItemMap().put(defId + "_" + selectedItem.getId(), selectedItem);
            db.getItemDefCrf().add(selectedItem);            
          }
          
        }
      }
     
      if (crfId == -1)  {//from view selected page
        getSubAttr(fp, db);
        getEventAttr(fp, db);
      }
     
      session.setAttribute("allSelectedItems", newSelectItems);     
    }

    if (crfId == 0) {//event or subject attribute page
      if (subAttr) {
        getSubAttr(fp, db);
      } else if (eventAttr) {
        getEventAttr(fp, db);
      }
    }

  }

  /**
   * @param year
   *          The year part of the date.
   * @param month
   *          The month part of the date.
   * @return A Date object corresponding to the first day of the specified year
   *         and month.
   */
  private Date getFirstDayOfMonth(int year, int month) {
    // scale month down to 0 .. 11 range
    month--;

    Calendar c = Calendar.getInstance();
    c.clear();
    c.set(year, month, 1, 0, 0, 0);
    return new Date(c.getTimeInMillis());
  }

  /**
   * @param year
   *          The year part of the date.
   * @param month
   *          The month part of the date.
   * @return A Date object corresponding to the last day of the specified year
   *         and month.
   */
  private Date getLastDayOfMonth(int year, int month) {
    // scale month down to 0 .. 11 range
    month--;

    // the idea is to get the first day of the next month
    // then subtract a day and let the Calendar class do the real work

    Calendar c = Calendar.getInstance();

    c.clear();

    // get the first day of the current month and year, e.g.:
    // a. Apr. 1 2001
    // b. Feb. 1 2000
    // c. Feb. 1 2001
    // d. Dec. 1 1999
    c.set(year, month, 1, 23, 59, 59);

    // get the first day of the next month, e.g.:
    // a. May 1 2001
    // b. Mar. 1 2000
    // c. Mar. 1 2001
    // d. Jan. 1 2000 (note roll-over to next year)
    c.add(Calendar.MONTH, 1);

    // get the immediately preceding date, e.g.:
    // a. Apr. 30 2001 (note that Calendar knows April is only 30 days)
    // b. Feb. 29 2000 (note sensitivity to the leap year)
    // c. Feb. 28 2001
    // d. Dec. 31 1999 (note roll-back to the previous year)
    c.add(Calendar.DATE, -1);

    return new Date(c.getTimeInMillis());
  }

  private ArrayList getMonths() {
    ArrayList answer = new ArrayList();

    answer.add("January");
    answer.add("February");
    answer.add("March");
    answer.add("April");
    answer.add("May");
    answer.add("June");
    answer.add("July");
    answer.add("August");
    answer.add("September");
    answer.add("October");
    answer.add("November");
    answer.add("December");

    return answer;
  }

  private ArrayList getYears() {
    ArrayList answer = new ArrayList();

    Calendar currTime = Calendar.getInstance();
    int currYear = currTime.get(Calendar.YEAR);

    for (int i = 1980; i <= currYear; i++) {
      answer.add(String.valueOf(i));
    }

    return answer;
  }

  private EntityBeanTable getFilterTable() {
    FormProcessor fp = new FormProcessor(request);
    FilterDAO fdao = new FilterDAO(sm.getDataSource());
    EntityBeanTable table = fp.getEntityBeanTable();

    ArrayList filters = (ArrayList) fdao.findAll();

    ArrayList filterRows = FilterRow.generateRowsFromBeans(filters);

    String[] columns = { "Filter Name", "Description", "Created By", "Created Date", "Status",
        "Actions" };
    table.setColumns(new ArrayList(Arrays.asList(columns)));
    table.hideColumnLink(5);
    table.setQuery("ApplyFilter", new HashMap());
    table.setRows(filterRows);
    table.computeDisplay();
    return table;
  }

  private String genAttMsg(DatasetBean db) {
    String summary = "";
    if (db.isShowEventEnd() || db.isShowEventLocation() || db.isShowEventStart()) {
      summary = summary + "You choose to show Event Attributes: ";
      if (db.isShowEventLocation()) {
        summary = summary + "Location, ";
      }
      if (db.isShowEventStart()) {
        summary = summary + "Start Date, ";
      }
      if (db.isShowEventEnd()) {
        summary = summary + "End Date ";
      }
    }
    if (db.isShowSubjectDob() || db.isShowSubjectGender()) {
      summary = summary + "You choose to show Subject Attributes:";
      if (db.isShowSubjectDob()) {
        summary = summary + " Date/Year of Birth, ";
      }
      if (db.isShowSubjectGender()) {
        summary = summary + "Gender ";
      }
    }
    return summary;
  }

  private void getSubAttr(FormProcessor fp, DatasetBean db) {
    String dob = fp.getString(DOB);
    if (!StringUtil.isBlank(dob) && "yes".equalsIgnoreCase(dob.trim())) {
      db.setShowSubjectDob(true);
    } else if (db.isShowSubjectDob()) {
      db.setShowSubjectDob(false);
    }

    String gender = fp.getString(GENDER);
    if (!StringUtil.isBlank(gender) && "yes".equalsIgnoreCase(gender.trim())) {
      db.setShowSubjectGender(true);
    } else if (db.isShowSubjectGender()) {
      db.setShowSubjectGender(false);
    }
  }
  
  private void getEventAttr(FormProcessor fp, DatasetBean db){
    String location = fp.getString(EVENT_LOCATION);
    if (!StringUtil.isBlank(location) && "yes".equalsIgnoreCase(location.trim())) {
      db.setShowEventLocation(true);
    } else if (db.isShowEventLocation()) {
      //user unchecked event location on page
      db.setShowEventLocation(false);

    }
    String start = fp.getString(EVENT_START);
    if (!StringUtil.isBlank(start) && "yes".equalsIgnoreCase(start.trim())) {
      db.setShowEventStart(true);
    } else if (db.isShowEventStart()) {
      db.setShowEventStart(false);
    }

    String end = fp.getString(EVENT_END);
    if (!StringUtil.isBlank(end) && "yes".equalsIgnoreCase(end.trim())) {
      db.setShowEventEnd(true);
    } else if (db.isShowEventEnd()) {
      db.setShowEventEnd(false);
    }
  }
}