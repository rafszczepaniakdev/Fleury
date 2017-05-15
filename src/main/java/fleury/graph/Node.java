package fleury.graph;

public class Node {
	private int number;
	private int x;
	private int y;
	
	public Node(int number, int x, int y) {
		super();
		this.number = number;
		this.x = x;
		this.y = y;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
