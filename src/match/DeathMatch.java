package match;

import server.ServerGameState;
import jig.engine.util.Vector2D;
import world.PlayerObject;

//# of kills
//# of deaths
//a timer
//where to spawn, etc.
public class DeathMatch extends Match {
	
	public DeathMatch(ServerGameState gs) {
		super(gs);
	}
	
	@Override
	public void startMatch() {
		// reset all non static objects
		
		// reset all scores
		for(PlayerObject p: players) {
			p.clearKills();
			p.clearDeaths();
		}
		
		// start timer
		
		// spawn players
		for(PlayerObject p: players) {
			// need access to spawn spots somehow?
		}
	}

	@Override
	public void endMatch() {
		// disable input?
		
		// display scores
		
		// start timer
		
	}
	
	@Override
	public void addPlayer(PlayerObject p) {
		players.add(p);
		p.clearKills();
		p.clearDeaths();
	}
	
	@Override
	public void removePlayer(PlayerObject p) {
		players.remove(p);
	}
	
	@Override
	public void spawnPlayer(PlayerObject p, Vector2D loc) {
		p.setCenterPosition(loc);
		p.setHealth(PlayerObject.MAXHEALTH);
	}
}
