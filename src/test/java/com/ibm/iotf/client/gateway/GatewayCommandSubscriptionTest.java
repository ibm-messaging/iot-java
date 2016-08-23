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
package com.ibm.iotf.client.gateway;

import java.io.IOException;
import java.util.Properties;
import junit.framework.TestCase;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.IoTFCReSTException;
import com.ibm.iotf.client.api.APIClient;
import com.ibm.iotf.client.app.ApplicationClient;

/**
 * This test verifies that the device receives the command published by the application
 * successfully.
 *
 */
public class GatewayCommandSubscriptionTest extends TestCase{
	
	private final static String GATEWAY_PROPERTIES_FILE = "/gateway.properties";
	private final static String APPLICATION_PROPERTIES_FILE = "/application.properties";
	
	private final static String DEVICE_TYPE = "iotsampleType";
	private final static String SIMULATOR_DEVICE_ID = "Arduino02";
	private static String GATEWAY_DEVICE_TYPE = "";
	private static String GATEWAY_DEVICE_ID = "";
	private static GatewayClient gwClient = null;
	
	public void setUp() {
	    // do the setup
	    createGatewayClient(GATEWAY_PROPERTIES_FILE);
	}
	
	
	public void tearDown() {
		gwClient.disconnect();
	}
	/**
	 * This method creates a GatewayClient instance by passing the required properties 
	 * and connects the Gateway to the Watson IoT Platform by calling the connect function.
	 * 
	 * After the successful connection to the Watson IoT Platform, the Gateway can perform the following operations,
	 *   1. Publish events for itself and on behalf of devices connected behind the Gateway
	 *   2. Subscribe to commands for itself and on behalf of devices behind the Gateway
	 */
	private static void createGatewayClient(String fileName) {
		 /**
		  * Load device properties
		  */
		Properties props = new Properties();
		try {
			props.load(GatewayEventPublishTest.class.getResourceAsStream(fileName));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			System.exit(-1);
		}		
		
		try {
			//Instantiate & connect the Gateway by passing the properties file
			gwClient = new GatewayClient(props);
			gwClient.connect(true);
			
			/**
			 * Get the Device Type and Device Id to which the application will publish the command
			 */
			GATEWAY_DEVICE_TYPE = trimedValue(props.getProperty("Gateway-Type"));
			GATEWAY_DEVICE_ID = trimedValue(props.getProperty("Gateway-ID"));
			
		} catch (Exception e) {
			// Looks like the gateway.property file is not updated with registration details
			return;
		}
	}
	
	
	@Test
	public void testGatewayCommandReception() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		
		// Ask application to publish the command to this gateway now
		publishCommand(true, null);
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertTrue("The command is not received by gateway", callback.commandReceived);
	}
	
	@Test
	public void testDeviceCommandReception() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID);
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertTrue("The command is not received by gateway", callback.commandReceived);
	}
	
	@Test
	public void test02DeviceCommandReception() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "stop", "json");
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertTrue("The command is not received by gateway", callback.commandReceived);
	}
	
	@Test
	public void test03DeviceCommandReception() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "stop", "json", 2);
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertTrue("The command is not received by gateway", callback.commandReceived);
	}

	
	@Test
	public void testNotification() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		
		gwClient.subscribeToGatewayNotification();
	}
	
	@Test
	public void testDeviceSpecificCommandReception() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "start");
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, "start");
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertTrue("The command is not received by gateway", callback.commandReceived);
	}

	@Test
	public void testDeviceSpecificCommandReceptionWithQoS() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "start", 2);
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, "start");
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertTrue("The command is not received by gateway", callback.commandReceived);
	}
	
	@Test
	public void testDeviceSpecificCommandReceptionWithFormat() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "start", "json", 2);
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, "start");
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertTrue("The command is not received by gateway", callback.commandReceived);
	}

	@Test
	public void testDeviceCommandUnsubscription() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID);
		
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		count = 0;
		callback.clear();
		gwClient.unsubscribeFromDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID);
		publishCommand(false, null);
		
		
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertFalse("The command should not be received by gateway", callback.commandReceived);
				
	}
	
	@Test
	public void test02DeviceCommandUnsubscription() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "stop");
		
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		count = 0;
		callback.clear();
		
		
		gwClient.unsubscribeFromDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "stop");
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertFalse("The command should not be received by gateway", callback.commandReceived);
				
	}
	
	@Test
	public void test03DeviceCommandUnsubscription() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "stop", "json");
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		count = 0;
		callback.clear();
		
		
		gwClient.unsubscribeFromDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "stop", "json");
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertFalse("The command should not be received by gateway", callback.commandReceived);
				
	}
	
	@Test
	public void test04DeviceCommandUnsubscription() throws MqttException{
		
		//Pass the above implemented CommandCallback as an argument to this device client
		GatewayCommandCallback callback = new GatewayCommandCallback();
		gwClient.setGatewayCallback(callback);
		
		gwClient.subscribeToDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "stop", "json", 2);
		
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		
		int count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		count = 0;
		callback.clear();
		
		
		gwClient.unsubscribeFromDeviceCommands(DEVICE_TYPE, SIMULATOR_DEVICE_ID, "stop", "json");
		
		// Ask application to publish the command to this gateway now
		publishCommand(false, null);
		
		count = 0;
		// wait for sometime before checking
		while(callback.commandReceived == false && count++ <= 5) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {}
		}
		
		assertFalse("The command should not be received by gateway", callback.commandReceived);
				
	}
	
	private void publishCommand(boolean gateway, String cmdName) {
		/**
		  * Load device properties
		  */
		Properties props = new Properties();
		try {
			props.load(GatewayCommandSubscriptionTest.class.getResourceAsStream(APPLICATION_PROPERTIES_FILE));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			System.exit(-1);
		}
		
		ApplicationClient myAppClient = null;
		try {
			//Instantiate the class by passing the properties file
			myAppClient = new ApplicationClient(props);
			myAppClient.connect();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		JsonObject data = new JsonObject();
		data.addProperty("name", "stop-rotation");
		data.addProperty("delay",  0);
		
		if(cmdName == null) {
			// use default command name 
			cmdName = "stop";
		}
		if(gateway) {
			//Registered flow allows 0, 1 and 2 QoS
			myAppClient.publishCommand(GATEWAY_DEVICE_TYPE, GATEWAY_DEVICE_ID, cmdName, data);
		} else {
			myAppClient.publishCommand(DEVICE_TYPE, SIMULATOR_DEVICE_ID, cmdName, data);
		}
		myAppClient.disconnect();
	}

	
	//Implement the CommandCallback class to provide the way in which you want the command to be handled
	private static class GatewayCommandCallback implements GatewayCallback{
		private boolean commandReceived = false;
		
		/**
		 * This method is invoked by the library whenever there is command matching the subscription criteria
		 */
		@Override
		public void processCommand(com.ibm.iotf.client.gateway.Command cmd) {
			commandReceived = true;
			System.out.println("Received command, name = "+cmd.getCommand() +
					", format = " + cmd.getFormat() + ", Payload = "+cmd.getPayload() + ", time = "+cmd.getTimestamp() +
					", deviceId = "+cmd.getDeviceId() + ", deviceType = "+cmd.getDeviceType());
			
		}

		@Override
		public void processNotification(Notification notification) {
			// TODO Auto-generated method stub
			
		}
		
		private void clear() {
			commandReceived = false;
		}
	}
	
	private static String trimedValue(String value) {
		if(value != null) {
			return value.trim();
		}
		return value;
	}

}
