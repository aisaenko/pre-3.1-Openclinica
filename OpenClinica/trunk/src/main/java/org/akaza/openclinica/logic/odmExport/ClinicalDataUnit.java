/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.logic.odmExport;

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.ODMBean;
import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.dao.extract.OdmExtractDAO;

import javax.sql.DataSource;

/**
 * A class for one ODM ClinicalData Element.
 * 
 * @author ywang (May, 2009)
 */

public class ClinicalDataUnit extends OdmUnit {
    private OdmClinicalDataBean odmClinicalData;

    public ClinicalDataUnit() {
    }

    public ClinicalDataUnit(DataSource ds, StudyBean study, int category) {
        super(ds, study, category);
        this.odmClinicalData = new OdmClinicalDataBean();
    }

    public ClinicalDataUnit(DataSource ds, DatasetBean dataset, ODMBean odmBean, StudyBean study, int category) {
        super(ds, dataset, odmBean, study, category);
        this.odmClinicalData = new OdmClinicalDataBean();
    }

    public void collectOdmClinicalData() {
        StudyBean study = studyBase.getStudy();
        String studyOID = study.getOid();
        if (studyOID == null || studyOID.length() <= 0) {
            logger.info("Constructed studyOID using study_id because oc_oid is missing from the table - study.");
            studyOID = "" + study.getId();
        }
        odmClinicalData.setStudyOID(studyOID);

        OdmExtractDAO oedao = new OdmExtractDAO(this.ds);
        if (this.getCategory() == 1 && study.isSite(study.getParentStudyId())) {
            String mvoid = "";
            if (this.dataset != null) {
                mvoid = this.dataset.getODMMetaDataVersionOid();
            }
            if (mvoid.length() > 0) {
                mvoid += "-" + studyOID;
            } else {
                mvoid = "v1.0.0" + "-" + studyOID;
            }
            odmClinicalData.setMetaDataVersionOID(mvoid);

        } else {
            odmClinicalData.setMetaDataVersionOID(this.dataset.getODMMetaDataVersionOid());
            if (odmClinicalData.getMetaDataVersionOID() == null || odmClinicalData.getMetaDataVersionOID().length() <= 0) {
                odmClinicalData.setMetaDataVersionOID("v1.0.0");
            }
        }
        oedao.getClinicalData(study, this.dataset, odmClinicalData, this.odmBean.getODMVersion());
    }

    public OdmClinicalDataBean getOdmClinicalData() {
        return odmClinicalData;
    }

    public void setOdmClinicalData(OdmClinicalDataBean odmClinicalData) {
        this.odmClinicalData = odmClinicalData;
    }
}