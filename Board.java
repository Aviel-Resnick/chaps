import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;

import javax.swing.JPanel;

public class Board extends JPanel {

	private boolean clickedFirstTime = true;
	private Chap clickedChap;
	private boolean shouldDrawArrow;
	private Point mousePoint;
	private int team = 1;


	public Board() {

		this.addMouseListener(new MouseAdapter() {

			//Reset the arrow
			public void mouseReleased(MouseEvent e) {

				clickedFirstTime = true;
				clickedChap = null;
				// we call the physics part and animate the result

				team = Main.gameLoop();
			}
		});

		this.addMouseMotionListener(new MouseAdapter() {

			//Draw an arrow if the mouse was clicked on a chap and dragged
			public void mouseDragged(MouseEvent e) {

				//Determine which chap was clicked if one of them was clicked
				if(clickedFirstTime) {
					for(Chap chap : Main.board) {
						if(chap.isClicked(e) && chap.getTeam() == team) {
							clickedChap = chap;
							clickedFirstTime = false;
							break;
						}
					}
				}

				//Draw the arrow as long as a chap was selected
				if(clickedChap != null) {
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

		this.setBackground(Color.WHITE);

		initializeOneSide(g, Constants.ONE_COLOR, Constants.ONE_START_Y);
		initializeOneSide(g, Constants.TWO_COLOR, Constants.TWO_START_Y);

		//Runs only if a mouse drag occurred
		if(shouldDrawArrow) {
			drawArrow(g, clickedChap);
		}

    }

	//Draw the chaps for one of the teams
	public void initializeOneSide(Graphics g, Color color, int y) {

		g.setColor(color);
		int teamByColor = color.equals(Constants.ONE_COLOR) ? 1 : 2;
		int x;

		for(int i = 0; i < 8; i++) {

			x = 50 + (i * 100);
			drawChap(g, x, y);

			//Populate the list with the initial chaps
			if(Main.board.size() < 16) {	//Prevents the addition of copies due to the repainting
				Main.board.add(new Chap(new double[] {x, y}, new double[] {0, 0}, teamByColor));
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
	public void drawChap(Graphics g, int x, int y) {

		g.fillOval(x - Constants.RADIUS, y - Constants.RADIUS, 2 * Constants.RADIUS, 2 * Constants.RADIUS);

	}

	//Draws an arrow to show the direction and force of the hit
	public void drawArrow(Graphics g, Chap chap) {

		if(clickedChap != null) {

			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.BLACK);
			double[] position = chap.getPosition();
			double changeX = position[0] - mousePoint.getX();
			double changeY = position[1] - mousePoint.getY();
			double endX = position[0] + (changeX);
			double endY = position[1] + (changeY);

			//The main line
			g2.draw(new Line2D.Double(position[0], position[1],
					endX, endY));

			//Arrow head
			Path2D.Double arrow = new Path2D.Double();
			arrow.moveTo(endX, endY);
			arrow.lineTo(endX - 10 * (Math.cos(Math.atan(changeY / changeX) + (Math.PI / 2))),
					endY - 10 * (Math.sin(Math.atan(changeY / changeX) + (Math.PI / 2))));
			arrow.lineTo(endX + changeX * 0.2, endY + changeY * 0.2);
			arrow.lineTo(endX + 10 * (Math.cos(Math.atan(changeY / changeX) + (Math.PI / 2))),
					endY + 10 * (Math.sin(Math.atan(changeY / changeX) + (Math.PI / 2))));
			arrow.lineTo(endX, endY);
			g2.draw(arrow);

		}

	}

}
