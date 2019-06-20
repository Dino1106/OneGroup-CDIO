package client;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import model.Ball;
import model.Coordinate;
import model.Goal;
import model.MapState;
import model.PseudoWall;
import model.Route;
import model.Wall;

public class PathFinder {

	public static final double robotDiameter = 21.0; // The "diameter" of the robot - its thickness.
	public static final double robotBufferSize = 40.0; // The buffer distance we want between the robot and an edge.
	public static final int speedSlow = 10;
	public static final int speedFast = 50;
	public static final int sleepTime = 5; // Sleep time in seconds.

	private Coordinate northWest;
	private Coordinate northEast;
	private Coordinate southWest;
	private Coordinate southEast;

	private Wall leftWall;
	private Wall rightWall;
	private PseudoWall upperWall;
	private PseudoWall lowerWall;
	private Client mainClient;
	public MapState mapState;

	// Create this PathFinder which will then find 4 distinct "quadrant
	// coordinates".
	// Also starts swallowing balls.
	public PathFinder(MapState mapState, Client mainClient) {
		this.mapState = mapState;
		this.mainClient = mainClient;
		northWest = mapState.quadrants.get(0);
		southWest = mapState.quadrants.get(1);
		northEast = mapState.quadrants.get(2);
		southEast = mapState.quadrants.get(3);

		System.out.println("[PathFinder] Quadrant print: NW: " + northWest + "\nNE: " + northEast + "\nSW: " + southWest
				+ "\nSE: " + southEast);
		generateWalls(mapState);
	}
	
	// We want to return route to a given ball.
	public Route getCalculatedRouteBall(MapState mapState, Ball ball) {
		System.out.println("----- PathFinder getCalculatedRouteBall for ball: " + ball);
		Route route = new Route(mapState.robot.coordinate, new ArrayList<Coordinate>());
		// This is where the magic happens.
		// First we see if the ball is close to a cross.
		Coordinate nearestToTarget;
		Coordinate auxiliaryForCross = null;
		boolean closeToCross = isBallCloseToCross(ball, mapState);
		// If it is, we go to the quadrant for the auxiliary point - NOT to the quadrant for the ball.
		if (closeToCross) {
			System.out.println("\t\t Ball is close to cross!");
			auxiliaryForCross = findCoordinateOnLine(new Coordinate(ball.x, ball.y), new Coordinate(mapState.cross.centerCoordinate.x, mapState.cross.centerCoordinate.y), robotDiameter + robotBufferSize);
			nearestToTarget = findNearestQuadrant(auxiliaryForCross);
		} else {
			System.out.println("\t\t Ball is not close to cross");
			// Else we find out which quadrant is nearest to the ball.
			nearestToTarget = findNearestQuadrant(new Coordinate(ball.x, ball.y));
		}
		// Now we find out which quadrant is nearest to the robot.
		Coordinate nearestToRobot = findNearestQuadrant(mapState.robot.coordinate);
		// The first coordinate we go to is the one nearest to the robot.
		route.coordinates.add(nearestToRobot);
		// Now we calculate a route between these two coordinates. A method has been
		// created, dedicated to finding a path between quadrants.
		route.coordinates.addAll(getRouteBetweenQuadrants(nearestToRobot, nearestToTarget));
		 // TODO: Comment back in auxiliary positions
		if (closeToCross) {
			// Now we need to get an auxiliary coordinate for balls near cross.
			// route.coordinates.add(auxiliaryForCross);
		} else {
			// Now we need to get an auxiliary coordinate for balls near corners or walls.
			// getCoordinatesForBallNearWalls(ball, route);
		}
		System.out.println("Calculated Route is: " + route.coordinates.toString());
		return route;
	}

	private boolean isBallCloseToCross(Ball ball, MapState mapState) {
		double distanceBetween = calculateDistances(new Coordinate(ball.x, ball.y), new Coordinate(mapState.cross.centerCoordinate.x, mapState.cross.centerCoordinate.y));
		if (distanceBetween < mapState.cross.radius) {
			return true;
		} else {
			return false;
		}
	}

