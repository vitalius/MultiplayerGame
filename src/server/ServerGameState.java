package server;

import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import world.GameObject;
import world.PlayerObject;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.BodyLayer;
import net.NetObject;
import net.NetState;

public class ServerGameState {

	private NetState netState;
	private Hashtable<Integer, GameObject> goTable;
	private BodyLayer<GameObject> layer; // all layer
//	private BodyLayer<PlayerObject> pLayer; // player layer
//	private BodyLayer<GameObject> bLayer; // bullet layer
//	private BodyLayer<GameObject> dLayer; // dynamic layer
//	private BodyLayer<GameObject> sLayer; // static layer
	private Random generator = new Random();

	public ServerGameState() {
		netState = new NetState();
		goTable = new Hashtable<Integer, GameObject>();
		layer = new AbstractBodyLayer.NoUpdate<GameObject>();
		//bLayer = new AbstractBodyLayer.NoUpdate<GameObject>();
		//dLayer = new AbstractBodyLayer.NoUpdate<GameObject>();
		//sLayer = new AbstractBodyLayer.NoUpdate<GameObject>();
	}

	public int getUniqueId() {
		Set<Integer> usedIds = goTable.keySet();
		int id = 100;
		while (usedIds.contains(id))
			id = generator.nextInt(65000);
		return id;
	}
	
	/**
	 * This method is called when adding a player because id is already provided
	 * 
	 */
	public void addPlayer(int id, PlayerObject p) {
		if (goTable.containsKey(id)) return;
		p.setID(id);
		goTable.put(id, p);
		netState.add(new NetObject(id, p.getPosition(), GameObject.PLAYER));
		layer.add(p);
		//	netState.add(new NetObject(id, p.getPosition(), GameObject));
		//}
	}
	
	public void add(GameObject go) {
		if (go.type == GameObject.STATIC) {
			addStatic(go);
			return;
		}
		addDynamic(go);
	}
	
	// add dynamic objects such as boxes
	public int addDynamic(GameObject go) {
		int id = getUniqueId();
		go.setID(id);
		if (goTable.containsKey(id)) return id;

		//goTable.put(id, go);
		
		if (go.type == GameObject.BULLET) {
			layer.add(go);
		}
		else if (go.type == GameObject.SMALLBOX) {
			layer.add(go);
		}
		else {
			return -1;
		}
		goTable.put(id, go);
		netState.add(new NetObject(id, go.getPosition(), go.type));
		
		return id;
	}
	
	// add static object such as platforms
	public void addStatic(GameObject go) {
		layer.add(go);
	}

	public void update() {
		clampAndUpdatePlayers();
		
		Hashtable<Integer, NetObject> netList = netState.getHashtable();
		for (Integer i : goTable.keySet()) {
			GameObject go = goTable.get(i);
			if (go != null) {
				NetObject no = netList.get(i);
				if (no != null) {

					// null point common here...
					no.setPosition(go.getPosition());

					// System.out.println(b.getVelocity());
					no.setVelocity(go.getVelocity());

					no.setRotation(go.getRotation());
				}
			}
		}
	}

	public Hashtable<Integer, GameObject> getHashtable() {
		return goTable;
	}

	/**
	 * Returns a Layer which can be added to rendering layer
	 * 
	 * @return
	 */
	public BodyLayer<GameObject> getLayer() {
		return layer;
	}

//	public BodyLayer<GameObject> getbLayer() {
//		return bLayer;
//	}
//
//	public BodyLayer<GameObject> getdLayer() {
//		return dLayer;
//	}
//
//	public BodyLayer<GameObject> getsLayer() {
//		return sLayer;
//	}

	/**
	 * clamp players angle and velocity
	 * 
	 * @return
	 */
	public void clampAndUpdatePlayers() {
		PlayerObject p = null;
		for (GameObject go : layer)
			if (go.type == GameObject.PLAYER) {
				p = (PlayerObject) go;
				p.updatePlayerState();
			}
		return;
	}

	public NetState getNetState() {
		return netState;
	}

	public void removeByID(int ID) {
		// GameObject ret = goList.get(ID);
		//goList.get(ID).setActivation(false);
		goTable.remove(ID);
		netState.objectList.remove(ID);
		// return ret;
	}
}
