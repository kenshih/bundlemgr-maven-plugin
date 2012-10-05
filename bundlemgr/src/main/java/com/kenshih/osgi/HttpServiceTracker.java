package com.kenshih.osgi;

import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
 
/**
 * note: ServiceTracker is thread-safe
 * when overriding {@link #addingService(ServiceReference)}, 
 * care must be given to add a symmetric {@link #removedService(ServiceReference, Object)}
 * as was done below
 * 
 * @author kenshih
 *
 */
public class HttpServiceTracker extends ServiceTracker {
	
	
  final private BundleContext context;
	
  public HttpServiceTracker(BundleContext context) {
    super(context, HttpService.class.getName(), null);
    this.context = context;
  }
  
  /**
   * because this is supposed to be thread-safe my assumption was that
   * check-then-using httpService is ok.
   * this may be incorrect
   */
  public Object addingService(ServiceReference reference) {
	    HttpService httpService = (HttpService) super.addingService(reference);
	    if (httpService == null)
	      return null;
	 
	    try {
	      System.out.println("Registering servlet at /bundlemgr, /bundlemgr/update");
	      httpService.registerServlet("/bundlemgr", new BundleManagerServlet(context), null, null);
	      httpService.registerServlet("/bundlemgr/updater", new BundleManagerServlet(context), null, null);
	    } catch (NamespaceException e) {
	      e.printStackTrace();
	      httpService.unregister("/bundlemgr");
	      httpService.unregister("/bundlemgr/updater");
	    } catch (ServletException e) {
		  e.printStackTrace();
		  httpService.unregister("/bundlemgr");
	      httpService.unregister("/bundlemgr/updater");
		}
	 
	    return httpService;
  }
  
  public void removedService(ServiceReference reference, Object service) {
	    HttpService httpService = (HttpService) service;
	    System.out.println("Unregistering /bundlemgr/update, /snibndmgr");
	    httpService.unregister("/bundlemgr/updater");
	    httpService.unregister("/bundlemgr");
	    super.removedService(reference, service);
  }
}
