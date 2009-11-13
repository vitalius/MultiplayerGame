package net;
import jig.engine.util.Vector2D;
import client.Player;

public class NetObject {
	
	private int id;
	private Vector2D position;
	private Player p;
	
	public NetObject(int p_id, Vector2D init) {
		id = p_id;
		p = null;
		setPosition(init);
	}

	public NetObject(int p_id, Vector2D init, boolean buildPlayer) {
		id = p_id;
		p = new Player();
		setPosition(init);
	}
	
	public Player getPlayer() { return p; }
	
	public int getId() { return id; }
	public Vector2D getPosition() { return position; }
	public void setPosition(Vector2D p) { position = p; }
}