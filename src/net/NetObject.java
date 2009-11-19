package net;  

import java.net.InetAddress;
import java.net.UnknownHostException;

import jig.engine.util.Vector2D;
import clients.SpriteObject;

public class NetObject {
	
	public static final int PLAYER = 0;
	public static final int BULLET = 1;
	
	private int id;
	private Vector2D position;
	private Vector2D velocity;
	private SpriteObject sprite;
	private String ipAddress;
	private int type;
	
	public NetObject(int id, Vector2D p) {
		init(id, p);
	}

	public NetObject(int id, Vector2D p, int type, boolean hasSprite) {
		init(id,p,type,hasSprite);
	}

	public NetObject(int id, Vector2D p, int type) {
		init(id,p,type);
	}
	
	public NetObject(int id, Vector2D p, int type, Vector2D v) {
		init(id,p,type);
		setVelocity(v);
	}
	
	public void init (int id, Vector2D p, int t, boolean hasSprite) {
		init(id,p,t);
		
		if (hasSprite) {
			switch(type) {
				case PLAYER:
					sprite = new SpriteObject("player");
					break;
				case BULLET:
					sprite = new SpriteObject("player");
					break;
			}
		}
	}
	
	public void init (int id, Vector2D p, int t) {
		init(id,p);
		type = t;	
	}
	
	public void init (int p_id, Vector2D p) {
		id = p_id;
		sprite = null;
		type = PLAYER;
		
		setPosition(p);
		setVelocity(new Vector2D(0,0));
		
	    try {
	        InetAddress addr = InetAddress.getLocalHost();
	        ipAddress = addr.getHostAddress();
	    } catch (UnknownHostException e) {
	    	ipAddress = "127.0.0.0.1";
	    }			
	}
	
	public void update(long deltaMs) {
		Vector2D p = getPosition();
		Vector2D v = getVelocity();
		
		setPosition(new Vector2D(p.getX()+v.getX()*deltaMs,
				                 p.getY()+v.getY()*deltaMs));
	
		if (sprite != null)
			sprite.setPosition(getPosition());
	}
	
	public SpriteObject getSprite () { return sprite; }
	
	public int getId() { return id; }
	
	public Vector2D getPosition() { return position; }
	public void setPosition(Vector2D p) { position = p; }
	
	public Vector2D getVelocity() { return velocity; }
	public void setVelocity(Vector2D v) { velocity = v; }
	
	public String getIp() { return ipAddress; }
	public void setIp(String ip) { ipAddress = ip; }
	
	public int getType() { return type; }
}