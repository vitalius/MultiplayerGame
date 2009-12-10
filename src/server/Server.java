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
import net.Action;
import net.NetStateManager;
import net.Protocol;

/**
 * Server
 * 
 */

public class Server extends ScrollingScreenGame {

	private static final int SCREEN_WIDTH = 1280;
	private static final int SCREEN_HEIGHT = 1024;

	/*
	 * This is a static, constant time between frames, all clients run as fast
	 * as the server runs
	 */
	private static int NET_MS = 30;
	private long netMS;

	private NetStateManager netState;
	public NetworkEngine ne;
	private CattoPhysicsEngine pe;
	public ServerGameState gameState;
	private LevelSet levels;
	private LevelMap level;
	private int playerID;
	private PlayerObject playerObject;
	private Action oldInput;

	public LinkedBlockingQueue<String> msgQueue;
	
	private TcpSender tcpSender;

	public Server() {
		super(SCREEN_WIDTH, SCREEN_HEIGHT, false);

		netState = new NetStateManager();
		pe = new CattoPhysicsEngine(new Vector2D(0, 300));
		gameState = new ServerGameState();
		ne = new NetworkEngine(this);
		// pe.setDrawArbiters(true);
		fre.setActivation(true);
		msgQueue = new LinkedBlockingQueue<String>();
		
		netMS = 0;

		tcpSender = new TcpSender();

		// temp resources
		PaintableCanvas.loadDefaultFrames("player", 32, 48, 1,
				JIGSHAPE.RECTANGLE, Color.red);
		PaintableCanvas.loadDefaultFrames("smallbox", 64, 64, 1,
				JIGSHAPE.RECTANGLE, Color.blue);
		PaintableCanvas.loadDefaultFrames("drum", 64, 64, 1,
				JIGSHAPE.RECTANGLE, Color.cyan);
		PaintableCanvas.loadDefaultFrames("playerSpawn", 10, 10, 1,
				JIGSHAPE.CIRCLE, Color.red);
		PaintableCanvas.loadDefaultFrames("bullet", 5, 5, 1,
				JIGSHAPE.RECTANGLE, Color.WHITE);

		// Load all levels, server mode
		levels = new LevelSet("/res/Levelset.txt", true);
		if (levels.getNumLevels() == 0) {
			System.err.println("Error: Levels loading failed.\n");
			System.exit(1);
		}
		// Get specified level.
		level = levels.getThisLevel(1);
		if (level == null) {
			System.err.println("Error: Level wasn't correctly loaded.\n");
			System.exit(1);
		}
		level.buildLevel(gameState);
		
		// Add a player to test movement, remove when not needed
		playerObject = new PlayerObject("player");
		playerObject.set(100, 1.0, 1.0, 0.0);
		Vector2D a = level.playerInitSpots.get(0);
		playerObject.setPosition(new Vector2D(a.getX(), a.getY()));
		playerID = 65001; // bleh
		gameState.addPlayer(playerID, playerObject);
		oldInput = new Action(playerID);

		netState.update(gameState.getNetState());
		
		gameObjectLayers.clear();
		pe.clear();
		gameObjectLayers.add(gameState.getLayer());
		pe.manageViewableSet(gameState.getLayer());
	}

