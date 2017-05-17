package fleury.graph;

import java.awt.Color;
import java.awt.Graphics;

public class Node {
	private int number;
	private int x;
	private int y;
	private boolean selected;
	
	public Node(int number, int x, int y) {
		super();
		this.number = number;
		this.x = x;
		this.y = y;
		this.selected=false;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void draw(Graphics g){
		if(!selected)
			g.setColor(Color.DARK_GRAY);
		else
			g.setColor(Color.RED);
		g.fillRect(x, y, 30, 30);
		g.setColor(Color.WHITE);
		g.drawString(String.valueOf(number), x + 12, y + 18);
	}
	
}
