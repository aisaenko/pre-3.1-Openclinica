/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileRow;
import org.akaza.openclinica.bean.extract.CommaReportBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.DisplayItemHeaderBean;
import org.akaza.openclinica.bean.extract.ExportFormatBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.extract.SPSSReportBean;
import org.akaza.openclinica.bean.extract.SPSSVariableNameValidationBean;
import org.akaza.openclinica.bean.extract.TabReportBean;
import org.akaza.openclinica.bean.extract.odm.FullReportBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.ODMBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.logic.odmExport.ClinicalDataCollector;
import org.akaza.openclinica.logic.odmExport.MetaDataCollector;
import org.akaza.openclinica.view.Page;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipOutputStream;

/**
 * Take a dataset and show it in different formats,<BR/> Detect whether or not
 * files exist in the system or database,<BR/> Give the user the option of
 * showing a stored dataset, or refresh the current one.
 * </P>
 * 
 * TODO eventually allow for a thread to be split off, so that exporting can run
 * seperately from the servlet and be retrieved at a later time.
 * 
 * @author thickerson
 * 
 * 
 */
public class ExportDatasetServlet extends SecureController {

    public static String getLink(int dsId) {
        return "ExportDataset?datasetId=" + dsId;
    }

    private static final String DATASET_DIR = SQLInitServlet.getField("filePath") + "datasets" + File.separator;

    private static String WEB_DIR = "/WEB-INF/datasets/";
    // may not use the above, security issue
    public File SASFile;
    public String SASFilePath;
    public File SPSSFile;
    public String SPSSFilePath;
    public File TXTFile;
    public String TXTFilePath;
    public File CSVFile;
    public String CSVFilePath;
    public ArrayList fileList;

