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

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;
	public static final int LEFT_UP = 4;
	public static final int LEFT_DOWN = 5;
	public static final int RIGHT_UP = 6;
	public static final int RIGHT_DOWN = 7;
	public static final int HALT = 8;

	public int moveState;
	private int id;
	private TcpClient tcp;
	private Protocol prot;

	public Player(int player_id, TcpClient t) {
		id = player_id;
		tcp = t;
		moveState = HALT;
		prot = new Protocol();
	}

	public void move(int d) {

		if (moveState == d)
			return;

		Action move;
		moveState = d;
		switch (moveState) {
		case LEFT:
			move = new Action(id, Action.CHANGE_VELOCITY, new Vector2D(
					-DELTA_V, 0));
			tcp.sendSocket(prot.encodeAction(move));
			break;
		case RIGHT:
			move = new Action(id, Action.CHANGE_VELOCITY, new Vector2D(DELTA_V,
					0));
			tcp.sendSocket(prot.encodeAction(move));
			break;
		case UP:
			move = new Action(id, Action.CHANGE_VELOCITY, new Vector2D(0,
					-DELTA_V));
			tcp.sendSocket(prot.encodeAction(move));
			break;
		case DOWN:
			move = new Action(id, Action.CHANGE_VELOCITY, new Vector2D(0,
					DELTA_V));
			tcp.sendSocket(prot.encodeAction(move));
			break;
		case HALT:
			move = new Action(id, Action.CHANGE_VELOCITY, new Vector2D(0, 0));
			tcp.sendSocket(prot.encodeAction(move));
			break;
		}
	}

	public void move(int x, int y) {

		Action move;
		move = new Action(id, Action.CHANGE_VELOCITY, new Vector2D(DELTA_V * x,
				DELTA_V * y));
		tcp.sendSocket(prot.encodeAction(move));
		if (x == 0 && y == 0)
			moveState = HALT;
		if (y == 0) {
			if (x < 0)
				moveState = LEFT;
			else
				moveState = RIGHT;
		} else if (x == 0) {
			if (y < 0)
				moveState = UP;
			else
				moveState = DOWN;
		} else {
			if (x < 0 && y < 0)
				moveState = LEFT_UP;
			else if (x < 0 && y > 0)
				moveState = LEFT_DOWN;
			else if (x > 0 && y < 0)
				moveState = RIGHT_UP;
			else if (x > 0 && y > 0)
				moveState = RIGHT_DOWN;
		}
	}

	/**
	 * Doesn't work yet, DO NOT USE
	 * 
	 * @param ip
	 */
	public void join(String ip) {
		Action join = new Action(id, Action.JOIN, ip);
		tcp.sendSocket(prot.encodeAction(join));
	}
}