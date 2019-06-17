package model;

public class Ball {
	
	public double x, y;
	public double height = 4;
	
	public Ball() {
		//Empty
	}
	
	public Ball(double x2, double y2) {
		this.x = x2;
		this.y = y2;
	}

	@Override
	public String toString() {
		return "Ball [x=" + x + ", y=" + y + "]";
	}

}
