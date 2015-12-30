package org.akaza.openclinica.logic.core.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class And extends AbstractFunction {
    public And(){
        super();
    }
    
    /**
     * @see Function#execute(HashMap)
     */
    public void execute(HashMap<Integer, String> map) {
        logger.info("Execute the function ... ");
        this.map = map;
        boolean result = true;
        for (int i = 0; i < argumentCount(); i++) {
            String sarg = processArgument(getArgument(i));
            if(sarg == null){
                value = null;
                return;
            }
            boolean arg = Boolean.parseBoolean(sarg);
            result &= arg;
            if(!result){
                value = Boolean.FALSE.toString();
                return;
            }
        }
        value = Boolean.TRUE.toString();
    }
    
    /**
     * @see Function#getScript()
     */
    public List<Object> getScript(){
        List<Object> result = new ArrayList<Object>(10);
        result.add("(");
        result.addAll(createScript(getArgument(0)));
        result.add(" || ");
        
        result.addAll(createScript(getArgument(1)));
        result.add(")");
        
        return result;
    }
}
