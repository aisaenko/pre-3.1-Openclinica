/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.bean.rule.expression;

/**
 * @author Krikor Krumlian
 * 
 */
public interface ExpressionProcessor {

    boolean process();

    void setExpression(ExpressionBean e);

}
