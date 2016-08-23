/**
 *****************************************************************************
 Copyright (c) 2016 IBM Corporation and other Contributors.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 Contributors:
 Sathiskumar Palaniappan - Initial Contribution
 *****************************************************************************
 *
 */
package com.ibm.iotf.client.application;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.iotf.client.app.ApplicationStatus;
import com.ibm.iotf.client.app.Command;
import com.ibm.iotf.client.app.DeviceStatus;
import com.ibm.iotf.client.app.Event;
import com.ibm.iotf.client.app.EventCallback;
import com.ibm.iotf.client.app.StatusCallback;
import com.ibm.iotf.client.device.DeviceClient;

import junit.framework.TestCase;

/**
 * This test verifies that the event & device connectivity status are successfully received by the
 * application.
 *
 */
public class ApplicationEventSubscriptionTest extends TestCase{
	
	private final static String DEVICE_PROPERTIES_FILE = "/device.properties";
	private final static String APPLICATION_PROPERTIES_FILE = "/application.properties";
	
	/**
	 * This method publishes a device event such that the application will receive the same
	 * and verifies that the event is same.
	 */
	private void deviceEventPublish() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(DEVICE_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		DeviceClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new DeviceClient(props);
			myClient.connect();
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
			
		//Generate a JSON object of the event to be published
		JsonObject event = new JsonObject();
		event.addProperty("name", "foo");
		event.addProperty("cpu",  90);
		event.addProperty("mem",  70);
					
		myClient.publishEvent("blink", event);
		myClient.disconnect();
	}
	
	@Test
	public void test01EventSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		//Add status callback
		MyStatusCallback statusbk = new MyStatusCallback();
		myClient.setStatusCallback(statusbk);
		
		// Subscribe to device events and device connectivity status
		//myClient.subscribeToDeviceStatus();
		myClient.subscribeToDeviceEvents(deviceType, deviceId);
		
