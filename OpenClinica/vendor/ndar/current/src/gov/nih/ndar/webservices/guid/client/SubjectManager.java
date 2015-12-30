package gov.nih.ndar.webservices.guid.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.akaza.openclinica.bean.subject.Person;

public class SubjectManager {
    public static final int PERFECT_HASH_CODE = 1;

    public static final int GOOD_HASH_CODE = 0;

    public static final int BAD_HASH_CODE = -1;

    public static final String UNKNOW_STRING = "*";

    private Logger logger;

    private static SubjectManager manager;

    private GuidWSStub.GuidWSConfigResponse config;

    private MessageDigest md;

    private SubjectManager() {
	logger = Logger.getLogger(SubjectManager.class.getName());
	try {
	    md = MessageDigest.getInstance("SHA-512");
	} catch (NoSuchAlgorithmException e) {
	    logger
		    .info("The MessageDigest with SHA-512 algorithm can not be created: "
			    + e.getMessage());
	    e.printStackTrace();
	}
	GuidWSClient client = GuidWSClient.getInstance();
	config = client.getGuidWSConfig();
    }

    public static SubjectManager getInstance() {
	if (manager == null) {
	    manager = new SubjectManager();
	}
	return manager;
    }

    public String getGuid(Person person) throws TooFewInformationException {
	GuidWSStub.GuidRequest request = createRequest(person);
	GuidWSClient client = GuidWSClient.getInstance();
	GuidWSStub.GuidResponse response = client.getGuid(request);
	if (response != null) {
	    return response.getGuidResponse();
	}
	return null;
    }

    public GuidWSStub.GuidRequest createRequest(Person person) {
	GuidWSStub.GuidRequest request = new GuidWSStub.GuidRequest();
	List fields = getFieldsForHashCode1(person);
	String digestInput = createDigestInput(fields);
	request.setHashCode1(generateHashCode(digestInput,
		countMissingField(fields)));

	fields = getFieldsForHashCode2(person);
	digestInput = createDigestInput(fields);
	request.setHashCode2(generateHashCode(digestInput,
		countMissingField(fields)));

	fields = getFieldsForHashCode3(person);
	digestInput = createDigestInput(fields);
	request.setHashCode3(generateHashCode(digestInput,
		countMissingField(fields)));

	fields = getFieldsForHashCode4(person);
	digestInput = createDigestInput(fields);
	request.setHashCode4(generateHashCode(digestInput,
		countMissingField(fields)));

	fields = getFieldsForHashCode5(person);
	digestInput = createDigestInput(fields);
	request.setHashCode5(generateHashCode(digestInput,
		countMissingField(fields)));
	return request;
    }

    public final byte[] generateHashCode(String input, int nm) {
	if (md == null || input == null) {
	    return null;
	}
	md.reset();
	md.update(input.getBytes());
	byte[] digest = md.digest();
	byte[] hashCode = new byte[digest.length + 1];
	System.arraycopy(digest, 0, hashCode, 0, digest.length);
	hashCode[digest.length] = (byte) nm;
	return hashCode;
    }

    public String getAlgorithm() {
	if (md == null) {
	    return md.getAlgorithm();
	}

	return null;
    }

