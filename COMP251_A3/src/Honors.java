import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Honors {
	
	
	public int min_moves(int[][] board) {
		
		// use bfs (kind of like creating a graph) - don't initialize the graph
		// use bfs to create the graph - since there are a lot of nodes, maybe just edit the actual value in the graph itself

		boolean[][] visited = new boolean[board.length][board[0].length];
		int discovery = 0;
		Queue<int[]> q = new LinkedList<>();	// initialize everything to be used
		
		int[] start = {0,0};	// create a starting point with only positions
		q.add(start);	// add it to the queue
		visited[0][0]=true;	// AFTER adding, set visited to true
		while(!q.isEmpty()) {
			int size = q.size();
			for(int i = 0; i < size; i++) {
				int[] toCheck = q.remove();
				
				int iPos = toCheck[0];
				int jPos = toCheck[1];
				int jump = board[iPos][jPos];	// extract all information needed
				
				if(iPos == board.length-1 && jPos ==board[0].length-1) return discovery;
				
				addToQ(iPos, jPos, jump, q, visited, board);
			}
			discovery++;

		}
		
		return -1; // if can't find a path
	}
	

	private void addToQ(int iPos, int jPos, int jump, Queue<int[]> q, boolean[][] visited, int[][] board) {

		try {	// top
			boolean isVisited = visited[iPos-jump][jPos];
			if(!isVisited) {
				int[] toAdd = {iPos-jump,jPos};
				q.add(toAdd);
				visited[iPos-jump][jPos]=true;
			}

		} catch (Exception e) {}
		try {	// bottom
			boolean isVisited = visited[iPos+jump][jPos];
			if(!isVisited) {
				int[] toAdd = {iPos+jump,jPos};
				q.add(toAdd);
				visited[iPos+jump][jPos]=true;
			}

		} catch (Exception e) {}
		try {	// left
			boolean isVisited = visited[iPos][jPos-jump];
			if(!isVisited) {
				int[] toAdd = {iPos,jPos-jump};
				q.add(toAdd);
				visited[iPos][jPos-jump]=true;
			}

		} catch (Exception e) {}
		try {	// right
			boolean isVisited = visited[iPos][jPos+jump];
			if(!isVisited) {
				int[] toAdd = {iPos,jPos+jump};
				q.add(toAdd);
				visited[iPos][jPos+jump]=true;
			}

		} catch (Exception e) {}
		
	}

	public void test(String filename) throws FileNotFoundException{
		Scanner sc = new Scanner(new File(filename));
		int num_rows = sc.nextInt();
		int num_columns = sc.nextInt();
		int [][]board = new int[num_rows][num_columns];
		for (int i=0; i<num_rows; i++) {
			char line[] = sc.next().toCharArray();
			for (int j=0; j<num_columns; j++) {
				board[i][j] = (int)(line[j]-'0');
			}
			
		}
		sc.close();
		int answer = min_moves(board);
		System.out.println(answer);
	}


	public static void main(String[] args) throws FileNotFoundException{
		Honors honors = new Honors();
//		honors.test(args[0]); // run 'javac Honors.java' to compile, then run 'java Honors testfilename'

		// new code - grid 15, grid 19
		String toTest = "/Users/jerryxia/eclipse-workspace/COMP251_A3/src/Grid-17.in";
		honors.test(toTest);
		// new code
	}

}
