package server;

import net.GameState;
import net.NetObject;

import jig.engine.util.Vector2D;

/**
 * Server
 * 
 * @author vitaliy
 *
 */

public class Server {
	
	public static final int BCAST_PORT = 5000;
	public static final int TCP_PORT   = 5001;
	
	public static String client_IP = "127.0.0.1";
	
	public static void main (String[] vars) {
		GameState gameState = new GameState();
		NetObject player0 = new NetObject(1, new Vector2D(100,100));		
		gameState.addPlayer(player0);
		
		Broadcaster bcaster = new Broadcaster(BCAST_PORT, gameState);
		bcaster.start();
		bcaster.addIP(client_IP);
		
		TcpServer controlServer = new TcpServer(5001, gameState);
		controlServer.run();
	}
}