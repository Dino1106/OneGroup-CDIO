package model;

public class Ball {
	
	public int x, y;
	public double height = 4;
	
	public Ball() {
		
	}
	
	public Ball(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Ball [x=" + x + ", y=" + y + "]";
	}

}
