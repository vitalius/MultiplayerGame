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
	private int rscNum = 0;

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
	private LinkedList<LevelMap> readLevelSet(String filename) {
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
				while (thisLine != null && thisLine.compareTo("EndLevel") != 0) {
					String[] a = thisLine.split(" ");
					GameObject go;
					if (a.length == 7) {
						go = getRsc(a);
					} else if (a.length == 9) {
						go = getCustomRsc(a);
					} else {
						s.close();
						res = null;
						System.out.println("Error: Object parameters is missing in least one number!");
						return null;
					}
					
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
	
	private GameObject getCustomRsc(String[] a){
		String rsc = a[0];
		int x = java.lang.Integer.parseInt(a[1]);
		int y = java.lang.Integer.parseInt(a[2]);
		int width = java.lang.Integer.parseInt(a[3]);
		int height = java.lang.Integer.parseInt(a[4]);
		y = -(y + height);
		double rot = Math.toRadians(java.lang.Integer.parseInt(a[5]));
		//int width = (int) Math.sqrt(new Vector2D(x1,y1).distance2(new Vector2D(x2,y2)));
		double mass = java.lang.Double.parseDouble(a[6]);
		if(mass < 0) {
			mass = Double.MAX_VALUE;
		}
		double fric = java.lang.Double.parseDouble(a[7]);
		double rest = java.lang.Double.parseDouble(a[8]);
		
		// create custom resource
		BufferedImage b[] = new BufferedImage[1];
		b[0] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = b[0].getGraphics();
		g.setColor(Color.green);
		g.fillRect(0, 0, width, height);
		g.dispose();
		rsc = rsc.concat(String.valueOf(rscNum++));
		ResourceFactory.getFactory().putFrames(rsc, b);
			
		GameObject go = new GameObject(rsc);
		go.set(mass, fric, rest, rot);
		go.setPosition(new Vector2D(x,y));
		
		return go;
	}
	
	private GameObject getRsc(String[] a){
		String rsc = a[0];
		int x1 = java.lang.Integer.parseInt(a[1]);
		int y1 = java.lang.Integer.parseInt(a[2]);
		double rot = Math.toRadians(java.lang.Double.parseDouble(a[3]));
		double mass = java.lang.Double.parseDouble(a[4]);
		if(mass < 0) {
			mass = Double.MAX_VALUE;
		}
		double fric = java.lang.Double.parseDouble(a[5]);
		double rest = java.lang.Double.parseDouble(a[6]);
		
		GameObject go = new GameObject(rsc);
		go.set(mass, fric, rest, rot);
		go.setPosition(new Vector2D(x1,y1));
		
		return go;
	}
}
