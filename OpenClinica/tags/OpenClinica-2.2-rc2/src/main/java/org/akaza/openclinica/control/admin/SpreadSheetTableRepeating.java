/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * <P>
 * Returns multiple types of things based on the parsing; returns
 * html table returns data objects as SQL strings.
 * <P>
 * The most important method here is the toNIB() method, which returns the
 * NewInstrumentBean which in turn creates a new instrument version in the
 * database.
 * 
 * 
 * @author thickerson with help from Brian Gilman @ the Whitehead Institute
 *         modified by jxu
 * @version CVS: $Id: SpreadSheetTableRepeating.java 10382 2007-11-21 21:47:56Z ywang $
 */

public class SpreadSheetTableRepeating implements SpreadSheetTable {

  private POIFSFileSystem fs = null;

  private UserAccountBean ub = null;

  private String versionName = null;
 
  private int crfId = 0;

  private String crfName = "";

  private String versionIdString = "";
  
  private boolean isRepeating = false; 
  
  private HashMap itemGroups = new HashMap();
  
  private HashMap itemsToGrouplabels = new HashMap();

  //the default; all crf ids should be > 0, tbh 8-29 :-)
  protected final Logger logger = Logger.getLogger(getClass().getName());

  public SpreadSheetTableRepeating(FileInputStream parseStream,
                                   UserAccountBean ub, 
      String versionName)
      throws IOException {
	  //super();

    this.fs = new POIFSFileSystem(parseStream);
    this.ub = ub;
    this.versionName = versionName;
    HSSFWorkbook wb = new HSSFWorkbook(fs);
    int numSheets = wb.getNumberOfSheets();
    for (int j = 0; j < numSheets; j++) {
        HSSFSheet sheet = wb.getSheetAt(j);//sheetIndex);
        String sheetName = wb.getSheetName(j);
        if (sheetName.equalsIgnoreCase("groups")) {
        	isRepeating = true;
        }
        //*** now we've set it up so that we can switch back to classic, tbh, 06/07
    }
	  //should be set in the super(), tbh 05/2007
  }

  public void setCrfId(int id) {
    this.crfId = id;
  }

  public int getCrfId() {
    return this.crfId;
  }

