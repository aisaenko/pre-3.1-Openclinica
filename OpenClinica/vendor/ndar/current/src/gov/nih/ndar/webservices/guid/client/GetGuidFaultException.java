
/**
 * GetGuidFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.1 Nov 13, 2006 (07:31:44 LKT)
 */
package gov.nih.ndar.webservices.guid.client;

public class GetGuidFaultException extends java.lang.Exception{
    
    private gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidFault faultMessage;
    
    public GetGuidFaultException() {
        super("GetGuidFaultException");
    }
           
    public GetGuidFaultException(java.lang.String s) {
       super(s);
    }
    
    public GetGuidFaultException(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidFault msg){
       faultMessage = msg;
    }
    
    public gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidFault getFaultMessage(){
       return faultMessage;
    }
}
    