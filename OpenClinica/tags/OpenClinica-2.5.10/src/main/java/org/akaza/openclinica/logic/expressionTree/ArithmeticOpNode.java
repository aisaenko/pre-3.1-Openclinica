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
        assert op == Operator.PLUS || op == Operator.MINUS || op == Operator.MULTIPLY || op == Operator.DIVIDE;
        assert left != null && right != null;
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    String testCalculate() throws OpenClinicaSystemException {

        String l = left.testValue();
        String r = right.testValue();
        validate(l, r);

        double x = Double.valueOf(l);
        double y = Double.valueOf(r);
        return calc(x, y);
    }

    @Override
    String calculate() throws OpenClinicaSystemException {

        String l = left.value();
        String r = right.value();
        validate(l, r);

        double x = Double.valueOf(l);
        double y = Double.valueOf(r);
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
        case DIVIDE:
            return String.valueOf(x / y);
        default:
            return null; // Bad operator!
        }
    }

    void validate(String l, String r) throws OpenClinicaSystemException {

        try {
            Double.valueOf(l);
            Double.valueOf(r);
        } catch (NumberFormatException e) {
            throw new OpenClinicaSystemException(l + " and " + r + " cannot be used with the " + op.toString() + " operator");
        }
    }

    @Override
    void printStackCommands() {
        left.printStackCommands();
        right.printStackCommands();
        logger.info("  Operator " + op);
    }
}
