/**
 * 
 */
package cv;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_GRAY2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_HOUGH_GRADIENT;
import static org.bytedeco.opencv.global.opencv_imgproc.HoughCircles;
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

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

/**
 * @author Jakub Tomczak
 *
 */
public class ObjectReader implements Runnable {
	
	public static final int x_circle = 0;
	public static final int  y_circle = 1;
	public static final int rad_circle = 2;
	public static final int xstart_line = 0;
	public static final int  ystart_line = 1;
	public static final int xend_line = 2;
	public static final int yend_line = 3;
	
	
	private CanvasFrame vid_frame = new CanvasFrame("frame1");
	private CanvasFrame vid_edges = new CanvasFrame("edges");
	private Vec4iVector Line_set = new Vec4iVector();
	private  Vec3fVector Circle_set = new Vec3fVector();
	private int Camera_id;
	private  Mat picture_global = new Mat() , picture_plain = new Mat();
	private Mat edges_global;
	boolean vid;
	

	
	
	
	
	
	
	public ObjectReader()
	{
		Camera_id = 0;
		vid_frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		vid = true;
	
		
	}
	public ObjectReader(int camera)
	{
		Camera_id = camera;
		vid_frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		vid = true;
		
	}
	
	public ObjectReader(String imgpath)
	{
	 Camera_id = 0;
	 vid_frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	 vid = false;
	 picture_global = imread(imgpath);
	 
		
		
	}
	
	
	public void create_nodes()
	{
		int i;
		int u;
		for(u = 0; u < get_vec_len(); u++) 
		for(i = 0; i < get_vec_len(); i++)
		if(i!=u)
		line(get_pic(), new Point(get_circle(u, x_circle),get_circle(u, y_circle)), new Point( get_circle(i, x_circle),get_circle(i, y_circle)), Scalar.MAGENTA);
	}
	
	
	
	/**
	 * src_gray: Input image (grayscale)
	 *	circles: A vector that stores sets of 3 values: x_{c}, y_{c}, r for each detected circle.
	 *	CV_HOUGH_GRADIENT: Define the detection method. Currently this is the only one available in OpenCV
	 *  dp = 1: The inverse ratio of resolution
	 *	min_dist = src_gray.rows/8: Minimum distance between detected centers
	 *	param_1 = 200: Upper threshold for the internal Canny edge detector
	 *	param_2 = 100*: Threshold for center detection.
	 *	min_radius = 0: Minimum radio to be detected. If unknown, put zero as default.
	 *	max_radius = 0: Maximum radius to be detected. If unknown, put zero as default
	 *
	 */


	private void extract_layer()
	{
		int i;

		Mat picture = get_pic();
		BytePointer dat;

		cvtColor(picture, picture, COLOR_BGR2HSV);
		dat = picture.data();

		for (i = 0; i < (picture.arrayHeight()*picture.arrayWidth()*3);i += 3)
		{
			dat = dat.put(0 + i , (byte) dat.get(i+2));
			dat = dat.put(1 + i  , (byte) dat.get(i+2));
		}
		//picture = picture.data(dat);

		cvtColor(picture, picture, COLOR_BGR2GRAY);
		picture_plain = picture_global.clone();
		cvtColor(picture, picture, COLOR_GRAY2BGR);
	}

	private void extract_circles(int resolution_ratio,int min_distance,int Canny_threshold,int Center_threshold,int min_rad, int max_rad)
	{

		Vec3fVector circle = new Vec3fVector();
		HoughCircles(get_plain(), circle , CV_HOUGH_GRADIENT, resolution_ratio,min_distance, Canny_threshold, Center_threshold, min_rad,max_rad);
		toVec(circle);


	}

	private void extract_lines(double rho,double theta,int threshold, int minLineLength,int maxLineGap,Size filter_dim, int threshold1, int threshold2){

		Vec4iVector lines = new Vec4iVector();
		Mat blurred = new Mat(),edges = new Mat() ;
		blur(get_plain(),blurred  , filter_dim);
		Canny(blurred,edges, threshold1, threshold2);
		HoughLinesP(edges, lines,rho, theta, threshold, minLineLength, maxLineGap);
		to_lineVec(lines);
		draw_lines();



	}








	
	public synchronized int get_circle(int circle_number, int parameter)
	{
		return (int) Circle_set.get(circle_number).get(parameter);
	}
	public synchronized int get_line(int line_number, int parameter)
	{
		return new IntPointer(Line_set.get(line_number)).get(parameter);
	}
	
	public synchronized int get_vec_len()
	{
		return (int) Circle_set.size();
	}
	public synchronized int get_Linevec_len()
	{
		return (int) Line_set.size();
	}
	public synchronized Mat get_pic()
	{
		return picture_global;
	}
	public synchronized Mat get_plain()
	{
		return picture_plain;
	}
	
