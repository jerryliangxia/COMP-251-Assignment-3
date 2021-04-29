import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Vaccines {
	
	public class Country{
		private int ID;
		private int vaccine_threshold;
		private int vaccine_to_receive;
		private ArrayList<Integer> allies_ID;
		private ArrayList<Integer> allies_vaccine; 
		public Country() {
			this.allies_ID = new ArrayList<Integer>();
			this.allies_vaccine = new ArrayList<Integer>();
			this.vaccine_threshold = 0;
			this.vaccine_to_receive = 0;
		}
		public int get_ID() {
			return this.ID;
		}
		public int get_vaccine_threshold() {
			return this.vaccine_threshold;
		}
		public ArrayList<Integer> get_all_allies_ID() {
			return allies_ID;
		}
		public ArrayList<Integer> get_all_allies_vaccine() {
			return allies_vaccine;
		}
		public int get_allies_ID(int index) {
			return allies_ID.get(index);
		}
		public int get_allies_vaccine(int index) {
			return allies_vaccine.get(index);
		}
		public int get_num_allies() {
			return allies_ID.size();
		}
		public int get_vaccines_to_receive() {
			return vaccine_to_receive;
		}
		public void set_allies_ID(int value) {
			allies_ID.add(value);
		}
		public void set_allies_vaccine(int value) {
			allies_vaccine.add(value);
		}
		public void set_ID(int value) {
			this.ID = value;
		}
		public void set_vaccine_threshold(int value) {
			this.vaccine_threshold = value;
		}
		public void set_vaccines_to_receive(int value) {
			this.vaccine_to_receive = value;
		}
		
	}
	
	public int vaccines(Country[] graph){
		//Computing the exceed in vaccines per country.
		// remove the first country:
		int answer = breadthFirstSearch(graph, 0);
		
		// bad method: check all countries even if they didn't change
		// recursion! pass in the countries affected with a private method
		return answer;
	}
	
	private int breadthFirstSearch(Country[] graph, int index) {
		// deactivate the node by getting rid of all it's edges, while doing so recursively call on each node
		// using a breadth first traversal
		int numSurvivors = graph.length-1;// SHOULD BE 1 LESS
		Queue<Integer> q = new LinkedList<>();
		HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
		q.add(graph[0].get_ID());
		hashMap.put(graph[0].get_ID(), graph[0].get_ID());
		while(q.size()!=0) {
			Integer idToCheck = q.remove();
			Country outOfCommission = graph[idToCheck-1];
			for(int i = 0; i < graph[idToCheck-1].get_num_allies(); i++) {
				Country victim = graph[outOfCommission.get_allies_ID(i)-1];
				victim.vaccine_to_receive -= outOfCommission.get_allies_vaccine(i);
				if(victim.vaccine_threshold > victim.vaccine_to_receive
						&&
						hashMap.get(victim.ID)==null) {	// if the node is white
					hashMap.put(victim.ID, victim.ID);
					q.add(victim.get_ID());	// remove repetition of additions?
					numSurvivors--;
				}
			}
		}
		
		return numSurvivors;
	}

	public void test(String filename) throws FileNotFoundException {
		Vaccines hern = new Vaccines();
		Scanner sc = new Scanner(new File(filename));
		int num_countries = sc.nextInt();
		Country[] graph = new Country[num_countries];
		for (int i=0; i<num_countries; i++) {
			graph[i] = hern.new Country(); 
		}
		for (int i=0; i<num_countries; i++) {
			if (!sc.hasNext()) {
                sc.close();
                sc = new Scanner(new File(filename + ".2"));
            }
			int amount_vaccine = sc.nextInt();
			graph[i].set_ID(i+1);
			graph[i].set_vaccine_threshold(amount_vaccine);
			int other_countries = sc.nextInt();
			for (int j =0; j<other_countries; j++) {
				int neighbor = sc.nextInt();
				int vaccine = sc.nextInt();
				graph[neighbor -1].set_allies_ID(i+1);
				graph[neighbor -1].set_allies_vaccine(vaccine);
				graph[i].set_vaccines_to_receive(graph[i].get_vaccines_to_receive() + vaccine);
			}
		}
		sc.close();
		int answer = vaccines(graph);
		System.out.println(answer);
	}

	public static void main(String[] args) throws FileNotFoundException{
		Vaccines vaccines = new Vaccines();
		String toTest = "/Users/jerryxia/eclipse-workspace/COMP251_A3/src/03.in";
//		vaccines.test(args[0]); // run 'javac Vaccines.java' to compile, then run 'java Vaccines testfilename'
		vaccines.test(toTest);
	}

}