	// Now check if ball is near wall. If it isn't, then we don't send any auxiliary
	// coordinates.
	// Let's see if ball is close to a corner. If it is, we disregard the "ball
	// close to wall" part.
	private void getCoordinatesForBallNearWalls(Ball ball, Route route) {
		System.out.println("----- PathFinder getCoordinatesForBallNearWalls");
		boolean ballCloseToCorner = false;
		Coordinate quadrant = findNearestQuadrant(new Coordinate(ball.x, ball.y));
		if (isBallCloseToCorner(ball, leftWall, upperWall)) {
			System.out.println("\t\t Ball is close to a corner!");
			ballCloseToCorner = true;
			Coordinate newCoordinate = new Coordinate(0, 0);
			newCoordinate.x = leftWall.upper.x + robotDiameter + robotBufferSize;
			newCoordinate.y = upperWall.left.y - robotDiameter - robotBufferSize;
			//isInsideCrossPad(newCoordinate);
			route.coordinates.add(newCoordinate);
		}
		if (isBallCloseToCorner(ball, leftWall, lowerWall)) {
			System.out.println("\t\t Ball is close to a corner!");
			ballCloseToCorner = true;
			Coordinate newCoordinate = new Coordinate(0, 0);
			newCoordinate.x = leftWall.upper.x + robotDiameter + robotBufferSize;
			newCoordinate.y = lowerWall.left.y + robotDiameter + robotBufferSize;
			//isInsideCrossPad(newCoordinate);
			route.coordinates.add(newCoordinate);
		}
		if (isBallCloseToCorner(ball, rightWall, upperWall)) {
			System.out.println("\t\t Ball is close to a corner!");
			ballCloseToCorner = true;
			Coordinate newCoordinate = new Coordinate(0, 0);
			newCoordinate.x = rightWall.upper.x - robotDiameter - robotBufferSize;
			newCoordinate.y = upperWall.right.y - robotDiameter - robotBufferSize;
			//isInsideCrossPad(newCoordinate);
			route.coordinates.add(newCoordinate);
		}
		if (isBallCloseToCorner(ball, rightWall, lowerWall)) {
			System.out.println("\t\t Ball is close to a corner!");
			ballCloseToCorner = true;
			Coordinate newCoordinate = new Coordinate(0, 0);
			newCoordinate.x = rightWall.upper.x - robotDiameter - robotBufferSize;
			newCoordinate.y = lowerWall.right.y + robotDiameter + robotBufferSize;
			//isInsideCrossPad(newCoordinate);
			route.coordinates.add(newCoordinate);
		}
		if (!ballCloseToCorner) {
			// Let's see if a ball is close to one of the four walls. If it is, we set up
			// the route to now stand opposite the wall.
			if (isBallCloseToWall(ball, leftWall)) {
				System.out.println("\t\t Ball is close to a wall!");
				// TODO: Refine where the robot goes here before approaching a wall-close ball.
				Coordinate newCoordinate = new Coordinate(0, 0);
				newCoordinate.x = quadrant.x;
				newCoordinate.y = ball.y;
				System.out.println("Pre Ball Left: " +ball.x+", "+ball.y);
				ball.x = robotBufferSize;
				System.out.println("Post Ball Left: " +ball.x+", "+ball.y);
				route.coordinates.add(newCoordinate);
			}
			if (isBallCloseToWall(ball, rightWall)) {
				System.out.println("\t\t Ball is close to a wall!");
				Coordinate newCoordinate = new Coordinate(0, 0);
				newCoordinate.x = quadrant.x;
				newCoordinate.y = ball.y;
				System.out.println("Pre Ball Right: " +ball.x+", "+ball.y);
				ball.x -= robotBufferSize ;
				System.out.println("Post Ball Right: " +ball.x+", "+ball.y);
				route.coordinates.add(newCoordinate);
			}
			if (isBallCloseToWall(ball, upperWall)) {
				System.out.println("\t\t Ball is close to a wall!");
				Coordinate newCoordinate = new Coordinate(0, 0);
				newCoordinate.x = ball.x;
				newCoordinate.y = quadrant.y;
				System.out.println("Pre Ball Upper: " +ball.x+", "+ball.y);
				ball.y -= robotBufferSize;
				System.out.println("Post Ball Upper: " +ball.x+", "+ball.y);
				route.coordinates.add(newCoordinate);
			}
			if (isBallCloseToWall(ball, lowerWall)) {
				System.out.println("\t\t Ball is close to a wall!");
				Coordinate newCoordinate = new Coordinate(0, 0);
				newCoordinate.x = ball.x;
				newCoordinate.y = quadrant.y;
				System.out.println("Pre Ball Lower: " +ball.x+", "+ball.y);
				ball.y = robotBufferSize;
				System.out.println("Post Ball Lower: " +ball.x+", "+ball.y);
				route.coordinates.add(newCoordinate);
			}
		}
	}

