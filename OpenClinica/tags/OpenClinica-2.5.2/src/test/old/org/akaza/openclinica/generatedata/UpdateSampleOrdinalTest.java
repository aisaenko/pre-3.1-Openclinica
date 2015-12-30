/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.generatedata;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.DAOTestBase;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * @author jxu
 *
 * Sets up sample ordinal for study events
 */
public class UpdateSampleOrdinalTest extends DAOTestBase {
    private DAODigester digester = new DAODigester();

    private UserAccountDAO userdao;

    private SubjectDAO subjdao;

    private StudySubjectDAO ssubjdao;

    private StudyDAO sdao;

    private StudyEventDefinitionDAO defdao;

    private EventDefinitionCRFDAO edcdao;

    private CRFDAO crfdao;

    private StudyEventDAO sedao;

    private EventCRFDAO ecdao;

    private CRFVersionDAO cvdao;

    private ItemDAO itemdao;

    private ItemDataDAO idadao;

    private ItemFormMetadataDAO itemMetadao;

    public static final int NUM_STUDIES = 30;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(UpdateSampleOrdinalTest.class);
    }

    public UpdateSampleOrdinalTest(String name) throws Exception {
        super(name);
        super.ds = setupTestDataSource();

        userdao = new UserAccountDAO(super.ds, getDigester("useraccount_dao.xml"));
        defdao = new StudyEventDefinitionDAO(super.ds, getDigester("studyeventdefinition_dao.xml"));
        edcdao = new EventDefinitionCRFDAO(super.ds, getDigester("event_definition_crf_dao.xml"));
        sdao = new StudyDAO(super.ds, getDigester("study_dao.xml"));
        sedao = new StudyEventDAO(super.ds, getDigester("study_event_dao.xml"));
        ecdao = new EventCRFDAO(super.ds, getDigester("eventcrf_dao.xml"));
        cvdao = new CRFVersionDAO(super.ds, getDigester("crfversion_dao.xml"));
        itemdao = new ItemDAO(super.ds, getDigester("item_dao.xml"));
        itemMetadao = new ItemFormMetadataDAO(super.ds, getDigester("item_form_metadata_dao.xml"));
        idadao = new ItemDataDAO(super.ds, getDigester("itemdata_dao.xml"));
        ssubjdao = new StudySubjectDAO(super.ds, getDigester("study_subject_dao.xml"));
    }

    private DAODigester getDigester(String filename) throws Exception {
        DAODigester dig = new DAODigester();
        dig.setInputStream(new FileInputStream(SQL_DIR + filename));

        try {
            dig.run();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }

        return dig;
    }

    /**
     * Sets sample ordinal for study events in the DB The method will go over
     * all the studies in the database, find all the study event definitions and
     * study subjects in each study, for each definition, find all the study
     * events for each subject, then order those the events.
     *
     * for example, study A has two definitions
     * def1(repeating),def2(non-repeating) and two subjects ssub1, ssub2 ssub1
     * has 3 study events: def1-event1, def1-event2 and def2-event1 so the
     * correct ordinal for def1-event1, def1-event2 and def2-event1 will be
     * 1,2,1
     *
     * ssub2 has 3 study events: def1-event1, def1-event2 and def1-event3 so the
     * correct ordinal for def1-event1, def1-event2 and def1-event3 will be
     * 1,2,3
     *
     * @throws Exception
     */

    public void testUpdateSampleOrdinal() throws Exception {

        ArrayList studies = (ArrayList) sdao.findAll();

        for (int j = 0; j < studies.size(); j++) {
            // find all the definitions and study subjects
            StudyBean study = (StudyBean) studies.get(j);
            ArrayList defs = defdao.findAllByStudy(study);
            ArrayList studySubs = ssubjdao.findAllByStudy(study);
            for (int k = 0; k < defs.size(); k++) {
                StudyEventDefinitionBean def = (StudyEventDefinitionBean) defs.get(k);
                for (int i = 0; i < studySubs.size(); i++) {
                    StudySubjectBean sub = (StudySubjectBean) studySubs.get(i);
                    // find all the events using this definition
                    ArrayList events = sedao.findAllByDefinitionAndSubjectOrderByOrdinal(def, sub);
                    // set order for the events
                    for (int a = 0; a < events.size(); a++) {
                        StudyEventBean event = (StudyEventBean) events.get(a);
                        event.setSampleOrdinal(a + 1);
                        UserAccountBean updater = new UserAccountBean();
                        updater.setId(event.getUpdaterId());
                        event.setUpdater(updater);
                        sedao.update(event);

                    }
                }// i
            }// k
        }

    }

}
