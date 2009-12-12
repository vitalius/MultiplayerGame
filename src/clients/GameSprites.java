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
		GameObject go = null;
		switch(no.getType()) {
		case GameObject.PLAYER:
			go = new PlayerObject(SPRITES + "#Player");
			spriteList.put(no.getId(), go);
			layer.add(go);
			break;
		case GameObject.SMALLBOX:
			go = new GameObject(SPRITES + "#Crate");
			spriteList.put(no.getId(), go);
			layer.add(go);
			break;
		case GameObject.PLAYERSPAWN:
			go = new GameObject("playerspawn");
			spriteList.put(no.getId(), go);
			layer.add(go);
			break;
		case GameObject.DRUM:
			go = new GameObject(SPRITES + "#Drum");
			spriteList.put(no.getId(), go);
			layer.add(go);
			break;
		case GameObject.BULLET:
			go = new GameObject("bullet");
			spriteList.put(no.getId(), go);
			layer.add(go);
			break;
		case GameObject.GRENADE:
			go = new GameObject("grenade");
			spriteList.put(no.getId(), go);
			layer.add(go);
			break;
		}
		go.setActivation(true);
		go.setPosition(no.getPosition());
		go.setVelocity(no.getVelocity());
		go.setRotation(no.getRotation());
		if (go.type == GameObject.PLAYER) {
			((PlayerObject)go).setHealth(no.getHealth());
			((PlayerObject)go).setFrameIndex(no.getFrameIndex());
			((PlayerObject)go).setFrame(no.getFrameIndex());
			// set state here
		}
	}
	
	public void sync(NetStateManager gameState) {
		Hashtable<Integer, NetObject> netList = gameState.getState().objectList;
		
		for (GameObject go : spriteList.values()) {
			if (!netList.containsKey(go.getID())) {
				go.setActivation(false);
			}
		}
		
		for (NetObject no : netList.values()) {
			if (spriteList.containsKey(no.getId())) {
				GameObject go = spriteList.get(no.getId());
				go.setActivation(true);
				go.setPosition(no.getPosition());
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
