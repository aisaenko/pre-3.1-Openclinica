/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;

/**
 * @author jxu
 *  
 */
public class SPSSReportBean extends ReportBean {
  private final static int FIRSTCASE = 3;

  private final static int FIRSTCASE_IND = 2;

  private final static int COLUMNS_IND = FIRSTCASE_IND - 1;

  private static final String[] builtin = { "SubjID", "ProtocolID", "DOB", "YOB", "Gender",
      "Location", "StartDate", "EndDate" };

  public static final List list = Arrays.asList(builtin);

  public HashMap descriptions = new HashMap();
 

  private boolean gender = false;//whether exporting gender  
  
  private String datFileName="C:\\path\\to\\your\\file.dat";
  
  

  /**
   * @return Returns the datFileName.
   */
  public String getDatFileName() {
    return datFileName;
  }
  /**
   * @param datFileName The datFileName to set.
   */
  public void setDatFileName(String datFileName) {
    this.datFileName = datFileName;
  }
  /**
   * @return Returns the descriptions.
   */
  public HashMap getDescriptions() {
    return descriptions;
  }
  /**
   * @param descriptions The descriptions to set.
   */
  public void setDescriptions(HashMap descriptions) {
    this.descriptions = descriptions;
  }
  /**
   * @return Returns the gender.
   */
  public boolean isGender() {
    return gender;
  }

  /**
   * @param gender
   *          The gender to set.
   */
  public void setGender(boolean gender) {
    this.gender = gender;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "";
  }

  public String getMetadataFile() {
    String answer = "* NOTE: If you have put this file in a different folder \n" +
    		"* from the associated data file, you will have to change the FILE \n" +
    		"* location on the line below to point to the physical location of your data file.\n";

    answer += "GET DATA  " 
        + "/TYPE = TXT" + "/FILE = '" + getDatFileName() + "' "
        + "/DELCASE = LINE " + "/DELIMITERS = \"\\t\" " + "/ARRANGEMENT = DELIMITED "
        + "/FIRSTCASE = " + FIRSTCASE + " " + // since we send the row of column
        // to the .dat file, start
        // importing at row 2
        "/IMPORTCASE = ALL ";

    if (data.size() <= 0) {
      answer += ".\n";
    } else {
      ArrayList columns = (ArrayList) data.get(this.COLUMNS_IND);

      answer += "/VARIABLES = \n";
      //builtin attributes     
      for (int i = 0; i < columns.size(); i++) {
        //int varNum = i + 1;
        String itemName = (String) columns.get(i);
        String varLabel = "V_" + itemName;
        if (isBuiltin(itemName)) {
          if (itemName.startsWith("Gender")) {
            gender = true;
          }
          varLabel = itemName;
        

        if (isDataColumnText(i)) {
          int len = getDataColumnMaxLen(i);
          if (len== 0) {
            len = 1; //mininum length required by spss
          }
          answer += "\t" + varLabel + " A" + len + "\n";
        } else {
          answer += "\t" + varLabel + " F1.0\n";
        }
        }
      }
      
      int startItem = columns.size()-items.size();
     
      //items
      for (int i = startItem; i < columns.size(); i++) {
        String temp = "";
        DisplayItemHeaderBean dih = (DisplayItemHeaderBean) items.get(i-startItem);
        ItemBean ib = dih.getItem();
        String varLabel = "V_" + dih.getItemHeaderName();       

        if (isDataColumnText(i)) {
          int len = getDataColumnMaxLen(i);
          if (len== 0) {
            len = 1; //mininum length required by spss
          }
          ArrayList metas = ib.getItemMetas();
          int optionCount = 0;
          for (int k = 0; k < metas.size(); k++) {
            ItemFormMetadataBean ifmb = (ItemFormMetadataBean) metas.get(k);
            ResponseSetBean rsb = ifmb.getResponseSet();
            if (rsb.getResponseType().equals(ResponseType.CHECKBOX)
                || rsb.getResponseType().equals(ResponseType.RADIO)
                || rsb.getResponseType().equals(ResponseType.SELECT)
                || rsb.getResponseType().equals(ResponseType.SELECTMULTI)) {
              optionCount++;
            }
          }
          if (optionCount == metas.size()) {
            //all responsetype of metas have options,need to show value labels
            if (len>8) {
              len =8;
            }
          } 
          answer += "\t" + varLabel + " A" + len + "\n";
        } else {
          answer += "\t" + varLabel + " F1.0\n";
        }
       
      }
      answer += ".\n";

      answer += "VARIABLE LABELS\n";
      //builtin attributes
      for (int i = 0; i < columns.size(); i++) {
        //int varNum = i + 1;
        String itemName = (String) columns.get(i);
        String varLabel = "V_" + itemName;
        if (isBuiltin(itemName)) {
          varLabel = itemName;
          answer += "\t" + varLabel + " \"" + (String)descriptions.get(itemName) + "\"\n";
        }
      }
      //items
      for (int i = 0; i < items.size(); i++) {
        String temp = "";
        DisplayItemHeaderBean dih = (DisplayItemHeaderBean) items.get(i);
        ItemBean ib = dih.getItem();
        String varLabel = "V_" + dih.getItemHeaderName();
        answer += "\t" + varLabel + " \"" + ib.getDescription() + "\"\n";
      }

      answer += ".\n";

      answer += "VALUE LABELS\n";

      if (isGender()) {

        answer += "\t" + "Gender" + "\n";
        answer += "\t" + "'M'" + " \"" + "Male" + "\"\n";
        answer += "\t" + "'F'" + " \"" + "Female" + "\"\n\t/\n";

      }

      for (int i = 0; i < items.size(); i++) {
        String temp = "";
        DisplayItemHeaderBean dih = (DisplayItemHeaderBean) items.get(i);
        ItemBean ib = dih.getItem();

        String varLabel = "V_" + dih.getItemHeaderName();
        temp += "\t" + varLabel + "\n";
        boolean allOption = true;
        ArrayList metas = ib.getItemMetas();
        HashMap optionMap = new LinkedHashMap();
        
        for (int k = 0; k < metas.size(); k++) {
          ItemFormMetadataBean ifmb = (ItemFormMetadataBean) metas.get(k);
          ResponseSetBean rsb = ifmb.getResponseSet();
          if (rsb.getResponseType().equals(ResponseType.TEXT)
              || rsb.getResponseType().equals(ResponseType.FILE)
              || rsb.getResponseType().equals(ResponseType.TEXTAREA)) {
            //has text response, dont show value labels
            allOption = false;
            break;
            
          } else {
            for (int j = 0; j < rsb.getOptions().size(); j++) {
              ResponseOptionBean ob = (ResponseOptionBean) rsb.getOptions().get(j);
              
              String key = ob.getValue();
              if (optionMap.containsKey(key)) {
                //System.out.println("\ncontains key" + key);
                ArrayList a = (ArrayList) optionMap.get(key);               
                if (!a.contains(ob.getText())) {              
                  a.add(ob.getText());
                  optionMap.put(key, a);
                }

              } else {
                //System.out.println("\nnew key" + key);               
                ArrayList a = new ArrayList();
                a.add(ob.getText());
                optionMap.put(key, a);

              }

            }       

          }
        }
        Iterator it = optionMap.keySet().iterator();
        while (it.hasNext()) {
          String value = (String) it.next();
          ArrayList a = (ArrayList) optionMap.get(value);
          String texts = "";
          if (a.size()>1) {
           for (int n = 0; n<a.size(); n++) {  
             texts += (String) a.get(n);
             if (n <a.size()-1){
               texts += "/";
             }
            
           }
          } else {
            texts = (String)a.get(0);
          }
          if (value.length()>8) {
            value = value.substring(0,8);
          }
          if (isValueText(value)){
            temp += "\t'" + value + "' \"" + texts + "\"\n";
          } else {
            temp += "\t" + value + " \"" + texts + "\"\n";
          }

        }//end of while (it.hasNext()) 

        if (allOption) {
          answer += temp + "\t/\n";
        }
        
      }

    }

    answer += ".\n EXECUTE.\n";

    return answer;
  }

