package model;

import java.util.ArrayList;

public class MapState {

	public ArrayList<Ball> ballList;
	public Cross cross;
	public ArrayList<Wall> wallList;
	public Goal goal1, goal2;
	public Robot robot;
	public ArrayList<Coordinate> quadrants;
	

	public MapState(ArrayList<Ball> ballList, Cross cross, ArrayList<Wall> wallList, Goal goal1, Goal goal2,
			Robot robotLocation, ArrayList<Coordinate> quadrants) {
		super();
		this.ballList = ballList;
		this.cross    = cross;
		this.wallList = wallList;
		this.goal1 = goal1;
		this.goal2 = goal2;
		this.robot = robotLocation;
		this.quadrants = quadrants;
	}


	@Override
	public String toString() {
		return "MapState [\nballList=" + ballList + ",\n cross=" + cross + ",\n wallList=" + wallList + ",\n goal1=" + goal1
				+ ",\n goal2=" + goal2 + ",\n robotLocation=" + robot + ",\n quadrants= "+ quadrants+"]";
	}
	
}
