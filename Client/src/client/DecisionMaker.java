package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import constants.ClientConstants;
import model.Ball;
import model.Coordinate;
import model.MapState;
import model.Route;
import vision.TestVisionTranslator;
import vision.VisionTranslator;

public class DecisionMaker {

	private VisionTranslator visionTranslator;
	private TestVisionTranslator testVisionTranslator;
	private PathFinder pathFinder;
	private MapState mapState;
	private Client mainClient;
	private int onFieldBallCount;
	private int pickedUpBallCount;

	public void run() {
		visionTranslator = new VisionTranslator(ClientConstants.cameraId);
		mapState = visionTranslator.getProcessedMap();
		mainClient = new Client();
		pathFinder = new PathFinder(mapState, mainClient);

		try {

			mainClient.connect();
			updateMap();
			System.out.println("RobotLocation efter MainClient Call " + mapState.robot);
			mainLoop();
		} catch (IOException e) {
			System.out.println("DecisionMaker error: " + e.getStackTrace());
		}
	}

	public void runRobotTest() {
//		visionTranslator = new VisionTranslator(1);
//		mapState = visionTranslator.getProcessedMap();
		mainClient = new Client();

		try {
			mainClient.connect();
			

			// Calibrate rotate
			mainClient.rotate(1080);

			// Be able to drive around 40 cm in the field.
//			testAroundInASquare();

			// Be able to pickUp all balls in list.
//			pickUpWileLoop();

		} catch (IOException e) {
			System.out.println("DecisionMaker error: " + e.getStackTrace());
		}
	}

	public void runVisionTest() {
		testVisionTranslator = new TestVisionTranslator();
		mapState = testVisionTranslator.getProcessedMap();
		mainClient = new Client();

		try {

			mainClient.connect(testVisionTranslator);

			updateMap();
			System.out.println("RobotLocation efter MainClient Call " + mapState.robot);
			mainLoop();
		} catch (IOException e) {
			System.out.println("DecisionMaker error: " + e.getStackTrace());
		}
	}

	public void mainLoop() {
		long startTime = System.nanoTime();
		mainClient.pickUpBalls(true);
		boolean keepRunning = true;
		while (keepRunning) {
			updateMap();
			System.out.println("----DecisionMaker\n\tOnFieldBallCount: " + onFieldBallCount + "\n\tPickedUpBallCount: "
					+ pickedUpBallCount);
			if (mapState.robot.coordinate.x == 0.0 && mapState.robot.coordinate.y == 0.0) {
				emergencyBack();
				continue;
			}
			
			if (pickedUpBallCount >= ClientConstants.maxBalls) {
				pathFinder.playSound("goal");
				deliverBalls();
				continue;
			}

			// This will stop the robot if ever there are no more balls on the field. Let's
			// hope
			// the judge isn't silly and throws new balls unto the field after it is done.
			if (onFieldBallCount < 1) {
				// Get rid of our last balls.
				if (pickedUpBallCount > 0) {
					pathFinder.playSound("goal");
					deliverBalls();
				}
				// Time to end this little game.
				keepRunning = false;
				pathFinder.playSound("victory");
				continue;
			}

			// Let's go pick up some balls.
			if (onFieldBallCount > 0 && pickedUpBallCount < ClientConstants.maxBalls) {
				pathFinder.playSound("ball");
				pickupBall();
				continue;
			}
		}
		pathFinder.stopMotors();
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		System.out.println("The robot program ran for: " + timeElapsed * Math.pow(10, -9));
	}

	private void emergencyBack() {
		pathFinder.emergencyBack();
		updateMap();
	}

	private void pickUpWileLoop() {
		int ballsCount = 10;
		while (ballsCount > 0) {
			mapState = visionTranslator.getProcessedMap();
			mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
			Ball ball = visionTranslator.getProcessedMap().ballList.get(0);
			ballsCount = visionTranslator.getProcessedMap().ballList.size();
			mainClient.pickUpBalls(true);
			mainClient.sendCoordinate(new Coordinate(ball.x, ball.y), 5);
			mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
			mainClient.sendSound(1);
		}
	}

