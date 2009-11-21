package net;  

import java.net.InetAddress;
import java.net.UnknownHostException;
import physics.Box;
import jig.engine.ResourceFactory;
import jig.engine.util.Vector2D;
//import clients.SpriteObject;

public class NetObject extends Box{
	
	public static final int PLAYER = 0;
	public static final int BULLET = 1;
	
	//private int id;
	//private Vector2D position;
	//private Vector2D velocity;
	//private SpriteObject sprite;
	private String ipAddress;
	private int type;
	
//	public NetObject(int id, Vector2D p) {
//		super("");
//		init(id, p);
//	}

//	public NetObject(int id, Vector2D p, int type, boolean hasSprite) {
//		super("");
//		init(id,p,type,hasSprite);
//	}

//	public NetObject(int id, Vector2D p, int type) {
//		super("");
//		init(id,p,type);
//	}
	
//	public NetObject(int id, Vector2D p, int type, Vector2D v) {
//		super("");
//		init(id,p,type);
//		setVelocity(v);
//	}
	
	public NetObject(String rsc) {
		super(rsc);
	}
	
	public NetObject(String rsc, Vector2D p) {
		super(rsc);
		init(p);
	}

	public NetObject(String rsc, Vector2D p, int type, boolean hasSprite) {
		super(rsc);
		init(p,type,hasSprite);
	}

	public NetObject(String rsc, Vector2D p, int type) {
		super(rsc);
		init(p,type);
	}
	
	public NetObject(String rsc, Vector2D p, int type, Vector2D v) {
		super(rsc);
		init(p,type);
		setVelocity(v);
	}
	
	public void init (Vector2D p, int t, boolean hasSprite) {
		init(p,t);
		
		if (hasSprite) {
			switch(type) {
				case PLAYER:
					frames = ResourceFactory.getFactory().getFrames("player");
					break;
				case BULLET:
					frames = ResourceFactory.getFactory().getFrames("player");
					break;
			}
		}
		width = frames.get(0).getWidth();
		height = frames.get(0).getHeight();
		active = true;
	}
	
	public void init (Vector2D p, int t) {
		init(p);
		type = t;	
	}
	
	public void init (Vector2D p) {
		//sprite = null;
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
	
	/*public void update(long deltaMs) {
		Vector2D p = getPosition();
		Vector2D v = getVelocity();
		
		setPosition(new Vector2D(p.getX()+v.getX()*deltaMs,
				                 p.getY()+v.getY()*deltaMs));
	
		if (sprite != null)
			sprite.setPosition(getPosition());
	}*/
	
	//public SpriteObject getSprite () { return sprite; }
	
	public int getId() { return id; }
	
	//public Vector2D getPosition() { return position; }
	//public void setPosition(Vector2D p) { position = p; }
	
	//public Vector2D getVelocity() { return velocity; }
	//public void setVelocity(Vector2D v) { velocity = v; }
	
	public String getIp() { return ipAddress; }
	public void setIp(String ip) { ipAddress = ip; }
	
	public int getType() { return type; }
}