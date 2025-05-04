/*
 * MDIFrame.java
 *
 */

package com.dnsalias.java.sqlclient;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import com.dnsalias.java.sqlclient.dialog.*;
import com.dnsalias.java.sqlclient.tools.*;
import com.dnsalias.java.sqlclient.ui.*;
import com.dnsalias.java.sqlclient.util.*;

import com.datadino.sqlclient.tools.*;
import com.datadino.sqlclient.ui.*;


/**
 *
 * @author  jbanes
 */
public class MDIFrame extends JFrame 
{
    private static MDIFrame master = null;
    private static DBLogin login = null;
    
    private static JFrame macLogin = null;
    private static ArrayList iframes = new ArrayList();
    private static ArrayList frames = new ArrayList();
    private static ArrayList menus = new ArrayList();
    
    private static WindowManager manager = new WindowManager();
    private static Hashtable internalManager;
    
    public static final boolean webstart = (System.getProperty("javawebstart.version") != null);
    
    /** Creates new form MDIFrame */
    private MDIFrame() 
    {
        Image image;
        Image icon = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/icon.png"));
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        if(ApplicationSettings.JAVALOBBY) image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/javalobby.png"));
        else image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/grey006.gif"));
        
        login = new DBLogin();
        setIconImage(icon);
        
        initComponents();
        
        MultiplexedToolbar tbar = new MultiplexedToolbar();
        Component[] comp = toolbar.getComponents();
        WindowSettings winSettings = (WindowSettings)ApplicationSettings.getInstance().loadPersistentObject("MDIFrameSettings");
        
        for(int i=0; i<comp.length; i++) 
        {
            toolbar.remove(comp[i]);
            
            if(comp[i] instanceof JLabel) tbar.addSeparator();
            else tbar.add(comp[i]);
        }
        
        tbar.getToolbar().revalidate();
        
        remove(toolbar);
        getContentPane().add(tbar.getToolbar(), BorderLayout.NORTH);
        
        if(!ApplicationSettings.getInstance().isMacOSX())
        {
            jDesktopPane1 = new BackgroundDesktopFrame(image);
            scrollPane.setViewportView(jDesktopPane1);
        }
        
        filemenu.setMnemonic('F');
        viewmenu.setMnemonic('V');
        helpmenu.setMnemonic('H');
        
        setSize(720, 550);
        
        internalManager = (Hashtable)ApplicationSettings.getInstance().loadPersistentObject("InternalWindowSettings");
        if(internalManager == null) internalManager = new Hashtable();
        
        if(winSettings != null)
        {
            this.setBounds(winSettings.bounds);
            if(winSettings.maximized) this.setState(Frame.MAXIMIZED_BOTH);
        }
        
        login.setLocation(getWidth()/2-login.getWidth()/2, getHeight()/2-login.getHeight()/2-25);
    }

    private boolean licensed()
    {
        if(!ApplicationSettings.getInstance().isRegisteredVersion())
        {
            JOptionPane.showMessageDialog(this, "This is a feature of the Professional Edition.\nVisit www.datadino.com to upgrade today!", "", JOptionPane.INFORMATION_MESSAGE);
            
            return false;
        }
        
        return true;
    }
    
