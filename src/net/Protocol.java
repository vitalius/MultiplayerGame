package net;

import world.GameObject;
import jig.engine.util.Vector2D;

/**
 * A collections of methods that encode class information into a String
 * and also decode classes from information String
 * 
 * @author vitaliy
 * @author Josh
 * @author Rolf
 *
 */
public class Protocol {
	
	public Protocol() {
		
	}

	public String encodeAction (Action a) {
		String output = a.getID()+"#";
		output += a.getType() + "#";
		
		switch(a.getType()) {
			case Action.JOIN_ACCEPT:
			case Action.JOIN_REQUEST:
			case Action.LEAVE_SERVER:
			case Action.SPAWN:
			case Action.TIMER:
				output += a.getMsg() + "#";
				break;
			case Action.INPUT:
				output += (a.jet    ? "1" : "0") + "#";
				output += (a.crouch  ? "1" : "0") + "#";
				output += (a.left  ? "1" : "0") + "#";
				output += (a.right ? "1" : "0") + "#";
				output += (a.jump  ? "1" : "0") + "#";
				output += (a.faceLeft ? "1" : "0") + "#";
				output += a.weapon + "#";
				break;
			case Action.CHANGE_VELOCITY:
			case Action.CHANGE_POSITION:
			case Action.SHOOT:
			case Action.EXPLOSION:
			case Action.SHOTGUNSFX:
			case Action.RIFLESFX:
				output += a.getArg().getX() + "#";
				output += a.getArg().getY() + "#";
				break;
			case Action.CHANGE_HEALTH:
			case Action.CHANGE_JETPACK:
				output += a.getDouble() + "#";
				break;
			case Action.TALK:
				output += a.getMsg() + "#";
				break;
			default:
				break;
		}
		return output;
	}
	
	public Action decodeAction (String input) {
		Action returnAction;
		String[] token = input.split("#");
		
		// fix messed up packets. Assumes messed up packets has 2 or less #s.
		if( token.length <= 2) {
			return new Action(0, Action.DO_NOTHING);
		}
		
		//System.out.println(input + " " + token[0]);

		int id = Integer.valueOf(token[0]).intValue();
		int type = Integer.valueOf(token[1]).intValue();
		
		double x, y, dou;
		
		switch (type) {
		case Action.JOIN_ACCEPT:
		case Action.JOIN_REQUEST:
		case Action.LEAVE_SERVER:
		case Action.SPAWN:
		case Action.TIMER:
			returnAction = new Action(id, type, token[2]);
			break;
		case Action.INPUT:
			returnAction = new Action(id,type);
			returnAction.jet    = Integer.valueOf(token[2]).intValue() == 1 ? true : false;
			returnAction.crouch  = Integer.valueOf(token[3]).intValue() == 1 ? true : false;
			returnAction.left  = Integer.valueOf(token[4]).intValue() == 1 ? true : false;
			returnAction.right = Integer.valueOf(token[5]).intValue() == 1 ? true : false;
			returnAction.jump  = Integer.valueOf(token[6]).intValue() == 1 ? true : false;
			returnAction.faceLeft = Integer.valueOf(token[7]).intValue() == 1 ? true : false;
			returnAction.weapon = Integer.valueOf(token[8]);
			break;
		case Action.CHANGE_VELOCITY:
		case Action.CHANGE_POSITION:
		case Action.SHOOT:
		case Action.EXPLOSION:
		case Action.SHOTGUNSFX:
		case Action.RIFLESFX:
			x = Double.valueOf(token[2]).doubleValue();
			y = Double.valueOf(token[3]).doubleValue();
			returnAction = new Action(id, type, new Vector2D(x,y));
			break;
		case Action.CHANGE_HEALTH:
		case Action.CHANGE_JETPACK:
			dou = Double.valueOf(token[2]).doubleValue();
			returnAction = new Action(id, type, dou);
			break;			
		case Action.TALK:
			returnAction = new Action(id, type, token[2]);
			break;
		default:
			returnAction = new Action(0, Action.DO_NOTHING);
			break;
		}
		
		return returnAction;
	}
	
	
	/**
	 * Encoding game state into a string
	 * 
	 * @param gs
	 * @return
	 */
	public String encode(NetState gs) {
		String output = gs.getSeqNum()+"*";
		
		for (NetObject p : gs.getNetObjects()) {
			if (!p.isActive() && p.getType() != GameObject.PLAYER) continue; // don't send inactives
			output += p.getId()+"$";
			output += p.getType()+"$";
			output += (float)p.getPosition().getX()+"$";
			output += (float)p.getPosition().getY()+"$";
			output += (float)p.getVelocity().getX()+"$";
			output += (float)p.getVelocity().getY()+"$";
			output += (float)p.getRotation()+"$";
			output += p.getHealth()+"$";
			output += p.getFrameIndex();
			output += "%";
		}
		
		output += "*";
		
		for (Action a : gs.getActions()) {
			output += encodeAction(a);
			output += "@";
		}
		//System.out.println("Protocol encode output: " + output);
		//System.out.println("NET MSG LEN:"+output.length());
		return output;
	}

	/**
	 * Decoding a NetState from a string
	 * 
	 * FORMAT:
	 * 			|---- this section repeats for multiple objects, delim by '%' ---| |-- Actions ------|
	 *   SEQ_NUM#OBJ_ID$OBJ_TYPE$POS_X$POS_Y$V_X$V_Y$ROTATION$HEALTH$FRAME_INDEX$%#ACTION_ID$TYPE_ID$@
	 * 
	 * @param input
	 * @return
	 */
	public NetState decode(String input) {
		NetState retState = new NetState();
		String[] token = input.split("\\*");
		
		// Sequence number
		int seq_num = Integer.valueOf(token[0]).intValue();
		retState.setSeqNum(seq_num);
		
		// Objects
		//System.out.println("Protocol decode input: " + input);
		String player[] = token[1].split("%");
		for (int i=0; i<player.length; i++) {
			String attr[] = player[i].split("\\$");
			int id = Integer.valueOf(attr[0]).intValue();
			int type = Integer.valueOf(attr[1]).intValue();
			double x = Double.valueOf(attr[2]).doubleValue();
			double y = Double.valueOf(attr[3]).doubleValue();
			double vx = Double.valueOf(attr[4]).doubleValue();
			double vy = Double.valueOf(attr[5]).doubleValue();
			double r = Double.valueOf(attr[6]).doubleValue();
			int h = Integer.valueOf(attr[7]).intValue();
			int f = Integer.valueOf(attr[8]).intValue();
			
			NetObject n = new NetObject(id, new Vector2D(x,y), type);
			n.setFrameIndex(f);
			n.setVelocity(new Vector2D(vx,vy));
			n.setRotation(r);
			n.setHealth(h);
			retState.add(n);
		}
		
		/* Check if there are actions that need to be parsed */
		if (token.length < 3)
			return retState;

		String action[] = token[2].split("@");
		for (int i=0; i<action.length; i++)
			retState.addAction(decodeAction(action[i]));
		
		return retState;
	}
}
