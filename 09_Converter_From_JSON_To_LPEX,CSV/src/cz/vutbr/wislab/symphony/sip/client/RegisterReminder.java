package cz.vutbr.wislab.symphony.sip.client;

import java.util.TimerTask;

public class RegisterReminder extends TimerTask{
	SipManager sipManager = null;
	
	
	public RegisterReminder(SipManager sipManager) {
		// TODO Auto-generated constructor stub
		this.sipManager=sipManager;
				
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		sipManager.register();
		System.out.println("Register repetition");	
	}
}
