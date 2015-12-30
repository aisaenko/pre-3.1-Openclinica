/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 * Created on Oct., 2006
 */  

package org.akaza.openclinica.bean.extract;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;


import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.extract.SasNameValidationBean;



/**
 * @auther ywang
 * 
 * Create CDISC ODM XML format for OpenClinica data based on CDISC ODM Version 1.2.1.
 * 
 * <p>At this point, it generates snapshot type file. It includes some requires elements 
 *    and attributes of general node, metadata node and clinical node. It generates
 *    one MetaDataVersion in one file. 
 * <p>The generated data file is ready for PROC CDISC to create a SAS data set. 
 * <p>For this class to work properly, another data type (an addition row) is added
 *	  as "text/plain" in the table export_format
 * 
 */



public class CdiscOdmXmlBean {
  private ArrayList xmlOutput;
  private DataSource ds;
  private String schemaLocation;
  private String metaDataVersionOID;
  private String metaDataVersionName;
  private String metaDataVersionPrevStudy;
  private String metaDataVersionPrevOID;
  private String indent = "    ";
  //private boolean dataReady = false;
  protected static String ODM_FileType = "Snapshot";
  private SimpleDateFormat sdf_datetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private String xmlns = "http://www.cdisc.org/ns/odm/v1.2";
  private String xmlHeading = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  //private SimpleDateFormat sdf_sqldate = new SimpleDateFormat("yyyy-MM-dd");
  private SasNameValidationBean sasname = new SasNameValidationBean();
  private String locale_df_string = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
  

  public CdiscOdmXmlBean(DataSource ds, String schemaLocation, String mdvOID, String mdvName, String mdvPrevStudy, String mdvPrevOID) {
    xmlOutput = new ArrayList();
    this.ds = ds;
    this.schemaLocation = schemaLocation;
    if(mdvOID != null && mdvOID.length()>0) {
    	this.metaDataVersionOID = StringEscapeUtils.escapeXml(mdvOID);
    } else {
    	this.metaDataVersionOID = "v1.0.0";
    }

    if(mdvName != null && mdvName.length()>0) {
    	this.metaDataVersionName = StringEscapeUtils.escapeXml(mdvName);  
	}else {
		this.metaDataVersionName = "MetaDataVersion_v1.0.0";
	}
    this.metaDataVersionPrevStudy = StringEscapeUtils.escapeXml(mdvPrevStudy);
    this.metaDataVersionPrevOID = StringEscapeUtils.escapeXml(mdvPrevOID);
  } 
  
  /**
   * This method constructs all information about an ODM tag and stores them in the form of
   * Strings in the ArrayList xmlOutput. This method is for Snapshot filetype and still under
   * construction so that some attributes are not available.
   * User should call this method to start the processing of converting relational-database-info 
   * into XML-formatted info
   * 
   * @param eb	ExtractBean
   */
  public void createCdiscOdmXML(ExtractBean eb){
	  xmlOutput.clear();
	  xmlOutput.add(this.xmlHeading);

	  //String root_line = "<ODM xmlns=\"" + this.xmlns + "\" xmlns:ds=\"http://www..w3.org/2000/09/xmldsig#\" "
	  String root_line = "<ODM xmlns=\"" + this.xmlns + "\" " 
	  					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " 
	  					+ "xsi:schemaLocation=\"" + this.xmlns + " " + this.schemaLocation + "\" ";
	  root_line += "ODMVersion=\"1.2\" " +
	  			"FileOID=\"" + eb.getDataset().getId() + "\" " +
	  			"FileType=\"" + ODM_FileType + "\" ";
	  //attribute 'Description' - optional;
	  //So first check if dataset has description
	  String temp = eb.getDataset().getDescription();
	  if(temp.trim().length() > 0) {
		  root_line += "Description=\"" + StringEscapeUtils.escapeXml(temp).trim() + "\" ";
	  }
	  //here the file OID is automatically set to be the dataset ID 
	  root_line += "CreationDateTime=\"" + sdf_datetime.format(new Date()) + "\" >";
	          //for jdk1.5
	          //"CreationDateTime=\"" + sdf_datetime.format(System.currentTimeMillis()) + "\" >";
	  String ending_line = "</ODM>";
	 
	  //first of all, set up the information about the ODM node, including schema file
	  xmlOutput.add(root_line);
	  //add the contents here in order
	  //1) the information about Study
	  addNodeStudy(eb, indent);
	  //2) the information about administrative data
	  addNodeAdminData(eb, indent);
	  //3) the information about reference data
	  addNodeReferenceData(eb, indent);
	  //4) the information about clinical Data
	  addNodeClinicalData(eb,indent);

	  //close the root tag
	  xmlOutput.add(ending_line);

	  //this.dataReady = true;
  }

