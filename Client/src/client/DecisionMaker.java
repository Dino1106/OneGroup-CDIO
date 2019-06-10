package client;

import java.util.ArrayList;
import java.util.List;

import model.Ball;
import model.MapState;
import model.Route;
import vision.VisionTranslator;

public class DecisionMaker {
	
	static final int maxBalls = 6; // Maximum number of balls in our robot.
	
	private VisionTranslator visionTranslator;
	private PathFinder pathFinder;
	private MapState mapState;
	private ArrayList<Route> routes;
	private int onFieldBallCount;
	private int pickedUpBallCount;
	
	
	
	public void MainLoop() {
		boolean keepRunning = true;
		while (keepRunning) {
			mapState = visionTranslator.getProcessedMap();
			onFieldBallCount = countBallsOnField();
			
			if (pickedUpBallCount >= maxBalls) {
				pathFinder.playSound("goal");
				deliverBalls();
			}
			
			// This will stop the robot if ever there are no more balls on the field. Let's hope
			// the judge isn't silly and throws new balls unto the field after it is done.
			if (onFieldBallCount < 1) {
				// Time to end this little game.
				if (pickedUpBallCount > 0) {
					pathFinder.playSound("goal");
					deliverBalls();
				}
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

	// Let's pick up a ball.
	private void pickupBall() {
		Route route = choosePath(true);
		pathFinder.driveRoute(route, mapState);
		pickedUpBallCount++;
	}

	// We wish to deliver all of our balls. Let's go to the nearest goal and do so.
	private void deliverBalls() {
		Route route = choosePath(false);
		pathFinder.driveRoute(route, mapState);
		mapState = visionTranslator.getProcessedMap();
		pathFinder.deliverBalls(mapState);
		pickedUpBallCount = 0;
	}
	
	// Let's find the routes to what we're searching for.
	// if FindBalls is false, we're searching for a goal instead.
	public Route choosePath(boolean findBalls) {
		ArrayList<Route> Routes;
		if (findBalls) {
			Routes = pathFinder.getCalculatedRoutesBalls(mapState);
		} else {
			Routes = pathFinder.getCalculatedRoutesGoals(mapState);
		}
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
	
	public int countBallsOnField() {
		return mapState.ballList.size();
	}
	
	public List<Ball> lowRiskBalls(){
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
