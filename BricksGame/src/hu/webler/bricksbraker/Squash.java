/*
    Squash.java
    Copyright (C) <2017>  <Zoltan Nagy>

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
    
    This is the main program jun can compile and run with java.
    
    If you have any questions please send me an email to the following address:
    nagyzoli.g@gmail.com
    
    Or if you have any comments feel free to write me directly on github issues
    
*/

// ===========================================================================
/*
 * This is a simple bricks breaker program written in java. Hopefully somebody will find
 * it useful.
 * 
 * Thanks for the Webler educational studio's teacher Istvan Bakos to explain, and teach the more simpler version of this program and the from
 * Princeton University Mr. Sedgewick And Mr. Kevin Wayne for publishing the Algorithms I 4th Edition book's code.
 * 
 * The original task was not to write a bricks breaker program, but a single ball bounce program with a paddle at the educational studio.
 * However I thought it would be useful for learning purposes to 
 * write a bricks breaker for practicing java, and graphics.
 * 
 * The code uses the Princeton University's Algorithms 4th Edition book's stdDraw.java
 * class to create the animation part.
 * Methods used from StdDraw.java class:
 *  - Static classes, static frames onscreen and offscreen BufferedImages.
 *  - Using the synchronized method for the keyPressed and keyReleased method from the book code.
 *  - Using the static object drawing functions.
 *  - Double-buffering
 *  - static method for initialization (init), callback function for init static class
 *
 * Ball, Brick and Paddle are different objects. Paddle is a static method,
 * it is always stored in memory while we are playing the game. Ball, and brick classes
 * are threaded to have their own Thread sleep parameter. 
 * Ball class has a parameter, a vector with brick objects. This way the ball and brick
 * thread will be "synchronized", so the same thread will be making to move the balls and animate
 * the bricks on the canvas.
 * 
 * The bouncing with the ball and the paddle is specified by a special mathematical formula
 * highly detailed
 * 
 * The sound is from a stackoverflow answer also in the ball class.
 * On Windows systems it is stable, but on linux after a time the sound freezes, but the game works
 * anyway
 * 
 */

package hu.webler.bricksbraker;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.*;

