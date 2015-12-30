/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.logic.SkipRuleDAO;
import org.akaza.openclinica.dao.logic.TableDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.PublisherDAO;
import org.akaza.openclinica.dao.submit.ResponseSetDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.VersioningMapDAO;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
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
 * @version CVS: $Id: SpreadSheetTable.java,v 1.28 2006/09/01 00:37:19 jxu Exp $
 */

public class SpreadSheetTable {
    public static final String CRF_SHEET = "CRF";

    public static final String SECTIONS_SHEET = "Sections";

    public static final String ITEMS_SHEET = "Items";

    public static final String[] REQUIRED_SHEETS = { CRF_SHEET, SECTIONS_SHEET,
	    ITEMS_SHEET };

    public static final String INSTRUCTIONS_SHEET = "Instructions"; //optional

    public static final String RULES_SHEET = "Rules"; //optional

    public static final String TABLES_SHEET = "Tables"; //optional
    
    public static final String[] OPTIONAL_SHEETS = { INSTRUCTIONS_SHEET, RULES_SHEET, TABLES_SHEET };

    public static final String ERROR_REQUIRED_FIELD = "REQUIRED FIELD";

    public static final String ERROR_DUPLICATE_FIELD = "DUPLICATE FIELD";

    public static final String ERROR_INVALID_LABEL = "NOT A VALID LABEL";

    private POIFSFileSystem fs = null;

    private UserAccountBean ub = null;

    private String versionName = null;

    private int crfId = 0;

    private String crfName = "";

    //the default; all crf ids should be > 0, tbh 8-29 :-)
    protected final Logger logger = Logger.getLogger(getClass().getName());

