package model;

public class Cross {
	
	public Coordinate coordinateWest;
	public Coordinate coordinateNorth;
	public Coordinate coordinateEast;
	public Coordinate coordinateSouth;
	public Coordinate middle;
	
	public Cross(Coordinate coordinateWest, Coordinate coordinateNorth, Coordinate coordinateEast, Coordinate coordinateSouth) {
		super();
		this.coordinateWest = coordinateWest;
		this.coordinateNorth = coordinateNorth;
		this.coordinateEast = coordinateEast;
		this.coordinateSouth = coordinateSouth;
		middle.x = (coordinateWest.x + coordinateEast.x)/2;
		middle.y = (coordinateNorth.y + coordinateSouth.y)/2;
	}

	@Override
	public String toString() {
		return "Cross [coordinate1=" + coordinate1 + ", coordinate2=" + coordinate2 + ", coordinate3=" + coordinate3
				+ ", coordinate4=" + coordinate4 + "]";
	}
	
	
}
