package server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import physics.Arbiter;
import physics.CattoPhysicsEngine;
import world.GameObject;
import world.LevelMap;
import world.LevelSet;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.BodyLayer;
import jig.engine.util.Vector2D;
import net.Action;
import net.NetStateManager;

/**
 * Server
 *
 */

public class Server extends StaticScreenGame{
	
	private static final int WORLD_WIDTH = 800;
	private static final int WORLD_HEIGHT = 600;
	
	/* This is a static, constant time between frames, all clients run as fast as the server runs */
	public static int DELTA_MS = 30;
	
	public NetStateManager netState;
	public NetworkEngine ne;
	public CattoPhysicsEngine pe;
	public ServerGameState gameState;
	public LevelSet levels;
	public LevelMap level;
	public GameObject player;

	public Server(int width, int height, boolean preferFullscreen) {
		super(width, height, preferFullscreen);
		
		netState = new NetStateManager();
		gameState = new ServerGameState();
		ne = new NetworkEngine(this);
		pe = new CattoPhysicsEngine(new Vector2D(0, 100));
		pe.setDrawArbiters(true);
		fre.setActivation(true);
		
		// Some Test Resources
		ResourceFactory factory = ResourceFactory.getFactory();

		BufferedImage[] b = new BufferedImage[1];
		b[0] = new BufferedImage(1600, 10, BufferedImage.TYPE_INT_RGB);
		Graphics g = b[0].getGraphics();
		g.setColor(Color.green);
		g.fillRect(0, 0, 1600, 10);
		g.dispose();
		factory.putFrames("ground", b);

		b = new BufferedImage[1];
		b[0] = new BufferedImage(16, 32, BufferedImage.TYPE_INT_RGB);
		g = b[0].getGraphics();
		g.setColor(Color.red);
		g.fillRect(0, 0, 16, 32);
		g.dispose();
		factory.putFrames("player", b);

		b = new BufferedImage[1];
		b[0] = new BufferedImage(100, 10, BufferedImage.TYPE_INT_RGB);
		g = b[0].getGraphics();
		g.setColor(Color.green);
		g.fillRect(0, 0, 100, 10);
		g.dispose();
		factory.putFrames("platform", b);

		b = new BufferedImage[1];
		b[0] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
		g = b[0].getGraphics();
		g.setColor(Color.blue);
		g.fillRect(0, 0, 32, 32);
		g.dispose();
		factory.putFrames("smallbox", b);
		
		b = new BufferedImage[1];
		b[0] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		g = b[0].getGraphics();
		g.setColor(Color.red);
		g.fillOval(0, 0, 10, 10);
		g.dispose();
		factory.putFrames("playerSpawn", b);
		
		b = new BufferedImage[1];
		b[0] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		g = b[0].getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 10, 10);
		g.dispose();
		factory.putFrames("bullet", b);

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
		level.buildLevel(gameState);
		
		// Add a player to test movement, remove when not needed
		GameObject p;
 		p = new GameObject("player");
 		p.set(100, 1.0, 1.0, 0.0);
 		Vector2D a = level.playerInitSpots.get(0);
 		p.setPosition(new Vector2D(a.getX(), a.getY()));
 		gameState.add(p, GameObject.PLAYER);
 		player = p;
		