  //-----------------  Methods related to Study node -----------------------
  /*
   * This method constructs a XML node of Study
   * 
   * @param eb	ExtractBean
   * @param	currentIndent  String
   */
  private void addNodeStudy(ExtractBean eb, String currentIndent){
	  DatasetBean dataset = eb.getDataset();

	  /*
	   * I hope that the studyID got from dataset is exactly the same as the studyID in currentstudy,
	   * this is what I am counting on.
	   */
	  //the starting info of the node Study
	  add(currentIndent, "<Study OID=\"" + dataset.getStudyId() + "\">");
	  //1) global variables
	  addStudyGlobalVariables(eb, currentIndent+indent);
	  //2) basic definitions. 
	  addStudyBasicDefinitions(eb, currentIndent+indent);
	  //3) metadata version
	  addStudyMetaDataVersion(eb, currentIndent+indent);
	  //the ending info of the node Study
	  add(currentIndent,"</Study>");
  }

  private void addStudyGlobalVariables(org.akaza.openclinica.bean.extract.ExtractBean eb, String currentIndent){
	  add(currentIndent, "<GlobalVariables>");
	  //OpenClinica ExtractBean.java uses parentstudyname for "Study Name"
	  add(currentIndent + indent, "<StudyName>" + StringEscapeUtils.escapeXml(eb.getParentStudyName()) + "</StudyName>");
	  add(currentIndent + indent, "<StudyDescription>");
	  add(currentIndent + indent + indent, StringEscapeUtils.escapeXml(eb.getParentStudySummary()));
	  add(currentIndent + indent, "</StudyDescription>");
	  add(currentIndent + indent, "<ProtocolName>" + StringEscapeUtils.escapeXml(eb.getParentProtocolId()) + "</ProtocolName>");
	  add(currentIndent, "</GlobalVariables>");
  }

  private void addStudyBasicDefinitions(org.akaza.openclinica.bean.extract.ExtractBean eb, String currentIndent){
	  
  }

  private void addStudyMetaDataVersion(org.akaza.openclinica.bean.extract.ExtractBean eb, String currentIndent){
	  add(currentIndent, "<MetaDataVersion OID=\"" + metaDataVersionOID + "\" Name=\"" + metaDataVersionName + "\">");
	  //for <Include>, 
	  //1. In order to have <Include>, previous metadataversionOID must be given.
	  //2. If there is no previous study, then previous study OID is as the same as the current study OID
	  //3. there is no Include if both previous study and previous metadataversionOID are empty
	  String line;
	  if(metaDataVersionPrevOID != null && metaDataVersionPrevOID.length()>0) {
		  if(metaDataVersionPrevStudy!=null && metaDataVersionPrevStudy.length()>0) {
			  line = "<Include StudyOID =\"" + metaDataVersionPrevStudy +"\"";
		  } else {
			  line = "<Include StudyOID =\"" + eb.getDataset().getStudyId() +"\"";
		  }
		  line += " MetaDataVersionOID=\"" + metaDataVersionPrevOID + "\"/>";
		  add(currentIndent+indent, line);
	  } 
	  
	  addMetaDataVersionProtocol(eb, currentIndent+indent);
	  addMetaDataVersionStudyEventDef(eb, currentIndent+indent);
 
	  add(currentIndent, "</MetaDataVersion>");
  }
 
private void addMetaDataVersionProtocol(ExtractBean eb, String currentIndent) {
	//The protocol lists the kinds of study events that can occur within a specific version of a Study.
	//It does not relate to the subjects but the version of a study.
	add(currentIndent, "<Protocol>");
	 for(int i=1; i<eb.getNumSEDs()+1; ++i) {
		 StudyEventDefinitionBean sed = (StudyEventDefinitionBean)eb.getStudyEvents().get(i-1);
		//At this point, I don't know how to handle Mandatory. Since it is required and sed is selected, I assign it 'yes'.
		add(currentIndent+indent, "<StudyEventRef StudyEventOID=\"SE." + sed.getId() + "\" Mandatory=\"Yes\"/>");
	}
	add(currentIndent, "</Protocol>");
}

private void addMetaDataVersionStudyEventDef(ExtractBean eb, String currentIndent) {
	String line;
	
	for(int i=1; i<eb.getNumSEDs()+1; ++i) {
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean)eb.getStudyEvents().get(i-1);
		line = "<StudyEventDef OID=\"SE." + sed.getId() + "\"  Name=\"" + StringEscapeUtils.escapeXml(sed.getName()) + "\" ";
		if(sed.isRepeating()) {
			line += "Repeating=\"Yes\" ";
		} else {
			line += "Repeating=\"No\" ";
		}
		String s = sed.getType();
		if(s.toLowerCase().equals("scheduled")) {
			s = "Scheduled";
		} else if(s.toLowerCase().equals("unscheduled")) {
			s = "Unscheduled";
		} else if(s.toLowerCase().equals("common")) {
			s = "Common";
		}
			
		line += "Type=\"" + s + "\">";
		add(currentIndent, line);	
		addMetaDataVersionFormRef(eb, sed, currentIndent + indent);
		add(currentIndent, "</StudyEventDef>");
	}

