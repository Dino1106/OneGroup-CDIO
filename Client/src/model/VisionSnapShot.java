package model;

import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;
import org.bytedeco.opencv.opencv_imgproc.Vec4iVector;

public class VisionSnapShot {
	
	private Vec3fVector balls;
	private int[][] walls;
	private int[][] cross;
	private int[][] robot;
	
	
	
	public VisionSnapShot(Vec3fVector balls, int[][] walls, int[][] cross, int[][] robot) {
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
	public int[][] getWalls() {
		return walls;
	}
	public void setWalls(int[][] walls) {
		this.walls = walls;
	}
	public int[][] getCross() {
		return cross;
	}
	public void setCross(int[][] cross) {
		this.cross = cross;
	}
	public int[][] getRobot() {
		return robot;
	}
	public void setRobot(int[][] robot) {
		this.robot = robot;
	}
	
	@Override
	public String toString() {
		return "VisionSnapShot [balls=" + balls + ", walls=" + walls + ", cross=" + cross + ", robot=" + robot + "]";
	}
	
}
