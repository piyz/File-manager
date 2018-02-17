package by.matrosov.filemanager;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;

public class FileManager extends JPanel{

    private JTree tree;
    private static FileSystemView fsv = FileSystemView.getFileSystemView();

    private FileManager() {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(treeNode);

        File[] roots = fsv.getRoots();
        for (File f : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(f);
            treeNode.add(node);

            File[] files = fsv.getFiles(f, true);
            for (File file : files) {
                if (file.isDirectory())
                    node.add(new DefaultMutableTreeNode(file));
            }
        }

        tree = new JTree();
        tree.setModel(model);

        //Create the scroll and add the tree to it.
        JScrollPane scroll = new JScrollPane(tree);
        add(scroll);
    }

    private static void createAndShowGUI(){
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
