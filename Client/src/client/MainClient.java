package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import interfaces.IMainClient;
import model.Coordinate;
import model.Robot;
 
public class MainClient implements IMainClient { 

	private Socket socket;
	private DataOutputStream dOut;
	private DataInputStream dIn;
 
	@Override
	public void connect() throws IOException {
		String ip = "192.168.43.187"; 
		System.out.println("Starting client");
		socket = new Socket(ip, PORT);
		dOut = new DataOutputStream(socket.getOutputStream());
		dIn = new DataInputStream(socket.getInputStream());	
	}
	
	@Override
	public void sendMotorSpeed(int speed) {
		try {
			String speedString = "1 " + speed;
			dOut.writeUTF(speedString);
			dOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendCoordinate(Coordinate destination, int speed){
		// Send coordinates to Server: 
		try {
			String coordinateString = "2 " + (destination.x) + " " + (destination.y) + " " + speed;
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

	@Override
	public void sendTravelDistance(int centimeters, int speed){
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
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
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
 
	@Override
	public void disconnect() { 
		try {
			socket.close();
			dOut.close();
			dIn.close();
		} catch (Exception exc) { 
			exc.printStackTrace();
		} 
	}
} 