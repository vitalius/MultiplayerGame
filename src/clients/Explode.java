package clients;

import jig.engine.physics.Body;

public class Explode extends Body {

	int slowdown = 0;
	int counter = 0;

	public Explode(String imgrsc) {
		super(imgrsc);
	}

	@Override
	public void update(long deltaMs) {
		if (slowdown < 50) {
			slowdown += deltaMs;
		} else {
			slowdown = 0;
			counter += 1;
			// counter = counter % this.getFrameCount();
			this.setFrame(counter);
		}
		if (counter == getFrameCount()) {
			this.setActivation(false);
		}
	}

	public void reset() {
		counter = 0;
		slowdown = 0;
		this.setActivation(true);
		setFrame(0);
	}
}