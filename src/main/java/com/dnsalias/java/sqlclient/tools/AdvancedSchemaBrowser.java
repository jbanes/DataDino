/*
 * AdvancedSchemaBrowser.java
 *
 * Created on September 10, 2002, 11:30 PM
 */

package com.dnsalias.java.sqlclient.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.datadino.sqlclient.ui.*;

import com.dnsalias.java.sqlclient.*;
import com.dnsalias.java.sqlclient.dialog.*;
import com.dnsalias.java.sqlclient.drivers.*;
import com.dnsalias.java.sqlclient.gdbi.*;
import com.dnsalias.java.sqlclient.gdbi.panels.*;
import com.dnsalias.java.sqlclient.util.*;
import com.dnsalias.java.sqlclient.ui.*;

/**
 *
 * @author  jbanes
 * @version 
 */
public class AdvancedSchemaBrowser extends JInternalFrame implements StatusBarOwner
{
    private SQLClientHandler handler;
    private SchemaInterface schema;
    
    private DefaultListModel defaultModel = new DefaultListModel();
    
    private TabInfo[] tabInfo;
    private DatabaseInterfacePanel[] panels;
    private String schemaFilter = "<all>";
    private DBObject lastSelected = null;
    private boolean enableSchema = true;
    
    private JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    
    private JPanel leftPanel = new JPanel(new BorderLayout());
    private JPanel tablePanel = new JPanel(new BorderLayout());
    private MultiplexedToolbar toolbar = new MultiplexedToolbar();
    private JSlideBar tableTabs = new JSlideBar();
    
