import java.util.*;

public class CAN {

  int dimensions;
	public BSPTree nodeTree;
	
	public CAN(int dimensions, BSPTree bSPTree) {
		this.dimensions = dimensions;
		nodeTree = bSPTree;
		nodeTree.setDimension(dimensions);
	}
	
  void setDimension(int dim) {
		dimensions = dim;
		nodeTree.setDimension(dim);
	}
	
	void addNode(Vector<Float> cordinates) {
		int result = 0;
		if(cordinates.size() != dimensions) {
			System.out.println("Please configure dimensions: Node cordinates mismatch with dimensions of network");
			return;
		}
		else {
			/* Create a new overlay network node */
			Node newnode = new Node(cordinates);
			
			/* Insert this node into BSPTree */
			result = nodeTree.insertNodeBSPTree(newnode);
			if( result == -1) {
				System.out.print("Fail to add Node: ");
			}
			else {
				System.out.print("Successfully added node: ");
			}
			newnode.displayCordinates();
		}
	}
	
	void removeNode(Vector<Float> nodeCord) {
		int result = 0;
		
		if(nodeCord.size() != dimensions) {
			System.out.println("Please configure dimensions: Node cordinates mismatch with dimensions of network");
			return;
		}
		else {
			result = nodeTree.deleteNodeBSPTree(nodeCord);
			if( result == -1) {
				System.out.print("Fail to remove Node: ");
			}
			else {
				System.out.print("Succesfully deleted Node: ");
			}
			
			for(int i = 0; i < nodeCord.size(); i++) {
				if(i == (dimensions - 1)) {
					System.out.println(nodeCord.get(i));
				}
				else {
					System.out.print(nodeCord.get(i) + ",");
				}
			}
		}
	}
}
