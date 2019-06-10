package client;

import java.io.Serializable;

public class Coordinate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4239112052905532281L;
	
	public int x, y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

}
