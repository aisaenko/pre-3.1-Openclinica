package org.akaza.openclinica.dao.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;

import org.akaza.openclinica.bean.logic.SkipRuleBean;

public class SkipRuleDAO extends AuditableEntityDAO {
    public SkipRuleDAO(DataSource ds){
        super(ds);
    }
    
    public SkipRuleDAO(DataSource ds, DAODigester digster){
        super(ds);
        this.digester = digster;
    }
    
    @Override
    public void setTypesExpected() {
        this.unsetTypeExpected();
        
        // rule_id
        this.setTypeExpected(1, TypeNames.INT);

        // name
        this.setTypeExpected(2, TypeNames.STRING);
        
        // instruction_text
        this.setTypeExpected(3, TypeNames.STRING);
        
        // condition
        this.setTypeExpected(4, TypeNames.STRING);
        
        // assignments
        this.setTypeExpected(5, TypeNames.STRING);
        
        // crf_version_id
        this.setTypeExpected(6, TypeNames.INT);
        
        // status_id
        this.setTypeExpected(7, TypeNames.INT);
        
        // owner_id
        this.setTypeExpected(8, TypeNames.INT);
        
        // date_created
        this.setTypeExpected(9, TypeNames.DATE);
        
        // date_updated
        this.setTypeExpected(10, TypeNames.DATE);
        
        // update_id
        this.setTypeExpected(11, TypeNames.INT);
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_SKIPRULE;
    }

    public EntityBean create(EntityBean eb) throws OpenClinicaException {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection findAll(String strOrderByColumn,
            boolean blnAscendingSort, String strSearchPhrase)
            throws OpenClinicaException {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection findAllByCRFVersionId(int crfVersionId) throws OpenClinicaException {
        ArrayList answer = new ArrayList();

        this.setTypesExpected();
      
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(crfVersionId));

        String sql = digester.getQuery("findAllByCrfVersionId");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        while (it.hasNext()) {
          SkipRuleBean rb = (SkipRuleBean) this.getEntityFromHashMap((HashMap) it.next());
          answer.add(rb);
        }

        return answer;
    }
    
    public Collection findAll() throws OpenClinicaException {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection findAllByPermission(Object objCurrentUser,
            int intActionType, String strOrderByColumn,
            boolean blnAscendingSort, String strSearchPhrase)
            throws OpenClinicaException {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection findAllByPermission(Object objCurrentUser,
            int intActionType) throws OpenClinicaException {
        // TODO Auto-generated method stub
        return null;
    }

    public EntityBean findByPK(int id) throws OpenClinicaException {
        SkipRuleBean answer = new SkipRuleBean();

        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(id));

        String sql = digester.getQuery("findAllByCRFVersionId");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            answer = (SkipRuleBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return answer;
    }

    public Object getEntityFromHashMap(HashMap hm) {
        SkipRuleBean answer = new SkipRuleBean();
        setEntityAuditInformation(answer, hm);
        
        answer.setId(((Integer)hm.get("rule_id")).intValue());
        answer.setName((String)hm.get("name"));
        answer.setInstruction((String)hm.get("instruction_text"));
        answer.setCondition((String)hm.get("condition"));
        answer.setSkipAction((String)hm.get("assignments"));
        answer.setCrfVersionId((Integer)hm.get("crf_version_id"));

        return answer;
    }

    public EntityBean update(EntityBean eb) throws OpenClinicaException {
        // TODO Auto-generated method stub
        return null;
    }

}
