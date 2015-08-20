package cz.vutbr.wislab.symphony.dlna.push;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;

public class AVTransportHandler {

	/************ Methods ************/
	
	
	/* Play */
	public static void play(final Device device, UpnpService upnpService){
		//
		Service service = device.findService(new UDAServiceId("AVTransport"));
		//
		final Action action = service.getAction("Play");
		//
		ActionInvocation actionInvocation = new ActionInvocation (action);
		actionInvocation.setInput("InstanceID","0");
		actionInvocation.setInput("Speed","1");
		//
		ActionCallback actionCallback = new ActionCallback (actionInvocation){
			@Override
			public void success (ActionInvocation invocation){
				System.out.println("*"+device.getDisplayString()+": Playing.");
			}
									
			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation,	String defaultMsg) {
				System.err.println("*"+device.getDisplayString()+": "+defaultMsg);
				//System.out.println("err");
			}
		};
		//
		upnpService.getControlPoint().execute(actionCallback);
	}
	
	/* Stop */
	public static void stop(final Device device, UpnpService upnpService){
		//
		Service service = device.findService(new UDAServiceId("AVTransport"));
		//
		final Action action = service.getAction("Stop");
		//
		ActionInvocation actionInvocation = new ActionInvocation (action);
		actionInvocation.setInput("InstanceID","0");
		//
		ActionCallback actionCallback = new ActionCallback (actionInvocation){
			@Override
			public void success (ActionInvocation invocation){
				System.out.println("*"+device.getDisplayString()+": Stopped.");
			}
									
			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation,	String defaultMsg) {
				System.err.println("*"+device.getDisplayString()+": "+defaultMsg);
				//System.out.println("err");
			}
		};
		//
		upnpService.getControlPoint().execute(actionCallback);
	}
	
	/* Set Current URI */
	public static void setCurrentURI(final Device device, String URI, UpnpService upnpService){
		//
		Service service = device.findService(new UDAServiceId("AVTransport"));
		//
		final Action action = service.getAction("SetAVTransportURI");
		//
		ActionInvocation actionInvocation = new ActionInvocation (action);
		actionInvocation.setInput("InstanceID","0");
		actionInvocation.setInput("CurrentURI", URI);
		//
		ActionCallback actionCallback = new ActionCallback (actionInvocation){
			@Override
			public void success (ActionInvocation invocation){
				System.out.println("*"+device.getDisplayString()+": URI Setted.");
			}
									
			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation,	String defaultMsg) {
				System.err.println("*"+device.getDisplayString()+": "+defaultMsg);
				//System.out.println("err");
			}
		};
		//
		upnpService.getControlPoint().execute(actionCallback);
	}
	
				
	/* Get Transport Settings */
	public static void getTransportSettings(final Device device, UpnpService upnpService){
		//
		Service service = device.findService(new UDAServiceId("AVTransport"));
		//
		final Action action = service.getAction("GetTransportSettings");
		//
		ActionInvocation actionInvocation = new ActionInvocation (action);
		actionInvocation.setInput("InstanceID","0");
		//
		ActionCallback actionCallback = new ActionCallback (actionInvocation){
			@Override
			public void success (ActionInvocation invocation){
				System.out.println("AVT Settings:");
				System.out.println("Play mode: "+ invocation.getOutput("PlayMode"));
				System.out.println("RecQuality mode: "+ invocation.getOutput("RecQualityMode"));
			}
									
			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation,	String defaultMsg) {
				System.err.println("*"+device.getDisplayString()+": "+defaultMsg);
				//System.out.println("err");
			}
		};
		//
		upnpService.getControlPoint().execute(actionCallback);
	}
	
	/* Get Transport Info */
	public static void getTransportInfo(final Device device, UpnpService upnpService){
		//
		Service service = device.findService(new UDAServiceId("AVTransport"));
		//
		final Action action = service.getAction("GetTransportInfo");
		//
		ActionInvocation actionInvocation = new ActionInvocation (action);
		actionInvocation.setInput("InstanceID","0");
		//
		ActionCallback actionCallback = new ActionCallback (actionInvocation){
			@Override
			public void success (ActionInvocation invocation){
				System.out.println("AVT Info:");
				System.out.println("Current transport state: "+ invocation.getOutput("CurrentTransportState"));
				System.out.println("Current transport status: "+ invocation.getOutput("CurrentTransportStatus"));
				System.out.println("Current speed: "+ invocation.getOutput("CurrentSpeed"));
			}
									
			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation,	String defaultMsg) {
				System.err.println("*"+device.getDisplayString()+": "+defaultMsg);
				//System.out.println("err");
			}
		};
		//
		upnpService.getControlPoint().execute(actionCallback);
	}
	
	/* Get Media Info */
	public static void getMediaInfo(final Device device, UpnpService upnpService){
		//
		Service service = device.findService(new UDAServiceId("AVTransport"));
		//
		final Action action = service.getAction("GetMediaInfo");
		//
		ActionInvocation actionInvocation = new ActionInvocation (action);
		actionInvocation.setInput("InstanceID","0");
		//
		ActionCallback actionCallback = new ActionCallback (actionInvocation){
			@Override
			public void success (ActionInvocation invocation){
				System.out.println("Media Info:");
				System.out.println("Current URI: "+ invocation.getOutput("CurrentURI"));
			}
									
			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation,	String defaultMsg) {
				System.err.println("*"+device.getDisplayString()+": "+defaultMsg);
				//System.out.println("err");
			}
		};
		//
		upnpService.getControlPoint().execute(actionCallback);
	}
	
	/* Get Transport State  */
	public static void refreshPlayingState(final Device device, final StateRepresentative stateRepresentative, UpnpService upnpService){
		//
		Service service = device.findService(new UDAServiceId("AVTransport"));
		//
		final Action action = service.getAction("GetTransportInfo");
		//
		ActionInvocation actionInvocation = new ActionInvocation (action);
		actionInvocation.setInput("InstanceID","0");
		//
		ActionCallback actionCallback = new ActionCallback (actionInvocation){
			
			@Override
			public void success (ActionInvocation invocation){
				String state=invocation.getOutput("CurrentTransportState").toString();
				//
				System.out.println("Setting stateRep to " + state);
				
				//
				stateRepresentative.setState(state);
			}
												
			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation,	String defaultMsg) {
				System.err.println("*"+device.getDisplayString()+": "+defaultMsg);
				stateRepresentative.setState("PLAYING");
				//System.out.println("err");
			}
		};
		//
		upnpService.getControlPoint().execute(actionCallback);
		//
		//playingState=actionCallback.returnState();
			
	}
	
	
	/* Play slideshow */
	public static void playSlideshow (final Device device, StateRepresentative stateRepresentative, UpnpService upnpService, String [] uris, long pictureTimeGap) throws InterruptedException{
		long processGap = 400;
		long pictureGap = pictureTimeGap;
		//
		for (int i=0; i<uris.length; i++){
			//
			refreshPlayingState(device, stateRepresentative, upnpService);
			Thread.sleep(processGap);
			//
			//System.out.println("StateRep: "+ stateRepresentative.getState());
			//
			if (i==0){
				System.out.println("Slideshow: Playing img #" +i);
				stop (device, upnpService);
				Thread.sleep(processGap);
				setCurrentURI(device, uris[i], upnpService);
				Thread.sleep(processGap);
				play(device, upnpService);
				Thread.sleep(pictureGap);
			}
			else if (i>0 && stateRepresentative.getState().equals("PLAYING")){
				System.out.println("Slideshow: Playing img #" +i);
				stop (device, upnpService);
				Thread.sleep(processGap);
				setCurrentURI(device, uris[i], upnpService);
				Thread.sleep(processGap);
				play(device, upnpService);
				Thread.sleep(pictureGap);
				if (i==uris.length-1){
					stop (device, upnpService);
					Thread.sleep(processGap);
					stop (device, upnpService);
					Thread.sleep(processGap);
					System.out.println("Slideshow: Over.");
				}
			}
			else {
				System.out.println("Breaking...");
				break;
			}
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