    public boolean validate(Person person) {
	if (person == null) {
	    logger.warning("The Person is null!");
	    return false;
	}
	if (config == null) {
	    logger.warning("The GuidWSConfig is null!");
	    return false;
	}

	int numberOfPerfect = 0;
	int numberOfJustFine = 0;

	// First hash code
	List fields = getFieldsForHashCode1(person);
	int value = validate(fields, config.getHashCode1MatchRule());
	if (value == PERFECT_HASH_CODE) {
	    numberOfPerfect++;
	} else if (value == GOOD_HASH_CODE) {
	    numberOfJustFine++;
	}

	// Second hash code
	fields = getFieldsForHashCode2(person);
	value = validate(fields, config.getHashCode2MatchRule());
	if (value == PERFECT_HASH_CODE) {
	    numberOfPerfect++;
	} else if (value == GOOD_HASH_CODE) {
	    numberOfJustFine++;
	}

	// Third hash code
	fields = getFieldsForHashCode3(person);
	fields.clear();
	value = validate(fields, config.getHashCode3MatchRule());
	if (value == PERFECT_HASH_CODE) {
	    numberOfPerfect++;
	} else if (value == GOOD_HASH_CODE) {
	    numberOfJustFine++;
	}

	// Fourth hash code
	fields = getFieldsForHashCode4(person);
	value = validate(fields, config.getHashCode4MatchRule());
	if (value == PERFECT_HASH_CODE) {
	    numberOfPerfect++;
	} else if (value == GOOD_HASH_CODE) {
	    numberOfJustFine++;
	}

	// Fifth hash code
	fields = getFieldsForHashCode5(person);
	value = validate(fields, config.getHashCode4MatchRule());
	if (value == PERFECT_HASH_CODE) {
	    numberOfPerfect++;
	} else if (value == GOOD_HASH_CODE) {
	    numberOfJustFine++;
	}

	// Is a valid collection of subject information
	if (numberOfPerfect >= config.getSubjectMatchRule()
		.getThresholdForPerfectMatch()) {
	    return true;
	}

	if (numberOfJustFine >= config.getSubjectMatchRule()
		.getThresholdForGoodMatch()) {
	    return true;
	}

	if ((numberOfPerfect + numberOfJustFine) >= config
		.getSubjectMatchRule().getThresholdForMixedMatch()) {
	    return true;
	}

	return false;
    }

    public static final List getFieldsForHashCode1(Person person) {
	List<String> fields = new Vector<String>(4);
	fields.add(person.getSsn());
	int gender = person.getGender();
	if(gender == Person.GENDER_UNKNOWN){
	    fields.add(null);
	}else{
	    fields.add(Integer.toString(gender));
	}
	Calendar dob = person.getDob();
	if(dob == null){
	    fields.add(null);
	    fields.add(null);
	} else {
	    fields.add(Integer.toString(dob.get(Calendar.DAY_OF_MONTH)));
	    fields.add(Integer.toString(dob.get(Calendar.YEAR)));
	}
	return fields;
    }

    public static final List getFieldsForHashCode2(Person person) {
	List<String> fields = new Vector<String>(6);
	fields.add(person.getGivenNameAtBirth());
	fields.add(person.getSurnameAtBirth());
	fields.add(person.getMiddleNameAtBirth());
	Calendar dob = person.getDob();
	if(dob == null){
	    fields.add(null);
	    fields.add(null);
	}else{
	    fields.add(Integer.toString(dob.get(Calendar.DAY_OF_MONTH)));
	    fields.add(Integer.toString(dob.get(Calendar.MONTH)));
	}
	fields.add(person.getCob());
	return fields;
    }

    public static final List getFieldsForHashCode3(Person person) {
	List<String> fields = new Vector<String>(7);
	fields.add(person.getMgivenName());
	fields.add(person.getMsurname());
	fields.add(person.getFgivenName());
	fields.add(person.getFsurname());
	fields.add(person.getGivenNameAtBirth());
	Calendar dob = person.getDob();
	if(dob == null){
	    fields.add(null);
	}else{
	    fields.add(Integer.toString(dob.get(Calendar.YEAR)));
	}
	fields.add(person.getCob());
	return fields;
    }

