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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.EntityAction;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;

/** 
 *
 * A class to validate form input.
 *
 * <p>The general usage of the Validator is as follows:
 * 
 * <code>
 * Validator v = new Validator(request); // request is an HttpServletRequest object
 *
 * // fieldName is the name of your HTML input field.
 * // validationType is one of the Validator static ints
 * // args depends on which type of validation you�re doing,
 * // see below for details
 * v.addValidation(fieldName, validationType, args);
 * 
 * // the following step is optional
 * // if you don't like the default error message for the validationType you are using,
 * // you can specify an error message specific to your form here
 * // note that this call must come after the addValidation call for the input
 * // you are validating, and before the next addValidation call
 * v.setErrorMessage(customErrorMessage);
 *
 * // add more validations as necessary
 * HashMap errors = v.validate();
 * 
 * if (errors.isEmpty()) {
 * 		// this means all of your validations were successful;
 *		// you can proceed with writing to the database or whatever
 * }
 * 
 * else {
 * 		// this means at least one of your fields did not validate properly.
 * }
 * </code>
 * 
 * <p>To determine whether there were any errors on the form, you use code like this:
 *
 * <code> 
 * ArrayList fieldMessages = errors.get(fieldname);
 * 
 * if (fieldMessages.isEmpty()) {
 * 		// there were no errors on the form
 * }
 * else {
 * 		// there were errors on the form
 * }
 * </code>
 * 
 * <p>There are 15 types of validations possible; below there are details on the
 * semantics as well as the syntax of the addValidation call:
 * 
 * <ul>
 * <li> NO_BLANKS � test that a given input is not blank
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.NO_BLANKS);
 * 	</ul>
 * <li> IS_A_NUMBER � test that a given input is a valid number, e.g. "5", "5.5", "-5", etc.
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_A_NUMBER);
 * 	</ul>
 * <li> IS_IN_RANGE � test that a given input is an integer in a specified range (inclusive), e.g. is between 1 and 5 inclusive
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_IN_RANGE, lowerBound, upperBound);
 * 	<li> e.g. addValidation(fieldname, Validator.IS_IN_RANGE, 1, 5);  
 * 	</ul>
 * <li> IS_A_DATE � test that a given input is in "MM/DD/YYYY" format. Note that this format is specified as
 * Validator.DATE.getDescription() (DATE is a static ValidationRegularExpression
 * object)
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_A_DATE)
 * 	</ul>
 * <li> IS_DATE_TIME � test that a given input is in "MM/DD/YYYY HH:MM a" format.
 * Note that this format is specified as Validator.DATE_TIME.getDescription()
 * (DATE is a static ValidationRegularExpression object)
 * 	<ul>
 * 	<li> addValidation(prefix, Validator.IS_A_DATE)
 * 	<li> This validation is a bit different from the others.  It assumes that you
 * are attempting to validate a *group* of inputs, all of which have the same
 * prefix, and have the following suffixes:
 * 		<ul>
 * 		<li> Date (in MM/dd/yyyy format)
 * 		<li> Hour (1 - 12)
 * 		<li> Minute (0 - 59)
 * 		<li> Half ("am" or "pm")
 * 		</ul>
 * 	<li> e.g., if you have a start datetime and end datetime on the same form,
 * 		you might have startDate, startHour, startMinute, startHalf, and similarly
 * 		for end.
 * 	<li> carrying on with this example, you could validate these inputs as follows:
 * 		<ul>
 * 		<li> addValidation("start", IS_DATE_TIME);
 * 		<li> addValidation("end", IS_DATE_TIME);
 * 		</ul> 
 * 	<li> to complete the example, if errors exist on the form, they will appear in the "start" and "end" key of the error messages
 * 	</ul>
 * <li> CHECK_SAME � test that one field equals the value of another. This is null-safe meaning that if
 * both fields are blank, no errors are reported.
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.CHECK_SAME, fieldname2)
 * 	<li> e.g. addValidation(�password�, Validator.CHECK_SAME, �confirmPassword�);
 * 	</ul>
 * <li> IS_A_EMAIL � test that one field is in proper e-mail format, ie
 * "username@institution.domain". Note that this format is specified as
 * Validator.EMAIL.getDescription() (EMAIL is a static
 * ValidationRegularExpression object)
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_A_EMAIL);
 * 	</ul>
 * <li> IS_A_PHONE_NUMBER - test that one field is in proper phone number format,
 * ie "123-456-7890".  Note that this format is specified as
 * Validator.PHONE_NUMBER.getDescription() (PHONE_NUMBER is a static
 * ValidationRegularExpression object)
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_A_PHONE_NUMER);
 * 	</ul> 
 * <li> LENGTH_NUMERIC_COMPARISON � test that the length of
 * a string meets some numeric comparison test, e.g. ">= 8 characters long".
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.LENGTH_NUMERIC_COMPARISON, operator, compareTo);
 * 	<li> operator is a NumericComparisonOperator object.
 * 	(NumericComparisonOperator is a typesafe enumeration which implements a
 * controlled vocabulary of �=�, �!=�, � <�, � <=�, �>�, �>=�)
 * 	<li> compareTo is an integer
 * 	<li> e.g. addValidation(�password�, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, 8);
 * 	</ul>
 * <li> ENTITY_EXISTS � not yet implemented, but it will test that the value of an input field holds
 * the primary key of some entity in the database; the entity type is implicitly
 * specified by an EntityDAO passed to addValidation
 * <li> USERNAME_UNIQUE - not yet implemented, but it will test that there are no users with the specified
 * username
 * <li> IS_AN_INTEGER � test that a string is a valid integer
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_AN_INTEGER)
 * 	</ul>
 * <li> IS_A_FILE � not yet implemented, but it will test that the value of an input is a successfully
 * updated file
 * <li> IS_OF_FILE_TYPE � not yet implemented, but it will test that
 * an uploaded file has a specified file type
 * <li> IS_IN_SET � test that an input field belongs to some ad-hoc set 
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_IN_SET, set);
 * 	<li> set is an ArrayList of Entities
 * 	</ul>
 * <li> IS_A_PASSWORD � test that an input field is a valid password
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_A_PASSWORD);
 * 	</ul>
 * <li> IS_A_USERNAME - test that an input field is a valid username
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_A_USERNAME, username);
 * 	</ul>
 * <li> IS_VALID_TERM � test that an input field holds the id of some controlled vocabulary term
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.IS_VALID_TERM, termType);
 * 	<li> termType is a TermType object
 * 	<li> TermType is a controlled vocabulary of controlled vocabularies, e.g. it lists all of the controlled vocabularies
 * 	<li> e.g. addValidation(�statusId�, Validator.IS_VALID_TERM, TermType.STATUS);
 * 	</ul>
 * <li> COMPARES_TO_STATIC_VALUE � test that an input field is an integer which
 * matches a Boolean comparison to some fixed integer value
 * 	<ul>
 * 	<li> addValidation(fieldname, Validator.COMPARES_TO_STATIC_VALUE, operator, compareTo);
 * 	<li> operator is a NumericComparisonOperator object
 * 	<li> e.g. addValidation(�numSubjects�, Validator.COMPARES_TO_STATIC_VALUE, NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, 1)
 * 	</ul>
 * <li> DATE_IS_AFTER_OR_EQUAL - test that one date comes after or is equal to
 * another
 * 	<ul>
 * 	<li> addValidation(laterDateFieldName, Validator.DATE_IS_AFTER_OR_EQUAL, earlierDateFieldName);
 * 	<li> e.g. addValidation("endDate", Validator.DATE_IS_AFTER_OR_EQUAL, "startDate");
 * 	</ul>
 * <li> ENTITY_EXISTS_IN_STUDY - test that an entity exists in a particular study
 * 	<ul>
 * 	<li> addValidation(entityIDFieldName, Validator.ENTITY_EXISTS_IN_STUDY, auditableEntityDAO, studyBean);
 * 	<li> note that the DAO provided must define findByPKAndStudyName; otherwise this validation always fails.
 * 	<li> e.g. addValidation("studySubject", Validator.ENTITY_EXISTS_IN_STUDY,
 * subjectStudyDAO, currentStudy);
 * 	</ul>
 * <li> NO_BLANKS_SET - test that at least one value was entered for a set (e.g. a checkbox or multiple select)
 * 	<ul>
 * 	<li> addValidation(fieldName, Validator.NO_BLANKS_SET);
 * 	<li> note that while the input name on the form should include brackets
 * 		(e.g. "input123[]"), brackets should not be included in the call to addValidation
 * 	<li> e.g. addValidation("input123", Validator.NO_BLANKS_SET);
 * 	</ul>
 * </ul>
 * 
 * <p>How to add a validation type</p>
 * 
 * <p> We will take, as an example, a new type of validation which checks that
 * the input field contains a multiple of some integer (specified by the control.)
 * For example, if you are asking the user to input time in 15-minute increments,
 * you can use this validation to ensure that the user enters a multiple of 15.
 * 
 * <ul>
 * <li> assign the type a public static int, e.g. IS_MULTIPLE
 * <li> if necessary, write an addValidation method to store that type of validation in
 * the validations HashMap. make sure to note that the addValidation method you
 * wrote is for the validation type in the comments.
 * <br/> a new addValidation method is necessary if there are no other addValidation
 * methods which accept the kind of arguments you need.  in this case, we need only
 * the multiple argument (15, in the example used above.)  In fact, there is no such
 * method (at the time of writing), so we add one:
 * <br/><br/>
 * <code>
 * public void addValidation(String fieldName, int type, int multiple) {
 * 		// for use with IS_MULTIPLE validations
 * 		Validation v = new Validation(type);
 * 		v.addArgument(multiple);
 * 		addValidation(fieldName, v);
 * }
 * </code>
 * 
 * <br/><br/> Note that in the code above, we added a comment to indicate which
 * validation type the addValidation method is used for.  This is a courtesy to
 * other developers who are using the class.
 * 
 * <li> if it's not necessary to write an addValidation method, determine which
 * addValidation will be used to store the type of validation you are writing,
 * and note this fact in the comments for the appropriate addValidation method.
 * 
 * <li> if necessary, write a method to execute the validation, similar to
 * isBlank, isNumber, etc. note that the utility functions
 * intComparesToStaticValue and matchesRegex can handle a wide variety of validations.
 * don't reinvent the wheel!
 * <br/> a new method is necessary, so we will write one:
 * <br/><br/>
 * <code>
 * public boolean isMultiple(String fieldName, int multiple) {
 * 		String fieldValue = request.getParameter(fieldName);
 *
 * 		if (fieldValue == null) {
 * 			return false;
 * 		}
 * 
 * 		try {
 * 			int i = Integer.parseInt(fieldValue);
 * 			if ((i % multiple) != 0) {
 * 				return false;
 * 			}
 * 		} catch (Exception e) {
 * 			return false;
 * 		}
 * 		return true;
 * }
 * </code>
 * 
 * <li> in validate(String fieldName, Validation v, HashMap errors), add a case
 * to respond to the new validation type be sure to get all necessary arguments,
 * determine if the input field passes validation, and call addError(fieldName, v)
 * if the field does not pass validation
 * <br/>For example:
 * <br/><br/>
 * 
 * <code>
 * 	// ... (top of switch statement)
 * 	case IS_MULTIPLE:
 * 		int multiple = v.getArgument(0);
 * 		if (!isMultiple(fieldName, multiple)) {
 * 			addError(fieldName, v);
 * 		}
 * 		break;
 * 	// ... (bottom of switch statement)
 * </code>
 * 
 * <li> in addError(String fieldName, Validation v), add a case to set a default
 * error message which will be displayed to the user if an input violates the
 * validation rule.
 * <br/>For example:
 * <br/><br/>
 * 
 * <code>
 * 	// ... (top of switch statement)
 * 	case IS_MULTIPLE:
 * 		int multiple = v.getArgument(0);
 * 		errorMessage = "The input you provided is not a multiple of " + multiple + ".";
 * 		break;
 * 	// ... (bottom of switch statement)
 * </code>
 *
 * @author ssachs
 * 
 */

