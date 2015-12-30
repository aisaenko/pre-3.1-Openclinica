/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.bean.rule.expression;

import org.akaza.openclinica.exception.OpenClinicaSystemException;

/**
 * @author Krikor Krumlian
 * 
 */
public class ExpressionProcessorFactory {

    public static ExpressionProcessor createExpressionProcessor(ExpressionBean e) {

        ExpressionProcessor ep = null;

        switch (e.getContext()) {
        case OC_RULES_V1: {
            ep = new OpenClinicaV1ExpressionProcessor();
            ep.setExpression(e);
            break;
        }
        default:
            throw new OpenClinicaSystemException("Context : " + e.getContext() + " not Valid");
        }

        return ep;

    }

}
