/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.bean.rule.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Krikor Krumlian
 * 
 */
public class OpenClinicaV1ExpressionProcessor implements ExpressionProcessor {

    ExpressionBean e;
    Pattern[] pattern;

    public OpenClinicaV1ExpressionProcessor() {

        pattern = new Pattern[4];

        pattern[3] = Pattern.compile("^[^\\[\\]]+|\\S+\\[(ALL|\\d+)\\]$");
        pattern[2] = Pattern.compile("^[^\\[\\]\\s]+$");
        pattern[1] = Pattern.compile("^[^\\[\\]]+|\\S+\\[(ALL|\\d+)\\]$");
        pattern[0] = Pattern.compile("^[^\\[\\]\\s]+$");
    }

    public boolean process() {
        return checkSyntax(e.getValue());
    }

    public boolean checkSyntax(String expression) {
        String[] splitExpression = expression.split("\\.");
        int patternIndex = 0;
        for (int i = splitExpression.length - 1; i >= 0; i--) {
            if (!match(splitExpression[i], pattern[patternIndex++])) {
                return false;
            }
        }
        return true;
    }

    private boolean match(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public void setExpression(ExpressionBean e) {
        this.e = e;
    }

}
