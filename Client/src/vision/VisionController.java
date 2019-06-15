package vision;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

import java.awt.geom.Point2D;
import java.util.Scanner;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;
import org.bytedeco.opencv.opencv_imgproc.Vec4iVector;

import model.VisionSnapShot;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_core.*;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class VisionController {

	private int imageHeight = 480; // where previously 720
	private int imageWidth = 720; // where previously 1366

	private static final int xStartLine = 0;
	private static final int yStartLine = 1;
	private static final int xEndLine = 2;
	private static final int yEndLine = 3;

	private int[][] frameCoordinates;

	private Vec4iVector lineSet = new Vec4iVector();

	private CanvasFrame vidFrameOriginal;
	private CanvasFrame vidFrameWarped;

	private Mat pictureOriginal = new Mat();
	private Mat pictureWarped = new Mat();
	private Mat pictureColor = new Mat();
	private Mat pictureRobot = new Mat();
	private Mat picturePlain = new Mat(); 

	private int cameraId = 0;
	private int[] params = new int[5];
	private boolean usingCamera = false;
	private boolean testMode = false;
	private boolean calibration = true;

	// Was previously in run
	private FrameGrabber grabber;
	private OpenCVFrameConverter.ToMat converter;
	private String imgPath;
	private IdentifyCoordinates identifyCoordinates;

	/**
	 * Constructor with a camera
	 * @param testMode If true = opens window, and refreshes with analysed picture
	 * @param cameraId The id of the camera, often either 0 or 1.
	 */
	public VisionController(boolean testMode, int cameraId) {
		this.cameraId = cameraId;
		this.usingCamera = true;
		this.testMode = testMode;
		createNecessaryObjects(testMode);
	}


	/**
	 * Constructor with a static image (only for testing)
	 * @param testMode If true = opens window, and refreshes with analysed picture
	 * @param imgpath The path for the static image
	 */
	public VisionController(boolean testMode, String imgPath) {
		this.imgPath = imgPath;
		createNecessaryObjects(testMode);
	}


	/**
	 * Helper for constructor, creates necessary objects.
	 * @param testMode If true = opens window, and refreshes with analysed picture
	 */
	private void createNecessaryObjects(boolean testMode) {

		try {
			this.identifyCoordinates = new IdentifyCoordinates();
			this.testMode = testMode;
			this.converter = new OpenCVFrameConverter.ToMat();

			if (testMode) {
				this.vidFrameOriginal = new CanvasFrame("Original picture");
				this.vidFrameOriginal.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
				this.vidFrameWarped = new CanvasFrame("Warped picture");
				this.vidFrameWarped.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			}

			if (this.usingCamera) {
				this.grabber = FrameGrabber.createDefault(this.cameraId);
				this.grabber.setImageHeight(this.imageHeight);
				this.grabber.setImageWidth(this.imageWidth);
			}

			edgeDetection();

		} catch (Exception e) {
			System.out.println("Error in VisionController, createNecessaryObjects():" + e.getStackTrace());
		}
	}


	/**
	 * Returns a snapshot of the current vision picture
	 * @return VisionSnapShot object, containing all objects for translator.
	 */
	public VisionSnapShot getSnapShot() {
		VisionSnapShot visionSnapShot = calculateSnapShot();
		System.out.println("VisionController, getSnapShot - sending snapshot");
		return visionSnapShot;
	}


	/**
	 * Runs algorithms in vision to gather snapshot.
	 * @return VisionSnapShot with all the raw data for the translator.
	 */
	private VisionSnapShot calculateSnapShot() {
		Vec3fVector balls = null;
		int[][] walls = null;
		int[][] cross = null;
		int[][] robot = null;

		//Take the picture
		this.pictureOriginal = takePicture(usingCamera);

		//Takes value layer and generates single-layered Mat
		picturePlain = extractLayer(pictureOriginal.clone());

		//Clone original picture and warp.
		this.pictureWarped = this.pictureOriginal.clone();
		transform(pictureWarped,frameCoordinates);
		transform(picturePlain, frameCoordinates);

		// Clone the "warped" picture
		this.pictureColor = this.pictureWarped.clone();
		this.pictureRobot = this.pictureWarped.clone();

		//See the pictures first time, to debug.
		if (this.testMode) {
			this.vidFrameOriginal.showImage(converter.convert(pictureOriginal));
			this.vidFrameWarped.showImage(converter.convert(pictureColor));
		}

		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.println("Calibration done, enter 'yes' to confirm, 'no' to restart");
			String s = in.nextLine();
			if (s.equals("yes")) {
				break;
			}
			if (s.equals("no")) {
				edgeDetection();
				return calculateSnapShot();
			}
		}

		// Get coordinates from picture for the walls
		walls = getWallCoordinatesFromPicture(pictureColor);

		// 1 - Identify balls with given parameters and draw circles
		System.out.println("Vision - Start identify balls");

		//TODO: Clear up what this should do...
		IdentifyBalls identifyBalls;
		int[] calib = {6, 5, 2, 6, 20};
		if(calibration) {
			identifyBalls = new IdentifyBalls(picturePlain.clone(), 1, 7, 120, 15, 5, 8, calib);
			params = identifyBalls.getParams();
			calibration = false;
		}else{
			identifyBalls = new IdentifyBalls(picturePlain.clone(),1,params[0],params[1],params[2],params[3],params[4]);
		}
		balls = identifyBalls.getCircles();

		System.out.println("Vision - End identify balls");

		System.out.println("Vision - Start identify cross");
		// 2 - Identify cross with constant parameters
		IdentifyCross identifyCross = new IdentifyCross(pictureColor.clone(), identifyCoordinates);
		cross = identifyCross.getArray();
		System.out.println("Vision - End identify cross");

		System.out.println("Vision - Start identify robot");
		// 4 - Identify robot				
		IdentifyRobot identifyRobot = new IdentifyRobot(pictureRobot, identifyCoordinates);
		robot = identifyRobot.getArray();
		System.out.println("Vision - End identify robot");


		if (testMode) {
			System.out.println("Vision - Insert drawings on live picture");
			identifyBalls.draw(pictureWarped,Scalar.CYAN,true);
			identifyCross.draw(pictureWarped, Scalar.BLUE);
			identifyRobot.draw(pictureWarped, Scalar.BLUE);

			// Update window frame with current picture frame
			this.vidFrameWarped.showImage(converter.convert(pictureWarped));
		}	

		return new VisionSnapShot(balls, walls, cross, robot);
	}


	/**
	 * Takes a picture, and returns a mat, using the global grabber.
	 * @param usingCamera decides to use either camera or static image.
	 * @return The mat from either camera, or static image.
	 */
	private Mat takePicture(boolean usingCamera) {		
		if (usingCamera) {
			try {

				this.grabber.start();

				// Save the frame as a Mat
				Frame frame = this.grabber.grab();

				this.grabber.stop();

				return this.converter.convert(frame);

			} catch (Exception e) {
				System.out.println("Error in VisionController, takePicture():" + e.getStackTrace());
				return null;
			}
		} else {
			return imread(imgPath);
		}
	}


	/**
	 * Runs the detection of edges (and takes pictures), until the frameCoordinates is not one of the corners of the original picture.
	 */
	private void edgeDetection() {
		System.out.println("VisionController - Starting edge detection");

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				pictureOriginal = takePicture(usingCamera);
				System.out.println("VisionController - Running edge detection");


				IdentifyEdges identifyEdges = new IdentifyEdges(pictureOriginal, identifyCoordinates);

				frameCoordinates = identifyEdges.getArray();
			}

		};

		Thread th = new Thread(runnable);
		th.start();
		try {
			th.join();

		} catch (InterruptedException e) {
			System.out.println("Error in VisionController, edgeDetection():" + e.getStackTrace());
		}

		System.out.println("VisionController - Stopping edge detection");
	}


	/**
	 * Checks if warped coordinates are going to be warped correctly.
	 * Runs before the picture is warped!
	 * @param coordinateArray The array to be checked
	 * @param imageHeight The max height of the original picture
	 * @param imageWidth The max length of the original picture
	 * @return true if edges are detected correct.
	 */
	private boolean isEdgesDetectedCorrect(int[][] coordinateArray, int imageHeight, int imageWidth) {

		boolean warpPicture = true;
		int partCoordinate;

		for (int i = 0; i < coordinateArray.length; i++) {
			for(int j = 0; j < coordinateArray[i].length; j++) {
				partCoordinate = coordinateArray[i][j];

				if (partCoordinate == imageHeight) {
					return false;
				} 
				else if (partCoordinate == imageWidth) {
					return false;
				}
				else if (partCoordinate == 0) {
					return false;
				} else {
					// Nothing
				}
			}
		}
		return warpPicture;
	}


	/**
	 * Takes value layer and generates single-layered Mat
	 * @param picture The mat that needs to be extracted
	 */
	private Mat extractLayer(Mat picture) {
		BytePointer dat;

		cvtColor(picture, picture, COLOR_BGR2HSV);
		dat = picture.data();

		for (int i = 0; i < (picture.arrayHeight() * picture.arrayWidth() * 3); i += 3) {
			dat = dat.put(0 + i, (byte) dat.get(i + 2));
			dat = dat.put(1 + i, (byte) dat.get(i + 2));
		}

		cvtColor(picture, picture, COLOR_BGR2GRAY);
		return picture;
	}


	/**
	 * Transforms the picture from 4 points in a rectangle array.
	 * @param picture The picture that needs to be transformed.
	 * @param rectangle The rectangle in an int matrix - must be (int[4][2])
	 */
	private void transform(Mat picture,int [][] rectangle)
	{	
		FloatPointer srcC = new FloatPointer(rectangle[0][0],rectangle[0][1],
				rectangle[1][0],rectangle[1][1],
				rectangle[2][0],rectangle[2][1],
				rectangle[3][0],rectangle[3][1]);

		double barrierWidth = Point2D.distance(rectangle[0][0], rectangle[0][1], rectangle[1][0], rectangle[1][1]);
		double barrierHeight = Point2D.distance(rectangle[1][0], rectangle[1][1], rectangle[2][0], rectangle[2][1]);

		double delta = barrierWidth/barrierHeight;

		int width = (int) (barrierWidth * delta);
		int height = (int) barrierWidth;

		FloatPointer dstC= new FloatPointer(0,0,
				width,0,
				width,height,
				0,height);

		Mat src = new Mat(new Size(2, 4), CV_32F, srcC);
		Mat dst = new Mat(new Size(2, 4), CV_32F, dstC);

		Mat perspective = getPerspectiveTransform(src,dst);
		warpPerspective(picture,picture,perspective,new Size(width,height));
	}


	/**
	 * Gets coordinates from a warped picture, 
	 * @param picture The picture to take reference from.
	 * @return returns the int array, int[i][j], i = point, j = x and y. (int[4][2])
	 */
	private int[][] getWallCoordinatesFromPicture(Mat picture) {

		int[][] wallCoordinates = new int[4][2];

		wallCoordinates[0][0] = 0;
		wallCoordinates[0][1] = 0;

		wallCoordinates[1][0] = picture.arrayWidth();
		wallCoordinates[1][1] = 0;

		wallCoordinates[2][0] = 0;
		wallCoordinates[2][1] = picture.arrayHeight();

		wallCoordinates[3][0] = picture.arrayWidth();
		wallCoordinates[3][1] = picture.arrayHeight();

		return wallCoordinates;
	}






























	// TODO: EVERYTHING BELOW IS IRRELEVANT TO THIS CLASS
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: THE METHOD NEEDS TO BE IMPLEMENTED IN LINE DETECTION

	/*
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
		return pictureOriginal;
	}

	public synchronized Mat getPlain() {
		return picturePlain;
	}

	public synchronized Mat getColor() {
		return pictureColor;
	}

	private void drawLines() {
		for (int i = 0; i < getLinesAmount(); i++) {
			line(getPic(), new Point(getLineXyxy(i, xStartLine), getLineXyxy(i, yStartLine)),
					new Point(getLineXyxy(i, xEndLine), getLineXyxy(i, yEndLine)), Scalar.RED);
		}
	}
	 */


}
