package by.matrosov.filemanager;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;

public class FileManager extends JPanel{

    private FileManager() {
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
