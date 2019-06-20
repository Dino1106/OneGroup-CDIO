package vision;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

import java.awt.geom.Point2D;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;

import constants.ClientConstants;
import model.VisionSnapShot;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_core.*;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class VisionController {

	private int[][] frameCoordinates;

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
	private boolean calibration = true;

	private FrameGrabber grabber;
	private OpenCVFrameConverter.ToMat converter;
	private String imgPath;
	private IdentifyCoordinates identifyCoordinates;
	private IdentifyEdges identifyEdges;

	/**
	 * Constructor with a camera
	 * @param cameraId The id of the camera, often either 0 or 1.
	 */
	public VisionController(int cameraId) {
		this.cameraId = cameraId;
		this.usingCamera = true;
		createNecessaryObjects();
	}

	/**
	 * Constructor with a static image (only for testing)
	 * @param imgpath The path for the static image
	 */
	public VisionController(String imgPath) {
		this.imgPath = imgPath;
		createNecessaryObjects();
	}

	/**
	 * Helper for constructor, creates necessary objects.
	 * @param testMode If true = opens window, and refreshes with analysed picture
	 */
	private void createNecessaryObjects() {

		try {
			this.identifyCoordinates = new IdentifyCoordinates();
			this.converter = new OpenCVFrameConverter.ToMat();

			if (this.usingCamera) {
				this.vidFrameOriginal = new CanvasFrame("Original picture");
				this.vidFrameOriginal.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
				this.vidFrameWarped = new CanvasFrame("Warped picture");
				this.vidFrameWarped.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
				this.grabber = FrameGrabber.createDefault(this.cameraId);
				this.grabber.setImageHeight(ClientConstants.imageHeight);
				this.grabber.setImageWidth(ClientConstants.imageWidth);
				this.grabber.start();
			} else {
				this.vidFrameOriginal = new CanvasFrame("Original picture - " + imgPath);
				this.vidFrameOriginal.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
				this.vidFrameWarped = new CanvasFrame("Warped picture - " + imgPath);
				this.vidFrameWarped.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			}

			edgeDetection();

		} catch (Exception e) {
			//("Error in VisionController, createNecessaryObjects():" + e.getStackTrace());
		}
	}

	/**
	 * Returns a snapshot of the current vision picture
	 * @return VisionSnapShot object, containing all objects for translator.
	 */
	public VisionSnapShot getSnapShot() {
		VisionSnapShot visionSnapShot = calculateSnapShot();
		return visionSnapShot;
	}

	/**
	 * Runs algorithms in vision to gather snapshot.
	 * @return VisionSnapShot with all the raw data for the translator.
	 */
	private VisionSnapShot calculateSnapShot() {

		Vec3fVector balls = null;
		int[][] walls = null;
		double[] cross = null;
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
		this.identifyEdges.drawAnchors(pictureOriginal, Scalar.BLACK);
		this.vidFrameOriginal.showImage(converter.convert(pictureOriginal));
		this.vidFrameWarped.showImage(converter.convert(pictureColor));

		// Get coordinates from picture for the walls
		walls = getWallCoordinatesFromPicture(pictureColor);

		
		// 1 - Identify balls with given parameters and draw circles
		IdentifyBalls identifyBalls;
		int[] calib = {6, 5, 2, 20, 20};
		
		if(calibration) {
			identifyBalls = new IdentifyBalls(picturePlain, 1, 11, 120, 15, 4, 8, calib);
			params = identifyBalls.getParams();
			calibration = false;
		} else {
			identifyBalls = new IdentifyBalls(picturePlain,1,params[0],params[1],params[2],params[3],params[4]);
		}
		balls = identifyBalls.getCircles();
		//("Vision - End identify balls");

		
		// 2 - Identify cross with constant parameters
		//("Vision - Start identify cross");
		IdentifyCross identifyCross = new IdentifyCross(pictureColor, identifyCoordinates);
		cross = identifyCross.getArray();
		//("Vision - End identify cross");

		
		// 4 - Identify robot			
		//("Vision - Start identify robot");	
		IdentifyRobot identifyRobot = new IdentifyRobot(pictureRobot, identifyCoordinates);
		robot = identifyRobot.getArray();
		//("Vision - End identify robot");

		
		// 5 - Draw lines on picture
		//("Vision - Insert drawings on live picture");
		identifyBalls.draw(pictureWarped,Scalar.CYAN,true);
		identifyCross.draw(pictureWarped, Scalar.BLUE);
		identifyRobot.draw(pictureWarped, Scalar.BLUE);

		// Update window frame with current picture frame
		this.vidFrameWarped.showImage(converter.convert(pictureWarped));
		this.vidFrameOriginal.showImage(converter.convert(pictureOriginal));

		pictureOriginal.release(); 
		picturePlain.release();
		pictureWarped.release();
		pictureColor.release();
		pictureRobot.release();

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
				// Save the frame as a Mat
				this.grabber.restart();
				Frame frame = this.grabber.grab();

				return this.converter.convert(frame);

			} catch (Exception e) {
				//("Error in VisionController, takePicture():" + e.getStackTrace());
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
		//("VisionController - Starting edge detection");

		pictureOriginal = takePicture(usingCamera);

		identifyEdges = new IdentifyEdges(pictureOriginal, identifyCoordinates);

		frameCoordinates = identifyEdges.getArray();
		
//		Runnable runnable = new Runnable() {
//
//			@Override
//			public void run() {
//				pictureOriginal = takePicture(usingCamera);
//
//				identifyEdges = new IdentifyEdges(pictureOriginal, identifyCoordinates);
//
//				frameCoordinates = identifyEdges.getArray();
//			}
//
//		};
//
//		Thread th = new Thread(runnable);
//		th.start();
//		try {
//			th.join();
//
//		} catch (InterruptedException e) {
//			//("Error in VisionController, edgeDetection():" + e.getStackTrace());
//		}

		//("VisionController - Stopping edge detection");
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

}
