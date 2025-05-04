/*
 * WindowSettings.java
 *
 * Created on September 26, 2003, 10:07 PM
 */

package com.datadino.sqlclient.ui;

import java.awt.*;

/**
 *
 * @author  jbanes
 */
public class WindowSettings implements java.io.Serializable
{
    public Rectangle bounds;
    public boolean maximized;
        
    /** Creates a new instance of WindowSettings */
    public WindowSettings(Rectangle bounds, boolean maximized)
    {
        this.bounds = bounds;
        this.maximized = maximized;
    }
    
}
