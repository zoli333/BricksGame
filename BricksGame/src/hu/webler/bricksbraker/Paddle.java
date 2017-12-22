/*
    <Paddle.java>  Copyright (C) <2017>  <Zoltan Nagy>
    
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

public class Paddle extends Thread {
	private double xpos,ypos;
	private double vX=0;
	private double speed=Squash.getPaddleSpeed();
	private int paddleWidth=Squash.getPaddleWidth();
	private int paddleHeight=Squash.getPaddleHeight();
	private int paddlePadding=Squash.getPaddlePadding();
	
	public Paddle() {
		setInitialPosition();
		
		this.start();
	}
	
	public void draw(){
		Squash.filledRectangle(xpos, ypos, paddleWidth, paddleHeight);
	}
	
	public void setInitialPosition(){
		xpos=Squash.getWidth()/2-paddleWidth/2;
		ypos=Squash.getHeight()-paddleHeight-paddlePadding;
	}
	
	
	public void move(){
		if(Squash.isKeyPressed(37)){
			vX=-speed;
		}else if(Squash.isKeyPressed(39)){
			vX=speed;
		}else{
			vX=0;
		}
		TestEdges();
		xpos+=vX;
	}
	
	@Override
	public void run() {
		while(true){
			move();
			try {
				sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void TestEdges(){ //when the paddle is at the right or left edge of the canvas
		if(xpos<=0 && vX==-speed || xpos+paddleWidth>=Squash.getWidth() && vX==speed){
			vX=0;
		}
	}
	
	

	public double getvX() {
		return vX;
	}

	public void setvX(double vX) {
		this.vX = vX;
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

	public int getPaddleWidth() {
		return paddleWidth;
	}

	public void setPaddleWidth(int paddleWidth) {
		this.paddleWidth = paddleWidth;
	}

	public int getPaddleHeight() {
		return paddleHeight;
	}

	public void setPaddleHeight(int paddleHeight) {
		this.paddleHeight = paddleHeight;
	}

	public int getPaddlePadding() {
		return paddlePadding;
	}

	public void setPaddlePadding(int paddlePadding) {
		this.paddlePadding = paddlePadding;
	}
	
	
	
}
