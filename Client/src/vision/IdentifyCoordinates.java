package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.medianBlur;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.core.CvType;

public class IdentifyCoordinates {


	public int[][] getEdgesTriangle(Mat picture) {
		// Dimensions of frame
		int frameWidth  = picture.cols();
		int frameHeight = picture.rows();

		int[][] coords = new int[4][2];

		Mat color_map = extractColor(picture, "blue");
		BytePointer p = color_map.data();
		
		

		d21:
			for( int y = 0; y <= frameHeight; y++ )
				for( int x = 0; x < frameWidth; x++ ){

					//System.out.print( p.get((y*color_map.arrayWidth())+x));
					if(p.get((y*color_map.arrayWidth())+x) == -1){

						checkRedundant(x, y, coords);

						break d21;
					}
				}
		d22:
			for( int y = 0; y >= frameHeight; y-- )
				for( int x = frameWidth; x < 0; x++ ){

					if(p.get((y*color_map.arrayWidth())+x) == -1){
						
						checkRedundant(x, y, coords);
						break d22;
					}
				}

				d23:
					for( int x = 0; x <= frameWidth; x++ )
						for( int y = 0; y <= frameHeight; y++ ){

							if(p.get((y*color_map.arrayWidth())+x) == -1){
						
								checkRedundant(x, y, coords);
								break d23;
							}
						}

				d24:
					for( int x = frameWidth; x >= 0; x-- )
						for( int y = 0; y <= frameHeight; y++ ){

							if(p.get((y*color_map.arrayWidth())+x) == -1){
								
								checkRedundant(x, y, coords);
								break d24;
							}
						}

						return coords;



	}

	public int[][] getEdgesSqaure(Mat picture){

		int[][] coords = new int[4][2];

		// Dimensions of frame
		int frameWidth  = picture.cols();
		int frameHeight = picture.rows();

		// Center of frame
		int[] center = { (int) frameWidth/2, (int) frameHeight/2 };

		// Coordinates of adjustment-box
		int[] upperLeftBoundary  = { center[0]-100, center[1]+100 };
		int[] upperRightBoundary = { center[0]+100, center[1]+100 };
		int[] lowerLeftBoundary  = { center[0]-100, center[1]-100 };
		int[] lowerRightBoundary = { center[0]+100, center[1]-100 };

		Mat color_map = extractColor(picture, "red");
		BytePointer p = color_map.data();


		d21:
			for( int y = lowerLeftBoundary[1]; y <= upperLeftBoundary[1]; y++ )
				for( int x = upperLeftBoundary[0]; x < upperRightBoundary[0]; x++ ){

					//System.out.print( p.get((y*color_map.arrayWidth())+x));
					if(p.get((y*color_map.arrayWidth())+x) == -1){

						coords[0][1] = y;
						coords[1][1] = y;

						break d21;
					}
				}
		d22:
			for( int y = upperLeftBoundary[1]; y >= lowerLeftBoundary[1]; y-- )
				for( int x = upperLeftBoundary[0]; x < upperRightBoundary[0]; x++ ){

					if(p.get((y*color_map.arrayWidth())+x) == -1){

						coords[3][1] = y;
						coords[2][1] = y;

						break d22;
					}
				}

				d23:
					for( int x = lowerLeftBoundary[0]; x <= lowerRightBoundary[0]; x++ )
						for( int y = lowerLeftBoundary[1]; y <= upperRightBoundary[1]; y++ ){

							if(p.get((y*color_map.arrayWidth())+x) == -1){

								coords[0][0] = x;
								coords[3][0] = x;

								break d23;
							}
						}

				d24:
					for( int x = lowerRightBoundary[0]; x >= lowerLeftBoundary[0]; x-- )
						for( int y = lowerLeftBoundary[1]; y <= upperRightBoundary[1]; y++ ){

							if(p.get((y*color_map.arrayWidth())+x) == -1){

								coords[1][0] = x;
								coords[2][0] = x;

								break d24;
							}
						}

						return coords;
	}

	public Mat extractColor(Mat picture, String color) {

		Mat to_out = new Mat();

		int h_min = 0, h_max = 255;
		int s_min = 0, s_max = 255;
		int v_min = 0, v_max = 255;


		// Transform the picture for a more precise calibration
		medianBlur(picture, to_out, 9);

		// Prepare for HSV color extraction
		cvtColor(to_out, to_out, COLOR_BGR2HSV);

		if(color.contentEquals("red")) {
			// Range of red color of cross
			h_min = 0; 		
			h_max = 10;
			s_min = 120;
			s_max = 255;
			v_min = 120;
			v_max = 255;

			/*
			// Range of red color in BGR
			int b_min = 0, 		b_max = 111;
			int g_min = 27, 	g_max = 136;
			int r_min = 151,	r_max = 255;
			 */

		} else if(color.contentEquals("blue")) {
			// Range of red color of cross
			h_min = 150; 		
			h_max = 180;
			s_min = 120;
			s_max = 255;
			v_min = 120;
			v_max = 255;
		}


		// Create Mat's based of the colors for the inRange function
		Mat min_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(h_min, s_min, v_min, 0));
		Mat max_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(h_max, s_max, v_max, 0));

		// Remove any other color than in the range of min and max
		opencv_core.inRange(to_out, min_Mat, max_Mat, to_out);

		return to_out;
	}

	public void checkRedundant(int x, int y, int[][] coords) {
		for(int i=0; i<coords.length; i++) {
			if(coords[i][0] == 0) {
				coords[i][0] = x;
				coords[i][1] = y;
			} else {
				if(x == coords[i][0] && y != coords[i][1]) {
					coords[i][0] = x;
					coords[i][1] = y;
				}

				if(x != coords[i][0] && y == coords[i][1]) {
					coords[i][0] = x;
					coords[i][1] = y;
				}
			}
		}
	}
}
