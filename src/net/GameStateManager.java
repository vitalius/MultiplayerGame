package net;

import java.util.Hashtable;


public class GameStateManager {
	
	private GameState current = null;
	private Protocol prot = null;
	
	public GameStateManager () {
		current = new GameState();
		prot = new Protocol();
	}
	
	public GameState getState() {
		return current;
	}
	
	public void update (GameState g) {
		current = g;
	}
	
	public void update (String s) {
		update(prot.decode(s));
	}
	
	public void sync (String s) {
		GameState d = prot.decode(s);
		current.setSeqNum(d.getSeqNum());
		Hashtable<Integer, NetObject> objectList = current.getHashtable();
		
		for(NetObject n : d.getNetObjects()) {
			
			if(objectList.containsKey(n.getId())) {
				objectList.get(n.getId()).setPosition(n.getPosition());
				objectList.get(n.getId()).setVelocity(n.getVelocity());
			}
			else
				objectList.put(n.getId(), n);
		}
	}
	
	
	public void processAction (final String s) {
		Action a = prot.decodeAction(s);
		Hashtable<Integer, NetObject> objectList = current.getHashtable();
		
		if (!objectList.containsKey(a.getId()))
			return;
		
		switch(a.getType()) {
		case Action.CHANGE_VELOCITY:
			objectList.get(a.getId()).setVelocity(a.getArg());
			break;
		case Action.CHANGE_POSITION:
			objectList.get(a.getId()).setPosition(a.getArg());
			break;	
		}
	}
}
