/*
 * DataDinoToolbarUI.java
 *
 * Created on May 6, 2004, 11:22 PM
 */

package com.datadino.themes;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

/**
 *
 * @author  jbanes
 */
public class DataDinoToolBarUI extends BasicToolBarUI
{
    private final static DataDinoToolBarUI ui = new DataDinoToolBarUI();
    private final static MouseInputAdapter fakeAdapter = new MouseInputAdapter() {};
    
    private MetalTheme theme = new DefaultMetalTheme();
    
    private Color highlight = theme.getControlHighlight();
    private Color lightShadow = theme.getControlShadow();
    private Color darkShadow = theme.getControlDarkShadow();
    
    private JComponent comp;
    
    /** Creates a new instance of DataDinoToolbarUI */
    public DataDinoToolBarUI()
    {
    }
    
    public static ComponentUI createUI(JComponent comp)
    {
        return ui;
    }
    
    public void installUI(JComponent c)
    {
        super.installUI(c);

        c.add(new Handle(), 0);
        c.setBorder(new EtchedBorder());
        setRolloverBorders(true);
    }
    
    protected MouseInputAdapter createDockingListener()
    {
        return fakeAdapter;
    }
    
    public synchronized void uninstallUI(JComponent c)
    {
        c.remove(0);
    }
    
    public void paint(Graphics g, JComponent c)
    {
        JComponent temp;
        
        for(int i=0; i<c.getComponentCount(); i++)
        {
            temp = (JComponent)c.getComponent(i);

            if(i == 1 && temp instanceof Handle)
            {
                c.remove(i);
                c.revalidate();
                i--;
            }
            
            if(temp.isOpaque()) temp.setOpaque(false);
        }
        
        super.paint(g, c);
    }
    
    private class Handle extends JPanel
    {
        public Handle()
        {
            //setLayout(null);
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
            return new Dimension(12, 0);
        }
        
        public Dimension getMinimumSize()
        {
            return new Dimension(12, 25);
        }
        
        public Dimension getMaximumSize()
        {
            return new Dimension(12, 250000);
        }
    }
}
