package org.akaza.openclinica.service.job;

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.extract.SPSSReportBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.extract.GenerateExtractFileService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.sql.DataSource;

public class ExampleSpringJob extends QuartzJobBean {

    // example code here
    private String message;

    // example code here
    public void setMessage(String message) {
        this.message = message;
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    // variables to pull out
    public static final String PERIOD = "periodToRun";
    public static final String TAB = "tab";
    public static final String CDISC = "cdisc";
    public static final String SPSS = "spss";
    public static final String DATASET_ID = "dsId";
    public static final String EMAIL = "contactEmail";
    public static final String USER_ID = "user_id";
    public static final String STUDY_NAME = "study_name";

    private static final String DATASET_DIR = SQLInitServlet.getField("filePath") + "datasets" + File.separator;

    // private BasicDataSource basicDataSource;
    private DataSource dataSource;
    private GenerateExtractFileService generateFileService;
    private UserAccountBean userBean;
    private JobDetailBean jobDetailBean;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // need to generate a Locale so that user beans and other things will
        // generate normally
        // TODO make dynamic?
        Locale locale = new Locale("en-US");
        ResourceBundleProvider.updateLocale(locale);
        ResourceBundle pageMessages = ResourceBundleProvider.getPageMessagesBundle();
        // logger.debug("--");
        // logger.debug("-- executing a job " + message + " at " + new
        // java.util.Date().toString());
        JobDataMap dataMap = context.getMergedJobDataMap();
        SimpleTrigger trigger = (SimpleTrigger) context.getTrigger();
        try {
            ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
            dataSource = (DataSource) appContext.getBean("dataSource");
            AuditEventDAO auditEventDAO = new AuditEventDAO(dataSource);
            // Scheduler scheduler = context.getScheduler();
            // JobDetail detail = context.getJobDetail();
            // jobDetailBean = (JobDetailBean) detail;
            /*
             * data map here should coincide with the job data map found in
             * CreateJobExportServlet, with the following code: jobDataMap = new
             * JobDataMap(); jobDataMap.put(DATASET_ID, datasetId);
             * jobDataMap.put(PERIOD, period); jobDataMap.put(EMAIL, email);
             * jobDataMap.put(TAB, tab); jobDataMap.put(CDISC, cdisc);
             * jobDataMap.put(SPSS, spss);
             */
            String alertEmail = dataMap.getString(EMAIL);
            int dsId = dataMap.getInt(DATASET_ID);
            String tab = dataMap.getString(TAB);
            String cdisc = dataMap.getString(CDISC);
            String spss = dataMap.getString(SPSS);
            int userId = dataMap.getInt(USER_ID);

            // String datasetId = dataMap.getString(DATASET_ID);
            // int dsId = new Integer(datasetId).intValue();
            // String userAcctId = dataMap.getString(USER_ID);
            // int userId = new Integer(userAcctId).intValue();
            // why the flip-flop? if one property is set to 'true' we can
            // see jobs in another screen but all properties have to be
            // strings

            logger.debug("-- found the job: " + dsId + " dataset id");

            // for (Iterator it = dataMap.entrySet().iterator(); it.hasNext();)
            // {
            // java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            // Object key = entry.getKey();
            // Object value = entry.getValue();
            // // logger.debug("-- found datamap property: " + key.toString() +
            // // " : " + value.toString());
            // }
            HashMap fileName = new HashMap<String, Integer>();
            if (dsId > 0) {
                // trying to not throw an error if there's no dataset id
                DatasetDAO dsdao = new DatasetDAO(dataSource);
                DatasetBean datasetBean = (DatasetBean) dsdao.findByPK(dsId);
                StudyDAO studyDao = new StudyDAO(dataSource);
                UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);
                // hmm, next three lines DRY?
                String pattern = "yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HHmmssSSS" + File.separator;
                SimpleDateFormat sdfDir = new SimpleDateFormat(pattern);
                String generalFileDir = DATASET_DIR + datasetBean.getId() + File.separator + sdfDir.format(new java.util.Date());

                // logger.debug("-- created the following dir: " +
                // generalFileDir);
                long sysTimeBegin = System.currentTimeMillis();
                // set up the user bean here, tbh
                // logger.debug("-- gen tab file 00");

                userBean = (UserAccountBean) userAccountDAO.findByPK(userId);
                // needs to also be captured by the servlet, tbh
                // logger.debug("-- gen tab file 00");
                generateFileService = new GenerateExtractFileService(dataSource, userBean);

                // logger.debug("-- gen tab file 00");

                StudyBean activeStudy = (StudyBean) studyDao.findByPK(userBean.getActiveStudyId());
                StudyBean parentStudy = new StudyBean();
                logger.debug("active study: " + userBean.getActiveStudyId() + " parent study: " + activeStudy.getParentStudyId());
                if (activeStudy.getParentStudyId() > 0) {
                    // StudyDAO sdao = new StudyDAO(sm.getDataSource());
                    parentStudy = (StudyBean) studyDao.findByPK(activeStudy.getParentStudyId());
                } else {
                    parentStudy = activeStudy;
                    // covers a bug in tab file creation, tbh 01/2009
                }

                logger.debug("-- found extract bean ");

                ExtractBean eb = generateFileService.generateExtractBean(datasetBean, activeStudy, parentStudy);

                StringBuffer message = new StringBuffer();
                StringBuffer auditMessage = new StringBuffer();
                // use resource bundle page messages to generate the email, tbh
                // 02/2009
                // message.append(pageMessages.getString("html_email_header_1")
                // + " " + alertEmail +
                // pageMessages.getString("html_email_header_2") + "<br/>");
                message.append("<p>" + pageMessages.getString("email_header_1") + " " + EmailEngine.getAdminEmail() + " "
                    + pageMessages.getString("email_header_2") + " Job Execution " + pageMessages.getString("email_header_3") + "</p>");
                message.append("<P>Dataset: " + datasetBean.getName() + "</P>");
                message.append("<P>Study: " + activeStudy.getName() + "</P>");
                message.append("<p>" + pageMessages.getString("html_email_body_1") + datasetBean.getName() + pageMessages.getString("html_email_body_2")
                    + SQLInitServlet.getField("sysURL") + pageMessages.getString("html_email_body_3") + "</p>");
                // logger.debug("-- gen tab file 00");
                if ("1".equals(tab)) {

                    logger.debug("-- gen tab file 01");
                    fileName = generateFileService.createTabFile(eb, sysTimeBegin, generalFileDir, datasetBean, activeStudy.getId(), parentStudy.getId());
                    message.append("<p>" + pageMessages.getString("html_email_body_4") + " " + getFileNameStr(fileName)
                        + pageMessages.getString("html_email_body_4_5") + SQLInitServlet.getField("sysURL.base") + "AccessFile?fileId="
                        + getFileIdInt(fileName) + pageMessages.getString("html_email_body_3") + "</p>");
                    auditMessage.append("You can access your tab-delimited file <a href='AccessFile?fileId=" + getFileIdInt(fileName) + "'>here</a>.<br/>");
                }

                if ("1".equals(cdisc)) {
                    String odmVersion = "oc1.2";// default - 1.2, need to
                    // externalize this somehow...
                    fileName = generateFileService.createODMFile(odmVersion, sysTimeBegin, generalFileDir, datasetBean, activeStudy);
                    logger.debug("-- gen odm file");
                    message.append("<p>" + pageMessages.getString("html_email_body_4") + " " + getFileNameStr(fileName)
                        + pageMessages.getString("html_email_body_4_5") + SQLInitServlet.getField("sysURL.base") + "AccessFile?fileId="
                        + getFileIdInt(fileName) + pageMessages.getString("html_email_body_3") + "</p>");
                    auditMessage.append("You can access your ODM XML file <a href='AccessFile?fileId=" + getFileIdInt(fileName) + "'>here</a>.<br/>");
                }

                if ("1".equals(spss)) {
                    SPSSReportBean answer = new SPSSReportBean();
                    fileName = generateFileService.createSPSSFile(datasetBean, eb, activeStudy, parentStudy, sysTimeBegin, generalFileDir, answer);
                    logger.debug("-- gen spss file");
                    message.append("<p>" + pageMessages.getString("html_email_body_4") + " " + getFileNameStr(fileName)
                        + pageMessages.getString("html_email_body_4_5") + SQLInitServlet.getField("sysURL.base") + "AccessFile?fileId="
                        + getFileIdInt(fileName) + pageMessages.getString("html_email_body_3") + "</p>");
                    auditMessage.append("You can access your SPSS files <a href='AccessFile?fileId=" + getFileIdInt(fileName) + "'>here</a>.<br/>");
                }

                // wrap up the message, and send the email
                message.append("<p>" + pageMessages.getString("html_email_body_5") + "</P><P>" + pageMessages.getString("email_footer"));
                try {
                    EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());

                    // String emailBodyString = email.toString();
                    String emailBodyString = message.toString();
                    // add mutiple emails to the list split by commas
                    // String[] alterEmails = alertEmail.split(",");
                    // for (String email : alterEmails) {
                    em.processHtml(alertEmail.trim(), EmailEngine.getAdminEmail(), "Job ran for " + datasetBean.getName(), emailBodyString);
                    // }
                    // addPageMessage(respage.getString(
                    // "your_message_sent_succesfully"));
                } catch (MessagingException me) {
                    // addPageMessage(respage.getString(
                    // "mail_cannot_be_sent_to_admin"));
                    me.printStackTrace();
                }
                TriggerBean triggerBean = new TriggerBean();
                triggerBean.setDataset(datasetBean);
                triggerBean.setUserAccount(userBean);
                triggerBean.setFullName(trigger.getName());
                auditEventDAO.createRowForExtractDataJobSuccess(triggerBean, auditMessage.toString());
            } else {
                TriggerBean triggerBean = new TriggerBean();
                // triggerBean.setDataset(datasetBean);
                triggerBean.setUserAccount(userBean);
                triggerBean.setFullName(trigger.getName());
                auditEventDAO.createRowForExtractDataJobFailure(triggerBean);
                // logger.debug("-- made it here for some reason, ds id: "
                // + dsId);
            }

