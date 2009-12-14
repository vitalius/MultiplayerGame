package weapons;

import net.Action;
import server.ServerGameState;
import world.GameObject;
import world.PlayerObject;
import jig.engine.util.Vector2D;

public class Shotgun extends Weapon {

	protected static int VEL_MAG = 1000;
	protected static int WEAPON_DELAY = 750;
	protected static int BULLET_NUM = 8;
	protected static int SPREAD = 4; // angle between bullets

	public Shotgun(PlayerObject p) {
		super(p);
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
			// System.out.println("rifle.shoot player: " +
			// player.getCenterPosition().toString() + " shoot right: " +
			// shootLoc.toString());
			
		} else {
			shootLoc = new Vector2D(player.getCenterPosition().getX()
					- player.getWidth() * .7, // bullet size is 5 pixels thats
												// why .7
					player.getCenterPosition().getY() - player.getHeight()
							* .25);
			// System.out.println("rifle.shoot player: " +
			// player.getCenterPosition().toString() + " shoot left: " +
			// shootLoc.toString());
		}
		
		// get velocity
		Vector2D shootVec = new Vector2D(cursor.getX()
				- player.getCenterPosition().getX(), cursor.getY()
				- player.getCenterPosition().getY()).unitVector();

		// set the spread
		shootVec = shootVec.rotate(-Math.toRadians(SPREAD*(BULLET_NUM-1)/2));
		for (int i = 0; i < BULLET_NUM; i++) {
			GameObject bullet = player.bullets.remove(0);// get from oldest one.
			bullet.setActivation(true);
			bullet.setPosition(shootLoc);
			bullet.setVelocity(shootVec.scale(VEL_MAG));
			bullet.owner = player;
			player.bullets.add(bullet); // add it to the start of the list
			shootVec = shootVec.rotate(Math.toRadians(SPREAD));
		}
		
		// add the sound
		ServerGameState.getGameState().getNetState().addAction(new Action(ServerGameState.getGameState().getUniqueId(),Action.SHOTGUNSFX,shootLoc));
	}
}
