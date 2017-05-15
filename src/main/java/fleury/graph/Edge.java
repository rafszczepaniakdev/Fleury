package fleury.graph;

public class Edge{
	private Node from;
	private Node to;
	
	public Edge(Node from, Node to) {
		super();
		this.from = from;
		this.to = to;
	}
	
	public Node getFrom() {
		return from;
	}



	public void setFrom(Node from) {
		this.from = from;
	}



	public Node getTo() {
		return to;
	}



	public void setTo(Node to) {
		this.to = to;
	}



	@Override
	public String toString() {
		return "Edge [from=" + from.getNumber() + ", to=" + to.getNumber() + "]";
	}

	
	
}
