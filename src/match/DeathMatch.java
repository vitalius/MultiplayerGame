package match;

import java.util.Collections;
import java.util.LinkedList;

import server.Server;
import server.ServerGameState;
import jig.engine.util.Vector2D;
import world.LevelMap;
import world.LevelSet;
import world.PlayerObject;

//# of kills
//# of deaths
//a timer
//where to spawn, etc.
public class DeathMatch extends Match {

	private int playerNumLimit = 6;

	// server.

	public DeathMatch(LevelSet l) {
		super(l);
	}

	@Override
	public void loadNextLevel() {

		System.out.println("deathmatch clear level and reset");
		// clean out all the objects first
		Server.getServer().clear();

		// get the next level number
		curLevel = (curLevel + 1) % levels.getNumLevels();

		// load current level
		LevelMap level = levels.getThisLevel(curLevel);
		if (level == null) {
			System.err.println("Error: Level wasn't correctly loaded.\n");
			System.exit(1);
		}
		level.buildLevel();

		// re-add players
		for (PlayerObject po : players) {
			ServerGameState.getGameState().getLayer().add(po);
		}
	}

	public void loadLevel(int levelNum) {
		// get the next level number
		curLevel = levelNum;

		// load current level
		LevelMap level = levels.getThisLevel(curLevel);
		if (level == null) {
			System.err.println("Error: Level wasn't correctly loaded.\n");
			System.exit(1);
		}
		level.buildLevel();
	}

	@Override
	public void startMatch() {
		gameState = Match.RUNNING;
		Server.getServer().setPaused(false);
		
		// load the level
		// loadNextLevel(); //TODO: fix if time

		// reset all scores
		for (PlayerObject p : players) {
			p.setActivation(false);
			p.setHealth(0); // this will be zero until we spawn
			p.clearKills();
			p.clearDeaths();
		}

		// start timer
		startTime = ServerGameState.getGameState().totalMs;
		// notify players to spawn
		String msg = "Game have started! Press F1-F4 to spawn.";
		Server.getServer().sendPublicMessage(msg);
	}

	private class playerscore implements Comparable<playerscore> {
		public int playerID = 0;
		public int score = 0;
		public int kills = 0;
		public int deaths = 0;
		

		public playerscore(int playid, int sco, int kil, int dea) {
			playerID = playid;
			score = sco;
			kills = kil;
			deaths = dea;
		}

		public int compareTo(playerscore o) {
			return score - o.score;
		}
	}

	@Override
	public void endMatch() {
		
		Server.getServer().setPaused(true);

		gameState = Match.RESULTS;
		System.out.println("Deathmatch end match event");

		LinkedList<playerscore> scores = new LinkedList<playerscore>();
		
		for(PlayerObject play: players) {
			scores.add(new playerscore(play.getID(), play.getKills() - play.getDeaths()
			, play.getKills(), play.getDeaths()));
			play.setHealth(0);
		}

		Collections.sort(scores);

		for (playerscore ps : scores) {
			String msg = "Deathmatch Player : " + ps.playerID + " Score: "
			+ ps.score + "Kills: " + ps.kills + "Deaths: " + ps.deaths;
			System.out.println(msg);
			Server.getServer().sendPublicMessage(msg);
		}
		

		// display scores

	}

	@Override
	public void addPlayer(PlayerObject p) {
		players.add(p);
		p.setActivation(false);
		p.setHealth(0); // this will be zero until we spawn
		p.setPosition(new Vector2D(5000,5000)); // add the player in the middle of nowher
		p.clearKills();
		p.clearDeaths();
		p.setTeam(players.size() - 1);
	}

	@Override
	public void removePlayer(PlayerObject p) {
		players.remove(p);
	}

	@Override
	public void spawnPlayer(PlayerObject p, int n) {
		p.isAlive = true;
		if (n > this.levels.getThisLevel(this.curLevel).playerInitSpots.size())
			return;

		p.setActivation(true);
		p
				.setCenterPosition(this.levels.getThisLevel(this.curLevel).playerInitSpots
						.get(n));
		p.setHealth(PlayerObject.MAXHEALTH);
		p.setVelocity(new Vector2D(0, 0));
	}

	@Override
	public boolean acceptPlayer() {
		return players.size() < playerNumLimit;
	}
}
