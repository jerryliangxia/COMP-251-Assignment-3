import java.util.*;
import java.io.File;

public class FordFulkerson {

	public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph){
		ArrayList<Integer> path = new ArrayList<Integer>();
		/* YOUR CODE GOES HERE*/
		// true for now
		String[][] table = createTable(graph);	// new initialization method
		//start at node 0		
		path.add(source);
		dfsVisit(source, table, graph, path, destination);
		if(path.size()==0) return null;
		else return path;
	}
	
	private static boolean dfsVisit(int nodeToTraverse, String[][] table, WGraph graph, ArrayList<Integer> path, int destination) {
		// get the node and get it's edge
		String[] edgesToTraverse = table[graph.getNbNodes()][nodeToTraverse].split(",");
		if(edgesToTraverse.length==0) return false;
		boolean found = false;
		for(int i = 0; i < edgesToTraverse.length; i++) {
			String node = edgesToTraverse[i];
			int nextNode = Integer.valueOf(node);
			
			if(checkIfValid(graph, table, edgesToTraverse, path, nextNode, nodeToTraverse, i)) path.add(nextNode);
			else continue;
			
			if(nextNode == destination) {	// if you found the length
				return true;
			}
			
			found = dfsVisit(nextNode, table, graph, path, destination);
			if(found == false &&
					i == edgesToTraverse.length-1) {
				path.remove(path.size()-1);
				return false;
			}
			else if(found == true) break;
		}
		if(found == false) path.remove(path.size()-1);
		return found;
	}
	
	private static boolean checkIfValid(WGraph graph, String[][] table, String[] edgesToTraverse, ArrayList<Integer> path, int nodeToTraverse, int node, int index) {
		if(path.toString().contains(", "+nodeToTraverse+"]") || path.toString().contains(", "+nodeToTraverse+",") 
				|| path.toString().contains("[ "+nodeToTraverse+"]") || path.toString().contains("["+nodeToTraverse+",")) {
			return false;
		}
		if(table[node][nodeToTraverse].equals("0")) return false;
		// newly added
		// checks if it's the max capacity (by seeing if a flag is there
		if(table[node][nodeToTraverse].equals("2147483647")) return false;
		return true;
	}
	

	
	private static String[][] createTable(WGraph graph) {
		String[][] table = new String[graph.getNbNodes()+1][graph.getNbNodes()];
		for(Edge e : graph.getEdges()) {
			int n1 = e.nodes[0];
			int n2 = e.nodes[1];
			table[n1][n2] = e.weight.toString();	// all forward edges made
			if(table[graph.getNbNodes()][n1]==null) table[graph.getNbNodes()][n1]="";
			table[graph.getNbNodes()][n1] += n2+",";
		}
		return table;
	}

	public static String fordfulkerson(WGraph graph){
		String answer="";
		int maxFlow = 0;
		
		/* YOUR CODE GOES HERE		*/
		// create an arraylist of integers and
		WGraph residualGraph = new WGraph(graph);	// create a new graph to go through - initially is an exact copy
		WGraph returnGraph = new WGraph(graph);	// graph we change and send off as our final result

		for(Edge e : returnGraph.getEdges()) {
			int n1 = e.nodes[0];
			int n2 = e.nodes[1];
			returnGraph.setEdge(n1, n2, 0);	// set all the flow to 0 in returnGraph
		}
		
		int size = residualGraph.getEdges().size();
		for(int i = 0; i < size; i++) {
			Edge e1 = residualGraph.getEdges().get(i);
			int n1 = e1.nodes[0];
			int n2 = e1.nodes[1];
			if(residualGraph.getEdge(n2, n1)==null) {	// if the edge does not exist
				// create a new edge - default is the bottleneck
				residualGraph.addEdge(new Edge(n2, n1, 0));	// in this case it can only be backward! so just add the bottleneck
			}
		}
		
		while(pathDFS(residualGraph.getSource(), residualGraph.getDestination(), residualGraph)!=null) {		
			ArrayList<Integer> list = pathDFS(residualGraph.getSource(), residualGraph.getDestination(), residualGraph);
			int min = findMin(list, residualGraph);	// min becomes the bottleneck
			augmentOriginalGraph(returnGraph, min, list);
			augmentResidualGraph(returnGraph, residualGraph, min, list);
		}
		graph = returnGraph;
		for(Edge e : graph.getEdges()) {
			if(e.nodes[1]==graph.getDestination()) {
				maxFlow+=e.weight;
			}
		}

		answer += maxFlow + "\n" + graph.toString();	
		return answer;
	}
	
	private static void augmentOriginalGraph(WGraph returnGraph, int bottleneck, ArrayList<Integer> list) {
		for(int i = 0; i < list.size()-1; i++) {
			int n1 = list.get(i);
			int n2 = list.get(i+1);	// get the edge's coordinates
			boolean isForward = returnGraph.getEdge(n1, n2) != null;
			if(isForward) {
				int curWeight = returnGraph.getEdge(n1, n2).weight;
				returnGraph.setEdge(n1, n2, curWeight+bottleneck);

			}
			else {
				int curWeight = returnGraph.getEdge(n2, n1).weight;
				returnGraph.setEdge(n2, n1, curWeight-bottleneck);
			}
		}
	}
	
	private static void augmentResidualGraph(WGraph returnGraph, WGraph residualGraph, int bottleneck, ArrayList<Integer> list) {
		for(int i = 0; i < list.size()-1; i++) {
			int n1 = list.get(i);
			int n2 = list.get(i+1);
			// could be a backward or a forward edge, test with returnGraph (as returnGraph never has any edges added)
			boolean isForward = returnGraph.getEdge(n1, n2) != null;
			// check if null (in the case it's a backward edge (have to do this anyway)
			if(residualGraph.getEdge(n2, n1)==null && isForward) {	// if the edge does not exist
				// create a new edge - default is the bottleneck
				residualGraph.addEdge(new Edge(n2, n1, bottleneck));	// in this case it can only be backward! so just add the bottleneck
			} else {	// otherwise, set the bottleneck in the existing backward edge!
				int curWeight = residualGraph.getEdge(n2, n1).weight;
				int result = curWeight + bottleneck;
				residualGraph.setEdge(n2, n1, result);
			}

			if(isForward) {	// if it's a forward edge
				// the residualGraph already HAS the values inside, just alter them from here (bw - bottleneck (init),
				// forward - the capacity (init)!
				int curWeight = residualGraph.getEdge(n1, n2).weight;
				int result = curWeight - bottleneck;
				// newly added
				// max cap becomes a flag instead of a number (in this case, max integer value
				if(result==0) residualGraph.setEdge(n1, n2, Integer.MAX_VALUE);
				else residualGraph.setEdge(n1, n2, result);
			}
			else {	// if it's a backward edge
				int curWeight = residualGraph.getEdge(n1, n2).weight;
				int result = curWeight - bottleneck;
				residualGraph.setEdge(n1, n2, result);
			}
		}

	}

	private static int findMin(ArrayList<Integer> list, WGraph residualGraph) {
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < list.size()-1; i++) {
			int n1 = list.get(i);
			int n2 = list.get(i+1);
			int weight = residualGraph.getEdge(n1, n2).weight;
			min = Math.min(min, weight);
		}	// get the minimum
		return min;
	}
	


	public static void main(String[] args){
		String file = args[0];
		File f = new File(file);
		WGraph g = new WGraph(file);
		 
		// new code
//		String file = "/Users/jerryxia/eclipse-workspace/COMP251_A3/src/ff2.txt";
//		WGraph g = new WGraph(file);
		// new code
		
	    System.out.println(fordfulkerson(g));
	 }
}

