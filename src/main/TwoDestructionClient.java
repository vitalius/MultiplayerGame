package main;

import gameObject.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import jig.engine.FontResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class TwoDestructionClient extends StaticScreenGame {

	class Intro extends VanillaAARectangle {
		Intro(String a) {
			super(a);
			position = new Vector2D(0, 0);
		}

		@Override
		public void update(long deltaMs) {
		}
	}

	static final int WORLD_WIDTH = 600, WORLD_HEIGHT = 600;

	FontResource StatFont;

	BodyLayer<Body> WorldLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> MovableLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
	BodyLayer<Body> InterfaceLayer = new AbstractBodyLayer.IterativeUpdate<Body>();

	int actionCount, actionMode;

	// static final String SPRITE_SHEET = "res/RolfKye.PNG";
	Player player;
	Intro I;
	SolidObject object;

	static final int GAMEINTRO = 0;
	static final int GAMEPRESTART = 1;
	static final int GAMEPLAYING = 2;
	static final int GAMEDEAD = 3;
	static final int GAMEOVER = 4;
	int gameMode = GAMEINTRO;

	public TwoDestructionClient() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		// ResourceFactory.getFactory().loadResources("res",
		// "2Destruction-Resources.xml");
		PaintableCanvas.loadDefaultFrames("box", 128, 128, 1,
				JIGSHAPE.RECTANGLE, null);
		PaintableCanvas.loadDefaultFrames("circle", 128, 128, 1,
				JIGSHAPE.CIRCLE, null);
		PaintableCanvas.loadDefaultFrames("player", 10, 10, 1,
				JIGSHAPE.CIRCLE, null);
		
		object = new SolidObject("circle", new Vector2D(100,100));
		WorldLayer.add(object);

		player = new Player("player", new Vector2D(10,10));
		MovableLayer.add(player);

		I = new Intro("box");
		InterfaceLayer.add(I);

		gameObjectLayers.add(InterfaceLayer); // add the layer to window.
		gameObjectLayers.add(WorldLayer); // add the layer to window.
		gameObjectLayers.add(MovableLayer); // add the layer to window.

		/*
		 * maps = LoadMap.loadFile("/res/RolfKye-DefaultLevelset.kye"); if (maps
		 * == null) { System.err.print("Error maps failed to load.\n");
		 * System.exit(1); }
		 */

		StatFont = ResourceFactory.getFactory().getFontResource(
				new Font("Sans Serif", Font.BOLD, 10), Color.white, null);
	}

	boolean mousedown = false;
	Point clicked;

	@Override
	public void update(long deltaMs) {
		super.update(deltaMs);
		keyboard.poll();
		int Dx = 0, Dy = 0;
		if(keyboard.isPressed(KeyEvent.VK_A) || keyboard.isPressed(KeyEvent.VK_LEFT)) {
			System.out.println("left event keypress");
			// XXX process event
			Dx -= 100;
		}
		if(keyboard.isPressed(KeyEvent.VK_W) || keyboard.isPressed(KeyEvent.VK_UP)) {
			System.out.println("jump event keypress");
			// XXX process event
			Dy -= 100;
		}
		if(keyboard.isPressed(KeyEvent.VK_D) || keyboard.isPressed(KeyEvent.VK_RIGHT)) {
			System.out.println("right event keypress");
			// XXX process event
			Dx += 100;
		}
		if(keyboard.isPressed(KeyEvent.VK_S) || keyboard.isPressed(KeyEvent.VK_DOWN)) {
			System.out.println("crouch event keypress");
			// XXX process event
			Dy += 100;
		}
		player.setVelocity(new Vector2D(Dx, Dy));

		if( keyboard.isPressed(KeyEvent.VK_SHIFT)) {
			System.out.println("Phase event keypress");
			// XXX process event
		}
		if( mouse.isLeftButtonPressed()) {
			System.out.println("Weapon fire keypress" + mouse.getLocation());
			// XXX process event
			clicked = mouse.getLocation();
		}
	}

	public void render(RenderingContext rc) {
		super.render(rc);
	}

	public static void main(String[] args) {
		TwoDestructionClient p = new TwoDestructionClient();
		p.run();
	}
}
