import lejos.robotics.geometry.Point;

import java.io.File;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.Path;

@SuppressWarnings("deprecation")
public class RobotMovement { 
	 
	private UnregulatedMotor ball_picker_left, ball_picker_right;
	private RegulatedMotor left_wheel, right_wheel;
    private DifferentialPilot pilot;
    private PoseProvider poseProvider;
    private Navigator navigator;
	private double wheelDiameter, trackWidth;
    
    public RobotMovement() {
    	/* Setup ball-picker motors */
    	this.ball_picker_left = new UnregulatedMotor(MotorPort.C); 
        this.ball_picker_right = new UnregulatedMotor(MotorPort.D);
        
        /* Setup wheels */
    	this.wheelDiameter = 6.88;
    	this.trackWidth = 19.5;
    	this.left_wheel = new EV3LargeRegulatedMotor(MotorPort.A);
    	this.right_wheel = new EV3LargeRegulatedMotor(MotorPort.B);
 
    	/* Setup navigator with pilot */
        this.pilot = new DifferentialPilot(wheelDiameter, trackWidth, left_wheel, right_wheel);
        this.navigator = new Navigator(pilot);
        this.poseProvider = new OdometryPoseProvider(pilot);
    }
    
	public boolean drive(Coordinate to) {
		
		Pose pose = poseProvider.getPose();				// Gets the 'pose' of robot (position and heading)
		Point location = pose.getLocation();
		pose.setLocation(location);				    // Set from coordinate using pose provider

		
		Path path = new Path();
		path.add(new Waypoint(pose.getLocation().getX(), pose.getLocation().getY()));
		path.add(new Waypoint(to.x, to.y));		
		
		navigator.setPath(path);
		navigator.followPath();
				
		if(navigator.waitForStop()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setMotorSpeed(int speed) {
		pilot.setLinearSpeed(speed);
	}
	
	public void setPickUpSpeed(int speed) {
		ball_picker_left.setPower(speed);
		ball_picker_right.setPower(speed); 
	}
	
	public void rotateToOrientation(double degrees) {
		pilot.rotate(degrees);
	}
	
	public boolean pickUpBalls(boolean pickUp) {
		ball_picker_left.setPower(85);
		ball_picker_right.setPower(85); 
		
		if(pickUp) {
		    ball_picker_left.backward();
		    ball_picker_right.forward();
		    return true;
		} else {
			ball_picker_left.setPower(85);
			ball_picker_right.setPower(85); 
			ball_picker_left.forward();
		    ball_picker_right.backward();
		    return false;
		}
	}
	
	public void playSound(int soundToPlay) {
		File sound = null;
		
		switch (soundToPlay) {
		case 1:
			sound = new File("pickUpSound.wav");
			break;
		case 2:
			sound = new File("victorySound.wav");
			break;
		default:
			break;
		}
		
		if(sound != null) {
			Sound.playSample(sound);
		} else {
			System.out.println("No sound detected..");
		}
		
	}
 
} 
