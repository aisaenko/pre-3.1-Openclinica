/*
 * Created on Jun 9, 2005
 *
 *
 */
package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.ArchivedDatasetFileRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * <P>
 * purpose of this servlet is to respond with a file listing after we've
 * outlasted the 'please wait' message.
 *
 * @author thickerson
 *
 */
public class ShowFileServlet extends SecureController {

    Locale locale;

    // < ResourceBundlerestext,resword,respage,resexception;

    public static String getLink(int fId, int dId) {
        return "ShowFile?fileId=" + fId + "&datasetId=" + dId;
    }

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int fileId = fp.getInt("fileId");
        int dsId = fp.getInt("datasetId");
        DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
        DatasetBean db = (DatasetBean) dsdao.findByPK(dsId);

        ArchivedDatasetFileDAO asdfdao = new ArchivedDatasetFileDAO(sm.getDataSource());
        ArchivedDatasetFileBean asdfBean = (ArchivedDatasetFileBean) asdfdao.findByPK(fileId);

        ArrayList newFileList = new ArrayList();
        newFileList.add(asdfBean);
        // request.setAttribute("filelist",newFileList);

        ArrayList filterRows = ArchivedDatasetFileRow.generateRowsFromBeans(newFileList);
        EntityBeanTable table = fp.getEntityBeanTable();
        String[] columns =
            { resword.getString("file_name"), resword.getString("run_time"), resword.getString("file_size"), resword.getString("created_date"),
                resword.getString("created_by") };

        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.hideColumnLink(0);
        table.hideColumnLink(1);
        table.hideColumnLink(2);
        table.hideColumnLink(3);
        table.hideColumnLink(4);

        // table.setQuery("ExportDataset?datasetId=" +db.getId(), new
        // HashMap());
        // trying to continue...
        // session.setAttribute("newDataset",db);
        request.setAttribute("dataset", db);
        request.setAttribute("file", asdfBean);
        table.setRows(filterRows);
        table.computeDisplay();

        request.setAttribute("table", table);
        Page finalTarget = Page.EXPORT_DATA_CUSTOM;

        finalTarget.setFileName("/WEB-INF/jsp/extract/generateMetadataFile.jsp");

        forwardPage(finalTarget);
    }

}
