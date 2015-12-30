package gov.nih.ndar.webservices.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.apache.ws.security.WSPasswordCallback;

public class PWCBHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            
            //When the server side need to authenticate the user
            WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];

            String id = pwcb.getIdentifer();
            String alias = SQLInitServlet.getPrivateKeyAlias();
            if(alias != null && alias.equals(id)) {
                pwcb.setPassword(SQLInitServlet.getPrivateKeyPassword());
            } 
        }
    }
    
}
