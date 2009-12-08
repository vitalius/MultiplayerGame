package clients;

import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import physics.Box;
import server.NetworkEngine;
import world.GameObject;
import world.LevelMap;
import world.LevelSet;
import net.Action;
import net.NetStateManager;
import net.SyncState;
import jig.engine.CursorResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.util.Vector2D;

/**
 * Client
 */
public class Client extends ScrollingScreenGame {

	// used for testing UI elements.
	private class uiItem extends Body {

		// int slowdowntest = 0;
		// int counter = 0;

		public uiItem(String imgrsc) {
			super(imgrsc);
		}

		@Override
		public void update(long deltaMs) {
			// if (slowdowntest < 1000) {
			// slowdowntest += deltaMs;
			// } else {
			// slowdowntest = 0;
			// counter += 1;
			// counter = counter % this.getFrameCount();
			// this.setFrame(counter);
			// }
		}
	}

	static final String PICTUREBACKGROUND = "res/GameBackground.png";
	static final String UIGFX = "res/ClientUI.png";

	public String SERVER_IP = "127.0.0.1";

	public static final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 1024;
	private static final int MAXJETFUEL = 2000;

	boolean keyPressed = false;
	boolean keyReleased = true;

	Action input;

	private NetStateManager netStateMan;
	private Player player;
	private LevelSet levels;
	private LevelMap level;

	private uiItem jetpack, health;

	private BodyLayer<Body> black = new AbstractBodyLayer.NoUpdate<Body>();
	private BodyLayer<Body> background = new AbstractBodyLayer.IterativeUpdate<Body>();
	private BodyLayer<uiItem> GUI = new AbstractBodyLayer.IterativeUpdate<uiItem>();

	GameSprites gameSprites;

	private SyncState state;

	private LinkedBlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>();

	private int jetFuel = MAXJETFUEL;

	public Client() {

		super(SCREEN_WIDTH, SCREEN_HEIGHT, false);

		// save those two lines for later.
		// <imagesrc>2Destruction.PNG</imagesrc>
		// <framesrc>2Destruction-spritesheet.xml</framesrc>
		ResourceFactory.getFactory().loadResources("res",
				"2Destruction-Resources.xml");
		// newgame = new Button(SPRITE_SHEET + "#Start");
	}

	public void runSetup() {
		ResourceFactory factory = ResourceFactory.getFactory();

		PaintableCanvas.loadDefaultFrames("player", 32, 48, 1,
				JIGSHAPE.RECTANGLE, Color.red);
		PaintableCanvas.loadDefaultFrames("smallbox", 32, 32, 1,
				JIGSHAPE.RECTANGLE, Color.blue);
		PaintableCanvas.loadDefaultFrames("playerSpawn", 10, 10, 1,
				JIGSHAPE.CIRCLE, Color.red);
		PaintableCanvas.loadDefaultFrames("bullet", 5, 5, 1,
				JIGSHAPE.RECTANGLE, Color.WHITE);
		// 1280, SCREEN_HEIGHT = 1024
		PaintableCanvas.loadDefaultFrames("blackback", SCREEN_WIDTH,
				SCREEN_HEIGHT, 1, JIGSHAPE.RECTANGLE, Color.black);

		// will be mostly transparent when done with adding gfx stuff.
		CursorResource cr = factory.makeCursor(UIGFX + "#Cursor", new Vector2D(
				15, 15), 1);
		mouse.setCursor(cr);

		netStateMan = new NetStateManager();
		gameSprites = new GameSprites();
		fre.setActivation(true);

		// Load entire level.
		levels = new LevelSet("/res/Levelset.txt");
		// Is there actual level?
		if (levels.getNumLevels() == 0) {
			System.err.println("Error: Levels loading failed.\n");
			System.exit(1);
		}
		// Get specified level.
		level = levels.getThisLevel(1);
		// Is there actual level?
		if (level == null) {
			System.err.println("Error: Level wasn't correctly loaded.\n");
			System.exit(1);
		}
		// Build world from level data
		level.buildLevelClient(gameSprites);

		// stateQueue = new LinkedBlockingQueue<String>(1);
		state = new SyncState();

		/* Start thread to sync gameState with server */
		BroadcastListener bListen = new BroadcastListener(state);
		bListen.start();

		/* Thread with TCP networking for server specific commands */
		TcpListener tcpListen = new TcpListener(NetworkEngine.TCP_CLIENT_PORT,
				msgQueue);
		tcpListen.start();

		/* Send TCP data via this object */
		TcpSender control = new TcpSender(SERVER_IP, NetworkEngine.TCP_PORT);

		/* Client id is 0 for now, we should make it some random digit */
		Random rand = new Random(); // this should actually come from the server
		player = new Player(rand.nextInt(), control);
		input = new Action(player.getID(), Action.INPUT);
		player.join(SERVER_IP);

		// Create background object and add to layer, to window.
		gameObjectLayers.clear();
		Box back = new Box("blackback");
		black.add(back);
		// background graphic.
		back = new Box(PICTUREBACKGROUND + "#background");
		background.add(back);
		// ui elements
		jetpack = new uiItem(UIGFX + "#JetFuel");
		jetpack.setPosition(new Vector2D(20, 20));
		GUI.add(jetpack);
		health = new uiItem(UIGFX + "#Health");
		health.setPosition(new Vector2D(20, 31));
		GUI.add(health);
		// Control of layering - background lowest layer of all.
		gameObjectLayers.add(GUI);// last forced render will draw it topmost on
		// screen coordities.
		gameObjectLayers.add(black);
		gameObjectLayers.add(background);
		gameObjectLayers.add(gameSprites.getLayer());

	}

