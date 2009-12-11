package weapons;

import server.ServerGameState;
import world.GameObject;
import world.PlayerObject;
import jig.engine.util.Vector2D;

public class GrenadeLauncher extends Weapon {

	protected static int VEL_MAG = 600;
	protected static int BUL_VEL_MAG = 1000;
	protected static int WEAPON_DELAY = 3000;
	//protected static int EXPLODE_DELAY = 3000; // use weapon delay for this 
	protected static int BULLET_NUM = 10;
	protected static int SPREAD = 36; // angle between bullets
	protected GameObject grenade;

	public GrenadeLauncher(PlayerObject p) {
		super(p);
		grenade = new GameObject("grenade");
		grenade.setActivation(false);
		grenade.set(20, 1, 1, 0);
		gs = ServerGameState.getGameState();
		if (gs != null) { // this is null on the client
			//System.out.println("Added Grenade");
			gs.add(grenade);
		}
	}

	@Override
	public void shoot(Vector2D cursor, long deltaMs) {
		if (ServerGameState.getGameState().totalMs < delayMs) { 
			return;
		}
		delayMs = ServerGameState.getGameState().totalMs + WEAPON_DELAY;

		// get starting location
		Vector2D shootLoc = null;
		if (player.isFacingRight) {
			shootLoc = new Vector2D(player.getCenterPosition().getX()
					+ player.getWidth() * .6, // bullet size is 5 pixels that's
												// why .6
					player.getCenterPosition().getY() - player.getHeight()
							* .25);
			
		} else {
			shootLoc = new Vector2D(player.getCenterPosition().getX()
					- player.getWidth() * .7, // bullet size is 5 pixels thats
												// why .7
					player.getCenterPosition().getY() - player.getHeight()
							* .25);
		}
		
		// get velocity
		Vector2D shootVec = new Vector2D(cursor.getX()
				- player.getCenterPosition().getX(), cursor.getY()
				- player.getCenterPosition().getY()).unitVector();
		
		// shoot the grenade
		grenade.setActivation(true);
		grenade.setPosition(shootLoc);
		grenade.setVelocity(shootVec.scale(VEL_MAG));
		//System.out.println("Shot Grenade");
	}
	
	public void explode() {
		if (ServerGameState.getGameState().totalMs < delayMs || 
				!grenade.isActive()) { 
			return;
		}
		grenade.setActivation(false);
		// explode
		Vector2D shootLoc = grenade.getCenterPosition();
		Vector2D shootVec = new Vector2D(1,0);
		for (int i = 0; i < BULLET_NUM; i++) {
			GameObject bullet = bullets.remove(0);// get from oldest one.
			bullet.setActivation(true);
			bullet.setPosition(shootLoc);
			bullet.setVelocity(shootVec.scale(BUL_VEL_MAG));
			bullets.add(bullet); // add it to the start of the list
			shootVec = shootVec.rotate(Math.toRadians(SPREAD));
		}
	}
}
