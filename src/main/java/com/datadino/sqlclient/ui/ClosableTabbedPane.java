/*
 * ClosableTabbedPane.java
 *
 * Created on December 23, 2002, 7:59 PM
 */

package com.datadino.sqlclient.ui;

import java.awt.*;

import javax.swing.*;


/**
 *
 * @author  jbanes
 * @version 
 */
public class ClosableTabbedPane extends JTabbedPane
{

    private static ImageIcon closeIcon = new ImageIcon(ClosableTabbedPane.class.getResource("/images/small-close-icon.png"));

    public ClosableTabbedPane() 
    {
    }

    private Rectangle getCloseButtonBoundsAt(int i) 
    {
        Rectangle bounds = getBoundsAt(i);
        Dimension tabPanelSize = getSize();
        
        if (bounds == null) return null;
        else 
        {
            bounds = new Rectangle(bounds);
            
            if(bounds.x + bounds.width >= tabPanelSize.width || bounds.y + bounds.height >= tabPanelSize.height) return null;

            bounds.x = bounds.x + bounds.width - 13;
            bounds.y = bounds.y + bounds.height/2 - 5;
            bounds.width = 12;
            bounds.height = 12;
            
            return bounds;
        }
    }
    
    public void paint(Graphics g)
    {
        super.paint(g);
     
        
    }
}
