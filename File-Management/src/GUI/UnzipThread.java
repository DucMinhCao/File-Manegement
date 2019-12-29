package GUI;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class UnzipThread extends Thread {
    private JProgressBar jProgressBar;
    private double progress;
    private File srcFile;
    private File desFile;

    public UnzipThread(JProgressBar jProgressBar, File srcFile, File desFile) throws IOException {
        this.jProgressBar = jProgressBar;
        this.srcFile = srcFile;
        this.desFile = desFile;
    }

    public void run() {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        ZipInputStream zis = null;
        try {
            fis = new FileInputStream(srcFile.getAbsolutePath());
            zis = new ZipInputStream(fis);
            long expectedBytes = srcFile.length();
            long totalBytesUnzipped = 0;
            byte[] buffer = new byte[1024];
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(desFile.getAbsolutePath() + "\\" + fileName);
                new File(newFile.getParent()).mkdir();
                fos = new FileOutputStream(newFile);
                int len = 0;
                while ((len = zis.read(buffer)) > 0 && !Thread.currentThread().isInterrupted()) {
                    fos.write(buffer, 0, len);
                    totalBytesUnzipped += len;
                    progress = (totalBytesUnzipped / (double) expectedBytes) * 100;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            jProgressBar.setValue((int) progress);
                        }
                    });
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
                Thread.sleep(1000);
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            try {
                zis.closeEntry();
                zis.close();
                fis.close();
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
