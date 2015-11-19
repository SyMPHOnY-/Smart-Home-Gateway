/*
 * Created on Tue Nov 03 20:21:00 CET 2015
 */
package WebPlay;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	public static BundleContext bc = null;
	public static EventAdmin admin;
	public static Event event;
  
	private static ServiceTracker<HttpService, HttpService> httpTracker;
	
  public void start(BundleContext context) throws Exception {
	  Activator.bc = context;
	  Activator.httpTracker = new ServletHolder(context);
	  httpTracker.open();
	  ServiceReference<EventAdmin> ref = context.getServiceReference(EventAdmin.class);
	  admin = context.getService(ref);
	  System.out.println("Bundle started.");
  }

  
  public void stop(BundleContext context) throws Exception {
	  httpTracker.close();
	  System.out.println("Bundle stopped.");
  }
}