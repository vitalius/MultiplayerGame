package net;

import jig.engine.util.Vector2D;

public class Action {
	public static final int CHANGE_VELOCITY = 0;
	public static final int CHANGE_POSITION = 1;
	public static final int JOIN_REQUEST    = 2;
	public static final int JOIN_ACCEPT     = 3;
	public static final int LEAVE_SERVER    = 4;
	public static final int DO_NOTHING      = 5;
	public static final int INPUT           = 6;
	public static final int SHOOT           = 7;
	public static final int CHANGE_HEALTH 	= 8;
	public static final int CHANGE_JETPACK 	= 9;
	public static final int EXPLOSION       = 10;
	public static final int TALK            = 11;
	public static final int SPAWN           = 12;

	public boolean jet      = false;
	public boolean left     = false;
	public boolean right    = false;
	public boolean crouch   = false;
	public boolean jump     = false;
	public boolean faceLeft = false;
	public int weapon = 0;
	public int spawn = 0;

	private int id;
	private int type;
	public Vector2D arg0;
	private String msg;
	private double dou;
	
	/**
	 * Used by displacing position, updating velocity and shooting
	 * 
	 * @param requesterId
	 * @param t - CHANGE_VELOCITY, CHANGE_POSITION, SHOOT
	 * @param v - vector2D
	 */
	public Action (int requesterId, int t, Vector2D v) {
		id = requesterId;
		type = t;
		arg0 = v;
	}
	
	/**
	 * Used by change CHANGE_HEALTH, CHANGE_JETPACK
	 * 
	 * @param requesterId
	 * @param t - CHANGE_HEALTH, CHANGE_JETPACK
	 * @param d - double value
	 */
	public Action (int requesterId, int t, double d) {
		id = requesterId;
		type = t;
		dou = d;
	}
	
	/**
	 * Used by join action, Attempt to add a player to the game
	 * And the input where String is a coded input like this: "1#0#0#1" - UP and RIGHT are pressed
	 * 
	 * @param requesterId
	 * @param t - JOIN, INPUT
	 * @param ip - IP address of a client
	 */
	public Action(int requesterId, int t, String s) {
		id = requesterId;
		type = t;
		msg = s;
	}

	/**
	 * Custom blank Action 
	 * 
	 * @param requesterId
	 * @param t
	 */
	public Action(int requesterId, int t) {
		id = requesterId;
		type = t;
		arg0 = null;
		msg = null;
	}
	
	/**
	 * Default blank Action
	 * 
	 * @param requesterId
	 */
	public Action(int requesterId) {
		id = requesterId;
		type = DO_NOTHING;
		arg0 = null;
		msg = null;
	}
	
	/**
	 * Used to compare input information in Actions
	 * 
	 * @param a
	 * @return
	 */
	public boolean equals(Action a) {
		if (    a.jet      == jet &&
				a.left     == left && 
				a.right    == right && 
				a.crouch   == crouch && 
				a.jump     == jump &&
				a.weapon   == weapon && 
				a.spawn    == spawn && 
				a.faceLeft == faceLeft     )
			return true;
		return false;
	}

	/**
	 * Copy input information used in .equals(Action a) information
	 * 
	 * @param a
	 */
	public void copy(Action a) {
		jet      = a.jet;
		crouch   = a.crouch;
		left     = a.left;
		right    = a.right;
		jump     = a.jump;
		weapon   = a.weapon;
		spawn    = a.spawn;
		faceLeft = a.faceLeft;
	}
	
	public int getType () { return type; }
	public int getID() { return id; }
	public void setID(int i) { id = i; }
	public Vector2D getArg() { return arg0; }
	public String getMsg() { return msg; }
	public double getDouble() { return dou; }
}
