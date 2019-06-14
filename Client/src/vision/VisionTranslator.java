package vision;

import java.awt.geom.Point2D;
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
	private double visionScale = 1;

	private double cameraHeight = 150.0;
	private int cameraX, cameraY;

	private double robotHeight = 20.0;

	public VisionTranslator(boolean testMode) {
		visionController = new VisionController(testMode, 1);

		Thread th = new Thread(visionController);
		th.start();

		try {
			th.join();
			visionSnapShot = visionController.getSnapShot();

		} catch (InterruptedException e) {
			System.out.println(e.getStackTrace());
		}
	}

	public MapState getProcessedMap() {

		this.visionScale = getScale();
		cameraX = (int) (visionController.getPic().cols() * visionScale);
		cameraY = (int) (visionController.getPic().rows() * visionScale);

		ArrayList<Wall> walls = calculateWalls();
		ArrayList<Goal> goals = calculateGoals(walls);

		return new MapState(
				calculateBalls(),
				calculateCross(),
				walls,
				goals.get(0),
				goals.get(1),
				calculateRobotLocation());
		
		/*
		 * this.visionScale = getScale(); cameraX = (int)
		 * (visionController.getPic().cols() * visionScale); cameraY = (int)
		 * (visionController.getPic().rows() * visionScale);
		 * 
		 * ArrayList<Wall> walls = calculateWalls(); ArrayList<Goal> goals =
		 * calculateGoals(walls);
		 * 
		 * return new MapState( calculateBalls(), calculateCross(), walls, goals.get(0),
		 * goals.get(1), calculateRobotLocation());
		 */
	}

	private double getScale() {
		Cross cross = calculateCross();
		int difference = cross.coordinate2.x - cross.coordinate1.x;
		return (double) difference / 20;
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

		/*
		Comparator<Coordinate> sortCoord =  new Comparator<Coordinate>() {

			@Override
			public int compare(Coordinate c1, Coordinate c2) {
				return -Integer.compare(c1.x,c2.x);
			}
		};
		 */


		if (visionSnapShot.getWalls() != null) {
			for(int i = 0; i < visionSnapShot.getWalls().length; i++) {
				int x = (int) (visionSnapShot.getWalls()[i][0]/visionScale);
				int y = (int) (visionSnapShot.getWalls()[i][1]/visionScale);

				Coordinate c = new Coordinate(x,y);
				coords.add(c);

			}

			//TODO: Uncomment when walls works. 

			//Collections.sort(coords, sortCoord);

			Wall w = new Wall();
			Wall w2 = new Wall();

			w.upper = coords.get(0);
			w.lower = coords.get(2);

			w2.upper = coords.get(1);
			w2.lower = coords.get(3);

			walls.add(w);
			walls.add(w2);
		}

		return walls;
	}

	private Cross calculateCross() {
		ArrayList<Coordinate> obstacle_coord = new ArrayList<Coordinate>();

		for(int i = 0; i < visionSnapShot.getCross().length; i++) {
			int x = (int) (visionSnapShot.getCross()[i][0]/visionScale);
			int y = (int) (visionSnapShot.getCross()[i][1]/visionScale);
			obstacle_coord.add(new Coordinate(x, y));
		}

		Cross obstacle = new Cross(obstacle_coord.get(0),obstacle_coord.get(1),obstacle_coord.get(2),obstacle_coord.get(3));

		return obstacle;

	}

	private ArrayList<Goal> calculateGoals(ArrayList<Wall> walls){
		ArrayList<Goal> goals = new ArrayList<Goal>();

		Goal goal1 = new Goal();
		Goal goal2 = new Goal();

		Wall wall1 = walls.get(0);
		Wall wall2 = walls.get(1);

		goal1.coordinate1 = new Coordinate(wall1.upper.x, wall1.lower.y);
		goal2.coordinate1 = new Coordinate(wall2.upper.x, wall2.lower.y);

		goals.add(goal1);
		goals.add(goal2);

		return goals;
	}

	private RobotLocation calculateRobotLocation() {
		int recievedArray[][] = visionSnapShot.getRobot();
		int orientation;
		
		// Calculate orientation via geometry
		Coordinate smallCircleCoordinate = new Coordinate(recievedArray[0][0],recievedArray[0][1]);
		Coordinate largeCircleCoordinate = new Coordinate(recievedArray[1][0],recievedArray[1][1]);
		Coordinate zeroPoint = new Coordinate(recievedArray[1][0]+5, recievedArray[1][1]);

		double b = zeroPoint.x - largeCircleCoordinate.x; 
		double c = Point2D.distance(largeCircleCoordinate.x, largeCircleCoordinate.y, smallCircleCoordinate.x, smallCircleCoordinate.y);
		double a = Point2D.distance(smallCircleCoordinate.x, smallCircleCoordinate.y, zeroPoint.x, zeroPoint.y);

		int degrees = (int) Math.toDegrees(Math.acos((b*b+c*c-a*a)/(2*b*c)));

		if(largeCircleCoordinate.y > smallCircleCoordinate.y) {
			orientation = 360 - degrees;
		}
		else orientation = degrees;

		RobotLocation roboloc = new RobotLocation(largeCircleCoordinate, orientation);


		return roboloc;
	}

	private void perspectiveTransform(Coordinate coord) {
		// Find height differences and the proportion
		double heightProportion = (double) (cameraHeight - robotHeight)/cameraHeight;

		// Find the scewed distance caused of height differences
		coord.x = (int) ((1-heightProportion)*coord.x + heightProportion*cameraX);
		coord.y = (int) ((1-heightProportion)*coord.y + heightProportion*cameraY);

	}

}
