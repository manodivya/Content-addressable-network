import java.util.*;

public class Node {

	boolean visited = false;
	Node parent;
	
	Vector<Float> locationCoordinates = new Vector<Float>();
	
	// Neighbor table - 
	Vector<Node> NeighborTable = new Vector<Node>();
	// Hashing table
	Hashtable<String, Vector<Vector<Float>>> contentTable = new Hashtable<String, Vector<Vector<Float>>>();
	
	Node(Vector<Float> cordinates){
		for(int i = 0; i < cordinates.size(); ++i) {
			locationCoordinates.add(cordinates.get(i));
		}
	}
	
	void displayCordinates() {
		int dimension = locationCoordinates.size();
		for(int i = 0; i < dimension; i++) {
			if(i == (dimension -1)) {
				System.out.println(locationCoordinates.get(i));
			}
			else {
				System.out.print(locationCoordinates.get(i) + ",");
			}
		}
	}
	
	
}
