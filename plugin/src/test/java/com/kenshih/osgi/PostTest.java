package com.kenshih.osgi;

import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;

public class PostTest {

	//@Test
	public void test() throws Exception{
		String filePath = "/Users/kenshih/Documents/workspace/github/bundle-installer/generated/bundle-installer.jar";
		Post post = Post.createPost("localhost", 4502, "admin", "admin");
		String result = post.filePost(filePath);
		post.shutdown();
		
		assertThat(result).isNotEmpty().contains("302");
	}
}
