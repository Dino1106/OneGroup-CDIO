package vision;

public class Main {

	public static void main(String[] args) {
		VisionController visionController = new VisionController(1);
		
		while(true) {
			visionController.getSnapShot();
		}
	}

}
