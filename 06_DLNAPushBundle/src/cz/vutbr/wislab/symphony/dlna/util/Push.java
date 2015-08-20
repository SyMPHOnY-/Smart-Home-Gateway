package cz.vutbr.wislab.symphony.dlna.util;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.Device;

public interface Push {
	
	
	public abstract void pushPicture (String udnString, String uri, long pictureTimeGap) throws InterruptedException ;
	
	public abstract void playSlideshow (String udnString,  String [] uris, long pictureTimeGap) throws InterruptedException;
	
	public abstract void pingOnDevice (String udnString) throws InterruptedException ;

}
