/*
 * Created on Tue Apr 07 13:23:39 CEST 2015
 */
package cz.vutbr.wislab.symphony.TVScreenGenerator.util;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {	
	public static BundleContext bc;
	private ServiceRegistration registration;
	
	
  /* (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {	  
	  bc = context;
		System.out.println("TVScreenGenerator: Activator is running");
		registration = bc.registerService(SlideGenerator.class.getName(),
				new Manager(), null);	  
  }
//update
  /* (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
		bc = null;
  }
}