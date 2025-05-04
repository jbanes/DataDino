/*
 * SourcePanel.java
 *
 * Created on August 14, 2003, 10:17 PM
 */

package com.dnsalias.java.sqlclient.gdbi.panels;

import java.awt.*;
import java.sql.*;

import javax.swing.*;

import com.dnsalias.java.sqlclient.*;
import com.dnsalias.java.sqlclient.gdbi.*;

import com.datadino.sqlclient.source.*;
import com.datadino.sqlclient.syntax.*;
import com.datadino.sqlclient.tools.*;
import com.datadino.sqlclient.ui.*;
/**
 *
 * @author  jbanes
 */
public class SourcePanel extends JPanel implements DatabaseInterfacePanel
{
    private DBObject dbo;
    private SQLClientHandler handler;
    private String sourcecode = null;
    
    private KeywordMap map = new KeywordMap(true, AdvancedSQLEditor.keywords.length);
    private TextAreaDefaults defaults = TextAreaDefaults.getDefaults();
    private JEditTextArea textarea;
    
    private Icon rotate = new ImageIcon(getClass().getResource("/images/rotate.png"));
    
    /** Creates a new instance of SourcePanel */
    public SourcePanel(DBObject dbo) 
    {
        this.dbo = dbo;
        this.handler = SQLClientHandler.getCurrentHandler();
     
        initKeywords();
        initStyles();
        
        textarea = new JEditTextArea(defaults);
        
        textarea.setTokenMarker(new SQLTokenMarker(map));
        textarea.getPainter().setFont(new Font("Monospaced", 0, 14));
        
        textarea.setEditable(false);
        
        this.setLayout(new BorderLayout(2, 2));
        this.add(textarea);;
    }
    
    private void initKeywords()
    {   
        String[] keywords = AdvancedSQLEditor.keywords;
        SQLClientHandler handler = this.handler.getConnection();
        
        try
        {
            keywords = AdvancedSQLEditor.getKeywords(handler.getMetaData().getSQLKeywords());
            
            handler.completeOperation();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        
        for(int i=0; i<keywords.length; i++)  map.add(keywords[i], Token.KEYWORD1);
    }
    
    private void initStyles()
    {
        defaults.caretBlinks = true;
        defaults.caretColor = Color.black;
        defaults.eolMarkers = false;
        defaults.paintInvalid = false;
        defaults.bracketHighlightColor = Color.magenta;
        defaults.lineHighlight = true;
        defaults.lineHighlightColor = new Color(0xEEEEEE);
        defaults.selectionColor = Color.lightGray;
        defaults.cols = 1;
        defaults.document = new SyntaxDocument();
        defaults.electricScroll = 1;
        
        defaults.styles[Token.KEYWORD1] = new SyntaxStyle(Color.blue, false, true);
        defaults.styles[Token.COMMENT1] = new SyntaxStyle(new Color(0x06A548), true, false);
        defaults.styles[Token.INVALID] = new SyntaxStyle(Color.red, false, false);
        defaults.styles[Token.LITERAL1] = new SyntaxStyle(Color.magenta, false, false);
    }
    
    public void activate() 
    {
        SQLClientHandler handler = this.handler.getConnection();
        ObjectSource source = new ObjectSource(handler);
        
        
        JStatusBar.setText(this, "Loading...", rotate, 0);
        JStatusBar.setText(this, "Generating SQL Source...", 2);
        
        try
        {
            sourcecode = source.getSource(dbo);
            
            handler.completeOperation();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        
        if(sourcecode == null) textarea.setText("/* Source code unavailable */");
        else textarea.setText(sourcecode);
        
        textarea.scrollTo(0, 0);
        textarea.revalidate();
        
        
        JStatusBar.setText(this, "Ready", 0);
        JStatusBar.setText(this, "SQL Source Generated.", 2);
    }

    public void deactivate() 
    {
    }

    public JComponent getPanel() 
    {
        return this;
    }

    public boolean isSaved() 
    {
        return true;
    }

    public void saveChanges() 
    {
    }
}

