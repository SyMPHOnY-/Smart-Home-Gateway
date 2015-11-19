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
		
		
		Activator.bc.registerService(SQLiteservice.class, sqlite, null);
		
		//sqlite.storeData("electricity_prod", "1234", "480", "Prod");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.bc.ungetService(Activator.bc.getServiceReference(SQLiteservice.class.getName()));
		DBConnection.closeDB();
		Activator.bc = null;
		System.out.println("SQLite service stopped");
	}
}
