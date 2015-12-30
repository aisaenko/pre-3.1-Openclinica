/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 

package org.akaza.openclinica.logic.core;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import org.akaza.openclinica.logic.core.function.*;


/**
 * Util class for scoring feature. It contains expression evaluation methods.
 * 
 * @author ywang (Jan. 2008)
 *
 */
public class ScoreUtil {
    private static final String FUNCTION_PACKAGE = "org.akaza.openclinica.logic.core.function";

    /**
     * Evaluation math expression which might contain functions. 
     * <p>Some pre-conditions:
     * <li>Supported arithmetic operators include only '+', '-', '*', '/'
     * <li>Math expression should pass ScoreValidator before evaluation.
     * 
     * @param expression
     * 
     * @return String
     */
	public static String eval(String expression, StringBuffer errors)
	{
		if(expression == null || expression.length() < 1){
			return expression;
		}
		//process the prefix
        String exp=expression;
        if (exp.startsWith("func:")) {
            exp = exp.substring(5);
        }
        exp = exp.replaceAll("##", ",");
        
		String value = "";
		char contents[] = exp.toCharArray();
		String token = "";
		String finalExpression = "";
		Info info = new Info();
		info.pos = 0;
		info.level = 0;
		while(info.pos < contents.length){
			char c = contents[info.pos];
			//ignore spaces
			if(c == ' '){
				//do nothing
			}else if(isOperator(c)){
				finalExpression += token + c;
				token = "";
			}
			else if(c == '('){
				String funcname = getFunctionName(token);
				if(funcname != null){
					try {
						token = ""+evalFunc(contents, info, (Function)Class.forName(funcname).newInstance(), errors);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
		                    e.printStackTrace();
		            }
					finalExpression += token;
					token = "";
				}else{
					info.level++;
					finalExpression += token + c;
					token = "";
				}
			}
			else if(c == ')'){
				finalExpression += token + c;
				token = "";
				info.level--;
			}
			 //This should not happen since it is automatically handled by getFunctionValue()
			 //So, if this happens, either the expression or unlikely code has a problem.
			else if(c == ','){
				System.out.println("weird found , in parse");
				token = "";
				return "";
			}else{
				token += c;
			}
			info.pos++;
		}
		
		if(info.level != 0){
			System.out.println("expression invalid, unpaired parentheses."); // !!!!!
		}
		//There might be a last token which should be added to the final expression.
		//For example, to expression 2+4, we must do so.
		finalExpression += token;
	
		if(finalExpression != null && finalExpression.length()>0){
			ArrayList<Object> ae = convert(finalExpression,errors);
			if(ae.size()==1) {
				value = ae.get(0).toString();
			}else {
				try {
					value = eval(createPostfix(ae),errors);
				} catch(Exception e){
					e.printStackTrace();
					errors.append(" Scoring failed; ");
				}
			}
		}

		return ""+value;
	}


	/**
	 * Evaluate a function which might contain arithmetic expressions, and return result as a String.
	 * If an item can not be found in the eventCRF, it will be treated as empty. If empty items exist in a function,
	 * the result will be empty.
	 * 
	 * @param contents
	 * @param info
	 * @param function
	 * @return
	 */
	public static String evalFunc(char[] contents, Info info, Function function, StringBuffer errors)
	{
		int originalLevel = info.level;
		info.pos++;
		info.level++;
		String token = "";
		//currentExpression is in fact representing the current argument.
		String currentExpression = "";
		while(info.pos < contents.length){
			char c = contents[info.pos];
			if(c == ')'){
				info.level--;
				//end of the function, marked by the equal level
				if(info.level == originalLevel){
					currentExpression += token;
					String t = evalArgument(currentExpression, errors);
					if(t != null && t.length()>0){
						function.addArgument(t);
					} else {
						//error message has been handled in evalArgument()
						return "";
					}
					token = "";
					break;
				}else{
					//end of an expression, just store them in the current argument
					currentExpression += token + c;
				}
				token = "";
			}
			else if(c == '('){
				//it is either the start of a function or an expression
				String funcname = getFunctionName(token);
				if(funcname != null){
					//store in the current argument
					try {
						currentExpression = ""+evalFunc(contents, info, (Function)Class.forName(funcname).newInstance(),errors);
					} catch (InstantiationException e) {
						e.printStackTrace();
						return "";
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						return "";
					} catch (IllegalAccessException e) {
	                    e.printStackTrace();
	                    return "";
		            }
				}//if it is the start of an expression
				else {
					info.level++;
					currentExpression += token + c;
				}
				token = "";
			}//end of an argument
			else if(c == ','){
				currentExpression += token;
				//compute the argument
				String t = evalArgument(currentExpression,errors);
				if(t != null && t.length()>0){
					function.addArgument(t);
				} else {
					return "";
				}
				token = "";
				//reset the argument for next one
				currentExpression = "";
			}
			else if(isOperator(c)){
				currentExpression += token + c;
				token = "";
			}else{
				if(c != ' '){
					token += c;
				}
			}
			info.pos++;
		}
		function.execute();
		if(function.getErrors().size()>0) {
			HashMap<Integer,String> es = function.getErrors();
			for(int i=0; i<es.size(); ++i ) {
				errors.append(es.get(Integer.valueOf(i)));
			}
		}
		
		return function.getValue();
	}
	
