import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Color;
import javax.swing.JFrame;
import java.lang.Math;

public class Main {
	//CONTROL PANEL!!! ----------------------------------------------------------
	//you can also change starting arrangement of Chaps in the
	//	"initialize" function on the bottom.

	//section 1: simulation---------------------
	private static int numberPoints = 5;
	//Make sure that this corresponds with the # of points you make in the
	// initialize function at the bottom.

	private static int dimension = 400;
	//distance from origin to each wall. origin is in the very center of the box.

	private static int r = 25;
	//radius of each Chap.

	private static double ro = 0;
	//mass distribution inside Chaps. ranges 0-1, inclusive.
		//0 = all mass at centerpoint (rotations dont happen here),
		//1 = balls are hollow shells,
		//0.2 = even distribution.

	private static double frictionCoefficient = 0.75;

	//section 2: animation---------------------
	private static double increment = 0.01;
	//increment is ONLY used for the animation (the simulation's time between each frame)
	//make sure it is finer than the time between collisions, or the animation will be shit.

	public static int waitTime = 10;
	//the amount of milliseconds to wait after each frame.



	//</control panel>------------------------------------------------------------

	public static int win = 0; //win: an int specifying the winner's color ID
	private static double time = 0;
	private static double[] Z = {0,0,0}; //just a zero vector for convenience
	public static List<Chap> board = new ArrayList<Chap>(); //all chaps on the board.
	private static Event event; //contains up to 2 involved chaps and event type.
	private static Event[][] collideMatrix = new Event[numberPoints][numberPoints];
		//keeps track of collision times between every pair of points. By storing these values,
		//	we can lower the running time from O(n^2) to O(n).
	private static Event[] wallList = new Event[numberPoints];
	private static List<double[][]> animation = new ArrayList<double[][]>(); //entire animation. composed of frames,
	//which encode positions of all pieces at each time increment.
	private static double[][] currentLayout = new double[numberPoints][2]; //a single frame of the animation.
	private static List<Event> recompute = new ArrayList<Event>(); //events to be recomputed. part of the time-saving structure.
	private static ArrayList<double[]> speeds = new ArrayList<double[]>();
	private static Chap[] boardArray;
	private static JFrame frame;
	private static Board theBoard;

	public static void main(String[] args) {

		initialize();
		System.out.println("Hey, I have done some initialization!");

	}

	public static void initialize() {
		//sets all constants in constants.java
		Constants.WINDOW_SIZE = 2 * dimension;
		Constants.RADIUS = r;
		Constants.FRICTION_COEFFICIENT = frictionCoefficient;

		int x = 0;
		for(int i = 0; i< numberPoints; i++){
			x += 60;
			double[] position = new double[]{x,0,0};
			board.add(new Chap(position,Z,1));
		}

		boardArray = new Chap[numberPoints];
		for(int i=0; i<numberPoints; i++){
			 for(int j=0; j<i; j++){
				 collideMatrix[i][j] = new Event(2, board.get(i), board.get(j) );
				 collideMatrix[i][j].p1.addType2(collideMatrix[i][j]);
				 collideMatrix[i][j].p2.addType2(collideMatrix[i][j]);
			 }
		}

		double[][] firstLayout = new double[numberPoints][2];

		for(int i=0; i<numberPoints; i++){
			 wallList[i] = new Event(1, board.get(i));
			 board.get(i).type1 = wallList[i];
			 board.get(i).solidify();
			 boardArray[i] = board.get(i);
			 boardArray[i].FinishStructure();
			 firstLayout[i] = board.get(i).getPosition();
	 	}
		Board.setCurrentLayout(firstLayout);

		frame = new JFrame("Chapayev");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theBoard = new Board();
		frame.add(theBoard);
		frame.pack();
		frame.setVisible(true);
	}


	public static int gameLoop(int team) {
		double time = 0;
		boolean allStop = false;
		boolean teamSwitch = false;
		double numberIncrements = 0;
		while(allStop == false && time < 2) {
			System.out.println("time: " + time);
			findNextEvent(); //finds time and nature of the next event.
			addToAnimation(event);		//create a smooth series of frames leading up to this event, and add it to the animation.
			handleEvent(); //goes to that time. resets positions and velocities.
			time = event.time; //update time
			allStop = checkStop();
		}

		for(double[][] layout : animation) {
			currentLayout = layout;
			theBoard.currentLayout = layout;
			theBoard.repaint();
			System.out.println("in my loop!");
			try{ Thread.sleep(waitTime); }
			catch (Exception exc){}
		}

		//now, all the peices have stopped moving.
		//figure out whose turn it is
		if(team == 1 && teamSwitch == true){
			team = 2;
		}
		else if(team == 2 && teamSwitch == true){
			team = 1;
		}
		return team;
	}

