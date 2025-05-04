/*
 * DataDinoButtonUI.java
 *
 * Created on May 9, 2004, 9:06 PM
 */

package com.datadino.themes;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

import com.datadino.themes.borders.*;


/**
 *
 * @author  jbanes
 */
public class DataDinoButtonUI extends BasicButtonUI
{
    private final static DataDinoButtonUI ui = new DataDinoButtonUI();
    
    
    /** Creates a new instance of DataDinoButtonUI */
    public DataDinoButtonUI()
    {
    }
    
    public static ComponentUI createUI(JComponent comp)
    {
        return ui;
    }
    
    /*public void installUI(JComponent c)
    {
        System.out.println(c.getBorder());
        c.setBorder(null);
        super.installUI(c);
        //c.setBorder(new ButtonBorder());
    }*/
    
    protected void installDefaults(AbstractButton b)
    {
        super.installDefaults(b);
        
        System.out.println(b.getBorder());
        System.out.println(UIManager.getBorder("Button.border"));
    }
    
    public void paint(Graphics g, JComponent c)
    {
       ((AbstractButton)c).setSelected(false);
        super.paint(g, c);
    }
    
    public void paintButtonPressed(Graphics g, AbstractButton b)
    {
        b.setSelected(true);
        super.paintButtonPressed(g, b);
    }
}
