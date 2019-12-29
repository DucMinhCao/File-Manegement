package GUI;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipThread extends Thread {
    private JProgressBar jProgressBar;
    private double progress;
    private List<File> srcFile;
    private File desFile;
    private boolean isThreadFinish;

    public ZipThread(JProgressBar jProgressBar, List<File> srcFile, File desFile, boolean isThreadFinish) throws IOException {
        this.jProgressBar = jProgressBar;
        this.srcFile = srcFile;
        this.desFile = desFile;
        this.isThreadFinish = isThreadFinish;
    }

    public void run() {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        FileInputStream fis = null;
        try {
            fos = new FileOutputStream(desFile);
            zos = new ZipOutputStream(fos);
            long expectedBytes = 0;
            for (File file : srcFile) {
                expectedBytes += file.length();
            }
            long totalBytesZipped = 0;
            byte[] buffer = new byte[1024];
            for (File file : srcFile) {
                fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int len = 0;
                while ((len = fis.read(buffer)) > 0 && !Thread.currentThread().isInterrupted()) {
                    zos.write(buffer, 0, len);
                    totalBytesZipped += len;
                    progress = (totalBytesZipped / (double) expectedBytes) * 100;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            jProgressBar.setValue((int) progress);
                        }
                    });
                }
                zos.closeEntry();
                fis.close();
                Thread.sleep(1000);
            }
            zos.close();
            isThreadFinish = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            try {
                zos.closeEntry();
                fis.close();
                zos.close();
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
