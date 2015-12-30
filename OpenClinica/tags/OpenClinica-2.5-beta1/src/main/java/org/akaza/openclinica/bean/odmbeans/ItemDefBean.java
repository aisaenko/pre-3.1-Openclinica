/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ywang (May, 2008)
 * 
 */

public class ItemDefBean extends ElementOIDBean {
    private String name;
    private String dataType;
    private String length;
    private String significantDigits;
    private String preSASFieldName;
    private TranslatedTextBean question;
    private List<RangeCheckBean> rangeChecks;
    private String codeListOID;
    
    public ItemDefBean() {
        question = new TranslatedTextBean();
        rangeChecks = new ArrayList<RangeCheckBean>();
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDataType(String datatype) {
        this.dataType = datatype;
    }

    public String getDateType() {
        return this.dataType;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLength() {
        return this.length;
    }

    public void setSignificantDigits(String scale) {
        this.significantDigits = scale;
    }

    public String getSignificantDigits() {
        return this.significantDigits;
    }

    public void setPreSASFieldName(String sasname) {
        this.preSASFieldName = sasname;
    }

    public String getPreSASFieldName() {
        return this.preSASFieldName;
    }

    public void setQuestion(TranslatedTextBean question) {
        this.question = question;
    }

    public TranslatedTextBean getQuestion() {
        return this.question;
    }

    public void setRangeCheck(List<RangeCheckBean> ranges) {
        this.rangeChecks = ranges;
    }

    public List<RangeCheckBean> getRangeCheck() {
        return this.rangeChecks;
    }

    public void setCodeListOID(String cloid) {
        this.codeListOID = cloid;
    }

    public String getCodeListOID() {
        return this.codeListOID;
    }
}