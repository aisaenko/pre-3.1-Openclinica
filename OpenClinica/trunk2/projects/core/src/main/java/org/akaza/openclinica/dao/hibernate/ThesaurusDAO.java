/**
 * 
 */
package org.akaza.openclinica.dao.hibernate;

import java.util.ArrayList;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.akaza.openclinica.domain.coding.ThesaurusBean;

/**
 * @author pgawade
 *
 */
public class ThesaurusDAO extends AbstractDomainDao<ThesaurusBean> {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public Class<ThesaurusBean> domainClass() {
        return ThesaurusBean.class;
    }   
    
    /**
     * Get the code for verbatim term from a specified dictionary version
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ThesaurusBean> getCodeFromThesaurus(String verbatimTerm, int studyId, int dictId/*, String dictVersion*/) {
    	
        String query = "from " + getDomainClassName() 
        + " th where th.study_id = :studyId"   
        + " and th.dictionary_id = :dictId"   
        //+ " and th.dictionary_version = " + dictVersion 
        + " and th.verbatim_term = :verbatimTerm";
        
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setInteger("studyId", studyId);
        q.setInteger("dictId", dictId);
        q.setString("verbatimTerm", verbatimTerm);
        return (ArrayList<ThesaurusBean>) q.list();
    }    
    	
}