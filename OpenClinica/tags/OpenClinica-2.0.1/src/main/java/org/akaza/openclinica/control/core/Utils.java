/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2007 Akaza Research
 */ 


package org.akaza.openclinica.control.core;

import java.util.Calendar;
import java.util.Date;


public class Utils {
	
	private static Utils ref;

	private Utils() {
	}

	public static Utils getInstacne() 
	{
		if (ref == null) {
			ref = new Utils();
		}
		return ref;
	}

	public Object clone() throws CloneNotSupportedException 
	{
		throw new CloneNotSupportedException();
	}
	
	
	/**
	   * This Method will compare the two Dates and return 
	   * a String with number of years , weeks and days.
	   * 
	   * @author Krikor Krumlian  10/20/2006
	   * @param eventStartDate The event start date
	   * @param subjectDOB the Subject's date of birth
	   * @return
	   */
	  public String processAge(Date eventStartDate, Date subjectDOB)
	  {
		  int years=0 , months=0, days=0;
		  String ret = "";
		  
		  if ( eventStartDate == null || subjectDOB == null )
		  {
			  return "N/A";
		  }
		  
		  // example : 10/20/2006
		  Calendar eventsd = (Calendar.getInstance());
		  eventsd.setTime(eventStartDate);
		  long init  = eventsd.getTimeInMillis();
		  
		  // example : 10/20/1990
		  Calendar dob = (Calendar.getInstance());
		  dob.setTime(subjectDOB);
		  long latter  = dob.getTimeInMillis();
		  
	      long difference = Math.abs(init - latter);
	      double daysDifference = (double)Math.floor(difference/1000/60/60/24);
		  
	      // Get the number of years
	      while ( daysDifference - 365.24 > 0 )
	      {
	       daysDifference = daysDifference - 365.24;
	       years++;
	      }
	      
	      // Get the number of months
	      while ( daysDifference - 30.43 > 0 )
	      {
	       daysDifference = daysDifference - 30.43;
	       months++;
	      }
	 
	      // Get the number of days
	      while ( daysDifference - 1 > 0 )
	      {
	       daysDifference = daysDifference - 1;
	       days++;
	      }
		  if ( years  >= 0 ) ret = ret + years  + " Years";
		  if ( months >= 0 ) ret = ret + " - " + months  + " Months";
		  if ( days   >= 0 ) ret = ret + " - " + days   + " Days";
		  if (ret.equals("")) ret = "Less than a day";
		  return ret;
		  
	  }
}