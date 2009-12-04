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

	private Hashtable<Integer, GameObject> goList = new Hashtable<Integer, GameObject>();
	private BodyLayer<GameObject> layer;
	private Random generator = new Random();

	public ServerGameState() {
		netState = new NetState();
		layer = new AbstractBodyLayer.NoUpdate<GameObject>();
	}

	public int getUniqueId() {
		Set<Integer> usedIds = goList.keySet();

		int id = 100;
		while (usedIds.contains(id))
			id = generator.nextInt(65000);

		return id;
	}

	/**
	 * 
	 * Adds a 'Box' to the boxList and create a corresponding NetObject to send
	 * to the clients
	 * 
	 * 
	 * @param b
	 *            - Box
	 * @param type
	 *            - type of object, this can be a player, a bullet, etc
	 */
	public int add(GameObject go, int type) {
		int id = getUniqueId();

		if (goList.containsKey(id))
			return id;

		goList.put(id, go);
		layer.add(go);
		if (go.type != GameObject.CUSTOM) {
			netState.add(new NetObject(id, go.getPosition(), type));
		}	
		return id;
	}

	/**
	 * This method is called when adding a player because id is already provided
	 * 
	 */
	public void add(int id, GameObject box, int type) {
		if (goList.containsKey(id))
			return;

		goList.put(id, box);
		layer.add(box);
		if (box.type != GameObject.CUSTOM) {
			netState.add(new NetObject(id, box.getPosition(), type));
		}
	}

	public void update() {
		clampAndUpdatePlayers();
		
		Hashtable<Integer, NetObject> netList = netState.getHashtable();
		for (Integer i : goList.keySet()) {
			GameObject go = goList.get(i);
			if (go != null) {
				NetObject no = netList.get(i);
				if (no != null) {

					// null point common here...
					no.setPosition(go.getPosition());

					// System.out.println(b.getVelocity());
					// Box's velocity vector is way too high for some reason,
					// maybe it should be scaled by DELTA_MS, i dunno
					no.setVelocity(go.getVelocity());

					no.setRotation(go.getRotation());
				}
			}
			PlayerObject p = null;
			if (go.type == GameObject.PLAYER) {
				p = (PlayerObject) go;
				p.clamp();
			}
		}
	}

	public Hashtable<Integer, GameObject> getHashtable() {
		return goList;
	}

	/**
	 * Returns a Layer which can be added to rendering layer
	 * 
	 * @return
	 */
	public BodyLayer<GameObject> getBoxes() {
		return layer;
	}

	/**
	 * clamp players angle and velocity
	 * 
	 * @return
	 */
	public void clampAndUpdatePlayers() {
		PlayerObject p = null;
		for (GameObject go : goList.values())
			if (go.type == GameObject.PLAYER)
				p = (PlayerObject) go;
				//p.clamp(); called by updatePlayerState() now.
				p.updatePlayerState();
		return;
	}

	public NetState getNetState() {
		return netState;
	}

	public void removeByID(int ID) {
		// GameObject ret = goList.get(ID);
		//goList.get(ID).setActivation(false);
		goList.remove(ID);
		// return ret;
	}
}
