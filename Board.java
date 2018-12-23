import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.List;

import javax.swing.JPanel;

public class Board extends JPanel {
	
	private boolean clickedFirstTime = true;
	private Chap clickedChap = null;
	private boolean shouldDrawArrow = false;
	private Point mousePoint;
	
	
	public Board() {
		
		this.addMouseListener(new MouseAdapter() {
			
			//Reset the arrow
			public void mouseReleased(MouseEvent e) {
				
				clickedFirstTime = true;
				
			}
			
		});
		
		this.addMouseMotionListener(new MouseAdapter() {
			
			//Draw an arrow if the mouse was clicked on a chap and dragged
			public void mouseDragged(MouseEvent e) {
				
				if(clickedFirstTime) {
					for(Chap chap : Main.board) {
						if(chap.isClicked(e)) {
							clickedChap = chap;
							clickedFirstTime = false;
							break;
						}
					}
				}
				
				if(clickedChap != null) {
					//System.out.println(Constants.distance(e, clickedChap));
					shouldDrawArrow = true;
					mousePoint = e.getPoint();
					repaint();
				}
				
			}
			
		});
		
	}
	
	//The dimensions of the panel
	public Dimension getPreferredSize() {
		
        return new Dimension(Constants.WINDOW_SIZE, Constants.WINDOW_SIZE);
    
	}

	//Method for painting everything
	//Note: Method is called twice during initialization
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		initializeOneSide(g, Constants.ONE_COLOR, Constants.ONE_START_Y);
		initializeOneSide(g, Constants.TWO_COLOR, Constants.TWO_START_Y);
		//printBoard(Main.board);
		//System.out.println("\nDone\n");
		
		//Runs only if a mouse drag occurred
		if(shouldDrawArrow) {
			drawArrow(g, clickedChap);
		}
        
    }
	
	//Draw the chaps for one of the teams
	public void initializeOneSide(Graphics g, Color color, int y) {
		
		g.setColor(color);
		int team = color.equals(Constants.ONE_COLOR) ? 1 : 2;
		int x;
		
		for(int i = 0; i < 8; i++) {
			
			x = 50 + (i * 100);
			drawCircle(g, x, y);
			
			//Populate the list with the initial chaps
			if(Main.board.size() < 16) {	//Prevents the addition of copies due to the repainting
				Main.board.add(new Chap(new double[] {x, y}, new double[] {0, 0}, team));
			}
			
		}
		
	}
	
	//Prints the board's full data
	public static void printBoard(List<Chap> board){
	    
		for(Chap c : board){
			c.printInfo();
	    }
		
	}
	
	//Draw circle with the x and y being the center coordinates of the circle
	public void drawCircle(Graphics g, int x, int y) {
		
		g.fillOval(x - Constants.RADIUS, y - Constants.RADIUS, 2 * Constants.RADIUS, 2 * Constants.RADIUS);
	
	}
	
	//Draws an arrow(line, actually) to show the direction and force of the hit
	public void drawArrow(Graphics g, Chap chap) {
		
		Graphics2D g2 = (Graphics2D) g;
		double[] position = chap.getPosition();
		double endX = position[0] + (position[0] - mousePoint.getX());
		double endY = position[1] + (position[1] - mousePoint.getY());
		
		g2.draw(new Line2D.Double(position[0], position[1], 
				endX, endY));
		
	}
	
}
