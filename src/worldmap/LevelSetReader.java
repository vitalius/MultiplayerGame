package worldmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import jig.engine.util.Vector2D;

public class LevelSetReader {

	public static LinkedList<LevelMap> ReadLevelSet(String filename) {
		LinkedList<LevelMap> res = new LinkedList<LevelMap>();

		try {
			BufferedReader s = new BufferedReader(new InputStreamReader(
					LevelMap.class.getResourceAsStream(filename)));
			if (s == null)
				return null;
			String thisLine = s.readLine();
			int levelnum = java.lang.Integer.parseInt(thisLine);
			
			// Level loading.
			for (int x = 0; x < levelnum; x++) {
				// Read inital "Level"
				thisLine = s.readLine();
				if (thisLine != "Level") {
					s.close();
					res = null;
					System.out
							.println("Error: Level file not formatted properly!");
					return null;
				}
				
				// Create new levelmap and read title.
				LevelMap thisMap = new LevelMap();
				thisLine = s.readLine();
				if (thisLine == null) {
					s.close();
					res = null;
					System.out.println("Error: Level is misformatted!");
					return null;
				}
				thisMap.LevelTitle = thisLine;

				// Load four spawn spots.
				for (int y = 0; y < 4; y++) {
					thisLine = s.readLine();
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
							.parseInt(a[0]), java.lang.Integer.parseInt(a[0]));
					thisMap.playerInitSpots.add(newspawn);
				}
				
				// Load objects, if any.

			}

			// res.add(thisMap);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}

		return res;
	}
}
