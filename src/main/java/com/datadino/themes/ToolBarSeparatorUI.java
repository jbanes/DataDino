/*
 * ToolBarSeparatorUI.java
 *
 * Created on May 16, 2004, 10:35 PM
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
 */
public class ToolBarSeparatorUI extends BasicToolBarSeparatorUI
{
    private static ToolBarSeparatorUI ui = new ToolBarSeparatorUI();
    
    private MetalTheme theme = new DefaultMetalTheme();
    
    private Color lightShadow = theme.getControlShadow();
    private Color darkShadow = theme.getControlDarkShadow();
    
    /** Creates a new instance of ToolBarSeparatorUI */
    public ToolBarSeparatorUI()
    {
    }
    
    public static ComponentUI createUI(JComponent c)
    {
        return ui;
    }
    
    public void installUI(JComponent c)
    {
        c.setLayout(null);
    }
    
    public void paint(Graphics g, JComponent c)
    {
        Dimension dim = c.getSize();
        
        g.setColor(lightShadow);
        g.drawLine(1, 3, 1, dim.height-4);
        g.setColor(Color.white);
        g.drawLine(2, 3, 2, dim.height-4);
    }

    public Dimension getPreferredSize(JComponent c)
    {
        return new Dimension(5, 0);
    }

    public Dimension getMinimumSize(JComponent c)
    {
        return new Dimension(5, 24);
    }

    public Dimension getMaximumSize(JComponent c)
    {
        return new Dimension(5, 250000);
    }
}
