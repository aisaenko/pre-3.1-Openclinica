package org.akaza.openclinica.i18n.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.ResourceBundle;

public class HtmlUtils {
	
	/**
	 * Generates the necessary Javascript code to create a localized CalendarPopup object.
	 * It adjusts:
	 *    First day of the week
	 *    Day headers
	 *    Month names
	 *    Month abbreviations
	 *    "Today" text
	 *     
	 * @param varname Javascript variable name for the CalendarPopup object
	 * @param divname <div> name to use for the calendar
	 * @return a String with the Javacript code
	 * @author Nacho M. Castejon and Jose Martinez Garcia, BAP Health 
	 */
	public static String getCalendarPopupCode(String varname, String divname){
		StringBuffer out=new StringBuffer();
		out.append("var "+varname+" = new CalendarPopup(\""+divname+"\");");
		
		int weekStart=Calendar.getInstance(ResourceBundleProvider.getLocale()).getFirstDayOfWeek();
		
		if (weekStart==Calendar.SUNDAY)
			out.append(varname+".setWeekStartDay(0);");
		if (weekStart==Calendar.MONDAY)
			out.append(varname+".setWeekStartDay(1);");
		DateFormatSymbols dfs = new DateFormatSymbols(ResourceBundleProvider.getLocale());
		String[] weekDays = dfs.getShortWeekdays();
		String[] monthNames = dfs.getMonths();
		String[] monthAbbrev = dfs.getShortMonths();
		
		out.append(varname+".setDayHeaders(");
		out.append("\""+weekDays[1].substring(0,1).toUpperCase(ResourceBundleProvider.getLocale())+"\"");
		for (int i=2;i<weekDays.length;i++)
			out.append(",\""+weekDays[i].substring(0,1).toUpperCase(ResourceBundleProvider.getLocale())+"\"");
		out.append(");");
		
		out.append(varname+".setMonthNames(");
	    out.append("\""+capitalize(monthNames[0])+"\"");
		for (int i=1;i<monthNames.length-1;i++)
			out.append(",\""+capitalize(monthNames[i])+"\"");
		out.append(");");
	    
	    out.append(varname+".setMonthAbbreviations(");
	    out.append("\""+capitalize(monthAbbrev[0])+"\"");
		for (int i=1;i<monthAbbrev.length-1;i++)
			out.append(",\""+capitalize(monthAbbrev[i])+"\"");
		out.append(");");
		ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();
		out.append(varname+".setTodayText(\""+reswords.getString("today")+"\");");
		return out.toString();
	}


/**
 * Capitalize the first letter of a String.
 * @param s String to capitalize
 * @return Capitalized string
 */
private static String capitalize(String s) {
 	char chars[] = s.toCharArray();
 	chars[0] = Character.toUpperCase(chars[0]);
 	return new String(chars);
}

}
