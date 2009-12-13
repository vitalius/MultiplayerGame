package world;

import java.util.ArrayList;

import jig.engine.physics.BodyLayer;
import jig.engine.util.Vector2D;
import physics.Arbiter;
import weapons.GrenadeLauncher;
import weapons.Rifle;
import weapons.Shotgun;
import weapons.Weapon;

public class PlayerObject extends GameObject {

	private static final double JUMPVEL = -300;
	// private static final double RUNVEL = 150;
	// private static final double WALKVEL = 100;
	private static final double WALKFORCE = 400;
	// private static final double CROUCHVEL = 50;
	private static final double JETFORCE = -400;
	private static final double FLOATFORCE = 100;
	private static final double NOFORCE = 0;
	private static final double NOVEL = 0;
	private static final double FRICTION = 1.0;
	public static final int MAXHEALTH = 2000;
	private static final double MAXVEL = 300;
	private static final int MAXJETFUEL = 2000;
	private int health;
	public boolean isFacingRight;

	private int keyLeftRight; // left right key
	private int keyJumpCrouch; // jump key
	private boolean keyJet; // jetpack key
	// private boolean keyCrouch; // crouch key
	// private boolean keyRun; // run toggle
	private boolean keyShoot; // shoot toggle

	private int jetFuel;
	private boolean onObject; // are we standing on an object

	// weapons
	private Weapon activeWeapon;
	private ArrayList<Weapon> weapons;
	
	// spawn point
	private int spawn;

	// statistics
	private int kills;
	private int deaths;

	// Player gfx sprite x,y positons

	// stand x, y, and positions
	static final public int LOC_PLAYER_STAND_X_LEFT = 0;
	static final public int LOC_PLAYER_STAND_Y_LEFT = 0;
	static final public int LOC_PLAYER_STAND_X_RIGHT = 0;
	static final public int LOC_PLAYER_STAND_Y_RIGHT = 1;
	static final public int LOC_PLAYER_STAND_FRAMES = 6;
	static final public int LOC_PLAYER_STAND_REPEAT = 1;

	// Running x, y and length of animation
	static final public int LOC_PLAYER_RUN_X_LEFT = 0;
	static final public int LOC_PLAYER_RUN_Y_LEFT = 5;
	static final public int LOC_PLAYER_RUN_X_RIGHT = 0;
	static final public int LOC_PLAYER_RUN_Y_RIGHT = 6;
	static final public int LOC_PLAYER_RUN_FRAMES = 4;
	static final public int LOC_PLAYER_RUN_REPEAT = 1;

	// jump x, y, and positions
	static final public int LOC_PLAYER_JUMP_X_LEFT = 0;
	static final public int LOC_PLAYER_JUMP_Y_LEFT = 3;
	static final public int LOC_PLAYER_JUMP_X_RIGHT = 0;
	static final public int LOC_PLAYER_JUMP_Y_RIGHT = 4;
	static final public int LOC_PLAYER_JUMP_FRAMES = 6;
	static final public int LOC_PLAYER_JUMP_REPEAT = 0;

	// flying x, y, and positions
	static final public int LOC_PLAYER_FLYING_X_LEFT = 2;
	static final public int LOC_PLAYER_FLYING_Y_LEFT = 3;
	static final public int LOC_PLAYER_FLYING_X_RIGHT = 2;
	static final public int LOC_PLAYER_FLYING_Y_RIGHT = 4;
	static final public int LOC_PLAYER_FLYING_FRAMES = 1;
	static final public int LOC_PLAYER_FLYING_REPEAT = 0;

	// die x, y, and positions
	static final public int LOC_PLAYER_DIE_X_LEFT = 0;
	static final public int LOC_PLAYER_DIE_Y_LEFT = 2;
	static final public int LOC_PLAYER_DIE_X_RIGHT = 3;
	static final public int LOC_PLAYER_DIE_Y_RIGHT = 2;
	static final public int LOC_PLAYER_DIE_FRAMES = 3;
	static final public int LOC_PLAYER_DIE_REPEAT = 0;

