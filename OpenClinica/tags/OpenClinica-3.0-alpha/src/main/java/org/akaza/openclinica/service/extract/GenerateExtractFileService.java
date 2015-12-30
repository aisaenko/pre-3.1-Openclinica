package org.akaza.openclinica.service.extract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.DisplayItemHeaderBean;
import org.akaza.openclinica.bean.extract.ExportFormatBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.extract.SPSSReportBean;
import org.akaza.openclinica.bean.extract.SPSSVariableNameValidationBean;
import org.akaza.openclinica.bean.extract.TabReportBean;
import org.akaza.openclinica.bean.extract.odm.FullReportBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.odmbeans.ODMBean;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.odmExport.ClinicalDataCollector;
import org.akaza.openclinica.logic.odmExport.MetaDataCollector;

public class GenerateExtractFileService {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private DataSource ds;
    private HttpServletRequest request;
    private DatasetDAO datasetDao;
    public static ResourceBundle resword;
    private UserAccountBean userBean;

    public GenerateExtractFileService(DataSource ds, HttpServletRequest request, UserAccountBean userBean) {
        this.ds = ds;
        this.request = request;
        this.userBean = userBean;
    }
    
    public GenerateExtractFileService(DataSource ds, UserAccountBean userBean) {
        this.ds = ds;
        this.userBean = userBean;
    }
    
    public void setUpResourceBundles() {
    	Locale locale;
    	try {
    		locale = request.getLocale();
    	} catch (NullPointerException ne) {
    		locale = new Locale("en-US");
    	}
    	
        ResourceBundleProvider.updateLocale(locale);
        resword = ResourceBundleProvider.getWordsBundle(locale);
    }
    
    /**
     * createTabFile, added by tbh, 01/2009
     */
    public HashMap<String,Integer> createTabFile(
    		ExtractBean eb, 
    		long sysTimeBegin, 
    		String generalFileDir, 
    		DatasetBean datasetBean,
			int activeStudyId,
			int parentStudyId) {
    	
    	TabReportBean answer = new TabReportBean();
        
		DatasetDAO dsdao = new DatasetDAO(ds);
		// create the extract bean here, tbh
		eb = dsdao.getDatasetData(eb, activeStudyId, parentStudyId);
        eb.getMetadata();
        eb.computeReport(answer);

        long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
        String TXTFileName = datasetBean.getName() + "_tab.xls";

        int fId = this.createFile(
        		TXTFileName, 
        		generalFileDir, 
        		answer.toString(), 
        		datasetBean, 
        		sysTimeEnd, 
        		ExportFormatBean.TXTFILE);
        logger.info("created txt file");
        //return TXTFileName;
        HashMap answerMap = new HashMap<String,Integer>();
        answerMap.put(TXTFileName, new Integer(fId));
        return answerMap;
    }
    
    /**
     * createODMfile, added by tbh, 01/2009
     */
    
