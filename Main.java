import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class Main {

	public static int win = 0; //win: an int specifying the winner's color ID
	public static List<Chap> board = new ArrayList<Chap>(); //all chaps on the board.
	public static Event event; //contains up to 2 involved chaps and event type.
	
	public static void main(String[] args) {
		
		initialize();
		
		/*
		 * This code needs to be worked on
		boolean allStop = false;
	    //the game is played here.
	    initializeChaps()

	    while(win == 0) {
	      while(allStop == false) {
	        findNextEvent(); //we are guaranteed that nothing happens till t=nextEvent.
	        animate(Event.getTime()); //move pieces assuming nothing collides until that time.
	        handleEvent(event);
	        allstop = checkStop();
	      }
	      //TODO: decide who's turn it is to flick, give user input.
	    }
	    */
		
	}
	
	public static void initialize() {
		
		JFrame frame = new JFrame("Chapayev");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new Board());
		frame.pack();
		frame.setVisible(true);
		
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
	
	/*
	public static double findNextEvent() {
	    double smallest = -1; //-1 is the special character for "NO EVENT"

	    //iterate over all predicted stops. find soonest one.
	    event.type = 1;
	    for(Chap c : board){
	      double candidate = c.stopTime(); //This doesn't exist at the moment
	      if(candidate > 0 && candidate < smallest) {
	        smallest = candidate;
	        event.c1 = c;
	        event.time = candidate;
	      }
	    }

	    //iterate over all possible collision times. find soonest positive time.
	    for(Chap a: board) {
	      for(Chap b: board) {
	        if(a==b) {
	          double candidate = -1;
	        }
	        else {
	          double candidate = collide(a,b); //collide needs to be worked on
	          if(candidate > 0 && candidate < smallest) {
	            smallest = candidate;
	            event.type = 2;
	            event.time = candidate;
	            event.c1 = a;
	            event.c2 = b;
	          }
	        }
	      }
	    }

	    //these loops have automatically populated the Event. time to animate!
	}
	*/
	
	/*
	public static handleEvent(event e) {
	    //resets velocities manually to [0,0] for stops,
	    //  solves for velocities in a collision.
	}
	*/
	
	/*
	public static double collide(Chap a, Chap b) { //return time of collision between a and b.
	    //this method requires some serious stuff.
	    return 0;
	  }
	*/
	
}
