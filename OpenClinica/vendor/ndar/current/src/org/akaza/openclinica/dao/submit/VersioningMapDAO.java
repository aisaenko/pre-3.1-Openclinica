package org.akaza.openclinica.dao.submit;

import java.util.Collection;
import java.util.HashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.exception.OpenClinicaException;

public class VersioningMapDAO extends AuditableEntityDAO {
    public VersioningMapDAO(DataSource ds) {
      super(ds);
    }

    public VersioningMapDAO(DataSource ds, DAODigester digester) {
      super(ds);
      this.digester = digester;
    }

    @Override
    public void setTypesExpected() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_VERSIONING_MAP;
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
        // TODO Auto-generated method stub
        return null;
    }

    public Object getEntityFromHashMap(HashMap hm) {
        // TODO Auto-generated method stub
        return null;
    }

    public EntityBean update(EntityBean eb) throws OpenClinicaException {
        // TODO Auto-generated method stub
        return null;
    }

}