    public static final List getFieldsForHashCode4(Person person) {
	List<String> fields = new Vector<String>(8);
	Calendar mdob = person.getMdob();
	if (mdob == null) {
	    fields.add(null);
	    fields.add(null);
	} else {
	    fields.add(Integer.toString(mdob.get(Calendar.DAY_OF_MONTH)));
	    fields.add(Integer.toString(mdob.get(Calendar.MONTH)));
	}
	Calendar fdob = person.getFdob();
	if(fdob == null){
	    fields.add(null);
	    fields.add(null);
	} else {
	    fields.add(Integer.toString(fdob.get(Calendar.DAY_OF_MONTH)));
	    fields.add(Integer.toString(fdob.get(Calendar.MONTH)));
	}
	fields.add(person.getGivenNameAtBirth());
	fields.add(person.getSurnameAtBirth());
	if(person.getGender() == Person.GENDER_UNKNOWN){
	    fields.add(null);
	}else{
	    fields.add(Integer.toString(person.getGender()));
	}
	fields.add(person.getCob());
	return fields;
    }

    public static final List getFieldsForHashCode5(Person person) {
	List<String> fields = new Vector<String>(6);
	fields.add(person.getGivenNameAtBirth());
	fields.add(person.getMiddleNameAtBirth());
	fields.add(person.getMgivenName());
	fields.add(person.getFgivenName());
	fields.add(person.getMsurname());
	Calendar dob = person.getDob();
	if(dob == null){
	    fields.add(null);
	}else{
	    fields.add(Integer.toString(dob.get(Calendar.MONTH)));
	}
	return fields;
    }

    /*
         * The field which is null means unknown, empty field means the subject
         * does not have it.
         */
    public static final int validate(List fieldList,
	    GuidWSStub.HashCodeMatchRule matchRule) {
	int numberOfMissing = countMissingField(fieldList);
	if (numberOfMissing <= matchRule.getLowerT()) {
	    return PERFECT_HASH_CODE;
	}
	if (numberOfMissing <= matchRule.getUpperT()) {
	    return GOOD_HASH_CODE;
	}
	return BAD_HASH_CODE;
    }

    public static final int countMissingField(List fieldList) {
	if (fieldList == null || fieldList.size() == 0) {
	    return 0;
	}

	int numberOfMissingField = 0;
	for (int i = 0; i < fieldList.size(); i++) {
	    String field = (String) fieldList.get(i);
	    if (field == null || field.length() == 0) {
		numberOfMissingField++;
	    }
	}
	return numberOfMissingField;
    }

    public static final String createDigestInput(List fieldList) {
	if (fieldList == null || fieldList.size() == 0) {
	    return null;
	}
	StringBuffer res = new StringBuffer("");
	for (int i = 0; i < fieldList.size(); i++) {
	    String field = (String) fieldList.get(i);
	    if (field == null || field.length() == 0) {
		field = UNKNOW_STRING;
	    } else {
		field = normalize(field);
	    }
	    if (i > 0) {
		res.append("_");
	    }
	    res.append(field);
	}

	return res.toString();
    }

    public static final String normalize(String s) {
	String localString = s.toUpperCase();
	localString = localString.replaceAll("[ |.|\\-|_]+", "");
	return localString;
    }

    public static void main(String[] argv) {
	Person person = new Person();
	person.setCob("Bethesd");
	Calendar dob = Calendar.getInstance();
	dob.set(2001, 02, 20);
	person.setFgivenName("Philips");
	person.setGivenNameAtBirth("Alexandra");
	person.setFgivenName("Smith");
	dob.set(1958, 04, 23);
	person.setFdob(dob);
	dob.set(1965, 8, 5);
	person.setFdob(dob);
	person.setGivenNameAtBirth("Smith");
	person.setMgivenName("Danna");
	person.setMiddleNameAtBirth("");
	person.setMsurname("White");
	person.setGender(0);
	person.setSsn("011-08-2222");

	SubjectManager manager = SubjectManager.getInstance();
	GuidWSStub.GuidRequest request = manager.createRequest(person);
	GuidWSClient client = GuidWSClient.getInstance();
	GuidWSStub.GuidResponse response = client.getGuid(request);
	System.out.println(response.getGuidResponse());
    }
}
