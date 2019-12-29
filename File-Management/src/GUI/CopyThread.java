package GUI;

import javax.swing.*;
import java.io.*;

public class CopyThread extends Thread {
    private JProgressBar jProgressBar;
    private double progress;
    private File srcFile;
    private File desFile;

    public CopyThread(JProgressBar jProgressBar, File srcFile, File desFile) throws IOException {
        this.jProgressBar = jProgressBar;
        this.srcFile = srcFile;
        this.desFile = desFile;
    }

    public void run() {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(desFile);
            long expectedBytes = srcFile.length();
            long totalBytesCopied = 0;
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) > 0 && !Thread.currentThread().isInterrupted()) {
                System.out.println();
                out.write(buffer, 0, len);
                totalBytesCopied += len;
                progress = (totalBytesCopied / (double) expectedBytes) * 100;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        jProgressBar.setValue((int) progress);
                    }
                });
            }
            Thread.sleep(1000);
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (Thread.currentThread().isInterrupted() == true) {
                if (desFile.delete()) {
                    System.out.println("Delete " + desFile.getName());
                } else {
                    System.out.println("Not Delete " + desFile.getName());
                }
            }
        }
    }
}
