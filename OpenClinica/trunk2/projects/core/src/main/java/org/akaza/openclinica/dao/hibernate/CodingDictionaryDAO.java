/**
 * 
 */
package org.akaza.openclinica.dao.hibernate;

import java.util.ArrayList;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.akaza.openclinica.domain.coding.CodingDictionaryBean;
import org.akaza.openclinica.domain.technicaladmin.AuditUserLoginBean;

/**
 * @author pgawade
 *
 */
public class CodingDictionaryDAO extends AbstractDomainDao<CodingDictionaryBean> {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public Class<CodingDictionaryBean> domainClass() {
        return CodingDictionaryBean.class;
    }   
    
    @SuppressWarnings("unchecked")
    public ArrayList<CodingDictionaryBean> findAll() {
        String query = "from " + getDomainClassName() + " cd ";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        return (ArrayList<CodingDictionaryBean>) q.list();
    }
    
    @SuppressWarnings("unchecked")
    public CodingDictionaryBean findByOID(String oid) {
        String query = "from " + getDomainClassName() + " cd where cd.oid = :oid";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        
        q.setString("oid", oid);
        return (CodingDictionaryBean) q.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<CodingDictionaryBean> getDictionaryForNameAndVersion(String dictionaryName, String dictionaryVersion) {
    	String query = "from " + getDomainClassName() + " cd where cd.name = :name"
    	+ " and cd.dictionaryVersion = :dictionaryVersion";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setString("name", dictionaryName);
        q.setString("dictionaryVersion", dictionaryVersion);
        return (ArrayList<CodingDictionaryBean>) q.list();
    }
}
