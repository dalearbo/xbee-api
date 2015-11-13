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
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;
import com.rapplogic.xbee.util.ByteUtils;

/** 
 * @author dale
 */
public class BroadcastSender {
	
	XBee xbee;

	
	public BroadcastSender(XBee _xbee){
		this.xbee = _xbee;
	}

	private final static Logger log = Logger.getLogger(BroadcastSender.class);

	public void sendMessage(String messageData) {
		if(messageData!=null){
			
			System.out.println("Message Length: "+messageData.trim().length());
			
			int [] payload = ByteUtils.stringToIntArray(messageData.trim());
			ZNetTxRequest request = new ZNetTxRequest(XBeeAddress64.BROADCAST, payload);
			// make it a broadcast packet
			request.setOption(ZNetTxRequest.Option.BROADCAST);

			log.info("request packet bytes (base 16) " + ByteUtils.toBase16(request.getXBeePacket().getPacket()));
			
			try {
				xbee.sendAsynchronous(request);
			} catch (XBeeException e) {
				if (xbee != null && xbee.isConnected()) {
					xbee.close();		
				}
				e.printStackTrace();
			}		
	    }
	}
}
