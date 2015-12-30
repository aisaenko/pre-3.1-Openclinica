package org.akaza.openclinica.logic.core.function;

/**
 * This is the base class for the Function interface, which contains common
 * fields and methods.
 * 
 * @author Hailong Wang, Ph.D
 * @veresion 1.0 08/25/2006
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.akaza.openclinica.bean.submit.ItemBean;

public abstract class AbstractFunction implements Function {
    /**
     * The argument list of this function
     */
    protected List<Object> arguments;
    
    /**
     * The value calculated out by this function on the arguments.
     */
    protected String value;
    
    /**
     * The error messages when performing the evaluation happened.
     */
    protected HashMap<Integer, String> errors;
    
    protected int errorCount;
    
    /**
     * The map of item id and item value.
     */
    protected HashMap<Integer, String> map;
    /**
     * The logger.
     */
    protected Logger logger;
    
    public AbstractFunction(){
        logger = Logger.getLogger(getClass().getName());
        arguments = new Vector<Object>(20);
        errors = new HashMap<Integer, String>(10);
        map = null;
        errorCount = 0;
    }

    /**
     * @see Function#addArgument(Object)
     */
    public void addArgument(Object arg) {
        arguments.add(arg);
    }

    /**
     * @see Function#setValue(String)
     */
    public void setValue(String newValue){
        this.value = newValue;
    }

    /**
     * @see Function#getValue()
     */
    public String getValue() {
        return value;
    }
    
    /**
     * @see Function#argumentCount()
     */
    public int argumentCount(){
        return arguments.size();
    }
    
    /**
     * @see Function#getArgument(int)
     */
    public Object getArgument(int index){
        return arguments.get(index);
    }    
    /**
     * @see Function#setArguments(List)
     */
    public void setArguments(List<Object> arguments){
        this.arguments = arguments;
    }
    
    /**
     * @see Function#getErrors()
     */
    public HashMap<Integer, String> getErrors(){
        return errors;
    }
    
    /**
     * @see Function#getVariables(Collection)
     */
    public Set<ItemBean> getVariables(Collection<ItemBean> variables){
        Set<ItemBean> result = new HashSet<ItemBean>(20);
        for(int i = 0; i < argumentCount(); i++){
            Object arg = getArgument(i);
            if(arg instanceof Function){
                Function f = (Function)arg;
                result.addAll(f.getVariables(variables));
            }else if(arg instanceof ItemBean){
                ItemBean ib = (ItemBean)arg;
                if(variables.contains(ib)){
                    result.add(ib);
                }
            }
            
        }
        return result;
    }
    
    /**
     * An helper function to process the argument.
     * @param obj an argument
     * @return    the value of th argument
     */
    protected String processArgument(Object obj){
        String result = null;
        if(obj == null){
            return null;
        }else if(obj instanceof Function){
            Function f = (Function)obj;
            f.execute(map);
            result = f.getValue();
        }else if(obj instanceof ItemBean){
            ItemBean ib = (ItemBean)obj;
            result = map.get(new Integer(ib.getId()));
        }else{
            result = obj.toString();
        }
        return result;
    }

    
    /**
     * An helper function to create the script fot this function.
     * @param obj an argument
     * @return    the value of th argument
     */
    public static List<Object> createScript(Object obj){
        List<Object> result = new ArrayList<Object>(20);
        if(obj instanceof Function){
            Function f = (Function)obj;
            result.addAll(f.getScript());
        }else if(obj instanceof ItemBean){
            result.add(obj);
        }else if(obj instanceof List){
        	result.addAll((List<Object>)obj);
        }else{
            result.add("'" + obj.toString() + "'");
        }
        return result;
    }
    
    /**
     * An helper function to create the script fot this function.
     * @param obj an argument
     * @return    the value of th argument
     */
    public static List<Object> prepareValue(Object obj){
        List<Object> result = new ArrayList<Object>(20);
        if(obj instanceof Function){
            Function f = (Function)obj;
            result.addAll(f.getScript());
        }else if(obj instanceof ItemBean){
            result.add(obj);
        }else if(obj instanceof List){
        	result.addAll((List<Object>)obj);
        }else{
            result.add(obj.toString());
        }
        return result;
    }
    
    public List<Object> getScript(){
        return null;
    }
    
    public HashMap<ItemBean, String> getAssignments(){
        return null;
    }
}
