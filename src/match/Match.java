package match;

import java.util.ArrayList;
import java.util.Timer;

import server.ServerGameState;

import jig.engine.util.Vector2D;

import world.GameObject;
import world.LevelSet;
import world.PlayerObject;

public abstract class Match {
	
	// list of players
	protected ArrayList<PlayerObject> players;
	protected LevelSet levels;
	protected int curLevel;
	protected long levelLen; // ms that the level will last
	protected long startTime; // the ms that the level started
	
	
	// initalize
	protected Match(LevelSet l) {	
		players = new ArrayList<PlayerObject>(0);
		//ServerGameState gs = ServerGameState.getGameState();
		//for (GameObject go : gs.getHashtable().values()) {
		//	if (go.getType() == GameObject.PLAYER){
		//		players.add((PlayerObject)go);
		//	}
		//}
		levels = l;
		curLevel = -1; // load level will inc this so level 0 will be loaded first
		levelLen = 60000 * 1; // default is 10 minute
	}
	
	// override to do timed stuff.
	//public void update(int deltaMs) {
	//	timer += deltaMs;
	//}
	public abstract void loadLevel(int levelNum);
	public abstract void loadNextLevel(); // load the next level
	public abstract void startMatch(); // Add parameters when needed.
	public abstract void endMatch(); // Add parameters when needed.
	public abstract void addPlayer(PlayerObject p);	
	public abstract void removePlayer(PlayerObject p);
	public abstract void spawnPlayer(PlayerObject p, Vector2D loc);
	public abstract boolean acceptPlayer();
	
	public ArrayList<PlayerObject> getPlayers() {
		return players;
	}
	
	public void update() {
		// spawn players if need be
		for (PlayerObject po : players) {
			if(po.getSpawn() > 0) {
				spawnPlayer(po, levels.getThisLevel(curLevel)
						.playerInitSpots.get(po.getSpawn()-1));
			}
		}
		
		// check timer
		//System.out.println("Match.update countdown: " + (ServerGameState.getGameState().totalMs - startTime));
		if (ServerGameState.getGameState().totalMs - startTime >= levelLen) {
			endMatch();
			// start a new match
			startMatch();
		}
	}
}
