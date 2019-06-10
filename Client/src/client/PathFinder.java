package client;

import java.util.ArrayList;

public class PathFinder {
	
	
	
	public ArrayList<Route> getCalculatedRoutesBalls(MapState mapState) {
		ArrayList<Route> routes = new ArrayList<Route>();
		for (Ball ball : mapState.ballList) {
			Route route = new Route();
			route.robotCoordinate = mapState.robotLocation.coordinate;
			route.coordinates.add(ball.coordinate);
			// TODO: This needs a lot of complexity to find out proper ways to get around obstacles.
			routes.add(route);
		}
		return routes;
	}
	
	public ArrayList<Route> getCalculatedRoutesGoals(MapState mapState) {
		ArrayList<Route> routes = new ArrayList<Route>();
		Goal goal;
		Route route;
		goal = mapState.goal1;
		route = new Route();
		route.robotCoordinate = mapState.robotLocation.coordinate;
		route.coordinates.add(goal.robotLocation.coordinate);
		// TODO: This needs a lot of complexity to find out proper ways to get around obstacles.
		routes.add(route);
		
		goal = mapState.goal2;
		route = new Route();
		route.robotCoordinate = mapState.robotLocation.coordinate;
		route.coordinates.add(goal.robotLocation.coordinate);
		// TODO: This needs a lot of complexity to find out proper ways to get around obstacles.
		routes.add(route);
		return routes;
	}
	
	public void deliverBalls(MapState mapState) {
		int orientation1, orientation2;
		orientation1 = mapState.robotLocation.orientation;
		middleOfGoal(mapState.goal1);
		int dist1, dist2;
		dist1 = calculateDistances(mapState.robotLocation.coordinate, middleOfGoal(mapState.goal1));
		dist2 = calculateDistances(mapState.robotLocation.coordinate, middleOfGoal(mapState.goal2));
		if (dist1 >= dist2) {
			orientation2 = mapState.goal1.robotLocation.orientation;
		} else {
			orientation2 = mapState.goal2.robotLocation.orientation;
		}
		// TODO: Turn orientation 1 backwards. Turn orientation 2 forward.
	}
	
	// 'Afstandsformlen' to calculate distance between two coordinates.
	private int calculateDistances(Coordinate coordinate1, Coordinate coordinate2) {
		return (int) Math.sqrt(Math.pow(coordinate1.x - coordinate2.x, 2) + Math.pow(coordinate1.y - coordinate2.y, 2));
	}

	public Coordinate middleOfGoal(Goal goal) {
		int x = (goal.coordinate1.x + goal.coordinate2.x)/2;
		int y = (goal.coordinate1.y + goal.coordinate2.y)/2;
		return new Coordinate(x, y);
	}

	public void playSound(String sound) {
		switch (sound) {
		case "victory":
			// TODO: Make Robert play his tune.
			break;
		case "ball":
			break;
		case "goal":
			break;
			default:
				break;
		}
	}

	public void driveRoute(Route route, MapState mapState) {
		// TODO For now, just feed the route robot position and exit position to the robot.
		
	}

}
