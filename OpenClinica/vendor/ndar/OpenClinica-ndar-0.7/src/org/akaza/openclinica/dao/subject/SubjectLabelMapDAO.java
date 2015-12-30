package org.akaza.openclinica.dao.subject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.subject.SubjectLabelMapBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;

public class SubjectLabelMapDAO extends AuditableEntityDAO {
    public SubjectLabelMapDAO(DataSource ds) {
        super(ds);
    }

    public SubjectLabelMapDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
    }

    @Override
    public void setTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.INT);
        this.setTypeExpected(3, TypeNames.INT);
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_SUBJECT_LABEL_MAP;
    }

    public EntityBean create(EntityBean eb) throws OpenClinicaException {
        SubjectLabelMapBean slmb = (SubjectLabelMapBean) eb;
        //per the create sql statement
        HashMap variables = new HashMap();
        variables.put(new Integer(1), slmb.getSubjectId());
        variables.put(new Integer(2), slmb.getSubjectEntryLabelId());
        this.execute(digester.getQuery("create"), variables);
        return eb;
    }

    public Collection findAll(String strOrderByColumn,
            boolean blnAscendingSort, String strSearchPhrase)
            throws OpenClinicaException {
        return null;
    }

    public Collection findAll() {
        this.setTypesExpected();
        ArrayList alist = this.select(digester.getQuery("findAll"));
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            SubjectLabelMapBean slmb = (SubjectLabelMapBean) this
                    .getEntityFromHashMap((HashMap) it.next());
            al.add(slmb);
        }
        return al;
    }

    public Collection findAllByPermission(Object objCurrentUser,
            int intActionType, String strOrderByColumn,
            boolean blnAscendingSort, String strSearchPhrase)
            throws OpenClinicaException {
        return null;
    }

    public Collection findAllByPermission(Object objCurrentUser,
            int intActionType) throws OpenClinicaException {
        return null;
    }

    public EntityBean findByPK(int id) throws OpenClinicaException {
        SubjectLabelMapBean slmb = new SubjectLabelMapBean();
        this.setTypesExpected();

        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), new Integer(id));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            slmb = (SubjectLabelMapBean) this
                    .getEntityFromHashMap((HashMap) it.next());
        }

        return slmb;
    }

    public Object getEntityFromHashMap(HashMap hm) {
        SubjectLabelMapBean slmb = new SubjectLabelMapBean();

        slmb.setId(((Integer) hm.get("subject_subjectentrylabel_id")).intValue());
        slmb.setSubjectId(((Integer)hm.get("subject_id")).intValue());
        slmb.setSubjectEntryLabelId(((Integer) hm.get("subjectentrylabel_id")).intValue());
        return slmb;
    }

    public EntityBean update(EntityBean eb) throws OpenClinicaException {
        SubjectLabelMapBean slmb = (SubjectLabelMapBean) eb;
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), slmb.getSubjectId());
        variables.put(new Integer(2), slmb.getSubjectEntryLabelId());
        variables.put(new Integer(3), slmb.getId());
        this.execute(digester.getQuery("update"), variables);
        return eb;
    }

    public void delete(SubjectLabelMapBean slmb) {
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), slmb.getId());
        this.execute(digester.getQuery("delete"), variables);
    }
    
    public List<SubjectLabelMapBean> findAllBySubjectId(int subjectId){
        this.setTypesExpected();
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), subjectId);
        
        String sql = digester.getQuery("findAllBySubjectId");
        ArrayList alist = this.select(sql, variables);
        ArrayList<SubjectLabelMapBean> al = new ArrayList<SubjectLabelMapBean>();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            SubjectLabelMapBean slmb = (SubjectLabelMapBean) this
                    .getEntityFromHashMap((HashMap) it.next());
            al.add(slmb);
        }
        return al;
    }
    public List<SubjectLabelMapBean> findAllBySubjectEntryLabelId(int subjectEntryLabelId){
        this.setTypesExpected();
        String sql = digester.getQuery("findAllBySubjectEntryLabelId");
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), subjectEntryLabelId);
        ArrayList alist = this.select(sql, variables);
        ArrayList<SubjectLabelMapBean> al = new ArrayList<SubjectLabelMapBean>();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            SubjectLabelMapBean slmb = (SubjectLabelMapBean) this
                    .getEntityFromHashMap((HashMap) it.next());
            al.add(slmb);
        }
        return al;
    }
}
