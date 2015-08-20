package cz.vutbr.wislab.symphony.sip.clientAAA;

import gov.nist.javax.sip.clientauthutils.AccountManager;
import gov.nist.javax.sip.clientauthutils.UserCredentials;

import javax.sip.ClientTransaction;

public class AccountManagerImpl implements AccountManager {
	
	public static String username;
	public static String password;
	public static String registrarIp;
    
	public AccountManagerImpl() {
		
	}
	
    public UserCredentials getCredentials(ClientTransaction challengedTransaction, String realm) {
       return new UserCredentialsImpl(username,registrarIp,password);
    }

}