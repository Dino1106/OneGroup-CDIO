package vision;

public class Main {

	public static void main(String[] args) {
		
		boolean testMode = true;
		VisionController visionController = new VisionController(testMode, 0);
		
		while(true) {
			visionController.getSnapShot();
		}
	}

}
