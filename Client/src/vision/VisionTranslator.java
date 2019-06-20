package vision;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import constants.ClientConstants;
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
	private double cameraX, cameraY;
	private ArrayList<Wall> walls;

	public VisionTranslator(int cameraId) {
		visionController = new VisionController(cameraId);

		// Warm-up - needs to be here for scale.
		visionSnapShot = visionController.getSnapShot();
		this.visionScale = getScale();
	}

	public MapState getProcessedMap() {
		visionSnapShot = visionController.getSnapShot();

		this.walls = calculateWalls();
		ArrayList<Goal> goals = calculateGoals(walls);

		// TODO: Kan tages ud s� den ikke beregnes hver gang

		// Calculate center of frame through upper-right wall location
		cameraX = (walls.get(1).upper.x / 2);
		cameraY = (walls.get(1).upper.y / 2);

		return new MapState(calculateBalls(), calculateCross(), walls, goals.get(0), goals.get(1),
				calculateRobotLocation(), calculateQuadrants());
	}

	private double getScale() {
		ArrayList<Wall> walls = calculateWalls();
		double difference = walls.get(1).lower.x - walls.get(0).lower.x;
		return difference / ClientConstants.longBarrierLength;
	}

	private ArrayList<Ball> calculateBalls() {
		Ball ball = new Ball();
		ArrayList<Ball> balls = new ArrayList<Ball>();

		for (int i = 0; i < visionSnapShot.getBalls().size(); i++) {

			Coordinate coord = new Coordinate((((visionSnapShot.getBalls().get(i).get(0)) / visionScale)),
					(((visionSnapShot.getBalls().get(i).get(1)) / visionScale)));
			perspectiveTransform(coord, ball.height);
			changeToRobotFormat(coord);

			Ball b = new Ball(coord.x, coord.y);
			balls.add(b);
		}
		return balls;
	}

	private ArrayList<Wall> calculateWalls() {
		ArrayList<Wall> walls = new ArrayList<Wall>();
		ArrayList<Coordinate> coords = new ArrayList<Coordinate>();

		if (visionSnapShot.getWalls() != null) {
			for (int i = 0; i < visionSnapShot.getWalls().length; i++) {
				int x = (int) (visionSnapShot.getWalls()[i][0] / visionScale);
				int y = (int) (visionSnapShot.getWalls()[i][1] / visionScale);

				Coordinate c = new Coordinate(x, y);
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

		double x = visionSnapShot.getCross()[0] / visionScale;
		double y = visionSnapShot.getCross()[1] / visionScale;

		Coordinate coord = new Coordinate(x, y);
		changeToRobotFormat(coord);

		// TODO: Radius is maybe not 10 always
		return new Cross(coord, 10);
	}

	private ArrayList<Goal> calculateGoals(ArrayList<Wall> walls) {
		ArrayList<Goal> goals = new ArrayList<Goal>();

		Goal smallGoal = new Goal();
		Goal largeGoal = new Goal();

		Wall wall1 = walls.get(0);
		Wall wall2 = walls.get(1);

		Coordinate smallGoalCoord = new Coordinate(wall1.lower.x, (double) wall1.upper.y / 2);
		Coordinate largeGoalCoord = new Coordinate(wall2.lower.x, (double) wall2.upper.y / 2);

		changeToRobotFormat(smallGoalCoord);
		changeToRobotFormat(largeGoalCoord);

		smallGoal.coordinate1 = smallGoalCoord;
		largeGoal.coordinate1 = largeGoalCoord;
		smallGoal.robotLocation = new Robot(new Coordinate(smallGoal.coordinate1.x + 20, smallGoal.coordinate1.y),
				180.0);
		largeGoal.robotLocation = new Robot(new Coordinate(largeGoal.coordinate1.x - 20, largeGoal.coordinate1.y), 0.0);

		goals.add(smallGoal);
		goals.add(largeGoal);

		return goals;
	}

	private Robot calculateRobotLocation() {

		Robot roboloc = new Robot();

		if (visionSnapShot.getRobot() != null) {
			int recievedArray[][] = visionSnapShot.getRobot();

			// Calculate orientation via geometry
			Coordinate smallCircleCoordinate = new Coordinate((recievedArray[0][0] / visionScale),
					(recievedArray[0][1] / visionScale));
			Coordinate largeCircleCoordinate = new Coordinate((recievedArray[1][0] / visionScale),
					(recievedArray[1][1] / visionScale));
			Coordinate zeroPoint = new Coordinate(((recievedArray[1][0]) / visionScale) + 50,
					(recievedArray[1][1] / visionScale));

			perspectiveTransform(smallCircleCoordinate, roboloc.height);
			perspectiveTransform(largeCircleCoordinate, roboloc.height);
			perspectiveTransform(zeroPoint, roboloc.height);

			changeToRobotFormat(smallCircleCoordinate);
			changeToRobotFormat(largeCircleCoordinate);
			changeToRobotFormat(zeroPoint);

			double len = Point2D.distance(largeCircleCoordinate.x, largeCircleCoordinate.y, smallCircleCoordinate.x,
					smallCircleCoordinate.y);

			double x = ((1 - ClientConstants.robotAxisShift) * largeCircleCoordinate.x
					+ ClientConstants.robotAxisShift * smallCircleCoordinate.x);
			double y = ((1 - ClientConstants.robotAxisShift) * largeCircleCoordinate.y
					+ ClientConstants.robotAxisShift * smallCircleCoordinate.y);

			largeCircleCoordinate = new Coordinate(x, y);

			double a = Point2D.distance(smallCircleCoordinate.x, smallCircleCoordinate.y, zeroPoint.x, zeroPoint.y);
			double b = zeroPoint.x - largeCircleCoordinate.x;
			double c = Point2D.distance(largeCircleCoordinate.x, largeCircleCoordinate.y, smallCircleCoordinate.x,
					smallCircleCoordinate.y);

			double cosA = (b * b + c * c - a * a) / (2 * b * c);
			double radA = Math.acos(cosA);
			double degrees = Math.toDegrees(radA);

			double orientation;

			if (largeCircleCoordinate.y > smallCircleCoordinate.y) {
				orientation = 360 - degrees;
			} else
				orientation = degrees;

			roboloc.coordinate = largeCircleCoordinate;
			roboloc.orientation = orientation;
		}
		else {
			roboloc.coordinate = new Coordinate(0.0,0.0);
			roboloc.orientation = 0.0;
		}

		return roboloc;
	}

	private void changeToRobotFormat(Coordinate coord) {
		coord.y = walls.get(0).upper.y - coord.y;
	}

	private void perspectiveTransform(Coordinate coord, double height) {
		// Find height differences and the proportion
		double heightProportion = (ClientConstants.cameraHeight - height) / ClientConstants.cameraHeight;

		// Find the scewed distance caused of height differences
		coord.x = (1 - heightProportion) * cameraX + heightProportion * coord.x;
		coord.y = (1 - heightProportion) * cameraY + heightProportion * coord.y;

	}

	private ArrayList<Coordinate> calculateQuadrants() {
		ArrayList<Coordinate> quadrants = new ArrayList<Coordinate>();
		Coordinate middle = new Coordinate(0, 0);

		middle.x = visionSnapShot.getCross()[0];
		middle.y = visionSnapShot.getCross()[1];

		getQuadrantsEvenSplit(quadrants, middle);

		return quadrants;
	}

	// Gets cross based on map size.
	private void getQuadrantsEvenSplit(ArrayList<Coordinate> quadrants, Coordinate middle) {
		middle.x = (walls.get(0).upper.x + walls.get(0).lower.x + walls.get(1).upper.x + walls.get(1).lower.x) / 4;
		middle.y = (walls.get(0).upper.y + walls.get(0).lower.y + walls.get(1).upper.y + walls.get(1).lower.y) / 4;

		quadrants.add(new Coordinate((walls.get(0).upper.x + middle.x) / 2, (walls.get(0).upper.y + middle.y) / 2));
		quadrants.add(new Coordinate((walls.get(0).lower.x + middle.x) / 2, (walls.get(0).lower.y + middle.y) / 2));
		quadrants.add(new Coordinate((walls.get(1).upper.x + middle.x) / 2, (walls.get(1).upper.y + middle.y) / 2));
		quadrants.add(new Coordinate((walls.get(1).lower.x + middle.x) / 2, (walls.get(1).lower.y + middle.y) / 2));
	}

	// Unused. Gets quadrants based on where cross is.
	private void getQuadrantsCrossSplit(ArrayList<Coordinate> quadrants, Coordinate middle) {
		quadrants.add(new Coordinate(((walls.get(0).upper.x + middle.x) / 2) / visionScale,
				((walls.get(0).upper.y + middle.y) / 2) / visionScale));
		quadrants.add(new Coordinate(((walls.get(0).lower.x + middle.x) / 2) / visionScale,
				((walls.get(0).lower.y + middle.y) / 2) / visionScale));
		quadrants.add(new Coordinate(((walls.get(1).upper.x + middle.x) / 2) / visionScale,
				((walls.get(1).upper.y + middle.y) / 2) / visionScale));
		quadrants.add(new Coordinate(((walls.get(1).lower.x + middle.x) / 2) / visionScale,
				((walls.get(1).lower.y + middle.y) / 2) / visionScale));
	}
}
