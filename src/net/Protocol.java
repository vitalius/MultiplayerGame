package net;

import jig.engine.util.Vector2D;

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
				output = a.getMsg() + "#";
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
		
		switch (type) {
		case Action.CHANGE_VELOCITY:
		case Action.CHANGE_POSITION:
			double x = Double.valueOf(token[2]).doubleValue();
			double y = Double.valueOf(token[3]).doubleValue();
			returnAction = new Action(id, type, new Vector2D(x,y));
			break;
		case Action.JOIN:
			returnAction = new Action(id, type,token[2]);
			break;
		default:
			returnAction = new Action(0, Action.DO_NOTHING);
			break;
		}
		
		return returnAction;
	}
	
	public String encode(GameState gs) {
		String output = gs.getSeqNum()+"#";
		
		for (NetObject p : gs.getNetObjects()) {
			output += p.getId()+"$";
			output += p.getType()+"$";
			output += (float)p.getPosition().getX()+"$";
			output += (float)p.getPosition().getY()+"$";
			output += (float)p.getVelocity().getX()+"$";
			output += (float)p.getVelocity().getY();
			output += "%";
		}
		
		return output;
	}


	public GameState decode(String input) {
		GameState retState = new GameState();
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
			
			NetObject n = new NetObject("player", new Vector2D(x,y), type, true);
			n.setVelocity(new Vector2D(vx,vy));
			retState.add(n);
		}
		
		return retState;
	}
}
