/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.core.form;

import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.HashMap;

/**
 * @author ssachs
 *
 */
// we need to extend DAOTestBase so we can have a datasource
public class DiscrepancyValidatorTest extends DAOTestBase {
    private UserAccountDAO uadao;
    private DAODigester digester = new DAODigester();
    private DiscrepancyValidator v;
    private HttpServletRequestImplementation request;

    public DiscrepancyValidatorTest(String name) {
        super(name);
        request = new HttpServletRequestImplementation();
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ValidatorTest.class);
    }

    @Override
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

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNoNotesCausesValidation() {
        request.setParameter("field", "");
        FormDiscrepancyNotes notes = new FormDiscrepancyNotes();
        v = new DiscrepancyValidator(request, notes);
        v.addValidation("field", Validator.NO_BLANKS);
        HashMap h = v.validate();
        assertEquals("no notes yields validation", h.containsKey("field"), true);
    }

    public void testNotesCausesNoValidation() {
        request.setParameter("field", "");
        FormDiscrepancyNotes notes = new FormDiscrepancyNotes();
        notes.addNote("field", new DiscrepancyNoteBean());
        v = new DiscrepancyValidator(request, notes);
        v.addValidation("field", Validator.NO_BLANKS);
        HashMap h = v.validate();
        assertEquals("notes yields no validation", h.containsKey("field"), false);
    }

    public void testNoNotesAndValidFieldCausesNoError() {
        request.setParameter("field", "value");
        FormDiscrepancyNotes notes = new FormDiscrepancyNotes();
        v = new DiscrepancyValidator(request, notes);
        v.addValidation("field", Validator.NO_BLANKS);
        HashMap h = v.validate();
        assertEquals("no notes and valid field yields no error", h.containsKey("field"), false);
    }

}