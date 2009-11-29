package net;

import jig.engine.util.Vector2D;

public class Action {
	public static final int CHANGE_VELOCITY = 0;
	public static final int CHANGE_POSITION = 1;
	public static final int JOIN            = 2;
	public static final int DO_NOTHING      = 4;
	public static final int INPUT           = 5;
	public static final int SHOOT           = 6;

	public boolean up    = false;
	public boolean left  = false;
	public boolean right = false;
	public boolean down  = false;
	public boolean jump  = false;
	
	public boolean shoot = false;

	private int id;
	private int type;
	private Vector2D arg0;
	private String msg;
	
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
	 * Used to compare key strokes in Actions
	 * 
	 * @param a
	 * @return
	 */
	public boolean equals(Action a) {
		if (a.up == up && a.left == left && a.right == right && a.down == down && a.shoot == shoot)
			return true;
		return false;
	}

	/**
	 * Copy the key strokes information
	 * 
	 * @param a
	 */
	public void copy(Action a) {
		up = a.up;
		down = a.down;
		left = a.left;
		right = a.right;
		jump = a.jump;
		shoot = a.shoot;
	}
	
	public int getType () { return type; }
	public int getId() { return id; }
	public Vector2D getArg() { return arg0; }
	public String getMsg() { return msg; }
}
