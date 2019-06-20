package client;

public class Main {
	
	
	public static void main(String[] args) {
		DecisionMaker decisionMaker = new DecisionMaker();
				
		// Normal vision + algorithm
		decisionMaker.run();
		
		// Normal vision + robot test
//		decisionMaker.runRobotTest();
		
		// Test vision + algorithm
//		decisionMaker.runVisionTest();
		
	}

}
