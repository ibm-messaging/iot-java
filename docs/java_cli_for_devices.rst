===============================================================================
Java Client Library - Devices
===============================================================================

Introduction
-------------------------------------------------------------------------------

This client library describes how to use devices with the Java ibmiotf client library. For help with getting started with this module, see `Java Client Library - Introduction <https://github.com/ibm-messaging/iot-java/blob/master/README.md>`__. 

Constructor
-------------------------------------------------------------------------------

The constructor builds the client instance, and accepts a Properties object containing the following definitions:

* org - Your organization ID. (This is a required field. In case of quickstart flow, provide org as quickstart.)
* domain - (Optional) The messaging endpoint URL. By default the value is "internetofthings.ibmcloud.com"(Watson IoT Production server)
* type - The type of your device. (This is a required field.)
* id - The ID of your device. (This is a required field.
* auth-method - Method of authentication (This is an optional field, needed only for registered flow and the only value currently supported is "token"). 
* auth-token - API key token (This is an optional field, needed only for registered flow).
* clean-session - true or false (required only if you want to connect the application in durable subscription. By default the clean-session is set to true).

**Note:** One must set clean-session to false to connect the device in durable subscription. Refer to `Subscription Buffers and Clean Session <https://docs.internetofthings.ibmcloud.com/reference/mqtt/index.html#/subscription-buffers-and-clean-session#subscription-buffers-and-clean-session>`__ for more information about the clean session.

The Properties object creates definitions which are used to interact with the Watson IoT Platform module. 

The following code shows a device publishing events in a Quickstart mode.


.. code:: java



	package com.ibm.iotf.sample.client.device;

	import java.util.Properties;

	import com.google.gson.JsonObject;
	import com.ibm.iotf.client.device.DeviceClient;

	public class QuickstartDeviceEventPublish {

		public static void main(String[] args) {
			
			//Provide the device specific data using Properties class
			Properties options = new Properties();
			options.setProperty("org", "quickstart");
			options.setProperty("type", "iotsample-arduino");
			options.setProperty("id", "00aabbccde03");
			
			DeviceClient myClient = null;
			try {
				//Instantiate the class by passing the properties file
				myClient = new DeviceClient(options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Connect to the IBM IoT Foundation
			myClient.connect();
			
			//Generate a JSON object of the event to be published
			JsonObject event = new JsonObject();
			event.addProperty("name", "foo");
			event.addProperty("cpu",  90);
			event.addProperty("mem",  70);

			//Quickstart flow allows only QoS = 0
			myClient.publishEvent("status", event, 0);
			System.out.println("SUCCESSFULLY POSTED......");

      ...

 


The following program shows a device publishing events in a registered flow


.. code:: java


	package com.ibm.iotf.sample.client.device;

	import java.util.Properties;

	import com.google.gson.JsonObject;
	import com.ibm.iotf.client.device.DeviceClient;

	public class RegisteredDeviceEventPublish {

		public static void main(String[] args) {
			
			//Provide the device specific data, as well as Auth-key and token using Properties class		
			Properties options = new Properties();

			options.setProperty("org", "uguhsp");
			options.setProperty("type", "iotsample-arduino");
			options.setProperty("id", "00aabbccde03");
			options.setProperty("auth-method", "token");
			options.setProperty("auth-token", "AUTH TOKEN FOR DEVICE");
			
			DeviceClient myClient = null;
			try {
				//Instantiate the class by passing the properties file
				myClient = new DeviceClient(options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Connect to the IBM IoT Foundation		
			myClient.connect();
			
			//Generate a JSON object of the event to be published
			JsonObject event = new JsonObject();
			event.addProperty("name", "foo");
			event.addProperty("cpu",  90);
			event.addProperty("mem",  70);
			
			//Registered flow allows 0, 1 and 2 QoS
			myClient.publishEvent("status", event);
			System.out.println("SUCCESSFULLY POSTED......");

      ...



Using a configuration file
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Instead of including a Properties object directly, you can use a configuration file containing the name-value pairs for Properties. If you are using a configuration file containing a Properties object, use the following code format.

.. code:: java


	package com.ibm.iotf.sample.client.device;

	import java.io.File;
	import java.util.Properties;

	import com.google.gson.JsonObject;
	import com.ibm.iotf.client.device.DeviceClient;

	public class RegisteredDeviceEventPublishPropertiesFile {

		public static void main(String[] args) {
			//Provide the device specific data, as well as Auth-key and token using Properties class	
			Properties options = DeviceClient.parsePropertiesFile(new File("C:\\temp\\device.prop"));

			DeviceClient myClient = null;
			try {
				//Instantiate the class by passing the properties file			
				myClient = new DeviceClient(options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Connect to the IBM IoT Foundation	
			myClient.connect();
			
			//Generate a JSON object of the event to be published
			JsonObject event = new JsonObject();
			event.addProperty("name", "foo");
			event.addProperty("cpu",  90);
			event.addProperty("mem",  70);
			
			//Registered flow allows 0, 1 and 2 QoS
			myClient.publishEvent("status", event, 1);
			System.out.println("SUCCESSFULLY POSTED......");
			
      ...

The content of the configuration file must be in the following format:

::

    [device]
    org=$orgId
    domain=$domain
    typ=$myDeviceType
    id=$myDeviceId
    auth-method=token
    auth-token=$token


----

Connecting to the Watson IoT Platform
----------------------------------------------------

Connect to the Watson IoT Platform by calling the *connect* function. The connect function takes an optional boolean parameter autoRetry (by default autoRetry is true) that controls allows the library to retry the connection when there is an MqttException. Note that the library won't retry when there is a MqttSecurityException due to incorrect device registration details passed even if the autoRetry is set to true.

Also, one can use the setKeepAliveInterval(int) method before calling connect() to set the MQTT "keep alive" interval. This value, measured in seconds, defines the maximum time interval between messages sent or received. It enables the client to detect if the server is no longer available, without having to wait for the TCP/IP timeout. The client will ensure that at least one message travels across the network within each keep alive period. In the absence of a data-related message during the time period, the client sends a very small "ping" message, which the server will acknowledge. A value of 0 disables keepalive processing in the client. The default value is 60 seconds.

.. code:: java

    DeviceClient myClient = new DeviceClient(options);
    myClient.setKeepAliveInterval(120);
    myClient.connect(true);
    
Also, use the overloaded connect(int numberOfTimesToRetry) function to control the number of retries when there is a connection failure.

.. code:: java

    DeviceClient myClient = new DeviceClient(options);
    
    myClient.connect(10);

After the successful connection to the IoTF service, the device client can perform the following operations, like publishing events and subscribe to device commands from application.

----


Publishing events
-------------------------------------------------------------------------------
Events are the mechanism by which devices publish data to the Watson IoT Platform. The device controls the content of the event and assigns a name for each event it sends.

When an event is received by the IBM IoT Foundation the credentials of the connection on which the event was received are used to determine from which device the event was sent. With this architecture it is impossible for a device to impersonate another device.

Events can be published at any of the three `quality of service levels <https://docs.internetofthings.ibmcloud.com/messaging/mqtt.html#/>` defined by the MQTT protocol.  By default events will be published as qos level 0.

Publish event using default quality of service
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
.. code:: java

			myClient.connect();
			
			JsonObject event = new JsonObject();
			event.addProperty("name", "foo");
			event.addProperty("cpu",  90);
			event.addProperty("mem",  70);
		    
			myClient.publishEvent("status", event);


----


Publish event using user-defined quality of service
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Events can be published at higher MQTT quality of servive levels, but these events may take slower than QoS level 0, because of the extra confirmation of receipt. Also Quickstart flow allows only Qos of 0

.. code:: java

			myClient.connect();
			
			JsonObject event = new JsonObject();
			event.addProperty("name", "foo");
			event.addProperty("cpu",  90);
			event.addProperty("mem",  70);
		    
			//Registered flow allows 0, 1 and 2 QoS
			myClient.publishEvent("status", event, 2);


----

Publish event using HTTP(s)
~~~~~~~~~~~~~~~~~~~~~~~~~~~
Apart from MQTT, the devices can publish events to IBM Watson IoT Platform using HTTP(s) by following 3 simple steps,

* Construct a DeviceClient instance using the properties file
* Construct an event that needs to be published
* Specify the event name and publish the event using publishEventOverHTTP() method as follows,

.. code:: java

    	DeviceClient myClient = new DeviceClient(deviceProps);
    
    	JsonObject event = new JsonObject();
			event.addProperty("name", "foo");
			event.addProperty("cpu",  90);
			event.addProperty("mem",  70);
			
    	int httpCode = myClient.publishEventOverHTTP("blink", event);
    	
The complete code can be found in the device example `HttpDeviceEventPublish <https://github.com/ibm-messaging/iot-java/blob/master/samples/iotfdeviceclient/src/com/ibm/iotf/sample/client/device/HttpDeviceEventPublish.java>`__
 
Based on the settings in the properties file, the publishEventOverHTTP() method either publishes the event in Quickstart or in Registered flow. When the Organization ID mentioned in the properties file is quickstart, publishEventOverHTTP() method publishes the event to Watson IoT Platform quickstart service and publishes the event in plain HTTP format. But when valid registered organization is mentioned in the properties file, this method always publishes the event in HTTPS (HTTP over SSL), so all the communication is secured.

Also, It is possible for an application to publish the event on behalf of a device to IBM Watson IoT Platform using HTTP(s). This can be achieved by following 3 simple steps,

* Construct the ApplicationClient instance using the properties file
* Construct the event that needs to be published
* Specify the event name, Device Type, Device ID and publish the event using publishEventOverHTTP() method as follows,

.. code:: java

    	ApplicationClient myClient = new ApplicationClient(props);
    
    	JsonObject event = new JsonObject();
			event.addProperty("name", "foo");
			event.addProperty("cpu",  90);
			event.addProperty("mem",  70);
			
    	code = myClient.publishEventOverHTTP(deviceType, deviceId, "blink", event);
 

The complete code can be found in the application example `HttpApplicationDeviceEventPublish <https://github.com/ibm-messaging/iot-java/blob/master/samples/iotfdeviceclient/src/com/ibm/iotf/sample/client/application/HttpApplicationDeviceEventPublish.java>`__



Handling commands
-------------------------------------------------------------------------------
When the device client connects it automatically subscribes to any command for this device. To process specific commands you need to register a command callback method. 
The messages are returned as an instance of the Command class which has the following properties:

* payload - java.lang.String
* format - java.lang.String
* command - java.lang.String
* timestamp - org.joda.time.DateTime

.. code:: java

	package com.ibm.iotf.sample.client.device;

	import java.util.Properties;
	import java.util.concurrent.BlockingQueue;
	import java.util.concurrent.LinkedBlockingQueue;

	import com.ibm.iotf.client.device.Command;
	import com.ibm.iotf.client.device.CommandCallback;
	import com.ibm.iotf.client.device.DeviceClient;


	//Implement the CommandCallback class to provide the way in which you want the command to be handled
	class MyNewCommandCallback implements CommandCallback, Runnable {
	
		// A queue to hold & process the commands for smooth handling of MQTT messages
		private BlockingQueue<Command> queue = new LinkedBlockingQueue<Command>();
	
		/**
	 	* This method is invoked by the library whenever there is command matching the subscription criteria
	 	*/
		@Override
		public void processCommand(Command cmd) {
			try {
				queue.put(cmd);
			} catch (InterruptedException e) {
			}			
		}

		@Override
		public void run() {
			while(true) {
				Command cmd = null;
				try {
					//In this sample, we just display the command
					cmd = queue.take();
					System.out.println("COMMAND RECEIVED = '" + cmd.getCommand() + "'\twith Payload = '" + cmd.getPayload() + "'");
				} catch (InterruptedException e) {}
			}
		}
	}


	public class RegisteredDeviceCommandSubscribe {

		
		public static void main(String[] args) {
			
			//Provide the device specific data, as well as Auth-key and token using Properties class		
			Properties options = new Properties();
			
			options.setProperty("org", "uguhsp");
			options.setProperty("type", "iotsample-arduino");
			options.setProperty("id", "00aabbccde03");
			options.setProperty("auth-method", "token");
			options.setProperty("auth-token", "AUTH TOKEN FOR DEVICE");
			
			DeviceClient myClient = null;
			try {
				//Instantiate the class by passing the properties file			
				myClient = new DeviceClient(options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Pass the above implemented CommandCallback as an argument to this device client
			myClient.setCommandCallback(new MyNewCommandCallback());

			//Connect to the IBM IoT Foundation	
			myClient.connect();
		}
	}