    private JPanel schemaPanel = new JPanel(new BorderLayout(3, 3));
    private JComboBox selectedSchema = new JComboBox();
    private JButton schemaRefresh = new JButton(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Refresh16.gif")));
    
    private JTabbedPane tabPanel = new JTabbedPane();
    private JStatusBar statusBar = new JStatusBar(3, new int[]{75, 100, 0});
    
    private Border oldBorder = new javax.swing.plaf.metal.MetalBorders.ButtonBorder();
    private Border emptyBorder = new javax.swing.plaf.metal.MetalBorders.RolloverButtonBorder();
    
    private JEditorPane htmlintro;
    private JPanel placeHolder = new StatusBarPanel(statusBar);
    
    private boolean loading = true;
    private boolean popup = false;
    private Object old = null;
    
    private boolean[] tabloading;
    
    public AdvancedSchemaBrowser()
    {
        this.handler = SQLClientHandler.getCurrentHandler();
        this.schema = new SchemaInterface(handler);
        
        try
        {
            initComponents();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            ErrorReport.displayError(e, this);
        }
    }
    
    public JStatusBar getStatusBar()
    {
        return statusBar;
    }

    private String loadHTMLPage(String page) throws IOException
    {
        InputStream in = getClass().getResourceAsStream(page);
        StringBuffer buffer = new StringBuffer();
        
        int data;
        int left = 0;
        int right = 0;
        
        while((data = in.read()) >= 0) buffer.append((char)data);
        
        in.close();
        
        while((left = buffer.toString().indexOf("&[\"", left)) >= 0)
        {
            right = buffer.toString().indexOf("\"]", left);
            buffer.replace(left, right, getClass().getResource(buffer.substring(left+3, right)).toString());
        }
        
        return buffer.toString();
    }
    
    private void initComponents() throws SQLException
    {
        int tab;
        
        setTitle("Schema Browser");
        
        getContentPane().add(split, BorderLayout.CENTER);
        
        placeHolder.setLayout(new BorderLayout());
        placeHolder.add(tabPanel, BorderLayout.CENTER);
        placeHolder.add(statusBar, BorderLayout.SOUTH);
        
        split.setLeftComponent(leftPanel);
        split.setRightComponent(placeHolder);
        split.setDividerLocation(175);
        split.setDividerSize(4);
        split.setBorder(new EmptyBorder(0, 0, 1, 0));
        
        leftPanel.add(tablePanel, BorderLayout.CENTER);
        leftPanel.add(schemaPanel, BorderLayout.NORTH);
        
        tablePanel.add(toolbar.getToolbar(), BorderLayout.NORTH);
        tablePanel.add(tableTabs, BorderLayout.CENTER);
        
        try
        {
            htmlintro = new JEditorPane("text/html", loadHTMLPage("/welcome.html"));
            
            htmlintro.setEditable(false);
            
            split.setRightComponent(htmlintro);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        tab = initializeTableTabs();
        initializeSchemaPanel();
        initializeToolbar();
        
        tabPanel.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                activateTab();
            }
        });
        
        
        ThreadPool.run(new TableListLoader(schemaFilter, tabInfo[tab]));
        
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);
        setBounds(25, 15, 625, 390);
    }
    
    private void initializeToolbar()
    {
        JButton add = new JButton();
        JButton refresh = new JButton();
        JButton delete = new JButton();
        
        
        add.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Add16.gif")));
        add.setToolTipText("New Table");
        add.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                addActionPerformed(evt);
            }
        });
        
        toolbar.add(add);
        
        delete.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Delete16.gif")));
        delete.setToolTipText("Delete Table");
        delete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                deleteActionPerformed(evt);
            }
        });
        
        toolbar.add(delete);
        
        refresh.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Refresh16.gif")));
        refresh.setToolTipText("Refresh Table List");
        refresh.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                refreshActionPerformed(evt);
            }
        });
        
        toolbar.add(refresh);
    }
    
    private void addActionPerformed(ActionEvent evt)
    {
        AdvancedTableEditor editor = new AdvancedTableEditor(handler);
        this.getDesktopPane().add(editor);
        this.getDesktopPane().moveToFront(editor);
        editor.setVisible(true);
        
        if(ApplicationSettings.getInstance().isMacOSX()) MDIFrame.createRootless(editor);
    }
    
    private void deleteActionPerformed(ActionEvent evt)
    {
        SQLClientHandler handler = this.handler.getConnection();
        TabInfo tab = tabInfo[tableTabs.getSelectedIndex()];
        DBObject table = (DBObject)tab.list.getSelectedValue();
        
        if(table == null) return;
        
        try
        {
            if(JOptionPane.showConfirmDialog(this, "Are you sure you want to delete "+table+"?") == JOptionPane.YES_OPTION)
            {
                tab.list.clearSelection();
            
                System.out.println("drop "+tab.name+" "+table.getDelimitedString());
                handler.executeSQLChange("drop "+tab.name+" "+table.getDelimitedString());

                refreshActionPerformed(new ActionEvent(tab.list, 0, "Refresh"));
                
                revalidate();
            }
            
            handler.completeOperation();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            ErrorReport.displayError(e, this);
        }
        
    }

    private void refreshActionPerformed(ActionEvent evt)
    {   
        DBObject def = (DBObject)tabInfo[tableTabs.getSelectedIndex()].list.getSelectedValue();
        
        if(old != null) def = (DBObject)old;
        
        ThreadPool.run(new TableListLoader(schemaFilter, tabInfo[tableTabs.getSelectedIndex()], def));
    }
    
    private int initializeTableTabs() throws SQLException
    {
        String[] categories = schema.getCategories();
        int tab = 0;
        ListSelectionListener listListener = new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e) 
            {
                selectedTableAction();
            }
        };
        
        tabInfo = new TabInfo[categories.length];
        
        for(int i=0; i<categories.length; i++)
        {
            JScrollPane scroll = new JScrollPane();
            
            tabInfo[i] = new TabInfo();
            
            tabInfo[i].name = categories[i];
            tabInfo[i].list = new JList();
            tabInfo[i].list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tabInfo[i].list.addListSelectionListener(listListener);
            tabInfo[i].popHandle = new PopupHandler(tabInfo[i].list);
            
            if(tabInfo[i].name.equalsIgnoreCase("TABLE")) tab = i;
            
            scroll.setViewportView(tabInfo[i].list);
            tableTabs.addTab(tabInfo[i].name, scroll);
        }
        
        tableTabs.setSelectedIndex(tab);
        
        tableTabs.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent changeEvent)
            {
                int selected = tableTabs.getSelectedIndex();
                
                if(tabInfo[selected].cached) return;

                System.out.println("Caching "+schemaFilter+"."+tabInfo[selected].name);

                ThreadPool.run(new TableListLoader(schemaFilter, tabInfo[selected]));
            }
        });
        
        return tab;
    }
    
    private void initializeSchemaPanel() throws SQLException
    {
        SQLClientHandler handler = this.handler.getConnection();
        String[] schemas = schema.getSchemas();
        String username = handler.getCurrentProfile().username;
        
        selectedSchema.addItem("<all>");
        selectedSchema.setSelectedIndex(0);
        selectedSchema.setToolTipText("Choose a schema to filter the list of tables");
        
        schemaPanel.setBorder(new EmptyBorder(3, 3, 3, 2));
        schemaRefresh.setOpaque(false);
        
        for(int i=0; i<schemas.length; i++)
        {
            selectedSchema.addItem(schemas[i]);
            
            if(schemas[i].equalsIgnoreCase(username)) 
            {
                selectedSchema.setSelectedIndex(i+1);
                schemaFilter = schemas[i];
            }
        }
        
        if(!handler.getMetaData().supportsSchemasInTableDefinitions()) 
        {
            schemaPanel.setVisible(false);
            selectedSchema.setSelectedItem(null);
        }
        
        schemaPanel.add(selectedSchema, BorderLayout.CENTER);
        schemaPanel.add(schemaRefresh, BorderLayout.EAST);
        
        if(ApplicationSettings.getInstance().isMacOSX())
        {
            schemaRefresh.setBorder(emptyBorder);
            schemaRefresh.addMouseListener(new HoverListener());
        }
        
        schemaRefresh.setToolTipText("Refresh Schema List");
        schemaRefresh.setPreferredSize(new Dimension(24, 20));
        schemaRefresh.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                refreshSchemaActionPerformed(evt);
            }
        });
        
        selectedSchema.setEditable(true);
        selectedSchema.setPreferredSize(new java.awt.Dimension(50, 22));
        selectedSchema.setMinimumSize(new java.awt.Dimension(50, 22));
        selectedSchema.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                selectedSchemaActionPerformed(evt);
            }
        });
        
        handler.completeOperation();
    }
    
    private synchronized void refreshSchemaActionPerformed(ActionEvent evt)
    {
        try
        {
            String[] schemas = schema.getSchemas();
            String selected = schemaFilter;
            
            enableSchema = false;
            
            selectedSchema.removeAllItems();
            selectedSchema.addItem("<all>");

            for(int i=0; i<schemas.length; i++)
            {
                selectedSchema.addItem(schemas[i]);
            }

            selectedSchema.setSelectedItem(selected);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            ErrorReport.displayError(e, this);
        }
            
        enableSchema = true;
    }
    
    private void selectedSchemaActionPerformed(ActionEvent evt)
    {   
        if(!enableSchema) return;
        if(selectedSchema.getSelectedItem() == null) return;
        if(schemaFilter.equals(selectedSchema.getSelectedItem())) return;
        
        schemaFilter = selectedSchema.getSelectedItem().toString();
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        for(int i=0; i<tabInfo.length; i++)
        {
            tabInfo[i].cached = false;
            tabInfo[i].list.setModel(defaultModel);
        }

        ThreadPool.run(new TableListLoader(schemaFilter, tabInfo[tableTabs.getSelectedIndex()]));
        
        setCursor(Cursor.getDefaultCursor());
    }
    
    private synchronized void selectedTableAction()
    {
        if(tableTabs.getSelectedIndex() < 0 || tabInfo[tableTabs.getSelectedIndex()].list.getSelectedIndex() < 0 || popup) return;
        
        if(split.getRightComponent() != placeHolder)
        {
            split.setRightComponent(placeHolder);
            split.setDividerLocation(175);
            split.setDividerSize(4);
        }
        
        ThreadPool.run(new Runnable() 
        {
            public void run()
            {
                synchronized(tableTabs)
                {
                    try
                    {
                        DBObject dbo = (DBObject)tabInfo[tableTabs.getSelectedIndex()].list.getSelectedValue();
                        String[] tabs = schema.getCategoryTabs(dbo.getType());
                        int selectedIndex = tableTabs.getSelectedIndex();
                        String tabName = "";

                        if(tabPanel.getSelectedIndex() >= 0) tabName = tabPanel.getTitleAt(tabPanel.getSelectedIndex());
                        if(dbo.equals(lastSelected)) return;
                        if(warnDataLoss()) return;

                        loading = true;

                        tabPanel.removeAll();
                        panels = new DatabaseInterfacePanel[tabs.length];
                        tabloading = new boolean[tabs.length];

                        for(int i=0; i<tabs.length; i++) 
                        {
                            panels[i] = schema.getTabInterface(dbo, tabs[i]);
                            tabPanel.addTab(tabs[i], panels[i].getPanel());

                            if(tabs[i].equals(tabName)) tabPanel.setSelectedIndex(i);
                        }

                        for(int i=0; i<tabInfo.length; i++)
                        {
                            if(i != selectedIndex) tabInfo[i].list.clearSelection();
                        }

                        tabloading[tabPanel.getSelectedIndex()] = true;
                        panels[tabPanel.getSelectedIndex()].activate();
                        tabloading[tabPanel.getSelectedIndex()] = false;

                        lastSelected = dbo;
                    }
                    catch(SQLException e)
                    {
                        e.printStackTrace();
                        ErrorReport.displayError(e, AdvancedSchemaBrowser.this);
                    }
                    finally
                    {
                        loading = false;
                    }
                }
            }
        });
    }
    
    private void activateTab()
    {
        if(tabPanel.getSelectedIndex() < 0 || loading || tabloading[tabPanel.getSelectedIndex()]) return;
        
        ThreadPool.run(new Runnable()
        {
            public void run()
            {
                tabloading[tabPanel.getSelectedIndex()] = true;
                panels[tabPanel.getSelectedIndex()].activate();
                tabloading[tabPanel.getSelectedIndex()] = false;
            }
        });
    }
    
    private boolean warnDataLoss()
    {
        boolean needsCommit = false;
        
        if(panels == null) return false;
        
        for(int i=0; i<panels.length; i++)
        {
            if(panels[i] != null && !panels[i].isSaved()) needsCommit = true;
        }
        
        if(!needsCommit) return false;
        
        String message = "Your changes to the database will be lost after\n"
                          + "this action! Would you like to commit first?";
        
        int selected = JOptionPane.showConfirmDialog(this, message);
        
        if(selected == JOptionPane.YES_OPTION)
        {
            for(int i=0; i<panels.length; i++)
            {
                if(!panels[i].isSaved()) panels[i].saveChanges();
            }
        }
        else if(selected == JOptionPane.NO_OPTION)
        {
            //nothing for now
        }
        else
        {
            return true;
        }
        
        return false;
    }
    
    private class TabInfo
    {
        public String name = "";
        public boolean cached = false;
        public JList list = null;
        
        public PopupHandler popHandle;
    }
    
    private class TableListLoader implements Runnable
    {
        private String schemaName;
        private TabInfo tabInfo;
        private DBObject selected;
        
        public TableListLoader(String schemaName, TabInfo tabInfo)
        {
            this(schemaName, tabInfo, null);
        }
        
        public TableListLoader(String schemaName, TabInfo tabInfo, DBObject selected)
        {
            if(schemaName.equals("<all>")) schemaName = null;
            
            this.schemaName = schemaName;
            this.tabInfo = tabInfo;
            this.selected = selected;
            
            tabInfo.cached = true;
        }
        
        public void run()
        {   
            try
            {
                Iterator iterator = schema.getDBObjects(tabInfo.name, schemaName);
                AdvancedListModel model = new AdvancedListModel();
                
                Vector temp = new Vector();
                
                tabInfo.list.setModel(model);
                
                while(iterator.hasNext())
                {
                    temp.add(iterator.next());
                    
                    if(temp.size() > 1000) 
                    {
                        model.add(temp);
                        temp.clear();
                    }
                }
                
                if(temp.size() > 0) 
                {
                    model.add(temp);
                    temp.clear();
                }
                
                if(selected != null)
                {
                    for(int i=0; i<model.getSize(); i++)
                    {
                        if(model.getElementAt(i).equals(selected))
                        {
                            tabInfo.list.setSelectedIndex(i);
                            return;
                        }
                    }
                    
                    tabPanel.removeAll();
                }
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private class HoverListener extends MouseAdapter
    {
        public void mouseEntered(MouseEvent evt)
        {
            ((JComponent)evt.getComponent()).setBorder(oldBorder);
        }
        
        public void mouseExited(MouseEvent evt)
        {
            ((JComponent)evt.getComponent()).setBorder(emptyBorder);
        }
    }
    
    private class PopupHandler implements MouseListener, ActionListener, PopupMenuListener
    {
        private JComponent comp;
        private JPopupMenu menu = new JPopupMenu();
        
        private JMenuItem newTable = new JMenuItem("Create New Table");
        private JMenuItem viewTable = new JMenuItem("View Table");
        private JMenuItem refreshTable = new JMenuItem("Refresh Table List");
        private JMenuItem deleteTable = new JMenuItem("Delete Table");
        
        private int selectedid = -1;
        
        private boolean selected = false;
        
        public PopupHandler(JComponent comp)
        {
            this.comp = comp;
            
            comp.addMouseListener(this);
            
            newTable.addActionListener(this);
            viewTable.addActionListener(this);
            refreshTable.addActionListener(this);
            deleteTable.addActionListener(this);
            
            menu.add(viewTable);
            menu.addSeparator();
            menu.add(newTable);
            menu.addSeparator();
            menu.add(refreshTable);
            menu.addSeparator();
            menu.add(deleteTable);
            
            menu.addPopupMenuListener(this);
        }
        
        public void actionPerformed(ActionEvent evt)
        {
            if(selectedid < 0) return;
            
            popup = true;
            old = ((JList)comp).getSelectedValue();
            ((JList)comp).setSelectedIndex(selectedid);
            
            if(evt.getSource() == newTable) addActionPerformed(new ActionEvent(comp, 0, "New Table"));
            if(evt.getSource() == viewTable) selected = true;
            if(evt.getSource() == refreshTable) refreshActionPerformed(new ActionEvent(comp, 0, "Refresh"));
            if(evt.getSource() == deleteTable) deleteActionPerformed(new ActionEvent(comp, 0, "Delete"));
            
            if(old != null && !selected) 
            {
                ((JList)comp).setSelectedValue(old, false);
                popup = false;
            }
            else
            {
                popup = false;
                selectedTableAction();
            }
            
            selected = false;
            selectedid = -1;
            old = null;
        }
        
        public void mouseExited(MouseEvent mouseEvent)
        {
        }
        
        public void mouseReleased(MouseEvent evt)
        {
            int clicked = ((JList)comp).locationToIndex(evt.getPoint());
            
            if(evt.isPopupTrigger())
            {
                menu.show(comp, evt.getX(), evt.getY());
                old = ((JList)comp).getSelectedValue();
                
                popup = true;
                ((JList)comp).setSelectedIndex(clicked);
            }
        }
        
        public void mousePressed(MouseEvent evt)
        {
            int clicked = ((JList)comp).locationToIndex(evt.getPoint());
            
            if(evt.isPopupTrigger()) 
            {
                menu.show(comp, evt.getX(), evt.getY());
                old = ((JList)comp).getSelectedValue();
                
                popup = true;
                ((JList)comp).setSelectedIndex(clicked);
            }
        }
        
        public void mouseClicked(MouseEvent mouseEvent)
        {
        }
        
        public void mouseEntered(MouseEvent mouseEvent)
        {
        }
        
        public void popupMenuCanceled(PopupMenuEvent evt)
        {
        }
        
        public void popupMenuWillBecomeInvisible(PopupMenuEvent evt)
        {
            selectedid = ((JList)comp).getSelectedIndex();
            
            if(old != null) 
            {
                ((JList)comp).setSelectedValue(old, false);
                popup = false;
            }
            
            old = null;
        }
        
        public void popupMenuWillBecomeVisible(PopupMenuEvent evt)
        {
        }
        
    }
}
