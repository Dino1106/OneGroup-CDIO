package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import model.Coordinate;
 
public class MainClient { 

	public static final int PORT = 1337;
	
	private static Socket socket;
	private static DataOutputStream dOut;
	private static DataInputStream dIn;
 
	public static void connect() throws IOException {
		String ip = "192.168.43.187"; 
		System.out.println("Starting client");
		socket = new Socket(ip, PORT);
		dOut = new DataOutputStream(socket.getOutputStream());
		dIn = new DataInputStream(socket.getInputStream());	
	}
	
	public static void sendMotorSpeed(int speed) {
		try {
			String speedString = "1 " + speed;
			dOut.writeUTF(speedString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendCoordinate(Coordinate coordinate, int speed){
		// Send coordinates to Server: 
		System.out.println("Going to coordinate: " + coordinate);
		try {
			String coordinateString = "2 " + coordinate.x + " " + coordinate.y + " " + speed;
			dOut.writeUTF(coordinateString);
			dOut.flush();
			String serverResponse = dIn.readUTF();
			String[] splitResponse = serverResponse.split(" ");
			boolean convertedResponse = Boolean.parseBoolean(splitResponse[1]);
			
			if(convertedResponse) {
				System.out.println("Path done: " + serverResponse);
			} else {
				// TODO: Add what to do
			}
			
		} catch(IOException io) {
			io.printStackTrace();
		}
	}

	public static void sendTravelDistance(int centimeters, int speed){
		System.out.println("SendTravelDistance: " + centimeters + ", " + speed);
		// Send coordinates to Server: 
		try {
			String coordinateString = "3 " + centimeters + " " + speed;
			dOut.writeUTF(coordinateString);
			dOut.flush();
			boolean serverResponse = dIn.readBoolean();
			
			if(serverResponse) {
				System.out.println("Path done: " + serverResponse);
			} else {
				// TODO: Add what to do
			}
			
		} catch(IOException io) {
			io.printStackTrace();
		}
	}
	
	public static void pickUpBalls(boolean pickUp) {
		System.out.println("pickUpBalls: " + pickUp);
		try {
			String pickUpString = "4 " + pickUp;
			dOut.writeUTF(pickUpString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void rotate(int degrees) {
		System.out.println("rotate, degrees: " + degrees);
		try {
			String rotateString = "5 " + degrees;
			dOut.writeUTF(rotateString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendPickUpSpeed(int speed) {
		System.out.println("sendPickUpSpeed, speed: " + speed);
		try {
			String speedString = "6 " + speed;
			dOut.writeUTF(speedString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void sendSound(int sound) {
		System.out.println("sendSound, sound: " + sound);
		try {
			String soundString = "7 " + sound;
			dOut.writeUTF(soundString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void setRobotLocation(Coordinate coordinate) {
		System.out.println("setRobotLocation, coordinate: " + coordinate);
		try {
			String coordinateString = "8 " + coordinate.x + " " + coordinate.y;
			dOut.writeUTF(coordinateString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	public static void disconnect() { 
		try {
			socket.close();
			dOut.close();
			dIn.close();
		} catch (Exception exc) { 
			exc.printStackTrace();
		} 
	}
} 