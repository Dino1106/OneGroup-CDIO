package model;

public class RobotLocation {
	
	public Coordinate coordinate;
	public int orientation;
	
	public RobotLocation(Coordinate coordinate, int orientation) {
		this.coordinate = coordinate;
		this.orientation = orientation;
	}

	@Override
	public String toString() {
		return "RobotLocation [coordinate=" + coordinate + ", orientation=" + orientation + "]";
	}

}