// TODO: add ability to halt at a given validation
// TODO: work on making this class extensible. this is probably best achieved by
// subclassing the Validation class
//	and making it more beefy (ie adding a checkIfValidated() type method to that
// class,
//	so that the work is done there and not in this class)
public class Validator {
	public static final ValidatorRegularExpression DATE = new ValidatorRegularExpression(
			"MM/DD/YYYY", "[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}");
	
	public static final ValidatorRegularExpression DATE_TIME = new ValidatorRegularExpression(
			"MM/DD/YYYY HH:MM a", "[0-9]{1,2}/[0-9]{1,2}/[0-9]{4} [0-9]{1,2}:[0-9]{1,2} [aApP][mM]");
	
	public static final ValidatorRegularExpression EMAIL = new ValidatorRegularExpression(
			"username@institution.domain", ".+@.+\\..*");
	
	public static final ValidatorRegularExpression PHONE_NUMBER = new ValidatorRegularExpression(
			"123-456-7890", "[0-9]{3}[\\-\\.][0-9]{3}[\\-\\.][0-9]{4}");
	
    public static final ValidatorRegularExpression POSTAL_CODE = new ValidatorRegularExpression("12345 or 12345-6789", "[0-9]{5}(\\-[0-9]{4})?");
    
	public static final int PASSWORD_MINIMUM_LENGTH = 8;
	
