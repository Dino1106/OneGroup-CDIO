package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import server.RobertServer; 
 
public class JaneClient { 
 
	public static void main(String[] args) throws IOException { 
		String ip = "192.168.43.187"; 
		if (args.length > 0) 
			ip = args[0]; 
		Socket sock = new Socket(ip, RobertServer.port); 
		System.out.println("Jane connected to Robert!"); 
 
		InputStream inputStream = sock.getInputStream(); 
		DataInputStream dataInputStream = new DataInputStream(inputStream); 
		String str = dataInputStream.readUTF(); 
		System.out.println(str); 
		sock.close(); 
	} 
 
} 
