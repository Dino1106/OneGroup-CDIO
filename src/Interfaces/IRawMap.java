package Interfaces;

import java.util.List;

import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;
import org.bytedeco.opencv.opencv_imgproc.Vec4iVector;

public interface IRawMap {
	
	public List<Vec3fVector> getBalls();
	public List<Vec4iVector> getWalls();
	public int getRobotOrientation();
	public Vec3fVector getObstacle();
}
