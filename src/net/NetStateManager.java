package net;

import java.util.Hashtable;

import world.GameObject;


public class NetStateManager {
	
	private NetState current = null;
	public Protocol prot = null;
	
	public NetStateManager () {
		current = new NetState();
		prot = new Protocol();
	}
	
	public NetState getState() {
		return current;
	}
	
	public void update (NetState g) {
		current = g;
	}
	
	public void update (String s) {
		update(prot.decode(s));
	}
	
	public void sync (String s) {
		NetState d = prot.decode(s);
		current.setSeqNum(d.getSeqNum());
		Hashtable<Integer, NetObject> objectList = current.getHashtable();
		
		for(NetObject n : d.getNetObjects()) {
			
			if(objectList.containsKey(n.getId())) {
				NetObject no = objectList.get(n.getId());
				no.setPosition(n.getPosition());
				no.setVelocity(n.getVelocity());
				no.setRotation(n.getRotation());
				no.setHealth(n.getHealth());
			}
			else
				objectList.put(n.getId(), n);
		}
	}
}
