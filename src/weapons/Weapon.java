package weapons;

import server.ServerGameState;
import jig.engine.util.Vector2D;
import world.PlayerObject;

public abstract class Weapon {
	
	protected static int VEL_MAG = 1000;
	protected static int WEAPON_DELAY = 1; // change this for each weapon
	
	protected ServerGameState gs;
	protected PlayerObject player; // the player that this weapon belongs to
	//public GameObject bulletType; // bullet type
	protected double velMag; // velocity magnitude if I use the bullet class this could be in there
	protected long delayMs; // delay between shots
	
	public Weapon(PlayerObject p) {
		player = p;
		// this is now done in the player object
		/*GameObject b = null;
		bullets = new ArrayList<GameObject>(10);
		for (int i = 0; i < 20; i++) {
			b = new GameObject("bullet");
			b.setActivation(false);
			b.set(MASS, FRIC, REST, ROT); 
			b.setForce(new Vector2D(0,ANTI_GRAV)); // don't let gravity affect the bullet, change for grenades
			bullets.add(b);
			gs = ServerGameState.getGameState();
			if (gs != null) { // this is null on the client
				gs.add(b);
			}
		}*/
		delayMs = 0;
	}
	
	public abstract void shoot(Vector2D cursor, long deltaMs);
}
