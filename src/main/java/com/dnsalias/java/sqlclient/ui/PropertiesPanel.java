/*
 * PropertiesPanel.java
 *
 * Created on September 30, 2002, 4:45 PM
 */

package com.dnsalias.java.sqlclient.ui;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class PropertiesPanel extends JPanel
{
    public PropertiesPanel()
    {
        setOpaque(true);
    }
    
    public void display(PropertyList list)
    {
        JLabel[] label = new JLabel[list.getSize()];
        JTextArea[] text = new JTextArea[list.getSize()];
        
        int maxwidth = 0;
        int maxheight[] = new int[list.getSize()];
        
        int y = 0;
        
        removeAll();
        setLayout(null);
        revalidate();
        repaint();
        
        if(getParent() != null) list.setWidth(getParent().getWidth()-3);
           
        for(int i=0; i<list.getSize(); i++)
        {
            label[i] = new JLabel(list.getKey(i)+":   ", JLabel.RIGHT);
            text[i] = new AutomaticTextArea(list.getValue(i));
            
            label[i].setVerticalAlignment(JLabel.TOP);
            label[i].setFont(label[i].getFont().deriveFont(Font.BOLD));
            
            text[i].setEditable(false);
            text[i].setLineWrap(true);
            text[i].setWrapStyleWord(true);
            text[i].setOpaque(false);
            
            add(label[i]);
            add(text[i]);
        }
        
        for(int i=0; i<label.length; i++)
        {
            if(label[i].getPreferredSize().width > maxwidth) maxwidth = label[i].getPreferredSize().width;
        }
        
        for(int i=0; i<text.length; i++)
        {
            text[i].setColumns(list.width-maxwidth);
            text[i].setSize(list.width-maxwidth, 17);
            
            try
            {
                Rectangle rect = text[i].modelToView(text[i].getText().length());
                maxheight[i] = rect.y+rect.height;
            }
            catch(BadLocationException e)
            {
                e.printStackTrace();
                maxheight[i] = text[i].getPreferredSize().height;
            }            
        }
        
        for(int i=0; i<label.length; i++)
        {
            label[i].setBounds(3, y, maxwidth, maxheight[i]);
            text[i].setBounds(maxwidth+3, y, list.width-maxwidth, maxheight[i]);
            
            y += maxheight[i]+4;
        }
        
        setPreferredSize(new Dimension(list.width+3, y));
        
        revalidate();
    }
    
    public PropertyList createList()
    {
        return new PropertyList();
    }
    
    public class PropertyList
    {
        private ArrayList keys = new ArrayList();
        private ArrayList values = new ArrayList();
    
        String html = null;
        
        private int width = 0;
        
        
        public void addProperty(String key, String value)
        {
            keys.add(key);
            values.add(value);
        }
        
        public int getWidth()
        {
            return width;
        }
        
        public void setWidth(int width)
        {
            this.width = width;
        }
        
        public int getSize()
        {
            return keys.size();
        }
        
        public String getKey(int index)
        {
            return keys.get(index).toString();
        }
        
        public String getValue(int index)
        {
            return values.get(index).toString();
        }
    }
    
    private class AutomaticTextArea extends JTextArea
    {
        public int columnWidth = 1;
        
        public AutomaticTextArea()
        {
            super();
        }
        
        public AutomaticTextArea(String text)
        {
            super(text);
        }
        
        protected int getColumnWidth()
        {
            return columnWidth;
        }
        
        /*public Dimension getPreferredSize()
        {
            
        }*/
    }
}
