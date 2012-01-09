package main;

public class MainEntry {
	public static void main(String[] args)
	  {
		System.out.println("start piong");
	    pongThread2 l = new pongThread2();
	    System.out.println("started piong");
	    System.out.println("start thread");
	    l.start();
	    System.out.println("started thread");
	  }
}
