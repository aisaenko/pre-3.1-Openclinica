/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.ws;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.EventServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.util.xml.DomUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.XPathParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

/**
 * @author Krikor Krumlian
 * 
 */
@Endpoint
public class EventEndpoint {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final String NAMESPACE_URI_V1 = "http://openclinica.org/ws/event/v1";
    private String dateFormat;

    private final EventServiceInterface eventService;
    private final DataSource dataSource;

    /**
     * Constructor
     * 
     * @param subjectService
     * @param cctsService
     */
    public EventEndpoint(EventServiceInterface eventService, DataSource dataSource) {
        this.eventService = eventService;
        this.dataSource = dataSource;
    }

    /**
     * if NAMESPACE_URI_V1:scheduleRequest execute this method
     * 
     */
    @PayloadRoot(localPart = "scheduleRequest", namespace = NAMESPACE_URI_V1)
    public Source createSubject(@XPathParam("//e:subject") NodeList subject, @XPathParam("//e:study/@uniqueIdentifier") String studyIdentifier,
            @XPathParam("//e:eventDefinitionOID") String eventDefinitionOID, @XPathParam("//e:location") String location,
            @XPathParam("//e:startDate") String startDate, @XPathParam("//e:startTime") String startTime, @XPathParam("//e:endDate") String endDate,
            @XPathParam("//e:endTime") String endTime) throws Exception {
        ResourceBundleProvider.updateLocale(new Locale("en_US"));
        Element subjectElement = (Element) subject.item(0);
        String studySubjectId = unMarshalltoSubjectId(subjectElement);

        Integer studyEventId =
            eventService.validateAndSchedule(studySubjectId, studyIdentifier, eventDefinitionOID, location, getDate(startDate, startTime), getDate(endDate,
                    endTime), getUserAccount());
        return new DOMSource(mapConfirmation(String.valueOf(studyEventId)));
    }

    private String unMarshalltoSubjectId(Element subjectElement) throws ParseException {

        Element studySubjectIdElement = DomUtils.getChildElementByTagName(subjectElement, "studySubjectId");
        String studySubjectIdValue = DomUtils.getTextValue(studySubjectIdElement);

        return studySubjectIdValue;
    }

    /**
     * Create Response
     * 
     * @param confirmation
     * @return
     * @throws Exception
     */
    private Element mapConfirmation(String confirmation) throws Exception {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        Element responseElement = document.createElementNS(NAMESPACE_URI_V1, "scheduleResponse");
        Element resultElement = document.createElementNS(NAMESPACE_URI_V1, "result");
        resultElement.setTextContent(confirmation);
        responseElement.appendChild(resultElement);
        return responseElement;

    }

    /**
     * Helper Method to resolve dates
     * 
     * @param dateAsString
     * @return
     * @throws ParseException
     */
    private Date getDate(String dateAsString, String hourMinuteAsString) throws ParseException {
        Date d = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateAsString += hourMinuteAsString == null ? " 00:00" : " " + hourMinuteAsString;
        d = sdf.parse(dateAsString);
        if (!sdf.format(d).equals(dateAsString)) {
            throw new OpenClinicaSystemException("Date not parseable");
        }
        return sdf.parse(dateAsString);
    }

    /**
     * Helper Method to get the user account
     * 
     * @return UserAccountBean
     */
    private UserAccountBean getUserAccount() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
        return (UserAccountBean) userAccountDao.findByUserName(username);
    }

    /**
     * @return
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

}