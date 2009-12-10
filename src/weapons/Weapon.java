package weapons;

import java.util.ArrayList;

import server.ServerGameState;
import jig.engine.util.Vector2D;
import world.GameObject;
import world.PlayerObject;

public abstract class Weapon {
	
	protected static int MASS = 10, FRIC = 1, REST = 1, ROT = 0;
	protected static int ANTI_GRAV = -300;
	protected static int VEL_MAG = 1000;
	protected static int WEAPON_DELAY = 1; // change this for each weapon
	
	protected PlayerObject player; // the player that this weapon belongs to
	//public GameObject bulletType; // bullet type
	protected ArrayList<GameObject> bullets; // reusable bullets
	protected double velMag; // velocity magnitude if I use the bullet class this could be in there
	protected long delayMs; // delay between shots
	
	public Weapon(PlayerObject p) {
		player = p;
		GameObject b = null;
		bullets = new ArrayList<GameObject>(10);
		for (int i = 0; i < 10; i++) {
			b = new GameObject("bullet");
			b.setActivation(false);
			b.set(MASS, FRIC, REST, ROT); 
			b.setForce(new Vector2D(0,ANTI_GRAV)); // don't let gravity affect the bullet, change for grenades
			bullets.add(b);
			ServerGameState.getGameState().add(b);
		}
		delayMs = 0;
	}
	
	public abstract void shoot(Vector2D cursor, long deltaMs);
}
