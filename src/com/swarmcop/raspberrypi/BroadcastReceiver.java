/**
 * Copyright (c) 2008 Andrew Rapp. All rights reserved.
 *  
 * This file is part of XBee-API.
 *  
 * XBee-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * XBee-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with XBee-API.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.swarmcop.raspberrypi;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;

/** 
 * @author dale
 */
public class BroadcastReceiver {

	private final static Logger log = Logger.getLogger(BroadcastReceiver.class);
	XBee xbee;
	
	public BroadcastReceiver(XBee _xbee){
		this.xbee = _xbee;
	}
	
	
	public String listenForBroadcast() throws XBeeException {
			
		XBeeResponse response = xbee.getResponse();
		log.info("received response " + response);
		//System.out.println("Received response: "+response);
		String responseMessage[] = response.toString().split("data=");
		if(responseMessage.length>1){
			String dataString = responseMessage[1];
			String[] bytesString = dataString.split(",");
			byte[] bytes = new byte[bytesString.length];
			for(int i = 0 ; i < bytes.length ; ++i) {
			    bytes[i] = Byte.parseByte(bytesString[i].substring(2).toUpperCase(),16);		//Trim off '0x' from beginning of byte, uppercase
			}
			String messageString = new String(bytes);
			System.out.println("Received response: "+messageString);
			return messageString;
				
		}
		return null;
	}
	
	public static void main(String[] args) throws XBeeException, InterruptedException  {
		//PropertyConfigurator.configure("log4j.properties");
		XBee xbee = new XBee();
		try {
			xbee.open("/dev/ttyUSB0", 9600);
		} catch (XBeeException e) {
			e.printStackTrace();
		}
		new BroadcastReceiver(xbee);
	}
}
