package model;

public class Cross {
	
	public Coordinate centerCoordinate;
	public double radius;
	
	public Cross(Coordinate centerCoordinate, double radius) {
		this.centerCoordinate = centerCoordinate;
		this.radius = radius;
	}

	@Override
	public String toString() {
		return "Cross [centerCoordinate=" + centerCoordinate + ", radius=" + radius + "]";
	}
	
}
