package com.ibm.iotf.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.junit.Test;

import com.ibm.iotf.client.api.Device;
import com.ibm.iotf.client.api.DeviceFactory;

import junit.framework.TestCase;

public class TestAddDevice extends TestCase {
	@Test
	public void testAddDevice(){
		try{
		Properties opt = new Properties();

		String deviceType = "";
		String deviceId = "";
		String authKey = "";
		String authToken = "";
		
		File file = new File("src/test.properties");
		FileInputStream fileInput = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fileInput);
		fileInput.close();

		Enumeration<Object> enuKeys = properties.keys();
		
		while (enuKeys.hasMoreElements()) {
			
			String key = (String) enuKeys.nextElement();
			System.out.println(key + ": " );
			if(key.equals("deviceType") ){
				
				deviceType = properties.getProperty(key);
				
			}
			if(key.equals("deviceId")){
				deviceId = properties.getProperty(key);
				
			}
			if(key.equals("authKey")){
				authKey = properties.getProperty(key);
				
			}
			if(key.equals("authToken")){
				authToken = properties.getProperty(key);
				
			}
			//System.out.println(key + ": " + value);
		}
	
		opt.put("authKey", authKey);
		opt.put("authToken", authToken);
		
		DeviceFactory factory = new DeviceFactory(opt);
		String metadata = "";
		Device device = factory.registerDevice(deviceType, deviceId, metadata);
		if(device != null)
			System.out.println("Device created and has " + device);
		else 
			System.out.println("Device not created....");
		
		assertEquals("Adddevices", device, factory.registerDevice(deviceType, deviceId, metadata));
		//assertEquals("A test for Hello World String", "Hello World", hello.sayHello());
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
