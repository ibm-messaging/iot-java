package com.ibm.iotf.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import com.ibm.iotf.client.api.DeviceFactory;
import com.ibm.iotf.client.api.HistoricalEvent;

import junit.framework.TestCase;

public class TestHistoricalEvents extends TestCase {
	
	public void testHistoricalEvent(){
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
			}
			opt.put("authKey", authKey);
			opt.put("authToken", authToken);
			DeviceFactory factory = new DeviceFactory(opt);
			
			HistoricalEvent[] event = factory.getHistoricalEvents(deviceType, "00aabbccde03");
			//List<HistoricalEvent> messageList = new ArrayList<HistoricalEvent>();
	
			assertEquals("HistoricalEvents received", event.length >0, factory.getHistoricalEvents(deviceType, "00aabbccde03"));
			assertEquals("No HistoricalEvents received", event.length <=0, factory.getHistoricalEvents(deviceType, "00aabbccde03"));
			//assertEquals("Device deregistered", true, factory.deleteDevice(deviceType, deviceId));
			
			//assertEquals("A test for Hello World String", "Hello World", hello.sayHello());
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