            // logger.debug("-- generated file: " + fileNameStr);
            // dataSource.
        } catch (Exception e) {
            // TODO Auto-generated catch block -- ideally should generate a fail
            // msg here, tbh 02/2009
            logger.debug("-- found exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getFileNameStr(HashMap fileName) {
        String fileNameStr = "";
        for (Iterator it = fileName.entrySet().iterator(); it.hasNext();) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            fileNameStr = (String) key;
            Integer fileID = (Integer) value;
            // fId = fileID.intValue();
        }
        return fileNameStr;
    }

    private int getFileIdInt(HashMap fileName) {
        // String fileNameStr = "";
        Integer fileID = new Integer(0);
        for (Iterator it = fileName.entrySet().iterator(); it.hasNext();) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            // fileNameStr = (String) key;
            fileID = (Integer) value;
            // fId = fileID.intValue();
        }
        return fileID.intValue();
    }

    private DataSource getDataSource(Scheduler scheduler) {
        try {
            ApplicationContext context = (ApplicationContext) scheduler.getContext().get("applicationContext");// dataMap
            // : (BasicDataSource) context.getBean("dataSource");
            dataSource = this.dataSource != null ? dataSource : (DataSource) context.getBean("dataSource"); // basicDataSource

        } catch (Exception e) {
            // TODO Auto-generated catch block
            // logger.debug("-- found an exception: " + e.getMessage());
            e.printStackTrace();
        }

        return dataSource;
    }

}
