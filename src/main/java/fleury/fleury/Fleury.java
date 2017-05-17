package fleury.fleury;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fleury.graph.Edge;
import fleury.graph.Node;

public class Fleury {
	List<Integer> output;
//	int[] output = new int[10];
	int outInd;
	int[][] neibh;
	int n;
	int indV;
	int[] vertex;
	
	public List<Integer> start(Map<Node, List<Edge>> graph){
		prepareFleury(graph);
		findBridges();
		neibh[0][0] = 0;
		showNeibh();
		int start = findStartNode();
		fleury(start);
		return output;
	}
	
	private void findBridges(){
		for(int i=0; i<n; i++){
			searchBridge(i, i);
		}
	}
	
	private void showNeibh(){
		for(int i=0; i<n;i++){
			for(int j=0;j<n;j++){
				System.out.print(neibh[i][j]);
			}
			System.out.println();
		}
	}
	
	private int findStartNode(){
		for(int i=0; i<n; i++){
			int counter = 0;
			for(int j=0; j<n;j++){
				if(neibh[i][j]==1 || neibh[i][j]==2){
					counter++;
				}
			}
			if(counter%2==1)
				return i;
		}
		
		return 0;
	}
	
	private void prepareFleury(Map<Node, List<Edge>> graph){
		int nodes = graph.keySet().size();
		output = new ArrayList<Integer>();
//		output = new int[nodes];
		outInd = 0;
		neibh = new int[nodes][nodes];
		for(List<Edge> edges: graph.values()){
			for(Edge edge: edges){
				int from = edge.getFrom().getNumber();
				int to = edge.getTo().getNumber();
//				System.out.println("CREATING: "+from+"-"+to);
				neibh[from][to] = neibh[to][from] = 1;
			}
		}
		n = nodes;
		indV = 1;
		vertex = new int[nodes];
	}
	
	private void fleury(int actual){
		int next;
		output.add(actual);
//		output[outInd++] = actual;
		
		for(next=0; next < n; next++)
			if(neibh[actual][next]!=0)
				break;
		
		if(next!=n){
			for(int i=0;i<n;i++)
				vertex[i]=0;
			indV = 1;
			searchBridge(actual, -1);
			
			if(neibh[actual][next]==2)
				for(int i=next+1;i<n;i++)
					if(neibh[actual][i]==1)
						next=i;
			neibh[actual][next]=neibh[next][actual] = 0;
			fleury(next);
		}
		System.out.println(output);
	}
	
	private int searchBridge(int actual, int parent){
		vertex[actual]=indV;
		int edgeVal = indV++;
		
		for(int i=0; i<n;i++){
			if(neibh[actual][i]!=0 && i!=parent){
				if(vertex[i]==0){
					int tmp = searchBridge(i, actual);
					if(tmp < edgeVal)
						edgeVal=tmp;
				}else if(vertex[i]<edgeVal){
					edgeVal=vertex[i];
				}
			}
		}
		if(parent>-1 && edgeVal == vertex[actual])
			neibh[parent][actual] = neibh[actual][parent] = 2;
		return edgeVal;
	}
}
