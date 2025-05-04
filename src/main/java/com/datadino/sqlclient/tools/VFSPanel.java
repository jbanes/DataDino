/*
 * VFSPanel.java
 *
 * Created on December 12, 2002, 10:27 PM
 */

package com.datadino.sqlclient.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.dnsalias.java.sqlclient.dialog.*;
import com.dnsalias.java.sqlclient.ui.*;
import com.dnsalias.java.sqlclient.util.*;

import com.datadino.sqlclient.vfs.*;
import com.datadino.sqlclient.ui.*;

/**
 *
 * @author  jbanes
 */
public class VFSPanel extends JPanel 
{
    private static String[] popupOptions = {
        "Mount File System", "Unmount File System", null, "Refresh", null, "New Script", 
        "New Folder", null, "Delete"};
    
    private Border oldBorder = new javax.swing.plaf.metal.MetalBorders.ButtonBorder();
    private Border emptyBorder = new javax.swing.plaf.metal.MetalBorders.RolloverButtonBorder();
    private VFSTreeModel model = new VFSTreeModel();

    private JMenuItem[] items = new JMenuItem[popupOptions.length];
    private PopupHandler popHandler = new PopupHandler();
    private JPopupMenu menu = new JPopupMenu();
    
    private ArrayList listeners = new ArrayList();
    private PanelListener plistener = new PanelListener();
    
    public VFSPanel()
    {
        initComponents();
        
        if(!ApplicationSettings.getInstance().isMacOSX())
        {
            closeButton.setUI(new javax.swing.plaf.metal.MetalButtonUI());

            closeButton.setBorder(emptyBorder);
            closeButton.addMouseListener(new HoverListener());
        }
        
        tree.setModel(model);
        tree.setCellRenderer(new VFSTreeCellRenderer());
        tree.addMouseListener(plistener);
        
        initFileSystem();
        initPopups();
        
        addComponentListener(plistener);
    }
    