    public SpreadSheetTable(FileInputStream parseStream, UserAccountBean ub,
	    String versionName) throws IOException {

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
	int ownerId = ub.getId();
	NewCRFBean ncrf = new NewCRFBean(ds, crfId);

	ncrf.setCrfId(crfId);//set crf id

	StringBuffer buf = new StringBuffer();
	HSSFWorkbook wb = new HSSFWorkbook(fs);
	int numSheets = wb.getNumberOfSheets();
	ArrayList queries = new ArrayList();
	ArrayList errors = new ArrayList();
	ArrayList repeats = new ArrayList();
	HashMap items = new HashMap();
	//    String pVersion = "";
	int parentId = 0;
	HashMap itemCheck = ncrf.getItemNames();
	HashMap openQueries = new LinkedHashMap();
	HashMap backupItemQueries = new LinkedHashMap();//save all the item queries if
	// deleting item happens
	ArrayList secNames = new ArrayList(); //check for dupes, also

	CRFDAO cdao = new CRFDAO(ds);

	ItemDAO idao = new ItemDAO(ds);
	CRFVersionDAO cvdao = new CRFVersionDAO(ds);

	int requiredSheetNum = 0;
	int optionalSheetNum = 0;
	for (int i = 0; i < numSheets; i++) {
	    String sheetName = wb.getSheetName(i);
	    for (int g = 0; g < REQUIRED_SHEETS.length; g++) { //count required
		if (sheetName.equalsIgnoreCase(REQUIRED_SHEETS[g])) {
		    requiredSheetNum++;
		    break;
		}
	    }
	    for (int h = 0; h < OPTIONAL_SHEETS.length; h++) { //count optional
		if (sheetName.equalsIgnoreCase(OPTIONAL_SHEETS[h])) {
		    optionalSheetNum++;
		    break;
		}
	    }
	}

	if (requiredSheetNum < REQUIRED_SHEETS.length) {
	    errors
		    .add("The excel spreadsheet doesn't have required valid worksheets. Please check whether it contains"
			    + " sheets of CRF, Sections and Items.");
	}

	//check to see if questions are referencing a valid section name, tbh 7/30
	for (int j = 0; j < numSheets; j++) {
	    HSSFSheet sheet = wb.getSheetAt(j);//sheetIndex);
	    String sheetName = wb.getSheetName(j);
	    if (sheetName.equalsIgnoreCase(INSTRUCTIONS_SHEET)) {
		//totally ignore instructions
	    } else {
		/*
		 * current strategem: build out the queries by hand and revisit this as
		 * part of the data loading module. We begin to check for errors here
		 * and look for blank cells where there should be data, tbh, 7/28
		 */
		int numRows = sheet.getPhysicalNumberOfRows();
		int lastNumRow = sheet.getLastRowNum();
		System.out.println("PhysicalNumberOfRows"
			+ sheet.getPhysicalNumberOfRows());
		//System.out.println("LastRowNum()" + sheet.getLastRowNum());
		String secName = "";
		String page = "";
		HashMap<String, ResponseSetBean> responses = new HashMap<String, ResponseSetBean>();
		HashMap htmlErrors = new HashMap();

		//the above two need to persist across mult. queries,
		//and they should be created FIRST anyway, since instrument is first
		//also need to add to VERSIONING_MAP, tbh, 6-6-3

		//try to count how many blank rows, if 5 concective blank rows found, stop reading
		int blankRowCount = 0;
		if (sheetName.equalsIgnoreCase(TABLES_SHEET)) {
            logger.info("read " + sheetName);
 
            int k = 0;
            // int num_Tables=0;
            HSSFCell cell=null;
            HSSFRow row=null;
          
            blankRowCount=0;
            while(k<lastNumRow){     
        		
        		if (blankRowCount == 5) {
        			System.out.println("hit end of the row ");
        			break;
        		}
						
        		if (sheet.getRow(k) == null) {
        			k++;
        			blankRowCount++;
        			continue;
        		} else
        			blankRowCount = 0;
                  
        		cell = sheet.getRow(k).getCell((short) 0);               	
               	String table_name_key= getValue(cell);
               	if(table_name_key.trim().compareToIgnoreCase("Table_Name")!=0){
               		k++;
               		continue;
               	}
               	cell = sheet.getRow(k).getCell((short) 1);
               	String table_name = getValue(cell);
               	table_name = table_name.trim().toUpperCase();
               	int rec_num=0;
               	
               	//create table names
               	List<Object> params = new ArrayList<Object>();
                params.add(versionName);
                params.add(crfName);
                params.add(table_name);
                params.add("");//TABLE DESCRIPTION
                TableDAO tdao = new TableDAO(ds);
                DAODigester digester = tdao.getDigester();
                String sql;
                sql = EntityDAO.createStatement(digester.getQuery("createTableScoreByCRFVersionNameAndCRFName"), params);
                queries.add(sql);
               	
               	// get the attributes for left side, top right side, inner data
				// and right side
               	k++;
               	row = sheet.getRow(k);
               	cell = row.getCell((short) 1);
               	int left_side_col = (new Integer(getValue(cell))).intValue();
               	String left_side_column_name[];
            	left_side_column_name = new String[left_side_col];  
            	String top_row_data [] = new String[100];
            	for(int m=0; m<left_side_col; m++){
            		top_row_data[m]="";
            		cell = row.getCell((short) (m+2));
            		left_side_column_name[m] = getValue(cell).trim().toUpperCase();  
            		
            		params.clear();//REMOVE TABLE DESCRIPTION remove(params.size()-1);
            		params.add(versionName);
                    params.add(crfName);
                    params.add(table_name);
                    params.add(left_side_column_name[m]);
                    params.add(table_name+'_'+left_side_column_name[m]);
                    sql = EntityDAO.createStatement(digester.getQuery("createTableScoreAttributesByCRFVersionNameAndCRFName"), params);
                    queries.add(sql);
            	}
            	
            	// top attribute name
            	k++;
               	cell = sheet.getRow(k).getCell((short) 2);
               	String top_row_name = getValue(cell).trim().toUpperCase();
               	
               	params.clear();
        		params.add(versionName);
                params.add(crfName);
                params.add(table_name);
                params.add(top_row_name);
                params.add(table_name+'_'+top_row_name);
                sql = EntityDAO.createStatement(digester.getQuery("createTableScoreAttributesByCRFVersionNameAndCRFName"), params);
                queries.add(sql);
                
               	// inner data
            	k++;
               	cell = sheet.getRow(k).getCell((short) 1);
               	String inner_data_name = getValue(cell).trim().toUpperCase();
               	params.clear();
        		params.add(versionName);
                params.add(crfName);
                params.add(table_name);
                params.add(inner_data_name);
                params.add(table_name+'_'+inner_data_name);
                sql = EntityDAO.createStatement(digester.getQuery("createTableScoreAttributesByCRFVersionNameAndCRFName"), params);
                queries.add(sql);
                
               	k++;
               	row = sheet.getRow(k);
        		//skips blank lines
        		while(row==null){
        			k++;
        			row = sheet.getRow(k);
        		}
        		
               	// get data for the top row attribute               
               	int no_col_data=left_side_col;
               	cell = sheet.getRow(k).getCell((short) no_col_data);
               	String top_data = getValue(cell);
               	
               	int blank_col=5;
               	// skips the blank lines to get the table data
               	while(StringUtil.isBlank(top_data)){
               		k++;
               		cell = sheet.getRow(k).getCell((short) no_col_data);
                   	top_data = getValue(cell);               		
               	}
               			
               	// get the top row data
               	//top_row_data[0]=top_row_data[1]="";
               	while(!StringUtil.isBlank(top_data)){
               		//append numbers in this format: -2-3-4-
               		if(top_data.indexOf('-')!=-1)
               			top_data = StringUtil.appendNumbers(top_data);
               		
               		top_row_data[no_col_data]='-' + top_data.trim() + '-';
               		no_col_data++;
               		cell = sheet.getRow(k).getCell((short) no_col_data);
               		top_data = getValue(cell);
               	}
               	
               	//check right side border with number of blank columns (ie: 5 bank columns)
               	for(int m=(no_col_data+1); m<(no_col_data+blank_col); m++){
               		cell = sheet.getRow(k).getCell((short) m);
               		top_data = getValue(cell);
               		if(!StringUtil.isBlank(top_data)){
               			errors.add("The table header cannot have any blank at row " + k + " between column " + no_col_data + "and column " + m + ", in the Tables worksheet.");
                        htmlErrors.put(j + ", " + k + ", " + m , "INVALID FIELD");
                        break;
               		}
               	}
            	
               	//get the table's row data and construct with sql stmts
            	String tempdata="";
            	int counter=0;
            	k++;
            	row = sheet.getRow(k);
           		cell = row.getCell((short) 0);
           		tempdata = getValue(cell);
               	while(!StringUtil.isBlank(tempdata)){
               		String data [] = new String[no_col_data];
	            	for(int m=0; m<no_col_data; m++){
	            		cell=row.getCell((short) m);
	            		tempdata=getValue(cell).trim();
	            		if(StringUtil.isBlank(tempdata)) continue;
	        			if(tempdata.indexOf('-')!=-1)
	        				tempdata = StringUtil.appendNumbers(tempdata);
	        			data[m]='-' + tempdata + '-'; 
	        			if(m<left_side_col) continue;
	        			//set up sql stmt to insert data into DB
	        			params.clear();
		        		params.add(versionName);
		                params.add(crfName);
		                params.add(table_name);
		                params.add(top_row_name);
		                params.add(top_row_data[m]);
		                params.add(counter);
		                sql = EntityDAO.createStatement(digester.getQuery("createAttributeValue"), params);
		                queries.add(sql);
		                for(int n=0; n<left_side_col; n++){
		                	params.clear();
			        		params.add(versionName);
			                params.add(crfName);
			                params.add(table_name);
			                params.add(left_side_column_name[n]);
			                params.add(data[n]);
			                params.add(counter);
			                sql = EntityDAO.createStatement(digester.getQuery("createAttributeValue"), params);
			                queries.add(sql);
		                }
		                params.clear();
		        		params.add(versionName);
		                params.add(crfName);
		                params.add(table_name);
		                params.add(inner_data_name);
		                params.add(data[m]);
		                params.add(counter);
		                sql = EntityDAO.createStatement(digester.getQuery("createAttributeValue"), params);
		                queries.add(sql);
		                counter++;
	            	}
	            	k++;
	            	row = sheet.getRow(k);
	            	if (row == null) {
	        			break;
	        		} 
	            	cell = row.getCell((short) 0);	            	
	            	tempdata = getValue(cell);
               	}
         }
        }else
		if (sheetName.equalsIgnoreCase(ITEMS_SHEET)) {
		    logger.info("read an item in sheet" + sheetName);

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
			    errors.add("The ITEM_NAME column was blank at row "
				    + k + ", Items worksheet.");
			    htmlErrors.put(j + "," + k + ",0",
				    ERROR_REQUIRED_FIELD);
			}
			if (repeats.contains(itemName)) {
			    errors.add("A duplicate ITEM_NAME of " + itemName
				    + " was detected at row " + k
				    + ", Items worksheet.");
			    htmlErrors.put(j + "," + k + ",0",
				    ERROR_DUPLICATE_FIELD);
			}
			repeats.add(itemName);

			cell = sheet.getRow(k).getCell((short) 1);
			String descLabel = getValue(cell);

			if (StringUtil.isBlank(descLabel)) {
			    errors
				    .add("The DESCRIPTION_LABEL column was blank at row "
					    + k + ", Items worksheet.");
			    htmlErrors.put(j + "," + k + ",1",
				    ERROR_REQUIRED_FIELD);
			}

			cell = sheet.getRow(k).getCell((short) 2);
			String leftItemText = getValue(cell);
			if (StringUtil.isBlank(leftItemText)) {
			    errors
				    .add("The LEFT_ITEM_TEXT column was blank at row "
					    + k + ", Items worksheet.");
			    htmlErrors.put(j + "," + k + ",2",
				    ERROR_REQUIRED_FIELD);
			} else {
			    leftItemText = transferString(wb, cell);
			}

			cell = sheet.getRow(k).getCell((short) 3);
			String unit = transferString(wb, cell);
			cell = sheet.getRow(k).getCell((short) 4);
			String rightItemText = transferString(wb, cell);

			cell = sheet.getRow(k).getCell((short) 5);
			secName = getValue(cell);

			if (!secNames.contains(secName)) {
			    errors
				    .add("The SECTION_LABEL column is not a valid section at row "
					    + k
					    + ", Items worksheet.  "
					    + "Please check the Sections worksheet to see that there is a valid LABEL for this SECTION_LABEL.");
			    htmlErrors.put(j + "," + k + ",5",
				    ERROR_INVALID_LABEL);
			}
			cell = sheet.getRow(k).getCell((short) 6);
			String header = transferString(wb, cell);

			cell = sheet.getRow(k).getCell((short) 7);
			String subHeader = transferString(wb, cell);

			cell = sheet.getRow(k).getCell((short) 8);
			String parentItem = getValue(cell);

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
			    errors
				    .add("The RESPONSE_TYPE column was blank at row "
					    + k + ", items worksheet.");
			    htmlErrors.put(j + "," + k + ",12",
				    ERROR_REQUIRED_FIELD);

			} else {
			    if (!ResponseType.findByName(responseType
				    .toLowerCase())) {
				errors
					.add("The RESPONSE_TYPE column was invalid at row "
						+ k + ", items worksheet.");
				htmlErrors.put(j + "," + k + ",12",
					"INVALID FIELD");
			    } else {
				responseTypeId = (ResponseType
					.getByName(responseType.toLowerCase()))
					.getId();
			    }
			}

			cell = sheet.getRow(k).getCell((short) 13);
			String responseLabel = getValue(cell);
			if (StringUtil.isBlank(responseLabel)) {
			    errors
				    .add("The RESPONSE_LABEL column was blank at row "
					    + k + ", items worksheet.");
			    htmlErrors.put(j + "," + k + ",13",
				    ERROR_REQUIRED_FIELD);
			}
			cell = sheet.getRow(k).getCell((short) 14);
			String resOptions = "" + getValue(cell);
			if (responseLabel.equalsIgnoreCase("text")
				|| responseLabel.equalsIgnoreCase("textarea")) {
			    resOptions = "text";
			}
			int numberOfOptions = 0;
			if (responses.get(responseLabel) == null
				&& (StringUtil.isBlank(resOptions))) {
			    errors
				    .add("The RESPONSE_OPTIONS_TEXT column was blank at row "
					    + k + ", Items worksheet.");
			    htmlErrors.put(j + "," + k + ",14",
				    ERROR_REQUIRED_FIELD);
			}
			if (responses.get(responseLabel) == null
				&& (!StringUtil.isBlank(resOptions))) {
			    //String[] resArray = resOptions.split(",");
			    String text1 = resOptions.replaceAll("\\\\,", "##");
			    String[] resArray = text1.split(",");
			    numberOfOptions = resArray.length;
			}
			cell = sheet.getRow(k).getCell((short) 15);
			String resValues = getValue(cell);
			if (responseLabel.equalsIgnoreCase("text")
				|| responseLabel.equalsIgnoreCase("textarea")) {
			    resValues = "text";
			}
			if (responses.get(responseLabel) == null
				&& (StringUtil.isBlank(resValues))) {
			    errors
				    .add("The RESPONSE_VALUES column was blank at row "
					    + k + ", Items worksheet.");
			    htmlErrors.put(j + "," + k + ",15",
				    ERROR_REQUIRED_FIELD);
			}
			if (numberOfOptions > 0) {
			    String value1 = resValues.replaceAll("\\\\,", "##");
			    String[] resValArray = value1.split(",");
			    if (resValArray.length != numberOfOptions) {
				errors
					.add("There are an incomplete number of option-value pairs in "
						+ "RESPONSE_OPTIONS and REPONSE_VALUES at row "
						+ k
						+ ", questions worksheet; perhaps you are missing a comma? If there is a comma in any option text/value itself, please use \\, instead.");
				htmlErrors.put(j + "," + k + ",14",
					"NUMBER OF OPTIONS DOES NOT MATCH");
				htmlErrors.put(j + "," + k + ",15",
					"NUMBER OF VALUES DOES NOT MATCH");
			    }
			}

			cell = sheet.getRow(k).getCell((short) 16);
			String dataType = getValue(cell);
			if (StringUtil.isBlank(dataType)) {
			    errors.add("The DATA_TYPE column was blank at row "
				    + k + ", items worksheet.");
			    htmlErrors.put(j + "," + k + ",16",
				    ERROR_REQUIRED_FIELD);

			} else {
			    if (!ItemDataType
				    .findByName(dataType.toLowerCase())) {
				errors
					.add("The DATA_TYPE column was invalid at row "
						+ k + ", Items worksheet.");
				htmlErrors.put(j + "," + k + ",16",
					"INVALID FIELD");
			    }
			}

			cell = sheet.getRow(k).getCell((short) 17);
			String regexp = getValue(cell);
			String regexp1 = "";
			if (!StringUtil.isBlank(regexp)) {
			    // parse the string and get reg exp eg. regexp: /[0-9]*/
			    regexp1 = regexp.trim();
			    if (regexp1.startsWith("regexp:")) {
				int first = regexp1.indexOf("/");
				int last = regexp1.lastIndexOf("/");
				String finalRegexp = regexp1.substring(
					first + 1, last);
				try {
				    Pattern p = Pattern.compile(finalRegexp);
				} catch (PatternSyntaxException pse) {
				    errors
					    .add("The VALIDATION column has an invalid regular expression at row "
						    + k
						    + ", Items worksheet. Example: regexp: /[0-9]*/ ");
				    htmlErrors.put(j + "," + k + ",17",
					    "INVALID FIELD");
				}
			    } else if (regexp1.startsWith("func:")) {
				boolean isProperFunction = false;
				try {
				    Validator
					    .processCRFValidationFunction(regexp1);
				    isProperFunction = true;
				} catch (Exception e) {
				    errors.add(e.getMessage() + ", at row " + k
					    + ", Items worksheet.");
				    htmlErrors.put(j + "," + k + ",17",
					    "INVALID FIELD");
				}
			    } else {
				errors
					.add("The VALIDATION column was invalid at row "
						+ k + ", Items worksheet. ");
				htmlErrors.put(j + "," + k + ",17",
					"INVALID FIELD");
			    }

			}

			cell = sheet.getRow(k).getCell((short) 18);
			String regexpError = getValue(cell);
			if (!StringUtil.isBlank(regexp)
				&& StringUtil.isBlank(regexpError)) {
			    errors
				    .add("The VALIDATION_ERROR_MESSAGE column was blank at row "
					    + k
					    + ", Items worksheet. It cannot be blank if VALIDATION is not blank.");
			    htmlErrors.put(j + "," + k + ",18",
				    ERROR_REQUIRED_FIELD);
			}

			boolean phiBoolean = false;
			cell = sheet.getRow(k).getCell((short) 19);
			String phi = getValue(cell);
			if (!"0".equals(phi) && !"1".equals(phi)) {
			    errors
				    .add("The PHI column was invalid at row "
					    + k
					    + ", Items worksheet. PHI can only be either 0 or 1.");
			    htmlErrors
				    .put(j + "," + k + ",19", "INVALID VALUE");
			} else {
			    phiBoolean = ("1".equals(phi)) ? true : false;
			}

			boolean isRequired = false;
			cell = sheet.getRow(k).getCell((short) 20);
			String required = getValue(cell);
			if (StringUtil.isBlank(required)) {
			    required = "0";
			}
			if (!"0".equals(required) && !"1".equals(required)) {
			    errors
				    .add("The REQUIRED column was invalid at row "
					    + k
					    + ", Items worksheet. REQUIRED can only be either 0 or 1.");
			    htmlErrors
				    .put(j + "," + k + ",20", "INVALID VALUE");
			} else {
			    isRequired = ("1".equals(required)) ? true : false;
			}

			//better spot for checking item might be right here, tbh 7-25
			List<Object> params = new ArrayList<Object>();
			params.add(itemName);
			params.add(descLabel);
			params.add(unit);
			params.add(new Boolean(phiBoolean));
			params.add(dataType.toUpperCase());
			params.add(new Integer(1));
			params.add(Status.AVAILABLE.getId());
			params.add(new Integer(ownerId));

			String vlSql = EntityDAO.createStatement(
				idao.getDigester().getQuery(
					"createByDataTypeCode"), params);

			backupItemQueries.put(itemName, vlSql);
			//to compare items from DB later, if two items have the same name,
			//but different units or phiStatus, they are different
			ItemBean ib = new ItemBean();
			ib.setName(itemName);
			ib.setUnits(unit);
			ib.setPhiStatus(phiBoolean);
			ib.setDescription(descLabel);
			ib.setDataType(ItemDataType.getByName(dataType
				.toLowerCase()));

			ResponseSetBean rsb = responses.get(responseLabel);
			if (rsb == null) {
			    rsb = new ResponseSetBean();
			    rsb.setResponseTypeId(responseTypeId);
			    String resOptions1 = resOptions.replace("\\\\,",
				    "\\,");
			    String resValues1 = resValues.replace("\\\\,",
				    "\\,");
			    rsb.setOptions(resOptions1, resValues1);
			}

			ItemFormMetadataBean ifmb = new ItemFormMetadataBean();
			ifmb.setResponseSet(rsb);
			ib.setItemMeta(ifmb);
			items.put(itemName, ib);

			if (!itemCheck.containsKey(itemName)) {// item not in the DB           
			    openQueries.put(itemName, vlSql);

			} else {// item in the DB
			    ItemBean oldItem = (ItemBean) idao
				    .findByNameAndCRFId(itemName, crfId);
			    if (oldItem.getOwnerId() == ownerId) {// owner can update               
				if (!cvdao.hasItemData(oldItem.getId())) {// no item data
				    params = new ArrayList<Object>();
				    params.add(descLabel);
				    params.add(unit);
				    params.add(new Boolean(phiBoolean));
				    params.add(dataType.toUpperCase());
				    params.add(itemName);
				    params.add(new Integer(ownerId));
				    params.add(new Integer(crfId));
				    String upSql = EntityDAO.createStatement(
					    idao.getDigester().getQuery(
						    "updateByDataTypeCode"),
					    params);
				    openQueries.put(itemName, upSql);
				} else {
				    params = new ArrayList<Object>();
				    params.add(descLabel);
				    params.add(new Boolean(phiBoolean));
				    params.add(itemName);
				    params.add(new Integer(ownerId));
				    params.add(new Integer(crfId));
				    String upSql = EntityDAO.createStatement(
					    idao.getDigester().getQuery(
						    "updateByOther"), params);
				    openQueries.put(itemName, upSql);
				}
			    } else {
				ownerId = oldItem.getOwner().getId();
			    }
			}
			params = new ArrayList<Object>();
			params.add(responseLabel);
			params.add(resOptions);
			params.add(resValues);
			params.add(responseType.toLowerCase());
			params.add(versionName);
			params.add(crfName);
			ResponseSetDAO rsdao = new ResponseSetDAO(ds);
			String sql = EntityDAO.createStatement(rsdao
				.getDigester().getQuery("create"), params);
			if (responses.get(responseLabel) == null) {
			    queries.add(sql);
			    responses.put(responseLabel, rsb);
			    //this will have to change since we have some data in the actual
			    // spreadsheet
			    //change it to caching response set names in a collection?
			    //or just delete the offending cells from the spreadsheet?
			}

			params = new ArrayList<Object>();
			params.add(versionName);
			params.add(crfName);
			params.add(responseLabel);
			params.add(versionName);
			params.add(crfName);
			params.add(itemName);
			params.add(ownerId);
			params.add(subHeader);
			params.add(header);
			params.add(leftItemText);
			params.add(rightItemText);
			if (!StringUtil.isBlank(parentItem)) {
			    params.add(parentItem);
			    params.add(new Integer(ownerId));
			} else {
			    params.add("0");
			}
			params.add(secName);
			params.add(versionName);
			params.add(crfName);
			params.add(new Integer(k));
			params.add(parentItem);
			params.add(new Integer(columnNum));
			params.add(page);
			params.add(questionNum);
			params.add(regexp1);
			params.add(regexpError);
			params.add(new Boolean(isRequired));
			ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(ds);
			if (!StringUtil.isBlank(parentItem)) {
			    String sql2 = EntityDAO
				    .createStatement(
					    ifmdao
						    .getDigester()
						    .getQuery(
							    "createByCRFVersionNameAndCRFNameWithParent"),
					    params);
			    queries.add(sql2);
			} else {
			    String sql2 = EntityDAO
				    .createStatement(
					    ifmdao
						    .getDigester()
						    .getQuery(
							    "createByCRFVersionNameAndCRFNameWithoutParent"),
					    params);
			    queries.add(sql2);
			}
			//link version with items now
			params = new ArrayList<Object>();
			params.add(versionName);
			params.add(crfName);
			params.add(itemName);
			params.add(new Integer(ownerId));
			VersioningMapDAO vmdao = new VersioningMapDAO(ds);
			String sql3 = EntityDAO.createStatement(vmdao
				.getDigester().getQuery("create"), params);
			queries.add(sql3);
		    }
		} else if (sheetName.equalsIgnoreCase(SECTIONS_SHEET)) {
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
				    .add("The SECTION_LABEL column was blank at row "
					    + k + ", Sections worksheet.");
			    htmlErrors.put(j + "," + k + ",0",
				    ERROR_REQUIRED_FIELD);
			}
			if (secNames.contains(secLabel)) {
			    errors
				    .add("The SECTION_LABEL column was a duplicate of "
					    + secLabel
					    + " at row "
					    + k
					    + ", sections worksheet.");
			    htmlErrors.put(j + "," + k + ",0",
				    ERROR_DUPLICATE_FIELD);
			}
			//logger.info("section name:" + secLabel + "row num:" +k);
			secNames.add(secLabel);
			cell = sheet.getRow(k).getCell((short) 1);
			String title = transferString(wb, cell);
			if (StringUtil.isBlank(title)) {
			    errors
				    .add("The SECTION_TITLE column was blank at row "
					    + k + ", Sections worksheet.");
			    htmlErrors.put(j + "," + k + ",1",
				    ERROR_REQUIRED_FIELD);
			}

