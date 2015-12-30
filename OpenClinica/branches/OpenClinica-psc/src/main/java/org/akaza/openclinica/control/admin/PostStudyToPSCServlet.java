/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.logging.Level;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;

public class PostStudyToPSCServlet extends SecureController {
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study")
                + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.SUBJECT_LIST_SERVLET, resexception
                .getString("not_admin"), "1");

    }

    // https://vera.bioinformatics.northwestern.edu/studycalendar/api/v1/studies/{study-identifier}/template
    protected void processRequest() throws Exception {

        // Prepare the request
        System.out.println("start");
        
        FormProcessor fp = new FormProcessor(request);
        int studyId = fp.getInt("studyId");
        
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        StudyBean study = (StudyBean)sdao.findByPK(studyId); 
        String studyName = study.getName();
        
        studyName = studyName.replace(" ","%20");
        System.out.println("studyName:" + studyName);
        
       // Request request = new Request(Method.GET, "http://localhost:8080/psc/api/v1/studies");
        Request request = new Request(Method.GET, "http://localhost:8080/psc/api/v1/studies/" + studyName + "/template");
        // Add the client authentication to the call
        ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
        ChallengeResponse authentication = new ChallengeResponse(scheme, "root", "12345678");
        request.setChallengeResponse(authentication);

        // Ask to the HTTP client connector to handle the call
        Client client = new Client(Protocol.HTTP);
        client.getContext().getParameters().add("converter",
                "com.noelios.restlet.http.HttpClientConverter");

        Response response = client.handle(request);
        boolean haveStudy = false;
        if (response.getStatus().isSuccess() || response.getStatus().equals(Status.REDIRECTION_SEE_OTHER)) {
            // Output the response entity on the JVM console
            System.out.println("yes, got it");
            response.getEntity().write(System.out);
            haveStudy = true;
            addPageMessage("This study is already in PSC.");
            forwardPage(Page.STUDY_LIST_SERVLET);
            return;
            
        } else if (response.getStatus().equals(Status.CLIENT_ERROR_UNAUTHORIZED)) {
            // Unauthorized access
            System.out.println("Access authorized by the server, " + "check your credentials");
        } else {
            // Unexpected status
            System.out.println("An unexpected status was returned: " + response.getStatus());
        }
        
        /*request = new Request(Method.GET, "http://localhost:8080/psc/api/v1/activities");        
        request.setChallengeResponse(authentication);
        
        response = client.handle(request);

        if (response.getStatus().isSuccess()) {
            // Output the response entity on the JVM console
            System.out.println("yes, got it again");
            response.getEntity().write(System.out);
        } else if (response.getStatus().equals(Status.CLIENT_ERROR_UNAUTHORIZED)) {
            // Unauthorized access
            System.out.println("Access authorized by the server, " + "check your credentials");
        } else {
            // Unexpected status
            System.out.println("An unexpected status was returned: " + response.getStatus());
        }*/
        if (!haveStudy){
         testSimplePost(study.getName());
         addPageMessage("Study is not in PSC yet, so export it to PSC.");
         forwardPage(Page.STUDY_LIST_SERVLET);
        }

    }

    public void testSimplePost(String studyName) throws Exception {
        // Prepare the REST call.
        //Request request = new Request();

        // Identify ourselves.
        //request.setReferrerRef("http://localhost:8080/OpenClinica-SNAPSHOT");

        // Target resource.
        //request.setResourceRef("http://localhost:8080/psc/api/v1/studies/Study1_from_OpenClinica/template");

        //Form form = new Form();
        //form.add("study-identifier", "Study1 from OpenClinica");

        //request.setEntity(form.getWebRepresentation());
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"); 
        sb.append("<study-snapshot assigned-identifier=\"" + studyName + "\" " +
     "xmlns=\"http://bioinformatics.northwestern.edu/ns/psc\">");
        sb.append("<planned-calendar>");
        sb.append("<epoch name=\"E1\"/>");        
        sb.append("</planned-calendar>");
        sb.append("</study-snapshot>");
        
        Representation sp = new StringRepresentation(sb.toString(),MediaType.TEXT_XML);
        
        
        //DomRepresentation sp = new DomRepresentation(MediaType.APPLICATION_XML);
        //Document xmldoc = new DocumentImpl();
        
        //sp.setDocument(xmldoc);
        //request.setEntity(sp);
        Request request = new Request(Method.POST, "http://localhost:8080/psc/api/v1/studies", sp);
        ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
        ChallengeResponse authentication = new ChallengeResponse(scheme, "root", "12345678");
        request.setChallengeResponse(authentication);

        System.out.println("client: one!");

        // Prepare HTTP client connector.
        Client client = new Client(Protocol.HTTP);
        client.getContext().getParameters().add("converter",
                "com.noelios.restlet.http.HttpClientConverter");

        client.getLogger().log(Level.INFO, "client: two!");

        // Make the call.
        Response response = client.handle(request);
        client.getLogger().log(Level.INFO, "client: three!");

        if (response.getStatus().isSuccess() || response.getStatus().equals(Status.REDIRECTION_SEE_OTHER)) {
            client.getLogger().log(Level.INFO, "client: four!");
            // Output the response entity on the JVM console
            response.getEntity().write(System.out);
            System.out.println("client: success!");
        } else {
            client.getLogger().log(Level.INFO, "client: failed!");
            System.out.println("failed" + response.getStatus() );
            System.out.println("failed" + response.getStatus().getDescription());

        }
    }
}
