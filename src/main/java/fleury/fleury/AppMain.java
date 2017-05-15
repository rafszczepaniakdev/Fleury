package fleury.fleury;

import java.awt.Color;
import java.awt.Dimension;
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

import fleury.graph.Edge;
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

	private boolean addNode;
	private boolean removeNode;
	private boolean moveNode;
	private boolean addEdge;
	private boolean removeEdge;

	private Node edgeStartNode;
	private Node movingNode;
	private int movingNodeOldX;
	private int movingNodeOldY;

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
		addNode = false;
		removeNode = false;
		moveNode = false;
		addEdge = false;
		removeEdge = false;
		graph = new HashMap<Node, List<Edge>>();
		nodeNr = 0;
		edgeStartNode = null;
		movingNode = null;
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

		drawEdges();
		mouseMoved();
		for (Node node : graph.keySet()) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(node.getX(), node.getY(), 30, 30);
			g.setColor(Color.WHITE);
			g.drawString(String.valueOf(node.getNumber()), node.getX() + 12, node.getY() + 18);

		}
	}

	private void drawEdges() {
		g.setColor(Color.BLACK);
		for (List<Edge> edges : graph.values()) {
			for (Edge edge : edges) {
				g.drawLine(edge.getFrom().getX() + 12, edge.getFrom().getY() + 18, edge.getTo().getX() + 12,
						edge.getTo().getY() + 18);
			}
		}
		// for(Map.Entry<Node, List<Edge>> entry: graph.k)
	}

	@Override
	public void keyPressed(KeyEvent ev) {
		switch (ev.getKeyCode()) {
		case KeyEvent.VK_A:
			clearFunctions();
			addNode = true;
			break;
		case KeyEvent.VK_D:
			clearFunctions();
			removeNode = true;
			break;
		case KeyEvent.VK_E:
			clearFunctions();
			addEdge = true;
			break;
		case KeyEvent.VK_M:
			clearFunctions();
			moveNode = true;
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
		addNode = false;
		removeNode = false;
		moveNode = false;
		addEdge = false;
		removeEdge = false;
		edgeStartNode = null;
	}

	@Override
	public void mouseClicked(MouseEvent ev) {
		int mx = ev.getX();
		int my = ev.getY();

		if (addNode) {
			addNode(mx, my);
		}
		if (addEdge) {
			if (edgeStartNode == null) {
				edgeStartNode = selectNode(mx, my);
			} else {
				Node edgeEndNode = selectNode(mx, my);
				if (edgeEndNode != null && !edgeStartNode.equals(edgeEndNode)
						&& !edgeExist(edgeStartNode, edgeEndNode)) {
					addEdge(edgeStartNode, edgeEndNode);
					edgeStartNode = null;
				}
			}
		}
		if (removeNode) {
			Node node = selectNode(mx, my);
			removeNode(node);
		}

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
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		movingNode = null;
	}

	public void mouseMoved() {
		int mx = MouseInfo.getPointerInfo().getLocation().x;
		int my = MouseInfo.getPointerInfo().getLocation().y;

		if (addNode) {
			g.setColor(Color.BLUE);
			g.fillRect(mx - 20, my - 40, 30, 30);
		}

	}

	private void addNode(int mx, int my) {
		if (isCreatePossible(mx, my)) {
			graph.put(new Node(nodeNr++, mx - 17, my - 14), new ArrayList<Edge>());
		}
	}

	private void removeNode(Node node) {
		for (List<Edge> edges : graph.values()) {
			edges.removeIf(edge -> edge.getFrom().equals(node) || edge.getTo().equals(node));
		}
		graph.remove(node);
	}

	private void addEdge(Node start, Node end) {
		List<Edge> startEdges = graph.get(start);

		Edge edge = new Edge(start, end);
		startEdges.add(edge);
		graph.put(start, startEdges);
	}
	
	private void removeEdge(int mx, int my){
		
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

		if (moveNode) {
			if (movingNode != null) {
				movingNode.setX(mx - 17);
				movingNode.setY(my - 14);
			} else {
				movingNode = selectNode(mx - 17, my - 14);
				if (movingNode != null) {
					movingNodeOldX = movingNode.getX();
					movingNodeOldY = movingNode.getY();
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