	Hashtable sedCrfIgItem = getSedCrfIgItem(eb);
	addMetaDataVersionFormDef(eb,sedCrfIgItem,currentIndent);
}

private void addMetaDataVersionFormRef(ExtractBean eb, StudyEventDefinitionBean sed, String currentIndent) {
	String line;
	EventDefinitionCRFDAO edcrfdao = new EventDefinitionCRFDAO(this.ds);
	ArrayList edcrfBeans = edcrfdao.findAllActiveByEventDefinitionId(sed.getId());
	ArrayList crfBeans = (ArrayList)sed.getCrfs();
			
	for(int k=0; k<crfBeans.size(); ++k) {
		CRFBean crfBean = (CRFBean) crfBeans.get(k);
		line = "<FormRef FormOID=\"FORM." + crfBean.getId() + "\"";
		for(int n=0; n<edcrfBeans.size(); ++n) {
			EventDefinitionCRFBean edcrfbean = (EventDefinitionCRFBean)edcrfBeans.get(n);
			if(edcrfbean.getCrfId() == crfBean.getId()) {
				if(edcrfbean.isRequiredCRF()) {
					line += " Mandatory=\"Yes\"";
				} else {
					line += " Mandatory=\"No\"";
				}
				edcrfBeans.remove(n);
			}
		}
		line += "/>";
		add(currentIndent, line);
	}

}

//currently, repeating of a form is arbitrarily set as No
private void addMetaDataVersionFormDef(ExtractBean eb, Hashtable sedCrfIgItem, String currentIndent) {
	for(int i=1; i<eb.getNumSEDs()+1; ++i) {
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean)eb.getStudyEvents().get(i-1);
		for(int k=1; k<eb.getSEDNumCRFs(i)+1; ++k) {
			Hashtable crfIgItem = (Hashtable) sedCrfIgItem.get(sed.getId() + "");
			CRFBean crfBean = (CRFBean) sed.getCrfs().get(k-1);
			add(currentIndent, "<FormDef OID=\"FORM." + crfBean.getId() + "\" Name=\"" + StringEscapeUtils.escapeXml(crfBean.getName()) + "\" Repeating=\"No\">");
			String crfId = Integer.toString(crfBean.getId());
			addMetaDataVersionItemGroupRef(crfId, crfIgItem, currentIndent+indent);
			add(currentIndent, "</FormDef>");
		}
	}
	addMetaDataVersionItemGroupDef(eb, sedCrfIgItem, currentIndent);

}

//section is mapped to itemgroup.
private void addMetaDataVersionItemGroupRef(String crfId, Hashtable crfIgItem, String currentIndent) {
	String line;
	Hashtable sectionItemData = (Hashtable)crfIgItem.get(crfId + "");
	TreeSet sortedKeys = new TreeSet();
	sortedKeys.addAll(sectionItemData.keySet());
	Iterator iter = sortedKeys.iterator();
	while(iter.hasNext()) {
		String sectionID = (String)iter.next();
		boolean isMandatory = false;
		line ="<ItemGroupRef ItemGroupOID=\"IG." + sectionID;
		//If there is at least one item is required, then Mandatory of a section/ItemGroup is "Yes"
		ArrayList aa = (ArrayList)sectionItemData.get(sectionID + "");
		for(int i=0; i<aa.size(); ++i) {
			TempItemInfo tii = (TempItemInfo)aa.get(i);
			ItemFormMetadataBean ifmBean = (ItemFormMetadataBean)tii.imfb; 
			if( ifmBean.isRequired() ) {
				line += "\" Mandatory=\"Yes\"";
				isMandatory = true;
				break;
			}
		}
		if(!isMandatory) {
			line += "\" Mandatory=\"No\"";
		}
		line += "/>";
		add(currentIndent, line);
	}
		
}

