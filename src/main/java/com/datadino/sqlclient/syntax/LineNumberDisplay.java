package com.datadino.sqlclient.syntax;

import java.awt.*;

import javax.swing.*;

public class LineNumberDisplay extends JComponent implements PainterListener
{
    private JEditTextArea area;
    private Color bg = new Color(230, 230, 230);
    private Color fg = new Color(100, 0, 0);
    
    public LineNumberDisplay(JEditTextArea area)
    {
        this.area = area;
        
        setFont(new Font("Courier New", 0, 14));
        setBackground(bg);
    }
    
    public void paint(Graphics g)
    {
        FontMetrics metrics = getFontMetrics(getFont());
        
        int x = 0;
        int y = 0;
        int first = area.getFirstLine();
        int count = Math.min(area.getVisibleLines(), area.getLineCount()-first);
        int charWidth = metrics.charWidth('0');
        int chars = Integer.toString(area.getLineCount()).length();
        
        String text;
        
        count = Math.max(count, 1);
        
        g.setColor(bg);
        g.fillRect(0, 0, getWidth()-2, getHeight());
        g.setColor(Color.white);
        g.fillRect(getWidth()-2, 0, 2, getHeight());
        g.setColor(fg);
        
        for(int i=0; i<count+1; i++)
        {
            text = Integer.toString(i+first);
            
            x = (chars-text.length())*charWidth+2;
            y = area.lineToY(i+first);
            
            g.drawString(text, x, y);
        }
    }
    
    public Dimension getPreferredSize()
    {
        FontMetrics metrics = getFontMetrics(getFont());
        int chars = Integer.toString(area.getLineCount()).length();
        
        return new Dimension(metrics.charWidth('0')*chars+6, 0);
    }
    
    public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }
    
    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }
    
    public void invalidateLineRange(int firstLine, int lastLine)
    {
        FontMetrics metrics = getFontMetrics(getFont());
        
        int charWidth = metrics.charWidth('0');
        int chars = Integer.toString(area.getLineCount()).length();
        
        if(chars*charWidth+6 != getWidth())
        {
            area.revalidate();
            return;
        }
        
        repaint();
    }
}