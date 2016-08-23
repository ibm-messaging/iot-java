/**
 *****************************************************************************
 Copyright (c) 2015-16 IBM Corporation and other Contributors.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 Contributors:
 Sathiskumar Palaniappan - Initial Contribution
 *****************************************************************************
 *
 */
package com.ibm.iotf.client.device;

import java.io.UnsupportedEncodingException;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.ibm.iotf.client.Message;

/**
 * The objects of this class hold the command and the format of the command sent to a device <br>
 * This class has only accessors and no mutators and later on might be made final
 * 
 */


public class Command extends Message{

	private String command, format;

	/**
	 * Note that this class does not have a default constructor <br>

	 * This class has only accessors and no mutators and later on might be made final <br>
	 * @param command
	 * 			Actual command in the form of String passed
	 * @param format
	 * 			Format is a String which can contain values such as "json"
	 * @param msg
	 * 			MqttMessage 
	 * @see <a href="http://www.eclipse.org/paho/files/javadoc/index.html">Paho Client Library</a> 
	 * @throws
	 * 			UnsupportedEncodingException When the Format is not UTF-8
	 * 
	 */	
	
	public Command(String command, String format, MqttMessage msg) throws UnsupportedEncodingException{
		super(msg);
		this.command = command;
		this.format = format;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getFormat() {
		return format;
	}

	/**
	 * 
	 * Provides a human readable String representation of this Command, including the timestamp and the actual command passed.
	 */
	public String toString() {
		return "[" + timestamp.toString() + "] " + command + ": " + data.toString(); 
	}

}