	/**
	 * a term is supposed to be something like 7+5*4.  Sometimes it is in fact
	 * a whole new expression
	 * @param term
	 * @param funcName
	 * @return
	 */
	public static String evalArgument(String arg, StringBuffer errors)
	{
		String v = "";
		if(arg != null && arg.length()>0){
			try {
				ArrayList<Object> ae = convert(arg,errors);
				if(ae.size()==1) {
					v = ae.get(0).toString();
				}else {
					v = eval(createPostfix(ae),errors);
				}
			} catch(Exception e){
				v = eval(arg, errors);
			}		
		}
		return v;
	}
	
	/**
	 * Convert expression to ArrayList<Object>. 
	 * 
	 * @param exp
	 * @param errors
	 * @return
	 */
	public static ArrayList<Object> convert(String exp, StringBuffer errors) {
		ArrayList<Object> list = new ArrayList<Object>();
		//If exp is actually number, there is no need to carry on
		if(ScoreValidator.isNumber(exp)) {
            list.add(exp);
            return list;
        }
        	
		char ch = ' ';
		exp = exp.replace(" ", "");
		String token = "";
		char[] c = exp.toCharArray();
		//initially it must be true
		boolean couldBeSign = true;
		//boolean isSign = false;
		for(int i=0; i<c.length; ++i) {
			ch = c[i];
			if(ch == '(' || ch ==')') {
				if(token.length()>0) {
					list.add(token);
				}
				list.add(String.valueOf(ch));
				token = "";
				if(ch == '('){
					couldBeSign = true;
				}else{
					couldBeSign = false;
				}
			} else if(isOperator(ch)) {
				if(token.length()>0) {
					list.add(token);
				}
				if(couldBeSign && isSign(ch)){
					if(token.length() > 0){
						System.out.println("wrong at operator " + ch);
					}else{
						token = "" + ch;
					}
					couldBeSign = false;
				}else{
					couldBeSign = true;
					list.add(String.valueOf(ch));
					token = "";
				}
			}else {
				couldBeSign = false;
				token += ch;
				//for scientific notation
				if(token.endsWith("E")||token.endsWith("e")) {
					int count = i+1;
					if(c[count]=='+'||c[count]=='-'||Character.isDigit(c[count])) {
						token += c[count];
						++count;
						while(Character.isDigit(c[count])) {
							token += c[count];
							++count;
						}
					}
					i = count-1;
				}
			}
		}
		if(token.length()>0)  list.add(token);
		
		if(list.size()==1) {
			if(list.get(0).toString().equalsIgnoreCase("NaN")) {
				//errors.append("Empty item or argument has been found;");
				list.clear();
				list.add("");
			}
		}
		
		return list;
	}
	
