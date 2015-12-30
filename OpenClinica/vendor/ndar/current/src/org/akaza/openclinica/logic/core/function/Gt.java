package org.akaza.openclinica.logic.core.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Gt extends AbstractFunction {
    public Gt(){
        super();
    }
    
    /**
     * @see Function#execute(HashMap)
     */
    public void execute(HashMap<Integer, String> map) {
        logger.info("Execute the function ... ");
        this.map = map;
        if(argumentCount() != 2){
            errors.put(new Integer(errorCount++), "The argument number for function eq should be 2: " + argumentCount());
        }
        String arg1 = processArgument(getArgument(0));
        String arg2 = processArgument(getArgument(1));
        
        if(arg1 != null && arg1.length() > 0 && arg2 != null && arg2.length() > 0){
            double darg1 = 0;
            double darg2 = 0;
            try{
                darg1 = Double.parseDouble(arg1);
            }catch(NumberFormatException e){
                errors.put(new Integer(errorCount++), getArgument(0) + " is not a valid number.");
                value = "Can't calculate";
                return;
            }
            try{
                darg2 = Double.parseDouble(arg2);
            }catch(NumberFormatException e){
                errors.put(new Integer(errorCount++), getArgument(1) + " is not a valid number.");
                value = "Can't calculate";
                return;
            }
            if(darg1 > darg2){
                value = Boolean.TRUE.toString();
            }else{
                value = Boolean.FALSE.toString();
            }
        }else{
            value = null;
        }
    }
    
    /**
     * @see Function#getScript()
     */
    public List<Object> getScript(){
        List<Object> result = new ArrayList<Object>(10);
        result.add("(");
        result.addAll(createScript(getArgument(0)));
        result.add(" > ");
        
        result.addAll(createScript(getArgument(1)));
        result.add(")");
        
        return result;
    }
}
