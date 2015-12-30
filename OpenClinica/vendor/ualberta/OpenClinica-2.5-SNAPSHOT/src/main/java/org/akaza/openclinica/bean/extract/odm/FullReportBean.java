/* OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 *//* OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */
package org.akaza.openclinica.bean.extract.odm;

import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.bean.odmbeans.OdmStudyBean;

/**
 * Create ODM XML document for entire study.
 * 
 * @author ywang (May, 2008)
 */

public class FullReportBean extends OdmXmlReportBean {
    private OdmStudyBean odmstudy;
    private OdmClinicalDataBean clinicaldata;

    /**
     * This method creates ODM XML document containing one entire study. This
     * method is still under construction. Right now it is for Snapshot filetype
     * only.
     */
    @Override
    public void createOdmXml(boolean isDataset) {
        this.addHeading();
        this.addRootStartLine();

        // add the contents here in order
        // 1) the information about Study
        addNodeStudy(isDataset);
        // 2) the information about administrative data
        // addNodeAdminData();
        // 3) the information about reference data
        // addNodeReferenceData();
        // 4) the information about clinical Data
        addNodeClinicalData();

        this.addRootEndLine();
    }

    public void addNodeStudy(boolean isDataset) {
        MetaDataReportBean meta = new MetaDataReportBean(this.odmstudy);
        meta.setODMVersion(this.getODMVersion());
        meta.setXmlOutput(this.getXmlOutput());
        meta.addNodeStudy(isDataset);
    }

    public void addNodeClinicalData() {
        ClinicalDataReportBean data = new ClinicalDataReportBean(this.clinicaldata);
        data.setODMVersion(this.getODMVersion());
        data.setXmlOutput(this.getXmlOutput());
        data.addNodeClinicalData();
    }

    public void setOdmStudy(OdmStudyBean odmstudy) {
        this.odmstudy = odmstudy;
    }

    public OdmStudyBean getOdmStudyBean() {
        return this.odmstudy;
    }

    public void setClinicalData(OdmClinicalDataBean clinicaldata) {
        this.clinicaldata = clinicaldata;
    }

    public OdmClinicalDataBean getClinicalData() {
        return this.clinicaldata;
    }
}