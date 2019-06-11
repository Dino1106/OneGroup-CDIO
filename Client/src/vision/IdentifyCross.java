package vision;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.core.CvType;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class IdentifyCross {

	private Mat transformedPicture;
	private int coords[][] = new int [2][4];
	
	// Dimensions of given frame
	private int frameHeight, frameWidth;
	
	// Center of frame
	private int[] center = new int[2];
	
	int upperLeftBoundary[]  = new int[2];
	int upperRightBoundary[] = new int[2];
	int lowerLeftBoundary[]  = new int[2];
	int lowerRightBoundary[] = new int[2];

	public IdentifyCross(Mat picture)	{
		transformedPicture = picture; 
		frameHeight = transformedPicture.cols();
		frameWidth  = transformedPicture.rows();
		
		center[0] = (int) frameHeight/2;
		center[1] = (int) frameWidth/2;
		
		// Coordinates of adjustment-box
		upperLeftBoundary[0] = center[0]+100;
		upperLeftBoundary[1] = center[1]-100;
		
		upperRightBoundary[0] = center[0]+100;
		upperRightBoundary[1] = center[0]+100;
		
		lowerLeftBoundary[0] = center[0]-100;
		lowerLeftBoundary[1] = center[0]-100;
		
		lowerRightBoundary[0] = center[0]-100;
		lowerRightBoundary[1] = center[0]+100;
		
		
		coords = calculateEdges(transformedPicture);
	}

	public int[][] calculateEdges(Mat to_transform) {
		
		

		BytePointer p;

		Mat color_map = extractColor(to_transform);
		p = color_map.data();

		d21:
			for( int y = upperLeftBoundary[1]; y <= lowerLeftBoundary[1]; y++ )
				for( int x = upperLeftBoundary[0]; x < upperRightBoundary[0]; x++ ){

					//System.out.print( p.get((y*color_map.arrayWidth())+x));
					if(p.get((y*color_map.arrayWidth())+x) == -1){

						coords[1][0] = y;
						coords[1][1] = y;

						break d21;
					}}
		d22:
			for( int y = lowerLeftBoundary[1]; y >= upperLeftBoundary[1]; y-- )
				for( int x = upperLeftBoundary[0]; x < upperRightBoundary[0]; x++ ){

					if(p.get((y*color_map.arrayWidth())+x) == -1){

						coords[1][3] = y;
						coords[1][2] = y;

						break d22;
					}}

					d23:
						for( int x = lowerLeftBoundary[0]; x <= lowerRightBoundary[0]; x++ )
							for( int y = upperLeftBoundary[1]; y <= lowerRightBoundary[1]; y++ ){

								if(p.get((y*color_map.arrayWidth())+x) == -1){

									coords[0][0] = x;
									coords[0][3] = x;

									break d23;
								}}

					d24:
						for( int x = lowerRightBoundary[0]; x >= lowerLeftBoundary[0]; x-- )
							for( int y = upperLeftBoundary[1]; y <= lowerRightBoundary[1]; y++ ){

								if(p.get((y*color_map.arrayWidth())+x) == -1){

									coords[0][1] = x;
									coords[0][2] = x;

									break d24;
								}}

								return coords;
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

	public int[][] get_array(){
		return coords;
	}

	public void draw(Mat color_map, Scalar BoxColor)
	{
		line(color_map,new Point(coords[0][0],coords[1][0]),new Point(coords[0][1],coords[1][1]),BoxColor);
		line(color_map,new Point(coords[0][1],coords[1][1]),new Point(coords[0][2],coords[1][2]),BoxColor);
		line(color_map,new Point(coords[0][2],coords[1][2]),new Point(coords[0][3],coords[1][3]),BoxColor);
		line(color_map,new Point(coords[0][3],coords[1][3]),new Point(coords[0][0],coords[1][0]),BoxColor);
		
		line(color_map,new Point(center[0],center[1]),new Point(center[0]+100,center[1]+100),BoxColor);
	}

	public  void draw_render_space(Mat color_map, Scalar BoxColor)
	{
		line(color_map,new Point(upperLeftBoundary[0], upperLeftBoundary[1]),new Point(lowerLeftBoundary[0],lowerLeftBoundary[1]),Scalar.RED);
		line(color_map,new Point(lowerLeftBoundary[0], lowerLeftBoundary[1]),new Point(lowerRightBoundary[0],lowerRightBoundary[1]),Scalar.RED);
		line(color_map,new Point(lowerRightBoundary[0], lowerRightBoundary[1]),new Point(upperRightBoundary[0], upperRightBoundary[1]),Scalar.RED);
		line(color_map,new Point(upperRightBoundary[0], upperRightBoundary[1]),new Point(upperLeftBoundary[0], upperLeftBoundary[1]),Scalar.RED);
	}

	public Mat extractColor(Mat picture) {

		Mat to_out = new Mat();

		// Transform the picture for a more precise calibration
		medianBlur(picture, to_out, 9);

		// Prepare for HSV color extraction
		cvtColor(to_out, to_out, COLOR_BGR2HSV);

		// Range of red color of cross
		int h_min = 0, 		h_max = 10;
		int s_min = 120, 	s_max = 255;
		int v_min = 120,	v_max = 255;

		// Range of red color in BGR
		int b_min = 0, 		b_max = 111;
		int g_min = 27, 	g_max = 136;
		int r_min = 151,	r_max = 255;

		// Create Mat's based of the colors for the inRange function
		Mat min_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(h_min, s_min, v_min, 0));
		Mat max_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(h_max, s_max, v_max, 0));

		// Remove any other color than in the range of min and max
		opencv_core.inRange(to_out, min_Mat, max_Mat, to_out);

		// In range 
		//cvtColor(to_out, to_out, COLOR_GRAY2BGR);

		return to_out;

	}

}

