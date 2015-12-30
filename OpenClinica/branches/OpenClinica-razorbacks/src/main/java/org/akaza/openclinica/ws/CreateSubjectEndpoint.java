/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2009 Akaza Research 
 */
package org.akaza.openclinica.ws;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.service.subject.SubjectServiceInterface;
import org.akaza.openclinica.ws.logic.CctsService;
import org.springframework.util.xml.DomUtils;
import org.springframework.ws.server.endpoint.AbstractDomPayloadEndpoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateSubjectEndpoint extends AbstractDomPayloadEndpoint {

    private static final String NAMESPACE_URI = "http://openclinica.org/create-subject";
    private String dateFormat;

    private final SubjectServiceInterface subjectService;
    private final CctsService cctsService;

    public CreateSubjectEndpoint(SubjectServiceInterface subjectService, CctsService cctsService) {
        this.subjectService = subjectService;
        this.cctsService = cctsService;
    }

    @Override
    protected Element invokeInternal(Element requestElement, Document document) throws Exception {
        SubjectTransferBean subjectTransferBean = parseIncomingXml(requestElement);
        // subjectService.validate(subjectTransferBean);
        // commit(subjectTransferBean);

        Element subjectElement = DomUtils.getChildElementByTagName(requestElement, "subject");
        Element studyElement = DomUtils.getChildElementByTagName(requestElement, "study");
        SubjectBean subject = mapSubject(subjectElement);
        StudyBean study = mapStudy(studyElement);
        String confirmation = subjectService.createSubject(subject, study, null);
        Element responseElement = document.createElementNS(NAMESPACE_URI, "createSubjectResponse");
        responseElement.appendChild(mapRewardConfirmation(document, confirmation));
        return responseElement;
    }

    private SubjectTransferBean parseIncomingXml(Element requestElement) throws ParseException {

        Element subjectElement = DomUtils.getChildElementByTagName(requestElement, "subject");
        Element studyElement = DomUtils.getChildElementByTagName(requestElement, "study");

        // Subject Elements
        Element personIdElement = DomUtils.getChildElementByTagName(subjectElement, "personId");
        Element studySubjectIdElement = DomUtils.getChildElementByTagName(subjectElement, "studySubjectId");
        Element genderElement = DomUtils.getChildElementByTagName(subjectElement, "gender");
        Element dateOfBirthElement = DomUtils.getChildElementByTagName(subjectElement, "dateOfBirth");

        // Subject Values
        String personIdValue = DomUtils.getTextValue(personIdElement);
        String studySubjectIdValue = DomUtils.getTextValue(studySubjectIdElement);
        String genderValue = DomUtils.getTextValue(genderElement);
        String dateOfBirthValue = DomUtils.getTextValue(dateOfBirthElement);

        // Study Values
        String studyOid = studyElement.getAttribute("oid");

        SubjectTransferBean subjectTransferBean =
            new SubjectTransferBean(personIdValue, studySubjectIdValue, getDate(dateOfBirthValue), genderValue.toCharArray()[0], studyOid);
        return subjectTransferBean;
    }

    private Date getDate(String dateAsString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
        return sdf.parse(dateAsString);
    }

    private SubjectBean mapSubject(Element subjectElement) throws ParseException {
        Element personIdElement = DomUtils.getChildElementByTagName(subjectElement, "personId");
        Element studySubjectIdElement = DomUtils.getChildElementByTagName(subjectElement, "studySubjectId");
        Element genderElement = DomUtils.getChildElementByTagName(subjectElement, "gender");
        Element dateOfBirthElement = DomUtils.getChildElementByTagName(subjectElement, "dateOfBirth");

        String personIdValue = DomUtils.getTextValue(personIdElement);
        String studySubjectIdValue = DomUtils.getTextValue(studySubjectIdElement);
        String genderValue = DomUtils.getTextValue(genderElement);
        String dateOfBirthValue = DomUtils.getTextValue(dateOfBirthElement);

        SubjectBean subjectBean = new SubjectBean();
        subjectBean.setUniqueIdentifier(personIdValue);
        // TODO: try and catch for parse exceptions
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        subjectBean.setDateOfBirth(sdf.parse(dateOfBirthValue));
        subjectBean.setGender(genderValue.toCharArray()[0]);
        subjectBean.setLabel(studySubjectIdValue);
        return subjectBean;
    }

    private StudyBean mapStudy(Element studyElement) {
        String identifier = studyElement.getAttribute("oid");
        StudyBean studyBean = new StudyBean();
        studyBean.setOid(identifier);
        return studyBean;
    }

    private Element mapRewardConfirmation(Document document, String confirmation) {
        Element confirmationElement = document.createElementNS(NAMESPACE_URI, "createSubjectConfirmation");
        confirmationElement.setAttribute("success", confirmation);
        return confirmationElement;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

}