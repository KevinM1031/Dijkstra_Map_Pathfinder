import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Nathaniel Webber
 * @author Kyounghan (Kevin) Min
 */
public class Utility {
	
	/**
	 * Calculates the distance between two points using the Haversine formula
	 * 
	 * This code is taken from https://rosettacode.org/wiki/Haversine_formula#Java,
	 * and modified slightly to meet the needs of this project.
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 */
	public static double haversine(double lat1, double lon1, double lat2, double lon2) {
		double R = 6372.8; // Radius of the earth in kilometers
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}
	
	/**
	 * Calculates the shortest path between two
	 * vertices using Dijkstra's algorithm.
	 * 
	 * @param G The graph.
	 * @param u Starting vertex.
	 * @param v Ending vertex.
	 * @return A list of path vertices.
	 */
	public static LinkedList<Integer> Dijkstra(Graph G, int u, int v) {
		G.setMark(u, 0);
		List<Integer> vList = new ArrayList<Integer>();
		List<Integer> delList = new ArrayList<Integer>();
		vList.add(u);
		int i, n = G.getSize()-1, p = u;
		int[] pArr = new int[n+1];
		for(i = 0; i < n+1; i++) 
			pArr[i] = -1;
		Edge E, minE;
		double mark;
		
		// Traversing and marking the entire graph
		while(true) {
			
			// Finding the vertex which will have the smallest marking
			minE = new Edge(-1, Double.MAX_VALUE);
			for(Integer j : vList) {
				E = G.getMinEdge(j);
				mark = G.getMark(j);
				
				if(E == null) {
					delList.add(j);
					continue;
				}
								
				if(mark+E.weight < minE.weight) {
					minE = E;
					p = j;
				}
			}
			
			// End if graph is discontinuous
			if(minE.next == -1) break;
			
			// Add mark to vertex
			G.setMark(minE.next, G.getMark(p)+minE.weight);
			pArr[minE.next] = p;
			vList.add(minE.next);
			
			// Remove vertices with its edges fully traversed
			for(Integer j : delList)
				vList.remove(j);
			delList.clear();
		}
		
		// Reverse-pathing to find the shortest ordered vertex path
		LinkedList<Integer> path = new LinkedList<Integer>();
		path.add(v);
		int j = v;
		
		while(pArr[j] != u) {
			if(pArr[j] == -1) return null;
			path.addFirst(pArr[j]);
			j = pArr[j];
		}
		
		path.addFirst(u);
		return path;
	}
}

/**
 * Implements mouse input listening function.
 * 
 * @author Kyounghan (Kevin) Min
 */
class MouseInput extends StreetMap implements MouseListener {
	
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) mouse = "LC";
		else if(e.getButton() == MouseEvent.BUTTON3) mouse = "RC";
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	
}
