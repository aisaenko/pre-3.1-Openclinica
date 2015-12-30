package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.bean.extract.DownloadDiscrepancyNote;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.managestudy.DiscrepancyNoteThread;
import org.akaza.openclinica.control.managestudy.DiscrepancyNoteUtil;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.exception.InsufficientPermissionException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A servlet that sends via HTTP a file containing Discrepancy-Note related data.
 * @author Bruce W. Perry
 * @see ChooseDownloadFormat
 * @see org.akaza.openclinica.bean.extract.DownloadDiscrepancyNote
 */
public class DiscrepancyNoteOutputServlet extends SecureController {
    // These are the headers that must appear in the HTTP response, when sending a
    // file back to the user
    public static String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    public static String CONTENT_DISPOSITION_VALUE = "attachment; filename=";

    /* Handle the HTTP Get or Post request. */
    @Override
    protected void processRequest() throws Exception {

        FormProcessor fp = new FormProcessor(request);
        // the fileName contains any subject id and study unique protocol id;
        // see: chooseDownloadFormat.jsp
        String fileName = request.getParameter("fileName");
        // replace any spaces in the study's unique protocol id, so that
        // the filename is formulated correctly
        if (fileName != null) {
            fileName = fileName.replaceAll(" ", "_");
        }
        fileName = fileName == null ? "" : fileName;
        // the format will be either csv (comma separated values) or pdf (portable document format)
        String format = request.getParameter("fmt");
        String studyIdentifier = request.getParameter("studyIdentifier");
        // Determine whether to limit the displayed DN's to a certain resolutionStatus
        // CHANGED TO LIST OF RESOLUTION STATUS IDS
        /*int resolutionStatus = 0;
        try {
            resolutionStatus = Integer.parseInt(request.getParameter("resolutionStatus"));
        } catch(NumberFormatException nfe){
            //Show all DN's
            resolutionStatus=-1;
        }*/
        // possibly for a later implementation: int definitionId = fp.getInt("defId");
        int subjectId = fp.getInt("subjectId");
        int discNoteType = fp.getInt("discNoteType");

        DiscrepancyNoteUtil discNoteUtil = new DiscrepancyNoteUtil();

        DownloadDiscrepancyNote downLoader = new DownloadDiscrepancyNote();
        if ("csv".equalsIgnoreCase(format)) {
            fileName = fileName + ".csv";
            response.setContentType(DownloadDiscrepancyNote.CSV);
        } else {
            response.setContentType(DownloadDiscrepancyNote.PDF);
            fileName = fileName + ".pdf";
        }
        response.addHeader(CONTENT_DISPOSITION_HEADER, CONTENT_DISPOSITION_VALUE + fileName);
        // Are we downloading a List of discrepancy notes or just one?
        // Not needed now: boolean isList = ("y".equalsIgnoreCase(isAList));
        StudyBean studyBean = (StudyBean) session.getAttribute("study");

        Set<Integer> resolutionStatusIds = (HashSet) session.getAttribute("resolutionStatus");

        // It will also change any resolution status IDs among parents of children that have a different
        // id value (last boolean parameter; 'true' to perform the latter task)
        // In this case we want to include all the discrepancy notes, despite the res status or
        // type filtering, because we don't want to filter out parents, thus leaving out a child note
        // that might match the desired res status
        List<DiscrepancyNoteBean> allDiscNotes = discNoteUtil.getThreadedDNotesForStudy(studyBean, new HashSet<Integer>(), sm.getDataSource(), 0, false);
        // filter for specific subjects
        if (subjectId > 0) {
            allDiscNotes = discNoteUtil.filterDiscNotesBySubject(allDiscNotes, subjectId, sm.getDataSource());
        }

        // Now we have to package all the discrepancy notes in DiscrepancyNoteThread objects
        // Do the filtering for type or status here
        List<DiscrepancyNoteThread> discrepancyNoteThreads =
            discNoteUtil.createThreadsOfParents(
              allDiscNotes, sm.getDataSource(), studyBean, resolutionStatusIds, discNoteType,false);

        if ("csv".equalsIgnoreCase(format)) {
            /*response.setContentLength(
              downLoader.getListContentLength(allDiscNotes,DownloadDiscrepancyNote.CSV));*/
            //3014: this has been changed to only show the parent of the thread; then changed back again!
            int contentLen = downLoader.getThreadListContentLength(discrepancyNoteThreads);
            response.setContentLength(contentLen);

            /*downLoader.downLoadDiscBeans(allDiscNotes,
              DownloadDiscrepancyNote.CSV,response.getOutputStream(), null);*/
            downLoader.downLoadThreadedDiscBeans(discrepancyNoteThreads, DownloadDiscrepancyNote.CSV, response.getOutputStream(), null);
        } else {
            response.setHeader("Pragma", "public");
            /*downLoader.downLoadDiscBeans(allDiscNotes,
              DownloadDiscrepancyNote.PDF,
              response.getOutputStream(), studyIdentifier);*/
            downLoader.downLoadThreadedDiscBeans(discrepancyNoteThreads, DownloadDiscrepancyNote.PDF, response.getOutputStream(), studyIdentifier);
        }

    }

    @Override
    protected void mayProceed() throws InsufficientPermissionException {

    }
}
