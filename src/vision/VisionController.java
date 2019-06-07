package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.HoughCircles;
import static org.bytedeco.opencv.global.opencv_imgproc.circle;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToOrgOpenCvCoreMat;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;
import org.bytedeco.opencv.opencv_imgproc.Vec4iVector;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.utils.Converters;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.*;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

/**
 * @author OneGroup
 *
 */



public class VisionController implements Runnable {
	
	private int imageHeight = 720;
	private int imageWidth = 1366;

	private static final int x_circle = 0;
	private static final int y_circle = 1;
	private static final int rad_circle = 2;
	private static final int xstart_line = 0;
	private static final int ystart_line = 1;
	private static final int xend_line = 2;
	private static final int yend_line = 3;
	
	

	private CanvasFrame vid_frame = new CanvasFrame("frame1");
	private CanvasFrame vid_edges = new CanvasFrame("edges");
	private CanvasFrame vid_color = new CanvasFrame("color");
	
	private Vec4iVector Line_set = new Vec4iVector();
	private Vec3fVector Circle_set = new Vec3fVector();
	
	private int Camera_id;
	private Mat picture_global = new Mat(), picture_plain = new Mat(), picture_color = new Mat();
	private Mat edges_global, color_global;
	private boolean vid;

	// Laptop camera constructor
	public VisionController() {
		Camera_id = 0;
		vid_frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		vid = true;
	}

	// USB plugged camera.
	// ID of the camera indicates which camera has to be used
	public VisionController(int camera) {
		Camera_id = camera;
		vid_frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		vid = true;
	}

	// Constructor with a static image
	public VisionController(String imgpath) {
		Camera_id = 0;
		vid_frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		vid = false;
		picture_global = imread(imgpath);
		picture_color = picture_global.clone();
		
	}


