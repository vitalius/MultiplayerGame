package clients;

import java.util.Collection;
import java.util.Hashtable;

import jig.engine.physics.vpe.VanillaSphere;

import net.NetStateManager;
import net.NetObject;

public class ClientGameState {
	
	private Hashtable<Integer, VanillaSphere> spriteList = new Hashtable<Integer, VanillaSphere>();
	
	public ClientGameState () { }
	
	public void init(NetStateManager gameState) {
		for(NetObject no : gameState.getState().getNetObjects())
			init(no);
	}
	
	public void init(NetObject no) {
		if (spriteList.containsKey(no.getId()))
			return;
		
		switch(no.getType()) {
		case NetObject.PLAYER:
			spriteList.put(no.getId(), new SpriteObject("player"));
			break;
		case NetObject.PLATFORM:
			spriteList.put(no.getId(), new SpriteObject("platform"));
			break;
		case NetObject.SMALLBOX:
			spriteList.put(no.getId(), new SpriteObject("smallbox"));
			break;
		case NetObject.GROUND:
			spriteList.put(no.getId(), new SpriteObject("ground"));
			break;
		}
		
	}
	
	public void sync(NetStateManager gameState) {
		for (NetObject no : gameState.getState().getNetObjects()) {
			if (spriteList.containsKey(no.getId())) {
				spriteList.get(no.getId()).setPosition(no.getPosition());
				spriteList.get(no.getId()).setVelocity(no.getVelocity());
				spriteList.get(no.getId()).setRotation(no.getRotation());
			} else
				init(no);
		}
	}
	
	public Collection<VanillaSphere> getSprites() { 
		return spriteList.values(); 
	} 
}
