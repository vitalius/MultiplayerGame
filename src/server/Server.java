package server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import physics.CattoPhysicsEngine;
import world.LevelMap;
import world.LevelSet;
import jig.engine.GameClock;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.hli.StaticScreenGame;
import jig.engine.util.Vector2D;
import net.NetStateManager;

/**
 * Server
 * 
 * @author Vitaliy
 *
 */

public class Server extends StaticScreenGame{
	
	private static final int WORLD_WIDTH = 800;
	private static final int WORLD_HEIGHT = 600;
	
	/* This is a static, constant time between frames, all clients run as fast as the server runs */
	public static int DELTA_MS = 30;
	
	private NetStateManager netState;
	private NetworkEngine ne;
	private CattoPhysicsEngine pe;
	private ServerGameState gameState;
	private LevelSet levels;
	private LevelMap level;
	
	public Server(int width, int height, boolean preferFullscreen) {
		super(width, height, preferFullscreen);
		
		netState = new NetStateManager();
		gameState = new ServerGameState();
		ne = new NetworkEngine(netState, gameState);
		pe = new CattoPhysicsEngine(new Vector2D(0, 40));
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
		g.fillRect(0, 0, 30, 40);
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
		
		netState.update(gameState.getNetState());
	}
	
	public void update(final long deltaMs) {
		super.update(deltaMs);
		pe.applyLawsOfPhysics(deltaMs);
		//keyboardMovementHandler(deltaMs);
	}
	
	@Override
	public void render(final RenderingContext gc) {
		super.render(gc);
		pe.renderPhysicsMarkup(gc);
	}
	
	public void run() {		
		beforeRunning();
		
		// do two updates to warm up the JVM
		// ...then establish the global time
		gameLoop(0);
		gameLoop(0);
		
		theClock.begin();
		
		running = true;
		long partialMs = 0;
		long deltaTime;
		long deltaMs;
		while (running && !gameframe.isExitAndCloseRequested()) {
			theClock.tick();
			// if we're running really fast, then each frame may complete
			// in a very small fraction of a second (less than 1 ms).
			// in this case, we've got a bit of a problem, since the naive
			// approach would simply convert deltaTime to deltaMs by dividing
			// by NANOS_PER_MS.  That, would case deltaMS to always equal 0.
			// So instead, we'll keep track of the remainder and add that the
			// next time through.  This way we're not loosing those 'partial' 
			// milliseconds.
			deltaTime = theClock.getDeltaGameTime() + partialMs;
			deltaMs = deltaTime / GameClock.NANOS_PER_MS;
			partialMs = deltaTime % GameClock.NANOS_PER_MS;
			gameLoop(deltaMs);	
			
			ne.update();
			gameState.update();
			
			// Limit FPS to 200
			try {
				Thread.sleep(DELTA_MS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		gameframe.closeAndExit();
	}
	
	private void gameLoop(long deltaMs)
	{
		//render
		RenderingContext rc = gameframe.getRenderingContext();
		render(rc);
		gameframe.displayBackBuffer();
		gameframe.clearBackBuffer();

		//update
		update(deltaMs);	
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