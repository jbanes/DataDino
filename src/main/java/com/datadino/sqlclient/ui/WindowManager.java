/*
 * WindowManager.java
 *
 * Created on August 16, 2003, 6:06 PM
 */

package com.datadino.sqlclient.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import com.dnsalias.java.sqlclient.util.*;

/**
 *
 * @author  jbanes
 */
public class WindowManager implements WindowListener, WindowStateListener, ComponentListener, Runnable, java.io.Serializable
{    
    private transient ArrayList windows = new ArrayList();
    private transient ArrayList titles = new ArrayList();
    private transient ArrayList identifiers = new ArrayList();
    
    private Hashtable options;
    
    private boolean running = false;
    private long targettime = 0;
    
    /** Creates a new instance of WindowManager */
    public WindowManager() 
    {
        options = (Hashtable)ApplicationSettings.getInstance().loadPersistentObject("WindowOptions");
        
        if(options == null) options = new Hashtable();
    }
    
    public void register(Frame frame, String identifier)
    {
        WindowOptions winopt;
        
        if(!windows.contains(frame))
        {
            windows.add(frame);
            identifiers.add(identifier);
            titles.add(frame.getTitle());
        }
        
        if(options.containsKey(identifier))
        {
            winopt = (WindowOptions)options.get(identifier);
            
            frame.setBounds(winopt.bounds);
            frame.setState(winopt.state);
        }
        
        frame.addWindowListener(this);
        frame.addWindowStateListener(this);
        frame.addComponentListener(this);
    }
    
    public synchronized void saveOptions(Frame source)
    {
        WindowOptions winopt;
        String identifier = (String)identifiers.get(windows.indexOf(source));
        
        winopt = (WindowOptions)options.get(identifier);
        
        if(winopt == null) 
        {
            winopt = new WindowOptions(source.getBounds(), source.getState());
        }
        else
        {
            winopt.bounds = source.getBounds();
            winopt.state = source.getState();
        }
        
        options.put(identifier, winopt);
        System.out.println(identifier+":"+winopt.bounds);
        
        if(!running) ThreadPool.run(this);
    }
    
    public void windowActivated(WindowEvent windowEvent) 
    {
    }
    
    public void windowClosed(WindowEvent windowEvent) 
    {
    }
    
    public void windowClosing(WindowEvent windowEvent) 
    {
        Frame source = (Frame)windowEvent.getSource();
        int index = windows.indexOf(source);
        
        System.out.println("Closing!");
        
        saveOptions(source);
        
        windows.remove(index);
        identifiers.remove(index);
        titles.remove(index);
    }
    
    public void windowDeactivated(WindowEvent windowEvent) 
    {
    }
    
    public void windowDeiconified(WindowEvent windowEvent) 
    {
    }
    
    public void windowIconified(WindowEvent windowEvent) 
    {
    }
    
    public void windowOpened(WindowEvent windowEvent) 
    {
    }
    
    public void windowStateChanged(WindowEvent windowEvent) 
    {
        Frame source = (Frame)windowEvent.getSource();
        
        saveOptions(source);
    }
    
    public void componentHidden(ComponentEvent componentEvent) 
    {
    }
    
    public void componentMoved(ComponentEvent componentEvent) 
    {
        Frame source = (Frame)componentEvent.getSource();
        
        saveOptions(source);
    }
    
    public void componentResized(ComponentEvent componentEvent) 
    {
        Frame source = (Frame)componentEvent.getSource();
        
        saveOptions(source);
    }
    
    public void componentShown(ComponentEvent componentEvent) 
    {
    }
    
    public void run() 
    {
        while(System.currentTimeMillis() < targettime)
        {
            try{Thread.sleep(100);}catch(Exception e) {}
        }
        
        ApplicationSettings.getInstance().savePersistentObject("WindowOptions", options);
        
        running = false;
    }
    
    private class WindowOptions implements java.io.Serializable
    {
        public Rectangle bounds;
        public int state;
        
        public WindowOptions(Rectangle bounds, int state)
        {
            this.bounds = bounds;
            this.state = state;
        }
    }
}
