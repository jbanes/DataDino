/*
 * JSlideBar.java
 *
 * Created on November 11, 2002, 10:13 PM
 */

package com.datadino.sqlclient.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.dnsalias.java.sqlclient.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class JSlideBar extends JPanel
{
    private ArrayList tabs = new ArrayList();
    private ArrayList tabNames = new ArrayList();
    private ArrayList tabButtons = new ArrayList();
    
    private ArrayList listeners = new ArrayList();
    private SlideTabListener listener = new SlideTabListener();
    
    private int selected = -1;
    private boolean macosx = ApplicationSettings.getInstance().isMacOSX();
    
    private Icon downArrow;
    private Icon rightArrow;
    
    public JSlideBar()
    {
        setLayout(null);
        setBorder(new EtchedBorder());
        
        downArrow = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icons/down-arrow.png")));
        rightArrow = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icons/right-arrow.png")));
    
        while(downArrow.getIconWidth() < 0) Thread.yield();
        while(rightArrow.getIconWidth() < 0) Thread.yield();
    }
    
    public void doLayout()
    {
        int height = getHeight();
        int compheight = 0;
        
        JComponent comp;
        Dimension dim = null;
        Insets insets;
        
        for(int i=tabs.size()-1; i > selected; i--)
        {
            comp = (JComponent)tabButtons.get(i);
            dim = comp.getMinimumSize();
            insets = comp.getInsets();
            
            if(!macosx) dim.height = Math.min(10, dim.height);
            
            dim.height += insets.top + insets.bottom;
            
            comp.setBounds(0, height-dim.height, getWidth(), dim.height);
            
            height -= dim.height;
        }
        
        compheight = height;
        height = 0;
        
        for(int i=0; i <= selected; i++)
        {
            comp = (JComponent)tabButtons.get(i);
            dim = comp.getMinimumSize();
            insets = comp.getInsets();
            
            if(!macosx) dim.height = Math.min(10, dim.height);
            
            dim.height += insets.top + insets.bottom;
            
            comp.setBounds(0, height, getWidth(), dim.height);
            
            if(i != selected)
            {
                ((Component)tabs.get(i)).setVisible(false);
                ((Component)tabs.get(i)).setBounds(0, 0, 0, 0);
            }
            
            height += dim.height;
        }
        
        if(selected >= 0)
        {
            insets = getInsets();
            ((Component)tabs.get(selected)).setVisible(true);
            ((Component)tabs.get(selected)).setBounds(insets.left, height, getWidth()-(insets.left+insets.right), compheight-height);
        }
        
        repaint();
    }

    public void addTab(String title, Component component)
    {
        JButton button = new JButton(title);
        
        button.setHorizontalAlignment(JButton.LEFT);
        button.putClientProperty("JButton.buttonType", "toolbar");
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 10));
        button.addActionListener(listener);
        
        tabNames.add(title);
        tabs.add(component);
        tabButtons.add(button);
        
        super.add(button);
        super.add(component);
        
        button.updateUI();
        
        if(selected < 0) 
        {
            selected = 0;
            button.setIcon(downArrow);
        }
        else 
        {
            button.setIcon(rightArrow);
            component.setVisible(false);
        }
    }
    
    public void addChangeListener(ChangeListener l)
    {
        listeners.add(l);
    }
    
    public Component getSelectedComponent()
    {
        return (Component)tabs.get(selected);
    }
    
    public int getSelectedIndex()
    {
        return selected;
    }
    
    public int getTabCount()
    {
        return tabs.size();
    }
    
    public String getTitleAt(int index)
    {
        return tabNames.get(index).toString();
    }
    
    public int indexOfComponent(Component component)
    {
        return tabs.indexOf(component);
    }
    
    public int indexOfTab(String title)
    {
        return tabNames.indexOf(title);
    }
    
    public void remove(int index)
    {
        super.remove((Component)tabs.get(index));
        super.remove((Component)tabButtons.get(index));
        
        tabs.remove(index);
        tabNames.remove(index);
        tabButtons.remove(index);
        
        if(tabs.size() < 1) selected = -1;
    }
    
    public void removeAll()
    {
        super.removeAll();
        
        tabs.clear();
        tabNames.clear();
        tabButtons.clear();
    }
    
    public void removeChangeListener(ChangeListener l) 
    {
        listeners.remove(l);
    }
    
    public void removeTabAt(int index) 
    {
        remove(index);
    }
    
    public void setSelectedComponent(Component c) 
    {
        setSelectedIndex(indexOfComponent(c));
    }
    
    public void setSelectedIndex(int index) 
    {
        ChangeEvent event = new ChangeEvent(this);
        JButton button;
        
        if(index >= tabs.size() || index < 0) throw new IllegalArgumentException("Tab "+index+" does not exist!");
        
        button = (JButton)tabButtons.get(selected);
        button.setIcon(rightArrow);
        button = (JButton)tabButtons.get(index);
        button.setIcon(downArrow);
        
        ((JComponent)tabs.get(selected)).setVisible(false);
        
        selected = index;
        
        ((JComponent)tabs.get(selected)).setVisible(true);
        
        for(int i=0; i<listeners.size(); i++)
        {
            ((ChangeListener)listeners.get(i)).stateChanged(event);
        }
    }
    
    private class SlideTabListener implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            int index = tabButtons.indexOf(evt.getSource());
            
            setSelectedIndex(index);
            revalidate();
        }
    }
    
    public static void main(String args[])
    {
        JFrame frame = new JFrame();
        JSlideBar bar = new JSlideBar();
        
        bar.addTab("Test 1", new JList());
        bar.addTab("Tables", new JList());
        bar.addTab("System Tables", new JList());
        bar.addTab("Views", new JList());
        
        frame.getContentPane().add(bar);
        frame.setBounds(300, 300, 200, 300);
        frame.setVisible(true);
    }
}
