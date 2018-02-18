package by.matrosov.filemanager;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class FileManager extends JPanel{

    private JTree tree;
    private static FileSystemView fsv = FileSystemView.getFileSystemView();

    private static class FileTreeModel implements TreeNode{

        private File file;
        private File[] children;
        private TreeNode parent;
        private boolean isFileSystemRoot;

        public FileTreeModel(File file, TreeNode parent, boolean isFileSystemRoot) {
            this.file = file;
            this.parent = parent;
            this.isFileSystemRoot = isFileSystemRoot;
            this.children = this.file.listFiles();
            if (this.children == null)
                this.children = new File[0];
        }

        FileTreeModel(File[] children) {
            this.file = null;
            this.parent = null;
            this.children = children;
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return new FileTreeModel(this.children[childIndex], this, this.parent == null);
        }

        @Override
        public int getChildCount() {
            return this.children.length;
        }

        @Override
        public TreeNode getParent() {
            return this.parent;
        }

        @Override
        public int getIndex(TreeNode node) {
            FileTreeModel ftn = (FileTreeModel) node;
            for (int i = 0; i < this.children.length; i++) {
                if (ftn.file.equals(this.children[i]))
                    return i;
            }
            return -1;
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }

        @Override
        public boolean isLeaf() {
            return (this.getChildCount() == 0);
        }

        @Override
        public Enumeration children() {
            final int elementCount = this.children.length;
            return new Enumeration<File>() {
                int count = 0;
                public boolean hasMoreElements() {
                    return this.count < elementCount;
                }
                public File nextElement() {
                    if (this.count < elementCount) {
                        return FileTreeModel.this.children[this.count++];
                    }
                    throw new NoSuchElementException("Vector Enumeration");
                }
            };
        }
    }

    private static class FileTreeCellRenderer extends DefaultTreeCellRenderer{
        private Map<String, Icon> iconCache = new HashMap<>();
        private Map<File, String> rootNameCache = new HashMap<>();

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            FileTreeModel fileTreeModel = (FileTreeModel) value;
            File file = fileTreeModel.file;
            String filename = "";
            if (file != null) {
                if (fileTreeModel.isFileSystemRoot) {
                    // long start = System.currentTimeMillis();
                    filename = this.rootNameCache.get(file);
                    if (filename == null) {
                        filename = fsv.getSystemDisplayName(file);
                        this.rootNameCache.put(file, filename);
                    }
                    // long end = System.currentTimeMillis();
                    // System.out.println(filename + ":" + (end - start));
                } else {
                    filename = file.getName();
                }
            }
            JLabel result = (JLabel) super.getTreeCellRendererComponent(tree,
                    filename, sel, expanded, leaf, row, hasFocus);
            if (file != null) {
                Icon icon = this.iconCache.get(filename);
                if (icon == null) {
                    // System.out.println("Getting icon of " + filename);
                    icon = fsv.getSystemIcon(file);
                    this.iconCache.put(filename, icon);
                }
                result.setIcon(icon);
            }
            return result;
        }
    }

    private FileManager() {
        File[] roots = File.listRoots();
        FileTreeModel model = new FileTreeModel(roots);
        tree = new JTree(model);
        tree.setCellRenderer(new FileTreeCellRenderer());
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    private static void createAndShowGUI(){

        //Create and set up the window.
        JFrame frame = new JFrame("FileManager");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new FileManager());

        //Display the window.
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileManager::createAndShowGUI);
    }
}
