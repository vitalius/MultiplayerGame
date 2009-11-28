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
	
	public Action (int requesterId, int t, Vector2D v) {
		id = requesterId;
		type = t;
		arg0 = v;
	}
	
	public Action(int requesterId, int t, String ip) {
		id = requesterId;
		type = t;
		msg = ip;
	}

	public Action(int requesterId, int t) {
		id = 0;
		type = t;
		arg0 = null;
		msg = null;
	}
	
	public Action(int requesterId) {
		id = 0;
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
