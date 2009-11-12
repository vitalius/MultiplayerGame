package net;
import jig.engine.util.Vector2D;

public class NetObject {
	
	private int id;
	private Vector2D position;
	
	public NetObject(int p_id, Vector2D init) {
		id = p_id;
		setPosition(init);
	}

	public int getId() { return id; }
	public Vector2D getPosition() { return position; }
	public void setPosition(Vector2D p) { position = p; }
}