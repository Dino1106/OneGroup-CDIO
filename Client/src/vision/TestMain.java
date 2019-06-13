package vision;

public class TestMain {

	public static void main(String[] args) {
		
		boolean testMode = true;
		
		VisionController test = new VisionController(testMode, "e.jpg");
		VisionController test1 = new VisionController(testMode, "b.jpg");
		VisionController test2 = new VisionController(testMode, "d1.jpg");
		Thread th = new Thread(test);
		Thread th1 = new Thread(test1);
		Thread th2 = new Thread(test2);
		th.start();
		th1.start();
		th2.start();
	}

}
