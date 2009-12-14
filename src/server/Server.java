package server;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;

import physics.Box;
import physics.CattoPhysicsEngine;
import world.GameObject;
import world.LevelMap;
import world.LevelSet;
import world.PlayerObject;
import jig.engine.PaintableCanvas;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.util.Vector2D;
import match.DeathMatch;
import match.Match;
import net.Action;
import net.NetStateManager;
import net.Protocol;

/**
 * Server
 * 
 */

public class Server extends ScrollingScreenGame {

	private static final int SCREEN_WIDTH = 1280;
	private static final int SCREEN_HEIGHT = 768;

	private static Server server;

	private static int NET_MS = 30;
	private long netMS;

	public NetStateManager netStateMan;
	public NetworkEngine ne;
	private CattoPhysicsEngine pe;
	public ServerGameState gameState;
	private LevelSet levels;
	private LevelMap level;
	private int playerID;
	private PlayerObject playerObject;
	private Action oldInput;
	private Match match;

	public LinkedBlockingQueue<String> msgQueue;

	public TcpSender tcpSender;

	public Server() {
		super(SCREEN_WIDTH, SCREEN_HEIGHT, false);

		server = this;

		netStateMan = new NetStateManager();
		pe = new CattoPhysicsEngine(new Vector2D(0, 300));
		gameState = new ServerGameState();
		ne = new NetworkEngine(this);
		// pe.setDrawArbiters(true);
		fre.setActivation(true);
		msgQueue = new LinkedBlockingQueue<String>();

		netMS = 0;

		tcpSender = new TcpSender();

		// temp resources
		PaintableCanvas.loadDefaultFrames("grenade", 10, 10, 1,
				JIGSHAPE.CIRCLE, Color.GREEN);
		PaintableCanvas.loadDefaultFrames("player", 32, 48, 1,
				JIGSHAPE.RECTANGLE, Color.red);
		PaintableCanvas.loadDefaultFrames("crate", 64, 64, 1,
				JIGSHAPE.RECTANGLE, Color.blue);
		PaintableCanvas.loadDefaultFrames("drum", 64, 64, 1,
				JIGSHAPE.RECTANGLE, Color.cyan);
		PaintableCanvas.loadDefaultFrames("playerSpawn", 10, 10, 1,
				JIGSHAPE.CIRCLE, Color.red);
		PaintableCanvas.loadDefaultFrames("bullet", 5, 5, 1, JIGSHAPE.CIRCLE,
				Color.WHITE);

		// Load all levels, server mode
		levels = new LevelSet("/res/Levelset.txt", true);
		if (levels.getNumLevels() == 0) {
			System.err.println("Error: Levels loading failed.\n");
			System.exit(1);
		}

		// Add a player to test movement, remove when not needed
		playerObject = new PlayerObject("player");
		playerObject.set(100, 1.0, 1.0, 0.0);
		playerID = 65001; // bleh
		gameState.addPlayer(playerID, playerObject);
		oldInput = new Action(playerID);

		netStateMan.update(gameState.getNetState());

		match = new DeathMatch(levels);
		match.loadLevel(1);
		match.startMatch();
		match.addPlayer(playerObject);

		gameObjectLayers.add(gameState.getLayer());
		pe.manageViewableSet(gameState.getLayer());
	}

	public static Server getServer() {
		return server;
	}

	/**
	 * clear gameobject layers and physics layers
	 * 
	 */
	public void clear() {
		gameObjectLayers.clear();
		pe.clear();
	}

