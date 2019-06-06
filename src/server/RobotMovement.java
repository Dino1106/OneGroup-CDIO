package server;

import client.Coordinate;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;

public class RobotMovement { 
	 
	public static final int port = 1337;
	final UnregulatedMotor ball_picker_left 	= new UnregulatedMotor(MotorPort.C); 
    final UnregulatedMotor ball_picker_right   	= new UnregulatedMotor(MotorPort.D);
	
	public void drive(Coordinate from, Coordinate to) {
		
		
	}
	
	public void rotateToOrientation(int degrees) {
		
		
	}
	
	public void pickUpBalls(boolean pickUp) {
		ball_picker_left.setPower(85);
		ball_picker_right.setPower(85); 
		
		if(pickUp) {
		    ball_picker_left.backward();
		    ball_picker_right.forward();
		} else {
			ball_picker_left.forward();
		    ball_picker_right.backward();
		}
	}
 
} 
