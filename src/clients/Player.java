package clients;

import jig.engine.util.Vector2D;
import net.Action;
import net.Protocol;


/**
 * Client version of the player
 * 
 * Controls one NetObject, clients character
 * 
 */
public class Player {
	
	public final static int JOINED  = 0;
	public final static int WAITING = 1;
	
	private int playerId;
	private TcpSender tcp;
	private Protocol prot;
	private Action input;
	public int state;
	
	public Player(int id, TcpSender t) {
		playerId = id;
		tcp = t;
		input = new Action(id, Action.INPUT);
		prot = new Protocol();
		state = WAITING;
	}

	
	public void move(Action a) {	
		if (input.equals(a))
			return;
	
		tcp.sendSocket(prot.encodeAction(a));
		input.copy(a);
	}
	
	public void join(String ip) {
		Action join = new Action(playerId, Action.JOIN_REQUEST, tcp.getMyIP());
		tcp.sendSocket(prot.encodeAction(join));
	}
	
	
	public void shoot(Vector2D Spot) {
		Action shooty = new Action(playerId, Action.SHOOT, Spot);
		tcp.sendSocket(prot.encodeAction(shooty));
	}
	
	public void setID(int id) { playerId = id; } 
	public int getID() { return playerId; }
}