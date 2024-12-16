package com.shopme.amazons3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;


public class AmazonS3Test {

	@Test
	public void testListFolder() {
//		AmazonS3Util.listFolder("user-photos/10");
	}
	
	@Test
	public void testUploadFile() throws FileNotFoundException {
		String fileName="z3413373777736_389f9e56cc7938fbfd347bbb1ebdfde8.jpg";
		String path="C:\\Users\\gtvvu\\Documents\\Zalo Received Files\\Anh zalo\\"+fileName;
		String folder="test";
		InputStream stream=new FileInputStream(path);
//		AmazonS3Util.uploadFile(folder, fileName, stream);
	}
}
