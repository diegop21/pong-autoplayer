package main;


import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * 
 * @author Christofer Johansson Hiitti
 *	This is a pong autoplayer! It localizises the ball (white ball) and 
 *	moves the mouse to where the ball will go.
 *	WARNING! This code is ROUGH!!! I made it in short time for fun, dont expect to much of it...
 */
public class pongThread2 implements Runnable{
	JFrame frame;
	Robot robot;
	JLabel info;
	  JLabel info_pos;
	  Thread thread;
//	  int Yoffset=1050; //My extra monitor makes java go crazzy..
	  int Yoffset=0;
	  int ballSize=20;
	  boolean saveImage=false;
	  boolean scanImage=false;
	  int guessSize=50;
	private boolean isPlay;
	private JCheckBox always_show_button;
	boolean GameOn=false;
	boolean moveMouse=false;
	boolean lastFoundWasUp=false;
	boolean goingUp=false;
	paintPanel pP;
	//GAME
	Rectangle board=new Rectangle(0,0,300,300);
	public pongThread2(){
		
		 try {
		      this.robot = new Robot();
		    }
		    catch (AWTException e) {
		      e.printStackTrace();
		    }
		 initUI();
		 
		 

		 
	}
	public void start(){
		thread=new Thread(this);
		 thread.start();
		 isPlay=true;
	}
	
    class paintPanel extends JPanel {
    	int k=0;
    	Pos lastFindBall=null;
    	Pos oldBall=null;
    	
