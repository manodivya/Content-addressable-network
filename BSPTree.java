import java.util.*;


public class BSPTree {

	/* The root of the tree */
	public BSPNode root;
	int dimension;
	
	float levelSplit = 1.0f;
	
	void setDimension(int dimen) {
		dimension = dimen;
	}
	
	int deleteNodeBSPTree(Vector<Float> nodeCord) {
		BSPNode deleteNode, siblingNode;
		@SuppressWarnings("unused")
		BSPNode mergedNode;
		deleteNode = searchTreeForOwner(root, nodeCord);
		if( deleteNode != null) {
			siblingNode = searchSibling(deleteNode);
			if( siblingNode == deleteNode) {
				root = null; 
				return 1;
			}
			deleteNodeFromAllTables(deleteNode, root);
			if(siblingNode.virtualNode != null) {
					mergedNode = mergeNodes(deleteNode, siblingNode);
					deleteNode = null;
			}
			else {
				siblingNode = searchMergedNode(siblingNode);
				siblingNode.leftChild.virtualNode.locationCoordinates.clear();
				for(int i = 0; i < deleteNode.virtualNode.locationCoordinates.size(); ++i) {
					siblingNode.leftChild.virtualNode.locationCoordinates.add(deleteNode.virtualNode.locationCoordinates.get(i));
				}
				deleteNode.virtualNode = siblingNode.leftChild.virtualNode;
				mergedNode = mergeNodes(siblingNode.leftChild, siblingNode.rightChild);				
			}
			updateAllNeighbors(root);
			return 1;
		}
		return -1;
	}
	
	BSPNode searchTreeForSplit(BSPNode pBSPNode,Node newNode) {
		BSPNode splitNode;
		/* Case: Leaf Node */
		if(pBSPNode.leftChild == null && pBSPNode.rightChild == null) {
			if(isOverlap(pBSPNode, newNode) == 1) {
				return pBSPNode;
			}
			else {
				return null;
			}
		}
		
		/* Recursively check at left subtree */
		splitNode = searchTreeForSplit(pBSPNode.leftChild, newNode);
		if(splitNode != null) {
			return splitNode;
		}
		
		/* Recursively check at right subtree */
		splitNode = searchTreeForSplit(pBSPNode.rightChild, newNode);
		if(splitNode != null) {
			return splitNode;
		}
		
		/* Case: no node found for partition at this level */
		return null;
	}
	
	/* Search owner node for these coordinates */
	BSPNode searchTreeForOwner(BSPNode pBSPNode, Vector<Float> cord) {
		int match = 0;
		float firstStart, firstEnd;
		BSPNode splitNode = null;
		
		if( pBSPNode == null) {
			return null;
		}
		/* Leaf Node*/
		if((pBSPNode.leftChild == null) && (pBSPNode.rightChild == null)) {
			for(int i = 0; i < dimension; ++i) {
				firstStart = pBSPNode.zoneStartCoordinates.get(i);
				firstEnd = pBSPNode.zoneEndCoordinates.get(i);
				if(firstStart <= cord.get(i) && cord.get(i) <= firstEnd) {
					match++;
				}
			}
			if( match == dimension) {
				return pBSPNode;
			}
		}
		
		splitNode = searchTreeForOwner(pBSPNode.leftChild, cord);
		if(splitNode != null)
			return splitNode;
		
		splitNode = searchTreeForOwner(pBSPNode.rightChild, cord);
		if(splitNode != null)
			return splitNode;
		
		/* owner not found */
		return null;
	}
	
	/* check whether nodes overlap zone */
	int isOverlap(BSPNode pBSPNode, Node newNode){
		float firstStart, firstEnd;
		int match = 0;
		for(int i = 0; i < pBSPNode.zoneStartCoordinates.size(); ++i) {
				
				firstStart = pBSPNode.zoneStartCoordinates.get(i);
				firstEnd = pBSPNode.zoneEndCoordinates.get(i);
				if(firstStart <= newNode.locationCoordinates.get(i) && newNode.locationCoordinates.get(i) <= firstEnd) {
					match++;
				}
		}
		if( match == dimension) {
			return 1;
		}
		return 0;
	}
	
