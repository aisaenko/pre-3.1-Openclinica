/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.service.rule.expression.ExpressionService;

/**
 * @author Krikor Krumlian
 * 
 */
public class OpenClinicaVariableNode extends ExpressionNode {
    String number;
    ExpressionService expressionService;
    ExpressionObjectWrapper expressionWrapper;

    OpenClinicaVariableNode(String val) {
        number = val;
        // validate();
    }

    OpenClinicaVariableNode(String val, ExpressionObjectWrapper expressionWrapper) {
        this.expressionWrapper = expressionWrapper;
        number = val;
        // validate();
    }

    @Override
    String testCalculate() throws OpenClinicaSystemException {
        ItemBean item = getExpressionService().getItemBeanFromExpression(number);
        String testString = "test";
        String testInt = "1";
        String testBoolean = "true";
        String testDate = "2008/01/01";
        if (item != null) {
            ItemDataType itemDataType = ItemDataType.get(item.getItemDataTypeId());
            switch (itemDataType.getId()) {
            case 1:
                return testBoolean;
            case 2:
                return testBoolean;
            case 3:
                return testString;
            case 4:
                return testString;
            case 5:
                return testString;
            case 6:
                return testInt;
            case 7:
                return testInt;
            case 8:
                return testString;
            case 9:
                return testDate;
            default:
                throw new OpenClinicaSystemException("Type not supported");
            }
        } else {
            throw new OpenClinicaSystemException("Item not found");
        }
    }

    @Override
    String calculate() throws OpenClinicaSystemException {
        // The value of the node is the number that it contains.
        // return number;
        return getExpressionService().evaluateExpression(number);
    }

    @Override
    void validate() throws OpenClinicaSystemException {
        // logger.info(number + " : " +
        // getExpressionService().checkSyntax(number));
        logger.info("e" + expressionWrapper.getRuleSet());
        if (!getExpressionService().ruleExpressionChecker(number)) {
            logger.info("Go down");
            throw new OpenClinicaSystemException("The following : " + number + " is not valid");
        }
    }

    @Override
    void printStackCommands() {
        // On a stack machine, just push the number onto the stack.
        logger.info("  Push " + number);
    }

    private ExpressionService getExpressionService() {
        expressionService = this.expressionService != null ? expressionService : new ExpressionService(expressionWrapper);
        return expressionService;
    }

}