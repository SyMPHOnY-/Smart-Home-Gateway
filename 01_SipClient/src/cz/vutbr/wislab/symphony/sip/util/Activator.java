/*
 * Created on Wed Jan 28 21:23:17 CET 2015
 */
package cz.vutbr.wislab.symphony.sip.util;

import java.util.HashMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import cz.vutbr.wislab.symphony.sip.client.SipManager;

public class Activator implements BundleActivator {

	public static BundleContext bc;
	public static EventAdmin admin;
	public static Event event;
	public static ServiceReference serviceReference;

	private Manager manager;
	private ServiceRegistration registration;
	
	public static SipManager sipManager = null;


	// SipManager sipManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		bc = context;
		System.out.println("Activator SIP is running");
		//registration of SipInterface services (methods)
		registration = bc.registerService(SipInterface.class.getName(),
				new Manager(), null);
		//Events listener registration
		ServiceReference<EventAdmin> ref = context.getServiceReference(EventAdmin.class);
		admin = context.getService(ref);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {		
		Activator.sipManager.deregister();		
		bc = null;

	}
}