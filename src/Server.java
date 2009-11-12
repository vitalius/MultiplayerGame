import jig.engine.util.Vector2D;

/**
 * Server
 * 
 * @author vitaliy
 *
 */

public class Server {
	
	public static final int BCAST_PORT = 5000;
	
	public static String client_IP = "127.0.0.1";
	
	public static void main (String[] vars) {
				
		Broadcaster bcaster = new Broadcaster(BCAST_PORT);
		bcaster.start();
		bcaster.addIP(client_IP);
			
		GameState gameState = new GameState();
		Player player0 = new Player(1, new Vector2D(100,100));		
		gameState.addPlayer(player0);
		bcaster.setBcastString(gameState.encode());
	}
}