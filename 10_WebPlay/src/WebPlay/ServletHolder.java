package WebPlay;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class ServletHolder extends ServiceTracker<HttpService, HttpService>{
	
	public ServletHolder(BundleContext context) {
		super(context, HttpService.class.getName(), null);
	}

	public HttpService addingService(ServiceReference<HttpService> reference)
	{
		HttpService httpService = context.getService(reference);
		try {
			httpService.registerServlet("/dlna",new ButtonServlet(), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 return httpService;
	}
	
	public void removedService(ServiceReference<HttpService> reference, HttpService service)
	{
		HttpService httpService = (HttpService) service;
		httpService.unregister("/dlna");
		super.removedService(reference, service);
	}

}