private void addMetaDataVersionItemGroupDef(ExtractBean eb, Hashtable sedCrfIgItem, String currentIndent) {
	for(int i=1; i<eb.getNumSEDs()+1; ++i) {
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean)eb.getStudyEvents().get(i-1);
		Hashtable crfIgItem = (Hashtable)sedCrfIgItem.get(sed.getId() + "");
		for(int k=1; k<eb.getSEDNumCRFs(i)+1; ++k) {
			CRFBean crfBean = (CRFBean) sed.getCrfs().get(k-1);	
			Hashtable sectionItemData = (Hashtable) crfIgItem.get(crfBean.getId() + "");
			SectionDAO sdao = new SectionDAO(this.ds);
			TreeSet sortedKeys = new TreeSet();
			sortedKeys.addAll(sectionItemData.keySet());
			Iterator iter = sortedKeys.iterator();
			while(iter.hasNext()) {
				String sectionID = (String)iter.next();
				int sid = Integer.parseInt(sectionID);
				SectionBean sb = (SectionBean)sdao.findByPK(sid);
				String comment="";
				if(!sb.getInstructions().equals("")) {
					comment="\" Comment=\"" + StringEscapeUtils.escapeXml(sb.getInstructions());
				}
				add(currentIndent, "<ItemGroupDef OID=\"IG." + sb.getId() + "\" Name=\"" + StringEscapeUtils.escapeXml(sb.getLabel()) 
						+ "\" Repeating=\"No\" SASDatasetName=\"" + sasname.getValidSasName(sb.getLabel()) 
						+ comment
						+ "\">");
				addMetaDataVersionItemRef(sectionID, sectionItemData, currentIndent);
				add(currentIndent, "</ItemGroupDef>");
			}
		}
	}
	
	addMetaDataVersionItemDef(eb, sedCrfIgItem, currentIndent);
}

private void addMetaDataVersionItemRef(String sectionId, Hashtable sectionItemData, String currentIndent) {
	String line;	
	ArrayList aa = (ArrayList)sectionItemData.get(sectionId + "");	
	//ItemRef, a reference to an ItemDef as it occurs within a specific ItemGroupDef.
	for(int i=0; i<aa.size(); ++i) {
		TempItemInfo tii = (TempItemInfo)aa.get(i);
		ItemFormMetadataBean ifmBean = (ItemFormMetadataBean)tii.imfb; 
		String name = tii.itemName + "." + tii.crfId;
		line = "<ItemRef ItemOID=\"IT." + StringEscapeUtils.escapeXml(name) + "\"";
		if(ifmBean.isRequired()) {
			line +=  " Mandatory=\"Yes\"";
		} else {
			line +=  " Mandatory=\"No\"";
		}
		line += "/>";
		add(currentIndent+indent, line);
	}
}

