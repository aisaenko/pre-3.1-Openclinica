/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.domain.coding;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * <p>
 * Log Usage Statistics
 * </p>
 * 
 * @author Pradnya Gawade
 */
@Entity
@Table(name = "thesaurus")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "thesaurus_id_seq") })
public class ThesaurusBean extends AbstractMutableDomainObject {

    private int dictionary_id;
    //private String dictionary_version;
    private int study_id;
    private Timestamp date_updated;
    private String verbatim_term;
    private String code;
    private String code_details;
	public int getDictionary_id() {
		return dictionary_id;
	}
	public void setDictionary_id(int dictionary_id) {
		this.dictionary_id = dictionary_id;
	}
//	public String getDictionary_version() {
//		return dictionary_version;
//	}
//	public void setDictionary_version(String dictionary_version) {
//		this.dictionary_version = dictionary_version;
//	}
	public int getStudy_id() {
		return study_id;
	}
	public void setStudy_id(int study_id) {
		this.study_id = study_id;
	}
	public Timestamp getDate_updated() {
		return date_updated;
	}
	public void setDate_updated(Timestamp date_updated) {
		this.date_updated = date_updated;
	}
	public String getVerbatim_term() {
		return verbatim_term;
	}
	public void setVerbatim_term(String verbatim_term) {
		this.verbatim_term = verbatim_term;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode_details() {
		return code_details;
	}
	public void setCode_details(String code_details) {
		this.code_details = code_details;
	}
        
	    

}