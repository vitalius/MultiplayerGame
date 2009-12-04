package world;

import physics.Box;

public class GameObject extends Box {
	
	public static final int PLAYER      = 0;
	public static final int CUSTOM      = 1;
	public static final int SMALLBOX    = 3;
	public static final int PLAYERSPAWN = 4;
	public static final int BULLET      = 5;

	public int type;
	
	public GameObject(String rsc) {
		super(rsc);
		if (rsc.compareTo("player") == 0) { setType(GameObject.PLAYER); }
		else if (rsc.compareTo("smallbox") == 0) { setType(GameObject.SMALLBOX); }
		else if (rsc.compareTo("playerspawn") == 0) { setType(GameObject.PLAYERSPAWN); }
		else { setType(GameObject.CUSTOM); }
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
	
	public void setRotation(double r) {
		rotation =  r;
	}
}
