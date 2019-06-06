package vision;
public class TestMain {

	public static void main(String[] args) {
		VisionController test = new VisionController("a.jpg");
		VisionController test1 = new VisionController("b.jpg");
		VisionController test2 = new VisionController("c.jpg");
		Thread th = new Thread(test);
		Thread th1 = new Thread(test1);
		Thread th2 = new Thread(test2);
		th.start();
		th1.start();
		th2.start();

	}

}
