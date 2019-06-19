package interfaces;

import model.Coordinate;

public interface IMainServer {

	int PORT = 1337;

	void carDrive(Coordinate coordinate, int speed);

	void carTravel(int centimeters, int speed);

	void carPickUpBalls(boolean pickUp);

	void rotate(double degrees);

	void setPickUpSpeed(int speed);

	void playSound(int soundToPlay);

	void setRobotLocation(Coordinate coordinate, double heading);

	void run();

}