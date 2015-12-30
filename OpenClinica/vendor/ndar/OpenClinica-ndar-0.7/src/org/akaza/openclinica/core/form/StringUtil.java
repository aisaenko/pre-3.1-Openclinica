/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.core.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/** 
 * 
 * Help class for string usage
 * 
 * @author jxu
 */
public class StringUtil {

  /**
   * Checks whether a string is blank
   * 
   * @param s
   * @return true if blank, false otherwise
   */
  public static boolean isBlank(String s) {
    return (s == null) ? true : s.trim().equals("") ? true : false;

  }

  public static boolean isNumber(String s) {
    //To Do: whether we consider a blank string is still a number?
    return Pattern.matches("[0-9]*", s) ? true : false;

  }

  public static boolean isValidDate(String s) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
    sdf.setLenient(false);
    try {
      java.util.Date date = sdf.parse(s);
      if (date.after(new java.util.Date())){
        return false; //not a date in the past,for date of birth
      }
    } catch (ParseException fe) {
      return false; //format is wrong
    }
    
    return true;

  }

  public static boolean isEmail(String s) {
    return Pattern.matches(
        "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*@[A-Za-z]+(\\.[A-Za-z]+)*", s) ? true
        : false;
  }

  public static String join(String glue, ArrayList a) {
  	String answer = "";
  	String join = "";
  	
  	for (int i = 0; i < a.size(); i++) {
  		String s = (String) a.get(i);
  		answer += join + s;
  		join = glue;
  	}
  	
  	return answer;
  }
  
  public static String appendNumbers(String str) {
	  
	  String finalstr="";
	  String arrstr[] = str.trim().split("-");
		if (arrstr.length == 2) {
			try {
				int firstNum = Integer.parseInt(arrstr[0]);
				int secondNum = Integer.parseInt(arrstr[1]);
				finalstr += firstNum;
				for (int i = (firstNum+1); i <= secondNum; i++) {
					finalstr += "-" +Integer.toString(i);
				}
			} catch (Exception e) {
				//Logger logger ;//= Logger.getLogger();
				//logger.info("Error parsing ... " + str);
				finalstr=str;
			}

		}
	  
	  return finalstr;
  }
//  /**
//   * @param s A string of words, which are substrings separated
//   * by non-word characters (reg ex "\W").
//   * @param numWords The number of words to cut-off at.
//   * @return A string composed of the first <code>numWords</code> words of <code>s</code>.
//   */
//  public static String firstWords(String s, int numWords) {
//  	Pattern p = Pattern.compile("\\W");
//  	String[] pieces = p.split(s, numWords);
//  	ArrayList a = new ArrayList(Arrays.asList(pieces));
//  	return join(" ", a);
//  }
}