import java.net.URL;
import java.security.Key;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.LineEvent.Type;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class Squash implements KeyListener{

    private static BufferedImage onscreenImage, offscreenImage;
    private static Graphics2D onscreen, offscreen;
    private static JFrame frame;
    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 700;
    static final Color DEFAULT_GAME_COLOR=new Color(240,240,240);
    private static final Color DEFAULT_PADDLE_COLOR = Color.BLACK;
    private static final Color DEFAULT_BALL_COLOR = Color.RED;
    private static final Color DEFAULT_BALL_BORDER_COLOR = Color.BLACK;
    private static final Color DEFAULT_GAMETABLE_BORDER_COLOR = Color.BLACK;
    static final Color DEFAULT_BRICK_COLOR = new Color(120,120,120);
    static final Color DEFAULT_BRICK_BORDER_COLOR = Color.BLACK;
    private static int width  = DEFAULT_WIDTH;
    private static int height = DEFAULT_HEIGHT;
    private static final int DEFAULT_PADDLE_WIDTH = 100;
    private static final int DEFAULT_PADDLE_HEIGHT = 20;
    private static final int DEFAULT_PADDLE_PADDING = 5;
    private static int paddleWidth = DEFAULT_PADDLE_WIDTH; //paddle with (default)
    private static int paddleHeight = DEFAULT_PADDLE_HEIGHT; //default paddle height
    private static int paddlePadding = DEFAULT_PADDLE_PADDING; //additional distance for the paddle from the bottom of the canvas 
    private static final double DEFAULT_PADDLE_SPEED=10; //default speed of the paddle
    private static double paddleSpeed=DEFAULT_PADDLE_SPEED; //speed of the paddle, set with sleep in the ball object
    private static final double DEFAULT_GRAVITY=0.005;
    private static double g = DEFAULT_GRAVITY; //gravity parameter, set 0.005 by default
    public static final int NUMBER_OF_MAX_BALLS=4; //maximum  numbber of balls 
    static int brickWidth=80, brickHeight=20; //default size of the brick
    static boolean startGame = true; //starting the game, (always be true first)
    private static Squash std = new Squash();  //call back, to get instance
    private static Object keyLock = new Object(); //key lock to synchronize
    private static TreeSet<Integer> keysDown = new TreeSet<Integer>(); // keydown opject for synchronization
    static int counter=0; //game frame counter 
    static Vector<Ball> balls = new Vector<>(); //storing the balls.
    static Vector<Brick> bricks=new Vector<>(); //bricks static vector, for storing bricks
    static int destroyedBricksCounter=0; // number of bricks to destroy
    static boolean isMusic=true; //play music meanwhile 
    public static boolean eternalLife=false; //the game restart when all of the bricks are destroyed, but it never ends until
    
    
    
    public static void setoffColor(Color color){
    	offscreen.setColor(color); 
    }
    
    public static void setonColor(Color color){
    	onscreen.setColor(color); 
    }
    
    public static double getGravity(){
    	return g;
    }    
    
    public static double getPaddleSpeed(){
    	return paddleSpeed;
    }
    public static int getPaddleWidth(){
    	return paddleWidth;
    }
    public static int getPaddleHeight(){
    	return paddleHeight;
    }
    public static int getPaddlePadding(){
    	return paddlePadding;
    }
    
    public static int getWidth(){
    	return width;
    }
    
    public static int getHeight(){
    	return height;
    }
    
    
    public static void filledRectangle(double x, double y, int w, int h){
    	offscreen.setColor(DEFAULT_PADDLE_COLOR);
    	offscreen.fill(new Rectangle2D.Double(x, y, w, h));
    }
    
    public static void brickFilledRectangle(double x, double y, int w, int h){
    	offscreen.fill(new Rectangle2D.Double(x, y, w, h));
    }
    public static void brickRectangle(double x, double y, int w, int h) {
    	offscreen.draw(new Rectangle2D.Double(x, y, w, h));
    }
    
    public static void rectangle(double x, double y, int w, int h) {
    	offscreen.setColor(DEFAULT_PADDLE_COLOR);
    	offscreen.draw(new Rectangle2D.Double(x, y, w, h));
    }
    
    
    public static void filledCircle(double x, double y, int r){
    	offscreen.setColor(DEFAULT_BALL_COLOR);
    	offscreen.fill(new Ellipse2D.Double(x, y, 2*r, 2*r));
    	
    }
    
    public static void circle(double x, double y, int r) {
    	offscreen.setColor(DEFAULT_BALL_BORDER_COLOR);
    	offscreen.draw(new Ellipse2D.Double(x, y, 2*r, 2*r));
    	
    }
    
    public static void drawBorder() {
    	onscreen.setColor(DEFAULT_GAMETABLE_BORDER_COLOR);
	onscreen.drawRect(0,0,width-1,height-1);
    }
    
    public static void show() {
	onscreen.drawImage(offscreenImage, 0, 0, null);
	drawBorder();
        frame.repaint();
    }
    
    public static void clear() {
        clear(DEFAULT_GAME_COLOR);
    }
    
    public static void clear(Color color) {
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
    }
    
    public static void pause(int t) {
        try {
            Thread.sleep(t);
        }
        catch (InterruptedException e) {
            System.out.println("Error sleeping");
        }
    }
    
    
	
    
 
    
    public static void init() {
    	frame=new JFrame("Bricks-Game");
    	frame.setMinimumSize(new Dimension(width,height));
    	
    	onscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		offscreenImage= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		offscreen=offscreenImage.createGraphics();
		onscreen=onscreenImage.createGraphics();
		
		
		onscreen.setColor(DEFAULT_GAME_COLOR);
		onscreen.fillRect(0, 0, width, height);
		clear();
		
		
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		offscreen.addRenderingHints(hints);
		
		ImageIcon icon = new ImageIcon(onscreenImage);
		JLabel draw = new JLabel(icon);
		frame.setContentPane(draw);
		frame.addKeyListener(std);
        	frame.pack();
        	frame.requestFocusInWindow();
		frame.setVisible(true);
		
	}
    
    

	public static boolean isKeyPressed(int keycode) {
		if(Squash.startGame){
			pause(1000);
		}
        	synchronized (keyLock) {
            		return keysDown.contains(keycode);
        	}
    }
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyPressed(KeyEvent e) {
		if(Squash.startGame){
			pause(1000);
			ballThread.start();
			Squash.startGame=false;
		}
		synchronized (keyLock) {
	            keysDown.add(e.getKeyCode());
	        }
		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		synchronized (keyLock) {
        	    keysDown.remove(e.getKeyCode());
        	}
	}
	
	public static void addBall() {
		if(counter%700==0 && balls.size()<NUMBER_OF_MAX_BALLS && !Squash.startGame){
			Ball newBall = ball.createNewBall();
			newBall.start();
			balls.add(newBall);
		}
		
	}

	public static void createBricks(){
    	double x0=50;
    	double y0=50;
    	double x=x0;
    	double y=y0;
    	for(int j=0;j<10;j++){
    		for(int i=0;i<(int)((Squash.getWidth()-2*50)/Squash.brickWidth);i++){	   
    			Brick brick=new Brick(x,y);
    			brick.setCrashed(false);
    			bricks.add(brick);
    			destroyedBricksCounter++;
    			x+=Squash.brickWidth;
    		}
    		x=x0;
    		y+=Squash.brickHeight;
    	}
    }


    static Paddle paddle;
    static Thread ballThread;
    static Thread paddleThread;
    static Ball ball;
    static Brick brick;
	
	
	public static void restartGame(){
		Squash.startGame=true;
		balls.clear();
		paddle.setInitialPosition();
		ball=null;
		ballThread=null;
		bricks.clear();
		bricks.trimToSize();
		Squash.bricks=null;
		Squash.bricks=new Vector<>(); //I had to destroy the bricks vector and create it once again, because while playing I didn't pop from the vector the destroyed bricks, but instead just not animating them.
		Squash.createBricks();
		ball=new Ball(Squash.bricks);
		ballThread=new Thread(ball);
		balls.add(ball);
		Squash.counter=0;
	}
	
	public static void main(String[] args) {
		init();
		paddle = new Paddle();
		Squash.createBricks();
		ball = new Ball(Squash.bricks);
		ballThread = new Thread(ball);
		balls.add(ball);
		
		while(true){
			Squash.clear();
			
			Squash.counter++;
			
			addBall();
			
			paddle.draw();
			
			for(Brick brick : bricks){
				if(!brick.isCrashed()){
					brick.draw();
				}
			}
			
			for(Ball b : balls){
				if(!Squash.eternalLife){
					if(b.utoAlattTeszt()){
						System.out.println("YOU LOST!");
						restartGame();
						break;
					}
					if(destroyedBricksCounter==0){
						System.out.println("YOU WON!");
						restartGame();
						break;
					}
				}else{
					if(destroyedBricksCounter==0){
						restartGame();
						break;
					}
				}
				
				b.draw();
			}
			
			show();
			pause(5);
			
		}
		
	}


}
