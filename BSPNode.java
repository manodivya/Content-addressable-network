import java.util.*;


public class BSPNode {
		
	Vector<Float> zoneStartCoordinates = new Vector<Float>();
	Vector<Float> zoneEndCoordinates = new Vector<Float>();
	BSPNode parent;
	Node virtualNode;
	float levelSplit;
	
	/* Left child */
	BSPNode leftChild = null;
	/* Right child */
	BSPNode rightChild = null;
	
	int insertItemInNode(String title, Vector<Float> cord) {
		Vector<Vector<Float>> nodes;
		nodes = virtualNode.contentTable.get(title);
		if(nodes == null) {
			nodes = new Vector<Vector<Float>>();
		}
		if(nodes.contains(cord)) {
			return -1;
		}
		nodes.add(cord);
		virtualNode.contentTable.put(title, nodes);
		return 1;
	}
	
	int deleteItemFromNode(String title, Vector<Float> itemCord) {
		Vector<Vector<Float>> nodes;
		
		nodes = virtualNode.contentTable.get(title);
		if(nodes.contains(itemCord)) {
			nodes.remove(itemCord);
			if(nodes.size() == 0) {
				virtualNode.contentTable.remove(title);
			}
			return 1;
		}
		else {
			return -1;
		}
	}
	
	void breadthFirstSearch(BSPNode dest) {
		Node front;
		Node child;
	
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(virtualNode);
		virtualNode.visited = true;
		virtualNode.parent = null;
		
		while( queue.peek() != null) {
			front = queue.poll();
			if(front == dest.virtualNode) {
				/* goal reached*/
				return;
			}
			for(int i = 0; i < front.NeighborTable.size(); ++i) {
				child = front.NeighborTable.get(i);
				if(child.visited == false) {
					child.parent = front;
					child.visited = true;
					queue.add(child);
				}
			}
		}
	}
	
	void routePath(BSPNode dest, Vector<Node> path) {
		/* Breadth first search to find routing path */ 
	
		Node pathNodes;
		breadthFirstSearch(dest);
		pathNodes = dest.virtualNode;
		while( pathNodes != null) {
			path.add(pathNodes);
			pathNodes = pathNodes.parent;
		}
	}
	
	void displayOwnerCordinates() {
		virtualNode.displayCordinates();
	}
	
	void deleteNeighbor(BSPNode neighbor) {
		virtualNode.NeighborTable.remove(neighbor);
	}
}
