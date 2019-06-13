package vision;

public class Main {

	public static void main(String[] args) {
		
		boolean testMode = false;
		
		VisionController test = new VisionController(testMode, 1);
		Thread th = new Thread(test);
		th.start();
	}

}
