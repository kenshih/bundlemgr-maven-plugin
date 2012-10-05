package com.kenshih.osgi;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal uploads an osgi bundle
 *
 * goal upload
 * 
 */
@Mojo( name = "upload")
public class BundleMgrMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * parameter expression="${project.build.directory}"
     * required
     */
	 @Parameter( property = "bundlepath", 
             required = true, 
             defaultValue = "/Users/kenshih/Documents/workspace/github/bundle-installer/generated/bundle-installer.jar" )
    private String bundlepath;

    public void execute()
        throws MojoExecutionException
    {
		Post post = Post.createPost("localhost", 4502, "admin", "admin");
		try {
			String result = post.filePost(bundlepath);
			post.shutdown();
			if(result.contains("302"))
				System.out.println("Installed: "+bundlepath);
			else
				System.out.println("Failed install of: "+bundlepath+ " at bundlemgr servlet");
		}
		catch (IOException e){
			System.out.println("Failed install of: "+bundlepath);
		}
		finally {
			post.shutdown();
		}
       
    }
}
