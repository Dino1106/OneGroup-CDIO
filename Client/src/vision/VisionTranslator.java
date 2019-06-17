package vision;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import client.PathFinder;
import model.Ball;
import model.Coordinate;
import model.Cross;
import model.Goal;
import model.MapState;
import model.Robot;
import model.VisionSnapShot;
import model.Wall;

public class VisionTranslator {

	private VisionController visionController;
	private VisionSnapShot visionSnapShot;
	private double visionScale = 1;

	private double cameraHeight = 175.0;
	private double robotAxisShift = 7.0/13.5;
	private int cameraX, cameraY;
	private int longBarrierLength = 169;
	private ArrayList<Wall> walls;

	public VisionTranslator(int cameraId) {
		visionController = new VisionController(cameraId);
		
		// Warm-up - needs to be here for scale.
		visionSnapShot = visionController.getSnapShot();
		this.visionScale = getScale();
	}

	public MapState getProcessedMap() {
		visionSnapShot = visionController.getSnapShot();
		//TODO: This is not possible anyMore...
		//cameraX = (int) (visionController.getPic().cols() * visionScale);
		//cameraY = (int) (visionController.getPic().rows() * visionScale);

		this.walls = calculateWalls();
		ArrayList<Goal> goals = calculateGoals(walls);

		return new MapState(
				calculateBalls(),
				calculateCross(),
				walls,
				goals.get(0),
				goals.get(1),
				calculateRobotLocation());
	}

	private double getScale() {
		ArrayList<Wall> walls = calculateWalls();
		int difference = walls.get(1).lower.x - walls.get(0).lower.x;
		return (double) difference / longBarrierLength;
	}

	private ArrayList<Ball> calculateBalls() {
		ArrayList<Ball> balls = new ArrayList<Ball>();

		for(int i = 0; i < visionSnapShot.getBalls().size(); i++) {
			
			Coordinate coord = new Coordinate( (int) (((visionSnapShot.getBalls().get(i).get(0)) / visionScale)), 
											   (int) (((visionSnapShot.getBalls().get(i).get(1)) / visionScale)));
			
			System.out.println("Pre: " +coord.x+", "+coord.y);
			changeToRobotFormat(coord);
			System.out.println("Post: " +coord.x+", "+coord.y);
			
			Ball b = new Ball(coord.x,coord.y);
			balls.add(b);
		}
		return balls;
	}

