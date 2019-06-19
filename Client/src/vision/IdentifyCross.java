package vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class IdentifyCross {
	
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
	
	private int coords[][] = new int [4][2];
	private double circle[] = new double[3];
	

	public IdentifyCross(Mat picture, IdentifyCoordinates identify)	{
		coords = identify.getEdgesSqaure(picture);
		
		// Calculate center of Cross
		circle[0] = (double) ((coords[1][0] + coords[0][0]) / 2);
		circle[1] = (double) ((coords[1][1] + coords[2][1]) / 2);
		
		calcRadius();
		
	}

//	int get_coords(int x_y,int position)
//	{
//		return coords[x_y][position];
//	}

	public double[] getArray(){
		return circle;
	}

	public void draw(Mat picture, Scalar BoxColor)
	{
		circle(picture, new Point((int) circle[0], (int) circle[1]), (int) circle[2], BoxColor);
		
	}
	
	public void calcRadius() {
		// Find average distances to each edge
		double distLeft  = circle[0] - coords[0][0];
		double distRight = coords[1][0] - circle[0];
		double distUpper = circle[1] - coords[1][1];
		double distLower = coords[2][1] - circle[1];

		// Find average of each distance
		circle[2] = (double) ((distLeft + distRight + distUpper + distLower) / 4);
	}
}

