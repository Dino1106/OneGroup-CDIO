package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.opencv.global.opencv_imgproc.HoughCircles;
import static org.bytedeco.opencv.global.opencv_imgproc.circle;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.line;
import static org.bytedeco.opencv.global.opencv_imgproc.medianBlur;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;
import org.opencv.core.CvType;

public class IdentifyCoordinates {
	private BytePointer colorP;

	public int[][] getWallCorners(Mat picture)
	{
		int frameWidth  = picture.cols();
		int frameHeight = picture.rows();
		int center[] = {frameWidth/2,frameHeight/2};
		int col = frameWidth-1;
		int row = frameHeight-1;
		int[][] coords = new int[8][2];

		colorP = picture.data();
		int[] lu={0,0};
		int[] u={center[0],0};
		int[] ru={col,0};
		int[] lc={0,center[1]};
		int [] c=center;
		int [] rc={col,center[1]};
		int [] ld={0,row};
		int [] d={center[0],row};
		int [] rd={col,row};

		coords[0] = findPixel(u,lu[0],c[1],false,picture.arrayWidth());
		coords[1] = findPixel(lc,c[0],lu[1],true,picture.arrayWidth());
		coords[2] = findPixel(d,ld[0],c[1],false,picture.arrayWidth());
		coords[3] = findPixel(lc,c[0],ld[1],true,picture.arrayWidth());
		coords[4] = findPixel(d,rd[0],c[1],false,picture.arrayWidth());
		coords[5] = findPixel(rc,c[0],rd[1],true,picture.arrayWidth());
		coords[6] = findPixel(rc,c[0],ru[1],true,picture.arrayWidth());
		coords[7] = findPixel(u,ru[0],c[1],false,picture.arrayWidth());

		return coords;
	}

	private int[] findPixel(int[] start, int stop_x, int stop_y,boolean flip, int PictureWidth){
		int[] to_out = new int[2];
		int temp;
		if(flip){
			temp = stop_x; stop_x = stop_y; stop_y = temp;temp = start[0]; start[0] = start[1]; start[1] = temp;
		}

		if(start[1] < stop_y){
			for(int i = start[1]; i <= stop_y;i++ ) {
				if (start[0] < stop_x) {
					for (int u = start[0]; u <= stop_x; u++) {
						if(!flip){
							if(colorP.get((i*PictureWidth)+u) == -1){
								to_out[0] = u;
								to_out[1] = i;
								if(flip) {temp = start[0]; start[0] = start[1]; start[1] = temp;}
								return to_out;
							}
						}else{
							if(colorP.get((u*PictureWidth)+i) == -1){
								to_out[0] = i;
								to_out[1] = u;
								if(flip) {temp = start[0]; start[0] = start[1]; start[1] = temp;}
								return to_out;
							}
						}
					}
				} else {

					for (int u = start[0]; u >= stop_x; u--) {

						if(!flip){
							if(colorP.get((i*PictureWidth)+u) == -1){
								to_out[0] = u;
								to_out[1] = i;
								if(flip) {temp = start[0]; start[0] = start[1]; start[1] = temp;}
								return to_out;
							}
						}else{
							if(colorP.get((u*PictureWidth)+i) == -1){
								to_out[0] = i;
								to_out[1] = u;
								if(flip) {temp = start[0]; start[0] = start[1]; start[1] = temp;}
								return to_out;
							}
						}


					}
				}
			}
		}

		else{
			for(int i = start[1]; i >= stop_y;i-- ){
				if(start[0] < stop_x){
					for(int u= start[0]; u <= stop_x;u++){
						if(!flip){
							if(colorP.get((i*PictureWidth)+u) == -1){
								to_out[0] = u;
								to_out[1] = i;
								if(flip) {temp = start[0]; start[0] = start[1]; start[1] = temp;}
								return to_out;
							}
						}else{
							if(colorP.get((u*PictureWidth)+i) == -1){
								to_out[0] = i;
								to_out[1] = u;
								if(flip) {temp = start[0]; start[0] = start[1]; start[1] = temp;}
								return to_out;
							}
						}
					}
				}else{
					for(int u= start[0]; u >= stop_x;u--){
						if(!flip){
							if(colorP.get((i*PictureWidth)+u) == -1){
								to_out[0] = u;
								to_out[1] = i;
								if(flip) {temp = start[0]; start[0] = start[1]; start[1] = temp;}
								return to_out;
							}
						}else{
							if(colorP.get((u*PictureWidth)+i) == -1){
								to_out[0] = i;
								to_out[1] = u;
								if(flip) {temp = start[0]; start[0] = start[1]; start[1] = temp;}
								return to_out;
							}
						}
					}
				}
			}
		}


		return to_out;
	}