	// We want to return routes to both goals.
	// TODO: Maybe we want to go to the "best" goal, always?
	public ArrayList<Route> getCalculatedRoutesGoals(MapState mapState) {
		System.out.println("----- PathFinder getCalculatedRoutesGoals");
		ArrayList<Route> routes = new ArrayList<Route>();
		Route route;
		Coordinate nearestToRobot;
		Coordinate nearestToGoal;
		/* 
		 * We ignore the route to the leftmost goal. We want the smallest goal.
		 * 
		route = new Route(mapState.robot.coordinate, new ArrayList<Coordinate>());
		// Now we find way to the goal's assigned "robotlocation" place.
		nearestToRobot = findNearestQuadrant(mapState.robot.coordinate);
		route.coordinates.add(nearestToRobot);
		nearestToGoal = findNearestQuadrant(mapState.goal1.robotLocation.coordinate);
		route.coordinates.addAll(getRouteBetweenQuadrants(nearestToRobot, nearestToGoal));
		route.coordinates.add(mapState.goal1.robotLocation.coordinate);
		routes.add(route);
		*/

		route = new Route(mapState.robot.coordinate, new ArrayList<Coordinate>());
		// Now we find way to the goal's assigned "robotlocation" place.
		nearestToRobot = findNearestQuadrant(mapState.robot.coordinate);
		route.coordinates.add(nearestToRobot);
		nearestToGoal = findNearestQuadrant(mapState.goal2.robotLocation.coordinate);
		route.coordinates.addAll(getRouteBetweenQuadrants(nearestToRobot, nearestToGoal));
		route.coordinates.add(mapState.goal2.robotLocation.coordinate);

		routes.add(route);
		return routes;
	}

