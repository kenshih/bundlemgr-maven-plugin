package com.kenshih.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
 
/**
 * For troubleshooting purposes, please note:
 * BundleContext is currently passed to HttpServiceTracker and BundleManagerServlet
 * 
 * @author kenshih
 *
 */
public class Activator implements BundleActivator {
 
  private HttpServiceTracker serviceTracker;
 
  public void start(BundleContext context) throws Exception {
	serviceTracker = new HttpServiceTracker(context);
    serviceTracker.open();
  }
 
  public void stop(BundleContext context) throws Exception {
    serviceTracker.close();
    serviceTracker = null;
  }
 
}