package model;

public class Goal {
	
	public Coordinate coordinate1;
	public Coordinate coordinate2;
	public RobotLocation robotLocation;
	
	@Override
	public String toString() {
		return "Goal [coordinate1=" + coordinate1 + ", coordinate2=" + coordinate2 + ", robotLocation=" + robotLocation
				+ "]";
	}
	
	
}
