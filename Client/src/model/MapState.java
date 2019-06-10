package model;

import java.util.ArrayList;

public class MapState {

	public ArrayList<Ball> ballList;
	public Cross cross;
	public ArrayList<Wall> wallList;
	public Goal goal1, goal2;
	public RobotLocation robotLocation;
	

	public MapState(ArrayList<Ball> ballList, Cross cross, ArrayList<Wall> wallList, Goal goal1, Goal goal2,
			RobotLocation robotLocation) {
		super();
		this.ballList = ballList;
		this.cross = cross;
		this.wallList = wallList;
		this.goal1 = goal1;
		this.goal2 = goal2;
		this.robotLocation = robotLocation;
	}

}
