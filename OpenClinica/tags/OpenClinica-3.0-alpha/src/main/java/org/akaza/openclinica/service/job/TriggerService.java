package org.akaza.openclinica.service.job;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.Validator;
import org.quartz.JobDataMap;
import org.quartz.SimpleTrigger;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

public class TriggerService {

    public TriggerService() {
        // do nothing, for the moment
    }

    public static final String PERIOD = "periodToRun";
    public static final String TAB = "tab";
    public static final String CDISC = "cdisc";
    public static final String SPSS = "spss";
    public static final String DATASET_ID = "dsId";
    public static final String DATE_START_JOB = "job";
    public static final String EMAIL = "contactEmail";
    public static final String JOB_NAME = "jobName";
    public static final String JOB_DESC = "jobDesc";
    public static final String USER_ID = "user_id";

    public SimpleTrigger generateTrigger(FormProcessor fp, UserAccountBean userAccount) {
        Date startDateTime = fp.getDateTime(DATE_START_JOB);
        // check the above?
        int datasetId = fp.getInt(DATASET_ID);
        String period = fp.getString(PERIOD);
        String email = fp.getString(EMAIL);
        String jobName = fp.getString(JOB_NAME);
        String jobDesc = fp.getString(JOB_DESC);
        String spss = fp.getString(SPSS);
        String tab = fp.getString(TAB);
        String cdisc = fp.getString(CDISC);
        BigInteger interval = new BigInteger("0");
        if ("monthly".equalsIgnoreCase(period)) {
            interval = new BigInteger("2419200000"); // how many
            // milliseconds in
            // a month? should
            // be 24192000000
        } else if ("weekly".equalsIgnoreCase(period)) {
            interval = new BigInteger("604800000"); // how many
            // milliseconds in
            // a week? should
            // be 6048000000
        } else { // daily
            interval = new BigInteger("86400000");// how many
            // milliseconds in a
            // day?
        }
        // set up and commit job here

        SimpleTrigger trigger = new SimpleTrigger(jobName, "DEFAULT", 64000, interval.longValue());

        // set the job detail name,
        // based on our choice of format above
        // what if there is more than one detail?
        // what is the number of times it should repeat?
        // arbitrary large number, 64K should be enough :)

        trigger.setDescription(jobDesc);
        // set just the start date
        trigger.setStartTime(startDateTime);
        trigger.setName(jobName);// + datasetId);
        trigger.setGroup("DEFAULT");// + datasetId);
        trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
        // set job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(DATASET_ID, datasetId);
        jobDataMap.put(PERIOD, period);
        jobDataMap.put(EMAIL, email);
        jobDataMap.put(TAB, tab);
        jobDataMap.put(CDISC, cdisc);
        jobDataMap.put(SPSS, spss);
        jobDataMap.put(USER_ID, userAccount.getId());

        trigger.setJobDataMap(jobDataMap);
        // trigger.setRepeatInterval(interval.longValue());
        // System.out.println("default for volatile: " + trigger.isVolatile());
        trigger.setVolatility(false);
        return trigger;
    }

    public HashMap validateForm(FormProcessor fp, HttpServletRequest request, String[] triggerNames, String properName) {
        Validator v = new Validator(request);
        v.addValidation(JOB_NAME, Validator.NO_BLANKS);
        // need to be unique too
        v.addValidation(JOB_DESC, Validator.NO_BLANKS);
        v.addValidation(EMAIL, Validator.IS_A_EMAIL);
        v.addValidation(PERIOD, Validator.NO_BLANKS);
        v.addValidation(DATE_START_JOB + "Date", Validator.IS_A_DATE);

        // TODO job names will have to be unique, tbh

        String tab = fp.getString(TAB);
        String cdisc = fp.getString(CDISC);
        String spss = fp.getString(SPSS);
        HashMap errors = v.validate();
        if ((tab == "") && (cdisc == "") && (spss == "")) {
            // throw an error here, at least one should work
            // errors.put(TAB, "Error Message - Pick one of the below");
            v.addError(errors, TAB, "Please pick at least one of the below.");
        }
        for (String triggerName : triggerNames) {
            if (triggerName.equals(fp.getString(JOB_NAME)) && (!triggerName.equals(properName))) {
                v.addError(errors, JOB_NAME, "A job with that name already exists.  Please pick another name.");
            }
        }
        return errors;
    }

    public HashMap validateForm(FormProcessor fp, HttpServletRequest request, String[] triggerNames) {
        return validateForm(fp, request, triggerNames, "");
    }
}
