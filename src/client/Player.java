package client;

import java.awt.Point;

import net.NetObject;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Player extends VanillaAARectangle {

	private NetObject nObj;
	private TcpClient tcp;

	private boolean facingLeft = true;

	public Player() {
		super("player");
		position = new Vector2D(0, 0);
	}

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
		facingLeft = true;
		tcp.sendMove(nObj.getId(), new Vector2D(pos.getX() - 5, pos.getY()));
	}

	public void moveRight() {
		Vector2D pos = nObj.getPosition();
		facingLeft = false;
		tcp.sendMove(nObj.getId(), new Vector2D(pos.getX() + 5, pos.getY()));
	}

	public void moveUp() {
		Vector2D pos = nObj.getPosition();
		tcp.sendMove(nObj.getId(), new Vector2D(pos.getX(), pos.getY() - 5));
	}

	public void moveDown() {
		Vector2D pos = nObj.getPosition();
		tcp.sendMove(nObj.getId(), new Vector2D(pos.getX(), pos.getY() + 5));
	}

	public void moveStop() {
		setVelocity(new Vector2D(0, 0));
	}

	// Tests if facing the direction gun is pointing
	// If so sends to server that weapon was fired.
	// Otherwise nothing happens.
	public void fireWeapon(Point point) {
		double deltaX = position.getX() - point.x;
		if (deltaX > 0 && facingLeft) {
			System.out.println("Player fired weapon to left!");
		} else if (deltaX < 0 && !facingLeft) {
			System.out.println("Player fired weapon to right!");
		} else {
			System.out.println("Player tried to fire but was facing other way!");
		}
	}

	@Override
	public void update(long deltaMs) {
		setPosition(nObj.getPosition());
	}
}