package robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import interfaces.IMainServer;
import lejos.hardware.Sound;
import model.Coordinate;

public class MainServer extends Thread implements IMainServer {
	
	public static final int PORT = 1337;
	private static Socket client;
	private RobotMovement robotControls;
	private static boolean looping = true;
	private static ServerSocket server;
	private static DataOutputStream dOut;

	public MainServer(Socket client) {
		MainServer.client = client;
		this.robotControls = new RobotMovement();
	}
	
	public static void main(String[] args) throws IOException {
		server = new ServerSocket(PORT);
		Sound.playSample(new File("pickUpSound.wav"));

		while(looping) {
			System.out.println("Awaiting client...");
			client = server.accept();
			dOut = new DataOutputStream(client.getOutputStream());
			new MainServer(client).start();
		}
    }
	
	public void carDrive(Coordinate coordinate, int speed) {
		try {
			// Write response to client
			boolean response = robotControls.drive(coordinate, speed);
			dOut.writeUTF("2 " + response);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void carTravel(double centimeters, int speed) {
		robotControls.travel(centimeters, speed);
	}
	
	public void carPickUpBalls(boolean pickUp) {
		try {
			boolean response = robotControls.pickUpBalls(pickUp);
			dOut.writeUTF("1 " + response);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void rotate(double degrees) {
		robotControls.rotateToOrientation(degrees);
	}
	
	public void setPickUpSpeed(int speed) {
		robotControls.setPickUpSpeed(speed);
	}
	
	public void playSound(int soundToPlay) {
		robotControls.playSound(soundToPlay);
	}
	
	public void setRobotLocation(Coordinate coordinate, double heading) {
		robotControls.setRobotLocation(coordinate, heading);
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
					carDrive(coordinateForward, driveToSpeed);
					break;
				case 3:
					double centimeters = Double.parseDouble(splitInputs[1]);
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