	// Code to execute in the class:
	@Override
	public void run() {
		try {
			// Controller Initializers
			FrameGrabber grabber = FrameGrabber.createDefault(Camera_id);
			OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
			grabber.setImageHeight(imageHeight);
			grabber.setImageWidth(imageWidth);

			if (vid) picture_global = converter.convert(grabber.grab());

			// Set Calibration values for Identify Balls
			int[] calib = {6, 5, 2, 6, 20};
			
			test(picture_color);
			vid_color.showImage(converter.convert(get_color()));
			
			

			// Generate layers
			extract_layer(picture_global);

			// 1 - Identify balls with given parameters and draw circles
			IdentifyBalls identifyBalls = new IdentifyBalls(picture_plain, 1, 3, 120, 15, 2, 8, calib);
			draw_circles(false, identifyBalls.get_circle());



			// Update window frame with current picture frame
			vid_frame.showImage(converter.convert(get_pic()));
	 		vid_edges.showImage(converter.convert(get_plain()));
	 		
			
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	// Creates lines between all circles
	public void create_nodes() {
		int i;
		int u;
		for (u = 0; u < get_Circles_amount(); u++)
			for (i = 0; i < get_Circles_amount(); i++)
				if (i != u)
					line(get_pic(), new Point(get_circle_xyr(u, x_circle), get_circle_xyr(u, y_circle)),
							new Point(get_circle_xyr(i, x_circle), get_circle_xyr(i, y_circle)), Scalar.MAGENTA);
	}


	// Takes Value layer and generates single-layered Mat
	private void extract_layer(Mat picture) {
		BytePointer dat;
		
		
		
		cvtColor(picture, picture, COLOR_BGR2HSV);
		dat = picture.data();
		
		for (int i = 0; i < (picture.arrayHeight() * picture.arrayWidth() * 3); i += 3) {
			dat = dat.put(0 + i, (byte) dat.get(i + 2));
			dat = dat.put(1 + i, (byte) dat.get(i + 2));
		}
		
	
		
		
		cvtColor(picture, picture, COLOR_BGR2GRAY);
		picture_plain = picture_global.clone();
		cvtColor(picture, picture, COLOR_GRAY2BGR);
		
		
	}
	
	
	public void test(Mat picture) {
		//cvtColor(picture, picture, COLOR_BGR2HSV);

		int h_min = 10, 	h_max = 11;
		int s_min = 55, 	s_max = 100;
		int v_min = 60,		v_max = 100;
		
		int b_min = 0, 		b_max = 111;
		int g_min = 27, 	g_max = 136;
		int r_min = 151,	r_max = 255;
		
		
		Mat min_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(b_min, g_min, r_min, 0));
		Mat max_Mat = new Mat(1, 1, CvType.CV_32SC4, new Scalar(b_max, g_max, r_max, 0));
		
		opencv_core.inRange(picture, min_Mat, max_Mat, picture_color);
		
		
	}
	
	/*
	private void extract_layer() {
		Mat picture = get_pic();
		BytePointer dat;

// TODO: THE METHOD NEEDS TO BE IMPLEMENTED IN IDENTIFY_CROSS.java
	private void extract_lines(double rho, double theta, int threshold, int minLineLength, int maxLineGap,
		Size filter_dim, int threshold1, int threshold2) {
		Vec4iVector lines = new Vec4iVector();
		Mat blurred = new Mat(), edges = new Mat();
		blur(get_plain(), blurred, filter_dim);
		Canny(blurred, edges, threshold1, threshold2);
		HoughLinesP(edges, lines, rho, theta, threshold, minLineLength, maxLineGap);
		Line_set = lines;
		draw_lines();
	}


	public synchronized int get_circle_xyr(int circle_number, int parameter) {
		return (int) Circle_set.get(circle_number).get(parameter);
	}

	public synchronized int get_line_xyxy(int line_number, int parameter) {
		return new IntPointer(Line_set.get(line_number)).get(parameter);
	}

	public synchronized int get_Circles_amount() {
		return (int) Circle_set.size();
	}

	public synchronized int get_Lines_amount() {
		return (int) Line_set.size();
	}

	public synchronized Mat get_pic() {
		return picture_global;
	}

	public synchronized Mat get_plain() {
		return picture_plain;
	}

	public synchronized Mat get_color() {
		return picture_color;
	}



	//private synchronized void toVec(Vec3fVector vec) {
	//	Circle_set = vec;
	//}



	private synchronized void draw_circles(Boolean centers, Vec3fVector ballCoords) {
		for (int i = 0; i < 7; i++) {
			circle(get_pic(), new Point((int) ballCoords.get(i).get(x_circle), (int) ballCoords.get(i).get(y_circle)), (int) ballCoords.get(i).get(rad_circle),
					Scalar.RED);
			if (centers) {
				line(get_pic(), new Point(get_circle_xyr(i, x_circle) - 3, get_circle_xyr(i, y_circle)),
						new Point(get_circle_xyr(i, x_circle) + 3, get_circle_xyr(i, y_circle)), Scalar.BLUE);
				line(get_pic(), new Point(get_circle_xyr(i, x_circle), get_circle_xyr(i, y_circle) - 3),
						new Point(get_circle_xyr(i, x_circle), get_circle_xyr(i, y_circle) + 3), Scalar.BLUE);
			}
		}
	}

	private void draw_lines() {
		for (int i = 0; i < get_Lines_amount(); i++) {
			line(get_pic(), new Point(get_line_xyxy(i, xstart_line), get_line_xyxy(i, ystart_line)),
					new Point(get_line_xyxy(i, xend_line), get_line_xyxy(i, yend_line)), Scalar.RED);
		}

		// System.out.println(new IntPointer(Line_set.get(0)).get(0));

	}






/*
// TODO: Do not delete comments below
	/*
	 * @Override public void run() { try {
	 * 
	 * FrameGrabber grabber = FrameGrabber.createDefault(Camera_id);
	 * OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat(); if
	 * (vid) { grabber.start();
	 * 
	 * while (vid) { update_image(converter.convert(grabber.grab())); //
	 * extract_circles(1,50,120,80,50,100); extract_layer();
	 * vid_frame.showImage(converter.convert(get_pic()));
	 * vid_edges.showImage(converter.convert(get_edges())); } } else {
	 * Generate_Objects(); // extract_circles(1,50,120,80,50,100);
	 * vid_frame.showImage(converter.convert(get_pic()));
	 * vid_edges.showImage(converter.convert(get_plain())); } } catch (Exception e)
	 * { e.printStackTrace(); } }
	 */
}
