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
import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.dao.extract.OdmExtractDAO;

import java.util.Set;

import javax.sql.DataSource;

/**
 * Populate odm clinical data
 * 
 * @author ywang (May, 2008)
 */

public class ClinicalDataCollector extends OdmDataCollector {
    private OdmClinicalDataBean odmClinicalData;

    /**
     * 
     * @param ds
     * @param dataset
     */
    public ClinicalDataCollector(DataSource ds, DatasetBean dataset) {
        super(ds, dataset);
        this.odmClinicalData = new OdmClinicalDataBean();
    }

    public ClinicalDataCollector(DataSource ds, DatasetBean dataset, Set<Integer> nullClSet) {
        super(ds, dataset);
        this.odmClinicalData = new OdmClinicalDataBean();
        this.nullClSet = nullClSet;
    }

    @Override
    public void collectFileData() {
        this.collectOdmRoot();
        this.collectOdmClinicalData();
    }

    public void collectOdmClinicalData() {
        StudyBean study = this.getStudy();
        String studyOID = study.getOid();
        if (studyOID == null || studyOID.length() <= 0) {
            logger.info("Constructed studyOID using study_id because oc_oid is missing from the table - study.");
            studyOID = "" + study.getId();
            study.setOid(studyOID);
        }
        odmClinicalData.setStudyOID(studyOID);
        odmClinicalData.setMetaDataVersionOID(this.dataset.getODMMetaDataVersionOid());
        if (odmClinicalData.getMetaDataVersionOID() == null || odmClinicalData.getMetaDataVersionOID().length() <= 0) {
            odmClinicalData.setMetaDataVersionOID("v1.0.0");
        }
        OdmExtractDAO oedao = new OdmExtractDAO(this.ds);
        oedao.getClinicalData(study, this.dataset, odmClinicalData, this.getODMBean().getODMVersion());
    }

    public void setOdmClinicalData(OdmClinicalDataBean clinicaldata) {
        this.odmClinicalData = clinicaldata;
    }

    public OdmClinicalDataBean getOdmClinicalData() {
        return this.odmClinicalData;
    }
}