	static final public int ROWDOWN = 36;// 36 is length of single row
	static final public int COLORS = 6; // 6 colors.

	// Data concerning frame setting
	// x, y of current animation
	private int frameX = LOC_PLAYER_STAND_X_LEFT;
	private int frameY = LOC_PLAYER_STAND_Y_LEFT;
	// player color
	private int color = 0;
	// current frame in animation ( [0-maxframe) )
	private int animation = 0;
	// aniframes = animation size (EX: 4 frames)
	private int aniframes = LOC_PLAYER_STAND_FRAMES;
	// current frame - (real frame in entire player sheet)
	private int currentframe = 0;
	// enable loop or not.
	private int animationloop = LOC_PLAYER_STAND_REPEAT;

	public PlayerObject(String rsc) {
		super(rsc);
		setType(GameObject.PLAYER);
		keyLeftRight = 0; // left right key
		keyJumpCrouch = 0; // jump key
		// keyCrouch = false; // crouch key
		keyJet = false; // jetpack key
		// keyRun = false; // run toggle
		keyShoot = false; // not shooting
		jetFuel = MAXJETFUEL;
		health = MAXHEALTH;
		onObject = false;
		kills = 0;
		deaths = 0;
		weapons = new ArrayList<Weapon>(0);
		weapons.add(new Rifle(this));
		weapons.add(new Shotgun(this));
		weapons.add(new GrenadeLauncher(this));
		activeWeapon = weapons.get(2);
	}

	/*
	 * public void setKeys(int leftRight, int jumpCrouch, boolean jet, boolean
	 * crouch, boolean run, boolean shoot) { keyLeftRight = leftRight;
	 * keyJumpCrouch = jumpCrouch; //keyCrouch = crouch; keyJet = jet; keyShoot
	 * = shoot; if (run) keyRun = !keyRun; // toggle run }
	 */

	// horizontal movement
	public void leftright(int leftRight) {
		keyLeftRight = leftRight;
		if (keyLeftRight != 0) { // pressed button
			friction = 0;
			if (onObject) { // on object
				// velocity = new Vector2D( WALKVEL*keyLeftRight,
				// velocity.getY() ); // one speed for now
				force = new Vector2D(WALKFORCE * mass * leftRight, force.getY());
			} else { // in air
				force = new Vector2D(FLOATFORCE * mass * leftRight, force
						.getY());
			}
		} else { // released button
			friction = FRICTION;
			if (onObject) { // on object
				// System.out.println("On Object");
				velocity = new Vector2D(0, velocity.getY());
				force = new Vector2D(NOFORCE, force.getY());
			} else { // in air
				// System.out.println("Off Object");
				force = new Vector2D(NOFORCE, force.getY());
			}
		}
	}

	// jump
	public void jumpCrouch(int jumpCrouch) {
		keyJumpCrouch = jumpCrouch;

		if (keyJumpCrouch < 0) {// && jetFuel > 0) { // pressed button
			if (onObject) { // on object
				velocity = new Vector2D(velocity.getX(), JUMPVEL);
			}
		} else if (keyJumpCrouch > 0) { // pressed button {
			// TODO: Crouch
		} else { // released key
			if (velocity.getY() < 0 && !keyJet) { // going up still
				velocity = new Vector2D(velocity.getX(), NOVEL);
			}
		}
	}

	// jetpack
	public void jet(boolean jet) {
		keyJet = jet;
		if (keyJet) { // pressed button
			force = new Vector2D(force.getX(), JETFORCE * mass);
		} else { // released button
			force = new Vector2D(force.getX(), NOFORCE);
		}
	}

	public void shoot(boolean shoot, Vector2D cursor, long deltaMs) {
		keyShoot = shoot;
		if (keyShoot) { // pressed button
			// shoot the weapon
			activeWeapon.shoot(cursor, deltaMs);
		}
	}