	/**
	 * Handle keyboard strokes for movement
	 */
	public void keyboardMovementHandler(long deltaMs) {
		keyboard.poll();

		if (netStateMan.getState().objectList.get(player.getID()).getHealth() > 0) {
			input.crouch = keyboard.isPressed(KeyEvent.VK_DOWN)
					|| keyboard.isPressed(KeyEvent.VK_S);
			GameObject p = gameSprites.spriteList.get(player.getID());
			if (jetFuel > 0
					&& (keyboard.isPressed(KeyEvent.VK_UP) || keyboard
							.isPressed(KeyEvent.VK_W))) {
				input.jet = true;
				jetFuel -= 2;
			} else {
				input.jet = false;
			}
			if (!(keyboard.isPressed(KeyEvent.VK_UP) || keyboard
					.isPressed(KeyEvent.VK_W)) && jetFuel < MAXJETFUEL)
				++jetFuel;

			input.left = keyboard.isPressed(KeyEvent.VK_LEFT)
					|| keyboard.isPressed(KeyEvent.VK_A);
			input.right = keyboard.isPressed(KeyEvent.VK_RIGHT)
					|| keyboard.isPressed(KeyEvent.VK_D);
			input.jump = keyboard.isPressed(KeyEvent.VK_SPACE);
			player.move(input);
		} else {
			input.crouch = false;
			input.jet = false;
			input.left = false;
			input.right = false;
			input.jump = false;
			player.move(input);
		}

	}

	int shootlimit = 0;

	public void update(long deltaMs) {
		super.update(deltaMs);

		Vector2D mousePos = screenToWorld(new Vector2D(mouse.getLocation()
				.getX(), mouse.getLocation().getY()));

		// get messages from the server
		String s = state.get();
		if (s != null)
			netStateMan.sync(s);
		gameSprites.sync(netStateMan);


		// Move background to 90% of cursor world coordite location.
		// as seen from player view
		GameObject p = gameSprites.spriteList.get(player.getID());
		if (p != null && p.getCenterPosition() != null) {
			// System.out.println("p: " + p.getPosition().toString());
			background.get(0).setCenterPosition(
			// Based on cursor pos.
					// new Vector2D(.99 * mousePos.getX() / 2,
					// .99 * mousePos.getY() / 2));
					new Vector2D(0 + .2 * p.getCenterPosition().getX() / 2,
							0 + .2 * p.getCenterPosition().getY() / 2));
			// System.out.println("mousePos: " + mousePos.toString());
			// System.out.println("playerPos: " + p.getPosition().toString());
			centerOnPoint(
					(int) (p.getCenterPosition().getX() + mousePos.getX()) / 2,
					(int) (mousePos.getY()) / 2);

			// it is assumed that health is in range [0-2000].
			// System.out.println(hl);
			int hl = netStateMan.getState().objectList.get(player.getID())
					.getHealth();
			if (hl > 0) {
				int hframe = 25 - (int) ((((double) hl) / 2000.0) * 25);
				//System.out.println(hframe + " hframe, client");
				health.setFrame(hframe);
			} else {
				health.setFrame(25);
			}
			if (jetFuel > 0) {
				int jframe = 25 - (int) ((((double) jetFuel) / 2000.0) * 25);
				//System.out.println(jframe + " jframe " + jetFuel + "jetfuel, client");
				jetpack.setFrame(jframe);
			} else {
				jetpack.setFrame(25);
			}
		}
		keyboardMovementHandler(deltaMs);

		if (shootlimit < 250) {
			shootlimit += deltaMs;
		} else if (p != null && mouse.isLeftButtonPressed() && netStateMan.getState().objectList.get(player.getID())
				.getHealth() > 0) {
			if (p.getCenterPosition() != null) {
				shootlimit = 0;
				// Since we know player is always generally in center of
				// screen...
				// Get shoot vector and normalize it.
				Vector2D shot = new Vector2D(mousePos.getX()
						- p.getCenterPosition().getX(), mousePos.getY()
						- p.getCenterPosition().getY());
				shot = shot.unitVector();
				player.shoot(shot);
			}
			// System.out.println("Weapon fire keypress" + mouse.getLocation());
		}
	}

	public void render(RenderingContext rc) {
		black.render(rc);// draw at screen coordities.
		super.render(rc);
		GUI.render(rc);
		// background.render(rc);
	}

	public static void main(String[] vars) {
		Client c = new Client();
		int as = 0;
		// unfinished yet
		while (as == 0) {
			String s = JOptionPane
					.showInputDialog("Enter server IP address or empty if want 127.0.0.1");
			if (s.compareTo("") == 0)
				s = "127.0.0.1";
			String[] a = s.split("\\.");
			if (a.length == 4) {
				int a1 = java.lang.Integer.parseInt(a[0]);
				int a2 = java.lang.Integer.parseInt(a[1]);
				int a3 = java.lang.Integer.parseInt(a[2]);
				int a4 = java.lang.Integer.parseInt(a[3]);
				System.out.println(a1 + "." + a2 + "." + a3 + "." + a4);
				// check formatting if its properly done.
				if (a1 > 0 && a1 < 254 && a2 >= 0 && a2 <= 254 && a3 >= 0
						&& a3 <= 254 && a4 > 0 && a4 <= 254) {
					String res = String.valueOf(a1 + "." + a2 + "." + a3 + "."
							+ a4);
					c.SERVER_IP = res;// definitely correctly formatted.
					as = 1;
				}
			}
			if (as == 0)
				JOptionPane
						.showMessageDialog(
								null,
								"Misformatted IP address "
										+ s
										+ "\n\nNeed to be X.X.X.X with X in range [0-254].");
		}

		c.runSetup();
		c.run();
	}
}