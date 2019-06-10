import java.io.Serializable;

public class Coordinate implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3869446341373817110L;
	
	public int x, y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

}
