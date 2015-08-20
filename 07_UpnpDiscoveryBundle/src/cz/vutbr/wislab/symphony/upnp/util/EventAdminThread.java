package cz.vutbr.wislab.symphony.upnp.util;

	import java.util.HashMap;

	import org.osgi.service.event.Event;

	public class EventAdminThread extends Thread{

		private boolean running = true;
		
		public EventAdminThread()
		{
			super("EventAdminThread thread");
		}
		
		public void run()
		{
			HashMap<String, String> properties = new HashMap<String, String>();
			while (running)
			{
				properties.put("SymphonyEvent", "Obsah eventu");
				Activator.event = new Event("symphony/event", properties);
				Activator.admin.postEvent(Activator.event);		
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void stopThread()
		{
			running = false;
			System.out.println("Vlakno zastaveno");
		}
	}  