package client;

import java.awt.event.KeyEvent;

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
	NetObject netPlayer;
	Player player;
	
	BodyLayer<Body> WorldLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> MovableLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> InterfaceLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	
	public Client() {
		
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		PaintableCanvas.loadDefaultFrames("player", 10, 10, 1, JIGSHAPE.CIRCLE, null);
		
		gameState = new GameState();	
		netPlayer = new NetObject(1, new Vector2D(98.999999,100));
		
		gameState.addPlayer(netPlayer);
		
		System.out.println(gameState.encode());
		gameState.decode(gameState.encode());
		
		/* Start thread to sync gameState with server */
		BroadcastListener bListen = new BroadcastListener(gameState);
		bListen.start();
		
		TcpClient control = new TcpClient("127.0.0.1", 5001);
		
		player = new Player("player", new Vector2D(100,100), netPlayer, control);
		MovableLayer.add(player);
		
		gameObjectLayers.add(InterfaceLayer); // add the layer to window.
		gameObjectLayers.add(WorldLayer); // add the layer to window.
		gameObjectLayers.add(MovableLayer); // add the layer to window.
	}
	
	public void update(long deltaMs) {
		super.update(deltaMs);
		
		keyboard.poll();
		
		
		if(keyboard.isPressed(KeyEvent.VK_LEFT)) player.moveLeft();
		if(keyboard.isPressed(KeyEvent.VK_RIGHT)) player.moveRight();
		if(keyboard.isPressed(KeyEvent.VK_UP)) player.moveUp();
		if(keyboard.isPressed(KeyEvent.VK_DOWN)) player.moveDown();

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