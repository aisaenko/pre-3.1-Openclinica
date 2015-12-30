package org.akaza.openclinica.logic.core;

/**
 * The <code>Parser</code> is used to parse the formula, instantiates the
 * right function object and passes in the arguments, and returns the function
 * object.
 * 
 * @author Hailong Wang, Ph.D
 * @version 1.0, 08/25/2006
 */
import java.util.Stack;
import java.util.Vector;
import java.util.HashMap;
import java.util.logging.Logger;

import org.akaza.openclinica.logic.core.function.Function;
import org.akaza.openclinica.bean.submit.ItemBean;

public class Parser {
    private static final String FUNCTION_PACKAGE = "org.akaza.openclinica.logic.core.function";
    private static final int TOKEN = 0;
    private static final int FUNCTION = 1;

    private Logger logger = Logger.getLogger(getClass().getName());
    private HashMap<String, ItemBean> map;
    public Parser(HashMap<String, ItemBean> map){
        this.map = map;
    }
    
    /**
     * Parses the formula to instantiate the right function object and returns it.
     * @param s the formula.
     * @return  the value.
     */
    public Object parse(String exp) {
        logger.info("Start to parse " + exp + " ... ");
        char ch;
        String token = "";
        int currentPos = 0;
        int status = TOKEN;
        Stack<Object> st = new Stack<Object>();
        
        // process the prefix
        if (exp.indexOf("func:") >= 0) {
            exp = exp.substring(5);
        }
        exp = exp.replaceAll("##", ",");
        int length = exp.length();
        while (currentPos < length) {
            ch = exp.charAt(currentPos);
            if (ch == '(') {
                if (token != null && token.length() > 0) {
                    st.push(token.trim());
                }
                status = TOKEN;
                st.push("(");
                token = "";
            } else if (ch == ',') {
                if(status == TOKEN){
                    token = token.trim();
                    if(token.contains(":")){
                		String arr[] = token.split(":");
                		token=arr[1].trim();
                	}
                    if (map.containsKey(token)) {
                        st.push(map.get(token.trim()));
                    }else{
                        st.push(token);
                    }
                }
                status = TOKEN;
                token = "";
            } else if (ch == ')') {
                if(status == TOKEN){
                    token = token.trim();
                    if(token.contains(":")){
                		String arr[] = token.split(":");
                		token=arr[1].trim();
                	}
                    if(map.containsKey(token)){
                        st.push(map.get(token));
                    }else{
                        st.push(token);
                    }
                }
                Vector<Object> params = new Vector<Object>();
                Object obj = st.pop();
                while (!obj.equals("(")) {
                    params.insertElementAt(obj, 0);
                    obj = st.pop();
                }
                String s = (String)st.pop();
                try {
                	if(s.contains(":")){
                		String arr[] = s.split(":");
                		s=arr[1].trim();
                	}
                    Function function = (Function) Class.forName(
                            convertToClassName(FUNCTION_PACKAGE, s)).newInstance();
                    function.setArguments(params);
                    st.push(function);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                status = FUNCTION;
            } else {
                token += ch;
            }
            currentPos++;
        }
        
        logger.info("End parsing " + exp + ".");
        return st.pop();
    }
    
    /**
     * A helper function to create a function class name.
     * 
     * @param functionName
     *            a function name.
     * @return the function class name.
     */
    public static String convertToClassName(String packageName, String functionName){
        if(functionName == null || functionName.length() == 0){
            return null;
        }
        return packageName + "." + functionName.substring(0, 1).toUpperCase()+functionName.substring(1, functionName.length());
    }
}
