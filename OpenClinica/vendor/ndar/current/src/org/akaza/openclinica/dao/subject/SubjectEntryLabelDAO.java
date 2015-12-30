package org.akaza.openclinica.dao.subject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.subject.SubjectEntryLabelBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;

public class SubjectEntryLabelDAO extends AuditableEntityDAO {
    public SubjectEntryLabelDAO(DataSource ds) {
        super(ds);
      }

      public SubjectEntryLabelDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
      }

    @Override
    public void setTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1,TypeNames.INT);
        this.setTypeExpected(2,TypeNames.STRING);
        this.setTypeExpected(3,TypeNames.STRING);
        this.setTypeExpected(4,TypeNames.INT);
        this.setTypeExpected(5,TypeNames.DATE);
        this.setTypeExpected(6,TypeNames.INT);
        this.setTypeExpected(7,TypeNames.DATE);
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_SUBJECT_ENTRY_LABEL;
    }

    public EntityBean create(EntityBean eb) throws OpenClinicaException {
        SubjectEntryLabelBean selb = (SubjectEntryLabelBean)eb;
        //per the create sql statement
        HashMap variables = new HashMap();
        variables.put(new Integer(1), selb.getName());
        variables.put(new Integer(2), selb.getDescription());
        variables.put(new Integer(3), selb.getOwnerId());
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
            SubjectEntryLabelBean selb = 
            (SubjectEntryLabelBean) this.getEntityFromHashMap((HashMap) it.next());
          al.add(selb);
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

    public SubjectEntryLabelBean findByLabel(String label){
        SubjectEntryLabelBean selb = new SubjectEntryLabelBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), label);

        String sql = digester.getQuery("findByLabel");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            selb = (SubjectEntryLabelBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return selb;
    }
    
    public EntityBean findByPK(int id) throws OpenClinicaException {
        SubjectEntryLabelBean selb = new SubjectEntryLabelBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(id));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            selb = (SubjectEntryLabelBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return selb;
    }

    public Object getEntityFromHashMap(HashMap hm) {
        SubjectEntryLabelBean selb = new SubjectEntryLabelBean();
        //below inserted to find out a class cast exception, tbh
        Date dateCreated = (Date) hm.get("date_created");
        Date dateUpdated = (Date) hm.get("date_updated");
        // Integer statusId = (Integer) hm.get("status_id");
        Integer ownerId = (Integer) hm.get("owner_id");
        Integer updateId = (Integer) hm.get("update_id");

        selb.setId(((Integer) hm.get("subjectentrylabel_id")).intValue());
        selb.setCreatedDate(dateCreated);
        selb.setUpdatedDate(dateUpdated);
        // selb.setStatus(Status.get(statusId.intValue()));
        selb.setOwnerId(ownerId.intValue());
        selb.setUpdaterId(updateId==null ? 0 : updateId.intValue());

        selb.setName((String)hm.get("label"));
        selb.setDescription((String)hm.get("description"));
        return selb;
    }

    public EntityBean update(EntityBean eb) throws OpenClinicaException {
        SubjectEntryLabelBean selb = (SubjectEntryLabelBean)eb;
        HashMap variables = new HashMap();
        variables.put(new Integer(1), selb.getName());
        variables.put(new Integer(2), selb.getDescription());
        variables.put(new Integer(3), selb.getUpdaterId());
        variables.put(new Integer(4), selb.getId());
        this.execute(digester.getQuery("update"), variables);
        return eb;
    }

    public void delete(SubjectEntryLabelBean selb) {
        HashMap variables = new HashMap();

        variables = new HashMap();
        variables.put(new Integer(1), selb.getId());
        this.execute(digester.getQuery("delete"), variables);
    }
}
