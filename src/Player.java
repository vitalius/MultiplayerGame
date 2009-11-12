import jig.engine.util.Vector2D;

public class Player {
	
	private int id;
	private Vector2D position;
	
	public Player(int p_id, Vector2D init) {
		id = p_id;
		position = init;
	}

	public int getId() { return id; }
	
	public Vector2D getPosition() {
		return position;
	}
	
	public void setPosition(Vector2D p) {
		position = p;
	}
	
	public void update(long deltaMs) {
		//
	}
}