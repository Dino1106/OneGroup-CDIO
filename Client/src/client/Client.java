package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import constants.ClientConstants;
import model.Coordinate;
import model.Robot;
import vision.TestVisionTranslator;
 
public class Client { 

	private Socket socket;
	private DataOutputStream dOut;
	private DataInputStream dIn;
	private TestVisionTranslator testVisionTranslator = null;
	private boolean serverResponse;
	
	public void connect() throws IOException {
		System.out.println("Starting client");
		socket = new Socket(ClientConstants.ip, ClientConstants.port);
		dOut = new DataOutputStream(socket.getOutputStream());
		dIn = new DataInputStream(socket.getInputStream());	
	}
	
	/**
	 * Only for testing purposes
	 * @param ipToTestMainServer
	 * @throws IOException
	 */
	public void connect(TestVisionTranslator testVisionTranslator) throws IOException {
		System.out.println("Starting client with testVision");
		this.testVisionTranslator = testVisionTranslator;
		socket = new Socket(ClientConstants.ip, ClientConstants.port);
		dOut = new DataOutputStream(socket.getOutputStream());	
	}
	
	public void sendMotorSpeed(int speed) {
		try {
			String speedString = "1 " + speed;
			dOut.writeUTF(speedString);
			dOut.flush();
		} catch (IOException e) {
			System.out.println("Client error: " + e.getStackTrace());
		}
	}
	
	public void sendCoordinate(Coordinate destination, int speed){
		// Send coordinates to Server: 
		try {
			String coordinateString = "2 " + (destination.x) + " " + (destination.y) + " " + speed;
			dOut.writeUTF(coordinateString);
			dOut.flush();
			boolean convertedResponse = dIn.readBoolean();
			
			if(convertedResponse) {
				System.out.println("Path done: " + serverResponse);
				return;
			}
			
			// Tells the testVision that the 
			if (testVisionTranslator != null) {
				testVisionTranslator.gotoCoordinate(destination);
			}
		} catch(IOException io) {
			System.out.println("Client error: " + io.getStackTrace());
		}
	}

	public void sendTravelDistance(double centimeters, int speed){
		System.out.println("SendTravelDistance: " + centimeters + ", " + speed);
		// Send coordinates to Server: 
		try {
			String coordinateString = "3 " + centimeters + " " + speed;
			dOut.writeUTF(coordinateString);
			dOut.flush();
			boolean serverResponse = dIn.readBoolean();
			
			if(serverResponse) {
				System.out.println("Path done: " + serverResponse);
				return;
			}
			
		} catch(IOException io) {
			System.out.println("Client error: " + io.getStackTrace());
		}
	}
	
	public void pickUpBalls(boolean pickUp) {
		System.out.println("pickUpBalls: " + pickUp);
		try {
			String pickUpString = "4 " + pickUp;
			dOut.writeUTF(pickUpString);
			dOut.flush();
		} catch (IOException e) {
			System.out.println("Client error: " + e.getStackTrace());
		}
	}
	
	public void rotate(double orientation1) {
		System.out.println("rotate, degrees: " + orientation1);
		try {
			String rotateString = "5 " + orientation1;
			dOut.writeUTF(rotateString);
			dOut.flush();
		} catch (IOException e) {
			System.out.println("Client error: " + e.getStackTrace());
		}
	}
	
	public void sendPickUpSpeed(int speed) {
		System.out.println("sendPickUpSpeed, speed: " + speed);
		try {
			String speedString = "6 " + speed;
			dOut.writeUTF(speedString);
			dOut.flush();
		} catch (IOException e) {
			System.out.println("Client error: " + e.getStackTrace());
		}
		
	}
	
	public void sendSound(int sound) {
		System.out.println("sendSound, sound: " + sound);
		try {
			String soundString = "7 " + sound;
			dOut.writeUTF(soundString);
			dOut.flush();
		} catch (IOException e) {
			System.out.println("Client error: " + e.getStackTrace());
		}
		
	}
	
	public void setRobotLocation(Robot robot) {
		System.out.println("setRobotLocation, coordinate: " + robot.coordinate + ", orientation:" + robot.orientation);
		try {
			String coordinateString = "8 " + robot.coordinate.x + " " + robot.coordinate.y + " " + robot.orientation;
			dOut.writeUTF(coordinateString);
			dOut.flush();
		} catch (IOException e) {
			System.out.println("Client error: " + e.getStackTrace());
		}
	}
	
	public void stopAllMotors() {
		try {
			String stopMotors = "9 ";
			dOut.writeUTF(stopMotors);
			dOut.flush();
		} catch(IOException e) {
			System.out.println("Client error: " + e.getStackTrace());
		}
	}
 
	public void disconnect() { 
		try {
			socket.close();
			dOut.close();
			dIn.close();
		} catch (Exception exc) { 
			System.out.println("Client error: " + exc.getStackTrace());
		} 
	}
} 