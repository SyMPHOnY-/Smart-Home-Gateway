package WebPlay;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;



public class ButtonServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		resp.getOutputStream().println("<style type=\"text/css\">body{background-color: black;}input#play{width: 100%;height: 100%; font-size: 100px; color: white; background-color: black; border: none;}</style><form method=\"post\"><input type=\"submit\" id=\"play\" value=\"Play\"></form>");
	  }
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setContentType("text/html");
			HashMap<String, String> properties = new HashMap<String, String>();
			/*properties.put("UpnpEvent", "Device_Removed");
			properties.put("UDN", "19a14780-00ae-1000-a3f9-4844f761ff04");
			properties.put("Name", "Samsung Electronics UE46ES8080 1.0");
			properties.put("Type", "MediaRenderer");
			Activator.event = new Event("symphony/event", properties);
			//System.out.println("Posting event");
			Activator.admin.postEvent(Activator.event);*/
			
			//properties.clear();
			
			properties.put("UpnpEvent", "Device_Added");
			properties.put("UDN", "2c3f5fdc-1dd2-11b2-98ae-000982195dd8");
			properties.put("Name", "Samsung Electronics UE46ES8080 1.0");
			properties.put("Type", "MediaRenderer");
			Activator.event = new Event("symphony/event", properties);
			//System.out.println("Posting event");
			Activator.admin.postEvent(Activator.event);
			resp.getOutputStream().println("<style type=\"text/css\">body{background-color: black;}input#play{width: 100%;height: 100%; font-size: 100px; color: white; background-color: black; border: none;}</style><form method=\"post\"><input type=\"submit\" id=\"play\" value=\"Play\"></form>");

		  }

}
