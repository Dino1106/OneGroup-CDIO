package vision;

import org.bytedeco.opencv.opencv_core.Mat;

public class Main {

	public static void main(String[] args) {
		ObjectReader test = new ObjectReader();
		Thread th = new Thread(test);
		th.start();
		
		//System.out.println( im.arraySize() + "hue");

	}

}
