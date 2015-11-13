package com.swarmcop.raspberrypi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;



public class CopyOfSocketServer{ 
	 private boolean runServer=true;
	 private static final int SERVERPORTSEND = 5005;			//Port for sending message
	 private static final int SERVERPORTRECEIVE = 5004;			//Use a separate port for receiving message
	 private DataOutputStream outChannel;
	 private Thread receiveThread;
	 private Thread sendThread;
	 public ServerSocket serverSocketReceive;
	 public ServerSocket serverSocketSend;
	 protected Socket socketReceive;
	 protected Socket socketSend;
	 
	 
	 
	 public CopyOfSocketServer(){
		 serverSocketReceive = null;
		 serverSocketSend = null;
		
		 
    } 
		
	 

	 public static void main(String[] args)
	   { 
		 	
		 	CopyOfSocketServer mySS = new CopyOfSocketServer();
		 	try{
				mySS.serverSocketReceive = new ServerSocket(SERVERPORTRECEIVE); 
				//mySS.serverSocketSend = new ServerSocket(SERVERPORTSEND);
				try {
					System.out.println ("Waiting for Connection");
					while(mySS.runServer){
						mySS.serverSocketReceive.setSoTimeout(30000);
						//mySS.serverSocketSend.setSoTimeout(10000);
						mySS.socketReceive=mySS.serverSocketReceive.accept();
						//mySS.socketSend=mySS.serverSocketSend.accept();
						mySS.receiveThread = mySS.new ServerThread (mySS.socketReceive);
						//mySS.sendThread = mySS.new ServerThread (mySS.socketSend);	
						mySS.receiveThread.start();
						//mySS.sendThread.start();
					}
					
				}catch (IOException e) { 
		          System.err.println("Accept failed."); 
		          System.exit(1);
		         } 
			}catch (IOException e) { 
			     System.err.println("Could not listen on ports");
	    	} finally{	
	    		try {
		              System.out.println ("Closing Server Connection Socket");
		              mySS.serverSocketReceive.close();
		              mySS.serverSocketSend.close();
		              System.exit(1); 
		             }
		         catch (IOException e)
		             { 
		              System.err.println("Could not close ports"); 
		              System.exit(1); 
		             } 
		        }
	   }
	 
	 
	 class ServerThread extends Thread{
		 
		private final int SERVERPORT;
			
		public ServerThread(Socket socketPort){
			this.SERVERPORT = socketPort.getPort();
		}
		

		public void start() {
			try{
				if(SERVERPORT==SERVERPORTSEND){							//outChannel is set up on the send port only
					outChannel = new DataOutputStream(socketSend.getOutputStream());
				} else {	//SERVERPORT==SERVERPORTRECEIVE
					MessageReceiveThread receiveThread = new MessageReceiveThread(socketReceive);	//only need comm port for receive sockets
					new Thread(receiveThread).start();
				} 
			} catch(UnknownHostException e1){
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		}
	 }

	
	class MessageReceiveThread implements Runnable {
	
		private DataInputStream input;
		
		public MessageReceiveThread(Socket clientSocket){
			try{
				System.out.println("ClientSocket: "+clientSocket.toString());
				this.input = new DataInputStream(clientSocket.getInputStream());
				
				} catch(IOException e){
					e.printStackTrace();
				}
		}
		
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()){
				try{
					byte[] inMessageBytes = new byte[10000];
	        		input.read(inMessageBytes);
	     			String message = new String(inMessageBytes);
	     			message.trim();
	     			//Need to do this to let socket know when bad data is coming through
	     			if(!message.startsWith("0")){
	     				System.out.println("Closed, waiting for reconnection...");
	     				break;
	     			}
	     			message=message.replaceFirst("0", "");
	     			System.out.println("Received: "+message);
	     			if(message.contains("Shut Down Server")){
	     				System.out.println("Sending signal to shut down");
	     				runServer=false;
	     			}
	     			
				} catch(IOException e){
					e.printStackTrace();
				}
				catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
}
	 
	 