
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer extends Thread {
	
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
		
		while(looping) {
			System.out.println("Awaiting client...");
			client = server.accept();
			dOut = new DataOutputStream(client.getOutputStream());
			new MainServer(client).start();
		}
    }
	
	public void carDrive(Coordinate coordinate) {
		try {
			// Write response to client
			boolean response = robotControls.drive(coordinate);
			dOut.writeBoolean(response);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void setMotorSpeed(int speed) {
		robotControls.setMotorSpeed(speed);
	}
	
	public void carPickUpBalls(boolean pickUp) {
		try {
			boolean response = robotControls.pickUpBalls(pickUp);
			dOut.writeBoolean(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void rotate(int degrees) {
		robotControls.rotateToOrientation(degrees);
	}
	
	public void setPickUpSpeed(int speed) {
		robotControls.setPickUpSpeed(speed);
	}
	
	public void playSound(int soundToPlay) {
		robotControls.playSound(soundToPlay);
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
				case 1:
					int speed = Integer.parseInt(splitInputs[1]);
					setMotorSpeed(speed);
					break;
				case 2:
					Coordinate coordinate = new Coordinate(Integer.parseInt(splitInputs[1]), Integer.parseInt(splitInputs[2]));
					carDrive(coordinate);
					break;
				case 3:
					boolean pickUp = Boolean.parseBoolean(splitInputs[1]);
					carPickUpBalls(pickUp);
					break;
				case 4:
					int degrees = Integer.parseInt(splitInputs[1]);
					rotate(degrees);
					break;
				case 5:
					int pickUpSpeed = Integer.parseInt(splitInputs[1]);
					setPickUpSpeed(pickUpSpeed);
					break;
				case 6:
					int soundToPlay = Integer.parseInt(splitInputs[1]);
					playSound(soundToPlay);
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
