package clients;

import java.util.Collection;
import java.util.Hashtable;
import world.PlayerObject;
import world.GameObject;

import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.BodyLayer;
import jig.engine.util.Vector2D;

import net.NetStateManager;
import net.NetObject;

public class GameSprites {
	
	BodyLayer<GameObject> layer;
	
	public Hashtable<Integer, GameObject> spriteList = new Hashtable<Integer, GameObject>();
	
	static final String SPRITES = "res/2Destruction-spritesheet.png";
	
	static final public int LOC_PLAYER_RUN = 0;
	static final public int LOC_PLAYER_STAND = 0;
	static final public int ROWDOWN = 36;
	
	
	public GameSprites () {
		layer = new AbstractBodyLayer.IterativeUpdate<GameObject>();
	}
	
	public void init(NetStateManager gameState) {
		for(NetObject no : gameState.getState().getNetObjects())
			init(no);
	}
	
	public void init(NetObject no) {
		if (spriteList.containsKey(no.getId()))
			return;
		GameObject so = null;
		switch(no.getType()) {
		case GameObject.PLAYER:
			so = new PlayerObject("player");//SPRITES + "#Player");
			spriteList.put(no.getId(), so);
			layer.add(so);
			break;
		case GameObject.SMALLBOX:
			so = new GameObject("smallbox");
			spriteList.put(no.getId(), so);
			layer.add(so);
			break;
		case GameObject.PLAYERSPAWN:
			so = new GameObject("playerspawn");
			spriteList.put(no.getId(), so);
			layer.add(so);
			break;
		case GameObject.BULLET:
			so = new GameObject("bullet");
			spriteList.put(no.getId(), so);
			layer.add(so);
			break;
		case GameObject.GRENADE:
			so = new GameObject("grenade");
			spriteList.put(no.getId(), so);
			layer.add(so);
			break;
		}
	}
	
	public void sync(NetStateManager gameState) {
		for (NetObject no : gameState.getState().getNetObjects()) {
			if (spriteList.containsKey(no.getId())) {
					
				Vector2D p = no.getPosition();
				GameObject go = spriteList.get(no.getId());
				go.setPosition(p);
				//System.out.println(no.getVelocity());
				go.setVelocity(no.getVelocity());
				go.setRotation(no.getRotation());
				if (go.type == GameObject.PLAYER) {
					//System.out.println("sync health: " + no.getHealth());
					((PlayerObject)go).setHealth(no.getHealth());
					// set state here
				}
			} else
				init(no);
		}
	
	}
	
	public Collection<GameObject> getSprites() { 
		return spriteList.values(); 
	}
	
	public BodyLayer<GameObject> getLayer() { 
		return layer;
	}
}
