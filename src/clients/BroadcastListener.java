package clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import net.SyncState;

import server.NetworkEngine;

public class BroadcastListener extends Thread {
	
	private DatagramSocket socket;
	//private LinkedBlockingQueue<String> queue;
	SyncState state;
	
	public BroadcastListener (SyncState s) {
		state = s;
		try {
			socket = new DatagramSocket(NetworkEngine.BCAST_PORT);
		} catch (IOException e) {
			System.out.println("Error: Can't open port "+NetworkEngine.BCAST_PORT);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Reads packet data from Broadcast server
	 * 
	 * @return
	 */
	public String readBPacket() {
		DatagramPacket packet;
		byte[] buf = new byte[NetworkEngine.BCAST_BUF_SIZE];
		packet = new DatagramPacket(buf, buf.length);

		try {
			socket.receive(packet);
		} catch (IOException e) {
			System.out.println("Error: Can't recieve packet.");
			e.printStackTrace();
		}
		return new String (packet.getData(),0,packet.getLength());	
	}
	
	/**
	 * Runs this threat to keep server and client game state in sync
	 */
	public void run() {
		while(true) {
			String s = readBPacket();
			state.set(s);
			//gm.sync(s);
			//System.out.println(s);
		}
	}
	
	public void close() {
		socket.close();
	}
}
