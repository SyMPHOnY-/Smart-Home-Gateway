package cz.vutbr.wislab.symphony.sip.util;

public interface SipInterface {
	/**
	 * 
	 * @param localIP IP address of local device
	 * @param localPort port of local device (usually 5060)
	 * @param transportProtocol udp/tcp protocol - usually udp is using
	 * @param proxyIp IP address of SIP Proxy server 
	 * @param proxyPort port of Sip Proxy server (usually 5060)
	 * @param registrarIp IP address of SIP Registrar server
	 * @param sipUserName username to SIP account without address (in case tag.smarthome@a1.net use only tag.smarthome)
	 * @param sipPassword password to SIP aacount
	 */
	public void setProfile (String[] profile);	
	
	/**
	 * 
	 * Send SIP MESSAGE to defined address 
	 * @param toAddress address of MESSAGE receiver
	 * @param message MESSAGE to send
	 */
	public void sendMessage(String toAddress, String message);
	
	/**
	 * Register to SIP registrar
	 */
	
	public void register();
	
	/**
	 * Deregister from SIP refistrar
	 */
	public void deregister();
	
	
}