private void addMetaDataVersionItemDef(ExtractBean eb, Hashtable sedCrfIgItem, String currentIndent) {
	ItemDAO idao = new ItemDAO(this.ds);
	for(int i=1; i<eb.getNumSEDs()+1; ++i) {
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean)eb.getStudyEvents().get(i-1);
		Hashtable crfIgItem = (Hashtable)sedCrfIgItem.get(sed.getId() + "");
		for(int k=1; k<eb.getSEDNumCRFs(i)+1; ++k) {
			CRFBean crfBean = (CRFBean) sed.getCrfs().get(k-1);	
			Hashtable sectionItemData = (Hashtable)crfIgItem.get(crfBean.getId() + "");
			TreeSet sortedKeys = new TreeSet();
			sortedKeys.addAll(sectionItemData.keySet());
			Iterator iter = sortedKeys.iterator();
			while(iter.hasNext()) {
				String sectionID = (String)iter.next();
				ArrayList aa = (ArrayList)sectionItemData.get(sectionID + "");
				for(int j=0; j<aa.size(); ++j) {
					TempItemInfo tii = (TempItemInfo)aa.get(j);
					ItemFormMetadataBean ifmBean = (ItemFormMetadataBean)tii.imfb;
					ItemBean ib = (ItemBean)idao.findByPK(ifmBean.getItemId());
					String datatype = getODMDataType(ib.getItemDataTypeId(), ifmBean);
					String header = "";
					String left = "";
					String right = "";
					if(ifmBean.getHeader().length()>0) {
						header = StringEscapeUtils.escapeXml(ifmBean.getHeader()) + ".";
					} 
					if(ifmBean.getLeftItemText().length()>0) {
						left = " " + StringEscapeUtils.escapeXml(ifmBean.getLeftItemText()) + ".";
					} 
					if(ifmBean.getRightItemText().length()>0) {
						right = " " + StringEscapeUtils.escapeXml(ifmBean.getRightItemText());
					}
					String comment="";
					if(!ib.getDescription().equals("")){
						comment="\" Comment=\"" + StringEscapeUtils.escapeXml(ib.getDescription());
					}
					//add(currentIndent, "<ItemDef OID=\"IT." + tii.itemName + "\" Name=\"" + stringCut(ifmBean.getLeftItemText()) + "\" DataType=\"" + datatype + "\" SASFieldName=\"" + sasname.getValidSasName(ifmBean.getLeftItemText()) + "\"/>" );
					String line ="";
					if(datatype.equalsIgnoreCase("text")) {
						line += "<ItemDef OID=\"IT." + StringEscapeUtils.escapeXml(tii.itemName) + "." + tii.crfId 
								+ "\" Name=\"" + StringEscapeUtils.escapeXml(tii.itemName) + "\" DataType=\"" + datatype 
								+ "\" Length=\"" + 4000 + "\" SASFieldName=\"" + sasname.getValidSasName(tii.itemName) 
								+ comment;
					}else if(datatype.equalsIgnoreCase("integer")) {
						line += "<ItemDef OID=\"IT." + StringEscapeUtils.escapeXml(tii.itemName) + "." + tii.crfId 
								+ "\" Name=\"" + StringEscapeUtils.escapeXml(tii.itemName) + "\" DataType=\"" + datatype 
								+ "\" Length=\"" + 8 + "\" SASFieldName=\"" + sasname.getValidSasName(tii.itemName) 
								+ comment;
					}else  {
						line += "<ItemDef OID=\"IT." + StringEscapeUtils.escapeXml(tii.itemName) + "." + tii.crfId 
								+ "\" Name=\"" + StringEscapeUtils.escapeXml(tii.itemName) + "\" DataType=\"" + datatype 
								+ "\" SASFieldName=\"" + sasname.getValidSasName(tii.itemName) 
								+ comment;
					}
					if(!(header.equals("") && left.equals("") && right.equals(""))) {
						line += "\">";
						add(currentIndent, line);
						add(currentIndent + indent, "<Question><TranslatedText>");
						add(currentIndent + indent + indent, header + left + right);
						add(currentIndent + indent, "</TranslatedText></Question>");
						add(currentIndent, "</ItemDef>");
					} else {
						line += "\"/>";
						add(currentIndent, line);
					}
				}
			}
		}
	}
}	

/***---------- Methods related to AdminData nodes. However, it is not available currently ---------**/
  private void addNodeAdminData(ExtractBean eb, String currentIndent){

  }
/***---------- Methods related to ReferenceData nodes. However, it is not available currently ---------**/
  private void addNodeReferenceData(ExtractBean eb, String currentIndent){
	  
  }
  

  
