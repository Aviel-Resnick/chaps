import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
/*
Chap Class
- position
- velocity
- bool (on board)
- color (could be an int value, denotes team)
*/

public class Chap {

  private double[] position = new double[2]; //position vector
  private double[] velocity = new double[2]; //velocity vector
  private boolean onBoard;
  private int team; //int so we could possibly have 2+ teams.
  private boolean isMoving;
  private double[] angularV = new double[3]; //angular velocity vector.
  public Event type1;
  public Event[] type2;
  private List<Event> type2liquid= new ArrayList<Event>();


  public Chap(double[] pos, double[] vel, int team) {
    this.position = pos;
    this.velocity = vel;
    this.onBoard = true;
    this.angularV = new double[]{0,0,0};
    this.isMoving = false;
    this.team = team;
  }

  public void FinishStructure(){
    this.type1.type = 1;
    this.type1.p1 = this;
  }

  public void printInfo() {
    System.out.println("---------------------------");
    System.out.println("pos: (" + position[0] + "," + position[1] + ")");
    System.out.println("vel: (" + velocity[0] + "," + velocity[1] + ")");
    System.out.println("onBoard: " + onBoard);
    System.out.println("team: " + team);
    System.out.println("---------------------------");
  }

  public double[] whereAt(double t){
    //returns where it will be in t time units.
    double[] pos = {-1,-1};
    return pos;
  }

  public void addType2(Event e){
    //add type 2 event to mutable array list
    type2liquid.add(e);
  }

  public void solidify(){
    //convert type2liquid arryalist to type2 Array. no longer mutable so structure
    // is solid and cannot change!
    type2 = new Event[type2liquid.size()];
    for(int i = 0; i<type2.length; i++){
      type2[i] = type2liquid.get(i);
    }
  }

  public double[] getAngularV(){
    return this.angularV;
  }

  public void setAngularV(double[] angularV){
    this.angularV = angularV;
  }


  public double stopTime(double time) { //tells when the chap will stop moving assuming no collisions.
    //TODO: MAKE SURE TO ADD the CURRENT TIME FROM PARAMETERS!
    double[] zero = {0,0};
    if(velocity == zero) {
      return -1;
    }
    return -1;
    //TODO: MAKE SURE TO ADD the CURRENT TIME FROM PARAMETERS!
  }


  public double[] move(double time) { //use the friction # to simulate movement
    return this.position; //placeholder until we code this
  }

  public double[] getPosition() {
    return this.position;
  }

  public void setPosition(double[] pos) {
    this.position = pos;
  }

  public double[] getVelocity() {
    return this.velocity;
  }

  public void setVelocity(double[] velocity) {
    this.velocity = velocity;
  }

  public boolean isOnBoard() {
    return this.onBoard;
  }

  public boolean isMoving() {
    return this.isMoving;
  }

  public int getTeam() {
    return this.team;
  }

  public void setTeam(int team) {
    this.team = team;
  }

  public boolean isClicked(MouseEvent e) {

		if(e.getX() <= this.position[0] + Constants.RADIUS && e.getX() >= this.position[0] - Constants.RADIUS &&
				e.getY() >= this.position[1] - Constants.RADIUS && e.getY() <= this.position[1] + Constants.RADIUS) {
			return true;
		}

		return false;

	}

}
