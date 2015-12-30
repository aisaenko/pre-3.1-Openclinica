/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

import com.oreilly.servlet.MultipartRequest;

/**
 * Create a new CRF verison by uploading excel file
 * 
 * @author jxu
 */
public class CreateCRFVersionServlet extends SecureController {

    /**
     * 
     */
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
    }

    public void processRequest() throws Exception {

        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);

        setToPanel("Create CRF", "<br>Create a new CRF by entering a name and description.");

        setToPanel("Create CRF Version",
                "<br>Create a new CRF version by uploading an excel spreadsheet "
                        + "defining the CRF's data elements and layout.");
        setToPanel(
                "Revise CRF Version",
                "<br>If you are the owner of a CRF version, and the CRF version "
                        + "has not been used in a study, you can overwrite "
                        + "the CRF version by uploading a new excel spreadsheet with same version name. In this case, "
                        + "system will ask you whether you want to delete the "
                        + "previous contents and upload a new version.");
        setToPanel("CRF Spreadsheet <br>Template", "<br>Download a blank CRF Excel spreadsheet "
                        + "template <a href=\"DownloadVersionSpreadSheet?template=1\"><b>here</b></a>.");
        setToPanel(
                "Example CRF <br>Spreadsheets",
                "<br>Download example CRFs and instructions from the"
                        + " <a href=\"http://www.openclinica.org/entities/entity_details.php?eid=151\"><b>OpenClinica.org portal</b></a> "
                        + "(OpenClinica.org user account required).");

        CRFDAO cdao = new CRFDAO(sm.getDataSource());
        CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
        EventDefinitionCRFDAO edao = new EventDefinitionCRFDAO(sm.getDataSource());

        FormProcessor fp = new FormProcessor(request);
        String action = request.getParameter("action");
        CRFVersionBean version = (CRFVersionBean) session.getAttribute("version");

        logger.info("crf Id: " + version.getCrfId());

        if (StringUtil.isBlank(action)) {
            logger.info("action is blank");
            request.setAttribute("version", version);
            forwardPage(Page.CREATE_CRF_VERSION);
        } else {
            Validator v = new Validator(request);
            // logger.info("version name:" +fp.getString("name"));
            v.addValidation("name", Validator.NO_BLANKS);
            v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON,
                    NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

            version.setName(fp.getString("name"));
            if (version.getCrfId() == 0) {
                version.setCrfId(fp.getInt("crfId"));
            }
            session.setAttribute("version", version);
            errors = v.validate();
            if (!errors.isEmpty()) {
                logger.info("has validation errors ");
                request.setAttribute("formMessages", errors);
                forwardPage(Page.CREATE_CRF_VERSION);

            } else {

                if ("confirm".equalsIgnoreCase(action)) {
                    logger.info("action is confirm");
                    CRFBean crf = (CRFBean) cdao.findByPK(version.getCrfId());
                    ArrayList versions = (ArrayList) vdao.findAllByCRF(crf.getId());
                    for (int i = 0; i < versions.size(); i++) {
                        CRFVersionBean version1 = (CRFVersionBean) versions.get(i);
                        if (version.getName().equals(version1.getName())) {
                            // version already exists
                            logger.info("owner or not:" + ub.getId() + "," + version1.getOwnerId());
                            if (ub.getId() != version1.getOwnerId()) {// not owner

                addPageMessage("The CRF version you try to upload already exists in the DB and is created by "
                    + version1.getOwner().getName()
                    + ", please contact the owner to delete the previous version. "
                    + "Or you can change the version name and upload a different version for the CRF.");

                                forwardPage(Page.CREATE_CRF_VERSION);
                                return;

                            } else {// owner,
                                ArrayList definitions = (ArrayList) edao.findByDefaultVersion(version1.getId());
                                if (!definitions.isEmpty()) {// used in definition

                                    request.setAttribute("definitions", definitions);

                                    forwardPage(Page.REMOVE_CRF_VERSION_DEF);
                                    return;
                                } else {// not used in definition

                                    int previousVersionId = version1.getId();
                                    version.setId(previousVersionId);
                                    session.setAttribute("version", version);
                                    session.setAttribute("previousVersionId", new Integer(previousVersionId));

                                    forwardPage(Page.REMOVE_CRF_VERSION_CONFIRM);
                                    return;

                }
              }
            }
          }
          //didn't find same version in the DB,let user upload the excel file
          forwardPage(Page.UPLOAD_CRF_VERSION);
        } else if ("submit".equalsIgnoreCase(action)) {//submit
          logger.info("no same version, it's owner, no definition, can upload");
          String tempFile = "";
		try {
			tempFile = uploadFile(version);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warning("*** Found exception ***");
			e.printStackTrace();
			throw e;
		}
		session.setAttribute("tempFileName", tempFile);
          if (tempFile == null) {
            forwardPage(Page.UPLOAD_CRF_VERSION);
          } else {
            forwardPage(Page.CREATE_CRF_VERSION_CONFIRM);
          }

        } else if ("confirmsql".equalsIgnoreCase(action)) {
          logger.info("confirm sql");
          NewCRFBean nib = (NewCRFBean) session.getAttribute("nib");
          if (nib != null && nib.getItemQueries() != null) {          
             request.setAttribute("openQueries", nib.getItemQueries());
          } else {
            request.setAttribute("openQueries", new HashMap());
          }
          //check whether need to delete previous version
          Boolean deletePreviousVersion = (Boolean) session.getAttribute("deletePreviousVersion");
          Integer previousVersionId = (Integer) session.getAttribute("previousVersionId");
          if ((deletePreviousVersion != null && deletePreviousVersion.equals(Boolean.TRUE))
              && (previousVersionId != null && previousVersionId.intValue() > 0)) {
            logger.info("need to delete previous version");
            //whether we can delete
            boolean canDelete = canDeleteVersion(previousVersionId.intValue());
            if (!canDelete) {
              logger.info("but cannot delete previous version");
              if (session.getAttribute("itemsHaveData") == null
                  && session.getAttribute("eventsForVersion") == null) {
                addPageMessage("You are not owner of some items, so you cannot delete the CRF version.");
              }
              if (session.getAttribute("itemsHaveData") == null) {
                session.setAttribute("itemsHaveData", new ArrayList());
              }
              if (session.getAttribute("eventsForVersion") == null) {
                session.setAttribute("eventsForVersion", new ArrayList());
              }

                            forwardPage(Page.CREATE_CRF_VERSION_NODELETE);
                            return;
                        }
                        try {
                            CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
                            ArrayList nonSharedItems = (ArrayList) cvdao
                                    .findNotSharedItemsByVersion(previousVersionId.intValue());

                            // now delete the version and related info
                            nib.deleteFromDB();

                            NewCRFBean nib1 = (NewCRFBean) session.getAttribute("nib");
                            // not all the items already in the item table,still need to
                            // insert new ones
                            if (!nonSharedItems.isEmpty()) {
                                logger.info("items are not shared, need to insert");
                                request.setAttribute("openQueries", nib1.getBackupItemQueries());
                                nib1.setItemQueries(nib1.getBackupItemQueries());
                            } else {
                                // no need to insert new items, all items are already in the
                                // item table(shared by other versions)
                                nib1.setItemQueries(new HashMap());

                            }
                            session.setAttribute("nib", nib1);

                        } catch (OpenClinicaException pe) {
                            request.setAttribute("excelErrors", nib.getDeleteErrors());
                            forwardPage(Page.CREATE_CRF_VERSION_ERROR);
                            return;
                        }
                    }

                    forwardPage(Page.CREATE_CRF_VERSION_CONFIRMSQL);
                } else if ("commitsql".equalsIgnoreCase(action)) {// submit
                    logger.info("commit sql");
                    NewCRFBean nib1 = (NewCRFBean) session.getAttribute("nib");
                    if (nib1 != null) {
                        try {
                            nib1.insertToDB();
                            request.setAttribute("queries", nib1.getQueries());
                            // return those properties to initial values
                            session.removeAttribute("version");
                            session.removeAttribute("eventsForVersion");
                            session.removeAttribute("itemsHaveData");
                            session.removeAttribute("nib");
                            session.removeAttribute("deletePreviousVersion");
                            session.removeAttribute("previousVersionId");

                            // save new version spreadsheet
                            String tempFile = (String) session.getAttribute("tempFileName");
                            if (tempFile != null) {
                                logger.info("*** ^^^ *** saving new version spreadsheet" + tempFile);
                                try {
                                    String dir = SQLInitServlet.getField("filePath");
                                    File f = new File(dir + "crf" + File.separator + "original" + File.separator + tempFile);
                                    // check to see whether crf/new/ folder exists inside, if not, creates
                                    // the crf/new/ folder
                                    String finalDir = dir + "crf" + File.separator + "new" + File.separator;

                                    if (!new File(finalDir).isDirectory()) {
                                        logger.info("need to create folder for excel files" + finalDir);
                                        new File(finalDir).mkdirs();
                                    }

                                    String newFile = version.getCrfId() + version.getName() + ".xls";
                                    logger.info("*** ^^^ *** new file: " + newFile);
                                    File nf = new File(finalDir + newFile);
                                    logger.info("copying old file "+f.getName()+ 
                                            " to new file " + nf.getName());
                                    copy(f, nf);
                                    // ?
                                } catch (IOException ie) {
                                    addPageMessage("CRF version spreadsheet could not be saved on the server, please contact your admin for help.");
                                }

                            }
                            session.removeAttribute("tempFileName");
                            forwardPage(Page.CREATE_CRF_VERSION_DONE);
                        } catch (OpenClinicaException pe) {
                            request.setAttribute("excelErrors", nib1.getErrors());
                            forwardPage(Page.CREATE_CRF_VERSION_ERROR);
                        }
                    } else {
                        forwardPage(Page.UPLOAD_CRF_VERSION);
                    }
                } else if ("delete".equalsIgnoreCase(action)) {
                    logger.info("user wants to delete previous version");
                    session.setAttribute("deletePreviousVersion", Boolean.TRUE);
                    forwardPage(Page.UPLOAD_CRF_VERSION);
                }

            }
        }

    }

    /**
     * Uploads the excel version file
     * 
     * @param version
     * @throws Exception
     */
    public String uploadFile(CRFVersionBean version) throws Exception {
        String dir = SQLInitServlet.getField("filePath");

        // DefaultFileRenamePolicy rename = new DefaultFileRenamePolicy();
        // All the uploaded files will be saved in filePath/crf/original/
        File rootDir = new File(dir);
        // test whether the filePath defined is valid
        if (!rootDir.exists()) {
            logger.info("The filePath in datainfo.properties is invalid " + dir);
            addPageMessage("The filePath you defined in datainfo.properties does not seem to be a valid path, please check it.");
            session.setAttribute("version", version);
            return null;
        }

        String theDir = dir + "crf" + File.separator + "original" + File.separator;
        File theDirFile = new File(theDir);

        if (theDirFile.isDirectory()) {
            logger.info("Found the directory " + theDir);
        } else {
            theDirFile.mkdirs();
            logger.info("Made the directory " + theDir);
        }
        MultipartRequest multi = new MultipartRequest(request, theDir, 50 * 1024 * 1024);
        Enumeration params = multi.getParameterNames();
        Enumeration files = multi.getFileNames();

    String tempFile = null;
    while (files.hasMoreElements()) {
      String name = (String) files.nextElement();
      String filename = multi.getFilesystemName(name);
      String type = multi.getContentType(name);
      File f = multi.getFile(name);
      System.out.println("file name:" + f.getName());
      if (f == null || 
    		  (f.getName() == null) || 
    		  ((f.getName().indexOf(".xls") < 0) && (f.getName().indexOf(".XLS") < 0))) {
    	  //last line added to fix bug for NDAR, 6-27-06 tbh
        // was changed again by jxu 08-28-06 to fix the code bug
        addPageMessage("The file you uploaded does not seem to be an Excel spreadsheet.");
        session.setAttribute("version", version);
        return tempFile;

            } else {
                tempFile = f.getName();
                SpreadSheetTable htab = new SpreadSheetTable(new FileInputStream(theDir + tempFile), ub, 
                        version.getName());

                htab.setCrfId(version.getCrfId());

                // create newCRFBean here
                NewCRFBean nib = htab.toNewCRF(sm.getDataSource());
                ArrayList ibs = isItemSame(nib.getItems(), version);

                if (!ibs.isEmpty()) {
                    ArrayList warnings = new ArrayList();
                    warnings
                        .add("You may not modify the Description, Data Type, Units, or PHI Status of items already included in this CRF.");
                    for (int i = 0; i < ibs.size(); i++) {
                        ItemBean ib = (ItemBean) ibs.get(i);
                        if (ib.getOwner().getId() == ub.getId()) {
                            warnings
                                .add("The item '"
                                            + ib.getName()
                                            + "' in your spreadsheet already exists for this CRF (as part of another version) "
                                            + "and has a different DESCRIPTION("
                                            + ib.getDescription()
                                            + "), "
                                            + "DATA_TYPE("
                                            + ib.getDataType().getName()
                                            + "), "
                                            + "UNITS("
                                            + ib.getUnits()
                                            + "), and/or PHI_STATUS("
                                            + ib.isPhiStatus()
                                            + "). UNITS and DATA_TYPE will not be changed if the item has data already. PHI, DESCRIPTION, "
                                            + "will be changed if you continue. "
                                            + "If you think you made mistake, please go back and rename the item.");
                        } else {
                            warnings.add("The item '" + ib.getName() + "' in your spreadsheet already exists for "
                                            + "this CRF (as part of another version) and has a different DESCRIPTION("
                                            + ib.getDescription() + "), " + "DATA_TYPE(" + ib.getDataType().getName() + ") ,"
                                            + "UNITS(" + ib.getUnits() + "), and/or PHI_STATUS(" + ib.isPhiStatus()
                                            + "). These fields cannot be modified because "
                                            + "you are not the CRF owner, and if you continue, "
                                            + "the PHI, UNITS, DESCRIPTION, and/or DATA_TYPE for this item will be ignored. "
                                            + "If you do wish to keep your settings, please go back and rename the item to"
                                            + " one that does not already exist in the CRF.");
                        }

                        request.setAttribute("warnings", warnings);
                    }
                }
                ItemBean ib = isResponseValid(nib.getItems(), version);
                if (ib != null) {

                    nib
                        .getErrors()
                        .add(
                                    "The item:"
                                            + ib.getName()
                                            + "in your spreadsheet already exists in the DB with different "
                                            + "response options/text. You cannot code an existing response "
                                            + "option with a different text, or code an existing response "
                                            + "text with a different option. Please check it and upload your spreadsheet again.");
                }

                request.setAttribute("excelErrors", nib.getErrors());
                request.setAttribute("htmlTable", nib.getHtmlTable());
                session.setAttribute("nib", nib);
            }
        }
        return tempFile;
    }

  /**
   * Checks whether the version can be deleted
   * 
   * @param previousVersionId
   * @return
   */
  private boolean canDeleteVersion(int previousVersionId) {
    CRFVersionDAO cdao = new CRFVersionDAO(sm.getDataSource());
    ArrayList items = null;
    ArrayList itemsHaveData = new ArrayList();
    //boolean isItemUsedByOtherVersion =
    // cdao.isItemUsedByOtherVersion(previousVersionId);
    //if (isItemUsedByOtherVersion) {
    // ArrayList itemsUsedByOtherVersion = (ArrayList)
    // cdao.findItemUsedByOtherVersion(previousVersionId);
    //  session.setAttribute("itemsUsedByOtherVersion",itemsUsedByOtherVersion);
    // return false;
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    ArrayList events = ecdao.findAllByCRFVersion(previousVersionId);
    if (!events.isEmpty()) {
      session.setAttribute("eventsForVersion", events);
      return false;
        }
        items = (ArrayList) cdao.findNotSharedItemsByVersion(previousVersionId);
        for (int i = 0; i < items.size(); i++) {
            ItemBean item = (ItemBean) items.get(i);
            if (ub.getId() != item.getOwner().getId()) {
                logger.info("not owner" + item.getOwner().getId() + "<>" + ub.getId());
                return false;
            }
            if (cdao.hasItemData(item.getId())) {
                itemsHaveData.add(item);
                logger.info("item has data");
                session.setAttribute("itemsHaveData", itemsHaveData);
                return false;
            }
        }

        //user is the owner and item not have data,
        //delete previous version with non-shared items
        NewCRFBean nib = (NewCRFBean) session.getAttribute("nib");
        nib.setDeleteQueries(cdao.generateDeleteQueries(previousVersionId, items));
        session.setAttribute("nib", nib);
        return true;

    }

    /**
     * Checks whether the item with same name has the same other fields: units,
     * phi_status if no, they are two different items, cannot have the same same
     * 
     * @param items
     *          items from excel
     * @return the items found
     */
    private ArrayList isItemSame(HashMap items, CRFVersionBean version) {
        ItemDAO idao = new ItemDAO(sm.getDataSource());
        ArrayList diffItems = new ArrayList();
        Set names = items.keySet();
        Iterator it = names.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            ItemBean newItem = (ItemBean) idao.findByNameAndCRFId(name, version.getCrfId());
            ItemBean item = (ItemBean) items.get(name);
            if (newItem.getId() > 0) {
                if (!item.getUnits().equalsIgnoreCase(newItem.getUnits())
                        || item.isPhiStatus() != newItem.isPhiStatus()
                        || item.getDataType().getId() != newItem.getDataType().getId()
                        || !item.getDescription().equalsIgnoreCase(newItem.getDescription())) {

                    logger
                        .info("found two items with same name but different units/phi/datatype/description");
                    //logger.info("item" + newItem.getId() );
                    //logger.info("unit" + newItem.getUnits() + " |" + item.getUnits() );
                    //logger.info("phi" + newItem.isPhiStatus() + "| " +
                    // item.isPhiStatus() );
                    //logger.info("DataType" + newItem.getDataType().getId() + " | " +
                    // item.getDataType().getId() );
                    //logger.info("Description" + newItem.getDescription() + " |" +
                    // item.getDescription() );
                    diffItems.add(newItem);
                }
            }
        }

        return diffItems;
    }

    private ItemBean isResponseValid(HashMap items, CRFVersionBean version) {
        ItemDAO idao = new ItemDAO(sm.getDataSource());
        ItemFormMetadataDAO metadao = new ItemFormMetadataDAO(sm.getDataSource());
        Set names = items.keySet();
        Iterator it = names.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            ItemBean oldItem = (ItemBean) idao.findByNameAndCRFId(name, version.getCrfId());
            ItemBean item = (ItemBean) items.get(name);
            if (oldItem.getId() > 0) {//found same item in DB
                ArrayList metas = metadao.findAllByItemId(oldItem.getId());
                for (int i = 0; i < metas.size(); i++) {
                    ItemFormMetadataBean ifmb = (ItemFormMetadataBean) metas.get(i);
                    ResponseSetBean rsb = ifmb.getResponseSet();
                    if (hasDifferentOption(rsb, item.getItemMeta().getResponseSet()) != null) {
                        return item;
                    }
                }

            }
        }
        return null;

    }

    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

  /**
   * When the version is added, for each non-new item OpenClinica should check
   * the RESPONSE_OPTIONS_TEXT, and REPONSE_VALUES used for the item in other
   * versions of the CRF.
   * 
   * For a given RESPONSE_VALUES code, the associated RESPONSE_OPTIONS_TEXT
   * string is different than in a previous version
   * 
   * For a given RESPONSE_OPTIONS_TEXT string, the associated RESPONSE_VALUES
   * code is different than in a previous version
   * 
   * @param oldRes
   * @param newRes
   * @return The original option
   */
  public ResponseOptionBean hasDifferentOption(ResponseSetBean oldRes, ResponseSetBean newRes) {
    ArrayList oldOptions = oldRes.getOptions();       
    ArrayList newOptions = newRes.getOptions();
   
        if(ResponseType.CALCULATION.getName().equalsIgnoreCase(newRes.getResponseType().getName())){
            return null;
        }
    for (int i = 0; i < oldOptions.size(); i++) {//from database
      ResponseOptionBean rob = (ResponseOptionBean) oldOptions.get(i);
      String text = rob.getText();
      String value = rob.getValue();
      for (int j = 0; j < newOptions.size(); j++) {//from spreadsheet
        ResponseOptionBean rob1 = (ResponseOptionBean) newOptions.get(j);
        String text1 = rob1.getText();
                
        String value1 = rob1.getValue();
                
        if (text1.equalsIgnoreCase(text) && !value1.equals(value)) {
          System.out.println("different response value:" + value1 + "|" + value);
          return rob;
        } else if (!text1.equalsIgnoreCase(text) && value1.equals(value)) {
          System.out.println("different response text:" + text1 + "|" + text);
          return rob;
        }
        
        
      }

    }
    return null;
  }

    /**
     * Copy one file to another
     * 
     * @param src
     * @param dst
     * @throws IOException
     */
    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
  }
