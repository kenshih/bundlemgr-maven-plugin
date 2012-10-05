package com.kenshih.osgi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A servlet that says hello world, basically
 */
@Component(metatype = false, immediate = true)
public class ExampleTrivialBundle  extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ExampleTrivialBundle.class);
	private static final String SERVLET_URL = "/hack/example";
	
	@SuppressWarnings("unused")
	@Reference(bind="setPage",unbind="unsetPage")
	private HttpService httpService;
	
	@Override
	 protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		response.getOutputStream().println("<html><body><h1>Happy Hack Day!</h1></body></html>");
	}
	
	
	protected void setPage(HttpService http) {
	try {
			http.registerServlet(SERVLET_URL, this, null, null );
			log.info("REGISTERED "+SERVLET_URL);
		} catch (ServletException e) {
			log.error("error registering servlet "+SERVLET_URL,e);
		} catch (NamespaceException e) {
		log.error("error registering servlet "+SERVLET_URL,e);
		}
	}
	 
	protected void unsetPage(HttpService http) {
		 http.unregister(SERVLET_URL);
		 log.info("UNREGISTERED "+SERVLET_URL);
	}
}
