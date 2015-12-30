package org.akaza.openclinica.bean.extract;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
/*
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
*/



import javax.sql.DataSource;
import org.w3c.dom.*;


import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.extract.SasNameValidationBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.exception.MultipleItemValuesFoundInXMLParsingException;



/**
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 *
 * <p>Create CDISC ODM XML format for OpenClinica data based on CDISC ODM Version 1.2.1.
 * <p>At this point, it generates snapshot type file. It includes some requires elements 
 *    and attributes of general node, metadata node and clinical node. It can only generate
 *    one MetaDataVersion in one file. 
 * <p>The generated data file is ready for PROC CDISC to create a SAS data set. 
 * <p>For this class to work properly, another data type (an addition row) is added
 *	  as "text/plain" in the table export_format
 *
 * Created on Oct., 2006
 *  
 * 
 *
 * @auther ywang
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
  private boolean dataReady = false;
  protected static String ODM_FileType = "Snapshot";
  private SimpleDateFormat sdf_datetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private String xmlns = "http://www.cdisc.org/ns/odm/v1.2";
  private String xmlHeading = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  private SimpleDateFormat sdf_sqldate = new SimpleDateFormat("yyyy-MM-dd");
  

  public CdiscOdmXmlBean(DataSource ds, String schemaLocation, String mdvOID, String mdvName, String mdvPrevStudy, String mdvPrevOID) {
    xmlOutput = new ArrayList();
    this.ds = ds;
    this.schemaLocation = schemaLocation;
    if(mdvOID != null && mdvOID.length()>0) {
    	this.metaDataVersionOID = mdvOID;
    } else {
    	this.metaDataVersionOID = "v1.0.0";
    }

    if(mdvName != null && mdvName.length()>0) {
    	this.metaDataVersionName = mdvName;  
	}else {
		this.metaDataVersionName = "Version 1.0.0";
	}
    this.metaDataVersionPrevStudy = mdvPrevStudy;
    this.metaDataVersionPrevOID = mdvPrevOID;
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

	  String root_line = "<ODM xmlns=\"" + this.xmlns + "\" xmlns:ds=\"http://www..w3.org/2000/09/xmldsig#\" "
	  					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " 
	  					+ "xsi:schemaLocation=\"" + this.xmlns + " " + this.schemaLocation + "\" ";
	  //attribute 'Description' - optional;
	  //So first check if dataset has description
	  String temp = eb.getDataset().getDescription();
	  if(temp.trim().length() > 0) {
		  root_line += "Description=\"" + temp + "\" ";
	  }
	  //here the file OID is automatically set to be the dataset ID 
	  root_line += "FileType=\"" + ODM_FileType + "\" " +
	          "FileOID=\"" + eb.getDataset().getId() + "\" " +
	          "CreationDateTime=\"" + sdf_datetime.format(new Date()) + "\" >";
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

	  this.dataReady = true;
  }

  //-----------------  Methods related to Study node -----------------------
  /**
   * This method constructs a XML node of Study
   * @param eb	ExtractBean
   * @param	currentIndent  String
   */
  private void addNodeStudy(ExtractBean eb, String currentIndent){
	  DatasetBean dataset = eb.getDataset();

	  /*
	   * I hope that the studyID got from dataset is exactly the same as the studyID in currentstudy,
	   * this is what I am counting on.
	   */
	  int studyId = dataset.getStudyId();

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
	  //we may need add getname in studybean? so we can use currentstudy's name instead of parentstudyname?
	  //add(currentIndent + indent, "<StudyName>" + eb.getStudy().getName() + "</StudyName>");
	  add(currentIndent + indent, "<StudyName>" + eb.getParentStudyName() + "</StudyName>");
	  add(currentIndent + indent, "<StudyDescription>");
	  add(currentIndent + indent + indent, eb.getStudy().getSummary());
	  add(currentIndent + indent, "</StudyDescription>");
	  add(currentIndent + indent, "<ProtocolName>" + eb.getParentProtocolId() + "</ProtocolName>");
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

	  StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(this.ds);
	  ArrayList studyEventDefs = seddao.findAllWithStudyEvent(eb.getStudy());
	  Collections.sort(studyEventDefs);
	  
	  ArrayList selectedStudyEventDefBeans = new ArrayList();
	  ArrayList selectedFormBeans = new ArrayList();
	  ArrayList selectedItemBeans = new ArrayList();
	  ArrayList items;
	  CRFDAO cdao = new CRFDAO(this.ds);
	  ItemDAO idao = new ItemDAO(this.ds);
	  selectedStudyEventDefBeans.clear();
	  selectedFormBeans.clear();
	  selectedItemBeans.clear();
	  for(int i=0; i<studyEventDefs.size(); ++i) {
		  StudyEventDefinitionBean sed = (StudyEventDefinitionBean)studyEventDefs.get(i);
		
		  if(eb.selectedSED(sed)) {
			  selectedStudyEventDefBeans.add(sed);
		  }
	  }
	  
	  addMetaDataVersionProtocol(selectedStudyEventDefBeans, currentIndent+indent);
	  addMetaDataVersionStudyEventDef(eb, selectedStudyEventDefBeans, selectedFormBeans, selectedItemBeans, currentIndent+indent);
	  
	  add(currentIndent, "</MetaDataVersion>");
  }
 
