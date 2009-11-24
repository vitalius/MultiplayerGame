package worldmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.BodyLayer;
import jig.engine.util.Vector2D;
import physics.Box;
import physics.CattoPhysicsEngine;

public class WorldMapTester extends StaticScreenGame {

	private static final int WORLD_WIDTH = 800;
	private static final int WORLD_HEIGHT = 600;
	
	CattoPhysicsEngine physics;
	Box player;
	BodyLayer<Box> boxes;

	LevelSet levels;
	LevelMap level;

	// Will be initially playerless. Press 1-4 to add players in team 1-4.
	public WorldMapTester() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		physics = new CattoPhysicsEngine(new Vector2D(0, 40));
		physics.setDrawArbiters(true);

		fre.setActivation(true);

		ResourceFactory factory = ResourceFactory.getFactory();

		// Create texture for stuff
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

		// Load entire level.
		levels = new LevelSet("/res/Levelset.txt");
		
		// Is there actual level?
		if (levels.getNumLevels() == 0) {
			System.err.println("Error: Levels loading failed.\n");
			System.exit(1);
		}
	}

	public void LevelTest(int levelNum) {

		boxes = new AbstractBodyLayer.NoUpdate<Box>();

		gameObjectLayers.clear();
		physics.clear();

		// Get specified level.
		level = levels.getThisLevel(levelNum);
		// Is there actual level?
		if (level == null) {
			System.err.println("Error: Level wasn't correctly loaded.\n");
			System.exit(1);
		}

		// Build world from level data
		BuildLevel(boxes);

		// Add layer to window.
		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		run();
	}
	
	private void addGround(final BodyLayer<Box> boxes, int X, int Y, double R) {
		Box b;
		b = new Box("ground");
		b.set(Double.MAX_VALUE, .2, 1.0, R);
		b.setPosition(new Vector2D(X, Y));
		boxes.add(b);
		return;
	}

	private void addPlatform(final BodyLayer<Box> boxes, int X, int Y, double R) {
		Box b;
		b = new Box("platform");
		b.set(Double.MAX_VALUE, .2, 1.0, R);
		b.setPosition(new Vector2D(X, Y));
		boxes.add(b);
		return;
	}

	private void addSmallBox(final BodyLayer<Box> boxes, int X, int Y, double R) {
		Box b;
		b = new Box("smallbox");
		b.set(100, .2, 1.0, R);
		b.setPosition(new Vector2D(X, Y));
		boxes.add(b);
		return;
	}

	private void addPlayer(final BodyLayer<Box> boxes, int Team) {
		Box b;
		b = new Box("player");
		b.set(100, .2, 1.0, 0.0);
		Vector2D a = level.playerInitSpots.get(Team);
		b.setPosition(new Vector2D(a.getX(), a.getY()));
		boxes.add(b);
		player = b;
		return;
	}

	// Build world from level data.
	public void BuildLevel(final BodyLayer<Box> boxes) {

		// Used for showing location of spawn spots. (Temp, change to debug only when finished)
		PaintableCanvas.loadDefaultFrames("playerSpawn", 10, 10, 1,
				JIGSHAPE.CIRCLE, Color.red);
		for (int x = 0; x < level.playerInitSpots.size(); x++) {
			TempObj a = new TempObj("playerSpawn");
			a.setPosition(level.playerInitSpots.get(x));
			//System.out.println(a.getPosition());
			boxes.add(a);
		}

		// Create objects based on object type.
		for (int x = 0; x < level.Objects.size(); x++) {
			ObjectData s = level.Objects.get(x);
			//System.out.println(s);
			if (s.type.compareTo("ground") == 0) {
				addGround(boxes, s.x, s.y, s.rotation);
			} else if (s.type.compareTo("platform") == 0) {
				addPlatform(boxes, s.x, s.y, s.rotation);
			} else if (s.type.compareTo("smallbox") == 0) {
				addSmallBox(boxes, s.x, s.y, s.rotation);
			}
		}
	}
	
	int slowdownAdd = 0; // used to slow down spawn speed!
	public void keyboardMovementHandler(final long deltaMs) {
		keyboard.poll();
		
        boolean down = keyboard.isPressed(KeyEvent.VK_DOWN);
        boolean up = keyboard.isPressed(KeyEvent.VK_UP);
		boolean left = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean right = keyboard.isPressed(KeyEvent.VK_RIGHT);
		
		if (left || right || down || up) {
			if(left) player.setVelocity(new Vector2D(-50,0));
			if(right) player.setVelocity(new Vector2D(50,0));
			if(up) player.setVelocity(new Vector2D(0,-50));
			if(down) player.setVelocity(new Vector2D(0,50));
		}
		else
			if(down) player.setVelocity(new Vector2D(0,0));
		
		slowdownAdd += deltaMs;
		if (keyboard.isPressed(KeyEvent.VK_1)) {
			//if (slowdownAdd > 500) {
				slowdownAdd = 0;
				addPlayer(boxes, 0);
			//}
		}
		if (keyboard.isPressed(KeyEvent.VK_2)) {
			//if (slowdownAdd > 500) {
				slowdownAdd = 0;
				addPlayer(boxes, 1);
			//}
		}
		if (keyboard.isPressed(KeyEvent.VK_3)) {
			//if (slowdownAdd > 500) {
				slowdownAdd = 0;
				addPlayer(boxes, 2);
			//}
		}
		if (keyboard.isPressed(KeyEvent.VK_4)) {
			//if (slowdownAdd > 500) {
				slowdownAdd = 0;
				addPlayer(boxes, 3);
			//}
		}
	}

	@Override
	public void update(final long deltaMs) {
		super.update(deltaMs);
		physics.applyLawsOfPhysics(deltaMs);
		keyboardMovementHandler(deltaMs);
	}

	@Override
	public void render(final RenderingContext gc) {
		super.render(gc);
		physics.renderPhysicsMarkup(gc);
	}

	public static void main(String[] args) {
		WorldMapTester p = new WorldMapTester();
		p.LevelTest(0);// first level for now.
	}
}