    private static void splash(String imagename)
    {
        Image image = Toolkit.getDefaultToolkit().createImage(MDIFrame.class.getResource(imagename));
        JWindow window = new JWindow();
        MediaTracker tracker = new MediaTracker(window);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JLabel label = new JLabel(new ImageIcon(image));
        
        try
        {
            tracker.addImage(image, 0);
            tracker.waitForAll();

            label.setOpaque(false);
            
            window.setBounds((screenSize.width-image.getWidth(null))/2,(screenSize.height-image.getHeight(null))/2, image.getWidth(null), image.getHeight(null));
            window.getRootPane().setLayout(new BorderLayout());
            window.getRootPane().add(label);

            System.out.println("Splash ["+imagename+"] x: "+image.getWidth(null)+" y: "+image.getHeight(null));
            
            window.setVisible(true);

            for(int i=0; i<5; i++)
            {
                Thread.sleep(1000);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        window.dispose();
    }
    
    public static void initialize()
    {
        File file = new File(System.getProperty("user.home")+File.separator+".datadino"+File.separator+"drivers");
        ActionListener action = new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                JNLPServices.showDocument(evt.getActionCommand());
            }
        };
        
        if(ApplicationSettings.JAVALOBBY) splash("/images/jlspecialurl.png");
        
        if(master == null) 
        {
            try
            {
                System.out.println(System.getProperty("os.name"));
                
                if(ApplicationSettings.getInstance().isMacOSX())
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                else
                {
                    LookAndFeel laf = new com.datadino.themes.DataDinoLookAndFeel();
                    //LookAndFeel laf = new com.incors.plaf.alloy.AlloyLookAndFeel();
                    
                    //((com.incors.plaf.alloy.AlloyLookAndFeel)laf).setTheme(new com.incors.plaf.alloy.themes.bedouin.BedouinTheme(), true);

                    ((com.datadino.themes.DataDinoLookAndFeel)laf).setCurrentTheme(new com.datadino.themes.KunststoffDataDinoTheme());
                    
                    UIManager.put("ClassLoader", MDIFrame.class.getClassLoader());
                    UIManager.setLookAndFeel(laf);
                }
            }
            catch(Exception e) {e.printStackTrace();}
            
            if(!file.exists() && !LicenseAgreement.showLicense()) System.exit(0);
            
            master = new MDIFrame();
            if(!ApplicationSettings.getInstance().isMacOSX()) master.show();
            master.jDesktopPane1.add(login);
            
            if(ApplicationSettings.getInstance().isMacOSX())
            {
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                
                macLogin = new JFrame(login.getTitle());
                
                
                macLogin.setJMenuBar(master.menubar);
                macLogin.setBounds(login.getBounds());
                macLogin.setLocation(dim.width/2-macLogin.getWidth()/2, dim.height/2-macLogin.getHeight()/2);
                macLogin.setContentPane(login.getContentPane());
                macLogin.setVisible(true);
                
                macLogin.addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent evt)
                    {
                        //System.exit(0);
                        master.exitForm(null);
                    }
                });
                
                manager.register(macLogin, "Login");
            }
            else
            {
                login.setVisible(true);
                master.jDesktopPane1.setSelectedFrame(login);
            }
            
            if(webstart)
            {
                Icon icon = new ImageIcon(master.getClass().getResource("/toolbarButtonGraphics/development/WebComponent16.gif"));
                JMenuItem menuItem;
                JMenuItem temp = master.helpmenu.getItem(0);

                master.helpmenu.remove(temp);
                
                menuItem = new JMenuItem("Home page", icon);
                menuItem.setActionCommand("http://www.datadino.com/");
                menuItem.addActionListener(action);
                master.helpmenu.add(menuItem);

                menuItem = new JMenuItem("Request a feature", icon);
                menuItem.setActionCommand("http://www.datadino.com/bugzilla");
                menuItem.addActionListener(action);
                master.helpmenu.add(menuItem);

                menuItem = new JMenuItem("Request database support", icon);
                menuItem.setActionCommand("http://www.datadino.com/databases.html");
                menuItem.addActionListener(action);
                master.helpmenu.add(menuItem);

                master.helpmenu.add(new JSeparator());
                master.helpmenu.add(temp);
            }
        }
        
        if(!file.exists()) file.mkdirs();
        
