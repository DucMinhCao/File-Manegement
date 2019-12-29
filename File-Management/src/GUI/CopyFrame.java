package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class CopyFrame {
    public JFrame jFrame;
    public JPanel jPanel;
    public JProgressBar jProgressBar;
    public JButton cancelButton;
    public String threadId;

    public CopyFrame() {
        InitComponent();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public void InitComponent() {
        jFrame = new JFrame("Copy");
        jFrame.setBounds(100, 100, 400, 95);

        jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jProgressBar = new JProgressBar();
        jProgressBar.setStringPainted(true);

        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(50, 50));
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                for(Thread thread : threadSet){
                    if(thread.getName().equals(threadId)){
                        if (!thread.isInterrupted()) {
                            thread.interrupt();
                        }
                    }
                }
                jFrame.setVisible(false);
                jFrame.dispose();
            }
        });

        jPanel.add(jProgressBar);
        jPanel.add(Box.createRigidArea(new Dimension(50, 5)));
        jPanel.add(cancelButton);
        jFrame.getContentPane().add(jPanel);
    }
}
