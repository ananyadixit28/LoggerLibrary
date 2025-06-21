package main.java.com.logger.rotation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

public class FileRotationManager {
    private final String baseFileName;
    private final long maxFileSize;
    private final int maxBackupFiles;

    public FileRotationManager(String baseFileName, long maxFileSize, int maxBackupFiles) {
        this.baseFileName = baseFileName;
        this.maxFileSize = maxFileSize;
        this.maxBackupFiles = maxBackupFiles;
    }

    public boolean shouldRotate() {
        File file = new File(baseFileName);
        return file.exists() && file.length() >= maxFileSize;
    }

    public void rotateFile() throws IOException {
        File currentFile = new File(baseFileName);
        if (!currentFile.exists()) {
            return;
        }

        // Shift existing backup files
        for (int i = maxBackupFiles - 1; i >= 1; i--) {
            String backupName = baseFileName + "." + i + ".gz";
            String nextBackupName = baseFileName + "." + (i + 1) + ".gz";

            File backupFile = new File(backupName);
            if (backupFile.exists()) {
                if (i == maxBackupFiles - 1) {
                    backupFile.delete(); // Remove oldest backup
                } else {
                    backupFile.renameTo(new File(nextBackupName));
                }
            }
        }

        // Compress and move current file to .1.gz
        String firstBackupName = baseFileName + ".1.gz";
        compressFile(baseFileName, firstBackupName);

        // Delete original file
        currentFile.delete();
    }

    private void compressFile(String sourceFile, String targetFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile);
             GZIPOutputStream gzos = new GZIPOutputStream(fos)) {

            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                gzos.write(buffer, 0, length);
            }
        }
    }
}
