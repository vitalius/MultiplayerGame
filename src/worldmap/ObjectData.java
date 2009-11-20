package worldmap;

// Object data for store till level is loaded.
public class ObjectData {
	public String type;
	public int x = 0;
	public int y = 0;
	public double rotation = 0.0;
	
	ObjectData(String Type, int X, int Y, int Rotation) {
		type = Type;
		x = X;
		y = Y;
		// Convert to radian
		rotation = (Rotation / 360) * 2 * Math.PI;
	}
}
