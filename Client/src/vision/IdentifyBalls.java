package vision;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_core.CV_8U;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.opencv.global.opencv_imgproc.HoughCircles;
import static org.bytedeco.opencv.global.opencv_imgproc.circle;
import static org.bytedeco.opencv.global.opencv_imgproc.line;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;

import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgproc.line;

import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgproc.line;

///INITIAL CONDITIONS auto_circle(1 ,3, 120, 15, 2, 8);
/// INITIAL CONDITIONS calib (6 5 2 6 20 )

public class IdentifyBalls {
	private int resolutionRatio, minDistance, cannyThreshold, centerThreshold;
	private static final int xCircle = 0;
	private static final int yCircle = 1;
	private static final int radCircle = 2;
	private int minRad, maxRad;
	private int calib[];
	private Mat picture;
	private Vec3fVector circle = new Vec3fVector();

	public IdentifyBalls(Mat picture, int resolutionRatio, int minDistance, int cannyThreshold, int centerThreshold, int minRad, int maxRad) {
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
		circle = new Vec3fVector();

		autoCircle(minDistance, cannyThreshold, centerThreshold, minRad, maxRad);
	}

	private void extractCircles(int resolutionRatio, int minDistance, int cannyThreshold, int centerThreshold, int minRad, int maxRad) {
		HoughCircles(picture, circle, CV_HOUGH_GRADIENT, resolutionRatio, minDistance, cannyThreshold, centerThreshold, minRad, maxRad);
	}

	public synchronized Vec3fVector getCircles() {
		return circle;
	}

	private void autoCircle(int paramMinDistance, int paramCannyThreshold, int paramCenterThreshold, int paramMinRad, int paramMaxRad) {

		int amount_circles = 10;

		int sec1, sec2 = paramCannyThreshold, sec3 = paramCenterThreshold, sec4 = paramMinRad, sec5 = paramMaxRad;
		outerloop:
			do {
				for (sec1 = paramMinDistance /* (param1-max_change) */; sec1 <= paramMinDistance + calib[0]; sec1++) {
					extractCircles(1, sec1, sec2, sec3, sec4, sec5);
					if (eval(amount_circles))
					{
						minDistance = sec1;
						cannyThreshold = sec2;
						centerThreshold = sec3;
						minRad = sec4;
						maxRad = sec5;
						break outerloop;
					}
					for (sec2 = (paramCannyThreshold - calib[1]); sec2 <= paramCannyThreshold + calib[1]; sec2++) {
						extractCircles(1, sec1, sec2, sec3, sec4, sec5);
						if (eval(amount_circles)){
							minDistance = sec1;
							cannyThreshold = sec2;
							centerThreshold = sec3;
							minRad = sec4;
							maxRad = sec5;
							break outerloop;}
						for (sec3 = (paramCenterThreshold - calib[2]); sec3 <= paramCenterThreshold + calib[2]; sec3++) {
							extractCircles(1, sec1, sec2, sec3, sec4, sec5);
							if (eval(amount_circles)){
								minDistance = sec1;
								cannyThreshold = sec2;
								centerThreshold = sec3;
								minRad = sec4;
								maxRad = sec5;
								break outerloop;}
							for (sec4 = (paramMinRad); sec4 <= paramMinRad + calib[3]; sec4++) {
								extractCircles(1, sec1, sec2, sec3, sec4, sec5);
								if (eval(amount_circles)){
									minDistance = sec1;
									cannyThreshold = sec2;
									centerThreshold = sec3;
									minRad = sec4;
									maxRad = sec5;
									break outerloop;}
								for (sec5 = (paramMaxRad); sec5 <= paramMaxRad + calib[4]; sec5++) {
									extractCircles(1, sec1, sec2, sec3, sec4, sec5);
									if (eval(amount_circles)){
										minDistance = sec1;
										cannyThreshold = sec2;
										centerThreshold = sec3;
										minRad = sec4;
										maxRad = sec5;
										break outerloop;}
								}
							}
						}
					}

				}
			} while (!eval(amount_circles--));
	}

	public int[] getParams(){
		int[] params = {minDistance,cannyThreshold,centerThreshold,minRad,maxRad};
		return params;
	}

	private boolean eval(int amount) {
		if (circle.size() == amount)
			return true;
		return false;
	}

	public void draw(Mat drawIn, Scalar color, boolean centers) {
		for (int i = 0; i < circle.size(); i++) {
			circle(drawIn,
					new Point((int) circle.get(i).get(xCircle),
							(int) circle.get(i).get(yCircle)),
					(int) circle.get(i).get(radCircle),
					Scalar.RED);
			if (centers) {
				line(drawIn, new Point(getXyr(i, xCircle) - 3, getXyr(i, yCircle)),
						new Point(getXyr(i, xCircle) + 3, getXyr(i, yCircle)), color);
				line(drawIn, new Point(getXyr(i, xCircle), getXyr(i, yCircle) - 3),
						new Point(getXyr(i, xCircle), getXyr(i, yCircle) + 3), color);
			}
		}

	}

	public synchronized int getXyr(int circle_number, int parameter) {
		return (int) circle.get(circle_number).get(parameter);
	}

	public void drawNodes(Mat drawIn, Scalar color) {
		int i;
		int u;
		for (u = 0; u < getSize(); u++)
			for (i = 0; i < getSize(); i++)
				if (i != u)
					line(drawIn, new Point(getXyr(u, xCircle), getXyr(u, yCircle)),
							new Point(getXyr(i, xCircle), getXyr(i, yCircle)), color);
	}


	public synchronized int getSize() {
		return (int) circle.size();
	}


	/*
	public void moveBalls( Mat pictureGlobal,Mat perspective)
	{

		//FloatPointer a = new FloatPointer(pictureGlobal.arrayWidth()*pictureGlobal.arrayHeight());
		Mat template = new Mat(new Size(pictureGlobal.arrayWidth(),pictureGlobal.arrayHeight()),CV_8U);
		int width =template.arrayWidth();
		long actualArrayPosition;
		long x,y;
		int height = template.arrayHeight();
		BytePointer p = template.data();
		int n = 0;
		for(int i = 0; i < circle.size();i++){
			y = (long) (width*circle.get(i).get(1));
			x= (int) (circle.get(i).get(0));
			actualArrayPosition =  (y+x);
			p.put(actualArrayPosition,(byte)1);
		}


		warpPerspective(template,template,perspective,new Size(width,height));

		for(int i = 0; i < template.arrayHeight();i++)
			for(int u = 0; u < template.arrayWidth();u++){
				if(p.get((i*template.arrayWidth())+u)==1){
					circle.get(n).put(0,u);
					circle.get(n++).put(1,i);
				}
			}



	}
}


	 */
}
