package utility.physics;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;


public class Velocity {

	public static Logger logger = Logger.getLogger(Velocity.class);
	
	private DecimalFormat df = new DecimalFormat("0.00");
	
	protected double x = 0;
	protected double y = 0;
	
	public Velocity() {
		this(0,0);
	}
	
	public Velocity(double x, double y) {
		this.x=x;
		this.y=y;
	}

	public void add(Velocity v) {
	
		x += v.x;
		y += v.y;
	}
	
	/**
	 * Reflect velocity in the X direction.
	 * 
	 * (4,1) becomes (-4,1).
	 * 
	 * Equivalent to hitting a wall. Momentum is conserved.
	 */
	public void reflectX() {
		
		logger.debug("Reflecting velocity X.  Currently " + this);
		x = -x;
		logger.debug("Now " + this);
	}
	
	/**
	 * Reflect velocity in the Y direction.
	 * 
	 * (4,1) becomes (4,-1).
	 * 
	 * Equivalent to hitting a wall. Momentum is conserved.
	 */
	public void reflectY() {
		
		logger.warn("Reflecting velocity Y.  Currently " + this);
		y = -y;
		logger.warn("Now " + this);
	}	
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void addX(double x) {
		this.x += x;
	}
	
	public void subtractX(double x) {
		this.x -= x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void addY(double y) {
		this.y += y;
	}	

	public void subtractY(double y) {
		this.y -= y;
	}	
	
	/**
	 * Scale velocity by ratio
	 * 
	 * E.g., Velocity (2,4) scaled by ratio 0.5 -> New velocity(1,2) 
	 * @param root
	 */
	public void scale(double ratio) {
		
		logger.debug("Scaling velocity by "+ratio+": x="+df.format(x)+",y="+df.format(y));
		
		x *= ratio;
		y *= ratio;
		
		logger.debug("x="+df.format(x)+",y="+df.format(y));
	}
	
	public Velocity copy() {
		return new Velocity(x,y);
	}
	
	public String toString() {
		
		return "(" + df.format(x) + ", " + df.format(y) + ")";
	}
}
