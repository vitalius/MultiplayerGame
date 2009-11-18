package worldmap;

import java.awt.Color;

import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.ecpe.CattoPhysicsEngine;
import jig.engine.util.Vector2D;

public class WorldMapTester extends StaticScreenGame {

	private static final int WORLD_WIDTH = 800;
	private static final int WORLD_HEIGHT = 500;
	
	CattoPhysicsEngine physics;

	public WorldMapTester() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		
		physics = new CattoPhysicsEngine(new Vector2D(0, 40));
		physics.setDrawArbiters(true);
		
		
		
		PaintableCanvas.loadDefaultFrames("player", 10, 10, 1, JIGSHAPE.CIRCLE,
				Color.red);
		LevelSet levels = new LevelSet("/res/Levelset.txt");

		if (levels.getNumLevels() == 0) {
			System.err.println("Error: Level loading failed.\n");
			System.exit(1);
		}

		BodyLayer<Body> WorldLayer = new AbstractBodyLayer.IterativeUpdate<Body>();

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

	}

	@Override
	public void update(long deltaMs) {
	}

	public void render(RenderingContext rc) {
		super.render(rc);
	}

	public static void main(String[] args) {
		WorldMapTester p = new WorldMapTester();
		p.run();
	}
}
