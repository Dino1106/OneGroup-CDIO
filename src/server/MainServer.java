package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import client.MainClient;
import lejos.hardware.Button;
import lejos.hardware.Key;

public class MainServer extends Thread {
	
	public static final int port = 1337;
	private Socket client;
	private RobotMovement robotControls;
	private static boolean looping = true;
	private static ServerSocket server;

	public MainServer(Socket client) {
		this.client = client;
		this.robotControls = new RobotMovement();
		Button.ESCAPE.addKeyListener(new EscapeListener());
	}
	
	public static void main(String[] args) throws IOException {
		server = new ServerSocket(port);
		
		while(looping) {
			System.out.println("Awaiting client...");
			new MainServer(server.accept()).start();
		}
    }
	
	public void carAction(int command) {
		switch(command) {
		case MainClient.PICKUP:
			robotControls.pickUpBalls(true);
			break;
		case MainClient.SPIT:
			robotControls.pickUpBalls(false);
			break;
		}
	}
	
	public void run() {
		System.out.println("CLIENT CONNECTED");
		try {
			InputStream in = client.getInputStream();
			DataInputStream dIn = new DataInputStream(in);
			
			while(client != null) {
				int command = dIn.readInt();
				System.out.println("MODE: " + command);
				
				if(command == MainClient.CLOSE) {
					client.close();
					client = null;
				} else {
					carAction(command);
				}
			}
			
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private class EscapeListener implements lejos.hardware.KeyListener {

		@Override
		public void keyPressed(Key k) {
			looping = false;
			System.exit(0);
			
		}

		@Override
		public void keyReleased(Key k) {
			// TODO Auto-generated method stub
			
		}
	
	}
}
