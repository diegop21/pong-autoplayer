package main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Loop implements Runnable
{
  Thread thread;
  static final String[] gapList = { "none", "press", "release", "click" };
  static final String[] spec_action = { "none", "color" };
  boolean isPlay;
  int pause;
  int setup_pause;
  int stepp;
  int mouse_action_befor;
  int mouse_action_after;
  JFrame frame;
  public Robot robot;
  Vector moves;
  TextArea info;
  JLabel info_pos;
  JButton click_button;
  JComboBox ComboBox_block;
  JComboBox ComboBox_spec_action;
  JComboBox ComboBox_mouse_action_befor;
  JComboBox ComboBox_mouse_action_after;
  JCheckBox always_show_button;
  int last_x;
  int last_y;
  boolean setup;

  public Loop()
  {
    this.setup = true;
    this.setup_pause = 2;
    this.mouse_action_befor = 0;
    this.mouse_action_after = 0;
    KeyListener kl = new KeyListener()
    {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        System.out.println("press " + e.getKeyCode());
        if (e.getKeyCode() == 27) {
          if (Loop.this.setup) {
            Loop.this.isPlay = false;
            Loop.this.frame.dispose();
          } else {
            Loop.this.startsetup();
          }
        } else if (e.getKeyCode() == 82) {
          try {
            Loop.this.read(); } catch (Exception ew) {
            System.out.println("read error");
          }
        }
        else if (e.getKeyCode() == 83) {
          if ((Loop.this.moves != null) && (Loop.this.setup)) {
            Loop.this.startmovin();
          }
          else if (!Loop.this.setup) {
            Loop.this.startsetup();
          }
          else {
            System.out.println("inga moves...");
          }

        }
        else if (e.getKeyCode() == 10) {
          Loop.this.addMovment();
        }
        else if ((e.getKeyCode() >= 48) && (e.getKeyCode() <= 57)) {
          Loop.this.setup_pause = (e.getKeyCode() - 48);
          System.out.println("pause " + (e.getKeyCode() - 48));
        } else if (e.getKeyCode() == 81)
        {
          int i = Loop.this.ComboBox_mouse_action_befor.getSelectedIndex();
          i++;
          if (i >= Loop.gapList.length) i = 0;
          Loop.this.ComboBox_mouse_action_befor.setSelectedIndex(i);
        }
        else if (e.getKeyCode() == 87)
        {
          int i = Loop.this.ComboBox_mouse_action_after.getSelectedIndex();
          i++;
          if (i >= Loop.gapList.length) i = 0;
          Loop.this.ComboBox_mouse_action_after.setSelectedIndex(i);
        } else if (e.getKeyCode() == 69)
        {
          int i = Loop.this.ComboBox_spec_action.getSelectedIndex();
          i++;
          if (i >= Loop.spec_action.length) i = 0;
          Loop.this.ComboBox_spec_action.setSelectedIndex(i);
        }
      }

      public void keyTyped(KeyEvent e)
      {
      }
    };
    this.frame = new JFrame();
    this.frame.setDefaultCloseOperation(3);
    Container content = this.frame.getContentPane();

    content.setLayout(new FlowLayout());
    this.ComboBox_mouse_action_befor = new JComboBox(gapList);
    content.add(this.ComboBox_mouse_action_befor);
    this.ComboBox_mouse_action_befor.addKeyListener(kl);

    this.ComboBox_mouse_action_after = new JComboBox(gapList);
    content.add(this.ComboBox_mouse_action_after);
    this.ComboBox_mouse_action_after.addKeyListener(kl);

    this.ComboBox_spec_action = new JComboBox(spec_action);
    content.add(this.ComboBox_spec_action);
    this.ComboBox_spec_action.addKeyListener(kl);

    this.info_pos = new JLabel("pos");
    this.info_pos.setHorizontalTextPosition(0);
    this.info_pos.setVerticalTextPosition(0);
    this.info_pos.addKeyListener(kl);
    content.add(this.info_pos);
    this.info = new TextArea("enter to save movment\nr to read \ns to start\nrolldowns is in order\nbefor move-after move-wait for colorchange\npress 0-9 for pause after move\nQ-W-E to toggle", 10, 35);
    this.info.setEditable(true);
    content.add(this.info);

    this.click_button = new JButton("Start");
    this.click_button.addKeyListener(kl);
    this.click_button.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        if ((Loop.this.moves != null) && (Loop.this.setup)) {
          Loop.this.startmovin();
        }
        else if (!Loop.this.setup) {
          Loop.this.startsetup();
        }
        else
          System.out.println("inga moves...");
      }
    });
    content.add(this.click_button);

    JButton button2 = new JButton("Read");
    button2.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        try {
          Loop.this.read(); } catch (Exception ew) {
          System.out.println("read error");
        }
      }
    });
    content.add(button2);
    button2.addKeyListener(kl);

    this.always_show_button = new JCheckBox("On topp");
    this.always_show_button.setSelected(true);
    content.add(this.always_show_button);
    this.always_show_button.addKeyListener(kl);

    this.frame.setSize(300, 300);
    this.frame.setFocusable(true);
    this.frame.setTitle("befor-after-colorchange");

    this.info.addKeyListener(kl);
    this.frame.setVisible(true);
    this.ComboBox_mouse_action_after.requestFocus();

    System.out.println("press add");
    this.stepp = 0;
    try {
      this.robot = new Robot();
    }
    catch (AWTException e) {
      e.printStackTrace();
    }
    this.robot.setAutoDelay(10);

    this.isPlay = true;
    this.thread = new Thread(this);
    this.thread.start();
  }

  public void startsetup() {
    this.click_button.setText("Start");
    this.setup = true;
    System.out.println("setup");
  }
  public void startmovin() {
    if (this.moves.size() <= 0) {
      return;
    }

    this.click_button.setText("Stop");

    this.last_x = MouseInfo.getPointerInfo().getLocation().x;
    this.last_y = MouseInfo.getPointerInfo().getLocation().y;
    this.pause = 0;
    this.stepp = 0;
    this.setup = false;
    System.out.println("Start");
  }

  public void run()
  {
    while (this.isPlay)
    {
      if ((!this.frame.isActive()) && (this.always_show_button.getSelectedObjects() != null)) {
        this.frame.toFront();
      }
      if (!this.setup)
        uppdate_movments();
      else
        setupmain();
      try
      {
        Thread.sleep(40 + this.pause * 1000);
        this.pause = 0;
      } catch (InterruptedException ie) {
        this.pause = 0;
      }
    }
  }

  public void setupmain()
  {
    this.info_pos.setText("(" + MouseInfo.getPointerInfo().getLocation().x + ", " + MouseInfo.getPointerInfo().getLocation().y + ") pause " + this.setup_pause);
  }

  public void read()
  {
    this.moves = new Vector();
    String text = this.info.getText();
    int new_line = text.indexOf("\n");
    while ((new_line = text.indexOf("\n")) > 0)
    {
      this.moves.add(new movment(text.substring(0, new_line)));
      text = text.substring(new_line + 1);
    }

    if (text.length() > 3)
    {
      this.moves.add(new movment(text));
    }

    uppdate_info();
  }

  public void addMovment()
  {
    if (this.moves == null) {
      this.moves = new Vector();
    }

    this.moves.add(new movment(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y, this.ComboBox_mouse_action_befor.getSelectedIndex(), this.ComboBox_mouse_action_after.getSelectedIndex(), this.setup_pause, this.ComboBox_spec_action.getSelectedIndex() > 0 ? new trigger(null) : null));
    uppdate_info();
  }
  public void uppdate_info() {
    if (this.moves == null) {
      return;
    }
    String text = "";
    String seperator = "\n";
    for (int i = 0; i < this.moves.size(); i++) {
      movment m = (movment)this.moves.elementAt(i);
      text = text + m.toString() + seperator;
    }
    this.info.setText(text);
  }

  public void uppdate_movments()
  {
    movment m = (movment)this.moves.elementAt(this.stepp);
    if (m.trigger != null) {
      String tmpc = this.robot.getPixelColor(m.x, m.y).getRed() + "," + this.robot.getPixelColor(m.x, m.y).getGreen() + "," + this.robot.getPixelColor(m.x, m.y).getBlue();
      if (m.trigger.color == null) {
        m.trigger.color = tmpc;
        this.info_pos.setText("Running on " + this.stepp + " wait for colorchange");

        return;
      }

      if (m.trigger.color.equals(tmpc))
      {
        this.info_pos.setText("Running on " + this.stepp + " wait for colorchange");
        return;
      }
      m.trigger.color = null;
    }

    this.info_pos.setText("Running on " + this.stepp);

    if (m.befor == 3) {
      this.robot.mousePress(16);
      this.robot.mouseRelease(16);
    } else if (m.befor == 1) {
      this.robot.mousePress(16);
    } else if (m.befor == 2) {
      this.robot.mouseRelease(16);
    }
    this.robot.mouseMove(m.x, m.y);
    if (m.after == 3) {
      this.robot.mousePress(16);
      this.robot.mouseRelease(16);
    } else if (m.after == 1) {
      this.robot.mousePress(16);
    } else if (m.after == 2) {
      this.robot.mouseRelease(16);
    }
    this.pause = m.pause;

    this.stepp += 1;
    if (this.stepp >= this.moves.size())
      this.stepp = 0;
  }

  public void uppdate()
  {
    if (this.stepp == 0) {
      this.robot.mouseMove(981, 737);
      this.robot.mousePress(16);
      this.robot.mouseRelease(16);
      this.pause = 2;
    }
    else if (this.stepp == 1) {
      this.robot.mouseMove(955, 351);
      this.robot.mousePress(16);
      this.robot.mouseRelease(16);
      this.pause = 2;
    }
    else if (this.stepp == 2) {
      this.robot.mouseMove(873, 745);
      this.robot.mousePress(16);
      this.robot.mouseMove(1070, 444);
      this.robot.mouseRelease(16);
      this.pause = 2;
    }
    else if (this.stepp == 3) {
      this.robot.mouseMove(1070, 444);

      this.robot.mousePress(16);
      this.robot.mouseMove(873, 745);
      this.robot.mouseRelease(16);
      this.pause = 2;
    }
    else if (this.stepp == 3) {
      this.robot.mouseMove(929, 810);

      this.robot.mousePress(16);
      this.robot.mouseRelease(16);
      this.pause = 8;
    }
    else
    {
      this.stepp = 0;
      return;
    }

    this.stepp += 1; } 
  public class movment { public static final int press = 1;
    public static final int release = 2;
    public static final int click = 3;
    public static final int none = 0;
    public static final int new_color = 1;
    Loop.trigger trigger;
    int x;
    int y;
    int befor;
    int after;
    int pause;

    public movment(int x, int y, int befor, int after, int pause, Loop.trigger t) { this.x = x;
      this.y = y;
      this.befor = befor;
      this.after = after;
      this.pause = pause;
      this.trigger = t; }

    public movment(String t)
    {
      System.out.println("fick " + t);

      this.x = Integer.parseInt(t.substring(0, t.indexOf(",")));
      this.y = Integer.parseInt(t.substring(t.indexOf(",") + 1, t.indexOf(" ")));
      int nex = t.indexOf(" ") + 1;
      t = t.substring(nex);
      nex = t.indexOf(" ") + 1;
      String tmp = t.substring(0, nex - 1);
      System.out.println("befor " + tmp);
      if (tmp.equals("none"))
        this.befor = 0;
      else if (tmp.equals("press"))
        this.befor = 1;
      else if (tmp.equals("release"))
        this.befor = 2;
      else if (tmp.equals("click")) {
        this.befor = 3;
      }

      t = t.substring(nex);
      nex = t.indexOf(" ") + 1;
      tmp = t.substring(0, nex - 1);

      if (tmp.equals("none"))
        this.after = 0;
      else if (tmp.equals("press"))
        this.after = 1;
      else if (tmp.equals("release"))
        this.after = 2;
      else if (tmp.equals("click")) {
        this.after = 3;
      }

      t = t.substring(nex);
      nex = t.indexOf(" ") + 1;
      tmp = t.substring(0, nex - 1);
      this.pause = Integer.parseInt(tmp);

      t = t.substring(nex);

      if (t.equals("none"))
        this.trigger = null;
//      else
//        this.trigger = new Loop.trigger(Loop.this, null);
    }

    public String toString()
    {
      return this.x + "," + this.y + " " + (this.befor == 1 ? "press" : "") + (this.befor == 2 ? "release" : "") + (this.befor == 3 ? "click" : "") + (this.befor <= 0 ? "none" : "") + " " + (this.after == 1 ? "press" : "") + (this.after == 2 ? "release" : "") + (this.after == 3 ? "click" : "") + (this.after <= 0 ? "none" : "") + " " + this.pause + " " + (this.trigger != null ? "color" : "none");
    }
  }

  public class trigger
  {
    String color;

    public trigger(String c)
    {
      this.color = c;
    }
  }
}