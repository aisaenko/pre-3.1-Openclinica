/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.DatasetRow;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author thickerson
 * 
 *  
 */
public class EditDatasetServlet extends SecureController {

  public static String getLink(int dsId) {
    return "EditDataset?dsId=" + dsId;
  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    String action = request.getParameter("action");
    if ("validate".equalsIgnoreCase(action)) {
      //check name, description, status for right now
      Validator v = new Validator(request);

      v.addValidation("dsName", Validator.NO_BLANKS);
      v.addValidation("dsDesc", Validator.NO_BLANKS);
      v.addValidation("dsStatus", Validator.IS_VALID_TERM, TermType.STATUS);

      v.addValidation("dsName", Validator.LENGTH_NUMERIC_COMPARISON,
          NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
      v.addValidation("dsDesc", Validator.LENGTH_NUMERIC_COMPARISON,
          NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
      //what else to validate?

      HashMap errors = v.validate();

      if (!StringUtil.isBlank(fp.getString("dsName"))) {
        //logger.info("dsName" + fp.getString("dsName"));
        DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
        DatasetBean dsBean = (DatasetBean) dsdao.findByNameAndStudy(fp.getString("dsName").trim(),
            currentStudy);
        if (dsBean.getId() > 0 && (dsBean.getId() != fp.getInt("dsId"))) {
          Validator.addError(errors, "dsName",
              "This Dataset name has been used by another Dataset, please choose a unique name.");
        }
      }
      
      if (!errors.isEmpty()) {
        String fieldNames[] = { "dsName", "dsDesc" };
        fp.setCurrentStringValuesAsPreset(fieldNames);
        fp.addPresetValue("dsStatus", fp.getInt("dsStatus"));

        addPageMessage("There were some errors in your update." + "  See below for details.");
        setInputMessages(errors);
        setPresetValues(fp.getPresetValues());

        //TODO determine if this is necessary
        int dsId = fp.getInt("dsId");
        DatasetDAO dsDAO = new DatasetDAO(sm.getDataSource());

        DatasetBean showDataset = (DatasetBean) dsDAO.findByPK(dsId);
        request.setAttribute("dataset", showDataset);
        //maybe just set the above to the session?

        request.setAttribute("statuses", getStatuses());
        forwardPage(Page.EDIT_DATASET);
      } else {
        int dsId = fp.getInt("dsId");
        DatasetDAO dsDAO = new DatasetDAO(sm.getDataSource());
        DatasetBean dataset = (DatasetBean) dsDAO.findByPK(dsId);
        dataset.setName(fp.getString("dsName"));
        dataset.setDescription(fp.getString("dsDesc"));
        dataset.setStatus(Status.get(fp.getInt("dsStatus")));
        dataset.setUpdater(ub);
        //dataset.setUpdaterId(ub.getId());
        dsDAO.update(dataset);
        addPageMessage("Dataset Properties have been updated.");
        //forward to view all datasets
        ArrayList datasets = (ArrayList) dsDAO.findAllByStudyId(currentStudy.getId());
        //changed from findAll()
        EntityBeanTable table = fp.getEntityBeanTable();
        ArrayList datasetRows = DatasetRow.generateRowsFromBeans(datasets);

        String[] columns = { "Dataset Name", "Description", "Created By", "Created Date", "Status",
            "Actions" };
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.hideColumnLink(5);
        table.setQuery("ViewDatasets", new HashMap());
        table.setRows(datasetRows);
        table.computeDisplay();

        request.setAttribute("table", table);
        //request.setAttribute("datasets", datasets);
        forwardPage(Page.VIEW_DATASETS);

      }
    } else {
      int dsId = fp.getInt("dsId");
      DatasetDAO dsDAO = new DatasetDAO(sm.getDataSource());
      DatasetBean showDs = (DatasetBean) dsDAO.findByPK(dsId);
      request.setAttribute("dataset", showDs);
      request.setAttribute("statuses", getStatuses());
      forwardPage(Page.EDIT_DATASET);
    }
  }

  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    //TODO add a limit so that the owner can edit, no one else?
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)
        || currentRole.getRole().equals(Role.INVESTIGATOR)) {
      return;
    }

    addPageMessage("You don't have the correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU,
        "not allowed to access extract data servlet", "1");

  }

  private ArrayList getStatuses() {
    Status statusesArray[] = { Status.AVAILABLE, Status.PENDING, Status.PRIVATE, Status.UNAVAILABLE };
    List statuses = Arrays.asList(statusesArray);
    return new ArrayList(statuses);
  }

}