/*
 * StatusBar.java
 *
 * Created on November 19, 2002, 9:21 PM
 */

package com.datadino.sqlclient.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class JStatusBar extends JPanel
{
    private JLabel[] labels;
    private int[] sizes;
    private int height = 0;
    private Border border = new CompoundBorder(new EmptyBorder(2, 2, 0, 0), new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED), new EmptyBorder(0, 2, 2, 1)));
    
    public JStatusBar()
    {
        this(1, new int[]{0});
    }
    
    public JStatusBar(int fields, int[] sizes)
    {
        Dimension dim;
        Insets insets;
        
        this.labels = new JLabel[fields];
        this.sizes = sizes;
        
        //this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        //this.setBorder(new EmptyBorder(2, 1, 2, 1));
        
        for(int i=0; i<labels.length; i++)
        {
            labels[i] = new JLabel(" ", JLabel.LEFT);
            
            dim = labels[i].getMinimumSize();
            insets = labels[i].getInsets();
            
            labels[i].setFont(labels[i].getFont().deriveFont(Font.PLAIN, 11));
            labels[i].setBorder(border);
            labels[i].setVerticalAlignment(JLabel.CENTER);
            
            if(dim.height+insets.top+insets.bottom > height) this.height = dim.height+insets.top+insets.bottom;
           
            add(labels[i]);
        }
    }
    
    public void doLayout()
    {
        Insets insets = getInsets();
        Insets linsets;
        
        int[] sizes = new int[this.sizes.length];
        int unspecified = 0;
        int width = getWidth()-insets.left-insets.right;
        int total = 0;
        int pos = insets.left;
        
        for(int i=0; i<sizes.length; i++)
        {
            sizes[i] = this.sizes[i];
            
            if(sizes[i] < 1) unspecified++;
            else total += sizes[i];
        }
        
        for(int i=0; i<sizes.length; i++)
        {
            if(sizes[i] < 1) sizes[i] = Math.min(width-total, (int)(width/unspecified));
        }
        
        for(int i=0; i<sizes.length; i++)
        {
            labels[i].setBounds(pos, insets.top, sizes[i], getHeight()-insets.top-insets.bottom);
            pos += sizes[i];
        }
    }
    
    public int getAlignment(int field)
    {
        return labels[field].getHorizontalAlignment();
    }
    
    public void setAlignment(int align, int field)
    {
        labels[field].setHorizontalAlignment(align);
    }
    
    public void setText(String text, int field)
    {
        setText(text, null, field);
    }
    
    public void setText(String text, Icon icon, int field)
    {
        Insets insets = labels[field].getInsets();
        //Graphics g = labels[field].getGraphics();
        
        labels[field].setText(text);
        labels[field].setIcon(icon);
        
        /*if(g != null)
        {
            g.clearRect(insets.left, insets.top, labels[field].getWidth()-insets.left-insets.right, labels[field].getHeight()-insets.top-insets.bottom);
            labels[field].paintAll(g);
        }*/
        
        repaint();
    }
    
    public static void setText(Component comp, String text, int field)
    {
        setText(comp, text, null, field);
    }
    
    public static void setText(Component comp, String text, Icon icon, int field)
    {
        Component parent = comp;
        
        while(comp.getParent() != null)
        {
            if(comp instanceof StatusBarOwner)
            {
                ((StatusBarOwner)comp).getStatusBar().setText(text, icon, field);
                return;
            }
            else
            {
                comp = comp.getParent();
            }
        }
    }
    
    public static String getText(Component comp, int field)
    {
        Component parent = comp;
        
        while(comp.getParent() != null)
        {
            if(comp instanceof StatusBarOwner)
            {
                return ((StatusBarOwner)comp).getStatusBar().getText(field);
            }
            else
            {
                comp = comp.getParent();
            }
        }
        
        return null;
    }
    
    public static Icon getIcon(Component comp, int field)
    {
        Component parent = comp;
        
        while(comp.getParent() != null)
        {
            if(comp instanceof StatusBarOwner)
            {
                return ((StatusBarOwner)comp).getStatusBar().getIcon(field);
            }
            else
            {
                comp = comp.getParent();
            }
        }
        
        return null;
    }
    
    public String getText(int field)
    {
        return labels[field].getText();
    }
    
    public Icon getIcon(int field)
    {
        return labels[field].getIcon();
    }
    
    public Dimension getPreferredSize()
    {
        int heght = 0;
        
        for(int i=0; i<labels.length; i++)
        {
            if(labels[i].getPreferredSize().height > height) height = labels[i].getPreferredSize().height;
        }
        
        return new Dimension(0, height);
    }
}
