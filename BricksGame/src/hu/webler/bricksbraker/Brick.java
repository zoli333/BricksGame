/*
    <Brick.java>  Copyright (C) <2017>  <Zoltan Nagy>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    If you have any questions please send me an email to the following address:
    nagyzoli.g@gmail.com
    
    Or if you have any comments feel free to write me directly on github issues
    
*/

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
