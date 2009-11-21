package net;

import java.util.Collection;
import java.util.Hashtable;

import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.BodyLayer;

public class GameState {

	private int seq_num = 0;
	
	public BodyLayer<NetObject> boxes;
	private Hashtable<Integer, NetObject> objectList = new Hashtable<Integer, NetObject>();
	
	public GameState() { 
		boxes = new AbstractBodyLayer.NoUpdate<NetObject>();
	}
	
	public int getSeqNum() { return seq_num; }
	public void setSeqNum(int n) { seq_num = n; }
	
	public void add(NetObject p) { 
		boxes.add(p);
		objectList.put(p.getId(), p);
	}
	
	public Hashtable<Integer, NetObject> getHashtable() {
		return objectList;
	}
	
	public Collection<NetObject> getNetObjects() { 
		return objectList.values(); 
	} 
}
