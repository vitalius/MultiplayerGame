package match;

import java.util.ArrayList;
import java.util.Timer;

import jig.engine.util.Vector2D;

import world.PlayerObject;

public abstract class Match {
	
	private Timer timer;
	private ArrayList<PlayerObject> players;
	// list of players
	
	// initalize
	protected Match() {		
		timer = new Timer();
		players = new ArrayList<PlayerObject>(0);
	}
	
	// override to do timed stuff.
	//public void update(int deltaMs) {
	//	timer += deltaMs;
	//}
	
	public abstract void startMatch(); // Add parameters when needed.
	public abstract void endMatch(); // Add parameters when needed.
	
	public void addPlayer(PlayerObject p) {
		players.add(p);
	}
	
	public void removePlayer(PlayerObject p) {
		players.remove(p);
	}
	
	public void spawnPlayer(PlayerObject p, Vector2D loc) {
		p.setCenterPosition(loc);
	}
}
