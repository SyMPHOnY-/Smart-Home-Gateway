package cz.vutbr.wislab.symphony.sip.util;

import cz.vutbr.wislab.symphony.sip.client.NotInitializedException;
import cz.vutbr.wislab.symphony.sip.client.SipManager;


public class Manager implements SipInterface {
	
		
	@Override
	public void setProfile(String[] registerField) {
		// TODO Auto-generated method stub
		System.out.println("SIP profile is created");		
		SipManager.localIp = registerField[0];
		SipManager.localPort = Integer.valueOf(registerField[1]);
		SipManager.transport = registerField[2];
		SipManager.proxyIp = registerField[3];
		SipManager.proxyPort = Integer.valueOf(registerField[4]);
		SipManager.registrarIp = registerField[5];;
		SipManager.sipUserName = registerField[6];
		SipManager.sipPassword = registerField[7];
		
		Activator.sipManager = new SipManager();
		
		
	}

	@Override
	public void sendMessage(String toAddress, String message) {
		// TODO Auto-generated method stub
		try {
			Activator.sipManager.sendMessage(toAddress, message);
		} catch (NotInitializedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void register() {
		Activator.sipManager.register();
		// TODO Auto-generated method stub		
	}

	@Override
	public void deregister() {
		// TODO Auto-generated method stub
		Activator.sipManager.deregister();
		
		
	}	
}
