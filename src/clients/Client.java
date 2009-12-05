package clients;

import java.awt.Color;
import java.awt.event.KeyEvent;
import physics.Box;
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

	static final String PICTUREBACKGROUND = "res/GameBackground.png";

	public static final String SERVER_IP = "127.0.0.1";

	public static final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 1024;

	boolean keyPressed = false;
	boolean keyReleased = true;

	Action input;

	private NetStateManager netStateMan;
	private Player player;
	private LevelSet levels;
	private LevelMap level;

	private BodyLayer<Body> black = new AbstractBodyLayer.NoUpdate<Body>();
	private BodyLayer<Body> background = new AbstractBodyLayer.IterativeUpdate<Body>();

	GameSprites gameSprites;

	private SyncState state;

	public Client() {

		super(SCREEN_WIDTH, SCREEN_HEIGHT, false);
		
		// save those two lines for later.
        //<imagesrc>2Destruction.PNG</imagesrc>
        //<framesrc>2Destruction-spritesheet.xml</framesrc>
		ResourceFactory.getFactory().loadResources("res",
		"2Destruction-Resources.xml");
		//newgame = new Button(SPRITE_SHEET + "#Start");


		ResourceFactory factory = ResourceFactory.getFactory();

		PaintableCanvas.loadDefaultFrames("player", 32, 48, 1,
				JIGSHAPE.RECTANGLE, Color.red);
		PaintableCanvas.loadDefaultFrames("smallbox", 32, 32, 1,
				JIGSHAPE.RECTANGLE, Color.blue);
		PaintableCanvas.loadDefaultFrames("playerSpawn", 10, 10, 1,
				JIGSHAPE.CIRCLE, Color.red);
		PaintableCanvas.loadDefaultFrames("bullet", 10, 10, 1,
				JIGSHAPE.RECTANGLE, Color.DARK_GRAY);
		PaintableCanvas.loadDefaultFrames("target", 5, 5, 1, JIGSHAPE.CIRCLE,
				Color.red);
		//1280, SCREEN_HEIGHT = 1024
		PaintableCanvas.loadDefaultFrames("blackback", SCREEN_WIDTH, SCREEN_HEIGHT, 1,
				JIGSHAPE.RECTANGLE, Color.black);

		CursorResource cr = factory.makeCursor("target", new Vector2D(0, 0), 1);
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

		TcpClient control = new TcpClient(SERVER_IP, 5001);

		/* Client id is 0 for now, we should make it some random digit */
		player = new Player(0, control);
		input = new Action(0, Action.INPUT);

		gameObjectLayers.clear();

		player.join(SERVER_IP);

		// Create background object and add to layer, to window.
		Box back = new Box("blackback");
		black.add(back);
		back = new Box(PICTUREBACKGROUND + "#background");
		background.add(back);
		// Control of layering - background lowest layer of all.
		gameObjectLayers.add(black);
		gameObjectLayers.add(background);
		gameObjectLayers.add(gameSprites.getLayer());

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

		Vector2D mousePos = screenToWorld(new Vector2D(mouse.getLocation()
				.getX(), mouse.getLocation().getY()));

		String s = state.get();
		if (s != null) {
			netStateMan.sync(s);
		}

		gameSprites.sync(netStateMan);

		// Move background to 90% of cursor world coordite location.
		// as seen from player view
		GameObject p = gameSprites.spriteList.get(player.getID());
		if (p != null && p.getCenterPosition() != null) {
			// System.out.println("p: " + p.getPosition().toString());
			background.get(0).setCenterPosition(
			// Based on cursor pos.
					new Vector2D(.99 * mousePos.getX() / 2,
							.99 * mousePos.getY() / 2));
			// System.out.println("mousePos: " + mousePos.toString());
			// System.out.println("playerPos: " + p.getPosition().toString());
			centerOnPoint(
					(int) (p.getCenterPosition().getX() + mousePos.getX()) / 2,
					(int) (mousePos.getY()) / 2); // centers on player

		}
		keyboardMovementHandler();

		if (shootlimit < 250) {
			shootlimit += deltaMs;
		} else if (mouse.isLeftButtonPressed()) {
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
		// background.render(rc);
	}

	public static void main(String[] vars) {
		Client c = new Client();
		c.run();
	}
}