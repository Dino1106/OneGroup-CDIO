package model;

import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;
import org.bytedeco.opencv.opencv_imgproc.Vec4iVector;

public class VisionSnapShot {
	
	private Vec3fVector balls;
	private Vec4iVector walls;
	private Vec4iVector cross;
	private Vec4iVector robot;
	
	public VisionSnapShot(Vec3fVector balls, Vec4iVector walls, Vec4iVector cross, Vec4iVector robot) {
		super();
		this.balls = balls;
		this.walls = walls;
		this.cross = cross;
		this.robot = robot;
	}

	public Vec3fVector getBalls() {
		return balls;
	}
	public void setBalls(Vec3fVector balls) {
		this.balls = balls;
	}
	public Vec4iVector getWalls() {
		return walls;
	}
	public void setWalls(Vec4iVector walls) {
		this.walls = walls;
	}
	public Vec4iVector getCross() {
		return cross;
	}
	public void setCross(Vec4iVector cross) {
		this.cross = cross;
	}
	public Vec4iVector getRobot() {
		return robot;
	}
	public void setRobot(Vec4iVector robot) {
		this.robot = robot;
	}
	
	@Override
	public String toString() {
		return "VisionSnapShot [balls=" + balls + ", walls=" + walls + ", cross=" + cross + ", robot=" + robot + "]";
	}
	
}
