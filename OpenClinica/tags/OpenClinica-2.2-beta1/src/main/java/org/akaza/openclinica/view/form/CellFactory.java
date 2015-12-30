package org.akaza.openclinica.view.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.jdom.Element;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;

/**
 * The class responsible for adding different types of input elements or tags
 * to an Element as content. The Element represents an HTML "TD"  or td tag. This class
 * adds content to the cell such as input fields and select lists.
 * HorizontalFormBuilder uses this class to
 * help build an HTML form, for instance.
 */
public class CellFactory {
  //The object to which this class delegates the creation of
  //input elements or tags.
  private DataEntryInputGenerator inputGenerator = new
    DataEntryInputGenerator();
  //Two static fields used by addTextToCell() to position text inside the
  //table cell
  public final static String RIGHT = "right";
  public final static String LEFT = "left";
  /** Take an Element object and add various types of input tags to it as
   * element content.
   * @param td A JDOM Element object representing the td cell
   * @param responseName a String reprsenting the type of XHTML input
   * as in text, textarea, or select
   * @param displayBean The DisplayItemBean associated with the input element.
   * @param tabIndex The tab index to use for the input element.
   * @param hasDiscrepancyMgt
   * @return The altered td Element with its XHTML content. */
  public Element createCellContents(Element td,
                                    String responseName,
                                    DisplayItemBean displayBean,
                                    Integer tabIndex, boolean hasDiscrepancyMgt){
    /*if(! "".equalsIgnoreCase(displayBean.getMetadata().getQuestionNumberLabel())) {
      td.addContent(displayBean.getMetadata().getQuestionNumberLabel()+" ");
    }*/
    if(responseName.equalsIgnoreCase("text")){
      td = inputGenerator.createTextInputTag(td,
        displayBean.getItem().getId(),
        tabIndex,
        displayBean.getMetadata().getDefaultValue(),
        (displayBean.getItem().getItemDataTypeId()==9),
        displayBean.getData().getValue());
    }  else if( responseName.equalsIgnoreCase("textarea")){
      td = inputGenerator.createTextareaTag(td,displayBean.getItem().getId(),
        tabIndex, displayBean.getData().getValue(),
        displayBean.getMetadata().getDefaultValue());

    }  else if( responseName.equalsIgnoreCase("checkbox")){
      td = inputGenerator.createCheckboxTag(td,displayBean.getItem().getId(),
        displayBean.getMetadata().getResponseSet().getOptions(),tabIndex, true,
        displayBean.getData().getValue(),
        displayBean.getMetadata().getDefaultValue(),
        ("horizontal".equalsIgnoreCase(displayBean.getMetadata().getResponseLayout())));

    }  else if( responseName.equalsIgnoreCase("radio")){
      td = inputGenerator.createRadioButtonTag(td,displayBean.getItem().getId(),
        displayBean.getMetadata().getResponseSet().getOptions(),tabIndex, true,
        displayBean.getData().getValue(),
        displayBean.getMetadata().getDefaultValue(),
        ("horizontal".equalsIgnoreCase(displayBean.getMetadata().getResponseLayout())));

    }   else if( responseName.equalsIgnoreCase("single-select")){
      //YW 08-14-2007 <<combine default_value with options
      td = inputGenerator.createSingleSelectTag(td,displayBean.getItem().getId(),
        displayBean.getMetadata().getResponseSet().getOptions(),tabIndex,
        displayBean.getMetadata().getDefaultValue(),
        displayBean.getData().getValue());
    }   else if( responseName.equalsIgnoreCase("multi-select")){
      td = inputGenerator.createMultiSelectTag(td,displayBean.getItem().getId(),
        displayBean.getMetadata().getResponseSet().getOptions(),tabIndex,
        displayBean.getData().getValue(), displayBean.getMetadata().getDefaultValue());
    }

    if(displayBean.getMetadata().isRequired()) {
      td = inputGenerator.createRequiredAlert(td);
    }

    if(hasDiscrepancyMgt) {
      Element href;
      href = inputGenerator.createDiscrepancyNoteSymbol(displayBean.getNumDiscrepancyNotes(),
        tabIndex,displayBean.getData().getId(),
        displayBean.getItem().getId());
      if(href != null) td.addContent(href);
    }
    return td;
  }

  public Element[] createCellContentsForChecks(
    String responseName,
    DisplayItemBean displayBean,
    Integer optionsLength,
    Integer tabIndex){
    Element[] tdElements = new Element[optionsLength];
    Element td;
    List<ResponseOptionBean> list = new ArrayList<ResponseOptionBean>();
    int indx = 0;
    ResponseOptionBean reOptBean;

    for(Iterator iter = displayBean.getMetadata().getResponseSet().getOptions().
      iterator();iter.hasNext();)  {
      reOptBean = (ResponseOptionBean) iter.next();
      list.add(reOptBean);
      td = new Element("td");
      if( responseName.equalsIgnoreCase("checkbox")){
        td = inputGenerator.createCheckboxTag(td,
          displayBean.getItem().getId(),
          list,tabIndex, false, displayBean.getData().getValue(),
          displayBean.getMetadata().getDefaultValue(), false);
      } else {
        //Final true value is for horizontal radios
        td = inputGenerator.createRadioButtonTag(td,
          displayBean.getItem().getId(),
          list,tabIndex, false, displayBean.getData().getValue(),
          displayBean.getMetadata().getDefaultValue(), true);
      }
      tdElements[indx] = td;
      indx++;
      list.clear();
    }
    return tdElements;
  }
  //This method adds text content to the Element represented by the parameter
  //cell. The method places the text content inside a span element, then adds the
  //span to the cell, returning the altered Element or cell.
  public Element addTextToCell(Element cell, String text,
                               String textPositionRelativeToInput ){
    if (textPositionRelativeToInput.equalsIgnoreCase(LEFT)){
      Element el = new Element("span");
      el.setAttribute("class","aka_text_block_shared");
      el.addContent(text+" ");
      cell.addContent(0,el);
    }   else  if (textPositionRelativeToInput.equalsIgnoreCase(RIGHT)){
      Element el = new Element("span");
      el.setAttribute("style","aka_text_block_shared");
      el.addContent(" "+text);
      cell.addContent(el);
    }
    return cell;
  }
}