		netState.update(gameState.getNetState());
	}
	  	
	 
	 	// this can be removed when the server no longer needs to test player movement
	 	public void keyboardMovementHandler() {
	 		keyboard.poll();
	 		
	 		boolean down =  keyboard.isPressed(KeyEvent.VK_DOWN)  || keyboard.isPressed(KeyEvent.VK_S);
	 		boolean up =    keyboard.isPressed(KeyEvent.VK_UP)    || keyboard.isPressed(KeyEvent.VK_W);
	 		boolean left =  keyboard.isPressed(KeyEvent.VK_LEFT)  || keyboard.isPressed(KeyEvent.VK_A);
	 		boolean right = keyboard.isPressed(KeyEvent.VK_RIGHT) || keyboard.isPressed(KeyEvent.VK_D);
	 		
	 		int x = 0, y = 0;
	 		if(left)   --x;
	 		if(right)  ++x;
	 		if(up)     --y;
	 		if(down)   ++y;
	 		//System.out.println(x + " " +  y);
	 		if(x!=0 || y!=0) player.move(x, y);
	 	}
	
	/**
	 * Just like client has a "keyboardHandler" method that capture key strokes 
	 * and acts on them, this method gets action request from clients
	 * and updates the game server state.
	 * 
	 * TcpServer listens on client requests and calls this method
	 * 
	 * @param action = encoded action string
	 */
	public synchronized void processAction(String action) {
	
		Action a = netState.prot.decodeAction(action);
		
		// Get hashtable of all physics objects currently in the game
		Hashtable<Integer, GameObject> objectList = gameState.getHashtable();
		
		// If there is no such object ID on the server, simply return and do nothing
		if (!objectList.containsKey(a.getId()) && a.getType() != Action.JOIN)
			return;
		
		switch(a.getType()) {			
		//////////////////////////////////////////////
		// Handling players input, keystrokes
		case Action.INPUT:
			//System.out.println("Player id: "+a.getId());
			//System.out.println("up:"+a.up+" down:"+a.down+" left:"+a.left+" right:"+a.right+" jump:"+a.jump);
			
			GameObject playerObject = objectList.get(a.getId());
			
			// Cool idea from Rolf for handling input
			int x = 0, y = 0;
			
			if (a.up)    --y;
			if (a.down)  ++y;
			if (a.left)  --x;
			if (a.right) ++x;
			
			// detect if jumping while on an object
			Arbiter arb;
			BodyLayer<GameObject> layer = gameState.getBoxes();
			GameObject otherObject;
			if (y < 0) { // if jump was pressed
				y = 0;
				for (int i = 0; i < layer.size(); i++) {
					otherObject = layer.get(i);
					if (playerObject.hashCode() == otherObject.hashCode()) break;
					arb = new Arbiter(playerObject, otherObject);
					//System.out.println("playerObject: "+playerObject.getType());
					//System.out.println("otherObject: "+otherObject.getType());
					//System.out.println("num contacts: "+arb.getNumContacts());
					if ( arb.getNumContacts() > 0 ) {
						y = -1;
						break;
					}
	
				}
			}
			
			Vector2D newVelocity = new Vector2D(100*x, 100*y);
			if(x != 0 || y != 0) playerObject.setVelocity(newVelocity);
			
			break;
			
			
		/////////////////////////////////////////////
		// Adding a player
		case Action.JOIN:
			System.out.println("Adding player id:"+a.getId());
			
			ne.addPlayer(a.getId(), a.getMsg());
			GameObject player = new GameObject("player");
			player.set(100, .2, 1.0, 0.0);
			Vector2D spawn = level.playerInitSpots.get(0);
	 		player.setPosition(new Vector2D(spawn.getX(), spawn.getY()));
			gameState.add(a.getId(), player, GameObject.PLAYER);	

			// Reseting physics/render layers
			gameObjectLayers.clear();
			gameObjectLayers.add(gameState.getBoxes());
			pe.clear();
			pe.manageViewableSet(gameState.getBoxes());
			
			break;
			
		case Action.CHANGE_VELOCITY:
			objectList.get(a.getId()).setVelocity(a.getArg());
			break;
		case Action.CHANGE_POSITION:
			objectList.get(a.getId()).setPosition(a.getArg());
			break;

		case Action.SHOOT:
			/*
			System.out.println(a.getId() + " " + a.getArg());
			// add new bullet here.
			Vector2D shootloc = a.getArg();
			GameObject p;
	 		p = new GameObject("bullet");
	 		p.set(100, .2, 1.0, 0.0);// leaving in player stats becuause what should bullets have?.
	 		// set place at player.
	 		Vector2D place = objectList.get(a.getId()).getCenterPosition();
	 		// set velocity.. get mag of vector
	 		double xx = place.getX() - shootloc.getX();
	 		double yy = place.getY() - shootloc.getY();
	 		double mag = Math.sqrt(xx * xx + yy * yy);
	 		shootloc.scale(mag);
	 		// set V
	 		p.setVelocity(shootloc);
	 		// set poosition - away from player a little.
	 		p.setPosition(new Vector2D(place.getX() + 10, place.getY() + 10));

	 		gameState.add(p, GameObject.BULLET);
	 		// hope this works!!
	 		
			// Reseting physics/render layers
	 		
	 		synchronized (pe) {
	 		synchronized(gameObjectLayers) {
	 			gameObjectLayers.clear();
	 			gameObjectLayers.add(gameState.getBoxes());
	 			pe.clear();
	 			pe.manageViewableSet(gameState.getBoxes()); 
	 		}}
			*/
			break;
		}
	}
	
	public void update(final long deltaMs) {
		synchronized (gameState) {
			super.update(deltaMs);
			pe.applyLawsOfPhysics(deltaMs);
			ne.update();
		    gameState.update();
		    keyboardMovementHandler();
		}
	}
	
	@Override
	public void render(final RenderingContext gc) {
		synchronized(gameObjectLayers) {
            super.render(gc);
		    pe.renderPhysicsMarkup(gc);
		}
	}
	
	public static void main (String[] vars) {
		Server s = new Server(WORLD_WIDTH, WORLD_HEIGHT, false);
		
		s.gameObjectLayers.clear();
		s.pe.clear();
		s.gameObjectLayers.add(s.gameState.getBoxes());
		s.pe.manageViewableSet(s.gameState.getBoxes());
		s.run();
		
	}
}