  public String getDataFile() {
    String answer = "";

    for (int i = 0; i < data.size(); i++) {// if start with row 2, not include header, just row data
      ArrayList row = (ArrayList) data.get(i);

      for (int j = 0; j < row.size(); j++) {
        answer += (String) row.get(j) + "\t";
      }
      answer += "\n";
    }

    return answer;
  }

  private boolean isDataColumnText(int col) {
    for (int i = FIRSTCASE_IND; i < data.size(); i++) {
      String entry = getDataColumnEntry(col, i);
      try {
        float f = Float.parseFloat(entry);
      } catch (Exception e) {
        return true;
      }
    }

    return false;
  }
  
  private boolean isValueText(String value) {    
      try {
        float f = Float.parseFloat(value);
      } catch (Exception e) {
        return true;
      }
   

    return false;
  }

  private int getDataColumnMaxLen(int col) {
    int max = 0;

    for (int i = FIRSTCASE_IND; i < data.size(); i++) {
      String entry = getDataColumnEntry(col, i);

      if (entry.length() > max) {
        max = entry.length();
      }
    }

    return max;
  }

  private boolean isBuiltin(String itemName) {
    for (int i = 0; i < list.size(); i++) {
      String attribute = (String) list.get(i);
      if (itemName != null & itemName.startsWith(attribute)) {
        return true;
      }
    }
    return false;
  }

  private String getDescription(String itemName) {
    for (int i = 0; i < list.size(); i++) {
      String attribute = (String) list.get(i);
      if (itemName != null & itemName.startsWith(attribute)) {
        return (String) descriptions.get(attribute);
      }
    }
    return "";
  }

}