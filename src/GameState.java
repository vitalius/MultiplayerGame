import java.util.Hashtable;
import jig.engine.util.Vector2D;


public class GameState {
	
	private int seq_num = 0;
	
	Hashtable<Integer, Player> playerList = new Hashtable<Integer, Player>();

	public GameState() { }
	public int getSeqNum() { return seq_num; }
	public void setSeqNum(int n) { seq_num = n; }
	public void addPlayer(Player p) { playerList.put(p.getId(), p); }
	
	/**
	 * Encode current game state into a String
	 * 
	 * @return encode string
	 */
	public String encode() {
		String output = "S:"+seq_num;

		output += "#";
		for (Player p : playerList.values()) {
			output += p.getId()+"$";
			output += p.getPosition().getX()+"$";
			output += p.getPosition().getY();
			output += "%";
		}
		output += "#";
		
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
		String[] seq = token[0].split(":");
		seq_num = Integer.valueOf(seq[1]).intValue();
		
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
}
