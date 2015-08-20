package WMBusBundle;

import java.util.HashMap;

import org.osgi.service.event.Event;

import wmbusmessages.WmbusPacket;

public final class GwMain implements Runnable {

	static private boolean bundleActive;
	
	// singleton
	GwMain()
	{
	}
	
	// sent data to other bundles
	static public void GwBundleEvent(WmbusPacket gwPacket) {
		//gwPacket.print();
		
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("WMBus-incomming-packet", gwPacket.getManufacturer());
		properties.put("SERIAL", gwPacket.getSerialNumber());
		if (gwPacket.getManufacturer().equals("WEPTECH")) {
			properties.put("TEMP", gwPacket.getPhenomenaList().get(0));
			properties.put("HUM", gwPacket.getPhenomenaList().get(1));
		}	
		if (gwPacket.getManufacturer().equals("BONEGA")) {
			properties.put("TYPE", gwPacket.getPhenomenaList().get(0));
			properties.put("VALUE", gwPacket.getPhenomenaList().get(1));
		}
		Activator.event = new Event("symphony/event", properties);
		Activator.admin.postEvent(Activator.event);
	}

	// receive settings from other bundles
	static public void GwBundleSet(GwSettings settings) {
		// Nothing to be set right now
	}
	
	// start gw bundle
	static public void GwBundleStart()
	{
		GwMain 	  gwMain 	   = new GwMain();
		Thread 	  threadGwMain = new Thread(gwMain);
		threadGwMain.start();
	}
	
	// stop gw bundle
	static public void GwBundleStop()
	{
		bundleActive=false;
	}

	@Override
	public void run() {
		UDPServer udpServer 	  = new UDPServer();
		Thread 	  threadUdpServer = new Thread(udpServer);
		threadUdpServer.start();
		
		bundleActive = true;
		
		while (true) 
		{
			if(!bundleActive)
				return;
			
			WmbusPacket gwPacket;
			gwPacket = udpServer.getGwPacket();

			if (gwPacket != null && gwPacket.isValid()) {
				gwPacket.processMeasuredData();
				GwBundleEvent(gwPacket);
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("GW Thread Cannot Sleep");
			}
		}
	}
	
	public static void main(String[] args) {
		GwMain.GwBundleStart();
	}
}
