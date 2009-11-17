package worldmap;

import jig.engine.physics.vpe.VanillaAARectangle;

public class TempObj extends VanillaAARectangle {

	public TempObj(String rsc, int width, int height) {
		super(rsc);
		this.width = width;
		this.height = height;
	}

	@Override
	public void update(long deltaMs) {
	}
}