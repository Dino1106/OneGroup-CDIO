package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.line;

import java.awt.Color;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

public class IdentifyRobot {
	
	IdentifyCoordinates identify = new IdentifyCoordinates();
	private int coords[][] = new int [2][2];

	public IdentifyRobot(Mat picture)	{
		//coords = identify.getCirleCoordinates(picture);
	}
	
	int get_coords(int x_y,int position)
	{
		return coords[x_y][position];
	}
	
	public int[][] getArray(){
		return coords;
	}
	
	public void draw(Mat picture, Scalar BoxColor)
	{
		line(picture,new Point(coords[0][0],coords[0][1]),new Point(coords[1][0],coords[1][1]),BoxColor);
		line(picture,new Point(coords[1][0],coords[1][1]),new Point(coords[0][0],coords[0][1]),BoxColor);
	}
}
