package worldmap;

import java.util.LinkedList;
import jig.engine.util.Vector2D;

public class LevelMap {
	
	public static int MeleePlay = 0; // Only game type for now.
	//public static int TeamPlay = 0; // TeamPlay
	
	public String LevelTitle;				// Title of game
	public int LevelType;					// Type of game
	LinkedList<Vector2D> playerInitSpots;	// Spawn spots - Team spawn spots if TeamPlay type.
	LinkedList<ObjectData> Objects;			// Solid objects IE floor, wall, etc.

	LinkedList<ObjectData> MovableObjects;	// Solid movable objects like boxes, etc but NOT players.

	
	//LinkedList<Vector2D> TeamFlags;			// add when flag type is added.
	// anything else needed?
	
	LevelMap() {
		LevelTitle = "";
		LevelType = MeleePlay;
		playerInitSpots = new LinkedList<Vector2D>();
		Objects = new LinkedList<ObjectData>();
		MovableObjects = new LinkedList<ObjectData>();
	}
}
