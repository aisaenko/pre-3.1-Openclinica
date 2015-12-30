/**
 * 
 */
package org.akaza.openclinica.service.coding;

import org.akaza.openclinica.dao.core.CoreResources;

import sun.reflect.generics.factory.CoreReflectionFactory;


/**
 * @author pgawade
 *
 */
public class BioPortalWebServiceManager {
	private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this
			.getClass().getName());
	

	//ToDo: temporarily hard coded to access bioportal instance on NCBO central server. Later it will
	// be changed to connect to BioPortal instance hosted on Akaza server. apiKey will also need to be changed then.
//	private final static String  BIOPORTAL_URL_PREFIX = "http://rest.bioontology.org/bioportal/search/";
//	private final static String BIOPORTAL_GET_ROOTPATH_URL = "http://rest.bioontology.org/bioportal/virtual/rootpath/";
//	private final static String BIOPORTAL_GET_TERM_URL = "http://rest.bioontology.org/bioportal/concepts/"; 
	private final static String apiKey = "apikey=d1401892-8b8f-463e-ba26-426650edf04a";
	private final static String  BIOPORTAL_WS_URL_SEARCH = "search/?query=";
	private final static String BIOPORTAL_WS_URL_GET_ROOTPATH = "virtual/rootpath/";
	private final static String BIOPORTAL_WS_URL_GET_TERM = "concepts/";
	private final static String TERMINOLOGY_SERVICES_URL_PROP = "terminologyServicesURL";
	/**
	 * @param args
	 * Set the value of ONTOLOGY_ID to search ontology or ontologies or interest. To search multiple 
	 * ontologies, include ontology identifier as a comma-separated list. To find the ontologyId, use the 
	 * "List latest version" web service documented 
	 * at: http://www.bioontology.org/wiki/index.php/NCBO_REST_services#List_all_the_latest_version_of_ontologies
	 */
	public static String findMatch(String ontologyId, String term, boolean isExactMatch) {
		String bioPortalConceptId = null;
		if(term != null){		
			String trimmedSearchTerm = term.trim();			
			
			/**
			* NOTE: See http://www.bioontology.org/wiki/index.php/NCBO_REST_services#Search_services for full list of search parameters  
			*/
			String searchParameters = null;
			//@pgawade 07-Dec-2011: exact match should be turned off in the search parameters in order to
			// leave it upto auto coding in case there is match found with more than one terms in the dictionary
			if(isExactMatch){
				searchParameters = "?ontologyids="+ontologyId+"&isexactmatch=1&includeproperties=0";
			}
			else{
				searchParameters = "?ontologyids="+ontologyId+"&isexactmatch=0&includeproperties=0";
			}
				
			//Configure web service URL 
//			String bioPortalSearchUrl = BIOPORTAL_URL_PREFIX+trimmedSearchTerm+searchParameters;
			String bioPortalSearchUrl = CoreResources.getField(TERMINOLOGY_SERVICES_URL_PROP) + BIOPORTAL_WS_URL_SEARCH + trimmedSearchTerm + searchParameters;
			String newBioPortalSearchUrl = bioPortalSearchUrl + "&" + apiKey;
			
			//Call Search REST URL and Parse results 
//			ParseXMLFile.parseXMLFile(bioPortalSearchUrl);
			bioPortalConceptId = ParseBioPortalXMLFile.getBioPortalConceptId(newBioPortalSearchUrl, term);
		}
		return bioPortalConceptId;
	}
	
	public static String findConceptRootPath(String ontologyId,String conceptId) {
		String bioPortalRootPath = null;
		if(conceptId != null){		
			String trimmedconceptId = conceptId.trim();			
			
			/**
			* NOTE: See http://www.bioontology.org/wiki/index.php/NCBO_REST_services#Search_services for full list of search parameters  
			*/
			
			//Configure web service URL 
//			String bioPortalServiceUrl = BIOPORTAL_GET_ROOTPATH_URL + ontologyId + "/"+ trimmedconceptId;
			String bioPortalServiceUrl = CoreResources.getField(TERMINOLOGY_SERVICES_URL_PROP) + BIOPORTAL_WS_URL_GET_ROOTPATH + ontologyId + "/"+ trimmedconceptId;
			String newbioPortalServiceUrl = bioPortalServiceUrl + "?" + apiKey;
			
			//Call Search REST URL and Parse results 
//			ParseXMLFile.parseXMLFile(bioPortalSearchUrl);
			bioPortalRootPath = ParseBioPortalXMLFile.getBioPortalRootPath(newbioPortalServiceUrl);
		}
		return bioPortalRootPath;
	}

	public static String findConceptLabel(String ontologyVersionId, String conceptId) {
		String conceptLabel = null;
		if(conceptId != null){		
			String trimmedconceptId = conceptId.trim();			
			
			/**
			* NOTE: See http://www.bioontology.org/wiki/index.php/NCBO_REST_services#Search_services for full list of search parameters  
			*/
			
			//Configure web service URL 
//			String bioPortalServiceUrl = BIOPORTAL_GET_TERM_URL + ontologyVersionId +  "?conceptid="+ trimmedconceptId;
			String bioPortalServiceUrl = CoreResources.getField(TERMINOLOGY_SERVICES_URL_PROP) + BIOPORTAL_WS_URL_GET_TERM + ontologyVersionId +  "?conceptid="+ trimmedconceptId;
			String newbioPortalServiceUrl = bioPortalServiceUrl + "&" + apiKey;
			
			//Call Search REST URL and Parse results 
//			ParseXMLFile.parseXMLFile(bioPortalSearchUrl);
			conceptLabel = ParseBioPortalXMLFile.getTermLabel(newbioPortalServiceUrl);
		}
		return conceptLabel;
	}

}
