package org.akaza.openclinica.bean.rule;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class FileUploadHelper {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    public List<File> returnFiles(HttpServletRequest request, ServletContext context) {

        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        return isMultipart ? getFiles(request, context, null) : new ArrayList<File>();
    }

    public List<File> returnFiles(HttpServletRequest request, ServletContext context, String dirToSaveUploadedFileIn) {

        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        return isMultipart ? getFiles(request, context, createDirectoryIfDoesntExist(dirToSaveUploadedFileIn)) : new ArrayList<File>();
    }

    @SuppressWarnings("unchecked")
    private List<File> getFiles(HttpServletRequest request, ServletContext context, String dirToSaveUploadedFileIn) {
        List<File> files = new ArrayList<File>();

        // FileCleaningTracker fileCleaningTracker =
        // FileCleanerCleanup.getFileCleaningTracker(context);

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            // Parse the request
            List<FileItem> items = upload.parseRequest(request);
            // Process the uploaded items

            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = iter.next();

                if (item.isFormField()) {
                    // DO NOTHING , THIS SHOULD NOT BE Handled here
                } else {
                    files.add(processUploadedFile(item, dirToSaveUploadedFileIn));
                }
            }
            return files;
        } catch (FileUploadException fue) {
            throw new OpenClinicaSystemException(fue.getMessage(), fue.getCause());
        } catch (Exception e) {
            throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
        }
    }

    private File processUploadedFile(FileItem item, String dirToSaveUploadedFileIn) throws Exception {
        dirToSaveUploadedFileIn = dirToSaveUploadedFileIn == null ? System.getProperty("java.io.tmpdir") : dirToSaveUploadedFileIn;
        File uploadedFile = new File(dirToSaveUploadedFileIn + File.separator + item.getName());
        item.write(uploadedFile);
        return uploadedFile;

    }

    private String createDirectoryIfDoesntExist(String theDir) {
        if (!new File(theDir).isDirectory()) {
            new File(theDir).mkdirs();
        }
        return new File(theDir).toString();
    }

}
