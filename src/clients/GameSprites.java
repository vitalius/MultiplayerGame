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
			so = new PlayerObject("player");
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
		}
	}
	
	public void sync(NetStateManager gameState) {
		for (NetObject no : gameState.getState().getNetObjects()) {
			if (spriteList.containsKey(no.getId())) {
					
				Vector2D p = no.getPosition();
				spriteList.get(no.getId()).setPosition(p);
				//System.out.println(no.getVelocity());
				spriteList.get(no.getId()).setVelocity(no.getVelocity());
				spriteList.get(no.getId()).setRotation(no.getRotation());
			} else
				init(no);
		}
	
	}
	
	/*
	public synchronized void sync(NetStateManager gameState, Vector2D offset) {
			for (NetObject no : gameState.getState().getNetObjects()) {
				if (spriteList.containsKey(no.getId())) {
					
					// fixing the offset, because in jig, rectangle extending VanillaShere is just a giant sphere
					SpriteObject s = (SpriteObject)spriteList.get(no.getId());
					Vector2D p = no.getPosition();
					Vector2D newPos = new Vector2D(p.getX()-(s.getRadius()-s.getImgWidth()/2) - offset.getX(), 
												   p.getY()-(s.getRadius()-s.getImgHeight()/2) - offset.getY());
					spriteList.get(no.getId()).setPosition(newPos);
					//System.out.println(no.getVelocity());
					spriteList.get(no.getId()).setVelocity(no.getVelocity());
					spriteList.get(no.getId()).setRotation(no.getRotation());
				} else
					init(no);
			}
	}
	*/
	
	public Collection<GameObject> getSprites() { 
		return spriteList.values(); 
	}
	
	public BodyLayer<GameObject> getLayer() { 
		return layer;
	}
}
