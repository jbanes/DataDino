/*
 * DataDinoMenuUI.java
 *
 * Created on March 5, 2003, 9:11 PM
 */

package com.datadino.themes;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class DataDinoMenuBarUI extends BasicMenuBarUI
{   
    public static ComponentUI createUI(JComponent c) 
    {
        return new DataDinoMenuBarUI();
    }
    
    public void installUI(JComponent c)
    {
        super.installUI(c);
        
        c.setBorder(new MenuBorder());
    }
    
    private class MenuBorder implements Border
    {
        private Insets insets = new Insets(2, 2, 1, 2);
        
        public Insets getBorderInsets(Component component)
        {
            return insets;
        }
        
        public boolean isBorderOpaque()
        {
            return true;
        }
        
        public void paintBorder(Component component, Graphics g, int x, int y, int width, int height)
        {
            g.setColor(Color.white);
            g.drawLine(x+1, y+1, width+x+1, y+1);
            g.drawLine(x+1, y+1, x+1, y+height+1);
            g.drawLine(x+width-1, y, x+width-1, y+height);
            
            g.setColor(Color.gray);
            g.drawLine(x, y, width+x-2, y);
            g.drawLine(x, y, x, y+height);
            g.drawLine(x+width-2, y+height, x+width-2, y+height);
            g.drawLine(x+width-2, y+1, x+width-2, y+height);
        }
        
    }
}
