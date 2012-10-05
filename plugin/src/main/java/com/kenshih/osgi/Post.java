package com.kenshih.osgi;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kshih
 */
public class Post {
	
	private HttpHost targetHost;
	private DefaultHttpClient httpclient;
	private BasicHttpContext localcontext;
	private String host;
	private int port;
	private String userName;
	private String password;
	private static Logger log = LoggerFactory.getLogger(Post.class);

	public static Post createPost(String host, int port, String userName, String password){
		Post post = new Post();
		post.host=host;
		post.port=port;
		post.userName=userName;
		post.password=password;
		post.targetHost = new HttpHost(host, port, "http");
        post.httpclient = new DefaultHttpClient();
      
        //include for test scenario
    	if(userName != null ){
    		post.httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(post.targetHost.getHostName(), post.targetHost.getPort()),
                new UsernamePasswordCredentials(userName, password));
    	}
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local
        // auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(post.targetHost, basicAuth);

        // Add AuthCache to the execution context
        post.localcontext = new BasicHttpContext();
        post.localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        return post;
	}
	
	
	public String filePost(final String filePath) throws ClientProtocolException, IOException{
		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		
		HttpPost httpPost = new HttpPost("/system/console/bundles");
		MultipartEntity entity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
		File file = new File(filePath);
		entity.addPart("bundlefile", new FileBody((file), "application/octet-stream" ));
		entity.addPart( "action", new StringBody( "install", "text/plain", Charset.forName( "UTF-8" )));
		httpPost.setEntity(entity);
		//run
		HttpResponse response = httpclient.execute(targetHost, httpPost, localcontext);
		StatusLine sl = response.getStatusLine();
		if (sl != null) {
			return ""+sl.getStatusCode();
		} 
//fall-through if no response
		return null;
	}
	

	/**
	 * shuts down underlying connection manager (re-instantiated on next {@link #createGet(String, int, String, String)}
	 */
	public void shutdown(){
		if(httpclient!=null) httpclient.getConnectionManager().shutdown();
		httpclient=null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		//last ditch effort
		try { shutdown(); } catch(Throwable e){}//swallow
		super.finalize();
	}	
	
	protected void setHttpClient(DefaultHttpClient client){
		this.httpclient = client;
	}
	
		
	
}

