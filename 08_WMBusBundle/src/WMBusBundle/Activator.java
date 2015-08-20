/*
 * Created on Sat Apr 18 10:23:32 CEST 2015
 */
package WMBusBundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
//import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

//import wmbusmessages.WmbusPacket;

public class Activator implements BundleActivator {
	  /* (non-Javadoc)
	   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	   */
	//public static BundleContext bundleContext;
	public static EventAdmin admin;
	public static Event event;
	
	public void start(BundleContext context) throws Exception {
		//Activator.bundleContext = context;
		//ServiceRegistration registration = context.registerService(GwMain.class.getName(),
		//			new GwMain(), null);
		ServiceReference<EventAdmin> ref = context.getServiceReference(EventAdmin.class);
		admin = context.getService(ref);

	
		GwMain.GwBundleStart();
		System.out.println("Starting WMBus Bundle");
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {	
		GwMain.GwBundleStop();
		System.out.println("Stoping WMBus Bundle");
	}  
}