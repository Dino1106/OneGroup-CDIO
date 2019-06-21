package robot;

import java.io.File;

import constants.ServerConstants;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;
import model.Coordinate;

@SuppressWarnings("deprecation")
public class RobotMovement { 
	
	private UnregulatedMotor ballPickerLeft, ballPickerRight;
	private RegulatedMotor leftWheel, rightWheel;
    private DifferentialPilot pilot;
    private PoseProvider poseProvider;
    private Navigator navigator;
    
    public RobotMovement() {
    	/* Setup ball-picker motors */
    	this.ballPickerLeft = new UnregulatedMotor(MotorPort.C); 
        this.ballPickerRight = new UnregulatedMotor(MotorPort.D);
        
        /* Setup wheels */
    	this.leftWheel = new EV3LargeRegulatedMotor(MotorPort.B);
    	this.rightWheel = new EV3LargeRegulatedMotor(MotorPort.A);
 
    	/* Setup navigator with pilot */
        this.pilot = new DifferentialPilot(ServerConstants.wheelDiameter, ServerConstants.trackWidth, leftWheel, rightWheel);
        this.poseProvider = new OdometryPoseProvider(pilot);
        this.navigator = new Navigator(pilot, poseProvider);
    }
    
	public boolean drive(Coordinate to, int speed) {
		navigator.getMoveController().setLinearSpeed(speed);
		float x = (float) to.x;
		float y = (float) to.y;
		System.out.println("I am going to x = " + x + ", y = " + y);
		navigator.goTo( x, y);
						
		if(navigator.waitForStop()) {
			System.out.println("I have stopped");
			return true;
		} else {
			System.out.println("I am still driving");
			return false;
		}
	}
	
	public boolean travel(double centimeters, int speed) {
		System.out.println("Driving " + centimeters +" centimeters with " + speed + " speed");
		navigator.getMoveController().setLinearSpeed(speed);
		navigator.getMoveController().travel(centimeters);
		
		if(navigator.waitForStop()) {
			System.out.println("I have stopped travelling");
			return true;
		} else {
			System.out.println("I am still travelling");
			return false;
		}
	}
	
	public void rotate(double degrees) {
		System.out.println("I am rotating " + degrees + " degrees");
		pilot.rotate(degrees);
	}
	
	public boolean pickUpBalls(boolean pickUp) {
		if(pickUp) {
			System.out.println("Picking up balls");
			ballPickerLeft.setPower(100);
			ballPickerRight.setPower(100);
		    ballPickerLeft.backward();
		    ballPickerRight.forward();
		    return true;
		} else {
			System.out.println("Spitting out balls");
			ballPickerLeft.setPower(100);
			ballPickerRight.setPower(100); 
			ballPickerLeft.forward();
		    ballPickerRight.backward();
		    return false;
		}
	}
	
	public void setRobotLocation(Coordinate coordinate, double heading) {
		float x = (float) coordinate.x;
		float y = (float) coordinate.y;
		navigator.getPoseProvider().setPose(new Pose(x, y, (float) heading));
		System.out.println("Navigator location: x = " + navigator.getPoseProvider().getPose().getLocation().x + ", y = " + navigator.getPoseProvider().getPose().getLocation().y + ", with heading = " + navigator.getPoseProvider().getPose().getHeading());
	}
	
	public void playSound(int soundToPlay) {
		File sound = null;
		
		switch (soundToPlay) {
		case 1:
			sound = new File("pickUpSound.wav");
			Sound.playSample(sound);
			break;
		case 2:
			sound = new File("victorySound.wav");
			Sound.playSample(sound);
			break;
		case 3:
			Sound.playTone(800, 400);
			break;
		case 4:
			Sound.playTone(2000, 400);
			break;
		case 5:
			Sound.playTone(400, 400);
			break;
		default:
			System.out.println("No sound detected.");
			break;
		}
		
	}
	
	public void stopAllMotors() {
		navigator.getMoveController().stop();
		ballPickerLeft.stop();
		ballPickerRight.stop();
	}
 
} 
