package vision;

import java.util.ArrayList;

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
		visionController = new VisionController(testMode, "e.jpg");
		this.visionScale = visionScale;
		
		Thread th = new Thread(visionController);
		th.start();
		
		try {
			
			th.join();
			visionSnapShot = visionController.getSnapShot();
			MapState map = getProcessedMap();
			System.out.println("\n\n\n\n HELO ----" + map.toString() + "\n\n\n\n");
			
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public MapState getProcessedMap() {
		
		//TODO: CalculateRobot method should be created instead of this hard-code.
		RobotLocation robot_start = new RobotLocation();
		robot_start.coordinate = new Coordinate(0, 0);
		
		return new MapState(
				calculateBalls(),
				calculateCross(),
				calculateWalls(),
				//TODO: Insert calculateGoals, when it works :D
				null, //calculateGoals().get(0),
				null, //calculateGoals().get(1),
				robot_start);
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
		if (visionSnapShot.getWalls() != null) {
			for(int i = 0; i < visionSnapShot.getWalls().get(0).sizeof(); i++) {
				Wall w = new Wall();
				w.upper.x = (int) (visionSnapShot.getWalls().get(i).get(0)/visionScale);
				w.upper.y = (int) (visionSnapShot.getWalls().get(i).get(1)/visionScale);
				w.lower.x = (int) (visionSnapShot.getWalls().get(i).get(2)/visionScale);
				w.lower.y = (int) (visionSnapShot.getWalls().get(i).get(3)/visionScale);
				
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
	
}
