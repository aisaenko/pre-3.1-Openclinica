package org.akaza.openclinica.logic.score.function;

import java.util.List;
import java.util.Vector;

public class Sum extends AbstractFunction {
    public Sum() {
        super();
    }

    /**
     * If one argument is "", then the value of this function will be "" too.
     *
     * All arguments should has been parsed to numbers before this execute()
     * method is called.
     *
     * @see Function#execute
     */
    public void execute() {
        logger.info("Execute the function Sum execute() ... ");

        List<Double> values = new Vector<Double>();
        for (int i = 0; i < argumentCount(); i++) {
            String arg = getArgument(i).toString();
            if (arg == null || arg.length() == 0) {
                value = "";
                return;
            }
            try {
                values.add(Double.parseDouble(arg));
            } catch (Exception e) {
                // errors.put(new Integer(errorCount++), e.getMessage() + " when
                // evaluate " + "Sum(); ");
                errors.put(new Integer(errorCount++), "Unparseable number:" + " " + arg + " " + "in evaluation of" + " Sum(); ");
            }
        }
        if (errors.size() > 0) {
            logger.error("The following errors happended when Sum() evaluation was performed: " + errors);
            value = "";
            return;
        }
        double v = 0;
        for (int i = 0; i < values.size(); i++) {
            v += values.get(i);
        }

        value = Double.toString(v);
    }
}