private void addMetaDataVersionProtocol(ArrayList selectedStudyEventDefBeans, String currentIndent) {
	//The protocol lists the kinds of study events that can occur within a specific version of a Study.
	//It does not relate to the subjects but the version of a study.
	add(currentIndent, "<Protocol>");
	for(int i=0; i<selectedStudyEventDefBeans.size(); ++i) {
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean)selectedStudyEventDefBeans.get(i);
		//At this point, I don't know how to handle Mandatory. Since it is required and sed is selected, I assign it 'yes'.
		add(currentIndent+indent, "<StudyEventRef StudyEventOID=\"SE." + sed.getId() + "\" Mandatory=\"Yes\"/>");
	}
	add(currentIndent, "</Protocol>");
}

private void addMetaDataVersionStudyEventDef(ExtractBean eb, ArrayList selectedStudyEventDefBeans, ArrayList selectedFormBeans, ArrayList selectedItemBeans, String currentIndent) {
	String line;
	for(int i=0; i<selectedStudyEventDefBeans.size(); ++i) {
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean)selectedStudyEventDefBeans.get(i);
		line = "<StudyEventDef OID=\"SE." + sed.getId() + "\"  Name=\"" + sed.getName() + "\" ";
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
		addMetaDataVersionFormRef(eb, sed, selectedFormBeans, currentIndent + indent);
		add(currentIndent, "</StudyEventDef>");
	}

	addMetaDataVersionFormDef(eb,selectedFormBeans,currentIndent);
}

//At this point, only one version of a form for a study event definition for a study is used, 
//that is, the latest created version is always chosen for a study event definition of a particular subject under a particular study.
//However, this limits this CDISC ODM XML to one condition that latest created version
private void addMetaDataVersionFormRef(ExtractBean eb, StudyEventDefinitionBean sed, ArrayList selectedFormBeans, String currentIndent) {
	CRFDAO cdao = new CRFDAO(this.ds);
	Collection crfs = cdao.findAllActiveByDefinition(sed);
	EventDefinitionCRFDAO edcrfdao = new EventDefinitionCRFDAO(this.ds);
	ArrayList edcrfBeans = edcrfdao.findAllActiveByEventDefinitionId(sed.getId());
	Iterator it = crfs.iterator();
	String line;

	while(it.hasNext()){
		CRFBean crfBean = (CRFBean)it.next();
		if(eb.selectedSEDCRF(sed, crfBean)){
			//add(currentIndent, "<FormRef FormOID=\"Form." + crfBean.getId() + "\" Mandatory=\"Yes\"/>");
			line = "<FormRef FormOID=\"Form." + crfBean.getId() + "\"";
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

			//for FormDef
			//make sure crfBeans included in selectedFormBeans are distinctive
			//do we need more efficient search algorithm?
			if(selectedFormBeans.size()>0) {
				int distinctive = 1;
				for(int n=0; n<selectedFormBeans.size(); ++n) {
					CRFBean sfb = (CRFBean)selectedFormBeans.get(n);
					if(crfBean.getId() == sfb.getId()) {
						distinctive = 0;
						break;
					}
				}
				if(distinctive == 1) {
					selectedFormBeans.add(crfBean);
				}
			}else if(selectedFormBeans.size()==0){
				selectedFormBeans.add(crfBean);
			}
		}
	}
}

