package gov.nih.ndar.webservices.guid.client;

public class TooFewInformationException extends Exception {
    public TooFewInformationException(String s){
        super(s);
    }
    
    public TooFewInformationException(String s, Throwable t){
        super(s, t);
    }
}
