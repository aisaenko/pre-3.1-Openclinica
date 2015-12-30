package org.akaza.openclinica.bean.subject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.control.subjectmgmt.EditSubjectServlet;
import org.akaza.openclinica.dao.subject.LdapServer;

public class Person extends AuditableEntityBean {
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 0;
    public static final int GENDER_UNKNOWN = -1;
    private Logger logger = Logger.getLogger(getClass().getName());

    private String personId;
    private String surname;
    private String givenName;
    private String streetAddress;
    private String localityName;
    private String stateOrProvinceName;
    private String postalCode;
    private String telephoneNumber;
    private String emailAddress;
    private Calendar dob;
    // Social Security Number
    private String ssn;
    
    // Physical gender at birth, 0: male, 1:female
    private int gender;
    
    // Complete legal given name at birth
    private String givenNameAtBirth;
    
    // Complete legal family name at birth
    private String surnameAtBirth;
    
    // Complete additional legal name or names at birth
    private String middleNameAtBirth;
    
    // City of birth
    private String cob;
    
    // Father's complete legal given name at his birth
    private String fgivenName;
    
    // Father's complete legal family name at his birth
    private String fsurname;
    
    // Mother's complete legal given name at her birth
    private String mgivenName;
    
    // Mother's complete legal family name at her birth
    private String msurname;
    
    // Mother's day of birth
    private Calendar mdob;
    
    // Father's day of birth
    private Calendar fdob;
    
    /**
     * Indicates the system id that other persion id comes from.
     */
    private String otherSystemId;
    
    /**
     * Person id imported from other system.
     */
    private String otherPersonId;
    
    private SubjectEntryLabelBean selb;
    
    public Person(){
        personId = null;
        surname = null;
        givenName = null;
        streetAddress = null;
        localityName = null;
        stateOrProvinceName = null;
        postalCode = null;
        telephoneNumber = null;
        emailAddress = null;
        dob = null;
        gender = -1;
    }
    
    public Person(Attributes attrs){
        
        try {
            Attribute attr = attrs.get(LdapServer.UID);
            if(attr != null){
                this.personId = (String)attr.get();
            }

            attr = attrs.get(LdapServer.SN);
            if(attr != null){
                this.surname = (String)attr.get();
            }

            attr = attrs.get(LdapServer.GN);
            if(attr != null){
                this.givenName = (String)attr.get();
            }

            attr = attrs.get(LdapServer.EMAIL_ADDRESS);
            if(attr != null){
                this.emailAddress = (String)attr.get();
            }

            attr = attrs.get(LdapServer.LOCALITY_NAME);
            if(attr != null){
                this.localityName = (String)attr.get();
            }

            attr = attrs.get(LdapServer.STREET_ADDRESS);
            if(attr != null){
                this.streetAddress = (String)attr.get();
            }

            attr = attrs.get(LdapServer.STATE_OR_PROVINCE_NAME);
            if(attr != null){
                this.stateOrProvinceName = (String)attr.get();
            }

            attr = attrs.get(LdapServer.TELEPHONE_NUMBER);
            if(attr != null){
                this.telephoneNumber = (String)attr.get();
            }

            attr = attrs.get(LdapServer.POSTAL_CODE);
            if(attr != null){
                this.postalCode = (String)attr.get();
            }

            attr = attrs.get(LdapServer.DOB);
            String attrStr = (attr == null ? null : (String)attr.get());
            if(attrStr != null && attrStr.length() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        	this.dob = Calendar.getInstance();
	        	this.dob.clear();
	        	try {
	        		this.dob.setTime(sdf.parse(attrStr));
	            } catch (ParseException e){
	                logger.severe(e.getMessage());
	            }
            }
        } catch (NamingException e) {
            logger.severe(e.getMessage());
        }
    }
    
    public String getPersonId(){
        return personId;
    }
    
    public void setPersonId(String personId){
        this.personId = personId;
    }
    
    public String getFullName(){
        if((givenName == null || givenName.length() == 0) && (surname == null || surname.length() == 0)){
            return "";
        }
        
        return givenName + " " + surname;
    }
    
    public String getSurname(){
        return surname;
    }
    
    public void setSurname(String surname){
        this.surname = surname;
    }
    
    public String getGivenName(){
        return givenName;
    }

    public void setGivenName(String givenName){
        this.givenName = givenName;
    }
    
    public String getStreetAddress(){
        return streetAddress;
    }
    
    public void setStreetAddress(String streetAddress){
        this.streetAddress = streetAddress;
    }
    
    public String getLocalityName(){
        return localityName;
    }
    
    public void setLocalityName(String localityName){
        this.localityName = localityName;
    }
    
    public String getStateOrProvinceName(){
        return stateOrProvinceName;
    }
    
    public void setStateOrProvinceName(String stateOrProvinceName){
        this.stateOrProvinceName = stateOrProvinceName;
    }
    
