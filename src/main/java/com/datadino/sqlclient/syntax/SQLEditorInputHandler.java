/*
 * SQLEditorInputHandler.java
 *
 * Created on January 7, 2003, 11:10 PM
 */

package com.datadino.sqlclient.syntax;

import java.awt.*;
import java.awt.event.*;

import com.datadino.sqlclient.tools.*;

import com.dnsalias.java.sqlclient.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class SQLEditorInputHandler extends DefaultInputHandler
{
    public ActionListener SAVE = new save();
    public ActionListener RUN = new run();
    public ActionListener NEW = new newdocument();
    public ActionListener OPEN = new open();
    
    private AdvancedSQLEditor editor;
    
    public SQLEditorInputHandler(AdvancedSQLEditor editor) 
    {
        this.editor = editor;
    }

    public void addDefaultKeyBindings()
    {
        String meta = "C";

        if(ApplicationSettings.getInstance().isMacOSX()) meta = "M";
            
        super.addDefaultKeyBindings();
        
        addKeyBinding(meta+"+S", SAVE);
        addKeyBinding(meta+"+R", RUN);
        addKeyBinding(meta+"+N", NEW);
        addKeyBinding(meta+"+O", OPEN);
    }
    
    public class save implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            editor.save();
        }
    }
    
    public class run implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            editor.execute();
        }
    }
    
    public class newdocument implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            editor.newDocument();
        }
    }
    
    public class open implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            editor.open();
        }
    }
}
