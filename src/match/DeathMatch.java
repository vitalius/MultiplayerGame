package match;

import java.util.Collections;
import java.util.LinkedList;

import net.Action;
import net.NetObject;
import net.NetStateManager;
import net.Protocol;
import physics.CattoPhysicsEngine;
import server.Server;
import server.ServerGameState;
import server.TcpSender;
import jig.engine.hli.ScrollingScreenGame;
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
	//server.
	
	
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
		// load the level
		//loadNextLevel(); //TODO: fix if time
		
		// reset all scores
		for(PlayerObject p: players) {
			p.setActivation(false);
			p.setHealth(0); // this will be zero until we spawn
			p.clearKills();
			p.clearDeaths();
		}		
		
		// start timer
		startTime = ServerGameState.getGameState().totalMs;
		// notify players to spawn
		String msg = "Game have started! Press f1-4 to spawn.";
		Action a = new Action(0, Action.TALK, msg);
		for(PlayerObject play: players) {
			// get netobject of player
			NetObject nplay = Server.getServer().gameState.getNetState().objectList.get(play.getID());
			// get ip
			String ip = nplay.getIp();
			//Server.getServer().sendMsg(ip,msg);
			// use server tcpsender to send action a. 
			//Server.getServer().tcpSender.sendSocket(ip, Server.getServer().netStateMan.prot.encodeAction(a));
			//System.out.println("Deathmatch Sent to " + ip + "this message: " + Server.getServer().netStateMan.prot.encodeAction(a) + "message: " + msg);
		}
	}
	
	private class playerscore implements Comparable<playerscore>{
		public int playerID = 0;
		public int score = 0;
		playerscore(int playid, int sco) {
			playerID = playid;
			score = sco;
		}
		public int compareTo(playerscore o) {
			return score - o.score;
		}
	}

	@Override
	public void endMatch() {
		System.out.println("Deathmatch end match event");
		
		LinkedList<playerscore> scores = new LinkedList<playerscore>();
		
		// reset all scores
		for(PlayerObject p: players) {
			scores.add(new playerscore(p.getID(), p.getKills() - p.getDeaths()));
			p.setActivation(false);
			p.setHealth(0); // this will be zero until we spawn
			p.clearKills();
			p.clearDeaths();
		}
		
		Collections.sort(scores);
		
		for(playerscore ps : scores) {
			System.out.println("Deathmatch score: " + ps.playerID + " scored:" + ps.score);		
		}
		
		//Server.getServer().clear();
		//loadLevel(1);

		// FOR ow force exit
		//System.exit(0);
		
		// disable input?
		
		// display scores
		
		// start timer
		
	}
	
	@Override
	public void addPlayer(PlayerObject p) {
		players.add(p);
		p.setActivation(false);
		p.setHealth(0); // this will be zero until we spawn
		p.clearKills();
		p.clearDeaths();
		p.setTeam(players.size()-1);
	}
	
	@Override
	public void removePlayer(PlayerObject p) {
		players.remove(p);
	}
	
	@Override
	public void spawnPlayer(PlayerObject p, int n) {
		if (n > this.levels.getThisLevel(this.curLevel).playerInitSpots.size())
			return;
		
		p.setActivation(true);
		p.setCenterPosition(this.levels.getThisLevel(this.curLevel).playerInitSpots.get(n));
		p.setHealth(PlayerObject.MAXHEALTH);
	}

	@Override
	public boolean acceptPlayer() {
		return players.size() < playerNumLimit;
	}
}
