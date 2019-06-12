package vision;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.core.CvType;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class IdentifyCross {

	IdentifyCoordinates identify = new IdentifyCoordinates();
	
	private int coords[][] = new int [4][2];

	public IdentifyCross(Mat picture)	{
		coords = identify.getEdgesSqaure(picture);
	}

	/**
	 *
	 * Returns xy Coordinates of a square around the cross
	 *
	 * x = 0 , y = 1
	 *  position = 0 <--- high-left corner
	 *  position = 1 <--- high-right corner
	 *  position = 2 <--- low-right corner
	 *  position = 3 <--- low-left corner
	 *
	 */
	int get_coords(int x_y,int position)
	{
		return coords[x_y][position];
	}

	public int[][] getArray(){
		return coords;
	}

	public void draw(Mat color_map, Scalar BoxColor)
	{
		line(color_map,new Point(coords[0][0],coords[0][1]),new Point(coords[1][0],coords[1][1]),BoxColor);
		line(color_map,new Point(coords[1][0],coords[1][1]),new Point(coords[2][0],coords[2][1]),BoxColor);
		line(color_map,new Point(coords[2][0],coords[2][1]),new Point(coords[3][0],coords[3][1]),BoxColor);
		line(color_map,new Point(coords[3][0],coords[3][1]),new Point(coords[0][0],coords[0][1]),BoxColor);
		
	}
	
	
	/*
	public  void draw_render_space(Mat color_map, Scalar BoxColor)
	{
		line(color_map,new Point(upperLeftBoundary[0], upperLeftBoundary[1]),new Point(lowerLeftBoundary[0],lowerLeftBoundary[1]),Scalar.RED);
		line(color_map,new Point(lowerLeftBoundary[0], lowerLeftBoundary[1]),new Point(lowerRightBoundary[0],lowerRightBoundary[1]),Scalar.RED);
		line(color_map,new Point(lowerRightBoundary[0], lowerRightBoundary[1]),new Point(upperRightBoundary[0], upperRightBoundary[1]),Scalar.RED);
		line(color_map,new Point(upperRightBoundary[0], upperRightBoundary[1]),new Point(upperLeftBoundary[0], upperLeftBoundary[1]),Scalar.RED);
	}
	*/

}