    public void addVFSPanelListener(VFSPanelListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeVFSPanelListener(VFSPanelListener listener)
    {
        listeners.remove(listener);
    }
    
    private void initFileSystem()
    {
        File config = new File(System.getProperty("user.home")+File.separator+".datadino"+File.separator+"sql.vfs");
        File scripts = new File(System.getProperty("user.home")+File.separator+".datadino"+File.separator+"scripts");
        
        if(!scripts.exists()) scripts.mkdir();
        
        if(!config.exists())
        {
            try
            {
                PrintWriter out = new PrintWriter(new FileOutputStream(config));
                
                out.println("/scripts");
                out.println(scripts.toURL().toString());
                
                out.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(config)));
            String line;
            URL url;
            
            while((line = in.readLine()) != null)
            {
                url = new URL(in.readLine());
                
                try
                {
                    model.getFileSystem().mount(line, url);
                }
                catch(Exception e)
                {
                    //Failed to load a file system
                    e.printStackTrace();
                }
            }
            
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void initPopups()
    {
        for(int i=0; i<popupOptions.length; i++)
        {
            if(popupOptions[i] != null)
            {
                items[i] = new JMenuItem(popupOptions[i]);
                items[i].addActionListener(popHandler);
                items[i].setActionCommand(popupOptions[i]);

                menu.add(items[i]);
            }
            else menu.addSeparator();
        }
        
        tree.addMouseListener(popHandler);
    }
    
    public VFSFileSystem getFileSystem()
    {
        return model.getFileSystem();
    }

    public void mount(String path, URL url) throws VFSException
    {
        model.getFileSystem().mount(path, url);
        writeVFS();
    }
    
    public void unmount(String path)
    {
        model.getFileSystem().unmount(path);
        writeVFS();
    }
    
    private void writeVFS()
    {
        try
        {
            File config = new File(System.getProperty("user.home")+File.separator+".datadino"+File.separator+"sql.vfs");
            PrintWriter out = new PrintWriter(new FileOutputStream(config));
            Enumeration paths = model.getFileSystem().getMountPoints();
            String path;

            while(paths.hasMoreElements())
            {
                path = paths.nextElement().toString();
                
                out.println(path);
                out.println(model.getFileSystem().getMountURL(path).toExternalForm());
            }

            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        scrollPane = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        
        setLayout(new java.awt.BorderLayout());
        
        scrollPane.setBorder(null);
        tree.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 2, 1, 1)));
        tree.setEditable(true);
        scrollPane.setViewportView(tree);
        
        add(scrollPane, java.awt.BorderLayout.CENTER);
        
        jPanel1.setLayout(new java.awt.BorderLayout());
        
        jPanel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EtchedBorder(), new javax.swing.border.EmptyBorder(new java.awt.Insets(2, 2, 2, 2))));
        title.setText("  SQL Scripts");
        jPanel1.add(title, java.awt.BorderLayout.CENTER);
        
        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close-icon.png")));
        closeButton.setPreferredSize(new java.awt.Dimension(18, 16));
        closeButton.setMaximumSize(new java.awt.Dimension(20, 20));
        closeButton.setMinimumSize(new java.awt.Dimension(10, 10));
        closeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                closeButtonActionPerformed(evt);
            }
        });
        
        jPanel1.add(closeButton, java.awt.BorderLayout.EAST);
        
        add(jPanel1, java.awt.BorderLayout.NORTH);
        
    }//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeButtonActionPerformed
    {//GEN-HEADEREND:event_closeButtonActionPerformed
        setVisible(false);
        ((JComponent)getParent()).revalidate();
    }//GEN-LAST:event_closeButtonActionPerformed

    private Frame getFrame()
    {
        Component comp = this;
        
        while(comp.getParent() != null && !(comp instanceof Frame)) comp = comp.getParent();
        
        return (Frame)comp;
    }

    private void refresh(VFSFile file)
    {
        refresh(file, tree.getSelectionPath(), tree.isExpanded(tree.getSelectionPath()));
    }

    private void refresh(VFSFile file, TreePath path, boolean expanded)
    {
        file.refresh();

        model.fireTreeModelChanged(file);
        tree.setSelectionPath(path);
        if(expanded) tree.expandPath(path);
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
    
    private class PopupHandler implements ActionListener, MouseListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            if(evt.getSource() == items[0]) mountFileSystem();
            else if(evt.getSource() == items[1]) unmountFileSystem();
            else if(evt.getSource() == items[3]) refresh((VFSFile)tree.getSelectionPath().getLastPathComponent());
            else if(evt.getSource() == items[5]) newScript();
            else if(evt.getSource() == items[6]) newFolder();
            else if(evt.getSource() == items[8]) delete();
        }
        
        private void newFolder()
        {
            TreePath path = tree.getSelectionPath();
            VFSFile file = (VFSFile)path.getLastPathComponent();
            boolean expanded = tree.isExpanded(path);
            String name = findName(file, "New Folder", null);
            
            if(!file.createDirectory(name))
            {
                JOptionPane.showMessageDialog(VFSPanel.this, "Error! Could not create a new folder!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else 
            {
                refresh(file, path, expanded);
                tree.startEditingAtPath(path.pathByAddingChild(file.getChild(name)));
            }
        }
        
        protected void newScript()
        {
            TreePath path = tree.getSelectionPath();
            VFSFile file = (VFSFile)path.getLastPathComponent();
            boolean expanded = tree.isExpanded(path);
            String name = findName(file, "New Script", "sql");
            
            if(!file.createFile(name))
            {
                JOptionPane.showMessageDialog(VFSPanel.this, "Error! Could not create a new script!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else 
            {
                refresh(file, path, expanded);
                tree.startEditingAtPath(path.pathByAddingChild(file.getChild(name)));
            }
        }
        
        protected String findName(VFSFile file, String name, String extention)
        {
            String result = (extention == null) ? name : name+"."+extention;
            int index = 1;
            
            while(file.getChild(result) != null)
            {
                result = (extention == null) ? name+"["+index+"]" : name+"["+index+"]."+extention;
                index++;
            }
            
            return result;
        }
        
        protected void delete()
        {
            TreePath path = tree.getSelectionPath();
            VFSFile file = (VFSFile)path.getLastPathComponent();
            VFSFile parent = file.getParent();
            
            if(!file.delete())
            {
                JOptionPane.showMessageDialog(VFSPanel.this, "Could not delete "+file.getName()+"!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                model.fireTreeNodeRemoved(file, path);
                if(parent != null) refresh(parent);
            }
        }
        
        private void mountFileSystem()
        {
            File dir;
            Frame frame = getFrame();
            FileSystemTree dialog = new FileSystemTree(frame, true, true);
            String mountpoint;
            
            dialog.setLocation(frame.getX()+15, frame.getY()+15);
            dialog.show();
            
            if(dialog.isCanceled()) return;
            
            dir = dialog.getSelectedFile();
            
            mountpoint = "/"+dir.getName();
            
            if(model.getFileSystem().getVFSFile("/").getChild(dir.getName()) != null)
            {
                int i = 1;
                
                while(model.getFileSystem().getVFSFile("/").getChild(dir.getName()+" ("+i+")") != null) i++;
                
                mountpoint = "/"+dir.getName()+" ("+i+")";
            }
            
            try
            {
                mount(mountpoint, dir.toURL());
            }
            catch(Exception e)
            {
                e.printStackTrace();
                
                ErrorReport.displayError("Could not mount filesystem!", e, frame);
            }
        }
        
        private void unmountFileSystem()
        {
            unmount("/"+((VFSFile)tree.getSelectionPath().getLastPathComponent()).getName());
        }
        
        public void mouseExited(MouseEvent mouseEvent)
        {
        }
        
        public void mouseReleased(MouseEvent mouseEvent)
        {
            if(mouseEvent.isPopupTrigger()) popup(mouseEvent.getX(), mouseEvent.getY());
        }
        
        public void mousePressed(MouseEvent mouseEvent)
        {
            if(mouseEvent.isPopupTrigger()) popup(mouseEvent.getX(), mouseEvent.getY());
        }
        
        private void popup(int x, int y)
        {
            int row = tree.getRowForLocation(x, y);
            TreePath path = tree.getPathForRow(row);
            
            tree.setSelectionRow(row);
            
            if(path == null || path.getPathCount() > 2)
            {
                items[0].setEnabled(false);
                items[1].setEnabled(false);
                items[5].setEnabled(true);
                items[6].setEnabled(true);
                items[8].setEnabled(true);
            }
            else if(path.getPathCount() == 1)
            {
                items[0].setEnabled(true);
                items[1].setEnabled(false);
                items[5].setEnabled(false);
                items[6].setEnabled(false);
                items[8].setEnabled(false);
            }
            else if(path.getPathCount() == 2)
            {
                items[0].setEnabled(false);
                items[1].setEnabled(true);
                items[5].setEnabled(true);
                items[6].setEnabled(true);
                items[8].setEnabled(false);
            }
            
            menu.show(tree, x, y);
        }
        
        public void mouseClicked(MouseEvent mouseEvent)
        {
        }
        
        public void mouseEntered(MouseEvent mouseEvent)
        {
        }
    }
    
    private class VFSTreeCellRenderer extends JLabel implements TreeCellRenderer
    {
        private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
        
        public Component getTreeCellRendererComponent(JTree tree, Object obj, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {   
            VFSFile file = (VFSFile)obj;
            Icon icon = file.getIcon();
            TreeCellRenderer render = defaultRenderer;
            
            if(file.getIcon() != null)
            {
                renderer.setClosedIcon(icon);
                renderer.setOpenIcon(icon);
                renderer.setLeafIcon(icon);
                
                render = renderer;
            }
            
            return render.getTreeCellRendererComponent(tree, obj, selected, expanded, leaf, row, hasFocus);
        }
    }
    
    private class PanelListener implements ComponentListener, MouseListener
    {
        private VFSFile selected = null;
        private int clicks = 0;
        private long lastClick = 0;
        
        public void componentShown(ComponentEvent evt)
        {
            for(int i=0; i<listeners.size(); i++)
            {
                ((VFSPanelListener)listeners.get(i)).VFSPanelShown();
            }
        }
        
        public void componentMoved(ComponentEvent componentEvent)
        {
        }
        
        public void componentResized(ComponentEvent componentEvent)
        {
        }
        
        public void componentHidden(ComponentEvent componentEvent)
        {
            for(int i=0; i<listeners.size(); i++)
            {
                ((VFSPanelListener)listeners.get(i)).VFSPanelHidden();
            }
        }
        
        public void mouseExited(MouseEvent mouseEvent)
        {
        }
        
        public void mouseReleased(MouseEvent mouseEvent)
        {
        }
        
        public void mouseClicked(MouseEvent mouseEvent)
        {
        }
        
        public void mousePressed(MouseEvent mouseEvent)
        {
            if(lastClick+500 < System.currentTimeMillis()) clicks = 0;
            if(mouseEvent.getModifiers() != mouseEvent.BUTTON1_MASK) return;
            
            lastClick = System.currentTimeMillis();
            
            if(tree.getLastSelectedPathComponent() == selected)
            {
                clicks++;
            }
            else if(tree.getLastSelectedPathComponent() != null)
            {
                selected = (VFSFile)tree.getLastSelectedPathComponent();
                clicks = 1;
            }
            else 
            {
                selected = null;
                clicks = 0;
            }
            
            if(selected != null && clicks > 1 && !selected.isDirectory())
            {  
                clicks = 0;
                
                for(int i=0; i<listeners.size(); i++)
                {
                    ((VFSPanelListener)listeners.get(i)).VFSFileDoubleClicked(selected);
                }
            }
        }
        
        public void mouseEntered(MouseEvent mouseEvent)
        {
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTree tree;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel title;
    private javax.swing.JButton closeButton;
    // End of variables declaration//GEN-END:variables
}
