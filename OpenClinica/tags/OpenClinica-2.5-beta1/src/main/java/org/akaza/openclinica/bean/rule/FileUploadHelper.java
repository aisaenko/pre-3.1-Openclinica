package org.akaza.openclinica.bean.rule;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class FileUploadHelper {

    public List<File> returnFiles(HttpServletRequest request, ServletContext context) {

        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        return isMultipart ? getFiles(request, context) : new ArrayList<File>();
    }

    @SuppressWarnings("unchecked")
    public List<File> getFiles(HttpServletRequest request, ServletContext context) {
        List<File> files = new ArrayList<File>();

        // FileCleaningTracker fileCleaningTracker =
        // FileCleanerCleanup.getFileCleaningTracker(context);

        // Create a factory for disk-based file items
        FileItemFactory factory = new DiskFileItemFactory();

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
                    files.add(processUploadedFile(item));
                }
            }
            return files;
        } catch (FileUploadException fue) {
            throw new OpenClinicaSystemException(fue.getMessage(), fue.getCause());
        } catch (Exception e) {
            throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
        }
    }

    private File processUploadedFile(FileItem item) throws Exception {
        File uploadedFile = new File(item.getName());
        item.write(uploadedFile);
        return uploadedFile;

    }

}