	/**
	 * Create postfix(ArrayList<Object>) for an expression(ArrayList<Object>). This method only handles (, ), sign(+ -),
	 * arithmethic operators (ie, + * - /). 
	 * 
	 * @param exp
	 * @return
	 */
	public static ArrayList<Object> createPostfix(ArrayList<Object> exp) {
		if(exp.size()<3) {
			return exp;
		}
		Stack<Object> temp = new Stack<Object>();
		ArrayList<Object> post = new ArrayList<Object>();
		for(int i=0; i<exp.size(); ++i) {
			if(exp.get(i).toString().equals("(")) {
				temp.push("(");
			}else if(exp.get(i).toString().equals(")")) {
				while(!temp.isEmpty()) {
					String s = (String)temp.pop();
					if(!s.equals("(")) {
						post.add(s);
					} else {
						break;
					}
				}
			} else if(isOperator(exp.get(i).toString())) {
				boolean finished = false;
				while(!temp.isEmpty() && !finished) {
					String s = (String)temp.pop();
					if(isOperator(s)) {
						if(getPriority(s.toString()) >= getPriority(exp.get(i).toString())) {
							post.add(s);
						}else{
							temp.push(s);
							finished = true;
						}
					}else{
						temp.push(s);
						finished = true;
					}
				}
				temp.push(exp.get(i));
			} else {
				post.add(exp.get(i));
			}
		}
		while(!temp.isEmpty()) {
			post.add(temp.pop());
		}
		
		return post;
	}
	
	/**
	 * Evaluates + * - / using postfix algorithm and return a String. 
	 * If the parameter exp size is 1, the first and the only element of exp will be returned. 
	 * If exp size > 1 and contains non-number elements, empty string will be returned.
	 * 
	 * @param exp	ArrayList<Object>	should be postfix of an expression.
	 * @param errors
	 * @return
	 */
	public static String eval(ArrayList<Object> exp, StringBuffer errors) {
		String stringValue = "";
		double value = Double.NaN;
		Stack<Double> st = new Stack<Double>();
		int size = exp.size();
		if(size==1) {
			try {
				value = Double.valueOf(exp.get(0).toString());
			} catch (Exception e) {
				//for function like decode whose argument might be a String
				stringValue = exp.get(0).toString();
			}
		}else if (size>2) {
			for(int i=0; i<size; ++i) {
				String s = exp.get(i).toString();
				if(isOperator(s)) {
					char ch = s.charAt(0);
					double second = st.pop();
					double first = st.pop();
					try {
						if(ch == '+') {
							value = first + second;
						} else if(ch == '-') {
							value = first - second;
						} else if(ch == '*'){
							value = first * second;
						} else if(ch == '/') {
							value = first/second;
						}
					} catch(Exception ee) {
						errors.append(ee.getMessage() + "; ");
						ee.printStackTrace();
						value = Double.NaN;
					}
					st.push(value);
				} else {
					double d = Double.NaN;
					try {
						d = Double.valueOf(exp.get(i).toString());
					} catch(Exception e) {
						e.printStackTrace();
						errors.append(" Number was expected for " + exp.get(i).toString() + "; ");
						return exp.get(i).toString();
					}
					st.push(d);
				}
			}
		}
		stringValue = stringValue.equalsIgnoreCase("") ? (((value+"").equalsIgnoreCase("NaN")) ? "" : (value+"")) : stringValue;
		
		return stringValue;
	}

	/**
	 * Return true if one character matches one of those characters
	 * '+', '-', '*', '/'
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isOperator(char c)
	{
		return c == '+' || c == '-'  || c == '*' || c == '/';
	}
	
	private static boolean isOperator(String s)
	{
		return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
	}

	private static boolean isSign(String s)
	{
		return s.equals("+") || s.equals("-");
	}
	
	private static boolean isSign(char c)
	{
		return c == '+' || c == '-';
	}
	
	
	public static String getFunctionName(String token)
	{
		return Parser.convertToClassName(FUNCTION_PACKAGE, token) ;
	}
	
	/*
	 * Only handled + * - /
	 */
	protected static byte getPriority(String operator) {
		byte p = 0;
		if(operator.equals("+") || operator.equals("-")) {
			p = 0;
		} else if(operator.equals("*") || operator.equals("/")) {
			p = 1;
		} else {
			p = -1;
		}
		return p;
	}
	
	static class Info {
		int level = 0;
		int pos = 0;
	}
	
}