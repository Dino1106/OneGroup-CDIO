package model;

public class Wall {
	
	public Coordinate upper;
	public Coordinate lower;
	
	@Override
	public String toString() {
		return "Wall [upper=" + upper + ", lower=" + lower + "]";
	}
}
