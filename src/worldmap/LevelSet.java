package worldmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

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

	LevelSet(String file) {
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

		System.out.println(filename);

		try {
			InputStreamReader R = new InputStreamReader(LevelMap.class
					.getResourceAsStream(filename));
			System.out.println(R);
			BufferedReader s = new BufferedReader(R);
			System.out.println(s);
			if (s == null)
				return null;
			String thisLine = s.readLine();
			int levelnum = java.lang.Integer.parseInt(thisLine);

			// Level loading.
			for (int l = 0; l < levelnum; l++) {
				// Read inital "Level"
				thisLine = s.readLine();
				System.out.println(thisLine.compareTo("Level") + "###");
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
				System.out.println(thisLine);
				if (thisLine == null) {
					s.close();
					res = null;
					System.out.println("Error: Level is misformatted!");
					return null;
				}
				thisMap.LevelTitle = thisLine;

				// Read level type
				thisLine = s.readLine();
				System.out.println(thisLine);
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
					System.out.println(thisLine);
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
					System.out.println(newspawn);
				}

				// Load objects, if any.
				thisLine = s.readLine();
				System.out.println(thisLine);
				while (thisLine != null && thisLine.compareTo("MovingObjects") != 0) {
					String[] a = thisLine.split(" ");
					if (a.length < 5) {
						s.close();
						res = null;
						System.out
								.println("Error: Object parameters is missing in least one number!");
						return null;
					}
					int x = java.lang.Integer.parseInt(a[0]);
					int y = java.lang.Integer.parseInt(a[1]);
					int w = java.lang.Integer.parseInt(a[2]);
					int h = java.lang.Integer.parseInt(a[3]);
					int r = java.lang.Integer.parseInt(a[4]);
					ObjectData aa = new ObjectData(x, y, w, h, r);
					thisMap.Objects.add(aa);
					System.out.println(aa);
					thisLine = s.readLine();
					System.out.println(thisLine);
				}

				// Load movable objects, if any.
				thisLine = s.readLine();
				System.out.println(thisLine);
				while (thisLine != null && thisLine.compareTo("EndLevel") != 0) {
					String[] a = thisLine.split(" ");
					if (a.length < 5) {
						s.close();
						res = null;
						System.out
								.println("Error: Object parameters is missing in least one number!");
						return null;
					}
					int x = java.lang.Integer.parseInt(a[0]);
					int y = java.lang.Integer.parseInt(a[1]);
					int w = java.lang.Integer.parseInt(a[2]);
					int h = java.lang.Integer.parseInt(a[3]);
					int r = java.lang.Integer.parseInt(a[4]);
					ObjectData aa = new ObjectData(x, y, w, h, r);
					thisMap.MovableObjects.add(aa);
					System.out.println(aa);
					thisLine = s.readLine();
					System.out.println(thisLine);
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
