import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.ImageIcon;

import java.awt.BasicStroke;
import java.awt.Color;

import javax.swing.JLabel;

/**
 * Opens a graphic user interface window containing the
 * map from StreetMap.java as an interactive image format.
 * 
 * @author Kyounghan Min
 */
@SuppressWarnings("serial")
public class GUI extends JFrame {
	
	// Essential UI componenets
	private JLabel mapContainer;
	
	// Map data containers
	private Graph G;
	private List<double[]> V;
	private List<Integer> P;
	private double[] mapBounds;
	
	// Text containers
	private String title;
	private Font titleFont;
	private String subtitle;
	private Font subtitleFont;
	
	// Display option containers
	private int roadWidth;
	private Color roadColor;
	private int pathWidth;
	private Color pathColor;
	private double zoom;
	private double xF, yF;
	
	/**
	 * Class constructor; opens a window.
	 */
	public GUI(Graph G, List<double[]> V, List<Integer> P, double[] mapBounds) {
		this.P = P;
		initialize(G, V, mapBounds);
	}
	public GUI(Graph G, List<double[]> V, double[] mapBounds) {
		initialize(G, V, mapBounds);
	}
	
	/**
	 * Initializes JFrame and other necessary variables.
	 * 
	 * @param G The map graph.
	 * @param V A list of vertices' coordinates.
	 * @param mapBounds Coordinate maxima and minima of map.
	 */
	private void initialize(Graph G, List<double[]> V, double[] mapBounds) {
		mapContainer = new JLabel();
		this.G = G;
		this.V = V;
		this.mapBounds = mapBounds;
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, screenSize.width, screenSize.height);
		
		setTitle("Street Map");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addImage();
		setVisible(true);
		addMouseListener(new MouseInput());
		
		title = "Title";
		titleFont = new Font("Monospace", Font.BOLD, 13);
		subtitle = "Subtitle";
		subtitleFont = new Font("Monospace", Font.BOLD, 10);
		
		roadWidth = 1;
		roadColor = Color.black;
		pathWidth = 3;
		pathColor = Color.red;
		zoom = 1.0;
		xF = yF = 0.5;
	}
	
	/**
	 * Sets the GUI title.
	 * 
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Sets the GUI subtitle.
	 * 
	 * @param subtitle The subtitle.
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	/**
	 * Amplifies the zoom value.
	 * 
	 * @param amplifier The amplifier.
	 */
	public void zoom(double amplifier) {
		int width = mapContainer.getWidth()-60;
		int height = mapContainer.getHeight()-60;
		double nXF = (double)(MouseInfo.getPointerInfo().getLocation().x - (getLocation().x + mapContainer.getLocation().x + 30))/width;
		double nYF = (double)(MouseInfo.getPointerInfo().getLocation().y - (getLocation().y + mapContainer.getLocation().y + 30))/height;
		zoom *= amplifier;
		xF = xF - 1/(zoom*2) + 1/zoom*nXF;
		yF = yF - 1/(zoom*2) + 1/zoom*nYF;
	}
	
	/**
	 * Updates the map image. Automatically revalidates the
	 * GUI JFrame.
	 * 
	 * @param animate Whether to animate path drawing.
	 */
	public void updateMap(boolean animate) {
		getContentPane().add(mapContainer);
		revalidate();
		
		// Setting up map
		int width = mapContainer.getWidth();
		int height = mapContainer.getHeight();
		BufferedImage I = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = I.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Setting up map dimensions
		double xM = width*zoom, yM = height*zoom;
		double xL = Math.abs(mapBounds[1]-mapBounds[3]), yL = Math.abs(mapBounds[0]-mapBounds[2]);
		double xI = (xM-(yM-60)*(xL/yL))/2, yI = 30;
		if(xM/yM < xL/yL) {
			xI = 30;
			yI += (yM-(xM-60)*(yL/xL))/2;
		}
		double yP = yM*yF-yM/zoom/2, xP = xM*xF-xM/zoom/2;
		
		g2d.setColor(Color.white);
		g2d.fillRect(20, 20, width-40, height-40);
		g2d.setColor(roadColor);
		g2d.setStroke(new BasicStroke(roadWidth));
		
		// Drawing each road
		int i, n = G.getSize();
		double[] v1, v2;
		for(i = 0; i < n; i++) {
			for(Edge e : G.getEdges(i)) {				
				v1 = V.get(i);
				v2 = V.get(e.next);
				if(animate)
					drawRoad(xM, yM, xL, yL, xI, yI, xP, yP, v1[0], v1[1], v2[0], v2[1], 0, g2d);
				else {
					drawRoad(xM, yM, xL, yL, xI, yI, xP, yP, v1[0], v1[1], v2[0], v2[1], (int)(v1[2]*v2[2]), g2d);
				}
			}
		}
		
		// Drawing each path
		if(animate) {
			n = P.size()-1;
			for(i = 0; i < n; i++) {
				
				v1 = V.get(P.get(i));
				v2 = V.get(P.get(i+1));
				
				drawRoad(xM, yM, xL, yL, xI, yI, xP, yP, v1[0], v1[1], v2[0], v2[1], 1, g2d);
	
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				
				mapContainer.setIcon(new ImageIcon(I));
				revalidate();
			}
		}
		
		// Creating image base
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Color.lightGray);
		g2d.fillRect(0, 0, width, 20);
		g2d.fillRect(0, 0, 20, height);
		g2d.fillRect(width-20, 0, 20, height);
		g2d.fillRect(0, height-20, width, 20);
		g2d.setColor(Color.black);
		g2d.drawRect(20, 20, width-40, height-40);
		g2d.setFont(titleFont);
		g2d.drawString(title, 40, 15);
		int tw = g2d.getFontMetrics().stringWidth(title);
		g2d.setFont(subtitleFont);
		g2d.drawString(subtitle, 50+tw, 15);
		
		
		mapContainer.setIcon(new ImageIcon(I));
		revalidate();
	}
	
	/**
	 * Draws a road with received parameters.
	 */
	private void drawRoad(double xM, double yM, double xL, double yL, double xI, double yI,
			double xP, double yP, double lat1, double lon1, double lat2, double lon2, int highlight, Graphics2D g2d) {
						
		lat1 = 1-Math.abs(lat1-mapBounds[0])/yL;
		lon1 = Math.abs(lon1-mapBounds[1])/xL;
		lat2 = 1-Math.abs(lat2-mapBounds[0])/yL;
		lon2 = Math.abs(lon2-mapBounds[1])/xL;
		
		int x1 = (int) (lon1*(xM-xI*2) + xI-xP);
		int y1 = (int) (lat1*(yM-yI*2) + yI-yP);
		int x2 = (int) (lon2*(xM-xI*2) + xI-xP);
		int y2 = (int) (lat2*(yM-yI*2) + yI-yP);
		
		if(highlight == 1) {
			g2d.setColor(pathColor);
			g2d.setStroke(new BasicStroke(pathWidth));
		} else {
			g2d.setColor(roadColor);
			g2d.setStroke(new BasicStroke(roadWidth));
		}
		
		g2d.drawLine(x1, y1, x2, y2);
	}
	
	/**
	 * Generates map image.
	 */
	private void addImage() {
		getContentPane().add(mapContainer);
	}
	
	
}
