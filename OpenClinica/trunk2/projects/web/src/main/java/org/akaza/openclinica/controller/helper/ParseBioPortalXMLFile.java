package org.akaza.openclinica.controller.helper;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

public class ParseBioPortalXMLFile{
	private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("org.akaza.openclinica.controller.helper.ParseBioPortalXMLFile");
	/**
	 * @param uri RESTful Search URI for BioPortal web services
	 */

	public static String getBioPortalConceptId (String uri){
		String bioPortalConceptId = null;
		try {
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(uri);

			//Normalize text representation
			doc.getDocumentElement().normalize();
			
			/**
			 * NOTE: The values for "getElementsByTagName may need to be changed depending on the web service used 
			 */
			NodeList listOfSearchResults = doc.getElementsByTagName("searchBean");
			int totalSearchResults = listOfSearchResults.getLength(); //total search results also available as XML value
			//System.out.println("Total Results: "+ totalSearchResults);
			
			String TAB = "\t";
			if (totalSearchResults == 0) {
				System.out.print("NO SEARCH RESULTS: "+uri+"\n"); 
			}
	
			else {
				for(int s=0; s<listOfSearchResults.getLength(); s++) {
					Node firstSearchNode = listOfSearchResults.item(s);

					if(firstSearchNode.getNodeType() == Node.ELEMENT_NODE){
						Element firstSearchElement = (Element)firstSearchNode;                    
						
						//-- Print URL to access term in BioPortal 
						//System.out.print("http://bioportal.bioontology.org/virtual/"+bioPortalOntologyId+"/"+conceptIdentifier);
						NodeList conceptIdShort = firstSearchElement.getElementsByTagName("conceptIdShort");
						Element conceptIdElement = (Element)conceptIdShort.item(0);
						NodeList textOIDList = conceptIdElement.getChildNodes();
						bioPortalConceptId = (((Node)textOIDList.item(0)).getNodeValue().trim());
						//System.out.print(bioPortalOntologyId+TAB);
						logger.debug("bioPortalConceptId: " + bioPortalConceptId);
					}
				}
			}
		}catch (SAXParseException err) {
			System.out.println ("**PARSING ERROR" + ", line "+err.getLineNumber () + ", uri " + err.getSystemId ());
			System.out.println(" " + err.getMessage ());

		}catch (SAXException e) {
			Exception x = e.getException ();
			((x == null) ? e : x).printStackTrace ();

		}catch (Throwable t) {
			t.printStackTrace ();
		}		
		return bioPortalConceptId;
	}
	
//	public static String uri = "http://rest.bioontology.org/bioportal/virtual/rootpath/1422/10019211?apikey=d1401892-8b8f-463e-ba26-426650edf04a";
	public static String getBioPortalRootPath (String uri){
//	public static void main (String args[]){
		String bioPortalRootPath = null;
		try {
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(uri);

			//Normalize text representation
			doc.getDocumentElement().normalize();
			
			/**
			 * NOTE: The values for "getElementsByTagName may need to be changed depending on the web service used 
			 */
			NodeList listOfSearchResults = doc.getElementsByTagName("entry");
//			NodeList listOfSearchResults = doc.getElementsByTagName("relations");
			int totalSearchResults = listOfSearchResults.getLength(); //total search results also available as XML value
			//System.out.println("Total Results: "+ totalSearchResults);
			
			String TAB = "\t";
			if (totalSearchResults == 0) {
				System.out.print("NO SEARCH RESULTS: "+uri+"\n"); 
			}
	
			else {
				for(int s=0; s<listOfSearchResults.getLength(); s++) {
					Node firstSearchNode = listOfSearchResults.item(s);

					if(firstSearchNode.getNodeType() == Node.ELEMENT_NODE){
						Element firstSearchElement = (Element)firstSearchNode;                    
//						System.out.print("Path1 " + firstSearchElement.getAttribute("Path"));
////						System.out.print("Path attr node val: " + firstSearchElement.getAttributeNode("Path").getNodeValue());
//						NodeList pathList = firstSearchElement.getElementsByTagName("Path");
//						System.out.print("Path2: " + ((Element)pathList.item(0)).getFirstChild().getNodeValue());
						//-- Print URL to access term in BioPortal 
						//System.out.print("http://bioportal.bioontology.org/virtual/"+bioPortalOntologyId+"/"+conceptIdentifier);
						NodeList entryList = firstSearchElement.getElementsByTagName("string");
						Element entryElement = (Element)entryList.item(1);
//						System.out.print("LastChild: " + entryElement.getLastChild().getNodeValue());
//						//System.out.print("AttributeNode val: " + entryElement.getAttributeNode("Path").getNodeValue());
//						System.out.print("Attribute: " + entryElement.getAttribute("Path"));
//						NodeList textOIDList = entryElement.getChildNodes();
//						bioPortalRootPath = (((Node)textOIDList.item(1)).getNodeValue().trim());
////						NodeList textOIDList = firstSearchElement.getChildNodes();
//						bioPortalRootPath = (((Node)textOIDList.item(1)).getNodeValue().trim());
						bioPortalRootPath = entryElement.getFirstChild().getNodeValue();

//						System.out.print("bioPortalRootPath: " + bioPortalRootPath);
						logger.debug("bioPortalRootPath: " + bioPortalRootPath);
					}
				}
			}
		}catch (SAXParseException err) {
			System.out.println ("**PARSING ERROR" + ", line "+err.getLineNumber () + ", uri " + err.getSystemId ());
			System.out.println(" " + err.getMessage ());

		}catch (SAXException e) {
			Exception x = e.getException ();
			((x == null) ? e : x).printStackTrace ();

		}catch (Throwable t) {
			t.printStackTrace ();
		}		
		return bioPortalRootPath;
	}
//	public static String uri = "http://rest.bioontology.org/bioportal/concepts/42280?conceptid=10019233&apikey=d1401892-8b8f-463e-ba26-426650edf04a";
	public static String getTermLabel (String uri){
//	public static void main (String args[]){
		String termLable = null;
		try {
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(uri);

			//Normalize text representation
			doc.getDocumentElement().normalize();
			
			/**
			 * NOTE: The values for "getElementsByTagName may need to be changed depending on the web service used 
			 */
			NodeList listOfSearchResults = doc.getElementsByTagName("classBean");
			int totalSearchResults = listOfSearchResults.getLength(); //total search results also available as XML value
			//System.out.println("Total Results: "+ totalSearchResults);
			
			String TAB = "\t";
			if (totalSearchResults == 0) {
				System.out.print("NO SEARCH RESULTS: "+uri+"\n"); 
			}
	
			else {
//				for(int s=0; s<listOfSearchResults.getLength(); s++) {
//					Node firstSearchNode = listOfSearchResults.item(s);
				
				Node firstSearchNode = listOfSearchResults.item(0);
					if(firstSearchNode.getNodeType() == Node.ELEMENT_NODE){
						Element firstSearchElement = (Element)firstSearchNode;                    

						NodeList entryList = firstSearchElement.getElementsByTagName("label");
						Element entryElement = (Element)entryList.item(0);
						
						termLable =  entryElement.getFirstChild().getNodeValue();
					
						logger.debug("termLable: " + termLable);
//						System.out.println("termLable: " + termLable);
//					if(firstSearchNode.getNodeType() == Node.ELEMENT_NODE){
//						Element firstSearchElement = (Element)firstSearchNode;                    
//						
//						//-- Print URL to access term in BioPortal 
//						//System.out.print("http://bioportal.bioontology.org/virtual/"+bioPortalOntologyId+"/"+conceptIdentifier);
//						NodeList conceptIdShort = firstSearchElement.getElementsByTagName("Path");
//						Element conceptIdElement = (Element)conceptIdShort.item(0);
//						NodeList textOIDList = conceptIdElement.getChildNodes();
//						bioPortalRootPath = (((Node)textOIDList.item(0)).getNodeValue().trim());
//						//System.out.print(bioPortalOntologyId+TAB);
//						logger.debug("bioPortalRootPath: " + bioPortalRootPath);
					}
//				}//for
			}
		}catch (SAXParseException err) {
			System.out.println ("**PARSING ERROR" + ", line "+err.getLineNumber () + ", uri " + err.getSystemId ());
			System.out.println(" " + err.getMessage ());

		}catch (SAXException e) {
			Exception x = e.getException ();
			((x == null) ? e : x).printStackTrace ();

		}catch (Throwable t) {
			t.printStackTrace ();
		}		
		return termLable;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String verbatimTerm = "Headache";
		String rootPath = "123.456.789";
		String codingStatus = null;
		String SOC = "";
    	String HLGT = "";
    	String HLT = "";
    	
		String[] tokens = rootPath.split("\\.");
		if(tokens.length != 3){
			System.out.println("Number of levels in the root path hierarchy of the concept are " + verbatimTerm + " are: " + tokens.length);
			codingStatus = "concept rootpath could not be retrieved correctly";
		} 
		else{       			
			String HLTconceptId = tokens[0];
			System.out.println("token 0: " +  tokens[0]);
//			HLT = SearchCodingTermsBioPortal.findConceptLabel(HLTconceptId);
			System.out.println("HLT: " + HLT);
			
			String HLGTconceptId = tokens[1];
			System.out.println("token 1: " +  tokens[1]);
//			HLGT = SearchCodingTermsBioPortal.findConceptLabel(HLGTconceptId);
			System.out.println("HLGT: " + HLGT);
			
			String SOCconceptId = tokens[0];
			System.out.println("token 2: " +  tokens[2]);
//			SOC = SearchCodingTermsBioPortal.findConceptLabel(SOCconceptId);
			System.out.println("HLTTermLabel: " + SOC);
			
//			codedTermBean.setHLT(HLT);
//			codedTermBean.setHLGT(HLGT);
//			codedTermBean.setSOC(SOC);
			codingStatus = "Complete";   
		}  

	}
}
