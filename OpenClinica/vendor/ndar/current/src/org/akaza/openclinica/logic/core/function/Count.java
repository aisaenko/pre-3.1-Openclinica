package org.akaza.openclinica.logic.core.function;

import java.util.HashMap;

public class Count extends AbstractFunction {
    public Count(){
        super();
    }
    
    /**
     * @see Function#execute(HashMap)
     */
    public void execute(HashMap<Integer, String> map){
        logger.info("Execute the function Count... ");
        this.map = map;
        
        double totalCount=0;
        String condition="";
        for (int i = 0; i < argumentCount(); i++) {
            condition = processArgument(getArgument(i));
            if(condition != null && condition.length() > 0){
            	totalCount+=1;
            }        
        }
        value = Double.toString(totalCount);       
    }
}