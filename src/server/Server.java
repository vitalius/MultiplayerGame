package server;

import java.util.Random;

import jig.engine.util.Vector2D;


import net.GameState;
import net.GameStateManager;
import net.NetObject;

/**
 * Server
 * 
 * @author Vitaliy
 *
 */

public class Server {
	
	/* This is a static, constant time between frames, all clients run as fast as the server runs */
	public static int DELTA_MS = 30;
	
	public static void main (String[] vars) {
		GameStateManager gm = new GameStateManager();
		NetworkEngine ne = new NetworkEngine(gm);
		
		/* Basic box, size of clients window screen to test networking */
		CollisionDetection cd = new CollisionDetection();
		
		/* Build few objects with random velocities for test */
		Random r = new Random(System.currentTimeMillis());
		GameState c = new GameState();
		for (int i = 0; i < 10; i++) {
			c.add(new NetObject(i, 
					new Vector2D(300,300), 
					NetObject.PLAYER, 
					new Vector2D(r.nextDouble()-0.5,r.nextDouble()-0.5)));
		}
		gm.update(c);
		/* end of network test building */
		
		for (;;) {
			ne.update();
			
			for(NetObject n : gm.getState().getNetObjects()) {
				cd.checkBox(n);
				n.update(DELTA_MS);
			}
			
			// Limit FPS to 200
			try {
				Thread.sleep(DELTA_MS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}