    public String getPostalCode(){
        return postalCode;
    }
    
    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }
    
    public String getEmailAddress(){
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
    }
    
    public String getTelephoneNumber(){
        return telephoneNumber;
    }
    
    public void setTelephoneNumber(String telephoneNumber){
        this.telephoneNumber = telephoneNumber;
    }
    
    public String getOtherSystemId(){
        return otherSystemId;
    }
    
    public void setOtherSystemId(String otherSystemId){
        this.otherSystemId = otherSystemId;
    }
    
    public String getOtherPersonId(){
        return otherPersonId;
    }
    
    public void setOtherPersonId(String otherPersonId){
        this.otherPersonId = otherPersonId;
    }
    
    public SubjectEntryLabelBean getSubjectEntryLabelBean(){
        return selb;
    }
    
    public void setSubjectEntryLabelBean(SubjectEntryLabelBean selb){
        this.selb = selb;
    }
    
    public boolean hasPersonId(){
        if(personId == null || personId.length() == 0){
            return false;
        }
        
        return true;
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer("");
        sb.append(getFullName());
        sb.append("\n");
        sb.append(getStreetAddress());
        sb.append("\n");
        sb.append(getLocalityName());
        sb.append(" ");
        sb.append(getStateOrProvinceName());
        sb.append(", ");
        sb.append(getPostalCode());
        sb.append("\n");
        sb.append("Email: ");
        sb.append(getEmailAddress());
        sb.append("\n");
        sb.append("Tel: ");
        sb.append(getTelephoneNumber());
        sb.append("\n");
        sb.append("Dob: ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sb.append(getDob() == null ? null : sdf.format(getDob().getTime()));
        sb.append("\n");
        return sb.toString();
    }
    
    public boolean equals(Object obj){
	if(!(obj instanceof Person)){
	    return false;
	}
	Person p = (Person)obj;
	if(p == null){
	    return false;
	}
	if(!isSame(personId, p.getPersonId())){
	    return false;
	}
	if(!isSame(surname, p.getSurname())){
	    return false;
	}
	if(!isSame(givenName, p.getGivenName())){
	    return false;
	}
	if(!isSame(dob, p.getDob())){
	    return false;
	}
	if(!isSame(localityName, p.getLocalityName())){
	    return false;
	}
	if(!isSame(postalCode, p.getPostalCode())){
	    return false;
	}
	if(!isSame(stateOrProvinceName, p.getStateOrProvinceName())){
	    return false;
	}
	if(!isSame(streetAddress, p.getStreetAddress())){
	    return false;
	}
	if(!isSame(telephoneNumber, p.getTelephoneNumber())){
	    return false;
	}
	if(!isSame(otherPersonId, p.getOtherPersonId())){
	    return false;
	}
	if(!isSame(otherSystemId, p.getOtherSystemId())){
	    return false;
	}
	return true;
    }
    
    private boolean isSame(String s1, String s2){
	if((s1 == null || s1.length() == 0) && (s2 == null || s2.length() == 0)){
	    return true;
	}else if((s1 != null && s1.length() > 0) && (s2 != null && s2.length() > 0) && s1.equalsIgnoreCase(s2)){
		return true;
	}
	return false;
    }
    private boolean isSame(Calendar c1, Calendar c2){
	if(c1 == null && c2 == null){
	    return true;
	}else if(c1 == null || c2 == null){
	    return false;
	}else{
	    if(EditSubjectServlet.calendarToString(c1, "yyyyMMdd").equals(EditSubjectServlet.calendarToString(c2, "yyyyMMdd"))){
		return true;
	    }
	    return false;
	}
    }

    public String getCob() {
        return cob;
    }

    public void setCob(String cob) {
        this.cob = cob;
    }

    public Calendar getDob() {
        return dob;
    }

    public void setDob(Calendar dob) {
        this.dob = dob;
    }

    public Calendar getFdob() {
        return fdob;
    }

    public void setFdob(Calendar fdob) {
        this.fdob = fdob;
    }


    public Calendar getMdob() {
        return mdob;
    }

    public void setMdob(Calendar mdob) {
        this.mdob = mdob;
    }


    public String getMiddleNameAtBirth() {
        return middleNameAtBirth;
    }

    public void setMiddleNameAtBirth(String middleNameAtBirth) {
        this.middleNameAtBirth = middleNameAtBirth;
    }

    public String getFgivenName() {
        return fgivenName;
    }

    public void setFgivenName(String fgivenName) {
        this.fgivenName = fgivenName;
    }

    public String getFsurname() {
        return fsurname;
    }

    public void setFsurname(String fsurname) {
        this.fsurname = fsurname;
    }

    public String getGivenNameAtBirth() {
        return givenNameAtBirth;
    }

    public void setGivenNameAtBirth(String givenNameAtBirth) {
        this.givenNameAtBirth = givenNameAtBirth;
    }

    public String getMgivenName() {
        return mgivenName;
    }

    public void setMgivenName(String mgivenName) {
        this.mgivenName = mgivenName;
    }

    public String getMsurname() {
        return msurname;
    }

    public void setMsurname(String msurname) {
        this.msurname = msurname;
    }

    public String getSurnameAtBirth() {
        return surnameAtBirth;
    }

    public void setSurnameAtBirth(String surnameAtBirth) {
        this.surnameAtBirth = surnameAtBirth;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

 }
