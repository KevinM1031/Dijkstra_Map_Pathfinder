import java.util.LinkedList;
import java.util.List;

/**
 * The edge node class for the Graph class below.
 * 
 * @author Kyounghan (Kevin) Min
 */
class Edge {
	protected int next;
	protected double weight;
	
	Edge(int next, double weight) {
		this.next = next;
		this.weight = weight;
	}
}

/**
 * <p>
 * Implementation of an adjacency list graph with 
 * limited functions only to fulfill the task
 * of Djikstra pathfinding algorithm. The edges
 * are undirected.
 * </p>
 * 
 * <p>
 * Note that the integer keys for vertices must be
 * of consecutive nonnegative integers, starting
 * at 0.
 * </p>
 * 
 * <p>
 * The mark on each vertices are set as 0 by default.
 * </p>
 * 
 * @author Kyounghan (Kevin) Min
 */
public class Graph {
	private List<Edge>[] graphList;
	private double[] mark;
	private int n;
	
	/**
	 * Initializes adjacency list graph array.
	 * 
	 * @param n Number of indices.
	 */
	@SuppressWarnings("unchecked")
	Graph(int n) {
		this.n = n;
		graphList = new LinkedList[n];
		mark = new double[n];
		for(int i = 0; i < n; i++) {
			graphList[i] = new LinkedList<Edge>();
			mark[i] = 0;
		}
	}
	
	/**
	 * Adds an undirected edge connecting u and v.
	 * 
	 * @param u First vertex.
	 * @param v Second vertex.
	 * @param weight Weight of edge.
	 */
	public void addEdge(int u, int v, double weight) {
		graphList[u].add(new Edge(v, weight));
		graphList[v].add(new Edge(u, weight));
	}
	
	/**
	 * Returns all edges of vertex u.
	 * 
	 * @param u The vertex.
	 * @return The edges.
	 */
	public List<Edge> getEdges(int u) {
		return graphList[u];
	}
	
	/**
	 * Returns the smallest weight edge of vertex u
	 * that leads to an unmarked vertex (marked as 0).
	 * 
	 * @return The smallest weight edge to an
	 * unmarked vertex.
	 */
	public Edge getMinEdge(int u) {
		Edge min = new Edge(-1, Integer.MAX_VALUE);
		boolean minFound = false;
		for(Edge e : graphList[u])
			if(mark[e.next] == 0.0 && min.weight > e.weight) {
				min = e;
				minFound = true;
			}
		
		if(minFound) return min;
		else return null;
	}
	
	/**
	 * Sets a mark on vertex u.
	 * 
	 * @param u The vertex.
	 * @param m The mark.
	 */
	public void setMark(int u, double m) {
		mark[u] = m;
	}
	
	/**
	 * Reveals the mark on vertex u.
	 * 
	 * @param u The vertex.
	 * @return The mark of vertex u.
	 */
	public double getMark(int u) {
		return mark[u];
	}
	
	/**
	 * Clears marks on all vertices,
	 * setting them to 0.
	 */
	public void clearMarks() {
		for(int i = 0; i < n; i++)
			mark[i] = 0;
	}
	
	/**
	 * Returns the number of vertices in the graph.
	 * 
	 * @return The number of vertices.
	 */
	public int getSize() {
		return n;
	}
	
	public boolean hasUnvisited() {
		for (int i = 0; i < n; i++)
			if (mark[i] == -1)
				return true;
		return false;
	}
	
	
}
