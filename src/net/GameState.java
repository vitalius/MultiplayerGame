package net;
import java.util.Hashtable;
import jig.engine.util.Vector2D;


public class GameState {

	public static final int ACTION_MOVE = 0;
	public static final int ADD_PLAYER  = 1;
	
	private int seq_num = 0;
	
	Hashtable<Integer, NetObject> playerList = new Hashtable<Integer, NetObject>();

	public GameState() { }
	public int getSeqNum() { return seq_num; }
	public void setSeqNum(int n) { seq_num = n; }
	public void addPlayer(NetObject p) { playerList.put(p.getId(), p); }
	
	/**
	 * Encode current game state into a String
	 * 
	 * @return encode string
	 */
	public String encode() {
		String output = seq_num+"#";
		
		for (NetObject p : playerList.values()) {
			output += p.getId()+"$";
			output += p.getPosition().getX()+"$";
			output += p.getPosition().getY();
			output += "%";
		}
		
		return output;
	}

	/**
	 * Decode current game state
	 * 
	 * @param input
	 */
	public void decode(String input) {
		String[] token = input.split("#");
		
		// Sequence number
		seq_num = Integer.valueOf(token[0]).intValue();
		
		// Players
		String player[] = token[1].split("%");
		for (int i=0; i<player.length; i++) {
			String attr[] = player[i].split("\\$");
			int id = Integer.valueOf(attr[0]).intValue();
			double x = Double.valueOf(attr[1]).doubleValue();
			double y = Double.valueOf(attr[2]).doubleValue();
			
			playerList.get(id).setPosition(new Vector2D(x,y));
		}
	}
	
	/**
	 * Process clients action request
	 * 
	 * @param a
	 */
	public void processAction(String a) {
		String[] token = a.split(":");
		int id = Integer.valueOf(token[0]).intValue();
		int action = Integer.valueOf(token[1]).intValue();
	
		switch(action) {
		case ACTION_MOVE:
			seq_num++;
			double x = Double.valueOf(token[2]).doubleValue();
			double y = Double.valueOf(token[3]).doubleValue();
			playerList.get(id).setPosition(new Vector2D(x,y));
			break;
		case ADD_PLAYER:
			seq_num++;
			break;
		}
		
	}
}
