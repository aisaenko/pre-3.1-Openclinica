package org.akaza.openclinica.dao.logic;

import java.util.Collection;
import java.util.HashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.exception.OpenClinicaException;


public class TableDAO extends AuditableEntityDAO {
    public TableDAO(DataSource ds){
        super(ds);
    }
    
    public void setTypesExpected() {
        this.unsetTypeExpected();
    }
    
    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_TABLE;
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
    	return null;
    }
    public EntityBean update(EntityBean eb) throws OpenClinicaException {
        // TODO Auto-generated method stub
        return null;
    }
    public Object getEntityFromHashMap(HashMap hm) {
    	return null;
    }
}