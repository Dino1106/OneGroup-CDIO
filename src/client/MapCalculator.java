package client;

import java.util.List;

import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;

import Interfaces.IRawMap;

public class MapCalculator{
	
	private IRawMap raw_map;
	
	public MapCalculator(IRawMap raw_map) {
		this.raw_map = raw_map;
	}
	
	public MapState getProcessedMap() {
		return null;
		
	}
	
	public List<Ball> calculateBalls() {
		List<Ball> balls = null;
		
		for(Vec3fVector ball: raw_map.getBalls()) {
			Ball b = new Ball();
			b.coordinate.x = (int) ball.get(ball.position()).get(0);
			b.coordinate.y = (int) ball.get(ball.position()).get(1);
			balls.add(b);
		}
		return balls;
	}
}
