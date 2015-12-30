/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p/>
 * Returns multiple types of things based on the parsing; returns
 * html table returns data objects as SQL strings.
 * <p/>
 * The most important method here is the toNIB() method, which returns the
 * NewInstrumentBean which in turn creates a new instrument version in the
 * database.
 *
 * @author thickerson with help from Brian Gilman @ the Whitehead Institute
 *         modified by jxu
 * @version CVS: $Id: SpreadSheetTable.java,v 1.28 2006/09/01 00:37:19 jxu Exp $
 */

public class SpreadSheetTableClassic implements SpreadSheetTable {//extends SpreadSheetTable {

    private POIFSFileSystem fs = null;

    private UserAccountBean ub = null;

    private String versionName = null;

    private int crfId = 0;

    private String crfName = "";

    private String versionIdString = "";

    //the default; all crf ids should be > 0, tbh 8-29 :-)
    protected final Logger logger = Logger.getLogger(getClass().getName());

    public SpreadSheetTableClassic(FileInputStream parseStream, UserAccountBean ub,
                                   String versionName)
            throws IOException {
        //super();
        this.fs = new POIFSFileSystem(parseStream);
        this.ub = ub;
        this.versionName = versionName;
    }

    public void setCrfId(int id) {
        this.crfId = id;
    }

    public int getCrfId() {
        return this.crfId;
    }

