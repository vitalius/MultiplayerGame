package net;

import jig.engine.util.Vector2D;

public class Action {
	public static final int CHANGE_VELOCITY = 0;
	public static final int CHANGE_POSITION = 1;
	public static final int JOIN            = 2;
	public static final int DO_NOTHING      = 4;

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
		t = DO_NOTHING;
		arg0 = null;
		msg = null;
	}
	
	public int getType () { return type; }
	public int getId() { return id; }
	public Vector2D getArg() { return arg0; }
	public String getMsg() { return msg; }
}
