package cz.vutbr.wislab.symphony.sip.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


import cz.vutbr.wislab.symphony.sip.client.SipManager;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] registerField = new String[] {"LocalIP", "LocalPort", "udp", "ProxyIP", "ProxyPort", "RegistratIP", "sipUsername", "SipPassword" };


		SipManager.localIp = registerField[0];
		SipManager.localPort = Integer.valueOf(registerField[1]);
		SipManager.transport = registerField[2];
		SipManager.proxyIp = registerField[3];
		SipManager.proxyPort = Integer.valueOf(registerField[4]);
		SipManager.registrarIp = registerField[5];;
		SipManager.sipUserName = registerField[6];
		SipManager.sipPassword = registerField[7];
		
		SipManager sipManager = new SipManager();
		sipManager.register();	
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*try {
			sipManager.sendMessage("sip:tag.smartcenter@a1.net","test ze Smarthome");
		} catch (NotInitializedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	
	}
	
	
	private static String getLocalIP () { 
	 	String localIP = null;
		NetworkInterface ni = null;
		try {
			ni = NetworkInterface.getByName("eth0");
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Local IP address can not be resolved");
		}

		Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
		while (inetAddresses.hasMoreElements()) {
			InetAddress ia = inetAddresses.nextElement();
			if (!ia.isLinkLocalAddress()) {
				localIP = ia.getHostAddress();
				System.out.println("Local IP address is: " + localIP);// this is not loopback
			}
		}
		return localIP;		
	}

}