    public HashMap<String, Integer> createODMFile(String odmVersion, 
    		long sysTimeBegin, 
    		String generalFileDir, 
    		DatasetBean datasetBean) {
    	
    	MetaDataCollector mdc = new MetaDataCollector(ds, datasetBean);

    	ClinicalDataCollector cdc = new ClinicalDataCollector(ds, datasetBean, mdc.getNullClSet());
    	MetaDataCollector.setTextLength(200);
    	if (odmVersion != null) {
    		// by default odmVersion is 1.2
    		if ("1.3".equals(odmVersion)) {
    			ODMBean odmb = new ODMBean();
    			odmb.setSchemaLocation("http://www.cdisc.org/ns/odm/v1.3 ODM1-3-0.xsd");
    			ArrayList<String> xmlnsList = new ArrayList<String>();
    			xmlnsList.add("xmlns=\"http://www.cdisc.org/ns/odm/v1.3\"");
    			xmlnsList.add("xmlns:OpenClinica=\"http://www.openclinica.org/ns/openclinica_odm/v1.3\"");
    			odmb.setXmlnsList(xmlnsList);
    			odmb.setODMVersion("1.3");
    			mdc.setODMBean(odmb);
    			cdc.setODMBean(odmb);
    		} else if ("oc1.2".equals(odmVersion)) {
    			ODMBean odmb = new ODMBean();
    			odmb.setSchemaLocation("http://www.cdisc.org/ns/odm/v1.2 OpenClinica-ODM1-2-1.xsd");
    			ArrayList<String> xmlnsList = new ArrayList<String>();
    			xmlnsList.add("xmlns=\"http://www.cdisc.org/ns/odm/v1.2\"");
    			xmlnsList.add("xmlns:OpenClinica=\"http://www.openclinica.org/ns/openclinica_odm/v1.2\"");
    			odmb.setXmlnsList(xmlnsList);
    			odmb.setODMVersion("oc1.2");
    			mdc.setODMBean(odmb);
    			cdc.setODMBean(odmb);
    		} else if ("oc1.3".equals(odmVersion)) {
    			ODMBean odmb = mdc.getODMBean();
    			odmb.setSchemaLocation("http://www.cdisc.org/ns/odm/v1.3 OpenClinica-ODM1-3-0.xsd");
    			ArrayList<String> xmlnsList = new ArrayList<String>();
    			xmlnsList.add("xmlns=\"http://www.cdisc.org/ns/odm/v1.3\"");
    			xmlnsList.add("xmlns:OpenClinica=\"http://www.openclinica.org/ns/openclinica_odm/v1.3\"");
    			odmb.setXmlnsList(xmlnsList);
    			odmb.setODMVersion("oc1.3");
    			mdc.setODMBean(odmb);
    			cdc.setODMBean(odmb);
    		}
    	}
    	mdc.collectFileData();
    	cdc.collectOdmClinicalData();
    	FullReportBean report = new FullReportBean();
    	report.setClinicalData(cdc.getOdmClinicalData());
    	report.setOdmStudy(mdc.getOdmStudy());
    	report.setOdmBean(mdc.getODMBean());
    	report.setODMVersion(odmVersion);
    	report.createOdmXml(Boolean.TRUE);
    	long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
    	String ODMXMLFileName = mdc.getODMBean().getFileOID() + ".xml";
    	int fId = this.createFile(ODMXMLFileName, generalFileDir, report.getXmlOutput().toString(), datasetBean, sysTimeEnd, ExportFormatBean.XMLFILE);
    	// return ODMXMLFileName;
    	HashMap answerMap = new HashMap<String,Integer>();
        answerMap.put(ODMXMLFileName, new Integer(fId));
        return answerMap;
    }
    /**
     * createSPSSFile, added by tbh, 01/2009
     * @param db
     * @param eb
     * @param currentstudyid
     * @param parentstudy
     * @return
     */
    public HashMap<String, Integer> createSPSSFile(DatasetBean db, 
    		ExtractBean eb2, 
    		StudyBean currentStudy, 
    		StudyBean parentStudy, 
    		long sysTimeBegin, 
    		String generalFileDir, 
    		SPSSReportBean answer) {
    	setUpResourceBundles();
    	
    	String SPSSFileName = db.getName() + "_data_spss.dat";
        String DDLFileName = db.getName() + "_ddl_spss.sps";
        String ZIPFileName = db.getName() + "_spss";

        SPSSVariableNameValidationBean svnvbean = new SPSSVariableNameValidationBean();
        answer.setDatFileName(SPSSFileName);
        DatasetDAO dsdao = new DatasetDAO(ds);
        
        // create the extract bean here, tbh
		ExtractBean eb = this.generateExtractBean(db, currentStudy, parentStudy);

        eb = dsdao.getDatasetData(eb, currentStudy.getId(), parentStudy.getId());

        eb.getMetadata();

        eb.computeReport(answer);

        answer.setItems(eb.getItemNames());// set up items here to get
        // itemMetadata

        // set up response sets for each item here
        ItemDAO itemdao = new ItemDAO(ds);
        ItemFormMetadataDAO imfdao = new ItemFormMetadataDAO(ds);
        ArrayList items = answer.getItems();
        for (int i = 0; i < items.size(); i++) {
            DisplayItemHeaderBean dih = (DisplayItemHeaderBean) items.get(i);
            ItemBean item = dih.getItem();
            ArrayList metas = imfdao.findAllByItemId(item.getId());
            // for (int h = 0; h < metas.size(); h++) {
            // ItemFormMetadataBean ifmb = (ItemFormMetadataBean)
            // metas.get(h);
            // logger.info("group name found:
            // "+ifmb.getGroupLabel());
            // }
            // logger.info("crf versionname" +
            // meta.getCrfVersionName());
            item.setItemMetas(metas);

        }
        
        HashMap eventDescs = new HashMap<String, String>();

        eventDescs = eb.getEventDescriptions();

        eventDescs.put("SubjID", resword.getString("study_subject_ID"));
        eventDescs.put("ProtocolID", resword.getString("protocol_ID_site_ID"));
        eventDescs.put("DOB", resword.getString("date_of_birth"));
        eventDescs.put("YOB", resword.getString("year_of_birth"));
        eventDescs.put("Gender", resword.getString("gender"));
        answer.setDescriptions(eventDescs);

        ArrayList generatedReports = new ArrayList<String>();
        // YW <<
        generatedReports.add(answer.getMetadataFile(svnvbean, eb).toString());
        generatedReports.add(answer.getDataFile().toString());
        // YW >>

        long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;

        ArrayList titles = new ArrayList();
        // YW <<
        titles.add(DDLFileName);
        titles.add(SPSSFileName);
        // YW >>

        // create new createFile method that accepts array lists to
        // put into zip files
        int fId = this.createFile(ZIPFileName, titles, generalFileDir, generatedReports, db, sysTimeEnd, ExportFormatBean.TXTFILE);
        //return DDLFileName;
        HashMap answerMap = new HashMap<String,Integer>();
        answerMap.put(DDLFileName, new Integer(fId));
        return answerMap;
    }

