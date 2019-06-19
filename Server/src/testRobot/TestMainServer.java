package testRobot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

import interfaces.IMainServer;
import model.Coordinate;

public class TestMainServer extends Thread implements IMainServer{

	private static Socket client;
	private static boolean looping = true;
	private static ServerSocket server;
	private Coordinate robotLocation;
	private double robotOrientation;
	private static DataOutputStream dOut;

	public TestMainServer(Socket client) {
		TestMainServer.client = client;

		this.robotLocation = new Coordinate(0.0, 0.0);
		this.robotOrientation = 0.0;
	}

	public static void main(String[] args) throws IOException {
		server = new ServerSocket(PORT);

		while(looping) {
			System.out.println("Awaiting client...");
			System.out.println("My ip is: " + Inet4Address.getLocalHost().getHostAddress());
			System.out.println("-----------------------------------\n\n");
			client = server.accept();
			dOut = new DataOutputStream(client.getOutputStream());
			new TestMainServer(client).start();
		}
	}

	@Override
	public void carDrive(Coordinate coordinate, int speed) {
		System.out.println("--------- ROBOT - carDrive(Coordinate coordinate, int speed) ---------");
		System.out.println("My Location: " + this.robotLocation.toString());
		System.out.println("My Orientation: " + this.robotOrientation + " degrees");
		System.out.println("Going to coordinate " + coordinate.toString() + ", with speed: " + speed);
		System.out.println("\n\n");
		try {
			boolean response = true;
			dOut.writeUTF("2 " + response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void carTravel(double centimeters, int speed) {
		System.out.println("--------- ROBOT - carTravel(int centimeters, int speed) ---------");
		System.out.println("My Location: " + this.robotLocation.toString());
		System.out.println("My Orientation: " + this.robotOrientation + " degrees");
		System.out.println("Going forward " + centimeters + " cm, with speed: " + speed);
		System.out.println("\n\n");
	}

	@Override
	public void carPickUpBalls(boolean pickUp) {
		System.out.println("--------- ROBOT - carPickUpBalls(boolean pickUp) ---------");
		System.out.println("Pickupmode: " + pickUp);
		System.out.println("\n\n");
	}

	@Override
	public void rotate(double degrees) {
		System.out.println("--------- ROBOT - rotate(double degrees) ---------");
		System.out.println("Rotate " + degrees + " degrees");
		System.out.println("\n\n");
		try {
			boolean response = true;
			dOut.writeUTF("1 " + response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setPickUpSpeed(int speed) {
		System.out.println("--------- ROBOT - setPickUpSpeed(int speed) ---------");
		System.out.println("Pickup speed set to: " + speed);
		System.out.println("\n\n");
	}

	@Override
	public void playSound(int soundToPlay) {
		System.out.println("--------- ROBOT - playSound(int soundToPlay) ---------");
		System.out.println("Playing sound nr.: " + soundToPlay);
		System.out.println("\n\n");
	}

	@Override
	public void setRobotLocation(Coordinate coordinate, double heading) {
		System.out.println("--------- ROBOT - setRobotLocation(Coordinate coordinate, double heading) ---------");
		System.out.println("My current location: " + this.robotLocation.toString());
		System.out.println("My current orientation: " + this.robotOrientation + " degrees");
		this.robotLocation = coordinate;
		this.robotOrientation = heading;

		System.out.println("My new location: " + this.robotLocation.toString());
		System.out.println("My new orientation: " + this.robotOrientation + " degrees");

		System.out.println("\n\n");
	}

	@Override
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
					carDrive(coordinateForward, driveToSpeed);
					break;
				case 3:
					int centimeters = Integer.parseInt(splitInputs[1]);
					int travelSpeed = Integer.parseInt(splitInputs[2]);
					carTravel(centimeters, travelSpeed);
					break;
				case 4:
					boolean pickUp = Boolean.parseBoolean(splitInputs[1]);
					carPickUpBalls(pickUp);
					break;
				case 5:
					double degrees = Double.parseDouble(splitInputs[1]);
					rotate(degrees);
					break;
				case 6:
					int pickUpSpeed = Integer.parseInt(splitInputs[1]);
					setPickUpSpeed(pickUpSpeed);
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


}
