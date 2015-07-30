import java.util.*;

public class Content {
	
	int dimension;
	public CAN network;
	
	void setDimension(int dimen) {
		dimension = dimen;
	}
	
	public Content(CAN net, int dim) {
		// TODO Auto-generated constructor stub
		network = net;
		dimension = dim;
	}

	void insertItem(String title, Vector<Float> itemCord) throws Exception {
		BSPNode ownerNode = null;
		int result = 0;
		Vector<Float> hashCord = new Vector<Float>();
		
		if(itemCord.size() != dimension) {
			System.out.println("Please configure dimensions: Node cordinates mismatch with dimensions of network");
			return;
		}
		else {
			hashCord = hashItem(title, dimension);
			ownerNode = network.nodeTree.searchTreeForOwner(network.nodeTree.root, hashCord);
			
			result = ownerNode.insertItemInNode(title, itemCord);
			if( result == -1) {
				System.out.println("Item already uploaded");
			}
			else {
				System.out.println("Item uploaded: " + title);
			}
		}
	}
	
  void deleteItem(String title, Vector<Float> itemCord) throws Exception {
  	BSPNode ownerNode = null;
  	int result = 0;
		Vector<Float> hashCord = new Vector<Float>();
		
		if(itemCord.size() != dimension) {
			System.out.println("Please configure dimensions: Node cordinates mismatch with dimensions of network");
			return;
		}
		else {
			hashCord = hashItem(title, dimension);
			ownerNode = network.nodeTree.searchTreeForOwner(network.nodeTree.root, hashCord);
	
			result = ownerNode.deleteItemFromNode(title, itemCord);
			if( result == -1) {
				System.out.println("Item not found: " + title);
			}
			else {
				System.out.println("Item deleted: " + title);
			}
		}
	} 
  
  void findItem(String title, Vector<Float> nodeCord) throws Exception {
  	BSPNode requestedNodeOwner;
  	BSPNode hashItemOwner;
  	BSPNode contentOwner;
  	Node interNode;
  	Vector<Float> hashCord = new Vector<Float>();
  	Vector<Vector<Float>> destCord = new Vector<Vector<Float>>();
  	Vector<Node> pathNodes = new Vector<Node>();
  	
  	if(nodeCord.size() != dimension) {
			System.out.println("Please configure dimensions: Node cordinates mismatch with dimensions of network");
			return;
		}
		else {
	  	requestedNodeOwner = network.nodeTree.searchTreeForOwner(network.nodeTree.root, nodeCord);
		hashCord = hashItem(title, dimension);
	  	hashItemOwner = network.nodeTree.searchTreeForOwner(network.nodeTree.root, hashCord);
	  	if(hashItemOwner.virtualNode.contentTable.containsKey(title)) {
			requestedNodeOwner.routePath(hashItemOwner, pathNodes);
	  	}
	  	else {
	  		System.out.println("unable to locate item: " + title);
	  		return;
	  	}
	  	
	  	/* Output */
	  	System.out.print("Path to locate " + title + " : ");
	  	for(int i = 0; i < nodeCord.size(); i++) {
				if(i == (dimension -1)) {
					System.out.print(nodeCord.get(i) + " => ");
				}
				else {
					System.out.print(nodeCord.get(i) + ", ");
				}
			}
	  	for(int j = pathNodes.size(); j > 0; --j) {
	  		interNode = pathNodes.get(j - 1);
	  		for(int i = 0; i < interNode.locationCoordinates.size(); i++) {
	  			if(i == (dimension -1)) {
	  				System.out.print(interNode.locationCoordinates.get(i) + " => ");
	  			}
	  			else {
	  				System.out.print(interNode.locationCoordinates.get(i) + ", ");
	  			}
	  		}
	  	}
		destCord = hashItemOwner.virtualNode.contentTable.get(title);
		System.out.print(" File exists at: ");
		for(int j = 0; j < destCord.size(); ++j) {
			nodeCord = destCord.get(j);
			System.out.print("[");
		  	for(int i = 0; i < nodeCord.size(); i++) {
				if(i == (dimension -1)) {
					if(j == (destCord.size() - 1)) {
	  					System.out.println(nodeCord.get(i));
	  				}
	  				else {
						System.out.print(nodeCord.get(i) + "] ");
					}
				}
				else {
					System.out.print(nodeCord.get(i) + ", ");
				}
			}
	
		}
	}
  }
  
  /* This function generates d-dimensional coordinates for content by hashing. */
  // TODO: need to modify dimensions
	@SuppressWarnings("unchecked")
	public Vector<Float> hashItem(String title, int dimensions) throws Exception {
		int numChunks = 0;
		int track = 0;
		int i, j;
		Vector<Vector<Byte>> chunks = new Vector<Vector<Byte>>();
		
		byte[] str = title.getBytes("UTF8");
		numChunks = (int) Math.ceil(str.length / dimensions);
		if((str.length % 2) != 0) {
			numChunks++;
		}
		
		for(i = 1; i <= numChunks; ++i) {
			Vector<Byte> newChunk = new Vector<Byte>();
			for(j = 0; j < dimensions; ++j) {
				if(track < str.length) {
					newChunk.add(str[track++]);	
				}
				else { /* Padding bytes */
					newChunk.add((byte)0);
				}
			}
			if(i%2 == 0) {
				// even numbered chunk 
				chunks.add(newChunk);
			}
			else {
				// odd numbered chunk 
				chunks.add(reverseCompChunk(newChunk));
			}	
		}
	
		/* Exclusive OR of all chunks to get coordinates */
		Vector<Byte> c1 = new Vector<Byte>();
		Vector<Byte> c2 = new Vector<Byte>();
		Vector<Byte> res = new Vector<Byte>();
		Vector<Float> cord = new Vector<Float>();
		
		c1 = chunks.get(0);
		c2 = chunks.get(1);
		for(i = 0; i < dimensions; ++i) {
			res.add((byte)(c2.get(i) ^ c1.get(i)));
		}
		for(j = 2; j < numChunks; ++j) {
				c1.clear();
				c1 = chunks.get(j);
				c2.clear();
				c2 = (Vector<Byte>)res.clone();
				res.clear();
				for(i = 0; i < dimensions; ++i) {
					res.add((byte)(c2.get(i) ^ c1.get(i)));
				}
		}
		
		/* Normalize coordinates b/w real number 0 & 1 */
		for(i = 0; i < dimensions; ++i) {
			cord.add(((float)(res.get(i) & 0xff) / 256));
		}
		return cord;
	}

	/* This function reverse the complete chunk */
	Vector<Byte> reverseCompChunk(Vector<Byte> chunk) {
		Vector<Byte> newChunk = new Vector<Byte>();
		int numBytes = chunk.size();
		
		for(int i = 0;i < numBytes; ++i) {
			newChunk.add(reverseByte(chunk.lastElement()));
			chunk.remove(chunk.size()-1);
		}
		return newChunk;
	}
	
	/* This function reverse the whole byte */
	byte reverseByte(byte str){
		byte revstr = 0;
		for(int i=0; i<8; i++) {
			revstr <<= 1;
			revstr |= (str & 1);
			str >>= 1;
		}
		return revstr;
	}
}
