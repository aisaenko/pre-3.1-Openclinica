package org.akaza.openclinica.dao.subject;

/**
 * The singleton class was used to interact with LDAP server
 * 
 * @author Hailong Wang, Ph.D
 * @version 1.0
 */
import gov.nih.ndar.webservices.guid.client.GuidWSClient;
import gov.nih.ndar.webservices.guid.client.GuidWSStub;
import gov.nih.ndar.webservices.guid.client.SubjectManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.akaza.openclinica.bean.subject.Person;
import org.akaza.openclinica.dao.core.SQLInitServlet;

public class LdapServer {
    public static final int GT = 1;
    public static final int GE = 2;
    public static final int LT = 3;
    public static final int LE = 4;
    public static final int EQ = 5;
    public static final int UE = 6;

    public static final String UID = "uid";
    public static final String CN = "cn";
    public static final String SN = "sn";
    public static final String GN = "givenName";
    public static final String TELEPHONE_NUMBER = "telephonenumber";
    public static final String STREET_ADDRESS = "street";
    public static final String LOCALITY_NAME = "l";
    public static final String POSTAL_CODE = "postalCode";
    public static final String STATE_OR_PROVINCE_NAME = "st";
    public static final String EMAIL_ADDRESS = "mail";
    public static final String DOB = "dob";
    public static final String COB = "cob";
    public static final String SSN = "ssn";
    
    public static final String SURNAME_AT_BIRTH = "snb";
    public static final String GIVEN_NAME_AT_BIRTH = "gnb";
    public static final String MIDDLE_NAME_AT_BIRTH = "mnb";
    
    public static final String GENDER = "gender";
    public static final String SURNAME_OF_FATHER = "fsnb";
    public static final String GIVEN_NAME_OF_FATHER = "fgnb";
    public static final String DOB_OF_FATHER = "fdob";
    public static final String SURNAME_OF_MOTHER = "msnb";
    public static final String GIVEN_NAME_OF_MOTHER = "mgnb";
    public static final String DOB_OF_MOTHER = "mdob";
    
    public static final int QUERY_BY_UID = 1;
    public static final int QUERY_BY_CN = 2;
    public static final int QUERY_BY_SN = 3;
    public static final int QUERY_BY_GN = 4;
    public static final int QUERY_BY_TELEPHONE_NUMBER = 5;
    public static final int QUERY_BY_STREET_ADDRESS = 6;
    public static final int QUERY_BY_LOCALITY_NAME = 7;
    public static final int QUERY_BY_POSTAL_CODE = 8;
    public static final int QUERY_BY_STATE_OR_PROVINCE_NAME = 9;
    public static final int QUERY_BY_EMAIL_ADDRESS = 10;
    public static final int QUERY_BY_DOB = 11;
    
    public static final int SUCCESS = 0;
    public static final int EMPTY_PERSON_ERROR = 1;
    public static final int NO_CONNECTION_TO_LDAP_SERVER_ERROR = 2;
    public static final int ALREADY_EXISTING_SUBJECT_ERROR = 3;
    public static final int CREATING_SUBCONTEXT_IN_LDAP_ERROR = 4;
    public static final String GUID_GENERATION_WS = "ws";

    private String baseDN;

    private Logger logger = Logger.getLogger(getClass().getName());

    private LdapContext context;
    private static LdapServer server;
    private LdapServer() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
/*
        env.put(Context.PROVIDER_URL, "ldap://localhost:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=admin, " + baseDN);
        env.put(Context.SECURITY_CREDENTIALS, "1234");
*/

        env.put(Context.PROVIDER_URL, SQLInitServlet.getLdapProviderURL());
        env.put(Context.SECURITY_AUTHENTICATION, SQLInitServlet.getSecurityAuthentication());
        env.put(Context.SECURITY_PRINCIPAL, SQLInitServlet.getSecurityPrincipal());
        env.put(Context.SECURITY_CREDENTIALS, SQLInitServlet.getSecurityCredentials());
        if (SQLInitServlet.getLdapBaseDN()!=null) {
        	baseDN=SQLInitServlet.getLdapBaseDN();
        }

