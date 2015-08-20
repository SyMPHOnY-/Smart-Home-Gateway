package cz.vutbr.wislab.symphony.dlna.push;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;


public class UpnpServiceHandler {
	
	/************ Instances ************/
	
	/* Upnp Service Registry Listener */
	RegistryListener listener=new RegistryListener() {
				
		@Override
		public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
			//System.out.println("{UPNP Discovery}:Registry: Remote device updated: "+ device.getDisplayString());
		}
		
		@Override
		public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
			//System.out.println("{UPNP Discovery}:Registry: Remote device removed: "+ device.getDisplayString());
		}
		
		@Override
		public void remoteDeviceDiscoveryStarted(Registry registry,	RemoteDevice device) {
			//System.out.println("{UPNP Discovery}:Registry: Discovery process started: "+device.getDisplayString());
		}
		
		@Override
		public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
			//System.out.println("{UPNP Discovery}:Registry: Discovery process failed: "+device.getDisplayString());
		}
		
		@Override
		public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
			//System.out.println("{UPNP Discovery}:Registry: Remote device added "+ device.getDisplayString() +
			//		" UDN: " + device.getIdentity().getUdn());
			
					
		
		}
		
		@Override
		public void localDeviceRemoved(Registry registry, LocalDevice device) {
			//System.out.println("{UPNP Discovery}:Registry: Local device removed: "+ device.getDisplayString());
		}
		
		@Override
		public void localDeviceAdded(Registry registry, LocalDevice device) {
			//System.out.println("{UPNP Discovery}:Registry: Local device added: "+ device.getDisplayString());
			
		}
		
		@Override
		public void beforeShutdown(Registry registry) {
			//System.out.println("{UPNP Discovery}:Registry: Shutting down... ");
			//System.out.println(registry.getDevices().size()+" devices in list.");
		}
		
		@Override
		public void afterShutdown() {
			//System.out.println("{UPNP Discovery}:Registry: Shutted down.");
			
		}
	};
	
	
	/* Upnp Service  */
	UpnpService upnpService;
	
	
	/* Device ArrayList */
	ArrayList <Device> deviceArrayList;
	
	
	/************ Methods ************/
	
	/* Start Upnp Service */
	public void start (){
		System.out.println ("{DLNA UPNP}:Creating Apache configuration...");
		ApacheServiceConfiguration config=new ApacheServiceConfiguration();
		//		
		System.out.println ("{DLNA UPNP}:Starting UPNP service...");
		upnpService = new UpnpServiceImpl(config,listener);
	}
	
	/* Shut down Upnp Service  */
	public void shutdown (){
		System.out.println ("{UPNP Discovery}:Shutting down...");
		//
		upnpService.shutdown();
	}
	
	/* Device Discovery */
	public void discover (int waitingTime) throws InterruptedException{
		for (int i=0; i<2;i=2){	
			System.out.println("{UPNP Discovery}:Searching for devices...");
			upnpService.getControlPoint().search();
			//
			Thread.sleep(waitingTime*1000);
			//
			refreshDeviceList();
			System.out.println("{UPNP Discovery}:Searching over. " + deviceArrayList.size()+" devices found.");
		}
		//printDeviceList();
		//
		
	}
	
	/* Refresh device list */
	private void refreshDeviceList (){
		deviceArrayList= new ArrayList <Device> (upnpService.getRegistry().getDevices());
	}
	
	/* Print device list */
	public void printDeviceList (){
		refreshDeviceList();
		//
		System.out.println("{UPNP Discovery}:Device list:");
		//System.out.println(deviceArrayList.size() + " devices:");
		//
		for (int i=0; i<deviceArrayList.size(); i++){
			System.out.println("#"+i+":");
			System.out.println("Device: "+ deviceArrayList.get(i).getDisplayString());
			System.out.println("Type: "+ deviceArrayList.get(i).getType().getDisplayString());
			System.out.println("UDN: "+ deviceArrayList.get(i).getIdentity().getUdn().getIdentifierString());
			//
			if (deviceArrayList.get(i).getServices().length!=0){
				System.out.println("Services available:");
				for (int j=0; j<deviceArrayList.get(i).getServices().length; j++){
					System.out.println("\t  "+deviceArrayList.get(i).getServices()[j].getServiceType().getType()+" ");
				}
			}
			else{
				System.out.println("Services available: none");
			}
		}
		System.out.println();
		//	
	}
	
	/* Return device list */
	public ArrayList <Device> getDeviceList(){
		refreshDeviceList();
		//
		return deviceArrayList;
	}
	
	/* Return UPNP Service */
	public UpnpService getUpnpService (){
		return upnpService;
	}
	
	/* Play slideshow */
	/*private void playSlideshow (Device device) throws InterruptedException{
		String [] uris = new String [6];
		uris[0]="http://192.168.188.1:49200/IMAGE/DLNA-17-0/dlna/1WELCOME.jpg";
		uris[1]="http://192.168.188.1:49200/IMAGE/DLNA-17-0/dlna/2HUMIDITY.jpg";
		uris[2]="http://192.168.188.1:49200/IMAGE/DLNA-17-0/dlna/3ENERGY.jpg";
		uris[3]="http://192.168.188.1:49200/IMAGE/DLNA-17-0/dlna/4BALANCE.jpg";
		uris[4]="http://192.168.188.1:49200/IMAGE/DLNA-17-0/dlna/5COLD.jpg";
		uris[5]="http://192.168.188.1:49200/IMAGE/DLNA-17-0/dlna/6HOT.jpg";
		
		String [] uris1 = new String [7];
		uris1[0]="http://10.0.0.38:52187/MediaServerContent_0/1/0000000000000006/obr%20-%200.jpg";
		uris1[1]="http://10.0.0.38:52187/MediaServerContent_0/3/0000000000000007/obr%20-%201.jpg";
		uris1[2]="http://10.0.0.38:52187/MediaServerContent_0/5/0000000000000008/obr%20-%202.jpg";
		uris1[3]="http://10.0.0.38:52187/MediaServerContent_0/7/0000000000000009/obr%20-%203.jpg";
		uris1[4]="http://10.0.0.38:52187/MediaServerContent_0/9/000000000000000A/obr%20-%204.jpg";
		uris1[5]="http://10.0.0.38:52187/MediaServerContent_0/11/000000000000000B/obr%20-%205.jpg";
		uris1[6]="http://10.0.0.38:52187/MediaServerContent_0/13/000000000000000C/obr%20-%206.jpg";
		
		StateRepresentative stateRep = new StateRepresentative();
		
		AVTransportHandler.playSlideshow(device, stateRep , upnpService, uris, 7000);
	}
	*/
	
	public Device getDevice(UDN udn){
		
		Device dev = upnpService.getRegistry().getDevice(udn, true);
		
		return dev;
	}
	
}



















