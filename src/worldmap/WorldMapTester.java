package worldmap;

import java.awt.Color;
import java.awt.Graphics;
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
	String boxResource, plankResource;

	public WorldMapTester() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		physics = new CattoPhysicsEngine(new Vector2D(0, 40));
		physics.setDrawArbiters(true);

		fre.setActivation(true);

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
		
	}
	
	public void LevelTest() {
		
		Box b;
		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		gameObjectLayers.clear();
		physics.clear();

		addGround(boxes);
		addPlatforms(boxes);
		addSmallBoxes(boxes);
		b = new Box("player");
		b.set(400, .2, 1.0);
		b.setPosition(new Vector2D(392, 0));
		boxes.add(b);

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);
		
		run();
	}
	
	private void addGround(final BodyLayer<Box> boxes) {
		Box b;
		b = new Box("ground");
		b.set(Double.MAX_VALUE, .2, 1.0);
		b.setPosition(new Vector2D(0,WORLD_HEIGHT-b.getHeight()));
		boxes.add(b);
		return;
	}
	
	private void addPlatforms(final BodyLayer<Box> boxes) {
		Box b;
		b = new Box("platform");
		b.set(Double.MAX_VALUE, .2, 1.0);
		b.setPosition(new Vector2D(100, 500));
		boxes.add(b);
		b = new Box("platform");
		b.set(Double.MAX_VALUE, .2, 1.0);
		b.setPosition(new Vector2D(350, 500));
		boxes.add(b);
		b = new Box("platform");
		b.set(Double.MAX_VALUE, .2, 1.0);
		b.setPosition(new Vector2D(600, 500));
		boxes.add(b);
		b = new Box("platform");
		b.set(Double.MAX_VALUE, .2, 1.0);
		b.setPosition(new Vector2D(350, 300));
		boxes.add(b);
		return;
	}
	
	private void addSmallBoxes(final BodyLayer<Box> boxes) {
		Box b;
		b = new Box("smallbox");
		b.set(100, .2, 1.0);
		b.setPosition(new Vector2D(150, 450));
		boxes.add(b);
		b = new Box("smallbox");
		b.set(100, .2, 1.0);
		b.setPosition(new Vector2D(400, 450));
		boxes.add(b);
		b = new Box("smallbox");
		b.set(100, .2, 1.0);
		b.setPosition(new Vector2D(650, 450));
		boxes.add(b);
		b = new Box("smallbox");
		b.set(100, .2, 1.0);
		b.setPosition(new Vector2D(400, 250));
		boxes.add(b);
		return;
	}
	
	public void BuildLevel() {
		
		PaintableCanvas.loadDefaultFrames("player", 10, 10, 1, JIGSHAPE.CIRCLE,
				Color.red);
		LevelSet levels = new LevelSet("/res/Levelset.txt");

		if (levels.getNumLevels() == 0) {
			System.err.println("Error: Level loading failed.\n");
			System.exit(1);
		}

		BodyLayer<Box> WorldLayer = new AbstractBodyLayer.NoUpdate<Box>();
		

		gameObjectLayers.clear();
		physics.clear();

		LevelMap level = levels.getThisLevel(0);

		System.out.println(level.playerInitSpots.size());

		for (int x = 0; x < level.playerInitSpots.size(); x++) {
			TempObj a = new TempObj("player");
			a.setPosition(level.playerInitSpots.get(x));
			System.out.println(a.getPosition());
			WorldLayer.add(a);
		}

		System.out.println(level.Objects.size());

		for (int x = 0; x < level.Objects.size(); x++) {
			ObjectData s = level.Objects.get(x);
			System.out.println(s);
			PaintableCanvas.loadDefaultFrames("objectbox" + x, s.width, s.height, 1,
					JIGSHAPE.RECTANGLE, Color.black);
			TempObj a = new TempObj("objectbox" + x);
			System.out.println(s.x + " " + s.y);
			a.setPosition(new Vector2D(s.x, s.y));
			WorldLayer.add(a);
		}

		for (int x = 0; x < level.MovableObjects.size(); x++) {
			ObjectData s = level.MovableObjects.get(x);
			System.out.println(s);
			PaintableCanvas.loadDefaultFrames("objectmovebox" + x, s.width, s.height, 1,
					JIGSHAPE.RECTANGLE, Color.blue);
			TempObj a = new TempObj("objectmovebox" + x);
			System.out.println(s.x + " " + s.y);
			a.setPosition(new Vector2D(s.x, s.y));
			WorldLayer.add(a);
		}

		gameObjectLayers.add(WorldLayer);// add the layer to window.
		physics.manageViewableSet(WorldLayer);
		run();
	}

	@Override
	public void update(final long deltaMs) {
		super.update(deltaMs);
		physics.applyLawsOfPhysics(deltaMs);
	}

	@Override
	public void render(final RenderingContext gc) {
		super.render(gc);
		physics.renderPhysicsMarkup(gc);
	}

	public static void main(String[] args) {
		WorldMapTester p = new WorldMapTester();
		p.LevelTest();
	}
}
