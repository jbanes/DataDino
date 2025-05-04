/*
 * MouseWheelAjustmentListener.java
 *
 * Created on January 2, 2003, 9:37 PM
 */

package com.datadino.sqlclient.syntax;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class MouseWheelAjustmentListener 
{
    private JScrollBar source;
    private JComponent comp;
    
    private ArrayList listeners = new ArrayList();
    
    public MouseWheelAjustmentListener(JComponent comp, JScrollBar source) 
    {
        this.comp = comp;
        this.source = source;
        
        comp.addMouseWheelListener(new MouseWheelListener()
        {
            public void mouseWheelMoved(MouseWheelEvent e) 
            {
                wheelMoved(e.getUnitsToScroll());
            }
        });
    }
    
    public void addAdjustmentListener(AdjustmentListener listener)
    {
        listeners.add(listener);
    }

    public void removeAdjustmentListener(AdjustmentListener listener)
    {
        listeners.remove(listener);
    }
    
    private void wheelMoved(int amount)
    {
        AdjustmentListener listener;
        int direction = ((amount > 0) ? AdjustmentEvent.BLOCK_INCREMENT : AdjustmentEvent.BLOCK_DECREMENT);
        
        source.setValue(source.getValue()+amount);
        
        for(int i=0; i<listeners.size(); i++)
        {
            listener = (AdjustmentListener)listeners.get(i);
            
            listener.adjustmentValueChanged(new AdjustmentEvent(source, (int)AdjustmentEvent.ADJUSTMENT_EVENT_MASK, direction, Math.abs(amount)));
        }
    }
}
