package world;

import java.util.Vector;

import jig.engine.util.Vector2D;

import physics.Box;

public class GameObject extends Box {
	
	public static final int PLAYER      = 0;
	public static final int STATIC      = 1;
	public static final int SMALLBOX    = 2;
	public static final int PLAYERSPAWN = 4;
	public static final int BULLET      = 5;
	public static final int DRUM        = 6;
	
	public Vector<GameObject> listBullets = new Vector<GameObject>();
	public int bulletCount = 0;

	public int type;
	
	public GameObject(String rsc) {
		super(rsc);
		if (rsc.compareTo("player") == 0) { setType(GameObject.PLAYER); }
		else if (rsc.compareTo("smallbox") == 0) { setType(GameObject.SMALLBOX); }
		else if (rsc.compareTo("drum") == 0) { setType(GameObject.DRUM); }
		else if (rsc.compareTo("playerspawn") == 0) { setType(GameObject.PLAYERSPAWN); }
		else if (rsc.compareTo("bullet") == 0) { setType(GameObject.BULLET); }
		else { setType(GameObject.STATIC); }
	}
	
	public Double getMass() {
		return mass;
	}
	
	public void setMass(Double m) {
		mass =  m;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int t) {
		type =  t;
	}
	
	public void setID(int id) {
		this.id =  id;
	}
	
	public int getID() {
		return id;
	}
	
	public void setRotation(double r) {
		rotation =  r;
	}
	
	public void setForce(Vector2D f) {
		force =  f;
	}
}
