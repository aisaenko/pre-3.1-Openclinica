package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.bean.extract.DownloadDiscrepancyNote;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.managestudy.DiscrepancyNoteUtil;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.core.form.FormProcessor;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * A servlet that sends via HTTP a file containing Discrepancy-Note related data.
 * @author Bruce W. Perry
 * @see ChooseDownloadFormat
 * @see org.akaza.openclinica.bean.extract.DownloadDiscrepancyNote
 */
public class DiscrepancyNoteOutputServlet extends SecureController {
    //These are the headers that must appear in the HTTP response, when sending a
    //file back to the user
    public static String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    public static String CONTENT_DISPOSITION_VALUE = "attachment; filename=";

    /* Handle the HTTP Get or Post request. */
    protected void processRequest() throws Exception {

        FormProcessor fp = new FormProcessor(request);
        //the fileName contains any subject id and study unique protocol id;
        //see: chooseDownloadFormat.jsp
        String fileName = request.getParameter("fileName");
        //replace any spaces in the study's unique protocol id, so that
        //the filename is formulated correctly
        if(fileName != null){
            fileName = fileName.replaceAll(" ","_");
        }
        fileName = (fileName == null ? "" : fileName);
        //the format will be either csv (comma separated values) or pdf (portable document format)
        String format = request.getParameter("fmt");
        String studyIdentifier = request.getParameter("studyIdentifier");
        //Determine whether to limit the displayed DN's to a certain resolutionStatus
        //CHANGED TO LIST OF RESOLUTION STATUS IDS
        /*int resolutionStatus = 0;
        try {
            resolutionStatus = Integer.parseInt(request.getParameter("resolutionStatus"));
        } catch(NumberFormatException nfe){
            //Show all DN's
            resolutionStatus=-1;
        }*/
       //possibly for a later implementation:  int definitionId = fp.getInt("defId");
        int subjectId = fp.getInt("subjectId");
        int discNoteType = fp.getInt("discNoteType");

        DiscrepancyNoteUtil discNoteUtil = new DiscrepancyNoteUtil();

        DownloadDiscrepancyNote downLoader = new DownloadDiscrepancyNote();
        if("csv".equalsIgnoreCase(format)) {
            fileName = fileName + ".csv";
            response.setContentType(DownloadDiscrepancyNote.CSV);
        } else {
            response.setContentType(DownloadDiscrepancyNote.PDF);
            fileName = fileName + ".pdf";
        }
        response.addHeader(CONTENT_DISPOSITION_HEADER,
          CONTENT_DISPOSITION_VALUE+fileName);
        // Are we downloading a List of discrepancy notes or just one?
        //Not needed now: boolean isList = ("y".equalsIgnoreCase(isAList));
         StudyBean studyBean = (StudyBean) session.getAttribute("study");

        Set<Integer> resolutionStatusIds = (HashSet) session.getAttribute("resolutionStatus");
        //This method will optionally filter for resolution status and disc note type (last parameter)
        List<DiscrepancyNoteBean> allDiscNotes = discNoteUtil.getDNotesForStudy(studyBean,
          resolutionStatusIds,sm.getDataSource(), discNoteType);
        //filter for specific subjects
        if(subjectId > 0)  {
        allDiscNotes = discNoteUtil.filterDiscNotesBySubject(allDiscNotes,subjectId,
          sm.getDataSource()); }

        //Filter notes by eventCRFId?

        if("csv".equalsIgnoreCase(format)) {
            response.setContentLength(
              downLoader.getListContentLength(allDiscNotes,DownloadDiscrepancyNote.CSV));

            downLoader.downLoadDiscBeans(allDiscNotes,
              DownloadDiscrepancyNote.CSV,response.getOutputStream(), null);
        } else {
            downLoader.downLoadDiscBeans(allDiscNotes,
              DownloadDiscrepancyNote.PDF,
              response.getOutputStream(), studyIdentifier);
        }



    }

    protected void mayProceed() throws InsufficientPermissionException {

    }
}
