package clients;

import net.Action;
import net.Protocol;


/**
 * Client version of the player
 * 
 * Controls one NetObject, clients character
 * 
 */
public class Player {
	
	private int playerId;
	private TcpClient tcp;
	private Protocol prot;
	private Action input;
	
	public Player(int id, TcpClient t) {
		playerId = id;
		tcp = t;
		input = new Action(id, Action.INPUT);
		prot = new Protocol();
	}

	
	public void move(Action a) {	
		if (input.equals(a))
			return;
	
		tcp.sendSocket(prot.encodeAction(a));
		input.copy(a);
	}
	
	public void join(String ip) {
		Action join = new Action(playerId, Action.JOIN, ip);
		tcp.sendSocket(prot.encodeAction(join));
	}
}