//        System.setSecurityManager(new SecurityBlocker());
        
        try
        {
            ApplicationSettings.getInstance().store(System.out, "");
        }
        catch(Exception e) {e.printStackTrace();}
        
        if(ApplicationSettings.getInstance().isExpired())
        {
            JOptionPane.showMessageDialog(MDIFrame.master, "The trial period has expired! If you wish to \ncontinue using the professional features, \nplease purchase the Professional Edition.");
            ApplicationSettings.getInstance().removePesistentProperty("license-key");
        }
    }
    
    public static void connected()
    {
        if(!ApplicationSettings.getInstance().isMacOSX())
        {
            master.connectitem.setEnabled(false);
            master.disconnectitem.setEnabled(true);
            master.schemaitem.setEnabled(true);
            master.sqleditoritem.setEnabled(true);
            master.datamigratoritem.setEnabled(true);
            master.schemaexportitem.setEnabled(true);

            master.connectButton.setEnabled(false);
            master.disconnectButton.setEnabled(true);
            master.schemaBrowserButton.setEnabled(true);
            master.sqlEditorButton.setEnabled(true);
            master.dataMigratorButton.setEnabled(true);
            master.schemaExporterButton.setEnabled(true);
        }
        
        if(ApplicationSettings.getInstance().isMacOSX())
        {
            macLogin.setVisible(false);
        }
        else
        {
            login.setVisible(false);
            master.jDesktopPane1.remove(login);
        }
            
    }
   
    public static void disconnected()
    {
        if(!ApplicationSettings.getInstance().isMacOSX())
        {
            master.connectitem.setEnabled(true);
            master.disconnectitem.setEnabled(false);
            master.schemaitem.setEnabled(false);
            master.sqleditoritem.setEnabled(false);
            master.datamigratoritem.setEnabled(false);
            master.schemaexportitem.setEnabled(false);

            master.connectButton.setEnabled(true);
            master.disconnectButton.setEnabled(false);
            master.schemaBrowserButton.setEnabled(false);
            master.sqlEditorButton.setEnabled(false);
            master.dataMigratorButton.setEnabled(false);
            master.schemaExporterButton.setEnabled(false);
        }
        
        if(ApplicationSettings.getInstance().isMacOSX())
        {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            
            macLogin.setLocation(dim.width/2-macLogin.getWidth()/2, dim.height/2-macLogin.getHeight()/2-25);
            macLogin.setVisible(true);
            manager.register(macLogin, "Login");
        }
        else
        {
            master.jDesktopPane1.add(login);
            login.setLocation(master.getWidth()/2-login.getWidth()/2, master.getHeight()/2-login.getHeight()/2-25);
            login.setVisible(true);
        }
    }
    
    public static void showSchemaBrowser()
    {
        master.schemaitemActionPerformed(null);   
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        scrollPane = new javax.swing.JScrollPane();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        toolbar = new javax.swing.JToolBar();
        connectButton = new javax.swing.JButton();
        disconnectButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        schemaBrowserButton = new javax.swing.JButton();
        sqlEditorButton = new javax.swing.JButton();
        dataMigratorButton = new javax.swing.JButton();
        schemaExporterButton = new javax.swing.JButton();
        menubar = new javax.swing.JMenuBar();
        filemenu = new javax.swing.JMenu();
        connectitem = new javax.swing.JMenuItem();
        disconnectitem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        licenseitem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        exititem = new javax.swing.JMenuItem();
        viewmenu = new javax.swing.JMenu();
        schemaitem = new javax.swing.JMenuItem();
        sqleditoritem = new javax.swing.JMenuItem();
        datamigratoritem = new javax.swing.JMenuItem();
        schemaexportitem = new javax.swing.JMenuItem();
        windowmenu = new javax.swing.JMenu();
        helpmenu = new javax.swing.JMenu();
        aboutitem = new javax.swing.JMenuItem();

        setTitle("DataDino Database Explorer");
        setFont(new java.awt.Font("Arial", 0, 12));
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

        scrollPane.setViewportView(jDesktopPane1);

        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        connectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons/database-icon.png")));
        connectButton.setToolTipText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                connectitemActionPerformed(evt);
            }
        });

        toolbar.add(connectButton);

        disconnectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons/database-broken-icon.png")));
        disconnectButton.setToolTipText("Disconnect");
        disconnectButton.setEnabled(false);
        disconnectButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                disconnectitemActionPerformed(evt);
            }
        });

        toolbar.add(disconnectButton);

        jLabel1.setText("   ");
        toolbar.add(jLabel1);

        schemaBrowserButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Find16.gif")));
        schemaBrowserButton.setToolTipText("Schema Browser");
        schemaBrowserButton.setEnabled(false);
        schemaBrowserButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                schemaitemActionPerformed(evt);
            }
        });

        toolbar.add(schemaBrowserButton);

        sqlEditorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons/sql-icon.png")));
        sqlEditorButton.setToolTipText("SQL Editor");
        sqlEditorButton.setEnabled(false);
        sqlEditorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                sqleditoritemActionPerformed(evt);
            }
        });

        toolbar.add(sqlEditorButton);

        dataMigratorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons/database-export-icon.png")));
        dataMigratorButton.setToolTipText("Migrate Data");
        dataMigratorButton.setEnabled(false);
        dataMigratorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                datamigratoritemActionPerformed(evt);
            }
        });

        toolbar.add(dataMigratorButton);

        schemaExporterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Export16.gif")));
        schemaExporterButton.setToolTipText("Export Schema");
        schemaExporterButton.setEnabled(false);
        schemaExporterButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                schemaexportitemActionPerformed(evt);
            }
        });

        toolbar.add(schemaExporterButton);

        getContentPane().add(toolbar, java.awt.BorderLayout.NORTH);

        filemenu.setText("File");
        connectitem.setText("Connect");
        connectitem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons/database-icon.png")));
        connectitem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                connectitemActionPerformed(evt);
            }
        });

        filemenu.add(connectitem);

        disconnectitem.setText("Disconnect");
        disconnectitem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons/database-broken-icon.png")));
        disconnectitem.setEnabled(false);
        disconnectitem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                disconnectitemActionPerformed(evt);
            }
        });

        filemenu.add(disconnectitem);

        filemenu.add(jSeparator1);

        licenseitem.setText("License Key");
        licenseitem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Preferences16.gif")));
        licenseitem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                licenseitemActionPerformed(evt);
            }
        });

        filemenu.add(licenseitem);

        filemenu.add(jSeparator2);

        exititem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blank16.png")));
        exititem.setLabel("Exit");
        exititem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exititemActionPerformed(evt);
            }
        });

        filemenu.add(exititem);

        menubar.add(filemenu);

        viewmenu.setText("View");
        schemaitem.setText("Schema Browser");
        schemaitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        schemaitem.setEnabled(false);
        schemaitem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                schemaitemActionPerformed(evt);
            }
        });

        viewmenu.add(schemaitem);

        sqleditoritem.setText("SQL Editor");
        sqleditoritem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        sqleditoritem.setEnabled(false);
        sqleditoritem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                sqleditoritemActionPerformed(evt);
            }
        });

        viewmenu.add(sqleditoritem);

        datamigratoritem.setText("Data Migrator");
        datamigratoritem.setEnabled(false);
        datamigratoritem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                datamigratoritemActionPerformed(evt);
            }
        });

        viewmenu.add(datamigratoritem);

        schemaexportitem.setText("Export Schema");
        schemaexportitem.setEnabled(false);
        schemaexportitem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                schemaexportitemActionPerformed(evt);
            }
        });

        viewmenu.add(schemaexportitem);

        menubar.add(viewmenu);

        windowmenu.setText("Windows");
        menubar.add(windowmenu);

        helpmenu.setText("Help");
        aboutitem.setText("About");
        aboutitem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/About16.gif")));
        aboutitem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                aboutitemActionPerformed(evt);
            }
        });

        helpmenu.add(aboutitem);

        menubar.add(helpmenu);

        setJMenuBar(menubar);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-680)/2, (screenSize.height-500)/2, 680, 500);
    }//GEN-END:initComponents

    private void licenseitemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_licenseitemActionPerformed
    {//GEN-HEADEREND:event_licenseitemActionPerformed
        LicenseManager manager = new LicenseManager();
        
        jDesktopPane1.add(manager);
        
        if(ApplicationSettings.getInstance().isMacOSX()) rootless(manager).setVisible(true);
        else register(manager);
    }//GEN-LAST:event_licenseitemActionPerformed

    private void schemaexportitemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_schemaexportitemActionPerformed
    {//GEN-HEADEREND:event_schemaexportitemActionPerformed
        if(!licensed()) return;
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SchemaExporter exporter = new SchemaExporter();
            
        jDesktopPane1.add(exporter);
        
        if(ApplicationSettings.getInstance().isMacOSX()) rootless(exporter).setVisible(true);
        else register(exporter);
        
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_schemaexportitemActionPerformed

    private void datamigratoritemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_datamigratoritemActionPerformed
    {//GEN-HEADEREND:event_datamigratoritemActionPerformed
        if(!licensed()) return;
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        DataMigrator migrator = new DataMigrator();
            
        jDesktopPane1.add(migrator);
        
        if(ApplicationSettings.getInstance().isMacOSX()) rootless(migrator).setVisible(true);
        else register(migrator);
        
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_datamigratoritemActionPerformed

    private void sqleditoritemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sqleditoritemActionPerformed
    {//GEN-HEADEREND:event_sqleditoritemActionPerformed
        if(!licensed()) return;
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        //SQLEditor editor = new SQLEditor();
        AdvancedSQLEditor editor = new AdvancedSQLEditor();
            
        jDesktopPane1.add(editor);
        
        if(ApplicationSettings.getInstance().isMacOSX()) rootless(editor).setVisible(true);
        else register(editor);
        
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_sqleditoritemActionPerformed

    private void schemaitemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_schemaitemActionPerformed
    {//GEN-HEADEREND:event_schemaitemActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        AdvancedSchemaBrowser browser = new AdvancedSchemaBrowser();
        //SchemaBrowser browser = new SchemaBrowser();
        
        jDesktopPane1.add(browser);
        
        if(ApplicationSettings.getInstance().isMacOSX()) rootless(browser).setVisible(true);
        else register(browser);
        
        setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_schemaitemActionPerformed

    private void aboutitemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aboutitemActionPerformed
    {//GEN-HEADEREND:event_aboutitemActionPerformed
        // Add your handling code here:
        About about = new About();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        
        
        jDesktopPane1.add(about);
        
        if(ApplicationSettings.getInstance().isMacOSX()) about.setLocation(dim.width/2-about.getWidth()/2, dim.height/2-about.getHeight()/2);
        else about.setLocation(getWidth()/2-about.getWidth()/2, getHeight()/2-about.getHeight()/2);
        
        if(ApplicationSettings.getInstance().isMacOSX()) rootless(about).setVisible(true);
        else register(about);
    }//GEN-LAST:event_aboutitemActionPerformed

    private void disconnectitemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_disconnectitemActionPerformed
    {//GEN-HEADEREND:event_disconnectitemActionPerformed
        // Add your handling code here:
        JInternalFrame[] frames = jDesktopPane1.getAllFrames();
        JFrame frame;
        
        for(int i=0; i<frames.length; i++)
        {
            if(frames[i] != login) frames[i].dispose();
        }
        
        while(this.frames.size() > 0)
        {
            frame = (JFrame)this.frames.get(0);
            this.frames.remove(0);
            this.menus.remove(0);
            frame.dispose();
        }
        
        try
        {
            if(SQLClientHandler.getCurrentHandler() != null) SQLClientHandler.getCurrentHandler().closeConnection();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        SQLClientHandler.setCurrentHandler(null);
        disconnected();
    }//GEN-LAST:event_disconnectitemActionPerformed

    private void connectitemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_connectitemActionPerformed
    {//GEN-HEADEREND:event_connectitemActionPerformed
        if(ApplicationSettings.getInstance().isMacOSX())
        {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            
            macLogin.setLocation(dim.width/2-macLogin.getWidth()/2, dim.height/2-macLogin.getHeight()/2-25);
            macLogin.setVisible(true);
        }
        else
        {
            if(login.getParent() == null) jDesktopPane1.add(login);
            login.setLocation(master.getWidth()/2-login.getWidth()/2, master.getHeight()/2-login.getHeight()/2-25);
            login.setVisible(true);
        }
    }//GEN-LAST:event_connectitemActionPerformed

    private void exititemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exititemActionPerformed
    {//GEN-HEADEREND:event_exititemActionPerformed
        // Add your handling code here:
        exitForm(null);
    }//GEN-LAST:event_exititemActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        WindowSettings settings = new WindowSettings(getBounds(), ((getState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH));

	ApplicationSettings.getInstance().savePersistentObject("MDIFrameSettings", settings);
        
        if(disconnectitem.isEnabled()) disconnectitemActionPerformed(null);
        
        System.exit(0);
    }//GEN-LAST:event_exitForm

    public static void createRootless(JInternalFrame frame)
    {
        master.rootless(frame).setVisible(true);
    }
    
    private JFrame rootless(JInternalFrame internal)
    {
        DataDinoFrame frame = new DataDinoFrame(internal.getTitle());
        WindowHandler handler = new WindowHandler(frame, internal);
        JMenuItem menuitem;
        
        initComponents();
        
        setJMenuBar(null);
        
        connectitem.setEnabled(false);
        disconnectitem.setEnabled(true);
        schemaitem.setEnabled(true);
        sqleditoritem.setEnabled(true);
        datamigratoritem.setEnabled(true);
        schemaexportitem.setEnabled(true);
        
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(true);
        schemaBrowserButton.setEnabled(true);
        sqlEditorButton.setEnabled(true);
        dataMigratorButton.setEnabled(true);
        schemaExporterButton.setEnabled(true);
        
        frame.setBounds(internal.getBounds());
        frame.setContentPane(internal.getContentPane());
        frame.setJMenuBar(menubar);
        
        if(menus.size() > 0)
        {
            JMenu menu = (JMenu)menus.get(0);
            JMenuItem item;
            WindowHandler winhandler;
            
            for(int i=0; i<menu.getItemCount(); i++)
            {
                item = (JMenuItem)menu.getItem(i);
                winhandler = (WindowHandler)item.getActionListeners()[0];
                item = new JMenuItem(item.getText());
                
                item.addActionListener(winhandler);
                windowmenu.add(item);
            }
        }
        
        menubar = null;
        iframes.add(internal);
        frames.add(frame);
        menus.add(windowmenu);
        
        for(int i=0; i<menus.size(); i++)
        {
            menuitem = new JMenuItem(frame.getTitle());
            menuitem.addActionListener(handler);
            ((JMenu)menus.get(i)).add(menuitem);
        }
        
        frame.addWindowListener(handler);
        internal.addInternalFrameListener(handler);
        frame.setTitleListener(handler);
        
        manager.register(frame, internal.getClass().getName());
        
        return frame;
    }
    
    
    private void register(JInternalFrame internal)
    {
        InternalWindowHandler handler = new InternalWindowHandler(internal);
        JMenuItem menuitem = new JMenuItem(internal.getTitle());
        WindowSettings options = (WindowSettings)internalManager.get(internal.getClass().getName());
        
        windowmenu.add(menuitem);
        
        iframes.add(internal);
        
        internal.addInternalFrameListener(handler);
        menuitem.addActionListener(handler);
        
        if(internal instanceof DataDinoInternalFrame) ((DataDinoInternalFrame)internal).setTitleListener(handler);
        
        //TODO: Make this work!
        //manager.register(frame, internal.getClass().getName());
        
        internal.setVisible(true);
        
        if(options != null)
        {
            internal.setBounds(options.bounds);
            try{internal.setMaximum(options.maximized);} catch(Exception e) {e.printStackTrace();}
        }
    }
    
    private class WindowHandler implements WindowListener, WindowFocusListener, InternalFrameListener, ActionListener, ChangeListener
    {
        private JFrame frame;
        private JInternalFrame parent;
        
        public WindowHandler(JFrame frame, JInternalFrame parent)
        {
            this.frame = frame;
            this.parent = parent;
        }
        
        public void windowActivated(WindowEvent windowEvent) 
        {
            //frame.setJMenuBar(menubar);
        }
        
        public void windowClosing(WindowEvent windowEvent) 
        {
            int index = frames.indexOf(frame);
            
            
            frames.remove(frame);
            iframes.remove(parent);
            menus.remove(index);
            
            for(int i=0; i<menus.size(); i++) ((JMenu)menus.get(i)).remove(index);
            
            if(frames.size() == 0) disconnectitemActionPerformed(null);
            
            parent.dispose();
        }
        
        public void windowClosed(WindowEvent windowEvent) 
        {
            
        }
        
        public void windowDeactivated(WindowEvent windowEvent) {
        }
        
        public void windowDeiconified(WindowEvent windowEvent) 
        {
        }
        
        public void windowIconified(WindowEvent windowEvent) {
        }
        
        public void windowOpened(WindowEvent windowEvent) {
        }
        
        public void windowGainedFocus(java.awt.event.WindowEvent windowEvent) 
        {
        }
        
        public void windowLostFocus(java.awt.event.WindowEvent windowEvent) 
        {
        }
        
        public void internalFrameActivated(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void internalFrameClosed(javax.swing.event.InternalFrameEvent internalFrameEvent) 
        {
            int index = iframes.indexOf(parent);
            
            if(index >= 0)
            {
                menus.remove(index);
                
                for(int i=0; i<menus.size(); i++) ((JMenu)menus.get(i)).remove(index);
                if(ApplicationSettings.getInstance().isMacOSX()) frames.remove(frame);
                
                iframes.remove(parent);
                frame.dispose();

                if(frames.size() == 0 && ApplicationSettings.getInstance().isMacOSX()) disconnectitemActionPerformed(null);
            }
        }
        
        public void internalFrameClosing(javax.swing.event.InternalFrameEvent internalFrameEvent) 
        {
        }
        
        public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void internalFrameIconified(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void internalFrameOpened(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void actionPerformed(ActionEvent e)
        {
            frame.toFront();
            frame.requestFocusInWindow();
        }
        
        public void stateChanged(ChangeEvent e)
        {
            int index = frames.indexOf(frame);
            
            for(int i=0; i<menus.size(); i++) ((JMenu)menus.get(i)).getItem(index).setText(frame.getTitle());
        }
        
    }
    
    
    private class InternalWindowHandler implements InternalFrameListener, ActionListener, ChangeListener
    {
        private JInternalFrame frame;
        
        public InternalWindowHandler(JInternalFrame frame)
        {
            this.frame = frame;
        }
        
        public void internalFrameActivated(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void internalFrameClosed(javax.swing.event.InternalFrameEvent internalFrameEvent) 
        {
            int index = iframes.indexOf(frame);
            
            if(index >= 0)
            {
                internalManager.put(frame.getClass().getName(), new WindowSettings(new Rectangle(frame.getNormalBounds()), frame.isMaximum()));
                ApplicationSettings.getInstance().savePersistentObject("InternalWindowSettings", internalManager);
                
                windowmenu.remove(index);
                
                iframes.remove(frame);
                frame.dispose();
            }
        }
        
        public void internalFrameClosing(javax.swing.event.InternalFrameEvent internalFrameEvent) 
        {
        }
        
        public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void internalFrameIconified(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void internalFrameOpened(javax.swing.event.InternalFrameEvent internalFrameEvent) {
        }
        
        public void actionPerformed(ActionEvent e)
        {
            frame.toFront();
            frame.requestFocusInWindow();
        }
        
        public void stateChanged(ChangeEvent e)
        {
            int index = iframes.indexOf(frame);
            
            windowmenu.getItem(index).setText(frame.getTitle());
        }
    }
    
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) 
    {
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "DataDino");
        
        if(ApplicationSettings.getInstance().isMacOSX())
        {
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.showGrowBox", "false");
            //System.setProperty("apple.awt.brushMetalLook", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "DataDino");
        }
        
        //dumpSystemSettings();
        
        initialize();
    }
    
    private static void dumpSystemSettings()
    {
        System.getProperties().list(System.out);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutitem;
    private javax.swing.JButton connectButton;
    private javax.swing.JMenuItem connectitem;
    private javax.swing.JButton dataMigratorButton;
    private javax.swing.JMenuItem datamigratoritem;
    private javax.swing.JButton disconnectButton;
    private javax.swing.JMenuItem disconnectitem;
    private javax.swing.JMenuItem exititem;
    private javax.swing.JMenu filemenu;
    private javax.swing.JMenu helpmenu;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JMenuItem licenseitem;
    private javax.swing.JMenuBar menubar;
    private javax.swing.JButton schemaBrowserButton;
    private javax.swing.JButton schemaExporterButton;
    private javax.swing.JMenuItem schemaexportitem;
    private javax.swing.JMenuItem schemaitem;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton sqlEditorButton;
    private javax.swing.JMenuItem sqleditoritem;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JMenu viewmenu;
    private javax.swing.JMenu windowmenu;
    // End of variables declaration//GEN-END:variables

}
