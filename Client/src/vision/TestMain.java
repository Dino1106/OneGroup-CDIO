package vision;

public class TestMain {

	public static void main(String[] args) {
		
		boolean testMode = true;
		
		VisionController test = new VisionController(testMode, "e.jpg");
		test.getSnapShot();
		
		//VisionController test1 = new VisionController(testMode, "b.jpg");
		//VisionController test2 = new VisionController(testMode, "d1.jpg");
		
		
	}

}