    @Override
    public void processRequest() throws Exception {
        DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
        FormProcessor fp = new FormProcessor(request);
        String action = fp.getString("action");
        int datasetId = fp.getInt("datasetId");
        if (datasetId == 0) {
            try {
                DatasetBean dsb = (DatasetBean) session.getAttribute("newDataset");
                datasetId = dsb.getId();
                logger.info("dataset id was zero, trying session: " + datasetId);
            } catch (NullPointerException e) {

                e.printStackTrace();
                logger.info("tripped over null pointer exception");
            }
        }
        DatasetBean db = (DatasetBean) dsdao.findByPK(datasetId);

        /**
         * @vbc 08/06/2008 NEW EXTRACT DATA IMPLEMENTATION get study_id and
         *      parentstudy_id int currentstudyid = currentStudy.getId(); int
         *      parentstudy = currentStudy.getParentStudyId(); if (parentstudy >
         *      0) { // is OK } else { // same parentstudy = currentstudyid; } //
         */
        int currentstudyid = currentStudy.getId();
        // YW 11-09-2008 << modified logic here.
        int parentstudy = currentstudyid;
        // YW 11-09-2008 >>

        StudyBean parentStudy = new StudyBean();
        if (currentStudy.getParentStudyId() > 0) {
            StudyDAO sdao = new StudyDAO(sm.getDataSource());
            parentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
        }

        ExtractBean eb = new ExtractBean(sm.getDataSource());
        eb.setDataset(db);
        eb.setShowUniqueId(SQLInitServlet.getField("show_unique_id"));
        eb.setStudy(currentStudy);
        eb.setParentStudy(parentStudy);
        eb.setDateCreated(new java.util.Date());

        if (StringUtil.isBlank(action)) {
            logger.info("action is blank");
            request.setAttribute("dataset", db);
            logger.info("just set dataset to request");
            // find out if there are any files here:
            File currentDir = new File(DATASET_DIR + db.getId() + File.separator);
            if (!currentDir.isDirectory()) {
                currentDir.mkdirs();
            }
            ArchivedDatasetFileDAO asdfdao = new ArchivedDatasetFileDAO(sm.getDataSource());
            ArrayList fileListRaw = new ArrayList();
            fileListRaw = asdfdao.findByDatasetId(datasetId);
            fileList = new ArrayList();
            Iterator fileIterator = fileListRaw.iterator();
            while (fileIterator.hasNext()) {
                ArchivedDatasetFileBean asdfBean = (ArchivedDatasetFileBean) fileIterator.next();
                // set the correct webPath in each bean here
                // changed here, tbh, 4-18
                // asdfBean.setWebPath(WEB_DIR+db.getId()+"/"+asdfBean.getName());
                // asdfBean.setWebPath(DATASET_DIR+db.getId()+File.separator+
                // asdfBean.getName());
                asdfBean.setWebPath(asdfBean.getFileReference());
                if (new File(asdfBean.getFileReference()).isFile()) {
                    // logger.warn(asdfBean.getFileReference()+" is a
                    // file!");
                    fileList.add(asdfBean);
                } else {
                    logger.warn(asdfBean.getFileReference() + " is NOT a file!");
                }
            }

            logger.warn("");
            logger.warn("file list length: " + fileList.size());
            request.setAttribute("filelist", fileList);

            ArrayList filterRows = ArchivedDatasetFileRow.generateRowsFromBeans(fileList);
            EntityBeanTable table = fp.getEntityBeanTable();
            String[] columns =
                { resword.getString("file_name"), resword.getString("run_time"), resword.getString("file_size"), resword.getString("created_date"),
                    resword.getString("created_by") };
            table.setColumns(new ArrayList(Arrays.asList(columns)));
            table.hideColumnLink(0);
            table.hideColumnLink(1);
            table.hideColumnLink(2);
            table.hideColumnLink(3);
            table.hideColumnLink(4);

            table.setQuery("ExportDataset?datasetId=" + db.getId(), new HashMap());
            // trying to continue...
            session.setAttribute("newDataset", db);
            table.setRows(filterRows);
            table.computeDisplay();

            request.setAttribute("table", table);
            // for the side info bar
            TabReportBean answer = new TabReportBean();

            resetPanel();
            panel.setStudyInfoShown(false);
            setToPanel(resword.getString("study_name"), eb.getStudy().getName());
            setToPanel(resword.getString("protocol_ID"), eb.getStudy().getIdentifier());
            setToPanel(resword.getString("dataset_name"), db.getName());
            setToPanel(resword.getString("created_date"), local_df.format(db.getCreatedDate()));
            setToPanel(resword.getString("dataset_owner"), db.getOwner().getName());
            setToPanel(resword.getString("date_last_run"), local_df.format(db.getDateLastRun()));

            logger.warn("just set file list to request, sending to page");
            forwardPage(Page.EXPORT_DATASETS);
        } else {
            logger.info("**** found action ****: " + action);
            String generateReport = "";
            // generate file, and show screen export
            // String generalFileDir = DATASET_DIR + db.getId() +
            // File.separator;
            // change this up, so that we don't overwrite anything
            String pattern = "yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HHmmssSSS" + File.separator;
            SimpleDateFormat sdfDir = new SimpleDateFormat(pattern);
            String generalFileDir = DATASET_DIR + db.getId() + File.separator + sdfDir.format(new java.util.Date());

            db.setName(db.getName().replaceAll(" ", "_"));
            Page finalTarget = Page.GENERATE_DATASET;
            finalTarget = Page.EXPORT_DATA_CUSTOM;

            // now display report according to format specified

            // TODO revise final target to set to fileReference????
            long sysTimeBegin = System.currentTimeMillis();
            int fId = 0;
            if ("sas".equalsIgnoreCase(action)) {
                // generateReport =
                // dsdao.generateDataset(db,
                // ExtractBean.SAS_FORMAT,
                // currentStudy,
                // parentStudy);
                long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
                String SASFileName = db.getName() + "_sas.sas";
                // logger.info("found data set: "+generateReport);
                this.createFile(SASFileName, generalFileDir, generateReport, db, sysTimeEnd, ExportFormatBean.TXTFILE);
                logger.info("created sas file");
                request.setAttribute("generate", generalFileDir + SASFileName);
                finalTarget.setFileName(generalFileDir + SASFileName);
                // won't work since page creator is private
            } else if ("odm".equalsIgnoreCase(action)) {
                String odmVersion = fp.getString("odmVersion");
                MetaDataCollector mdc = new MetaDataCollector(sm.getDataSource(), db);
                MetaDataCollector.setTextLength(200);
                if (odmVersion != null && "1.3".equals(odmVersion)) {
                    ODMBean odmb = mdc.getODMBean();
                    odmb.setSchemaLocation("http://www.cdisc.org/ns/odm/v1.3 ODM1-3-0.xsd");
                    odmb.setXmlns("http://www.cdisc.org/ns/odm/v1.3");
                    odmb.setODMVersion("1.3");
                }
                mdc.collectFileData();
                ClinicalDataCollector cdc = new ClinicalDataCollector(sm.getDataSource(), db, mdc.getNullClSet());
                cdc.collectOdmClinicalData();
                FullReportBean report = new FullReportBean();
                report.setClinicalData(cdc.getOdmClinicalData());
                report.setOdmStudy(mdc.getOdmStudy());
                report.setOdmBean(mdc.getODMBean());
                report.createOdmXml(Boolean.TRUE);
                long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
                String ODMXMLFileName = mdc.getODMBean().getFileOID() + ".xml";
                fId = this.createFile(ODMXMLFileName, generalFileDir, report.getXmlOutput().toString(), db, sysTimeEnd, ExportFormatBean.XMLFILE);
                request.setAttribute("generate", generalFileDir + ODMXMLFileName);
            } else if ("txt".equalsIgnoreCase(action)) {
                // generateReport =
                // dsdao.generateDataset(db,
                // ExtractBean.TXT_FORMAT,
                // currentStudy,
                // parentStudy);
                TabReportBean answer = new TabReportBean();
                eb = dsdao.getDatasetData(eb, currentstudyid, parentstudy);

                eb.getMetadata();
                eb.computeReport(answer);

                long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
                String TXTFileName = db.getName() + "_tab.xls";

                fId = this.createFile(TXTFileName, generalFileDir, answer.toString(), db, sysTimeEnd, ExportFormatBean.TXTFILE);
                logger.info("created txt file");
                request.setAttribute("generate", generalFileDir + TXTFileName);
                // finalTarget.setFileName(generalFileDir+TXTFileName);
            } else if ("html".equalsIgnoreCase(action)) {
                // html based dataset browser
                TabReportBean answer = new TabReportBean();

                eb = dsdao.getDatasetData(eb, currentstudyid, parentstudy);
                eb.getMetadata();
                eb.computeReport(answer);
                request.setAttribute("dataset", db);
                request.setAttribute("extractBean", eb);
                finalTarget = Page.GENERATE_DATASET_HTML;

            } else if ("spss".equalsIgnoreCase(action)) {
                String SPSSFileName = db.getName() + "_data_spss.dat";
                String DDLFileName = db.getName() + "_ddl_spss.sps";
                String ZIPFileName = db.getName() + "_spss";

                SPSSVariableNameValidationBean svnvbean = new SPSSVariableNameValidationBean();
                SPSSReportBean answer = new SPSSReportBean();
                answer.setDatFileName(SPSSFileName);
                eb = dsdao.getDatasetData(eb, currentstudyid, parentstudy);

                eb.getMetadata();

                eb.computeReport(answer);

                answer.setItems(eb.getItemNames());// set up items here to get
                // itemMetadata

                // set up response sets for each item here
                ItemDAO itemdao = new ItemDAO(sm.getDataSource());
                ItemFormMetadataDAO imfdao = new ItemFormMetadataDAO(sm.getDataSource());
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

                HashMap eventDescs = eb.getEventDescriptions();

                eventDescs.put("SubjID", resword.getString("study_subject_ID"));
                eventDescs.put("ProtocolID", resword.getString("protocol_ID_site_ID"));
                eventDescs.put("DOB", resword.getString("date_of_birth"));
                eventDescs.put("YOB", resword.getString("year_of_birth"));
                eventDescs.put("Gender", resword.getString("gender"));
                answer.setDescriptions(eventDescs);

                ArrayList generatedReports = new ArrayList();
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
                fId = this.createFile(ZIPFileName, titles, generalFileDir, generatedReports, db, sysTimeEnd, ExportFormatBean.TXTFILE);

                /*
                 * String dataReport = (String) generatedReports.get(0);
                 * this.createFile(SPSSFileName,generalFileDir, dataReport, db,
                 * sysTimeEnd, ExportFormatBean.TXTFILE); logger.info("*** just
                 * created test spss data file: " + SPSSFileName);
                 * 
                 * String ddlReport = (String)generatedReports.get(1);
                 * this.createFile(DDLFileName,generalFileDir,ddlReport,db,
                 * sysTimeEnd, ExportFormatBean.TXTFILE); logger.info("*** just
                 * created test spss ddl file: " + DDLFileName);
                 */

                /*
                 * at this point, we want to redirect to the main page and add a
                 * message that the two files are below, available for download
                 */
                request.setAttribute("generate", generalFileDir + DDLFileName);
            } else if ("csv".equalsIgnoreCase(action)) {
                CommaReportBean answer = new CommaReportBean();
                eb = dsdao.getDatasetData(eb, currentstudyid, parentstudy);
                eb.getMetadata();
                eb.computeReport(answer);
                long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
                // logger.info("found data set: "+generateReport);
                String CSVFileName = db.getName() + "_comma.txt";
                fId = this.createFile(CSVFileName, generalFileDir, answer.toString(), db, sysTimeEnd, ExportFormatBean.CSVFILE);
                logger.info("just created csv file");
                request.setAttribute("generate", generalFileDir + CSVFileName);
                // finalTarget.setFileName(generalFileDir+CSVFileName);
            } else if ("excel".equalsIgnoreCase(action)) {
                // HSSFWorkbook excelReport = dsdao.generateExcelDataset(db,
                // ExtractBean.XLS_FORMAT,
                // currentStudy,
                // parentStudy);
                long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
                // TODO this will change and point to a created excel
                // spreadsheet, tbh
                String excelFileName = db.getName() + "_excel.xls";
                // fId = this.createFile(excelFileName,
                // generalFileDir,
                // excelReport,
                // db, sysTimeEnd,
                // ExportFormatBean.EXCELFILE);
                // logger.info("just created csv file, for excel output");
                // response.setHeader("Content-disposition","attachment;
                // filename="+CSVFileName);
                // logger.info("csv file name: "+CSVFileName);

                finalTarget = Page.GENERATE_EXCEL_DATASET;

                // response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=" + db.getName() + "_excel.xls");
                request.setAttribute("generate", generalFileDir + excelFileName);
                logger.info("set 'generate' to :" + generalFileDir + excelFileName);
                // excelReport.write(stream);
                // stream.flush();
                // stream.close();
                // finalTarget.setFileName(WEB_DIR+db.getId()+"/"+excelFileName);
            }
            // request.setAttribute("generate",generateReport);
            // TODO might not set the above to request and instead aim the
            // user directly to the generated file?
            // logger.info("*** just set generated report to request: "+action);
            // create the equivalent to:
            // <%@page contentType="application/vnd.ms-excel"%>
            if (!finalTarget.equals(Page.GENERATE_EXCEL_DATASET) && !finalTarget.equals(Page.GENERATE_DATASET_HTML)) {
                // to catch all the others and try to set a new path for file
                // capture
                // tbh, 4-18-05
                // request.setAttribute("generate",finalTarget.getFileName());
                // TODO changing path to show refresh page, then window with
                // link to download file, tbh 06-08-05
                // finalTarget.setFileName(
                // "/WEB-INF/jsp/extract/generatedFileDataset.jsp");
                finalTarget.setFileName("" + "/WEB-INF/jsp/extract/generateMetadataCore.jsp");
                // also set up table here???
                ArchivedDatasetFileDAO asdfdao = new ArchivedDatasetFileDAO(sm.getDataSource());

                ArchivedDatasetFileBean asdfBean = (ArchivedDatasetFileBean) asdfdao.findByPK(fId);
                // *** do we need this below? tbh
                ArrayList newFileList = new ArrayList();
                newFileList.add(asdfBean);
                // request.setAttribute("filelist",newFileList);

                ArrayList filterRows = ArchivedDatasetFileRow.generateRowsFromBeans(newFileList);
                EntityBeanTable table = fp.getEntityBeanTable();
                String[] columns =
                    { resword.getString("file_name"), resword.getString("run_time"), resword.getString("file_size"), resword.getString("created_date"),
                        resword.getString("created_by") };

                table.setColumns(new ArrayList(Arrays.asList(columns)));
                table.hideColumnLink(0);
                table.hideColumnLink(1);
                table.hideColumnLink(2);
                table.hideColumnLink(3);
                table.hideColumnLink(4);

                // table.setQuery("ExportDataset?datasetId=" +db.getId(), new
                // HashMap());
                // trying to continue...
                // session.setAttribute("newDataset",db);
                request.setAttribute("dataset", db);
                request.setAttribute("file", asdfBean);
                table.setRows(filterRows);
                table.computeDisplay();

                request.setAttribute("table", table);
                // *** do we need this above? tbh
            }
            logger.info("set first part of 'generate' to :" + generalFileDir);
            logger.info("found file name: " + finalTarget.getFileName());
            forwardPage(finalTarget);
        }
    }

    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
            || currentRole.getRole().equals(Role.INVESTIGATOR) || currentRole.getRole().equals(Role.MONITOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU, resexception.getString("not_allowed_access_extract_data_servlet"), "1");

    }

    public int createFile(String zipName, ArrayList names, String dir, ArrayList contents, DatasetBean ds, long time, ExportFormatBean efb) {
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
            fb.setName(zipName + ".zip");
            fb.setFileReference(dir + zipName + ".zip");
            // current location of the file on the system
            // fb.setFileSize((int)newFile.length());
            fb.setFileSize(totalSize);
            // set the above to compressed size?
            fb.setRunTime((int) time);
            // need to set this in milliseconds, get it passed from above
            // methods?
            fb.setDatasetId(ds.getId());
            fb.setExportFormatBean(efb);
            fb.setExportFormatId(efb.getId());
            fb.setOwner(sm.getUserBean());
            fb.setOwnerId(sm.getUserBean().getId());

            boolean write = true;
            ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(sm.getDataSource());

            if (write) {
                fbFinal = (ArchivedDatasetFileBean) asdfDAO.create(fb);
                logger.info("Created ADSFile!: " + fbFinal.getId() + " for " + zipName + ".zip");
            } else {
                logger.info("duplicate found: " + fb.getName());
            }
            // created in database!

        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        }
        return fbFinal.getId();
    }

    public int createFile(String name, String dir, String content, DatasetBean ds, long time, ExportFormatBean efb) {
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
            // java.io.BufferedInputStream bis = new
            // java.io.BufferedInputStream(is);
            // java.io.PrintStream newOut = new java.io.PrintStream(
            // new java.io.BufferedOutputStream(
            // new FileOutputStream(newFile)));
            // java.io.ByteArrayOutputStream newOut = new
            // ByteArrayOutputStream();
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
            fb.setDatasetId(ds.getId());
            // logger.info("ODM setDatasetid: " + ds.getId() );
            fb.setExportFormatBean(efb);
            // logger.info("ODM setExportFormatBean: success" );
            fb.setExportFormatId(efb.getId());
            // logger.info("ODM setExportFormatId: " + efb.getId());
            fb.setOwner(sm.getUserBean());
            // logger.info("ODM setOwner: " + sm.getUserBean());
            fb.setOwnerId(sm.getUserBean().getId());
            // logger.info("ODM setOwnerId: " + sm.getUserBean().getId() );

            boolean write = true;
            ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(sm.getDataSource());
            // eliminating all checks so that we create multiple files, tbh 6-7
            /*
             * ArrayList col = asdfDAO.findByDatasetId(fb.getDatasetId());
             * Iterator checkIt = col.iterator(); while (checkIt.hasNext()) {
             * ArchivedDatasetFileBean fbCheck =
             * (ArchivedDatasetFileBean)checkIt.next(); if
             * ((fbCheck.getName().equals(fb.getName()))&&
             * (fbCheck.getExportFormatId()==fb.getExportFormatId())&&
             * (fbCheck.getFileReference().equals(fb.getFileReference()))) {
             * write = false; } }
             */
            if (write) {
                fbFinal = (ArchivedDatasetFileBean) asdfDAO.create(fb);

            } else {
                logger.info("duplicate found: " + fb.getName());
            }
            // created in database!

        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        }

        return fbFinal.getId();
    }

    public int createFile(String name, String dir, HSSFWorkbook content, DatasetBean ds, long time, ExportFormatBean efb) {
        try {
            File complete = new File(dir);
            if (!complete.isDirectory()) {
                complete.mkdirs();
            }
            File newFile = new File(complete, name);
            newFile.setLastModified(System.currentTimeMillis());
            FileOutputStream fileOut = new FileOutputStream(newFile);
            content.write(fileOut);
            fileOut.close();

            ArchivedDatasetFileBean fb = new ArchivedDatasetFileBean();
            fb.setName(name);
            fb.setFileReference(dir + name);
            // current location of the file on the system
            fb.setFileSize((int) newFile.length());
            fb.setRunTime((int) time);
            // need to set this in milliseconds, get it passed from above
            // methods?
            fb.setDatasetId(ds.getId());
            fb.setExportFormatBean(efb);
            fb.setExportFormatId(efb.getId());
            fb.setOwner(sm.getUserBean());
            fb.setOwnerId(sm.getUserBean().getId());

            boolean write = true;
            int fbCheckId = 0;
            ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(sm.getDataSource());
            /*
             * ArrayList col = asdfDAO.findByDatasetId(fb.getDatasetId());
             * Iterator checkIt = col.iterator(); while (checkIt.hasNext()) {
             * ArchivedDatasetFileBean fbCheck =
             * (ArchivedDatasetFileBean)checkIt.next(); if
             * ((fbCheck.getName().equals(fb.getName()))&&
             * (fbCheck.getExportFormatId()==fb.getExportFormatId())&&
             * (fbCheck.getFileReference().equals(fb.getFileReference()))) {
             * write = false; fbCheckId = fbCheck.getId(); } }
             */
            if (write) {
                ArchivedDatasetFileBean fbFinal = (ArchivedDatasetFileBean) asdfDAO.create(fb);
                logger.info("Created ADSFile: " + fbFinal.getId());
                return fbFinal.getId();
            } else {
                logger.info("duplicate found: " + fb.getName());
                return fbCheckId;
            }
            // created in database!

        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public ArchivedDatasetFileBean generateFileBean(File datasetFile, String relativePath, int formatId) {
        ArchivedDatasetFileBean adfb = new ArchivedDatasetFileBean();
        adfb.setName(datasetFile.getName());
        if (datasetFile.canRead()) {
            logger.info("File can be read");
        } else {
            logger.info("File CANNOT be read");
        }
        logger.info("Found file length: " + datasetFile.length());
        logger.info("Last Modified: " + datasetFile.lastModified());
        adfb.setFileSize(new Long(datasetFile.length()).intValue());
        adfb.setExportFormatId(formatId);
        adfb.setWebPath(relativePath);
        adfb.setDateCreated(new java.util.Date(datasetFile.lastModified()));
        return adfb;
    }
}
