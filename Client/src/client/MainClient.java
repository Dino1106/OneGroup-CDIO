package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import server.MainServer;
 
public class MainClient extends Frame implements KeyListener { 

	public static final int PORT = MainServer.port;
	public static final int PICKUP = 0,
							SPIT   = 1,
							CLOSE  = -1;
	Button btnConnect;
	TextField textIpAddress;
	TextArea messages;
	
	private Socket socket;
	private DataOutputStream dOut;
	
	public MainClient(String title, String ip) {

		super(title);
		this.setSize(400, 300); 
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { 
				System.out.println("Ending Client"); 
				disconnect();
					System.exit(0);
				} 
			});
			buildGUI(ip); 
			this.setVisible(true); 
			btnConnect.addKeyListener(this);
	}
 
	public static void main(String[] args) throws IOException { 
		String ip = "192.168.43.187"; 
		if (args.length > 0) ip = args[0];
		System.out.println("Starting client");
		new MainClient("Test continous connection", ip);  
	}
	
	public void buildGUI(String ip) {
		Panel mainPanel = new Panel (new BorderLayout ());
		ControlListener cl = new ControlListener();
		btnConnect = new Button("Connect"); 
		btnConnect.addActionListener(cl);
		textIpAddress = new TextField(ip,16);
		messages = new TextArea("status: DISCONNECTED"); 
		messages.setEditable(false);
		
		Panel north = new Panel(new FlowLayout(FlowLayout.LEFT));
		north.add(btnConnect); 
		north.add(textIpAddress);
		
		Panel center = new Panel(new GridLayout(5,1));
		center.add(new Label("P to pick up\nD to deliver"));
		
		Panel center4 = new Panel(new FlowLayout(FlowLayout.LEFT));
		center4.add(messages); 
		center.add(center4);
		mainPanel.add(north, "North"); 
		mainPanel.add(center, "Center"); 
		this.add(mainPanel);
	}
	
	private void sendCommand(int command){
		// Send coordinates to Server: 
		messages.setText("status: SENDING command."); 
		
		try {
			dOut.writeInt (command); 
		} catch(IOException io) {
			messages.setText("status: ERROR Problems occurred sending data.");
		}
			messages.setText("status: Command SENT.");
		}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		sendCommand(e.getKeyCode());
		System.out.println("Pressed " + e.getKeyCode());
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private class ControlListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("Connect")) {
				try {
					socket = new Socket(textIpAddress.getText(), PORT);
					dOut = new DataOutputStream(socket.getOutputStream());
					messages.setText("status: CONNECTED");
					btnConnect.setLabel("Disconnect"); 
					} catch (Exception exc) {
						messages.setText("status: FAILURE Error establishing connection with server.");
						System.out.println("Error: " + exc);
					}
				}
			else if (command.equals("Disconnect")) { 
				disconnect();
			}
		}
		
	}
 
	public void disconnect() { 
		try {
			sendCommand(CLOSE); socket.close();
			btnConnect.setLabel("Connect"); messages.setText("status: DISCONNECTED");
		} catch (Exception exc) { 
			messages.setText("status: FAILURE Error closing connection with server."); 
			System.out.println("Error: " + exc);
		} 
	}
} 
