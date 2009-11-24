package clients;

import jig.engine.physics.vpe.VanillaSphere;


public class SpriteObject extends VanillaSphere{
	
	private int imgHeight = 0;
	private int imgWidth = 0;
	
	public SpriteObject(String res) {
		super(res);
		imgHeight = frames.get(visibleFrame).getHeight();
		imgWidth = frames.get(visibleFrame).getWidth();
	}

	public int getImgHeight() {
		return imgHeight;
	}
	public int getImgWidth() {
		return imgWidth;
	}	
	@Override
	public void update(long deltaMs) {
		
	}
}
