package by.matrosov.filemanager;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.Vector;

public class FileManager extends JPanel{

    private JTree tree;
    private static FileSystemView fsv = FileSystemView.getFileSystemView();
    private static boolean useSystemLookAndFeel = true;

    private DefaultTreeModel model;

    private FileManager() {
        setLayout(new BorderLayout());
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();

        File[] roots = File.listRoots();
        for (File f : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FileNode(f));
            treeNode.add(node);
            node.add(new DefaultMutableTreeNode(Boolean.TRUE));
        }

        model = new DefaultTreeModel(treeNode);
        tree = new JTree();
        tree.setModel(model);

        tree.addTreeExpansionListener(new DirExpansionListener());

        //tree.setEditable(true);
        //model.addTreeModelListener(new MyTreeModelListener());

        //Create the scroll and add the tree to it.
        JScrollPane scroll = new JScrollPane(tree);
        add(scroll);
    }

    private class FileNode
    {
        File m_file;

        FileNode(File file)
        {
            m_file = file;
        }

        public File getFile()
        {
            return m_file;
        }

        public String toString()
        {
            return m_file.getName().length() > 0 ? m_file.getName() :
                    m_file.getPath();
        }

        boolean hasSubDirs()
        {
            File[] files = listFiles();
            if (files == null)
                return false;
            for (File file : files) {
                if (file.isDirectory())
                    return true;
            }
            return false;
        }

        int compareTo(FileNode toCompare)
        {
            return  m_file.getName().compareToIgnoreCase(
                    toCompare.m_file.getName() );
        }

        File[] listFiles()
        {
            if (!m_file.isDirectory())
                return null;
            try
            {
                return m_file.listFiles();
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(null,
                        "Error reading directory "+m_file.getAbsolutePath(),
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }

        public boolean expand(DefaultMutableTreeNode parent)
        {
            DefaultMutableTreeNode flag = (DefaultMutableTreeNode)parent.getFirstChild();
            if (flag==null)    // No flag
                return false;
            Object obj = flag.getUserObject();
            if (!(obj instanceof Boolean))
                return false;      // Already expanded

            parent.removeAllChildren();  // Remove Flag

            File[] files = listFiles();
            if (files == null)
                return true;

            Vector v = new Vector();

            for (int k=0; k<files.length; k++)
            {
                File f = files[k];
                if (!(f.isDirectory()))
                    continue;

                FileNode newNode = new FileNode(f);

                boolean isAdded = false;
                for (int i=0; i<v.size(); i++)
                {
                    FileNode nd = (FileNode)v.elementAt(i);
                    if (newNode.compareTo(nd) < 0)
                    {
                        v.insertElementAt(newNode, i);
                        isAdded = true;
                        break;
                    }
                }
                if (!isAdded)
                    v.addElement(newNode);
            }

            for (int i=0; i<v.size(); i++)
            {
                FileNode nd = (FileNode)v.elementAt(i);
                //IconData idata = new IconData(nd);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(nd);
                parent.add(node);

                if (nd.hasSubDirs())
                    node.add(new DefaultMutableTreeNode(Boolean.TRUE));
            }

            return true;
        }
    }

    private class DirExpansionListener implements TreeExpansionListener{

        private DefaultMutableTreeNode getTreeNode(TreePath path)
        {
            return (DefaultMutableTreeNode)(path.getLastPathComponent());
        }

        private FileNode getFileNode(DefaultMutableTreeNode node)
        {
            if (node == null)
                return null;
            Object obj = node.getUserObject();

            if (obj instanceof FileNode)
                return (FileNode)obj;
            else
                return null;
        }

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            final DefaultMutableTreeNode node = getTreeNode(
                    event.getPath());
            final FileNode fnode = getFileNode(node);

            Thread runner = new Thread(() -> {
                if (fnode != null && fnode.expand(node))
                {
                    Runnable runnable = () -> model.reload(node);
                    SwingUtilities.invokeLater(runnable);
                }
            });
            runner.start();
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {

        }
    }

    /*

    private class MyTreeModelListener implements TreeModelListener{

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode)(node.getChildAt(index));

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {

        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {

        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {

        }
    }

    */

    private static void createAndShowGUI(){
        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }
        }

        //Create and set up the window.
        JFrame frame = new JFrame("FileManager");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new FileManager());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileManager::createAndShowGUI);
    }
}
