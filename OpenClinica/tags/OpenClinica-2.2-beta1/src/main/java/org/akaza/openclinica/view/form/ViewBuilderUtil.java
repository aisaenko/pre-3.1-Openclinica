package org.akaza.openclinica.view.form;

import org.jdom.*;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;

import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;

/**
 * A utility class containing various methods that are used for generating Web form views.
 *
 */
public class ViewBuilderUtil {

  public boolean hasResponseLayout(List<DisplayItemBean> displayBeans) {
    String responseName;
    for (DisplayItemBean dBean : displayBeans) {
      responseName = dBean.getMetadata().getResponseSet().getResponseType().getName();
      if (responseName == null) {
        responseName = "";
      }
      if (dBean.getMetadata().getResponseLayout().equalsIgnoreCase("horizontal") &&
        (responseName.equalsIgnoreCase("radio") ||
          responseName.equalsIgnoreCase("checkbox"))) {
        return true;
      }
    }
    return false;
  }

  public int calcNumberofColumns(
    DisplayItemGroupBean displayGroup) {
    if (displayGroup == null || displayGroup.getItems().size() == 0) {
      return 0;
    }
    int columns = 0;
    String responseName;
    for (DisplayItemBean disBean : displayGroup.getItems()) {
      responseName = disBean.getMetadata().getResponseSet().
        getResponseType().getName();
      if (responseName.equalsIgnoreCase("radio") ||
        responseName.equalsIgnoreCase("checkbox")) {
        columns += disBean.getMetadata().getResponseSet().getOptions().size();
      } else {
        columns++;
      }

    }
    return columns;
  }

  public void addRemoveRowControl(Element row, String repeatParentId) {
    Element repeatCell = new Element("td");
    repeatCell = this.setClassNames(repeatCell);
    repeatCell.addContent(RepeatManager.
      createrepeatButtonControl("remove", repeatParentId));
    row.addContent(repeatCell);

  }

  public void createAddRowControl(Element tbody,
                                  String repeatParentId, int columnNumber) {
    Element addButtonRow = new Element("tr");
    Element addButtonCell = new Element("td");
    addButtonCell = this.setClassNames(addButtonCell);
    addButtonCell.setAttribute("colspan", columnNumber + "");
    addButtonCell.addContent(RepeatManager.createrepeatButtonControl("add",
      repeatParentId));
    addButtonRow.addContent(addButtonCell);
    tbody.addContent(addButtonRow);
  }

  public Element setClassNames(Element styledElement) {
    String cssClasses = CssRules.getClassNamesForTag(styledElement.getName());
    return cssClasses.length() == 0 ?
      styledElement : styledElement.setAttribute("class", cssClasses);
  }

  public void showTitles(Element divRoot, SectionBean sectionBean) {
    //Don't create an Element if the Section does not have
    //a title, subtitle, or instructions
    if (divRoot == null || (sectionBean.getInstructions().length() == 0 &&
      sectionBean.getTitle().length() == 0 &&
      sectionBean.getSubtitle().length() == 0)) {
      return;
    }

    Element table = new Element("table");
    table.setAttribute("class", CssRules.
      getClassNamesForTag("table section"));
    table.setAttribute("width", "100%");
    divRoot.addContent(table);

    if (sectionBean.getTitle().length() > 0) {
      addTitles(sectionBean, table, "Title");
    }
    if (sectionBean.getSubtitle().length() > 0) {
      addTitles(sectionBean, table, "Subtitle");
    }
    if (sectionBean.getInstructions().length() > 0) {
      addTitles(sectionBean, table, "Instructions");
    }
  }

  public void addTitles(SectionBean sbean, Element table,
                        String content) {
    Element tr = new Element("tr");
    tr.setAttribute("class", "aka_stripes");
    table.addContent(tr);
    Element title = new Element("td");
    title.setAttribute("nowrap", "nowrap");
    title.setAttribute("class", "aka_table_cell_left");
    Element bold = new Element("b");
    bold.addContent(content + ":  ");
    title.addContent(bold);
    if (content.equalsIgnoreCase("subtitle")) {
      title.addContent(sbean.getSubtitle());
    } else if (content.equalsIgnoreCase("title")) {
      title.addContent(sbean.getTitle());
    } else if (content.equalsIgnoreCase("instructions")) {
      title.addContent(sbean.getInstructions());
    }
    tr.addContent(title);
  }

  /**
   *
   * @param sortedDataMap
   * @param rowContentBeans
   * @param tabIndex
   * @param repeatParentId
   * @param hasDiscrepancyMgt
   * @return
   */
  public List<Element> generatePersistentMatrixRows(
    SortedMap<Integer, List<ItemDataBean>> sortedDataMap,
    List<DisplayItemBean> rowContentBeans,
    int tabIndex, String repeatParentId,
    boolean hasDiscrepancyMgt) {


    List<Element> newRows = new ArrayList<Element>();
    List<ItemDataBean> tempList  = new ArrayList<ItemDataBean>();
    Element tr;
    Element td;
    CellFactory cellFactory = new CellFactory();
    RepeatManager repeatManager = new RepeatManager();
    String responseName;
    //for each repeated row of the matrix table..
    for(Integer ordinal : sortedDataMap.keySet()) {
      tr = new Element("tr");
      tempList = sortedDataMap.get(ordinal);

      for(DisplayItemBean disItemBean : rowContentBeans) {
        for(ItemDataBean itemDBean : tempList) {
          if(disItemBean.getItem().getId() == itemDBean.getItemId()) {
            disItemBean.setData(itemDBean);
            break;
          }
        }
        responseName= disItemBean.getMetadata().getResponseSet().
          getResponseType().getName();
        td = new Element("td");
        td = this.setClassNames(td);
        td = cellFactory.createCellContents(td, responseName,
          disItemBean, ++tabIndex, hasDiscrepancyMgt);
        //In this case, the parent id looks like parentId_1, etc.
        String forcedParId = (ordinal-1) +"";
        td = repeatManager.addChildRepeatAttributes(td, repeatParentId,
          disItemBean.getItem().getId(), forcedParId);
        tr.addContent(td);
      }
     this.addRemoveRowControl(tr,repeatParentId);
     newRows.add(tr);
    }

    return newRows;
  }

}
