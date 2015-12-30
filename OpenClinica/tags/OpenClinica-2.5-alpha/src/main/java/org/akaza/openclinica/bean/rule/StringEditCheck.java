package org.akaza.openclinica.bean.rule;

import java.util.logging.Logger;

/*
 * String edit checks should only support EQUAL , NOTEQUAL
 */

public class StringEditCheck implements EditCheckInterface {

    // if ( x = y ) is true?
    String xSourceValue;
    String ySourceValue;
    Operator operator;
    protected final Logger logger = Logger.getLogger(getClass().getName());

    public StringEditCheck(String sourceValue, String sourceValue2, Operator operator) {
        super();
        xSourceValue = sourceValue;
        ySourceValue = sourceValue2;
        if (!isOperatorAccepted(operator)) {
            throw new RuntimeException("The provided Operator is not Accepted");
        }
    }

    private boolean isOperatorAccepted(Operator suppliedOperator) {
        if (suppliedOperator == Operator.EQUAL || suppliedOperator == Operator.NOTEQUAL) {
            this.operator = suppliedOperator;
            return true;
        }
        return false;
    }

    public boolean check() {
        logger.info("xSourceValue : " + xSourceValue);
        logger.info("xSourceValue : " + ySourceValue);
        logger.info("Operator : " + operator);
        if (operator == Operator.EQUAL) {
            return xSourceValue.equals(ySourceValue) ? true : false;
        }
        if (operator == Operator.NOTEQUAL) {
            return !xSourceValue.equals(ySourceValue) ? true : false;
        }
        return false;
    }
}