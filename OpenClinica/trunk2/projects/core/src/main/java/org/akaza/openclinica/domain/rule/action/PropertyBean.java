package org.akaza.openclinica.domain.rule.action;

import org.akaza.openclinica.bean.odmbeans.CodeListBean;
import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.akaza.openclinica.domain.coding.CodingDictionaryBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rule_action_property")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "rule_action_property_id_seq") })
public class PropertyBean extends AbstractMutableDomainObject {

    private String oid;
    private String value;
    private ExpressionBean valueExpression;
    //private String coding_dictionary_id;
    private String codeListOID;

    @Column(name = "oc_oid")
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rule_expression_id")
    public ExpressionBean getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(ExpressionBean valueExpression) {
        this.valueExpression = valueExpression;
    }  

	@Override
    public String toString() {
//        return "PropertyBean [oid=" + oid + ", value=" + value + ", valueExpression=" + valueExpression + ", coding_dictionary_id=" + coding_dictionary_id + "]";
		return "PropertyBean [oid=" + oid + ", value=" + value + ", valueExpression=" + valueExpression + ", codeListOID=" + codeListOID + "]";
    }
	
	@Column(name = "codelist_oid")
    public String getCodeListOID() {
		return codeListOID;
	}

	public void setCodeListOID(String codeListOID) {
		this.codeListOID = codeListOID;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((oid == null) ? 0 : oid.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((valueExpression == null) ? 0 : valueExpression.hashCode());
//        result = prime * result + ((coding_dictionary_id == null) ? 0 : coding_dictionary_id.hashCode());
        result = prime * result + ((codeListOID == null) ? 0 : codeListOID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PropertyBean other = (PropertyBean) obj;
        if (oid == null) {
            if (other.oid != null)
                return false;
        } else if (!oid.equals(other.oid))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (valueExpression == null) {
            if (other.valueExpression != null)
                return false;
        } else if (!valueExpression.equals(other.valueExpression))
            return false;
//        if (coding_dictionary_id == null) {
//            if (other.coding_dictionary_id != null)
//                return false;
//        } else if (!coding_dictionary_id.equals(other.coding_dictionary_id))
//            return false;
        if (codeListOID == null) {
            if (other.codeListOID != null)
                return false;
        } else if (!codeListOID.equals(other.codeListOID))
            return false;
        return true;
    }

}
