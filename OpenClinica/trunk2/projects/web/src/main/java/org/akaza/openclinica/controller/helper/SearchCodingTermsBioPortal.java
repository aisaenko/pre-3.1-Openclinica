package org.akaza.openclinica.controller.helper;

import java.io.File;
import java.util.ArrayList;

public class SearchCodingTermsBioPortal {
	
	private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this
			.getClass().getName());
	/**
	 * NOTE: Edit the value of ONTOLOGY_ID to search ontology or ontologies or interest. To search multiple 
	 * ontologies, include ontology identifier as a comma-separated list. To find the ontologyId, use the 
	 * "List latest version" web service documented 
	 * at: http://www.bioontology.org/wiki/index.php/NCBO_REST_services#List_all_the_latest_version_of_ontologies 
	 */
	private final static String ONTOLOGY_ID = "1422";
	private final static String ONTOLOGY_VERSION_ID = "42280";
	private final static String  BIOPORTAL_URL_PREFIX = "http://rest.bioontology.org/bioportal/search/";
	private final static String BIOPORTAL_GET_ROOTPATH_URL = "http://rest.bioontology.org/bioportal/virtual/rootpath/";
	private final static String BIOPORTAL_GET_TERM_URL = "http://rest.bioontology.org/bioportal/concepts/"; 
	private final static String apiKey = "apikey=d1401892-8b8f-463e-ba26-426650edf04a";

	/**
	 * @param args
	 */
	public static String findExactMatch(String term) {
		String bioPortalConceptId = null;
		if(term != null){		
			String trimmedSearchTerm = term.trim();			
			
			/**
			* NOTE: See http://www.bioontology.org/wiki/index.php/NCBO_REST_services#Search_services for full list of search parameters  
			*/
			String searchParameters = "?ontologyids="+ONTOLOGY_ID+"&isexactmatch=1"+"&includeproperties=0";	
			//Configure web service URL 
			String bioPortalSearchUrl = BIOPORTAL_URL_PREFIX+trimmedSearchTerm+searchParameters;
			
			String newBioPortalSearchUrl = bioPortalSearchUrl + "&" + apiKey;
			
			//Call Search REST URL and Parse results 
//			ParseXMLFile.parseXMLFile(bioPortalSearchUrl);
			bioPortalConceptId = ParseBioPortalXMLFile.getBioPortalConceptId(newBioPortalSearchUrl);
		}
		return bioPortalConceptId;
	}
	
	public static String findConceptRootPath(String conceptId) {
		String bioPortalRootPath = null;
		if(conceptId != null){		
			String trimmedconceptId = conceptId.trim();			
			
			/**
			* NOTE: See http://www.bioontology.org/wiki/index.php/NCBO_REST_services#Search_services for full list of search parameters  
			*/
			
			//Configure web service URL 
			String bioPortalServiceUrl = BIOPORTAL_GET_ROOTPATH_URL + ONTOLOGY_ID + "/"+ trimmedconceptId;
			
			String newbioPortalServiceUrl = bioPortalServiceUrl + "?" + apiKey;
			
			//Call Search REST URL and Parse results 
//			ParseXMLFile.parseXMLFile(bioPortalSearchUrl);
			bioPortalRootPath = ParseBioPortalXMLFile.getBioPortalRootPath(newbioPortalServiceUrl);
		}
		return bioPortalRootPath;
	}

	public static String findConceptLabel(String conceptId) {
		String conceptLabel = null;
		if(conceptId != null){		
			String trimmedconceptId = conceptId.trim();			
			
			/**
			* NOTE: See http://www.bioontology.org/wiki/index.php/NCBO_REST_services#Search_services for full list of search parameters  
			*/
			
			//Configure web service URL 
			String bioPortalServiceUrl = BIOPORTAL_GET_TERM_URL + ONTOLOGY_VERSION_ID +  "?conceptid="+ trimmedconceptId;
			
			String newbioPortalServiceUrl = bioPortalServiceUrl + "&" + apiKey;
			
			//Call Search REST URL and Parse results 
//			ParseXMLFile.parseXMLFile(bioPortalSearchUrl);
			conceptLabel = ParseBioPortalXMLFile.getTermLabel(newbioPortalServiceUrl);
		}
		return conceptLabel;
	}
}
