/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;

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
 */
public class StudySubjectDAO extends AuditableEntityDAO {


    // private DAODigester digester;

    public void setQueryNames() {
        findAllByStudyName = "findAllByStudy";
        findByPKAndStudyName = "findByPKAndStudy";
        getCurrentPKName = "getCurrentPK";

    }

    public StudySubjectDAO(DataSource ds) {
        super(ds);
        // digester = SQLFactory.getInstance().getDigester(digesterName);
        setQueryNames();
    }

    public StudySubjectDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
        setQueryNames();
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_STUDYSUBJECT;
    }

    @Override
    public void setTypesExpected() {
        // study_subject_id | integer | not null default
        // nextval('public.study_subject_study_subject_id_seq'::text)
        // label | character varying(30) |
        // secondary_label | character varying(30) |
        // subject_id | numeric |
        // study_id | numeric |
        // status_id | numeric |
        // enrollment_date | date |
        // date_created | date |
        // date_updated | date |
        // owner_id | numeric |
        // update_id | numeric |

        this.unsetTypeExpected();
        int ind = 1;
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // study_subject_id
        this.setTypeExpected(ind, TypeNames.STRING);
        ind++; // label
        this.setTypeExpected(ind, TypeNames.STRING);
        ind++; // secondary_label
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // subject_id
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // study_id
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // status_id

        this.setTypeExpected(ind, TypeNames.DATE);
        ind++; // enrollment_date
        this.setTypeExpected(ind, TypeNames.DATE);
        ind++; // date_created
        this.setTypeExpected(ind, TypeNames.DATE);
        ind++; // date_updated
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // owner_id
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // update_id
        this.setTypeExpected(ind, TypeNames.STRING);
        ind++; // oc oid

    }

    /**
     * <p>
     * getEntityFromHashMap, the method that gets the object from the database
     * query.
     */
    public Object getEntityFromHashMap(HashMap hm) {
        StudySubjectBean eb = new StudySubjectBean();
        super.setEntityAuditInformation(eb, hm);
        // STUDY_SUBJECT_ID, LABEL, SUBJECT_ID, STUDY_ID
        // STATUS_ID, DATE_CREATED, OWNER_ID, STUDY_GROUP_ID
        // DATE_UPDATED, UPDATE_ID
        Integer ssid = (Integer) hm.get("study_subject_id");
        eb.setId(ssid.intValue());

        eb.setLabel((String) hm.get("label"));
        eb.setSubjectId(((Integer) hm.get("subject_id")).intValue());
        eb.setStudyId(((Integer) hm.get("study_id")).intValue());
        // eb.setStudyGroupId(((Integer) hm.get("study_group_id")).intValue());
        eb.setEnrollmentDate((Date) hm.get("enrollment_date"));
        eb.setSecondaryLabel((String) hm.get("secondary_label"));
        eb.setOid((String) hm.get("oc_oid"));
        return eb;
    }

    public Collection findAll() {
        this.setTypesExpected();
        String sql = digester.getQuery("findAll");
        ArrayList alist = this.select(sql);
        ArrayList answer = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            StudySubjectBean eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
            answer.add(eb);
        }
        return answer;
    }


    public int findTheGreatestLabel() {
        this.setTypesExpected();
        String sql = digester.getQuery("findAll");
        ArrayList alist = this.select(sql);
        ArrayList answer = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            StudySubjectBean eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
            answer.add(eb);
        }

        int greatestLabel = 0;
        for (int i = 0; i < answer.size(); i++) {
            StudySubjectBean sb = (StudySubjectBean) answer.get(i);
            int labelInt = 0;
            try {
                labelInt = Integer.parseInt(sb.getLabel());
            } catch (NumberFormatException ne) {
                labelInt = 0;
            }
            if (labelInt > greatestLabel) {
                greatestLabel = labelInt;
            }
        }
        return greatestLabel;
    }

    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public ArrayList findAllByStudyOrderByLabel(StudyBean sb) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(sb.getId()));
        variables.put(new Integer(2), new Integer(sb.getId()));

        return executeFindAllQuery("findAllByStudyOrderByLabel", variables);
    }

    public ArrayList findAllActiveByStudyOrderByLabel(StudyBean sb) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(sb.getId()));
        variables.put(new Integer(2), new Integer(sb.getId()));

        return executeFindAllQuery("findAllActiveByStudyOrderByLabel", variables);
    }

    public ArrayList findAllWithStudyEvent(StudyBean currentStudy) {
        ArrayList answer = new ArrayList();

        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(currentStudy.getId()));
        variables.put(new Integer(2), new Integer(currentStudy.getId()));

        String sql = digester.getQuery("findAllWithStudyEvent");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        while (it.hasNext()) {
            StudySubjectBean ssb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
            answer.add(ssb);
        }

        return answer;
    }

    public ArrayList findAllBySubjectId(int subjectId) {
        ArrayList answer = new ArrayList();

        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(subjectId));

        String sql = digester.getQuery("findAllBySubjectId");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        while (it.hasNext()) {
            StudySubjectBean ssb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
            answer.add(ssb);
        }

        return answer;
    }

    public EntityBean findAnotherBySameLabel(String label, int studyId, int studySubjectId) {
        StudySubjectBean eb = new StudySubjectBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), label);
        variables.put(new Integer(2), new Integer(studyId));
        variables.put(new Integer(3), new Integer(studySubjectId));

        String sql = digester.getQuery("findAnotherBySameLabel");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return eb;
    }

    public EntityBean findAnotherBySameLabelInSites(String label, int studyId, int studySubjectId) {
        StudySubjectBean eb = new StudySubjectBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), label);
        variables.put(new Integer(2), new Integer(studyId));
        variables.put(new Integer(3), new Integer(studySubjectId));

        String sql = digester.getQuery("findAnotherBySameLabelInSites");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return eb;
    }

    public EntityBean findByPK(int ID) {
        StudySubjectBean eb = new StudySubjectBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(ID));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return eb;
    }

    public StudySubjectBean findByLabelAndStudy(String label, StudyBean study) {
        StudySubjectBean answer = new StudySubjectBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), label);
        variables.put(new Integer(2), new Integer(study.getId()));
        variables.put(new Integer(3), new Integer(study.getId()));

        String sql = digester.getQuery("findByLabelAndStudy");

        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            answer = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return answer;
    }

    /**
     * Finds a study subject which has the same label provided in the same study
     *
     * @param label
     * @param studyId
     * @param id
     * @return
     */
    public StudySubjectBean findSameByLabelAndStudy(String label, int studyId, int id) {
        StudySubjectBean answer = new StudySubjectBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), label);
        variables.put(new Integer(2), new Integer(studyId));
        variables.put(new Integer(3), new Integer(studyId));
        variables.put(new Integer(4), new Integer(id));

        String sql = digester.getQuery("findSameByLabelAndStudy");

        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            answer = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return answer;
    }

    /**
     * @deprecated Creates a new studysubject
     */
    @Deprecated
    public EntityBean create(EntityBean eb) {
        StudySubjectBean sb = (StudySubjectBean) eb;
        HashMap variables = new HashMap();
        HashMap nullVars = new HashMap();

        // INSERT INTO study_subject
        // (LABEL, SUBJECT_ID, STUDY_ID, STATUS_ID,
        // DATE_CREATED, OWNER_ID, ENROLLMENT_DATE,SECONDARY_LABEL)
        // VALUES (?,?,?,?,NOW(),?,?,?)

        int ind = 1;
        variables.put(new Integer(ind), sb.getLabel());
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getSubjectId()));
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getStudyId()));
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getStatus().getId()));
        ind++;
        // Date_created is now()
        variables.put(new Integer(ind), new Integer(sb.getOwnerId()));
        ind++;
        // variables.put(new Integer(ind), new Integer(sb.getStudyGroupId()));
        // ind++;
        if (sb.getEnrollmentDate() == null) {
            nullVars.put(new Integer(ind), new Integer(Types.DATE));
            variables.put(new Integer(ind), null);
            ind++;
        } else {
            variables.put(new Integer(ind), sb.getEnrollmentDate());
            ind++;
        }
        variables.put(new Integer(ind), sb.getSecondaryLabel());
        ind++;

        this.execute(digester.getQuery("create"), variables, nullVars);

        if (isQuerySuccessful()) {
            sb.setId(getCurrentPK());
        }

        return sb;
    }

    /**
     * Create a study subject (that is, enroll a subject in a study).
     *
     * @param sb
     *            The study subject to create.
     * @param withGroup
     *            <code>true</code> if the group id has been set (primarily
     *            for use with genetic studies); <code>false</false> otherwise.
     * @return The study subject with id set to the insert id if the operation was successful, or 0 otherwise.
     */
    public StudySubjectBean create(StudySubjectBean sb, boolean withGroup) {
        HashMap variables = new HashMap();
        HashMap nullVars = new HashMap();

        int ind = 1;
        variables.put(new Integer(ind), sb.getLabel());
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getSubjectId()));
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getStudyId()));
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getStatus().getId()));
        ind++;
        // Date_created is now()
        variables.put(new Integer(ind), new Integer(sb.getOwner().getId()));
        ind++;

        // if (withGroup) {
        // variables.put(new Integer(ind), new Integer(sb.getStudyGroupId()));
        // ind++;
        // } else {
        // nullVars.put(new Integer(ind), new Integer(TypeNames.INT));
        // variables.put(new Integer(ind), null);
        // ind++;
        // }

        Date enrollmentDate = sb.getEnrollmentDate();
        if (enrollmentDate == null) {
            nullVars.put(new Integer(ind), new Integer(Types.DATE));
            variables.put(new Integer(ind), null);
            ind++;
        } else {
            variables.put(new Integer(ind), enrollmentDate);
            ind++;
        }

        variables.put(new Integer(ind), sb.getSecondaryLabel());
        ind++;
        
        variables.put(new Integer(ind), getValidOid(sb));
        ind++;

        this.executeWithPK(digester.getQuery("create"), variables, nullVars);
        if (isQuerySuccessful()) {
            sb.setId(getLatestPK());
        }

        SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(ds);
        ArrayList groupMaps = sb.getStudyGroupMaps();
        for (int i = 0; i < groupMaps.size(); i++) {
            SubjectGroupMapBean sgmb = (SubjectGroupMapBean) groupMaps.get(i);
            sgmb = (SubjectGroupMapBean) sgmdao.create(sgmb);
            if (sgmdao.isQuerySuccessful()) {
                sgmb.setId(sgmdao.getCurrentPK());
            }
        }

        return sb;
    }

    public StudySubjectBean createWithGroup(StudySubjectBean sb) {
        return create(sb, true);
    }

    public StudySubjectBean createWithoutGroup(StudySubjectBean sb) {
        return create(sb, false);
    }
    
    /**
     * Creates a valid OID for the StudySubject
     */
    private String getOid(StudySubjectBean ssb) {

        String oid;
        try {
            oid = ssb.getOid() != null ? ssb.getOid() : ssb.getOidGenerator().generateOid(ssb.getLabel());
            return oid;
        } catch (Exception e) {
            throw new RuntimeException("CANNOT GENERATE OID");
        }
    }

    private String getValidOid(StudySubjectBean ssb) {

        String oid = getOid(ssb);
        logger.info(oid);
        String oidPreRandomization = oid;
        while (findByOidAndStudy(oid, ssb.getStudyId()) != null) {
            oid = ssb.getOidGenerator().randomizeOid(oidPreRandomization);
        }
        return oid;

    }
    
    public StudySubjectBean findByOidAndStudy(String oid, int studyId) {
        StudySubjectBean studySubjectBean = new StudySubjectBean();
        setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), oid);
        variables.put(new Integer(2), new Integer(studyId));
        variables.put(new Integer(3), new Integer(studyId));
        String sql = digester.getQuery("findByOidAndStudy");

        ArrayList rows = this.select(sql, variables);
        Iterator it = rows.iterator();

        if (it.hasNext()) {
            studySubjectBean = (StudySubjectBean) this.getEntityFromHashMap((HashMap) it.next());
            return studySubjectBean;
        } else {
            return null;
        }
    }

    /**
     * Updates a StudySubject
     */
    public EntityBean update(EntityBean eb) {
        StudySubjectBean sb = (StudySubjectBean) eb;
        HashMap variables = new HashMap();
        HashMap nullVars = new HashMap();

        // UPDATE study_subject SET LABEL=?, SUBJECT_ID=?, STUDY_ID=?,
        // STATUS_ID=?, ENROLLMENT_DATE=?, DATE_UPDATED=?,
        // UPDATE_ID=?, SECONDARY_LABEL=? WHERE STUDY_SUBJECT_ID=?
        int ind = 1;
        variables.put(new Integer(ind), sb.getLabel());
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getSubjectId()));
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getStudyId()));
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getStatus().getId()));
        ind++;
        Date enrollmentDate = sb.getEnrollmentDate();
        if (enrollmentDate == null) {
            nullVars.put(new Integer(ind), new Integer(Types.DATE));
            variables.put(new Integer(ind), null);
            ind++;
        } else {
            variables.put(new Integer(ind), enrollmentDate);
            ind++;
        }

        variables.put(new Integer(ind), new java.util.Date());
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getUpdater().getId()));
        ind++;
        variables.put(new Integer(ind), sb.getSecondaryLabel());
        ind++;
        variables.put(new Integer(ind), new Integer(sb.getId()));
        ind++;

        String sql = digester.getQuery("update");
        this.execute(sql, variables, nullVars);

        return sb;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
        ArrayList al = new ArrayList();

        return al;
    }

    public StudySubjectBean findBySubjectIdAndStudy(int subjectId, StudyBean study) {
        StudySubjectBean answer = new StudySubjectBean();

        this.unsetTypeExpected();
        setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(subjectId));
        variables.put(new Integer(2), new Integer(study.getId()));
        variables.put(new Integer(3), new Integer(study.getId()));

        String sql = digester.getQuery("findBySubjectIdAndStudy");

        ArrayList results = select(sql, variables);
        if (results.size() > 0) {
            // System.out.println("result size is >0");
            HashMap row = (HashMap) results.get(0);
            answer = (StudySubjectBean) getEntityFromHashMap(row);
        }

        return answer;
    }

    public ArrayList findAllByStudyId(int studyId) {
        return findAllByStudyIdAndLimit(studyId, false);
    }

    public ArrayList findAllByStudyIdAndLimit(int studyId, boolean isLimited) {
        ArrayList answer = new ArrayList();

        this.setTypesExpected();
        int ind = 1;
        this.setTypeExpected(ind, TypeNames.STRING);
        ind++; // unique_identifier
        this.setTypeExpected(ind, TypeNames.CHAR);
        ind++; // gender
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // study_subject_id
        this.setTypeExpected(ind, TypeNames.STRING);
        ind++; // label
        this.setTypeExpected(ind, TypeNames.STRING);
        ind++; // secondary_label
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // subject_id
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // study_id
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // status_id

        this.setTypeExpected(ind, TypeNames.DATE);
        ind++; // enrollment_date
        this.setTypeExpected(ind, TypeNames.DATE);
        ind++; // date_created
        this.setTypeExpected(ind, TypeNames.DATE);
        ind++; // date_updated
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // owner_id
        this.setTypeExpected(ind, TypeNames.INT);
        ind++; // update_id
        this.setTypeExpected(ind, TypeNames.STRING);
        ind++; // secondary_label
        this.setTypeExpected(ind, TypeNames.STRING);
        ind++; // studyName

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyId));
        variables.put(new Integer(2), new Integer(studyId));

        String sql = null;
        if (isLimited) {
            sql = digester.getQuery("findAllByStudyIdAndLimit");
        } else {
            sql = digester.getQuery("findAllByStudyId");
        }
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            StudySubjectBean ssb = (StudySubjectBean) this.getEntityFromHashMap(hm);
            ssb.setUniqueIdentifier((String) hm.get("unique_identifier"));
            ssb.setStudyName((String) hm.get("name"));
            // System.out.println("gender here:" + hm.get("gender").getClass());
            try {
                if (hm.get("gender") == null || ((String) hm.get("gender")).equals(" ")) {
                    System.out.println("here");
                    ssb.setGender(' ');

                } else {
                    String gender = (String) hm.get("gender");
                    char[] genderarr = gender.toCharArray();
                    ssb.setGender(genderarr[0]);
                }
            } catch (ClassCastException ce) {
                // object type is Character
                ssb.setGender(' ');
            }

            answer.add(ssb);
        }

        return answer;
    }
}