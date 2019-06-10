package vision;

import java.util.ArrayList;
import Interfaces.IRawMap;
import Interfaces.IVisionConverter;
import model.Ball;
import model.Coordinate;
import model.Cross;
import model.Goal;
import model.MapState;
import model.RobotLocation;
import model.VisionSnapShot;
import model.Wall;

public class VisionTranslator {
	
	private VisionController visionController;
	
	public VisionTranslator() {
		
	}
	
	
	public MapState getProcessedMap() {
		
		RobotLocation robot_start = new RobotLocation();
		robot_start.coordinate.x = 0;
		robot_start.coordinate.y = 0;
		
		MapState processedMap = new MapState(calculateBalls(),calculateObstacle(),calculateWalls(),calculateGoals().get(0),calculateGoals().get(1),
				robot_start);
		
		return processedMap;
		
	}
	
	private ArrayList<Ball> calculateBalls() {
		ArrayList<Ball> balls = new ArrayList<Ball>();
		
		for(int i = 0; i > raw_map.getBalls()[0].length; i++) {
			Ball b = new Ball();
			b.coordinate.x = raw_map.getBalls()[i][0];
			b.coordinate.y = raw_map.getBalls()[i][1];
			balls.add(b);
		}
		return balls;
	}
	
	private ArrayList<Wall> calculateWalls() {
		ArrayList<Wall> walls = new ArrayList<Wall>();
		
		for(int i = 0; i > raw_map.getWalls()[0].length; i++) {
			Wall w = new Wall();
			w.upper.x = raw_map.getWalls()[i][0];
			w.upper.y = raw_map.getWalls()[i][1];
			w.lower.x = raw_map.getWalls()[i][0];
			w.lower.y = raw_map.getWalls()[i][1];
			walls.add(w);
		}
		return walls;
	}
	
	private Cross calculateObstacle() {
		ArrayList<Coordinate> obstacle_coord = new ArrayList<Coordinate>();
		for(int i = 0; i > raw_map.getObstacle()[0].length; i++) {
			int x = raw_map.getObstacle()[i][0];
			int y = raw_map.getObstacle()[i][1];
			obstacle_coord.add(new Coordinate(x, y));
		}
		
		Cross obstacle = new Cross(obstacle_coord.get(0),obstacle_coord.get(1),obstacle_coord.get(2),obstacle_coord.get(3));
		
		return obstacle;
		
	}
	
	private ArrayList<Goal> calculateGoals(){
		ArrayList<Wall> sides = calculateWalls();
		ArrayList<Goal> goals = new ArrayList<Goal>();
		
		for(Wall wall : sides) {
			Goal goal = new Goal();
			
			goal.coordinate1.x = ((wall.upper.x - wall.lower.x)/2) + wall.lower.x;
			goal.coordinate1.y = ((wall.upper.y - wall.lower.y)/2) + wall.lower.y;
			
			goals.add(goal);
		}
		
		return goals;
	}
	
}
