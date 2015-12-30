package org.akaza.openclinica.logic.core.function;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Age extends AbstractFunction {
	int[] monthday = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	int daysInMonth = 30;
	
	public Age() {
		super();
	}

	/**
	 * @see Function#execute(HashMap)
	 */
	public void execute(HashMap<Integer, String> map) {
		logger.info("Execute the function Age... ");
		this.map = map;

		//the 2nd and 3rd args come from ScoreCalculator -> doCalculation() f.addArguments(obj)
		if (argumentCount() == 3) {
			String condition = processArgument(getArgument(0));

			Calendar cal0 = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal0.setTime((Date) (this.getArgument(1)));
			cal1.setTime((Date) (this.getArgument(2)));

			//System.out.println(cal0.toString() + "  " + cal1);

			int dob_year = cal0.get(Calendar.YEAR);
			int dob_month = cal0.get(Calendar.MONTH);
			int dob_day = cal0.get(Calendar.DAY_OF_MONTH);
			int intview_year = cal1.get(Calendar.YEAR);
			int intview_month = cal1.get(Calendar.MONTH);
			int intview_day = cal1.get(Calendar.DAY_OF_MONTH);
			int years = 0, months = 0, days = 0;

			// calculate days
			if (intview_day >= dob_day)
				days = intview_day - dob_day;
			else {
				intview_day += daysInMonth;//monthday[intview_month];
				intview_month--;
				days = intview_day - dob_day;
			}
			// calculate months
			if (intview_month >= dob_month)
				months = intview_month - dob_month;
			else {
				intview_month += 12;
				intview_year--;
				months = intview_month - dob_month;
			}
			years = intview_year - dob_year;

			

			//System.out.println(days + " days " + months + " months " + years	+ " years   totalMonths: " + totalMonths);

			if (condition.equalsIgnoreCase("month") == true) {
				int totalMonths = 0;

				//convert all to months
				totalMonths = years * 12 + months;
				value = (new Integer(totalMonths)).toString();
			}else if (condition.equalsIgnoreCase("year") == true){
				value = (new Integer(years)).toString();							
			}else {
				value = days + " days " + months + " months " + years
						+ " years";
			}
		} else {
			logger.info("Wrong number of args for Age ... ");
			value = "";
			return;
		}

	}
}