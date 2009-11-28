package world;

import jig.engine.util.Vector2D;
import physics.Arbiter;
import physics.Box;
import physics.CattoPhysicsEngine;

public class GameObject extends Box {
	
	public static final double JUMPVEL = 100;
	public static final double JETVEL = 100;
	public static final double RUNVEL = 150;
	public static final double WALKVEL = 100;
	public static final double FLOATVEL = 25;
	
	public static final int PLAYER   = 0;
	public static final int GROUND   = 1;
	public static final int PLATFORM = 2;
	public static final int SMALLBOX = 3;
	public static final int PLAYERSPAWN   = 4;
	
	public int type;
	
	public GameObject(String rsc) {
		super(rsc);
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
	
	// jetpack
	public void jet() {
		this.setVelocity(new Vector2D( this.getVelocity().getX(), GameObject.JETVEL));
	}
	
	// jump
	public void jump(CattoPhysicsEngine pe) {
		this.setVelocity(new Vector2D( this.getVelocity().getX(), GameObject.JUMPVEL));
	}
	
	// walking, running and floating
	public void move(int x, int y) {
		this.setVelocity(new Vector2D(x*GameObject.JUMPVEL, this.getVelocity().getY()));
	}
	
}
