/*
 * HighlightedTextArea.java
 *
 * Created on December 4, 2002, 10:34 PM
 */

package com.datadino.sqlclient.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.dnsalias.java.sqlclient.ui.*;

import com.datadino.sqlclient.syntax.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class HighlightedTextArea extends JPanel
{
    private static String[] keywords = {
        "select", "update", "insert", "delete", "from", "where", "order", "by", "limit", "as",
        "exec", "call", "create", "case", "when", "then", "end", "else", "like", "table", "database",
        "drop", "user", "view", "sequence", "index", "into", "asc", "desc"
    };
    
    private static final String[] buttonIcons = {
        "/toolbarButtonGraphics/media/Play16.gif",
        null,
        "/toolbarButtonGraphics/general/Cut16.gif",
        "/toolbarButtonGraphics/general/Copy16.gif",
        "/toolbarButtonGraphics/general/Paste16.gif"
    };
    
    private static final String[] buttonActions = {
        "Run",
        null,
        "Cut",
        "Copy",
        "Paste"
    };
    
    private MultiplexedToolbar toolbar = new MultiplexedToolbar();
    
    private ActionHandler handler = new ActionHandler();
    
    private TextAreaDefaults defaults = TextAreaDefaults.getDefaults();
    private JEditTextArea area;
    private KeywordMap map = new KeywordMap(true, keywords.length);
    
    
    public HighlightedTextArea() 
    {
        setLayout(new BorderLayout());
        
        initKeywords();
        initStyles();
        initButtons();
        
        area = new JEditTextArea(defaults);
        area.setTokenMarker(new SQLTokenMarker(map));
        area.getPainter().setFont(new Font("Courier New", 0, 12));
        area.setBorder(new EtchedBorder());
        
        add(toolbar.getToolbar(), BorderLayout.NORTH);
        add(area, BorderLayout.CENTER);
        
        area.requestFocus();
    }

    private void initButtons()
    {
        JButton button;
        ImageIcon icon;
        
        for(int i=0; i<buttonIcons.length; i++)
        {
            if(buttonIcons[i] == null) 
            {
                toolbar.addSeparator();
            }
            else
            {
                icon = new ImageIcon(getClass().getResource(buttonIcons[i]));
                button = new JButton(icon);
                
                button.setActionCommand(buttonActions[i]);
                button.addActionListener(handler);
                button.setToolTipText(buttonActions[i]);
                
                toolbar.add(button);
            }
        }
    }
    
    private void initKeywords()
    {   
        for(int i=0; i<keywords.length; i++) map.add(keywords[i], Token.KEYWORD1);
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
        
        defaults.styles[Token.KEYWORD1] = new SyntaxStyle(Color.blue, false, true);
        defaults.styles[Token.COMMENT1] = new SyntaxStyle(new Color(0x06A548), true, false);
        defaults.styles[Token.INVALID] = new SyntaxStyle(Color.red, false, false);
        defaults.styles[Token.LITERAL1] = new SyntaxStyle(Color.magenta, false, false);
    }
    
    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            if(evt.getActionCommand().equals("Cut")) area.cut();
            if(evt.getActionCommand().equals("Copy")) area.copy();
            if(evt.getActionCommand().equals("Paste")) area.paste();
        }
    }
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        HighlightedTextArea area = new HighlightedTextArea();
        
        frame.getContentPane().add(area);
        frame.setSize(500, 300);
        frame.setVisible(true);
    }
}