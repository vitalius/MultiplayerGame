package client;


import net.NetObject;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Player extends VanillaAARectangle {
	
	private NetObject nObj;
	private TcpClient tcp;
	
	public Player(String a) {
		super(a);
		position = new Vector2D(0, 0);
	}

	public Player(String a, Vector2D init, NetObject no, TcpClient c) {
		super(a);
		position = init;
		nObj = no;
		tcp = c;
	}
	
	public void moveLeft() {
		Vector2D pos = nObj.getPosition();
		tcp.sendMove(nObj.getId(), new Vector2D(pos.getX()-5, pos.getY()));
	}
	
	public void moveRight() {
		Vector2D pos = nObj.getPosition();
		tcp.sendMove(nObj.getId(), new Vector2D(pos.getX()+5, pos.getY()));
	}
	
	public void moveUp() {
		Vector2D pos = nObj.getPosition();
		tcp.sendMove(nObj.getId(), new Vector2D(pos.getX(), pos.getY()-5));
	}
	
	public void moveDown() {
		Vector2D pos = nObj.getPosition();
		tcp.sendMove(nObj.getId(), new Vector2D(pos.getX(), pos.getY()+5));
	}

	@Override
	public void update(long deltaMs) {
		setPosition(nObj.getPosition());
	}
}