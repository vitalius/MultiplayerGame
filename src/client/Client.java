package client;

import java.awt.event.KeyEvent;
import java.util.Vector;

import net.GameState;
import net.NetObject;

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
 * 
 * @author Vitaliy
 *
 */

public class Client extends StaticScreenGame {
	
	static final int WORLD_WIDTH = 600, WORLD_HEIGHT = 600;
	
	GameState gameState;
	Vector<NetObject> netPlayers;
	//NetObject netPlayer1;
	Player self;
	Vector<Player> players;
	//Player player1;
	
	BodyLayer<Body> WorldLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> MovableLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> InterfaceLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	
	public Client() {
		
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		PaintableCanvas.loadDefaultFrames("player", 10, 10, 1, JIGSHAPE.CIRCLE, null);
		
		// CONNECT server
		// request join
		// get list of players
		// OR handle DENY event
		
		gameState = new GameState();
		
		netPlayers = new Vector<NetObject>();
		players = new Vector<Player>();
		
		// loop to load all players, including this player
		netPlayers.add( new NetObject(1, new Vector2D(98.999999,100)));
		netPlayers.add( new NetObject(2, new Vector2D(98.999999,100)));
		//netPlayer0 = new NetObject(1, new Vector2D(98.999999,100));
		//netPlayer1 = new NetObject(2, new Vector2D(98.999999,100));
		
		// loop add all players
		for(int x = 0; x < netPlayers.size(); x++)
			gameState.addPlayer(netPlayers.elementAt(x));
		
		System.out.println(gameState.encode());
		gameState.decode(gameState.encode());
		
		/* Start thread to sync gameState with server */
		BroadcastListener bListen = new BroadcastListener(gameState);
		bListen.start();
		
		TcpClient control = new TcpClient("127.0.0.1", 5001);
		//TcpClient control = new TcpClient("10.97.53.76", 5001);
		
		// loop and add all players
		for(int x = 0; x < netPlayers.size(); x++) {
			players.add( new Player("player", new Vector2D(100,100), netPlayers.elementAt(x), control));
			MovableLayer.add(players.elementAt(x));

		}
		
		// for now assume first one is self...
		self = players.elementAt(0);
		//player1 = new Player("player", new Vector2D(100,100), netPlayer1, control);

		//MovableLayer.add(player0);
		//MovableLayer.add(player1);
		
		gameObjectLayers.add(InterfaceLayer); // add the layer to window.
		gameObjectLayers.add(WorldLayer); // add the layer to window.
		gameObjectLayers.add(MovableLayer); // add the layer to window.
	}
	
	public void update(long deltaMs) {
		super.update(deltaMs);
		
		keyboard.poll();
		
		
		if(keyboard.isPressed(KeyEvent.VK_LEFT)) self.moveLeft();
		if(keyboard.isPressed(KeyEvent.VK_RIGHT)) self.moveRight();
		if(keyboard.isPressed(KeyEvent.VK_UP)) self.moveUp();
		if(keyboard.isPressed(KeyEvent.VK_DOWN)) self.moveDown();

		if( mouse.isLeftButtonPressed()) {
			System.out.println("Weapon fire keypress" + mouse.getLocation());
		}		
	}
	
	public void render(RenderingContext rc) {
		super.render(rc);
	}
	
	public static void main (String[] vars) {		
		Client c = new Client();
		c.run();
	}
}