			cell = sheet.getRow(k).getCell((short) 2);
			String subtitle = transferString(wb, cell);
			cell = sheet.getRow(k).getCell((short) 3);
			String instructions = transferString(wb, cell);
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
			List<Object> params = new ArrayList<Object>();
			params.add(versionName);
			params.add(crfName);
			params.add(Status.AVAILABLE.getId());
			params.add(secLabel);
			params.add(title);
			params.add(instructions);
			params.add(subtitle);
			params.add(pageNumber);
			params.add(new Integer(k));
			params.add(parentId);
			params.add(ownerId);
			SectionDAO sdao = new SectionDAO(ds);
			String sql = EntityDAO.createStatement(sdao
				.getDigester().getQuery(
					"createByCRFVersionNameAndCRFName"),
				params);
			queries.add(sql);
		    }//end for loop
		} else if (sheetName.equalsIgnoreCase(CRF_SHEET)) {
		    logger.info("read crf");
		    //one row, four cells, all strings
		    HSSFCell cell = sheet.getRow(1).getCell((short) 0);
		    crfName = getValue(cell);
		    if (StringUtil.isBlank(crfName)) {
			errors
				.add("The CRF_NAME column was blank in the CRF worksheet.");
			htmlErrors.put(j + ",1,0", "REQUIRED FIELD");
		    }

		    try {
			CRFBean checkName = (CRFBean) cdao.findByPK(crfId);
			if (!checkName.getName().equals(crfName)) {
			    logger.info("crf name is mismatch");
			    errors
				    .add("The CRF_NAME column did not match the intended CRF version "
					    + "you want to upload.  Make sure this reads '"
					    + checkName.getName()
					    + "' before you continue.");
			    htmlErrors.put(j + ",1,0", "DID NOT MATCH CRF");
			}
		    } catch (Exception pe) {
			logger.warning("Exception happened when check CRF name"
				+ pe.getMessage());
		    }

		    cell = sheet.getRow(1).getCell((short) 1);
		    String version = getValue(cell);
		    if (StringUtil.isBlank(version)) {
			errors
				.add("The VERSION column was blank in the CRF worksheet.");
			htmlErrors.put(j + ",1,1", "REQUIRED FIELD");
		    } else if (!version.equals(versionName)) {
			errors
				.add("The VERSION column did not match the intended version name "
					+ "you want to upload. Make sure this reads '"
					+ versionName
					+ "' before you continue.");
			htmlErrors.put(j + ",1,1", "DID NOT MATCH VERSION");
		    }

		    cell = sheet.getRow(1).getCell((short) 2);
		    String versionDesc = getValue(cell);

		    cell = sheet.getRow(1).getCell((short) 3);
		    String revisionNotes = getValue(cell);
		    if (StringUtil.isBlank(revisionNotes)) {
			errors
				.add("The REVISION_NOTES column was blank in the CRF worksheet.");
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
			logger.info("found a matching version name..."
				+ version);
			errors
				.add("The VERSION column is not unique.  This can cause confusion in "
					+ "selecting the correct CRF. Please make sure you change the "
					+ "version name so that it can be uniquely identified by users in the system. "
					+ "Otherwise, the previous same version will be deleted from database.");
			htmlErrors.put(j + ",1,2", "NOT UNIQUE");

		    }
		    //get Publisher's information.
		    //if there's no info then it is "Unknown"
		    PublisherDAO pubdao= new PublisherDAO(ds);
		    
		    cell = sheet.getRow(1).getCell((short) 4);
		    String Publisher_Name="Unknown";
		    if(cell!=null)
		    	Publisher_Name = getValue(cell).trim();
		    
		    int Publisher_Id = -1;
		    
		    //There is some info for Publisher
		    if (StringUtil.isBlank(Publisher_Name)==false){
		    	Publisher_Id = pubdao.getId(Publisher_Name);
		    	
		    	//create new Publisher
		    	if(Publisher_Id==-1){//if(alist.size()==0){
		    		String Publisher_Abbreviation="";
		    		String Publisher_Description ="";
		    		cell = sheet.getRow(1).getCell((short) 5);
		    		if(cell!=null)
		    			Publisher_Abbreviation = getValue(cell).trim();
				    cell = sheet.getRow(1).getCell((short) 6);
				    if(cell!=null)
				    	Publisher_Description = getValue(cell).trim();
				    
				    pubdao.createPublisher(Publisher_Name, Publisher_Abbreviation, Publisher_Description);
		    		Publisher_Id = pubdao.getId(Publisher_Name);
		    	} 
		    }      

		    String sql = "";
		    if (crfId == 0) {
			List<Object> params = new ArrayList<Object>();
			params.add(crfName);
			params.add(Status.AVAILABLE.getId());
			params.add(version);
			params.add(versionDesc);
			params.add(new Integer(ownerId));
			params.add(revisionNotes);
			if(Publisher_Id!=-1)
				params.add(Publisher_Id);			
			else 
				params.add(null);
			sql = EntityDAO.createStatement(cvdao.getDigester()
				.getQuery("createByCRFName"), params);
			queries.add(sql);
		    } else {
			List<Object> params = new ArrayList<Object>();
			params.add(new Integer(crfId));
			params.add(Status.AVAILABLE.getId());
			params.add(version);
			params.add(versionDesc);
			params.add(new Integer(ownerId));
			params.add(revisionNotes);
			if(Publisher_Id!=-1)
				params.add(Publisher_Id);			
			else 
				params.add(null);
			sql = EntityDAO.createStatement(cvdao.getDigester()
				.getQuery("create"), params);
			queries.add(sql);
		    }

		    List<Object> params = new ArrayList<Object>();
		    params.add(version);
		    params.add(crfName);
		} else if (sheetName.equalsIgnoreCase(RULES_SHEET)) {
		    String sql = "", name = "", instruction = "", condition = "", action = "";
		    Collection<String> ruleNames = new ArrayList<String>();
		    logger.info("read an item in sheet" + sheetName);

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
			name = getValue(cell);

			if (StringUtil.isBlank(name)) {
			    errors
				    .add("The SKIP_RULE_NAME column was blank at row "
					    + k
					    + ", "
					    + sheetName
					    + " worksheet.");
			    htmlErrors.put(j + "," + k + ",0",
				    ERROR_REQUIRED_FIELD);
			}
			if (ruleNames.contains(name)) {
			    errors.add("A duplicate SKIP_RULE_NAME of " + name
				    + " was detected at row " + k + ", "
				    + sheetName + " worksheet.");
			    htmlErrors.put(j + "," + k + ",0",
				    ERROR_DUPLICATE_FIELD);
			} else {
			    ruleNames.add(name);
			}

			cell = sheet.getRow(k).getCell((short) 1);
			instruction = getValue(cell);

			cell = sheet.getRow(k).getCell((short) 2);
			condition = getValue(cell);
			if (StringUtil.isBlank(condition)) {
			    errors.add("The CONDITION column was blank at row "
				    + k + ", " + sheetName + " worksheet.");
			    htmlErrors.put(j + "," + k + ",2",
				    ERROR_REQUIRED_FIELD);
			}

			cell = sheet.getRow(k).getCell((short) 3);
			action = getValue(cell);
			if (StringUtil.isBlank(action)) {
			    errors.add("The ACTION column was blank at row "
				    + k + ", " + sheetName + " worksheet.");
			    htmlErrors.put(j + "," + k + ",3",
				    ERROR_REQUIRED_FIELD);
			}

			List<Object> params = new ArrayList<Object>();
			params.add(name);
			params.add(instruction);
			params.add(condition);
			params.add(action);
			params.add(versionName);
			params.add(crfName);
			params.add(Status.AVAILABLE.getId());
			params.add(new Integer(ownerId));
			SkipRuleDAO srdao = new SkipRuleDAO(ds);
			DAODigester digester = srdao.getDigester();
			sql = EntityDAO.createStatement(digester
				.getQuery("createByCRFVersionNameAndCRFName"),
				params);
			queries.add(sql);
		    }
		}

		//move html creation to here, include error creation as well, tbh 7/28
		buf.append(sheetName + "<br>");
		buf
			.append("<div class=\"box_T\"><div class=\"box_L\"><div class=\"box_R\"><div class=\"box_B\"><div class=\"box_TL\"><div class=\"box_TR\"><div class=\"box_BL\"><div class=\"box_BR\">");

		buf.append("<div class=\"textbox_center\">");
		buf
			.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"");
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
			    error = "<span class=\"alert\">"
				    + htmlErrors.get(errorKey) + "</span>";
			}
			if (cell == null) {
			    cellType = HSSFCell.CELL_TYPE_BLANK;
			} else {
			    cellType = cell.getCellType();
			}
			switch (cellType) {
			case HSSFCell.CELL_TYPE_BLANK:
			    buf.append("<td class=\"table_cell\">" + error
				    + "</td>");
			    break;
			case HSSFCell.CELL_TYPE_NUMERIC:
			    buf.append("<td class=\"table_cell\">"
				    + cell.getNumericCellValue() + " " + error
				    + "</td>");
			    break;
			case HSSFCell.CELL_TYPE_STRING:
			    buf.append("<td class=\"table_cell\">"
				    + cell.getStringCellValue() + " " + error
				    + "</td>");
			    break;
			default:
			    buf.append("<td class=\"table_cell\">" + error
				    + "</td>");
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

    public static String getValue(HSSFCell cell) {
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
	    Double newVal = new Double(cell.getNumericCellValue());
	    int changeme = newVal.intValue();
	    //Integer changeme2 = new Integer(changeme);
	    val = Integer.toString(changeme);
	    //buf.append("<td><font class=\"bodytext\">" + cell.getNumericCellValue()
	    // + "</font></td>");
	    break;
	case HSSFCell.CELL_TYPE_STRING:
	    val = cell.getStringCellValue();
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
			buf.append("<td>" + cell.getNumericCellValue()
				+ "</td>");
			break;
		    case HSSFCell.CELL_TYPE_STRING:
			buf
				.append("<td>" + cell.getStringCellValue()
					+ "</td>");
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

    public static final String excelToHtml(String str, HSSFFont font) {
	String s = StringEscapeUtils.escapeHtml(str);

	int index = s.indexOf('\n');
	if (index > 0) {
	    s = s.replaceAll("\n", "<br/>");
	}

	if (font.getBoldweight() == HSSFFont.BOLDWEIGHT_BOLD) {
	    s = "<b>" + s + "</b>";
	}

	if (font.getItalic()) {
	    s = "<i>" + s + "</i>";
	}
	if (font.getStrikeout()) {
	    s = "<strike>" + s + "</strike>";
	}
	if (font.getUnderline() != HSSFFont.U_NONE) {
	    s = "<u>" + s + "</u>";
	}
	if (font.getColor() != HSSFFont.COLOR_NORMAL) {
	    HSSFColor color = (HSSFColor) HSSFColor.getIndexHash().get(
		    new Integer(font.getColor()));
	    s = "<font color=\"" + convertToHexString(color) + "\">" + s
		    + "</font>";
	}
	return s;
    }
    
    public static final String transferString(HSSFWorkbook workbook, HSSFCell cell) {
	if (workbook == null || cell == null
		|| cell.getCellType() != HSSFCell.CELL_TYPE_STRING) {
	    return "";
	}
	HSSFRichTextString rts = cell.getRichStringCellValue();
	StringBuffer ret = new StringBuffer("");
	int nruns = rts.numFormattingRuns();
	if (nruns > 0) {
	    int begin = 0;
	    int end = rts.getIndexOfFormattingRun(0);
	    HSSFFont font = workbook.getFontAt(cell.getCellStyle().getFontIndex());
	    int index = 0;
	    while(index < nruns){
		ret.append(excelToHtml(rts.getString().substring(begin, end), font));
		begin = end;
		if(index < (nruns-1)){
		    end = rts.getIndexOfFormattingRun(index+1);
		}else{
		    end = rts.length();
		}
		font = workbook.getFontAt(rts.getFontOfFormattingRun(index));
		index++;
	    }
	    ret.append(excelToHtml(rts.getString().substring(begin, end), font));
	} else {
	    ret.append(excelToHtml(rts.getString(), workbook.getFontAt(cell.getCellStyle().getFontIndex())));
	}
	return ret.toString();
    }

    public static final String convertToHexString(HSSFColor color) {
	if (color == null) {
	    return null;
	}
	short[] triplet = color.getTriplet();
	StringBuffer ret = new StringBuffer("#");
	if (triplet[0] < 16) {
	    ret.append("0");
	    ret.append(Integer.toHexString(triplet[0]));
	} else {
	    ret.append(Integer.toHexString(triplet[0]));
	}
	if (triplet[1] < 16) {
	    ret.append("0");
	    ret.append(Integer.toHexString(triplet[1]));
	} else {
	    ret.append(Integer.toHexString(triplet[1]));
	}
	if (triplet[2] < 16) {
	    ret.append("0");
	    ret.append(Integer.toHexString(triplet[2]));
	} else {
	    ret.append(Integer.toHexString(triplet[2]));
	}
	return ret.toString();
    }
}
