/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.core.form;

import org.akaza.openclinica.core.form.*;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;

import java.io.FileInputStream;
import java.util.*;
import test.org.akaza.openclinica.dao.core.*;
import org.akaza.openclinica.dao.login.*;

import org.akaza.openclinica.bean.core.*;
import org.xml.sax.SAXException;

/**
 * @author ssachs
 *
 */
// we need to extend DAOTestBase so we can have a datasource
public class ValidatorTest extends DAOTestBase {
	/*
	 protected void setUp() throws Exception {
	 super.setUp();
	 digester.setInputStream(new FileInputStream(SQL_DIR + "event_definition_crf_dao.xml"));
	 
	 
	 try {
	 digester.run();    
	 } catch (SAXException saxe) {
	 saxe.printStackTrace();
	 }
	 
	 super.ds = setupNewNewPostgresDataSource();
	 //super.ds = setupOraDataSource();
	  //super.ds = setupDataSource();
	   sdao = new EventDefinitionCRFDAO(super.ds, digester);   
	   }
	   */
	
	private UserAccountDAO uadao;
	private DAODigester digester = new DAODigester();
	private Validator v;
	private HttpServletRequestImplementation request;
	
	public ValidatorTest(String name) {
		super(name);
		request = new HttpServletRequestImplementation();
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(ValidatorTest.class);
	}
	
