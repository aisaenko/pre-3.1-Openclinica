/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.admin;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.akaza.openclinica.dao.admin.CRFDAO;

/**
 * A PSC activity object exported from OpenClinica CRF
 * @author jxu
 *
 */
public class CRFActivityBean {
  
  private ArrayList<String> xmlOutput;
  private DataSource ds;
 
  private String indent = "    ";
  //private boolean dataReady = false;
  private static String XML_HEADING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  private static String SOURCES_HEADING = "<sources xmlns=\"http://bioinformatics.northwestern.edu/ns/psc\" " +
                 "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                 "xsi:schemaLocation=\"http://bioinformatics.northwestern.edu/ns/psc http://bioinformatics.northwestern.edu/ns/psc/psc.xsd\">";
  private static String SOURCE ="<source name=\"OpenClinica\">";
  private static String SOURCE_END="</source>";
  private static String SOURCES_END ="</sources>";
  public CRFActivityBean(DataSource ds) {
    xmlOutput = new ArrayList<String>();
    this.ds = ds;
    
  } 

  public void createActivityXML(){
      xmlOutput.clear();
      xmlOutput.add(XML_HEADING);
      String currentIndent = indent;    
      
      currentIndent = xmlAdd("", SOURCES_HEADING);      
      
      currentIndent = xmlAdd(currentIndent,SOURCE);
      
      CRFDAO cdao = new CRFDAO(this.ds);
      ArrayList<CRFBean> crfs = (ArrayList<CRFBean>)cdao.findAll();     
      for (int i=0; i<crfs.size();i++){
        CRFBean crf = crfs.get(i);
        if (crf.isActive()){
          int j = i+1;
          String content = "<activity name=\"" + crf.getName() + "\" type-id=\"5\" code=\"" + j + "\" />";
          xmlAdd(currentIndent,content);
        }
      }
      
       xmlAdd(indent, SOURCE_END);
       xmlAdd("",SOURCES_END);
  }

  /**
   * @return the ds
   */
  public DataSource getDs() {
    return ds;
  }

  /**
   * @param ds the ds to set
   */
  public void setDs(DataSource ds) {
    this.ds = ds;
  }

  /**
   * @return the xmlOutput
   */
  public ArrayList<String> getXmlOutput() {
    return xmlOutput;
  }

  /**
   * @param xmlOutput the xmlOutput to set
   */
  public void setXmlOutput(ArrayList<String> xmlOutput) {
    this.xmlOutput = xmlOutput;
  }
  
  public String xmlAdd(String currentIndent, String content) {
    xmlOutput.add(currentIndent + content);
    currentIndent = currentIndent + indent;
    return currentIndent;
  }

}
