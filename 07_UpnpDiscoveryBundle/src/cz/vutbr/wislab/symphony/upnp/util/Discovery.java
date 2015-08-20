package cz.vutbr.wislab.symphony.upnp.util;

import java.util.ArrayList;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.registry.RegistryListener;

public interface Discovery {
		
	public abstract void start() throws InterruptedException;
		
	public abstract void shutdown();
		
	public abstract void discover() throws InterruptedException;
	
	public abstract void printDevices();
	
	public abstract ArrayList <String[]> getDevices();
	
	public abstract Device getDevice(String udn);
	
	public abstract UpnpService getService();
	
	
}