        try {
            context = new InitialLdapContext(env, null);
        } catch (NamingException e) {
            logger.severe(e.getMessage());
        }
    }

    public static LdapServer getInstance(){
	if(server == null){
	    server = new LdapServer();
	}
	return server;
    }
    
    public boolean isConnected(){
	if(context == null){
	    return false;
	}
	return true;
    }
    
    public boolean deletePerson(Person person) {
        if (context == null) {
            return false;
        }

        String dn = createDN(person);
        try {
            context.destroySubcontext(dn);
        } catch (NameNotFoundException e) {
            logger.info(e.getMessage());
            return false;
        } catch (NotContextException e) {
            logger.info(e.getMessage());
            return false;
        } catch (ContextNotEmptyException e) {
            logger.info(e.getMessage());
            return false;
        } catch (NamingException e) {
            logger.info(e.getMessage());
            return false;
        }
        return true;
    }

    public List<Person> query(int qt, Object value) {
    	if (context==null) {
    		return null;
    	}

    	Attributes matchAttrs = new BasicAttributes(true);
        if(qt ==  LdapServer.QUERY_BY_CN){
            matchAttrs.put(new BasicAttribute(CN, value));
        }else if(qt ==  LdapServer.QUERY_BY_EMAIL_ADDRESS){
            matchAttrs.put(new BasicAttribute(EMAIL_ADDRESS, value));
        }else if(qt ==  LdapServer.QUERY_BY_GN){
            matchAttrs.put(new BasicAttribute(GN, value));
        }else if(qt ==  LdapServer.QUERY_BY_SN){
            matchAttrs.put(new BasicAttribute(SN, value));
        }else if(qt ==  LdapServer.QUERY_BY_LOCALITY_NAME){
            matchAttrs.put(new BasicAttribute(LOCALITY_NAME, value));
        }else if(qt ==  LdapServer.QUERY_BY_POSTAL_CODE){
            matchAttrs.put(new BasicAttribute(POSTAL_CODE, value));
        }else if(qt ==  LdapServer.QUERY_BY_STATE_OR_PROVINCE_NAME){
            matchAttrs.put(new BasicAttribute(STATE_OR_PROVINCE_NAME, value));
        }else if(qt ==  LdapServer.QUERY_BY_STREET_ADDRESS){
            matchAttrs.put(new BasicAttribute(STREET_ADDRESS, value));
        }else if(qt ==  LdapServer.QUERY_BY_TELEPHONE_NUMBER){
            matchAttrs.put(new BasicAttribute(TELEPHONE_NUMBER, value));
        }else if(qt ==  LdapServer.QUERY_BY_UID){
            matchAttrs.put(new BasicAttribute(UID, value));
        }else{
            
        }
        List<Person> personList = new Vector<Person>();
        try {
            NamingEnumeration answers = context.search(baseDN, matchAttrs);
            while (answers.hasMore()) {
                SearchResult sr = (SearchResult) answers.next();
                Attributes attrs = sr.getAttributes();
                if (attrs == null) {
                    continue;
                }
                Person person = new Person(attrs);
                personList.add(person);
            }
        } catch (NamingException e) {
            logger.severe(e.getMessage());
        }
        return personList;
    }

    public int insertPerson(Person person) {
        if (person == null) {
            return EMPTY_PERSON_ERROR;
        }

        Attributes attributes = new BasicAttributes();

        Attribute objClasses = new BasicAttribute("objectclass");
        objClasses.add("top");
        objClasses.add("ndarPerson");
        attributes.put(objClasses);

        String stringFullName = person.getFullName();
        if(stringFullName != null && stringFullName.length() > 0){
            Attribute cn = new BasicAttribute(LdapServer.CN, stringFullName);
            attributes.put(cn);
        }

        String stringSurname = person.getSurname();
        if (stringSurname != null && stringSurname.length() > 0) {
            Attribute sn = new BasicAttribute(LdapServer.SN, stringSurname);
            attributes.put(sn);
        }

        String stringGivenName = person.getGivenName();
        if (stringGivenName != null && stringGivenName.length() > 0) {
            Attribute gn = new BasicAttribute(LdapServer.GN, stringGivenName);
            attributes.put(gn);
        }

        String givenNameAtBirth = person.getGivenNameAtBirth();
        if (givenNameAtBirth != null && givenNameAtBirth.length() > 0) {
            Attribute sn = new BasicAttribute(LdapServer.GIVEN_NAME_AT_BIRTH, givenNameAtBirth);
            attributes.put(sn);
        }

        String middleNameAtBirth = person.getMiddleNameAtBirth();
        if (middleNameAtBirth != null && middleNameAtBirth.length() > 0) {
            Attribute mnab = new BasicAttribute(LdapServer.MIDDLE_NAME_AT_BIRTH, middleNameAtBirth);
            attributes.put(mnab);
        }

        String surnameAtBirth = person.getSurnameAtBirth();
        if (surnameAtBirth != null && surnameAtBirth.length() > 0) {
            Attribute snab = new BasicAttribute(LdapServer.SURNAME_AT_BIRTH, surnameAtBirth);
            attributes.put(snab);
        }
        
        String fgivenName = person.getFgivenName();
        if (fgivenName != null && fgivenName.length() > 0) {
            Attribute fgn = new BasicAttribute(LdapServer.GIVEN_NAME_OF_FATHER, fgivenName);
            attributes.put(fgn);
        }

        String fsurname = person.getFsurname();
        if (fsurname != null && fsurname.length() > 0) {
            Attribute fsn = new BasicAttribute(LdapServer.SURNAME_OF_FATHER, fsurname);
            attributes.put(fsn);
        }
        
        String mgivenName = person.getMgivenName();
        if (mgivenName != null && mgivenName.length() > 0) {
            Attribute mgn = new BasicAttribute(LdapServer.GIVEN_NAME_OF_MOTHER, mgivenName);
            attributes.put(mgn);
        }

        String msurname = person.getMsurname();
        if (msurname != null && msurname.length() > 0) {
            Attribute msn = new BasicAttribute(LdapServer.SURNAME_OF_MOTHER, msurname);
            attributes.put(msn);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if(person.getDob() != null){
            Attribute dob = new BasicAttribute(LdapServer.DOB, sdf.format(person.getDob().getTime()));
            attributes.put(dob);
        }
        
        if(person.getFdob() != null){
            Attribute fdob = new BasicAttribute(LdapServer.DOB_OF_FATHER, sdf.format(person.getFdob().getTime()));
            attributes.put(fdob);
        }
        
        if(person.getMdob() != null){
            Attribute mdob = new BasicAttribute(LdapServer.DOB_OF_MOTHER, sdf.format(person.getMdob().getTime()));
            attributes.put(mdob);
        }
        
        String stringTelephoneNumber = person.getTelephoneNumber();
        if (stringTelephoneNumber != null && stringTelephoneNumber.length() > 0) {
            Attribute telephoneNumber = new BasicAttribute(LdapServer.TELEPHONE_NUMBER, stringTelephoneNumber);
            attributes.put(telephoneNumber);
        }

        String stringStreetAddress = person.getStreetAddress();
        if (stringStreetAddress != null && stringStreetAddress.length() > 0) {
            Attribute streetAddress = new BasicAttribute(
                    LdapServer.STREET_ADDRESS, stringStreetAddress);
            attributes.put(streetAddress);
        }

        String stringLocalityName = person.getLocalityName();
        if (stringLocalityName != null && stringLocalityName.length() > 0) {
            Attribute localityName = new BasicAttribute(
                    LdapServer.LOCALITY_NAME, stringLocalityName);
            attributes.put(localityName);
        }

        String stringPostalCode = person.getPostalCode();
        if (stringPostalCode != null && stringPostalCode.length() > 0) {
            Attribute postalCode = new BasicAttribute(LdapServer.POSTAL_CODE,
                    stringPostalCode);
            attributes.put(postalCode);
        }

        String stringStateOrProvinceName = person.getStateOrProvinceName();
        if (stringStateOrProvinceName != null
                && stringStateOrProvinceName.length() > 0) {
            Attribute stateOrProvinceName = new BasicAttribute(
                    LdapServer.STATE_OR_PROVINCE_NAME,
                    stringStateOrProvinceName);
            attributes.put(stateOrProvinceName);
        }

        String stringEmailAddress = person.getEmailAddress();
        if (stringEmailAddress != null && stringEmailAddress.length() > 0) {
            Attribute emailAddress = new BasicAttribute(
                    LdapServer.EMAIL_ADDRESS, stringEmailAddress);
            attributes.put(emailAddress);
        }
        
        String stringCob = person.getCob();
        if(stringCob != null && stringCob.length() > 0){
            Attribute cobAttr = new BasicAttribute(LdapServer.COB, stringCob);
            attributes.put(cobAttr);
        }
        
        String ssnString = person.getSsn();
        if(ssnString != null && ssnString.length() > 0){
            Attribute ssnAttr = new BasicAttribute(LdapServer.SSN, ssnString);
            attributes.put(ssnAttr);
        }
        
        int gender = person.getGender();
        if (gender != -1) {
            Attribute genderAttr = new BasicAttribute(
                    LdapServer.GENDER, Integer.toString(gender));
            attributes.put(genderAttr);
        }
        if (!person.hasPersonId()) {
            person.setPersonId(createPersonId(person, SQLInitServlet.getGuidGeneration()));
        }

        /*
         * There are two posiblities that NDAR ID comes from:
         * (1) GUID WS
         * 	(a) If there is a record with the same NDAR ID in the LDAP server, that 
         * 	    means you need to update the PII information instead of creating new record.
         * 	(b) If there is no record with the same NDAR ID in the LDAP server, that
         * 	    means you need to create a new record in the LDAP server.
         * (2) Local generated randomly
         * 	(a) If there is a record with the same NDAR ID in the LDAP server, that
         * 	    means the NDAR ID randomly generated was used, you have to generate
         * 	    a new unique NDAR ID, then create a new record in the LDAP server.
         * 	(b) if there is no record with the same NDAR ID in the LDAP server, that
         * 	    means the NDAR ID randomly generated is already unique and ready to be
         * 	    used.
         */
         
        synchronized (context) {
            String pid = person.getPersonId();
	    List<Person> ps = query(LdapServer.QUERY_BY_UID, pid);
	    if (ps != null && ps.size() > 0) {
		if (GUID_GENERATION_WS.equals(SQLInitServlet.getGuidGeneration())) {
		    return ALREADY_EXISTING_SUBJECT_ERROR;
		}
	    while (ps != null && ps.size() > 0) {
			NDARIDManager manager = NDARIDManager.getInstance();
			pid = manager.getNDARId();
			ps = query(LdapServer.QUERY_BY_UID, pid);
		    }
		    person.setPersonId(pid);
		    String dn = createDN(person);
		    Attribute uid = new BasicAttribute(LdapServer.UID, person
			    .getPersonId());
		    attributes.put(uid);

		    try {
			context.createSubcontext(dn, attributes);
		    } catch (NamingException e) {
			logger.severe(e.getMessage());
			return CREATING_SUBCONTEXT_IN_LDAP_ERROR;
		    }
	    } else {
		String dn = createDN(person);
		Attribute uid = new BasicAttribute(LdapServer.UID, person
			.getPersonId());
		attributes.put(uid);

		try {
		    context.createSubcontext(dn, attributes);
		} catch (NamingException e) {
		    logger.severe(e.getMessage());
		    return CREATING_SUBCONTEXT_IN_LDAP_ERROR;
		}
	    }
	}
        return SUCCESS;
    }

    public boolean updatePerson(Person oldPerson, Person newPerson) throws NamingException{
	if(context == null){
	    return false;
	}
        if (oldPerson == null || !oldPerson.hasPersonId()) {
            return false;
        }
        if (newPerson == null || !newPerson.hasPersonId()) {
            return false;
        }

        if (!newPerson.getPersonId().equals(oldPerson.getPersonId())) {
            return false;
        }

        if(newPerson.equals(oldPerson)){
            return true;
        }
        
        String dn = createDN(newPerson);
        Attributes existedAttrs = null;
        synchronized (context) {
	    try {
		existedAttrs = context.getAttributes(dn);
	    } catch (NamingException e) {
		logger.info("The entry does not exist: " + dn);
		logger.severe(e.getMessage());
		return false;
	    }

	    if (isChanged(oldPerson, existedAttrs)) {
		logger.info("Somebody has changed the attributes since you retrieved those attributes. So you are not able to update the attributes related to this dn: "
				+ dn);
		return false;
	    }
	    List<ModificationItem> mods = new Vector<ModificationItem>(1);
	    ModificationItem mi = createModificationItem(newPerson
		    .getFullName(), existedAttrs.get(LdapServer.CN), LdapServer.CN);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getSurname(), existedAttrs
		    .get(LdapServer.SN), LdapServer.SN);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getGivenName(), existedAttrs
		    .get(LdapServer.GN), LdapServer.GN);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getTelephoneNumber(),
		    existedAttrs.get(LdapServer.TELEPHONE_NUMBER), LdapServer.TELEPHONE_NUMBER);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getStreetAddress(),
		    existedAttrs.get(LdapServer.STREET_ADDRESS), LdapServer.STREET_ADDRESS);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getLocalityName(),
		    existedAttrs.get(LdapServer.LOCALITY_NAME), LdapServer.LOCALITY_NAME);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getPostalCode(), existedAttrs
		    .get(LdapServer.POSTAL_CODE), LdapServer.POSTAL_CODE);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getStateOrProvinceName(),
		    existedAttrs.get(LdapServer.STATE_OR_PROVINCE_NAME), LdapServer.STATE_OR_PROVINCE_NAME);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getEmailAddress(),
		    existedAttrs.get(LdapServer.EMAIL_ADDRESS), LdapServer.EMAIL_ADDRESS);
	    if (mi != null) {
		mods.add(mi);
	    }

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    mi = createModificationItem(sdf.format(newPerson.getDob().getTime()),
		    existedAttrs.get(LdapServer.DOB), LdapServer.DOB);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getGivenNameAtBirth(),
		    existedAttrs.get(LdapServer.GIVEN_NAME_AT_BIRTH), LdapServer.GIVEN_NAME_AT_BIRTH);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getSurnameAtBirth(),
		    existedAttrs.get(LdapServer.SURNAME_AT_BIRTH), LdapServer.SURNAME_AT_BIRTH);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getMiddleNameAtBirth(),
		    existedAttrs.get(LdapServer.MIDDLE_NAME_AT_BIRTH), LdapServer.MIDDLE_NAME_AT_BIRTH);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getFgivenName(),
		    existedAttrs.get(LdapServer.GIVEN_NAME_OF_FATHER), LdapServer.GIVEN_NAME_OF_FATHER);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getFsurname(),
		    existedAttrs.get(LdapServer.SURNAME_OF_FATHER), LdapServer.SURNAME_OF_FATHER);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getMgivenName(),
		    existedAttrs.get(LdapServer.GIVEN_NAME_OF_MOTHER), LdapServer.GIVEN_NAME_OF_MOTHER);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getMsurname(),
		    existedAttrs.get(LdapServer.SURNAME_OF_MOTHER), LdapServer.SURNAME_OF_MOTHER);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(Integer.toString(newPerson.getGender()),
		    existedAttrs.get(LdapServer.GENDER), LdapServer.GENDER);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(sdf.format(newPerson.getFdob().getTime()),
		    existedAttrs.get(LdapServer.DOB_OF_FATHER), LdapServer.DOB_OF_FATHER);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(sdf.format(newPerson.getMdob().getTime()),
		    existedAttrs.get(LdapServer.DOB_OF_MOTHER), LdapServer.DOB_OF_MOTHER);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getCob(),
		    existedAttrs.get(LdapServer.COB), LdapServer.COB);
	    if (mi != null) {
		mods.add(mi);
	    }

	    mi = createModificationItem(newPerson.getSsn(),
		    existedAttrs.get(LdapServer.SSN), LdapServer.SSN);
	    if (mi != null) {
		mods.add(mi);
	    }

	    try {
		ModificationItem[] modis = new ModificationItem[mods.size()];
		mods.toArray(modis);
		context.modifyAttributes(dn, modis);
	    } catch (NamingException e) {
		logger.severe(e.getMessage());
		return false;
	    }
	}
	return true;
    }

    /**
         * Creates the ModificationItem object based on the current value and
         * value existed in the ldap server.
         * 
         * @param s
         *                the current value.
         * @param attr
         *                the current attribute in the ldap server.
         * @param attrId  the attribute id for the attribute.
         * 
         * @return a ModificationItem object if current value and value existed
         *         in the ldap server are different, otherwise null
         */
    private ModificationItem createModificationItem(String s, Attribute attr,
	    String attrId) {
	    if (s != null && s.length() > 0) {
		if (attr != null) {
		    return new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
			    new BasicAttribute(attr.getID(), s));
		}
	    return new ModificationItem(DirContext.ADD_ATTRIBUTE,
			    new BasicAttribute(attrId, s));
	    }
		if (attr != null) {
		    return new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
			    new BasicAttribute(attr.getID()));
		}
	return null;
    }
    /**
         * Check if the attribute was changed since this attribute was
         * retrieved.
         * 
         * @param value
         *                the old value which was retrieved previously.
         * @param attr
         *                current attribute in the ldap server.
         * @return true if current attribute is different from the old value.
         * @throws NamingException
         *                 exception happened when retrieving the value for this
         *                 attribute.
         */
    private boolean isChanged(String oldValue, Attribute attr)
	    throws NamingException {
	if (oldValue == null || oldValue.length() == 0) {
	    if (attr == null) {
		return false;
	    }
	    try {
		String currentValue = (String) attr.get();
		if (currentValue != null && currentValue.length() > 0) {
		    return true;
		}
		return false;
	    } catch (NoSuchElementException nsee) {
		logger.info(nsee.getMessage());
		return false;
	    }
	}

	if (attr == null) {
	    return true;
	}

	try {
	    String currentValue = (String) attr.get();
	    if (currentValue == null || currentValue.length() == 0) {
		return true;
	    } else if (currentValue.equals(oldValue)) {
		return false;
	    } else {
		return true;
	    }
	} catch (NoSuchElementException nsee) {
	    logger.info(nsee.getMessage());
	    return true;
	}
    }
    
    /**
         * Check if the attributes associated with person id was changed since
         * the person was created based on LDAP server.
         * 
         * @param person
         *                a person object.
         * @param attrs
         *                current attributes associated with person id.
         * @return true if the attributes didn't change since the person was
         *         created based on LDAP server.
         */
    private boolean isChanged(Person person, Attributes attrs) throws NamingException{
        if(isChanged(person.getFullName(), attrs.get(LdapServer.CN))){
            return true;
        }
        if(isChanged(person.getSurname(), attrs.get(LdapServer.SN))){
            return true;
        }
        if(isChanged(person.getGivenName(), attrs.get(LdapServer.GN))){
            return true;
        }
        if(isChanged(person.getTelephoneNumber(), attrs.get(LdapServer.TELEPHONE_NUMBER))){
            return true;
        }

        if(isChanged(person.getStreetAddress(), attrs.get(LdapServer.STREET_ADDRESS))){
            return true;
        }

        if(isChanged(person.getLocalityName(), attrs.get(LdapServer.LOCALITY_NAME))){
            return true;
        }

        if(isChanged(person.getPostalCode(), attrs.get(LdapServer.POSTAL_CODE))){
            return true;
        }

        if(isChanged(person.getStateOrProvinceName(), attrs.get(LdapServer.STATE_OR_PROVINCE_NAME))){
            return true;
        }

        if(isChanged(person.getEmailAddress(), attrs.get(LdapServer.EMAIL_ADDRESS))){
            return true;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if(isChanged(sdf.format(person.getDob().getTime()), attrs.get(LdapServer.DOB))){
            return true;
        }
	return false;
    }
    
    public String createPersonId(Person person, String guidGeneration ){
	if(guidGeneration != null && "ws".equals(guidGeneration)){
	    SubjectManager manager = SubjectManager.getInstance();
	    GuidWSStub.GuidRequest request = manager.createRequest(person);
	    
	    GuidWSClient client = GuidWSClient.getInstance();
	    return client.getGuid(request).getGuidResponse();
	}
    NDARIDManager manager = NDARIDManager.getInstance();
    return manager.getNDARId();
    }

    public String createDN(Person person) {
        if (person == null || !person.hasPersonId()) {
            return null;
        }

        StringBuffer sb = new StringBuffer("");
        sb.append("uid=");
        sb.append(person.getPersonId());
        sb.append(", ");
        sb.append(baseDN);
        return sb.toString();
    }

    public int countPersons() {
        return countPersons(baseDN);
    }

    private int countPersons(String dn) {
    	if (context==null) {
    		return -1;
    	}

    	int count = 0;
        NamingEnumeration persons = null;
        try {
            persons = context.list(dn);
        } catch (NamingException e) {

        }
        if (persons == null) {
            return 0;
        }
        try {
            while (persons.hasMore()) {
                persons.next();
                count++;
            }
            return count;
        } catch (NamingException e) {

        }
        return -1;
    }

    public static void main(String[] argv) {
        LdapServer server = LdapServer.getInstance();
        Person person = new Person();
        person.setSurname("Joe");
        person.setGivenName("Smith");
        person.setLocalityName("Bethesda");
        person.setStateOrProvinceName("MD");
        person.setEmailAddress("smith.joe@abc.com");
        person.setTelephoneNumber("301-555-6666");
        person.setStreetAddress("2000 Rockville Pike, Apt# 8");
        person.setPostalCode("20892");
        Calendar dob = Calendar.getInstance();
        dob.set(2000, 10, 10);
        person.setDob(dob);

//        person.setPersonId("ndar_2");
//        server.updatePerson(person);
        //server.deletePerson(person);
        server.insertPerson(person);
        List<Person> personList = server.query(QUERY_BY_CN, "Smith Joe");
        if(personList == null){
            return;
        }
        for(Person p : personList){
            System.out.println(p.toString());
        }
    }
}
