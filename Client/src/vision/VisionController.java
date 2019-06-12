package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.circle;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

import java.util.ArrayList;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;
import org.bytedeco.opencv.opencv_imgproc.Vec4iVector;

import model.Ball;
import model.Cross;
import model.Goal;
import model.MapState;
import model.RobotLocation;
import model.VisionSnapShot;
import model.Wall;

import static org.bytedeco.opencv.global.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_core.*;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class VisionController implements Runnable {

	private boolean testMode = false;

	private int imageHeight = 720;
	private int imageWidth = 1366;


	private static final int xStartLine = 0;
	private static final int yStartLine = 1;
	private static final int xEndLine = 2;
	private static final int yEndLine = 3;

	private Vec3fVector balls;
	private int[][] walls;
	private int[][] cross;
	private int[][] robot;
	
	private Vec4iVector lineSet = new Vec4iVector();


	private CanvasFrame vidFrame;
	private CanvasFrame vidFrameBlue;

	private Mat pictureGlobal = new Mat();
	private Mat picturePlain = new Mat(); 
	private Mat pictureColor = new Mat();
	private Mat pictureRobot = new Mat();

	private int cameraId;
	private boolean vid = false;

	public VisionController(boolean testMode) {
		this.cameraId = 0;
		this.vid = true;
		this.testMode = testMode;

		if(testMode) {
			this.vidFrame = new CanvasFrame("frame1");
			this.vidFrameBlue = new CanvasFrame("blue");
			this.vidFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			this.vidFrameBlue.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		}
	}

	// USB plugged camera.
	// ID of the camera indicates which camera has to be used
	public VisionController(boolean testMode, int camera) {
		this.cameraId = camera;
		this.vid = true;
		this.testMode = testMode;
		
		if(testMode) {
			this.vidFrame = new CanvasFrame("frame1");
			this.vidFrameBlue = new CanvasFrame("blue");
			this.vidFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			this.vidFrameBlue.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		}
	}

	// Constructor with a static image
	public VisionController(boolean testMode, String imgpath) {
		this.cameraId = 0;
		this.vid = false;
		this.pictureGlobal = imread(imgpath);

		if(testMode) {
			this.vidFrame = new CanvasFrame("frame1");
			this.vidFrameBlue = new CanvasFrame("blue");
			this.vidFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			this.vidFrameBlue.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		}
	}


	// Code to execute in the class:
	@Override
	public void run() {
		try {
			// Controller Initializers
			FrameGrabber grabber = FrameGrabber.createDefault(cameraId);
			OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
			grabber.setImageHeight(imageHeight);
			grabber.setImageWidth(imageWidth);
			//grabber.setFrameRate(5.0);

			if (vid) { 
				grabber.start(); 
			}

			do {

				// Save the frame as a Mat
				if (vid) pictureGlobal = converter.convert(grabber.grab());

				// Clone the "global" picture
				pictureColor = pictureGlobal.clone();
				picturePlain = pictureGlobal.clone();
				pictureRobot = pictureGlobal.clone();

				extractLayer(pictureGlobal);

				// Set Calibration values for Identify Balls 
				int[] calib = {6, 5, 2, 6, 20}; 

				// 1 - Identify balls with given parameters and draw circles
				IdentifyBalls identifyBalls = new IdentifyBalls(picturePlain.clone(), 1, 3, 120, 15, 2, 8, calib);
				this.balls = identifyBalls.getCircles();


				// 2 - Identify cross with constant parameters
				IdentifyCross identifyCross = new IdentifyCross(pictureColor.clone());
				this.cross = identifyCross.get_array();

				// 3 - Identify Walls by cross
				IdentifyWalls identifyWalls = new IdentifyWalls(identifyCross.get_array());
				this.walls = identifyWalls.getArray();

				// 4 - Identify robot				
				IdentifyRobot identifyRobot = new IdentifyRobot(pictureRobot.clone());
				this.robot = identifyRobot.get_array();
				
				if (testMode) {
					identifyBalls.draw(pictureColor,Scalar.CYAN,true);
					identifyCross.draw(pictureColor, Scalar.BLUE);
					identifyWalls.draw(pictureColor,Scalar.RED);
					line(pictureColor, new Point(0,0), new Point(identifyWalls.centerCross[0],identifyWalls.centerCross[1]),Scalar.RED);
					line(pictureColor, new Point(0,0), new Point(identifyWalls.centerCross[0],identifyWalls.centerCross[1]),Scalar.RED);
					identifyRobot.draw(pictureRobot, Scalar.BLUE);
					vidFrame.showImage(converter.convert(pictureColor));
					vidFrameBlue.showImage(converter.convert(pictureRobot));
				}
				
				// Update window frame with current picture frame




			}while(vid);} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public VisionSnapShot getSnapShot() {
		return new VisionSnapShot(this.balls, this.walls, this.cross, this.robot);
	}

	// Creates lines between all circles



	// Takes Value layer and generates single-layered Mat
	private void extractLayer(Mat picture) {
		BytePointer dat;

		cvtColor(picture, picture, COLOR_BGR2HSV);
		dat = picture.data();

		for (int i = 0; i < (picture.arrayHeight() * picture.arrayWidth() * 3); i += 3) {
			dat = dat.put(0 + i, (byte) dat.get(i + 2));
			dat = dat.put(1 + i, (byte) dat.get(i + 2));
		}

		cvtColor(picture, picture, COLOR_BGR2GRAY);
		picturePlain = picture.clone();
		cvtColor(picture, picture, COLOR_GRAY2BGR);
	}

	// TODO: EVERYTHING BELOW IS IRRELEVANT TO THIS CLASS
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: THE METHOD NEEDS TO BE IMPLEMENTED IN LINE DETECTION
	private void extractLines(double rho, 
			double theta,
			int threshold,
			int minLineLength,
			int maxLineGap,
			Size filterDim,
			int threshold1,
			int threshold2) {
		Vec4iVector lines = new Vec4iVector();
		Mat blurred = new Mat(), edges = new Mat();
		blur(getPlain(), blurred, filterDim);
		Canny(blurred, edges, threshold1, threshold2);
		HoughLinesP(edges, lines, rho, theta, threshold, minLineLength, maxLineGap);
		lineSet = lines;
		drawLines();
	}



	public synchronized int getLineXyxy(int line_number, int parameter) {
		return new IntPointer(lineSet.get(line_number)).get(parameter);
	}


	public synchronized int getLinesAmount() {
		return (int) lineSet.size();
	}

	public synchronized Mat getPic() {
		return pictureGlobal;
	}

	public synchronized Mat getPlain() {
		return picturePlain;
	}

	public synchronized Mat getColor() {
		return pictureColor;
	}

	//private synchronized void toVec(Vec3fVector vec) {
	//	Circle_set = vec;
	//}



	private void drawLines() {
		for (int i = 0; i < getLinesAmount(); i++) {
			line(getPic(), new Point(getLineXyxy(i, xStartLine), getLineXyxy(i, yStartLine)),
					new Point(getLineXyxy(i, xEndLine), getLineXyxy(i, yEndLine)), Scalar.RED);
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
