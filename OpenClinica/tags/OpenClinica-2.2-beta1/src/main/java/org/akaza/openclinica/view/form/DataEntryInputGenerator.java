package org.akaza.openclinica.view.form;

import java.util.*;

import org.jdom.Element;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;

/**
 * This class creates various types of input fields such as text
 * inputs and select lists. It is used by a class such as HorizontalFormBuilder to
 * dynamically generate HTML tables.
 */
public class DataEntryInputGenerator implements InputGenerator{
  private static Map<String,String> NULL_VALUES_INITVERSION =
    new HashMap<String,String>();
  static{
    NULL_VALUES_INITVERSION.put("NI","no information");
    NULL_VALUES_INITVERSION.put("NA","not applicable");
    NULL_VALUES_INITVERSION.put("UNK","unknown");
    NULL_VALUES_INITVERSION.put("NASK","not asked");
    NULL_VALUES_INITVERSION.put("ASKU","asked but unknown");
    NULL_VALUES_INITVERSION.put("NAV","not available");
    NULL_VALUES_INITVERSION.put("OTH","other");
    NULL_VALUES_INITVERSION.put("PINF","positive infinity");
    NULL_VALUES_INITVERSION.put("NINF","negative infinity");
    NULL_VALUES_INITVERSION.put("MSK","masked");
    NULL_VALUES_INITVERSION.put("NP","not present");
  }
  public static Map<String,String> NULL_VALUES_LONGVERSION =
    Collections.unmodifiableMap(NULL_VALUES_INITVERSION);

