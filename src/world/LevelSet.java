package world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import jig.engine.ResourceFactory;
import jig.engine.util.Vector2D;

/* Rolf Redford
 * 
 * LevelSet stores all levels read from file.
 * It contains linked list of LevelMap.
 * 
 * Accessor is by getThisLevel, get size by getNumLevels
 */
public class LevelSet {

	private LinkedList<LevelMap> levels; // Levels

	public LevelSet(String file) {
		levels = readLevelSet(file);
	}

	// Get level.
	public LevelMap getThisLevel(int levelnum) {
		if (levels.size() > levelnum)
			return levels.get(levelnum);
		return null;
	}

	// Get number of levels read from file.
	public int getNumLevels() {
		return levels.size();
	}

	// Read file passed to this and create levelmaps and make linkedlist.
	private static LinkedList<LevelMap> readLevelSet(String filename) {
		LinkedList<LevelMap> res = new LinkedList<LevelMap>();

		//System.out.println(filename);

		try {
			InputStreamReader R = new InputStreamReader(LevelMap.class
					.getResourceAsStream(filename));
			//System.out.println(R);
			BufferedReader s = new BufferedReader(R);
			//System.out.println(s);
			if (s == null)
				return null;
			String thisLine = s.readLine();
			int levelnum = java.lang.Integer.parseInt(thisLine);

			// Level loading.
			for (int l = 0; l < levelnum; l++) {
				// Read inital "Level"
				thisLine = s.readLine();
				//System.out.println(thisLine.compareTo("Level") + "###");
				if (thisLine.compareTo("Level") != 0) {
					s.close();
					res = null;
					System.out
							.println("Error: Level file not formatted properly!");
					return null;
				}

				// Create new levelmap and read title.
				LevelMap thisMap = new LevelMap();
				thisLine = s.readLine();
				//System.out.println(thisLine);
				if (thisLine == null) {
					s.close();
					res = null;
					System.out.println("Error: Level is misformatted!");
					return null;
				}
				thisMap.LevelTitle = thisLine;

				// Read level type
				thisLine = s.readLine();
				//System.out.println(thisLine);
				if (thisLine == null) {
					s.close();
					res = null;
					System.out.println("Error: Level is misformatted!");
					return null;
				}
				thisMap.LevelType = java.lang.Integer.parseInt(thisLine);

				// Load four spawn spots.
				for (int y = 0; y < 4; y++) {
					thisLine = s.readLine();
					//System.out.println(thisLine);
					if (thisLine == null) {
						s.close();
						res = null;
						System.out.println("Error: Level is misformatted!");
						return null;
					}
					String[] a = thisLine.split(" ");
					if (a.length < 2) {
						s.close();
						res = null;
						System.out
								.println("Error: Player spawn spot is missing in least one number!");
						return null;
					}
					Vector2D newspawn = new Vector2D(java.lang.Integer
							.parseInt(a[0]), java.lang.Integer.parseInt(a[1]));
					thisMap.playerInitSpots.add(newspawn);
					//System.out.println(newspawn);
				}

				// Load objects, if any.
				thisLine = s.readLine();
				//System.out.println(thisLine);
				BufferedImage[] b;
				int rscNum = 0;
				while (thisLine != null && thisLine.compareTo("EndLevel") != 0) {
					String[] a = thisLine.split(" ");
					if (a.length < 9) {
						s.close();
						res = null;
						System.out
								.println("Error: Object parameters is missing in least one number!");
						return null;
					}
					String rsc = a[0];
					int left = java.lang.Integer.parseInt(a[1]);
					int top = java.lang.Integer.parseInt(a[2]);
					int right = java.lang.Integer.parseInt(a[3]);
					int bottom = java.lang.Integer.parseInt(a[4]);
					double rot = Math.toRadians(java.lang.Double.parseDouble(a[5]));
					double mass = java.lang.Double.parseDouble(a[6]);
					if(mass < 0) {
						mass = Double.MAX_VALUE;
					}
					double fric = java.lang.Double.parseDouble(a[7]);
					double rest = java.lang.Double.parseDouble(a[8]);
					
					if (rsc.compareTo("static") == 0) {
						// create custom resource
						b = new BufferedImage[1];
						b[0] = new BufferedImage(right-left, bottom-top, BufferedImage.TYPE_INT_RGB);
						Graphics g = b[0].getGraphics();
						g.setColor(Color.green);
						g.fillRect(0, 0, right-left, bottom-top);
						g.dispose();
						rsc = rsc.concat(String.valueOf(rscNum++));
						ResourceFactory.getFactory().putFrames(rsc, b);
					}
					
					GameObject go = new GameObject(rsc);
					go.set(mass, fric, rest, rot);
					go.setPosition(new Vector2D(left,top)); 
					thisMap.Objects.add(go);
					//System.out.println(go);
					thisLine = s.readLine();
					//System.out.println(thisLine);
				}
				
				res.add(thisMap);
			}
			s.close();
			R.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}

		return res;
	}
}
