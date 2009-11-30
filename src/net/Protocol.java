package net;

import jig.engine.util.Vector2D;

/**
 * A collections of methods that encode class information into a String
 * and also decode classes from information String
 * 
 * @author vitaliy
 *
 */
public class Protocol {
	
	public Protocol() {
		
	}

	public String encodeAction (Action a) {
		String output = a.getId()+"#";
		output += a.getType() + "#";
		
		switch(a.getType()) {
			case Action.CHANGE_VELOCITY:
			case Action.CHANGE_POSITION:
				output += a.getArg().getX() + "#";
				output += a.getArg().getY() + "#";
				break;
			case Action.JOIN:
				output += a.getMsg() + "#";
				break;
			case Action.INPUT:
				output += (a.jet    ? "1" : "0") + "#";
				output += (a.crouch  ? "1" : "0") + "#";
				output += (a.left  ? "1" : "0") + "#";
				output += (a.right ? "1" : "0") + "#";
				output += (a.jump  ? "1" : "0") + "#";
				output += (a.shoot ? "1" : "0") + "#";
				break;
			case Action.SHOOT:
				output += a.getArg().getX() + "#";
				output += a.getArg().getY() + "#";
				break;
			default:
				break;
		}
		return output;
	}
	
	public Action decodeAction (String input) {
		Action returnAction;
		String[] token = input.split("#");
		
		int id = Integer.valueOf(token[0]).intValue();	
		int type = Integer.valueOf(token[1]).intValue();
		
		double x, y;
		
		switch (type) {
		case Action.CHANGE_VELOCITY:
		case Action.CHANGE_POSITION:
			x = Double.valueOf(token[2]).doubleValue();
			y = Double.valueOf(token[3]).doubleValue();
			returnAction = new Action(id, type, new Vector2D(x,y));
			break;
		case Action.JOIN:
			returnAction = new Action(id, type, token[2]);
			break;
		case Action.INPUT:
			returnAction = new Action(id,type);
			returnAction.jet    = Integer.valueOf(token[2]).intValue() == 1 ? true : false;
			returnAction.crouch  = Integer.valueOf(token[3]).intValue() == 1 ? true : false;
			returnAction.left  = Integer.valueOf(token[4]).intValue() == 1 ? true : false;
			returnAction.right = Integer.valueOf(token[5]).intValue() == 1 ? true : false;
			returnAction.jump  = Integer.valueOf(token[6]).intValue() == 1 ? true : false;
			returnAction.shoot = Integer.valueOf(token[7]).intValue() == 1 ? true : false;
			break;
		case Action.SHOOT:
			x = Double.valueOf(token[2]).doubleValue();
			y = Double.valueOf(token[3]).doubleValue();
			returnAction = new Action(id, type, new Vector2D(x,y));
			break;
		default:
			returnAction = new Action(0, Action.DO_NOTHING);
			break;
		}
		
		return returnAction;
	}
	
	public String encode(NetState gs) {
		String output = gs.getSeqNum()+"#";
		
		for (NetObject p : gs.getNetObjects()) {
			output += p.getId()+"$";
			output += p.getType()+"$";
			output += (float)p.getPosition().getX()+"$";
			output += (float)p.getPosition().getY()+"$";
			output += (float)p.getVelocity().getX()+"$";
			output += (float)p.getVelocity().getY()+"$";
			output += (float)p.getRotation();
			output += "%";
		}
		
		return output;
	}


	public NetState decode(String input) {
		NetState retState = new NetState();
		String[] token = input.split("#");
		
		// Sequence number
		int seq_num = Integer.valueOf(token[0]).intValue();
		retState.setSeqNum(seq_num);
		
		// Objects
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
			
			NetObject n = new NetObject(id, new Vector2D(x,y), type);
			n.setVelocity(new Vector2D(vx,vy));
			n.setRotation(r);
			retState.add(n);
		}
		
		return retState;
	}
}
