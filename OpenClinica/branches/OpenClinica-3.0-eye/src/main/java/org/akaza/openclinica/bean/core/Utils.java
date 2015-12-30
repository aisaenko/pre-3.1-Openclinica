/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2007 Akaza Research
 */

package org.akaza.openclinica.bean.core;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

public class Utils {

    private static Utils ref;

    private Utils() {
    }

    public static Utils getInstacne() {
        if (ref == null) {
            ref = new Utils();
        }
        return ref;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * This Method will compare the two Dates and return a String with number of
     * years , weeks and days.
     *
     * @author Krikor Krumlian 10/20/2006
     * @param eventStartDate
     *            The event start date
     * @param subjectDOB
     *            the Subject's date of birth
     * @return
     */
    public String processAge(Date eventStartDate, Date subjectDOB) {
        int years = 0, months = 0, days = 0;
        String ret = "";

        if (eventStartDate == null || subjectDOB == null) {
            return "N/A";
        }

        // example : 10/20/2006
        Calendar eventsd = Calendar.getInstance();
        eventsd.setTime(eventStartDate);
        long init = eventsd.getTimeInMillis();

        // example : 10/20/1990
        Calendar dob = Calendar.getInstance();
        dob.setTime(subjectDOB);
        long latter = dob.getTimeInMillis();

        // logger.info("<<< event start date: "+eventsd.toString());
        // logger.info("<<< subject birth date: "+dob.toString());
        long difference = Math.abs(init - latter);
        double daysDifference = Math.floor(difference / 1000 / 60 / 60 / 24);
        // logger.info("<<< found age, days difference "+daysDifference);

        if (daysDifference > 200 * 365.24) {
            return "N/A";
            // year is probably set to 0001, in which case DOB was not used but
            // is now
        }

        // Get the number of years
        while (daysDifference - 365.24 > 0) {
            daysDifference = daysDifference - 365.24;
            years++;
        }

        // Get the number of months
        while (daysDifference - 30.43 > 0) {
            daysDifference = daysDifference - 30.43;
            months++;
        }

        // Get the number of days
        while (daysDifference - 1 >= 0) {
            daysDifference = daysDifference - 1;
            days++;
            // was off by one day, hope this fixes it, tbh 102007
        }
        ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();
        if (years > 0)
            ret = ret + years + " " + reswords.getString("Years") + " - ";
        if (months > 0)
            ret = ret + months + " " + reswords.getString("Months") + " - ";
        if (days > 0)
            ret = ret + days + " " + reswords.getString("Days");
        // also changed the above, tbh 10 2007
        if (ret.equals(""))
            ret = reswords.getString("Less_than_a_day");
        return ret;

    }

    /**
     * Convert string with from_pattern to string with to_pattern
     *
     * @param value
     * @return
     *
     * @author ywang 12-06-2007
     */
    public static String convertedItemDateValue(String itemValue, String from_pattern, String to_pattern) {
        String temp = itemValue == null ? null : itemValue.trim();
        if (itemValue != null && temp.length() > 4 && temp.length() == from_pattern.length()) {
            SimpleDateFormat sdf = new SimpleDateFormat(from_pattern);
            sdf.setLenient(false);
            try {
                java.util.Date date = sdf.parse(itemValue);
                return new SimpleDateFormat(to_pattern).format(date);
            } catch (ParseException fe) {
                return itemValue;
            }
        } else {
            return itemValue;
        }
    }

    /**
     * Zip StringBuffer to a file
     *
     * @param fileName
     * @param filePath
     * @param content
     *
     * @author ywang (07-2008)
     */
    public static boolean createZipFile(String fileName, String filePath, StringBuffer content) {
        try {
            File dir = new File(filePath);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            ZipOutputStream z = new ZipOutputStream(new FileOutputStream(new File(dir, fileName + ".zip")));
            z.putNextEntry(new ZipEntry(fileName));
            byte[] bytes = content.toString().getBytes();
            z.write(bytes, 0, bytes.length);
            z.closeEntry();
            z.finish();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * age = the_year_of_controlDate - the_year_of_birthDate
     *
     * @param birthDate
     * @param controlDate
     * @return
     */
    public static Integer getAge(Date birthDate, Date controlDate) {
        Integer age = -1;
        if (birthDate.before(controlDate)) {
            Calendar dateOfBirth = Calendar.getInstance();
            dateOfBirth.setTime(birthDate);
            Calendar theDate = Calendar.getInstance();
            theDate.setTime(controlDate);
            age = theDate.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
            Calendar today = Calendar.getInstance();
            // add the age to the year to see if it's happened yet
            dateOfBirth.add(Calendar.YEAR, age);
            // subtract one from the age if the birthday hasn't happened yet
            if (today.before(dateOfBirth)) {
                age--;
            }
        }
        return age;
    }

    public static String getAttachedFilePath(StudyBean study) {
        String attachedFilePath = SQLInitServlet.getField("attached_file_location");
        if (attachedFilePath == null || attachedFilePath.length() <= 0) {
            attachedFilePath = SQLInitServlet.getField("filePath") + "attached_files" + File.separator + study.getIdentifier() + File.separator;
        } else {
            attachedFilePath += study.getIdentifier() + File.separator;
        }
        return attachedFilePath;
    }

    /**
     * Checks the msg to see if there is any url in it. If there is any url it inserts link tags so that it
     * is clickable on the page.
     * @param msg text to search
     * @return
     */
    public static String enableUrlInText(String msg) {
        if (msg != null && msg.length() > 0) {
            if (!msg.contains("<a href") && (msg.contains("http://") || msg.contains("www."))) {
                StringBuilder message = new StringBuilder(msg);
                int startingIndex = 0;
                if (msg.contains("http://")) {
                    startingIndex = message.indexOf("http://");
                } else {
                    startingIndex = message.indexOf("www.");
                }
                String sub = message.substring(startingIndex).trim();
                int urlEndIndex = sub.indexOf(' ');
                String url = sub.substring(0, urlEndIndex);
                StringBuilder newUrl = new StringBuilder();
                if (msg.contains("http://")) {
                    newUrl.insert(0, "<a href='"+ url +"' target='_blank'>" + url + "</a>" );
                } else {
                    newUrl.insert(0, "<a href='http://"+ url +"' target='_blank'>" + url + "</a>" );
                }
                message.delete(startingIndex, startingIndex + url.length());
                message.insert(startingIndex, newUrl);
                return message.toString();
            }
        }
        return msg;
    }
    
    public static String getAttachedFileRootPath() {
        String rootPath = SQLInitServlet.getField("attached_file_location");
        if (rootPath == null || rootPath.length() <= 0) {
            rootPath = SQLInitServlet.getField("filePath") + "attached_files" + File.separator;
        }
        return rootPath;
    }
    
    /*
     * see if a regular expression fits
     * @author thickerson
     * August 5th 2010
     */
    public static boolean isMatchingRegexp(String input, String regexp) {
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(input);
        return m.matches();
    }
    
    public static boolean isWithinRegexp(String input, String regexp) {
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(input);
        return m.find();
    }
}