	int checkSplit(BSPNode pBSPNode, Node newNode) {
		for(int i = 0; i < pBSPNode.zoneStartCoordinates.size(); ++i) {
			if((pBSPNode.zoneEndCoordinates.get(i) - pBSPNode.zoneStartCoordinates.get(i)) == pBSPNode.levelSplit) {
				return 1;
			}
		}
		return 0;
	}
	
	/* check whether split is possible or not */
	int isSplit(BSPNode pBSPNode, Node newNode){
		
		while(checkSplit(pBSPNode, newNode) != 1) {
			pBSPNode.levelSplit /= 2;
		}
		
		float firstStart, firstEnd;
		float secondStart, secondEnd;
		for(int i = 0; i < pBSPNode.zoneStartCoordinates.size(); ++i) {
			if((pBSPNode.zoneEndCoordinates.get(i) - pBSPNode.zoneStartCoordinates.get(i)) == pBSPNode.levelSplit) {
				firstStart = pBSPNode.zoneStartCoordinates.get(i);
				firstEnd = firstStart + ((pBSPNode.zoneEndCoordinates.get(i) - firstStart) / 2);
				secondStart = firstEnd;
				secondEnd = pBSPNode.zoneEndCoordinates.get(i);
				if((firstStart <= pBSPNode.virtualNode.locationCoordinates.get(i) && 
					pBSPNode.virtualNode.locationCoordinates.get(i) < firstEnd && 
					secondStart <= newNode.locationCoordinates.get(i) && 
					newNode.locationCoordinates.get(i) < secondEnd) || 
					(firstStart <= newNode.locationCoordinates.get(i) && 
					newNode.locationCoordinates.get(i) < firstEnd && 
					secondStart <= pBSPNode.virtualNode.locationCoordinates.get(i) && 
					pBSPNode.virtualNode.locationCoordinates.get(i) < secondEnd)) {
						return i;
					}
			}
		}
		return -1;
	}
	
	void isNeighbor(BSPNode pBSPNode, BSPNode neighborNode) {
		int overlap = 0;
		float currentNodeStart, currentNodeEnd;
		float neighborNodeStart, neighborNodeEnd;
		boolean adjacent = false;
		for(int i = 0; i < dimension; ++i) {
			currentNodeStart = pBSPNode.zoneStartCoordinates.get(i);
			currentNodeEnd = pBSPNode.zoneEndCoordinates.get(i);
			neighborNodeStart = neighborNode.zoneStartCoordinates.get(i);
			neighborNodeEnd = neighborNode.zoneEndCoordinates.get(i);
			if(((neighborNodeStart > currentNodeStart) && (neighborNodeStart < currentNodeEnd)) ||
					((neighborNodeEnd > currentNodeStart) && (neighborNodeEnd < currentNodeEnd)) ||
					((currentNodeStart > neighborNodeStart) && (currentNodeStart < neighborNodeEnd)) ||
					((currentNodeEnd > neighborNodeStart) && (currentNodeEnd < neighborNodeEnd)) ||
					((currentNodeStart == neighborNodeStart) && (currentNodeEnd == neighborNodeEnd))) {
						overlap++;
					}
			else {
				if((currentNodeEnd == neighborNodeStart)
						|| (currentNodeStart == neighborNodeEnd)){
					adjacent = true;
				}
			}
		}
		if((overlap == (dimension - 1)) && adjacent == true) {
			/* pBSPNode gets a neighbor */
			if((pBSPNode.virtualNode.NeighborTable.indexOf(neighborNode.virtualNode) == -1) && (neighborNode.virtualNode != null)) {
				pBSPNode.virtualNode.NeighborTable.add(neighborNode.virtualNode);
			}
		}
		else {
			if(pBSPNode.virtualNode.NeighborTable.contains(neighborNode.virtualNode)) {
				pBSPNode.virtualNode.NeighborTable.remove(neighborNode.virtualNode);
				neighborNode.deleteNeighbor(pBSPNode);
			}
		}
	}
	
	void findNeighbors(BSPNode pBSPNode, BSPNode neighborNode) {
		if(neighborNode == null) {
			return;
		}
		
		if(neighborNode.leftChild == null && neighborNode.rightChild == null) {
			/* if pBSPNode is neighbor of neighborNode, then insert this 
			 * neighbor node to its Routing table */
			isNeighbor(pBSPNode, neighborNode);
		}
		findNeighbors(pBSPNode, neighborNode.leftChild);
		findNeighbors(pBSPNode, neighborNode.rightChild);
	}
	
