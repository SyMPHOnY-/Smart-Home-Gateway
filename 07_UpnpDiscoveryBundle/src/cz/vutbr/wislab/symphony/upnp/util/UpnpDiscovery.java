package cz.vutbr.wislab.symphony.upnp.util;

import java.util.ArrayList;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import cz.vutbr.wislab.symphony.upnp.discovery.UpnpServiceHandler;

public class UpnpDiscovery implements Discovery {
	
	UpnpServiceHandler upnp;
	
	@Override
	public void start() throws InterruptedException {
			
		
		upnp = new UpnpServiceHandler();
		//
		upnp.start();
		//
		Thread.sleep(1000);
	}

	@Override
	public ArrayList<String[]> getDevices() {
		return  new ArrayList<String[]> (upnp.getDeviceList());
	}

	@Override
	public UpnpService getService() {
		return upnp.getUpnpService();
	}

	@Override
	public void shutdown() {
		upnp.shutdown();
		
	}

	@Override
	public void discover() throws InterruptedException {
		upnp.discover(10);
		
	}

	@Override
	public void printDevices() {
		upnp.printDeviceList();
		
	}

	@Override
	public Device getDevice(String udnString) {
		UDN udn=new UDN(udnString);
		
		Device dev = upnp.getDevice(udn);
		
		return dev;
	}

	
	
	
}