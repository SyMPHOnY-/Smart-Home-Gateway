package cz.vutbr.wislab.symphony.dlna.util;

import org.fourthline.cling.UpnpService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import cz.vutbr.wislab.symphony.dlna.push.UpnpServiceHandler;

import java.util.Hashtable; 



public class Activator implements BundleActivator {
 	
	
	public static BundleContext bundleContext;
	
	
 	
	public void start(BundleContext context) throws Exception {
		System.out.println("{DLNA Push}:Starting bundle.");
		Activator.bundleContext = context;
		//
		System.out.println("{DLNA Push}:Setting upnp.");
		UpnpServiceHandler upnpHandler = new UpnpServiceHandler();
		upnpHandler.start();
		
		
		Push dlnaPush = new DLNAPush(upnpHandler.getUpnpService());
		//
		context.registerService(Push.class.getName(), dlnaPush, null);
		System.out.println("{DLNA Push}:Service is running.");
	}

  
	public void stop(BundleContext context) throws Exception {
		System.out.println("{DLNA Push}: Shutting down.");
		//
		Activator.bundleContext=null;
	}
  
  
}