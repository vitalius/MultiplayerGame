package world;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import jig.engine.ResourceFactory;

public class TileMaker {

	// used to generate textures
	static ResourceFactory factory = ResourceFactory.getFactory();

	static public void generateTexture(int wid, int hei) {

		if (!factory.areFramesLoaded("static" + wid + "x" + hei)) {

			BufferedImage[] b = new BufferedImage[1];

			b[0] = // Create an image
			new BufferedImage(wid, hei, BufferedImage.TYPE_INT_RGB);

			BufferedImage tile = new BufferedImage(50, 50,
					BufferedImage.TYPE_INT_RGB);

			Graphics g = b[0].getGraphics();

			// draw on tile, need to change to load image
			Graphics2D tg = tile.createGraphics();
			tg.setColor(Color.DARK_GRAY);
			tg.fillRect(0, 0, 50, 50);
			tg
					.setPaint(new GradientPaint(40, 0, Color.green, 0, 40,
							Color.gray));
			tg.fillOval(5, 5, 40, 40); // Draw a circle with this gradient

			// Use this new tile to create a TexturePaint
			((Graphics2D) g).setPaint(new TexturePaint(tile, new Rectangle(0,
					0, 50, 50)));
			Rectangle rect = new Rectangle(wid, hei);
			((Graphics2D) g).fill(rect); // Fill letter shape

			// BufferedImage[] b = new BufferedImage[1];
			// b[0] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
			g.dispose();
			factory.putFrames("static" + wid + "x" + hei, b);
		}
	}

}
