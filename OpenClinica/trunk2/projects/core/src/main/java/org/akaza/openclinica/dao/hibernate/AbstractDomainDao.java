package org.akaza.openclinica.dao.hibernate;

import java.util.List;

import org.akaza.openclinica.domain.DomainObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractDomainDao<T extends DomainObject> {

    private SessionFactory sessionFactory;

    abstract Class<T> domainClass();

    public String getDomainClassName() {
        return domainClass().getName();
    }

    @SuppressWarnings("unchecked")
    public T findById(Integer id) {
        getSessionFactory().getStatistics().logSummary();
        String query = "from " + getDomainClassName() + " do  where do.id = :id";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setInteger("id", id);
        return (T) q.uniqueResult();
    }
    @SuppressWarnings("unchecked")
    @Transactional
    public List<T> findAll() {
        String query = "from " + getDomainClassName();
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        return (List<T>) q.list();
    }
    @SuppressWarnings("unchecked")
    
    public List<T> findAllSortByColumn(String columnName) {
        String query = "from " + getDomainClassName()+" order by "+ columnName;
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        return (List<T>) q.list();
    }

    /**
     * The method to return based up on the key value and name.
     * @param id, the column value
     * @param key, the column name
     * @return
     */
    //JN
    @SuppressWarnings("unchecked")  
    @Transactional
    public T findByColumnName(Object id,String key) {
        String query = "from " + getDomainClassName() + " do  where do."+key +"= ?";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setParameter(0, id);
        return (T) q.uniqueResult();
    }
    @Transactional
    public List<T>  findAllByColumnName(Object id,String key) {
        String query = "from " + getDomainClassName() + " do  where do."+key +"= ?";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setParameter(0, id);
        return (List<T> )q.list();
    }
    
    
    
    @Transactional
    public T saveOrUpdate(T domainObject) {
        getSessionFactory().getStatistics().logSummary();
        getCurrentSession().saveOrUpdate(domainObject);
        return domainObject;
    }
    @Transactional
    public T merge(T domainObject){
        getCurrentSession().merge(domainObject);
        return domainObject;
    }
    @Transactional
    public T merge(String entityName,T domainObject){
        getCurrentSession().merge(entityName, domainObject);
        return domainObject;
    }
    
    @Transactional
    public T update(T domainObject){
        getCurrentSession().update(domainObject);
        return domainObject;
    }
    public Long count() {
        return (Long) getCurrentSession().createQuery("select count(*) from " + domainClass().getName()).uniqueResult();
    }

    
    /**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
      
        
        
        this.sessionFactory = sessionFactory;
    }

    /**
     * @return Session Object
     */
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

}
