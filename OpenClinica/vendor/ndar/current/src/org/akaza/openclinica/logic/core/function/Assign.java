package org.akaza.openclinica.logic.core.function;

import org.akaza.openclinica.bean.submit.ItemBean;

import java.util.*;

public class Assign extends AbstractFunction {
    public Assign(){
        super();
    }

    /**
     * @see Function#execute(HashMap)
     */
    public void execute(HashMap<Integer, String> map){
        
    }
    
    /**
     * @see Function#getScript()
     */
    @Override
    public HashMap<ItemBean, String> getAssignments(){
        HashMap<ItemBean, String> result = new HashMap<ItemBean, String>(10);
        for(int i = 0; i < argumentCount(); i += 2){
            List k = prepareValue(getArgument(i));
            Object k0 = (k==null || k.size()<1 ? null : k.get(0));
            List v = prepareValue(getArgument(i+1));
            Object v0 = (v==null || v.size()<1 ? null : v.get(0));
            if (k0==null || v0==null || !((k0 instanceof ItemBean) && (v0 instanceof String))) {
            	logger.warning("Problem with the assignment types (k="+k0+", v="+v0+")");
            	return null;
            }
            result.put((ItemBean)k.get(0), (String)v.get(0));
        }
        return result;
    }

}
