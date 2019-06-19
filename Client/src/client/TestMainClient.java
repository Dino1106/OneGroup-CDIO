package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import interfaces.IMainClient;
import model.Coordinate;
import model.Robot;
import vision.TestVisionTranslator;
 
public class TestMainClient implements IMainClient{ 

	public static final int PORT = 1337;
	
	private Socket socket;
	private DataOutputStream dOut;
	private DataInputStream dIn;

	private TestVisionTranslator testVisionTranslator;
	
	/**
	 * Only for testing purposes
	 * @param ipToTestMainServer
	 * @throws IOException
	 */
	public void connect(String ipToTestMainServer, TestVisionTranslator testVisionTranslator) throws IOException {
		System.out.println("Starting client");
		this.testVisionTranslator = testVisionTranslator;
		socket = new Socket(ipToTestMainServer, PORT);
		dOut = new DataOutputStream(socket.getOutputStream());
		dIn = new DataInputStream(socket.getInputStream());	
	}
	
	@Override
	public void connect() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public void sendMotorSpeed(int speed) {
		try {
			String speedString = "1 " + speed;
			dOut.writeUTF(speedString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendCoordinate(Coordinate destination, int speed){
		testVisionTranslator.gotoCoordinate(destination);

		// Send coordinates to Server: 
		try {
			String coordinateString = "2 " + (destination.x) + " " + (destination.y) + " " + speed;
			dOut.writeUTF(coordinateString);
			dOut.flush();
			String serverResponse = dIn.readUTF();
			System.out.print("hue123");
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
	
	public void pickUpBalls(boolean pickUp) {
		System.out.println("pickUpBalls: " + pickUp);
		try {
			String pickUpString = "4 " + pickUp;
			dOut.writeUTF(pickUpString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void rotate(double orientation1) {
		System.out.println("rotate, degrees: " + orientation1);
		try {
			String rotateString = "5 " + orientation1;
			dOut.writeUTF(rotateString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPickUpSpeed(int speed) {
		System.out.println("sendPickUpSpeed, speed: " + speed);
		try {
			String speedString = "6 " + speed;
			dOut.writeUTF(speedString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendSound(int sound) {
		System.out.println("sendSound, sound: " + sound);
		try {
			String soundString = "7 " + sound;
			dOut.writeUTF(soundString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setRobotLocation(Robot robot) {
		System.out.println("setRobotLocation, coordinate: " + robot.coordinate + ", orientation:" + robot.orientation);
		try {
			String coordinateString = "8 " + robot.coordinate.x + " " + robot.coordinate.y + " " + robot.orientation;
			dOut.writeUTF(coordinateString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	public void disconnect() { 
		try {
			socket.close();
			dOut.close();
			dIn.close();
		} catch (Exception exc) { 
			exc.printStackTrace();
		} 
	}

	@Override
	public void sendTravelDistance(double centimeters, int speed) {
		// TODO Auto-generated method stub
		
	}
} 