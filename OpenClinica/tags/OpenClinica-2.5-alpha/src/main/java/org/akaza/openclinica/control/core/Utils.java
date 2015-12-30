/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2007 Akaza Research
 */

package org.akaza.openclinica.control.core;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

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

        // System.out.println("<<< event start date: "+eventsd.toString());
        // System.out.println("<<< subject birth date: "+dob.toString());
        long difference = Math.abs(init - latter);
        double daysDifference = Math.floor(difference / 1000 / 60 / 60 / 24);
        // System.out.println("<<< found age, days difference "+daysDifference);

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
        if (itemValue.length() > 4) {
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

}
