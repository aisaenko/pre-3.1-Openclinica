package org.akaza.openclinica.logic.core.function;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class Sum extends AbstractFunction {
    public Sum(){
        super();
    }

    /**
     * If one argument is "", then the value of this function will be "" too.
     * @see Function#execute
     */

    public void execute(HashMap<Integer, String> map){
        logger.info("Execute the function Sum... ");
        this.map = map;
        
        NumberFormat nf = NumberFormat.getInstance();
        List<Number> values = new Vector<Number>();
        for (int i = 0; i < argumentCount(); i++) {
            String arg = processArgument(getArgument(i));
            if(arg == null || arg.length() == 0){
                value = "";
                return;
            }
            try {
                values.add(nf.parse(arg));
            } catch (ParseException e) {
                errors.put(new Integer(errorCount++), e.getMessage());
            }
        }
        if(errors.size() > 0){
            logger.severe("The following errors happended when the evaluation was performed: " + errors);
            return;
        }
        double v = 0;
        for (int i = 0; i < values.size(); i++) {
            v += values.get(i).doubleValue();
        }

        value = Double.toString(v);
    }   

}