//currently, repeating of a form is arbitrarily set as No
private void addMetaDataVersionFormDef(ExtractBean eb, ArrayList selectedFormBeans, String currentIndent) {
	ArrayList items = new ArrayList();
	//CRFVersionDAO cvdao = new CRFVersionDAO(this.ds);
	ItemDAO idao = new ItemDAO(this.ds);
	ArrayList selectedItemGroupBeans = new ArrayList();
	int size = selectedFormBeans.size();
	ArrayList selectedCRFVersionIds = new ArrayList();
	//i-th element shows the start index of selectedItemGroupBeans' elements of i-th selectedCRFVeresionIds. 
	int[] itemGroupPositions = new int[size+1];
	itemGroupPositions[0] = 0;
	System.out.println("in formdef, selectedFormBeans.size=" + selectedFormBeans.size());
  
	for(int i=0; i<size; ++i) {
    	CRFBean crfBean = (CRFBean)selectedFormBeans.get(i);
        add(currentIndent, "<FormDef OID=\"FORM." + crfBean.getId() + "\" Name=\"" + crfBean.getName() + "\" Repeating=\"No\">");
        CRFVersionBean crfVb = findLatestCreatedCRFVersionBean(crfBean.getId());
        addMetaDataVersionItemGroupRef(crfVb, selectedItemGroupBeans, currentIndent+indent);
        Integer crfvbid = new Integer(crfVb.getId());
        selectedCRFVersionIds.add(crfvbid);
		//selectedCRFVersionIds.add(crfVb.getId());
		itemGroupPositions[i+1]=selectedItemGroupBeans.size();
		add(currentIndent, "</FormDef>");
    }
	addMetaDataVersionItemGroupDef(selectedCRFVersionIds, selectedItemGroupBeans, itemGroupPositions, currentIndent);
}

private CRFVersionBean findLatestCreatedCRFVersionBean(int crfId) {
	CRFVersionDAO cvdao = new CRFVersionDAO(this.ds);
	ArrayList crfVersions = cvdao.findAllByCRFId(crfId);
	CRFVersionBean latestCRFVersion = new CRFVersionBean();
	try {
		if(crfVersions.size()==1) {
			latestCRFVersion = (CRFVersionBean)crfVersions.get(0);
		} else if(crfVersions.size()>1) {
			latestCRFVersion = (CRFVersionBean)crfVersions.get(0);
			Date latestdate = sdf_sqldate.parse(latestCRFVersion.getDateCreated().toString());
			for(int i=1; i<crfVersions.size(); ++i) {
				CRFVersionBean cvb = (CRFVersionBean)crfVersions.get(i);
				Date cvbdate = sdf_sqldate.parse(cvb.getDateCreated().toString());
				if(cvbdate.after(latestdate)) {
					latestdate = cvbdate;
					latestCRFVersion = cvb;
				}
			}
		} 
	} catch (java.text.ParseException pe) {
		pe.printStackTrace();
	}
	
	return latestCRFVersion;
}

//currently, map section to itemgroup and only the section of the latest created crf version of a crf will be listed under crf
//and Mandatory has been set as "NO"
private void addMetaDataVersionItemGroupRef(CRFVersionBean crfVb, ArrayList selectedItemGroupBeans, String currentIndent) {
	//CRFVersionBean crfVb = findLatestCreatedCRFVersionBean(CRFId);
	SectionDAO sdao = new SectionDAO(this.ds);
	ArrayList sbs = sdao.findAllByCRFVersionId(crfVb.getId());

	for(int i=0; i<sbs.size(); ++i) {
		SectionBean sbean = (SectionBean)sbs.get(i);
		add(currentIndent, "<ItemGroupRef ItemGroupOID=\"IG." + sbean.getId() + "\" Mandatory=\"No\"/>");
		//for ItemGroupDef
		//make sure selectedItemGroupBeans elements are distinctive
		if(selectedItemGroupBeans.size()>0) {
			int distinctive = 1;
			for(int n=0; n<selectedItemGroupBeans.size(); ++n) {
				SectionBean sb = (SectionBean)selectedItemGroupBeans.get(n);
				if(sbean.getId() == sb.getId()) {
					distinctive = 0;
					break;
				}
			}
			if(distinctive == 1) {
					selectedItemGroupBeans.add(sbean);
			}
		}else if(selectedItemGroupBeans.size()==0){
			selectedItemGroupBeans.add(sbean);
		}		
	}
}

