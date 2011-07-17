package objects;

public class City {

	// Normalized values
	private double x;
	private double y;

	// Original values
	private double origX;
	private double origY;

	public City() {

	}

	public City(double x, double y, double origX, double origY) {
		this.x = x;
		this.y = y;
		this.origX = origX;
		this.origY = origY;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getOrigX() {
		return origX;
	}

	public double getOrigY() {
		return origY;
	}
}
