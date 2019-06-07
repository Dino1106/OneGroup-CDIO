package server;

import lejos.robotics.geometry.Point;
import client.Coordinate;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.chassis.*;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;

public class RobotMovement { 
	 
	private UnregulatedMotor ball_picker_left, ball_picker_right;
	private Wheel left_wheel, right_wheel;
    private Chassis chassis;
    private MovePilot pilot;
    private PoseProvider poseProvider;
    private Navigator navigator;
	private double wheelDiameter;
    
    public RobotMovement() {
    	/* Setup ball-picker motors */
    	this.ball_picker_left = new UnregulatedMotor(MotorPort.C); 
        this.ball_picker_right = new UnregulatedMotor(MotorPort.D);
        
        /* Setup wheels and chassis for pilot */
    	this.wheelDiameter = 6.88;
    	this.left_wheel = WheeledChassis.modelWheel(Motor.A, wheelDiameter);
    	this.right_wheel = WheeledChassis.modelWheel(Motor.B, wheelDiameter);
    	this.chassis = new WheeledChassis(new Wheel[] {left_wheel, right_wheel}, WheeledChassis.TYPE_DIFFERENTIAL);
 
    	/* Setup navigator with pilot */
        this.pilot = new MovePilot(chassis); 
        this.navigator = new Navigator(pilot);
        this.poseProvider = new OdometryPoseProvider(pilot);
    }
    
	public void drive(Coordinate to) {
		// TODO: Maybe use coordinate from as method parameter instead of pose
		
		Pose pose = poseProvider.getPose();				// Gets the 'pose' of robot (position and heading)
		Point location = pose.getLocation();
		pose.setLocation(location);					    // Set from coordinate using pose provider
		
		navigator.goTo(new Waypoint(to.x, to.y));
		
	}
	
	public void rotateToOrientation(double degrees) {
		pilot.rotate(degrees);
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
