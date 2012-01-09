package main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
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

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class pongThread implements Runnable{
	JFrame frame;
	Robot robot;
	TextArea info;
	  JLabel info_pos;
	  Thread thread;
	  int Yoffset=1050;
	  int ballSize=3;
	  boolean saveImage=true;
	private boolean isPlay;
	private JCheckBox always_show_button;
	boolean GameOn=false;
	boolean lastFoundWasUp=false;
	boolean goingUp=false;
	//GAME
	Pos LCorner = null;
	Pos RCorner = null;
	Pos Hlower = null;
	Pos Hhigher = null;
	Pos ballPosL =null;
	Pos ballPosH =null;
	
	public pongThread(){
		
		 try {
		      this.robot = new Robot();
		    }
		    catch (AWTException e) {
		      e.printStackTrace();
		    }
		 initUI();
		 
		 
//		 try {
//			 BufferedImage bf=robot.createScreenCapture(new Rectangle(0,0,100,100));
//		 File outputfile = new File("saved.png");
//		    ImageIO.write(bf, "png", outputfile);
//		    System.out.println("Saved! "+ outputfile.getAbsolutePath());
//		} catch (IOException e) {
//		    System.out.println("error! "+ e.getMessage());
//		}
		 
	}
	public void start(){
		thread=new Thread(this);
		 thread.start();
		 isPlay=true;
	}
	public void initUI(){
		 this.frame = new JFrame();
		    this.frame.setDefaultCloseOperation(3);
		    Container content = this.frame.getContentPane();

		    content.setLayout(new FlowLayout());
		    this.info_pos = new JLabel("pos");
		    this.info_pos.setHorizontalTextPosition(0);
		    this.info_pos.setVerticalTextPosition(0);
		    content.add(this.info_pos);
		    this.info = new TextArea("helooo", 10, 35);
		    this.info.setEditable(false);
		    info.setText("hoover on top left corner, press a");
		   
		    content.add(this.info);
		    JButton click_button = new JButton("Reset");
		    
		    click_button.addActionListener(new ActionListener()
		    {
		      public void actionPerformed(ActionEvent e) {
		        
		          System.out.println("Reset!");
		          LCorner = null;
		      	 RCorner = null;
		      	 Hlower = null;
		      	 Hhigher = null;
		      	 ballPosL =null;
		      	 ballPosH =null;
		      	GameOn=false;
		    	 lastFoundWasUp=false;
		    	 goingUp=false;
		          start();
		      }
		    });
		    content.add(click_button);
		    
		    this.always_show_button = new JCheckBox("On topp");
		    this.always_show_button.setSelected(false);
		    content.add(this.always_show_button);
		    
		    
		    KeyListener kl = new KeyListener()
		    {
		      public void keyPressed(KeyEvent e) {
		    	  if (e.getKeyChar()=='a'){
		    		  
		    		  if (LCorner==null){
				    	  LCorner=getCurrentPos();
				      }else if (RCorner==null){
				    	  RCorner=getCurrentPos();
				      }else if (Hlower==null){
				    	  Hlower=getCurrentPos();
				      }else if (Hhigher==null){
				    	  Hhigher=getCurrentPos();
				      }
		    		  
		    	  }else if (e.getKeyChar()=='s' && Hhigher!=null){
		    		  GameOn=!GameOn;
		    		  
		    	  }
		    	  if (LCorner==null){
			    	  info.setText("hoover on top left corner, press a");
			      }else if (RCorner==null){
			    	  info.setText("hoover on top right corner, press a");
			      }else if (Hlower==null){
			    	  info.setText("hoover on lover check height, press a");
			      }else if (Hhigher==null){
			    	  info.setText("hoover on higher check height, press a");
			      }else {
			    	  info.setText("to start! Press s");
//			    	  GameOn=true;
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
			 info.addKeyListener(kl);
		    frame.addKeyListener(kl);
		    this.frame.setSize(300, 300);
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
		    	  
		    	  trackBall();
		    	  
		      }
		      
		      
		      try
		      {
		        Thread.sleep(1);
		        
		      } catch (InterruptedException ie) {
		        
		      }
		    }
	}
	private void saveImage(BufferedImage bf,String name) {
		 try {
	 File outputfile = new File(name);
	    ImageIO.write(bf, "png", outputfile);
	    System.out.println("Saved! "+ outputfile.getAbsolutePath());
	} catch (IOException e) {
	    System.out.println("error! "+ e.getMessage());
	}
	}
	public void trackBall(){
//		long tb=System.currentTimeMillis();
		int lowerx=-1;
//		if (ballPosL==null){
			lowerx=findBallOnLine(Hlower);
//		}
//		System.out.println("search took "+(System.currentTimeMillis()-tb));
		if (lowerx>0){
			info.setText("found ball on lower "+lowerx);
//			robot.mouseMove(lowerx, Hlower.y);
			ballPosL=new Pos(lowerx,Hlower.y);
			if (lastFoundWasUp){
				goingUp=false;
			}else{
				goingUp=true;
			}
			lastFoundWasUp=false;
		}else {
			//check higher
			int higherx=findBallOnLine(Hhigher);
			if (higherx>0){
				info.setText("found ball on higherx "+higherx);
//				robot.mouseMove(higherx, Hlower.y);
				ballPosH=new Pos(higherx,Hhigher.y);
				if (lastFoundWasUp){
					goingUp=false;
				}else{
					goingUp=true;
				}
				lastFoundWasUp=true;
			}
			
		}
		if (ballPosH==null || ballPosL==null){
			return;
		}
		
		if (!goingUp){
			System.out.println("going down!");
			return;
		}
		
		//calc where it should end up!
//		double angle = Math.atan2(-ballPosH.y+ballPosL.y, -ballPosH.x+ballPosL.x);
//		System.out.println("going up, angle "+(angle*360/Math.PI));
		
		int AB = Math.abs(ballPosH.x-ballPosL.x)*Math.abs(LCorner.y-ballPosL.y)/Math.abs(ballPosH.y-ballPosL.y);
		int x=0;
		if (ballPosH.x>ballPosL.x){
			x=AB+ballPosL.x;
		}else {
			x=ballPosL.x-AB;
		}
		System.out.println("AB "+AB+ " x "+x);
		
		if (x>RCorner.x){
			System.out.println("x>RCorner.x");
			x =RCorner.x-(x-RCorner.x);
		}else if (x<LCorner.x){
			System.out.println("x<LCorner.x");
			x =LCorner.x+(LCorner.x-x);
		}
		ballPosH=null;ballPosL=null;
		
		robot.mouseMove(x, getCurrentPos().y);
		
	}
	
	public int findBallOnLine(Pos pos){
		int w=RCorner.x-LCorner.x;
		BufferedImage bf=robot.createScreenCapture(new Rectangle(LCorner.x,pos.y,w,1));
		
		for (int i=0;i<w;i+=ballSize){
			if (bf.getRGB(i, 0)==-1){
				int j=0;
				i-=ballSize;
				if (i<0){
					i=0;
				}
				while (bf.getRGB(i, 0)!=-1){
					i++;
				}
				
				while ((i+j)<w && bf.getRGB(i+j, 0)==-1){
					j++;
				}
				
				
				return (i+j/2)+LCorner.x;
			}
		}
		return -1;
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
	class Pos{
		int x,y;
		public Pos(int x,int y){
			this.x=x;
			this.y=y;
		}
	}
}
