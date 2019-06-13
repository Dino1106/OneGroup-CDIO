package vision;

public class Main {

	public static void main(String[] args) {
		
		boolean testMode = true;
		
		VisionController test = new VisionController(testMode, 1);
		Thread th = new Thread(test);
		th.start();
	}

}
