package server; 
 
import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.utility.Delay; 
 
public class RobertServer { 
	 
	public static final int port = 1337; 
 
	public static void main(String[] args) throws IOException { 
		 ServerSocket sersock = new ServerSocket(port); 
	     System.out.println("Server is ready");  //  message to know the server is running
	 
	     Socket clientStream = sersock.accept();               
	                                                                                          
	     InputStream istream = clientStream.getInputStream();  
	     DataInputStream dstream = new DataInputStream(istream);
	 
		 String message2 = dstream.readLine();
	     System.out.println(message2);
	     
	     UnregulatedMotor left_wheel 				= new UnregulatedMotor(MotorPort.A);
	     UnregulatedMotor right_wheel 				= new UnregulatedMotor(MotorPort.B);
	     final UnregulatedMotor ball_picker_left 	= new UnregulatedMotor(MotorPort.C);
	     final UnregulatedMotor ball_picker_right   = new UnregulatedMotor(MotorPort.D);
	     
	     EV3TouchSensor touchSensor 				= new EV3TouchSensor(SensorPort.S1);
		 
	     //EV3UltrasonicSensor ultraSonic 	= new EV3UltrasonicSensor(SensorPort.S1);
	     
	     
	     
	     //left_wheel.setPower(50);
	     //left_wheel.forward();
	     
	     //right_wheel.setPower(50);
	     //right_wheel.forward();
	     
	     /* left backward-right forward suck */
	     ball_picker_left.setPower(85);
	     ball_picker_left.backward();
	     
	     ball_picker_right.setPower(85);
	     ball_picker_right.forward();
	     
	     while (true) {
	         int sampleSize = touchSensor.sampleSize();

	         float[] touchSample = new float[sampleSize];
	         touchSensor.fetchSample(touchSample, 0);
	         
	         if(touchSample[0]==1){
	             left_wheel.stop();
	             right_wheel.stop();
	             ball_picker_left.stop();
	             ball_picker_right.stop();
	             break;
	         }
	         
	     }
	     
	     left_wheel.close(); 
	     right_wheel.close();
	     ball_picker_left.close();
	     ball_picker_right.close();
	     
	     touchSensor.close();
	     
	     dstream.close(); 
	     istream.close(); 
	     clientStream.close(); 
	     sersock.close();
	  } 
 
} 