	// We want the robot to turn towards the goal, and then spit out balls.
	public void deliverBalls(MapState mapState) {
		System.out.println("----- PathFinder deliverBalls");
		// Find the closest goal.
		Goal goal = null;
		if (calculateDistances(mapState.robot.coordinate, mapState.goal1.coordinate1) > calculateDistances(
				mapState.robot.coordinate, mapState.goal2.coordinate1)) {
			goal = mapState.goal1;
		} else {
			goal = mapState.goal2;
		}
		rotateToOrientation(goal.robotLocation.orientation);
		// Wait for SLEEPTIME seconds.
		try {
			mainClient.pickUpBalls(false);
			TimeUnit.SECONDS.sleep(8);
			mainClient.pickUpBalls(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// 'Afstandsformlen' to calculate distance between two coordinates.
	public double calculateDistances(Coordinate coordinate1, Coordinate coordinate2) {
		return Math.sqrt(Math.pow(coordinate1.x - coordinate2.x, 2) + Math.pow(coordinate1.y - coordinate2.y, 2));
	}

	// Unused.
	// Finds the distance between a coordinate to a line between two coordinates.
	public int calculateDistancesLine(Coordinate coordinate, Coordinate line1, Coordinate line2) {
		// First we gotta find line1 and line2.
		System.out.println("Line 1 and 2 x: " + line1.x + " " + line2.x);
		double a = (line2.y - line1.y) / (line2.x - line1.x);
		double b = line1.y - (a * line1.x);
		double upper = Math.abs(a * line1.x + b - line1.y);
		double lower = Math.sqrt(Math.pow(a, 2) + 1);
		double dist = upper / lower;
		return (int) dist; // There's a minor loss here in conversion.
	}
	
	public void isInsideCrossPad(Coordinate coordinate) {
		double[][] paddingZone = new double[2][2];
		
		paddingZone[0][0] = mapState.cross.centerCoordinate.x - 15.0;
		paddingZone[0][1] = mapState.cross.centerCoordinate.x + 15.0;
		paddingZone[1][0] = mapState.cross.centerCoordinate.y - 15.0;
		paddingZone[1][1] = mapState.cross.centerCoordinate.x + 15.0;
		
		if(paddingZone[0][0] < coordinate.x  && coordinate.x < paddingZone[0][1] &&
		   paddingZone[1][0] < coordinate.y  && coordinate.y < paddingZone[1][1]) {
				
			if( coordinate.x - paddingZone[0][0] > paddingZone[0][1] - coordinate.x ) {
				coordinate.x = paddingZone[0][1] + 5;
			}
			else coordinate.x =  paddingZone[1][0] - 5;
			
		}
		
	}
	
	// Finds a new coordinate that is X away from coordinate 2, where coordinate 1
	// and 2 create a line.
	public Coordinate findCoordinateOnLine(Coordinate coordinate1, Coordinate coordinate2, double distance) {
		Coordinate auxiliary = new Coordinate(0, 0);
		double distanceBetween = calculateDistances(coordinate1, coordinate2);
		double factor = distanceBetween/(distanceBetween + distance);
		auxiliary.x = (coordinate1.x + coordinate2.x)/factor;
		auxiliary.y = (coordinate1.y + coordinate2.y)/factor;
		System.out.println("[Pathfinder]: FindCoordinateOnLine;\nDistance Between: " + distanceBetween + "\nDistance: " + distance + "\nCoordinate1, 2 and auxiliary: " + coordinate1 + coordinate2 + auxiliary);
		return auxiliary;
		
	}

	// Calculates the distance from a coordinate to a given wall. Please notice this
	// assumes walls are only vertical objects.
	public double calculateDistanceToWall(Coordinate coordinate, Wall wall) {
		double distance = coordinate.x - (wall.upper.x + wall.lower.x) / 2; // Takes average.
		distance = Math.abs(distance);
		return distance;
	}

	// Overloads parameter to handle pseudowalls. Please notice this assumes
	// pseudowalls are horizontal objects.
	public double calculateDistanceToWall(Coordinate coordinate, PseudoWall wall) {
		double distance = coordinate.y - (wall.left.y + wall.right.y) / 2;
		distance = Math.abs(distance);
		return distance;
	}

	public boolean isBallCloseToWall(Ball ball, Wall wall) {
		if (calculateDistanceToWall(new Coordinate(ball.x, ball.y), wall) < 2 * robotBufferSize) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isBallCloseToWall(Ball ball, PseudoWall wall) {
		if (calculateDistanceToWall(new Coordinate(ball.x, ball.y), wall) < 2 * robotBufferSize) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isBallCloseToCorner(Ball ball, Wall wall1, PseudoWall wall2) {
		if (isBallCloseToWall(ball, wall1) && isBallCloseToWall(ball, wall2)) {
			return true;
		}
		return false;
	}

	// Gets a route between two quadrants. Has been created as a mess of if
	// statements - a manual deduction is the most effective.
	private ArrayList<Coordinate> getRouteBetweenQuadrants(Coordinate fromCoordinate, Coordinate toCoordinate) {
		System.out.println("----- PathFinder getRouteBetweenQuadrants");
		ArrayList<Coordinate> output = new ArrayList<Coordinate>();
		// For northWest
		if (fromCoordinate.equals(northWest)) {
			if (toCoordinate.equals(northWest)) {
				// Do nothing.
			}
			if (toCoordinate.equals(northEast)) {
				output.add(northEast);
			}
			if (toCoordinate.equals(southEast)) {
				output.add(southWest);
				output.add(southEast);
			}
			if (toCoordinate.equals(southWest)) {
				output.add(southWest);
			}
		}
		// For northEast
		if (fromCoordinate.equals(northEast)) {
			if (toCoordinate.equals(northWest)) {
				output.add(northWest);
			}
			if (toCoordinate.equals(northEast)) {
				// Do nothing.
			}
			if (toCoordinate.equals(southEast)) {
				output.add(southEast);
			}
			if (toCoordinate.equals(southWest)) {
				output.add(southEast);
				output.add(southWest);
			}
		}
		// For SouthEast
		if (fromCoordinate.equals(southEast)) {
			if (toCoordinate.equals(northWest)) {
				output.add(northEast);
				output.add(northWest);
			}
			if (toCoordinate.equals(northEast)) {
				output.add(northEast);
			}
			if (toCoordinate.equals(southEast)) {
				// Do nothing.
			}
			if (toCoordinate.equals(southWest)) {
				output.add(southWest);
			}
		}
		// For southWest
		if (fromCoordinate.equals(southWest)) {
			if (toCoordinate.equals(northWest)) {
				output.add(northWest);
			}
			if (toCoordinate.equals(northEast)) {
				output.add(southEast);
				output.add(northEast);
			}
			if (toCoordinate.equals(southEast)) {
				output.add(southEast);
			}
			if (toCoordinate.equals(southWest)) {
				// Do nothing.
			}
		}

		return output;
	}

	// Generates pseudowalls.
	private void generateWalls(MapState mapState) {
		System.out.println("----- PathFinder generateWalls");
		leftWall = mapState.wallList.get(0);
		rightWall = mapState.wallList.get(1);
		upperWall = new PseudoWall();
		lowerWall = new PseudoWall();
		upperWall.left = new Coordinate(leftWall.upper.x, leftWall.upper.y);
		upperWall.right = new Coordinate(rightWall.upper.x, rightWall.upper.y);
		lowerWall.left = new Coordinate(leftWall.lower.x, leftWall.lower.y);
		lowerWall.right = new Coordinate(rightWall.lower.x, rightWall.lower.y);
		System.out.println("[PathFinder]: Pseudowall upper: " + upperWall.left + upperWall.right + " lower: "
				+ lowerWall.left + lowerWall.right);
	}

//	// Set the middle of the map and all quadrant points.
//	private void calculateQuadrants(MapState mapState) {
//		middleOfMap = new Coordinate(0, 0);
//		middleOfMap.x = (mapState.cross.coordinate1.x + mapState.cross.coordinate2.x + mapState.cross.coordinate3.x
//				+ mapState.cross.coordinate4.x) / 4;
//		middleOfMap.y = (mapState.cross.coordinate1.y + mapState.cross.coordinate2.y + mapState.cross.coordinate3.y
//				+ mapState.cross.coordinate4.y) / 4;
//		// Before we can find quadrants, we gotta determine which wall is which.
//		// TODO: If walls change, we gotta fix this part.
//		leftWall = new Wall();
//		rightWall = new Wall();
//		// Figure out which wall is left wall.
//		if (mapState.wallList.get(0).upper.x < mapState.wallList.get(1).upper.x) {
//			leftWall = mapState.wallList.get(0);
//			rightWall = mapState.wallList.get(1);
//		} else {
//			rightWall = mapState.wallList.get(0);
//			leftWall = mapState.wallList.get(1);
//		}
//		// Let's set the four quadrant points. First northwest point - the middle of the
//		// northwest quadrant.
//		northWest = new Coordinate(0, 0);
//		northWest.x = (leftWall.upper.x + middleOfMap.x) / 2;
//		northWest.y = (leftWall.upper.y + middleOfMap.y) / 2;
//		// Then the others.
//		northEast = new Coordinate(0, 0);
//		northEast.x = (rightWall.upper.x + middleOfMap.x) / 2;
//		northEast.y = (rightWall.upper.y + middleOfMap.y) / 2;
//		southWest = new Coordinate(0, 0);
//		southWest.x = (leftWall.lower.x + middleOfMap.x) / 2;
//		southWest.y = (leftWall.lower.y + middleOfMap.y) / 2;
//		southEast = new Coordinate(0, 0);
//		southEast.x = (rightWall.lower.x + middleOfMap.x) / 2;
//		southEast.y = (rightWall.lower.y + middleOfMap.y) / 2;
//	}

	// We find out which quadrant is closest to the requested ball.
	private Coordinate findNearestQuadrant(Coordinate coordinate) {
		System.out.println("----- PathFinder findNearestQuadrant");
		Coordinate output = new Coordinate(0, 0);
		double compare;
		double minimum = calculateDistances(coordinate, northWest);
		output = northWest;
		// Now we compare against northEast.
		compare = calculateDistances(coordinate, northEast);
		if (compare < minimum) {
			minimum = compare;
			output = northEast;
		}
		// Now we compare against southWest.
		compare = calculateDistances(coordinate, southWest);
		if (compare < minimum) {
			minimum = compare;
			output = southWest;
		}
		// Now we compare against southEast.
		compare = calculateDistances(coordinate, southEast);
		if (compare < minimum) {
			minimum = compare;
			output = southEast;
		}
		System.out.println("\t\t\tFor nearest quadrant, found: " + output);
		return output;
	}

	// Play a sound.
	public void playSound(String sound) {
		System.out.println("----- PathFinder playSound");
		switch (sound) {
		case "victory":
			mainClient.sendSound(2);
			break;
		case "ball":
			mainClient.sendSound(1); // TODO: Fix sounds.
//			mainClient.sendSound(3);
			break;
		case "goal":
			break;
		default:
			break;
		}
	}

	public void pickUpMode(boolean swallow) {
		System.out.println("----- PathFinder pickUpMode");
		mainClient.pickUpBalls(swallow);
	}

	// We want the robot to drive a whole route.
	public void driveRoute(Route route, MapState mapState) {
		for (Coordinate coordinate : route.coordinates) {
			System.out.println("----- PathFinder driveRoute \nRoute length: " + route.coordinates.size()
					+ ", \nSending coordinate " + coordinate.toString() + " to robot");
//			mainClient.setRobotLocation(mapState.robot);
			mainClient.sendCoordinate(coordinate, speedFast);
		}
	}

	public void swallowAndReverse(MapState mapState, Ball bestBall) {
		System.out.println("----- PathFinder swallowAndReverse");
		double robotOrientation = mapState.robot.orientation;
		double robotX = mapState.robot.coordinate.x;
		double robotY = mapState.robot.coordinate.y;
		double zeroPointX = robotX + 50;
		double zeroPointY = robotY;

/*		System.out.println("Robobitch at: " + robotX + ", " + robotY);
		System.out.println("Best ball at: " + bestBall.x + ", " + bestBall.y);

		double a = Point2D.distance(bestBall.x, bestBall.y, zeroPointX, zeroPointY);
		double b = zeroPointX - bestBall.x;
		double c = Point2D.distance(robotX, robotY, bestBall.x, bestBall.y);

		double cosA = (b*b + c*c - a*a) / (2*b*c);
		double radA = Math.acos(cosA);
		double targetDegrees = Math.toDegrees(radA);
		
		double targetOrientation;
		
		if(robotY > bestBall.y){
			targetOrientation = 360 - targetDegrees;
		}
		else targetOrientation = targetDegrees;

		System.out.println("[PathFinder] Orientation for swallow:\trobot ori: " + robotOrientation + " target ori: "
				+ targetOrientation);

		
		double counterClockWise = robotOrientation - targetOrientation;
		double clockWise = (360 - robotOrientation) + targetOrientation;
		
		System.out.println("CounterClockwise: " + counterClockWise);
		System.out.println("Clockwise: " + clockWise);

		if (clockWise < counterClockWise) {
			System.out.println("Turn ClockWise");
			mainClient.rotate(clockWise);
		} else {
			System.out.println("Turn CounterClockWise");
			// Negative because of Ev3's rotation
			mainClient.rotate(-counterClockWise);
		}

		double distance = calculateDistances(mapState.robot.coordinate, new Coordinate(bestBall.x, bestBall.y));
		distance = distance - robotDiameter / 4;
		System.out.println("Go distance: " + distance);

		mainClient.sendTravelDistance(distance, speedSlow);
		mainClient.sendTravelDistance(-distance, speedSlow);
		mainClient.sendMotorSpeed(speedFast);*/
		
		mainClient.sendCoordinate(new Coordinate(bestBall.x, bestBall.y), speedSlow);
		System.out.println("\n We went to coordinate to pick up ball at: " + bestBall);
		/* Not neccessary.
		Coordinate nearestToRobot = findNearestQuadrant(mapState.robot.coordinate);
		mainClient.sendCoordinate(nearestToRobot, speedSlow);
		*/
	}

}
