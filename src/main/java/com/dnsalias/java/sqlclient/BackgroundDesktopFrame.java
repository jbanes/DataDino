/*
 * BackgroundDesktopFrame.java
 *
 * Created on May 7, 2002, 2:38 PM
 */

package com.dnsalias.java.sqlclient;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class BackgroundDesktopFrame extends JDesktopPane
{
    Image background = null;
    int tilewidth = 0;
    int tileheight = 0;
    
    public BackgroundDesktopFrame(Image background)
    {
        MediaTracker tracker = new MediaTracker(this);
        
        this.background = background;
        
        try
        {
            tracker.addImage(background, 0);
            tracker.waitForAll();
        }
        catch(InterruptedException e) {}
        
        this.tilewidth = background.getWidth(null);
        this.tileheight = background.getHeight(null);
        
        setAutoscrolls(true);
        setDesktopManager(new ScrollableDesktopManager());
        setDragMode(OUTLINE_DRAG_MODE);
        
        setPreferredSize(new Dimension(100, 100));
    }

    public void paintComponent(Graphics g)
    {
        int tilex = getWidth()/background.getWidth(null)+1;
        int tiley = getHeight()/background.getHeight(null)+1;
        
        for(int y=0; y<tiley; y++)
        {
            for(int x=0; x<tilex; x++)
            {
                g.drawImage(background, x*tilewidth, y*tileheight, null);
            }
        }
    }
    
    private void setPreferredSize()
    {
        JInternalFrame[] frames = getAllFrames();
        
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        
        for(int i=0; i<frames.length; i++)
        {
            if(frames[i].getX() < left) left = frames[i].getX();
            if(frames[i].getX()+frames[i].getWidth() > right) right = frames[i].getX()+frames[i].getWidth();
            if(frames[i].getY() < top) top = frames[i].getY();
            if(frames[i].getY()+frames[i].getHeight() > bottom) bottom = frames[i].getY()+frames[i].getHeight();
        }
        
        setPreferredSize(new Dimension((right-left), (bottom-top)));
        invalidate();
        revalidate();
    }
    
    private class ScrollableDesktopManager extends DefaultDesktopManager
    {
        Border border = null;
        Border desktopPaneBorder = null;
        
        public void endDraggingFrame(JComponent comp)
        {
            super.endDraggingFrame(comp);
            
            if(comp.getX() < 0)
            {
                JInternalFrame[] frames = getAllFrames();
                
                for(int i=0; i<frames.length; i++)
                {
                    if(frames[i] != comp) frames[i].setLocation(frames[i].getX()-comp.getX(), frames[i].getY());
                }
                
                comp.setLocation(0, comp.getY());
            }
            
            setPreferredSize();
        }
        
        public void endResizingFrame(JComponent comp)
        {
            super.endResizingFrame(comp);
            setPreferredSize();
        }
        
        public void maximizeFrame(JInternalFrame comp)
        {
            super.maximizeFrame(comp);
            
            border = comp.getBorder();
            desktopPaneBorder = getBorder();
            
            comp.setBorder(null);
            setBorder(null);
        }
        
        public void minimizeFrame(JInternalFrame comp)
        {
            super.minimizeFrame(comp);
            
            if(comp.getBorder() == null) comp.setBorder(border);
            if(getBorder() == null) setBorder(desktopPaneBorder);
        }
    }
}