    public int createFile(String zipName, 
    		ArrayList names, 
    		String dir, 
    		ArrayList contents, 
    		DatasetBean datasetBean, 
    		long time, 
    		ExportFormatBean efb) {
        ArchivedDatasetFileBean fbFinal = new ArchivedDatasetFileBean();
        fbFinal.setId(0);
        try {
            File complete = new File(dir);
            if (!complete.isDirectory()) {
                complete.mkdirs();
            }
            int totalSize = 0;
            ZipOutputStream z = new ZipOutputStream(new FileOutputStream(new File(complete, zipName + ".zip")));
            FileInputStream is = null;
            for (int i = 0; i < names.size(); i++) {
                String name = (String) names.get(i);
                String content = (String) contents.get(i);
                File newFile = new File(complete, name);
                // totalSize = totalSize + (int)newFile.length();
                newFile.setLastModified(System.currentTimeMillis());

                BufferedWriter w = new BufferedWriter(new FileWriter(newFile));
                w.write(content);
                w.close();
                logger.info("finished writing the text file...");
                // now, we write the file to the zip file
                is = new FileInputStream(newFile);

                logger.info("created zip output stream...");

                z.putNextEntry(new java.util.zip.ZipEntry(name));

                int bytesRead;
                byte[] buff = new byte[512];

                while ((bytesRead = is.read(buff)) != -1) {
                    z.write(buff, 0, bytesRead);
                    totalSize += 512;
                }
                z.closeEntry();
            }
            logger.info("writing buffer...");
            // }
            z.flush();
            z.finish();
            z.close();
            
            if (is != null) {
                try {
                    is.close();
                } catch (java.io.IOException ie) {
                    ie.printStackTrace();
                }
            }
            logger.info("finished zipping up file...");
            // set up the zip to go into the database
            ArchivedDatasetFileBean fb = new ArchivedDatasetFileBean();
            fb.setName(zipName + ".zip");
            fb.setFileReference(dir + zipName + ".zip");
            // current location of the file on the system
            fb.setFileSize(totalSize);
            // set the above to compressed size?
            fb.setRunTime((int) time);
            // need to set this in milliseconds, get it passed from above
            // methods?
            fb.setDatasetId(datasetBean.getId());
            fb.setExportFormatBean(efb);
            fb.setExportFormatId(efb.getId());
            fb.setOwner(userBean);
            fb.setOwnerId(userBean.getId());
            fb.setDateCreated(new Date(System.currentTimeMillis()));

            boolean write = true;
            ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(ds);

            if (write) {
                fbFinal = (ArchivedDatasetFileBean) asdfDAO.create(fb);
                logger.info("Created ADSFile!: " + fbFinal.getId() + " for " + zipName + ".zip");
            } else {
                logger.info("duplicate found: " + fb.getName());
            }
            // created in database!

        } catch (Exception e) {
            logger.warn(e.getMessage());
            System.out.println("-- exception at create file: "+e.getMessage());
            e.printStackTrace();
        }
        return fbFinal.getId();
    }

