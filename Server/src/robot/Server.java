package robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import constants.ServerConstants;
import lejos.hardware.Sound;
import model.Coordinate;

public class Server extends Thread {
	
	private static Socket client;
	private RobotMovement robotControls;
	private static boolean looping = true;
	private static ServerSocket server;
	private static DataOutputStream dOut;

	public Server(Socket client) throws IOException {
		Server.client = client;
		dOut = new DataOutputStream(client.getOutputStream());
		this.robotControls = new RobotMovement();
	}
	
	public static void main(String[] args) throws IOException {
		server = new ServerSocket(ServerConstants.port);
		Sound.playSample(new File("pickUpSound.wav"));

		while(looping) {
			System.out.println("Awaiting client...");
			client = server.accept();
			new Server(client).start();
		}
    }
	
	public void carDrive(Coordinate coordinate, int speed) {
		try {
			// Write response to client
			boolean response = robotControls.drive(coordinate, speed);
			dOut.writeBoolean(response);
		} catch (IOException e) {
			e.printStackTrace();
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
					carDrive(coordinateForward, driveToSpeed);
					break;
				case 3:
					double centimeters = Double.parseDouble(splitInputs[1]);
					int travelSpeed = Integer.parseInt(splitInputs[2]);
					robotControls.travel(centimeters, travelSpeed);
					break;
				case 4:
					boolean pickUp = Boolean.parseBoolean(splitInputs[1]);
					robotControls.pickUpBalls(pickUp);
					break;
				case 5:
					double degrees = Double.parseDouble(splitInputs[1]);
					robotControls.rotate(degrees);
					break;
				case 7:
					int soundToPlay = Integer.parseInt(splitInputs[1]);
					robotControls.playSound(soundToPlay);
					break;
				case 8:
					Coordinate robotLocation = new Coordinate(Double.parseDouble(splitInputs[1]), Double.parseDouble(splitInputs[2]));
					double heading = Double.parseDouble(splitInputs[3]);
					robotControls.setRobotLocation(robotLocation, heading);
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