// ---------- Methods related to ClinicalData nodes -------------------
  /*
   * @param	eb ExtractBean
   * @param	currentIndent	String 	the indent space for the clinical data nodes
   */
  private void addNodeClinicalData(ExtractBean eb, String currentIndent){
	  int studyId = eb.getDataset().getStudyId();
	  HashMap groupNames = eb.getGroupNames();
	  //add the starting information about the clinical data node
	  add(currentIndent,"<ClinicalData StudyOID=\"" + studyId + "\" MetaDataVersionOID=\"" + metaDataVersionOID + "\">");
	  ArrayList ss = eb.getSubjects();
	  ItemDAO idao = new ItemDAO(this.ds);
	  for(int w=1; w<ss.size()+1; ++w){
		 StudySubjectBean studySubjectBean = (StudySubjectBean) ss.get(w-1);
	     //the starting tag information about the study subject tag
	  	  add(currentIndent+indent, "<SubjectData SubjectKey=\"" + studySubjectBean.getLabel() + "\">");
	  	  //obtain the studysubjectId first
	  	  int studysubjectId = studySubjectBean.getId();
	  	  //get studyEventDefinition of subjects;
	  	  for(int i=1; i<eb.getNumSEDs()+1; ++i) {
	  		  StudyEventDefinitionBean sed = (StudyEventDefinitionBean)eb.getStudyEvents().get(i-1);
	  		  int definitionId = sed.getId();
	  		  for(int j=1; j<eb.getSEDNumSamples(i)+1; ++j) {
	  			  String line = "<StudyEventData StudyEventOID=\"SE." + definitionId + "\" ";
		  		  if(sed.isRepeating()) {
				 	   line += "StudyEventRepeatKey=\"" + j +"\"";
		  		  }
		    	  line += ">";
		    	  add(currentIndent+indent+indent,line);
				  //get crf
				  for(int k=1; k<eb.getSEDNumCRFs(i)+1; ++k) {
					  CRFBean crfBean = (CRFBean) sed.getCrfs().get(k-1);
	  				  add(currentIndent+indent+indent+indent, "<FormData FormOID=\"FORM." + crfBean.getId() + "\">");
					  //get sections and items
	  			      //the hashtable used to organize the section/item group
	  				  //the key is the sectionID, the value is an arrayList of TempItemInfo
	  				  Hashtable sectionItemData = new Hashtable();
	  				  //1. from the item_form_metadata table, get section_id, and from section_id get SectionBean
					  //if item has value
	  				  TempItemInfo tti;
	  				  SectionDAO sdao = new SectionDAO(this.ds);
					  for(int n=1; n<eb.getNumItems(i,k)+1; ++n) {
						  //adding an extra loop here for repeating items, tbh
						  //for (int o=0; o < eb.getMaxItemDataBeanOrdinal(); o++) {
						  for (java.util.Iterator iter = groupNames.entrySet().iterator(); iter.hasNext(); ) {
							  java.util.Map.Entry groupEntry = (java.util.Map.Entry)iter.next();
							  String groupName = (String)groupEntry.getKey();
							  Integer groupCount = (Integer) groupEntry.getValue();
							  int size = groupCount.intValue();
							  for (int m = 1; m <= size; m++) {
								  tti = new TempItemInfo();
								  //TODO NO guard clause here like in extract bean, need to review and do, tbh
								  //YW 11-02-2007, at this time, !itemvalue.equals("") is good enough for avoiding groupName not 
								  //belong to an item.
								  String itemvalue = eb.getDataByIndex(w,i,j,k,n,m,groupName); //o+1 since o start from 0
								  if(!itemvalue.equals("")) {
									  tti.itemValue = itemvalue;
									  ItemBean ib = (ItemBean)eb.getColumns(sed,crfBean).get(n-1);
									  if(groupName.equals("Ungrouped")) {
										  tti.itemName = size > 1 ? ib.getName()+ "_" + m : ib.getName();
									  } else {
										  tti.itemName = size > 1 ? ib.getName()+ "_" + groupName + "_" + m : ib.getName()+ "_" + groupName;									  
									  }
									  tti.imfb = (ItemFormMetadataBean)eb.getItemFormMetadataBeans(sed,crfBean).get(n-1);
									  //tti.imfb.getSectionId();	
									  tti.crfId = crfBean.getId();
									  SectionBean sb = (SectionBean)sdao.findByPK(tti.imfb.getSectionId());
									  tti.section = sb;
									  addItemIntoSection(sectionItemData, tti); 
								  }
							  }//end of if							  
						  }//end of iter
						  //}//end of 'o' 
					  }
	  				  //2.	From the hashtable, find all keys and then all values, print them into the XML
					  TreeSet sortedKeys = new TreeSet();
					  sortedKeys.addAll(sectionItemData.keySet());
					  Iterator iter = sortedKeys.iterator();
					  while(iter.hasNext()) {
						  String sectionID = (String)iter.next();
		  				  ArrayList aa = (ArrayList)sectionItemData.get(sectionID);
		  				  //double make sure that this section/itemGroup has something
		  				  if(aa.size() > 0){
			  				  add(currentIndent+indent+indent+indent+indent, "<ItemGroupData ItemGroupOID=\"IG." + sectionID + "\" TransactionType=\"Insert\" >");
			  				  for(int m=0; m<aa.size(); ++m){
			  				  //for(int m=aa.size()-1; m>=0; --m){
				  			  	  tti = (TempItemInfo)aa.get(m);
				  			  	  ItemFormMetadataBean ifmBean = (ItemFormMetadataBean)tti.imfb;
				  			  	  ItemBean ib = (ItemBean)idao.findByPK(ifmBean.getItemId());
				  			  	  int ocDatatypeId = ib.getItemDataTypeId();
				  			  	  String value;
				  			  	  if(ocDatatypeId != 9) {
				  			  		  value = tti.itemValue;
				  			  	  } else {
				  			  		  value = getODMDate(tti.itemValue, locale_df_string);
				  			  	  }
				  			  	  add(currentIndent+indent+indent+indent+indent+indent, "<ItemData ItemOID=\"IT." + StringEscapeUtils.escapeXml(tti.itemName) +"." + tti.crfId + "\" Value=\"" + StringEscapeUtils.escapeXml(value) + "\"/>");

			  				  }
			  		 	  	  add(currentIndent+indent+indent+indent+indent, "</ItemGroupData>");
			  		  	  }
		  			 }//end of  while(myEnum.hasMoreElements()){
	  				 add(currentIndent+indent+indent+indent, "</FormData>");
				  }//end of  for(int k=1; k<eb.getSEDNumCRFs(i)+1; ++k) {
				  add(currentIndent+indent+indent, "</StudyEventData>");
	  		  }// end of for(int j=1; j<eb.getSEDNumSamples(i)+1; ++j) {
		  } //end of for(int i=1; i<eb.getNumSEDs()+1; ++i) {
	  	  add(currentIndent+indent, "</SubjectData>");
	  }//end of for(int w=1; w<ss.size()+1; ++w){
	  add(currentIndent, "</ClinicalData>");
  }
  
  
  
  
  //------------------ supporting methods --------------------------------------
  /*
   * This method return a Hashtable 'sedCrfIgItem'. 
   * In 'sedCrfIgItem', the key is sedId and the value is a Hashtable 'crfIgItem'. 
   * In 'crfIgItem', the key is CRFId and the value is a Hashtable 'sectionItemData'. 
   * In 'sectionItemData', the key is sectionId and the value is an arrayList of TempItemInfo
   * 
   * @param	eb ExtractBean
   * @param	currentIndent	String 	the indent space for the clinical data nodes
   *
   * @return	Hashtable
   */
