package model;

import java.util.ArrayList;

public class Route {

	public Coordinate robotCoordinate;
	public ArrayList<Coordinate> coordinates;
	
	public Route(Coordinate robotCoordinate, ArrayList<Coordinate> coordinates) {
		this.robotCoordinate = robotCoordinate;
		this.coordinates = coordinates;
	}
	
}
