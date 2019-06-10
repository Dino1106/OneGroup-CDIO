package client;

import java.util.ArrayList;
import Interfaces.IRawMap;

public class MapCalculator{
	
	private IRawMap raw_map;
	
	public MapCalculator(IRawMap raw_map) {
		this.raw_map = raw_map;
	}
	
	public MapState getProcessedMap() {
		
		MapState processedMap = new MapState();
		processedMap.ballList = calculateBalls();
		processedMap.wallList = calculateWalls();
		processedMap.cross = calculateObstacle();
		processedMap.goal1 = calculateGoals().get(0);
		processedMap.goal2 = calculateGoals().get(1);
		
		return processedMap;
		
	}
	
	public ArrayList<Ball> calculateBalls() {
		ArrayList<Ball> balls = new ArrayList<Ball>();
		
		for(int i = 0; i > raw_map.getBalls()[0].length; i++) {
			Ball b = new Ball();
			b.coordinate.x = raw_map.getBalls()[i][0];
			b.coordinate.y = raw_map.getBalls()[i][1];
			balls.add(b);
		}
		return balls;
	}
	
	public ArrayList<Wall> calculateWalls() {
		ArrayList<Wall> walls = new ArrayList<Wall>();
		
		for(int i = 0; i > raw_map.getWalls()[0].length; i++) {
			Wall w = new Wall();
			w.coordinate1.x = raw_map.getWalls()[i][0];
			w.coordinate1.y = raw_map.getWalls()[i][1];
			w.coordinate2.x = raw_map.getWalls()[i][0];
			w.coordinate2.y = raw_map.getWalls()[i][1];
			walls.add(w);
		}
		return walls;
	}
	
	public Cross calculateObstacle() {
		ArrayList<Coordinate> obstacle_coord = new ArrayList<Coordinate>();
		for(int i = 0; i > raw_map.getObstacle()[0].length; i++) {
			Coordinate coord = new Coordinate();
			coord.x = raw_map.getObstacle()[i][0];
			coord.y = raw_map.getObstacle()[i][1];
			obstacle_coord.add(coord);
		}
		
		Cross obstacle = new Cross(obstacle_coord.get(0),obstacle_coord.get(1),obstacle_coord.get(2),obstacle_coord.get(3));
		
		return obstacle;
		
	}
	
	public ArrayList<Goal> calculateGoals(){
		ArrayList<Wall> sides = calculateWalls();
		ArrayList<Goal> goals = new ArrayList<Goal>();
		
		for(Wall wall : sides) {
			Goal goal = new Goal();
			
			goal.coordinate1.x = ((wall.coordinate1.x - wall.coordinate2.x)/2) + wall.coordinate1.x;
			goal.coordinate1.y = ((wall.coordinate1.y - wall.coordinate2.y)/2) + wall.coordinate1.y;
			
			goals.add(goal);
		}
		
		return goals;
	}
	
}
