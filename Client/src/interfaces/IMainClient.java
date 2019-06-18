package interfaces;

import java.io.IOException;

import model.Coordinate;
import model.Robot;

public interface IMainClient {

	int PORT = 1337;

	void connect() throws IOException;

	void sendMotorSpeed(int speed);

	void sendCoordinate(Coordinate destination, int speed);

	void sendTravelDistance(int centimeters, int speed);

	void pickUpBalls(boolean pickUp);

	void rotate(double orientation1);

	void sendPickUpSpeed(int speed);

	void sendSound(int sound);

	void setRobotLocation(Robot robot);

	void disconnect();

}