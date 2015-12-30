/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.domain.coding;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * <p>
 * Coding dictionary
 * </p>
 * 
 * @author Pradnya Gawade
 */
@Entity
@Table(name = "coding_dictionary")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "coding_dictionary_id_seq") })
public class CodingDictionaryBean extends AbstractMutableDomainObject {

    private String oid;
	private String name;
    private String dictionaryVersion;
    private String description;
    //private String ontology_id;
    //private String ontology_version_id;
    private Integer dataTypeId;
    private String url;
    private Integer statusId;
        
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
//	public String getOntology_id() {
//		return ontology_id;
//	}
//	public void setOntology_id(String ontology_id) {
//		this.ontology_id = ontology_id;
//	}
	@Column(name = "dictionary_version")
	public String getDictionaryVersion() {
		return dictionaryVersion;
	}
	public void setDictionaryVersion(String dictionaryVersion) {
		this.dictionaryVersion = dictionaryVersion;
	}
//	public String getOntology_version_id() {
//		return ontology_version_id;
//	}
//	public void setOntology_version_id(String ontology_version_id) {
//		this.ontology_version_id = ontology_version_id;
//	}
	@Column(name = "oc_oid")
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@Column(name = "data_type_id")
	public Integer getDataTypeId() {
		return dataTypeId;
	}
	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Column(name = "status_id")
	public Integer getStatusId() {
		return statusId;
	}
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}
	
}