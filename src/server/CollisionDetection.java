package server;

import client.Client;
import jig.engine.util.Vector2D;
import net.NetObject;

public class CollisionDetection {
	public CollisionDetection() {
		
	}
	
	public void checkBox(NetObject n) {
		Vector2D p = n.getPosition();
		
		if (p.getX() < 0 || p.getX() > Client.WORLD_WIDTH)
			n.setVelocity(new Vector2D(n.getVelocity().getX()*-1, n.getVelocity().getY()));

		if (p.getY() < 0 || p.getY() > Client.WORLD_HEIGHT)
			n.setVelocity(new Vector2D(n.getVelocity().getX(), n.getVelocity().getY()*-1));

	}
}
