package cz.vutbr.wislab.symphony.upnp.util;

import java.util.EventObject;

import org.fourthline.cling.model.meta.Device;

public class UpnpEvent extends EventObject {
	
	public String content;
	public UpnpEventType type;
	public Device device;

	private static final long serialVersionUID = 1L;
	
	public enum UpnpEventType {
		device_added, device_removed
	}
	
	
	
	public UpnpEvent(Object source, UpnpEventType type, String content, Device device) {
		super(source);
		
		this.type=type;
		this.content=content;
		this.device=device;
	}
	

	
	
}
