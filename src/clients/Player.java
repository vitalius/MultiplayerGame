package clients;

import net.Action;
import net.Protocol;

import jig.engine.util.Vector2D;

/**
 * Client version of the player
 * 
 * Controls one NetObject, clients character
 * 
 * @author vitaliy
 *
 */
public class Player {
	
	public static final double DELTA_V = 0.5;

	public static final int LEFT  = 0;
	public static final int RIGHT = 1;
	public static final int UP    = 2;
	public static final int DOWN  = 3;
	public static final int HALT  = 4;


	public int moveState;
	private int playerId;
	private TcpClient tcp;
	private Protocol prot;
	
	public Player(int id, TcpClient t) {
		playerId = id;
		tcp = t;
		moveState = HALT;
		prot = new Protocol();
	}
	
	public void move(int d) {

		if (moveState == d)
			return;
		
		Action move;
		moveState = d;
		switch(moveState) {
			case LEFT:
				move = new Action(playerId, Action.CHANGE_VELOCITY, new Vector2D(-DELTA_V, 0));
				tcp.sendSocket(prot.encodeAction(move));				
				break;
			case RIGHT:
				move = new Action(playerId, Action.CHANGE_VELOCITY, new Vector2D(DELTA_V, 0));
				tcp.sendSocket(prot.encodeAction(move));				
				break;
			case UP:
				move = new Action(playerId, Action.CHANGE_VELOCITY, new Vector2D(0, -DELTA_V));
				tcp.sendSocket(prot.encodeAction(move));				
				break;
			case DOWN:
				move = new Action(playerId, Action.CHANGE_VELOCITY, new Vector2D(0, DELTA_V));
				tcp.sendSocket(prot.encodeAction(move));				
				break;
			case HALT:
				move = new Action(playerId, Action.CHANGE_VELOCITY, new Vector2D(0, 0));
				tcp.sendSocket(prot.encodeAction(move));				
				break;
		}
	}
	
	/**
	 * Doesn't work yet, DO NOT USE
	 * @param ip
	 */
	public void join(String ip) {
		Action join = new Action(playerId, Action.JOIN, ip);
		tcp.sendSocket(prot.encodeAction(join));
	}
}