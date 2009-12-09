package server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Hashtable;

/**
 * Broadcasts game state to a set of IPs
 * 
 * The broadcasting is done via UDP
 * 
 * @author Vitaliy
 *
 */
public class Broadcaster {
	
	private DatagramSocket socket;
	private int port;
	
	public Hashtable<Integer, String> ipList = new Hashtable<Integer, String>();
	
	public Broadcaster (int p) {	
	   port = p;

	   try {
		   socket = new DatagramSocket();
	   } catch (IOException e) {
		   System.out.println("Server Error: Can't create");
	   }	
	}
	
	/**
	 * Add an IP address to the broadcasting list
	 * 
	 * @param ip
	 * @return 
	 */
	public void addIP(int id, String ip) {
		System.out.println("Adding "+id+" at "+ip+" to broadcasting list.");
		if (!ipList.contains(ip))
			ipList.put(id, ip);
	}
	
	public void clearIPs() {
		ipList.clear();
	}
	
	/**
	 * Spam current game state to all clients (IPs in the ipList)
	 * 
	 * @param state - game state encoded into a string (see Protocol.java for details)
	 */
	public void spam(String state) {
		
		byte[] buf = new byte[NetworkEngine.BCAST_BUF_SIZE];
		
		if (ipList.size() < 1)
			return;
    		
		buf = state.getBytes();
			
		try {
			for(String s : ipList.values()) {
		    	DatagramPacket packet = new DatagramPacket(buf, buf.length,
		    				InetAddress.getByName(s), port);
	
		    	socket.send(packet);
		    	//System.out.println("Sending: "+ s + ":" + port);//new String (packet.getData(),0,packet.getLength()));
			}
	            
	     } catch (IOException e) {
	        e.printStackTrace();
	     }
	}
}
