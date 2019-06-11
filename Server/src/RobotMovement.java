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
    
    public RobotMovement() {
    	/* Setup ball-picker motors */
    	this.ballPickerLeft = new UnregulatedMotor(MotorPort.C); 
        this.ballPickerRight = new UnregulatedMotor(MotorPort.D);
        
        /* Setup wheels */
    	this.wheelDiameter = 6.88;
    	this.trackWidth = 22.8;
    	this.leftWheel = new EV3LargeRegulatedMotor(MotorPort.A);
    	this.rightWheel = new EV3LargeRegulatedMotor(MotorPort.B);
 
    	/* Setup navigator with pilot */
        this.pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftWheel, rightWheel);
        this.navigator = new Navigator(pilot);
        this.poseProvider = new OdometryPoseProvider(pilot);
    }
    
	public boolean drive(Coordinate to) {
		
		Pose pose = poseProvider.getPose();				// Gets the 'pose' of robot (position and heading)
		Point location = pose.getLocation();
		pose.setLocation(location);				    // Set from coordinate using pose provider

		navigator.goTo(new Waypoint(to.x, to.y));
				
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
		ballPickerLeft.setPower(speed);
		ballPickerRight.setPower(speed); 
	}
	
	public void rotateToOrientation(double degrees) {
		pilot.rotate(degrees);
	}
	
	public boolean pickUpBalls(boolean pickUp) {
		ballPickerLeft.setPower(85);
		ballPickerRight.setPower(85); 
		
		if(pickUp) {
		    ballPickerLeft.backward();
		    ballPickerRight.forward();
		    return true;
		} else {
			ballPickerLeft.setPower(85);
			ballPickerRight.setPower(85); 
			ballPickerLeft.forward();
		    ballPickerRight.backward();
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
