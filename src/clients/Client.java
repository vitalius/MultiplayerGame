package clients;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ConcurrentModificationException;
import world.GameObject;
import world.LevelMap;
import world.LevelSet;
import net.Action;
import net.NetStateManager;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

/**
 * Client
 */

public class Client extends ScrollingScreenGame {

	public static final String SERVER_IP = "127.0.0.1";

	public static final int SCREEN_WIDTH = 1600, SCREEN_HEIGHT = 1000;

	boolean keyPressed = false;
	boolean keyReleased = true;

	Action input;

	private NetStateManager netStateMan;
	private Player player;
	private LevelSet levels;
	private LevelMap level;

	// private BodyLayer<Body> layer = new AbstractBodyLayer.NoUpdate<Body>();
	// BodyLayer<Body> MovableLayer = new
	// AbstractBodyLayer.IterativeUpdate<Body>();
	// BodyLayer<Body> InterfaceLayer = new
	// AbstractBodyLayer.IterativeUpdate<Body>();

	GameSprites gameSprites;

	public Client() {

		super(SCREEN_WIDTH, SCREEN_HEIGHT, false);

		PaintableCanvas.loadDefaultFrames("player", 64, 96, 1,
				JIGSHAPE.RECTANGLE, Color.red);
		PaintableCanvas.loadDefaultFrames("smallbox", 32, 32, 1,
				JIGSHAPE.RECTANGLE, Color.blue);
		PaintableCanvas.loadDefaultFrames("playerSpawn", 10, 10, 1,
				JIGSHAPE.CIRCLE, Color.red);
		PaintableCanvas.loadDefaultFrames("bullet", 10, 10, 1,
				JIGSHAPE.RECTANGLE, Color.black);

		netStateMan = new NetStateManager();
		gameSprites = new GameSprites();
		
		// Load entire level.
		levels = new LevelSet("/res/Levelset.txt");
		// Is there actual level?
		if (levels.getNumLevels() == 0) {
			System.err.println("Error: Levels loading failed.\n");
			System.exit(1);
		}
		// Get specified level.
		level = levels.getThisLevel(0);
		// Is there actual level?
		if (level == null) {
			System.err.println("Error: Level wasn't correctly loaded.\n");
			System.exit(1);
		}
		// Build world from level data
		level.buildLevelClient(gameSprites);

		/* Start thread to sync gameState with server */
		BroadcastListener bListen = new BroadcastListener(netStateMan);
		bListen.start();

		TcpClient control = new TcpClient(SERVER_IP, 5001);

		/* Client id is 0 for now, we should make it some random digit */
		player = new Player(0, control);
		input = new Action(0, Action.INPUT);

		player.join(SERVER_IP);
	}

	/**
	 * Handle keyboard strokes for movement
	 */
	public void keyboardMovementHandler() {
		keyboard.poll();

		input.crouch = keyboard.isPressed(KeyEvent.VK_DOWN)
				|| keyboard.isPressed(KeyEvent.VK_S);
		input.jet = keyboard.isPressed(KeyEvent.VK_UP)
				|| keyboard.isPressed(KeyEvent.VK_W);
		input.left = keyboard.isPressed(KeyEvent.VK_LEFT)
				|| keyboard.isPressed(KeyEvent.VK_A);
		input.right = keyboard.isPressed(KeyEvent.VK_RIGHT)
				|| keyboard.isPressed(KeyEvent.VK_D);
		input.jump = keyboard.isPressed(KeyEvent.VK_SPACE);

		player.move(input);
	}

	int shootlimit = 0;

	public void update(long deltaMs) {

		gameSprites.sync(netStateMan);

		GameObject p = gameSprites.spriteList.get(player.getID());
		if (p != null && p.getPosition() != null) {
			// System.out.println("p: " + p.getPosition().toString());
			centerOn(p);
		}
		keyboardMovementHandler();

		if (shootlimit < 250) {
			shootlimit += deltaMs;
		} else if (mouse.isLeftButtonPressed() && shootlimit > 250) {
			shootlimit = 0;
			// Since we know player is always generally in center of screen...
			// Adjust click location into world location.
			Vector2D shot = new Vector2D(mouse.getLocation().x
					- (SCREEN_WIDTH / 2), mouse.getLocation().y
					- (SCREEN_HEIGHT / 2));
			shot = shot.unitVector();
			player.shoot(shot);
			// System.out.println("Weapon fire keypress" + mouse.getLocation());
		}
		super.update(deltaMs);

	}

	public void render(RenderingContext rc) {
		super.render(rc);

		AffineTransform at = this.getScreenToWorldTransform();
		try {
			at.invert();
		} catch (NoninvertibleTransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		rc.setTransform(at); // I would prefer to use a AbstractBodyLayer to
								// hold the objects to make this easy
		try {
			for (Body sprite : gameSprites.getSprites())
				sprite.render(rc);
		} catch (NullPointerException e) {

		} catch (ConcurrentModificationException e2) {

		}

	}

	public static void main(String[] vars) {
		Client c = new Client();
		c.gameObjectLayers.clear();
		c.gameObjectLayers.add(c.gameSprites.getLayer());
		c.run();
	}
}