	// this can be removed when the server no longer needs to test player
	// movement
	public void inputHandler(long deltaMs) {
		keyboard.poll();

		// player alive/dead test code.
		if (keyboard.isPressed(KeyEvent.VK_P) && playerID != -1) {
			gameState.removeByID(playerID);
			playerID = -1;

		} else if (keyboard.isPressed(KeyEvent.VK_O) && playerID == -1) {
			playerObject = new PlayerObject("player");
			playerObject.set(100, 1.0, 1.0, 0.0);
			Vector2D a = level.playerInitSpots.get(0);
			playerObject.setPosition(new Vector2D(a.getX(), a.getY()));
			playerID = 65001; // bleh
			gameState.addPlayer(playerID, playerObject);
			oldInput = new Action(playerID);
			netState.update(gameState.getNetState());

		}
		
		if (keyboard.isPressed(KeyEvent.VK_R)) {

	        File f = new File("image.png");
	        BufferedImage bi = new BufferedImage(4250, 1500, BufferedImage.TYPE_INT_RGB);
	        gameframe.getRenderingContext();
	        Graphics2D g2 = bi.createGraphics();
	        for(Box b : gameState.getLayer())
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
			input.shoot = mouse.isLeftButtonPressed();
			input.arg0 = screenToWorld(new Vector2D(mouse.getLocation()
					.getX(), mouse.getLocation().getY()));

			//if (oldInput.equals(input))
			//	return;
			String action = new Protocol().encodeAction(input);
			processAction(action, deltaMs);
			oldInput.copy(input);
		}
	} 

	/**
	 * Joining a new client to the server game
	 * 
	 * @param a Action type of JOIN_REQUEST
	 */
	public void joinClient(Action a) {
		Action response;
		String clientIP = a.getMsg();
		Integer playerID = gameState.getUniqueId(); // Generating a unique ID for a new player
		
		// To refuse connection to the server game
		//response = new Action(0, Action.LEAVE_SERVER, "a");
		//tcpSender.sendSocket(clientIP, netState.prot.encodeAction(response));
		//return;
		
		System.out.println("Adding player ip:" + clientIP + " id:" + playerID);
		
		// Initializing new player 
		PlayerObject player = new PlayerObject("player");
		player.set(100, 1.0, 1.0, 0.0);
		Vector2D spawn = level.playerInitSpots.get(1);
		player.setPosition(new Vector2D(spawn.getX(), spawn.getY()));
		gameState.addPlayer(playerID, player);
		
		// Sending player's ID as a reply to the client 
		response = new Action(0, Action.JOIN_ACCEPT, playerID.toString());
		tcpSender.sendSocket(clientIP, netState.prot.encodeAction(response));
		
		// Add clients IP to the broadcasting list 
		ne.addPlayer(a.getID(), a.getMsg());
	}
	
	/**
	 * Just like client has a "keyboardHandler" method that capture key strokes
	 * and acts on them, this method gets action request from clients and
	 * updates the game server state.
	 * 
	 * TcpServer listens on client requests and stores all the requests into a 
	 * Concurrent safe queue. Then, update() method reads queue content and calls this
	 * method on every message in the queue.
	 * 
	 * @param action
	 *            = encoded action string
	 */
	public void processAction(String action, long deltaMs) {

		Action a = netState.prot.decodeAction(action);

		// Get hashtable of all physics objects currently in the game
		Hashtable<Integer, GameObject> objectList = gameState.getHashtable();

		// If there is no such object ID on the server, simply return and do
		// nothing

		if (!objectList.containsKey(a.getID()) && a.getType() != Action.JOIN_REQUEST)
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

			playerObject.procInput(x, y, a.jet, false, false, a.shoot, a.arg0, gameState.getLayer(), deltaMs);

			break;

		// ///////////////////////////////////////////
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
		}
	}

	public void update(final long deltaMs) {
		super.update(deltaMs);
		pe.applyLawsOfPhysics(deltaMs);
		netMS += deltaMs;
		
		if (netMS > NET_MS) {
			ne.update();
			netMS = 0;
		}
		inputHandler(deltaMs);

		while (msgQueue.size() > 0) {
			this.processAction(msgQueue.poll(), deltaMs);
		}
		gameState.update(deltaMs);
		
		// just for the server player
		Vector2D mousePos = screenToWorld(new Vector2D(mouse.getLocation().getX(), mouse.getLocation().getY()));
		centerOnPoint((int)(playerObject.getCenterPosition().getX()+mousePos.getX())/2, (int)(playerObject.getCenterPosition().getY()+mousePos.getY())/2); // centers on player
	}

	public static void main(String[] vars) {
		Server s = new Server();
		s.run();
	}
}