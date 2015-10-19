package cz.vutbr.wislab.symphony.sip.client;

public interface SipManagerInterface {
	public void sendMessage(String to, String message) throws NotInitializedException;

}
