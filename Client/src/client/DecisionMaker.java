package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Ball;
import model.Coordinate;
import model.MapState;
import model.Route;
import vision.Main;
import vision.VisionTranslator;

public class DecisionMaker {
	
	static final int maxBalls = 10; // Maximum number of balls in our robot.
	static int ballsCount = 10;
	
	private static VisionTranslator visionTranslator;
	private static PathFinder pathFinder;
	private static MapState mapState;
	private static MainClient mainClient;
	private static int onFieldBallCount;
	private static int pickedUpBallCount;
	
	public static void main(String[] args) {
		
		visionTranslator = new VisionTranslator(0);
		mainClient = new MainClient();
		System.out.println("DecisionMaker first Map: " + visionTranslator.getProcessedMap().toString());

		try {
		mainClient.connect();
		
		mapState = visionTranslator.getProcessedMap();
		System.out.println("new Mapstate: " + mapState.toString());
		mainClient.setRobotLocation(mapState.robot);
		Ball ball = mapState.ballList.get(0);
		mainClient.pickUpBalls(true);
		mainClient.sendCoordinate(new Coordinate(ball.x, ball.y), 360);
		mainClient.sendSound(1);
		mainClient.pickUpBalls(false);
	
		
//		//Venstre nede
//		mapState = visionTranslator.getProcessedMap();
//		System.out.println("new Mapstate: " + mapState.toString());
//		mainClient.setRobotLocation(mapState.robot);
//		mainClient.sendSound(1);
//		Coordinate coord = new Coordinate(20, 20);
//		//Coordinate coord = new Coordinate(40, 40);
//		mainClient.sendCoordinate(coord, 350);
//
//		//Venstre oppe
//		mapState = visionTranslator.getProcessedMap();
//		mainClient.setRobotLocation(mapState.robot);
//		mainClient.sendSound(1);
//		//coord = new Coordinate(40, 82);
//		coord = new Coordinate(20, 102);
//		mainClient.sendCoordinate(coord, 350);
//
//		//Højre oppe
//		mapState = visionTranslator.getProcessedMap();
//		mainClient.setRobotLocation(mapState.robot);
//		mainClient.sendSound(1);
//		//coord = new Coordinate(129, 82);
//		coord = new Coordinate(149, 102);
//		mainClient.sendCoordinate(coord, 350);
//
//		//Højre nede
//		mapState = visionTranslator.getProcessedMap();
//		mainClient.setRobotLocation(mapState.robot);
//		mainClient.sendSound(1);
//		//coord = new Coordinate(129, 40);
//		coord = new Coordinate(149, 20);
//		mainClient.sendCoordinate(coord, 350);
//
//		//Venstre nede
//		mapState = visionTranslator.getProcessedMap();
//		mainClient.setRobotLocation(mapState.robot);
//		mainClient.sendSound(1);
//		//coord = new Coordinate(40, 40);
//		coord = new Coordinate(20, 20);
//		mainClient.sendCoordinate(coord, 350);
//

		//Yay
		mainClient.sendSound(2);
		mainClient.rotate(360);

		//updateMap();
		//MainClient.setRobotLocation(mapState.robotLocation.coordinate);
		//pathFinder = new PathFinder(mapState);
		//System.out.println("RobotLocation efter MainClient Call " + mapState.robotLocation);
		//mainLoop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void mainLoop() {
		boolean keepRunning = true;
		while (keepRunning) {
			updateMap();
			
			if (pickedUpBallCount >= maxBalls) {
				pathFinder.playSound("goal");
				deliverBalls();
			}
			
			// This will stop the robot if ever there are no more balls on the field. Let's hope
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
			}
			
			// Let's go pick up some balls.
			if (onFieldBallCount > 0 && pickedUpBallCount < maxBalls) {
				pathFinder.playSound("ball");
				pickupBall();
			}
		}
	}

	// Gets new map info, updates data.
	private static void updateMap() {
		mapState = visionTranslator.getProcessedMap();
		mainClient.setRobotLocation(mapState.robot);
		
		System.out.println("\n\n updateMap():\n" + mapState.toString() + "\n\n");
		
		onFieldBallCount = countBallsOnField();
	}

	// Let's pick up a ball.
	private static void pickupBall() {
		Ball bestBall = decideBestBall();
		Route route = choosePathBall(bestBall);
		pathFinder.driveRoute(route, mapState);
		updateMap();
		pathFinder.swallowAndReverse(mapState, bestBall);
		updateMap();
		mainClient.setRobotLocation(mapState.robot);
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
		int bestRisk = pathFinder.calculateDistances(new Coordinate(bestBall.x, bestBall.y), mapState.robot.coordinate);
		// TODO: Risk calculation here should be more advanced than simply distance.
		for (Ball ball : mapState.ballList) {
			int ballRisk = pathFinder.calculateDistances(new Coordinate(ball.x, ball.y), mapState.robot.coordinate);
			if (ballRisk < bestRisk) {
				bestBall = ball;
				bestRisk = ballRisk;
			}
		}
		return bestBall;
	}
	
	public static Route choosePathBall(Ball ball) {
		Route route = pathFinder.getCalculatedRouteBall(mapState, ball);
		return route;
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