	private void testAroundInASquare() {
		// Venstre nede
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		// Coordinate coord = new Coordinate(20, 20);
		Coordinate coord = new Coordinate(40, 40);
		mainClient.sendCoordinate(coord, 350);

		// Venstre oppe
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		coord = new Coordinate(40, 82);
		// coord = new Coordinate(20, 102);
		mainClient.sendCoordinate(coord, 350);

		// Højre oppe
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		coord = new Coordinate(129, 82);
		// coord = new Coordinate(149, 102);
		mainClient.sendCoordinate(coord, 350);

		// Højre nede
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		coord = new Coordinate(129, 40);
		// coord = new Coordinate(149, 20);
		mainClient.sendCoordinate(coord, 350);

		// Venstre nede
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		coord = new Coordinate(40, 40);
		// coord = new Coordinate(20, 20);
		mainClient.sendCoordinate(coord, 350);
	}

	// Gets new map info, updates data.
	private void updateMap() {
		mapState = visionTranslator.getProcessedMap();
		mainClient.setRobotLocation(mapState.robot);
		pathFinder.mapState = mapState;

		System.out.println("\n\n updateMap():\n" + mapState.toString() + "\n\n");

		onFieldBallCount = countBallsOnField();
	}

	// Let's pick up a ball.
	private void pickupBall() {
		Ball bestBall = decideBestBall();
		Route route = choosePathBall(bestBall);
		pathFinder.driveRoute(route, mapState);
		updateMap();
		System.out.println("----DecisionMaker: I have decided to pick up the ball at: " + bestBall);
		pathFinder.swallowAndReverse(mapState, bestBall);
		updateMap();
		pickedUpBallCount++;
		System.out.println("---DecisionMaker: PickedUpBallCount: " + pickedUpBallCount);
	}

	// We wish to deliver all of our balls. Let's go to the nearest goal and do so.
	private void deliverBalls() {
		Route route = choosePathGoals();
		pathFinder.driveRoute(route, mapState);
		updateMap();
		pathFinder.deliverBalls(mapState);
		pickedUpBallCount = 0;
	}

	private Ball decideBestBall() {
		Ball bestBall = mapState.ballList.get(0);
		double bestRisk = pathFinder.calculateDistances(new Coordinate(bestBall.x, bestBall.y),
				mapState.robot.coordinate);
		// TODO: Risk calculation here should be more advanced than simply distance.
		for (Ball ball : mapState.ballList) {
			double ballRisk = pathFinder.calculateDistances(new Coordinate(ball.x, ball.y), mapState.robot.coordinate);
			if (ballRisk < bestRisk) {
				bestBall = ball;
				bestRisk = ballRisk;
			}
		}
		return bestBall;
	}

	public Route choosePathBall(Ball ball) {
		Route route = pathFinder.getCalculatedRouteBall(mapState, ball);
		return route;
	}

	// Let's find the routes to what we're searching for.
	// if FindBalls is false, we're searching for a goal instead.
	public Route choosePathGoals() {
		System.out.println("----DecisionMaker: Time to go to a goal.");
		ArrayList<Route> Routes;
		Routes = pathFinder.getCalculatedRoutesGoals(mapState);
		int best = 999; // Arbitrarily large value.
		Route bestRoute = null;
		for (Route route : Routes) {
			if (route.coordinates.size() < best) {
				best = route.coordinates.size();
				bestRoute = route;
			}
		}
		System.out.println("---DecisionMaker: We've decided to use following route to goal: " + bestRoute.toString());
		return bestRoute;
	}

	public int countBallsOnField() {
		return mapState.ballList.size();
	}

	public List<Ball> lowRiskBalls() {
		List<Ball> safeBalls = new ArrayList<Ball>();
		int paddingVert, paddingHori;

		paddingVert = (int) ((mapState.wallList.get(0).upper.y - mapState.wallList.get(0).lower.y) * 0.10);
		paddingHori = (int) ((mapState.wallList.get(1).upper.x - mapState.wallList.get(0).lower.x) * 0.10);

		for (Ball ball : mapState.ballList) {
			if (ball.x < mapState.wallList.get(0).upper.x + paddingHori
					&& ball.y > mapState.wallList.get(0).upper.y - paddingVert) {
				if (ball.y > mapState.wallList.get(0).upper.y + paddingVert) {
					if (ball.x < mapState.wallList.get(1).upper.x - paddingHori) {
						safeBalls.add(ball);
					}
				}
			}
		}
		return safeBalls;
	}
}
