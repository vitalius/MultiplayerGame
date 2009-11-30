package clients;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ConcurrentModificationException;

import net.Action;
import net.NetStateManager;

import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.util.Vector2D;

/**
 * Client
 */

public class Client extends StaticScreenGame {

	public static final String SERVER_IP = "127.0.0.1";

	public static final int WORLD_WIDTH = 800, WORLD_HEIGHT = 600;

	boolean keyPressed = false;
	boolean keyReleased = true;

	Action input;

	NetStateManager netStateMan;
	Player player;

	BodyLayer<Body> WorldLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> MovableLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> InterfaceLayer = new AbstractBodyLayer.IterativeUpdate<Body>();

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

		netStateMan = new NetStateManager();
		gameSprites = new GameSprites();

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
		super.update(deltaMs);

		Vector2D playerPos = netStateMan.getState().getHashtable().get(player.getID()).getPosition();
			
		// Adjust offset so player is at center of screen
		Vector2D ajustView = new Vector2D(playerPos.getX() - WORLD_WIDTH / 2,
					playerPos.getY() - WORLD_HEIGHT / 2);

		gameSprites.sync(netStateMan, ajustView);
	
		keyboardMovementHandler();

		shootlimit += deltaMs;
		if (mouse.isLeftButtonPressed() && shootlimit > 250) {
			shootlimit = 0;
			Vector2D shot = new Vector2D(mouse.getLocation().x, mouse.getLocation().y);
			player.shoot(shot);
			//System.out.println("Weapon fire keypress" + mouse.getLocation());
		}
	}

	public void render(RenderingContext rc) {
		super.render(rc);

		try {
			for (Body sprite : gameSprites.getSprites())
				sprite.render(rc);
		} catch (NullPointerException e) {

		} catch (ConcurrentModificationException e2) {

		}

	}

	public static void main(String[] vars) {
		Client c = new Client();
		c.run();
	}
}