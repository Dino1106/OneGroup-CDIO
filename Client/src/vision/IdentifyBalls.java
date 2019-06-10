package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.opencv.global.opencv_imgproc.HoughCircles;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;

///INITIAL CONDITIONS auto_circle(1 ,3, 120, 15, 2, 8);
/// INITIAL CONDITIONS calib (6 5 2 6 20 )



public class IdentifyBalls {
	Mat picture;
	int resolution_ratio;
	int min_distance;
	int canny_threshold;
	int center_threshold;
	int min_rad;
	int max_rad;
	int calib[] = new int[5];
	Vec3fVector circle = new Vec3fVector();

	private int expected_amount_of_balls = 7;


	public IdentifyBalls(Mat picture, int resolution_ratio, int min_distance, int canny_threshold, int center_threshold, int min_rad, int max_rad ) {
		this.picture = picture;
		this.resolution_ratio = resolution_ratio;
		this.min_distance = min_distance;
		this.canny_threshold = canny_threshold;
		this.center_threshold = center_threshold;
		this.min_rad = min_rad;
		this.max_rad = max_rad;


		extract_circles(resolution_ratio, min_distance, canny_threshold, center_threshold, min_rad, max_rad);
	}


	public IdentifyBalls(Mat picture, int resolution_ratio, int min_distance, int canny_threshold, int center_threshold, int min_rad, int max_rad, int calib[]) {
		this.picture = picture;
		this.resolution_ratio = resolution_ratio;
		this.min_distance = min_distance;
		this.canny_threshold = canny_threshold;
		this.center_threshold = center_threshold;
		this.min_rad = min_rad;
		this.max_rad = max_rad;
		this.calib = calib;


		auto_circle(min_distance, canny_threshold, center_threshold, min_rad, max_rad);
	}

	private void extract_circles(int resolution_ratio, int min_distance, int canny_threshold, int center_threshold,
								 int min_rad, int max_rad) {
		HoughCircles(picture, circle, CV_HOUGH_GRADIENT, resolution_ratio, min_distance, canny_threshold,
				center_threshold, min_rad, max_rad);
	}

	public synchronized Vec3fVector get_circle() {
		return circle;
	}

	private void auto_circle(int param1, int param2, int param3, int param4, int param5) {

		int amount_circles = 7;

		int sec1, sec2 = param2, sec3 = param3, sec4 = param4, sec5 = param5;
		outerloop:
		do {
			for (sec1 = param1 /* (param1-max_change) */; sec1 <= param1 + calib[0]; sec1++) {
				extract_circles(1, sec1, sec2, sec3, sec4, sec5);
				if (eval(amount_circles))
					break outerloop;
				for (sec2 = (param2 - calib[1]); sec2 <= param2 + calib[1]; sec2++) {
					extract_circles(1, sec1, sec2, sec3, sec4, sec5);
					if (eval(amount_circles))
						break outerloop;
					for (sec3 = (param3 - calib[2]); sec3 <= param3 + calib[2]; sec3++) {
						extract_circles(1, sec1, sec2, sec3, sec4, sec5);
						if (eval(amount_circles))
							break outerloop;
						for (sec4 = (param4); sec4 <= param4 + calib[3]; sec4++) {
							extract_circles(1, sec1, sec2, sec3, sec4, sec5);
							if (eval(amount_circles))
								break outerloop;
							for (sec5 = (param5); sec5 <= param5 + calib[4]; sec5++) {
								extract_circles(1, sec1, sec2, sec3, sec4, sec5);
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
