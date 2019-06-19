package vision;

public class VisionMain {

	public static void main(String[] args) {
		VisionController visionController = new VisionController(0);
		
		while(true) {
			visionController.getSnapShot();
		}
	}

}
