/*
 * VFSPanelListener.java
 *
 * Created on December 21, 2002, 11:13 AM
 */

package com.datadino.sqlclient.tools;

import com.datadino.sqlclient.vfs.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public interface VFSPanelListener 
{
    public void VFSFileDoubleClicked(VFSFile file);
    
    public void VFSPanelShown();
    
    public void VFSPanelHidden();
}

