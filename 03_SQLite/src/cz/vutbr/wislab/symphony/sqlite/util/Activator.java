package cz.vutbr.wislab.symphony.sqlite.util;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import cz.vutbr.wislab.symphony.sqlite.SQLiteservice;



public class Activator implements BundleActivator{

	public static BundleContext bc;
	
	@Override
	public void start(BundleContext context) throws Exception {
		Activator.bc = context;
		System.out.println("SQLite service running");
		SQLiteservice sqlite = new SQLiteImplement();
		
		//sqlite.setDatabazeFile("/home/student/Desktop/SQlite/symphony");  
		//sqlite.setDatabazeFile("/tiny/symphony");
		Activator.bc.registerService(SQLiteservice.class, sqlite, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.bc.ungetService(Activator.bc.getServiceReference(SQLiteservice.class.getName()));
		Activator.bc = null;
		System.out.println("SQLite service stopped");
	}
}