	public static boolean checkStop() { //returns false if any chap is still moving.
	    for (Chap c : board) {
	      double[] currentVel = c.getVelocity();
	      if (currentVel[0] != 0 || currentVel[1] != 0) {
	    	  return false;
	      }
	    }
	    return true;
	}


	public static void findNextEvent() {
		//who needs updates? Come get your updates!
		Chap[] needsUpdates; //this is for walls only.
		recompute = new ArrayList<Event>();

		if(time == 0) {
			needsUpdates = boardArray; //wall updates: beginning

			//collision updates: beginning
			for(int i = 0; i<numberPoints; i++){
				for(int j=0; j<i; j++){
					recompute.add(collideMatrix[i][j]);
				}
			}
		}

		else if(event.type == 1) {
			needsUpdates = new Chap[]{event.p1}; //wall updates: if last was type 1

			//collision updates: if last was type 1
			for(int i = 0; i<numberPoints; i++){
				for(int j = 0; j<i; j++){
					if(collideMatrix[i][j].p1 == event.p1 || collideMatrix[i][j].p2 == event.p1) {
						recompute.add(collideMatrix[i][j]);
					}
				}
			}
		}
		else{
			needsUpdates = new Chap[]{event.p1, event.p2}; //wall updates: if last was type 2.

			//collisions updates: if last event was type 2
			for(Event e : event.p1.type2){
				if(e.p1 != event.p2 && e.p2 != event.p2){
					recompute.add(e);
				}
			}
			for(Event e : event.p2.type2){
				recompute.add(e);
			}
		}

		//update the wallList & put the new guys in their places.
		for(Chap p : needsUpdates){
			double[] update = nextWall(time,p);
			p.type1.wallNormal = new double[]{update[0], update[1], update[2]};
			p.type1.time = update[3];

		}
		Arrays.sort(wallList);

		//now, after updating the walls, update the collisions!
		for(Event e : recompute){
			e.time = collide(e.p1,e.p2);
		}


		//pick smallest collidetime
		Event smallestCollide = collideMatrix[1][0];
		for(int i = 0; i < numberPoints; i++) {
			for(int j = 0; j < i; j++) {
				if(collideMatrix[i][j].time < smallestCollide.time) {
					smallestCollide = collideMatrix[i][j];
				}
			}
		}
		//smallest walltime
		Event smallestWall = wallList[numberPoints-1];

		//pick sooner one of wall or interChap!
		if(smallestCollide.time < smallestWall.time) {
			event = smallestCollide;
		}
		else{
			event = smallestWall;
		}
	}

	public static double squareMag(double[] v){
		return (v[0]*v[0]) + (v[1]*v[1]) + (v[2]*v[2]);
	}

	public static double DP(double[] v1, double[] v2) {
		return v1[0]*v2[0] + v1[1]*v2[1] + v1[2]*v2[2];
	}

	public static double[] CP(double[] v1, double[] v2) {
		double[] answer = {v1[1]*v2[2] - v2[1]*v1[2], v2[0]*v1[2] - v1[0]*v2[2], v1[0]*v2[1] - v2[0]*v1[1]};
		return answer;
	}

	public static double[] LC(double[] v1, double[] v2, double[] v3, double[] v4,
														double s1, double s2, double s3, double s4) {
		double zero = s1*v1[0] + s2*v2[0] + s3*v3[0] + s4*v4[0];
		double one = s1*v1[1] + s2*v2[1] + s3*v3[1] + s4*v4[1];
		double two = s1*v1[2] + s2*v2[2] + s3*v3[2] + s4*v4[2];
		double[] answer = {zero, one, two};
		return answer;
	}


	public static double[] nextWall(double time, Chap p) { //return the wallNormal and the time of collision.
		//if it's not moving
		double a = dimension;
		double[] c = p.getPosition();
		double[] v = p.getVelocity();
		double[][] nList = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};
		double[][] candidates = {{1,0,0,0},{-1,0,0,0},{0,1,0,0},{0,-1,0,0},{0,0,1,0},{0,0,-1,0}};
		if(v[0] == 0 && v[1] == 0 && v[2] == 0) {
			return new double[]{1,0,0,99999+time};
		}