	public static final ValidatorRegularExpression USERNAME = new ValidatorRegularExpression(
			"at least 5 alphanumeric or underscore characters", "[A-Za-z0-9_]{5,}");
	
	public static final int NO_BLANKS = 1;
	public static final int IS_A_NUMBER = 2;
	public static final int IS_IN_RANGE = 3;
	public static final int IS_A_DATE = 4;
	public static final int IS_DATE_TIME = 21;
	public static final int CHECK_SAME = 5;//this is for matching passwords, e.g.
	public static final int IS_A_EMAIL = 6;
	public static final int LENGTH_NUMERIC_COMPARISON = 7;
	public static final int ENTITY_EXISTS = 8; // for checking if a primary key is valid
	public static final int USERNAME_UNIQUE = 9;
	public static final int IS_AN_INTEGER = 10;
	public static final int IS_A_FILE = 11; // to check for uploads, making sure that there is a file present
	public static final int IS_OF_FILE_TYPE = 12;
	public static final int DATE_IS_AFTER_OR_EQUAL = 13;
	public static final int IS_IN_SET = 14; // for controlled vocabularies that aren't in the db
	public static final int IS_A_PASSWORD = 15;
	public static final int IS_A_USERNAME = 16;
	public static final int IS_VALID_TERM = 17;
	public static final int COMPARES_TO_STATIC_VALUE = 18; // for comparisons like "NumQuestions >= 1"
	public static final int ENTITY_EXISTS_IN_STUDY = 19;
	public static final int IS_A_PHONE_NUMBER = 20;
	public static final int NO_BLANKS_SET = 22;
	public static final int IN_RESPONSE_SET = 23;
	public static final int IN_RESPONSE_SET_SINGLE_VALUE = 24;
	public static final int MATCHES_INITIAL_DATA_ENTRY_VALUE = 25;
	public static final int IS_REQUIRED = 26;
	public static final int MATCHES_REGULAR_EXPRESSION = 27;
	public static final int DATE_IN_PAST = 28;
	public static final int IS_A_POSTAL_CODE = 29;
	/**
	 * The last field for which an addValidation method was invoked.  This is
	 * used by setErrorMessage(String).
	 */
	protected String lastField;
	
	protected HashMap validations;
	
	protected HashMap errors;
	
	protected HttpServletRequest request;
	
	public Validator(HttpServletRequest request) {
		validations = new HashMap();
		errors = new HashMap();
		this.request = request;
		lastField = "";
	}
	
	protected ArrayList getFieldValidations(String fieldName) {
		ArrayList fieldValidations;
		
		if (validations.containsKey(fieldName)) {
			fieldValidations = (ArrayList) validations.get(fieldName);
		} else {
			fieldValidations = new ArrayList();
		}
		return fieldValidations;
	}
	
	// function used to squirrel away the validations until validate is called
	public void addValidation(String fieldName, Validation v) {
		ArrayList fieldValidations = getFieldValidations(fieldName);
		fieldValidations.add(v);
		validations.put(fieldName, fieldValidations);
	}
	
	//	protected int getLastValidationInd(String fieldName) {
	//		ArrayList fieldValidations = getFieldValidations(fieldName);
	//		return fieldValidations.size() - 1;
	//	}
	
	/*
	 * use for: NO_BLANKS, IS_A_NUMBER, IS_A_DATE, IS_A_EMAIL, IS_AN_INTEGER,
	 * IS_A_PASSWORD, IS_A_USERNAME, IS_A_PHONE_NUMBER, IS_DATE_TIME, NO_BLANKS_SET
	 * DATE_IN_PAST
	 */
	public void addValidation(String fieldName, int type) {
		Validation v = new Validation(type);
		addValidation(fieldName, v);
	}
	
	/*
	 * use for: IS_IN_RANGE
	 */
	public void addValidation(String fieldName, int type, int start, int end) {
		// TODO: assert type == is in range
		// For finding out if a number is in a range
		
		Validation v = new Validation(type);
		v.addArgument(start);
		v.addArgument(end);
		
		addValidation(fieldName, v);
	}
	
	/*
	 * use for: CHECK_SAME, IS_A_FILE
	 */
	public void addValidation(String fieldName, int type, String dir) {
		// TODO: assert type = IS_OF_FILE_TYPE or CHECK_SAME or
		// DATE_IS_AFTER_OR_EQUAL
		
		// for checking to see if there is a file present in the system or not
		// also for IS_OF_FILE_TYPE - in this case dir is a file type
		
		Validation v = new Validation(type);
		v.addArgument(dir);
		
		addValidation(fieldName, v);
	}
	
	/*
	 * use for: LENGHT_NUMERIC_COMPARISON, COMPARES_TO_STATIC_VALUE
	 */
	public void addValidation(String fieldName, int type, NumericComparisonOperator operator,
			int compareTo) {
		// TODO: assert type = COMPARE_TO_STATIC_VALUE or LENGHT_NUMERIC_COMPARISON
		
		Validation v = new Validation(type);
		v.addArgument(operator);
		v.addArgument(compareTo);
		
		addValidation(fieldName, v);
	}
	
