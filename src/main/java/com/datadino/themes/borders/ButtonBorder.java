/*
 * ButtonBorder.java
 *
 * Created on May 9, 2004, 9:13 PM
 */

package com.datadino.themes.borders;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

/**
 *
 * @author  jbanes
 */
public class ButtonBorder implements Border, UIResource
{
    private static Insets insets = new Insets(7, 7, 7, 7);
    private Color shadow = new Color(128, 128, 128);
    
    
    /** Creates a new instance of ButtonBorder */
    public ButtonBorder()
    {
    }
    
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
        if(component instanceof AbstractButton && ((JButton)component).isSelected())
        {
            g.setColor(shadow);
            g.drawRect(x+1, y+1, width-2, height-2);
            g.setColor(Color.black);
            g.drawRect(x, y, width, height);
        }
        else
        {
            g.setColor(shadow);
            g.drawRect(x, y, width-2, height-2);
            g.setColor(Color.white);
            g.drawRect(x, y, width-1, height-1);
            g.setColor(Color.black);
            g.drawLine(x+width-1, y, x+width-1, y+height-1);
            g.drawLine(x, y+height-1, x+width-1, y+height-1);
        }
    }
    
}