private Hashtable getSedCrfIgItem(ExtractBean eb){
	Hashtable sedCrfIgItem = new Hashtable();
	HashMap groupNames = eb.getGroupNames();
	for(int i=1; i<eb.getNumSEDs()+1; ++i) {
		Hashtable crfIgItem = new Hashtable();
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean)eb.getStudyEvents().get(i-1);
		int definitionId = sed.getId();
		for(int j=1; j<eb.getSEDNumSamples(i)+1; ++j) {
			//get crf
			for(int k=1; k<eb.getSEDNumCRFs(i)+1; ++k) {
				CRFBean crfBean = (CRFBean) sed.getCrfs().get(k-1);
				//1. get sections and items
				//the hashtable used to organize the section/item group
				//the key is the sectionID, the value is an arrayList of TempItemInfo
				Hashtable sectionItemData = new Hashtable();
				//1.1 from the item_form_metadata table, get section_id, and from section_id get SectionBean
				//if item has value
				TempItemInfo tti;
				SectionDAO sdao = new SectionDAO(this.ds);
				for(int n=1; n<eb.getNumItems(i,k)+1; ++n) {
					 //for (int o=0; o < eb.getMaxItemDataBeanOrdinal(); o++) {
					  for (java.util.Iterator iter = groupNames.entrySet().iterator(); iter.hasNext(); ) {
						  java.util.Map.Entry groupEntry = (java.util.Map.Entry)iter.next();
						  String groupName = (String)groupEntry.getKey();
						  if(eb.inKeys(i, j, k, n, groupName)) {
							  Integer groupCount = (Integer) groupEntry.getValue();
							  int size = groupCount.intValue();
							  for (int m = 1; m <= size; m++) {
								  tti = new TempItemInfo();
								  ItemBean ib = (ItemBean)eb.getColumns(sed,crfBean).get(n-1);
								  if(groupName.equals("Ungrouped")) {
									  tti.itemName = size > 1 ? ib.getName()+ "_" + m : ib.getName();
								  } else {
									  tti.itemName = size > 1 ? ib.getName()+ "_" + groupName + "_" + m : ib.getName()+ "_" + groupName;										  
								  }
								  tti.imfb = (ItemFormMetadataBean)eb.getItemFormMetadataBeans(sed,crfBean).get(n-1);
								  SectionBean sb = (SectionBean)sdao.findByPK(tti.imfb.getSectionId());
								  tti.section = sb;
								  tti.crfId = crfBean.getId();
								  addItemIntoSection(sectionItemData, tti);
							  }
						  }
					  }
					 //}
				}
				//1.2 put Hashtable sectionItemData into Hashtable crfSectionItem
				crfIgItem.put(crfBean.getId() + "", sectionItemData);
			}//end of  for(int k=1; k<eb.getSEDNumCRFs(i)+1; ++k) {
		}// end of for(int j=1; j<eb.getSEDNumSamples(i)+1; ++j) {
		sedCrfIgItem.put(definitionId + "", crfIgItem);
	} //end of for(int i=1; i<eb.getNumSEDs()+1; ++i) {
	
	return sedCrfIgItem;
}
  
  //----------- a method to group Items based on sectionID -----
  private void addItemIntoSection(Hashtable ht, TempItemInfo ti) {
	  ArrayList a = (ArrayList)ht.get(ti.imfb.getSectionId()+"");
	  if(a == null){
		  a = new ArrayList();
	  }
	  a.add(ti);
	  ht.put(ti.imfb.getSectionId() + "", a); 
  }
  
  class TempItemInfo {
	  ItemFormMetadataBean imfb;
	  String  itemName="", itemValue="";
	  SectionBean section;
	  int crfId=0;
  }
  
  //convert openclinica DataType to ODM DataType
  private String getODMDataType(int OCDataTypeId, ItemFormMetadataBean ifmBean) {
	  switch (OCDataTypeId) {
	  	  //BL		//BN	//ED	//TEL	//ST	  
		  case 1:	case 2:	case 3:	case 4:	case 5:
		  //SET
		  case 8:  return "text";
		  //INT. This includes two groups. One is real integer which could be used to calculation,
		  //     which will be return as "integer".
		  //     Another is only option index for some response type like multiple-select etc, which
		  //     will be return as "text".
		  case 6:
			  ResponseSetBean rsb = ifmBean.getResponseSet();
		      if (rsb.getResponseType().equals(ResponseType.TEXT)
		            || rsb.getResponseType().equals(ResponseType.TEXTAREA)) {
		    	  return "integer";
		      } else {
		    	  return "text";
		      }
		  //REAL
		  case 7:  return "float";
		  //DATE
		  case 9:  return "date";
		  default:  return "text";
	  }
  }
  
  /*
   * convert Date format to ODM Date format String (yyyy-MM-dd)
   */
  private String getODMDate(String d, String locale_df_string) {
	  try {
		  return new SimpleDateFormat("yyyy-MM-dd").format((java.util.Date)new SimpleDateFormat(locale_df_string).parse(d));
	  }catch(Exception fe) {
			//fe.printStackTrace();
		  	//This way will fail user's ODM xml against ODM1-2-1 schema and force user to check.
			return d;
	  }
  }
  
  /**
   * Get XML output.
   * 
   * @return  ArrayList<String>.  
   */ 
  /* All information of from the database
   * is organized in an array of String in order.  If print all the strings
   * in this ArrayList in order, everything should be fine.
   */
  public ArrayList getXMLOutput() {
	  return xmlOutput;
  }

  /**
   * Set the heading of the xml file.  By default, it is <?xml version="1.0" encoding="UTF-8"?>
   * 
   * @param heading String
   */
  protected void setXMLHeading(String heading)
  {
	  this.xmlHeading = heading;
  }

  /**
   * Get the heading of the xml file
   * 
   * @return String
   */
  protected String getXMLHeading()
  {
	  return this.xmlHeading;
  }
  
  /**
   * Set the namespace of the xml file.  By default, it is "http://www.cdisc.org/ns/odm/v1.2"
   * 
   * @param xmlns
   */
  protected void setXmlns(String xmlns)
  {
	  this.xmlns = xmlns;
  }
  
  /**
   * Get the namespace of the xml file
   * 
   * @return String
   */
  protected String getXmlns()
  {
	  return this.xmlns;
  }  

  /**
   * Return the used-indent value.  This value is used to indent all the XML information.
   *
   * @return	String	
   */
  public String getIndent()
  {
	  return this.indent;
  }

  /**
   * Set the indent information for displaying the xml information.
   * If you prefer having no indentation, please set it to empty string.
   * By default, it is 4 spaces.
   * 
   * @param indent String
   */
  public void setIndent(String indent)
  {
	  this.indent = indent;
  }

  private void add(String currentIndent, String info)
  {
	  xmlOutput.add(currentIndent + info);
  }
  
  private static String stringCut(String s) {
	  return s == null ? s : s.split("\\:")[0];
  }
}
