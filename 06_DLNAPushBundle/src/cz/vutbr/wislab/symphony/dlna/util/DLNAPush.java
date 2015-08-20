package cz.vutbr.wislab.symphony.dlna.util;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.UDN;

import cz.vutbr.wislab.symphony.dlna.push.AVTransportHandler;
import cz.vutbr.wislab.symphony.dlna.push.StateRepresentative;


public class DLNAPush implements Push{
	
	private UpnpService upnpService;
	
	/* Parametric constructor */
	public DLNAPush (UpnpService upnpService){
		this.upnpService=upnpService;
	}

	
	@Override
	public void pushPicture(String udnString, String uri, long pictureTimeGap) throws InterruptedException {
		
		UDN udn=new UDN(udnString);
		
		Device device=upnpService.getControlPoint().getRegistry().getDevice(udn, true);
		Thread.sleep(1000);
		AVTransportHandler.stop(device, upnpService);
		Thread.sleep(1000);
		AVTransportHandler.setCurrentURI(device, uri, upnpService);
		Thread.sleep(1000);
		AVTransportHandler.play(device, upnpService);
		Thread.sleep(pictureTimeGap);
		AVTransportHandler.stop(device, upnpService);
	}

	@Override
	public void playSlideshow(String udnString, String[] uris, long pictureTimeGap) throws InterruptedException {
		
		
		UDN udn=new UDN(udnString);
		
		Device device=upnpService.getControlPoint().getRegistry().getDevice(udn, true);
		
		StateRepresentative stateRep = new StateRepresentative();
		
		AVTransportHandler.playSlideshow(device, stateRep, upnpService, uris, pictureTimeGap);
		
	}


	@Override
	public void pingOnDevice(String udnString) throws InterruptedException {
		UDN udn=new UDN(udnString);
		
		System.out.println("Searching for " + udnString);
		upnpService.getControlPoint().search(new UDNHeader(new UDN(udnString)));
		Thread.sleep(2000);
		
		
		Device device=upnpService.getControlPoint().getRegistry().getDevice(udn, true);
		
		if (device!=null)System.out.println("Device found: " + device.getDisplayString());
		else System.out.println("Device not found.");
	}}
