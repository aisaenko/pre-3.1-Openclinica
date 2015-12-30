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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

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
    private static HashMap<String, String> nullValueMap = new HashMap<String, String>();
    protected Set<Integer> nullClSet; // contains all item_ids which have
    // codelist
    // with null value(s)

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    /**
     * In this constructor, odmbean is new ODMBean; study, sedBeansInStudy and
     * nullValueMap have been initialized; nullClSet has been set as new HashSet<Integer>;
     * 
     * 
     * @param ds
     * @param study
     */
    public OdmDataCollector(DataSource ds, StudyBean study) {
        this.ds = ds;
        if (this.ds == null) {
            logger.info("Can not collect ODM data because dataSource is null!");
            return;
        }
        if (study == null) {
            logger.info("Can not collect ODM data because study is null!");
            return;
        }
        odmbean = new ODMBean();
        nullValueMap = initialNullValueMap();
        nullClSet = new HashSet<Integer>();

        populateStudyAndSeds(study.getId());
    }

    /**
     * This constructor initialized all data members.
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
        nullValueMap = initialNullValueMap();

        populateStudyAndSeds(this.dataset.getStudyId());
    }

    private HashMap<String, String> initialNullValueMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UNK", ".U");
        map.put("NA", ".A");
        map.put("NI", ".I");
        map.put("NASK", ".K");
        map.put("NAV", ".V");
        map.put("PINF", ".P");
        map.put("NINF", ".N");
        map.put("MSK", ".M");
        map.put("ASKU", ".S");
        map.put("OTH", ".O");
        map.put("NP", ".X");
        return map;
    }

    private void populateStudyAndSeds(int studyId) {
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
        SimpleDateFormat localTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone timeZone = TimeZone.getDefault();
        localTime.setTimeZone(timeZone);
        int offset = localTime.getTimeZone().getOffset(creationDatetime.getTime());
        String sign = "+";
        if (offset < 0) {
            offset = -offset;
            sign = "-";
        }
        int hours = offset / 3600000;
        int minutes = (offset - hours * 3600000) / 60000;
        DecimalFormat twoDigits = new DecimalFormat("00");
        odmbean.setFileOID(this.dataset.getName() + "D" + (new SimpleDateFormat("yyyyMMddHHmmssZ")).format(creationDatetime));
        odmbean.setDescription(this.dataset.getDescription().trim());
        odmbean.setCreationDateTime((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss" + sign + twoDigits.format(hours) + ":" + twoDigits.format(minutes)))
                .format(creationDatetime));

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

    public static HashMap<String, String> getNullValueMap() {
        return nullValueMap;
    }

    public static void setNullValueMap(HashMap<String, String> nullMap) {
        nullValueMap = nullMap;
    }

    public void setNullClSet(Set<Integer> nullSet) {
        this.nullClSet = nullSet;
    }

    public Set<Integer> getNullClSet() {
        return this.nullClSet;
    }
}
