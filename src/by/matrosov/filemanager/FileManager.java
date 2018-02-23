package by.matrosov.filemanager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class FileManager extends JPanel{

    private static FileSystemView fsv = FileSystemView.getFileSystemView();
    private DefaultTreeModel treeModel;
    private JTree tree;

    private FileManager() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);

        TreeSelectionListener treeSelectionListener = tse -> {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
            showChildren(node);
        };

        File[] roots = fsv.getRoots();
        for (File fileSystemRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
            root.add( node );

            File[] files = fsv.getFiles(fileSystemRoot, true);
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(new DefaultMutableTreeNode(file));
                }
            }
        }

        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(treeSelectionListener);

        JScrollPane treeScroll = new JScrollPane(tree);
        add(treeScroll);
    }

    private void showChildren(final DefaultMutableTreeNode node){
        tree.setEnabled(false);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            protected Void doInBackground() throws Exception {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fsv.getFiles(file, true); //!!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    private static void createAndShowGUI(){
        JFrame frame = new JFrame("FileManager");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setContentPane(new FileManager());

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileManager::createAndShowGUI);
    }
}
