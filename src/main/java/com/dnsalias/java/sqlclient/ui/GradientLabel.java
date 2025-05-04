/*
 * GradientLabel.java
 *
 * Created on September 11, 2002, 1:27 AM
 */

package com.dnsalias.java.sqlclient.ui;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class GradientLabel extends JLabel
{
    private GradientPaint gradient;
    
    public GradientLabel() 
    {
        super();
    }
    
    public GradientLabel(String text)
    {
        super(text);
        setFont(new Font("Helvetica", Font.PLAIN, 16));
        setForeground(Color.yellow);
    }
    
    public GradientLabel(Icon icon)
    {
        super(icon);
    }
    
    public GradientLabel(String text, Icon icon, int horizontalAlignment)
    {
        super(text, icon, horizontalAlignment);
    }
    
    public void paint(Graphics g)
    {
        Graphics2D g2D = (Graphics2D)g;
        Font font = getFont();
        
        gradient = new GradientPaint(0, 0, Color.gray, getWidth(), getHeight(), new Color((float)1.0, (float)1.0, (float)1.0, (float)1.0));
        g2D.setFont(font);
        
        g2D.setPaint(gradient);
        g2D.fill(new Rectangle(getWidth(), getHeight()));
        
        g2D.setPaint(null);
        
        g2D.setColor(Color.black);
        g2D.drawString(getText(), 6, getFontMetrics(font).getAscent()+1);
        
        g2D.setColor(getForeground());
        g2D.drawString(getText(), 5, getFontMetrics(font).getAscent());
    }
}
