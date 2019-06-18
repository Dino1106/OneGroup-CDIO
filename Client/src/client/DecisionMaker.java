
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
