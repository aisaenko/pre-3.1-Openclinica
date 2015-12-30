/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 

package org.akaza.openclinica.logic.core;

/**
 * The <code>Parser</code> is used to parse item variables in expression.
 * 
 * @author ywang
 * @version 1.0, 01-16-2008
 * 
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.TreeSet;

import org.akaza.openclinica.bean.submit.ItemBean;

public class Parser {
    private Logger logger = Logger.getLogger(getClass().getName());
    private HashMap<String, ItemBean> map;
    private HashMap<String,String> itemdata;
    private StringBuffer errors;
    public Parser(HashMap<String, ItemBean> map, HashMap<String,String> itemdata){
        this.map = map;
        this.itemdata = itemdata;
        this.errors = new StringBuffer();
    }
    
   /**
    * <p>Given expression, replace item variables with their values.
    * <p>Supported expression could start with a key word 'func' and include
    * <li>operators +, -, *, /
    * <li>parenthesises  (, )
    * <li>formula as the pattern that starts with formula name,
    * formula arguments are grouped in parenthesises with delimiter as ','
    * For example: sum(item_x,item_y,item_z)
    * 
    * @param expression
    * @param ordinal
    * @return
    * 
    * @author ywang 1-18-2008
    */
   public String parse(String expression, int ordinal) {
	  char ch, next;
      String var = "";
      int currentPos = 0;
      String finalexp = "";
      String aexp = "";
      
      // process the prefix
      String exp=expression;
      if (exp.indexOf("func:") >= 0) {
          exp = exp.substring(5).trim();
      }
      exp = exp.replaceAll("##", ",");
      
      int length = exp.length();
      boolean isEmpty = false;
      while(currentPos < length-1) {
      	ch = exp.charAt(currentPos);
      	aexp += ch;
      	if(ch != '(' && ch != ')' && ch != ',' && ch != '+' && ch != '-' && ch != '*' && ch != '/' ) {
      		var += ch;
      	} else {
      		finalexp += ch;
      	}
      	next = exp.charAt(currentPos+1);
      	if(next == '(' || next == ')' || next == ',' || next == '+' || next == '-' || next == '*' || next == '/') {
      		++currentPos;
      		if(map.containsKey(var.trim()) && next != '(') {
      			String key = ((ItemBean)map.get(var.trim())).getId() + "_" + ordinal;
      			if(itemdata.containsKey(key)) {
	      			String idvalue = itemdata.get(key);
	      			if(idvalue.length()>0) {
	      				finalexp += idvalue;
	      			}else {
	      				//if no item value found, score result will be empty
	      				errors.append(" " + var.trim() + " " + "is empty" + "; ");
	      				isEmpty = true;
	      				//finalexp += "NaN";
	      				//return "";
	      			}
      			}else {
      				//if no item value found, score result will be empty
      				errors.append(" " + var.trim() + " " + "is empty" + "; ");
      				isEmpty = true;
      			}
  			} else {
  				finalexp += var;
  			}
  			var = "";      		
      		finalexp += next;
      		aexp += next;
      	}      	
      	++currentPos;
      }
      
      ch = exp.charAt(length-1);
      if(ch != ')') {
      	var += ch;
      } else {
      	finalexp += aexp.length()< length ? ch : "";
      }
      if(var.length()>0) {
    	    if(map.containsKey(var.trim())) {
      			String key = ((ItemBean)map.get(var.trim())).getId() + "_" + ordinal;
      			if(itemdata.containsKey(key)) {
	      			String idvalue = itemdata.get(key);
	      			if(idvalue.length()>0) {
	      				finalexp += idvalue;
	      			} else {
	      				//if no item value found, score result will be empty
	      				errors.append(" " + var.trim() + " " + "is empty" + "; ");
	      				isEmpty = true;
	      				//finalexp += "NaN";
	      				//return "";
	      			}
      			} else {
      				//if no item value found, score result will be empty
      				errors.append(" " + var.trim() + " " + "is empty" + "; ");
      				isEmpty = true;
      			}
	        }else{
	        	finalexp += var;
	        }
			var = "";
      }
      return finalexp = isEmpty ? "" : finalexp;
	 }
   
   /**
    * <p>Given expression, replace item variables with their values.
    * <p>Supported expression could start with a key word 'func' and include:
    * <li>operators +, -, *, /
    * <li>parenthesises  (, )
    * <li>formula as the pattern that starts with formula name,
    * formula arguments are grouped in parenthesises with delimiter as ','
    * For example: sum(item_x,item_y,item_z)
    * 
    * @param expression
    * @param ordinal
    * @return
    * 
    * @author ywang 1-18-2008
    */
   public String parse(String expression,HashMap<Integer,TreeSet<Integer>> itemOrdinals) {	  
	  char ch, next;
      String var = "";
      int currentPos = 0;
      String finalexp = "";
      String aexp = "";
      
      // process the prefix
      String exp=expression;
      if (exp.indexOf("func:") >= 0) {
          exp = exp.substring(5).trim();
      }
      exp = exp.replaceAll("##", ",");
      
      int length = exp.length();
      boolean isEmpty = false;
      while(currentPos < length-1) {
      	ch = exp.charAt(currentPos);
      	aexp += ch;
      	if(ch != '(' && ch != ')' && ch != ',' && ch != '+' && ch != '-' && ch != '*' && ch != '/' ) {
      		var += ch;
      	} else {
      		finalexp += ch;
      	}
      	next = exp.charAt(currentPos+1);
      	if(next == '(' || next == ')' || next == ',' || next == '+' || next == '-' || next == '*' || next == '/') {
      		++currentPos;
      		String ivar = var.trim();
      		if(map.containsKey(ivar) && next != '(') {
      			int itemId = ((ItemBean)map.get(ivar)).getId();
      			TreeSet<Integer> ordinals = itemOrdinals.get(itemId);
      			int groupsize = ordinals.size();
      			int count = 0;
      			Iterator it = ordinals.iterator();
      			while(it.hasNext()) {
      				String key = itemId + "_" + it.next();
      				String idvalue = "";
      				if(itemdata.containsKey(key)) {
      					idvalue = itemdata.get(key);
      					if(idvalue.length()>0) {
      						finalexp += (count<groupsize-1)?(idvalue + ","):idvalue;
      					}else {
      						//if no item value found, score result will be empty
      						errors.append(" " + var.trim() + " " + "is empty" + "; ");
	      					isEmpty = true;
	      					//finalexp += "NaN";
	      					//return "";
      					}
      				} else {
      					//if no item value found, score result will be empty
      					errors.append(" " + var.trim() + " " + "is empty" + "; ");
      					isEmpty = true;
      				}
      				++count;
      			}
  			} else {
  				finalexp += var;
  			}
  			var = "";      		
      		finalexp += next;
      		aexp += next;
      	}      	
      	++currentPos;
      }
      
      ch = exp.charAt(length-1);
      if(ch != ')') {
      	var += ch;
      } else {
      	finalexp += aexp.length()< length ? ch : "";
      }
      if(var.length()>0) {
    	  	String ivar = var.trim();
		    if(map.containsKey(ivar)) {
	  			int itemId = ((ItemBean)map.get(ivar)).getId();
      			TreeSet<Integer> ordinals = itemOrdinals.get(itemId);
      			int groupsize = ordinals.size();
      			int count = 0;
      			Iterator it = ordinals.iterator();
      			while(it.hasNext()) {
      				String key = itemId + "_" + it.next();
	  				String idvalue = "";
	  				if(itemdata.containsKey(key)) {
	  					idvalue = itemdata.get(key);
	  					if(idvalue.length()>0) {
	  						finalexp += (count<groupsize-1)?(idvalue + ","):idvalue;
	  					}else {
	  						//if no item value found, score result will be empty
	  						errors.append(" " + var.trim() + " " + "is empty" + "; ");
	      					isEmpty = true;
	      					//finalexp += "NaN";
	      					//return "";
	  					}
	  				} else {
	  					//if no item value found, score result will be empty
	  					errors.append(" " + var.trim() + " " + "is empty" + "; ");
	  					isEmpty = true;
	  				}
	  				count++;
	  			} 
	        }else{
	        	finalexp += var;
	        }
			var = "";
      }
      finalexp = isEmpty ? "" : finalexp;
      finalexp = finalexp.replace(",,", ",");
      finalexp = finalexp.replace(",)", ")");
      
      return finalexp;
	 }
    
   /**
    * Return true if item in expression shows its name in a changedItems collection.
    * 
    * @param updatedData
    * @param expression
    * @param ordinal
    * @return
    */
   public boolean isChanged(TreeSet<String> changedItems, String expression, int ordinal) {
	   char ch, next;
      String var = "";
      int currentPos = 0;
      
      // process the prefix
      String exp=expression;
      if (exp.indexOf("func:") >= 0) {
          exp = exp.substring(5).trim();
      }
      exp = exp.replaceAll("##", ",");
      
      int length = exp.length();
      while(currentPos < length-1) {
      	ch = exp.charAt(currentPos);
      	if(ch != '(' && ch != ')' && ch != ',' && ch != '+' && ch != '-' && ch != '*' && ch != '/' ) {
      		var += ch;
      	} 
      	next = exp.charAt(currentPos+1);
      	if(next == '(' || next == ')' || next == ',' || next == '+' || next == '-' || next == '*' || next == '/') {
      		++currentPos;
      		if(changedItems.contains(var.trim()))	return true;
  			var = "";
      	}      	
      	++currentPos;
      }
      
      ch = exp.charAt(length-1);
      if(ch != ')') {
      	var += ch;
      }
      if(var.length()>0) {
    	  if(changedItems.contains(var.trim()))	return true;
    	  var = "";
      }
      return false;
   }
   
   public boolean isChanged(TreeSet<String> changedItems, String expression) {	  
	  char ch, next;
      String var = "";
      int currentPos = 0;
      
      // process the prefix
      String exp=expression;
      if (exp.indexOf("func:") >= 0) {
          exp = exp.substring(5).trim();
      }
      exp = exp.replaceAll("##", ",");
      
      int length = exp.length();
      while(currentPos < length-1) {
      	ch = exp.charAt(currentPos);
      	if(ch != '(' && ch != ')' && ch != ',' && ch != '+' && ch != '-' && ch != '*' && ch != '/' ) {
      		var += ch;
      	}
      	next = exp.charAt(currentPos+1);
      	if(next == '(' || next == ')' || next == ',' || next == '+' || next == '-' || next == '*' || next == '/') {
      		++currentPos;
      		if(changedItems.contains(var.trim()))	return true;
  			var = "";
      	}      	
      	++currentPos;
      }
      
      ch = exp.charAt(length-1);
      if(ch != ')') {
      	var += ch;
      }
      if(var.length()>0) {
    	  	if(changedItems.contains(var.trim()))	return true;
			var = "";
      }
      return false;
   }
     
    /**
     * A helper function to create a function class name.
     * 
     * @param functionName
     *            a function name.
     * @return the function class name.
     * 
     * @author Hailong Wang, 08/25/2006
     */
    public static String convertToClassName(String packageName, String functionName){
        if(functionName == null || functionName.length() == 0){
            return null;
        }
        return packageName + "." + functionName.substring(0, 1).toUpperCase()+functionName.substring(1, functionName.length());
    }
    
    public HashMap<String,ItemBean> getMap() {
    	return this.map;
    }
    public HashMap<String,String> getItemData() {
    	return this.itemdata;
    }
    public StringBuffer getErrors() {
    	return this.errors;
    }
    public void setErrors(StringBuffer e) {
    	this.errors = e;
    }
}
