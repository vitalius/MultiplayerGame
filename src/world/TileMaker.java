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

	static public String generateTexture(int x, int y, int wid, int hei,
			double rot) {

		int xx = x % 50, yy = y % 50;

		while (xx < 0) {
			xx += 50;
		}
		while (yy < 0) {
			yy += 50;
		}

		// get offset
		xx = 50 - xx;// (xx % 50);
		yy = 50 - yy;// (yy % 50);

		System.out.print(xx + " " + yy + " " + wid + " " + hei + " " + rot
				+ " tilemaker\n");

		// Now that basic math is done, see if we had made it
		// with same offset, rotation, size. Probably unlikely.
		String res = "static" + xx + yy + wid + hei + rot;
		if (!factory.areFramesLoaded(res)) {

			BufferedImage tile = new BufferedImage(50, 50,
					BufferedImage.TYPE_INT_RGB);

			// draw on tile, need to change to load image
			Graphics2D tg = tile.createGraphics();

			tg.translate(xx , yy);
			tg.setColor(Color.DARK_GRAY);
			tg.fillRect(0, 0, 50, 50);
			tg
					.setPaint(new GradientPaint(40, 0, Color.green, 0, 40,
							Color.gray));
			tg.fillOval(5, 5, 40, 40); // Draw a circle with this gradient

			// draw again at negative y (translate on existing tranlation)
			tg.translate(0, -50);
			tg.setColor(Color.DARK_GRAY);
			tg.fillRect(0, 0, 50, 50);
			tg
					.setPaint(new GradientPaint(40, 0, Color.green, 0, 40,
							Color.gray));
			tg.fillOval(5, 5, 40, 40); // Draw a circle with this gradient

			// draw again at negative x,y
			tg.translate(-50, 0);
			tg.setColor(Color.DARK_GRAY);
			tg.fillRect(0, 0, 50, 50);
			tg
					.setPaint(new GradientPaint(40, 0, Color.green, 0, 40,
							Color.gray));
			tg.fillOval(5, 5, 40, 40); // Draw a circle with this gradient

			// draw again at negative x
			tg.translate(0, 50);
			tg.setColor(Color.DARK_GRAY);
			tg.fillRect(0, 0, 50, 50);
			tg
					.setPaint(new GradientPaint(40, 0, Color.green, 0, 40,
							Color.gray));
			tg.fillOval(5, 5, 40, 40); // Draw a circle with this gradient

			// whew done. now use texture to draw large object.

			BufferedImage[] b = new BufferedImage[1];

			b[0] = // Create an image
			new BufferedImage(wid, hei, BufferedImage.TYPE_INT_RGB);
			Graphics g = b[0].getGraphics();

			// Use this new tile to create a TexturePaint
			((Graphics2D) g).setPaint(new TexturePaint(tile, new Rectangle(0,
					0, 50, 50)));
			// rotate around center of tile.
			((Graphics2D)g).rotate(-rot);

			Rectangle rect = new Rectangle(wid, hei);
			((Graphics2D) g).fill(rect);
			double mag = Math.sqrt(wid^2 + hei^2);
			rect.add(Math.cos(-rot)*mag, Math.sin(-rot)*mag);
			mag = hei;
			rect.add(Math.cos(-rot)*mag, Math.sin(-rot)*mag);
			mag = wid;
			rect.add(Math.cos(-rot)*mag, Math.sin(-rot)*mag);

			// clean up.
			g.dispose();
			tg.dispose();
			factory.putFrames(res, b);
		}
		return res;
	}
}
