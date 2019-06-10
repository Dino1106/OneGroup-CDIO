import lejos.robotics.geometry.Point;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.*;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;

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
    
	public Point drive(Coordinate to) {
		// TODO: Maybe use coordinate from as method parameter instead of pose
		
		Pose pose = poseProvider.getPose();				// Gets the 'pose' of robot (position and heading)
		Point location = pose.getLocation();
		pose.setLocation(location);				    // Set from coordinate using pose provider
		
		navigator.goTo(new Waypoint(to.x, to.y));
		System.out.println("Robot location is now " + location);
		
		return location;
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
