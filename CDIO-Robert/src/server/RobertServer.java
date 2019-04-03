package server; 
 
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Battery; 
 
public class RobertServer { 
	 
	public static final int port = 1337; 
 
	public static void main(String[] args) throws IOException { 
		// Setup server. 
		ServerSocket server = new ServerSocket(port); 
		System.out.println("Awaiting Jane"); 
		Socket client = server.accept(); 
		System.out.println("Jane CONNECTED."); 
		 
		// Establish an outputstream and a corresponding dataoutputstream. 
		OutputStream outputStream = client.getOutputStream(); 
		DataOutputStream dataOutputStream =  new DataOutputStream(outputStream); 
		 
		dataOutputStream.writeUTF("Battery	" + Battery.getVoltage()); 
		 
		dataOutputStream.flush(); 
		server.close(); 
 
	} 
 
} 