	BSPNode findBSPNode(BSPNode searchNode, Node node) {
		BSPNode tempNode;
		if(searchNode == null) {
			return null;
		}
		
		if(searchNode.leftChild == null && searchNode.rightChild == null) {
			if(searchNode.virtualNode == node) {
				return searchNode;
			}
		}
		
		tempNode = findBSPNode(searchNode.leftChild, node);
		if(tempNode != null) {
			return tempNode;
		}
		
		tempNode = findBSPNode(searchNode.rightChild, node);
		if(tempNode != null) {
			return tempNode;
		}
		
		return null;
	}
	
	int insertNodeBSPTree(Node newNode) {
		float firstStart, firstEnd;
		float secondStart, secondEnd;
		BSPNode tempNode;
		/* Root BSP node */
		if(root == null) {
			BSPNode pBSPNode = new BSPNode();
			for( int i = 0; i < dimension; ++i) {
				pBSPNode.zoneStartCoordinates.add(0.0f);
				pBSPNode.zoneEndCoordinates.add(1.0f);
			}
			pBSPNode.parent = null;
			pBSPNode.levelSplit = 1.0f;
			pBSPNode.virtualNode = newNode;
			pBSPNode.leftChild = null;
			pBSPNode.rightChild = null;
			root = pBSPNode;
		}
		else {
			BSPNode splitNode = searchTreeForSplit(root, newNode);
			/* To get the dimension to split */
			int splitCord = isSplit(splitNode, newNode);
			if(splitNode != null && splitCord != -1) {
				BSPNode leftNode = new BSPNode();
				BSPNode rightNode = new BSPNode();
				leftNode.parent = splitNode;
				rightNode.parent = splitNode;
				leftNode.leftChild = null;
				leftNode.rightChild = null;
				leftNode.levelSplit = splitNode.levelSplit;
				rightNode.leftChild = null;
				rightNode.rightChild = null;
				rightNode.levelSplit = splitNode.levelSplit;
				for(int i = 0; i < dimension; ++i) {
					if( i == splitCord ) {
						firstStart = splitNode.zoneStartCoordinates.get(i);
						firstEnd = firstStart + ((splitNode.zoneEndCoordinates.get(i) - firstStart) / 2);
						secondStart = firstEnd;
						secondEnd = splitNode.zoneEndCoordinates.get(i);
						leftNode.zoneStartCoordinates.add(firstStart);
						leftNode.zoneEndCoordinates.add(firstEnd);
						rightNode.zoneStartCoordinates.add(secondStart);
						rightNode.zoneEndCoordinates.add(secondEnd);
					}
					else {
						leftNode.zoneStartCoordinates.add(splitNode.zoneStartCoordinates.get(i));
						rightNode.zoneStartCoordinates.add(splitNode.zoneStartCoordinates.get(i));
						leftNode.zoneEndCoordinates.add(splitNode.zoneEndCoordinates.get(i));
						rightNode.zoneEndCoordinates.add(splitNode.zoneEndCoordinates.get(i));
					}
				}
				if(isOverlap(leftNode, splitNode.virtualNode) == 1) {
					leftNode.virtualNode = splitNode.virtualNode;
					rightNode.virtualNode = newNode;
				}	
				else {
					rightNode.virtualNode = splitNode.virtualNode;
					leftNode.virtualNode = newNode;	
				}
				/* Split Hash tables depending upon zones*/
				splitContentTable(leftNode, rightNode);
				
				/* Connect the children on tree */
				splitNode.leftChild = leftNode;
				splitNode.rightChild = rightNode;
				
				/* Need to create the routing table for both nodes */
				if( splitNode.virtualNode.NeighborTable.size() > 0) {
					leftNode.virtualNode.NeighborTable = splitNode.virtualNode.NeighborTable;
				}
				splitNode.virtualNode = null;
				
				updateAllNeighbors(root);
		/*		for(int i = 0; i < leftNode.virtualNode.NeighborTable.size(); ++i) {
					tempNode = findBSPNode(root, leftNode.virtualNode.NeighborTable.get(i));
					findNeighbors(tempNode, root);
				}
				findNeighbors(leftNode, root);
				findNeighbors(rightNode, root); */
			}
			else {
				//System.out.println("Recursive Splitting not supported for " + dimension + "D");
				return -1;
			}
		}
		return 1;
	}
	
