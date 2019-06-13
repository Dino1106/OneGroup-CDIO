package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Ball;
import model.Coordinate;
import model.Cross;
import model.Goal;
import model.MapState;
import model.RobotLocation;
import model.Route;
import model.Wall;
import vision.VisionTranslator;

public class DecisionMaker {
	
	static final int maxBalls = 6; // Maximum number of balls in our robot.
	
	private static VisionTranslator visionTranslator;
	private static PathFinder pathFinder;
	private static MapState mapState;
	private static int onFieldBallCount;
	private static int pickedUpBallCount;
	
	private static ArrayList<Ball> ballList = new ArrayList<>();
	private static ArrayList<Wall> wallList = new ArrayList<>();
	private static Cross cross;
	private static Goal goal1, goal2;
	private static RobotLocation robotLocation;
	
	public static void main(String[] args) {
		
		//TODO: Implement visionTranslator implementation.
		//visionTranslator = new VisionTranslator(false);
		//System.out.println(visionTranslator.getProcessedMap().toString());
		
		
		///*  -- THIS SHOULD WORK WHEN EVERYTHING WORKS.
		try {
			MainClient.connect();
			updateMap();
			pathFinder = new PathFinder(mapState);
			mainLoop();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//*/
		
		/* -------- FOR TEST ONLY ------
		try {
			MainClient.connect();
			ballList.add(new Ball(80, 40));
			Wall wall1 = new Wall();
			wall1.upper = new Coordinate(0, 90);
			wall1.lower = new Coordinate(0, 0);
			wallList.add(wall1);
			
			Wall wall2 = new Wall();
			wall2.upper = new Coordinate(160, 90);
			wall2.lower = new Coordinate(160, 0);
			wallList.add(wall2);
			
			cross = new Cross(new Coordinate(80, 65), new Coordinate(80, 25), new Coordinate(60, 45), new Coordinate(100, 45));
			goal1 = new Goal();
			goal1.coordinate1 = new Coordinate(0, 0);
			goal1.coordinate2 = new Coordinate(0, 90);
			goal1.robotLocation = new RobotLocation(new Coordinate(0, 0), 0);
			
			goal2 = new Goal();
			goal2.coordinate1 = new Coordinate(160, 0);
			goal2.coordinate2 = new Coordinate(160, 90);
			goal2.robotLocation = new RobotLocation(new Coordinate(0, 0), 0);
			robotLocation = new RobotLocation(new Coordinate(10, 10), 0);
			
			mapState = new MapState(ballList, cross, wallList, goal1, goal2, robotLocation);
			pathFinder = new PathFinder(mapState);
			mainLoop();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
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
		onFieldBallCount = countBallsOnField();
	}

	// Let's pick up a ball.
	private static void pickupBall() {
		Ball bestBall = decideBestBall();
		Route route = choosePathBall(bestBall);
		pathFinder.driveRoute(route, mapState);
		updateMap();
		pathFinder.swallowAndReverse(mapState, bestBall);
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
		int bestRisk;
		bestRisk = pathFinder.calculateDistances(new Coordinate(bestBall.x, bestBall.y), mapState.robotLocation.coordinate);
		// TODO: Risk calculation here should be more advanced than simply distance.
		for (Ball ball : mapState.ballList) {
			int ballRisk = pathFinder.calculateDistances(new Coordinate(ball.x, ball.y), mapState.robotLocation.coordinate);
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
