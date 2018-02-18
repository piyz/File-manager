package by.matrosov.filemanager;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class FileManager extends JPanel{

    private JTree tree;

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

    private FileManager() {
        File[] roots = File.listRoots();
        FileTreeModel model = new FileTreeModel(roots);
        tree = new JTree(model);
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