    public int createFile(String name, 
    		String dir, 
    		String content, 
    		DatasetBean datasetBean, 
    		long time, 
    		ExportFormatBean efb) {
        ArchivedDatasetFileBean fbFinal = new ArchivedDatasetFileBean();
        fbFinal.setId(0);
        try {
            File complete = new File(dir);
            if (!complete.isDirectory()) {
                complete.mkdirs();
            }
            File newFile = new File(complete, name);
            newFile.setLastModified(System.currentTimeMillis());

            BufferedWriter w = new BufferedWriter(new FileWriter(newFile));
            w.write(content);
            w.close();
            logger.info("finished writing the text file...");
            // now, we write the file to the zip file
            FileInputStream is = new FileInputStream(newFile);
            ZipOutputStream z = new ZipOutputStream(new FileOutputStream(new File(complete, name + ".zip")));
            logger.info("created zip output stream...");
            // we write over the content no matter what
            // we then check to make sure there are no duplicates
            // TODO need to change the above -- save all content!
            // z.write(content);
            z.putNextEntry(new java.util.zip.ZipEntry(name));
            // int length = (int) newFile.length();
            int bytesRead;
            byte[] buff = new byte[512];
            // read from buffered input stream and put into zip file
            // while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
            while ((bytesRead = is.read(buff)) != -1) {
                z.write(buff, 0, bytesRead);
            }
            logger.info("writing buffer...");
            // }
            z.closeEntry();
            z.finish();
            // newFile = new File(complete, name+".zip");
            // newFile.setLastModified(System.currentTimeMillis());
            //
            // BufferedWriter w2 = new BufferedWriter(new FileWriter(newFile));
            // w2.write(newOut.toString());
            // w2.close();
            if (is != null) {
                try {
                    is.close();
                } catch (java.io.IOException ie) {
                    ie.printStackTrace();
                }
            }
            logger.info("finished zipping up file...");
            // set up the zip to go into the database
            ArchivedDatasetFileBean fb = new ArchivedDatasetFileBean();
            fb.setName(name + ".zip");
            // logger.info("ODM filename: " + name + ".zip");
            fb.setFileReference(dir + name + ".zip");
            // logger.info("ODM fileReference: " + dir + name + ".zip");
            // current location of the file on the system
            fb.setFileSize((int) newFile.length());
            // logger.info("ODM setFileSize: " + (int)newFile.length() );
            // set the above to compressed size?
            fb.setRunTime((int) time);
            // logger.info("ODM setRunTime: " + (int)time );
            // need to set this in milliseconds, get it passed from above
            // methods?
            fb.setDatasetId(datasetBean.getId());
            // logger.info("ODM setDatasetid: " + ds.getId() );
            fb.setExportFormatBean(efb);
            // logger.info("ODM setExportFormatBean: success" );
            fb.setExportFormatId(efb.getId());
            // logger.info("ODM setExportFormatId: " + efb.getId());
            fb.setOwner(userBean);
            // logger.info("ODM setOwner: " + sm.getUserBean());
            fb.setOwnerId(userBean.getId());
            // logger.info("ODM setOwnerId: " + sm.getUserBean().getId() );
            fb.setDateCreated(new Date(System.currentTimeMillis()));
            boolean write = true;
            ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(ds);
            // eliminating all checks so that we create multiple files, tbh 6-7
            if (write) {
                fbFinal = (ArchivedDatasetFileBean) asdfDAO.create(fb);
            } else {
                logger.info("duplicate found: " + fb.getName());
            }
            // created in database!

        } catch (Exception e) {
            logger.warn(e.getMessage());
            System.out.println("-- exception thrown at createFile: " + e.getMessage());
            logger.info("-- exception thrown at createFile: " + e.getMessage());
            e.printStackTrace();
        }

        return fbFinal.getId();
    }
    
    public ExtractBean generateExtractBean(DatasetBean dsetBean, StudyBean currentStudy, StudyBean parentStudy) {
    	ExtractBean eb = new ExtractBean(ds);
        eb.setDataset(dsetBean);
        eb.setShowUniqueId(SQLInitServlet.getField("show_unique_id"));
        eb.setStudy(currentStudy);
        eb.setParentStudy(parentStudy);
        eb.setDateCreated(new java.util.Date());
        return eb;
    }

}
