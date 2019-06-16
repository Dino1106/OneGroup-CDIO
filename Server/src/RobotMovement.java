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

@SuppressWarnings("deprecation")
public class RobotMovement { 
	 
	private UnregulatedMotor ballPickerLeft, ballPickerRight;
	private RegulatedMotor leftWheel, rightWheel;
    private DifferentialPilot pilot;
    private PoseProvider poseProvider;
    private Navigator navigator;
	private double wheelDiameter, trackWidth;
	private Pose pose;
    
    public RobotMovement() {
    	/* Setup ball-picker motors */
    	this.ballPickerLeft = new UnregulatedMotor(MotorPort.C); 
        this.ballPickerRight = new UnregulatedMotor(MotorPort.D);
        
        /* Setup wheels */
    	this.wheelDiameter = 5.05;
    	this.trackWidth = 17.5;
    	this.leftWheel = new EV3LargeRegulatedMotor(MotorPort.A);
    	this.rightWheel = new EV3LargeRegulatedMotor(MotorPort.B);
 
    	/* Setup navigator with pilot */
        this.pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftWheel, rightWheel);
        this.navigator = new Navigator(pilot);
        this.poseProvider = new OdometryPoseProvider(pilot);
		this.pose = poseProvider.getPose();  // Gets the 'pose' of robot (position and heading)
    }
    
	public boolean drive(Coordinate to, int speed) {
		Point location = pose.getLocation();
		pose.setLocation(location);				   	 	// Set from coordinate using pose provider

		System.out.println("I think my location is: x = " + location.x + ", y = " + location.y);
		System.out.println("I am going to x = " + to.x + ", y = " + to.y);
		pilot.setLinearSpeed(speed);
		navigator.goTo(new Waypoint(to.x, to.y));
				
		if(navigator.waitForStop()) {
			System.out.println("I have stopped");
			return true;
		} else {
			System.out.println("I am still driving");
			return false;
		}
	}
	
	public void travel(int centimeters, int speed) {
		System.out.println("Driving " + centimeters +" centimeters with " + speed + " speed");
		setMotorSpeed(speed);
		pilot.travel(centimeters);
	}
	
	
	public void setMotorSpeed(int speed) {
		pilot.setLinearSpeed(speed);
	}
	
	public void setPickUpSpeed(int speed) {
		ballPickerLeft.setPower(speed);
		ballPickerRight.setPower(speed); 
	}
	
	public void rotateToOrientation(double degrees) {
		System.out.println("I am rotating " + degrees + " degrees");
		pilot.rotate(degrees);
	}
	
	public boolean pickUpBalls(boolean pickUp) {
		if(pickUp) {
			System.out.println("Picking up balls");
			ballPickerLeft.setPower(85);
			ballPickerRight.setPower(85);
		    ballPickerLeft.backward();
		    ballPickerRight.forward();
		    return true;
		} else {
			System.out.println("Spitting out balls");
			ballPickerLeft.setPower(85);
			ballPickerRight.setPower(85); 
			ballPickerLeft.forward();
		    ballPickerRight.backward();
		    return false;
		}
	}
	
	public void setRobotLocation(Coordinate coordinate) {
		Point point = new Point(coordinate.x, coordinate.y);
		pose.setLocation(point);
		System.out.println("My new location is x = " + coordinate.x + ", y = " + coordinate.y);
	}
	
	public Coordinate getRobotLocation() {
		Point roboPoint = pose.getLocation();
		return new Coordinate((int) (roboPoint.x), (int) (roboPoint.y));
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
