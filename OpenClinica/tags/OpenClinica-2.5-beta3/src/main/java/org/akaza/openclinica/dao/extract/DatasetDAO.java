/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.extract;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.submit.ItemDAO;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.sql.DataSource;

/**
 * The data access object for datasets; also generates datasets based on their
 * query and criteria set; also generates the extract bean, which holds dataset
 * information.
 * 
 * @author thickerson
 * 
 * 
 */
public class DatasetDAO extends AuditableEntityDAO {

    // private DataSource ds;
    // private DAODigester digester;

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_DATASET;
    }

    protected void setQueryNames() {
        getCurrentPKName = "getCurrentPK";
    }

    /**
     * Creates a DatasetDAO object, for use in the application only.
     * 
     * @param ds
     */
    public DatasetDAO(DataSource ds) {
        super(ds);
        this.setQueryNames();
    }

    /**
     * Creates a DatasetDAO object suitable for testing purposes only.
     * 
     * @param ds
     * @param digester
     */
    public DatasetDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
        this.setQueryNames();
    }

    @Override
    public void setTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.INT);
        this.setTypeExpected(3, TypeNames.INT);
        this.setTypeExpected(4, TypeNames.STRING);// name
        this.setTypeExpected(5, TypeNames.STRING);// desc
        this.setTypeExpected(6, TypeNames.STRING);// sql
        this.setTypeExpected(7, TypeNames.INT);// num runs
        this.setTypeExpected(8, TypeNames.TIMESTAMP);// date start. YW,
        // 08-21-2007, datatype
        // changed to Timestamp
        this.setTypeExpected(9, TypeNames.TIMESTAMP);// date end
        this.setTypeExpected(10, TypeNames.DATE);// created
        this.setTypeExpected(11, TypeNames.DATE);// updated
        this.setTypeExpected(12, TypeNames.DATE);// last run
        this.setTypeExpected(13, TypeNames.INT);// owner id
        this.setTypeExpected(14, TypeNames.INT);// approver id
        this.setTypeExpected(15, TypeNames.INT);// update id
        this.setTypeExpected(16, TypeNames.BOOL);// show_event_location
        this.setTypeExpected(17, TypeNames.BOOL);// show_event_start
        this.setTypeExpected(18, TypeNames.BOOL);// show_event_end
        this.setTypeExpected(19, TypeNames.BOOL);// show_subject_dob
        this.setTypeExpected(20, TypeNames.BOOL);// show_subject_gender
        this.setTypeExpected(21, TypeNames.BOOL);// show_event_status
        this.setTypeExpected(22, TypeNames.BOOL);// show_subject_status
        this.setTypeExpected(23, TypeNames.BOOL);// show_subject_unique_id
        this.setTypeExpected(24, TypeNames.BOOL);// show_subject_age_at_event
        this.setTypeExpected(25, TypeNames.BOOL);// show_crf_status
        this.setTypeExpected(26, TypeNames.BOOL);// show_crf_version
        this.setTypeExpected(27, TypeNames.BOOL);// show_crf_int_name
        this.setTypeExpected(28, TypeNames.BOOL);// show_crf_int_date
        this.setTypeExpected(29, TypeNames.BOOL);// show_group_info
        this.setTypeExpected(30, TypeNames.BOOL);// show_disc_info
        this.setTypeExpected(31, TypeNames.STRING);// odm_metadataversion_name
        this.setTypeExpected(32, TypeNames.STRING);// odm_metadataversion_oid
        this.setTypeExpected(33, TypeNames.STRING);// odm_prior_study_oid
        this.setTypeExpected(34, TypeNames.STRING);// odm_prior_metadataversion_oid
        this.setTypeExpected(35, TypeNames.BOOL);// show_secondary_id
    }

    public void setExtractTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// subj id
        this.setTypeExpected(2, TypeNames.STRING);// subj identifier
        this.setTypeExpected(3, TypeNames.INT);// study id
        this.setTypeExpected(4, TypeNames.STRING);// study ident
        this.setTypeExpected(5, TypeNames.INT);// event def crf id
        this.setTypeExpected(6, TypeNames.INT);// crf id
        this.setTypeExpected(7, TypeNames.STRING);// crf label
        this.setTypeExpected(8, TypeNames.STRING);// crf name
        this.setTypeExpected(9, TypeNames.INT);// version id
        this.setTypeExpected(10, TypeNames.STRING);// version label
        this.setTypeExpected(11, TypeNames.STRING);// version name
        this.setTypeExpected(12, TypeNames.INT);// study event id
        this.setTypeExpected(13, TypeNames.INT);// event crf id
        this.setTypeExpected(14, TypeNames.INT);// item data id
        this.setTypeExpected(15, TypeNames.STRING);// value
        // oops added three more here
        this.setTypeExpected(16, TypeNames.STRING);// sed.name
        this.setTypeExpected(17, TypeNames.BOOL);// repeating
        this.setTypeExpected(18, TypeNames.INT);// sample ordinal
        this.setTypeExpected(19, TypeNames.INT);// item id
        this.setTypeExpected(20, TypeNames.STRING);// item name
        this.setTypeExpected(21, TypeNames.STRING);// item desc
        this.setTypeExpected(22, TypeNames.STRING);// item units
        this.setTypeExpected(23, TypeNames.DATE);// date created for item
        // data
        this.setTypeExpected(24, TypeNames.INT);// study event definition id
        this.setTypeExpected(25, TypeNames.STRING);// option stings
        this.setTypeExpected(26, TypeNames.STRING);// option values
        this.setTypeExpected(27, TypeNames.INT);// response type id
        this.setTypeExpected(28, TypeNames.STRING);// gender
        this.setTypeExpected(29, TypeNames.DATE);// dob
        // added more columns below this line
        this.setTypeExpected(30, TypeNames.INT);// s.status_id AS
        // subject_status_id,
        this.setTypeExpected(31, TypeNames.STRING);// s.unique_identifier,
        this.setTypeExpected(32, TypeNames.BOOL);// s.dob_collected,
        this.setTypeExpected(33, TypeNames.INT);// ec.completion_status_id,
        this.setTypeExpected(34, TypeNames.DATE);// ec.date_created AS
        // event_crf_start_time,
        this.setTypeExpected(35, TypeNames.INT);// crfv.status_id AS
        // crf_version_status_id,
        this.setTypeExpected(36, TypeNames.STRING);// ec.interviewer_name,
        this.setTypeExpected(37, TypeNames.DATE);// ec.date_interviewed,
        this.setTypeExpected(38, TypeNames.DATE);// ec.date_completed AS
        // event_crf_date_completed,
        this.setTypeExpected(39, TypeNames.DATE);// ec.date_validate_completed
        // AS
        // event_crf_date_validate_completed,
        this.setTypeExpected(40, TypeNames.INT);// sgmap.study_group_id,
        this.setTypeExpected(41, TypeNames.INT);// sgmap.study_group_class_id
        // this.setTypeExpected(39, TypeNames.STRING);//ig.name AS
        // item_group_name,
        // this.setTypeExpected(40, TypeNames.STRING);//dn.description AS
        // discrepancy_note_description,
        // this.setTypeExpected(41, TypeNames.INT);//dn.resolution_status_id AS
        // discrepancy_resolution_status_id,
        // this.setTypeExpected(42, TypeNames.STRING);//dn.detailed_notes,
        // this.setTypeExpected(43, TypeNames.INT);//type id for dn
        // added more columns above this line
        this.setTypeExpected(42, TypeNames.STRING);// location
        this.setTypeExpected(43, TypeNames.TIMESTAMP);// date start. YW,
        // 08-21-2007, datatype
        // changed to Timestamp
        this.setTypeExpected(44, TypeNames.TIMESTAMP);// date end
        this.setTypeExpected(45, TypeNames.INT);// item data ordinal, added tbh
        this.setTypeExpected(46, TypeNames.STRING);// item group name, added
        this.setTypeExpected(47, TypeNames.STRING);// secondary label
        // tbh
        this.setTypeExpected(48, TypeNames.INT);// item_data_type_id
        this.setTypeExpected(49, TypeNames.STRING);// study_event_definition_oid
        this.setTypeExpected(50, TypeNames.STRING);// crf_version_oid
        this.setTypeExpected(51, TypeNames.STRING);// item_group_oid
        this.setTypeExpected(52, TypeNames.STRING);// item_oid
        this.setTypeExpected(53, TypeNames.STRING);// study_subject_oid
        this.setTypeExpected(54, TypeNames.INT);// sed_order
        this.setTypeExpected(55, TypeNames.INT);// crf_order
        this.setTypeExpected(56, TypeNames.INT);// item_order
    }

    public void setDefinitionCrfItemTypesExpected() {
        this.unsetTypeExpected();
        // copy from itemdao.setTypesExpected()
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.STRING);
        this.setTypeExpected(3, TypeNames.STRING);
        this.setTypeExpected(4, TypeNames.STRING);
        this.setTypeExpected(5, TypeNames.BOOL);// phi status
        this.setTypeExpected(6, TypeNames.INT);// data type id
        this.setTypeExpected(7, TypeNames.INT);// reference type id
        this.setTypeExpected(8, TypeNames.INT);// status id
        this.setTypeExpected(9, TypeNames.INT);// owner id
        this.setTypeExpected(10, TypeNames.DATE);// created
        this.setTypeExpected(11, TypeNames.DATE);// updated
        this.setTypeExpected(12, TypeNames.INT);// update id
        this.setTypeExpected(13, TypeNames.STRING);// oc_oid

        this.setTypeExpected(14, TypeNames.INT);// sed_id
        this.setTypeExpected(15, TypeNames.STRING);// sed_name
        this.setTypeExpected(16, TypeNames.INT);// crf_id
        this.setTypeExpected(17, TypeNames.STRING);// crf_name
    }

    public EntityBean update(EntityBean eb) {
        DatasetBean db = (DatasetBean) eb;
        HashMap variables = new HashMap();
        HashMap nullVars = new HashMap();
        variables.put(new Integer(1), new Integer(db.getStudyId()));
        variables.put(new Integer(2), new Integer(db.getStatus().getId()));
        variables.put(new Integer(3), db.getName());
        variables.put(new Integer(4), db.getDescription());
        variables.put(new Integer(5), db.getSQLStatement());
        variables.put(new Integer(6), db.getDateLastRun());
        variables.put(new Integer(7), new Integer(db.getNumRuns()));

        variables.put(new Integer(8), new Integer(db.getUpdaterId()));
        if (db.getApproverId() <= 0) {
            // nullVars.put(new Integer(9), null);
            // ABOVE IS WRONG; follow the example below:
            nullVars.put(new Integer(9), new Integer(Types.NUMERIC));
            variables.put(new Integer(9), null);
        } else {
            variables.put(new Integer(9), new Integer(db.getApproverId()));
        }

        variables.put(new Integer(10), db.getDateStart());
        variables.put(new Integer(11), db.getDateEnd());
        variables.put(new Integer(12), new Integer(db.getId()));
        this.execute(digester.getQuery("update"), variables, nullVars);
        return eb;
    }

    public EntityBean create(EntityBean eb) {
        /*
         * INSERT INTO DATASET (STUDY_ID, STATUS_ID, NAME, DESCRIPTION,
         * SQL_STATEMENT, OWNER_ID, DATE_CREATED, DATE_LAST_RUN, NUM_RUNS,
         * DATE_START, DATE_END,
         * SHOW_EVENT_LOCATION,SHOW_EVENT_START,SHOW_EVENT_END,
         * SHOW_SUBJECT_DOB,SHOW_SUBJECT_GENDER) VALUES
         * (?,?,?,?,?,?,NOW(),NOW(),?,NOW(),'2005-11-15', ?,?,?,?,?) ADDED THE
         * COLUMNS 7-2007, TBH ALTER TABLE dataset ADD COLUMN show_event_status
         * bool DEFAULT false; ALTER TABLE dataset ADD COLUMN
         * show_subject_status bool DEFAULT false; ALTER TABLE dataset ADD
         * COLUMN show_subject_unique_id bool DEFAULT false; ALTER TABLE dataset
         * ADD COLUMN show_subject_age_at_event bool DEFAULT false; ALTER TABLE
         * dataset ADD COLUMN show_crf_status bool DEFAULT false; ALTER TABLE
         * dataset ADD COLUMN show_crf_version bool DEFAULT false; ALTER TABLE
         * dataset ADD COLUMN show_crf_int_name bool DEFAULT false; ALTER TABLE
         * dataset ADD COLUMN show_crf_int_date bool DEFAULT false; ALTER TABLE
         * dataset ADD COLUMN show_group_info bool DEFAULT false; ALTER TABLE
         * dataset ADD COLUMN show_disc_info bool DEFAULT false; added table
         * mapping dataset id to study group classes id, tbh
         * 
         */
        DatasetBean db = (DatasetBean) eb;
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        HashMap nullVars = new HashMap();
        variables.put(new Integer(1), new Integer(db.getStudyId()));
        variables.put(new Integer(2), new Integer(db.getStatus().getId()));
        variables.put(new Integer(3), db.getName());
        variables.put(new Integer(4), db.getDescription());
        variables.put(new Integer(5), db.getSQLStatement());
        variables.put(new Integer(6), new Integer(db.getOwnerId()));
        variables.put(new Integer(7), new Integer(db.getNumRuns()));
        variables.put(new Integer(8), new Boolean(db.isShowEventLocation()));
        variables.put(new Integer(9), new Boolean(db.isShowEventStart()));
        variables.put(new Integer(10), new Boolean(db.isShowEventEnd()));
        variables.put(new Integer(11), new Boolean(db.isShowSubjectDob()));
        variables.put(new Integer(12), new Boolean(db.isShowSubjectGender()));
        variables.put(new Integer(13), new Boolean(db.isShowEventStatus()));
        variables.put(new Integer(14), new Boolean(db.isShowSubjectStatus()));
        variables.put(new Integer(15), new Boolean(db.isShowSubjectUniqueIdentifier()));
        variables.put(new Integer(16), new Boolean(db.isShowSubjectAgeAtEvent()));
        variables.put(new Integer(17), new Boolean(db.isShowCRFstatus()));
        variables.put(new Integer(18), new Boolean(db.isShowCRFversion()));
        variables.put(new Integer(19), new Boolean(db.isShowCRFinterviewerName()));
        variables.put(new Integer(20), new Boolean(db.isShowCRFinterviewerDate()));
        variables.put(new Integer(21), new Boolean(db.isShowSubjectGroupInformation()));
        // variables.put(new Integer(22), new
        // Boolean(db.isShowDiscrepancyInformation()));
        variables.put(new Integer(22), new Boolean(false));
        // currently not changing structure to allow for disc notes to be added
        // in the future
        variables.put(new Integer(23), db.getODMMetaDataVersionName());
        variables.put(new Integer(24), db.getODMMetaDataVersionOid());
        variables.put(new Integer(25), db.getODMPriorStudyOid());
        variables.put(new Integer(26), db.getODMPriorMetaDataVersionOid());
        variables.put(new Integer(27), db.isShowSubjectSecondaryId());

        this.executeWithPK(digester.getQuery("create"), variables, nullVars);

        // logger.warn("**************************************************");
        // logger.warn("just created dataset bean: "+
        // "show event status "+db.isShowEventStatus()+
        // "show subject age at event "+db.isShowSubjectAgeAtEvent()+
        // "show group info "+db.isShowGroupInformation()+
        // "show disc info "+db.isShowDiscrepancyInformation());
        // logger.warn("**************************************************");

        if (isQuerySuccessful()) {
            eb.setId(getLatestPK());
            if (db.isShowSubjectGroupInformation()) {
                // add additional information here
                for (int i = 0; i < db.getSubjectGroupIds().size(); i++) {
                    createGroupMap(eb.getId(), ((Integer) db.getSubjectGroupIds().get(i)).intValue(), nullVars);
                }
            }
        }
        return eb;
    }

    public Object getEntityFromHashMap(HashMap hm) {
        DatasetBean eb = new DatasetBean();
        this.setEntityAuditInformation(eb, hm);
        eb.setDescription((String) hm.get("description"));
        eb.setStudyId(((Integer) hm.get("study_id")).intValue());
        eb.setName((String) hm.get("name"));
        eb.setId(((Integer) hm.get("dataset_id")).intValue());
        eb.setSQLStatement((String) hm.get("sql_statement"));
        eb.setNumRuns(((Integer) hm.get("num_runs")).intValue());
        eb.setDateStart((Date) hm.get("date_start"));
        eb.setDateEnd((Date) hm.get("date_end"));
        eb.setApproverId(((Integer) hm.get("approver_id")).intValue());
        eb.setDateLastRun((Date) hm.get("date_last_run"));
        eb.setShowEventEnd(((Boolean) hm.get("show_event_end")).booleanValue());
        eb.setShowEventStart(((Boolean) hm.get("show_event_start")).booleanValue());
        eb.setShowEventLocation(((Boolean) hm.get("show_event_location")).booleanValue());
        eb.setShowSubjectDob(((Boolean) hm.get("show_subject_dob")).booleanValue());
        eb.setShowSubjectGender(((Boolean) hm.get("show_subject_gender")).booleanValue());
        eb.setShowEventStatus(((Boolean) hm.get("show_event_status")).booleanValue());
        eb.setShowSubjectStatus(((Boolean) hm.get("show_subject_status")).booleanValue());
        eb.setShowSubjectUniqueIdentifier(((Boolean) hm.get("show_subject_unique_id")).booleanValue());
        eb.setShowSubjectAgeAtEvent(((Boolean) hm.get("show_subject_age_at_event")).booleanValue());
        eb.setShowCRFstatus(((Boolean) hm.get("show_crf_status")).booleanValue());
        eb.setShowCRFversion(((Boolean) hm.get("show_crf_version")).booleanValue());
        eb.setShowCRFinterviewerName(((Boolean) hm.get("show_crf_int_name")).booleanValue());
        eb.setShowCRFinterviewerDate(((Boolean) hm.get("show_crf_int_date")).booleanValue());
        eb.setShowSubjectGroupInformation(((Boolean) hm.get("show_group_info")).booleanValue());
        // eb.setShowDiscrepancyInformation(((Boolean)
        // hm.get("show_disc_info")).booleanValue());
        // do we want to find group info here? looks like the best place for
        // non-repeats...
        // if (eb.isShowSubjectGroupInformation()) {
        eb.setSubjectGroupIds(getGroupIds(eb.getId()));
        // }
        eb.setODMMetaDataVersionName((String) hm.get("odm_metadataversion_name"));
        eb.setODMMetaDataVersionOid((String) hm.get("odm_metadataversion_oid"));
        eb.setODMPriorStudyOid((String) hm.get("odm_prior_study_oid"));
        eb.setODMPriorMetaDataVersionOid((String) hm.get("odm_prior_metadataversion_oid"));
        eb.setShowSubjectSecondaryId((Boolean) hm.get("show_secondary_id"));
        return eb;
    }

    private ArrayList getGroupIds(int datasetId) {
        ArrayList<Integer> groupIds = new ArrayList<Integer>();
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// dataset id
        this.setTypeExpected(2, TypeNames.INT);// subject group id
        HashMap<Integer, Integer> variablesNew = new HashMap<Integer, Integer>();
        variablesNew.put(new Integer(1), new Integer(datasetId));
        ArrayList alist = this.select(digester.getQuery("findAllGroups"), variablesNew);
        // convert them to ids for the array list, tbh
        // the above is an array list of hashmaps, each hash map being a row in
        // the DB
        for (Iterator iter = alist.iterator(); iter.hasNext();) {
            HashMap row = (HashMap) iter.next();
            Integer id = (Integer) row.get("study_group_class_id");
            groupIds.add(id);
        }
        return groupIds;
    }

    public Collection findAll() {
        this.setTypesExpected();
        ArrayList alist = this.select(digester.getQuery("findAll"));
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;
    }

    public Collection findTopFive(StudyBean currentStudy) {
        int studyId = currentStudy.getId();
        this.setTypesExpected();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyId));
        variables.put(new Integer(2), new Integer(studyId));
        ArrayList alist = this.select(digester.getQuery("findTopFive"), variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;
    }

    /**
     * find by owner id, reports a list of datasets by user account id.
     * 
     * @param ownerId
     *            studyId
     */

    public Collection findByOwnerId(int ownerId, int studyId) {
        // TODO add an findbyadminownerid?
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyId));
        variables.put(new Integer(2), new Integer(studyId));
        variables.put(new Integer(3), new Integer(ownerId));

        ArrayList alist = this.select(digester.getQuery("findByOwnerId"), variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            DatasetBean eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;
    }

    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public EntityBean findByPK(int ID) {
        DatasetBean eb = new DatasetBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(ID));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
        } else {
            logger.warn("found no object: " + sql + " " + ID);
        }
        return eb;
    }

    /**
     * 
     * @param name
     * @return
     */
    public EntityBean findByNameAndStudy(String name, StudyBean study) {
        DatasetBean eb = new DatasetBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), name);
        variables.put(new Integer(2), new Integer(study.getId()));
        String sql = digester.getQuery("findByNameAndStudy");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (DatasetBean) this.getEntityFromHashMap((HashMap) it.next());
        } else {
            logger.warn("found no object: " + sql + " " + name);
        }
        return eb;
    }

    // /**
    // *
    // * @param db
    // * @param format
    // * @param currentStudy The current study.
    // * @param parentStudy The parent of the current study, if the current
    // study has a parent; or an inactive StudyBean, otherwise.
    // * @return
    // */
    // public String generateDataset(DatasetBean db, int format, StudyBean
    // currentStudy, StudyBean parentStudy) {
    // //set up the types first
    //
    // this.setExtractTypesExpected();
    //
    // StringBuffer returnText = new StringBuffer("");
    // ArrayList alist = this.select(db.getSQLStatement());
    // //currently hits the view, returns the data
    //
    // ExtractBean exbean = new ExtractBean(ds);
    //
    // exbean = this.getExtractBeanFromArrayList(alist, currentStudy);
    //
    // exbean.setDataset(db);
    // if (!parentStudy.isActive()) {
    // exbean.setParentProtocolId(currentStudy.getIdentifier());
    // exbean.setParentStudyName(currentStudy.getName());
    // }
    // else {
    // exbean.setParentProtocolId(parentStudy.getIdentifier());
    // exbean.setParentStudyName(parentStudy.getName());
    // exbean.setSiteName(currentStudy.getName());
    // }
    //
    // exbean.setFormat(format);
    //
    // returnText.append(exbean.export());
    // return returnText.toString();
    // }
    //
    // //<<<<<<< DatasetDAO.java
    // public ArrayList generateSPSSDatasets(DatasetBean db,
    // int format,
    // StudyBean currentStudy,
    // StudyBean parentStudy) {
    // ArrayList files = new ArrayList();
    // //repeat the first few steps, including getting the data from the view
    // this.setExtractTypesExpected();
    // //StringBuffer ddlFile = new StringBuffer("");
    // //StringBuffer dataFile = new StringBuffer("");
    // ArrayList alist = this.select(db.getSQLStatement());
    // ExtractBean exbean = new ExtractBean(ds);
    //
    // exbean = this.getExtractBeanFromArrayList(alist, currentStudy);
    // exbean.setDataset(db);
    // if (!parentStudy.isActive()) {
    // exbean.setParentProtocolId(currentStudy.getIdentifier());
    // exbean.setParentStudyName(currentStudy.getName());
    // }
    // else {
    // exbean.setParentProtocolId(parentStudy.getIdentifier());
    // exbean.setParentStudyName(parentStudy.getName());
    // exbean.setSiteName(currentStudy.getName());
    // }
    //
    // try {
    // //exbean.setFormat(format);
    // files = exbean.exportSPSS();
    // //
    // } catch (NullPointerException e) {
    // //
    // e.printStackTrace();
    // if (files.isEmpty()) {
    // logger.warn("*** empty list");
    // }
    // Iterator it = files.iterator();
    // while (it.hasNext()) {
    // String file = (String)it.next();
    // logger.warn("NPE thrown with this: "+file);
    // }
    // }
    //
    // return files;
    // }

    // =======
    // /**
    // *
    // * @param db
    // * @param format
    // * @param currentStudy The current study.
    // * @param parentStudy The parent of the current study, if the current
    // study has a parent; or an inactive StudyBean, otherwise.
    // * @return
    // */
    // public ExtractBean generateDataset(DatasetBean db, StudyBean
    // currentStudy, StudyBean parentStudy) {
    // //set up the types first
    //
    // this.setExtractTypesExpected();
    // StringBuffer returnText = new StringBuffer("");
    // ArrayList alist = this.select(db.getSQLStatement());
    // //currently hits the view, returns the data
    // ExtractBean exbean = new ExtractBean(ds);
    //
    // exbean = this.getExtractBeanFromArrayList(alist, currentStudy);
    //
    // exbean.setDataset(db);
    // if (!parentStudy.isActive()) {
    // exbean.setParentProtocolId(currentStudy.getIdentifier());
    // exbean.setParentStudyName(currentStudy.getName());
    // }
    // else {
    // exbean.setParentProtocolId(parentStudy.getIdentifier());
    // exbean.setParentStudyName(parentStudy.getName());
    // exbean.setSiteName(currentStudy.getName());
    // }
    // return exbean;
    // }
    //
    // public HSSFWorkbook generateExcelDataset(DatasetBean db,
    // int format,
    // StudyBean currentStudy,
    // StudyBean parentStudy) {
    //
    // HSSFWorkbook wb = new HSSFWorkbook();
    // this.setExtractTypesExpected();
    //
    // ArrayList alist = this.select(db.getSQLStatement());
    //
    // ExtractBean exbean = new ExtractBean(ds);
    //
    // exbean = this.getExtractBeanFromArrayList(alist, currentStudy);
    //
    // exbean.setDataset(db);
    // if (!parentStudy.isActive()) {
    // exbean.setParentProtocolId(currentStudy.getIdentifier());
    // exbean.setParentStudyName(currentStudy.getName());
    // }
    // else {
    // exbean.setParentProtocolId(parentStudy.getIdentifier());
    // exbean.setParentStudyName(parentStudy.getName());
    // exbean.setSiteName(currentStudy.getName());
    // }
    //
    // exbean.setFormat(format);
    //
    // wb = exbean.exportExcel();
    // return wb;
    // }

    /**
     * Implements the Data Algorithm described in Dataset Export Algorithms,
     * stores output in the returned ExtractBean.
     * 
     * @vbc 08/06/2008 NEW EXTRACT DATA IMPLEMENTATION - add Study
     * 
     * @param eb
     *            The ExtractBean containing the dataset and study for which
     *            data is being retrieved.
     * @return An ExtractBean containing structured data stored by subject,
     *         study event definition, ordinal, CRF and item, as well as the
     *         maximum ordinal per study event definition.
     * 
     * 
     */
    public ExtractBean getDatasetData(ExtractBean eb, int currentstudyid, int parentstudyid) {

        /**
         * @vbc 08/06/2008 NEW EXTRACT DATA IMPLEMENTATION replace with a new
         *      algorithm
         */

        String sql = eb.getDataset().getSQLStatement();

        /**
         * I. First step to get the event_definition_id and item_id
         */

        // String parseSQLDataset(String sql, boolean issed, boolean
        // hasfilterzero)
        // get the study_event_definition_id IN (1,2,3)
        String st_sed_in = parseSQLDataset(sql, true, true);
        String st_itemid_in = parseSQLDataset(sql, false, true);

        /**
         * The next step get the string (study_subj_1, study_subj_2,...) for SQL
         * IN NOTE: This is not the final list of stusy_subject_id that will be
         * displayed!! It is only to help extract data faster with IN (...)
         * instead of SELECT subquerry
         */
        //
        // String st_studysubjectid_in =
        // getINStringInitStudySubjectIDs(currentstudyid, parentstudyid,
        // st_sed_in);
        /**
         * Get the final string of event_crf_id that will be used in other SQL
         * as IN (value1, value2,...)
         */
        // String st_eventcrfid_in = getINStringEventCRFIDs(currentstudyid,
        // parentstudyid, st_sed_in, st_studysubjectid_in);
        /**
         * get the study subjects; to each study subject it associates the data
         * from the subjects themselves
         */
        ArrayList newRows = selectStudySubjects(currentstudyid, parentstudyid, st_sed_in, st_itemid_in);
        /**
         * Add it to ths subjects
         */
        eb.addStudySubjectData(newRows);

        /**
         * II. Add the study_event records
         */

        /**
         * Add InKeysHelper HashMap This is to speed up the function InKeys in
         * theh ExtractBean protected boolean inKeys(int sedInd, int
         * sampleOrdinal, int crfInd, int itemInd, String groupName) {
         * 
         * This loop inside InKeys that has a major performance problem is
         * replaced:
         * 
         * for (Iterator iter = data.entrySet().iterator(); iter.hasNext();) {
         * String key = (String) ((java.util.Map.Entry) iter.next()).getKey() +
         * "_Ungrouped"; String testKey = getDataKey(0, currentDef.getId(),
         * sampleOrdinal, currentCRF.getId(), currentItem.getId(), 1,
         * groupName).substring(2); testKey = groupName.equals("Ungrouped") ?
         * testKey + "_Ungrouped" : testKey; if (key.contains(testKey)) { return
         * true; } }
         * 
         * 
         */

        // setHashMapInKeysHelper(String sedin, String itin, int studyid, int
        // parentid)
        HashMap nhInHelpKeys = setHashMapInKeysHelper(currentstudyid, parentstudyid, st_sed_in, st_itemid_in);
        eb.setHmInKeys(nhInHelpKeys);

        /**
         * Get the arrays of ArrayList for SQL BASE There are split in two
         * querries for perfomance
         */
        eb.resetArrayListEntryBASE_ITEMGROUPSIDE();
        loadBASE_EVENTINSIDEHashMap(currentstudyid, parentstudyid, st_sed_in, st_itemid_in, eb);
        loadBASE_ITEMGROUPSIDEHashMap(currentstudyid, parentstudyid, st_sed_in, st_itemid_in, eb);

        /**
         * add study_event data
         */
        eb.addStudyEventData();

        /**
         * add item_data
         */

        eb.addItemData();

        /**
         * NOTE:
         */
        /*
         * setExtractTypesExpected(); ArrayList viewRows = select(sql); Iterator
         * it = viewRows.iterator();
         * 
         * StudySubjectDAO ssdao = new StudySubjectDAO(ds); ItemGroupDAO igdao =
         * new ItemGroupDAO(ds); while (it.hasNext()) { HashMap row = (HashMap)
         * it.next(); // study subject properties Integer subjectId = (Integer)
         * row.get("subject_id"); // YW << make sure a StudySubject belong to
         * current study which // could be a site // otherwise it is not
         * necessary to extract this StudySubject // if currentStudy is a study
         * not a site, the all subjects belong to // its sites will be got
         * StudySubjectBean ssbean =
         * ssdao.findBySubjectIdAndStudy(subjectId.intValue(), eb.getStudy());
         * Integer studySubjectId = new Integer(ssbean.getId()); if
         * (studySubjectId.intValue() > 0) { // YW >> String studySubjectLabel =
         * (String) row.get("subject_identifier"); Date dateOfBirth = (Date)
         * row.get("date_of_birth"); String gender = (String) row.get("gender"); //
         * added more subject properties, tbh String secondaryID = (String)
         * row.get("unique_identifier"); Boolean dobCollected = (Boolean)
         * row.get("dob_collected"); Integer subjectStatusId = (Integer)
         * row.get("subject_status_id"); String subjectSecondaryLabel = (String)
         * row.get("secondary_label"); // study event properties Integer
         * studyEventDefinitionId = (Integer)
         * row.get("study_event_definition_id"); String studyEventDefinitionName =
         * (String) row.get("study_event_definition_name"); Boolean repeating =
         * (Boolean) row.get("study_event_definition_repeating"); Integer
         * sampleOrdinal = (Integer) row.get("sample_ordinal"); // event start,
         * end, location triple String studyEventLocation = (String)
         * row.get("location"); Date studyEventStart = (Date)
         * row.get("date_start"); Date studyEventEnd = (Date)
         * row.get("date_end"); // item data Integer crfId = (Integer)
         * row.get("crf_id"); Integer itemId = (Integer) row.get("item_id"); //
         * YW 12-06-2007 << do convert for date data-type item. String
         * itemValue = Utils.convertedItemDateValue((String) row.get("value"),
         * oc_df_string, local_df_string); // YW >> // TODO add more columns
         * here: add Item Group data, add Disc. // data, add CRF data // String
         * itemGroupName = (String) row.get("item_group_name"); // Integer
         * itemDataId = (Integer) row.get("item_data_id"); // String
         * discNoteDescription = (String) //
         * row.get("discrepancy_note_description"); // Integer
         * resolutionStatusId = (Integer) //
         * row.get("discrepancy_resolution_status_id"); // String
         * discDetailedNotes = (String) // row.get("detailed_notes"); // Integer
         * discTypeId = (Integer) // row.get("discrepancy_note_type_id");
         * 
         * String crfVersionName = (String) row.get("crf_version_name"); Integer
         * crfVersionStatusId = (Integer) row.get("crf_version_status_id");
         * String interviewerName = (String) row.get("interviewer_name"); Date
         * dateInterviewed = (Date) row.get("date_interviewed"); Date
         * dateCompleted = (Date) row.get("event_crf_date_completed"); Date
         * dateValidateCompleted = (Date)
         * row.get("event_crf_date_validate_completed"); Integer
         * completionStatusId = (Integer) row.get("completion_status_id");
         * 
         * Integer studyGroupId = (Integer) row.get("study_group_id"); Integer
         * studyGroupClassId = (Integer) row.get("study_group_class_id");
         * Integer itemDataOrdinal = (Integer) row.get("item_data_ordinal");
         * String itemGroupName = "Ungrouped"; String itemGroupName_temp =
         * (String) row.get("item_group_name"); // this code will not support
         * 2.0.1 upgrades, since we are // looking at the item-group itself //
         * not at the updated extract_data_table // changed the above line from ""
         * to "Ungrouped", tbh 01-14-2008 ArrayList<ItemGroupBean> itemGroups =
         * (ArrayList<ItemGroupBean>) igdao.findGroupsByItemID(itemId); for
         * (int g = 0; g < itemGroups.size(); ++g) { if
         * (itemGroupName_temp.equals(itemGroups.get(g).getName())) {
         * itemGroupName = itemGroupName_temp; break; } } //
         * eb.addStudySubjectData(studySubjectId, studySubjectLabel,
         * dateOfBirth, gender, subjectStatusId, dobCollected, secondaryID, //
         * subjectSecondaryLabel); // eb.addStudyEventData(studySubjectId,
         * studyEventDefinitionName, studyEventDefinitionId, sampleOrdinal,
         * studyEventLocation, studyEventStart, // studyEventEnd,
         * crfVersionName, crfVersionStatusId, dateInterviewed, interviewerName,
         * dateCompleted, dateValidateCompleted, // completionStatusId); // or
         * add CRFversion data here??? logger.info("*** found item group name
         * before we call add item data on 707: " + itemGroupName); if
         * (!"".equals(itemGroupName)) { // logger.info("got inside the loop!");
         * eb.addItemData(studySubjectId, studyEventDefinitionId, sampleOrdinal,
         * crfId, itemId, itemValue, itemDataOrdinal, itemGroupName); if
         * (itemDataOrdinal.intValue() > eb.getMaxItemDataBeanOrdinal()) {
         * eb.setMaxItemDataBeanOrdinal(itemDataOrdinal.intValue()); //
         * logger.info("### just updated max ordinal for // itemdatabean:
         * "+itemDataOrdinal.intValue()); } }
         * eb.addGroupName(itemGroupName_temp, itemDataOrdinal); // TODO add new
         * functions here // req's change: remove item groups, // discrepancy //
         * add subject groups // * eb.addDiscrepancyNoteData(itemDataId,
         * discNoteDescription, // * resolutionStatusId, discDetailedNotes,
         * discTypeId); // eb.addSubjectGroupData(studyGroupId, //
         * studyGroupClassId);//???add to item data instead??? //
         * eb.addCRFVersionData();//???add to study event data // instead??? }
         */

        // logger.info("count for item data at end: " +
        // eb.getItemNames().size());
        // logger.info("count for group names at end: " +
        // eb.getGroupNames().size());
        return eb;
    }

    // public ExtractBean getExtractBeanFromArrayListOld(ArrayList viewRows,
    // StudyBean currentStudy) {
    // ExtractBean exbean = new ExtractBean();
    //
    // Iterator it = viewRows.iterator();
    // //have to add a guard clause in there to make sure we
    // //properly regulate active-study subjects vs. all subjects for admins
    // while (it.hasNext()) {
    //
    // HashMap row = (HashMap) it.next();
    // Integer studyId = (Integer) row.get("study_id");
    // logger.warn("before guard clause:");
    // if (studyId.intValue()==currentStudy.getId()) {//or you are an admin
    // //start guard clause
    // logger.warn("inside guard clause:");
    // Integer studyEventDefinitionId = (Integer)
    // row.get("study_event_definition_id");
    // String studyEventDefinitionName = (String)
    // row.get("study_event_definition_name");
    // Boolean studyEventDefinitionRepeating = (Boolean)
    // row.get("study_event_definition_repeating");
    //
    // ExtractStudyEventDefinitionBean sedb =
    // exbean.addStudyEventDefinition(studyEventDefinitionId,
    // studyEventDefinitionName, studyEventDefinitionRepeating);
    //
    // Integer crfVersionId = (Integer) row.get("crf_version_id");
    // String crfName = (String) row.get("crf_name");
    // String crfVersionName = (String) row.get("crf_version_name");
    //
    // ExtractCRFVersionBean cvb = sedb.addCRFVersion(crfVersionId, crfName,
    // crfVersionName);
    //
    // // note: these do NOT correspond to table names and columns!!
    // Integer studySubjectId = (Integer) row.get("subject_id");
    // String studySubjectLabel = (String) row.get("subject_identifier");
    // //check max length of V1 here, for SPSS purposes
    // //exbean.updateSPSSColLengths("Subject Unique
    // ID",studySubjectLabel.length());
    // String protocolId = (String) row.get("study_identifier");
    // //check the max length of protocol Id here for part of V2, for SPSS
    // purposes
    // //need to add the siteUniqueIdentifier to it to determine the full length
    // of V2
    // //exbean.updateSPSSColLengths("Protocol-ID-Site ID",protocolId.length());
    // Date dateOfBirth = (Date)row.get("date_of_birth");
    // String gender = (String)row.get("gender");
    //
    //
    // ExtractStudySubjectBean ssb =
    // exbean.addStudySubject(studySubjectId, studySubjectLabel,
    // protocolId,dateOfBirth,gender);
    //
    // Integer dbOrdinal = (Integer) row.get("sample_ordinal");
    // Integer studyEventId = (Integer) row.get("study_event_id");
    // String studyEventLocation = (String) row.get("location");
    // Date studyEventStart = (Date) row.get("date_start");
    // Date studyEventEnd = (Date) row.get("date_end");
    // ssb.addStudyEvent(sedb, studyEventId, studyEventLocation,
    // studyEventStart, studyEventEnd, dbOrdinal);
    //
    // Integer itemId = (Integer) row.get("item_id");
    // String itemName = (String) row.get("item_name");
    // String itemValue = (String) row.get("value");
    //
    // cvb.addItem(itemId, itemName);
    // ssb.addValue(sedb, dbOrdinal, crfVersionId, itemId, itemValue);
    // //measure max length of column VX here for SPSS generation purposes
    // //exbean.updateSPSSColLengths(itemName,itemValue.length());
    // logger.warn("entered "+itemValue.length()+" for item "+itemName);
    // sedb.updateCRFVersion(cvb);
    // exbean.updateStudyEventDefinition(sedb);
    // exbean.updateStudySubject(ssb);
    //
    // ResponseSetBean rsb = new ResponseSetBean();
    // String optionsText = (String) row.get("options_text");
    // String optionsValues = (String) row.get("options_values");
    // //we only want to set options that are numbers,
    // //characters are rejected by SPSS
    // Pattern catchText = Pattern.compile("[[0-9],]+");
    // java.util.regex.Matcher matchText =
    // catchText.matcher(optionsValues);
    // boolean b = matchText.matches();
    // //logger.warn("found options: "+optionsValues+", returning: "+b);
    // rsb.setOptions(optionsText, optionsValues);
    // Integer responseTypeId = (Integer) row.get("response_type_id");
    // if (b) {
    // rsb.setResponseTypeId(responseTypeId.intValue());
    // } else {
    // rsb.setResponseTypeId(1);
    // }
    // exbean.addResponseSet(itemName,rsb);
    // exbean.updateMaxSamples(sedb, ssb.getNumSamples(sedb.getId()));
    // //end guard clause
    // }
    // }
    //
    // return exbean;
    //
    // }

    // public ExtractBean getExtractBeanFromArrayList(ArrayList viewRows) {
    // ExtractBean exbean = new ExtractBean();
    //
    // ArrayList rows = new ArrayList(); // contains subject identifiers
    // ArrayList columns = new ArrayList();
    // HashMap content = new HashMap();
    //
    // columns.add("Subject");
    //
    // // tells us if a row for a subject has been added
    // // key is a String of format "0001" where "0001" is the
    // subject_identifier
    // // for a given subject, the key is present if the subject has been added,
    // not present otherwise
    // HashMap subjectAdded = new HashMap();
    //
    // // tells us if a column for the crfVersion-itemId combination has been
    // added
    // // key is a String of format "1-2" where "1" is the crfVersionId and "2"
    // is the itemId
    // // for a given crfVersion/item combination, the key is present if the
    // combination has been added, not present otherwise
    // // the value will be the itemNum of the crfVersion/itemId combination
    // HashMap crfVersionItemIdAdded = new HashMap();
    //
    // int itemNum = 0;
    //
    // Iterator it = viewRows.iterator();
    //
    // while (it.hasNext()) {
    // HashMap row = (HashMap) it.next();
    // String subjectIdentifier = (String) row.get("subject_identifier");
    //
    // if (!subjectAdded.containsKey(subjectIdentifier)) {
    // subjectAdded.put(subjectIdentifier, Boolean.TRUE);
    // rows.add(subjectIdentifier);
    // }
    //
    // String CRFName = (String) row.get("crf_name");
    // String CRFVersionName = (String) row.get("crf_version_name");
    // String itemName = (String) row.get("item_name");
    // String contentValue = (String) row.get("value");
    //
    // Integer crfVersionId = (Integer) row.get("crf_version_id");
    // Integer itemId = (Integer) row.get("item_id");
    //
    // if ((crfVersionId == null) || (itemId == null)) {
    // logger.warn("found null crfversionid/itemid");
    // continue;
    // }
    //
    // String key = crfVersionId.intValue() + "-" + itemId.intValue();
    // if (!crfVersionItemIdAdded.containsKey(key)) {
    // itemNum++;
    // crfVersionItemIdAdded.put(key, new Integer(itemNum));
    //
    // columns.add("CRF Name " + itemNum);
    // content.put("CRF Name " + itemNum, CRFName);
    //
    // columns.add("CRF Version " + itemNum);
    // content.put("CRF Version " + itemNum, CRFVersionName);
    //
    // columns.add(itemName + " " + itemNum);
    // String contentKey = subjectIdentifier + " " + itemName + " " + itemNum;
    // content.put(contentKey, contentValue);
    // }
    // else {
    // Integer itemNumInteger = (Integer) crfVersionItemIdAdded.get(key);
    // String contentKey = subjectIdentifier + " " + itemName + " " +
    // itemNumInteger.intValue();
    // content.put(contentKey, contentValue);
    // }
    // }
    //
    // // int maxCRFs = 0;
    // // ArrayList subjects = new ArrayList();
    // // ArrayList listCRFs = new ArrayList();
    // // HashMap content = new HashMap();
    // // ArrayList columnHeaders = new ArrayList();
    // // Iterator it = alist.iterator();
    // // columnHeaders.add("Subject");
    // // while (it.hasNext()) {
    // // HashMap hm = (HashMap) it.next();
    // // String subjectIdentifier = (String) hm.get("subject_identifier");
    // // //logger.warn("** captured subject identifier:
    // "+subjectIdentifier);
    // // if (!subjects.contains(subjectIdentifier)) {
    // // subjects.add(subjectIdentifier);
    // // logger.warn("** ADDED subject identifier: "+subjectIdentifier);
    // // } else {
    // // //logger.warn("** DID NOT ADD subject identifier");
    // // }
    // // String CRFName = (String) hm.get("crf_name");
    // // String CRFVersionName = (String) hm.get("crf_version_name");
    // // String checkCRF = CRFName + " " + CRFVersionName;
    // // if (!listCRFs.contains(checkCRF)) {
    // // maxCRFs++;
    // // columnHeaders.add("CRF Name " + maxCRFs);
    // // columnHeaders.add("CRF Version " + maxCRFs);
    // // content.put("CRF Name " + maxCRFs, CRFName);
    // // content.put("CRF Version " + maxCRFs, CRFVersionName);
    // // //so this means that either row+column or just column names
    // // //can be filled in the export dataset functions
    // // }
    // // String itemName = (String) hm.get("item_name");
    // // String contentValue = (String) hm.get("value");
    // // String orderedItemName = itemName + " " + maxCRFs;
    // // String contentKey = subjectIdentifier + " " + orderedItemName;
    // //
    // //if (itemName.equals("2_B1")) {
    // // logger.info("** ADDED column header for item values: " +
    // orderedItemName + "; CRF: " + CRFName + " " + CRFVersionName + "; column
    // num: " + maxCRFs + "; subject: " + subjectIdentifier);
    // // logger.info("key is " + contentKey + " and value is " +
    // contentValue);
    // //}
    // //
    // // columnHeaders.add(orderedItemName);
    // // content.put(contentKey, contentValue);
    // // }
    // //
    // // // three types of keys in content:
    // // // CRF Name x - the name of a CRF for the x-th three-column entry
    // // // CRF Version x - the name of a CRF version for the x-th three-column
    // entry
    // // // 001 2_B1 3 - the value of the item with item name "2_B1", for
    // subject with identifier "001", in the 3d three column entry
    // // // ("001", "2_B1", and "3" are just examples)
    //
    // exbean.setColNames(columns);
    // exbean.setRowNames(rows);
    // exbean.setContent(content);
    // return exbean;
    // }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
        ArrayList al = new ArrayList();

        return al;
    }

    public ArrayList findAllByStudyId(int studyId) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyId));
        variables.put(new Integer(2), new Integer(studyId));

        return executeFindAllQuery("findAllByStudyId", variables);
    }

    public ArrayList findAllByStudyIdAdmin(int studyId) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyId));
        variables.put(new Integer(2), new Integer(studyId));

        return executeFindAllQuery("findAllByStudyIdAdmin", variables);
    }

    /**
     * Initialize itemMap, itemIds, itemDefCrf and groupIds for a DatasetBean
     * 
     * @param db
     * @return
     * @author ywang (Feb., 2008)
     */
    public DatasetBean initialDatasetData(int datasetId) {
        ItemDAO idao = new ItemDAO(ds);
        DatasetBean db = (DatasetBean) findByPK(datasetId);
        String sql = db.getSQLStatement();
        sql = sql.split("study_event_definition_id in")[1];
        String[] ss = sql.split("and item_id in");
        String sedIds = ss[0];
        String[] sss = ss[1].split("and");
        String itemIds = sss[0];

        this.setDefinitionCrfItemTypesExpected();
        logger.debug("begin to execute GetDefinitionCrfItemSql");
        ArrayList alist = select(getDefinitionCrfItemSql(sedIds, itemIds));
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap row = (HashMap) it.next();
            ItemBean ib = (ItemBean) idao.getEntityFromHashMap(row);
            Integer defId = (Integer) row.get("sed_id");
            String defName = (String) row.get("sed_name");
            String crfName = (String) row.get("crf_name");
            Integer itemId = ib.getId();
            String key = defId + "_" + itemId;
            if (!db.getItemMap().containsKey(key)) {
                ib.setSelected(true);
                ib.setDefName(defName);
                ib.setCrfName(crfName);
                ib.setDatasetItemMapKey(key);
                // YW 2-22-2008, CreateDatasetServlet.java shows that eventIds
                // contains study_event_definition_ids
                if (!db.getEventIds().contains(defId)) {
                    db.getEventIds().add(defId);
                }
                db.getItemIds().add(itemId);
                db.getItemDefCrf().add(ib);
                db.getItemMap().put(key, ib);
            }
        }
        db.setSubjectGroupIds(getGroupIds(db.getId()));
        return db;
    }

    protected String getDefinitionCrfItemSql(String sedIds, String itemIds) {
        return "select item.*, sed.study_event_definition_id as sed_id, sed.name as sed_name, crf.crf_id, crf.name as crf_name"
            + " from study_event_definition sed, event_definition_crf edc, crf, crf_version cv,item_form_metadata ifm, item"
            + " where sed.study_event_definition_id in " + sedIds + " and item.item_id in " + itemIds
            + " and sed.study_event_definition_id = edc.study_event_definition_id and edc.crf_id = crf.crf_id"
            + " and crf.crf_id = cv.crf_id and cv.crf_version_id = ifm.crf_version_id and ifm.item_id = item.item_id";
    }

    /**
     * Update all columns of the dataset table except owner_id
     * 
     * @param eb
     * @return
     * 
     * @author ywang (Feb., 2008)
     */
    public EntityBean updateAll(EntityBean eb) {
        eb.setActive(false);
        DatasetBean db = (DatasetBean) eb;
        HashMap variables = new HashMap();
        HashMap nullVars = new HashMap();
        variables.put(new Integer(1), new Integer(db.getStudyId()));
        variables.put(new Integer(2), new Integer(db.getStatus().getId()));
        variables.put(new Integer(3), db.getName());
        variables.put(new Integer(4), db.getDescription());
        variables.put(new Integer(5), db.getSQLStatement());
        variables.put(new Integer(6), db.getDateLastRun());
        variables.put(new Integer(7), new Integer(db.getNumRuns()));

        variables.put(new Integer(8), new Integer(db.getUpdaterId()));
        if (db.getApproverId() <= 0) {
            // nullVars.put(new Integer(9), null);
            // ABOVE IS WRONG; follow the example below:
            nullVars.put(new Integer(9), new Integer(Types.NUMERIC));
            variables.put(new Integer(9), null);
        } else {
            variables.put(new Integer(9), new Integer(db.getApproverId()));
        }

        variables.put(new Integer(10), db.getDateStart());
        variables.put(new Integer(11), db.getDateEnd());
        variables.put(new Integer(12), new Boolean(db.isShowEventLocation()));
        variables.put(new Integer(13), new Boolean(db.isShowEventStart()));
        variables.put(new Integer(14), new Boolean(db.isShowEventEnd()));
        variables.put(new Integer(15), new Boolean(db.isShowSubjectDob()));
        variables.put(new Integer(16), new Boolean(db.isShowSubjectGender()));
        variables.put(new Integer(17), new Boolean(db.isShowEventStatus()));
        variables.put(new Integer(18), new Boolean(db.isShowSubjectStatus()));
        variables.put(new Integer(19), new Boolean(db.isShowSubjectUniqueIdentifier()));
        variables.put(new Integer(20), new Boolean(db.isShowSubjectAgeAtEvent()));
        variables.put(new Integer(21), new Boolean(db.isShowCRFstatus()));
        variables.put(new Integer(22), new Boolean(db.isShowCRFversion()));
        variables.put(new Integer(23), new Boolean(db.isShowCRFinterviewerName()));
        variables.put(new Integer(24), new Boolean(db.isShowCRFinterviewerDate()));
        variables.put(new Integer(25), new Boolean(db.isShowSubjectGroupInformation()));
        variables.put(new Integer(26), new Boolean(false));
        variables.put(new Integer(27), db.getODMMetaDataVersionName());
        variables.put(new Integer(28), db.getODMMetaDataVersionOid());
        variables.put(new Integer(29), db.getODMPriorStudyOid());
        variables.put(new Integer(30), db.getODMPriorMetaDataVersionOid());
        variables.put(new Integer(31), new Integer(db.getId()));
        this.execute(digester.getQuery("updateAll"), variables, nullVars);
        if (isQuerySuccessful()) {
            eb.setActive(true);
        }
        return eb;
    }

    public EntityBean updateGroupMap(DatasetBean db) {
        HashMap nullVars = new HashMap();
        db.setActive(false);
        boolean success = true;

        ArrayList<Integer> sgcIds = this.getGroupIds(db.getId());
        if (sgcIds == null)
            sgcIds = new ArrayList<Integer>();
        ArrayList<Integer> dbSgcIds = (ArrayList<Integer>) db.getSubjectGroupIds().clone();
        if (dbSgcIds == null)
            dbSgcIds = new ArrayList<Integer>();
        if (sgcIds.size() > 0) {
            for (Integer id : sgcIds) {
                if (!dbSgcIds.contains(id)) {
                    removeGroupMap(db.getId(), id, nullVars);
                    if (!isQuerySuccessful())
                        success = false;
                } else {
                    dbSgcIds.remove(id);
                }
            }
        }
        if (success) {
            if (dbSgcIds.size() > 0) {
                for (Integer id : dbSgcIds) {
                    createGroupMap(db.getId(), id, nullVars);
                    if (!isQuerySuccessful())
                        success = false;
                }
            }
        }
        if (success) {
            db.setActive(true);
        }
        return db;
    }

    protected void createGroupMap(int datasetId, int studyGroupClassId, HashMap nullVars) {
        HashMap<Integer, Integer> variablesNew = new HashMap<Integer, Integer>();
        variablesNew.put(new Integer(1), new Integer(datasetId));
        Integer groupId = Integer.valueOf(studyGroupClassId);
        variablesNew.put(new Integer(2), groupId);
        this.execute(digester.getQuery("createGroupMap"), variablesNew, nullVars);
    }

    protected void removeGroupMap(int datasetId, int studyGroupClassId, HashMap nullVars) {
        HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
        variables.put(new Integer(1), new Integer(datasetId));
        Integer groupId = Integer.valueOf(studyGroupClassId);
        variables.put(new Integer(2), groupId);
        this.execute(digester.getQuery("removeGroupMap"), variables, nullVars);
    }

    /**
     * 
     * @vbc 08/06/2008 NEW EXTRACT DATA IMPLEMENTATION parses the sql dataset
     *      and extract the two IN set of values - remove the value 0 from the
     *      study_event_definition_id - if ssed is true return the
     *      study_event_definition_id else return the item_id set
     */

    protected String parseSQLDataset(String sql, boolean issed, boolean hasfilterzero)

    {
        // for instance:
        // select distinct * from extract_data_table
        // where study_event_definition_id in (3, 4)
        // and item_id in (53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 53, 54,
        // 55, 56, 57, 58, 59, 60, 61, 62, 63)
        // and (date(date_created) >= date('1900-01-01')) and
        // (date(date_created) <= date('2100-12-31'))

        int sedid_one = 0;
        int sedid_two = 0;
        int itid_one = 0;
        int itid_two = 0;
        String sed_st = "";
        String sed_stno = "";
        String it_st = "";
        String it_stno = "";
        Vector sedvec_tmp = new Vector();
        Vector sedvec = new Vector();
        Vector itvec = new Vector();

        // get the first
        sedid_one = sql.indexOf("(");
        sedid_two = sql.indexOf(")");
        if (sedid_one != -1 && sedid_two != -1) {
            // found - get the substring
            sed_st = sql.substring(sedid_one + 1, sedid_two);
            // parse it for values
            boolean hasmore = true;
            int no;
            do {
                // get to the first comma
                int ic = sed_st.indexOf(",");
                if (ic != -1) {
                    // found
                    sed_stno = sed_st.substring(0, ic);
                    // get into int
                    try {
                        no = Integer.parseInt(sed_stno.trim());
                        sedvec_tmp.add(new Integer(no));

                        // set the new string
                        sed_st = sed_st.substring(ic + 1, sed_st.length());

                    } catch (NumberFormatException nfe) {
                        // info("Exception when converted to Integer
                        // for:"+number);
                        // fall through
                    }// try

                } else {
                    // only one
                    try {
                        no = Integer.parseInt(sed_st.trim());

                        sedvec_tmp.add(new Integer(no));
                    } catch (NumberFormatException nfe) {
                        // info("Exception when converted to Integer
                        // for:"+number);
                        // fall through
                    }// try

                    hasmore = false;
                }

            } while (hasmore);

        } else {
            // ERROR
        }// if

        // get the second
        sql = sql.substring(sedid_two + 1, sql.length());
        itid_one = sql.indexOf("(");
        itid_two = sql.indexOf(")");
        if (itid_one != -1 && sedid_two != -1) {
            // found - get the substring
            it_st = sql.substring(itid_one + 1, itid_two);
            // parse it for values
            boolean hasmore = true;
            int no;
            do {
                // get to the first comma
                int ic = it_st.indexOf(",");
                if (ic != -1) {
                    // found
                    it_stno = it_st.substring(0, ic);
                    // get into int
                    try {
                        no = Integer.parseInt(it_stno.trim());
                        itvec.add(new Integer(no));

                        // set the new string
                        it_st = it_st.substring(ic + 1, it_st.length());

                    } catch (NumberFormatException nfe) {
                        // info("Exception when converted to Integer
                        // for:"+number);
                        // fall through
                    }// try

                } else {
                    // only one
                    try {
                        no = Integer.parseInt(it_st.trim());

                        itvec.add(new Integer(no));
                    } catch (NumberFormatException nfe) {
                        // info("Exception when converted to Integer
                        // for:"+number);
                        // fall through
                    }// try

                    hasmore = false;
                }

            } while (hasmore);

        } else {
            // ERROR
        }// if

        // Eliminate 0 from SED but only if
        if (hasfilterzero) {
            for (int i = 0; i < sedvec_tmp.size(); i++) {
                Integer itmp = (Integer) sedvec_tmp.get(i);
                if (itmp.intValue() != 0) {
                    sedvec.add(itmp);
                }
            }// for
        }// if
        int a = 0;
        a = 9;

        String stsed_in = "";
        for (int ij = 0; ij < sedvec.size(); ij++) {
            stsed_in = stsed_in + ((Integer) sedvec.get(ij)).toString();
            if (ij == sedvec.size() - 1) {
                // last
            } else {
                stsed_in = stsed_in + ",";
            }// if

        }// for

        String stit_in = "";
        for (int ij = 0; ij < itvec.size(); ij++) {
            stit_in = stit_in + ((Integer) itvec.get(ij)).toString();
            if (ij == itvec.size() - 1) {
                // last
            } else {
                stit_in = stit_in + ",";
            }// if

        }// for

        stsed_in = "(" + stsed_in + ")";
        stit_in = "(" + stit_in + ")";

        if (issed) {
            return stsed_in;
        } else {
            return stit_in;
        }//

    }

}
