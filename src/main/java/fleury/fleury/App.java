package fleury.fleury;

import javax.swing.JFrame;

public class App {
	
	public static void main(String[] args) {
		JFrame window = new JFrame("Fleury");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setContentPane(new AppMain());
        window.setResizable(false);
        window.pack();
        window.setVisible(true);
	}
	
//	public static Map<Node, Set<Integer>> createSimpleGraph(){
//		Map<Node, Set<Integer>> graph = new HashMap<Node, Set<Integer>>();
//		
//		Node node0 = new Node(0);
//		Set<Integer> neibh0 = new HashSet<Integer>();
//		neibh0.add(1);
//		neibh0.add(3);
//		graph.put(node0, neibh0);
//		
//		Node node1 = new Node(1);
//		Set<Integer> neibh1 = new HashSet<Integer>();
//		neibh1.add(0);
//		neibh1.add(3);
//		graph.put(node1, neibh1);
//		
//		Node node2 = new Node(2);
//		Set<Integer> neibh2 = new HashSet<Integer>();
//		neibh2.add(3);
//		neibh2.add(4);
//		graph.put(node2, neibh2);
//		
//		Node node3 = new Node(3);
//		Set<Integer> neibh3 = new HashSet<Integer>();
//		neibh3.add(0);
//		neibh3.add(1);
//		neibh3.add(2);
//		neibh3.add(4);
//		graph.put(node3, neibh3);
//		
//		Node node4 = new Node(4);
//		Set<Integer> neibh4 = new HashSet<Integer>();
//		neibh4.add(2);
//		neibh4.add(3);
//		graph.put(node4, neibh4);
//		
//		return graph;
//	}
//	
//	public static Node findNonZeroLevelNode(Map<Node, Set<Integer>> graph){
//		Node node = null;
//		for(Map.Entry<Node, Set<Integer>> entry: graph.entrySet()){
//			if(entry.getValue().size()%2==0){
//				node = entry.getKey();
//			}
//		}
//		return node!=null ? node : graph.keySet().iterator().next();
//	}
//	
//	public static Set<Edge> prepareEdges(Map<Node, Set<Integer>> graph){
//		Set<Edge> edges = new HashSet<Edge>();
//		
//		for(Map.Entry<Node, Set<Integer>> entry: graph.entrySet()){
//			Set<Integer> neibhs = entry.getValue();
//			for(Integer number: neibhs){
//				boolean exist = false;
//				for(Edge edge: edges){
//					if(number==edge.getFrom().getNumber() && edge.getTo()==entry.getKey()){
//						exist=true;
//					}
//				}
//				if(!exist){
//					edges.add(new Edge(entry.getKey(), findNode(graph, number)));
//				}
//			}
//		}
//		
//		return edges;
//	}
//	
//	public static Node findNode(Map<Node, Set<Integer>> graph, Integer number){
//		for(Node node: graph.keySet()){
//			if(node.getNumber()==number){
//				return node;
//			}
//		}
//		return null;
//	}
//	
//	public static List<Integer> prepareVisited(Set<Node> nodes){
//		List<Integer> visited = new ArrayList<Integer>();
//
//		for(Node node: nodes){
//			visited.add(node.getNumber());
//		}
//		
//		return visited;
//	}
//	
//	public static void fleury(Map<Node, Set<Integer>> graph, Node actual){
//		
//		List<Node> path = new ArrayList<Node>();
//		path.add(actual);
//		if(!graph.get(actual).isEmpty()){
//			Set<Edge> edges = prepareEdges(graph);
//			List<Integer> visited = prepareVisited(graph.keySet());
//			findBridge(graph, visited, actual, null);
//		}
//	}
//	
//	public static boolean isVisited(List<Integer> visited, Integer number){
//		return visited.stream().anyMatch(x->x.equals(number));
//	}
//	
//	public static void findBridge(Map<Node, Set<Integer>> graph, List<Integer> visited, Node actual, Node parent){
//		Set<Integer> neibh = graph.get(actual);
//		neibh.forEach(x->{
//			if((parent!=null && parent!=findNode(graph, x))){
//				if(isVisited(visited, x)){
//					
//				}else{
//					
//				}
//			}
//		});
//		if(parent!=null && )
//	}

}
