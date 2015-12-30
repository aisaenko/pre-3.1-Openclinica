/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research
 */
package org.akaza.openclinica.dao.ws;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.rule.RuleSetAuditBean;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.ws.SubjectTransferBean;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

public class SubjectTransferDAO extends EntityDAO {

    UserAccountDAO userAccountDao;

    public SubjectTransferDAO(DataSource ds) {
        super(ds);
        this.getCurrentPKName = "getCurrentPrimaryKey";
        // this.getNextPKName = "getNextPK";
    }

    @Override
    public int getCurrentPK() {
        int answer = 0;

        if (getCurrentPKName == null) {
            return answer;
        }

        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);

        ArrayList al = select(digester.getQuery(getCurrentPKName));

        if (al.size() > 0) {
            HashMap h = (HashMap) al.get(0);
            answer = ((Integer) h.get("key")).intValue();
        }

        return answer;
    }

    private UserAccountDAO getUserAccountDao() {
        return this.userAccountDao != null ? this.userAccountDao : new UserAccountDAO(ds);
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_SUBJECTTRANSFER;
    }

    public void setTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT); // subject_transfer_id
        this.setTypeExpected(2, TypeNames.STRING); // grid_id
        this.setTypeExpected(3, TypeNames.STRING); // personId
        this.setTypeExpected(4, TypeNames.STRING); // study_subject_id
        this.setTypeExpected(5, TypeNames.STRING); // secondary_id
        this.setTypeExpected(6, TypeNames.DATE); // enrollment_date
        this.setTypeExpected(7, TypeNames.DATE); // dateOfBirth
        this.setTypeExpected(8, TypeNames.CHAR); // gender
        this.setTypeExpected(9, TypeNames.STRING); // study_oid
        this.setTypeExpected(10, TypeNames.INT); // owner_id
        this.setTypeExpected(11, TypeNames.TIMESTAMP); // datetime_received

    }

    public Object getEntityFromHashMap(HashMap hm) {
        SubjectTransferBean subjectTransferBean = new SubjectTransferBean();
        subjectTransferBean.setId((Integer) hm.get("subject_transfer_id"));
        subjectTransferBean.setGridId((String) hm.get("grid_id"));
        subjectTransferBean.setPersonId((String) hm.get("person_id"));
        subjectTransferBean.setStudySubjectId((String) hm.get("study_subject_id"));
        subjectTransferBean.setSecondaryId((String) hm.get("secondary_id"));
        subjectTransferBean.setEnrollmentDate((Date) hm.get("enrollment_date"));
        subjectTransferBean.setDateOfBirth((Date) hm.get("date_of_birth"));
        try {
            String gender = (String) hm.get("gender");
            char[] genderarr = gender.toCharArray();
            subjectTransferBean.setGender(genderarr[0]);
        } catch (ClassCastException ce) {
            subjectTransferBean.setGender(' ');
        }
        subjectTransferBean.setOwner((UserAccountBean)
        getUserAccountDao().findByPK((Integer) hm.get("owner_id")));
        subjectTransferBean.setStudyOid((String) hm.get("study_oid"));
        subjectTransferBean.setOwnerId((Integer) hm.get("owner_id"));
        subjectTransferBean.setDatetimeReceived((Timestamp) hm.get("datetime_received"));

        return subjectTransferBean;
    }

    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
        return new ArrayList();
    }

    public Collection findAll() throws OpenClinicaException {
        this.setTypesExpected();
        ArrayList<?> alist = this.select(digester.getQuery("findAll"));
        ArrayList<SubjectTransferBean> al = new ArrayList<SubjectTransferBean>();
        Iterator<?> it = alist.iterator();
        while (it.hasNext()) {
            SubjectTransferBean tf = (SubjectTransferBean) this.getEntityFromHashMap((HashMap<?, ?>) it.next());
            al.add(tf);
        }
        return al;
    }

    public EntityBean findByPK(int id) throws OpenClinicaException {
        RuleSetAuditBean ruleSetAudit = null;

        this.setTypesExpected();
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), id);

        String sql = digester.getQuery("findByPK");
        ArrayList<?> alist = this.select(sql, variables);

        Iterator<?> it = alist.iterator();

        if (it.hasNext()) {
            ruleSetAudit = (RuleSetAuditBean) this.getEntityFromHashMap((HashMap<?, ?>) it.next());
        }
        return ruleSetAudit;
    }

    public boolean isFoundByGridId(String gridId) {
        boolean isFound = false;
        this.setTypesExpected();
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), gridId);
        String sql = digester.getQuery("countOneByGridId");
        ArrayList<?> al = this.select(sql, variables);
        if (al.size() > 0) {
            HashMap<?, ?> h = (HashMap<?, ?>) al.get(0);
            int n = ((Integer) h.get("count")).intValue();
            isFound = n > 0 ? true : false;
        }
        return isFound;
    }

    public ArrayList<RuleSetAuditBean> findAllByRuleSet(RuleSetBean ruleSet) {
        ArrayList<RuleSetAuditBean> ruleSetAuditBeans = new ArrayList<RuleSetAuditBean>();

        this.setTypesExpected();
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), ruleSet.getId());

        String sql = digester.getQuery("findAllByRuleSet");
        ArrayList<?> alist = this.select(sql, variables);
        Iterator<?> it = alist.iterator();

        while (it.hasNext()) {
            RuleSetAuditBean ruleSetAudit = (RuleSetAuditBean) this.getEntityFromHashMap((HashMap<?, ?>) it.next());
            ruleSetAuditBeans.add(ruleSetAudit);
        }
        return ruleSetAuditBeans;
    }

    public EntityBean create(EntityBean eb, UserAccountBean ub) {
        // INSERT INTO subject_transfer
        // (grid_id, person_id, study_subject_id, secondary_id, enrollment_date,
        // date_of_birth, gender, study_oid, ownder_id, date_received)
        // VALUES (?,?,?,?,?,?,?,?,?,?)
        SubjectTransferBean subjectTransferBean = (SubjectTransferBean) eb;
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();

        variables.put(1, subjectTransferBean.getGridId());
        variables.put(2, subjectTransferBean.getPersonId());
        variables.put(3, subjectTransferBean.getStudySubjectId());
        variables.put(4, subjectTransferBean.getSecondaryId());
        variables.put(5, subjectTransferBean.getEnrollmentDate());
        if (subjectTransferBean.getDateOfBirth() == null) {
            nullVars.put(new Integer(6), new Integer(Types.DATE));
            variables.put(new Integer(6), null);
        } else {
            variables.put(new Integer(6), subjectTransferBean.getDateOfBirth());
        }
        if (subjectTransferBean.getGender() != 'm' && subjectTransferBean.getGender() != 'f') {
            nullVars.put(new Integer(7), new Integer(Types.CHAR));
            variables.put(new Integer(7), null);
        } else {
            char[] ch = { subjectTransferBean.getGender() };
            variables.put(new Integer(7), new String(ch));
        }
        variables.put(8, subjectTransferBean.getStudyOid());
        variables.put(9, new Integer(subjectTransferBean.getOwnerId()));
        variables.put(10, subjectTransferBean.getDatetimeReceived());

        executeWithPK(digester.getQuery("create"), variables, nullVars);
        if (isQuerySuccessful()) {
            subjectTransferBean.setId(getLatestPK());
        }
        return subjectTransferBean;
    }

    public EntityBean create(EntityBean eb) throws OpenClinicaException {
        SubjectTransferBean subjectTransferBean = (SubjectTransferBean) eb;
        return create(eb, subjectTransferBean.getOwner());
    }

    public EntityBean update(EntityBean eb) throws OpenClinicaException {
        return new ItemGroupMetadataBean(); // To change body of implemented

    }

    public void delete(String gridId) throws OpenClinicaException {
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(1, gridId);

        String sql = digester.getQuery("delete");
        this.execute(sql, variables);
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase)
            throws OpenClinicaException {
        return new ArrayList<RuleSetAuditBean>();
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType) throws OpenClinicaException {
        return new ArrayList<RuleSetAuditBean>();
    }

}
