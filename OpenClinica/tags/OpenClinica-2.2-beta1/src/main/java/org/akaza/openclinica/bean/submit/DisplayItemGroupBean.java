package org.akaza.openclinica.bean.submit;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: bruceperry
 * Date: May 7, 2007
 */
public class DisplayItemGroupBean {
  private ItemGroupBean itemGroupBean;
  private ItemGroupMetadataBean groupMetaBean;
  private List<DisplayItemBean> items;
  private int ordinal;//not used so far
  private String editFlag;//add, edit or remove
  
  
  public DisplayItemGroupBean() {
    this.itemGroupBean = new ItemGroupBean();
    this.groupMetaBean = new ItemGroupMetadataBean();
    this.items = new ArrayList<DisplayItemBean>();
    ordinal =0;
    editFlag="";
  }

  public ItemGroupBean getItemGroupBean() {
    return itemGroupBean;
  }

  /**
   * @return the groupMetaBean
   */
  public ItemGroupMetadataBean getGroupMetaBean() {
    return groupMetaBean;
  }

  /**
   * @param groupMetaBean the groupMetaBean to set
   */
  public void setGroupMetaBean(ItemGroupMetadataBean groupMetaBean) {
    this.groupMetaBean = groupMetaBean;
  }

  public void setItemGroupBean(ItemGroupBean formGroupBean) {
    this.itemGroupBean = formGroupBean;
  }

  public List<DisplayItemBean> getItems() {
    return items;
  }

  public void setItems(List<DisplayItemBean> items) {
    this.items = items;
  }

  /**
   * @return the ordinal
   */
  public int getOrdinal() {
    return ordinal;
  }

  /**
   * @param ordinal the ordinal to set
   */
  public void setOrdinal(int ordinal) {
    this.ordinal = ordinal;
  }

  /**
   * @return the editFlag
   */
  public String getEditFlag() {
    return editFlag;
  }

  /**
   * @param editFlag the editFlag to set
   */
  public void setEditFlag(String editFlag) {
    this.editFlag = editFlag;
  }
  
  
 
    
}
