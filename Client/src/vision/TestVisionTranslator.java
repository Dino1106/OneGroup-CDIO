package vision;
import java.util.ArrayList;
import model.Ball;
import model.Coordinate;
import model.Cross;
import model.Goal;
import model.MapState;
import model.Robot;
import model.Wall;

public class TestVisionTranslator {
	
	private ArrayList<Ball> balls;
	private ArrayList<Wall> walls;
	private Cross cross;
	private Robot robot;
	private ArrayList<Goal> goals;
	private ArrayList<Coordinate> quadrants;

	public TestVisionTranslator(){
		this.balls = generateBalls();
		this.walls = generateWalls();
		this.cross = generateCross(walls);
		this.goals = generateGoals(walls);
		this.robot = generateRobot();
		this.quadrants = generateQuadrants();
		
	}

	public MapState getProcessedMap() {
		return new MapState(balls, cross, walls, goals.get(0), goals.get(1), robot, quadrants);
	}

	/**
	 * Gets called by TestMainClient when robot is going to a coordinate
	 * @param destination The coordinate the robot is going to.
	 */
	public void gotoCoordinate(Coordinate destination) {
		this.robot.coordinate = destination;
		for (Ball ball : balls) {
			if ((ball.x > destination.x - 5) && (ball.x < destination.x + 5)) {
				if ((ball.y > destination.y - 5) && (ball.y < destination.y + 5)) {
					balls.remove(ball);
					return;
				}
			}
		}
		
	}

	private ArrayList<Ball> generateBalls() {
		ArrayList<Ball> balls = new ArrayList<Ball>();
		
		// x between 
		balls.add(new Ball(5, 5)); // 1
		balls.add(new Ball(20, 20)); // 2
		balls.add(new Ball(40, 40)); // 3
		balls.add(new Ball(60, 60)); // 4
		balls.add(new Ball(80, 80)); // 5
		balls.add(new Ball(100, 100)); // 6
		balls.add(new Ball(120, 120)); // 7
		balls.add(new Ball(60, 155)); // 8
		balls.add(new Ball(50, 70)); // 9
		balls.add(new Ball(35, 99)); // 10
		
		return balls;
	}

	private ArrayList<Wall> generateWalls() {
		ArrayList<Wall> walls = new ArrayList<Wall>();
		Wall w = new Wall();
		Wall w2 = new Wall();

		w.upper = new Coordinate(0, 122);
		w.lower = new Coordinate(0, 0);

		w2.upper = new Coordinate(169, 122);
		w2.lower = new Coordinate(169, 0);

		walls.add(w);
		walls.add(w2);
		return walls;
	}

	private Cross generateCross(ArrayList<Wall> walls) {
		Wall wall1 = walls.get(0);
		Wall wall2 = walls.get(1);
		
		double halfWall_x = wall2.upper.x - wall1.upper.x;
		double halfWall_y = wall1.upper.y - wall1.lower.y;
		
		return new Cross(new Coordinate(halfWall_x, halfWall_y), 10);
	}

	private ArrayList<Goal> generateGoals(ArrayList<Wall> walls){
		
		ArrayList<Goal> goals = new ArrayList<Goal>();

		Goal smallGoal = new Goal();
		Goal largeGoal = new Goal();

		Wall wall1 = walls.get(0);
		Wall wall2 = walls.get(1);

		Coordinate smallGoalCoord = new Coordinate(wall1.lower.x, (double) wall1.upper.y/2);
		Coordinate largeGoalCoord = new Coordinate(wall2.lower.x, (double) wall2.upper.y/2);
		
		smallGoal.coordinate1 = smallGoalCoord;
		largeGoal.coordinate1 = largeGoalCoord;

		goals.add(smallGoal);
		goals.add(largeGoal);
		
		return goals;
	}

	private Robot generateRobot() {
		return new Robot(new Coordinate(50,50), 0);
	}
	
	private ArrayList<Coordinate> generateQuadrants(){
		ArrayList<Wall> edges = generateWalls();
		ArrayList<Coordinate> quadrants = new ArrayList<Coordinate>();
		Coordinate middle = new Coordinate(0,0);
		
		middle.x = (edges.get(0).upper.x + edges.get(0).lower.x + edges.get(1).upper.x + edges.get(1).lower.x)/4;
		middle.y = (edges.get(0).upper.y + edges.get(0).lower.y + edges.get(1).upper.y + edges.get(1).lower.y)/4;
		
		quadrants.add(new Coordinate((edges.get(0).upper.x + middle.x)/2, edges.get(0).upper.y + middle.y));
		quadrants.add(new Coordinate((edges.get(0).lower.x + middle.x)/2, edges.get(0).lower.y + middle.y));
		quadrants.add(new Coordinate((edges.get(1).upper.x + middle.x)/2, edges.get(1).upper.y + middle.y));
		quadrants.add(new Coordinate((edges.get(1).lower.x + middle.x)/2, edges.get(1).lower.y + middle.y));
		
		return quadrants;
	}

}
