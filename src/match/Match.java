package match;

import java.util.ArrayList;

import net.Action;

import server.Server;
import server.ServerGameState;

import world.LevelSet;
import world.PlayerObject;

public abstract class Match {

	public static int RUNNING = 0;
	public static int RESULTS = 1;

	public static long MATCH_LEN = 1000 * 60 * 1; // ms * seconds * minutes
	public static long RESULTS_LEN = 1000 * 10; // ms * seconds

	// list of players
	protected ArrayList<PlayerObject> players;
	protected LevelSet levels;
	protected int curLevel;
	protected long startTime; // the ms that the level started
	protected int gameState;

	// initalize
	protected Match(LevelSet l) {
		players = new ArrayList<PlayerObject>(0);
		// ServerGameState gs = ServerGameState.getGameState();
		// for (GameObject go : gs.getHashtable().values()) {
		// if (go.getType() == GameObject.PLAYER){
		// players.add((PlayerObject)go);
		// }
		// }
		levels = l;
		curLevel = -1; // load level will inc this so level 0 will be loaded
						// first
	}

	// override to do timed stuff.
	// public void update(int deltaMs) {
	// timer += deltaMs;
	// }
	public abstract void loadLevel(int levelNum);

	public abstract void loadNextLevel(); // load the next level

	public abstract void startMatch(); // Add parameters when needed.

	public abstract void endMatch(); // Add parameters when needed.

	public abstract void addPlayer(PlayerObject p);

	public abstract void removePlayer(PlayerObject p);

	public abstract void spawnPlayer(PlayerObject p, int n);

	public abstract boolean acceptPlayer();

	public ArrayList<PlayerObject> getPlayers() {
		return players;
	}

	public void update() {
		// spawn players if need be
		// for (PlayerObject po : players) {
		// if(po.getSpawn() > 0) {
		// spawnPlayer(po, po.getSpawn()-1);
		// }
		// }

		// check timer
		// System.out.println("Match.update countdown: " +
		// (ServerGameState.getGameState().totalMs - startTime));
		if (ServerGameState.getGameState().totalMs - startTime >= MATCH_LEN
				&& gameState == Match.RUNNING) {
			endMatch();
		}

		if (ServerGameState.getGameState().totalMs - startTime >= MATCH_LEN
				+ RESULTS_LEN
				&& gameState == Match.RESULTS) {
			// start a new match
			startMatch();
		}
		
		// Sending clients time left in the death match
		Server.getServer().gameState.getNetState().addAction(
				new Action(Server.getServer().gameState.getUniqueId(),
						Action.TIMER, 
						""+(MATCH_LEN - (ServerGameState.getGameState().totalMs - startTime))));
	}
}
