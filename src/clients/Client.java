package clients;

import java.awt.event.KeyEvent;
import java.util.ConcurrentModificationException;

import net.NetStateManager;
import net.NetObject;

import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;


/**
 * Client
 */

public class Client extends StaticScreenGame {
	
	public static final int WORLD_WIDTH = 800, WORLD_HEIGHT = 600;
	
	boolean keyPressed = false;
	boolean keyReleased = true;
	
	NetStateManager gm;
	Player player;

	BodyLayer<Body> WorldLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> MovableLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> InterfaceLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	
	ClientGameState clientGm;

	public Client() {

		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		PaintableCanvas.loadDefaultFrames("player", 30, 40, 1, JIGSHAPE.RECTANGLE, null);
		PaintableCanvas.loadDefaultFrames("ground", 1600, 10, 1, JIGSHAPE.RECTANGLE, null);
		PaintableCanvas.loadDefaultFrames("smallbox", 32, 32, 1, JIGSHAPE.RECTANGLE, null);
		PaintableCanvas.loadDefaultFrames("platform", 100, 10, 1, JIGSHAPE.RECTANGLE, null);
		
		gm = new NetStateManager();
		clientGm = new ClientGameState();

		/* Start thread to sync gameState with server */
		BroadcastListener bListen = new BroadcastListener(gm);
		bListen.start();

		TcpClient control = new TcpClient("127.0.0.1", 5001);
		
		player = new Player(0, control);
	}
	
	/**
	 * Handle keyboard strokes for movement
	 */
	public void keyboardMovementHandler() {
		keyboard.poll();
		
        boolean down = keyboard.isPressed(KeyEvent.VK_DOWN);
        boolean up = keyboard.isPressed(KeyEvent.VK_UP);
		boolean left = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean right = keyboard.isPressed(KeyEvent.VK_RIGHT);
		
		if (left || right || down || up) {
			int x = 0, y = 0;
			if(left) x--;
			if(right) x++;
			if(up) y--;
			if(down) x++;
			player.move(x, y);
		}
		else
			player.move(Player.HALT);
	}

	public void update(long deltaMs) {
		super.update(deltaMs);

		try {
			for (NetObject no : gm.getState().getNetObjects())
				no.update(deltaMs);
			
			clientGm.sync(gm);
		} catch (ConcurrentModificationException e2) {
			
		}
	
		keyboardMovementHandler();
		
		if( mouse.isLeftButtonPressed()) {
			System.out.println("Weapon fire keypress" + mouse.getLocation());
		}
	}

	public void render(RenderingContext rc) {
		super.render(rc);
		
		try {
			for (Body sprite : clientGm.getSprites())
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