package net;

import java.util.Collection;
import java.util.Hashtable;

public class NetState {

	private int seq_num = 0;
	
	public Hashtable<Integer, NetObject> objectList = new Hashtable<Integer, NetObject>();
	
	public NetState() { 
	}
	
	public int getSeqNum() { return seq_num; }
	public void setSeqNum(int n) { seq_num = n; }
	
	public void add(NetObject p) { 
		objectList.put(p.getId(), p);
	}
	
	public void clear() {
		objectList.clear();
	}
	
	public Hashtable<Integer, NetObject> getHashtable() {
		return objectList;
	}
	
	public Collection<NetObject> getNetObjects() { 
		return objectList.values(); 
	} 
}