	/*
	 * use for: ENTITY_EXISTS_IN_STUDY
	 */
	public void addValidation(String fieldName, int type, AuditableEntityDAO dao, StudyBean study) {
		// for entity exists validation
		Validation v = new Validation(type);
		v.addArgument(dao);
		v.addArgument(study);
		
		addValidation(fieldName, v);
	}
	
	/*
	 * use for: ENTITY_EXISTS
	 */
	public void addValidation(String fieldName, int type, EntityDAO edao) {
		// for entity exists validation
		Validation v = new Validation(type);
		v.addArgument(edao);
		
		addValidation(fieldName, v);
	}
	
	// TODO: add is_of_file_type addValidation method
	
	/*
	 * use for: IS_IN_SET
	 */
	public void addValidation(String fieldName, int type, ArrayList set) {
		// TODO: assert type == is_in_set
		
		Validation v = new Validation(type);
		v.addArgument(set);
		
		addValidation(fieldName, v);
	}
	
	/*
	 * use for: IS_VALID_TERM
	 */
	public void addValidation(String fieldName, int type, TermType termType) {
		// assert type == is_valid_term
		
		Validation v = new Validation(type);
		v.addArgument(termType);
		
		addValidation(fieldName, v);
	}
	
	/**
	 * Add a validation to check that every response provided is an element
	 * of the specified set.
	 * @param fieldName The name of the input on the form.
	 * @param type The type of validation.  Should be IN_RESPONSE_SET or IN_RESPONSE_SET_SINGLE_VALUE.
	 * @param set The response set to check for membership.
	 */
	public void addValidation(String fieldName, int type, ResponseSetBean set) {
		Validation v = new Validation(type);
		v.addArgument(set);
		addValidation(fieldName, v);
	}
	
	/**
	 * Add a validation to check that the specified input
	 * matches the value from initial data entry.
	 * @param fieldName The name of the input.
	 * @param type The type of validation.  Should be MATCHES_INITIAL_DATA_ENTRY_VALUE.
	 * @param idb The bean representing the value from initial data entry.
	 */
	public void addValidation(String fieldName, int type, ItemDataBean idb, boolean isMultiple) {
		Validation v = new Validation(type);
		// we have to make this a a new String
		// to ensure that if someone calls idb.setValue()
		// before we get around to validating, we will still use the original value
		v.addArgument(new String(idb.getValue()));
		v.addArgument(isMultiple);
		
		addValidation(fieldName, v);		
	}
	
	/*
	 * Executes all of the validations which have been requested.
	 */
	public HashMap validate() {
		Set keys = validations.keySet();
		Iterator keysIt = keys.iterator();
		
		while (keysIt.hasNext()) {
			String fieldName = (String) keysIt.next();
			
			ArrayList fieldValidations = getFieldValidations(fieldName);
			for (int i = 0; i < fieldValidations.size(); i++) {
				Validation v = (Validation) fieldValidations.get(i);
				validate(fieldName, v);
			}
		}
		
		return errors;
	}
	
	protected void addError(String fieldName, Validation v) {
		String errorMessage = "";
		
		if (v.isErrorMessageSet()) {
			errorMessage = v.getErrorMessage();
		}
		else {
			switch (v.getType()) {
			case NO_BLANKS:
				errorMessage = "This field cannot be blank.";
				break;
			case IS_A_NUMBER:
				errorMessage = "This field should be a number.";
				break;
			case IS_IN_RANGE:
				float lowerBound = v.getFloat(0);
				float upperBound = v.getFloat(1);
				errorMessage =  "The input should be between " + new Float(lowerBound).intValue() + " and " + new Float(upperBound).intValue() + ".";
				break;
			case IS_A_DATE:
				errorMessage = "The input you provided is not a valid date in " + DATE.getDescription() + " format.";
				break;
			case IS_DATE_TIME:
				errorMessage = "The input you provided is not a valid date/time in " + DATE_TIME.getDescription() + " format.";
				break;
			case CHECK_SAME:
				errorMessage = "Your answers for this value did not match.  Please provide answers that match one another.";
				break;
			case IS_A_EMAIL:
				errorMessage = "The input you provided is not a valid email address in " + EMAIL.getDescription() + " format.";
				break;
			case IS_A_PHONE_NUMBER:
				errorMessage = "The input you provided is not a valid phone number in " + PHONE_NUMBER.getDescription() + " format.";
				break;
            case IS_A_POSTAL_CODE:
                errorMessage = "The input you provided is not a valid postal code in " + POSTAL_CODE.getDescription() + " format.";
                break;
			case ENTITY_EXISTS:
				errorMessage = "You did not select a valid entity.";
				break;
			case ENTITY_EXISTS_IN_STUDY:
				errorMessage = "You did not select a valid entity in the current study.";
				break;
			case USERNAME_UNIQUE:
				errorMessage =  "The username you selected already exists.";
				break;
			case IS_AN_INTEGER:
				errorMessage = "The input you provided is not an integer.";
				break;
				//		case IS_A_FILE:
				//			break;
				//		case IS_OF_FILE_TYPE:
				//			break;
			case IS_IN_SET:
				errorMessage = "The input you provided is not an acceptable option."	;
				break;
			case IS_A_PASSWORD:
				errorMessage = "The password must be at least " + PASSWORD_MINIMUM_LENGTH + " characters long.";
				break;
			case IS_A_USERNAME:
				errorMessage = "The input you provided is not a valid username in " + USERNAME.getDescription() + " format.";
				break;
			case IS_VALID_TERM:
				errorMessage = "The input you provided is invalid.";
				break;
			case COMPARES_TO_STATIC_VALUE:
				NumericComparisonOperator operator = (NumericComparisonOperator) v.getArg(0);
				float compareTo = v.getFloat(1);
				errorMessage = "The input you provided is not " + operator.getDescription() + " "
				+ new Float(compareTo).intValue() + ".";
				break;
			case LENGTH_NUMERIC_COMPARISON:
				NumericComparisonOperator operator2 = (NumericComparisonOperator) v.getArg(0);
				int compareTo2 = v.getInt(1);
				
				errorMessage = "The input you provided is not " + operator2.getDescription() + " " + compareTo2 + " characters long.";
				break;
			case DATE_IS_AFTER_OR_EQUAL:
				String earlierDateFieldName = (String) v.getString(0);
				
				String earlierDateValue = (String) request.getParameter(earlierDateFieldName);
				if ((earlierDateValue == null) || (earlierDateValue.equals(""))) {
					errorMessage = "The input you provided must not precede the earlier date.";
				}
				else {
					errorMessage = "The input you provided must not precede " + earlierDateValue + ".";
				}
				break;
			case NO_BLANKS_SET:
				errorMessage = "You must choose at least one value.";
				break;
			case IN_RESPONSE_SET:
				errorMessage = "All of the values you choose must be from the specified response set.";
				break;
			case IN_RESPONSE_SET_SINGLE_VALUE:
				errorMessage = "The values you specify must be from the valid response set.";
				break;
			case MATCHES_INITIAL_DATA_ENTRY_VALUE:
				errorMessage = "The value you specify does not match the value from initial data entry.";
				break;
			case IS_REQUIRED:
				errorMessage = "The input is required.";
				break;
			case DATE_IN_PAST:
				errorMessage = "The date you provided is not in the past.";
				break;
			case MATCHES_REGULAR_EXPRESSION:
				errorMessage = "The input does not match the regular expression " + v.getString(0) + ".";
			}
		}
		
		addError(fieldName, errorMessage);
	}
	
