package code;

import static code.ProjectConstants.*;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.*;
import javax.swing.*;

import code.SoundManager.Sounds;


/*The game window.*/
public class GameWindow extends JFrame
{
	
	private GraphicsDevice dev;
	private TetrisPanel t;
	
	
	/*Creates a GameWindow, by default.*/
	public GameWindow(boolean useSounds, boolean useAI, boolean useGenetic)
	{
		this(STARTFS, null, useSounds, useAI, useGenetic);
	}
	
	
	/*Creates a GameWindow and make it fullscreen or not.
	 * May be from another GameWindow.*/
	public GameWindow(boolean fullscreen, GameWindow old, boolean useSounds, boolean useAI, boolean useGenetic)
	{
		super();
		if(fullscreen)
		{
			createFullscreenWindow(old, useSounds, useAI, useGenetic);
		}
		
		else createWindow(old, useSounds, useAI, useGenetic);
		
		if(old!=null)
		{
			//Cleanup
			old.setVisible(false);
			old.dispose();
			old = null;
		}
	}
	
	
	
	private void createWindow(GameWindow old, boolean useSounds, boolean useAI, boolean useGenetic)
	{
		setUndecorated(false);
		setTitle("JTetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		if(old != null)
			t = old.t;
		else t = new TetrisPanel(useSounds, useAI, useGenetic);
		
		t.setPreferredSize(new Dimension(800,600));
		setContentPane(t);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		if(old==null)
			t.engine.startengine();
	}
	
	
	
	private void createFullscreenWindow(GameWindow old, boolean useSounds, boolean useAI, boolean useGenetic)
	{
		setUndecorated(true);
		setTitle("JTetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		
		if(old != null)
			t = old.t;
		else t = new TetrisPanel(useSounds, useAI, useGenetic);
		
		t.setPreferredSize(new Dimension(800,600));
		setContentPane(t);
		
		try{
			dev =  GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			dev.setFullScreenWindow(this);
			dev.setDisplayMode(new DisplayMode
					(800,600,32,DisplayMode.REFRESH_RATE_UNKNOWN));
		}catch(Throwable t){
				throw new RuntimeException("Getting screen device failed");
		}
		
		t.setPreferredSize(new Dimension(800,600));
		setContentPane(t);
		
		setVisible(true);
		SwingUtilities.updateComponentTreeUI(this);
		
		if(old==null)
			t.engine.startengine();
	}
	
	
	/*Creates a fullscreen window from an old window.*/
	public static GameWindow enterFullScreen(GameWindow win, boolean useSounds, boolean useAI, boolean useGenetic)
	{
		win = new GameWindow(true, win, useSounds, useAI, useGenetic);
		try{
			win.dev.setFullScreenWindow(win);
			//800x600 fullscreen?
			win.dev.setDisplayMode(new DisplayMode
					(800,600,32,DisplayMode.REFRESH_RATE_UNKNOWN));
		
		}catch(Throwable t)
		{
			win.dev.setFullScreenWindow(null);
			throw new RuntimeException("Failed fullscreen");
		}
		return win;
	}
	
	
	/*Creates a windowed window (lol?) from an old window.*/
	public static GameWindow exitFullScreen(GameWindow win, boolean useSounds, boolean useAI, boolean useGenetic)
	{
		if(win.dev != null)
			win.dev.setFullScreenWindow(null);
		win = new GameWindow(false, win, useSounds, useAI, useGenetic);
		return win;
	}
}