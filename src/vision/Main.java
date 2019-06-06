package vision;

import org.bytedeco.opencv.opencv_core.Mat;

public class Main {

	public static void main(String[] args) {
		VisionController test = new VisionController();
		Thread th = new Thread(test);
		th.start();
		
		//System.out.println( im.arraySize() + "hue");

	}

}
