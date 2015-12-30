package org.akaza.openclinica.logic.core.function;

import java.util.HashMap;

public final class Decode extends AbstractFunction {
    public Decode(){
        super();
    }

    /**
     * @see Function#execute(HashMap)
     */
    public void execute(HashMap<Integer, String> map){
        logger.info("Execute the function Decode... ");
        this.map = map;
        
        String condition = processArgument(getArgument(0));

        if(condition == null || condition.length() == 0){
            value = "";
            return;
        }
        boolean found = false;
        for(int i = 1; i < argumentCount()-1; i += 2){
            if(condition.equals(processArgument(getArgument(i)))){
                value = processArgument(getArgument(i+1));
                found = true;
                break;
            }
        }
        
        if(!found){
            if(argumentCount()%2 == 0){
                value = processArgument(getArgument(argumentCount()-1));
            }else{
                value = "";
            }
        }
    }   
}
