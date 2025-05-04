/*
 * DataDinoFrame.java
 *
 * Created on September 25, 2003, 8:52 AM
 */

package com.datadino.sqlclient.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author  jbanes
 */
public class DataDinoInternalFrame extends JInternalFrame
{
    private ChangeListener listener;
    
    /** Creates a new instance of DataDinoFrame */
    public DataDinoInternalFrame()
    {
    }
    
    public DataDinoInternalFrame(String title)
    {
        setTitle(title);
    }
    
    public ChangeListener getTitleListener()
    {
        return listener;
    }
    
    public void setTitleListener(ChangeListener listener)
    {
        this.listener = listener;
    }
    
    public void setTitle(String text)
    {
        super.setTitle(text);
        
        if(listener != null) listener.stateChanged(new ChangeEvent(this));
    }
}
