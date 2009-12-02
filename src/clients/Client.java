package clients;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;

import physics.Box;

import world.PlayerObject;
import worldmap.TempObj;

import net.Action;
import net.NetObject;
import net.NetState;
import net.NetStateManager;

import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;

/**
 * Client
 */

public class Client extends ScrollingScreenGame {

	public static final String SERVER_IP = "127.0.0.1";

	public static final int WORLD_WIDTH = 800, WORLD_HEIGHT = 600;

	boolean keyPressed = false;
	boolean keyReleased = true;

	Action input;

	NetStateManager netStateMan;
	Player player;

	private BodyLayer<Body> background = new AbstractBodyLayer.IterativeUpdate<Body>();
	// BodyLayer<Body> MovableLayer = new
	// AbstractBodyLayer.IterativeUpdate<Body>();
	// BodyLayer<Body> InterfaceLayer = new
	// AbstractBodyLayer.IterativeUpdate<Body>();

	GameSprites gameSprites;

	public Client() {

		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		PaintableCanvas.loadDefaultFrames("player", 16, 32, 1,
				JIGSHAPE.RECTANGLE, Color.red);
		PaintableCanvas.loadDefaultFrames("ground", 1600, 10, 1,
				JIGSHAPE.RECTANGLE, Color.green);
		PaintableCanvas.loadDefaultFrames("smallbox", 32, 32, 1,
				JIGSHAPE.RECTANGLE, Color.blue);
		PaintableCanvas.loadDefaultFrames("platform", 100, 10, 1,
				JIGSHAPE.RECTANGLE, Color.green);
		PaintableCanvas.loadDefaultFrames("playerSpawn", 10, 10, 1,
				JIGSHAPE.CIRCLE, Color.red);
		PaintableCanvas.loadDefaultFrames("bullet", 10, 10, 1,
				JIGSHAPE.RECTANGLE, Color.black);

		// background. made small so we can debug its motions.
		PaintableCanvas.loadDefaultFrames("background", 100, 100, 1,
				JIGSHAPE.RECTANGLE, Color.gray);

		netStateMan = new NetStateManager();
		gameSprites = new GameSprites();

		/* Start thread to sync gameState with server */
		BroadcastListener bListen = new BroadcastListener(netStateMan);
		bListen.start();

		TcpClient control = new TcpClient(SERVER_IP, 5001);

		/* Client id is 0 for now, we should make it some random digit */
		player = new Player(0, control);
		input = new Action(0, Action.INPUT);

		// Create background object and add to layer, to window.
		Box back = new Box("background");
		background.add(back);
		gameObjectLayers.add(background);

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
		Vector2D a = null;

		gameSprites.sync(netStateMan);

		// Move background to 10% of player position.
		// actually -10% because we want motions to be realistic.
		VanillaSphere p = gameSprites.spriteList.get(player.getID());
		if (p != null && p.getPosition() != null) {
			a = p.getPosition();
			background.get(0).setCenterPosition(
					new Vector2D(a.getX() * -.1 + WORLD_WIDTH / 2, a.getY()
							* -.1 + WORLD_HEIGHT / 2));
			// System.out.println("p: " + p.getPosition().toString());
			centerOn(p);
		}
		keyboardMovementHandler();

		if (shootlimit < 250) {
			shootlimit += deltaMs;
		} else if (mouse.isLeftButtonPressed()) {
			shootlimit = 0;
			// Since we know player is always generally in center of screen...
			// Adjust click location into world location.
			Vector2D shot = new Vector2D(mouse.getLocation().x
					- (WORLD_WIDTH / 2), mouse.getLocation().y
					- (WORLD_HEIGHT / 2));
			shot = shot.unitVector();
			player.shoot(shot);
			// System.out.println("Weapon fire keypress" + mouse.getLocation());
		}
		super.update(deltaMs);

	}

	public void render(RenderingContext rc) {
		super.render(rc);
		background.render(rc);

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
