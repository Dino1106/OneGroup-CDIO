package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import lejos.robotics.geometry.Point;
 
public class MainClient { 

	public static final int PORT = 1337;
	private static Socket socket;
	private static ObjectOutputStream oOut;
	private static ObjectInputStream oIn;
 
	public static void main(String[] args) throws IOException { 
		String ip = "192.168.43.187"; 
		if (args.length > 0) ip = args[0];
		System.out.println("Starting client");
		socket = new Socket(ip, PORT);
		oOut = new ObjectOutputStream(socket.getOutputStream());
		oIn = new ObjectInputStream(socket.getInputStream());
		
		sendCommand(new Coordinate(10, 0));
	}
	
	private static void sendCommand(Coordinate coordinate){
		// Send coordinates to server:
		try {
			oOut.writeObject(coordinate);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Read response from server:
		try {
			Point serverPoint = (Point) oIn.readObject();
			System.out.println(serverPoint);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
 
	public void disconnect() { 
		try {
			socket.close();
		} catch (Exception exc) { 
			System.out.println("Error: " + exc);
		} 
	}
} 