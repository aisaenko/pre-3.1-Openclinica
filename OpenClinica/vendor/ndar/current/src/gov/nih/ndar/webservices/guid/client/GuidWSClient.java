package gov.nih.ndar.webservices.guid.client;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;

public class GuidWSClient {
    private GuidWSStub stub;
    private Logger logger;
    private static GuidWSClient client;
    
    private GuidWSClient(){
        logger = Logger.getLogger(getClass().getName());
        try {
            ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(SQLInitServlet.getRepositoryLocation(), null);
            stub = new GuidWSStub(ctx, SQLInitServlet.getGuidWSURL(), SQLInitServlet.getPolicyLocation());
        } catch (Exception e) {
            logger.warning("Exception happened when initializing GuidWSStub object: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
    
    public static GuidWSClient getInstance(){
        if(client == null){
            client = new GuidWSClient();
        }
        
        return client;
    }
    
    public GuidWSStub.GuidResponse getGuid(GuidWSStub.GuidRequest request){
        if(stub != null){
            try{
                return stub.getGuid(request);
            }catch(GetGuidFaultException e){
                logger.warning("GetGuidFaultException happend when invoking getGuid(): " + e.getMessage());
                e.printStackTrace();
                return null;
            }catch(RemoteException e){
                logger.warning("RemoteException happend when invoking getGuid(): " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    public GuidWSStub.GuidWSConfigResponse getGuidWSConfig(){
        if(stub != null){
           try{
                return stub.getGuidWSConfig();
            }catch(GetGuidFaultException e){
                logger.warning("GetGuidFaultException happend when invoking getGuid(): " + e.getMessage());
                e.printStackTrace();
                return null;
            }catch(RemoteException e){
                logger.warning("RemoteException happend when invoking getGuid(): " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    public static void main(String[] argv){
	GuidWSClient client = new GuidWSClient();
    }
}
