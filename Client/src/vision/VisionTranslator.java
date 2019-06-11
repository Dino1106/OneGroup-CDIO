package vision;

import java.util.ArrayList;

import org.bytedeco.javacpp.IntPointer;

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
	
	public VisionTranslator(boolean testMode) {
		visionController = new VisionController(testMode, "a.jpg");
		
		Thread th = new Thread(visionController);
		th.start();
		
		try {
			
			th.join();
			visionSnapShot = visionController.getSnapShot();
			MapState map = this.getProcessedMap();
			System.out.println(map.toString());
			
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
			int x = (int) visionSnapShot.getBalls().get(i).get(0);
			int y = (int) visionSnapShot.getBalls().get(i).get(1);
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
				w.upper.x = (int) visionSnapShot.getWalls().get(i).get(0);
				w.upper.y = (int) visionSnapShot.getWalls().get(i).get(1);
				w.upper.x = (int) visionSnapShot.getWalls().get(i).get(2);
				w.upper.y = (int) visionSnapShot.getWalls().get(i).get(3);
				
				walls.add(w);
			}
		}
		
		return walls;
	}

	private Cross calculateCross() {
		ArrayList<Coordinate> obstacle_coord = new ArrayList<Coordinate>();
		
		for(int i = 0; i < visionSnapShot.getCross()[0].length; i++) {
			int x = visionSnapShot.getCross()[0][i];
			int y = visionSnapShot.getCross()[1][i];
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
