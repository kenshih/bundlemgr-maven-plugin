package com.kenshih.osgi;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which touches a timestamp file.
 *
 * goal hi
 * 
 * phase process-sources
 */
@Mojo( name = "hi")
public class BundleMgrMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * parameter expression="${project.build.directory}"
     * required
     */
	 @Parameter( property = "str", 
             required = true, 
             defaultValue = "${project.build.directory}" )
    private String string;

    public void execute()
        throws MojoExecutionException
    {
    	System.out.println("got here with string: "+string);
       
    }
}
