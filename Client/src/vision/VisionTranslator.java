package vision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	private VisionSnapShot visionSnapShot;
	private double visionScale;
	
	public VisionTranslator(boolean testMode, double visionScale) {
		visionController = new VisionController(testMode, "a.jpg");
		this.visionScale = visionScale;
	}
	
	public MapState getProcessedMap() {
		
		Thread th = new Thread(visionController);
		th.start();
		
		try {
			th.join(1000);
			visionSnapShot = visionController.getSnapShot();
			
			return new MapState(
					calculateBalls(),
					calculateCross(),
					calculateWalls(),
					calculateGoals().get(0),
					calculateGoals().get(1),
					calculateRobotLocation());
			
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
	}
	
	private ArrayList<Ball> calculateBalls() {
		ArrayList<Ball> balls = new ArrayList<Ball>();
		
		for(int i = 0; i < visionSnapShot.getBalls().get(0).sizeof(); i++) {
			int x = (int) (visionSnapShot.getBalls().get(i).get(0)/visionScale);
			int y = (int) (visionSnapShot.getBalls().get(i).get(1)/visionScale);
			Ball b = new Ball(x,y);
			balls.add(b);
		}
		return balls;
	}
	
	private ArrayList<Wall> calculateWalls() {
		ArrayList<Wall> walls = new ArrayList<Wall>();
		ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
		Comparator<Coordinate> sortCoord =  new Comparator<Coordinate>() {

			@Override
			public int compare(Coordinate c1, Coordinate c2) {
				return -Integer.compare(c1.x,c2.x);
			}
		};
		
		if (visionSnapShot.getWalls() != null) {
			for(int i = 0; i < visionSnapShot.getWalls()[0].length; i++) {
				int x = (int) (visionSnapShot.getWalls()[i][0]/visionScale);
				int y = (int) (visionSnapShot.getWalls()[i][1]/visionScale);
				
				Coordinate c = new Coordinate(x,y);
				coords.add(c);
				
			}
			
			Collections.sort(coords, sortCoord);
			
			for(int i = 0; i < coords.size(); i+=2) {
				Wall w = new Wall();
				if(coords.get(i).y > coords.get(i).y) {
					w.upper.x = coords.get(i).x;
					w.upper.y = coords.get(i).y;
					w.lower.x = coords.get(i+1).y;
					w.upper.x = coords.get(i+1).x;
				}
				else {
					w.upper.x = coords.get(i+1).x;
					w.upper.y = coords.get(i+1).y;
					w.lower.x = coords.get(i).y;
					w.upper.x = coords.get(i).x;
				}
				
				walls.add(w);
			}
			
		}
		
		return walls;
	}

	private Cross calculateCross() {
		ArrayList<Coordinate> obstacle_coord = new ArrayList<Coordinate>();
		
		for(int i = 0; i < visionSnapShot.getCross()[0].length; i++) {
			int x = (int) (visionSnapShot.getCross()[0][i]/visionScale);
			int y = (int) (visionSnapShot.getCross()[1][i]/visionScale);
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
			
			goal.coordinate1.x = (int) ((((wall.upper.x - wall.lower.x)/2) + wall.lower.x)/visionScale);
			goal.coordinate1.y = (int) ((((wall.upper.y - wall.lower.y)/2) + wall.lower.y)/visionScale);
			
			goals.add(goal);
		}
		
		return goals;
	}
	
	private RobotLocation calculateRobotLocation() {
		//TODO: Make calculateRobotLocation
		
		return null;
		
	}
	
}