	void splitContentTable(BSPNode leftNode, BSPNode rightNode) {
		Enumeration<String> titles;
		String title;
		Vector<Vector<Float>> points;
		Vector<Float> cord;
		int match = 0;
		titles = leftNode.virtualNode.contentTable.keys();
		while(titles.hasMoreElements()) {
			title = (String) titles.nextElement();
			points = leftNode.virtualNode.contentTable.get(title);
			for(int j = 0; j < points.size(); ++j) {
				cord = points.get(j);
				for(int i = 0; i < dimension; ++i ) {
					if((leftNode.zoneStartCoordinates.get(i) <= cord.get(i)) && (cord.get(i) < leftNode.zoneEndCoordinates.get(i))) {
						match++;
					}
				}	
				if(match != dimension) {
					rightNode.insertItemInNode(title, cord);
					leftNode.deleteItemFromNode(title, cord);
				}
			}
		}
	}
	
	BSPNode searchSibling(BSPNode node) {
		BSPNode parent;
		parent = node.parent;
		
		if( parent == null) {
			return node;
		}
		
		if(parent.leftChild == node) {
				return parent.rightChild;
		}
		else if(parent.rightChild == node) {
			return parent.leftChild;
		}
		
		return null;
	}
	
	void mergeContentTable(BSPNode deleteNode, BSPNode sibling) {
		Enumeration<String> titles;
		String title;
		Vector<Vector<Float>> points;
		Vector<Float> cord;
		
		titles = deleteNode.virtualNode.contentTable.keys();
		while(titles.hasMoreElements()) {
			title = (String) titles.nextElement();
			points = deleteNode.virtualNode.contentTable.get(title);
			for(int j = 0; j < points.size(); ++j) {
				cord = points.get(j);
				sibling.insertItemInNode(title, cord);
			}
		}
	}
	
	void deleteNodeFromAllTables(BSPNode deletenode, BSPNode node) {
		if(node == null) {
			return;
		}
		
		if((node.leftChild == null) && (node.rightChild == null)) {
			if(node.virtualNode.NeighborTable.contains(deletenode.virtualNode)) {
				node.virtualNode.NeighborTable.remove(deletenode.virtualNode);
			}
		}
		deleteNodeFromAllTables(deletenode, node.leftChild);
		deleteNodeFromAllTables(deletenode, node.rightChild);
	}
	
	BSPNode mergeNodes(BSPNode deleteNode, BSPNode sibling) {
		BSPNode parent;
		parent = deleteNode.parent;
		
		mergeContentTable(deleteNode, sibling);
		
		parent.virtualNode = sibling.virtualNode;
		parent.leftChild = null;
		parent.rightChild = null;
	
		return parent;
	}
	
	BSPNode searchMergedNode(BSPNode node) {
		BSPNode mergeNode;
		if(node == null) {
			return null;
		}
		
		if((node.leftChild.virtualNode != null) && (node.rightChild.virtualNode != null)) {
			return node;
		}
		
		mergeNode = searchMergedNode(node.leftChild);
		if( mergeNode != null) {
			return mergeNode;
		}
		
		mergeNode = searchMergedNode(node.rightChild);
		if( mergeNode != null) {
			return mergeNode;
		}
		
		return null;
	}
	
	void updateAllNodes(BSPNode node) {
		if(node == null) {
			return;
		}
		
		if(node.virtualNode != null) {
			node.virtualNode.visited = false;
			node.virtualNode.parent = null;
		}
		
		updateAllNodes(node.leftChild);
		updateAllNodes(node.rightChild);	
	}

	void updateAllNeighbors(BSPNode node) {
		if(node == null) {
			return;
		}
		
		if(node.virtualNode != null) {
			findNeighbors(node, root);
		}
		
		updateAllNeighbors(node.leftChild);
		updateAllNeighbors(node.rightChild);	
	}
	
	
	
}
