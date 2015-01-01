/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-Q07
 * (c) Copyright IBM Corp. 2014
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */
package com.ibm.iotcloud.client;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A client that handles connections with the IBM Internet of Things Cloud
 * Service.
 */
public class Device extends AbstractClient{
	
	private static final String CLASS_NAME = Device.class.getName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);
	
	private Properties options;
	
	/**
	 * Create a device client for the IBM Internet of Things Cloud service. Connecting to
	 * a specific account on the service.
	 * @throws Exception 
	 */
	public Device(Properties options) throws Exception {
		this.options = options;
		this.serverURI = "tcp://" + options.getProperty("org") + "." + HOSTNAME + ":" + PORT;
		this.clientId = "d" + CLIENT_ID_DELIMITER + getOrgId() + CLIENT_ID_DELIMITER + getDeviceType() + CLIENT_ID_DELIMITER + getDeviceId();
		
		if (getAuthMethod() == null) {
			this.clientUsername = null;
			this.clientPassword = null;
		}
		else if (!getAuthMethod().equals("token")) {
			throw new Exception("Unsupported Authentication Method: " + getAuthMethod());
		}
		else {
			// use-token-auth is the only authentication method currently supported
			this.clientUsername = "use-token-auth";
			this.clientPassword = getAuthToken();
		}
		createClient(new DeviceCallback());
	}
	
	public String getOrgId() {
		return options.getProperty("org");
	}

	public String getDeviceId() {
		return options.getProperty("id");
	}

	public String getDeviceType() {
		return options.getProperty("type");
	}

	public String getAuthMethod() {
		return options.getProperty("auth-method");
	}

	public String getAuthToken() {
		return options.getProperty("auth-token");
	}


	@Override
	public void connect() {
		super.connect();
		if (!getOrgId().equals("quickstart")) {
			subscribeToCommands();
		}
	}
	
	private void subscribeToCommands() {
		try {
			client.subscribe("iot-2/cmd/+/fmt/json", 2);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Publish data to the IBM Internet of Things Cloud service. Note that data is published
	 * at Quality of Service (QoS) 0, which means that a successful send does not guarantee
	 * receipt even if the publich is successful.
	 * 
	 * @param event
	 *            Name of the dataset under which to publish the data
	 * @param data
	 *            Object to be added to the payload as the dataset
	 * @return Whether the send was successful.
	 */
	public boolean publishEvent(String event, Object data) {
		return publishEvent(event, data, 0);
	}
	
	public boolean publishEvent(String event, Object data, int qos) {
		if (!isConnected()) {
			return false;
		}
		JsonObject payload = new JsonObject();
		
		String timestamp = ISO8601_DATE_FORMAT.format(new Date());
		payload.addProperty("ts", timestamp);
		
		JsonElement dataElement = gson.toJsonTree(data);
		payload.add("d", dataElement);
		
		String topic = "iot-2/evt/" + event + "/fmt/json";
		
		LOG.fine("Topic   = " + topic);
		LOG.fine("Payload = " + payload.toString());
		
		MqttMessage msg = new MqttMessage(payload.toString().getBytes(Charset.forName("UTF-8")));
		msg.setQos(0);
		msg.setRetained(false);
		
		try {
			client.publish(topic, msg);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
			return false;
		} catch (MqttException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	private class DeviceCallback implements MqttCallback {
		/**
		 * If we lose connection trigger the connect logic to attempt to
		 * reconnect to the IBM Internet of Things Cloud.
		 */
		public void connectionLost(Throwable e) {
			LOG.info("Connection lost: " + e.getMessage());
			connect();
		}
		
		/**
		 * A completed deliver does not guarantee that the message is recieved by the service
		 * because devices send messages with Quality of Service (QoS) 0. The message count
		 * represents the number of messages that were sent by the device without an error on
		 * from the perspective of the device.
		 */
		public void deliveryComplete(IMqttDeliveryToken token) {
			LOG.fine("Delivery Complete!");
			messageCount++;
		}
		
		/**
		 * The Device client does not currently support subscriptions.
		 */
		public void messageArrived(String topic, MqttMessage msg) throws Exception {
		}
	}
	
}
