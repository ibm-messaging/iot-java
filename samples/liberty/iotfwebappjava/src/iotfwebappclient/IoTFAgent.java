package iotfwebappclient;

import java.util.Properties;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.iotf.client.app.ApplicationStatus;
import com.ibm.iotf.client.app.Command;
import com.ibm.iotf.client.app.DeviceStatus;
import com.ibm.iotf.client.app.Event;
import com.ibm.iotf.client.app.EventCallback;
import com.ibm.iotf.client.app.StatusCallback;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class IoTFAgent {
	public Properties options = new Properties();
	public ApplicationClient client = null;
	public BlockingQueue<String> messages =  new LinkedBlockingQueue<String>();
	
	public IoTFAgent(BlockingQueue<String> messages) {
		this.messages = messages;
		options = new Properties();
		options.put("id", "App" + (Math.random() * 10000));
		options.put("auth-method", "apikey");
	
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		try {
			if (VCAP_SERVICES != null) {
				JSONObject vcap = (JSONObject) JSONObject.parse(VCAP_SERVICES);
				JSONArray iotf = (JSONArray) vcap.get("iotf-service");
				JSONObject iotfInstance = (JSONObject) iotf.get(0);
				JSONObject iotfCredentials = (JSONObject) iotfInstance.get("credentials");

				String apiKey = (String)iotfCredentials.get("apiKey");
				String apiToken = (String)iotfCredentials.get("apiToken");
				
				if(apiKey == null || apiKey.equals("") || apiToken == null || apiToken.equals("") ) {
					options.put("auth-key", "some key");
					options.put("auth-token", "some token");
				} else {
					options.put("auth-key", apiKey);
					options.put("auth-token", apiToken);					
				}
			}
		} catch(Exception ex) {
		
		}
			
	//	options.put("auth-key", "a-uguhsp-8ya0dmsjh9");
	//	options.put("auth-token", ")2lMPstbsqkOTs@s&M");
		
		try {
			client = new ApplicationClient(options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.connect();
		client.setEventCallback(new MyEventCallback(messages));
		client.setStatusCallback(new MyStatusCallback());
		client.subscribeToDeviceEvents("iotsample-arduino", "00aabbccde03", "status", "nonjson", 0);
				
	}
	
	private class MyEventCallback implements EventCallback {
	private BlockingQueue<String> messages = new LinkedBlockingQueue<String>();
	
		public MyEventCallback(BlockingQueue<String> messages) {
			this.messages = messages;
		}
		public void processEvent(Event e) {
			System.out.println("Event " + e.getPayload());
			messages.offer(e.getPayload());
		}
	
		public void processCommand(Command cmd) {
			System.out.println("Command " + cmd.getPayload());			
	//		messages.offer(cmd.getPayload());
		}
		
	}
	
	private class MyStatusCallback implements StatusCallback {
	
		public void processApplicationStatus(ApplicationStatus status) {
	//		System.out.println("Application Status = " + status.getPayload());
		}
	
		public void processDeviceStatus(DeviceStatus status) {
	//		System.out.println("Device Status = " + status.getPayload());
		}
	}
	
}
