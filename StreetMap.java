
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Main class for Project 3 of CSC 172 course.
 * 
 * @author Nathaniel Webber
 * @author Kyounghan (Kevin) Min
 */
public class StreetMap {
	
	public static String mouse = "idle";

	/**
	 * The main driver method.
	 * 
	 * The arguments may be one of the three of the following:
	 * 
	 * <li>
	 * (map_file_path) --show
	 *    --> Shows the map.
	 * </li>
	 * 
	 * <li>
	 * (map_file_path) --directions (start_intersection) (destination_intersection)
	 *    --> Calculates the shortest distance between two points.
	 * </li>
	 * 
	 * <li>
	 * (map_file_path) --show --directions (start_intersection) (destination_intersection)
	 *    --> Shows the map and the shortest distance between two intersections.
	 *        The shortest path is also displayed in the map.
	 * </li>
	 * 
	 * Example args: src/monroe.txt --show --directions i185852 i294475 
	 * 
	 * @param args The arguments in suggested format as described in Project 3
	 *             handout.
	 */
	public static void main(String[] args) {

		// Get data from input text file
		String dataFile = args[0];
		Map<String, Integer> D = new HashMap<String, Integer>(); // key dictionary
		Map<Integer, String> RevD = new HashMap<Integer, String>(); // reversed key dictionary
		List<double[]> V = new ArrayList<double[]>(); // vertex dimension list
		double[] mapBounds = { 0, 0, 0, 0 }; // latitude longitude bounds (minLat, minLon, maxLat, maxLon)
		Graph G = null;
		
		try {
			G = getData(dataFile, D, RevD, V, mapBounds);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Read additional arguments
		int argCount = args.length; // This is assuming that the arguments provided are actually formatted correctly
		switch (argCount) {
		case 2: // If there are only 2 arguments, it should just be --show
			if (args[1].equals("--show")) {
				getGUI(G, V, mapBounds, "Mapping: " + args[0]);
			} break;
			
		case 4: // If there are 4 arguments, it has to be --directions and the two
				// intersections.
			if (args[1].equals("--directions")) {
				LinkedList<Integer> P = Utility.Dijkstra(G, D.get(args[2]), D.get(args[3]));
				if(P == null) {
					System.out.println(args[2] + " and " + args[3] + " are not connected.");
					break;
				}
				printPath(G, P, D, RevD);
				
				double[] v;
				for (Integer i : P) {
					v = V.get(i);
					V.set(i, new double[] { v[0], v[1], 1 });
				}
			} break;
			
		case 5: // If there are 5 arguments, both functions have been called, and we need to
				// figure out which arguments are the intersections. Fortunately there are only
				// 2 possible valid configurations
			if (args[1].equals("--show")) { // In this case the intersections will be indexes 3 and 4
				LinkedList<Integer> P = Utility.Dijkstra(G, D.get(args[3]), D.get(args[4]));
				if(P == null) {
					System.out.println(args[3] + " and " + args[4] + " are not connected.");
					break;
				}
				printPath(G, P, D, RevD);
				
				double[] v;
				for (Integer i : P) {
					v = V.get(i);
					V.set(i, new double[] { v[0], v[1], 1 });
				}
				
				getGUI(G, V, P, mapBounds, "Mapping: " + args[0], "Going from " + args[3] + " to " + args[4]);

				break;

			}
			else if (args[1].equals("--directions")) { // And in this case the intersections will be indexes 2 and 3
				LinkedList<Integer> P = Utility.Dijkstra(G, D.get(args[3]), D.get(args[4]));
				if(P == null) {
					System.out.println(args[3] + " and " + args[4] + " are not connected.");
					break;
				}				printPath(G, P, D, RevD);
				
				double[] v;
				for (Integer i : P) {
					v = V.get(i);
					V.set(i, new double[] { v[0], v[1], 1 });
				}
				
				getGUI(G, V, P, mapBounds, "Mapping: " + args[0], "Going from " + args[3] + " to " + args[4]);

				break;
			}

		}

	}
	
	/**
	 * Prints the directions to the console in the format specified by the handout.
	 * 
	 * @param G The graph.
	 * @param P The list of vertex indexes that were calculated by Dijkstra's
	 *          algorithm.
	 * @param D The key dictionary.
	 * 
	 * @author Nathaniel Webber
	 */
	private static void printPath(Graph G, List<Integer> P, Map<String, Integer> D, Map<Integer, String> RevD) {
		double kiloDistance = G.getMark(P.get(P.size() - 1));
		double mileDistance = kiloDistance * 0.621371192;
		for (int i = 0; i < P.size() - 1; i++)
			System.out.print(RevD.get(P.get(i)) + " -> ");
		System.out.print(P.get(P.size() - 1));
		System.out.println();
		System.out.println("The path is : " + mileDistance + " miles.");
	}
	
	/**
	 * Opens a map GUI, containing the map as an image and
	 * highlighting the shortest path with animations.
	 * 
	 * @param G The map graph.
	 * @param V A list of vertices' coordinates.
	 * @param P A list of vertices which are a part of the shortest path.
	 * @param mapBounds Coordinate maxima and minima of map.
	 * @param title GUI display title.
	 * @param subtitle GUI display subtitle.
	 * 
	 * @author Kyounghan (Kevin) Min
	 */
	private static void getGUI(Graph G, List<double[]> V, List<Integer> P, double[] mapBounds, String title, String subtitle) {
		GUI gui = new GUI(G, V, P, mapBounds);
		gui.setTitle(title);
		gui.setSubtitle(subtitle);
		gui.updateMap(true);
		
		// Getting updates
		int rc = 0, lc = 0, i = 0, j = 0;
		while(true) {
			
			if(i > 0) i++;
			if(i > 25) lc = i = 0;
			
			if(j > 0) j++;
			if(j > 25) rc = j = 0;
			
			// Left click
			if(gui.hasFocus() && mouse.equals("LC")) {
				mouse = "idle";
				lc++;
				i++;
				
				if(lc > 1) {
					gui.zoom(2.0);
					lc = 0;
				}
				gui.updateMap(false);
			}
			
			// Right click
			else if(gui.hasFocus() && mouse.equals("RC")) {
				mouse = "idle";
				rc++;
				j++;
				
				if(rc > 1) {
					gui.zoom(0.5);
					rc = 0;
				}
				gui.updateMap(false);
			}
						
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Opens a map GUI, containing the map as an image.
	 * 
	 * @param G The map graph.
	 * @param V A list of vertices' coordinates.
	 * @param mapBounds Coordinate maxima and minima of map.
	 * @param title GUI display title.
	 * 
	 * @author Kyounghan (Kevin) Min
	 */
	private static void getGUI(Graph G, List<double[]> V, double[] mapBounds, String title) {
		GUI gui = new GUI(G, V, mapBounds);
		gui.setTitle(title);
		gui.setSubtitle("");
		gui.updateMap(true);
		
		// Getting updates
		int rc = 0, lc = 0, i = 0, j = 0;
		while(true) {
			
			if(i > 0) i++;
			if(i > 25) lc = i = 0;
			
			if(j > 0) j++;
			if(j > 25) rc = j = 0;
			
			// Left click
			if(gui.hasFocus() && mouse.equals("LC")) {
				mouse = "idle";
				lc++;
				i++;
				
				if(lc > 1) {
					gui.zoom(2.0);
					lc = 0;
				}
				gui.updateMap(false);
			}
			
			// Right click
			else if(gui.hasFocus() && mouse.equals("RC")) {
				mouse = "idle";
				rc++;
				j++;
				
				if(rc > 1) {
					gui.zoom(0.5);
					rc = 0;
				}
				gui.updateMap(false);
			}
						
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads and takes data from the data file, writing the key dictionary D and
	 * vertex dimension list V. A map graph of the data is also generated and
	 * returned.
	 * 
	 * @param path The path to input text file.
	 * @param D    Key dictionary.
	 * @param RevD Reversed key dictionary.
	 * @param V    Vertex dimension list.
	 * @return A graph representation of the map data.
	 * @throws IOException If input text file path leads to nowhere.
	 * 
	 * @author Nathaniel Webber
	 * @author Kyounghan (Kevin) Min
	 */
	private static Graph getData(String path, Map<String, Integer> D, Map<Integer, String> RevD, 
			List<double[]> V, double[] mapBounds) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(path));

		List<double[]> E = new ArrayList<double[]>(); // temporary edge list
		int i = 0, u, v;
		String line = reader.readLine(), key;
		double lat, lon;
		double minLat = Integer.MAX_VALUE, minLon = Integer.MAX_VALUE;
		double maxLat = Integer.MIN_VALUE, maxLon = Integer.MIN_VALUE;
		double[] dim1, dim2;

		// Iterating through the input file
		while (line != null) {

			// Reading intersection (vertex) data
			if (line.charAt(0) == 'i') {
				line = line.substring(line.indexOf("	") + 1);

				// Add key to dictionary
				key = line.substring(0, line.indexOf("	"));
				line = line.substring(line.indexOf("	") + 1);
				D.put(key, i);
				RevD.put(i, key);
				i++;

				// Add intersection to vertex list
				lat = Double.parseDouble(line.substring(0, line.indexOf("	")));
				lon = Double.parseDouble(line.substring(line.indexOf("	") + 1));
				V.add(new double[] { lat, lon, 0 });

				// Check for max and min coordinates
				if (lat > maxLat)
					maxLat = lat;
				else if (lat < minLat)
					minLat = lat;
				if (lon > maxLon)
					maxLon = lon;
				else if (lon < minLon)
					minLon = lon;

				// Reading road (edge) data
			} else if (line.charAt(0) == 'r') {
				line = line.substring(line.indexOf("	") + 1);

				// Add road to graph
				key = line.substring(0, line.indexOf("	"));
				line = line.substring(line.indexOf("	") + 1);
				u = D.get(line.substring(0, line.indexOf("	")));
				v = D.get(line.substring(line.indexOf("	") + 1));
				dim1 = V.get(u);
				dim2 = V.get(v);
				E.add(new double[] { u, v, Utility.haversine(dim1[0], dim1[1], dim2[0], dim2[1]) });
			}

			line = reader.readLine();

		}

		reader.close();

		// Record max/min latitudes and longitudes
		mapBounds[0] = minLat;
		mapBounds[1] = minLon;
		mapBounds[2] = maxLat;
		mapBounds[3] = maxLon;

		// Using edge list to create graph
		Graph G = new Graph(i);
		for (double[] e : E)
			G.addEdge((int) e[0], (int) e[1], e[2]);

		return G;
	}
}
