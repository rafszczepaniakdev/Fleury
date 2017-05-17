package fleury.app;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import fleury.fleury.Fleury;
import fleury.graph.Edge;
import fleury.graph.JsonParser;
import fleury.graph.Node;

public class AppMain extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -2318489391810394155L;

	public static int WIDTH = 800;
	public static int HEIGHT = 600;

	private Thread thread;
	private boolean running;

	private BufferedImage image;
	private Graphics2D g;

	private int FPS = 60;
	private double averageFPS;

	private JsonParser jsonParser;

	private boolean modifyNode;
	private boolean modifyEdge;
	private boolean moveNode;

	private boolean eulerRoad;
	private boolean eulerGraph;

	private Node edgeStartNode;
	private Node movingNode;
	private int movingNodeOldX;
	private int movingNodeOldY;
	private Fleury fleury;

	private Font font;

	private Map<Node, List<Edge>> graph;
	private int nodeNr;

	public AppMain() {
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
	}

	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void init() {
		jsonParser = new JsonParser();
		moveNode = false;
		modifyEdge = false;
		modifyNode = false;
		eulerRoad = false;
		eulerGraph = false;
		graph = new HashMap<Node, List<Edge>>();
		nodeNr = 0;
		edgeStartNode = null;
		movingNode = null;
		font = new Font("TimesRoman", Font.PLAIN, 15);
		g.setFont(font);
		fleury = new Fleury();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
	}

	public void run() {
		running = true;
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		init();

		long startTime;
		long URDTimeMillis;
		long waitTime;

		long targetTime = 1000 / FPS;
		while (running) {
			int frameCount = 0;
			long totalTime = 0;
			startTime = System.nanoTime();

			gameUpdate();
			gameRender();
			gameDraw();

			URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime - URDTimeMillis;

			try {
				Thread.sleep(waitTime);
			} catch (Exception e) {
			}

			totalTime += System.nanoTime() - startTime;
			frameCount++;
			if (frameCount == FPS) {
				averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
			}
		}
	}

	private synchronized void gameUpdate() {

	}

	private synchronized void gameDraw() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	private synchronized void gameRender() {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 800, 600);

		drawTopPanel();

		drawEdges();
		mouseMoved();
		for (Node node : graph.keySet()) {
			node.draw(g);
		}
	}

	private void drawEdges() {
		g.setStroke(new BasicStroke(3));
		for (List<Edge> edges : graph.values()) {
			for (Edge edge : edges) {
				if (edge.isHighlight())
					g.setColor(Color.BLUE);
				else
					g.setColor(Color.GRAY);
				g.drawLine(edge.getFrom().getX() + 12, edge.getFrom().getY() + 18, edge.getTo().getX() + 12,
						edge.getTo().getY() + 18);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent ev) {
		switch (ev.getKeyCode()) {
		case KeyEvent.VK_E:
			clearFunctions();
			modifyEdge = true;
			break;
		case KeyEvent.VK_M:
			clearFunctions();
			moveNode = true;
			break;
		case KeyEvent.VK_N:
			clearFunctions();
			modifyNode = true;
			break;
		case KeyEvent.VK_L:
			clearFunctions();
			graph = jsonParser.loadGraph();
			nodeNr = graph.keySet().size();
			eulerGraph = isEulerGraph();
			eulerRoad = existEulerRoad();
			break;
		case KeyEvent.VK_S:
			clearFunctions();
			jsonParser.saveGraph(graph);
			break;
		case KeyEvent.VK_ENTER:
			clearFunctions();
			if (eulerGraph || eulerRoad) {
				runFleury();
			}

			break;
		default:
			clearFunctions();

		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	private void clearFunctions() {
		graph.values().forEach(edges ->{
			edges.forEach(edge ->{
				edge.setHighlight(false);
			});
		});
		modifyNode = false;
		modifyEdge = false;

		moveNode = false;
		if(edgeStartNode!=null){
			edgeStartNode.setSelected(false);
			edgeStartNode = null;
		}
		
	}

	private void unselectNodes() {
		graph.keySet().forEach(x -> x.setSelected(false));
	}

	@Override
	public void mouseClicked(MouseEvent ev) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent ev) {
		int mx = ev.getX();
		int my = ev.getY();

		if (my > 60) {
			if (modifyNode && ev.getButton() == 1) {
				addNode(mx, my);
			}
			if (modifyEdge && ev.getButton() == 1) {
				if (edgeStartNode == null) {
					edgeStartNode = selectNode(mx, my);
					if (edgeStartNode != null)
						edgeStartNode.setSelected(true);
				} else {
					Node edgeEndNode = selectNode(mx, my);
					if (edgeEndNode != null && !edgeStartNode.equals(edgeEndNode)
							&& !edgeExist(edgeStartNode, edgeEndNode)) {
						addEdge(edgeStartNode, edgeEndNode);
						edgeStartNode.setSelected(false);
						edgeStartNode = null;
					}
				}
			}
			if (modifyNode && ev.getButton() == 3) {
				Node node = selectNode(mx, my);
				if (node != null)
					removeNode(node);
			}
			if (modifyEdge && ev.getButton() == 3) {
				if (edgeStartNode == null) {
					edgeStartNode = selectNode(mx, my);
					if (edgeStartNode != null)
						edgeStartNode.setSelected(true);
				} else {
					Node edgeEndNode = selectNode(mx, my);
					if (edgeEndNode != null && !edgeStartNode.equals(edgeEndNode)
							&& edgeExist(edgeStartNode, edgeEndNode)) {
						removeEdge(edgeStartNode, edgeEndNode);
						edgeStartNode.setSelected(false);
						edgeStartNode = null;
					}
				}
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(movingNode!=null){
			unselectNodes();
			movingNode = null;
		}
	}

	public void mouseMoved() {
		int mx = MouseInfo.getPointerInfo().getLocation().x;
		int my = MouseInfo.getPointerInfo().getLocation().y;

		if (modifyNode && my > 85) {
			g.setColor(Color.BLUE);
			g.fillRect(mx - 20, my - 40, 30, 30);
		}

	}

	private void addNode(int mx, int my) {
		if (isCreatePossible(mx, my)) {
			graph.put(new Node(nodeNr++, mx - 17, my - 14), new ArrayList<Edge>());
			eulerGraph = isEulerGraph();
			eulerRoad = existEulerRoad();
		}
	}

	private void removeNode(Node node) {
		for (List<Edge> edges : graph.values()) {
			edges.removeIf(edge -> edge.getFrom().equals(node) || edge.getTo().equals(node));
		}
		reNumber(node);
		nodeNr--;
		graph.remove(node);

		eulerGraph = isEulerGraph();
		eulerRoad = existEulerRoad();
	}

	private void addEdge(Node start, Node end) {
		List<Edge> startEdges = graph.get(start);

		Edge edge = new Edge(start, end);
		startEdges.add(edge);
		graph.put(start, startEdges);
		eulerGraph = isEulerGraph();
		eulerRoad = existEulerRoad();
	}

	private void removeEdge(Node from, Node to) {
		for (List<Edge> edges : graph.values()) {
			edges.removeIf(edge -> (edge.getFrom().equals(from) || edge.getTo().equals(from))
					&& (edge.getFrom().equals(to) || edge.getTo().equals(to)));
		}
		eulerGraph = isEulerGraph();
		eulerRoad = existEulerRoad();
	}

	private boolean isCreatePossible(int mx, int my) {
		for (Map.Entry<Node, List<Edge>> entry : graph.entrySet()) {
			Node node = entry.getKey();
			if (Math.abs(node.getX() - (mx - 17)) < 30 && Math.abs(node.getY() - (my - 14)) < 30) {
				return false;
			}
		}
		return true;
	}

	private Node selectNode(int mx, int my) {
		for (Map.Entry<Node, List<Edge>> entry : graph.entrySet()) {
			Node node = entry.getKey();
			if (Math.abs(node.getX() - (mx - 17)) < 30 && Math.abs(node.getY() - (my - 14)) < 30) {
				return node;
			}
		}
		return null;
	}

	private boolean edgeExist(Node start, Node end) {
		for (List<Edge> edges : graph.values()) {
			for (Edge edge : edges) {
				if ((edge.getFrom().equals(start) || edge.getTo().equals(start))
						&& (edge.getFrom().equals(end) || edge.getTo().equals(end))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void mouseDragged(MouseEvent ev) {
		int mx = ev.getX();
		int my = ev.getY();

		if (my > 60) {
			if (moveNode) {
				if (movingNode != null) {
					movingNode.setX(mx - 17);
					movingNode.setY(my - 14);
				} else {
					movingNode = selectNode(mx - 17, my - 14);
					if (movingNode != null) {
						movingNode.setSelected(true);
						movingNodeOldX = movingNode.getX();
						movingNodeOldY = movingNode.getY();
					}
				}
			}
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private void runFleury() {
		List<Integer> numbers = fleury.start(graph);
		for (int i = 0; i < numbers.size() - 1; i++) {
			Edge edge = getEdgeBetween(numbers.get(i), numbers.get(i + 1));
//			System.out.println("Edge:" + edge.getFrom().getNumber() + ":" + edge.getTo().getNumber());
			edge.setHighlight(true);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Edge getEdgeBetween(int from, int to) {
		for (List<Edge> edges : graph.values()) {
			for (Edge edge : edges) {
				if ((edge.getFrom().getNumber() == from && edge.getTo().getNumber() == to)
						|| (edge.getFrom().getNumber() == to && edge.getTo().getNumber() == from)) {
					return edge;
				}
			}
		}
		return null;
	}

	private boolean existEulerRoad() {
		if (graph.isEmpty())
			return false;

		int nonEvenDegree = 0;
		for (Node node : graph.keySet()) {
			if (!isEvenDegreeNode(node)) {
				if (nonEvenDegree < 2 && haveEdges(node)) {
					nonEvenDegree++;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isEulerGraph() {
		if (graph.isEmpty())
			return false;

		for (Node node : graph.keySet()) {
			if (!isEvenDegreeNode(node)) {
				return false;
			}
		}
		return true;
	}

	private boolean haveEdges(Node node) {
		for (List<Edge> edges : graph.values()) {
			for (Edge edge : edges) {
				if ((edge.getFrom().equals(node) || edge.getTo().equals(node))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isEvenDegreeNode(Node node) {
		int edgesCounter = 0;
		for (List<Edge> edges : graph.values()) {
			for (Edge edge : edges) {
				if ((edge.getFrom().equals(node) || edge.getTo().equals(node))) {
					edgesCounter++;
				}
			}
		}
		return edgesCounter % 2 == 0 && edgesCounter > 0;

	}

	private void reNumber(Node node) {
		int number = node.getNumber();
		for (Node reNum : graph.keySet()) {
			if (reNum.getNumber() > number)
				reNum.setNumber(reNum.getNumber() - 1);
		}
	}

	private void drawTopPanel() {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, WIDTH, 50);

		if (eulerRoad)
			g.setColor(Color.GREEN);
		else
			g.setColor(Color.RED);
		g.fillOval(10, 10, 10, 10);
		g.setColor(Color.WHITE);
		g.drawString("Road", 26, 20);

		if (eulerGraph)
			g.setColor(Color.GREEN);
		else
			g.setColor(Color.RED);
		g.fillOval(10, 30, 10, 10);
		g.setColor(Color.WHITE);
		g.drawString("Cicle", 26, 40);

		if (!modifyNode)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.GREEN);
		g.drawString("N - Modify nodes", 100, 20);
		if (!moveNode)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.GREEN);
		g.drawString("M - Move nodes", 100, 40);
		if (!modifyEdge)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.GREEN);
		g.drawString("E - Modify edges", 250, 20);

		g.setColor(Color.WHITE);

		g.drawString("ESC - Cancel", 250, 40);

		g.drawString("LPM - Add", 400, 20);
		g.drawString("PPM - Remove", 400, 40);

		g.drawString("S - Save graph", 550, 20);
		g.drawString("L - Load graph", 550, 40);

	}

}
