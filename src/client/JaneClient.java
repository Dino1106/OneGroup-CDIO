package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import server.RobotMovement; 
 
public class JaneClient { 
 
	public static void main(String[] args) throws IOException { 
		String ip = "192.168.43.187"; 
		if (args.length > 0) 
			ip = args[0]; 
		Socket sock = new Socket(ip, RobotMovement.port); 
		System.out.println("Jane connected to Robert!"); 
 
		// Initialize input and output.
		InputStream inputStream = sock.getInputStream(); 
		DataInputStream dataInputStream = new DataInputStream(inputStream); 
		
		OutputStream outputStream = sock.getOutputStream(); 
		DataOutputStream dataOutputStream =  new DataOutputStream(outputStream); 
		
		String str = dataInputStream.readUTF(); 
		System.out.println(str); 
		
		dataOutputStream.writeInt(5);
		dataOutputStream.flush();
		
		sock.close(); 
	} 
 
} 
