package server; 
 
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Battery;
import lejos.hardware.Sound;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay; 
 
public class RobertServer { 
	 
	public static final int port = 1337; 
 
	public static void main(String[] args) throws IOException { 
		// Setup server. 
		ServerSocket server = new ServerSocket(port); 
		System.out.println("Awaiting Jane"); 
		Socket client = server.accept(); 
		System.out.println("Jane CONNECTED."); 
		
		// Setup motors.
        UnregulatedMotor motorA = new UnregulatedMotor(MotorPort.A);
        UnregulatedMotor motorB = new UnregulatedMotor(MotorPort.B);
		 
		// Establish an outputstream and a corresponding dataoutputstream. 
		OutputStream outputStream = client.getOutputStream(); 
		DataOutputStream dataOutputStream =  new DataOutputStream(outputStream); 
		
		// Corresponding input.
		InputStream inputStream = client.getInputStream(); 
		DataInputStream dataInputStream = new DataInputStream(inputStream); 
		 
		dataOutputStream.writeUTF("Battery	" + Battery.getVoltage()); 
		dataOutputStream.flush(); 
		
		int moveSec = dataInputStream.readInt();
        
        motorA.setPower(100);
        motorB.setPower(100);
		
        Delay.msDelay(1000 * moveSec);
        
        motorA.stop();
        motorB.stop();
        
        motorA.close(); 
        motorB.close();
        
		server.close();
		
        Sound.beep();
 
	} 
 
} 
