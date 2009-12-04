package world;

import java.util.LinkedList;
import clients.GameSprites;

import server.ServerGameState;
import jig.engine.util.Vector2D;

public class LevelMap {
	
	public static int MeleePlay = 0; // Only game type for now.
	//public static int TeamPlay = 0; // TeamPlay
	
	public String LevelTitle;				// Title of game
	public int LevelType;					// Type of game
	public boolean mirror;
	public double scale;
	public Vector2D offset;
	public LinkedList<Vector2D> playerInitSpots;	// Spawn spots - Team spawn spots if TeamPlay type.
	public LinkedList<GameObject> Objects;			// Solid objects IE floor, wall, etc.
	
	//LinkedList<Vector2D> TeamFlags;			// add when flag type is added.
	// anything else needed?
	
	LevelMap() {
		LevelTitle = "";
		LevelType = MeleePlay;
		playerInitSpots = new LinkedList<Vector2D>();
		Objects = new LinkedList<GameObject>();
	}
	
	// Build world from level data.
	public void buildLevel(final ServerGameState gs) {

		for (int x = 0; x < playerInitSpots.size(); x++) {
			// GameObject not applicable due to being a place to spawn not an object.
			GameObject a = new GameObject("playerSpawn");
			a.setPosition(playerInitSpots.get(x));
			System.out.println(a.getPosition());
			gs.add(a, GameObject.PLAYERSPAWN);
		}

		// Create objects based on object type.
		for (int i = 0; i < Objects.size(); i++) {
			GameObject s = Objects.get(i);
			//System.out.println(s);
			gs.add(s, s.getType());
		}
	}
	
	// Build world from level data.
	public void buildLevelClient(final GameSprites gs) {
		// Create objects based on object type.
		for (int i = 0; i < Objects.size(); i++) {
			GameObject s = Objects.get(i);
			//System.out.println(s);
			gs.spriteList.put(s.hashCode(), s);
		}
	}
}
