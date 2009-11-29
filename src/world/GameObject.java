package world;

import jig.engine.physics.BodyLayer;
import jig.engine.util.Vector2D;
import physics.Arbiter;
import physics.Box;
import physics.CattoPhysicsEngine;

public class GameObject extends Box {
	
	public static final int PLAYER      = 0;
	public static final int GROUND      = 1;
	public static final int PLATFORM    = 2;
	public static final int SMALLBOX    = 3;
	public static final int PLAYERSPAWN = 4;
	public static final int BULLET      = 5;

	private static final double JUMPVEL = -100;
	private static final double JETVEL = -100;
	private static final double RUNVEL = 150;
	private static final double WALKVEL = 100;
	private static final double FLOATVEL = 25;
	
	private double prevFriction;
	private boolean isJet;
	public int type;
	
	public GameObject(String rsc) {
		super(rsc);
		prevFriction = friction;
		isJet = false;
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
	public void movePlayer(int x, int jump, int jet, BodyLayer<GameObject> layer) {
		// detect if on an object
		boolean onObject = false;
		Arbiter arb;
		GameObject otherObject;
		for (int i = 0; i < layer.size(); i++) {
			otherObject = layer.get(i);
			if (this.hashCode() == otherObject.hashCode()) continue;
			arb = new Arbiter(this, otherObject);
			//System.out.println("playerObject: "+playerObject.getType());
			//System.out.println("otherObject: "+otherObject.getType());
			//System.out.println("num contacts: "+arb.getNumContacts());
			if ( arb.getNumContacts() > 0 ) {
				onObject = true;
				break;
			}
		}
		
		// turn friction off when moving
		if (x == 0) {
			prevFriction = friction;
			friction = 0;
		} else {
			friction = prevFriction;
		}
		
		double xVel = 0;
		double yVel = 0;
		// jumping, walking and floating
		if (onObject) {
			if (x > 0) {
				xVel = WALKVEL;
			} else if (x < 0) {
				xVel = -WALKVEL;
			}
			
			if (jump < 0) {
				yVel = JUMPVEL;
			}
		} else {
			if (x > 0) {
				xVel = FLOATVEL; // TODO: this should be acceleration 
			} else if (x < 0) {
				xVel = -FLOATVEL; // TODO: this should be acceleration 
			}
			
			yVel = 0;
		}
		
		if (jet < 0) {
			yVel = JETVEL; // TODO: this should be acceleration 
		}
		
		Vector2D newVelocity = new Vector2D(xVel, yVel);
		//if(x != 0 || y != 0) 
		setVelocity(newVelocity);
		//this.setVelocity(new Vector2D(x*GameObject.JUMPVEL, this.getVelocity().getY()));
	}
	
}
