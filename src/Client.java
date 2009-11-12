import jig.engine.PaintableCanvas;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.StaticScreenGame;
import jig.engine.util.Vector2D;

/**
 * Client
 * 
 * @author Vitaliy
 *
 */

public class Client extends StaticScreenGame {
	
	static final int WORLD_WIDTH = 600, WORLD_HEIGHT = 600;
	
	public Client() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		PaintableCanvas.loadDefaultFrames("player", 10, 10, 1, JIGSHAPE.CIRCLE, null);
		
		GameState gameState = new GameState();	
		Player player0 = new Player(1, new Vector2D(100,100));
		
		gameState.addPlayer(player0);
		
		System.out.println(gameState.encode());
		gameState.decode(gameState.encode());
		
		/* Start thread reading current game state */
		NetIO netIO = new NetIO(gameState);
		netIO.start();
	}
	
	public void update(long deltaMs) {
		
	}
	
	public static void main (String[] vars) {		
		Client c = new Client();
		c.run();
	}
}