		this.deviceEventPublish();
		
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Event is not received by application", eventbk.eventReceived);
		//assertTrue("Device connectivity status is not received by application", statusbk.statusReceived);
	}

	@Test
	public void test11CommandSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceCommands();
		
		myClient.publishCommand(deviceType, deviceId, "start", null);
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Command is not received by application", eventbk.cmdReceived);
	}


	@Test
	public void test12CommandSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceCommands(deviceType, deviceId);
		
		myClient.publishCommand(deviceType, deviceId, "start", null);
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Command is not received by application", eventbk.cmdReceived);
	}

	@Test
	public void test13CommandSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceCommands(deviceType, deviceId, "start");
		
		myClient.publishCommand(deviceType, deviceId, "start", null);
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Command is not received by application", eventbk.cmdReceived);
	}
	
	@Test
	public void test14CommandSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceCommands(deviceType, deviceId, "hello");
		
		myClient.publishCommand(deviceType, deviceId, "start", null);
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertFalse("Device Command is not supposed to be received by application", eventbk.cmdReceived);
	}
	
	@Test
	public void test15CommandSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceCommands(deviceType, deviceId, "start");
		myClient.unsubscribeFromDeviceCommands(deviceType, deviceId, "start");
		
		myClient.publishCommand(deviceType, deviceId, "start", null);
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertFalse("Device Command is not supposed to be received by application", eventbk.cmdReceived);
	}
	
	@Test
	public void test16CommandSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceCommands(deviceType, deviceId, "start", "json");
		
		myClient.publishCommand(deviceType, deviceId, "start", null);
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Command is not received by application", eventbk.cmdReceived);
	}
	
	@Test
	public void test17CommandSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceCommands(deviceType, deviceId, "start", "json");
		myClient.unsubscribeFromDeviceCommands(deviceType, deviceId, "start", "json");
		
		myClient.publishCommand(deviceType, deviceId, "start", null);
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertFalse("Device Command is not supposed to be received by application", eventbk.cmdReceived);
	}
	
	@Test
	public void test18CommandSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceCommands(deviceType);
		
		myClient.publishCommand(deviceType, deviceId, "start", null);
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Command is not received by application", eventbk.cmdReceived);
	}
	
	@Test
	public void test19CommandSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(5);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceCommands(deviceType, deviceId, "start", "json", 1);
		
		myClient.publishCommand(deviceType, deviceId, "start", null);
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Command is not received by application", eventbk.cmdReceived);
	}
	
	
	@Test
	public void test02Subscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(true);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceEvents(deviceType, deviceId, "blink");
		
		this.deviceEventPublish();
		
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Event is not received by application", eventbk.eventReceived);
	}
	
	/**
	 * Application subscribing to all the device events
	 */
	@Test
	public void test05Subscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(true);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceEvents();
		
		this.deviceEventPublish();
		
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Event is not received by application", eventbk.eventReceived);
	}
	
	/**
	 * Application subscribing to all the device events of a particular device type
	 */
	@Test
	public void test06Subscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(true);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceEvents(deviceType);
		
		this.deviceEventPublish();
		
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Event is not received by application", eventbk.eventReceived);
	}
	
	/**
	 * Application subscribing to all the device events of a particular device type, device Id and JSON format
	 */
	@Test
	public void test07Subscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(true);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		myClient.subscribeToDeviceEvents(deviceType, deviceId, "blink", "json");
		this.deviceEventPublish();
		
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 10) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Event is not received by application", eventbk.eventReceived);
	}
	
	/**
	 * Application subscribing to all the device events of a particular device type, device Id, JSON format and Qos
	 */
	@Test
	public void test08SubscribewithQos() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(true);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		//Add status callback
		MyStatusCallback statusbk = new MyStatusCallback();
		myClient.setStatusCallback(statusbk);
		
		myClient.subscribeToDeviceEvents();
		this.deviceEventPublish();
		
		myClient.subscribeToDeviceEvents(deviceType, deviceId, "blink", "json", 0);
		
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertTrue("Device Event is not received by application", eventbk.eventReceived);
	}
	
	/**
	 * Negative test
	 */
	@Test
	public void test03Subscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(true);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		//Add status callback
		MyStatusCallback statusbk = new MyStatusCallback();
		myClient.setStatusCallback(statusbk);
		
		// Subscribe to some different event
		myClient.subscribeToDeviceEvents(deviceType, deviceId, "Nonevent");
		//myClient.subscribeToDeviceStatus(deviceType);
		this.deviceEventPublish();
		
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertFalse("Device Event is not supposed to be received by application", eventbk.eventReceived);
		//assertTrue("Device connectivity status is not received by application", statusbk.statusReceived);
	}
	
	/**
	 * Negative test
	 */
	@Test
	public void test08UnSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(true);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		//Add status callback
		MyStatusCallback statusbk = new MyStatusCallback();
		myClient.setStatusCallback(statusbk);
		
		// Subscribe to some different event
		myClient.subscribeToDeviceEvents(deviceType, deviceId, "blink");
		myClient.unsubscribeFromDeviceEvents(deviceType, deviceId, "blink");
		
		this.deviceEventPublish();
		
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertFalse("Device Event is not supposed to be received by application", eventbk.eventReceived);
	}
	
	/**
	 * Negative test
	 */
	@Test
	public void test09UnSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(true);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		// Add event callback
		MyEventCallback eventbk = new MyEventCallback();
		myClient.setEventCallback(eventbk);
		
		//Add status callback
		MyStatusCallback statusbk = new MyStatusCallback();
		myClient.setStatusCallback(statusbk);
		
		// Subscribe to some different event
		myClient.subscribeToDeviceEvents(deviceType, deviceId, "blink", "json");
		myClient.unsubscribeFromDeviceEvents(deviceType, deviceId, "blink", "json");
		
		this.deviceEventPublish();
		
		int count = 0;
		// wait for sometime before checking
		while(eventbk.eventReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		assertFalse("Device Event is not supposed to be received by application", eventbk.eventReceived);
	}
	
	/**
	 * Negative test
	 */
	@Test
	public void test10UnSubscribe() {
		/**
		 * Load device properties
		 */
		Properties props = new Properties();
		try {
			props.load(ApplicationEventSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			return;
		} 
			
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect(true);
		} catch (Exception e) {
			System.out.println(""+e.getMessage());
			// Looks like the properties file is not udpated, just ignore;
			return;
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = trimedValue(props.getProperty("Device-Type"));
		String deviceId = trimedValue(props.getProperty("Device-ID"));
		
		//Add status callback
		MyStatusCallback statusbk = new MyStatusCallback();
		myClient.setStatusCallback(statusbk);
		
		// Subscribe to some different event
		//myClient.subscribeToDeviceStatus(deviceType, deviceId);
		//myClient.unSubscribeFromDeviceStatus(deviceType, deviceId);
		
		this.deviceEventPublish();
		
		int count = 0;
		// wait for sometime before checking
		while(statusbk.statusReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		myClient.disconnect();
		// ToDo uncomment when the bug is fixed
		//assertFalse("Device status is not supposed to be received by application", statusbk.statusReceived);
	}
	
	public void test04QuickstartEventPublish() {
		Properties props = new Properties();
		
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new ApplicationClient(props);
			myClient.connect();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		/**
		 * Get the Device Type and Device Id on behalf the application will publish the event
		 */
		String deviceType = "iotsample-ardunio";
		String deviceId = "00112233aabb";

		
		//Quickstart flow allows only QoS = 0
		boolean status = myClient.publishEvent(deviceType, deviceId, "blink", null);
		assertTrue("Failed to publish the event in quickstart", status);

	}

	private static class MyEventCallback implements EventCallback {
		private volatile boolean eventReceived = false;
		private volatile boolean cmdReceived = false;

		@Override
		public void processEvent(Event e) {
			eventReceived = true;
			System.out.println("Received Event, name = "+e.getEvent() +
					", format = " + e.getFormat() + ", Payload = "+e.getPayload() + ", time = "+e.getTimestamp());
		}

		@Override
		public void processCommand(Command cmd) {
			cmdReceived = true;
			System.out.println("Received command, name = "+cmd.getCommand() +
					", format = " + cmd.getFormat() + ", Payload = "+cmd.getPayload() + ", time = "+cmd.getTimestamp());		
		}		
	}
	
	private static class MyStatusCallback implements StatusCallback {
		private volatile boolean statusReceived = false;

		@Override
		public void processApplicationStatus(ApplicationStatus status) {
			System.out.println("Application Status = " + status.getAction()
					+ " " + status.getClientAddr() + status.getClientId() + status.getConnectTime() +
					status.getId() + status.getPayload() + status.getPort() + status.getProtocol() +
			status.getReadBytes() + status.getReadMsg() + status.getReason());
		}

		@Override
		public void processDeviceStatus(DeviceStatus status) {
			statusReceived = true;
			System.out.println("Device Status = " + status.getPayload());
			
			System.out.println("Device Status = " + status.getAction()
					+ " " + status.getClientAddr() + status.getClientId() + status.getConnectTime() +
					 status.getPayload() + status.getPort() + status.getProtocol() +
			status.getReadBytes() + status.getReadMsg() + status.getReason());

		}
	}

	private static String trimedValue(String value) {
		if(value != null) {
			return value.trim();
		}
		return value;
	}

}