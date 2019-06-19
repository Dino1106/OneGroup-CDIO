package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import interfaces.IMainClient;
import model.Ball;
import model.Coordinate;
import model.MapState;
import model.Route;
import vision.TestVisionTranslator;
import vision.VisionTranslator;

public class DecisionMaker {
	
	static final int maxBalls = 8; // Maximum number of balls in our robot.
	static int ballsCount = 10;
	
	private static VisionTranslator visionTranslator;
	private static PathFinder pathFinder;
	private static MapState mapState;
	private static IMainClient mainClient;
//	private static TestMainClient mainClient;
	private static int onFieldBallCount;
	private static int pickedUpBallCount;
	
	public static void main(String[] args) throws InterruptedException {
		
		visionTranslator = new VisionTranslator(1);
		mapState = visionTranslator.getProcessedMap();
		mainClient = new MainClient();
//		pathFinder = new PathFinder(mapState, mainClient);
		System.out.println("DecisionMaker first Map: " + visionTranslator.getProcessedMap().toString());

		try {
		mainClient.connect();
		
		// Be able to drive around 40 cm in the field.
		testAroundInASquare();

		
//		updateMap();
//		System.out.println("RobotLocation efter MainClient Call " + mapState.robot);
//		mainLoop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void testPickUpBallsRecklessly() {
		while(ballsCount > 0) {
			mapState = visionTranslator.getProcessedMap();
			mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
			Ball ball = visionTranslator.getProcessedMap().ballList.get(0);
			ballsCount = visionTranslator.getProcessedMap().ballList.size();
			mainClient.pickUpBalls(true);
			mainClient.sendCoordinate(new Coordinate(ball.x, ball.y), 50);
			mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
			mainClient.sendSound(1);
		}
	}
	
	public static void mainLoop() {
		mainClient.pickUpBalls(true);
		boolean keepRunning = true;
		while (keepRunning) {
			updateMap();
			
			// TODO: Reimplement delivering balls.
//			if (pickedUpBallCount >= maxBalls) {
//				pathFinder.playSound("goal");
//				deliverBalls();
//			}
			
			// This will stop the robot if ever there are no more balls on the field. Let's hope
			// the judge isn't silly and throws new balls unto the field after it is done.
			if (onFieldBallCount < 1) {
				// Get rid of our last balls.
				if (pickedUpBallCount > 0) {
					pathFinder.playSound("goal");
//					deliverBalls();
				}
				// Time to end this little game.
				keepRunning = false;
				pathFinder.playSound("victory");
			}
			
//			 Let's go pick up some balls.
			if (onFieldBallCount > 0 && pickedUpBallCount < maxBalls) {
				pathFinder.playSound("ball");
				pickupBall();
			}
		}
	}
	
	private static void testAroundInASquare() {
		//Venstre nede
		mapState = visionTranslator.getProcessedMap();
		System.out.println("new Mapstate: " + mapState.toString());
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		//Coordinate coord = new Coordinate(20, 20);
		Coordinate coord = new Coordinate(40, 40);
		mainClient.sendCoordinate(coord, 350);
		
		//Venstre oppe
		mapState = visionTranslator.getProcessedMap();
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		coord = new Coordinate(40, 82);
		//coord = new Coordinate(20, 102);
		mainClient.sendCoordinate(coord, 350);
		
		//Højre oppe
		mapState = visionTranslator.getProcessedMap();
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		coord = new Coordinate(129, 82);
		//coord = new Coordinate(149, 102);
		mainClient.sendCoordinate(coord, 350);

		//Højre nede
		mapState = visionTranslator.getProcessedMap();
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		coord = new Coordinate(129, 40);
		//coord = new Coordinate(149, 20);
		mainClient.sendCoordinate(coord, 350);
		
		//Venstre nede
		mapState = visionTranslator.getProcessedMap();
		mainClient.setRobotLocation(visionTranslator.getProcessedMap().robot);
		mainClient.sendSound(1);
		coord = new Coordinate(40, 40);
		//coord = new Coordinate(20, 20);
		mainClient.sendCoordinate(coord, 350);
	}

	// Gets new map info, updates data.
	private static void updateMap() {
		mapState = visionTranslator.getProcessedMap();
		mainClient.setRobotLocation(mapState.robot);
		pathFinder.mapState = mapState;
		
		System.out.println("\n\n updateMap():\n" + mapState.toString() + "\n\n");
		
		onFieldBallCount = countBallsOnField();
	}

	// Let's pick up a ball.
	private static void pickupBall() {
		Ball bestBall = decideBestBall();
		Route route = pathFinder.getCalculatedRouteBall(mapState, bestBall);
		pathFinder.driveRoute(route, mapState);
		updateMap();
		pathFinder.swallowAndReverse(mapState, bestBall);
		updateMap();
		pickedUpBallCount++;
	}

	// We wish to deliver all of our balls. Let's go to the nearest goal and do so.
	private static void deliverBalls() {
		Route route = choosePathGoals();
		pathFinder.driveRoute(route, mapState);
		updateMap();
		pathFinder.deliverBalls(mapState);
		pickedUpBallCount = 0;
	}
	
	private static Ball decideBestBall() {
		Ball bestBall = mapState.ballList.get(0);
		double bestRisk = pathFinder.calculateDistances(new Coordinate(bestBall.x, bestBall.y), mapState.robot.coordinate);
		// TODO: Risk calculation here should be more advanced than simply distance.
		for (Ball ball : mapState.ballList) {
			double ballRisk = pathFinder.calculateDistances(new Coordinate(ball.x, ball.y), mapState.robot.coordinate);
			if (ballRisk < bestRisk) {
				bestBall = ball;
				bestRisk = ballRisk;
			}
		}
		System.out.println("---DecideBestBall. Best Ball decided is: \n" + bestBall);
		return bestBall;
	}
	
	// Let's find the routes to what we're searching for.
	// if FindBalls is false, we're searching for a goal instead.
	public static Route choosePathGoals() {
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
		return bestRoute;
	}
	
	public static int countBallsOnField() {
		return mapState.ballList.size();
	}
	
	public static List<Ball> lowRiskBalls(){
		List<Ball> safeBalls = new ArrayList<Ball>();
		int paddingVert, paddingHori;
		
		paddingVert = (int) ((mapState.wallList.get(0).upper.y - mapState.wallList.get(0).lower.y)* 0.10);
		paddingHori = (int) ((mapState.wallList.get(1).upper.x - mapState.wallList.get(0).lower.x)* 0.10); 
		
		for(Ball ball : mapState.ballList) {
			if(ball.x < mapState.wallList.get(0).upper.x + paddingHori && ball.y > mapState.wallList.get(0).upper.y - paddingVert) {
				if(ball.y > mapState.wallList.get(0).upper.y + paddingVert) {
					if(ball.x < mapState.wallList.get(1).upper.x - paddingHori) {
						safeBalls.add(ball);
					}
				}
			}
		}
		return safeBalls;	
	}
}
