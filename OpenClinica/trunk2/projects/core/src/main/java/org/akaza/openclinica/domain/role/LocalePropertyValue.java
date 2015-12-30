package org.akaza.openclinica.domain.role;

import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.akaza.openclinica.dao.hibernate.LocalePropertyValueDao;
import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


/**
 * 
 * @author htaycher The domain Object for persisting to the Domain Table.
 */
@Entity
@Table(name = "locale_based_description")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "locale_based_description_id_seq") })

public class LocalePropertyValue  extends AbstractMutableDomainObject{
	
	private String propertyName;
	private String localeName;
    private String localizedDescription;
    
    @Column(name = "property_name")
    public String getPropertyName() {
        return propertyName;
    }
    public void  setPropertyName(String propertyName) {
         this.propertyName=propertyName;
    }
    
    @Column(name = "locale_name")
    public String getLocaleName() {
        return localeName;
    }
    public void  setLocaleName(String localeName) {
         this.localeName=localeName;
    }
    
    @Column(name = "localized_description")
    public String getLocaleDescription() {
        return localizedDescription;
    }
    public void  setLocaleDescription(String localizedDescription) {
         this.localizedDescription=localizedDescription;
    }
    
  
   
}
