package vision;

import static org.bytedeco.opencv.global.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.opencv.global.opencv_imgproc.HoughCircles;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;

public class IdentifyBalls {
	Mat picture;
	int resolution_ratio;
	int min_distance;
	int canny_threshold;
	int center_threshold;
	int min_rad;
	int max_rad;
	Vec3fVector circle = new Vec3fVector();
	
	public IdentifyBalls(Mat picture, int resolution_ratio, int min_distance, int canny_threshold, int center_threshold, int min_rad, int max_rad) {
		this.picture = picture;
		this.resolution_ratio = resolution_ratio;
		this.min_distance = min_distance;
		this.canny_threshold = canny_threshold;
		this.center_threshold = center_threshold;
		this.min_rad = min_rad;
		this.max_rad = max_rad;
		
		extract_circles(resolution_ratio, min_distance, canny_threshold, center_threshold, min_rad, max_rad);
	}
	
	private void extract_circles(int resolution_ratio, int min_distance, int canny_threshold, int center_threshold,
			int min_rad, int max_rad) {
		HoughCircles(picture, circle, CV_HOUGH_GRADIENT, resolution_ratio, min_distance, canny_threshold,
				center_threshold, min_rad, max_rad);
	}
	public synchronized int get_circle(int circle_number, int parameter) {
		return (int) circle.get(circle_number).get(parameter);
	}
}