	protected void addError(String fieldName, String errorMessage) {
		Validator.addError(errors, fieldName, errorMessage);
	}
	
	/**
	 * Adds an error to a <code>HashMap</code> of errors generated by validate.
	 * This can be used for "one-off" validations, e.g.:
	 * 
	 * <code>
	 * errors = v.validate();
	 * 
	 * if (someSpecialConditionIsNotMet()) {
	 *     Validator.addError(errors, fieldName, "The special condition was not met.");
	 * }
	 * </code>
	 * 
	 * @param existingErrors
	 *          The <code>HashMap</code> of errors generated by a call to
	 *          <code>validate()</code>.
	 * @param fieldName
	 *          The field name to add the error to.
	 * @param errorMessage
	 *          The message to add to the field name.
	 */
	public static void addError(HashMap existingErrors, String fieldName, String errorMessage) {
		ArrayList fieldErrors;
		
		if (existingErrors.containsKey(fieldName)) {
			fieldErrors = (ArrayList) existingErrors.get(fieldName);
		}
		fieldErrors = new ArrayList();
		
		fieldErrors.add(errorMessage);
		
		existingErrors.put(fieldName, fieldErrors);
		
		return;
	}
	
	protected HashMap validate(String fieldName, Validation v) {
		switch (v.getType()) {
		case NO_BLANKS:
			if (isBlank(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_NUMBER:
			if (!isNumber(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_IN_RANGE:		 
			float lowerBound = v.getFloat(0);
			float upperBound = v.getFloat(1);
			
			if (!isInRange(fieldName, lowerBound, upperBound)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_DATE:
			if (!isDate(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_DATE_TIME:
			if (!isDateTime(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case CHECK_SAME:
			String compareField = v.getString(0);
			
			if (!isSame(fieldName, compareField)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_EMAIL:
			if (!isEmail(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_PHONE_NUMBER:
			if (!isPhoneNumber(fieldName)) {
				addError(fieldName, v);
			}
			break;
        case IS_A_POSTAL_CODE:
            if(!isPostalCode(fieldName)){
                addError(fieldName, v);
            }
            break;
		case ENTITY_EXISTS:
			EntityDAO edao = (EntityDAO) v.getArg(0);
			
			if (!entityExists(fieldName, edao)) {
				addError(fieldName, v);
			}
			break;
		case ENTITY_EXISTS_IN_STUDY:
			AuditableEntityDAO dao = (AuditableEntityDAO) v.getArg(0);
			StudyBean study = (StudyBean) v.getArg(1);
			
			if (!entityExistsInStudy(fieldName, dao, study)) {
				addError(fieldName, v);
			}
			break;
		case USERNAME_UNIQUE:
			UserAccountDAO udao = (UserAccountDAO) v.getArg(0);
			
			if (!usernameUnique(fieldName, udao)) {
				addError(fieldName, v);
			}
			break;
		case IS_AN_INTEGER:		  
			if (!isInteger(fieldName)) {
				addError(fieldName, v);
			}
			break;
			//		case IS_A_FILE:
			//			break;
			//		case IS_OF_FILE_TYPE:
			//			break;
		case IS_IN_SET:
			ArrayList set = (ArrayList) v.getArg(0);
			
			if (!isInSet(fieldName, set)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_PASSWORD:
			if (!lengthComparesToStaticValue(fieldName,
					NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, PASSWORD_MINIMUM_LENGTH)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_USERNAME:
			if (!isUsername(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_VALID_TERM:
			TermType termType = (TermType) v.getArg(0);
			
			if (!isValidTerm(fieldName, termType)) {
				addError(fieldName, v);
			}
			break;
		case COMPARES_TO_STATIC_VALUE:
			NumericComparisonOperator operator = (NumericComparisonOperator) v.getArg(0);
			float compareTo = v.getFloat(1);
			
			if (!comparesToStaticValue(fieldName, operator, compareTo)) {
				addError(fieldName, v);
			}
			break;
		case LENGTH_NUMERIC_COMPARISON:
			NumericComparisonOperator operator2 = (NumericComparisonOperator) v.getArg(0);
			int compareTo2 = v.getInt(1);
			
			if (!lengthComparesToStaticValue(fieldName, operator2, compareTo2)) {
				addError(fieldName, v);
			}
			break;
		case DATE_IS_AFTER_OR_EQUAL:
			String earlierDateFieldName = (String) v.getString(0);
			
			if (!isDateAfterOrEqual(fieldName, earlierDateFieldName)) {
				addError(fieldName, v);
			}
			break;
		case NO_BLANKS_SET:
			if (isSetBlank(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IN_RESPONSE_SET:
			ResponseSetBean rsb = (ResponseSetBean) v.getArg(0);
			
			if (!isInResponseSet(fieldName, rsb, true)) {
				addError(fieldName, v);
			}
			break;
		case IN_RESPONSE_SET_SINGLE_VALUE:
			ResponseSetBean rsbSingle = (ResponseSetBean) v.getArg(0);
			
			if (!isInResponseSet(fieldName, rsbSingle, false)) {
				addError(fieldName, v);
			}
			break;
		case MATCHES_INITIAL_DATA_ENTRY_VALUE:
			String oldValue = (String) v.getString(0);
			boolean isMultiple = v.getBoolean(1);
			if (!valueMatchesInitialValue(fieldName, oldValue, isMultiple)) {
				addError(fieldName, v);
			}
			break;
		case IS_REQUIRED:
			if (isBlank(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case DATE_IN_PAST:
			if (!isDateInPast(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case MATCHES_REGULAR_EXPRESSION:
			if (!matchesRegex(fieldName, v.getString(0))) {
				addError(fieldName, v);
			}
			break;
		}
		
		return errors;
	}
	
	// validation functions that determine whether a field passes validation
	protected boolean isBlank(String fieldName) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return true;
		}
		
		if (fieldValue.trim().equals("")) {
			return true;
		}
		
		return false;
	}
	
	protected boolean isNumber(String fieldName) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		
		try {
			float f = Float.parseFloat(fieldValue);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	protected boolean isInRange(String fieldName, float lowerBound, float upperBound) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		
		try {
			float i = Float.parseFloat(fieldValue);
			
			if ((i >= lowerBound) && (i <= upperBound)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		
		return false;
	}
	
	/**
	 * @param fieldName The name of a field containing some text string.
	 * @return <code>true</code> if the field contains a valid date in "MM/dd/yyyy" format;
	 * <code>false</code> otherwise.
	 */
	protected boolean isDate(String fieldName) {
		String fieldValue = request.getParameter(fieldName);
		if (fieldValue == null) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		sdf.setLenient(false);
		try {
			java.util.Date date = sdf.parse(fieldValue);
			return isYearNotFourDigits(date);
		} catch (ParseException fe) {
			return false; 
		}
	}
	
	/**
	 * @param fieldName The name of a field containing some text string.
	 * @return <code>true</code> if the field contains a valid date in "MM/dd/yyyy" format and is in the past;
	 * <code>false</code> otherwise.
	 */
	protected boolean isDateInPast(String fieldName) {
		Date d = null;
		if (fieldName != null) {
			d = FormProcessor.getDateFromString(request.getParameter(fieldName)); 
		}
		if (d != null) {
			Date today = new Date();
			if (today.compareTo(d) >= 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Assumes the date was entered in "MM/dd/yyyy" or "MM/dd/yyyy hh:mm a" format.
	 * @param d The date whose year we want to query for having four or less digits.
	 * @return <code>true</code> if the date's year has less than four digits;
	 * <code>false</code> otherwise.
	 */
	protected boolean isYearNotFourDigits(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		if (c.get(Calendar.YEAR) < 1000 || c.get(Calendar.YEAR) > 9999) { // did the user enter a year with less than 4 digits or more than 4 digits?
			return false;
		}
		return true;
	}
	
	/**
	 * @param prefix The prefix for a set of fields which together are used to input a date/time.
	 * @return <code>true</code> if the fields encode a valid date/time in "MM/dd/yyyy hh:mm a" format.
	 * <code>false</code> otherwise.
	 */
	protected boolean isDateTime(String prefix) {
		String date = request.getParameter(prefix + "Date");
		String hour = request.getParameter(prefix + "Hour");
		String minute = request.getParameter(prefix + "Minute");
		String half = request.getParameter(prefix + "Half");
		
		if ((date == null) || (hour == null) || (minute == null) || (half == null)) {
			return false;
		}
		
		String fieldValue = date + " " + hour + ":" + minute + ":00 " + half;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		sdf.setLenient(false);
		try {
			java.util.Date result = sdf.parse(fieldValue);
			return isYearNotFourDigits(result);
		} catch (Exception fe) {
			return false;
		}
	}
	
	protected boolean isUsername(String fieldName) {
		return matchesRegex(fieldName, USERNAME);
	}
	
	protected boolean isSame(String field1, String field2) {
		String value1 = request.getParameter(field1);
		String value2 = request.getParameter(field2);
		
		if ((value1 == null) && (value2 == null)) {
			return true;
		}
		
		if (value1 == null) {
			return false;
		}
		
		if (value2 == null) {
			return false;
		}
		
		return value1.equals(value2);
	}
	
	protected boolean isEmail(String fieldName) {
		return matchesRegex(fieldName, EMAIL);
	}
	
    protected boolean isPhoneNumber(String fieldName) {
        return matchesRegex(fieldName, PHONE_NUMBER);
    }
    
    protected boolean isPostalCode(String fieldName) {
        return matchesRegex(fieldName, POSTAL_CODE);
    }
    
	protected boolean lengthComparesToStaticValue(String fieldName, NumericComparisonOperator operator,
			int compareTo) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		
		try {
			int length = fieldValue.length();
			return intComparesToStaticValue(length, operator, compareTo);
		} catch (Exception e) {
			return false;
		}
	}
	
	// TODO: entity exists method
	
	protected boolean isInteger(String fieldName) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		try {
			int i = Integer.parseInt(fieldValue);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	// TODO: is_a_file, IS_OF_FILE_TYPE methods
	
	protected boolean isInSet(String fieldName, ArrayList set) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		
		if (set == null) {
			return false;
		}
		
		if (set.contains(fieldValue)) {
			return true;
		}
		
		return false;
	}
	
	protected boolean isValidTerm(String fieldName, TermType termType) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		
		try {
			int i = Integer.parseInt(fieldValue);
			
			if (termType.equals(TermType.ENTITY_ACTION)) {
				return EntityAction.contains(i);
			} else if (termType.equals(TermType.ROLE)) {
				return Role.contains(i);
			} else if (termType.equals(TermType.STATUS)) {
				return Status.contains(i);
			} else if (termType.equals(TermType.USER_TYPE)) {
				return UserType.contains(i);
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	protected boolean comparesToStaticValue(String fieldName, NumericComparisonOperator operator,
			float compareTo) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		
		try {
			float i = Float.parseFloat(fieldValue);
			return floatComparesToStaticValue(i, operator, compareTo);
		} catch (Exception e) {
			return false;
		}
	}
	
	// utility functions used by the validation functions
	protected boolean intComparesToStaticValue(int i, NumericComparisonOperator operator, int compareTo) {
		boolean compares = false;
		
		if (operator.equals(NumericComparisonOperator.EQUALS)) {
			compares = i == compareTo;
		} else if (operator.equals(NumericComparisonOperator.NOT_EQUALS)) {
			compares = i != compareTo;
		} else if (operator.equals(NumericComparisonOperator.GREATER_THAN)) {
			compares = i > compareTo;
		} else if (operator.equals(NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO)) {
			compares = i >= compareTo;
		} else if (operator.equals(NumericComparisonOperator.LESS_THAN)) {
			compares = i < compareTo;
		} else if (operator.equals(NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO)) {
			compares = i <= compareTo;
		}
		
		return compares;
	}
	
	
	//	 utility functions used by the validation functions
	protected boolean floatComparesToStaticValue(float i, NumericComparisonOperator operator, float compareTo) {
		boolean compares = false;
		
		if (operator.equals(NumericComparisonOperator.EQUALS)) {
			compares = i == compareTo;
		} else if (operator.equals(NumericComparisonOperator.NOT_EQUALS)) {
			compares = i != compareTo;
		} else if (operator.equals(NumericComparisonOperator.GREATER_THAN)) {
			compares = i > compareTo;
		} else if (operator.equals(NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO)) {
			compares = i >= compareTo;
		} else if (operator.equals(NumericComparisonOperator.LESS_THAN)) {
			compares = i < compareTo;
		} else if (operator.equals(NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO)) {
			compares = i <= compareTo;
		}
		
		return compares;
	}
	
	
	protected boolean matchesRegex(String fieldName, String regex) {
		ValidatorRegularExpression vre = new ValidatorRegularExpression(regex, regex);
		return matchesRegex(fieldName, vre);
	}
	
	protected boolean matchesRegex(String fieldName, ValidatorRegularExpression re) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		
		Pattern p = Pattern.compile(re.getRegularExpression());
		Matcher m = p.matcher(fieldValue);
		
		if (m.matches()) {
			return true;
		}
		
		return false;
	}
	
	protected boolean entityExists(String fieldName, EntityDAO edao) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		
		try {
			int id = Integer.parseInt(fieldValue);
			EntityBean e = edao.findByPK(id);
			
			if (!e.isActive()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	protected boolean entityExistsInStudy(String fieldName, AuditableEntityDAO dao, StudyBean study) {
		
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return false;
		}
		
		try {
			int id = Integer.parseInt(fieldValue);
			AuditableEntityBean e = (AuditableEntityBean) dao.findByPKAndStudy(id, study);
			
			if (!e.isActive()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	protected boolean usernameUnique(String fieldName, UserAccountDAO udao) {
		String fieldValue = request.getParameter(fieldName);
		
		if (fieldValue == null) {
			return true;
		}
		
		try {
			UserAccountBean ub = (UserAccountBean) udao.findByUserName(fieldValue);
			
			if (ub.isActive()) {
				return false;
			}
		} catch (Exception e) {
			return true;
		}
		
		return true;
	}
	
	protected boolean isDateAfterOrEqual(String laterDateFieldName, String earlierDateFieldName) {
		String laterDateValue = request.getParameter(laterDateFieldName);
		String earlierDateValue = request.getParameter(earlierDateFieldName);
		
		if ((laterDateValue == null) || (earlierDateValue == null)) {
			return false;
		}
		
		Date laterDate = FormProcessor.getDateFromString(laterDateValue);
		Date earlierDate = FormProcessor.getDateFromString(earlierDateValue);
		
		if (laterDate.compareTo(earlierDate) >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean isSetBlank(String fieldName) {
		String fieldValues[] = request.getParameterValues(fieldName);
		
		if ((fieldValues == null) || (fieldValues.length == 0)) {
			return true;
		}
		
		return false;
	}
	
	protected boolean isInResponseSet(String fieldName, ResponseSetBean set, boolean multValues) {
		// prep work - makes checking for a value in the set very fast
		HashMap values = new HashMap();
		
		ArrayList options = set.getOptions();
		for (int i = 0; i < options.size(); i++) {
			ResponseOptionBean rob = (ResponseOptionBean) options.get(i);
			values.put(rob.getValue(), Boolean.TRUE);
		}
		
		String fieldValues[];
		if (multValues) {
			fieldValues = request.getParameterValues(fieldName);
		}
		else {
			fieldValues = new String[1];
			String fieldValue = request.getParameter(fieldName);
			fieldValues[0] = (fieldValue == null) ? "" : fieldValue;
		}
		
		// this means the user didn't fill in anything - and nothing is still,
		// trivially, in the response set
		if (fieldValues == null) {
			return true;
		}
		
		for (int i = 0; i < fieldValues.length; i++) {
			String value = fieldValues[i];
			
			// in principle this shouldn't happen, but since the empty valye is
			// trivially a member of the response set, it's okay
			if (value == null) {
				continue;
			}
			
			if (!values.containsKey(value)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Determine if the value for the specified field matches the value in the bean.
	 * 
	 * @param fieldName The name of the input.
	 * @param oldValue The data to be matched against the input value.
	 * @param isMultiple <code>true</code> if the input is a checkbox or multiple select, <code>false</code> otherwise.
	 * @return <code>true</code> if the value of fieldName matches the value in idb,
	 * <code>false</code> otherwise.
	 */
	protected boolean valueMatchesInitialValue(String fieldName, String oldValue, boolean isMultiple) {
		String fieldValue = "";
		String glue = "";
		
		if (isMultiple) {
			String[] fieldValues = request.getParameterValues(fieldName);
			
			if (fieldValues != null) {
				for (int i = 0; i < fieldValues.length; i++) {
					fieldValue += fieldValue + glue + fieldValues[i];
					glue = ",";
				}
			}
		}
		else {
			fieldValue = request.getParameter(fieldName);
		}
		
		if (fieldValue == null) {
			return false;
		}
		
		return fieldValue.equals(oldValue);		
	}
	
	/**
	 * Set the error message that is generated if the last field added
	 * to the Validator does not properly validate.
	 * @param message The error message to display.
	 */
	public void setErrorMessage(String message) {
		if (lastField == null) {
			return ;
		}
		
		ArrayList fieldValidations = (ArrayList) validations.get(lastField);
		if (fieldValidations == null) {
			return ;
		}
		
		int lastInd = fieldValidations.size() - 1;
		Validation v = (Validation) fieldValidations.get(lastInd);
		if (v == null) {
			return ;
		}
		
		v.setErrorMessage(message);
		fieldValidations.set(lastInd, v);
		validations.put(lastField, fieldValidations);
	}
	
	public static Validation processCRFValidationFunction(String inputFunction) throws Exception {
		String fname;
		ArrayList args;
		
		HashMap numArgsByFunction = new HashMap();
		numArgsByFunction.put("range", new Integer(2));
		numArgsByFunction.put("gt", new Integer(1));
		numArgsByFunction.put("lt", new Integer(1));
		numArgsByFunction.put("gte", new Integer(1));
		numArgsByFunction.put("lte", new Integer(1));
		numArgsByFunction.put("ne", new Integer(1));
		numArgsByFunction.put("eq", new Integer(1));
		
		HashMap valTypeByFunction = new HashMap();
		valTypeByFunction.put("range", new Integer(Validator.IS_IN_RANGE));
		valTypeByFunction.put("gt", new Integer(Validator.COMPARES_TO_STATIC_VALUE));
		valTypeByFunction.put("lt", new Integer(Validator.COMPARES_TO_STATIC_VALUE));
		valTypeByFunction.put("gte", new Integer(Validator.COMPARES_TO_STATIC_VALUE));
		valTypeByFunction.put("lte", new Integer(Validator.COMPARES_TO_STATIC_VALUE));
		valTypeByFunction.put("ne", new Integer(Validator.COMPARES_TO_STATIC_VALUE));
		valTypeByFunction.put("eq", new Integer(Validator.COMPARES_TO_STATIC_VALUE));
		
		HashMap compareOpByFunction = new HashMap();
		compareOpByFunction.put("gt", NumericComparisonOperator.GREATER_THAN);
		compareOpByFunction.put("lt", NumericComparisonOperator.LESS_THAN);
		compareOpByFunction.put("gte", NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO);
		compareOpByFunction.put("lte", NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO);
		compareOpByFunction.put("ne", NumericComparisonOperator.NOT_EQUALS);
		compareOpByFunction.put("eq", NumericComparisonOperator.EQUALS);
		
		// this is a regular expression for the pattern:
		// func: fname(arg1 [, arg2] ... [, argn])
		// if it matches then:
		//		fname is group 1
		//		arg1 is group 2
		//		"," + arg2 is group 3
		//		...
		//		"," + argn is group n + 1
		Pattern funcPattern = Pattern.compile("func:\\s*([A-Za-z]+)\\(([^,]*)?(,[^,]*)*\\)");
		Matcher funcMatcher = funcPattern.matcher(inputFunction);
		
		if (!funcMatcher.matches()) {
			throw new Exception("The syntax is incorrect.");
			// error: the syntax is incorrect, should be func: fname(arg1,...,argn)
		}
		
		int numGroups = funcMatcher.groupCount();
		// note that numGroups must be > 1
		fname = funcMatcher.group(1);
		args = new ArrayList();
		for (int i = 2; i <= funcMatcher.groupCount(); i++) {
			String arg = funcMatcher.group(i);
			
			if (StringUtil.isBlank(arg)) {
				continue;
			}
			
			// if i > = 3, then we are dealing with arg2 or above
			// this means we need to get rid of the preceding ,
			if (i >= 3) {
				arg = arg.substring(1);
			}
			arg = arg.trim();
			args.add(arg);
		}
		
		if (!fname.equalsIgnoreCase("range") && !fname.equalsIgnoreCase("gt")
				&& !fname.equalsIgnoreCase("lt")  && !fname.equalsIgnoreCase("gte")
				&& !fname.equalsIgnoreCase("lte") && !fname.equalsIgnoreCase("eq")
				&& !fname.equalsIgnoreCase("ne")) {
			throw new Exception("The VALIDATION column has an invalid function");
		}
		
		// test that the right number of arguments have been provdided; complain if not        
		Integer numProperArgsInFunction = (Integer) numArgsByFunction.get(fname);
		if (args.size() != numProperArgsInFunction.intValue()) {
			throw new Exception("The VALIDATION column has an invalid function: the number of arguments is incorrect");
		}
		
		// test that each argument is a number; complain if not
		for (int i = 0; i < args.size(); i++) {
			int ord = i + 1;
			try {
				float f = Float.parseFloat((String) args.get(i));
			} catch (Exception e) {
				throw new Exception("The VALIDATION column has an invalid function: argument " + ord + " is not a number");
			}
		}
		
		
		// success - all tests have been passed
		// now we compose the validation object created by this function
		Integer valType = (Integer) valTypeByFunction.get(fname); 
		Validation v = new Validation(valType.intValue());
		
		if (!fname.equalsIgnoreCase("range")) {	    
			NumericComparisonOperator operator = (NumericComparisonOperator) compareOpByFunction.get(fname);
			v.addArgument(operator);
		}
		
		for (int i = 0; i < args.size(); i++) {
			float f = Float.parseFloat((String) args.get(i));
			v.addArgument(f);
			
		}
		
		return v;
	}
	
	public static Validation processCRFValidationRegex(String inputRegex) throws Exception {
		Validation v = new Validation(Validator.MATCHES_REGULAR_EXPRESSION);
		
		String pieces[] = inputRegex.split(":");
		
		String regex = "";
		if ((pieces != null) && (pieces.length > 1)) {
			regex = pieces[1];
		}
		
		regex = regex.trim();
		int first = regex.indexOf("/");
		int last = regex.lastIndexOf("/");
		String finalRegexp = regex.substring(first + 1, last);
		
		if (StringUtil.isBlank(finalRegexp)) {
			throw new Exception("The regular expression is blank.");
		}
		
		v.addArgument(finalRegexp);
		return v;
	}
}