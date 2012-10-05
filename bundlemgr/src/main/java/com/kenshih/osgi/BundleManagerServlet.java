package com.kenshih.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Httpservice that wraps ModifiedManifestService in order to handle 
 * all the lifecycle events having to do with osgi bind/unbind etc
 * 
 * @author Ken Shih
 */

public class BundleManagerServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(BundleManagerServlet.class);
	private static final long serialVersionUID = 1L;

	//DO NOT LEAK THIS REFRENCE OUT 
	final private BundleContext context;
	 
	// consider this... will the context ever change? for example, during a set/unset event?
	// my assumption is not (seems like that would be crazy-talk)
	// ignorance guides me, i need to review the spec on DS
	protected BundleManagerServlet(final BundleContext context){
		this.context=context;
	}
	
	/**
	 * found this cool way of reading an is here: http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
	 * it always pays to do some code-by-google
	 * 
	 * this is for debugging only
	 */
	private String convertStreamToString(final InputStream is) {
	    try {
	        return new java.util.Scanner(is).useDelimiter("\\A").next();
	    } catch (java.util.NoSuchElementException e) {
	        return "{'error':'parsed nothin'}";
	    }
	}
	
	private String convertStreamToString2(final InputStream is) {
		final char[] buffer = new char[0x10000];
    	Reader in = null;
    	StringBuilder out = new StringBuilder();
    	try {    		
	    	in = new InputStreamReader(is, "UTF-8");
	    	try {
	    	  int read;
	    	  do {
	    	    read = in.read(buffer, 0, buffer.length);
	    	    if (read>0) {
	    	      out.append(buffer, 0, read);
	    	    }
	    	  } while (read>=0);
	    	} finally {
	    	  in.close();
	    	}
	    } catch (NoSuchElementException e) {
	        return "{'error':'parsed nothin2'}";
	    } catch (Throwable e) {
	    	return "{'error':'got throwable'}";
	    }
    	return out.toString();
	}
	
	/**
	 * just to make doPost cleaner
	 * nothing fancy. actually need to clean msg
	 * @param response
	 */
	private void printErrorInJsonOutput(HttpServletResponse response, String msg)
			throws IOException{
		response.setContentType("application/json"); 
		response.getOutputStream().print("{'error':'"+msg+"'}");
	}

	protected void doGet(HttpServletRequest request,  
			HttpServletResponse response) throws ServletException,  
			IOException {  
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request,  
			HttpServletResponse response) throws ServletException,  
			IOException {  
		log.info("entering.... " + getClass().getCanonicalName());
		
		String json="{'error':'failure in BundleManagerServlet doPost'}";
		
		InputStream is = request.getInputStream();
		if(is == null){
			printErrorInJsonOutput(response,"err getting input stream");
			return;
		}
		//convert to stream for debug only
		//json = convertStreamToString2(is);
		
		//string to decode 
		//String fileStringEncoded = convertStreamToString2(is);
		//String fileStringDecoded = URLDecoder.decode(fileStringEncoded, "UTF-8");
		//InputStream is2 = new ByteArrayInputStream(fileStringDecoded.getBytes());
		//try to get location string... may be too late
		String location = request.getParameter("location");
		if(location == null || "".equals(location)){
			printErrorInJsonOutput(response,"location not submitted");
			return;
		}
		printErrorInJsonOutput(response, ".......about to post bundle with location:"+location+"\n");
		
		//do stuff with bundle!
		try {
			Long id = installBundle(location, is);
			if(id==null){
				printErrorInJsonOutput(response, "Install failed with id==null");
				return;
			}
			json="{'success':'bundle installed with id=="+id+"'}";
			//response.setContentType("application/json"); 
			response.setContentType("text/plain"); 
			response.getOutputStream().print(json);
		} catch(BundleException e){
			printErrorInJsonOutput(response, e.getMessage());
			e.printStackTrace();
			e.printStackTrace(new PrintStream(response.getOutputStream()));
			printErrorInJsonOutput(response, "\n.......errors above\n");
			return;
		}
		log.info("leaving.... " + getClass().getCanonicalName());
	}  

	/*
	 * This is a good iface to reuse, but until validated, i don't want to expose it yet
	 * The below is adapted from the book "OSGi in Action", Manning 2011
	 */
	
	/**
	 * @param id
	 */
	private Bundle getBundle(final Long id) {
		Bundle bundle = context.getBundle(id);
		if(bundle == null) {
			throw new IllegalArgumentException("Attempted retreival of Bundle failed with bundleId: "+id);
		}
		return bundle;
	}
	
	/**
	 * @param location
	 * @return null if not found
	 * @throws Exception
	 */
	private Long getBundle(final String location) throws Exception {
		//location should be unique, so just return if found
		for(Bundle bundle: context.getBundles()){
			if(location.equalsIgnoreCase(bundle.getLocation())){
				return bundle.getBundleId();
			}
		}
		return null;
	}
	
	private void updateBundle(final Long id) throws Exception {
		Bundle bundle = getBundle(id);
		bundle.update();
	}
	
	private Long installBundle(final String location, final InputStream is) throws BundleException{
		Bundle bundle = context.installBundle(location, is);
		long id = bundle.getBundleId();
		return id;
	}
}