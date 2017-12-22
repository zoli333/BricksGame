/*
    <Ball.java>  Copyright (C) <2017>  <Zoltan Nagy>

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineEvent.Type;

import com.sun.swing.internal.plaf.synth.resources.synth;

public class Ball extends Thread {
	private double xpos,ypos;
	private int radius, diameter;
	private double vX,vY;
	private double ballCenterX, ballCenterY;
	private double paddleRadius;
	private double gravity;
	private Vector<Brick> bricks;
	private static Clip clip;
    private static AudioInputStream inputStream;
    public File f = new File("/home/zoli/workspace/FallabdaOwn6/ping_pong_8bit_plop.wav");
	
    
	public Ball(Vector<Brick> bricks){
		this.bricks=bricks;
		radius=10;
		diameter=2*radius;
		xpos=Squash.paddle.getXpos()+Squash.paddle.getPaddleWidth()/2-radius;
		ypos=Squash.paddle.getYpos()-diameter;
		paddleRadius=(double)Squash.paddle.getPaddleWidth()/2+radius;
		ballCenterX=xpos+(double)radius;
		ballCenterY=ypos+(double)radius;
		gravity=Squash.getGravity();
		Random rnd=new Random();
		vX=(rnd.nextDouble()+0.5)*2;
		if(rnd.nextBoolean()) vX=-vX;
		vY=(rnd.nextDouble()+0.5)*3;
	}
	

    public void playSound(File clipFile) {
    	try {
			
			clip = AudioSystem.getClip();
			inputStream = AudioSystem.getAudioInputStream(clipFile);
			clip.open(inputStream);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
    	
		
    }
	
	public void draw(){
		Squash.filledCircle(xpos, ypos, radius);
		Squash.circle(xpos,ypos,radius);
	}
	
	
	public void WallTest() { //jobb vagy balra kiutkozott e a labdank
		if ((xpos<=0 && vX<0) || //bal oldai fal 
			(xpos>=Squash.getWidth()-diameter && vX>0)) //jobboldali fal
		{ 
			vX=-vX;
		}
	}
	
	public void CeilingTest() { 
		if(Squash.eternalLife){
			if (ypos<=0 && vY<0 || ypos>=Squash.getHeight() && vY>0)
			{ 
				vY=-vY;
			}	
		}else{
			if (ypos<=0 && vY<0)
			{ 
				vY=-vY;
			}			
		}
		
	}
	
	public boolean utoAlattTeszt() {
		if(ypos>Squash.paddle.getYpos()+Squash.paddle.getPaddleHeight()){
			return true;
		}else{
			return false;
		}
	}
	
    
	
	public void PaddleTest() { 
		if(ballCenterY < Squash.paddle.getYpos() && 
		   ballCenterY > Squash.paddle.getYpos()-radius &&
		   ballCenterX > Squash.paddle.getXpos()-radius &&
		   ballCenterX < Squash.paddle.getXpos() + Squash.paddle.getPaddleWidth() + radius)
		{ 
			if(Squash.isMusic){
				//sound effect is from http://cs.au.dk/~dsound/DigitalAudio.dir/Greenfoot/Pong.dir/Pong.html
				playSound(f);
			}
			
			//compute the Center of the paddle
			double paddleCenter=Squash.paddle.getXpos()+Squash.paddle.getPaddleWidth()/2;
			
			//subtract the center from the ball's current position
			//this way the coordinate system will be at the center of the paddle
			//ballCenter is the point where the ball hit the paddle (-(paddlewidth+r)..0..(paddlewidth+r))
			//this is nothing but rescaling the hitting point on the paddle
			double x0 = ballCenterX-paddleCenter;
			//compute the circle's intersection point with the help of the ball's hitting point (x0)
			//this is a recomputation , we ask for the y coordinate on the circle with the x0 already known coordinate
			//and  there was needed a flip for the y axis
			double y0 = -Math.sqrt(paddleRadius*paddleRadius-x0*x0);
			//System.out.println(x0+", "+y0);
			
			//compute the derivate of the normal equation of the circle 
			//which is defined around the paddle with radis utoRadius
			//f(x,y)=x^2+y^2-r^2
			double normalX=2*x0;
			double normalY=2*y0;
			
			
			//normalize the N vector, to get a unit vector for the surface normal
			double normalizer=Math.sqrt(normalX*normalX + normalY*normalY);
			normalX=normalX/normalizer;
			normalY=normalY/normalizer;
			
			//now we have the normalized (unit) surface normals for the paddle.
			//Compute the new direction with these surface normals, this 
			//will be computed with the help of the reflection vector formula: new_direction=old_direction-2*dot(N,old_direction)*N
			//but instead with the surface normal pointing always upwards, the 
			//normal's will be changing from point to point where the ball hits the paddle
			double eta=2; //this is the constant which gives now perfect reflection but with different normal vectors, we can play with this parameter to not give perfect reflection
			double dotprod=vX*normalX+vY*normalY;
			vX=vX-eta*dotprod*normalX;//compute the reflection and get the new direction on the x direction
			vY=-vY;//y direction is remain the same (but inverted), as we just want to have a change in the x direction
			
			
			//this would be the new y direction, but other constraints would be necessary here..., for example
			//adding checking in the if statement for ballCenterY+vY instead of just ballCenterY
			//so it is not used
			//vY=vY-eta*dotprod*normalY;
			//System.out.println(x0+", "+ y0+", "+ vX+", "+vY);
			
		}
	} 	
	
	/*
	 * This is a version of PaddleTest() when I don't shift the coordinate system zero,zero point into the paddle's
	 * center point. In this case I use the the graphics default coordinate system, where the x axis points to the right
	 * and the y axis points to bottom directionS, and we compute the paddle center position relative to this coordinate system.
	 * The y axis goes from 0 to canvas size downwards, from this It is clear why we have to invert the y0 point, because the y axis 
	 * points downward as default, so if we want to compute the y0 coordinate from the circle's upper half, we have to invert
	 * the computed y0 point, to get upward direction.   
	public void utoTeszt() { 
		if(ballCenterY < Squash.paddle.getYpos() && 
		   ballCenterY > Squash.paddle.getYpos()-radius &&
		   ballCenterX > Squash.paddle.getXpos()-radius &&
		   ballCenterX < Squash.paddle.getXpos() + Squash.paddle.getPaddleWidth() + radius)
		{
		 	if(Squash.isMusic){
				Squash.playSound(f);
			}
			
			//compute the Center of the paddle relative to our default coordinate system
			double paddleCenterX=Squash.paddle.getXpos()+Squash.paddle.getPaddleWidth()/2;
			double paddleCenterY=Squash.paddle.getYpos();
			
			//compute the hit points with the ball on the paddle.
			//the circles equation: (x-paddleCenterX)^2+(y-paddleCenterY)^2=r^2
			double x0 = ballCenterX;
			//invert y0 to get upward point from the circle (if I didn't do this, we'd compute the y point from x from the 
			//bottom half circle, that is our normal vectors would pointing in the inverse direction 
			double y0 = -Math.sqrt(paddleRadius*paddleRadius-((x0 - paddleCenterX)*(x0 - paddleCenterX)))+paddleCenterY;
			
			//the derivatives of the circle's normal equation:
			//f(x,y)=(x-paddleCenterX)^2+(y-paddleCenterY)^2-r^2, at x0,y0 hitting points
			double normalX=2*(x0-paddleCenterX);
			double normalY=2*(y0-paddleCenterY);
			
			//normalizer for the computed normal vector x,y components
			double normalizer=Math.sqrt(normalX*normalX + normalY*normalY);
			normalX=normalX/normalizer;
			normalY=normalY/normalizer;
			
			//reflection vector
			double eta=2; //this is the constant which gives now perfect reflection but with different normal vectors, we can play with this parameter to not give perfect reflection
			double dotprod=vX*normalX+vY*normalY;
			vX=vX-eta*dotprod*normalX;//compute the reflection and get the new direction on the x direction
			vY=-vY;//y direction is remain the same (but inverted), as we just want to have a change in the x direction
			
		}
	} 	
	*/
	
	
	public void brickTeszt() {
		for(int i=0;i<bricks.size();i++){
			Brick brick = bricks.get(i);
			if(!brick.isCrashed()){
				if( ((ballCenterX>brick.getXpos()-radius &&
				   ballCenterX<brick.getXpos() && vX>0 ) ||
					(ballCenterX>brick.getXpos()+Squash.brickWidth &&
				   ballCenterX<brick.getXpos()+Squash.brickWidth+radius && vX<0)) && 
				    (ballCenterY>brick.getYpos()-radius &&
				   ballCenterY<brick.getYpos()+Squash.brickHeight+radius) )
				{
					vX=-vX;
					bricks.get(i).setCrashed(true);
					Squash.destroyedBricksCounter--;
				}
				
				if(  ((ballCenterY>brick.getYpos()-radius &&
					ballCenterY<brick.getYpos() && vY>0) ||
					  (ballCenterY>brick.getYpos()+Squash.brickHeight &&
					ballCenterY<brick.getYpos()+Squash.brickHeight+radius && vY<0)) &&
					  (ballCenterX>brick.getXpos() &&
				    ballCenterX<brick.getXpos()+Squash.brickWidth)  )
				{
					vY=-vY;
					bricks.get(i).setCrashed(true);
					Squash.destroyedBricksCounter--;
				}
			}
		}
	}
	
	
	@Override
	public void run() {
		int ballsleep=30;
		while(true){
			
			if(Squash.counter%500==0){
				if(ballsleep>=2){
					ballsleep--;
				}
			}
			if(Squash.counter%1000==0){
				if(gravity<0.1){
					gravity=gravity*2;
				}
			}
			
			move();
			try {
				sleep(ballsleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Ball createNewBall(){
		return new Ball(bricks);
	}
	
	public void move() {
		vY=vY+gravity;
		WallTest();
		CeilingTest();
		brickTeszt();
		PaddleTest();
		xpos+=vX;
		ypos+=vY;
		ballCenterX=xpos+radius;
		ballCenterY=ypos+radius;
	}

	public double getGravity() {
		return gravity;
	}

	public void setGravity(double gravity) {
		this.gravity = gravity;
	}
	
	
	
}
