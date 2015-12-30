
package org.akaza.openclinica.dao.hibernate;

import java.util.List;
import java.util.Locale;

import javax.persistence.Transient;

import org.akaza.openclinica.domain.role.LocalePropertyValue;
import org.springframework.transaction.annotation.Transactional;


public class LocalePropertyValueDao  extends AbstractDomainDao<LocalePropertyValue>{

    @Override
    Class<LocalePropertyValue> domainClass() {
        // TODO Auto-generated method stub
        return LocalePropertyValue.class;
    }
    
    @SuppressWarnings("unchecked")
     @Transactional
   
    public List<LocalePropertyValue> findByLocaleAndPropertyName(String propertyName, String localeName) {
        String query = "from " + getDomainClassName()+" do  where do.propertyName=:pName  and (do.localeName = 'en' or do.localeName=:lName)";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setString("pName", propertyName);
        q.setString("lName", localeName);
        return (List<LocalePropertyValue>) q.list();
    }
    @SuppressWarnings("unchecked")
    @Transactional
   
    public    String   getPropertyDescription(String fieldValue, Locale locale ){
    	Locale default_locale = new Locale("en_US");
    	List<LocalePropertyValue> results =       findByLocaleAndPropertyName(fieldValue, locale.getLanguage());
    	String result="";
    	for ( LocalePropertyValue lvalue:  results){
    		if( lvalue.getLocaleName().equals(locale.getLanguage())){
    			return lvalue.getLocaleDescription();
    		}
    		if (lvalue.getLocaleName().equals(default_locale.getLanguage())){
    			result = lvalue.getLocaleDescription();
    		}
    	}
    	return result;
    }
}