  public NewCRFBean toNewCRF(javax.sql.DataSource ds) throws IOException {
    NewCRFBean ncrf = new NewCRFBean(ds,crfId);

    ncrf.setCrfId(crfId);//set crf id

    StringBuffer buf = new StringBuffer();
    HSSFWorkbook wb = new HSSFWorkbook(fs);
    int numSheets = wb.getNumberOfSheets();
    ArrayList queries = new ArrayList();
    //ArrayList groupItemMapQueries = new ArrayList();
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
    CRFBean crf = (CRFBean)cdao.findByPK(crfId);
    
    ItemDataDAO iddao = new ItemDataDAO(ds);
    ItemDAO idao = new ItemDAO(ds);
    CRFVersionDAO cvdao = new CRFVersionDAO(ds);
    
    int validSheetNum = 0;
    for (int j = 0; j < numSheets; j++) {
      HSSFSheet sheet = wb.getSheetAt(j);//sheetIndex);
      String sheetName = wb.getSheetName(j);
      if (sheetName.equalsIgnoreCase("CRF") || sheetName.equalsIgnoreCase("Sections") 
          ||sheetName.equalsIgnoreCase("Items") ) {
        validSheetNum ++;
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
        //great minds apparently think alike...tbh, commented out 06/19/2007
        //System.out.println("LastRowNum()" + sheet.getLastRowNum());
        String secName = "";
        String page = "";
        //YW << for holding "responseLabel_responseType"
        ArrayList resPairs = new ArrayList();
        //YW >>
        ArrayList resNames = new ArrayList();//records all the response_labels
        HashMap htmlErrors = new HashMap();

        //the above two need to persist across mult. queries,
        //and they should be created FIRST anyway, since instrument is first
        //also need to add to VERSIONING_MAP, tbh, 6-6-3
        
        //try to count how many blank rows, if 5 concective blank rows found, stop reading
        int blankRowCount =0;
        
        if (sheetName.equalsIgnoreCase("Items")) {
          logger.info("read an item in sheet" + sheetName);
         
          for (int k = 1; k < numRows; k++) {
            //System.out.println("hit row "+k);
            if (blankRowCount ==5 ) {
              System.out.println("hit end of the row ");
              break;
            }
            if (sheet.getRow(k) == null ) {
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
            //*******************************************
            //group_label will go here, tbh in place 6
            //have to advance all the rest by one at least (if there are 
            //no other columns) tbh, 5-14-2007
            
            cell = sheet.getRow(k).getCell((short) 6);
            String groupLabel = getValue(cell);
            
            cell = sheet.getRow(k).getCell((short) 7);
            String header = getValue(cell);

            cell = sheet.getRow(k).getCell((short) 8);
            String subHeader = getValue(cell);

            cell = sheet.getRow(k).getCell((short) 9);
            String parentItem = getValue(cell);
            //BWP>>Prevent parent names that equal the Item names
            if(itemName != null &&
              itemName.equalsIgnoreCase(parentItem)) {
                parentItem="";
            }
            cell = sheet.getRow(k).getCell((short) 10);
            int columnNum = 0;
            String column = getValue(cell);
            if (!StringUtil.isBlank(column)) {
              try {
                columnNum = Integer.parseInt(column);
              } catch (NumberFormatException ne) {
                columnNum = 0;
              }
            }
          
            cell = sheet.getRow(k).getCell((short) 11);
            if (cell != null) {
              page = getValue(cell);
            }

            cell = sheet.getRow(k).getCell((short) 12);
            String questionNum = getValue(cell);

            cell = sheet.getRow(k).getCell((short) 13);
            String responseType = getValue(cell);
            int responseTypeId = 1;
            if (StringUtil.isBlank(responseType)) {
              errors.add("The RESPONSE_TYPE column was blank at row " + k + ", items worksheet.");
              htmlErrors.put(j + "," + k + ",13", "REQUIRED FIELD");

            } else {
              if (!ResponseType.findByName(responseType.toLowerCase())) {
                errors.add("The RESPONSE_TYPE column was invalid at row " + k
                    + ", items worksheet.");
                htmlErrors.put(j + "," + k + ",13", "INVALID FIELD");
              } else {
                responseTypeId = (ResponseType.getByName(responseType.toLowerCase())).getId();
              }
            }

            cell = sheet.getRow(k).getCell((short) 14);
            String responseLabel = getValue(cell);
            if (StringUtil.isBlank(responseLabel)) {
              errors.add("The RESPONSE_LABEL column was blank at row " + k + ", items worksheet.");
              htmlErrors.put(j + "," + k + ",14", "REQUIRED FIELD");
            }
            cell = sheet.getRow(k).getCell((short) 15);
            String resOptions = getValue(cell);
            if (responseLabel.equalsIgnoreCase("text") ||responseLabel.equalsIgnoreCase("textarea")){
              resOptions="text";
            }
            int numberOfOptions = 0;
            if (!resNames.contains(responseLabel) && (StringUtil.isBlank(resOptions))) {
              errors.add("The RESPONSE_OPTIONS_TEXT column was blank at row " + k
                  + ", Items worksheet.");
              htmlErrors.put(j + "," + k + ",15", "REQUIRED FIELD");
            }
            if (!resNames.contains(responseLabel) && (!StringUtil.isBlank(resOptions))) {
              //String[] resArray = resOptions.split(",");
              String text1 = resOptions.replaceAll("\\\\,", "##" );
              String[] resArray = text1.split(",");
              numberOfOptions = resArray.length;
            }
            cell = sheet.getRow(k).getCell((short) 16);
            String resValues = getValue(cell);
            if (responseLabel.equalsIgnoreCase("text") ||responseLabel.equalsIgnoreCase("textarea")){
              resValues="text";
            }
            if (!resNames.contains(responseLabel) && (StringUtil.isBlank(resValues))) {
              errors.add("The RESPONSE_VALUES column was blank at row " + k + ", Items worksheet.");
              htmlErrors.put(j + "," + k + ",16", "REQUIRED FIELD");
            }
            if (numberOfOptions > 0) {
              String value1 = resValues.replaceAll("\\\\,", "##" );
              String[] resValArray = value1.split(",");
              if (resValArray.length != numberOfOptions) {
                errors.add("There are an incomplete number of option-value pairs in "
                    + "RESPONSE_OPTIONS and RESPONSE_VALUES at row " + k
                    + ", questions worksheet; perhaps you are missing a comma? If there is a comma in any option text/value itself, please use \\, instead.");
                htmlErrors.put(j + "," + k + ",15", "NUMBER OF OPTIONS DOES NOT MATCH");
                htmlErrors.put(j + "," + k + ",16", "NUMBER OF VALUES DOES NOT MATCH");
              }
            }
            /*
             * Adding two columns here for the repeating rows, REsPONSE_LAYOUT and DEFAULT_VALUE
             * TBH, 06/05/2007
             * 
             * YW 08-02-2007: move default_value down after data_type
             * */

            //RESPONSE_LAYOUT
            cell = sheet.getRow(k).getCell((short) 17);
            //should be horizontal or vertical, tbh
            //BWP: the application will assume a vertical layout if
            //this value is not horizontal
             //BWP 08-02-2007 <<
            String responseLayout =getValue(cell);
              //BWP >>
            cell = sheet.getRow(k).getCell((short) 19);
            String dataType = getValue(cell);
            String dataTypeIdString = "1";
            if (StringUtil.isBlank(dataType)) {
              errors.add("The DATA_TYPE column was blank at row " + k + ", items worksheet.");
              htmlErrors.put(j + "," + k + ",19", "REQUIRED FIELD");

            } else {
              if (!ItemDataType.findByName(dataType.toLowerCase())) {
                errors.add("The DATA_TYPE column was invalid at row " + k + ", Items worksheet.");
                htmlErrors.put(j + "," + k + ",19", "INVALID FIELD");
              } else {
                //dataTypeId = (ItemDataType.getByName(dataType)).getId();
                dataTypeIdString = "(SELECT ITEM_DATA_TYPE_ID From ITEM_DATA_TYPE Where CODE='"
                    + dataType.toUpperCase() + "')";
              }
            }
            
            //DEFAULT_VALUE
            //can be anything, tbh
            //
            //YW 08-02-2007 << in database, default_value has been set type as varchar(255);
            // outside database, it's going to be tied with item's DATA_TYPE
            // here, default_value has been handled for dataType = date
            cell = sheet.getRow(k).getCell((short) 18);
            String default_value = getValue(cell);
            if("date".equalsIgnoreCase(dataType) && !"".equals(default_value)){
               //BWP>> try block needs to be added, because cell.getDateCellValue()
              //can throw an exception
              try {
                default_value = new SimpleDateFormat("MM/dd/yyyy").
                  format(cell.getDateCellValue());
                //TODO l10n date?
              } catch (Exception e){
                default_value="";
              }
           }
            //YW >>

            cell = sheet.getRow(k).getCell((short) 20);
            String regexp = getValue(cell);
            String regexp1="";
            if (!StringUtil.isBlank(regexp)){
              // parse the string and get reg exp eg. regexp: /[0-9]*/
              regexp1 = regexp.trim();
              if (regexp1.startsWith("regexp:")) {
            	  //YW 10-03-2007
              	  String finalRegexp = regexp1.substring(7).trim();
                  if(finalRegexp.startsWith("/") && finalRegexp.endsWith("/")) {
  	                finalRegexp = finalRegexp.substring(1, finalRegexp.length()-1);
	                try {
	                  Pattern p = Pattern.compile(finalRegexp);
	                  //YW 11-21-2007 << add another \ if there is \ in regexp 
	                  char[] chars = regexp1.toCharArray();
	                  regexp1 = "";
	                  for(char c : chars) {
	                	  if(c=='\\') {
	      					regexp1 += c + "\\";
	      				} else {
	      					regexp1 += c;
	      				}
	                  }
	                  //YW >>
	                }catch (PatternSyntaxException pse) {
	                  errors.add("The VALIDATION column has an invalid regular expression at row " + k
	                      + ", Items worksheet. Example: regexp: /[0-9]*/ ");
	                  htmlErrors.put(j + "," + k + ",20", "INVALID FIELD");
	                }
                } else {
                	errors.add("The VALIDATION column has an invalid regular expression at row " + k
  	                      + ", Items worksheet. Example: regexp: /[0-9]*/ ");
  	                  htmlErrors.put(j + "," + k + ",20", "INVALID FIELD");
                }
              } else if (regexp1.startsWith("func:")) {
                boolean isProperFunction = false;
                try {
                  Validator.processCRFValidationFunction(regexp1);
                  isProperFunction = true;
                }
                catch (Exception e) {
                  errors.add(e.getMessage() + ", at row " + k
                      + ", Items worksheet." );
                  htmlErrors.put(j + "," + k + ",20", "INVALID FIELD");
                }
              } else {
                errors.add("The VALIDATION column was invalid at row " + k
                    + ", Items worksheet. ");
                htmlErrors.put(j + "," + k + ",20", "INVALID FIELD");
              }


             
            }

            cell = sheet.getRow(k).getCell((short) 21);
            String regexpError = getValue(cell);
            if (!StringUtil.isBlank(regexp) && StringUtil.isBlank(regexpError)) {
              errors.add("The VALIDATION_ERROR_MESSAGE column was blank at row " + k
                  + ", Items worksheet. It cannot be blank if VALIDATION is not blank.");
              htmlErrors.put(j + "," + k + ",21", "REQUIRED FIELD");
            }

            boolean phiBoolean = false;
            cell = sheet.getRow(k).getCell((short) 22);
            String phi = getValue(cell);
            //String phi = "";
            //logger.info("++ phi: "+getValue(cell));
            if (StringUtil.isBlank(phi)) {
            	phi = "0";
            } else
            //throws NPE, so added the guard clause above, tbh 06/07
            if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC) {
            	double dphi = (double) cell.getNumericCellValue();
            	if( (dphi - (int)dphi)*1000 == 0 ) {
            		phi = (int)dphi + "";
            	}
            }
            if (!"0".equals(phi) && !"1".equals(phi)) {
              errors.add("The PHI column was invalid at row " + k
                  + ", Items worksheet. PHI can only be either 0 or 1.");
              htmlErrors.put(j + "," + k + ",22", "INVALID VALUE");
            } else {
              phiBoolean = ("1".equals(phi)) ? true : false;
            }
            
            boolean isRequired = false;
            cell = sheet.getRow(k).getCell((short) 23);
            String required = getValue(cell);
            //String required = "";
            //added to stop NPEs, tbh 06/04/2007
            if(StringUtil.isBlank(required)){
                required="0";
            } else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC) {
            	double dr = (double) cell.getNumericCellValue();
            	if( (dr - (int)dr)*1000 == 0 ) {
            		required = (int)dr + "";
            	}
            }
            
            if (!"0".equals(required) && !"1".equals(required)) {
              errors.add("The REQUIRED column was invalid at row " + k
                  + ", Items worksheet. REQUIRED can only be either 0 or 1.  ");
              htmlErrors.put(j + "," + k + ",23", "INVALID VALUE");
            } else {
              isRequired = ("1".equals(required)) ? true : false;
            }

            //better spot for checking item might be right here, tbh 7-25
            String vlSql = "INSERT INTO ITEM (NAME,DESCRIPTION,UNITS,PHI_STATUS,"
                + "ITEM_DATA_TYPE_ID, ITEM_REFERENCE_TYPE_ID,STATUS_ID,OWNER_ID,DATE_CREATED) "
                + "VALUES ('" + stripQuotes(itemName) + "','" + stripQuotes(descLabel) + "','"
                + stripQuotes(unit) + "'," + phiBoolean + "," + dataTypeIdString + ",1,1,"
                + ub.getId() + ", NOW())";

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
            String resOptions1 = resOptions.replaceAll("\\\\,","\\,");
            String resValues1 = resValues.replaceAll("\\\\,", "\\,");
            rsb.setOptions(stripQuotes(resOptions1),stripQuotes(resValues1));            
            
            ItemFormMetadataBean ifmb = new ItemFormMetadataBean();
            ifmb.setResponseSet(rsb);
            ib.setItemMeta(ifmb);
            items.put(itemName, ib);
            
            int ownerId = ub.getId();
            
            if (!itemCheck.containsKey(itemName)) {// item not in the DB           
                openQueries.put(itemName, vlSql);            

            } else {// item in the DB
              ItemBean oldItem = (ItemBean) idao.findByNameAndCRFId(itemName,crfId);
              if (oldItem.getOwnerId()==ub.getId()) {// owner can update               
                if (!cvdao.hasItemData(oldItem.getId())) {// no item data
                  String upSql ="UPDATE ITEM SET DESCRIPTION='" + stripQuotes(descLabel) + "'," +
                               "UNITS='" + stripQuotes(unit) + "'," +
                               "PHI_STATUS=" + phiBoolean + "," +
                               "ITEM_DATA_TYPE_ID=" + dataTypeIdString +
                               //added by jxu 08-29-06 to fix the missing from clause bug
                               " FROM versioning_map, crf_version" +
                               " WHERE item.name='" + stripQuotes(itemName)
                                 + "' AND item.owner_id = " + ownerId
                                 + " AND item.item_id = versioning_map.item_id AND"
                                 + " versioning_map.crf_version_id = crf_version.crf_version_id" 
                                 + " AND crf_version.crf_id = " + crfId;   
                  openQueries.put(itemName, upSql);
                } else {
                  String upSql ="UPDATE ITEM SET DESCRIPTION='" + stripQuotes(descLabel) + "'," +                 
                                "PHI_STATUS=" + phiBoolean +
//                              //added by jxu 08-29-06 to fix the missing from clause bug
                                " FROM versioning_map, crf_version" +
                                " WHERE item.name='" + stripQuotes(itemName)
                                + "' AND item.owner_id = " + ownerId
                                + " AND item.item_id = versioning_map.item_id AND"
                                + " versioning_map.crf_version_id = crf_version.crf_version_id" 
                                + " AND crf_version.crf_id = " + crfId; 
                  openQueries.put(itemName, upSql);
                }
              }
              else {
                ownerId = oldItem.getOwner().getId();
              }
            }
            String sql = "INSERT INTO RESPONSE_SET (LABEL, OPTIONS_TEXT, OPTIONS_VALUES, "
                + "RESPONSE_TYPE_ID, VERSION_ID)" + " VALUES ('" + stripQuotes(responseLabel)
                + "', '" + stripQuotes(resOptions) + "','" + stripQuotes(resValues) + "',"
                + "(SELECT RESPONSE_TYPE_ID From RESPONSE_TYPE Where NAME='"
                + stripQuotes(responseType.toLowerCase()) + "')," + versionIdString + ")";
            //YW << a response Label can not be used for more than one response type
            if (!resPairs.contains(responseLabel.toString().toLowerCase() + "_" + responseType.toString().toLowerCase())) {
            //YW >>	
            	if(!resNames.contains(responseLabel)) {
            		queries.add(sql);
            		resNames.add(responseLabel);
            	}
	              //this will have to change since we have some data in the actual
	              // spreadsheet
	              //change it to caching response set names in a collection?
	              //or just delete the offending cells from the spreadsheet?
	              
            	//YW <<
	             else {
	             errors.add("Error found at row \"" + (k+1) + "\" in items worksheet. ResponseLabel \"" + responseLabel +  
	      					"\" for ResponseType \"" + responseType + "\" has been used for another ResponseType.  ");
	                  htmlErrors.put(j + "," + k + ",14", "INVALID FIELD");
	            }
	            resPairs.add(responseLabel.toString().toLowerCase() + "_" + responseType.toString().toLowerCase());
	            //YW >>
            }
            
            String parentItemString = "0";
            if (!StringUtil.isBlank(parentItem)) {
              parentItemString = "(SELECT ITEM_ID FROM ITEM WHERE NAME='" + stripQuotes(parentItem)
                  + "' AND owner_id = " + ownerId + " ORDER BY OID DESC LIMIT 1)";
            }

            String sql2 = "INSERT INTO ITEM_FORM_METADATA (CRF_VERSION_ID, RESPONSE_SET_ID,"
                + "ITEM_ID,SUBHEADER,HEADER,LEFT_ITEM_TEXT,"
                + "RIGHT_ITEM_TEXT,PARENT_ID,SECTION_ID,ORDINAL,PARENT_LABEL,COLUMN_NUMBER,PAGE_NUMBER_LABEL,question_number_label,"
                + "REGEXP,REGEXP_ERROR_MSG,REQUIRED,DEFAULT_VALUE,RESPONSE_LAYOUT)" + " VALUES (" + versionIdString
                + ",(SELECT RESPONSE_SET_ID FROM RESPONSE_SET WHERE LABEL='"
                + stripQuotes(responseLabel) + "'" + " AND VERSION_ID=" + versionIdString
                + "),(SELECT ITEM_ID FROM ITEM WHERE NAME='" + itemName 
                + "' AND owner_id = " + ownerId + " ORDER BY OID DESC LIMIT 1),'"
                + stripQuotes(subHeader) + "','" + stripQuotes(header) + "','"
                + stripQuotes(leftItemText) + "','" + stripQuotes(rightItemText) + "',"
                + parentItemString + ", (SELECT SECTION_ID FROM SECTION WHERE LABEL='" + secName
                + "' AND " + "CRF_VERSION_ID IN " + versionIdString + "), " + k + ",'" + parentItem
                + "'," + columnNum + ",'" + stripQuotes(page) + "','" + stripQuotes(questionNum) + "','" 
                + stripQuotes(regexp1) + "','" + stripQuotes(regexpError) +  "', " + isRequired 
                + ", '" + stripQuotes(default_value) + "','" + stripQuotes(responseLayout) + "'" + ")";

            queries.add(sql2);
            //link version with items now
            String sql3 = "INSERT INTO VERSIONING_MAP (CRF_VERSION_ID, ITEM_ID) VALUES ( "
                + versionIdString + "," + "(SELECT ITEM_ID FROM ITEM WHERE NAME='" + itemName
                + "' AND owner_id = " + ownerId + " ORDER BY OID DESC LIMIT 1))";
            queries.add(sql3);
            
//            if (!StringUtil.isBlank(groupLabel)) {
//            	//add the item and the group label together
//            	//so that we can extract them
//            	//later down the road, tbh
//            	itemsToGrouplabels.put(itemName,groupLabel);
//            }
            if (!StringUtil.isBlank(groupLabel)) {
            	ItemGroupBean itemGroup;
            	ItemGroupMetadataBean igMeta;
            	
            	igMeta = new ItemGroupMetadataBean();
            	itemGroup = new ItemGroupBean();
            	
				try {
					logger.info("found "+groupLabel);
					itemGroup= (ItemGroupBean)itemGroups.get(groupLabel);
					logger.info("*** Found "+
							groupLabel+
							" and matched with "+
							itemGroup.getName());
					
					//if(itemGroup != null){
					igMeta = itemGroup.getMeta();
					//}   else {
						//itemGroup = new ItemGroupBean();
					//}
					
					if(igMeta == null) { igMeta  = new ItemGroupMetadataBean();}
				
            	//above throws Nullpointer, need to change so that it does not, tbh 07-08-07
					
					String sqlGroupLabel = "INSERT INTO ITEM_GROUP_METADATA ("+                 
						"item_group_id,header," +
						"subheader, layout, repeat_number, repeat_max," +
						" repeat_array,row_start_number, crf_version_id," +
						"item_id , ordinal) VALUES (" +
						"(SELECT ITEM_GROUP_ID FROM ITEM_GROUP WHERE NAME='" + itemGroup.getName() 
						+ "' AND crf_id = " + crfId + " ORDER BY OID DESC LIMIT 1),'"+
						igMeta.getHeader()+"', '" +
						igMeta.getSubheader()+ "', '" +
						//above removed?
						igMeta.getLayout()+ "', " +
						//above removed?
						igMeta.getRepeatNum()+", " +
						igMeta.getRepeatMax()+", '" +
						igMeta.getRepeatArray()+"', " +
						//above removed?
						igMeta.getRowStartNumber()+ "," +
						versionIdString + "," +
						"(SELECT ITEM_ID FROM ITEM WHERE NAME='" + itemName +
						"' AND owner_id = " + ub.getId() + " ORDER BY OID DESC LIMIT 1)," +
						k+")";
					
					queries.add(sqlGroupLabel);              
				} catch (NullPointerException e) {
					// Auto-generated catch block, added tbh 102007
					e.printStackTrace();
					errors.add("Error found at row \"" + (k+1) + 
							"\" in the Items worksheet. GROUP_LABEL \"" + groupLabel +  
	      					"\" does not exist in the Groups spreadsheet.  "+
	      					"Please add it to the Groups spreadsheet, or "+
	      					"change it to a Group that exists.  ");
	                  htmlErrors.put(j + "," + k + ",6", "GROUP DOES NOT EXIST");
				}
            } else {
            	
            	//this item doesn't have group, so put it into 'Ungrouped' group
            	String sqlGroupLabel = "INSERT INTO ITEM_GROUP_METADATA ("+                 
            	"item_group_id,header," +
            	"subheader, layout, repeat_number, repeat_max," +
            	" repeat_array,row_start_number, crf_version_id," +
            	"item_id , ordinal) VALUES (" +
            	"(SELECT ITEM_GROUP_ID FROM ITEM_GROUP WHERE NAME='Ungrouped' AND crf_id = " + crfId + " ORDER BY OID DESC LIMIT 1),'"+
            	"" +"', '" +
            	""+ "', '" +
            	"" + "', " +
            	1 + ", " +
            	1 + ", '', 1," +
            	versionIdString + "," +
            	"(SELECT ITEM_ID FROM ITEM WHERE NAME='" + itemName +
            	"' AND owner_id = " + ub.getId() + " ORDER BY OID DESC LIMIT 1)," + 
            	k + ")";
            	
            	queries.add(sqlGroupLabel);            
            	
            	
            }
            
          }//end of very long for loop, tbh
          
          //**************************************
          // above this is where we will add the first sql query for group names
          // will have to be put in a seperate list
          // and added at the end so that we insure
          // that all new item_names and group_names have been added, tbh 5/14
          
          //**************************************
          // below is place where we will add the sheet name for Groups
          // tbh, 5/14/2007
          // DONE -- add sql insert queries below
          // TODO review html error creation in table at end of file
          // TODO find out where to add the form group beans
          // TODO find out where to add the map beans
          
          // we need to make sure groups sql are executed first, because item_group_id is
          //used when we insert item group meta data with item 
        } else if (sheetName.equalsIgnoreCase("Groups")) {
        	logger.info("read groups, ***comment added 5.14.07");
        	ArrayList groupNames = new ArrayList();
        	// create a group - item relationship with this table? hmm
        	
        	// they are in order: group_label, group_layout, group_header,
        	// group_sub_header, group_repeat_number, group_repeat_max, group_repeat_array
        	// so: seven rows
            //let's insert the default group first                
            ItemGroupBean defaultGroup = new ItemGroupBean();
            defaultGroup.setName("Ungrouped");
            defaultGroup.setCrfId(crfId);
            defaultGroup.setStatus(Status.AVAILABLE);
            
            String defaultSql = "INSERT INTO ITEM_GROUP ( " +
            "name, crf_id, status_id, date_created ,owner_id)" + 
            "VALUES ('" + defaultGroup.getName() +"', " + defaultGroup.getCrfId() + "," 
            + defaultGroup.getStatus().getId() + "," + "now()," + ub.getId()+ ")";                               
      
                        
             if (!GroupCheck.containsKey("Ungrouped")) {        
                queries.add(defaultSql); 
             } 
        	for (int gk = 1; gk < numRows; gk++) {
        		if (blankRowCount ==5 ) {
                    logger.info("hit end of the row ");
                    break;
                  }
                  if (sheet.getRow(gk) == null) {
                    blankRowCount++;
                    continue;
                  }
                  HSSFCell cell = sheet.getRow(gk).getCell((short) 0);
                  String groupLabel = getValue(cell);
                  if (StringUtil.isBlank(groupLabel)) {
                      errors
                          .add("The GROUP_LABEL column was blank at row " + gk + ", Groups worksheet.");
                      htmlErrors.put(j + "," + gk + ",0", "REQUIRED FIELD");
                    }
                  //must these be unique?  probably so, tbh
                  if (groupNames.contains(groupLabel)) {
                      errors.add("The GROUP_LABEL column was a duplicate of " + 
                    		  groupLabel + " at row " + gk
                          + ", Groups worksheet.");
                      htmlErrors.put(j + "," + gk + ",0", "DUPLICATE FIELD");
                    } else {
                    	groupNames.add(groupLabel);
                    }
                  //removed reference to 'groupLayout' here, tbh 102007
                  
                  cell = sheet.getRow(gk).getCell((short) 1);
                  String groupHeader = getValue(cell);
                  
//                  cell = sheet.getRow(gk).getCell((short) 3);
//                  String groupSubheader = getValue(cell);
                  
                  cell = sheet.getRow(gk).getCell((short) 2);
                  String groupRepeatNumber = getValue(cell);
                  // to be switched to int, tbh
                  // adding clause to convert to int, tbh, 06/07
                  if(StringUtil.isBlank(groupRepeatNumber)){
                      groupRepeatNumber="1";
                  } else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC) {
                  	double dr = (double) cell.getNumericCellValue();
                  	if( (dr - (int)dr)*1000 == 0 ) {
                  		groupRepeatNumber = (int)dr + "";
                  	}
                  } else {
                	  logger.info("found a non-numeric code in a numeric field: groupRepeatNumber");
                  }
                  cell = sheet.getRow(gk).getCell((short) 3);
                  String groupRepeatMax = getValue(cell);
                  // to be switched to int, tbh
                  // adding clause to convert to int, tbh 06/07
                  if(StringUtil.isBlank(groupRepeatMax)){
                      groupRepeatMax="";//problem, tbh
                  } else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC) {
                  	double dr = (double) cell.getNumericCellValue();
                  	if( (dr - (int)dr)*1000 == 0 ) {
                  		groupRepeatMax = (int)dr + "";
                  	}
                  } else {
                	  logger.info("found a non-numeric code in a numeric field: groupRepeatMax");
                  }
//                  cell = sheet.getRow(gk).getCell((short) 6);
//                  String groupRepeatArray = getValue(cell);
                  //below added 06/14/2007, tbh
                 /* BWP>>commented out after removal of borders column
                  cell = sheet.getRow(gk).getCell((short) 7);
                  String groupBorders = getValue(cell);
                  Integer borders = 0;
                  try {
                	  borders = Integer.valueOf(groupBorders);
                	  if (borders.intValue() <0) {
                		  errors.add("The BORDERS column must be a positive integer. " + 
                				  groupBorders + " at row " + gk
                				  + ", Groups worksheet.");
                		  htmlErrors.put(j + "," + gk + ",7", "INVALID FIELD");
                	  }
                  } catch (NumberFormatException ne) {
                	  errors.add("The BORDERS column must be a positive integer. " + 
                			  groupBorders + " at row " + gk
                			  + ", Groups worksheet.");
                	  htmlErrors.put(j + "," + gk + ",7", "INVALID FIELD");
                  }
                  >>*/
                  //above added 06/14/2007, tbh
                  ItemGroupBean fgb = new ItemGroupBean();
                  fgb.setName(groupLabel);
                  fgb.setCrfId(crfId);
                  fgb.setStatus(Status.AVAILABLE);
                  
                  ItemGroupMetadataBean igMeta = new ItemGroupMetadataBean();
                  igMeta.setHeader(groupHeader);                  
                  //igMeta.setLayout(groupLayout);
                  //igMeta.setRepeatArray(groupRepeatArray);
                  try {
                	  igMeta.setRepeatMax(new Integer(
                		  Integer.parseInt(groupRepeatMax)));
                  } catch (NumberFormatException n2) {
                	  n2.printStackTrace();
                	  if ("".equals(groupRepeatMax)) {
                		  igMeta.setRepeatMax(40);
                	  } else {
                	  errors.add("The GROUP_REPEAT_MAX column must be a positive integer. " + 
                			  groupRepeatMax + " at row " + gk
                			  + ", Groups worksheet.");
                	  htmlErrors.put(j + "," + gk + ",3", "INVALID FIELD");
                	  }
                  }
                  try {
                	  igMeta.setRepeatNum(new Integer(
                    		  Integer.parseInt(groupRepeatNumber)));  
                  } catch (NumberFormatException n3) {
                	  n3.printStackTrace();
                	  errors.add("The GROUP_REPEAT_NUM column must be a positive integer, or blank. " + 
                			  groupRepeatNumber + " at row " + gk
                			  + ", Groups worksheet.");
                	  htmlErrors.put(j + "," + gk + ",2", "INVALID FIELD");
                  }
                  
                  //igMeta.setSubheader(groupSubheader);
                  fgb.setMeta(igMeta);
                  
                  //now, we place the form group bean where we can 
                  //generate the sql
                  //and find it again, tbh 5/14/2007
                  
                  //changed to add metadata into item_group_metadata table-jxu
                  String gsql = "INSERT INTO ITEM_GROUP ( " +
                        "name, crf_id, status_id, date_created ,owner_id)" + 
                        "VALUES ('" + fgb.getName() +"', " + fgb.getCrfId() + "," 
                        + fgb.getStatus().getId() + "," + "now()," + ub.getId()+ ")";                               
                  
                  
                  itemGroups.put(fgb.getName(),fgb);
                                 
                  
                  if (!GroupCheck.containsKey(fgb.getName())) {
                    // item  group not in the DB, then insert
                    //otherwise, will use the existing group because group name is unique
                    //and shared within CRF
                    queries.add(gsql);        

                  }
//                  if (!StringUtil.isBlank(groupLabel)) {
//                	  String itemName = (String)itemsToGrouplabels.get(groupLabel);
//                	  logger.info("found "+itemName+" when we passed group label "+groupLabel);
//                  	ItemGroupBean itemGroup = new ItemGroupBean();
//                  	//logger.info("found "+groupLabel);
//                  	itemGroup= (ItemGroupBean)itemGroups.get(groupLabel);
//                  	logger.info("*** Found "+
//                  		  groupLabel+
//                  		  " and matched with "+
//                  		  itemGroup.getName());
//                    igMeta = itemGroup.getMeta();
//                    //above throws Nullpointer, need to change so that it does not, tbh 07-08-07
//                    //moved down here from line 590, tbh
//                    
//                    String sqlGroupLabel = "INSERT INTO ITEM_GROUP_METADATA ("+                 
//                    "item_group_id,header," +
//                    "subheader, layout, repeat_number, repeat_max," +
//                    " repeat_array,row_start_number, crf_version_id," +
//                    "item_id , ordinal, borders) VALUES (" +
//                    "(SELECT ITEM_GROUP_ID FROM ITEM_GROUP WHERE NAME='" + itemGroup.getName() 
//                      + "' AND crf_id = " + crfId + " ORDER BY OID DESC LIMIT 1),'"+
//                    igMeta.getHeader()+"', '" +
//                    igMeta.getSubheader()+ "', '" +
//                    igMeta.getLayout()+ "', " +
//                    igMeta.getRepeatNum()+", " +
//                    igMeta.getRepeatMax()+", '" +
//                    igMeta.getRepeatArray()+"', " +
//                    igMeta.getRowStartNumber()+ "," +
//                    versionIdString + "," +
//                    "(SELECT ITEM_ID FROM ITEM WHERE NAME='" + itemName +
//                    "' AND owner_id = " + ub.getId() + " ORDER BY OID DESC LIMIT 1)," +
//                    igMeta.getOrdinal() + ",'" +
//                    igMeta.getBorders()+
//                    "')";
//                    queries.add(sqlGroupLabel);              
//                 
//                  }
        	}
        } else if (sheetName.equalsIgnoreCase("Sections")) {
          logger.info("read sections");
          
          //multiple rows, six cells, last one is number
          //changed 06/14/2007: seven cells, last on is number, the BORDER, tbh
          for (int k = 1; k < numRows; k++) {
            if (blankRowCount ==5 ) {
              //System.out.println("hit end of the row ");
            	//kludgey way to zero out the rows that can get created in the 
            	//editing process; is there a better way?  tbh 06/2007
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
            //below added 06/2007, tbh
            cell = sheet.getRow(k).getCell((short) 6);
            String strBorder = getValue(cell);
            Integer intBorder = new Integer(0);
            try {
            	intBorder = new Integer(strBorder);
            } catch (NumberFormatException npe) {
            	//let it pass here, tbh 06/18/2007
            }
            //change to sql 06/2007; change to section table in svn?  tbh
            
            String sql = "INSERT INTO SECTION (CRF_VERSION_ID,"
                + "STATUS_ID,LABEL, TITLE, INSTRUCTIONS, SUBTITLE, PAGE_NUMBER_LABEL,"
                + "ORDINAL, PARENT_ID, OWNER_ID, DATE_CREATED) " + "VALUES (" + versionIdString
                + ",1,'" + secLabel + "','" + stripQuotes(title) + "', '"
                + stripQuotes(instructions) + "', '" + stripQuotes(subtitle) + "','" + pageNumber
                + "'," + k + "," + parentId + "," + ub.getId() + ",NOW())";
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
    
    //queries.addAll(groupItemMapQueries);
    
    //added at the end so that items and groups already exist, tbh 5.15.07
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
   * @param subj
   *          the subject line
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
        val = cell.getNumericCellValue()+"";
        // >> YW
        //buf.append("<td><font class=\"bodytext\">" + cell.getNumericCellValue()
        // + "</font></td>");
        //added to check for whole numbers, tbh 6/5/07
        double dphi = (double) cell.getNumericCellValue();
    	if( (dphi - (int)dphi)*1000 == 0 ) {
    		val = (int)dphi + "";
    	}
    	//logger.info("found a numeric cell after transfer: "+val);
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

public boolean isRepeating() {
	return isRepeating;
}

public void setRepeating(boolean isRepeating) {
	this.isRepeating = isRepeating;
}
  
 

}

