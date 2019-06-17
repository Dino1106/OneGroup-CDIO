
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
    	this.wheelDiameter = 5.05;
    	this.trackWidth = 17.8;
    	this.leftWheel = new EV3LargeRegulatedMotor(MotorPort.A);
    	this.rightWheel = new EV3LargeRegulatedMotor(MotorPort.B);
 
    	/* Setup navigator with pilot */
        this.pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftWheel, rightWheel);
        this.poseProvider = new OdometryPoseProvider(pilot);
        this.navigator = new Navigator(pilot, poseProvider);
    }
    
	public boolean drive(Coordinate to, int speed) {
		pilot.setLinearSpeed(speed);
		float x = (float) to.x;
		float y = (float) to.y;
		System.out.println("I am going to x = " + x + ", y = " + y);
		navigator.goTo(x, y);
		
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
	
	public void setRobotLocation(Coordinate coordinate, double heading) {		
		float x = (float) coordinate.x;
		float y = (float) coordinate.y;
		poseProvider.setPose(new Pose(x, y, (float)heading));
		System.out.println("Navigator location: x = " + navigator.getPoseProvider().getPose().getLocation().x + ", y = " + navigator.getPoseProvider().getPose().getLocation().y + ", with heading = " + navigator.getPoseProvider().getPose().getHeading());
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
