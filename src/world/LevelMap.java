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
	public void buildLevel() {
		/*for (int i = 0; i < playerInitSpots.size(); i++) {
			// GameObject not applicable due to being a place to spawn not an object.
			GameObject go = new GameObject("playerSpawn");
			go.setPosition(playerInitSpots.get(i));
			System.out.println(go.getPosition());
			gs.add(go, GameObject.PLAYERSPAWN);
		}*/
		ServerGameState gs = ServerGameState.getGameState();
		// Create objects based on object type.
		for (int i = 0; i < Objects.size(); i++) {
			GameObject go = Objects.get(i);
			//System.out.println(s);
			gs.add(go);
		}
	}
	
	// Build world from level data.
	public void buildLevelClient(final GameSprites gs) {
		
		// Create objects based on object type.
		for (int i = 0; i < Objects.size(); i++) {
			GameObject go = Objects.get(i);
			//System.out.println("Object Type: " + go.getType());
			if (go.getType() != GameObject.STATIC) continue;
			//System.out.println(s);
			gs.spriteList.put(go.hashCode(), go);
			gs.getLayer().add(go);
		}
	}
}
