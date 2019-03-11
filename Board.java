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
	private int startingChaps = 8;
	private boolean isMouseReleased = false;
	private boolean isStatic = true;
	public static double[][] currentLayout;


	public Board() {
		System.out.println("here");

		this.addMouseListener(new MouseAdapter() {

			//Reset the arrow
			public void mouseReleased(MouseEvent e) {

				isMouseReleased = true;

				if(isMouseReleased && shouldDrawArrow && clickedChap != null) {
					double[] vel = calculateVelocity(clickedChap);
					clickedChap.setVelocity(vel);
					team = Main.gameLoop(team);
					System.out.println("Game Loop Done");
					isStatic = true;
				}

				clickedFirstTime = true;
				clickedChap = null;

				// we call the physics part and animate the result


				//TODO: Add a way to check that a player won/ winning message

				boolean won = true;
				int firstTeamCount = startingChaps;
				int secondTeamCount = startingChaps;

				for(Chap chap : Main.board) {
					if(chap.getTeam() == 0){
						firstTeamCount++;
					}
					else {
						secondTeamCount++;
					}
				}

				if(firstTeamCount > 0 && secondTeamCount == 0){
					// team 0 won, not sure how we want to use this, I'll just print for debugging purposes
					System.out.println("Team 0 Won");
				}

				if(firstTeamCount == 0 && secondTeamCount > 0){
					// team 1 won, not sure how we want to use this, I'll just print for debugging purposes
					System.out.println("Team 1 Won");
				}
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
					isStatic = false;
					repaint();
				}

			}

		});

	}

	public static void setCurrentLayout(double[][] layout) {
		currentLayout = layout;
	}

	//The dimensions of the panel
	public Dimension getPreferredSize() {

		return new Dimension(Constants.WINDOW_SIZE, Constants.WINDOW_SIZE);

	}

	//Method for painting everything
	//Note: Method is called twice during initialization
	public void paintComponent(Graphics g) {
		System.out.println("paintComponent");
		super.paintComponent(g);

		this.setBackground(Color.WHITE);
		for(double[] pos : currentLayout){
			drawChap(g, (int)(Math.round(pos[0])), (int)(Math.round(pos[1])));
		}

		//Runs only if a mouse drag occurred
		if(isStatic && shouldDrawArrow && clickedChap != null) {
			drawArrow(g, clickedChap);
		}

  }
	//Prints the board's full data
	public static void printBoard(List<Chap> board){

		for(Chap c : board){
			c.printInfo();
	    }

	}

	//Draw circle with the x and y being the center coordinates of the circle
	public void drawChap(Graphics g, int p0, int p1) {
		//int x = (int)Math.round(p0+Constants.WINDOW_SIZE);
		//int y = (int)Math.round(p1+Constants.WINDOW_SIZE); //TODO shitty code, do it in a loop. why am i stupid like this?

		g.fillOval(p0 - Constants.RADIUS, p1 - Constants.RADIUS, 2 * Constants.RADIUS, 2 * Constants.RADIUS);

	}

	//Draws an arrow to show the direction and force of the hit
	public void drawArrow(Graphics g, Chap chap) {

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

	public double[] calculateVelocity(Chap chap) {

		double[] position = chap.getPosition();
		double changeX = position[0] - mousePoint.getX();
		double changeY = position[1] - mousePoint.getY();

		return new double[]{changeX, changeY, 0};

	}

}
