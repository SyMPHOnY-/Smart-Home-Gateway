/*
 * Created on Thu Apr 16 17:25:03 CEST 2015
 */
package Core;

import java.util.Dictionary;
import java.util.Hashtable;

import org.knopflerfish.service.log.LogConfig;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;


public class Activator implements BundleActivator {
  
	public static BundleContext bc;	
	InitHandler inicialization = new InitHandler();
	
	public void start(BundleContext context) throws Exception {
		System.out.println("{Core Bundle}:Running!...");		
		Activator.bc = context;
		
		//ServiceReference ref = context.getServiceReference(LogConfig.class.getName());        
		//LogConfig conf = (LogConfig) context.getService(ref);				
	    //conf.setFile(true);
	    //conf.setOut(true);
	    
		//method which starts SyMPHOnY Bundles
		//startBundles(bc.getBundles());
		
		//start miniDLNA Server on NEC
		//inicialization.startMiniDLNA("minidlna -f /tiny/minidlna.conf");
		
		//set NTP pool for NTP Client on NEC
		//inicialization.setDate("0.openwrt.pool.ntp.org");
			
		System.out.println("{Core Bundle}:Registering events...");
		EventHandler eventHandler = new CoreEventHandler(context);
		Dictionary<String, String> d = new Hashtable<String, String>();
		d.put(EventConstants.EVENT_TOPIC, "symphony/event");
		Activator.bc.registerService( EventHandler.class, eventHandler, d);
	}
	

	
	public void stop(BundleContext context) throws Exception {
		System.out.println("{Core Bundle}:Stopping!...");	
		
		//stopBundles(bc.getBundles());		
		bc = null;
	}

	
	private void startBundles(Bundle[] bs) throws BundleException {
		// TODO Auto-generated method stub
				
		String name;	    
		for (Bundle bundle : bs) {
			name = bundle.getSymbolicName();
			
			if (name.equals("SipClient")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.start();
				}			
			}
			if (name.equals("SQLite")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.start();
				}			
			}
			if (name.equals("WMBusBundle")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.start();
				}			
			}
			if (name.equals("UpnpDiscoveryBundle")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.start();
				}			
			}
			if (name.equals("DLNAPushBundle")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.start();
				}			
			}
			if (name.equals("TVScreenGenerator")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.start();
				}			
			}
		}
	}
	
	private void stopBundles(Bundle[] bs) throws BundleException {
		// TODO Auto-generated method stub
		String name;		
		for (Bundle bundle : bs) {
			name = bundle.getSymbolicName();
			
			if (name.equals("SipClient")) {
				if (bundle.getState() == 32) {
					System.out.println("Stoping bundle :" + name);
					bundle.stop();	
					bundle.update();
				}			
			}
			if (name.equals("SQLite")) {
				if (bundle.getState() == 32) {
					System.out.println("Stoping bundle :" + name);
					bundle.stop();	
					bundle.update();
				}	
			}
			if (name.equals("WMBusBundle")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.stop();	
					bundle.update();
				}			
			}
			if (name.equals("UpnpDiscoveryBundle")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.stop();	
					bundle.update();
				}			
			}
			if (name.equals("DLNAPushBundle")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.stop();	
					bundle.update();
				}			
			}
			if (name.equals("TVScreenGenerator")) {
				if (bundle.getState() != 32) {
					System.out.println("Starting bundle :" + name);
					bundle.stop();	
					bundle.update();
				}			
			}
		}
	}
}