	// this can be removed when the server no longer needs to test player
	// movement
	public void inputHandler(long deltaMs) {
		keyboard.poll();
		Vector2D mousePos = screenToWorld(
				new Vector2D(mouse.getLocation().getX(), mouse.getLocation().getY()));

		// player alive/dead test code.
		if (keyboard.isPressed(KeyEvent.VK_P) && playerID != -1) {
			gameState.removeByID(playerID);
			playerID = -1;

		} else if (keyboard.isPressed(KeyEvent.VK_O) && playerID == -1) {
			playerObject = new PlayerObject("player");
			playerObject.set(100, 1.0, 1.0, 0.0);
			Vector2D a = level.playerInitSpots.get(0);
			playerObject.setPosition(new Vector2D(a.getX(), a.getY()));
			playerID = gameState.getUniqueId();
			gameState.addPlayer(playerID, playerObject);
			oldInput = new Action(playerID);
			netStateMan.update(gameState.getNetState());

		}

		if (keyboard.isPressed(KeyEvent.VK_R)) {

			File f = new File("image.png");
			BufferedImage bi = new BufferedImage(4250, 1500,
					BufferedImage.TYPE_INT_RGB);
			gameframe.getRenderingContext();
			Graphics2D g2 = bi.createGraphics();
			for (Box b : gameState.getLayer())
				b.renderImg(g2);
			try {
				ImageIO.write(bi, "png", f);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			System.exit(0);
		}

		// Bother with new input only when player is alive.
		if (playerID != -1) {
			Action input = new Action(playerID, Action.INPUT);
			input.crouch = keyboard.isPressed(KeyEvent.VK_DOWN)
					|| keyboard.isPressed(KeyEvent.VK_S);
			input.jet = keyboard.isPressed(KeyEvent.VK_UP)
					|| keyboard.isPressed(KeyEvent.VK_W);
			input.left = keyboard.isPressed(KeyEvent.VK_LEFT)
					|| keyboard.isPressed(KeyEvent.VK_A);
			input.right = keyboard.isPressed(KeyEvent.VK_RIGHT)
					|| keyboard.isPressed(KeyEvent.VK_D);
			input.jump = keyboard.isPressed(KeyEvent.VK_SPACE);
			

			if (mouse.isLeftButtonPressed()) {
				gameState.playerByID(playerID).shoot(true, mousePos, deltaMs);
			}
			
			
			if (keyboard.isPressed(KeyEvent.VK_1)) {
				input.weapon = 1;
			} else if (keyboard.isPressed(KeyEvent.VK_2)) {
				input.weapon = 2;
			} else if (keyboard.isPressed(KeyEvent.VK_3)) {
				input.weapon = 3;
			} else {
				input.weapon = 0;
			}
			
			if (keyboard.isPressed(KeyEvent.VK_F1)) {
				match.spawnPlayer(gameState.playerByID(playerID), 0);
			} else if (keyboard.isPressed(KeyEvent.VK_F2)) {
				match.spawnPlayer(gameState.playerByID(playerID), 1);
			} else if (keyboard.isPressed(KeyEvent.VK_F3)) {
				match.spawnPlayer(gameState.playerByID(playerID), 2);
			} else if (keyboard.isPressed(KeyEvent.VK_F4)) {
				match.spawnPlayer(gameState.playerByID(playerID), 3);
			}
			
			if (mousePos.getX() < gameState.playerByID(playerID).getCenterPosition().getX())
				input.faceLeft = true;
			else 
				input.faceLeft = false;
			
			String action = new Protocol().encodeAction(input);

			// System.out.println(input.weapon + " server");
			processAction(action, deltaMs);
			oldInput.copy(input);
		}
	}

	/**
	 * Joining a new client to the server game
	 * 
	 * @param a
	 *            Action type of JOIN_REQUEST
	 */
	public void joinClient(Action a) {
		Action response;
		String clientIP = a.getMsg();
		if (match.acceptPlayer()) {
			// Generating a unique ID for a new player
			Integer playerID = gameState.getUniqueId(); 

			System.out.println("Adding player ip:" + clientIP + " id:"
					+ playerID);

			// Initializing new player
			PlayerObject player = new PlayerObject("player");
			player.set(100, 1.0, 1.0, 0.0);
			// Vector2D spawn = level.playerInitSpots.get(1);
			// player.setPosition(new Vector2D(spawn.getX(), spawn.getY()));
			match.addPlayer(player);
			gameState.addPlayer(playerID, player);

			// Sending player's ID as a reply to the client
			response = new Action(0, Action.JOIN_ACCEPT, playerID.toString());
			tcpSender.sendSocket(clientIP, netStateMan.prot
					.encodeAction(response));

			// Add clients IP to the broadcasting list
			ne.addPlayer(playerID, a.getMsg());
		} else {
			// To refuse connection to the server game
			response = new Action(0, Action.LEAVE_SERVER, "a");
			sendMsg(clientIP, netStateMan.prot.encodeAction(response));
			//tcpSender
				//	.sendSocket(clientIP, netStateMan.prot.encodeAction(response));
		}
	}
	
	public void sendMsg(String ip, String ah) {
		tcpSender.sendSocket(ip, ah);
	}

	/**
	 * Just like client has a "keyboardHandler" method that capture key strokes
	 * and acts on them, this method gets action request from clients and
	 * updates the game server state.
	 * 
	 * TcpServer listens on client requests and stores all the requests into a
	 * Concurrent safe queue. Then, update() method reads queue content and
	 * calls this method on every message in the queue.
	 * 
	 * @param action
	 *            = encoded action string
	 */
	public void processAction(String action, long deltaMs) {

		Action a = netStateMan.prot.decodeAction(action);

		// Get hashtable of all physics objects currently in the game
		Hashtable<Integer, GameObject> objectList = gameState.getHashtable();

		// If there is no such object ID on the server, simply return and do
		// nothing

		if (!objectList.containsKey(a.getID())
				&& a.getType() != Action.JOIN_REQUEST)
			return;

		switch (a.getType()) {
		// ////////////////////////////////////////////
		// Handling players input, keystrokes
		case Action.INPUT:

			// System.out.println("up:"+a.up+" down:"+a.down+" left:"+a.left+
			// " right:"+a.right+" jump:"+a.jump);

			PlayerObject playerObject = (PlayerObject) objectList
					.get(a.getID());

			// Cool idea from Rolf for handling input
			int x = 0,
			y = 0;

			if (a.jump)
				--y;
			if (a.crouch)
				++y;
			if (a.left)
				--x;
			if (a.right)
				++x;

			playerObject.procInput(x, y, a.jet, false, false,
					a.weapon, a.spawn, a.faceLeft, gameState.getLayer(), deltaMs);

			break;

		////////////////////////////////////////////////
		// Adding a player
		case Action.JOIN_REQUEST:
			joinClient(a);
			break;
			
		case Action.CHANGE_VELOCITY:
			objectList.get(a.getID()).setVelocity(a.getArg());
			break;
		case Action.CHANGE_POSITION:
			objectList.get(a.getID()).setPosition(a.getArg());
			break;
		case Action.TALK:
			System.out.println("Server got talk: " + a.getMsg());
			break;
			
		///////////////////////////////////////////////////////
		// Request to shoot
		case Action.SHOOT:
			gameState.playerByID(a.getID()).shoot(true, a.getArg(), deltaMs);
			break;
			
		//////////////////////////////////////////////////////////////////////
		// Spawning a dead player
		case Action.SPAWN:
			int spawnSpot = Integer.valueOf(a.getMsg()).intValue();
			match.spawnPlayer(gameState.playerByID(a.getID()), spawnSpot);
			//System.out.println("Server recieved spawn request for Player ID:"+playerID+" loc:"+spawnSpot);
			break;
		}
	}

	public void update(final long deltaMs) {
		super.update(deltaMs);
		pe.applyLawsOfPhysics(deltaMs);

		netMS += deltaMs;
		if (netMS > NET_MS) {
			// System.out.println("server: " + netMS);
			ne.update();
			netMS = 0;
		}

		while (msgQueue.size() > 0) {
			this.processAction(msgQueue.poll(), deltaMs);
		}
		gameState.update(deltaMs);
		match.update();

		// just for the server player
		inputHandler(deltaMs);
		Vector2D mousePos = screenToWorld(new Vector2D(mouse.getLocation()
				.getX(), mouse.getLocation().getY()));
		centerOnPoint((int) (playerObject.getCenterPosition().getX() + mousePos
				.getX()) / 2,
				(int) (playerObject.getCenterPosition().getY() + mousePos
						.getY()) / 2); // centers on player
		//System.out.println("Server update player loc: "
			//	+ playerObject.getCenterPosition());
	}

	public static void main(String[] vars) {
		Server s = new Server();
		s.run();
	}
}