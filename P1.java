import java.io.BufferedInputStream;

import java.util.Scanner;
import java.util.Vector;

public class P1 {

	public static void main(String args[]) throws Exception {
		
		Scanner stdin = new Scanner(new BufferedInputStream(System.in));
		/* Initialization of Overlay Network and Content handler */
		BSPTree pBSPTree = new BSPTree();
		CAN canNetwork = new CAN(0, pBSPTree);
		Content contentHandler = new Content(canNetwork, 0);
    
		while(stdin.hasNext()) {
			String str = stdin.nextLine();
			
			// Split at # and discard from first # onwards
			str = str.split("#")[0];
			
			// Ignore if no alphabets or numbers are left in the string
			if(!str.matches(".*[a-zA-Z0-9].*")) continue;
			
			/*
			 * Get title name, if there
			 */
			String title = "";
			if (str.split("\"").length > 1) title = str.split("\"")[1]; 
			
			/*
			 * Remove multiple spaces/tabs b/w words and
			 * leading and trailing white spaces
			 */
			str = str.replaceAll("\\t+", "");
			str = str.replaceAll("\\s+", "");
			str = str.trim();
			
			/*
			 * Parse each command separately
			 */
			if (str.indexOf("dimension") != -1) {
				int dimension = 0;
				str = str.split("dimension")[1];
				
				dimension = Integer.parseInt(str);
				canNetwork.setDimension(dimension);
				contentHandler.setDimension(dimension);
			} else if (str.indexOf("addNode") != -1) {
				Vector<Float> v = new Vector<Float>();
				String arr[] = str.split("^addNode\\(|,|\\);");
				
				for(int i=0; i<arr.length; i++) {
					if (!isNumeric(arr[i])) continue;
					v.addElement(Float.parseFloat(arr[i]));
				}
				canNetwork.addNode(v);
			} else if (str.indexOf("removeNode") != -1) {
				Vector<Float> v = new Vector<Float>();
				String arr[] = str.split("^removeNode\\(|,|\\);");
				
				for(int i=0; i<arr.length; i++) {
					if (!isNumeric(arr[i])) continue;
					v.addElement(Float.parseFloat(arr[i]));
				}
				canNetwork.removeNode(v);
			} else if (str.indexOf("insertItem") != -1) {
				Vector<Float> v = new Vector<Float>();
				String arr[] = str.split("insertItem\\(|,|\\);");
				
				for(int i=1; i<arr.length; i++) {
					if (!isNumeric(arr[i])) continue;
					v.addElement(Float.parseFloat(arr[i]));
				}
				contentHandler.insertItem(title, v);
			} else if (str.indexOf("find") != -1) {
				Vector<Float> v = new Vector<Float>();
				String arr[] = str.split("find\\(|,|\\);");
				
				for(int i=1; i<arr.length; i++) {
					if(!isNumeric(arr[i])) continue;
					v.addElement(Float.parseFloat(arr[i]));
				}
				contentHandler.findItem(title, v);
				canNetwork.nodeTree.updateAllNodes(canNetwork.nodeTree.root);
			} else if (str.indexOf("deleteItem") != -1) {
				Vector<Float> v = new Vector<Float>();
				String arr[] = str.split("deleteItem\\(|,|\\);");
				
				for(int i=1; i<arr.length; i++) {
					if(!isNumeric(arr[i])) continue;
					v.addElement(Float.parseFloat(arr[i]));
				}
				contentHandler.deleteItem(title, v);
			} else {
				System.out.println("ERROR: Unexpected string!!");
			}
		}
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}