	// walking, running and floating
	public void procInput(int leftRight, int jumpCrouch, boolean jet,
			boolean crouch, boolean run, boolean shoot, int weapon, int spawn,
			Vector2D cursor, BodyLayer<GameObject> layer, long deltaMs) {

		if (this.getCenterPosition().getX() > cursor.getX()) {
			this.isFacingRight = false;
		} else {
			this.isFacingRight = true;
		}

		// detect if on an object TODO: static only?
		Arbiter arb;
		GameObject otherObject;
		onObject = false;
		for (int i = 0; i < layer.size(); i++) {
			otherObject = layer.get(i);
			if (this.hashCode() == otherObject.hashCode())
				continue;
			arb = new Arbiter(this, otherObject);
			// System.out.println("playerObject: "+playerObject.getType());
			// System.out.println("otherObject: "+otherObject.getType());
			// System.out.println("num contacts: "+arb.getNumContacts());
			if (arb.getNumContacts() > 0) {
				onObject = true;
				break;
			}
		}

		// setKeys(leftRight, jump, jet, crouch, run);
		if (keyLeftRight != leftRight)
			leftright(leftRight);
		if (keyJumpCrouch != jumpCrouch)
			jumpCrouch(jumpCrouch);
		if (keyJet != jet)
			jet(jet);
		shoot(shoot, cursor, deltaMs);

		if (weapon >= 1 && weapon <= 3) {
			activeWeapon = weapons.get(weapon - 1);
		}
		//System.out.println("PlayerObject procInput spawn")
		if (spawn != 0 && this.health <= 0) {
			this.spawn = spawn;
		}
	}

	public void updatePlayerState(long deltaMs) {
		// System.out.println(jetFuel);
		// Backup run out of fuel if client loses connection.
		if (keyJet && jetFuel > 0) {
			jetFuel = jetFuel - 1;
		} else if (!keyJet && jetFuel < MAXJETFUEL) {
			jetFuel++;
		}
		if (jetFuel <= 0)
			force = new Vector2D(force.getX(), NOFORCE);

		clamp();
		explodeGrenades();
		updateFrame(deltaMs);
		System.out.println("PlayerObject.updatePlayerState loc: " + this.getPosition().toString());
	}

	public void clamp() {
		setRotation(0);
		velocity = velocity.clampX(-MAXVEL, MAXVEL);
		velocity = velocity.clampY(-MAXVEL, MAXVEL);
	}

	public void explodeGrenades() {
		for (Weapon w : weapons) {
			if (w instanceof GrenadeLauncher) {
				((GrenadeLauncher) w).explode();
			}
		}
	}

	int oldhealth = MAXHEALTH;
	int animationControl = 251;

