import java.awt.Color;
import java.awt.event.MouseEvent;

public class Constants {

	public static int WINDOW_SIZE;

	public static int ONE_START_Y = 50;
	public static int TWO_START_Y = 700;
	public static Color ONE_COLOR = Color.BLUE;
	public static Color TWO_COLOR = Color.RED;

	public static int RADIUS;
	public static double FRICTION_COEFFICIENT;

	public static double distance(MouseEvent e, Chap chap) {

		double[] position = chap.getPosition();

		return (Math.sqrt((Math.abs(e.getX() - position[0])) + (Math.abs(e.getY() - position[1]))));

	}

}