	protected void setUp() throws Exception {
		request = new HttpServletRequestImplementation();
		super.setUp();
		digester.setInputStream(new FileInputStream(SQL_DIR + "useraccount_dao.xml"));
		
		try {
			digester.run();    
		} catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		
		super.ds = setupTestDataSource();
		uadao = new UserAccountDAO(super.ds, digester);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testNoBlanksSuccess() {
		request.setParameter("field", "value");
		v = new Validator(request);
		v.addValidation("field", Validator.NO_BLANKS);
		HashMap h = v.validate();
		assertEquals("success detected", h.containsKey("field"), false);		
	}
	
	public void testNoBlanksFailure() {
		request.clear();
		v = new Validator(request);
		v.addValidation("field", Validator.NO_BLANKS);
		HashMap h = v.validate();
		assertEquals("failure detected", h.containsKey("field"), true);
	}
	
	public void testNoBlanksFailureWithCustomErrorMessage() {
		String message = "custom error message";
		
		request.clear();
		v = new Validator(request);
		v.addValidation("field", Validator.NO_BLANKS);
		v.setErrorMessage(message);
		HashMap h = v.validate();
		
		assertEquals("failure detected", h.containsKey("field"), true);
		
		ArrayList outputMessages = (ArrayList) h.get("field");
		assertNotNull("messages well-formed", outputMessages);
		assertEquals("only 1 error message", outputMessages.size(), 1);

		String outputMessage = (String) outputMessages.get(0);
		assertTrue("correct error message output", message.equals(outputMessage));
	}
	
	public void testIsANumberSuccess() {
		request.setParameter("field", "5.5");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_NUMBER);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);		
	}
	
	public void testIsANumberFailure() {
		request.setParameter("field", "not a number");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_NUMBER);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testIsInRangeSuccess() {
		request.setParameter("field", "4");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_IN_RANGE, 1, 5);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testIsInRangeFailure() {
		request.setParameter("field", "10");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_IN_RANGE, 1, 5);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testIsDateTimeSuccessAM() {
		request.setParameter("fieldDate", "10/20/2004");
		request.setParameter("fieldHour", "11");
		request.setParameter("fieldMinute", "20");
		request.setParameter("fieldHalf", "AM");

		v = new Validator(request);
		v.addValidation("field", Validator.IS_DATE_TIME);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}

	public void testIsDateTimeSuccessPM() {
		request.setParameter("fieldDate", "10/20/2004");
		request.setParameter("fieldHour", "11");
		request.setParameter("fieldMinute", "20");
		request.setParameter("fieldHalf", "PM");

		v = new Validator(request);
		v.addValidation("field", Validator.IS_DATE_TIME);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}

	public void testIsDateTimeFailureDueToWrongFormat() {
		request.setParameter("field", "not a date");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_DATE_TIME);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testIsDateTimeFailureDueToInvalidTime() {
		request.setParameter("fieldDate", "10/20/2004");
		request.setParameter("fieldHour", "34");
		request.setParameter("fieldMinute", "56");
		request.setParameter("fieldHalf", "AM");

		v = new Validator(request);
		v.addValidation("field", Validator.IS_DATE_TIME);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}

	public void testIsDateSuccess() {
		request.setParameter("field", "10/20/2004");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_DATE);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testIsDateFailureDueToWrongFormat() {
		request.setParameter("field", "not a date");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_DATE);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testIsDateFailureDueToInvalidDateWeird() {
		request.setParameter("field", "30/50/2002");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_DATE);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}

	public void testIsDateFailureDueToInvalidDateLeapYear() {
		request.setParameter("field", "2/29/2001");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_DATE);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}

	public void testCheckSameSuccess() {
		request.setParameter("field1", "value");
		request.setParameter("field2", "value");
		v = new Validator(request);
		v.addValidation("field1", Validator.CHECK_SAME, "field2");
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testCheckSameFailure() {
		request.setParameter("field1", "value");
		request.setParameter("field2", "not the same value");
		v = new Validator(request);
		v.addValidation("field", Validator.CHECK_SAME, "field2");
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testIsEmailSuccess() {
		request.setParameter("field", "ssachs@e-guana.net");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_EMAIL);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testIsEmailFailure() {
		request.setParameter("field", "not an email");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_EMAIL);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testLengthNumericComparisonSuccess() {
		request.setParameter("field", "12345");
		v = new Validator(request);
		v.addValidation("field", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.GREATER_THAN, 4);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testLengthNumericComparisonFailure() {
		request.setParameter("field", "123");
		v = new Validator(request);
		v.addValidation("field", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.GREATER_THAN, 4);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testEntityExistsSuccess() {
		request.setParameter("field", "1");
		v = new Validator(request);
		v.addValidation("field", Validator.ENTITY_EXISTS, uadao);
		HashMap h = v.validate();
		assertTrue(!h.containsKey("field"));
	}
	
	public void testEntityExistsFailure() {
		request.setParameter("field", "0");
		v = new Validator(request);
		v.addValidation("field", Validator.ENTITY_EXISTS, uadao);
		HashMap h = v.validate();
		assertTrue(h.containsKey("field"));
	}
	
	public void testUsernameUniqueSuccess() {
		request.setParameter("field", "unused username");
		v = new Validator(request);
		v.addValidation("field", Validator.USERNAME_UNIQUE, uadao);
		HashMap h = v.validate();
		assertTrue(!h.containsKey("field"));		
	}

	public void testUsernameUniqueFailure() {
		request.setParameter("field", "user1");
		v = new Validator(request);
		v.addValidation("field", Validator.USERNAME_UNIQUE, uadao);
		HashMap h = v.validate();
		assertTrue(h.containsKey("field"));		
	}
	
	public void testIsIntegerSuccess() {
		request.setParameter("field", "5");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_AN_INTEGER);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	// the validator should tell us that "-1" is an integer
	public void testIsIntegerSuccessNegative() {
		request.setParameter("field", "-1");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_AN_INTEGER);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testIsIntegerFailure() {
		request.setParameter("field", "not an integer");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_AN_INTEGER);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	// TODO: TEST IS_A_FILE, IS_OF_FILE_TYPE
	
	public void testIsInSetSuccess() {
		request.setParameter("field", "set member");
		v = new Validator(request);
		ArrayList set = new ArrayList();
		set.add("set member");
		v.addValidation("field", Validator.IS_IN_SET, set);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testIsInSetFailure() {
		request.setParameter("field", "not a set member");
		v = new Validator(request);
		ArrayList set = new ArrayList();
		set.add("set member");
		v.addValidation("field", Validator.IS_IN_SET, set);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testIsPasswordSuccess() {
		request.setParameter("field", "validpassword");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_PASSWORD);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testIsPasswordFailure() {
		request.setParameter("field", "123");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_PASSWORD);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testIsValidTermSuccess() {
		request.setParameter("field", String.valueOf(Status.AVAILABLE.getId()));
		v = new Validator(request);
		v.addValidation("field", Validator.IS_VALID_TERM, TermType.STATUS);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testIsValidTermFailure() {
		request.setParameter("field", "-1");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_VALID_TERM, TermType.STATUS);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
	public void testComparesToStaticValueSuccess() {
		request.setParameter("field", "5");
		v = new Validator(request);
		v.addValidation("field", Validator.COMPARES_TO_STATIC_VALUE, NumericComparisonOperator.EQUALS, 5);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);
	}
	
	public void testComparesToStaticValueFailure() {
		request.setParameter("field", "6");
		v = new Validator(request);
		v.addValidation("field", Validator.COMPARES_TO_STATIC_VALUE, NumericComparisonOperator.EQUALS, 5);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
	
//	public void testDateIsAfterSuccess() {
//		request.setAttribute("field", "2001-1-1");
//		request.setAttribute("field2", "2000-1-1");
//		v = new Validator(request);
//		v.addValidation("field", Validator.DATE_IS_AFTER_OR_EQUAL, "field2");
//		HashMap h = v.validate();
//		assertTrue(!h.containsKey("field"));
//	}
//
//	public void testDateIsAfterSuccessBecauseOfEqual() {
//		request.setAttribute("field", "2001-1-1");
//		request.setAttribute("field2", "2001-1-1");
//		v = new Validator(request);
//		v.addValidation("field", Validator.DATE_IS_AFTER_OR_EQUAL, "field2");
//		HashMap h = v.validate();
//		assertTrue(!h.containsKey("field"));
//	}
//
//	public void testDateIsAfterFailure() {
//		request.setAttribute("field", "2000-1-1");
//		request.setAttribute("field2", "2001-1-1");
//		v = new Validator(request);
//		v.addValidation("field", Validator.DATE_IS_AFTER_OR_EQUAL, "field2");
//		HashMap h = v.validate();
//		assertTrue(h.containsKey("field"));
//	}

	// TODO: test entity exists in study
	
	public void testAddErrorStatic() {
		String error = "you have an error!";
		ArrayList messages = new ArrayList();
		HashMap inputMessages = new HashMap();
		inputMessages.put("field", messages);
		Validator.addError(inputMessages, "field", error);
		
		assertTrue(inputMessages.containsKey("field"));
		
		messages = (ArrayList) inputMessages.get("field");
		assertNotNull(messages);
		assertEquals(messages.size(), 1);
		
		String error2 = (String) messages.get(0);
		assertEquals(error2, error);
	}
	
	public void testIsPhoneSuccess() {
		request.setParameter("field", "123-456-7890");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_PHONE_NUMBER);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);		
	}

	public void testIsPhoneFailure() {
		request.setParameter("field", "123-456-78901");
		v = new Validator(request);
		v.addValidation("field", Validator.IS_A_PHONE_NUMBER);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);		
	}
	
	public void testFunctionProcessGTSuccess() {
	  Validation v = null;
	  NumericComparisonOperator nco = null;
	  int greaterThan = 2;
	  
	  try {
	    v = Validator.processCRFValidationFunction("func: gt(" + greaterThan + ")");
	    nco = (NumericComparisonOperator) v.getArg(0);
	  }
	  catch (Exception e) { }
	  
	  assertNotNull("valid object returned", v);
	  assertTrue("correct validation type", v.getType() == Validator.COMPARES_TO_STATIC_VALUE);
	  assertNotNull("nco not null", nco);
	  assertTrue("nco is GT operator", nco.equals(NumericComparisonOperator.GREATER_THAN));
	  assertTrue("correct argument", v.getFloat(1) == greaterThan);
	}
	
	public void testFunctionProcessGTFailureNotNumber() {
	  boolean passedTest = true;
	  Validation v = null;

	  try {
	    v = Validator.processCRFValidationFunction("func: gt(someString)");
	  }
	  catch (Exception e) { passedTest = true; }
	  assertTrue("caused exception", passedTest);
	}
	
	public void testFunctionProcessGTFailureBadNumArgs() {
	  boolean passedTest = true;
	  Validation v = null;

	  try {
	    v = Validator.processCRFValidationFunction("func: gt(1,2)");
	  }
	  catch (Exception e) { passedTest = true; }
	  assertTrue("caused exception", passedTest);
	}
	
	public void testRegexSuccess() {
		request.setParameter("field", "abcdef");
		v = new Validator(request);
		Validation re = null;
		try {
			re = Validator.processCRFValidationRegex("regex: /abc.*/");
		} catch (Exception e) { }
		
		assertNotNull("processed correctly", re);
		v.addValidation("field", re);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), false);		
	}
	
	public void testRegexFailure() {
		request.setParameter("field", "abcde");
		v = new Validator(request);
		Validation re = null;
		try {
			re = Validator.processCRFValidationRegex("regex: /^.{3}$/");
		} catch (Exception e) { }
		
		assertNotNull("processed correctly", re);

		v.addValidation("field", re);
		HashMap h = v.validate();
		assertEquals(h.containsKey("field"), true);
	}
}
