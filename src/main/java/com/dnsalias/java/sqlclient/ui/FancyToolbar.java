/*
 * FancyToolbar.java
 *
 * Created on October 31, 2002, 7:22 AM
 */

package com.dnsalias.java.sqlclient.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.metal.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class FancyToolbar extends JToolBar
{
    /*private MetalTheme theme = new DefaultMetalTheme();
    
    private Color highlight = theme.getControlHighlight();
    private Color lightShadow = theme.getControlShadow();
    private Color darkShadow = theme.getControlDarkShadow();
    
    private Border hoverBorder = new CompoundBorder(new MetalBorders.ButtonBorder(), new EmptyBorder(0, 0, 1, 1));
    private Border toggleBorder = new CompoundBorder(new MetalBorders.ToggleButtonBorder(), new EmptyBorder(0, 0, 1, 1));
    private Border emptyBorder = new EmptyBorder(3, 3, 4, 4);
    
    private HoverListener hover = new HoverListener();
    
    public FancyToolbar()
    {
        BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
        
        setOpaque(true);
        setBorder(new EtchedBorder());
        setLayout(layout);
        
        add(new Handle());
    }
    
    public Component add(Component comp)
    {
        JComponent jcomp;
        
        if(comp instanceof AbstractButton)
        {
            jcomp = (JComponent)comp;
            
            if(comp instanceof JToggleButton && ((JToggleButton)comp).isSelected()) 
            {
                jcomp.setBorder(toggleBorder);
            }
            else
            {
                jcomp.setBorder(emptyBorder);
            }
            
            jcomp.addMouseListener(hover);
            jcomp.setOpaque(false);
        }
        
        return super.add(comp);
    }
    
    public void paint(Graphics g)
    {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        
        super.paint(g);
    }
    
    public void addSeparator()
    {
        add(new Separator());
    }
    
    private class Separator extends JComponent
    {
        public Separator()
        {
            setLayout(null);
        }
        
        public void paintComponent(Graphics g)
        {
            Dimension dim = getSize();
            
            g.setColor(lightShadow);
            g.drawLine(1, 3, 1, dim.height-3);
            g.setColor(Color.white);
            g.drawLine(2, 3, 2, dim.height-3);
        }
        
        public Dimension getPreferredSize()
        {
            return new Dimension(4, 24);
        }
        
        public Dimension getMinimumSize()
        {
            return new Dimension(4, 24);
        }
        
        public Dimension getMaximumSize()
        {
            return new Dimension(4, 24);
        }
    }
    
    private class HoverListener extends MouseAdapter
    {
        public void mouseEntered(MouseEvent evt)
        {
            if(evt.getComponent() instanceof JToggleButton)
            {
                ((JComponent)evt.getComponent()).setBorder(toggleBorder);
            }
            else
            {
                ((JComponent)evt.getComponent()).setBorder(hoverBorder);
            }
        }
        
        public void mouseExited(MouseEvent evt)
        {
            if(evt.getComponent() instanceof JToggleButton)
            {
                if(!((JToggleButton)evt.getComponent()).isSelected()) ((JComponent)evt.getComponent()).setBorder(emptyBorder);
            }
            else
            {
                ((JComponent)evt.getComponent()).setBorder(emptyBorder);
            }
        }
    }
    
    private class Handle extends JPanel
    {
        public Handle()
        {
            setLayout(null);
        }
        
        public void paintComponent(Graphics g)
        {
            Dimension dim = getSize();
            
            g.setColor(darkShadow);
            g.drawLine(4, 3, 4, dim.height-2);
            g.drawLine(5, 3, 5, dim.height-2);
            
            g.drawLine(8, 3, 8, dim.height-2);
            g.drawLine(9, 3, 9, dim.height-2);
            
            g.setColor(highlight);
            g.drawLine(3, 2, 3, dim.height-3);
            g.drawLine(4, 2, 4, dim.height-3);
            
            g.drawLine(7, 2, 7, dim.height-3);
            g.drawLine(8, 2, 8, dim.height-3);
            
            //javax.swing.plaf.basic.BasicGraphicsUtils.drawEtchedRect(g, 2, 1, 4, dim.height-2, lightShadow, darkShadow, highlight, Color.white);
            //javax.swing.plaf.basic.BasicGraphicsUtils.drawEtchedRect(g, 6, 1, 4, dim.height-2, lightShadow, darkShadow, highlight, Color.white);
        }
        
        public Dimension getPreferredSize()
        {
            return new Dimension(12, 25);
        }
        
        public Dimension getMinimumSize()
        {
            return new Dimension(12, 25);
        }
        
        public Dimension getMaximumSize()
        {
            return new Dimension(12, 25);
        }
    }*/
}
