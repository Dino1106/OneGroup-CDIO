package vision;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
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

public class VisionController implements Runnable {

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
    private Mat perspective = new Mat();

	private int cameraId;
	private boolean vid = false;
	private boolean testMode = false;

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
		this.testMode = testMode;

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

			if (vid) {
				grabber.start(); 
			}

			do {
				// Save the frame as a Mat
				if (vid) pictureGlobal = converter.convert(grabber.grab());

				// Clone the "global" picture
				pictureColor = pictureGlobal.clone();
				pictureRobot = pictureGlobal.clone();



				// Set Calibration values for Identify Balls 
				int[] calib = {6, 5, 2, 6, 20};
                extractLayer(pictureGlobal);

				// 3 - Identify Walls by cross
				IdentifyWalls identifyWalls = new IdentifyWalls(pictureColor.clone());
				this.walls = identifyWalls.getArray();
				transform(pictureColor,walls);
				transform(picturePlain,walls);
				transform(pictureRobot,walls);
				
				for(int i = 0; i < walls.length; i++) {
					for(int j = 0; j < walls[0].length; j++) {
						System.out.println("walls: [" + i + "][" + j + "]: " + walls[i][j]);
					}
				}
				
				
				this.walls = new int[4][2];
				this.walls[0][0] = 0;
				this.walls[0][1] = 0;
				this.walls[1][0] = pictureColor.arrayWidth();
				this.walls[1][1] = 0;
				this.walls[2][0] = 0;
				this.walls[2][1] = pictureColor.arrayHeight();
				this.walls[3][0] = pictureColor.arrayWidth();
				this.walls[3][1] = pictureColor.arrayHeight();

				// 1 - Identify balls with given parameters and draw circles
				IdentifyBalls identifyBalls = new IdentifyBalls(picturePlain.clone(), 1, 5, 120, 15, 2, 8, calib);
				this.balls = identifyBalls.getCircles();


				// 2 - Identify cross with constant parameters
				IdentifyCross identifyCross = new IdentifyCross(pictureColor.clone());
				this.cross = identifyCross.getArray();


				// 4 - Identify robot				
				IdentifyRobot identifyRobot = new IdentifyRobot(pictureRobot);
				this.robot = identifyRobot.getArray();

				if (testMode) {
					identifyBalls.draw(pictureColor,Scalar.CYAN,true);
					identifyCross.draw(pictureColor, Scalar.BLUE);
					//identifyWalls.drawAnchors(pictureColor,Scalar.RED);
					line(pictureColor, new Point(0,0), new Point(identifyWalls.centerCross[0],identifyWalls.centerCross[1]),Scalar.RED);
					line(pictureColor, new Point(0,0), new Point(identifyWalls.centerCross[0],identifyWalls.centerCross[1]),Scalar.RED);
					identifyRobot.draw(pictureRobot, Scalar.BLUE);

					// Update window frame with current picture frame
					vidFrame.showImage(converter.convert(pictureColor));
					vidFrameBlue.showImage(converter.convert(pictureRobot));
				}
				//break;
			} while(vid);} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public VisionSnapShot getSnapShot() {
		System.out.println("VisionController - getSnapShot sending snapshot");
		VisionSnapShot vs = new VisionSnapShot(this.balls, this.walls, this.cross, this.robot);
		return vs;
	}

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

    private void transform(Mat picture,int [][] rectangle)
    {
        int width = picture.size().width() ;
        int height = picture.size().height();
        FloatPointer srcC = new FloatPointer(rectangle[0][0],rectangle[0][1],
                rectangle[1][0],rectangle[1][1],
                rectangle[2][0],rectangle[2][1],
                rectangle[3][0],rectangle[3][1]);

        FloatPointer dstC= new FloatPointer(0,0,
                width,0,
                width,height,
                0,height);

        Mat src = new Mat(new Size(2, 4), CV_32F, srcC);
        Mat dst = new Mat(new Size(2, 4), CV_32F, dstC);

        perspective = getPerspectiveTransform(src,dst);
        warpPerspective(picture,picture,perspective,new Size(width,height));
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

	private void drawLines() {
		for (int i = 0; i < getLinesAmount(); i++) {
			line(getPic(), new Point(getLineXyxy(i, xStartLine), getLineXyxy(i, yStartLine)),
					new Point(getLineXyxy(i, xEndLine), getLineXyxy(i, yEndLine)), Scalar.RED);
		}
	}


	private void transform(Mat picture,int [][] rectangle)
	{
		int width = picture.size().width();
		int height = picture.size().height();
		FloatPointer srcC = new FloatPointer(rectangle[0][0],rectangle[0][1],
											rectangle[1][0],rectangle[1][1],
											rectangle[2][0],rectangle[2][1],
											rectangle[3][0],rectangle[3][1]);

		FloatPointer dstC= new FloatPointer(0,0,
											width,0,
											width,height,
											0,height);

		Mat src = new Mat(new Size(2, 4), CV_32F, srcC);
		Mat dst = new Mat(new Size(2, 4), CV_32F, dstC);

		Mat perspective = getPerspectiveTransform(src,dst);
		warpPerspective(picture,picture,perspective,new Size(width,height));
	}



}
