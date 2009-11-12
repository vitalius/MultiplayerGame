package aux;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class SolidObject extends VanillaAARectangle {
	public SolidObject(String a) {
		super(a);
		position = new Vector2D(0, 0);
	}

	public SolidObject(String a, Vector2D init) {
		super(a);
		position = init;
	}

	@Override
	public void update(long deltaMs) {
		position = new Vector2D(position.getX() + (velocity.getX() * deltaMs)
				/ 1000, position.getY() + (velocity.getY() * deltaMs) / 1000);
	}
}