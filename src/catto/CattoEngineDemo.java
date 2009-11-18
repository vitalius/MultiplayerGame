package jig.demos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import jig.engine.GameFrame;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.ecpe.Box;
import jig.engine.physics.ecpe.CattoPhysicsEngine;
import jig.engine.physics.ecpe.Joint;
import jig.engine.util.Vector2D;

/**
 * A test of the Erin Catto Physics Engine.
 * 
 * @author Scott Wallace
 * 
 */
public class CattoEngineDemo extends StaticScreenGame {
	static final long NANOS_PER_SECOND = 1000000000;

	static final int WIDTH = 800;

	static final int HEIGHT = 600;

	long timeToQuit;

	long runTime;

	boolean abort = false;

	CattoPhysicsEngine physics;

	String boxResource, plankResource;
	
	boolean demoDone = false;

	/**
	 * Demonstrates the Catto Physics Engine through a series of simulations.
	 * 
	 */
	CattoEngineDemo() {
		super(WIDTH, HEIGHT, false);

		physics = new CattoPhysicsEngine(new Vector2D(0, 40));
		physics.setDrawArbiters(true);

		boxResource = "jig/demos/demo-spritesheet.png#small-box";
		plankResource = "jig/demos/demo-spritesheet.png#small-plank";

		fre.setActivation(true);

		ResourceFactory factory = ResourceFactory.getFactory();
		
		factory.loadResources("jig/demos", "demo-resources.xml");
		// Below we make a series of images to represent the ground
		// and planks and simple boxes. These images are then passed
		// to the factory where they are converted to the appropriate
		// internal representation and stored as singletons with
		// the specifed resource name...
		BufferedImage[] b = new BufferedImage[1];
		b[0] = new BufferedImage(600, 40, BufferedImage.TYPE_INT_RGB);
		Graphics g = b[0].getGraphics();
		g.setColor(Color.blue);
		g.fillRect(0, 0, 640, 40);
		g.dispose();
		factory.putFrames("local://ground", b);

		b = new BufferedImage[1];
		b[0] = new BufferedImage(300, 10, BufferedImage.TYPE_INT_RGB);
		g = b[0].getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 300, 10);
		g.dispose();
		factory.putFrames("local://longplank", b);

