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
public class ArithmeticOpNode extends ExpressionNode {
    Operator op;
    ExpressionNode left;
    ExpressionNode right;

    ArithmeticOpNode(Operator op, ExpressionNode left, ExpressionNode right) {
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
        double x = Double.valueOf(left.testValue());
        double y = Double.valueOf(right.testValue());
        return calc(x, y);
    }

    @Override
    String calculate() throws OpenClinicaSystemException {
        double x = Double.valueOf(left.value());
        double y = Double.valueOf(right.value());
        return calc(x, y);

    }

    private String calc(double x, double y) throws OpenClinicaSystemException {
        switch (op) {
        case PLUS:
            return String.valueOf(x + y);
        case MINUS:
            return String.valueOf(x - y);
        case MULTIPLY:
            return String.valueOf(x * y);
        default:
            return null; // Bad operator!
        }
    }

    @Override
    void validate() throws OpenClinicaSystemException {
        try {
            Double.valueOf(left.value());
            Double.valueOf(right.value());
        } catch (NumberFormatException e) {
            throw new OpenClinicaSystemException(left.value() + " and " + right.value() + " cannot be used with the " + op.toString() + " operator");
        }
    }

    @Override
    void testValidate() throws OpenClinicaSystemException {
        try {
            Double.valueOf(left.testValue());
            Double.valueOf(right.testValue());
        } catch (NumberFormatException e) {
            throw new OpenClinicaSystemException(left.value() + " and " + right.value() + " cannot be used with the " + op.toString() + " operator");
        }
    }

    @Override
    void printStackCommands() {
        left.printStackCommands();
        right.printStackCommands();
        logger.info("  Operator " + op);
    }
}
