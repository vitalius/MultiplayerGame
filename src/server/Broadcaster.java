package server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

import net.GameState;

/**
 * Broadcasts game state to a set of IPs
 * 
 * The broadcasting is done via UDP
 * 
 * @author vitaliy
 *
 */
public class Broadcaster extends Thread {
	
	public static final int BCAST_DELAY    = 100;
	public static final int BCAST_BUF_SIZE = 256;
	
	private boolean running;
	private DatagramSocket socket;
	private int port;
	
	private LinkedList<String> ipList = new LinkedList<String>();
	
	private GameState gs;
	
	public Broadcaster (int p, GameState g) {	
	   running = true;
	   port = p;
	   gs = g;
	   
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
	 */
	public void addIP(String ip) {
		System.out.println("Adding "+ip+" to broadcasting list.");
		ipList.add(ip);
	}
	
	/**
	 * Run this thread and keep broadcasting the game state to all
	 * IPs in the broadcast list
	 */
	public void run() {
		
		byte[] buf = new byte[BCAST_BUF_SIZE];
		
	    while (running) {

            try {
				Thread.sleep(BCAST_DELAY);
			} catch (InterruptedException e1) {
				System.out.println("Error in Thread.sleep.");
				e1.printStackTrace();
			}
            
			if (ipList.size() < 1)
				continue;
    		
			buf = gs.encode().getBytes();
			
		    try {
		    	for(String s : ipList) {
		    		DatagramPacket packet = new DatagramPacket(buf, buf.length,
		    				InetAddress.getByName(s), port);
	
		    		socket.send(packet);
		    		//System.out.println("Sending: "+ new String (packet.getData(),0,packet.getLength()));
		    	}
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            running = false;
	        }
	    }
	    socket.close();
	}	
	
}