	public void updateFrame(long deltaMs) {

		if (animationControl < 250) {
			animationControl += deltaMs;
			return;
		}

		// store current frame, animation, etc.
		int x = frameX;
		int y = frameY;
		int aniMax = aniframes;
		int loopenabled = animationloop;

		boolean freezeframe = false;

		// Check if health decreased or dead.
		// health decrease should make player use first death frame then reset.
		if (health < oldhealth || health == 0) {
			if (isFacingRight) {
				x = LOC_PLAYER_DIE_X_RIGHT;
				y = LOC_PLAYER_DIE_Y_RIGHT;
			} else {
				x = LOC_PLAYER_DIE_X_LEFT;
				y = LOC_PLAYER_DIE_Y_LEFT;
			}
			aniMax = LOC_PLAYER_DIE_FRAMES;
			loopenabled = LOC_PLAYER_DIE_REPEAT;
			freezeframe = true;
		}

		// jetpack on
		else if (keyJet) {
			if (isFacingRight) {
				x = LOC_PLAYER_FLYING_X_RIGHT;
				y = LOC_PLAYER_FLYING_Y_RIGHT;
			} else {
				x = LOC_PLAYER_FLYING_X_LEFT;
				y = LOC_PLAYER_FLYING_Y_LEFT;
			}
			aniMax = LOC_PLAYER_FLYING_FRAMES;
			loopenabled = LOC_PLAYER_FLYING_REPEAT;
		}

		// jumping
		else if (keyJumpCrouch < 0) {
			if (isFacingRight) {
				x = LOC_PLAYER_JUMP_X_RIGHT;
				y = LOC_PLAYER_JUMP_Y_RIGHT;
			} else {
				x = LOC_PLAYER_JUMP_X_LEFT;
				y = LOC_PLAYER_JUMP_Y_LEFT;
			}
			aniMax = LOC_PLAYER_JUMP_FRAMES;
			loopenabled = LOC_PLAYER_JUMP_REPEAT;
		}
		// running is key pressed
		else if (keyLeftRight != 0) {
			if (isFacingRight) {
				x = LOC_PLAYER_RUN_X_RIGHT;
				y = LOC_PLAYER_RUN_Y_RIGHT;
			} else {
				x = LOC_PLAYER_RUN_X_LEFT;
				y = LOC_PLAYER_RUN_Y_LEFT;
			}
			aniMax = LOC_PLAYER_RUN_FRAMES;
			loopenabled = LOC_PLAYER_RUN_REPEAT;
		}
		// Looks like standing this time. Or last frame of jump if still
		// changing in y axis.
		else {
			if (this.getVelocity().getY() < 0.1
					&& this.getVelocity().getY() > -0.1) {
				if (isFacingRight) {
					x = LOC_PLAYER_STAND_X_RIGHT;
					y = LOC_PLAYER_STAND_Y_RIGHT;
				} else {
					x = LOC_PLAYER_STAND_X_LEFT;
					y = LOC_PLAYER_STAND_Y_LEFT;
				}
				aniMax = LOC_PLAYER_STAND_FRAMES;
				loopenabled = LOC_PLAYER_STAND_REPEAT;
			} else {
				// falling, force to last frame of falling...
				if (isFacingRight) {
					x = LOC_PLAYER_JUMP_X_RIGHT;
					y = LOC_PLAYER_JUMP_Y_RIGHT;
				} else {
					x = LOC_PLAYER_JUMP_X_LEFT;
					y = LOC_PLAYER_JUMP_Y_LEFT;
				}
				aniMax = LOC_PLAYER_JUMP_FRAMES;
				loopenabled = LOC_PLAYER_JUMP_REPEAT;
				animation = LOC_PLAYER_JUMP_FRAMES - 1;
			}

		}

		// okay now set oldhealth = health,
		// then see what we must do in respect of frames.
		oldhealth = health;

		// same frame as before. advance a frame if time is right
		if (x == frameX && y == frameY) {
			// is it time yet? if not wait.
			// advance, if enabled. otherwise stay at last frame.
			animationControl = 0;
			if (animationloop != 0)
				animation = (animation + 1) % aniframes;
			else if (animation != aniframes - 1)
				animation++;
		}
		// else change to first frame of new animation
		else {
			frameX = x;
			frameY = y;
			animation = 0;
			aniframes = aniMax;
			currentframe = 0;
			animationloop = loopenabled;
			// freeze by pausing animation for extra time.
			if (freezeframe)
				animationControl = -250;
			else
				animationControl = 0;
		}

		// Now do final frame calculation.
		currentframe = frameX + frameY * ROWDOWN + 6 * (color) + animation;
		// System.out.println(currentframe + " " + frameX + " " + frameY
		// + " playerobject frame");
		if (currentframe >= 252)
			System.out.println(currentframe
					+ " OVERFLOW FRAME! playerobject frame");
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getKills() {
		return kills;
	}

	public void incKills() {
		this.kills++;
	}

	public void clearKills() {
		this.kills = 0;
	}

	public int getDeaths() {
		return deaths;
	}

	public void incDeaths() {
		this.deaths++;
	}

	public void clearDeaths() {
		this.deaths = 0;
	}

	public int getFrameIndex() {
		return currentframe;
	}

	public void setFrameIndex(int n) {
		currentframe = n;
	}
	
	public void setSpawn(int spawn) {
		this.spawn = spawn;
	}
	
	public int getSpawn() {
		return spawn;
	}
}
