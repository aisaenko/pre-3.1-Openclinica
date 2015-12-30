/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.exception.OpenClinicaSystemException;

/**
 * @author Krikor Krumlian
 * 
 */
public class RelationalOpNode extends ExpressionNode {
    Operator op; // The operator.
    ExpressionNode left; // The expression for its left operand.
    ExpressionNode right; // The expression for its right operand.

    RelationalOpNode(Operator op, ExpressionNode left, ExpressionNode right) {
        // Construct a BinOpNode containing the specified data.
        assert op == Operator.PLUS || op == Operator.MINUS || op == Operator.MULTIPLY;
        assert left != null && right != null;
        logger.info("Left : " + left.value());
        assert left.value() != null && right.value() != null;
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    String testCalculate() throws OpenClinicaSystemException {
        double x, y;
        if (ExpressionTreeHelper.isDate(left.testValue()) && ExpressionTreeHelper.isDate(right.testValue())) {
            x = ExpressionTreeHelper.getDate(left.testValue()).getTime();
            y = ExpressionTreeHelper.getDate(right.testValue()).getTime();
        } else {
            x = Double.valueOf(left.testValue());
            y = Double.valueOf(right.testValue());
        }
        return calc(x, y);
    }

    @Override
    String calculate() throws OpenClinicaSystemException {
        double x, y;
        if (ExpressionTreeHelper.isDate(left.value()) && ExpressionTreeHelper.isDate(right.value())) {
            x = ExpressionTreeHelper.getDate(left.value()).getTime();
            y = ExpressionTreeHelper.getDate(right.value()).getTime();
        } else {
            x = Double.valueOf(left.value());
            y = Double.valueOf(right.value());
        }
        return calc(x, y);
    }

    private String calc(double x, double y) throws OpenClinicaSystemException {
        switch (op) {
        case GREATER_THAN:
            return String.valueOf(x > y);
        case GREATER_THAN_EQUAL:
            return String.valueOf(x >= y);
        case LESS_THAN:
            return String.valueOf(x < y);
        case LESS_THAN_EQUAL:
            return String.valueOf(x <= y);
        default:
            return null; // Bad operator!
        }
    }

    private boolean isDouble(String x, String y) {
        try {
            Double.valueOf(x);
            Double.valueOf(y);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Override
    void validate() throws OpenClinicaSystemException {
        if (!(ExpressionTreeHelper.isDate(left.value()) && ExpressionTreeHelper.isDate(right.value())) && !isDouble(left.value(), right.value())) {
            throw new OpenClinicaSystemException(left.value() + " and " + right.value() + " cannot be used with the " + op.toString() + " operator");
        }
    }

    @Override
    void testValidate() throws OpenClinicaSystemException {
        if (!(ExpressionTreeHelper.isDate(left.testValue()) && ExpressionTreeHelper.isDate(right.testValue()))
            && !isDouble(left.testValue(), right.testValue())) {
            throw new OpenClinicaSystemException(left.value() + " and " + right.value() + " cannot be used with the " + op.toString() + " operator");
        }
    }

    @Override
    void printStackCommands() {
        // To evalute the expression on a stack machine, first do
        // whatever is necessary to evaluate the left operand, leaving
        // the answer on the stack. Then do the same thing for the
        // second operand. Then apply the operator (which means popping
        // the operands, applying the operator, and pushing the result).
        left.printStackCommands();
        right.printStackCommands();
        logger.info("  Operator " + op);
    }
}
