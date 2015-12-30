package org.akaza.openclinica.logic.core.function;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;

public class Divide extends AbstractFunction {
	public Divide() {
		super();
	}

	/**
	 * @see Function#execute(HashMap)
	 */
	public void execute(HashMap<Integer, String> map) {
		logger.info("Execute the function Divide... ");
		this.map = map;

		double result = 0;
		if (argumentCount() >= 2) {
			NumberFormat nf = NumberFormat.getInstance();
			try {
				double dividend = (nf.parse(processArgument(getArgument(0)))).doubleValue();
				double divisor = (nf.parse(processArgument(getArgument(1)))).doubleValue();

				if (divisor != 0) {
					result = dividend / divisor;
				} else {
					logger.info("Can not divide a zero... ");
					value = "";
					return;
				}
			} catch (ParseException e) {
				errors.put(new Integer(errorCount++), e.getMessage());
			}
			if (argumentCount() == 3) {
				String condition = processArgument(getArgument(2));
				if (condition.compareToIgnoreCase("ROUND") == 0) {
					result = Math.ceil(result);
					value = Double.toString(result);
				}else {
					nf.setMaximumFractionDigits(2);
					value = nf.format(result);
				}
			} else {
				nf.setMaximumFractionDigits(2);
				value = nf.format(result);
			}

		} else {
			logger.info("Wrong number of args for Division... ");
			value = "";
			return;
		}

	}
}