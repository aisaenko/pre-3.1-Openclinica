/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.core.form;

import junit.framework.TestCase;
import java.util.*;
import org.akaza.openclinica.core.form.*;
import org.akaza.openclinica.bean.core.*;
//import javax.servlet.http.*;

/**
 * @author ssachs
 */
public class FormProcessorTest extends TestCase {
	
	private FormProcessor fp;
	private HttpServletRequestImplementation request;

	
	public FormProcessorTest(String name) {
		super(name);
		request = new HttpServletRequestImplementation();
		fp = new FormProcessor(request);
	}
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(FormProcessorTest.class);
	}
	
	protected void setUp() throws Exception {
		request = new HttpServletRequestImplementation();
		fp = new FormProcessor(request);
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetString() {
		request.setParameter("field", "value");
		fp.setRequest(request);
		String val = fp.getString("field");
		assertEquals(val, "value");
	}

	public void testGetStringNotPresent() {
		fp.setRequest(request);
		String val = fp.getString("field");
		assertEquals(val, FormProcessor.DEFAULT_STRING);
	}
	
	public void testGetInt() {
		request.setParameter("field", "5");
		fp.setRequest(request);
		int val = fp.getInt("field");
		assertEquals(val, 5);
	}
	
	public void testGetIntNotPresent() {
		fp.setRequest(request);
		int val = fp.getInt("field");
		assertEquals(val, FormProcessor.DEFAULT_INT);
	}

	public void testGetIntMalformed() {
		request.setParameter("field", "not an int");
		fp.setRequest(request);
		int val = fp.getInt("field");
		assertEquals(val, FormProcessor.DEFAULT_INT);
	}
	
	public void testGetBooleanTrue() {
		request.setParameter("field", "1");
		fp.setRequest(request);
		boolean val = fp.getBoolean("field");
		assertEquals(val, true);
	}

	public void testGetBooleanFalse() {
		request.setParameter("field", "0");
		fp.setRequest(request);
		boolean val = fp.getBoolean("field");
		assertEquals(val, false);
	}
	
	public void testGetBooleanNotPresent() {
		fp.setRequest(request);
		boolean val = fp.getBoolean("field");
		assertEquals(val, FormProcessor.DEFAULT_BOOLEAN);
	}
	
	public void testGetFloat() {
		request.setParameter("field", "5.5");
		fp.setRequest(request);
		float val = fp.getFloat("field");

		assertTrue(val == (float) 5.5);
	}
	
	public void testGetFloatNotPresent() {
		fp.setRequest(request);
		float val = fp.getFloat("field");
		assertTrue(val == (float) 0.0);
	}

//	public void testGetEntity() {
//		request.setParameter("field", "9");
//		fp.setRequest(request);
//		StudyBean sb = (StudyBean) fp.getEntity("field", new StudyDAO(this.ds));
//		
//		assertNotNull(sb);
//		assertTrue(sb.isActive());
//		assertEquals(sb.getId(), 9);
//	}

	public void testGetDateTimeStaticSuccess() {
		Date d = FormProcessor.getDateTimeFromString("1/1/2000 11:20:55 pm");

		assertNotNull("date valid", d);
		assertEquals("correct date returned", d.toString(), "Sat Jan 01 23:20:55 EST 2000");		
	}

	public void testGetDateTimeStaticFailure() {
		Date d = FormProcessor.getDateTimeFromString("1/1/2000 32:20:55 pm");

		assertNotNull("date valid", d);
		assertTrue("default date returned", d.equals(FormProcessor.DEFAULT_DATE));		
	}

	public void testGetDateTimeSuccess() {
		request.setParameter("fieldDate", "1/1/2000");
		request.setParameter("fieldHour", "11");
		request.setParameter("fieldMinute", "20");
		request.setParameter("fieldHalf", "pm");
		fp.setRequest(request);
		
		Date d = fp.getDateTime("field");

		assertNotNull("date valid", d);
		assertEquals("correct date returned", d.toString(), "Sat Jan 01 23:20:00 EST 2000");
	}
	
	public void testGetDateTimeFailure() {
		request.setParameter("fieldDate", "1/1/2000");
		request.setParameter("fieldHour", "32");
		request.setParameter("fieldMinute", "20");
		request.setParameter("fieldHalf", "pm");
		fp.setRequest(request);
		
		Date d = fp.getDateTime("field");

		assertNotNull("date valid", d);
		assertTrue("default date returned", d.equals(FormProcessor.DEFAULT_DATE));		
	}
	
	public void testGetDateStatic() {
		Date d = FormProcessor.getDateFromString("1/1/2000");

		assertNotNull(d);
		assertEquals(d.toString(), "Sat Jan 01 00:00:00 EST 2000");		
	}
	
	public void testGetDatePresent() {
		request.setParameter("field", "10/26/1979"); // someday every schoolboy and girl will know this date...
		fp.setRequest(request);
		Date d = fp.getDate("field");

		assertNotNull(d);
		assertEquals(d.toString(), "Fri Oct 26 00:00:00 EDT 1979");
	}

	public void testGetDateNotPresent() {
		fp.setRequest(request);
		Date d = fp.getDate("field");
		assertNotNull(d);
		assertTrue(d.equals(FormProcessor.DEFAULT_DATE));
	}
	
	public void testIsSubmittedTrue() {
		request.setParameter(FormProcessor.FIELD_SUBMITTED, "1");
		fp.setRequest(request);
		boolean val = fp.isSubmitted();
		assertEquals(val, true);
	}
		
	public void testIsSubmittedFalse() {
		fp.setRequest(request);
		boolean val = fp.isSubmitted();
		assertEquals(val, false);
	}

	public void testAddPresetValueInt() {
		fp.addPresetValue("field", 1);
		HashMap values = fp.getPresetValues();
		Integer i = (Integer) values.get("field");
		assertNotNull(i);
		assertEquals(i.intValue(), 1);
	}
	
	public void testAddPresetValueBoolean() {
		fp.addPresetValue("field", true);
		HashMap values = fp.getPresetValues();
		Boolean b = (Boolean) values.get("field");
		assertNotNull(b);
		assertEquals(b.booleanValue(), true);
	}

	public void testAddPresetValueString() {
		fp.addPresetValue("field", "value");
		HashMap values = fp.getPresetValues();
		String s = (String) values.get("field");
		assertNotNull(s);
		assertEquals(s, "value");
	}
	
	public void testAddPresetValueFloat() {
		fp.addPresetValue("field", (float) 3.0);
		HashMap values = fp.getPresetValues();
		Float f = (Float) values.get("field");
		assertNotNull(f);
		assertTrue("correct value retrieved", f.floatValue() == 3.0);
	}

	public void testAddPresetValueEntity() {
		Term t = new Term(1, "test");
		fp.addPresetValue("field", t);
		HashMap values = fp.getPresetValues();
		EntityBean t2 = (EntityBean) values.get("field");
		assertNotNull(t2);
		assertEquals("correct bean returned", t2.getId(), 1);
	}

	public void testSetCurrentStringValuesAsPreset() {
		request.setParameter("field1", "value1");
		request.setParameter("field2", "value2");
		request.setParameter("field3", "value3");
		fp.setRequest(request);
		
		String fields[] = {"field1", "field2", "field3"};
		fp.setCurrentStringValuesAsPreset(fields);

		HashMap values = fp.getPresetValues();

		String s = (String) values.get("field1");
		assertNotNull(s);
		assertEquals("correct string returned - 1", s, "value1");

		s = (String) values.get("field2");
		assertNotNull(s);
		assertEquals("correct string returned - 2", s, "value2");
		
		s = (String) values.get("field3");
		assertNotNull(s);
		assertEquals("correct string returned - 3", s, "value3");
	}

	public void testSetCurrentIntValuesAsPreset() {
		request.setParameter("field1", "1");
		request.setParameter("field2", "2");
		request.setParameter("field3", "3");
		fp.setRequest(request);
		
		String fields[] = {"field1", "field2", "field3"};
		fp.setCurrentIntValuesAsPreset(fields);

		HashMap values = fp.getPresetValues();

		Integer i = (Integer) values.get("field1");
		assertNotNull(i);
		assertEquals("correct int returned - 1", i.intValue(), 1);

		i = (Integer) values.get("field2");
		assertNotNull(i);
		assertEquals("correct int returned - 2", i.intValue(), 2);
		
		i = (Integer) values.get("field3");
		assertNotNull(i);
		assertEquals("correct int returned - 3", i.intValue(), 3);
	}

	public void testSetCurrentBoolValuesAsPreset() {
		request.setParameter("field1", "1");
		request.setParameter("field2", "0");
		fp.setRequest(request);
		
		String fields[] = {"field1", "field2"};
		fp.setCurrentBoolValuesAsPreset(fields);

		HashMap values = fp.getPresetValues();

		Boolean b = (Boolean) values.get("field1");
		assertNotNull(b);
		assertTrue("correct bool returned - 1", b.booleanValue());

		b = (Boolean) values.get("field2");
		assertNotNull(b);
		assertTrue("correct bool returned - 2", !b.booleanValue());
	}
}