  public static String ONCHANGE_TEXT_INPUT=
    "this.className='changedField';setImage('DataStatus_top','images/icon_UnsavedData.gif');setImage('DataStatus_bottom','images/icon_UnsavedData.gif');";
  //for radio buttons only; to deal with an IE/repetition model bug
  public static String ONCHANGE_TEXT_INPUT_RADIOS=
    "if(! detectIEWindows(navigator.userAgent)){this.className='changedField';}setImage('DataStatus_top','images/icon_UnsavedData.gif');setImage('DataStatus_bottom','images/icon_UnsavedData.gif');";
  /* This method generates a text input field for a cell inside an HTML table. Like the
  * other methods, the user passes in a reference to the Element object, and the
  * object receives new attributes and content. Then the method returns the altered
  * Element object.*/
  public Element createTextInputTag(Element tdCell,
                                    Integer itemId,
                                    Integer tabNumber,
                                    String defaultValue,
                                    boolean isDateType,
                                    String dbValue){
    Element element = new Element("input");
    element.setAttribute("type","text");
    element.setAttribute("tabindex",tabNumber.toString());
    element.setAttribute("name","input"+itemId.toString());
    element.setAttribute("onChange",ONCHANGE_TEXT_INPUT);
    if(dbValue != null && dbValue.length() > 0) {
      element.setAttribute("value",dbValue);
    }  else {
      element.setAttribute("value",defaultValue);
    }
    tdCell.addContent(element);
    Element dateElement;
    if(isDateType)  {
      dateElement =  getDateWidgetForCell(itemId);
      tdCell.addContent(dateElement);
      tdCell.addContent("(MM/DD/YYYY)");
    }

    return tdCell;
  }
  /* This method generates a checkbox tag for a cell inside an HTML table. Like the
* other methods, the user passes in a reference to the Element object, and the
* object receives new attributes and content. Then method returns the altered
* Element object.*/
  public Element createCheckboxTag(Element tdCell, Integer itemId,
                                   List options, Integer tabNumber,
                                   boolean includeLabel, String dbValue,
                                   String defaultValue, boolean isHorizontal) {
    Element element;
    String[] arrayOfValues= new String[]{};
    //Handles lone Strings, or Strings separated by spaces or commas
    if(dbValue != null && dbValue.length() > 0){
      arrayOfValues = handleSplitString(dbValue);
    }  else  if(defaultValue != null && defaultValue.length() > 0){
      arrayOfValues = handleSplitString(defaultValue);
    }
    for (Object responseOptBean : options) {
      element = this.initializeInputElement("checkbox",itemId,tabNumber);
      String value =  ((ResponseOptionBean)responseOptBean).getValue();
      element.setAttribute("value",value );
      //It's checked if its value equals the DB value
      if(dbValue != null && dbValue.length() > 0) {
        // && value.equalsIgnoreCase(dbValue)
        for(String string : arrayOfValues)  {
          if(value.equalsIgnoreCase(string)) {
            ((ResponseOptionBean)responseOptBean).setSelected(true);
          }
        }
      }  else  if(defaultValue != null && defaultValue.length() > 0) {
        // && value.equalsIgnoreCase(dbValue)
        for(String string : arrayOfValues)  {
          if(value.equalsIgnoreCase(string)) {
            ((ResponseOptionBean)responseOptBean).setSelected(true);
          }
        }
      }
      if( ((ResponseOptionBean)responseOptBean).isSelected() ) {
        element.setAttribute("checked","checked");      }
      tdCell.addContent(element);
      if(includeLabel) {
        tdCell.addContent(((ResponseOptionBean)responseOptBean).getText()); }
      //if the response_layout property is not "horizontal", then add a <br> tag
      if(! isHorizontal) {
      tdCell.addContent(new Element("br"));   }
    }

    return tdCell;
  }
  /* This method generates a radio button field for a cell inside an HTML table.
  *The options parameter contains ResponseOptionBeans, which provides
  *the "checked" value for the tag, as well as its accompanying text.
  *Like the other methods, the user passes in a reference to the Element object, and the
* object receives new attributes and content. Then the method returns the altered
* Element object.*/
  public Element createRadioButtonTag(Element tdCell,
                                      Integer itemId, List options,
                                      Integer tabNumber, boolean includeLabel,
                                      String dbValue, String defaultValue, boolean isHorizontal) {
    Element element;
    for (Object responseOptBean : options) {
      element = this.initializeInputElement("radio",itemId,tabNumber);
      String value =  ((ResponseOptionBean)responseOptBean).getValue();
      element.setAttribute("value",value );
      //It's checked if its value equals the DB value
      if(dbValue != null && value.equalsIgnoreCase(dbValue)) {
        ((ResponseOptionBean)responseOptBean).setSelected(true);
      }  else  if(defaultValue != null && value.equalsIgnoreCase(defaultValue)) {
        ((ResponseOptionBean)responseOptBean).setSelected(true);
      }
      if( ((ResponseOptionBean)responseOptBean).isSelected() ) {
        element.setAttribute("checked","checked");      }
      //dealing with IE/repetition model library bug
      if(isHorizontal)  {
        element.setAttribute("onclick",
          "if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'horizontal');}");
      } else {
        element.setAttribute("onclick",
          "if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'vertical');}");
      }

      tdCell.addContent(element);
      tdCell.addContent(" ");
      if(includeLabel) {
        tdCell.addContent(((ResponseOptionBean)responseOptBean).getText());
       // tdCell.addContent(new Element("br"));
      }
      //if the response_layout property is not "horizontal", then add a <br> tag
      if(! isHorizontal) {
      tdCell.addContent(new Element("br"));   }
    }
    return tdCell;
  }
  /* This method generates a single select tag for a cell inside an HTML table. Like the
* other methods, the user passes in a reference to the Element object, and the
* object receives new attributes and content. Then the method returns the altered
* Element object.
* The options List contains ResponseOptionBeans, which represent each option
* child element of the select tag. */
  public Element createSingleSelectTag(Element tdCell, Integer itemId,
                                       List options,Integer tabNumber) {
    Element element = new Element("select");
    element.setAttribute("tabindex",tabNumber.toString());
    //A repeating attribute may already have had its "name" attribute set
    if(element.getAttribute("name") == null) {
      element.setAttribute("name","item"+itemId.toString());
    }
    element.setAttribute("onChange",ONCHANGE_TEXT_INPUT);
    element.setAttribute("class","formfield");
    Element optElement;
    String optValue;
    String optText;
    for(Object responseOptBean : options) {
      optElement = new Element("option");
      optValue = ((ResponseOptionBean)responseOptBean).getValue();
      optText=((ResponseOptionBean)responseOptBean).getText();
      optElement.setAttribute("value",optValue);
      if(((ResponseOptionBean)responseOptBean).isSelected()) {
        optElement.setAttribute("selected","selected");
      }
      optElement.addContent(optText);
      element.addContent(optElement);
    }
    tdCell.addContent(element);
    return tdCell;
  }