	public int[][] getCirleCoordinates(Mat picture) {

		int[][] coords = new int[2][2];

		extractColor(picture, "purple");

		//BytePointer p = extractedMat.data();

		Vec3fVector circles = new Vec3fVector();
		findCircles(picture, circles);
		

		// Draw circles
		for(int i=0; i<circles.size(); i++) {
			circle(picture, new Point((int) circles.get(i).get(0), (int) circles.get(i).get(1)), (int) circles.get(i).get(2), Scalar.GREEN);
		}
		

		// Determine small/large circle
		if((int) circles.get(0).get(2) <= (int) circles.get(1).get(2)) {
			// Small circle
			coords[0][0] = (int) circles.get(0).get(0);
			coords[0][1] = (int) circles.get(0).get(1);

			// Large circle
			coords[1][0] = (int) circles.get(1).get(0);
			coords[1][1] = (int) circles.get(1).get(1);
		
		} else {
			// Small circle
			coords[0][0] = (int) circles.get(1).get(0);
			coords[0][1] = (int) circles.get(1).get(1);

			// Large circle
			coords[1][0] = (int) circles.get(0).get(0);
			coords[1][1] = (int) circles.get(0).get(1);

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

		extractColor(picture, "red");
		BytePointer p = picture.data();


		d21:
			for( int y = lowerLeftBoundary[1]; y <= upperLeftBoundary[1]; y++ )
				for( int x = upperLeftBoundary[0]; x < upperRightBoundary[0]; x++ ){

					//System.out.print( p.get((y*color_map.arrayWidth())+x));
					if(p.get((y * picture.arrayWidth())+x) == -1){

						coords[0][1] = y;
						coords[1][1] = y;

						break d21;
					}
				}
		d22:
			for( int y = upperLeftBoundary[1]; y >= lowerLeftBoundary[1]; y-- )
				for( int x = upperLeftBoundary[0]; x < upperRightBoundary[0]; x++ ){

					if(p.get((y * picture.arrayWidth())+x) == -1){

						coords[3][1] = y;
						coords[2][1] = y;

						break d22;
					}
				}

				d23:
					for( int x = lowerLeftBoundary[0]; x <= lowerRightBoundary[0]; x++ )
						for( int y = lowerLeftBoundary[1]; y <= upperRightBoundary[1]; y++ ){

							if(p.get((y * picture.arrayWidth())+x) == -1){

								coords[0][0] = x;
								coords[3][0] = x;

								break d23;
							}
						}

				d24:
					for( int x = lowerRightBoundary[0]; x >= lowerLeftBoundary[0]; x-- )
						for( int y = lowerLeftBoundary[1]; y <= upperRightBoundary[1]; y++ ){

							if(p.get((y * picture.arrayWidth())+x) == -1){

								coords[1][0] = x;
								coords[2][0] = x;

								break d24;
							}
						}

						return coords;
	}

	public void extractColor(Mat picture, String color) {

		//Mat to_out = new Mat();

		int h_min = 0, h_max = 255;
		int s_min = 0, s_max = 255;
		int v_min = 0, v_max = 255;
		
		int b_min = 0, 	b_max = 255;
		int g_min = 0, 	g_max = 255;
		int r_min = 0,	r_max = 255;


		// Transform the picture for a more precise calibration
		medianBlur(picture, picture, 9);

		// Prepare for HSV color extraction
		cvtColor(picture, picture, COLOR_BGR2HSV);

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

		} else if(color.contentEquals("purple")) {
			// Range of red color of cross
			
			
			//Purple paper is: 100/100/100
			
			h_min = 260/2;
			h_max = 340/2;
			s_min = 255/100 * 20;
			s_max = 255/100 * 100;
			v_min = 255/100 * 20;
			v_max = 255/100 * 100;
		}

		// Create Mat's based of the colors for the inRange function
		Mat min_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(h_min, s_min, v_min, 0));
		Mat max_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(h_max, s_max, v_max, 0));

		// Remove any other color than in the range of min and max
		opencv_core.inRange(picture, min_Mat, max_Mat, picture);
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

	public void findCircles(Mat picture, Vec3fVector circles) {
		HoughCircles(picture, circles, CV_HOUGH_GRADIENT, 1, 20, 50, 10, 15, 100);
	}

}
