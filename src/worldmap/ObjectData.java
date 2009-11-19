package worldmap;

// Object data for store till level is loaded.
public class ObjectData {	
	public int x = 0;
	public int y = 0;
	public int width = 0;
	public int height = 0;
	public double rotation = 0.0;
	
	ObjectData(int xx, int yy, int ww, int hh, int rr) {
		x = xx;
		y = yy;
		width = ww;
		height = hh;
		rotation = (rr / 360) * 2 * Math.PI;
	}
}