  //YW 08-14-2007 
  /**
   * <p>Combine default_value with options of single-selected response type.
   * <p>If there is default_value, by default,
   * <ul>
   * <li>if default_value matches one of options, this option will be selected.
   * <li>otherwise, the default_value will be listed at the top of options
   * </ul>
   * <p>If there is no default_value, no modification to options required.<br/>
   * BWP added parameter  databaseValue 09/13/2007
   */
  public Element createSingleSelectTag(Element tdCell, Integer itemId,
                                       List options, Integer tabNumber, String defaultValue,
                                       String databaseValue) {
    if(databaseValue != null && databaseValue.length() > 0) {
      tdCell = createSingleSelectTag(tdCell, itemId, options, tabNumber);
      Element select = tdCell.getChild("select");
      if(select != null) {
        List<Element>  optElements = select.getChildren("option");
        String optVal="";
        for(Element opts : optElements) {
          optVal = opts.getAttribute("value").getValue();
          if(opts.getAttribute("selected") != null) {
            opts.removeAttribute("selected");
          }
          if(optVal.equalsIgnoreCase(databaseValue)){
            opts.setAttribute("selected","selected");
          }
        }
      }
      return tdCell;
    }
    int selectedOption = -1;
    boolean foundMatch = false;
    boolean printDefault = false;
    //check if an option has been selected
    for(int i=0; i<options.size(); ++i) {
      ResponseOptionBean option = (ResponseOptionBean)options.get(i);
      if(option.isSelected()) {
        selectedOption = i;
        break;
      }
    }
    //handle default_value
    if(defaultValue.length()>0) {
      printDefault = true;
      for(int i=0; i<options.size(); ++i) {
        ResponseOptionBean option = (ResponseOptionBean)options.get(i);
        if(defaultValue.equalsIgnoreCase(option.getText()) || defaultValue.equalsIgnoreCase(option.getValue())) {
          if(selectedOption == -1) {
            selectedOption = i;
          }
          printDefault = false;
          foundMatch = true;
          break;
        }
      }
    }
    //modify options
    List<ResponseOptionBean> op = new ArrayList<ResponseOptionBean>();
    if(!foundMatch) {
      if(printDefault) {
        ResponseOptionBean ro = new ResponseOptionBean();
        ro.setText(defaultValue);
        ro.setValue("");
        op.add(ro);
        op.addAll(options);
      }
    } else {
      ((ResponseOptionBean)options.get(selectedOption)).setSelected(true);
    }

    if(op.size()>0) {
      tdCell = createSingleSelectTag(tdCell, itemId, op, tabNumber);
    } else {
      tdCell = createSingleSelectTag(tdCell, itemId, options, tabNumber);
    }

    return tdCell;
  }


