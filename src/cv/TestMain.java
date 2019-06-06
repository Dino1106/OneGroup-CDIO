package cv;
public class TestMain {

	public static void main(String[] args) {
		ObjectReader test = new ObjectReader("a.jpg");
		ObjectReader test1 = new ObjectReader("b.jpg");
		ObjectReader test2 = new ObjectReader("c.jpg");
		Thread th = new Thread(test);
		Thread th1 = new Thread(test1);
		Thread th2 = new Thread(test2);
		th.start();
		th1.start();
		th2.start();

	}

}
