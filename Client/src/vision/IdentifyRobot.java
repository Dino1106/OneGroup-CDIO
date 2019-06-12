package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.line;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

public class IdentifyRobot {
	
	IdentifyCoordinates identify = new IdentifyCoordinates();
	private int coords[][] = new int [3][2];

	public IdentifyRobot(Mat picture)	{
		coords = identify.getEdgesTriangle(picture);
	}
	
	int get_coords(int x_y,int position)
	{
		return coords[x_y][position];
	}
	
	public int[][] get_array(){
		return coords;
	}
	
	public void draw(Mat color_map, Scalar BoxColor)
	{
		line(color_map,new Point(coords[0][0],coords[0][1]),new Point(coords[1][0],coords[1][1]),BoxColor);
		line(color_map,new Point(coords[1][0],coords[1][1]),new Point(coords[2][0],coords[2][1]),BoxColor);
		line(color_map,new Point(coords[2][0],coords[2][1]),new Point(coords[0][0],coords[0][1]),BoxColor);
	}
}
