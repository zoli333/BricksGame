package hu.webler.bricksbraker;

import java.awt.Color;
import java.util.Vector;

public class Brick {

	private double xpos, ypos;
	private boolean crashed=false;
	
	
	public Brick(double x, double y) {
		xpos=x;
		ypos=y;
	}
	
	
	public void draw() {
		Squash.setoffColor(Squash.DEFAULT_BRICK_COLOR);
		Squash.brickFilledRectangle(xpos,ypos,Squash.brickWidth,Squash.brickHeight);
		Squash.setoffColor(Squash.DEFAULT_BRICK_BORDER_COLOR);
		Squash.rectangle(xpos,ypos,Squash.brickWidth,Squash.brickHeight);
	
	}


	public double getXpos() {
		return xpos;
	}

	public void setXpos(double xpos) {
		this.xpos = xpos;
	}

	public double getYpos() {
		return ypos;
	}

	public void setYpos(double ypos) {
		this.ypos = ypos;
	}

	public boolean isCrashed() {
		return crashed;
	}

	public void setCrashed(boolean crashed) {
		this.crashed = crashed;
	}
	
	
	
}