        public void paintComponent(Graphics g) {
            //call super paintComponent
            super.paintComponent(g);

            	
            //cast to Graphics2D
            Graphics2D g2 = (Graphics2D) g;
            BufferedImage image;
            if (board==null || board.width<0){
            	image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB); 
//            	
            	g2.drawString ("The CustomCanvas is in the CENTER area "+k, 10, 10);
            }else {
            image=robot.createScreenCapture(board);
            if (GameOn && scanImage){
//            	scanImage=false;
            	System.out.println("looking for ball!");
            	long tb=System.currentTimeMillis();
            	Pos ball=findBall(image,lastFindBall);
        		
            	if (ball!=null){
            		System.out.println("FoundBall!");
            		oldBall=lastFindBall;
            		lastFindBall=ball;
            	}else {
            		System.out.println("did not find ball...");
            	}
            	System.out.println("time to search ball took "+(System.currentTimeMillis()-tb));
            }
            }
            
            
//            //Draw BufferedImage
            g2.drawImage(image, 0, 0, null);
            g2.setColor(Color.red);
            if (lastFindBall!=null){
            	int x=lastFindBall.x+board.x;
            	g2.fillRect(lastFindBall.x-1, lastFindBall.y-1, 3, 3);
            	
            	if (oldBall!=null){
            		g2.setColor(Color.blue);
            		g2.fillRect(oldBall.x-1, oldBall.y-1, 3, 3);
            	}
            	
            	//we can do bbetter!
            	if (oldBall!=null && lastFindBall.y<oldBall.y){
            		//Not used now :/
            		int AB = Math.abs(lastFindBall.x-oldBall.x)*Math.abs(board.y-oldBall.y)/Math.abs(lastFindBall.y-oldBall.y);
            		if (lastFindBall.x>oldBall.x){
//            			x+=ballSize*2;
//            			x=AB+oldBall.x+board.x;
            			x+=4*Math.abs(oldBall.x-lastFindBall.x);
            		}else {
//            			x-=ballSize*2;
//            			x=oldBall.x-AB+board.x;
            			x-=4*Math.abs(oldBall.x-lastFindBall.x);
            		}
//            		System.out.println("AB "+AB+ " x "+x);
//            		
//            		if (x>board.x+board.width){
//            			System.out.println("x>RCorner.x");
//            			x =board.x+board.width-(x-(board.x+board.width));
//            		}else if (x<board.x){
//            			System.out.println("x<LCorner.x");
//            			x =board.x+(board.x-x);
//            		}
            		
            		if (x>board.x+board.width){
        			System.out.println("x>RCorner.x");
//        			x =board.x+board.width-(x-(board.x+board.width));
        			x=board.x+board.width-2;
        		}else if (x<board.x){
        			System.out.println("x<LCorner.x");
//        			x =board.x+(board.x-x);
        			x=board.x+2;
        		}
            		
            		g2.setColor(Color.gray);
            		g2.drawLine(oldBall.x, oldBall.y, x-board.x, 0);
            		
            	}
            	
            	
            	if (moveMouse)
            		robot.mouseMove(x, getCurrentPos().y);
            }
            g2.dispose();
        }
    } 
    public Pos findBall(BufferedImage image,Pos guess){
    	if (guess!=null){
    	int sy=guess.y-guessSize;
    	int sx=guess.x-guessSize;
    	int ey=guess.y+guessSize;
    	int ex=guess.x+guessSize;
    	
    	if (sy<0)
    		sy=0;
    	if (sx<0)
    		sx=0;
    	if (ey>=image.getHeight())
    		ey=image.getHeight();
    	if (ex>=image.getWidth())
    		ex=image.getWidth();
    	
    	
    	for(int y=sy;y<ey;y+=ballSize){
        		for(int x=ballSize;x<ex;x+=ballSize){
        			if (image.getRGB(x, y)==-1){
        				System.out.println("is this the ball?! one guess!");
        				Pos pos = isTouchBall(image,new Pos(x,y));
        				if (pos!=null){//found the ball!
        					System.out.println("it was! on guess! :D");
        					return pos;
        				}else {
        					System.out.println("it was not...");
        				}
        			}
        		}
        	}
    	}
    	
    	for(int y=0;y<image.getHeight();y+=ballSize){
//    	for(int y=image.getHeight()-1;y>0;y-=ballSize){
    		for(int x=ballSize;x<image.getWidth();x+=ballSize){
    			if (image.getRGB(x, y)==-1){
    				System.out.println("is this the ball?!");
    				Pos pos = isTouchBall(image,new Pos(x,y));
    				if (pos!=null){//found the ball!
    					System.out.println("it was!");
    					return pos;
    				}else {
    					System.out.println("it was not...");
    				}
    			}
    		}
    	}
    	return null;
    }
    
    public Pos isTouchBall(BufferedImage image,Pos pos){
    	List<Pos> whitePos = new ArrayList<Pos>();
    	List<String> whitePosString = new ArrayList<String>();
	whitePos.add(pos); //first pos

	Queue<Pos> que =new LinkedList<Pos>();
			que.add(pos);
			Pos parent;
			while (!que.isEmpty()){
				parent=que.poll();
				List<Pos> childs=new ArrayList<Pos>();
				if (parent.x-1>=0)				
					childs.add(new Pos(parent.x-1,parent.y));
				if (parent.x+1<image.getWidth())				
					childs.add(new Pos(parent.x+1,parent.y));
				if (parent.y-1>=0)				
					childs.add(new Pos(parent.x,parent.y-1));
				if (parent.y+1<=image.getHeight())				
					childs.add(new Pos(parent.x,parent.y+1));

				if (parent.x-1>=0 && parent.y-1>=0)				
					childs.add(new Pos(parent.x-1,parent.y-1));
				if (parent.x+1<image.getWidth() && parent.y-1>=0)				
					childs.add(new Pos(parent.x+1,parent.y-1));
				if (parent.y+1<=image.getHeight() && parent.x-1>=0)				
					childs.add(new Pos(parent.x-1,parent.y+1));
				if (parent.y+1<=image.getHeight() && parent.x+1<image.getWidth())				
					childs.add(new Pos(parent.x+1,parent.y+1));

				for (Pos child:childs){
					if (child.x<0 || child.y<0 || child.x>=image.getWidth() || child.y>image.getHeight())
						{}else{
					
					if (!whitePosString.contains(child.getString()) && (image.getRGB(child.x, child.y)==-1)){
						que.add(child);
						whitePos.add(child);
						whitePosString.add(child.getString());
//						System.out.println("ball size is "+whitePos.size());
						if (whitePos.size()>259)
							return null;
					}
						}
					
				}
				
				
			}
			if (whitePos.size()<240){
				System.out.println("not the ball!");
				return null;
			}
			System.out.println("tot ball size is "+whitePos.size());
			Collections.sort(whitePos);
			Pos posFirst=whitePos.get(0);
			Pos posLast=whitePos.get(whitePos.size()-1);
			int w=posLast.x-posFirst.x;
			int h=posLast.y-posFirst.y;
			
    	return new Pos(posFirst.x+w/2,posFirst.y+h/2);
    }
    

	public void initUI(){
		 this.frame = new JFrame();
		    this.frame.setDefaultCloseOperation(3);
		    Container content = this.frame.getContentPane();

		    this.info_pos = new JLabel("pos");
		    this.info = new JLabel("info");

		    
		    this.always_show_button = new JCheckBox("On topp");
		    this.always_show_button.setSelected(false);
  
		    KeyListener kl = new KeyListener()
		    {
		      public void keyPressed(KeyEvent e) {
		    	  if (e.getKeyChar()=='a'){
		    		  
		    		  if (board==null){
		    			  board=new Rectangle(getCurrentPos().x,getCurrentPos().y,-1,-1);
				      }else {
				    	  board.width	= getCurrentPos().x-board.x;
				    	  board.height 	= getCurrentPos().y-board.y; 
				      }
		    		  
		    	  }else if (e.getKeyChar()=='s' && board.height>0){
		    		  GameOn=!GameOn;
		    		  
		    	  }else	if (e.getKeyChar()=='1'){
		    		  board.x=getX();
		    		  board.y=getY();
		    	  }else	if (e.getKeyChar()=='2'){
		    		  board.width	= getCurrentPos().x-board.x;
			    	  board.height 	= getCurrentPos().y-board.y; 
		    	  }else	if (e.getKeyChar()=='c'){
		    		 scanImage=!scanImage;
		    	  }else	if (e.getKeyChar()=='m'){
			    		 moveMouse=!moveMouse;
			    	  }
		    	  
		    	  if (board==null){
			    	  info.setText("hoover on top left corner, press a");
			      }else if (board.height<0){
			    	  info.setText("hoover on down right corner, press a");
			      }else {
			    	  info.setText((GameOn?"gameON":"gameNoton")+" "+(moveMouse?"moveMouse":"notmoveMouse")+" "+(scanImage?"scanimage":"notscan"));

			      }
		      }

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			};

			pP=new paintPanel();
			frame.add (info_pos, BorderLayout.NORTH);
			frame.add (info, BorderLayout.SOUTH);
			frame.add (pP, BorderLayout.CENTER);
			
			
		    frame.addKeyListener(kl);
		    this.frame.setSize(600, 400);
		    this.frame.setFocusable(true);
		    this.frame.setTitle("befor-after-colorchange");
		    this.frame.setVisible(true);
	}
	@Override
	public void run() {
		
		 while (this.isPlay)
		    {
		      if ((!this.frame.isActive()) && (this.always_show_button.getSelectedObjects() != null)) {
		        this.frame.toFront();
		      }
		      
		      if (!GameOn){
		      this.info_pos.setText("(" + getX() + ", " + getY() + ") "+getRGBColorAtMouse()+ " , "+getHexColorAtMouse()+ " , "+getColorAtMouse().getRGB());
		      
		      }else {

		    	  pP.repaint();
		      }
		      
		      
		      try
		      {
		        Thread.sleep(1); //yeah... eating up system! :S
		        
		      } catch (InterruptedException ie) {
		        
		      }
		    }
	}
	//For debugging
	private void saveImage(BufferedImage bf,String name) {
		 try {
	 File outputfile = new File(name);
	    ImageIO.write(bf, "png", outputfile);
	    System.out.println("Saved! "+ outputfile.getAbsolutePath());
	} catch (IOException e) {
	    System.out.println("error! "+ e.getMessage());
	}
	}


	
	public int getY(){
		return MouseInfo.getPointerInfo().getLocation().y-Yoffset;
	}
	public int getX(){
		return MouseInfo.getPointerInfo().getLocation().x;
	}
	public String getHexColorAtMouse(){
		return colorToHex(getColorAtMouse());
		}
	public String getRGBColorAtMouse(){
		Color color = getColorAtMouse();
		return color.getRed()+","+color.getGreen()+","+color.getBlue();
		}
	public Color getColorAtMouse(){
		return getColor(getX(), getY()-2);
	}
	public Color getColor(int x, int y){
		Color color=null;
		BufferedImage bf=robot.createScreenCapture(new Rectangle(x,y,1,1));
		color=new Color(bf.getRGB(0, 0));
//		return robot.getPixelColor(x, y);
		return color;
	}
	public boolean isBall(int x,int y){
		return robot.getPixelColor(x, y).getRGB()==-1;
	}
	public String colorToHex(Color color){
		return Integer.toHexString(color.getRGB());
	}
	public Pos getCurrentPos(){
		return new Pos(getX(),getY());
	}
	class Pos implements Comparable<Pos>{
		int x,y;
		public Pos(int x,int y){
			this.x=x;
			this.y=y;
		}
		public String getString(){
			return "("+this.x+","+this.y+")";
		}
		@Override
		public int compareTo(Pos o) {
			if (this.y==o.y && this.x==o.x)
				return 0;
			if (this.y<o.y){
				return -1;
			}else if (this.y>o.y){
				return 1;
			}
			return (this.x<o.x?-1:1);
		}
	}
}
