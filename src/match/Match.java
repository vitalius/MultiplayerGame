package match;

import java.util.ArrayList;
import java.util.Timer;

import server.ServerGameState;

import jig.engine.util.Vector2D;

import world.GameObject;
import world.PlayerObject;

public abstract class Match {
	
	protected Timer timer;
	protected ArrayList<PlayerObject> players;
	// list of players
	
	// initalize
	protected Match(ServerGameState gs) {		
		timer = new Timer();
		players = new ArrayList<PlayerObject>(0);
		for (GameObject go : gs.getHashtable().values()) {
			if (go.getType() == GameObject.PLAYER){
				players.add((PlayerObject)go);
			}
		}
	}
	
	// override to do timed stuff.
	//public void update(int deltaMs) {
	//	timer += deltaMs;
	//}
	
	public abstract void startMatch(); // Add parameters when needed.
	public abstract void endMatch(); // Add parameters when needed.
	public abstract void addPlayer(PlayerObject p);	
	public abstract void removePlayer(PlayerObject p);
	public abstract void spawnPlayer(PlayerObject p, Vector2D loc);
}
