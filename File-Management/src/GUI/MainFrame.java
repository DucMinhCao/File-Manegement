package GUI;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainFrame {

    private JFrame jFrame;
    private JMenuBar jMenuBar;
    private JMenu fileMenu;
    private JMenu folderMenu;
    private JMenu chooseDirectoryMenu;
    private JMenuItem chooseDirectoryItem;
    private JMenuItem createFileItem;
    private JMenuItem deleteFileItem;
    private JMenuItem renameFileItem;
    private JMenuItem copyFileItem;
    private JMenuItem zipFileItem;
    private JMenuItem unzipFileItem;
    private JMenuItem viewFileContentItem;
    private JMenuItem createFolderItem;
    private JMenuItem deleteFolderItem;
    private JMenuItem renameFolderItem;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel centerPanel;
    private JTree fileManagerTree;
    private JScrollPane fileManagerScrollPane;
    private JScrollPane fileContentScrollPane;
    private JTextArea fileContentTextArea;


    public MainFrame() {
        InitComponent();
    }

    public void InitComponent() {
        jFrame = new JFrame("File Management System");
        jFrame.setBounds(100, 100, 1000, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jMenuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        folderMenu = new JMenu("Folder");
        chooseDirectoryMenu = new JMenu("Choose Directory");

        // File
        createFileItem = new JMenuItem("Create File");
        createFileItem.setPreferredSize(new Dimension(125,22));
        createFileItem.setIcon(new ImageIcon(getClass().getResource("/Images/add.png")));
        createFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                String fileName = JOptionPane.showInputDialog(null, "Input File Name");
                if (fileName != null) {
                    File newFile = new File(currentRoot + "\\" + fileName);
                    if (newFile.exists()) {
                        String newFileName = createFileWithPostFix(currentRoot + "\\" + fileName);
                        int choice = JOptionPane.showConfirmDialog(null, "The file is already created, would you like to create with postfix like " + newFileName);
                        if (choice == 0) {
                            File file = new File(newFileName);
                            try {
                                if (file.createNewFile()) {
                                    JOptionPane.showMessageDialog(null, "File Created");
                                }
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "File Created Failed");
                            }
                        } else if (choice == 1) {
                            String name = JOptionPane.showInputDialog(null, "Re Input File Name");
                            File file = new File(currentRoot + "\\" + name);
                            try {
                                file.createNewFile();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            if (newFile.createNewFile()) {
                                JOptionPane.showMessageDialog(null, "File Created");
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "File Created Failed");
                        }
                    }

                }
                reloadTreeModel(currentRoot);
            }
        });

        deleteFileItem = new JMenuItem("Delete File");
        deleteFileItem.setPreferredSize(new Dimension(125,22));
        deleteFileItem.setIcon(new ImageIcon(getClass().getResource("/Images/delete.png")));
        deleteFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> filePaths = new ArrayList<>();
                boolean deleteFlag = true;
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                TreePath[] paths = fileManagerTree.getSelectionPaths();
                for (TreePath path : paths) {
                    String filePath = path.getLastPathComponent().toString();
                    filePaths.add(filePath);
                }
                for (String filePath : filePaths) {
                    File target = new File(filePath);
                    if (!target.delete()) {
                        deleteFlag = false;
                        JOptionPane.showMessageDialog(null, target.getAbsolutePath() + "cant be deleted!");
                        return;
                    }
                }
                if (deleteFlag) {
                    JOptionPane.showMessageDialog(null, "Files Delected!");
                }
                reloadTreeModel(currentRoot);
            }
        });

        renameFileItem = new JMenuItem("Rename File");
        renameFileItem.setPreferredSize(new Dimension(125,22));
        renameFileItem.setIcon(new ImageIcon(getClass().getResource("/Images/document.png")));
        renameFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "";
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                TreePath[] paths = fileManagerTree.getSelectionPaths();
                for (TreePath path : paths) {
                    filePath = path.getLastPathComponent().toString();
                }
                System.out.println(filePath);
                File oldName = new File(filePath);
                String newName =  JOptionPane.showInputDialog("Input File's New Name: ");
                File newFileName = new File(currentRoot + "\\" + newName);
                boolean result = oldName.renameTo(newFileName);
                if (result) {
                    JOptionPane.showMessageDialog(null, "The file has been renamed");
                } else {
                    JOptionPane.showMessageDialog(null, "There is a file with that name! Please choose another name!");
                }
                reloadTreeModel(currentRoot);
            }
        });

        copyFileItem = new JMenuItem("Copy Files");
        copyFileItem.setPreferredSize(new Dimension(125,22));
        copyFileItem.setIcon(new ImageIcon(getClass().getResource("/Images/archives.png")));
        copyFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "";
                String desPath = "";
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                TreePath[] paths = fileManagerTree.getSelectionPaths();
                for (TreePath path : paths) {
                    filePath = path.getLastPathComponent().toString();
                }
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(jFrame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    desPath = fileChooser.getSelectedFile().getAbsolutePath();
                }
                String fileNameWithExt = filePath.substring(filePath.lastIndexOf("\\") + 1);
                String fileName = fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf("."));
                String extName = fileNameWithExt.substring(fileNameWithExt.lastIndexOf("."));
                File srcFile = new File(filePath);
                // Check trung, B1 tao luon file, B2 check trung ten, B3 neu trung ten thi doi ten theo postfix
                File desFile = new File(desPath + "\\" + fileName + "_copy" + extName);
                if (desFile.exists()) {
                    String desFileName = createFileWithPostFix(desPath + "\\" + fileName + "_copy" + extName);
                    desFile = new File(desFileName);
                    try {
                        desFile.createNewFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                // Copy file here
                CopyFrame copy = new CopyFrame();
                copy.jFrame.setVisible(true);
                try {
                    CopyThread copyThread = new CopyThread(copy.jProgressBar, srcFile, desFile);
                    String threadId = copyThread.getName();
                    copy.threadId = threadId;
                    copyThread.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        zipFileItem = new JMenuItem("Zip Files");
        zipFileItem.setPreferredSize(new Dimension(125,22));
        zipFileItem.setIcon(new ImageIcon(getClass().getResource("/Images/zip.png")));
        zipFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<File> fileNeedToBeZipped = new ArrayList<>();
                String desPath = "";
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                TreePath[] paths = fileManagerTree.getSelectionPaths();
                for (TreePath path : paths) {
                    String filePath = path.getLastPathComponent().toString();
                    fileNeedToBeZipped.add(new File(filePath));
                }
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(jFrame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    desPath = fileChooser.getSelectedFile().getAbsolutePath();
                }
                String zipFileName = JOptionPane.showInputDialog(null, "Please Input Zip File Name");
                File zipFile = new File(desPath + "\\" + zipFileName);
                if (zipFile.exists()) {
                    String newZipFileName = createFileWithPostFix(desPath + "\\" + zipFileName);
                    zipFile = new File(newZipFileName);
                    try {
                        zipFile.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                // Zip File Here
                boolean isThreadFinish = false;
                ZipFrame zip = new ZipFrame();
                zip.jFrame.setVisible(true);
                ZipThread zipThread = null;
                try {
                    zipThread = new ZipThread(zip.jProgressBar, fileNeedToBeZipped, zipFile, isThreadFinish);
                    String threadId = zipThread.getName();
                    zip.threadId = threadId;
                    zipThread.start();
                    fileManagerTree.setModel(new FilesContentProvider(currentRoot));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        unzipFileItem = new JMenuItem("Unzip Files");
        unzipFileItem.setPreferredSize(new Dimension(125,22));
        unzipFileItem.setIcon(new ImageIcon(getClass().getResource("/Images/unzip.png")));
        unzipFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "";
                String desPath = "";
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                TreePath[] paths = fileManagerTree.getSelectionPaths();
                for (TreePath path : paths) {
                    filePath = path.getLastPathComponent().toString();
                }
                //Unzip Here
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(jFrame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    desPath = fileChooser.getSelectedFile().getAbsolutePath();
                }
                String fileNameWithExt = filePath.substring(filePath.lastIndexOf("\\") + 1);
                String fileName = fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf("."));
                File src = new File(filePath);
                File dir = new File(desPath + "\\" + fileName);
                if (dir.exists()) {
                    String newDirName = createFolderWithPostFix(desPath + "\\" + fileName);
                    System.out.println(newDirName);
                    dir = new File(newDirName);
                    dir.mkdir();
                }
                UnzipFrame Unzip = new UnzipFrame();
                Unzip.jFrame.setVisible(true);
                try {
                    UnzipThread unzipThread = new UnzipThread(Unzip.jProgressBar, src, dir);
                    String threadId = unzipThread.getName();
                    Unzip.threadId = threadId;
                    unzipThread.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                reloadTreeModel(currentRoot);
            }
        });

        viewFileContentItem = new JMenuItem("View File Content");
        viewFileContentItem.setPreferredSize(new Dimension(130,22));
        viewFileContentItem.setIcon(new ImageIcon(getClass().getResource("/Images/view.png")));
        viewFileContentItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "";
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                TreePath[] paths = fileManagerTree.getSelectionPaths();
                for (TreePath path : paths) {
                    filePath = path.getLastPathComponent().toString();
                }
                File target = new File(filePath);
                if (target.isFile()) {
                    try {
                        fileContentTextArea.read(new BufferedReader(new FileReader(filePath)), null);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        //Folder
        createFolderItem = new JMenuItem("Create Folder");
        createFolderItem.setPreferredSize(new Dimension(125,22));
        createFolderItem.setIcon(new ImageIcon(getClass().getResource("/Images/add.png")));
        createFolderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                String fileName = JOptionPane.showInputDialog(null, "Input Folder Name");
                if (fileName != null) {
                    File newFile = new File(currentRoot + "\\" + fileName);
                    if (newFile.exists()) {
                        String newFolderName = createFolderWithPostFix(currentRoot + "\\" + fileName);
                        int choice = JOptionPane.showConfirmDialog(null, "The folder is already created, would you like to create with postfix like " + newFolderName);
                        if (choice == 0) {
                            File file = new File(newFolderName);
                            try {
                                if (file.mkdir()) {
                                    JOptionPane.showMessageDialog(null, "Folder Created");
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Folder Created Failed");
                            }
                        } else if (choice == 1) {
                            String name = JOptionPane.showInputDialog(null, "Re Input Folder Name");
                            File file = new File(currentRoot + "\\" + name);
                            try {
                                file.mkdir();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            if (newFile.mkdir()) {
                                JOptionPane.showMessageDialog(null, "Folder Created");
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Folder Created Failed");
                        }
                    }

                }
                reloadTreeModel(currentRoot);
            }
        });

        deleteFolderItem = new JMenuItem("Delete Folder");
        deleteFolderItem.setPreferredSize(new Dimension(125,22));
        deleteFolderItem.setIcon(new ImageIcon(getClass().getResource("/Images/delete.png")));
        deleteFolderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "";
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                TreePath[] paths = fileManagerTree.getSelectionPaths();
                for (TreePath path : paths) {
                    filePath = path.getLastPathComponent().toString();
                }
                File dir = new File(filePath);
                if (dir.isDirectory() == false) {
                    JOptionPane.showMessageDialog(null, "Not A Directory");
                }
                File[] listFiles = dir.listFiles();
                for (File file : listFiles) {
                    file.delete();
                }
                if (dir.delete()) {
                    JOptionPane.showMessageDialog(null, "The directory is delected");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid");
                }
                reloadTreeModel(currentRoot);
            }
        });

        renameFolderItem = new JMenuItem("Rename Folder");
        renameFolderItem.setPreferredSize(new Dimension(125,22));
        renameFolderItem.setIcon(new ImageIcon(getClass().getResource("/Images/document.png")));
        renameFolderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "";
                String currentRoot = fileManagerTree.getModel().getRoot().toString();
                TreePath[] paths = fileManagerTree.getSelectionPaths();
                for (TreePath path : paths) {
                    filePath = path.getLastPathComponent().toString();
                }
                File dir = new File(filePath);
                if (!dir.isDirectory()) {
                    JOptionPane.showMessageDialog(null, "Not A Directory");
                }
                String newName = JOptionPane.showInputDialog(null, "Input New Directory Name");

                if (newName != null) {
                    File newDirName = new File(currentRoot + "\\" + newName);
                    boolean result = dir.renameTo(newDirName);
                    if (result) {
                        JOptionPane.showMessageDialog(null, "The folder has been renamed");
                    } else {
                        JOptionPane.showMessageDialog(null, "There is a folder with that name! Please choose another name!");
                    }
                    reloadTreeModel(currentRoot);
                }
            }
        });

        //Directory
        chooseDirectoryItem = new JMenuItem("Choose Directory");
        chooseDirectoryItem.setPreferredSize(new Dimension(140,26));
        chooseDirectoryItem.setIcon(new ImageIcon(getClass().getResource("/Images/choose.png")));
        chooseDirectoryItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(jFrame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    fileManagerTree.setModel(new FilesContentProvider(file.getAbsolutePath()));
                } else {
                    System.out.println("Not choose");
                }
            }
        });

        fileMenu.add(createFileItem);
        fileMenu.add(deleteFileItem);
        fileMenu.add(renameFileItem);
        fileMenu.add(copyFileItem);
        fileMenu.add(zipFileItem);
        fileMenu.add(unzipFileItem);
        fileMenu.add(viewFileContentItem);

        folderMenu.add(createFolderItem);
        folderMenu.add(deleteFolderItem);
        folderMenu.add(renameFolderItem);

        chooseDirectoryMenu.add(chooseDirectoryItem);

        jMenuBar.add(fileMenu);
        jMenuBar.add(folderMenu);
        jMenuBar.add(chooseDirectoryMenu);

        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(800, 400));
        leftPanel.setLayout(new BorderLayout(0,5));

        fileManagerTree = new JTree();
        fileManagerScrollPane = new JScrollPane(fileManagerTree);
        fileManagerTree.setModel(new FilesContentProvider(""));
        fileManagerTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        fileManagerScrollPane.setViewportView(fileManagerTree);
        leftPanel.add(fileManagerScrollPane, BorderLayout.CENTER);

        rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(1, 1, 25, 0));

        fileContentTextArea = new JTextArea();
        fileContentTextArea.setPreferredSize(new Dimension(800, 500));
        Font font = new Font("Fira Code", Font.PLAIN, 15);
        fileContentTextArea.setFont(font);
        fileContentTextArea.setEditable(false);
        fileContentScrollPane = new JScrollPane(fileContentTextArea);
        rightPanel.add(fileContentScrollPane);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.LINE_AXIS));
        centerPanel.add(leftPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(15, 5)));
        centerPanel.add(rightPanel);

        jFrame.setJMenuBar(jMenuBar);
        jFrame.getContentPane().add(centerPanel);
    }

    public void reloadTreeModel(String path) {
        fileManagerTree.setModel(new FilesContentProvider(""));
        fileManagerTree.setModel(new FilesContentProvider(path));
    }

    public static String createFileWithPostFix(String fileName) {
        File target = new File(fileName);
        String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf("."));
        String extName = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = "";
        int fileNO = 0;
        if (target.exists() && !target.isDirectory()) {
            while (target.exists()) {
                fileNO++;
                newFileName = fileNameWithoutExt + " (" + fileNO + ")" + extName;
                target = new File(newFileName);
            }
        }
        return newFileName;
    }

    public static String createFolderWithPostFix(String folderName) {
        File target = new File(folderName);
        String newFileName = "";
        int folderNO = 0;
        if (target.exists() && target.isDirectory()) {
            while (target.exists()) {
                folderNO++;
                newFileName = folderName + " (" + folderNO + ")";
                target = new File(newFileName);
            }
        }
        return newFileName;
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame window = new MainFrame();
                window.jFrame.setVisible(true);
            }
        });
    }
}
