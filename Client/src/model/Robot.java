package model;

public class Robot {
	
	public Coordinate coordinate;
	public double orientation;
	public double height = 18.5;
	
	public Robot() {
		// Empty constructor
	}
	
	public Robot(Coordinate coordinate, double orientation) {
		this.coordinate = coordinate;
		this.orientation = orientation;
	}

	@Override
	public String toString() {
		return "RobotLocation [coordinate=" + coordinate + ", orientation=" + orientation + "]";
	}

}