  public Element createMultiSelectTag(Element tdCell, Integer itemId,
                                      List options, Integer tabNumber, String dbValue,
                                      String defaultValue) {
    //Database values are Strings separated by spaces or commas as
    //in "meeny moe NASK" or "meeny,moe,NASK"
    String[] arrayOfValues= new String[]{};
    boolean hasDBValue=false;
    boolean hasDefaultValue=false;
    List<String> dbValues = new ArrayList<String>();
    List<String> defValues = new ArrayList<String>();
    if(dbValue != null && dbValue.length() > 0) {
      dbValues= new ArrayList<String>();
      arrayOfValues = handleSplitString(dbValue);
      for(String subVal : arrayOfValues) {
        dbValues.add(subVal);
      }
      hasDBValue=true;
    }
    if(defaultValue != null && defaultValue.length() > 0) {
      defValues= new ArrayList<String>();
      arrayOfValues = handleSplitString(defaultValue);
      for(String subVal : arrayOfValues) {
        defValues.add(subVal);
      }
      hasDefaultValue=true;
    }
    Element element = new Element("select");
    element.setAttribute("tabindex",tabNumber.toString());
    //A repeating attribute may already have had its "name" attribute set
    if(element.getAttribute("name") == null) {
      element.setAttribute("name","item"+itemId.toString());
    }
    element.setAttribute("multiple","multiple");
    //start out with two visible
    element.setAttribute("size","2");
    Element optElement;
    String optValue;
    String optText;
    for(Object responseOptBean : options) {
      optElement = new Element("option");
      optValue = ((ResponseOptionBean)responseOptBean).getValue();
      optText = ((ResponseOptionBean)responseOptBean).getText();
      optElement.setAttribute("value",optValue);
      if (hasDBValue){
        if(dbValues.contains(optValue)) {
          optElement.setAttribute("selected","selected");
        }
      }  else if(hasDefaultValue){
        if(defValues.contains(optValue)) {
          optElement.setAttribute("selected","selected");
        }
      } else {
        if(((ResponseOptionBean)responseOptBean).isSelected()) {
          optElement.setAttribute("selected","selected");
        }
      }
      optElement.addContent(optText);
      element.addContent(optElement);
    }
    tdCell.addContent(element);
    return tdCell;
  }
  /* This method generates a textarea tag for a cell inside an HTML table. Like the
  * other methods, the user passes in a reference to the Element object, and the
  * object receives new attributes and content. Then the method returns the altered
  * Element object.*/
  public Element createTextareaTag(Element tdCell, Integer itemId,
                                   Integer tabNumber, String dbValue, String defaultValue){
    Element element = new Element("textarea");
    element.setAttribute("tabindex",tabNumber.toString());
    //A repeating attribute may already have had its "name" attribute set
    if(element.getAttribute("name") == null) {
      element.setAttribute("name","item"+itemId.toString());
    }
    element.setAttribute("onChange",ONCHANGE_TEXT_INPUT);
    element.setAttribute("rows","5");
    if(dbValue != null && dbValue.length() > 0) {
      element.addContent(dbValue);
    }  else if (defaultValue != null && defaultValue.length() > 0){
      element.addContent(defaultValue);
    } else {
      element.addContent(new NbspaceContent());
    }

    tdCell.addContent(element);
    return tdCell;
  }
  //Initialize an input element with its input type, tab number, and name attributes
  //This method returns a JDOM Element object representing an input tag
  private Element initializeInputElement(String inputType,Integer itemId,
                                         Integer tabNumber) {
    Element element = new Element("input");
    element.setAttribute("type",inputType);
    element.setAttribute("tabindex",tabNumber.toString());
    //A repeating attribute may already have had its "name" attribute set
    if(element.getAttribute("name") == null) {
      element.setAttribute("name","item"+itemId.toString());
    }
    if(! inputType.equalsIgnoreCase("radio")) {
      element.setAttribute("onChange",ONCHANGE_TEXT_INPUT);
    } else {
      element.setAttribute("onChange",ONCHANGE_TEXT_INPUT_RADIOS);

    }
    return element;

  }
  //Create a Date widget for a cell, using the same attribute values and
  //elements of the exisiting OpenClinica JSPs
  //Returns an Element representing an "a href" tag containing an img tag
  public Element getDateWidgetForCell(Integer itemId){
    Element href = new Element("a");
    href.setAttribute("href","#");
    StringBuilder sbuilder = new StringBuilder(
      "var sib = getSib(this.previousSibling);cal1.select(sib,'anchor");
    sbuilder.append(itemId).append("','MM/dd/yyyy'); return false;");
    href.setAttribute("onClick",sbuilder.toString());
    href.setAttribute("name","anchor"+itemId);
    href.setAttribute("id","anchor"+itemId);
    Element img = new Element("img");
    img.setAttribute("src","images/bt_Calendar.gif");
    img.setAttribute("alt","Show Calendar");
    img.setAttribute("title","Show Calendar");
    img.setAttribute("border","0");
    href.addContent(img);
    return href;
  }
  //Create a span element with a class attribute referring to the "alert" class.
  //This method returns the altered Element object, which contains the span
  //element as content
  public Element createRequiredAlert(Element tdCell) {
    Element alertReq = new Element("span");
    alertReq.setAttribute("class","alert");
    alertReq.addContent("*");
    tdCell.addContent(alertReq);
    return tdCell;
  }
  //This method creates an "a href" element that contains an img element.
  //the link element is designed to generate a discrepancy note. The method
  //uses the same markup as the existing discrepancy-note related JSPs.
  public Element createDiscrepancyNoteSymbol(Integer numDiscrepancyNotes,
                                             Integer tabNumber,
                                             Integer itemDataId,
                                             Integer itemId) {
    Element ahref = new Element("a");
    ahref.setAttribute("tabindex",(tabNumber + 1000)+"");
    ahref.setAttribute("href","#");
    StringBuilder clickValue=new StringBuilder("openDNoteWindow('CreateDiscrepancyNote?id=<c:out value=");
    clickValue.append(itemDataId).append("/>&name=itemData&field=input<c:out value=");
    clickValue.append(itemId).append(" />&column=value','spanAlert-input<c:out value=");
    clickValue.append(itemId).append("/>'); return false;");
    ahref.setAttribute("onClick",clickValue.toString());

    Element img = new Element("img");
    img.setAttribute("name","flag_input"+itemId);
    String fileName = (numDiscrepancyNotes > 0 ? "icon_Note.gif" : "icon_noNote.gif");
    img.setAttribute("src","images/"+fileName);
    img.setAttribute("border","0");
    img.setAttribute("alt","Discrepancy Note");
    img.setAttribute("title","Discrepancy Note");
    ahref.addContent(img);
    return ahref;
  }
  //This method returns a span element containing any error messages
  //associated with a particular input tag.
  public Element createInputErrorMessage(Map<Object,ArrayList> messages,
                                         Integer itemId){
    String key = "input"+itemId;
    ArrayList _messages = messages.get(key);
    String errMsg="";
    for(Object msg: _messages)  {
      errMsg = (String) msg;
    }
    Element msgSpan = new Element("span");
    msgSpan.setAttribute("id","spanAlert-"+key);
    msgSpan.setAttribute("class","alert");
    msgSpan.addContent(errMsg);
    return msgSpan;
  }

  private String[] handleSplitString(String param) {
    if(param == null )  { return new String[]{}; }
    String[] values = new String[]{};
    if(param.contains(",")){
      values = param.split(",");
    }  else {
      values = param.split(" ");
    }
    //trim array contents
    for(int i = 0; i < values.length;i++) {
      values[i] = values[i].trim();
    }
    return values;
  }

}
