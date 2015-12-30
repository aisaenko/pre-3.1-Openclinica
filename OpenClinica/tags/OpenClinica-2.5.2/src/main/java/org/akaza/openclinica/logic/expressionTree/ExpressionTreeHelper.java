/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Krikor Krumlian
 * 
 */
public class ExpressionTreeHelper {

    protected static final Logger logger = LoggerFactory.getLogger(ExpressionTreeHelper.class.getName());

    static Date getDate(String dateString) {
        logger.info("DateString : " + dateString);
        String[] componentsOfDate = dateString.split("[/|.|-]");
        if (componentsOfDate.length == 3) {
            dateString = componentsOfDate[0] + "/" + componentsOfDate[1] + "/" + componentsOfDate[2];
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date d = sdf.parse(dateString);
                return d;
            } catch (ParseException e) {
                throw new OpenClinicaSystemException(dateString + " Can not be transformed into a Date");
            }
        } else {
            throw new OpenClinicaSystemException(dateString + " Can not be transformed into a Date");
        }
    }

    static boolean isDate(String dateString) {
        logger.info("DateString : " + dateString);
        String[] componentsOfDate = dateString.split("[/|.|-]");
        if (componentsOfDate.length == 3) {
            dateString = componentsOfDate[0] + "/" + componentsOfDate[1] + "/" + componentsOfDate[2];
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                sdf.parse(dateString);
                return true;
            } catch (ParseException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