		b = new BufferedImage[1];
		b[0] = new BufferedImage(50, 10, BufferedImage.TYPE_INT_RGB);
		g = b[0].getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 50, 10);
		g.dispose();
		factory.putFrames("local://shortplank", b);

		b = new BufferedImage[1];
		b[0] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
		g = b[0].getGraphics();
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, 32, 32);
		g.dispose();
		factory.putFrames("local://bigbox", b);
		
		//only exit if it was the user
		gameframe.registerExitHandler(new CattoExitHandler(this));

	}

	/**
	 * Creates two stacks of blocks in a pryamid shape. Demonstrates stacking
	 * and stability of multiple collisons.
	 * 
	 * @param seconds
	 *            the number of seconds to run the simulation
	 */
	public void pyramidTest(final int seconds) {

		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		gameObjectLayers.clear();
		physics.clear();

		makeGround(boxes);
		makePyramid(boxes, 10, 200, 0);
		makePyramid(boxes, 10, 500, 0);
		makeUI();

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		timeToQuit = seconds * 1000;
		runTime = 0;
		run();

	}

	/**
	 * Creates a teeter-totter with two 'light' weight blocks on one side and a
	 * 'heavy' brick which falls on the other.
	 * 
	 * @param seconds
	 *            the number of seconds to run the simulation
	 */
	public void teeterTest(final int seconds) {

		Box b;
		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		gameObjectLayers.clear();
		physics.clear();

		makeGround(boxes);

		b = new Box("local://longplank");
		b.set(100.0, .2, 1.0);
		b.setCenterPosition(new Vector2D(300, 540));
		boxes.add(b);

		Joint j = new Joint(b, boxes.get(0), new Vector2D(300, 540), .2);
		physics.add(j);

		b = new Box(boxResource);
		b.set(100, .2, 1.0);
		b.setCenterPosition(new Vector2D(160, 500));
		boxes.add(b);
		b = new Box(boxResource);
		b.set(100, .2, 1.0);
		b.setCenterPosition(new Vector2D(180, 500));
		boxes.add(b);

		b = new Box(boxResource);
		b.set(200, .2, 1.0);
		b.setCenterPosition(new Vector2D(430, 200));
		boxes.add(b);

		makeUI();

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		timeToQuit = seconds * 1000;
		runTime = 0;
		run();

	}

	/**
	 * Drops a single block onto the static ground.
	 * 
	 * @param seconds
	 *            the number of seconds to run the simulation
	 */
	public void singleBoxTest(final int seconds) {

		Box b;
		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		gameObjectLayers.clear();
		physics.clear();

		makeGround(boxes);
		b = new Box(boxResource);
		b.set(400, .2, 1.0);
		b.setPosition(new Vector2D(392, 0));
		boxes.add(b);
		makeUI();

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		timeToQuit = seconds * 1000;
		runTime = 0;
		run();
		//System.out.println("Rendering Context: " + gameframe.getRenderingContext());
	}

	/**
	 * Drops a series of blocks on to on another. Demonstrates the stability of
	 * stacked objects.
	 * 
	 * @param seconds
	 *            the number of seconds to run the simulation
	 */
	public void verticalStackTest(final int seconds) {
		Box b;
		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		gameObjectLayers.clear();
		physics.clear();

		makeGround(boxes);
		for (int i = 0; i < 10; i++) {
			b = new Box(boxResource);
			// increase the surface friction of the boxes somewhat
			b.set(400, .4, 1.0);
			b.setCenterPosition(new Vector2D(400, 540 - i * 25));
			boxes.add(b);
		}

		makeUI();

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		timeToQuit = seconds * 1000;
		runTime = 0;
		run();
	}

	/**
	 * Creates a stack of blocks on a tilted surface. The coffeficient of
	 * friction and the angle of the surface are very close to what would be
	 * required to keep the stack stable.
	 * 
	 * @param seconds
	 *            the number of seconds to run the simulation
	 */
	public void tiltedStackTest(final int seconds) {
		Box b;
		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		gameObjectLayers.clear();
		physics.clear();

		makeLongPlank(boxes, new Vector2D(300, 500), .1);
		for (int i = 0; i < 10; i++) {
			b = new Box(boxResource);
			// increase the surface friction *A LOT* these are small boxes
			b.set(400, .6, 1.0);
			b.setCenterPosition(new Vector2D(300, 475 - i * 25));
			boxes.add(b);
		}

		makeUI();

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		timeToQuit = seconds * 1000;
		runTime = 0;
		run();
	}

	/**
	 * A series of tileted platforms and blocks each with different coefficients
	 * of friction. The blocks slide down the platforms at different rates as a
	 * result.
	 * 
	 * @param seconds
	 *            the number of seconds to run the simuation
	 */
	public void frictionTest(final int seconds) {
		Box b;
		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		gameObjectLayers.clear();
		physics.clear();

		makeGround(boxes);
		makeLongPlank(boxes, new Vector2D(400, 100), .25);
		makeLongPlank(boxes, new Vector2D(450, 250), -.25);
		makeLongPlank(boxes, new Vector2D(400, 400), .25);
		makeShortPlank(boxes, new Vector2D(580, 140));
		makeShortPlank(boxes, new Vector2D(270, 290));
		makeShortPlank(boxes, new Vector2D(580, 440));

		for (int i = 0; i < 5; i++) {
			b = new Box(boxResource);
			b.set(400, i * .05, 1.0);
			b.setCenterPosition(new Vector2D(350 - i * 5, 20 - i * 20));
			boxes.add(b);
		}

		makeUI();

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		timeToQuit = seconds * 1000;
		runTime = 0;
		run();

	}

	/**
	 * A single joint use to anchor a block and turn it into a pendulum. A
	 * simple demonstration of the Joint class.
	 * 
	 * @param seconds
	 *            the number of seconds to run the simulation
	 */
	public void pendulumTest(final int seconds) {
		Box b;
		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		gameObjectLayers.clear();
		physics.clear();

		makeGround(boxes);

		b = new Box(boxResource);
		b.set(400, .2, 1.0);
		b.setCenterPosition(new Vector2D(500, 200));
		boxes.add(b);

		Joint j = new Joint(b, boxes.get(0), new Vector2D(400, 200), .2);
		physics.add(j);

		makeUI();

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		timeToQuit = seconds * 1000;
		runTime = 0;
		run();

	}

	/**
	 * A complex test involving multiple boxes, rotation, and joints.
	 * 
	 * @param seconds
	 *            the number of seconds to run the simulation
	 */
	public void dominosTest(final int seconds) {
		Box b1, b2, teeter, support;
		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		int i;
		Joint j;

		gameObjectLayers.clear();
		physics.clear();

		makeGround(boxes);
		makeLongPlank(boxes, new Vector2D(300, 150), 0);
		makeLongPlank(boxes, new Vector2D(380, 250), -.3);
		makeShortPlank(boxes, new Vector2D(200, 280));

		for (i = 0; i < 10; i++) {
			b2 = new Box(plankResource);
			b2.set(50, .2, 1.0);
			b2.setCenterPosition(new Vector2D(200 + i * 25, 129));
			b2.setRotation(Math.PI / 2);
			boxes.add(b2);
		}

		b2 = new Box(boxResource);
		b2.set(400, .2, 1.0);
		b2.setCenterPosition(new Vector2D(100, 80));
		b2.setVelocity(new Vector2D(30, -30));
		boxes.add(b2);

		j = new Joint(boxes.get(0), b2, new Vector2D(150, 80), .2);
		physics.add(j);

		teeter = new Box("local://longplank");
		teeter.set(100.0, .2, 1.0);
		teeter.setCenterPosition(new Vector2D(370, 390));
		boxes.add(teeter);
		support = makeLongPlank(boxes, new Vector2D(370, 420), 0);

		j = new Joint(teeter, support, new Vector2D(340, 390), .2);
		physics.add(j);

		b1 = new Box("local://bigbox");
		b1.set(300, .4, 1.0);
		b1.setCenterPosition(new Vector2D(530, 350));
		b1.setRotation(0.0);
		boxes.add(b1);

		b2 = new Box(plankResource);
		b2.set(20, .20, 1.0);
		b2.setCenterPosition(new Vector2D(530, 330));
		boxes.add(b2);

		j = new Joint(support, b1, b1.getCenterPosition(), 1.0);
		physics.add(j);

		j = new Joint(b2, b1, b2.getCenterPosition().translate(
				new Vector2D(16, 2)), .10);
		physics.add(j);

		makeUI();

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		timeToQuit = seconds * 1000;
		runTime = 0;
		run();
	}

	/**
	 * A series of long skinny boxes held together with joints to form a bridge.
	 * This demonstrates the dynamic nature of the joints and their use for
	 * constraining multiple objects to make realistic forms.
	 * 
	 * @param seconds
	 *            the number of seconds to run the simulation
	 */
	public void bridgeTest(final int seconds) {
		Box b1, b2, grnd;
		BodyLayer<Box> boxes = new AbstractBodyLayer.NoUpdate<Box>();

		int i, nPlanks = 16;
		int x = 60, y = 300;
		float relax = .8f;
		Joint j;

		gameObjectLayers.clear();
		physics.clear();

		for (i = 0; i < nPlanks; i++) {
			b2 = new Box(plankResource);
			b2.set(50, .2, 1.0);
			b2.setCenterPosition(new Vector2D(x + 40 + i * 40, y));
			boxes.add(b2);
		}
		for (i = 1; i < nPlanks; i++) {
			b1 = boxes.get(i - 1);
			b2 = boxes.get(i);
			j = new Joint(b1, b2, new Vector2D(
					(b1.getCenterPosition().getX() + b2.getCenterPosition()
							.getX()) / 2, b2.getCenterPosition().getY()), relax);
			physics.add(j);
		}
		makeGround(boxes);

		b1 = boxes.get(0);
		b2 = boxes.get(nPlanks - 1);
		grnd = boxes.get(nPlanks);

		j = new Joint(grnd, b1, b1.getCenterPosition().translate(
				new Vector2D(-30, 0)), relax);
		physics.add(j);

		j = new Joint(grnd, b2, b2.getCenterPosition().translate(
				new Vector2D(30, 0)), relax);
		physics.add(j);

		b2 = new Box(boxResource);
		b2.set(400, .2, 1.0);
		b2.setCenterPosition(new Vector2D(-30, 0));
		b2.setVelocity(new Vector2D(30, -30));
		boxes.add(b2);

		makeUI();

		gameObjectLayers.add(boxes);
		physics.manageViewableSet(boxes);

		timeToQuit = seconds * 1000;
		runTime = 0;
		run();
	}

	/**
	 * Makes the UI layer, and adds the frame rate element to it.
	 */
	private void makeUI() {
		// BodyLayer<Box> ui =
		//		new AbstractBodyLayer.NoUpdate<Box>();

		// FrameRateElement fre = new FrameRateElement(
		// new Vector2D(HEIGHT - 150, WIDTH - 40));
		// fre.initializeHistogram(70, 2);
		// ui.add(fre);
		// gameObjectLayers.add(ui);
	}

	/**
	 * Makes a long box to represent the ground and adds it to the specified box
	 * layer.
	 * 
	 * @param boxes
	 *            the layer holding all the boxes
	 * @return the box representing the ground
	 */
	private Box makeGround(final BodyLayer<Box> boxes) {
		Box b;
		b = new Box("local://ground");
		b.set(Double.MAX_VALUE, .2, 1.0);
		b.setCenterPosition(new Vector2D(400, 580));
		boxes.add(b);
		return b;
	}

	/**
	 * Makes a long box to represent a plank and adds it to the specified box
	 * layer.
	 * 
	 * @param boxes
	 *            the layer holding all the boxes
	 * @param p
	 *            the position of the plank
	 * @param r
	 *            the initial rotation of the plank in radians
	 * @return the box representing the plank
	 */
	private Box makeLongPlank(final BodyLayer<Box> boxes, final Vector2D p,
			final double r) {

		Box b;
		b = new Box("local://longplank");
		b.set(Double.MAX_VALUE, .2, 1.0);
		b.setCenterPosition(p);
		b.setRotation(r);
		boxes.add(b);
		return b;
	}

	/**
	 * Makes a short rectangular box to represent a plank and is suitable for a
	 * bridge. Then adds it to the specified box layer.
	 * 
	 * @param boxes
	 *            the layer holding all the boxes
	 * @param p
	 *            the position of the plank
	 * @return the box representing the plank
	 */
	private Box makeShortPlank(final BodyLayer<Box> boxes, final Vector2D p) {

		Box b;
		b = new Box("local://shortplank");
		b.set(Double.MAX_VALUE, .2, 1.0);
		b.setCenterPosition(p);
		b.setRotation(Math.PI / 2.0);
		boxes.add(b);
		return b;
	}

	/**
	 * Arranges boxes into initial positions so they will fall and create a
	 * pryamid.
	 * 
	 * @param boxes
	 *            the number of boxes in the pyramid
	 * @param rows
	 *            the number of rows in the pryamid
	 * @param xoffset
	 *            the x location
	 * @param yoffset
	 *            the y location
	 */
	private void makePyramid(final BodyLayer<Box> boxes, final int rows,
			final int xoffset, final int yoffset) {
		Box b;
		int i, j;

		for (i = 0; i < rows; i++) {
			for (j = 0; j < i; j++) {
				b = new Box(boxResource);
				b.set(400, .2, 1.0);
				b.setCenterPosition(new Vector2D(xoffset + (j - i / 2.0)
						* (b.getWidth() + 2), yoffset + i
						* (b.getHeight() + 15)));
				boxes.add(b);

			}
		}

	}

	/**
	 * Updates the world with the default rules by deferring to the Catto
	 * Physics Engine. Tracks the time the simulation has run so far and stops
	 * when the appropriate time has accrued.
	 * 
	 * @param deltaMs
	 *            the time in milliseconds since the last iteration of the game
	 *            loop.
	 */
	@Override
	public void update(final long deltaMs) {
		super.update(deltaMs);

		runTime += deltaMs;

		physics.applyLawsOfPhysics(deltaMs);
		if (runTime >= timeToQuit) {
			running = false;
		}
	}

	/**
	 * Render the boxes and physics engine markup.
	 * 
	 * @param gc
	 *            the Game Frame's rendering context.
	 */
	@Override
	public void render(final RenderingContext gc) {
		super.render(gc);
		physics.renderPhysicsMarkup(gc);
	}
	
	/**
	 * Runs the Catto Test and all of the simulations.
	 * 
	 * @param args
	 *            ignored
	 */
	public static void main(final String[] args) {
		//jig.engine.lwjgl.LWResourceFactory.makeCurrentResourceFactory();
		CattoEngineDemo t = new CattoEngineDemo();

		t.singleBoxTest(8);
		t.dominosTest(40);
		t.verticalStackTest(10);
		t.tiltedStackTest(10);
		t.pyramidTest(15);
		t.frictionTest(30);
		t.teeterTest(15);
		t.pendulumTest(10);
		t.bridgeTest(35);
		
		t.demoDone = true;
		
		t.gameframe.closeAndExit(true);
		
	}
	
	class CattoExitHandler implements GameFrame.ExitHandler 
	{
		CattoEngineDemo gameRef;
		
		CattoExitHandler(CattoEngineDemo gameRef) {
			this.gameRef = gameRef;
		}
		
		public boolean handleExit(boolean systemExit) {
			return !systemExit || gameRef.demoDone;
		}
	
	}
}
