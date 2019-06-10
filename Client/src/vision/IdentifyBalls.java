package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.opencv.global.opencv_imgproc.HoughCircles;

import java.util.ArrayList;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;

import model.Ball;

///INITIAL CONDITIONS auto_circle(1 ,3, 120, 15, 2, 8);
/// INITIAL CONDITIONS calib (6 5 2 6 20 )

public class IdentifyBalls {
	private int resolutionRatio, minDistance, cannyThreshold, centerThreshold;
	private int minRad, maxRad;
	private int calib[];
	private Mat picture;
	private Vec3fVector circle;

	public IdentifyBalls(Mat picture, int resolutionRatio, int minDistance, int cannyThreshold, int centerThreshold, int minRad, int maxRad ) {
		this.resolutionRatio = resolutionRatio;
		this.minDistance = minDistance;
		this.cannyThreshold = cannyThreshold;
		this.centerThreshold = centerThreshold;
		this.minRad = minRad;
		this.maxRad = maxRad;
		this.picture = picture;
		calib = new int[5];
		circle = new Vec3fVector();


		extractCircles(resolutionRatio, minDistance, cannyThreshold, centerThreshold, minRad, maxRad);
	}


	public IdentifyBalls(Mat picture, int resolutionRatio, int minDistance, int cannyThreshold, int centerThreshold, int minRad, int maxRad, int calib[]) {
		this.picture = picture;
		this.resolutionRatio = resolutionRatio;
		this.minDistance = minDistance;
		this.cannyThreshold = cannyThreshold;
		this.centerThreshold = centerThreshold;
		this.minRad = minRad;
		this.maxRad = maxRad;
		this.calib = calib;


		autoCircle(minDistance, cannyThreshold, centerThreshold, minRad, maxRad);
	}

	private void extractCircles(int resolutionRatio, int minDistance, int cannyThreshold, int centerThreshold,
								 int minRad, int maxRad) {
		HoughCircles(picture, circle, CV_HOUGH_GRADIENT, resolutionRatio, minDistance, cannyThreshold,
				centerThreshold, minRad, maxRad);
	}

	public synchronized Vec3fVector getCircle() {
		return circle;
	}

	private void autoCircle(int param1, int param2, int param3, int param4, int param5) {

		int amount_circles = 7;

		int sec1, sec2 = param2, sec3 = param3, sec4 = param4, sec5 = param5;
		outerloop:
		do {
			for (sec1 = param1 /* (param1-max_change) */; sec1 <= param1 + calib[0]; sec1++) {
				extractCircles(1, sec1, sec2, sec3, sec4, sec5);
				if (eval(amount_circles))
					break outerloop;
				for (sec2 = (param2 - calib[1]); sec2 <= param2 + calib[1]; sec2++) {
					extractCircles(1, sec1, sec2, sec3, sec4, sec5);
					if (eval(amount_circles))
						break outerloop;
					for (sec3 = (param3 - calib[2]); sec3 <= param3 + calib[2]; sec3++) {
						extractCircles(1, sec1, sec2, sec3, sec4, sec5);
						if (eval(amount_circles))
							break outerloop;
						for (sec4 = (param4); sec4 <= param4 + calib[3]; sec4++) {
							extractCircles(1, sec1, sec2, sec3, sec4, sec5);
							if (eval(amount_circles))
								break outerloop;
							for (sec5 = (param5); sec5 <= param5 + calib[4]; sec5++) {
								extractCircles(1, sec1, sec2, sec3, sec4, sec5);
								if (eval(amount_circles))
									break outerloop;
							}
						}
					}
				}

			}
		} while (!eval(amount_circles--));

	}

	private boolean eval(int amount) {
		if (circle.size() == amount)
			return true;
		return false;
	}
	
}
