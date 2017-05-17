package fleury.graph;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonParser {
	private JSONParser parser;
	private Object obj;
	
	public JsonParser(){
		parser = new JSONParser();
	}
	
	public Map<Node, List<Edge>> loadGraph(){
		Map<Node, List<Edge>> graph = new HashMap<Node, List<Edge>>();
		
		try {
			obj = parser.parse(new FileReader("src/graph.json"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject json = (JSONObject) obj;
		JSONArray nodes = (JSONArray) json.get("nodes");
		for(Object object:nodes){
			JSONObject jsonObject = (JSONObject) object;
			long number = (long) jsonObject.get("number");
			long x = (long) jsonObject.get("x");
			long y = (long) jsonObject.get("y");
			Node node = new Node((int) number, (int) x, (int) y);
			graph.put(node, new ArrayList<Edge>());
		}
		
		JSONArray edges = (JSONArray) json.get("edges");
		for(Object object:edges){
			JSONObject jsonObject = (JSONObject) object;
			long from = (long) jsonObject.get("from");
			long to = (long) jsonObject.get("to");
			Node nodeFrom = getNode(graph,from);
			Node nodeTo = getNode(graph, to);
			Edge edge = new Edge(nodeFrom, nodeTo);
			
			List<Edge> nodeEdges = graph.get(nodeFrom);
			nodeEdges.add(edge);
			graph.put(nodeFrom, nodeEdges);
		}
		
		
		return graph;
	}
	
	public void saveGraph(Map<Node, List<Edge>> graph){
		JSONObject save = new JSONObject();
		JSONArray nodes = new JSONArray();
		for(Node node: graph.keySet()){
			JSONObject jsonNode = new JSONObject();
			jsonNode.put("number", node.getNumber());
			jsonNode.put("x", node.getX());
			jsonNode.put("y", node.getY());
			nodes.add(jsonNode);
		}
		save.put("nodes", nodes);
		JSONArray edges = new JSONArray();
		for(List<Edge> edgesList: graph.values()){
			for(Edge edge: edgesList){
				JSONObject jsonEdge = new JSONObject();
				jsonEdge.put("from", edge.getFrom().getNumber());
				jsonEdge.put("to", edge.getTo().getNumber());
				edges.add(jsonEdge);
			}
		}
		save.put("edges", edges);
		try (FileWriter file = new FileWriter("src/graph.json")) {
            file.write(save.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private Node getNode(Map<Node, List<Edge>> graph, long num){
		int number = (int) num;
		
		for(Node node: graph.keySet()){
			if(node.getNumber()==number){
				return node;
			}
		}
		
		return null;
	}
}
