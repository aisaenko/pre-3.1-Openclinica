package org.akaza.openclinica.logic.core.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Or extends AbstractFunction {
    public Or(){
        super();
    }
    
    /**
     * @see Function#execute(HashMap)
     */
    public void execute(HashMap<Integer, String> map) {
        logger.info("Execute the function ... ");
        this.map = map;
        boolean result = false;
        for (int i = 0; i < argumentCount(); i++) {
            boolean arg = Boolean.parseBoolean(processArgument(getArgument(i)));
            result |= arg;
            if(result){
                value = Boolean.TRUE.toString();
                return;
            }
        }
        value = Boolean.FALSE.toString();
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
