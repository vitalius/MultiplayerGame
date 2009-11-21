package worldmap;

import java.util.LinkedList;
import jig.engine.util.Vector2D;

public class LevelMap {
	
	public static int MeleePlay = 0; // Only game type for now.
	//public static int TeamPlay = 0; // TeamPlay
	
	public String LevelTitle;				// Title of game
	public int LevelType;					// Type of game
	public LinkedList<Vector2D> playerInitSpots;	// Spawn spots - Team spawn spots if TeamPlay type.
	public LinkedList<ObjectData> Objects;			// Solid objects IE floor, wall, etc.
	
	//LinkedList<Vector2D> TeamFlags;			// add when flag type is added.
	// anything else needed?
	
	LevelMap() {
		LevelTitle = "";
		LevelType = MeleePlay;
		playerInitSpots = new LinkedList<Vector2D>();
		Objects = new LinkedList<ObjectData>();
	}
}