		double minTime = 99999+time;
		int minCandidate = 0;
		for(int i = 0; i<6 ; i++) {
			double[] n = nList[i];
			double Dvn = DP(v,n);

			if(Dvn <= 0.000000000001){ //shut up!
				candidates[i][3] = 99999+time;

			}
			else {
				//compute time
				double Dcn = DP(c,n);
				candidates[i][3] = (a-r-Dcn)/(Dvn);
			}

			//take minimum wall collisions
			if(candidates[i][3] < minTime) {
				minTime = candidates[i][3];
				minCandidate = i;
			}
		}
		candidates[minCandidate][3] += time;
		return candidates[minCandidate];
	}

	public static double collide(Chap a, Chap b) {
		double[] c1 = a.getPosition();
		double[] c2 = b.getPosition();
		double[] v1 = a.getVelocity();
		double[] v2 = b.getVelocity();
		double d0 = (c2[0] - c1[0]);
		double d1 = (c2[1] - c1[1]);
		double d2 = (c2[2] - c1[2]);
		double[] d = {d0,d1,d2};
		double jv0 = (v2[0] - v1[0]);
		double jv1 = (v2[1] - v1[1]);
		double jv2 = (v2[2] - v1[2]);
		double[] jv = {jv0,jv1,jv2};

		double Ddjv = DP(d,jv);
		double delta = Ddjv*Ddjv + (squareMag(jv)*(4*r*r - squareMag(d)));

		if(Ddjv >= 0|| delta <= 0) {
			return 99999+time;
		}

		double timeToCollide = (-1)*((Ddjv+Math.sqrt(delta))/squareMag(jv));
		return timeToCollide + time;
	}


	public static void addToAnimation(Event e) {
		double totalTime = e.time - time;
		boolean reportSpeeds;

		int numberLayouts = (int)Math.round(totalTime / increment);
		double[][] newLayout;

		for(int i = 0; i<numberLayouts; i++){
			newLayout = new double[numberPoints][2];
			for(int j=0; j<boardArray.length; j++) {

				double [] pos = boardArray[j].whereAt(i*increment);
				newLayout[j] = pos;
			}
			animation.add(newLayout);
		}
 	}

	public static void handleEvent() {
	    //updates all positions, then updates all velocities.
			//POSITIONS
			for(Chap p : board){
				p.setPosition(p.whereAt(event.time - time));
			}

			//VELOCITIES
			if(event.type == 1) { //updates linear and angular vels for wall hits.
				double[] n = event.wallNormal;
				double[] p = event.p1.getPosition();
				double[] v = event.p1.getVelocity();
				double[] omg = event.p1.getAngularV();

				//compute some values
				double Dvn = DP(v,n);
				double[] Cnomg = CP(n,omg);
				double[] Cnv = CP(n,v);
				double Dnomg = DP(n,omg);

				//sets new linear velocity
				event.p1.setVelocity( LC(v,n,Cnomg,Z,   (1-ro)/(1+ro), (-2*Dvn)/(ro+1), (2*ro*r)/(ro+1), 0) );

				//sets new angular velocity
				event.p1.setAngularV( LC(omg,n,Cnv,Z,   (ro-1)/(ro+1), (2*Dnomg)/(ro+1), (-2)/(r*(ro+1)), 0) );

			}
			else{
				Chap p1 = event.p1;
				Chap p2 = event.p2;

				//get some numbers
				double[] c1 = event.p1.getPosition();
				double[] v1 = event.p1.getVelocity();
				double[] omg1 = event.p1.getAngularV();
				//......
				double[] c2 = event.p2.getPosition();
				double[] v2 = event.p2.getVelocity();
				double[] omg2 = event.p2.getAngularV();

				//compute some more numbers
				double e0 = (c2[0] - c1[0])/(2*r);
				double e1 = (c2[1] - c1[1])/(2*r);
				double e2 = (c2[2] - c1[2])/(2*r);
				double[] e = {e0,e1,e2};

				double jv0 = (v2[0] - v1[0]);
				double jv1 = (v2[1] - v1[1]);
				double jv2 = (v2[2] - v1[2]);
				double[] jv = {jv0,jv1,jv2};

				double lomg0 = (omg2[0] + omg1[0])/(2);
				double lomg1 = (omg2[1] + omg1[1])/(2);
				double lomg2 = (omg2[2] + omg1[2])/(2);
				double[] lomg = {lomg0,lomg1,lomg2};

				double Djve = DP(jv, e);
				double Delomg = DP(e, lomg);
				double[] Celomg = CP(e, lomg);
				double[] Cejv = CP(e, jv);

				p1.setVelocity( LC(v1,v2,e,Celomg,  1/(ro+1), ro/(ro+1), Djve/(ro+1), (2*ro*r)/(ro+1)) );
				p2.setVelocity( LC(v2,v1,e,Celomg,  1/(ro+1), ro/(ro+1), -Djve/(ro+1), -(2*ro*r)/(ro+1)) );

				p1.setAngularV( LC(omg1,lomg,e,Cejv,  1, -2/(ro+1), 2*Delomg/(ro+1), 1/(r*(ro+1)) ) );
				p2.setAngularV( LC(omg2,lomg,e,Cejv,  1, -2/(ro+1), 2*Delomg/(ro+1), 1/(r*(ro+1)) ) );
			}

	}

	public static List<double[][]> getAnimation(){
		return animation;
	}

}
