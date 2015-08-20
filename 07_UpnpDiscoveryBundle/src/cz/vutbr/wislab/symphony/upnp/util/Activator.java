package cz.vutbr.wislab.symphony.upnp.util;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.util.ArrayList;
import java.util.Hashtable;

public class Activator implements BundleActivator {
 	
	
	public static BundleContext bundleContext;
	
	public static EventAdmin admin;
	
	public static Event event;
	
	private EventAdminThread t;
	
	private ServiceRegistration registration;
	
	public static Discovery discoveryS = null;
 
		
	public void start(BundleContext context) throws Exception {
				
		Activator.bundleContext = context;
		System.out.println("{UPNP Discovery}:Starting activator.");
		
		discoveryS=new UpnpDiscovery();
		
		System.out.println("{UPNP Discovery}:Service registration.");
		registration = context.registerService(Discovery.class.getName(), discoveryS,  null);
				
		System.out.println("{UPNP Discovery}:EventAdmin.");
		ServiceReference<EventAdmin> ref = context.getServiceReference(EventAdmin.class);
		admin = context.getService(ref);
			
		
		System.out.println("{UPNP Discovery}:UpnpDiscovery service bundle successfully started.");
	}

  
	public void stop(BundleContext context) throws Exception {
		System.out.println("{UPNP Discovery}:Shutting down discovery bundle.");
		//discoveryS.shutdown();
		//
		Activator.bundleContext=null;
		
		//t.stopThread();
		//t.join();
	}
  
  
}