private void addMetaDataVersionItemGroupDef(ArrayList selectedCRFVersionIds, ArrayList selectedItemGroupBeans, int[] itemGroupPositions, String currentIndent) {
	ArrayList selectedItemFormMetadataBeans = new ArrayList();
	for(int j=0; j<selectedCRFVersionIds.size(); ++j) {
		//int crfvid = (Integer)selectedCRFVersionIds.get(j);
		Integer crfvid = (Integer)selectedCRFVersionIds.get(j);
		for(int i=itemGroupPositions[j]; i<itemGroupPositions[j+1]; ++i) {
			SectionBean sb = (SectionBean)selectedItemGroupBeans.get(i);
			//add(currentIndent, "<ItemGroupDef OID=\"IG." + sb.getId() + "\" Name=\"" + sb.getLabel() + "\" Repeating=\"No\" SASDatasetName=\"" + sasname.getValidSasName(sb.getLabel()) + "\">");
			//add(currentIndent, "<ItemGroupDef OID=\"IG." + sb.getId() + "\" Name=\"" + sb.getLabel() + "\" Repeating=\"No\">");
			addMetaDataVersionItemRef(crfvid.intValue(), sb, selectedItemFormMetadataBeans, currentIndent);
			//add(currentIndent, "</ItemGroupDef>");
		}
	}
	addMetaDataVersionItemDef(selectedItemFormMetadataBeans, currentIndent);
}

private void addMetaDataVersionItemRef(int crfversionId, SectionBean sb, ArrayList selectedItemFormMetadataBeans, String currentIndent) {
	SasNameValidationBean sasname = new SasNameValidationBean();
	ItemFormMetadataDAO ifmDAO = new ItemFormMetadataDAO(this.ds);
	ItemDAO idao = new ItemDAO(this.ds);
	ArrayList ifmbeans = new ArrayList();
	try {
		ifmbeans = ifmDAO.findAllByCRFVersionIdAndSectionId(crfversionId, sb.getId());
	}catch (OpenClinicaException oce){
		oce.printStackTrace();
	}
	if(ifmbeans.size()>0) {
		add(currentIndent, "<ItemGroupDef OID=\"IG." + sb.getId() + "\" Name=\"" + sb.getLabel() + "\" Repeating=\"No\" SASDatasetName=\"" + sasname.getValidSasName(sb.getLabel()) + "\">");
	
		String line;	
		//ItemRef, a reference to an ItemDef as it occurs within a specific ItemGroupDef.
		for(int i=0; i<ifmbeans.size(); ++i) {
			ItemFormMetadataBean ifmbean = (ItemFormMetadataBean)ifmbeans.get(i);
			ItemBean ib = (ItemBean)idao.findByPK(ifmbean.getItemId());
			//line = "<ItemRef ItemOID=\"IT." + ifmbean.getItemId() + "\"";
			line = "<ItemRef ItemOID=\"IT." + ib.getName() + "\"";
			if(ifmbean.isRequired()) {
				line +=  " Mandatory=\"Yes\"";
			} else {
				line +=  " Mandatory=\"No\"";
			}
			line += "/>";
			add(currentIndent+indent, line);
		
			//For ItemDef,
			//make sure selectedItemFormMetadataBeans elements are distinctive
			if(selectedItemFormMetadataBeans.size()>0) {
				int distinctive = 1;
				for(int n=0; n<selectedItemFormMetadataBeans.size(); ++n) {
					ItemFormMetadataBean ifmb = (ItemFormMetadataBean)selectedItemFormMetadataBeans.get(n);
					if(ifmbean.getItemId() == ifmb.getItemId()) {
						distinctive = 0;
						break;
					}
				}
				if(distinctive==1) {
					selectedItemFormMetadataBeans.add(ifmbean);
				}
			}else if(selectedItemFormMetadataBeans.size()==0){
				selectedItemFormMetadataBeans.add(ifmbean);
			}
		}
		add(currentIndent, "</ItemGroupDef>");
	}
}

private void addMetaDataVersionItemDef(ArrayList selectedItemFormMetadataBeans, String currentIndent) {
	SasNameValidationBean sasname = new SasNameValidationBean();
	ItemDAO idao = new ItemDAO(this.ds);
	for(int i=0; i<selectedItemFormMetadataBeans.size(); ++i) {
		ItemFormMetadataBean ifmb = (ItemFormMetadataBean)selectedItemFormMetadataBeans.get(i);
		ItemBean ib = (ItemBean)idao.findByPK(ifmb.getItemId());
		add(currentIndent, "<ItemDef OID=\"IT." + ib.getName() + "\" Name=\"" + stringCut(ifmb.getLeftItemText()) + "\" DataType=\"string\" SASFieldName=\"" + sasname.getValidSasName(ifmb.getLeftItemText()) + "\"/>" );
		//add(currentIndent, "<ItemDef OID=\"IT." + ifmb.getItemId() + "\" Name=\"" + stringCut(ifmb.getLeftItemText()) + "\" DataType=\"string\" SASFieldName=\"" + sasname.getValidSasName(ifmb.getLeftItemText()) + "\"/>" );
		//add(currentIndent, "<ItemDef OID=\"IT." + ifmb.getItemId() + "\" Name=\"" + stringCut(ifmb.getLeftItemText()) + "\" DataType=\"string\"/>" );
	}
}	

