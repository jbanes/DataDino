/*
 * DatabaseInterfacePanel.java
 *
 * Created on September 17, 2002, 8:35 PM
 */

package com.dnsalias.java.sqlclient.gdbi.panels;

import javax.swing.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public interface DatabaseInterfacePanel 
{
    public void activate();
    
    public void deactivate();
    
    public boolean isSaved();
    
    public void saveChanges();
    
    public JComponent getPanel();
}

