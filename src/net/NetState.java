package net;

import java.util.Collection;
import java.util.Hashtable;

public class NetState {

	private int seq_num = 0;
	
	public Hashtable<Integer, NetObject> objectList = new Hashtable<Integer, NetObject>();
	public Hashtable<Integer, Action> actionList = new Hashtable<Integer, Action>();
	
	public NetState() { 
	}
	
	public int getSeqNum() { return seq_num; }
	public void setSeqNum(int n) { seq_num = n; }
	
	public void add(NetObject p) { 
		objectList.put(p.getId(), p);
	}
	
	public void clear() {
		objectList.clear();
		actionList.clear();
	}
	
	public Hashtable<Integer, NetObject> getHashtable() {
		return objectList;
	}
	
	public Collection<NetObject> getNetObjects() { 
		return objectList.values(); 
	}
	
	public void addAction(Action a) {
		actionList.put(a.getID(), a);
	}
	
	public Collection<Action> getActions() { 
		return actionList.values();
	}
	
	public void clearActions() {
		actionList.clear();
	}
}