/***---------- Methods related to AdminData nodes. However, it is not available currently ---------**/
  private void addNodeAdminData(ExtractBean eb, String currentIndent){

  }
/***---------- Methods related to ReferenceData nodes. However, it is not available currently ---------**/
  private void addNodeReferenceData(ExtractBean eb, String currentIndent){
	  
  }

  
  
/***---------- Methods related to ClinicalData nodes -------------------*/
  /**
   * @param	eb ExtractBean
   * @param	currentIndent	String 	the indent space for the clinical data nodes
   */
  private void addNodeClinicalData(ExtractBean eb, String currentIndent){
	  int studyId = eb.getDataset().getStudyId();
	  //add the starting information about the clinical data node
	  add(currentIndent,"<ClinicalData StudyOID=\"" + studyId + "\" MetaDataVersionOID=\"" + metaDataVersionOID + "\">");
	  ArrayList ss = eb.getSubjects();
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
						  tti = new TempItemInfo();
						  String itemvalue = eb.getDataByIndex(w,i,j,k,n);
						  if(!itemvalue.equals("")) {
							tti.itemValue = itemvalue;
						  	ItemBean ib = (ItemBean)eb.getColumns(sed,crfBean).get(n-1);
			  			  	tti.itemName = ib.getName();
			  			  	tti.imfb = (ItemFormMetadataBean)eb.getItemFormMetadataBeans(sed,crfBean).get(n-1);
						  	tti.imfb.getSectionId();		  			
						  	SectionBean sb = (SectionBean)sdao.findByPK(tti.imfb.getSectionId());
		  				  	tti.section = sb;
		  				  	addItemIntoSection(sectionItemData, tti);
						  }
					  }
	  				  //2.	From the hashtable, find all keys and then all values, print them into the XML
	  				  Enumeration myEnum = sectionItemData.keys();   
	  				  while(myEnum.hasMoreElements()){
		  				  String sectionID = (String)myEnum.nextElement();
		  				  ArrayList aa = (ArrayList)sectionItemData.get(sectionID);
		  				  //double make sure that this section/itemGroup has something
		  				  if(aa.size() > 0){
			  				  add(currentIndent+indent+indent+indent+indent, "<ItemGroupData ItemGroupOID=\"IG." + sectionID + "\" TransactionType=\"Insert\" >");
			  				  for(int m=0; m<aa.size(); ++m){
			  				  //for(int m=aa.size()-1; m>=0; --m){
				  			  	  tti = (TempItemInfo)aa.get(m);
				  			  	  //add(currentIndent+indent+indent+indent+indent+indent, "<ItemData ItemOID=\"IT." + tti.imfb.getItemId() + "\" Value=\"" + tti.itemValue + "\"/>");
				  			  	  add(currentIndent+indent+indent+indent+indent+indent, "<ItemData ItemOID=\"IT." + tti.itemName + "\" Value=\"" + tti.itemValue + "\"/>");

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

  //----------- a method to group Items based on sectionID -----
  private void addItemIntoSection(Hashtable ht, TempItemInfo ti)
  {
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
  }
  

 /** ---------- Other methods ------------------------------*/
  /**
   * @return  ArrayList	of String.  All information of from the database
   * is organized in an array of String in order.  If print all the strings
   * in this ArrayList in order, everything should be fine.
   */
  public ArrayList getXMLOutput() {
	  return xmlOutput;
  }

  /**
   * the heading of the xml file.  By default, it is <?xml version="1.0" encoding="UTF-8"?>
   * @param heading
   */
  protected void setXMLHeading(String heading)
  {
	  this.xmlHeading = heading;
  }

  protected String getXMLHeading()
  {
	  return this.xmlHeading;
  }
  
  /**
   * the namespace of the xml file.  By default, it is "http://www.cdisc.org/ns/odm/v1.2"
   * @param xmlns
   */
  protected void setXmlns(String xmlns)
  {
	  this.xmlns = xmlns;
  }
  
  protected String getXmlns()
  {
	  return this.xmlns;
  }  

  /**
   *
   * @return	String	Return the used-indent value.  This value is used to
   * indent all the XML information.
   */
  public String getIndent()
  {
	  return this.indent;
  }

  /**
   * set the indent information for displaying the xml information.
   * If you prefer having no indentation, please set it to empty string.
   * By default, it is 4 spaces.
   * @param indent
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
