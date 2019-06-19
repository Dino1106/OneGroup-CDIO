package testRobot;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

import constants.ServerConstants;
import model.Coordinate;

public class TestMainServer extends Thread {

	private static Socket client;
	private static boolean looping = true;
	private static ServerSocket server;
	private Coordinate robotLocation;
	private double robotOrientation;

	public TestMainServer(Socket client) {
		TestMainServer.client = client;

		this.robotLocation = new Coordinate(0.0, 0.0);
		this.robotOrientation = 0.0;
	}

	public static void main(String[] args) throws IOException {
		server = new ServerSocket(ServerConstants.port);

		while(looping) {
			System.out.println("Awaiting client...");
			System.out.println("My ip is: " + Inet4Address.getLocalHost().getHostAddress());
			System.out.println("-----------------------------------\n\n");
			client = server.accept();
			new TestMainServer(client).start();
		}
	}

	public void run() {
		System.out.println("CLIENT CONNECTED");
		try {
			InputStream in = client.getInputStream();
			DataInputStream dIn = new DataInputStream(in);

			while(client != null) {
				String input = dIn.readUTF();
				String[] splitInputs = input.split(" ");
				int typeOfCommand = Integer.parseInt(splitInputs[0]);

				switch (typeOfCommand) {
				case 2:
					Coordinate coordinateForward = new Coordinate(Double.parseDouble(splitInputs[1]), Double.parseDouble(splitInputs[2]));
					int driveToSpeed = Integer.parseInt(splitInputs[3]);
					drive(coordinateForward, driveToSpeed);
					break;
				case 3:
					double centimeters = Double.parseDouble(splitInputs[1]);
					int travelSpeed = Integer.parseInt(splitInputs[2]);
					travel(centimeters, travelSpeed);
					break;
				case 4:
					boolean pickUp = Boolean.parseBoolean(splitInputs[1]);
					pickUpBalls(pickUp);
					break;
				case 5:
					double degrees = Double.parseDouble(splitInputs[1]);
					rotate(degrees);
					break;
				case 7:
					int soundToPlay = Integer.parseInt(splitInputs[1]);
					playSound(soundToPlay);
					break;
				case 8:
					Coordinate robotLocation = new Coordinate(Double.parseDouble(splitInputs[1]), Double.parseDouble(splitInputs[2]));
					double heading = Double.parseDouble(splitInputs[3]);
					setRobotLocation(robotLocation, heading);
					break;
				default:
					break;
				}
			}

		} catch (IOException e) {
			return;
		}
	}

	private void drive(Coordinate coordinate, int speed) {
		System.out.println("--------- ROBOT - drive(Coordinate coordinate, int speed) ---------");
		System.out.println("My Location: " + this.robotLocation.toString());
		System.out.println("My Orientation: " + this.robotOrientation + " degrees");
		System.out.println("Going to coordinate " + coordinate.toString() + ", with speed: " + speed);
		System.out.println("\n\n");
	}

	private void travel(double centimeters, int speed) {
		System.out.println("--------- ROBOT - travel(int centimeters, int speed) ---------");
		System.out.println("My Location: " + this.robotLocation.toString());
		System.out.println("My Orientation: " + this.robotOrientation + " degrees");
		System.out.println("Going forward " + centimeters + " cm, with speed: " + speed);
		System.out.println("\n\n");
	}

	private void pickUpBalls(boolean pickUp) {
		System.out.println("--------- ROBOT - pickUpBalls(boolean pickUp) ---------");
		System.out.println("Pickupmode: " + pickUp);
		System.out.println("\n\n");
	}

	private void rotate(double degrees) {
		System.out.println("--------- ROBOT - rotate(double degrees) ---------");
		System.out.println("Rotate " + degrees + " degrees");
		System.out.println("\n\n");
	}

	private void playSound(int soundToPlay) {
		System.out.println("--------- ROBOT - playSound(int soundToPlay) ---------");
		System.out.println("Playing sound nr.: " + soundToPlay);
		System.out.println("\n\n");
	}

	private void setRobotLocation(Coordinate coordinate, double heading) {
		System.out.println("--------- ROBOT - setRobotLocation(Coordinate coordinate, double heading) ---------");
		System.out.println("My current location: " + this.robotLocation.toString());
		System.out.println("My current orientation: " + this.robotOrientation + " degrees");
		this.robotLocation = coordinate;
		this.robotOrientation = heading;

		System.out.println("My new location: " + this.robotLocation.toString());
		System.out.println("My new orientation: " + this.robotOrientation + " degrees");
		System.out.println("\n\n");
	}

}