	private ArrayList<Wall> calculateWalls() {
		ArrayList<Wall> walls = new ArrayList<Wall>();
		ArrayList<Coordinate> coords = new ArrayList<Coordinate>();

		if (visionSnapShot.getWalls() != null) {
			for(int i = 0; i < visionSnapShot.getWalls().length; i++) {
				int x = (int) (visionSnapShot.getWalls()[i][0]/visionScale);
				int y = (int) (visionSnapShot.getWalls()[i][1]/visionScale);

				Coordinate c = new Coordinate(x,y);
				coords.add(c);
			}

			Wall w = new Wall();
			Wall w2 = new Wall();

			w.upper = coords.get(2);
			w.lower = coords.get(0);

			w2.upper = coords.get(3);
			w2.lower = coords.get(1);

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
			Coordinate coord = new Coordinate(x,y);
			changeToRobotFormat(coord);
			obstacle_coord.add(coord);
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

		Coordinate goal1Coord = new Coordinate(wall1.upper.x, wall1.lower.y);
		Coordinate goal2Coord = new Coordinate(wall2.upper.x, wall2.lower.y);
		
		changeToRobotFormat(goal1Coord);
		changeToRobotFormat(goal2Coord);
		
		goal1.coordinate1 = goal1Coord;
		goal2.coordinate1 = goal2Coord;

		goals.add(goal1);
		goals.add(goal2);
		
		
		
		//TODO: Thic code does not work.
		/*
		if (goal1.coordinate1.x < walls.get(1).upper.x) {
			// Then use hardcoded values to construct a robot location.
			
			int x1 = goal1.coordinate1.x + (PathFinder.robotDiameter/ 2 + PathFinder.robotBufferSize);
			int y1 = goal1.coordinate1.y;
			int orientation1 = 180;
			Coordinate goal1Coordinate = new Coordinate(x1, y1);
			goal1.robotLocation = new Robot(goal1Coordinate, orientation1);

			int x2 = goal2.coordinate1.x - (PathFinder.robotDiameter/ 2 + PathFinder.robotBufferSize);
			int y2 = goal2.coordinate1.y;
			int orientation2 = 0;

			Coordinate goal2Coordinate = new Coordinate(x2, y2);
			goal1.robotLocation = new Robot(goal2Coordinate, orientation2);

		} else {

			int x2 = goal2.coordinate1.x + (PathFinder.robotDiameter/ 2 + PathFinder.robotBufferSize);
			int y2 = goal2.coordinate1.y;
			int orientation2 = 180;

			Coordinate goal2Coordinate = new Coordinate(x2, y2);
			goal1.robotLocation = new Robot(goal2Coordinate, orientation2);

			int x1 = goal1.coordinate1.x - (PathFinder.robotDiameter/ 2 + PathFinder.robotBufferSize);
			int y1 = goal1.coordinate1.y;
			int orientation1 = 0;
			Coordinate goal1Coordinate = new Coordinate(x1, y1);
			goal1.robotLocation = new Robot(goal1Coordinate, orientation1);

		}
		*/

		return goals;
	}

	private Robot calculateRobotLocation() {
		int recievedArray[][] = visionSnapShot.getRobot();
		
		Robot roboloc = new Robot();
		
		// Calculate orientation via geometry
		Coordinate smallCircleCoordinate = new Coordinate((int) (recievedArray[0][0] / visionScale),(int) (recievedArray[0][1] / visionScale));
		Coordinate largeCircleCoordinate = new Coordinate((int) (recievedArray[1][0] / visionScale),(int) (recievedArray[1][1] / visionScale));
		Coordinate zeroPoint = new Coordinate((int)((recievedArray[1][0]) / visionScale) + 50, (int) (recievedArray[1][1] / visionScale));
		
		changeToRobotFormat(smallCircleCoordinate);
		changeToRobotFormat(largeCircleCoordinate);
		changeToRobotFormat(zeroPoint);
		
//		perspectiveTransform(smallCircleCoordinate, roboloc.height);
//		perspectiveTransform(largeCircleCoordinate, roboloc.height);
//		perspectiveTransform(zeroPoint, roboloc.height);
		
		double len = Point2D.distance(largeCircleCoordinate.x, largeCircleCoordinate.y, smallCircleCoordinate.x, smallCircleCoordinate.y);

		int x = (int) ((1 - robotAxisShift) * largeCircleCoordinate.x + robotAxisShift * smallCircleCoordinate.x);
		int y = (int) ((1 - robotAxisShift) * largeCircleCoordinate.y + robotAxisShift * smallCircleCoordinate.y);
		
		largeCircleCoordinate = new Coordinate(x, y);
		
		double a = Point2D.distance(smallCircleCoordinate.x, smallCircleCoordinate.y, zeroPoint.x, zeroPoint.y);
		double b = zeroPoint.x - largeCircleCoordinate.x; 
		double c = Point2D.distance(largeCircleCoordinate.x, largeCircleCoordinate.y, smallCircleCoordinate.x, smallCircleCoordinate.y);

		double cosA = (b*b + c*c - a*a) / (2*b*c);
		double radA = Math.acos(cosA);
		double degrees = Math.toDegrees(radA);
		
		//System.out.println("VisionTranslator - hvad er akos: " + akos);
		//System.out.println("VisionTranslator - degrees: " + degrees);

		double orientation;
		
		if(largeCircleCoordinate.y > smallCircleCoordinate.y) {
			orientation = 360 - degrees;
		}
		else orientation = degrees;
		
		roboloc.coordinate = largeCircleCoordinate;
		roboloc.orientation = orientation;

		return roboloc;
	}
	
	private void changeToRobotFormat(Coordinate coord) {
		coord.y = walls.get(0).upper.y - coord.y;
	}

	private void perspectiveTransform(Coordinate coord, double height) {
		// Find height differences and the proportion
		double heightProportion = (double) (cameraHeight - height)/cameraHeight;
		System.out.println("Scale: " + heightProportion);

		// Find the scewed distance caused of height differences
		coord.x = (int) ((1-heightProportion)*coord.x + heightProportion*cameraX);
		coord.y = (int) ((1-heightProportion)*coord.y + heightProportion*cameraY);

	}

}
