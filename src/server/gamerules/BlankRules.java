package server.gamerules;

public abstract class BlankRules {
	
	int timer = 0;
	// list of players
	
	// initalize
	protected BlankRules() {		
	}
	
	// override to do timed stuff.
	public void update(int deltaMs) {
		timer += deltaMs;
	}
	
	public abstract void playerRespawn(); // Add parameters when needed.
	public abstract void playerDie(); // Add parameters when needed.
	public abstract void addPlayer(); // Add parameters when needed.
	public abstract void removePlayer(); // Add parameters when needed.
	// anything else needed?
}
