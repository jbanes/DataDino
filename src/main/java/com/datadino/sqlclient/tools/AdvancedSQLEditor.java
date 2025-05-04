/*
 * HighlightedTextArea.java
 *
 * Created on December 4, 2002, 10:34 PM
 */

package com.datadino.sqlclient.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.dnsalias.java.sqlclient.*;
import com.dnsalias.java.sqlclient.dialog.*;
import com.dnsalias.java.sqlclient.util.*;
import com.dnsalias.java.sqlclient.ui.*;

import com.datadino.sqlclient.syntax.*;
import com.datadino.sqlclient.vfs.*;
import com.datadino.sqlclient.ui.*;
import com.datadino.sqlclient.util.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class AdvancedSQLEditor extends DataDinoInternalFrame implements StatusBarOwner
{
    public static String[] keywords = {
        "select", "update", "insert", "delete", "from", "where", "order", "by", "limit", "as",
        "exec", "call", "create", "case", "when", "then", "end", "else", "like", "table", "database",
        "drop", "user", "view", "sequence", "index", "into", "asc", "desc", "set", "null", "not",
        "between", "and", "add", "alter", "default", "group", "primary", "key", "references",
        "values", "constraint", "on", "restrict", "no", "action", "foreign", "in", "procedure", "begin",
        "or", "distinct", "top", "join", "inner", "outer", "column", "unique", "using"
    };
    
    private static final String[] buttonIcons = {
        "/toolbarButtonGraphics/general/New16.gif",
        "/toolbarButtonGraphics/general/Open16.gif",
        "/toolbarButtonGraphics/general/Save16.gif",
        "/toolbarButtonGraphics/general/SaveAs16.gif",
        null,
        "/toolbarButtonGraphics/media/Play16.gif",
        "/toolbarButtonGraphics/media/Stop16.gif",
        null,
        "/toolbarButtonGraphics/general/Cut16.gif",
        "/toolbarButtonGraphics/general/Copy16.gif",
        "/toolbarButtonGraphics/general/Paste16.gif",
        null,
        "/toolbarButtonGraphics/general/Undo16.gif",
        "/toolbarButtonGraphics/general/Redo16.gif",
        null,
        "/images/icons/cpu.png",
        "/images/icons/table.png"
    };
    
    private static final String[] buttonActions = {
        "New",
        "Open",
        "Save",
        "Save As...",
        null,
        "Run",
        "Stop",
        null,
        "Cut",
        "Copy",
        "Paste",
        null,
        "Undo",
        "Redo",
        null,
        "Show Side Panel",
        "Show Results"
    };
    
    private static final String[] shortcuts = {
        "N",
        "O",
        "S",
        null,
        null,
        "R",
        null,
        null,
        "X",
        "C",
        "V",
        null,
        "Z",
        "Y",
        null,
        null,
        null
    };
    
    private static final String UNSAVED_MESSAGE = " has changed! Do you wish to save your changes?";
    
    
    private SQLClientHandler handler;
    
    private MultiplexedToolbar toolbar = new MultiplexedToolbar();
    
    private ActionHandler actionHandler = new ActionHandler();
    
    private TextAreaDefaults defaults = TextAreaDefaults.getDefaults();
    private JEditTextArea area;
    private JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private JTabbedPane tabs = new JTabbedPane(JTabbedPane.BOTTOM);
    private String noresults = 
        "<html><body bgcolor=\"white\">" +
        "<center>No results to display</center>" +
        "</body></html>";
    
    private JTextPane htmlText = new JTextPane();
    private JScrollPane scroll = new JScrollPane(htmlText);
    
    private KeywordMap map = new KeywordMap(true, keywords.length);
    private ArrayList customTabs = new ArrayList();
    
    private VFSPanel vfsdisplay = new VFSPanel();
    private JSplitPane vfssplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
    
    private JButton saveButton;
    private JButton saveAsButton;
    private JToggleButton sideToggle = null;
    private JToggleButton resultToggle = null;
    private int divloc = 0;
    private int splitloc = 0;
    
    private boolean saved = true;
    private boolean named = false;
    private String filename = "Untitled";
    private Object file = null;
    
    private JStatusBar status = new JStatusBar(3, new int[]{75, 60, 0});
    private JPanel editor = new StatusBarPanel(status, new BorderLayout(0, 0));
    
    private boolean stop = false;
    private Iterator results = null;
    
    private static Icon rotate = new ImageIcon(DBTableModel.class.getResource("/images/rotate.png"));
    private static Icon resultIcon = new ImageIcon(DBTableModel.class.getResource("/images/green-dot.png"));
    private static Icon updateIcon = new ImageIcon(DBTableModel.class.getResource("/images/yellow-dot.png"));
    
    public AdvancedSQLEditor() 
    {
        PanelListener pListener = new PanelListener();
        
        this.handler = SQLClientHandler.getCurrentHandler();
        
        getContentPane().setLayout(new BorderLayout());
        
        initKeywords();
        initStyles();
        initButtons();
        
        area = new JEditTextArea(defaults);
        area.setTokenMarker(new SQLTokenMarker(map));
        area.getPainter().setFont(new Font("Monospaced", 0, 14));
        area.addKeyListener(actionHandler);
        area.addCaretListener(new CaretHandler());
        area.getDocument().addDocumentListener(pListener);
        
        if(ApplicationSettings.getInstance().isMacOSX()) area.setDoubleBuffered(true);
        
        htmlText.setContentType("text/html");
        htmlText.setText(noresults);
        
        editor.add(area, BorderLayout.CENTER);
        editor.add(status, BorderLayout.SOUTH);
        
        split.setTopComponent(editor);
        split.setBottomComponent(htmlText);
        
        split.setDividerLocation(175);
        split.setDividerSize(4);
        split.setResizeWeight(0.5);
        split.setBorder(null);
        
        vfssplit.setDividerSize(4);
        vfssplit.setDividerLocation(200);
        vfssplit.setBorder(null);
        
        vfsdisplay.addVFSPanelListener(pListener);
        
        vfssplit.setLeftComponent(vfsdisplay);
        vfssplit.setRightComponent(editor);
        
        tabs.setTabPlacement(tabs.BOTTOM);
        
        getContentPane().add(toolbar.getToolbar(), BorderLayout.NORTH);
        getContentPane().add(vfssplit, BorderLayout.CENTER);
        
        newDocument();
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        
        setBounds(50, 30, 625, 425);
        
        status.setAlignment(JLabel.CENTER, 1);
        
        status.setText("Ready", 0);
        status.setText((area.getCaretLine()+1)+":"+(area.getCaretPosition()-area.getLineStartOffset(area.getCaretLine())+1), 1);
        
        this.addComponentListener(new ComponentListener()
        {
            public void componentResized(ComponentEvent evt) {}

            public void componentMoved(ComponentEvent evt) {}
            
            public void componentShown(ComponentEvent evt) 
            {
                area.requestFocus();
            }
            
            public void componentHidden(ComponentEvent evt) {}
        });
    }

    public void setTitle(String title)
    {
        super.setTitle(title);
        
        if(ApplicationSettings.getInstance().isMacOSX())
        {
            Component comp = area;
            
            while(comp.getParent() != null) comp = comp.getParent();
            
            if(comp instanceof Frame) ((Frame)comp).setTitle(title);
        }
    }
    
    private void initButtons()
    {
        JButton button;
        ImageIcon icon;
        
        String meta = "control ";
        String metaName = "Ctrl+";
            
        if(ApplicationSettings.getInstance().isMacOSX()) 
        {
            meta = "meta ";
            metaName = "Apple+";
        }
        
        for(int i=0; i<buttonIcons.length; i++)
        {
            if(buttonIcons[i] == null) 
            {
                toolbar.addSeparator();
            }
            else if(buttonActions[i].equals("Show Side Panel"))
            {
                icon = new ImageIcon(getClass().getResource(buttonIcons[i]));
                
                sideToggle = new JToggleButton(icon);
             
                sideToggle.setActionCommand(buttonActions[i]);
                sideToggle.addActionListener(actionHandler);
                sideToggle.setToolTipText(buttonActions[i]);
                sideToggle.setSelected(true);
                
                toolbar.add(sideToggle);
            }
            else if(buttonActions[i].equals("Show Results"))
            {
                icon = new ImageIcon(getClass().getResource(buttonIcons[i]));
                
                resultToggle = new JToggleButton(icon);
             
                resultToggle.setActionCommand(buttonActions[i]);
                resultToggle.addActionListener(actionHandler);
                resultToggle.setToolTipText(buttonActions[i]);
                
                toolbar.add(resultToggle);
            }
            else
            {
                icon = new ImageIcon(getClass().getResource(buttonIcons[i]));
                
                button = new JButton(icon);
                
                button.setActionCommand(buttonActions[i]);
                button.addActionListener(actionHandler);
                
                if(shortcuts[i] != null) button.setToolTipText(buttonActions[i]+" ("+metaName+shortcuts[i]+")");
                else button.setToolTipText(buttonActions[i]);
                
                if(buttonActions[i].equals("Save")) saveButton = button;
                if(buttonActions[i].equals("Save As...")) saveAsButton = button;
                
                toolbar.add(button);
            }
        }
    }
    
    public static String[] getKeywords(String input)
    {
        StringTokenizer tokens = new StringTokenizer(input, ",");
        String[] words = new String[tokens.countTokens()+keywords.length];
        
        for(int i=0; i<keywords.length; i++) words[i] = keywords[i];
        for(int i=keywords.length; i<words.length; i++) words[i] = tokens.nextToken().trim();
        
        return words;
    }
    
    private void initKeywords()
    {   
        SQLClientHandler handler = this.handler.getConnection();
        String[] keywords = this.keywords;
        
        try
        {
            keywords = getKeywords(handler.getMetaData().getSQLKeywords());
            
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
        defaults.inputHandler = new SQLEditorInputHandler(this);
        defaults.inputHandler.addDefaultKeyBindings();
        
        defaults.styles[Token.KEYWORD1] = new SyntaxStyle(Color.blue, false, true);
        defaults.styles[Token.COMMENT1] = new SyntaxStyle(new Color(0x06A548), true, false);
        defaults.styles[Token.INVALID] = new SyntaxStyle(Color.red, false, false);
        defaults.styles[Token.LITERAL1] = new SyntaxStyle(Color.magenta, false, false);
    }
    
    public void stop()
    {
        stop = true;
    
        if(results != null && results instanceof InterruptibleProcess) ((InterruptibleProcess)results).stop();
    }
    
    public void execute()
    {
        ThreadPool.run(new Runnable()
        {
            public void run()
            {
                synchronized(editor)
                {
                    JTable table = new JTable();
                    JScrollPane scroll = new JScrollPane(table);
                    int location = split.getDividerLocation();
                    int vlocation = vfssplit.getDividerLocation();
                    
                    Object temp;

                    StringBuffer text;
                    String sql;

                    int next = 0;
                    long timer = System.currentTimeMillis();

                    if(area.getSelectionEnd()-area.getSelectionStart() > 0) text = new StringBuffer(area.getSelectedText());
                    else text = new StringBuffer(area.getText());

                    if(text.toString().trim().length() < 1) return;

                    customTabs.clear();
                    tabs.removeAll();
                    stop = false;
                    results = null;

                    JStatusBar.setText(AdvancedSQLEditor.this, "Loading...", rotate, 0);

                    try
                    {
                        while(next < text.length() && !stop)
                        {
                            if(text.charAt(next) == ';')
                            {
                                sql = text.substring(0, next);
                                text.delete(0, next+1);
                                next = 0;

                                System.out.println(sql);

                                if(sql.trim().length() > 0)
                                {
                                    results = handler.executeQuery(sql);

                                    while(results.hasNext()) 
                                    {
                                        temp = results.next();
                                        
                                        if(temp instanceof Exception) throw (Exception)temp;
                                        else if(temp instanceof DBTableModel) customTabs.add(new CustomTab(sql, resultIcon, (TableModel)temp));
                                        else customTabs.add(new CustomTab(sql, updateIcon, (TableModel)temp));
                                    }
                                }
                            }
                            else if(text.charAt(next) == '\'')
                            {
                                next++;

                                while(next < text.length() && text.charAt(next) != '\'')
                                {
                                    if(next < text.length() && text.charAt(next) == '\\') next++;
                                    next++;
                                }
                            }

                            next++;
                        }

                        System.out.println(text.toString());

                        if(text.toString().trim().length() > 0 && !stop)
                        {
                            results = handler.executeQuery(text.toString());

                            while(results.hasNext())
                            {
                                temp = results.next();
                                        
                                if(temp instanceof Exception) throw (Exception)temp;
                                else if(temp instanceof DBTableModel) customTabs.add(new CustomTab(text.toString(), resultIcon, (TableModel)temp));
                                else customTabs.add(new CustomTab(text.toString(), updateIcon, (TableModel)temp));
                            }
                        }

                        getContentPane().remove(area);
                        split.setTopComponent(editor);
                        vfssplit.setRightComponent(split);
                        vfssplit.setDividerLocation(vlocation);
                        resultToggle.setSelected(true);

                        if(tabs.getTabCount() == 1)
                        {
                            split.setBottomComponent(tabs.getComponent(0));
                            split.setDividerLocation(location);
                        }
                        else
                        {
                            split.setBottomComponent(tabs);
                            split.setDividerLocation(location);
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        ErrorReport.displayError(e, AdvancedSQLEditor.this);
                    }

                    JStatusBar.setText(AdvancedSQLEditor.this, "Ready", 0);
                    JStatusBar.setText(AdvancedSQLEditor.this, (System.currentTimeMillis()-timer)+" ms", 2);

                    results = null;
                    
                    revalidate();
                }
            }
        });
    }
    
    public boolean isSaved()
    {
        return saved;
    }
    
    protected void setSaved(boolean saved)
    {
        this.saved = saved;
        
        saveButton.setEnabled(!saved);
        saveAsButton.setEnabled(!saved);
    }
    
    public void saveAs()
    {
        Component parent = AdvancedSQLEditor.this;
        FileDialog dialog;
        Reader in;
        StringBuffer buffer = new StringBuffer();

        int data = 0;
                
        while(parent.getParent() != null) parent = parent.getParent();
        dialog = new FileDialog((Frame)parent, "Select SQL script...", FileDialog.SAVE);
        dialog.show(true);

        if(dialog.getFile() == null) return;
        
        file = new File(dialog.getDirectory()+dialog.getFile());
        filename = dialog.getFile();
        named = true;
        
        save();
    }
    
    public void save()
    {
        try
        {
            Writer out = null;
            String output = area.getText();

            int data = 0;
            
            if(!named) 
            {
                saveAs();
                return;
            }
            
            if(file instanceof File) out = new PrintWriter(new FileOutputStream((File)file));
            if(file instanceof VFSFile) out = new PrintWriter(((VFSFile)file).getFileOutputStream());

            out.write(output);

            setSaved(true);
            setTitle("SQL Editor - "+filename);
            
            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

            Container parent = this;

            while(parent.getParent() != null) parent = parent.getParent();
            ErrorReport.displayError(e, (Frame)parent);
        }
    }
    
    public void newDocument()
    {
        if(!isSaved())
        {
            int choice = JOptionPane.showConfirmDialog(AdvancedSQLEditor.this, filename+UNSAVED_MESSAGE, "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            switch(choice)
            {
                case 0:
                    save();
                    break;

                case 2:
                    return;
            }
        }

        area.setText("");
        area.clearEdits();
        area.setCaretPosition(0);

        filename = "Untitled";
        file = null;
        
        named = false;
        setSaved(true);
        setTitle("SQL Editor - "+filename);
    }

    public void open()
    {
        try
        {
            Component parent = AdvancedSQLEditor.this;
            FileDialog dialog;
            Reader in;
            StringBuffer buffer = new StringBuffer();

            int data = 0;


            if(!isSaved())
            {
                int choice = JOptionPane.showConfirmDialog(AdvancedSQLEditor.this, filename+UNSAVED_MESSAGE, "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                switch(choice)
                {
                    case 0:
                        save();
                        break;

                    case 2:
                        return;
                }
            }

            while(parent.getParent() != null) parent = parent.getParent();
            dialog = new FileDialog((Frame)parent, "Select SQL script...", FileDialog.LOAD);
            dialog.show(true);

            if(dialog.getFile() == null) return;

            file = new File(dialog.getDirectory()+dialog.getFile());
            in = new BufferedReader(new InputStreamReader(new FileInputStream((File)file)));

            while((data = in.read()) >= 0) buffer.append((char)data);

            if(buffer.length() > 0) area.setText(buffer.toString());

            area.setText(buffer.toString());
            area.clearEdits();
            area.setCaretPosition(0);

            filename = dialog.getFile();
            named = true;
            setSaved(true);
            setTitle("SQL Editor - "+filename);

            in.close();

        }
        catch(IOException e)
        {
            e.printStackTrace();

            Container parent = AdvancedSQLEditor.this;

            while(parent.getParent() != null) parent = parent.getParent();
            ErrorReport.displayError(e, (Frame)parent);
        }
    }
    
    public JStatusBar getStatusBar()
    {
        return status;
    }
    
    private class ActionHandler implements ActionListener, KeyListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            if(evt.getActionCommand().equals("New")) newDocument();
            if(evt.getActionCommand().equals("Open")) open();
            if(evt.getActionCommand().equals("Save")) save();
            if(evt.getActionCommand().equals("Save As...")) saveAs();
            
            if(evt.getActionCommand().equals("Run")) execute();
            if(evt.getActionCommand().equals("Stop")) stop();
            
            if(evt.getActionCommand().equals("Cut")) area.cut();
            if(evt.getActionCommand().equals("Copy")) area.copy();
            if(evt.getActionCommand().equals("Paste")) area.paste();
            if(evt.getActionCommand().equals("Undo")) area.undo();
            if(evt.getActionCommand().equals("Redo")) area.redo();
            
            if(evt.getActionCommand().equals("Show Side Panel")) toggleSidePanel();
            if(evt.getActionCommand().equals("Show Results")) toggleResult();
            
        }
        
        private void toggleSidePanel()
        {
            if(vfsdisplay.isVisible()) divloc = vfssplit.getDividerLocation();
            vfsdisplay.setVisible(!vfsdisplay.isVisible());
        }
        
        private void toggleResult()
        {
            if(vfsdisplay.isVisible()) divloc = vfssplit.getDividerLocation();
            splitloc = split.getDividerLocation();
            
            if(resultToggle.isSelected())
            {
                split.setTopComponent(editor);
                vfssplit.setRightComponent(split);
            }
            else
            {
                vfssplit.setRightComponent(editor);
            }
            
            vfssplit.setDividerLocation(divloc);
            split.setDividerLocation(splitloc);
        }
        
        public void keyPressed(KeyEvent keyEvent)
        {
            
        }
        
        public void keyReleased(KeyEvent keyEvent)
        {
        }
        
        public void keyTyped(KeyEvent keyEvent)
        {
        }
    }
    
    private class CustomTab
    {
        private JTable table = new JTable();
        private TableModel model;
        private JScrollPane scroll = new JScrollPane(table);
        
        private String title;
        
        public CustomTab(String title, Icon icon, TableModel model)
        {
            this.title = title;
            this.model = model;
            
            table.setModel(model);
            table.setAutoResizeMode(table.AUTO_RESIZE_OFF);
            tabs.addTab(trimText(title), icon, scroll, title);
        }
        
        public String trimText(String sql)
        {
            StringTokenizer tokenizer = new StringTokenizer(sql.trim());
            String title;
        
            sql = "";

            while(tokenizer.hasMoreTokens()) sql += tokenizer.nextToken()+" ";

            title = sql;

            if(title.length() > 25) title = title.substring(0, 25)+"...";
            
            return title;
        }
    }
    
    private class PanelListener implements VFSPanelListener, DocumentListener
    {
        private static final String binaryMessage = 
            "Warning! This file appears to contain binary (non-text) data! Editing this\n" +
            "file may cause damage to your data. Are you sure you want to open it?";
        
        public void VFSFileDoubleClicked(VFSFile file)
        {
            try
            {
                StringBuffer buffer = new StringBuffer();
                InputStream in = file.getFileInputStream();
                int data;
                boolean binaryok = false;
                
                
                if(!isSaved())
                {
                    int choice = JOptionPane.showConfirmDialog(AdvancedSQLEditor.this, filename+UNSAVED_MESSAGE, "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    
                    switch(choice)
                    {
                        case 0:
                            save();
                            break;
                            
                        case 2:
                            return;
                    }
                }
                
                while((data = in.read()) >= 0) 
                {
                    if(!binaryok && data > 127)
                    {
                        binaryok = (JOptionPane.showConfirmDialog(AdvancedSQLEditor.this, binaryMessage, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) < 1);
                        
                        if(!binaryok) 
                        {
                            in.close();
                            return;
                        }
                    }
                    
                    buffer.append((char)data);
                }
                
                area.setText(buffer.toString());
                area.clearEdits();
                area.setCaretPosition(0);
                
                AdvancedSQLEditor.this.file = file;
                filename = file.getName();
                named = true;
                setSaved(true);
                setTitle("SQL Editor - "+filename);
                
                in.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        public void VFSPanelHidden()
        {
            if(vfssplit.getDividerLocation() > 0) divloc = vfssplit.getDividerLocation();
            
            sideToggle.setSelected(false);
        }
        
        public void VFSPanelShown()
        {
            vfssplit.setDividerLocation(divloc);
            sideToggle.setSelected(true);
        }
        
        public void removeUpdate(DocumentEvent documentEvent)
        {
            changedUpdate(documentEvent);
        }
        
        public void insertUpdate(DocumentEvent documentEvent)
        {
            changedUpdate(documentEvent);
        }
        
        public void changedUpdate(DocumentEvent documentEvent)
        {
            setSaved(false);
            setTitle("SQL Editor - "+filename+" *");
        }
        
    }
    
    private class CaretHandler implements CaretListener
    {
        public void caretUpdate(CaretEvent caretEvent)
        {
            JStatusBar.setText(AdvancedSQLEditor.this, (area.getCaretLine()+1)+":"+(area.getCaretPosition()-area.getLineStartOffset(area.getCaretLine())+1), 1);
        }
    }
}