package org.akaza.openclinica.dao.subject;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Logger;

public class NDARIDManager {
    public final int NUM_BYTES_OF_SEED = 20;
    private String prefix = "";
    private int length;
    private Random ran;
    private Logger logger;
    private static NDARIDManager manager;
    private NDARIDManager(){
	logger = Logger.getLogger(NDARIDManager.class.getName());
	prefix = "ndar_";
	length = 8;
	try{
	    ran = SecureRandom.getInstance("SHA1PRNG");
	}catch(NoSuchAlgorithmException nsae){
	    logger.severe(nsae.getMessage());
	}
	((SecureRandom)ran).setSeed(SecureRandom.getSeed(NUM_BYTES_OF_SEED));
    }
    
    public static NDARIDManager getInstance(){
	if(manager == null){
	    manager = new NDARIDManager();
	}
	return manager;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public String getNDARId(){
	StringBuffer ret = new StringBuffer(prefix);
	for(int i = 0; i < length; i++){
	    ret.append(ran.nextInt(10));
	}
	return ret.toString();
    }
}
