import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NetIO extends Thread {
	
	private DatagramSocket socket;
	private GameState gs;
	
	public NetIO (GameState gameState) {
		gs = gameState;
		try {
			socket = new DatagramSocket(Server.BCAST_PORT);
		} catch (IOException e) {
			System.out.println("Error: Can't open port "+Server.BCAST_PORT);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Reads packet data from Broadcast server
	 * 
	 * @return
	 */
	public String readPacket() {
		DatagramPacket packet;
		byte[] buf = new byte[256];
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
			String s = readPacket();
			gs.decode(s);
			System.out.println(s);
		}
	}
	
	public void close() {
		socket.close();
	}
}
