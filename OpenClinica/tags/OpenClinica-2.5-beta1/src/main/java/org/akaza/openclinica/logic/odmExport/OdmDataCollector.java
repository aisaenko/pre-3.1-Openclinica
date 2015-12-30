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
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.odmbeans.ODMBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

/**
 * Prepare data for ODM beans related to ODM XML exporting
 * 
 * @author ywang (May, 2008)
 */

public abstract class OdmDataCollector {
    protected DataSource ds;
    protected DatasetBean dataset;
    private ODMBean odmbean;
    // right now, only one study in a dataset
    private StudyBean study;
    private List<StudyEventDefinitionBean> sedBeansInStudy;

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    /**
     * This constructor also called the populateStudy() method.
     * 
     * @param ds
     * @param dataset
     */
    public OdmDataCollector(DataSource ds, DatasetBean dataset) {
        this.ds = ds;
        this.dataset = dataset;
        if (this.ds == null) {
            logger.info("Can not collect ODM data because dataSource is null!");
            return;
        }
        if (this.dataset == null) {
            logger.info("Can not collect ODM data because dataset is null!");
            return;
        }
        odmbean = new ODMBean();

        populateStudyAndSeds();
    }

    public void populateStudyAndSeds() {
        int studyId = this.dataset.getStudyId();
        StudyDAO sdao = new StudyDAO(this.ds);
        StudyBean sb = (StudyBean) sdao.findByPK(studyId);
        this.setStudy(sb);
        if (sb == null) {
            logger.info("Study is null.");
            return;
        } else {
            StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
            this.sedBeansInStudy = seddao.findAllActiveByStudy(this.study);
        }
        ;
    }

    public abstract void collectFileData();

    /**
     * Create an ODMBean with default ODMBean properties.
     * 
     * @return
     */
    public void collectOdmRoot() {
        if (this.dataset == null) {
            logger.info("empty ODMBean has been returned because dataset is null!");
            return;
        }
        Date creationDatetime = new Date();
        // !!!!! need handle dataset-oid
        odmbean.setFileOID(this.dataset.getName() + "D" + (new SimpleDateFormat("yyyyMMddHHmmss")).format(creationDatetime));
        System.out.println("===datasetname=" + dataset.getName() + " odm anme=" + odmbean.getFileOID());
        odmbean.setDescription(this.dataset.getDescription().trim());
        odmbean.setCreationDateTime((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(creationDatetime));
    }

    public void setDataSource(DataSource ds) {
        this.ds = ds;
    }

    public DataSource getDataSource() {
        return this.ds;
    }

    public void setDataset(DatasetBean dataset) {
        this.dataset = dataset;
    }

    public DatasetBean getDataset() {
        return this.dataset;
    }

    public void setODMBean(ODMBean odmbean) {
        this.odmbean = odmbean;
    }

    public ODMBean getODMBean() {
        return this.odmbean;
    }

    public void setStudy(StudyBean study) {
        this.study = study;
    }

    public StudyBean getStudy() {
        return this.study;
    }

    public void setSedBeansInStudy(List<StudyEventDefinitionBean> seds) {
        this.sedBeansInStudy = seds;
    }

    public List<StudyEventDefinitionBean> getSedBeansInStudy() {
        return this.sedBeansInStudy;
    }
}
