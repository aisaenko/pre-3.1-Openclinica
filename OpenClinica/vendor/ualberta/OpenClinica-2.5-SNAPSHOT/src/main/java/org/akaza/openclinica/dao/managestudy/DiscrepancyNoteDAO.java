/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DiscrepancyNoteDAO extends AuditableEntityDAO {
    // if true, we fetch the mapping along with the bean
    // only applies to functions which return a single bean
    private boolean fetchMapping = false;

    /**
     * @return Returns the fetchMapping.
     */
    public boolean isFetchMapping() {
        return fetchMapping;
    }

    /**
     * @param fetchMapping
     *            The fetchMapping to set.
     */
    public void setFetchMapping(boolean fetchMapping) {
        this.fetchMapping = fetchMapping;
    }

    private void setQueryNames() {
        findByPKAndStudyName = "findByPKAndStudy";
        getCurrentPKName = "getCurrentPrimaryKey";
    }

    public DiscrepancyNoteDAO(DataSource ds) {
        super(ds);
        setQueryNames();
    }

    public DiscrepancyNoteDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
        setQueryNames();
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_DISCREPANCY_NOTE;
    }

    @Override
    public void setTypesExpected() {
        // discrepancy_note_id serial NOT NULL,
        // description varchar(255),
        // discrepancy_note_type_id numeric,
        // resolution_status_id numeric,

        // detailed_notes varchar(1000),
        // date_created date,
        // owner_id numeric,
        // parent_dn_id numeric,
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.STRING);
        this.setTypeExpected(3, TypeNames.INT);
        this.setTypeExpected(4, TypeNames.INT);

        this.setTypeExpected(5, TypeNames.STRING);
        this.setTypeExpected(6, TypeNames.DATE);
        this.setTypeExpected(7, TypeNames.INT);
        this.setTypeExpected(8, TypeNames.INT);
        this.setTypeExpected(9, TypeNames.STRING);
        this.setTypeExpected(10, TypeNames.INT);
    }

    public void setMapTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.INT);
        this.setTypeExpected(3, TypeNames.STRING);
    }

    /**
     * <p>
     * getEntityFromHashMap, the method that gets the object from the database
     * query.
     */
    public Object getEntityFromHashMap(HashMap hm) {
        DiscrepancyNoteBean eb = new DiscrepancyNoteBean();
        Date dateCreated = (Date) hm.get("date_created");
        Integer ownerId = (Integer) hm.get("owner_id");
        eb.setCreatedDate(dateCreated);
        eb.setOwnerId(ownerId.intValue());

        // discrepancy_note_id serial NOT NULL,
        // description varchar(255),
        // discrepancy_note_type_id numeric,
        // resolution_status_id numeric,

        // detailed_notes varchar(1000),
        // date_created date,
        // owner_id numeric,
        // parent_dn_id numeric,
        eb.setId(selectInt(hm, "discrepancy_note_id"));
        eb.setDescription((String) hm.get("description"));
        eb.setDiscrepancyNoteTypeId(((Integer) hm.get("discrepancy_note_type_id")).intValue());
        eb.setResolutionStatusId(((Integer) hm.get("resolution_status_id")).intValue());
        eb.setParentDnId(((Integer) hm.get("parent_dn_id")).intValue());
        eb.setDetailedNotes((String) hm.get("detailed_notes"));
        eb.setEntityType((String) hm.get("entity_type"));
        eb.setDisType(DiscrepancyNoteType.get(eb.getDiscrepancyNoteTypeId()));
        eb.setResStatus(ResolutionStatus.get(eb.getResolutionStatusId()));
        eb.setStudyId(selectInt(hm, "study_id"));
        return eb;
    }

    public Collection findAll() {
        return this.executeFindAllQuery("findAll");
    }

    public ArrayList findAllParentsByStudy(StudyBean study) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(study.getId()));
        variables.put(new Integer(2), new Integer(study.getId()));
        ArrayList notes = executeFindAllQuery("findAllParentsByStudy", variables);

        if (fetchMapping) {
            for (int i = 0; i < notes.size(); i++) {
                DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) notes.get(i);
                dnb = findSingleMapping(dnb);
                notes.set(i, dnb);
            }
        }

        return notes;
    }

    public ArrayList findAllByStudyAndParent(StudyBean study, int parentId) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(parentId));
        variables.put(new Integer(2), new Integer(study.getId()));
        variables.put(new Integer(3), new Integer(study.getId()));
        return this.executeFindAllQuery("findAllByStudyAndParent", variables);
    }

    public ArrayList<DiscrepancyNoteBean> findAllItemNotesByEventCRF(int eventCRFId) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(eventCRFId));
        alist = this.select(digester.getQuery("findAllItemNotesByEventCRF"), variables);
        ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            al.add(eb);
        }
        return al;
    }

    public Collection findAllByEntityAndColumn(String entityName, int entityId, String column) {
        this.setTypesExpected();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        ArrayList alist = new ArrayList();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(entityId));
        variables.put(new Integer(2), column);
        if ("subject".equalsIgnoreCase(entityName)) {
            alist = this.select(digester.getQuery("findAllBySubjectAndColumn"), variables);
        } else if ("studySub".equalsIgnoreCase(entityName)) {
            alist = this.select(digester.getQuery("findAllByStudySubjectAndColumn"), variables);
        } else if ("eventCrf".equalsIgnoreCase(entityName)) {
            this.setTypeExpected(12, TypeNames.DATE);// date_start
            this.setTypeExpected(13, TypeNames.STRING);// sed_name
            this.setTypeExpected(14, TypeNames.STRING);// crf_name
            alist = this.select(digester.getQuery("findAllByEventCRFAndColumn"), variables);
        } else if ("studyEvent".equalsIgnoreCase(entityName)) {
            this.setTypeExpected(12, TypeNames.DATE);// date_start
            this.setTypeExpected(13, TypeNames.STRING);// sed_name
            alist = this.select(digester.getQuery("findAllByStudyEventAndColumn"), variables);
        } else if ("itemData".equalsIgnoreCase(entityName)) {
            this.setTypeExpected(12, TypeNames.DATE);// date_start
            this.setTypeExpected(13, TypeNames.STRING);// sed_name
            this.setTypeExpected(14, TypeNames.STRING);// crf_name
            this.setTypeExpected(15, TypeNames.STRING);// item_name
            alist = this.select(digester.getQuery("findAllByItemDataAndColumn"), variables);
        }

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setSubjectName((String) hm.get("label"));
            if ("eventCrf".equalsIgnoreCase(entityName) || "itemData".equalsIgnoreCase(entityName)) {
                eb.setEventName((String) hm.get("sed_name"));
                eb.setEventStart((Date) hm.get("date_start"));
                eb.setCrfName((String) hm.get("crf_name"));
                eb.setEntityName((String) hm.get("item_name"));

            } else if ("studyEvent".equalsIgnoreCase(entityName)) {
                eb.setEventName((String) hm.get("sed_name"));
                eb.setEventStart((Date) hm.get("date_start"));
            }
            if (fetchMapping) {
                eb = findSingleMapping(eb);
            }

            al.add(eb);
        }
        return al;
    }

    public ArrayList findAllEntityByPK(String entityName, int noteId) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(noteId));
        variables.put(new Integer(2), new Integer(noteId));
        if ("subject".equalsIgnoreCase(entityName)) {
            this.setTypeExpected(12, TypeNames.STRING);// column_name
            alist = this.select(digester.getQuery("findAllSubjectByPK"), variables);
        } else if ("studySub".equalsIgnoreCase(entityName)) {
            this.setTypeExpected(12, TypeNames.STRING);// column_name
            alist = this.select(digester.getQuery("findAllStudySubjectByPK"), variables);
        } else if ("eventCrf".equalsIgnoreCase(entityName)) {
            this.setTypeExpected(12, TypeNames.DATE);// date_start
            this.setTypeExpected(13, TypeNames.STRING);// sed_name
            this.setTypeExpected(14, TypeNames.STRING);// crf_name
            this.setTypeExpected(15, TypeNames.STRING);// column_name
            alist = this.select(digester.getQuery("findAllEventCRFByPK"), variables);
        } else if ("studyEvent".equalsIgnoreCase(entityName)) {
            this.setTypeExpected(12, TypeNames.DATE);// date_start
            this.setTypeExpected(13, TypeNames.STRING);// sed_name
            this.setTypeExpected(14, TypeNames.STRING);// column_name
            alist = this.select(digester.getQuery("findAllStudyEventByPK"), variables);
        } else if ("itemData".equalsIgnoreCase(entityName)) {
            this.setTypeExpected(12, TypeNames.DATE);// date_start
            this.setTypeExpected(13, TypeNames.STRING);// sed_name
            this.setTypeExpected(14, TypeNames.STRING);// crf_name
            this.setTypeExpected(15, TypeNames.STRING);// item_name
            this.setTypeExpected(16, TypeNames.STRING);// value
            // YW <<
            this.setTypeExpected(17, TypeNames.INT);// item_data_id
            this.setTypeExpected(18, TypeNames.INT);// item_id
            // YW >>
            alist = this.select(digester.getQuery("findAllItemDataByPK"), variables);
        }

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            if ("subject".equalsIgnoreCase(entityName) || "studySub".equalsIgnoreCase(entityName)) {
                eb.setSubjectName((String) hm.get("label"));
                eb.setColumn((String) hm.get("column_name"));
            } else if ("eventCrf".equalsIgnoreCase(entityName)) {
                eb.setSubjectName((String) hm.get("label"));
                eb.setEventName((String) hm.get("sed_name"));
                eb.setEventStart((Date) hm.get("date_start"));
                eb.setCrfName((String) hm.get("crf_name"));
                eb.setColumn((String) hm.get("column_name"));
            } else if ("itemData".equalsIgnoreCase(entityName)) {
                eb.setSubjectName((String) hm.get("label"));
                eb.setEventName((String) hm.get("sed_name"));
                eb.setEventStart((Date) hm.get("date_start"));
                eb.setCrfName((String) hm.get("crf_name"));
                eb.setEntityName((String) hm.get("item_name"));
                eb.setEntityValue((String) hm.get("value"));
                // YW <<
                eb.setEntityId(((Integer) hm.get("item_data_id")).intValue());
                eb.setItemId(((Integer) hm.get("item_id")).intValue());
                // YW >>

            } else if ("studyEvent".equalsIgnoreCase(entityName)) {
                eb.setSubjectName((String) hm.get("label"));
                eb.setEventName((String) hm.get("sed_name"));
                eb.setEventStart((Date) hm.get("date_start"));
                eb.setColumn((String) hm.get("column_name"));
            }
            if (fetchMapping) {
                eb = findSingleMapping(eb);
            }
            al.add(eb);
        }
        return al;
    }

    public ArrayList findAllSubjectByStudy(StudyBean study) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        this.setTypeExpected(12, TypeNames.STRING);// column_name
        this.setTypeExpected(13, TypeNames.INT);// subject_id

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(study.getId()));
        variables.put(new Integer(2), new Integer(study.getId()));
        variables.put(new Integer(3), new Integer(study.getId()));

        alist = this.select(digester.getQuery("findAllSubjectByStudy"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setSubjectName((String) hm.get("label"));
            eb.setColumn((String) hm.get("column_name"));
            eb.setEntityId(((Integer) hm.get("subject_id")).intValue());
            al.add(eb);
        }
        return al;
    }

    public ArrayList<DiscrepancyNoteBean> findAllSubjectByStudyAndId(StudyBean study,int subjectId) {
           this.setTypesExpected();
           ArrayList alist = new ArrayList();
           this.setTypeExpected(11, TypeNames.STRING);// ss.label
           this.setTypeExpected(12, TypeNames.STRING);// column_name
           this.setTypeExpected(13, TypeNames.INT);// subject_id

           HashMap variables = new HashMap();
           variables.put(new Integer(1), new Integer(study.getId()));
           variables.put(new Integer(2), new Integer(study.getId()));
           variables.put(new Integer(3), new Integer(study.getId()));
           variables.put(new Integer(4), new Integer(subjectId));

           alist = this.select(digester.getQuery("findAllSubjectByStudyAndId"), variables);

           ArrayList al = new ArrayList();
           Iterator it = alist.iterator();
           while (it.hasNext()) {
               HashMap hm = (HashMap) it.next();
               DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
               eb.setSubjectName((String) hm.get("label"));
               eb.setColumn((String) hm.get("column_name"));
               eb.setEntityId(((Integer) hm.get("subject_id")).intValue());
               al.add(eb);
           }
           return al;
       }


    public ArrayList findAllStudySubjectByStudy(StudyBean study) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        this.setTypeExpected(12, TypeNames.STRING);// column_name
        this.setTypeExpected(13, TypeNames.INT);// study_subject_id

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(study.getId()));
        variables.put(new Integer(2), new Integer(study.getId()));

        alist = this.select(digester.getQuery("findAllStudySubjectByStudy"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setSubjectName((String) hm.get("label"));
            eb.setColumn((String) hm.get("column_name"));
            eb.setEntityId(((Integer) hm.get("study_subject_id")).intValue());
            al.add(eb);
        }
        return al;
    }

    public ArrayList<DiscrepancyNoteBean> findAllStudySubjectByStudyAndId(StudyBean study,int studySubjectId) {
            this.setTypesExpected();
            ArrayList alist = new ArrayList();
            this.setTypeExpected(11, TypeNames.STRING);// ss.label
            this.setTypeExpected(12, TypeNames.STRING);// column_name
            this.setTypeExpected(13, TypeNames.INT);// study_subject_id

            HashMap variables = new HashMap();
            variables.put(new Integer(1), new Integer(study.getId()));
            variables.put(new Integer(2), new Integer(study.getId()));
            variables.put(new Integer(3), new Integer(studySubjectId));

            alist = this.select(digester.getQuery("findAllStudySubjectByStudyAndId"), variables);

            ArrayList al = new ArrayList();
            Iterator it = alist.iterator();
            while (it.hasNext()) {
                HashMap hm = (HashMap) it.next();
                DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
                eb.setSubjectName((String) hm.get("label"));
                eb.setColumn((String) hm.get("column_name"));
                eb.setEntityId(((Integer) hm.get("study_subject_id")).intValue());
                al.add(eb);
            }
            return al;
        }

    public ArrayList<DiscrepancyNoteBean> findAllStudySubjectByStudiesAndStudySubjectId(
                   StudyBean currentStudy,
                   StudyBean subjectStudy,
                   int studySubjectId) {
                this.setTypesExpected();
                ArrayList alist = new ArrayList();
                this.setTypeExpected(11, TypeNames.STRING);// ss.label
                this.setTypeExpected(12, TypeNames.STRING);// column_name
                this.setTypeExpected(13, TypeNames.INT);// study_subject_id

                HashMap variables = new HashMap();
                variables.put(new Integer(1), new Integer(currentStudy.getId()));
                variables.put(new Integer(2), new Integer(subjectStudy.getId()));
                variables.put(new Integer(3), new Integer(subjectStudy.getId()));
                variables.put(new Integer(4), new Integer(studySubjectId));

                alist = this.select(digester.getQuery("findAllStudySubjectByStudiesAndStudySubjectId"), variables);

                ArrayList al = new ArrayList();
                Iterator it = alist.iterator();
                while (it.hasNext()) {
                    HashMap hm = (HashMap) it.next();
                    DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
                    eb.setSubjectName((String) hm.get("label"));
                    eb.setColumn((String) hm.get("column_name"));
                    eb.setEntityId(((Integer) hm.get("study_subject_id")).intValue());
                    al.add(eb);
                }
                return al;
            }


    public ArrayList<DiscrepancyNoteBean> findAllSubjectByStudiesAndSubjectId(
      StudyBean currentStudy,
      StudyBean subjectStudy, int studySubjectId) {
                this.setTypesExpected();
                ArrayList alist = new ArrayList();
                this.setTypeExpected(11, TypeNames.STRING);// ss.label
                this.setTypeExpected(12, TypeNames.STRING);// column_name
                this.setTypeExpected(13, TypeNames.INT);// subject_id

                HashMap variables = new HashMap();
                variables.put(new Integer(1), new Integer(currentStudy.getId()));
                variables.put(new Integer(2), new Integer(subjectStudy.getId()));
                variables.put(new Integer(3), new Integer(subjectStudy.getId()));
                variables.put(new Integer(4), new Integer(currentStudy.getId()));
                variables.put(new Integer(5), new Integer(subjectStudy.getId()));
                variables.put(new Integer(6), new Integer(studySubjectId));

                alist = this.select(digester.getQuery("findAllSubjectByStudiesAndSubjectId"), variables);

                ArrayList al = new ArrayList();
                Iterator it = alist.iterator();
                while (it.hasNext()) {
                    HashMap hm = (HashMap) it.next();
                    DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
                    eb.setSubjectName((String) hm.get("label"));
                    eb.setColumn((String) hm.get("column_name"));
                    eb.setEntityId(((Integer) hm.get("subject_id")).intValue());
                    al.add(eb);
                }
                return al;
            }


    public ArrayList findAllStudyEventByStudy(StudyBean study) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        this.setTypeExpected(12, TypeNames.DATE);// date_start
        this.setTypeExpected(13, TypeNames.STRING);// sed_name
        this.setTypeExpected(14, TypeNames.STRING);// column_name
        this.setTypeExpected(15, TypeNames.INT);// study_event_id

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(study.getId()));
        variables.put(new Integer(2), new Integer(study.getId()));
        alist = this.select(digester.getQuery("findAllStudyEventByStudy"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setSubjectName((String) hm.get("label"));
            eb.setEventName((String) hm.get("sed_name"));
            eb.setEventStart((Date) hm.get("date_start"));
            eb.setColumn((String) hm.get("column_name"));
            eb.setEntityId(((Integer) hm.get("study_event_id")).intValue());

            al.add(eb);
        }
        return al;
    }

    /**
     *  Find all DiscrepancyNoteBeans associated with a certain Study Subject and Study.
     * @param study  A StudyBean, whose id property is checked.
     * @param studySubjectId  The id of a Study Subject.
     * @return An ArrayList of DiscrepancyNoteBeans.
     */
    public ArrayList findAllStudyEventByStudyAndId(StudyBean study,int studySubjectId) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        this.setTypeExpected(12, TypeNames.DATE);// date_start
        this.setTypeExpected(13, TypeNames.STRING);// sed_name
        this.setTypeExpected(14, TypeNames.STRING);// column_name
        this.setTypeExpected(15, TypeNames.INT);// study_event_id

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(study.getId()));
        variables.put(new Integer(2), new Integer(study.getId()));
        variables.put(new Integer(3), new Integer(studySubjectId));
        alist = this.select(digester.getQuery("findAllStudyEventByStudyAndId"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setSubjectName((String) hm.get("label"));
            eb.setEventName((String) hm.get("sed_name"));
            eb.setEventStart((Date) hm.get("date_start"));
            eb.setColumn((String) hm.get("column_name"));
            eb.setEntityId(((Integer) hm.get("study_event_id")).intValue());

            al.add(eb);
        }
        return al;
    }

    public ArrayList findAllStudyEventByStudiesAndSubjectId(StudyBean currentStudy,
                                                            StudyBean subjectStudy,
                                                            int studySubjectId) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        this.setTypeExpected(12, TypeNames.DATE);// date_start
        this.setTypeExpected(13, TypeNames.STRING);// sed_name
        this.setTypeExpected(14, TypeNames.STRING);// column_name
        this.setTypeExpected(15, TypeNames.INT);// study_event_id

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(currentStudy.getId()));
        variables.put(new Integer(2), new Integer(subjectStudy.getId()));
        variables.put(new Integer(3), new Integer(currentStudy.getId()));
        variables.put(new Integer(4), new Integer(studySubjectId));
        alist = this.select(digester.getQuery("findAllStudyEventByStudiesAndSubjectId"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setSubjectName((String) hm.get("label"));
            eb.setEventName((String) hm.get("sed_name"));
            eb.setEventStart((Date) hm.get("date_start"));
            eb.setColumn((String) hm.get("column_name"));
            eb.setEntityId(((Integer) hm.get("study_event_id")).intValue());

            al.add(eb);
        }
        return al;
    }

    public ArrayList findAllEventCRFByStudy(StudyBean study) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        this.setTypeExpected(12, TypeNames.DATE);// date_start
        this.setTypeExpected(13, TypeNames.STRING);// sed_name
        this.setTypeExpected(14, TypeNames.STRING);// crf_name
        this.setTypeExpected(15, TypeNames.STRING);// column_name
        this.setTypeExpected(16, TypeNames.INT);// event_crf_id

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(study.getId()));
        variables.put(new Integer(2), new Integer(study.getId()));
        alist = this.select(digester.getQuery("findAllEventCRFByStudy"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setEventName((String) hm.get("sed_name"));
            eb.setEventStart((Date) hm.get("date_start"));
            eb.setCrfName((String) hm.get("crf_name"));
            eb.setSubjectName((String) hm.get("label"));
            eb.setColumn((String) hm.get("column_name"));
            eb.setEntityId(((Integer) hm.get("event_crf_id")).intValue());
            al.add(eb);
        }
        return al;
    }

    public ArrayList findAllEventCRFByStudyAndParent(StudyBean study,
                                                     DiscrepancyNoteBean parent) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        this.setTypeExpected(12, TypeNames.DATE);// date_start
        this.setTypeExpected(13, TypeNames.STRING);// sed_name
        this.setTypeExpected(14, TypeNames.STRING);// crf_name
        this.setTypeExpected(15, TypeNames.STRING);// column_name
        this.setTypeExpected(16, TypeNames.INT);// event_crf_id

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(study.getId()));
        variables.put(new Integer(2), new Integer(study.getId()));
        variables.put(new Integer(3), new Integer(parent.getId()));

        alist = this.select(digester.getQuery("findAllEventCRFByStudyAndParent"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setEventName((String) hm.get("sed_name"));
            eb.setEventStart((Date) hm.get("date_start"));
            eb.setCrfName((String) hm.get("crf_name"));
            eb.setSubjectName((String) hm.get("label"));
            eb.setColumn((String) hm.get("column_name"));
            eb.setEntityId(((Integer) hm.get("event_crf_id")).intValue());
            al.add(eb);
        }
        return al;
    }

    public ArrayList<DiscrepancyNoteBean> findItemDataDNotesFromEventCRF(
      EventCRFBean eventCRFBean) {

        this.setTypesExpected();
        ArrayList dNotelist = new ArrayList();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(eventCRFBean.getId()));
        dNotelist = this.select(digester.getQuery("findItemDataDNotesFromEventCRF"), variables);

        ArrayList<DiscrepancyNoteBean> returnedNotelist =
          new ArrayList<DiscrepancyNoteBean>();
        Iterator it = dNotelist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setEventCRFId(eventCRFBean.getId());
            returnedNotelist.add(eb);
        }
        return returnedNotelist;

    }

    public ArrayList<DiscrepancyNoteBean> findParentItemDataDNotesFromEventCRF(
      EventCRFBean eventCRFBean) {

        this.setTypesExpected();
        ArrayList dNotelist = new ArrayList();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(eventCRFBean.getId()));
        dNotelist = this.select(digester.getQuery("findParentItemDataDNotesFromEventCRF"), variables);

        ArrayList<DiscrepancyNoteBean> returnedNotelist =
          new ArrayList<DiscrepancyNoteBean>();
        Iterator it = dNotelist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setEventCRFId(eventCRFBean.getId());
            returnedNotelist.add(eb);
        }
        return returnedNotelist;

    }

    public ArrayList<DiscrepancyNoteBean> findEventCRFDNotesFromEventCRF(
      EventCRFBean eventCRFBean) {

        this.setTypesExpected();
        this.setTypeExpected(11, TypeNames.STRING);
        ArrayList dNotelist = new ArrayList();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(eventCRFBean.getId()));
        dNotelist = this.select(digester.getQuery("findEventCRFDNotesFromEventCRF"), variables);

        ArrayList<DiscrepancyNoteBean> returnedNotelist =
          new ArrayList<DiscrepancyNoteBean>();
        Iterator it = dNotelist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setColumn((String)hm.get("column_name"));
            eb.setEventCRFId(eventCRFBean.getId());
            returnedNotelist.add(eb);
        }
        return returnedNotelist;

    }

    public ArrayList findAllItemDataByStudy(StudyBean study) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        this.setTypeExpected(12, TypeNames.DATE);// date_start
        this.setTypeExpected(13, TypeNames.STRING);// sed_name
        this.setTypeExpected(14, TypeNames.STRING);// crf_name
        this.setTypeExpected(15, TypeNames.STRING);// item_name
        this.setTypeExpected(16, TypeNames.STRING);// value
        this.setTypeExpected(17, TypeNames.INT);// item_data_id
        this.setTypeExpected(18, TypeNames.INT);// item_id

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(study.getId()));
        variables.put(new Integer(2), new Integer(study.getId()));
        alist = this.select(digester.getQuery("findAllItemDataByStudy"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setEventName((String) hm.get("sed_name"));
            eb.setEventStart((Date) hm.get("date_start"));
            eb.setCrfName((String) hm.get("crf_name"));
            eb.setSubjectName((String) hm.get("label"));
            eb.setEntityName((String) hm.get("item_name"));
            eb.setEntityValue((String) hm.get("value"));
            // YW << change EntityId from item_id to item_data_id.
            eb.setEntityId(((Integer) hm.get("item_data_id")).intValue());
            eb.setItemId(((Integer) hm.get("item_id")).intValue());
            // YW >>
            al.add(eb);
        }
        return al;
    }

    public ArrayList findAllItemDataByStudyAndParent(StudyBean study,
                                                     DiscrepancyNoteBean parent) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        this.setTypeExpected(11, TypeNames.STRING);// ss.label
        this.setTypeExpected(12, TypeNames.DATE);// date_start
        this.setTypeExpected(13, TypeNames.STRING);// sed_name
        this.setTypeExpected(14, TypeNames.STRING);// crf_name
        this.setTypeExpected(15, TypeNames.STRING);// item_name
        this.setTypeExpected(16, TypeNames.STRING);// value
        this.setTypeExpected(17, TypeNames.INT);// item_data_id
        this.setTypeExpected(18, TypeNames.INT);// item_id

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(study.getId()));
        variables.put(new Integer(2), new Integer(study.getId()));
        variables.put(new Integer(3), new Integer(parent.getId()));
        alist = this.select(digester.getQuery("findAllItemDataByStudyAndParent"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setEventName((String) hm.get("sed_name"));
            eb.setEventStart((Date) hm.get("date_start"));
            eb.setCrfName((String) hm.get("crf_name"));
            eb.setSubjectName((String) hm.get("label"));
            eb.setEntityName((String) hm.get("item_name"));
            eb.setEntityValue((String) hm.get("value"));
            // YW << change EntityId from item_id to item_data_id.
            eb.setEntityId(((Integer) hm.get("item_data_id")).intValue());
            eb.setItemId(((Integer) hm.get("item_id")).intValue());
            // YW >>
            al.add(eb);
        }
        return al;
    }

    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public EntityBean findByPK(int ID) {
        DiscrepancyNoteBean eb = new DiscrepancyNoteBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(ID));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (DiscrepancyNoteBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        if (fetchMapping) {
            eb = findSingleMapping(eb);
        }

        return eb;
    }

    /**
     * Creates a new discrepancy note
     */
    public EntityBean create(EntityBean eb) {
        DiscrepancyNoteBean sb = (DiscrepancyNoteBean) eb;
        HashMap variables = new HashMap();
        HashMap nullVars = new HashMap();
        // INSERT INTO discrepancy_note
        // (description, discrepancy_note_type_id ,
        // resolution_status_id , detailed_notes , date_created,
        // owner_id, parent_dn_id)
        // VALUES (?,?,?,?,now(),?,?)
        variables.put(new Integer(1), sb.getDescription());
        variables.put(new Integer(2), new Integer(sb.getDiscrepancyNoteTypeId()));
        variables.put(new Integer(3), new Integer(sb.getResolutionStatusId()));
        variables.put(new Integer(4), sb.getDetailedNotes());

        variables.put(new Integer(5), new Integer(sb.getOwner().getId()));
        if (sb.getParentDnId() == 0) {
            nullVars.put(new Integer(6), new Integer(Types.INTEGER));
            variables.put(new Integer(6), null);
        } else {
            variables.put(new Integer(6), new Integer(sb.getParentDnId()));
        }
        variables.put(new Integer(7), sb.getEntityType());
        variables.put(new Integer(8), new Integer(sb.getStudyId()));

        this.executeWithPK(digester.getQuery("create"), variables, nullVars);
        if (isQuerySuccessful()) {
            sb.setId(getLatestPK());
        }

        return sb;
    }

    /**
     * Creates a new discrepancy note map
     */
    public void createMapping(DiscrepancyNoteBean eb) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(eb.getEntityId()));
        variables.put(new Integer(2), new Integer(eb.getId()));
        variables.put(new Integer(3), eb.getColumn());
        String entityType = eb.getEntityType();

        if ("subject".equalsIgnoreCase(entityType)) {
            this.execute(digester.getQuery("createSubjectMap"), variables);
        } else if ("studySub".equalsIgnoreCase(entityType)) {
            this.execute(digester.getQuery("createStudySubjectMap"), variables);
        } else if ("eventCrf".equalsIgnoreCase(entityType)) {
            this.execute(digester.getQuery("createEventCRFMap"), variables);
        } else if ("studyEvent".equalsIgnoreCase(entityType)) {
            this.execute(digester.getQuery("createStudyEventMap"), variables);
        } else if ("itemData".equalsIgnoreCase(entityType)) {
            this.execute(digester.getQuery("createItemDataMap"), variables);
        }

    }

    /**
     * Updates a Study event
     */
    public EntityBean update(EntityBean eb) {
        //update discrepancy_note set
        //description =?, 
        //discrepancy_note_type_id =? ,
        //resolution_status_id =? ,  
        //detailed_notes =?
        //where discrepancy_note_id=?   
        DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) eb;
        dnb.setActive(false);

        HashMap variables = new HashMap();

        variables.put(new Integer(1), dnb.getDescription());
        variables.put(new Integer(2), new Integer(dnb.getDiscrepancyNoteTypeId()));
        variables.put(new Integer(3), new Integer(dnb.getResolutionStatusId()));
        variables.put(new Integer(4), dnb.getDetailedNotes());
        variables.put(new Integer(5), new Integer(dnb.getId()));
        this.execute(digester.getQuery("update"), variables);

        if (isQuerySuccessful()) {
            dnb.setActive(true);
        }

        return dnb;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
        ArrayList al = new ArrayList();

        return al;
    }

    @Override
    public int getCurrentPK() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);

        int pk = 0;
        ArrayList al = select(digester.getQuery("getCurrentPrimaryKey"));

        if (al.size() > 0) {
            HashMap h = (HashMap) al.get(0);
            pk = ((Integer) h.get("key")).intValue();
        }

        return pk;
    }

    public ArrayList findAllByParent(DiscrepancyNoteBean parent) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(parent.getId()));

        return this.executeFindAllQuery("findAllByParent", variables);
    }

    private DiscrepancyNoteBean findSingleMapping(DiscrepancyNoteBean note) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(note.getId()));

        setMapTypesExpected();
        String entityType = note.getEntityType();
        String sql = "";
        if ("subject".equalsIgnoreCase(entityType)) {
            sql = digester.getQuery("findSubjectMapByDNId");
        } else if ("studySub".equalsIgnoreCase(entityType)) {
            sql = digester.getQuery("findStudySubjectMapByDNId");
        } else if ("eventCrf".equalsIgnoreCase(entityType)) {
            sql = digester.getQuery("findEventCRFMapByDNId");
        } else if ("studyEvent".equalsIgnoreCase(entityType)) {
            sql = digester.getQuery("findStudyEventMapByDNId");
        } else if ("itemData".equalsIgnoreCase(entityType)) {
            sql = digester.getQuery("findItemDataMapByDNId");
        }

        ArrayList hms = select(sql, variables);

        if (hms.size() > 0) {
            HashMap hm = (HashMap) hms.get(0);
            note = getMappingFromHashMap(hm, note);
        }

        return note;
    }



    private DiscrepancyNoteBean getMappingFromHashMap(HashMap hm, DiscrepancyNoteBean note) {
        String entityType = note.getEntityType();
        String entityIDColumn = getEntityIDColumn(entityType);

        if (!entityIDColumn.equals("")) {
            note.setEntityId(selectInt(hm, entityIDColumn));
        }
        note.setColumn(selectString(hm, "column_name"));
        return note;
    }

    public static String getEntityIDColumn(String entityType) {
        String entityIDColumn = "";
        if ("subject".equalsIgnoreCase(entityType)) {
            entityIDColumn = "subject_id";
        } else if ("studySub".equalsIgnoreCase(entityType)) {
            entityIDColumn = "study_subject_id";
        } else if ("eventCrf".equalsIgnoreCase(entityType)) {
            entityIDColumn = "event_crf_id";
        } else if ("studyEvent".equalsIgnoreCase(entityType)) {
            entityIDColumn = "study_event_id";
        } else if ("itemData".equalsIgnoreCase(entityType)) {
            entityIDColumn = "item_data_id";
        }
        return entityIDColumn;
    }

    public AuditableEntityBean findEntity(DiscrepancyNoteBean note) {
        AuditableEntityDAO aedao = getAEDAO(note, ds);

        try {
            if (aedao != null) {
                AuditableEntityBean aeb = (AuditableEntityBean) aedao.findByPK(note.getEntityId());
                return aeb;
            }
        } catch (Exception e) {
        }

        return null;
    }

    public static AuditableEntityDAO getAEDAO(DiscrepancyNoteBean note, DataSource ds) {
        String entityType = note.getEntityType();
        if ("subject".equalsIgnoreCase(entityType)) {
            return new SubjectDAO(ds);
        } else if ("studySub".equalsIgnoreCase(entityType)) {
            return new StudySubjectDAO(ds);
        } else if ("eventCrf".equalsIgnoreCase(entityType)) {
            return new EventCRFDAO(ds);
        } else if ("studyEvent".equalsIgnoreCase(entityType)) {
            return new StudyEventDAO(ds);
        } else if ("itemData".equalsIgnoreCase(entityType)) {
            return new ItemDataDAO(ds);
        }

        return null;
    }

    public int findNumExistingNotesForItem(int itemDataId) {
        unsetTypeExpected();
        setTypeExpected(1, TypeNames.INT);

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(itemDataId));
        String sql = digester.getQuery("findNumExistingNotesForItem");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            try {
                Integer i = (Integer) hm.get("num");
                return i.intValue();
            } catch (Exception e) {
            }
        }

        return 0;
    }

    public ArrayList findExistingNotesForItemData(int itemDataId) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(itemDataId));
        alist = this.select(digester.getQuery("findExistingNotesForItemData"), variables);
        ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            al.add(eb);
        }
        return al;

    }

    public ArrayList<DiscrepancyNoteBean> findAllTopNotesByEventCRF(int eventCRFId) {
        this.setTypesExpected();
        ArrayList alist = new ArrayList();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(eventCRFId));
        alist = this.select(digester.getQuery("findAllTopNotesByEventCRF"), variables);
        ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            al.add(eb);
        }
        return al;
    }

    public ArrayList<DiscrepancyNoteBean>
    findOnlyParentEventCRFDNotesFromEventCRF(EventCRFBean eventCRFBean) {
        this.setTypesExpected();
        this.setTypeExpected(11, TypeNames.STRING);
        ArrayList dNotelist = new ArrayList();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(eventCRFBean.getId()));
        dNotelist = this.select(digester.getQuery("findOnlyParentEventCRFDNotesFromEventCRF"), variables);

        ArrayList<DiscrepancyNoteBean> returnedNotelist =
          new ArrayList<DiscrepancyNoteBean>();
        Iterator it = dNotelist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
            eb.setColumn((String)hm.get("column_name"));
            eb.setEventCRFId(eventCRFBean.getId());
            returnedNotelist.add(eb);
        }
        return returnedNotelist;
    }
}