	public synchronized Mat get_edges()
	{
		return edges_global;
	}
	
/*	private void calculate_nodes()
	{
		
		int i;
		
		Vector<Vector<Point>> temp_v = new Vector<Vector<Point>>();
		Vector<Point> temp_dat = new Vector<Point>();
		
		for(i = 0; i < get_vec_len()-1;i++) {
		temp_dat.add(new Point(get_circle(i, 0),get_circle(i,1)));
		temp_dat.add(new Point(get_circle(i+1, 0),get_circle(i+1,1)));
		temp_v.add(temp_dat);
		temp_dat.clear();
		}
	}
*/	
	
	
	
	
	private synchronized void toVec(Vec3fVector vec)
	{
		Circle_set = vec;
	}
	
	private synchronized void draw_circles(Boolean centers)
	{
		int i;
		for(i = 0; i < 7; i++) {
			circle(get_pic(), new Point(get_circle(i,x_circle),get_circle(i,y_circle)),get_circle(i,rad_circle), Scalar.RED);
				if (centers) {
					line(get_pic(), new Point(get_circle(i,x_circle)-3,get_circle(i,y_circle)), new Point(get_circle(i,x_circle)+3,get_circle(i,y_circle)), Scalar.BLUE);
					line(get_pic(), new Point(get_circle(i,x_circle),get_circle(i,y_circle)-3), new Point(get_circle(i,x_circle),get_circle(i,y_circle)+3), Scalar.BLUE);
					}
		}
	}
	
	private  void draw_lines()
	{
		int i;
		for(i = 0; i < get_Linevec_len(); i++) { 
			line(get_pic(), new Point(get_line(i,xstart_line),get_line(i,ystart_line)),  new Point(get_line(i,xend_line),get_line(i,yend_line)), Scalar.RED);
		}
		
		//System.out.println(new IntPointer(Line_set.get(0)).get(0));
		
	}
	

	
	private synchronized void to_lineVec(Vec4iVector vec)
	{
		Line_set = vec;
		
	}
	
	/*
	private synchronized void draw_lines()
	{
		HoughCircles(get_pic(), , arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		
		
	}
	*/
	
	private synchronized void update_image(Mat img, Mat edg)
	{
		picture_global = img;
		edges_global = edg;
	}
	
	private synchronized void update_image(Mat img)
	{
		picture_global = img;
	}












	private void Generate_Objects()
	{
		extract_layer();
		//extract_lines( 1, CV_PI/180, 30, 0, 200, new Size(3,3), 50, 100);
        //extract_circles(1,50,120,80,50,100);


		auto_circle(3,120,15,2,8);








	}


	private void auto_circle( int param1, int param2, int param3, int param4, int param5){
		int max_change_param1 = 6;
		int max_change_param2 = 5;
		int max_change_param3 = 2;
		int max_change_param4 = 6;
		int max_change_param5 = 20;
		int amount_circles = 7;

		int sec1,sec2=param2,sec3=param3,sec4=param4,sec5=param5;
		outerloop:
		do{
			for(sec1 = param1 /*(param1-max_change)*/;sec1 <= param1+max_change_param1;sec1++ ) {
				extract_circles(1, sec1, sec2, sec3, sec4, sec5);
				if (eval(amount_circles)) break outerloop;
				for (sec2 = (param2 - max_change_param2); sec2 <= param2 + max_change_param2; sec2++) {
					extract_circles(1, sec1, sec2, sec3, sec4, sec5);
					if (eval(amount_circles)) break outerloop;
					for (sec3 = (param3 - max_change_param3); sec3 <= param3 + max_change_param3; sec3++) {
						extract_circles(1, sec1, sec2, sec3, sec4, sec5);
						if (eval(amount_circles)) break outerloop;
						for (sec4 = (param4); sec4 <= param4 + max_change_param4; sec4++) {
							extract_circles(1, sec1, sec2, sec3, sec4, sec5);
							if (eval(amount_circles)) break outerloop;
							for (sec5 = (param5); sec5 <= param5 + max_change_param5; sec5++) {
								extract_circles(1, sec1, sec2, sec3, sec4, sec5);
								if (eval(amount_circles)) break outerloop;
							}
						}
					}
				}

			}}while(!eval(amount_circles--));
		draw_circles(true);
		create_nodes();

    }



	private boolean eval(int amount)
    {
        if(get_vec_len() == amount) return true;
        return false;
    }





	@Override
	public void run()
	{
		try {
			
			FrameGrabber grabber = FrameGrabber.createDefault(Camera_id);
			OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
			if(vid) {
	        grabber.start();
			}
	        while(vid) {
	        		update_image(converter.convert(grabber.grab()));
            		//extract_circles(1,50,120,80,50,100);
            		extract_layer();
            		vid_frame.showImage(converter.convert(get_pic()));
            		vid_edges.showImage(converter.convert(get_edges()));
	        			}
	        if(!vid)
	        {

			Generate_Objects();




				//extract_circles(1,50,120,80,50,100);
	        	vid_frame.showImage(converter.convert(get_pic()));
	        	vid_edges.showImage(converter.convert(get_plain()));
	        }
	        
	        
	        
	        
		} catch (Exception e) {e.printStackTrace();}
		
		
		
		
	}
	
	
	
	
	
	

}
