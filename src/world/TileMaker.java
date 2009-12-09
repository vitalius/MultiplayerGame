package world;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
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

		// System.out.print(xx + " " + yy + " " + wid + " " + hei + " " + rot
		// + " tilemaker\n");

		// Now that basic math is done, see if we had made it
		// with same offset, rotation, size. Probably unlikely.
		String res = "static" + xx + yy + wid + hei + rot;
		if (!factory.areFramesLoaded(res)) {

			BufferedImage tile = new BufferedImage(50, 50,
					BufferedImage.TYPE_INT_RGB);

			// draw on tile, need to change to load image
			Graphics2D tg = tile.createGraphics();

			// tg.translate(xx , yy);
			tg.setColor(Color.DARK_GRAY);
			tg.fillRect(0, 0, 50, 50);
			tg
					.setPaint(new GradientPaint(40, 0, Color.green, 0, 40,
							Color.gray));
			tg.fillOval(5, 5, 40, 40); // Draw a circle with this gradient

			/*
			 * // draw again at negative y (translate on existing tranlation)
			 * tg.translate(0, -50); tg.setColor(Color.DARK_GRAY);
			 * tg.fillRect(0, 0, 50, 50); tg .setPaint(new GradientPaint(40, 0,
			 * Color.green, 0, 40, Color.gray)); tg.fillOval(5, 5, 40, 40); //
			 * Draw a circle with this gradient
			 * 
			 * // draw again at negative x,y tg.translate(-50, 0);
			 * tg.setColor(Color.DARK_GRAY); tg.fillRect(0, 0, 50, 50); tg
			 * .setPaint(new GradientPaint(40, 0, Color.green, 0, 40,
			 * Color.gray)); tg.fillOval(5, 5, 40, 40); // Draw a circle with
			 * this gradient
			 * 
			 * // draw again at negative x tg.translate(0, 50);
			 * tg.setColor(Color.DARK_GRAY); tg.fillRect(0, 0, 50, 50); tg
			 * .setPaint(new GradientPaint(40, 0, Color.green, 0, 40,
			 * Color.gray)); tg.fillOval(5, 5, 40, 40); // Draw a circle with
			 * this gradient
			 */

			// whew done. now use texture to draw large object.

			BufferedImage[] b = new BufferedImage[1];

			b[0] = // Create an image
			new BufferedImage(wid, hei, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = b[0].createGraphics();

			// Use this new tile to create a TexturePaint
			g.setPaint(new TexturePaint(tile, new Rectangle(0, 0, 50, 50)));

			
			g.setClip(new Rectangle(0,0,wid,hei));

			// rotate around center of tile.
			g.rotate(-rot , wid/2, hei/2);

			Rectangle rect = g.getClipBounds();

			// add points to encompass entire rotated box.
			double mag = Math.sqrt((wid) ^ 2 + (hei) ^ 2);

			// rect.add(-2000,-2000);
			// rect.add(2000,2000);

			int xi = rect.x + xx - 50, yi = rect.y + yy - 50;
			int xd = rect.x + xx - 50, yd = rect.y + yy - 50;
			int w = rect.width + 50, h = rect.height + 50;

			System.out.print("start tiling" + " tilemaker\n");
			System.out.print("init " + xd + " " + yd + " " + w + " " + h
					+ " tilemaker\n");

			while (xd < xi + w) {
				while (yd < yi + h) {
					// System.out.print(xd + " " + yd + " " + w + " " + h
					// + " tilemaker\n");
					g.drawImage(tile, new AffineTransform(1f, 0f, 0f, 1f, xd,
							yd), null);
					yd += 50;
				}
				yd = yi;
				xd += 50;
			}

			// clean up.
			g.dispose();
			tg.dispose();
			factory.putFrames(res, b);
		}
		return res;
	}
}
