

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.robotics.geometry.Point;

public class MainServer extends Thread {
	
	public static final int PORT = 1337;
	
	private Socket client;
	private RobotMovement robotControls;
	private static boolean looping = true;
	private static ServerSocket server;
	private static ObjectOutputStream oOut;

	public MainServer(Socket client) {
		this.client = client;
		this.robotControls = new RobotMovement();
	}
	
	public static void main(String[] args) throws IOException {
		server = new ServerSocket(PORT);
		
		while(looping) {
			System.out.println("Awaiting client...");
			try {
				OutputStream sout = server.accept().getOutputStream();
				oOut = new ObjectOutputStream(sout);
			} catch (IOException e) {
				e.printStackTrace();
			}
			new MainServer(server.accept()).start();
		}
    }
	
	public void carAction(Coordinate coordinate) {
		Point newRobotPose = robotControls.drive(coordinate);
		
		// Send response back to client:
		try {
			oOut.writeObject(newRobotPose);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		System.out.println("CLIENT CONNECTED");
		try {
			InputStream in = client.getInputStream();
			ObjectInputStream oIn = new ObjectInputStream(in);
			
			while(client != null) {
				Coordinate coordinate;
				try {
					coordinate = (Coordinate) oIn.readObject();
					System.out.println("Coordinate: " + coordinate);
					
					if(coordinate == null) {
						client.close();
						client = null;
					} else {
						carAction(coordinate);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}