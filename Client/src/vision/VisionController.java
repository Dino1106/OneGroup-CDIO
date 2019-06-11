package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.circle;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;
import org.bytedeco.opencv.opencv_imgproc.Vec4iVector;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_core.*;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class VisionController implements Runnable {

	private int imageHeight = 720;
	private int imageWidth = 1366;


	private static final int xStartLine = 0;
	private static final int yStartLine = 1;
	private static final int xEndLine = 2;
	private static final int yEndLine = 3;
	
	private CanvasFrame vidFrame = new CanvasFrame("frame1");
	
	private Vec4iVector lineSet = new Vec4iVector();
	private Vec3fVector circleSet = new Vec3fVector();
	
	private int cameraId;
	private Mat pictureGlobal = new Mat(), picturePlain = new Mat(), pictureColor = new Mat();
	private boolean vid;

	// Laptop camera constructor
	public VisionController() {
		cameraId = 0;
		vidFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		vid = true;
	}

	// USB plugged camera.
	// ID of the camera indicates which camera has to be used
	public VisionController(int camera) {
		cameraId = camera;
		vidFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		vid = true;
	}

	// Constructor with a static image
	public VisionController(String imgpath) {
		cameraId = 0;
		vidFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		vid = false;
		pictureGlobal = imread(imgpath);

		// Copy image for color recognition 
		pictureColor = pictureGlobal.clone();
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

			if (vid) pictureGlobal = converter.convert(grabber.grab());

			// Set Calibration values for Identify Balls
			int[] calib = {6, 5, 2, 6, 20};

			// Generate layers
			extractLayer(pictureGlobal);

			// 1 - Identify balls with given parameters and draw circles
			IdentifyBalls identifyBalls = new IdentifyBalls(picturePlain, 1, 3, 120, 15, 2, 8, calib);
			identifyBalls.draw(pictureColor,Scalar.CYAN,true);

			// 2 - Identify cross with constant parameters
            IdentifyCross identifyCross = new IdentifyCross(pictureColor.clone());
            identifyCross.draw(pictureColor,Scalar.BLUE);


            // 3 - Identify Walls by cross
			IdentifyWalls identifyWalls = new IdentifyWalls(identifyCross.get_array());
			identifyWalls.drawWalls(pictureColor,Scalar.RED);
			line(pictureColor, new Point(0,0), new Point(identifyWalls.centerCross[0],identifyWalls.centerCross[1]),Scalar.RED);

			// Update window frame with current picture frame
			vidFrame.showImage(converter.convert(pictureColor));
	 		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		picturePlain = pictureGlobal.clone();
		cvtColor(picture, picture, COLOR_GRAY2BGR);
	}

	// TODO: THE METHOD NEEDS TO BE IMPLEMENTED IN LINE DETECTION
	private void extract_lines(double rho, double theta, int threshold, int minLineLength, int maxLineGap,
			Size filterDim, int threshold1, int threshold2) {
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