    public NewCRFBean toNewCRF(javax.sql.DataSource ds) throws IOException {
        String dbName = SQLInitServlet.getDBName();

        NewCRFBean ncrf = new NewCRFBean(ds, crfId);

        ncrf.setCrfId(crfId);//set crf id

        StringBuffer buf = new StringBuffer();
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        int numSheets = wb.getNumberOfSheets();
        ArrayList queries = new ArrayList();
        ArrayList errors = new ArrayList();
        ArrayList repeats = new ArrayList();
        HashMap tableNames = new HashMap();
        HashMap items = new HashMap();
        String pVersion = "";
        String pVerDesc = "";
        int parentId = 0;
        int dataTypeId = 5;//default is ST(String) type
        HashMap itemCheck = ncrf.getItemNames();
        HashMap GroupCheck = ncrf.getItemGroupNames();
        HashMap openQueries = new LinkedHashMap();
        HashMap backupItemQueries = new LinkedHashMap();//save all the item queries if
        // deleting item happens
        ArrayList secNames = new ArrayList(); //check for dupes, also


        CRFDAO cdao = new CRFDAO(ds);
        CRFBean crf = (CRFBean) cdao.findByPK(crfId);

        ItemDataDAO iddao = new ItemDataDAO(ds);
        ItemDAO idao = new ItemDAO(ds);
        CRFVersionDAO cvdao = new CRFVersionDAO(ds);

        int validSheetNum = 0;
        for (int j = 0; j < numSheets; j++) {
            HSSFSheet sheet = wb.getSheetAt(j);//sheetIndex);
            String sheetName = wb.getSheetName(j);
            if (sheetName.equalsIgnoreCase("CRF") || sheetName.equalsIgnoreCase("Sections")
                    || sheetName.equalsIgnoreCase("Items")) {
                validSheetNum++;
            }
        }
        if (validSheetNum != 3) {
            errors.add("The excel spreadsheet doesn't have required valid worksheets. Please check whether it contains" +
                    " sheets of CRF, Sections and Items.");
        }
        //check to see if questions are referencing a valid section name, tbh 7/30
        for (int j = 0; j < numSheets; j++) {
            HSSFSheet sheet = wb.getSheetAt(j);//sheetIndex);
            String sheetName = wb.getSheetName(j);
            if (sheetName.equalsIgnoreCase("Instructions")) {
                //totally ignore instructions
            } else {
                /*
                * current strategem: build out the queries by hand and revisit this as
                * part of the data loading module. We begin to check for errors here
                * and look for blank cells where there should be data, tbh, 7/28
                */
                int numRows = sheet.getPhysicalNumberOfRows();
                int lastNumRow = sheet.getLastRowNum();
                //System.out.println("PhysicalNumberOfRows" + sheet.getPhysicalNumberOfRows());
                logger.info("PhysicalNumberOfRows" + sheet.getPhysicalNumberOfRows());
                //System.out.println("LastRowNum()" + sheet.getLastRowNum());
                String secName = "";
                String page = "";
                ArrayList resNames = new ArrayList();//records all the response_labels
                HashMap htmlErrors = new HashMap();

                //the above two need to persist across mult. queries,
                //and they should be created FIRST anyway, since instrument is first
                //also need to add to VERSIONING_MAP, tbh, 6-6-3

                //try to count how many blank rows, if 5 concective blank rows found, stop reading
                int blankRowCount = 0;

                if (sheetName.equalsIgnoreCase("Items")) {
                    logger.info("read an item in sheet" + sheetName);

//        let's insert the default group first                
                    ItemGroupBean defaultGroup = new ItemGroupBean();
                    defaultGroup.setName("Ungrouped");
                    defaultGroup.setCrfId(crfId);
                    defaultGroup.setStatus(Status.AVAILABLE);

                    String defaultSql = "";
                    if (dbName.equals("oracle")) {
                        defaultSql = "INSERT INTO ITEM_GROUP ( " +
                                "name, crf_id, status_id, date_created ,owner_id)" +
                                "VALUES ('" + defaultGroup.getName() + "', " + defaultGroup.getCrfId() + ","
                                + defaultGroup.getStatus().getId() + "," + "sysdate," + ub.getId() + ")";
                    } else {
                        defaultSql = "INSERT INTO ITEM_GROUP ( " +
                                "name, crf_id, status_id, date_created ,owner_id)" +
                                "VALUES ('" + defaultGroup.getName() + "', " + defaultGroup.getCrfId() + ","
                                + defaultGroup.getStatus().getId() + "," + "now()," + ub.getId() + ")";
                    }


                    if (!GroupCheck.containsKey("Ungrouped")) {
                        queries.add(defaultSql);
                    }

                    for (int k = 1; k < numRows; k++) {
                        //System.out.println("hit row "+k);
                        if (blankRowCount == 5) {
                            System.out.println("hit end of the row ");
                            break;
                        }
                        if (sheet.getRow(k) == null) {
                            blankRowCount++;
                            continue;
                        }
                        HSSFCell cell = sheet.getRow(k).getCell((short) 0);
                        String itemName = getValue(cell);

                        if (StringUtil.isBlank(itemName)) {
                            errors.add("The ITEM_NAME column was blank at row " + k + ", Items worksheet.");
                            htmlErrors.put(j + "," + k + ",0", "REQUIRED FIELD");
                        }
                        if (repeats.contains(itemName)) {
                            errors.add("A duplicate ITEM_NAME of " + itemName + " was detected at row " + k
                                    + ", Items worksheet.");
                            htmlErrors.put(j + "," + k + ",0", "DUPLICATE FIELD");
                        }
                        repeats.add(itemName);

                        cell = sheet.getRow(k).getCell((short) 1);
                        String descLabel = getValue(cell);

                        if (StringUtil.isBlank(descLabel)) {
                            errors.add("The DESCRIPTION_LABEL column was blank at row " + k + ", Items worksheet.");
                            htmlErrors.put(j + "," + k + ",1", "REQUIRED FIELD");
                        }

                        cell = sheet.getRow(k).getCell((short) 2);
                        String leftItemText = getValue(cell);
                        if (StringUtil.isBlank(leftItemText)) {
                            errors.add("The LEFT_ITEM_TEXT column was blank at row " + k + ", Items worksheet.");
                            htmlErrors.put(j + "," + k + ",2", "REQUIRED FIELD");
                        }

                        cell = sheet.getRow(k).getCell((short) 3);
                        String unit = getValue(cell);
                        cell = sheet.getRow(k).getCell((short) 4);
                        String rightItemText = getValue(cell);

                        cell = sheet.getRow(k).getCell((short) 5);
                        if (cell != null) {
                            secName = getValue(cell);
                        }

                        if (!secNames.contains(secName)) {
                            errors
                                    .add("The SECTION_LABEL column is not a valid section at row "
                                            + k
                                            + ", Items worksheet.  "
                                            + "Please check the Sections worksheet to see that there is a valid LABEL for this SECTION_LABEL.");
                            htmlErrors.put(j + "," + k + ",5", "NOT A VALID LABEL");
                        }
                        cell = sheet.getRow(k).getCell((short) 6);
                        String header = getValue(cell);

                        cell = sheet.getRow(k).getCell((short) 7);
                        String subHeader = getValue(cell);

                        cell = sheet.getRow(k).getCell((short) 8);
                        String parentItem = getValue(cell);
                        //BWP>>Prevent parent names that equal the Item names
                        if (itemName != null &&
                                itemName.equalsIgnoreCase(parentItem)) {
                            parentItem = "";
                        }

                        cell = sheet.getRow(k).getCell((short) 9);
                        int columnNum = 0;
                        String column = getValue(cell);
                        if (!StringUtil.isBlank(column)) {
                            try {
                                columnNum = Integer.parseInt(column);
                            } catch (NumberFormatException ne) {
                                columnNum = 0;
                            }
                        }

                        cell = sheet.getRow(k).getCell((short) 10);
                        if (cell != null) {
                            page = getValue(cell);
                        }

                        cell = sheet.getRow(k).getCell((short) 11);
                        String questionNum = getValue(cell);

                        cell = sheet.getRow(k).getCell((short) 12);
                        String responseType = getValue(cell);
                        int responseTypeId = 1;
                        if (StringUtil.isBlank(responseType)) {
                            errors.add("The RESPONSE_TYPE column was blank at row " + k + ", items worksheet.");
                            htmlErrors.put(j + "," + k + ",12", "REQUIRED FIELD");

                        } else {
                            if (!ResponseType.findByName(responseType.toLowerCase())) {
                                errors.add("The RESPONSE_TYPE column was invalid at row " + k
                                        + ", items worksheet.");
                                htmlErrors.put(j + "," + k + ",12", "INVALID FIELD");
                            } else {
                                responseTypeId = (ResponseType.getByName(responseType.toLowerCase())).getId();
                            }
                        }

                        cell = sheet.getRow(k).getCell((short) 13);
                        String responseLabel = getValue(cell);
                        if (StringUtil.isBlank(responseLabel)) {
                            errors.add("The RESPONSE_LABEL column was blank at row " + k + ", items worksheet.");
                            htmlErrors.put(j + "," + k + ",13", "REQUIRED FIELD");
                        }
                        cell = sheet.getRow(k).getCell((short) 14);
                        String resOptions = getValue(cell);
                        if (responseLabel.equalsIgnoreCase("text") || responseLabel.equalsIgnoreCase("textarea")) {
                            resOptions = "text";
                        }
                        int numberOfOptions = 0;
                        if (!resNames.contains(responseLabel) && (StringUtil.isBlank(resOptions))) {
                            errors.add("The RESPONSE_OPTIONS_TEXT column was blank at row " + k
                                    + ", Items worksheet.");
                            htmlErrors.put(j + "," + k + ",14", "REQUIRED FIELD");
                        }
                        if (!resNames.contains(responseLabel) && (!StringUtil.isBlank(resOptions))) {
                            //String[] resArray = resOptions.split(",");
                            String text1 = resOptions.replaceAll("\\\\,", "##");
                            String[] resArray = text1.split(",");
                            numberOfOptions = resArray.length;
                        }
                        cell = sheet.getRow(k).getCell((short) 15);
                        String resValues = getValue(cell);
                        if (responseLabel.equalsIgnoreCase("text") || responseLabel.equalsIgnoreCase("textarea")) {
                            resValues = "text";
                        }
                        if (!resNames.contains(responseLabel) && (StringUtil.isBlank(resValues))) {
                            errors.add("The RESPONSE_VALUES column was blank at row " + k + ", Items worksheet.");
                            htmlErrors.put(j + "," + k + ",15", "REQUIRED FIELD");
                        }
                        if (numberOfOptions > 0) {
                            String value1 = resValues.replaceAll("\\\\,", "##");
                            String[] resValArray = value1.split(",");
                            if (resValArray.length != numberOfOptions) {
                                errors.add("There are an incomplete number of option-value pairs in "
                                        + "RESPONSE_OPTIONS and RESPONSE_VALUES at row " + k
                                        + ", questions worksheet; perhaps you are missing a comma? If there is a comma in any option text/value itself, please use \\, instead.");
                                htmlErrors.put(j + "," + k + ",14", "NUMBER OF OPTIONS DOES NOT MATCH");
                                htmlErrors.put(j + "," + k + ",15", "NUMBER OF VALUES DOES NOT MATCH");
                            }
                        }

                        cell = sheet.getRow(k).getCell((short) 16);
                        String dataType = getValue(cell);
                        String dataTypeIdString = "1";
                        if (StringUtil.isBlank(dataType)) {
                            errors.add("The DATA_TYPE column was blank at row " + k + ", items worksheet.");
                            htmlErrors.put(j + "," + k + ",16", "REQUIRED FIELD");

                        } else {
                            if (!ItemDataType.findByName(dataType.toLowerCase())) {
                                errors.add("The DATA_TYPE column was invalid at row " + k + ", Items worksheet.");
                                htmlErrors.put(j + "," + k + ",16", "INVALID FIELD");
                            } else {
                                //dataTypeId = (ItemDataType.getByName(dataType)).getId();
                                dataTypeIdString = "(SELECT ITEM_DATA_TYPE_ID From ITEM_DATA_TYPE Where CODE='"
                                        + dataType.toUpperCase() + "')";
                            }
                        }

                        cell = sheet.getRow(k).getCell((short) 17);
                        String regexp = getValue(cell);
                        String regexp1 = "";
                        if (!StringUtil.isBlank(regexp)) {
                            // parse the string and get reg exp eg. regexp: /[0-9]*/
                            regexp1 = regexp.trim();
                            if (regexp1.startsWith("regexp:")) {
                                //YW 10-03-2007
                                String finalRegexp = regexp1.substring(7).trim();
                                if (finalRegexp.startsWith("/") && finalRegexp.endsWith("/")) {
                                    finalRegexp = finalRegexp.substring(1, finalRegexp.length() - 1);
                                    try {
                                        Pattern p = Pattern.compile(finalRegexp);
                                        //YW 11-21-2007 << add another \ if there is \ in regexp
                                        char[] chars = regexp1.toCharArray();
                                        regexp1 = "";
                                        for (char c : chars) {
                                            if (c == '\\') {
                                                regexp1 += c + "\\";
                                            } else {
                                                regexp1 += c;
                                            }
                                        }
                                        //YW >>
                                    } catch (PatternSyntaxException pse) {
                                        errors.add("The VALIDATION column has an invalid regular expression at row " + k
                                                + ", Items worksheet. Example: regexp: /[0-9]*/ ");
                                        htmlErrors.put(j + "," + k + ",17", "INVALID FIELD");
                                    }
                                } else {
                                    errors.add("The VALIDATION column has an invalid regular expression at row " + k
                                            + ", Items worksheet. Example: regexp: /[0-9]*/ ");
                                    htmlErrors.put(j + "," + k + ",17", "INVALID FIELD");
                                }
                            } else if (regexp1.startsWith("func:")) {
                                boolean isProperFunction = false;
                                try {
                                    Validator.processCRFValidationFunction(regexp1);
                                    isProperFunction = true;
                                }
                                catch (Exception e) {
                                    errors.add(e.getMessage() + ", at row " + k
                                            + ", Items worksheet.");
                                    htmlErrors.put(j + "," + k + ",17", "INVALID FIELD");
                                }
                            } else {
                                errors.add("The VALIDATION column was invalid at row " + k
                                        + ", Items worksheet. ");
                                htmlErrors.put(j + "," + k + ",17", "INVALID FIELD");
                            }


                        }

                        cell = sheet.getRow(k).getCell((short) 18);
                        String regexpError = getValue(cell);
                        if (!StringUtil.isBlank(regexp) && StringUtil.isBlank(regexpError)) {
                            errors.add("The VALIDATION_ERROR_MESSAGE column was blank at row " + k
                                    + ", Items worksheet. It cannot be blank if VALIDATION is not blank.");
                            htmlErrors.put(j + "," + k + ",18", "REQUIRED FIELD");
                        }

                        boolean phiBoolean = false;
                        cell = sheet.getRow(k).getCell((short) 19);
                        //String phi = getValue(cell);
                        String phi = "";
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            double dphi = (double) cell.getNumericCellValue();
                            if ((dphi - (int) dphi) * 1000 == 0) {
                                phi = (int) dphi + "";
                            }
                        }
                        if (!"0".equals(phi) && !"1".equals(phi)) {
                            errors.add("The PHI column was invalid at row " + k
                                    + ", Items worksheet. PHI can only be either 0 or 1.");
                            htmlErrors.put(j + "," + k + ",19", "INVALID VALUE");
                        } else {
                            phiBoolean = ("1".equals(phi)) ? true : false;
                        }

                        boolean isRequired = false;
                        cell = sheet.getRow(k).getCell((short) 20);
                        String required = getValue(cell);
                        logger.info(getValue(cell));
                        //does the above no longer work???
                        //String required = "";
                        if (StringUtil.isBlank(required)) {
                            required = "0";
                        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            double dr = (double) cell.getNumericCellValue();
                            if ((dr - (int) dr) * 1000 == 0) {
                                required = (int) dr + "";
                            }
                        }

                        if (!"0".equals(required) && !"1".equals(required)) {
                            errors.add("The REQUIRED column was invalid at row " + k
                                    + ", Items worksheet. REQUIRED can only be either 0 or 1.");
                            htmlErrors.put(j + "," + k + ",20", "INVALID VALUE");
                        } else {
                            isRequired = ("1".equals(required)) ? true : false;
                        }

                        //better spot for checking item might be right here, tbh 7-25
                        String vlSql = "";
                        if (dbName.equals("oracle")) {
                            vlSql = "INSERT INTO ITEM (NAME,DESCRIPTION,UNITS,PHI_STATUS,"
                                    + "ITEM_DATA_TYPE_ID, ITEM_REFERENCE_TYPE_ID,STATUS_ID,OWNER_ID,DATE_CREATED) "
                                    + "VALUES ('" + stripQuotes(itemName) + "','" + stripQuotes(descLabel) + "','"
                                    + stripQuotes(unit) + "'," + (phiBoolean == true ? 1 : 0) + "," + dataTypeIdString + ",1,1,"
                                    + ub.getId() + ", sysdate)";
                        } else {
                            vlSql = "INSERT INTO ITEM (NAME,DESCRIPTION,UNITS,PHI_STATUS,"
                                    + "ITEM_DATA_TYPE_ID, ITEM_REFERENCE_TYPE_ID,STATUS_ID,OWNER_ID,DATE_CREATED) "
                                    + "VALUES ('" + stripQuotes(itemName) + "','" + stripQuotes(descLabel) + "','"
                                    + stripQuotes(unit) + "'," + phiBoolean + "," + dataTypeIdString + ",1,1,"
                                    + ub.getId() + ", NOW())";
                        }

                        backupItemQueries.put(itemName, vlSql);
                        //to compare items from DB later, if two items have the same name,
                        //but different units or phiStatus, they are different
                        ItemBean ib = new ItemBean();
                        ib.setName(itemName);
                        ib.setUnits(unit);
                        ib.setPhiStatus(phiBoolean);
                        ib.setDescription(descLabel);
                        ib.setDataType(ItemDataType.getByName(dataType.toLowerCase()));

                        //put metadata into item
                        ResponseSetBean rsb = new ResponseSetBean();
                        //notice that still "\\," in options - jxu-08-31-06
                        String resOptions1 = resOptions.replaceAll("\\\\,", "\\,");
                        String resValues1 = resValues.replaceAll("\\\\,", "\\,");
                        rsb.setOptions(stripQuotes(resOptions1), stripQuotes(resValues1));

                        ItemFormMetadataBean ifmb = new ItemFormMetadataBean();
                        ifmb.setResponseSet(rsb);
                        ib.setItemMeta(ifmb);
                        items.put(itemName, ib);

                        int ownerId = ub.getId();

                        if (!itemCheck.containsKey(itemName)) {// item not in the DB
                            openQueries.put(itemName, vlSql);

                        } else {// item in the DB
                            ItemBean oldItem = (ItemBean) idao.findByNameAndCRFId(itemName, crfId);
                            if (oldItem.getOwnerId() == ub.getId()) {// owner can update
                                if (!cvdao.hasItemData(oldItem.getId())) {// no item data
                                    String upSql = "";
                                    if (dbName.equals("oracle")) {
                                        upSql = "UPDATE ITEM SET DESCRIPTION='" + stripQuotes(descLabel) + "'," +
                                                "UNITS='" + stripQuotes(unit) + "'," +
                                                "PHI_STATUS=" + (phiBoolean ? 1 : 0) + "," +
                                                "ITEM_DATA_TYPE_ID=" + dataTypeIdString +
                                                " WHERE exists (SELECT versioning_map.item_id from versioning_map, crf_version where" +
                                                " versioning_map.crf_version_id = crf_version.crf_version_id" +
                                                " AND crf_version.crf_id= " + crfId + " AND item.item_id = versioning_map.item_id)" +
                                                " AND item.name='" + stripQuotes(itemName) +
                                                "' AND item.owner_id = " + ownerId;
                                    } else {
                                        upSql = "UPDATE ITEM SET DESCRIPTION='" + stripQuotes(descLabel) + "'," +
                                                "UNITS='" + stripQuotes(unit) + "'," +
                                                "PHI_STATUS=" + phiBoolean + "," +
                                                "ITEM_DATA_TYPE_ID=" + dataTypeIdString +
                                                " FROM versioning_map, crf_version" +
                                                " WHERE item.name='" + stripQuotes(itemName)
                                                + "' AND item.owner_id = " + ownerId
                                                + " AND item.item_id = versioning_map.item_id AND"
                                                + " versioning_map.crf_version_id = crf_version.crf_version_id"
                                                + " AND crf_version.crf_id = " + crfId;
                                    }//end of if dbname
                                    openQueries.put(itemName, upSql);
                                } else {
                                    String upSql = "";
                                    if (dbName.equals("oracle")) {
                                        upSql = "UPDATE ITEM SET DESCRIPTION='" + stripQuotes(descLabel) + "'," +
                                                "PHI_STATUS=" + (phiBoolean ? 1 : 0) + "," +
                                                " WHERE exists (SELECT versioning_map.item_id from versioning_map, crf_version where" +
                                                " versioning_map.crf_version_id = crf_version.crf_version_id" +
                                                " AND crf_version.crf_id= " + crfId + " AND item.item_id = versioning_map.item_id)" +
                                                " AND item.name='" + stripQuotes(itemName) +
                                                "' AND item.owner_id = " + ownerId;

                                    } else {
                                        upSql = "UPDATE ITEM SET DESCRIPTION='" + stripQuotes(descLabel) + "'," +
                                                "PHI_STATUS=" + phiBoolean +
                                                " FROM versioning_map, crf_version" +
                                                " WHERE item.name='" + stripQuotes(itemName)
                                                + "' AND item.owner_id = " + ownerId
                                                + " AND item.item_id = versioning_map.item_id AND"
                                                + " versioning_map.crf_version_id = crf_version.crf_version_id"
                                                + " AND crf_version.crf_id = " + crfId;
                                    }//end of if dbName
                                    openQueries.put(itemName, upSql);
                                }
                            } else {
                                ownerId = oldItem.getOwner().getId();
                            }
                        }
                        String sql = "INSERT INTO RESPONSE_SET (LABEL, OPTIONS_TEXT, OPTIONS_VALUES, "
                                + "RESPONSE_TYPE_ID, VERSION_ID)" + " VALUES ('" + stripQuotes(responseLabel)
                                + "', '" + stripQuotes(resOptions) + "','" + stripQuotes(resValues) + "',"
                                + "(SELECT RESPONSE_TYPE_ID From RESPONSE_TYPE Where NAME='"
                                + stripQuotes(responseType.toLowerCase()) + "')," + versionIdString + ")";
                        if (!resNames.contains(responseLabel)) {
                            queries.add(sql);
                            resNames.add(responseLabel);
                            //this will have to change since we have some data in the actual
                            // spreadsheet
                            //change it to caching response set names in a collection?
                            //or just delete the offending cells from the spreadsheet?
                        }
                        String parentItemString = "0";
                        if (!StringUtil.isBlank(parentItem)) {
                            if (dbName.equals("oracle")) {
                                parentItemString = "(SELECT MAX(ITEM_ID) FROM ITEM WHERE NAME='" + stripQuotes(parentItem)
                                        + "' AND owner_id = " + ownerId + " )";
                            } else {
                                parentItemString = "(SELECT ITEM_ID FROM ITEM WHERE NAME='" + stripQuotes(parentItem)
                                        + "' AND owner_id = " + ownerId + " ORDER BY OID DESC LIMIT 1)";
                            }
                        }

                        String sql2 = "";
                        if (dbName.equals("oracle")) {
                            sql2 = "INSERT INTO ITEM_FORM_METADATA (CRF_VERSION_ID, RESPONSE_SET_ID,"
                                    + "ITEM_ID,SUBHEADER,\"header\",LEFT_ITEM_TEXT,"
                                    + "RIGHT_ITEM_TEXT,PARENT_ID,SECTION_ID,ORDINAL,PARENT_LABEL,COLUMN_NUMBER,PAGE_NUMBER_LABEL,question_number_label,"
                                    + "REGEXP,REGEXP_ERROR_MSG,REQUIRED)" + " VALUES (" + versionIdString
                                    + ",(SELECT RESPONSE_SET_ID FROM RESPONSE_SET WHERE LABEL='"
                                    + stripQuotes(responseLabel) + "'" + " AND VERSION_ID=" + versionIdString
                                    + "),(SELECT MAX(ITEM_ID) FROM ITEM WHERE NAME='" + itemName
                                    + "' AND owner_id = " + ownerId + " ),'"
                                    + stripQuotes(subHeader) + "','" + stripQuotes(header) + "','"
                                    + stripQuotes(leftItemText) + "','" + stripQuotes(rightItemText) + "',"
                                    + parentItemString + ", (SELECT SECTION_ID FROM SECTION WHERE LABEL='" + secName
                                    + "' AND " + "CRF_VERSION_ID IN " + versionIdString + "), " + k + ",'" + parentItem
                                    + "'," + columnNum + ",'" + stripQuotes(page) + "','" + stripQuotes(questionNum) + "','"
                                    + stripQuotes(regexp1) + "','" + stripQuotes(regexpError) + "', " + (isRequired ? 1 : 0) + ")";

                        } else {
                            sql2 = "INSERT INTO ITEM_FORM_METADATA (CRF_VERSION_ID, RESPONSE_SET_ID,"
                                    + "ITEM_ID,SUBHEADER,HEADER,LEFT_ITEM_TEXT,"
                                    + "RIGHT_ITEM_TEXT,PARENT_ID,SECTION_ID,ORDINAL,PARENT_LABEL,COLUMN_NUMBER,PAGE_NUMBER_LABEL,question_number_label,"
                                    + "REGEXP,REGEXP_ERROR_MSG,REQUIRED)" + " VALUES (" + versionIdString
                                    + ",(SELECT RESPONSE_SET_ID FROM RESPONSE_SET WHERE LABEL='"
                                    + stripQuotes(responseLabel) + "'" + " AND VERSION_ID=" + versionIdString
                                    + "),(SELECT ITEM_ID FROM ITEM WHERE NAME='" + itemName
                                    + "' AND owner_id = " + ownerId + " ORDER BY OID DESC LIMIT 1),'"
                                    + stripQuotes(subHeader) + "','" + stripQuotes(header) + "','"
                                    + stripQuotes(leftItemText) + "','" + stripQuotes(rightItemText) + "',"
                                    + parentItemString + ", (SELECT SECTION_ID FROM SECTION WHERE LABEL='" + secName
                                    + "' AND " + "CRF_VERSION_ID IN " + versionIdString + "), " + k + ",'" + parentItem
                                    + "'," + columnNum + ",'" + stripQuotes(page) + "','" + stripQuotes(questionNum) + "','"
                                    + stripQuotes(regexp1) + "','" + stripQuotes(regexpError) + "', " + isRequired + ")";
                        }
                        queries.add(sql2);
                        //link version with items now
                        String sql3 = "";
                        if (dbName.equals("oracle")) {
                            sql3 = "INSERT INTO VERSIONING_MAP (CRF_VERSION_ID, ITEM_ID) VALUES ( "
                                    + versionIdString + "," + "(SELECT MAX(ITEM_ID) FROM ITEM WHERE NAME='" + itemName
                                    + "' AND owner_id = " + ownerId + " ))";
                        } else {
                            sql3 = "INSERT INTO VERSIONING_MAP (CRF_VERSION_ID, ITEM_ID) VALUES ( "
                                    + versionIdString + "," + "(SELECT ITEM_ID FROM ITEM WHERE NAME='" + itemName
                                    + "' AND owner_id = " + ownerId + " ORDER BY OID DESC LIMIT 1))";
                        }

                        queries.add(sql3);
//BADS FLAG
                        //this item doesn't have group, so put it into 'Ungrouped' group
                        String sqlGroupLabel = "";
                        if (dbName.equals("oracle")) {
                            sqlGroupLabel = "INSERT INTO ITEM_GROUP_METADATA (" +
                                    "item_group_id,\"header\"," +
                                    "subheader, layout, repeat_number, repeat_max," +
                                    " repeat_array,row_start_number, crf_version_id," +
                                    "item_id , ordinal, borders) VALUES (" +
                                    "(SELECT MAX(ITEM_GROUP_ID) FROM ITEM_GROUP WHERE NAME='Ungrouped' AND crf_id = " + crfId +
                                    " ),'" +
                                    "" + "', '" +
                                    "" + "', '" +
                                    "" + "', " +
                                    1 + ", " +
                                    1 + ", '', 1," +
                                    versionIdString + "," +
                                    "(SELECT MAX(ITEM_ID) FROM ITEM WHERE NAME='" + itemName +
                                    "' AND owner_id = " + ub.getId() + " )," +
                                    k + ",0)";

                        } else {
                            sqlGroupLabel = "INSERT INTO ITEM_GROUP_METADATA (" +
                                    "item_group_id,HEADER," +
                                    "subheader, layout, repeat_number, repeat_max," +
                                    " repeat_array,row_start_number, crf_version_id," +
                                    "item_id , ordinal, borders) VALUES (" +
                                    "(SELECT ITEM_GROUP_ID FROM ITEM_GROUP WHERE NAME='Ungrouped' AND crf_id = " + crfId + " ORDER BY OID DESC LIMIT 1),'" +
                                    "" + "', '" +
                                    "" + "', '" +
                                    "" + "', " +
                                    1 + ", " +
                                    1 + ", '', 1," +
                                    versionIdString + "," +
                                    "(SELECT ITEM_ID FROM ITEM WHERE NAME='" + itemName +
                                    "' AND owner_id = " + ub.getId() + " ORDER BY OID DESC LIMIT 1)," +
                                    k + ",0)";
                        }

                        queries.add(sqlGroupLabel);
                    }
                } else if (sheetName.equalsIgnoreCase("Sections")) {
                    logger.info("read sections");

                    //multiple rows, six cells, last one is number
                    for (int k = 1; k < numRows; k++) {
                        if (blankRowCount == 5) {
                            System.out.println("hit end of the row ");
                            break;
                        }
                        if (sheet.getRow(k) == null) {
                            blankRowCount++;
                            continue;
                        }
                        HSSFCell cell = sheet.getRow(k).getCell((short) 0);
                        String secLabel = getValue(cell);
                        if (StringUtil.isBlank(secLabel)) {
                            errors
                                    .add("The SECTION_LABEL column was blank at row " + k + ", Sections worksheet.");
                            htmlErrors.put(j + "," + k + ",0", "REQUIRED FIELD");
                        }
                        if (secNames.contains(secLabel)) {
                            errors.add("The SECTION_LABEL column was a duplicate of " + secLabel + " at row " + k
                                    + ", sections worksheet.");
                            htmlErrors.put(j + "," + k + ",0", "DUPLICATE FIELD");
                        }
                        //logger.info("section name:" + secLabel + "row num:" +k);
                        secNames.add(secLabel);
                        cell = sheet.getRow(k).getCell((short) 1);
                        String title = getValue(cell);
                        if (StringUtil.isBlank(title)) {
                            errors
                                    .add("The SECTION_TITLE column was blank at row " + k + ", Sections worksheet.");
                            htmlErrors.put(j + "," + k + ",1", "REQUIRED FIELD");
                        }

                        cell = sheet.getRow(k).getCell((short) 2);
                        String subtitle = getValue(cell);
                        cell = sheet.getRow(k).getCell((short) 3);
                        String instructions = getValue(cell);
                        cell = sheet.getRow(k).getCell((short) 4);
                        String pageNumber = getValue(cell);

                        cell = sheet.getRow(k).getCell((short) 5);
                        String parentSection = getValue(cell);
                        if (!StringUtil.isBlank(parentSection)) {
                            try {
                                parentId = Integer.parseInt(parentSection);
                            } catch (NumberFormatException ne) {
                                parentId = 0;
                            }
                        }
                        String sql = "";
                        if (dbName.equals("oracle")) {
                            sql = "INSERT INTO SECTION (CRF_VERSION_ID,"
                                    + "STATUS_ID,LABEL, TITLE, INSTRUCTIONS, SUBTITLE, PAGE_NUMBER_LABEL,"
                                    + "ORDINAL, PARENT_ID, OWNER_ID, DATE_CREATED) " + "VALUES (" + versionIdString
                                    + ",1,'" + secLabel + "','" + stripQuotes(title) + "', '"
                                    + stripQuotes(instructions) + "', '" + stripQuotes(subtitle) + "','" + pageNumber
                                    + "'," + k + "," + parentId + "," + ub.getId() + ",sysdate)";
                        } else {
                            sql = "INSERT INTO SECTION (CRF_VERSION_ID,"
                                    + "STATUS_ID,LABEL, TITLE, INSTRUCTIONS, SUBTITLE, PAGE_NUMBER_LABEL,"
                                    + "ORDINAL, PARENT_ID, OWNER_ID, DATE_CREATED) " + "VALUES (" + versionIdString
                                    + ",1,'" + secLabel + "','" + stripQuotes(title) + "', '"
                                    + stripQuotes(instructions) + "', '" + stripQuotes(subtitle) + "','" + pageNumber
                                    + "'," + k + "," + parentId + "," + ub.getId() + ",NOW())";
                        }
                        queries.add(sql);
                    }//end for loop
                } else if (sheetName.equalsIgnoreCase("CRF")) {
                    logger.info("read crf");
                    //one row, four cells, all strings
                    HSSFCell cell = sheet.getRow(1).getCell((short) 0);
                    crfName = getValue(cell);
                    if (StringUtil.isBlank(crfName)) {
                        errors.add("The CRF_NAME column was blank in the CRF worksheet.");
                        htmlErrors.put(j + ",1,0", "REQUIRED FIELD");
                    }

                    try {
                        CRFBean checkName = (CRFBean) cdao.findByPK(crfId);
                        if (!checkName.getName().equals(crfName)) {
                            logger.info("crf name is mismatch");
                            errors.add("The CRF_NAME column did not match the intended CRF version "
                                    + "you want to upload.  Make sure this reads '" + checkName.getName()
                                    + "' before you continue.");
                            htmlErrors.put(j + ",1,0", "DID NOT MATCH CRF");
                        }
                    } catch (Exception pe) {
                        logger.warning("Exception happened when check CRF name" + pe.getMessage());
                    }

                    cell = sheet.getRow(1).getCell((short) 1);
                    String version = getValue(cell);
                    ncrf.setVersionName(version);
                    //YW, 08-22-2007, since versionName is now obtained from spreadsheet,
                    //	blank check has been moved to CreateCRFVersionServlet.java
                    //    and mismatch check is not necessary
                    //if (StringUtil.isBlank(version)) {
                    //  errors.add("The VERSION column was blank in the CRF worksheet.");
                    //  htmlErrors.put(j + ",1,1", "REQUIRED FIELD");
                    //}else if (!version.equals(versionName)) {
                    //  errors.add("The VERSION column did not match the intended version name "
                    //      + "you want to upload. Make sure this reads '" + versionName
                    //      + "' before you continue.");
                    //  htmlErrors.put(j + ",1,1", "DID NOT MATCH VERSION");
                    //}

                    cell = sheet.getRow(1).getCell((short) 2);
                    String versionDesc = getValue(cell);

                    cell = sheet.getRow(1).getCell((short) 3);
                    String revisionNotes = getValue(cell);
                    if (StringUtil.isBlank(revisionNotes)) {
                        errors.add("The REVISION_NOTES column was blank in the CRF worksheet.");
                        htmlErrors.put(j + ",1,3", "REQUIRED FIELD");
                    }
                    //check for instrument existence here??? tbh 7/28
                    //engaging in new validation, tbh, 6-4-04
                    //modify nib.getinstversions to look for version name and description
                    //need to stop uploads of same name-description pairs

                    HashMap checkCRFVersions = ncrf.getCrfVersions();

                    //this now returns a hash map of key:version_name
                    // ->value:version_description
                    boolean overwrite = false;

                    if (checkCRFVersions.containsKey(version)) {
                        logger.info("found a matching version name..." + version);
                        errors.add("The VERSION column is not unique.  This can cause confusion in "
                                + "selecting the correct CRF. Please make sure you change the "
                                + "version name so that it can be uniquely identified by users in the system. "
                                + "Otherwise, the previous same version will be deleted from database.");
                        htmlErrors.put(j + ",1,2", "NOT UNIQUE");

                    }

                    String sql = "";
                    if (dbName.equals("oracle")) {
                        logger.warning("TEST 2");
                        if (crfId == 0) {
                            sql = "INSERT INTO CRF_VERSION (NAME, DESCRIPTION, CRF_ID, STATUS_ID,DATE_CREATED,"
                                    + "OWNER_ID,REVISION_NOTES) " + "VALUES ('" + stripQuotes(version) + "','"
                                    + stripQuotes(versionDesc) + "'," + "(SELECT CRF_ID FROM CRF WHERE NAME='"
                                    + crfName + "'),1,sysdate," + ub.getId() + ",'" + stripQuotes(revisionNotes) + "')";
                        } else {
                            sql = "INSERT INTO CRF_VERSION (NAME,DESCRIPTION, CRF_ID, STATUS_ID,DATE_CREATED,"
                                    + "OWNER_ID,REVISION_NOTES) " + "VALUES ('" + version + "','"
                                    + stripQuotes(versionDesc) + "'," + crfId + ",1,sysdate," + ub.getId() + ",'"
                                    + stripQuotes(revisionNotes) + "')";
                        }
                    } else {
                        if (crfId == 0) {
                            sql = "INSERT INTO CRF_VERSION (NAME, DESCRIPTION, CRF_ID, STATUS_ID,DATE_CREATED,"
                                    + "OWNER_ID,REVISION_NOTES) " + "VALUES ('" + stripQuotes(version) + "','"
                                    + stripQuotes(versionDesc) + "'," + "(SELECT CRF_ID FROM CRF WHERE NAME='"
                                    + crfName + "'),1,NOW()," + ub.getId() + ",'" + stripQuotes(revisionNotes) + "')";
                        } else {
                            sql = "INSERT INTO CRF_VERSION (NAME,DESCRIPTION, CRF_ID, STATUS_ID,DATE_CREATED,"
                                    + "OWNER_ID,REVISION_NOTES) " + "VALUES ('" + version + "','"
                                    + stripQuotes(versionDesc) + "'," + crfId + ",1,NOW()," + ub.getId() + ",'"
                                    + stripQuotes(revisionNotes) + "')";
                        }
                    }
                    queries.add(sql);
                    pVersion = version;
                    pVerDesc = versionDesc;
                }

                versionIdString = "(SELECT CRF_VERSION_ID FROM CRF_VERSION WHERE NAME ='" + pVersion
                        + "' AND CRF_ID=" + "(SELECT CRF_ID FROM CRF WHERE NAME ='" + stripQuotes(crfName) + "')" + ")";

                //move html creation to here, include error creation as well, tbh 7/28
                buf.append(sheetName + "<br>");
                buf.append("<div class=\"box_T\"><div class=\"box_L\"><div class=\"box_R\"><div class=\"box_B\"><div class=\"box_TL\"><div class=\"box_TR\"><div class=\"box_BL\"><div class=\"box_BR\">");

                buf.append("<div class=\"textbox_center\">");
                buf.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"");
                buf.append("caption=\"" + wb.getSheetName(j) + "\"" + ">");

                for (int i = 0; i < numRows; i++) {
                    buf.append("<tr>");

                    if (sheet.getRow(i) == null) {
                        continue;
                    }

                    int numCells = (int) sheet.getRow(i).getLastCellNum();

                    for (int y = 0; y < numCells; y++) {
                        HSSFCell cell = sheet.getRow(i).getCell((short) y);
                        int cellType = 0;
                        String error = "&nbsp;";
                        String errorKey = j + "," + i + "," + y;
                        if (htmlErrors.containsKey(errorKey)) {
                            error = "<span class=\"alert\">" + htmlErrors.get(errorKey) + "</span>";
                        }
                        if (cell == null) {
                            cellType = HSSFCell.CELL_TYPE_BLANK;
                        } else {
                            cellType = cell.getCellType();
                        }
                        switch (cellType) {
                            case HSSFCell.CELL_TYPE_BLANK:
                                buf.append("<td class=\"table_cell\">" + error + "</td>");
                                break;
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                buf.append("<td class=\"table_cell\">" + cell.getNumericCellValue() + " " + error
                                        + "</td>");
                                break;
                            case HSSFCell.CELL_TYPE_STRING:
                                buf.append("<td class=\"table_cell\">" + cell.getStringCellValue() + " " + error
                                        + "</td>");
                                break;
                            default:
                                buf.append("<td class=\"table_cell\">" + error + "</td>");
                        }
                    }
                    buf.append("</tr>");
                }
                buf.append("</table>");
                buf.append("<br></div>");
                buf.append("</div></div></div></div></div></div></div></div>");
                buf.append("</div><br>");
            }//end of the else sheet loop

        }//end of the for loop for sheets
        ncrf.setQueries(queries);
        ncrf.setItemQueries(openQueries);
        ncrf.setBackupItemQueries(backupItemQueries);
        ncrf.setItems(items);
        if (!errors.isEmpty()) {
            ncrf.setErrors(errors);
        }
        //logger.info("html table:" + buf.toString());
        ncrf.setHtmlTable(buf.toString());
        return ncrf;
    }

    /**
     * stripQuotes, utility function meant to replace single quotes in strings
     * with double quotes for SQL compatability. Don't -> Don''t, for example.
     *
     * @param subj the subject line
     * @return A string with all the quotes escaped.
     */
    public String stripQuotes(String subj) {
        if (subj == null) {
            return null;
        }
        String returnme = "";
        String[] subjarray = subj.split("'");
        if (subjarray.length == 1) {
            returnme = subjarray[0];
        } else {
            for (int i = 0; i < (subjarray.length - 1); i++) {
                returnme += subjarray[i];
                returnme += "''";
            }
            returnme += subjarray[subjarray.length - 1];
        }
        return returnme;
    }

    public String getValue(HSSFCell cell) {
        String val = null;
        int cellType = 0;
        if (cell == null) {
            cellType = HSSFCell.CELL_TYPE_BLANK;
        } else {
            cellType = cell.getCellType();
        }

        switch (cellType) {
            case HSSFCell.CELL_TYPE_BLANK:
                val = "";
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                //YW << Modify code so that floating number alone can be used for CRF version. Before it must use, e.g. v1.1
                //      Meanwhile modification has been done for read PHI cell and Required cell
                val = cell.getNumericCellValue() + "";
                logger.info("found a numeric cell: " + val);
                //what if the version is a whole number?  added other code below from PHI, tbh 6/5/07
                //So now we also treat 3, 3.0, 3.00 as same as 3. If users want 3.0 or 3.10 the best way is use String type. -YW
                // >> YW
                double dphi = (double) cell.getNumericCellValue();
                if ((dphi - (int) dphi) * 1000 == 0) {
                    val = (int) dphi + "";
                }
                logger.info("found a numeric cell after transfer: " + val);
                //buf.append("<td><font class=\"bodytext\">" + cell.getNumericCellValue()
                // + "</font></td>");
                break;
            case HSSFCell.CELL_TYPE_STRING:
                val = cell.getStringCellValue();
                if (val.matches("'")) {
                    //System.out.println("Found single quote! "+val);
                    val.replaceAll("'", "''");
                }
                //buf.append("<td><font class=\"bodytext\">" + cell.getStringCellValue()
                // + "</font></td>");
                break;
            default:
                val = "";
                //buf.append("<td></td>");
        }
        //logger.info("final val returned: "+val);
        return val.trim();
    }

    public String toHTML(int sheetIndex) throws IOException {
        StringBuffer buf = new StringBuffer();
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        int numSheets = wb.getNumberOfSheets();
        for (int j = 0; j < numSheets; j++) {
            HSSFSheet sheet = wb.getSheetAt(j);//sheetIndex);
            String sheetName = wb.getSheetName(j);
            buf.append(sheetName + "<br>");
            buf.append("<table border=\"2\"");
            buf.append("caption=\"" + wb.getSheetName(sheetIndex) + "\"" + ">");
            int numCols = sheet.getPhysicalNumberOfRows();

            for (int i = 0; i < numCols; i++) {
                buf.append("<tr>");

                if (sheet.getRow(i) == null) {
                    continue;
                }

                int numCells = (int) sheet.getRow(i).getLastCellNum();

                for (int y = 0; y < numCells; y++) {

                    HSSFCell cell = sheet.getRow(i).getCell((short) y);
                    int cellType = 0;
                    if (cell == null) {
                        cellType = HSSFCell.CELL_TYPE_BLANK;
                    } else {
                        cellType = cell.getCellType();
                    }

                    switch (cellType) {
                        case HSSFCell.CELL_TYPE_BLANK:
                            buf.append("<td> </td>");
                            break;
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            buf.append("<td>" + cell.getNumericCellValue() + "</td>");
                            break;
                        case HSSFCell.CELL_TYPE_STRING:
                            buf.append("<td>" + cell.getStringCellValue() + "</td>");
                            break;
                        default:
                            buf.append("<td></td>");
                    }
                }
                buf.append("</tr>");
            }

            buf.append("</table>");
        }//end of sheet count, added by tbh 5-31
        return buf.toString();
    }


}

